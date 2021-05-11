package com.sangupta.reread.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.batch.BatchJob;
import com.sangupta.jerry.batch.BatchJobItemExecutor;
import com.sangupta.jerry.util.DateUtils;
import com.sangupta.reread.entity.FeedCrawlDetails;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.service.FeedCrawlDetailsService;
import com.sangupta.reread.service.FeedCrawlerService;
import com.sangupta.reread.service.FeedRefreshService;

@Service
public class AsyncFeedRefreshServiceImpl implements FeedRefreshService {
	
	private static final long WAIT_TIME = DateUtils.ONE_SECOND * 5l;
	
	private static final long MIN_REFRESH_INTERVAL = DateUtils.ONE_SECOND * 15l;
	
	private static final Queue<String> QUEUE = new LinkedBlockingQueue<>();
	
	protected static final Set<String> EXECUTING = new HashSet<>();
	
	@Autowired
	protected FeedCrawlDetailsService feedCrawlDetailsService;
	
	@Autowired
	protected FeedCrawlerService feedCrawlerService;
	
	protected FeedRefreshJob job;
	
	@PostConstruct
	public void init() {
		BatchJobItemExecutor<String> refreshWorker = new BatchJobItemExecutor<String>() {

			@Override
			public void executeJobItem(String masterFeedID) {
				boolean added = EXECUTING.add(masterFeedID);
				if(!added) {
					return;
				}
				
				try {
					feedCrawlerService.crawlFeed(masterFeedID);
				} finally {
					EXECUTING.remove(masterFeedID);
				}
			}

			@Override
			public long getWaitTimeOnJobReadErrorInMillis() {
				return WAIT_TIME;
			}

			@Override
			public long getWaitTimeOnNullJobInMillis() {
				return WAIT_TIME;
			}

			@Override
			public boolean pauseExecution() {
				return false;
			}

			@Override
			public long pauseCheckInterval() {
				return 0;
			}

			@Override
			public boolean jobAlreadyProcessed(String masterFeedID) {
				if(EXECUTING.contains(masterFeedID)) {
					return true;
				}
				
				FeedCrawlDetails details = feedCrawlDetailsService.get(masterFeedID);
				if(details == null) {
					return false;
				}
				
				if(System.currentTimeMillis() - details.lastCrawled < MIN_REFRESH_INTERVAL) {
					return true;
				}
				
				return false;
			}

			@Override
			public boolean terminateExecutionOnNullJobItem() {
				return false;
			}
			
		};
		
		// create the job
		job = new FeedRefreshJob(refreshWorker);

		// add shut down hook
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				super.run();
				
				job.stopAsync();
			}
		});

		// start workers
		job.startJobAsync(5);
	}

	@Override
	public boolean refreshFeed(MasterFeed feed) {
		if(feed == null) {
			return false;
		}
		
		return QUEUE.add(feed.feedID);
	}

	@Override
	public boolean refreshFeeds(List<MasterFeed> feeds) {
		boolean refreshed = true;
		for(MasterFeed feed : feeds) {
			refreshed = refreshed & this.refreshFeed(feed);
		}
		
		return refreshed;
	}
	
	private static class FeedRefreshJob extends BatchJob<String> {
		
		final BatchJobItemExecutor<String> refreshWorker;

		public FeedRefreshJob(BatchJobItemExecutor<String> refreshWorker) {
			super("feed-refresh-job");
			this.refreshWorker = refreshWorker;
		}

		@Override
		protected String getJobItem() {
			return QUEUE.poll();
		}

		@Override
		protected long getShutdownWaitTimeMillis() {
			return DateUtils.ONE_MINUTE;
		}

		@Override
		protected BatchJobItemExecutor<String> getJobPieceExecutor() {
			return this.refreshWorker;
		}
		
	}

}

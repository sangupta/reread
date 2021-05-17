package com.sangupta.reread.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.entity.TimelineSortOption;
import com.sangupta.reread.entity.UserFeed;
import com.sangupta.reread.entity.UserFeedFolder;
import com.sangupta.reread.service.FeedListService;
import com.sangupta.reread.service.FeedTimelineService;

/**
 * {@link RedisTemplate} based implementation of the
 * {@link FeedTimelineService}.
 * 
 * @author sangupta
 *
 */
@Service
public class RedisFeedTimelineServiceImpl implements FeedTimelineService {

	public static final long PAGE_SIZE = 10;

	@Autowired
	protected RedisTemplate<String, String> redisTemplate;

	@Autowired
	protected FeedListService feedListService;

	@Override
	public void addToTimeline(String feedID, List<Post> posts, boolean forceUpdateAllTimeline) {
		final FeedList feedList = this.feedListService.get(SecurityContext.getUserID());
		final boolean isFeedInFeedList = feedList.containsFeed(feedID);
		final UserFeedFolder folder = feedList.getFolderForFeed(feedID);

		for (Post post : posts) {
			this.redisTemplate.opsForZSet().add(TIMELINE + feedID, post.feedPostID, post.updated);

			if (forceUpdateAllTimeline || isFeedInFeedList) {
				this.redisTemplate.opsForZSet().add(TIMELINE + FeedTimelineService.ALL_TIMELINE_ID, post.feedPostID, post.updated);

				if (folder != null) {
					this.redisTemplate.opsForZSet().add(TIMELINE + folder.folderID, post.feedPostID, post.updated);
				}
			}
		}
	}

	@Override
	public Collection<String> getTimeLine(String feedID) {
		return this.getTimeLine(feedID, TimelineSortOption.NEWEST, null);
	}

	@Override
	public Collection<String> getTimeLine(String timeLineID, TimelineSortOption sortOption, String afterPostID) {
		long rank = 0;
		if (AssertUtils.isNotEmpty(afterPostID)) {
			Long num = this.redisTemplate.opsForZSet().rank(TIMELINE + timeLineID, afterPostID);
			if (num != null) {
				rank = num.longValue();
			}
		}

		if (sortOption == TimelineSortOption.OLDEST) {
			return this.redisTemplate.opsForZSet().range(TIMELINE + timeLineID, rank + 1, rank + PAGE_SIZE);
		}

		if (AssertUtils.isNotEmpty(afterPostID)) {
			long total = this.size(timeLineID);
			rank = total - rank;
		}

		return this.redisTemplate.opsForZSet().reverseRange(TIMELINE + timeLineID, rank + 1, rank + PAGE_SIZE);
	}

	@Override
	public String getOldestPostID(String feedID) {
		Set<String> set = this.redisTemplate.opsForZSet().range(TIMELINE + feedID, 0, 0);
		if (AssertUtils.isNotEmpty(set)) {
			return set.iterator().next();
		}

		return null;
	}

	@Override
	public String getLatestID(String feedID) {
		Set<String> set = this.redisTemplate.opsForZSet().range(TIMELINE + feedID, -1, -1);
		if (AssertUtils.isNotEmpty(set)) {
			return set.iterator().next();
		}

		return null;
	}

	@Override
	public long size(String feedID) {
		Long num = this.redisTemplate.opsForZSet().zCard(TIMELINE + feedID);
		if (num == null) {
			return 0;
		}

		return num.longValue();
	}

	@Override
	public void removePost(String timelineID, String id) {
		this.redisTemplate.opsForZSet().remove(TIMELINE + timelineID, id);
	}

	@Override
	public void removeTimeline(String feedID, UserFeedFolder folder) {
		Set<String> set = this.redisTemplate.opsForZSet().range(TIMELINE + feedID, 0, -1);

		// remove from folder timeline
		if(folder != null) {
			this.redisTemplate.opsForZSet().remove(TIMELINE + folder.folderID, set);
		}

		// remove from all timeline
		this.redisTemplate.opsForZSet().remove(TIMELINE + ALL_TIMELINE_ID, set.toArray());
	}

	@Override
	public void recreateFolderTimeline(UserFeedFolder folder) {
		String timeline = TIMELINE + folder.folderID;
		
		Set<String> childIds = new HashSet<>();
		for(UserFeed child : folder.childFeeds) {
			childIds.add(TIMELINE + child.masterFeedID);
		}
		
		this.redisTemplate.delete(timeline);
		this.redisTemplate.opsForZSet().unionAndStore(timeline, childIds, timeline);
	}

//	protected Long diffAndStore(String destinationKey, List<String> keys) {
//		byte[] dstkey = SafeEncoder.encode(destinationKey);
//		byte[] numKeys = SafeEncoder.encode(String.valueOf(keys.size()));
//		byte[][] sets = new byte[keys.size()][];
//		for(int index = 0; index < keys.size(); index++) {
//			sets[index] = SafeEncoder.encode(keys.get(index));
//		}
//		
//		
//		final ProtocolCommand ZDIFFSTORE = new ProtocolCommand() {
//            @Override
//            public byte[] getRaw() {
//              return SafeEncoder.encode("zdiffstore");
//            }
//		};
//		
//		// remove the feeds from the all timeline
//		RedisConnection connection = this.redisTemplate.getConnectionFactory().getConnection();
//		Jedis jedis = (Jedis) connection.getNativeConnection();
//		Object object = jedis.sendCommand(ZDIFFSTORE, joinParameters(dstkey, numKeys, sets));
//
//		// remove the feeds from folder timeline
//		System.out.println(object);
//		return 0l;
//	}
//
//	/**
//	 * Copied from {@link BinaryClient#joinParameters}
//	 * @param first
//	 * @param second
//	 * @param rest
//	 * @return
//	 */
//	private byte[][] joinParameters(byte[] first, byte[] second, byte[][] rest) {
//		byte[][] result = new byte[rest.length + 2][];
//		result[0] = first;
//		result[1] = second;
//		System.arraycopy(rest, 0, result, 2, rest.length);
//		return result;
//	}
}

package com.sangupta.reread.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.jerry.constants.HttpStatusCode;
import com.sangupta.jerry.exceptions.HttpException;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.entity.OpmlFeed;
import com.sangupta.reread.service.FeedRefreshService;
import com.sangupta.reread.service.OpmlService;

@RestController
@RequestMapping("/opml")
public class OpmlController {
	
	@Autowired
	protected OpmlService opmlService;
	
	@Autowired
	protected FeedRefreshService feedRefreshService;
	
	@PostMapping("/import")
	public List<OpmlFeed> importOpml(@RequestBody byte[] bytes, @RequestParam(required = false, defaultValue = "false") boolean confirm) {
		if(AssertUtils.isEmpty(bytes)) {
			throw new HttpException(HttpStatusCode.BAD_REQUEST);
		}
		
		String file = new String(bytes, StandardCharsets.UTF_8);
		List<OpmlFeed> feeds = this.opmlService.parseOpml(file);
		if(!confirm) {
			return feeds;
		}
		
		// import all feeds
		List<MasterFeed> imported = this.opmlService.importFeeds(feeds);
		
		// schedule a background thread to refresh these feeds immediately
		this.feedRefreshService.refreshFeeds(imported);
		
		// all done
		return feeds;
	}

}

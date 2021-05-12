package com.sangupta.reread.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.reread.service.FeedCrawlerService;

@RestController
@RequestMapping("/refresh")
public class RefreshController {
	
	@Autowired
	protected FeedCrawlerService feedCrawlerService;

	@GetMapping("/feed/{feedID}")
	public String refreshFeed(@PathVariable String feedID) {
		this.feedCrawlerService.crawlFeed(feedID);
		return "done";
	}
	
	@GetMapping("/folder/{folderID}")
	public String refreshFolder(@PathVariable String folderID) {
		return "done";
	}
}

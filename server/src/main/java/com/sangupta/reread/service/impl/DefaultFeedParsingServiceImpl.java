package com.sangupta.reread.service.impl;

import org.springframework.stereotype.Service;

import com.sangupta.reread.entity.ParsedFeed;
import com.sangupta.reread.service.FeedParsingService;

@Service
public class DefaultFeedParsingServiceImpl implements FeedParsingService {

	@Override
	public ParsedFeed parseFeedFromUrl(String url) {
		return null;
	}

}

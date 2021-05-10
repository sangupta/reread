package com.sangupta.reread.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.OpmlFeed;
import com.sangupta.reread.service.OpmlService;
import com.sangupta.reread.utils.OpmlParser;

@Service
public class DefaultOpmlServiceImpl implements OpmlService {

	@Override
	public List<OpmlFeed> parseOpml(String opmlContents) {
		if(AssertUtils.isEmpty(opmlContents)) {
			return null;
		}
		
		return OpmlParser.parse(opmlContents);
	}

}

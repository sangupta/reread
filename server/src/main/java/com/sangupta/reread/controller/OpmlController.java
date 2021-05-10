package com.sangupta.reread.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sangupta.jerry.constants.HttpStatusCode;
import com.sangupta.jerry.exceptions.HttpException;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.OpmlFeed;
import com.sangupta.reread.service.OpmlService;

@RestController
@RequestMapping("/opml")
public class OpmlController {
	
	@Autowired
	protected OpmlService opmlService;
	
	@PostMapping("/import")
	public List<OpmlFeed> importOpml(@RequestBody String file) {
		if(AssertUtils.isEmpty(file)) {
			throw new HttpException(HttpStatusCode.BAD_REQUEST);
		}
		
		return this.opmlService.parseOpml(file);
	}

}

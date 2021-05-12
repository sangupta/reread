package com.sangupta.reread.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.reread.entity.Post;
import com.sangupta.reread.service.PostSearchService;
import com.sangupta.reread.service.PostService;

import io.redisearch.Document;
import io.redisearch.Query;
import io.redisearch.Schema;
import io.redisearch.SearchResult;
import io.redisearch.client.Client;
import io.redisearch.client.IndexDefinition;
import redis.clients.jedis.exceptions.JedisDataException;

@Service
public class RedisPostSearchServiceImpl implements PostSearchService {
	
	@Autowired
	protected PostService postService;
	
	protected Client redisSearchClient;

	public RedisPostSearchServiceImpl() {
		this.redisSearchClient = new Client("postSearch", "localhost", 6379);
	}

	@PostConstruct
	public void createIndex() {
		Schema schema = new Schema()
				.addTextField("title", 2.0)
				.addTextField("content", 1.0)
				.addTextField("author", 0.5)
				.addTextField("feedID", 0.1);

		IndexDefinition definition = new IndexDefinition();

		try {
			this.redisSearchClient.createIndex(schema, Client.IndexOptions.defaultOptions().setDefinition(definition));
		} catch(JedisDataException e) {
			// eat up
		}
	}
	
	@Override
	public void indexPost(Post post) {
		if(post == null) {
			return;
		}
		
		Map<String, Object> fields = new HashMap<>();
		fields.put("title", post.title);
		fields.put("content", post.plainText);
		fields.put("feedID", post.masterFeedID);
		if(post.author != null) {
			if(AssertUtils.isNotEmpty(post.author.name)) {
				fields.put("author", post.author.name);
			}
		}
		
		this.redisSearchClient.addDocument(post.feedPostID, fields);
	}

	@Override
	public List<Post> search(String text) {
		Query query = new Query(text).limit(0, 100);
		SearchResult results = this.redisSearchClient.search(query);
		if(results == null) {
			return null;
		}
		
		List<Post> posts = new ArrayList<>(Long.valueOf(results.totalResults).intValue());
		for(Document doc : results.docs) {
			posts.add(this.postService.get(doc.getId()));
		}
		
		return posts;
	}
	
}

package com.sangupta.reread.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.sangupta.jerry.security.SecurityContext;
import com.sangupta.reread.entity.User;

@Component
public class SingleMeUserFilter implements Filter, Ordered {
	
	private static final User USER = new User("me", "Friend");

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		SecurityContext.setPrincipal(USER);
		try {
			filterChain.doFilter(request, response);
		} finally {
			SecurityContext.clear();
		}
	}

	@Override
	public int getOrder() {
		return 0;
	}
	
}

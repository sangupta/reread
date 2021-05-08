package com.sangupta.reread.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class CorsFilter implements Filter, Ordered {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletResponse hsr = (HttpServletResponse) response;

		hsr.setHeader("Access-Control-Allow-Origin", "*");
		hsr.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
		hsr.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, HEAD, DELETE");
		hsr.setHeader("Access-Control-Max-Age", "86400");
		filterChain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public int getOrder() {
		return 10;
	}
	
}

package com.quu.controller;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class CORSFilter. It will intercept all request and responses made to/from this project.
 */
@WebFilter("/CORSFilter")
public class CORSFilter implements Filter { 
       
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
    	HttpServletRequest httpRequest = (HttpServletRequest)request;
    	HttpServletResponse httpResponse = (HttpServletResponse)response;
    	
    	httpResponse.addHeader("Access-Control-Allow-Origin", "*");
    	httpResponse.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
    	//httpResponse.addHeader("Access-Control-Allow-Credentials", "true");
    	httpResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
    	
    	//For HTTP OPTIONS verb/method reply with ACCEPTED status code -- per CORS handshake
		/*if (httpRequest.getMethod().equals("OPTIONS")) {
			httpResponse.setStatus(HttpServletResponse.SC_ACCEPTED);
			return;
		}*/
    	
		// pass the request along the filter chain
		chain.doFilter(httpRequest, httpResponse);
	}
	
}

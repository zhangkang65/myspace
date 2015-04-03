package com.linkage.toptea.sysmgr.config.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class XssFilter implements  Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest  req=(HttpServletRequest) request; //包装增强
		
		//循环便利出结果 
		RemoveScriptXssHttpServletRequestWrapper xssRequest = new RemoveScriptXssHttpServletRequestWrapper(req); 
		//处理请求  
		chain.doFilter(xssRequest, response);
		
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		System.out.println("---------filter---------------");
		
	}

}

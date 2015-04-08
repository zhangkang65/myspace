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
import javax.servlet.http.HttpServletResponse;

public class GenericEncodingFilter implements  Filter{

	@Override
	public void destroy() {
		
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest  request=(HttpServletRequest) req; //��װ��ǿ
		HttpServletResponse  response=(HttpServletResponse) res;
		
		//�޸ı��� 
		HttpServletRequest  codeRequest=new GenericServletRequestWrapper(request);
		//doFilter(codeRequest, res, chain);
		chain.doFilter(codeRequest, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	
		System.out.println("-------codefilter----------------");
	}

}

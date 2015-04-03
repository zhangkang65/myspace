package com.linkage.toptea.sysmgr.config.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RemoveScriptXssHttpServletRequestWrapper extends HttpServletRequestWrapper {

	HttpServletRequest orgRequest = null;

	public RemoveScriptXssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		orgRequest = request;
	}

	/**
	 * 覆盖getParameter方法，将参数名和参数值都做xss过滤。<br/>
	 * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
	 * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
	 */
	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		if (value != null) {
			value = xssEncode(value);
		}
		return value;
	}

	/**
	 * 覆盖getHeader方法，将参数名和参数值都做xss过滤。<br/>
	 * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br/>
	 * getHeaderNames 也可能需要覆盖
	 */
	@Override
	public String getHeader(String name) {

		String value = super.getHeader(xssEncode(name));
		if (value != null) {
			value = xssEncode(value);
		}
		return value;
	}

	/**
	 * 将容易引起xss漏洞的半角字符直接替换成全角字符
	 * 
	 * @param s
	 * @return
	 */
	private static String xssEncode(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		}
		
		StringBuilder sb = new StringBuilder(s.length() + 16);
		
		//开始标识
		int  beginflag=s.indexOf("<script");  
		//有script标签
		if(beginflag!=-1){  
			//有</script标签
			int   endflag=s.indexOf("</script");
			 //如果只有单项<script>标签  不构成威胁  
			if(endflag==-1){ 
				sb.append(s);
			}else{
				//int  endflagend 
				
				
				
			}
			
			//s.substring(beginIndex, endIndex)
		}else{  //不存在标签 
			sb.append(s);
		}
		
		
		return sb.toString();
	}

	/**
	 * 获取最原始的request
	 * 
	 * @return
	 */
	public HttpServletRequest getOrgRequest() {
		return orgRequest;
	}

	/**
	 * 获取最原始的request的静态方法
	 * 
	 * @return
	 */
	public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
		if (req instanceof RemoveScriptXssHttpServletRequestWrapper) {
			return ((RemoveScriptXssHttpServletRequestWrapper) req).getOrgRequest();
		}

		return req;
	}
}

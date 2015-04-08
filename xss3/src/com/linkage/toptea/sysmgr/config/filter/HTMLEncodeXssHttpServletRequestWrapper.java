package com.linkage.toptea.sysmgr.config.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HTMLEncodeXssHttpServletRequestWrapper extends HttpServletRequestWrapper {

	HttpServletRequest orgRequest = null;

	public HTMLEncodeXssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		orgRequest = request;
	}


	@Override
	public String getParameter(String name) {
		String value = super.getParameter(xssEncode(name));
		if (value != null) {
			value = xssEncode(value);
		}
		return value;
	}

	
	@Override
	public String getHeader(String name) {

		String value = super.getHeader(xssEncode(name));
		if (value != null) {
			value = xssEncode(value);
		}
		return value;
	}


	private static String xssEncode(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		}
		StringBuilder sb = new StringBuilder(s.length() + 16);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '>':
				sb.append("&gt;");// ȫ�Ǵ��ں�
				break;
			case '<':
				sb.append("&lt;");// ȫ��С�ں�
				break;
			case '&':
				sb.append("&amp;");// ȫ��
				break;
			case '"':
				sb.append("&quot;");//˫����
				break;
			case ' ':
				sb.append("&nbsp;");// ȫ�Ǿ���
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	
	public HttpServletRequest getOrgRequest() {
		return orgRequest;
	}

	
	public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
		if (req instanceof HTMLEncodeXssHttpServletRequestWrapper) {
			return ((HTMLEncodeXssHttpServletRequestWrapper) req).getOrgRequest();
		}

		return req;
	}
}

package com.linkage.toptea.sysmgr.config.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

	HttpServletRequest orgRequest = null;

	public XssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		orgRequest = request;
	}

	/**
	 * ����getParameter���������������Ͳ���ֵ����xss���ˡ�<br/>
	 * �����Ҫ���ԭʼ��ֵ����ͨ��super.getParameterValues(name)����ȡ<br/>
	 * getParameterNames,getParameterValues��getParameterMapҲ������Ҫ����
	 */
	@Override
	public String getParameter(String name) {
		String value = super.getParameter(xssEncode(name));
		if (value != null) {
			value = xssEncode(value);
		}
		return value;
	}

	/**
	 * ����getHeader���������������Ͳ���ֵ����xss���ˡ�<br/>
	 * �����Ҫ���ԭʼ��ֵ����ͨ��super.getHeaders(name)����ȡ<br/>
	 * getHeaderNames Ҳ������Ҫ����
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
	 * ����������xss©���İ���ַ�ֱ���滻��ȫ���ַ�
	 * 
	 * @param s
	 * @return
	 */
	private static String xssEncode(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		}
		StringBuilder sb = new StringBuilder(s.length() + 16);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '>':
				sb.append('��');// ȫ�Ǵ��ں�
				break;
			case '<':
				sb.append('��');// ȫ��С�ں�
				break;
			case '\'':
				sb.append('��');// ȫ�ǵ�����
				break;
			case '\"':
				sb.append('��');// ȫ��˫����
				break;
			case '&':
				sb.append('��');// ȫ��
				break;
			case '\\':
				sb.append('��');// ȫ��б��
				break;
			case '#':
				sb.append('��');// ȫ�Ǿ���
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * ��ȡ��ԭʼ��request
	 * 
	 * @return
	 */
	public HttpServletRequest getOrgRequest() {
		return orgRequest;
	}

	/**
	 * ��ȡ��ԭʼ��request�ľ�̬����
	 * 
	 * @return
	 */
	public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
		if (req instanceof XssHttpServletRequestWrapper) {
			return ((XssHttpServletRequestWrapper) req).getOrgRequest();
		}

		return req;
	}
}

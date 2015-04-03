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
	 * ����getParameter���������������Ͳ���ֵ����xss���ˡ�<br/>
	 * �����Ҫ���ԭʼ��ֵ����ͨ��super.getParameterValues(name)����ȡ<br/>
	 * getParameterNames,getParameterValues��getParameterMapҲ������Ҫ����
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
		
		//��ʼ��ʶ
		int  beginflag=s.indexOf("<script");  
		//��script��ǩ
		if(beginflag!=-1){  
			//��</script��ǩ
			int   endflag=s.indexOf("</script");
			 //���ֻ�е���<script>��ǩ  ��������в  
			if(endflag==-1){ 
				sb.append(s);
			}else{
				//int  endflagend 
				
				
				
			}
			
			//s.substring(beginIndex, endIndex)
		}else{  //�����ڱ�ǩ 
			sb.append(s);
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
		if (req instanceof RemoveScriptXssHttpServletRequestWrapper) {
			return ((RemoveScriptXssHttpServletRequestWrapper) req).getOrgRequest();
		}

		return req;
	}
}

package com.linkage.toptea.sysmgr.config.filter;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SecurityFilter implements Filter {
	protected String encoding = null;
	protected String ajaxEncoding = null;
	private static final String SAFE_CHECK_OPEN = "1";// 1 打开
	private static final String FORM_CHECK_OPEN = "1";// 1 打开
	private List securityList = null;
	private String formCheck = null;
	protected FilterConfig filterConfig = null;
	protected boolean ignoreEncoding = true;
	private String filePath = null;
	private String redirectPath = null;

	public void destroy() {
		this.encoding = null;
		this.filterConfig = null;
	}

	/**
	 * 需添加的过滤器
	 * 
	 * @param queryStr
	 *            查询字符
	 * @param funcId
	 *            系统获取的功能ID
	 * @param request
	 * @return true or false
	 */
	private boolean doSafeCheck(String queryStr, String funcId,
			ServletRequest request) {
		if (queryStr.indexOf("bomc.ah.amcc") == -1) {
			for (int i = 0; i < securityList.size(); i++) {
				String value = ((SecurityBean) securityList.get(i)).getValue();
				String mvalue = ((SecurityBean) securityList.get(i)).getMvalue();
				if(queryStr.indexOf("url") != -1){
					return true;
				}
				if (queryStr.indexOf(value) != -1 || queryStr.indexOf(mvalue) != -1) {
					System.out.println("已过滤一个危险入侵，参数queryStr:"+queryStr);
					System.out.println("value:"+value);
					System.out.println("mvalue"+mvalue);
					return false;
				}
			}
		}
		return true;
	}

	private boolean doFormCheck(HttpServletRequest request) throws Exception {
		Enumeration paraenum = request.getParameterNames();
		StringBuffer sb = new StringBuffer();
		while (paraenum.hasMoreElements()) {
			String paramName = (String) paraenum.nextElement();
			String paramValue = request.getParameter(paramName);
			sb.append(paramValue);
		}
		String param = sb.toString();
		//System.out.println("----------param:"+param+"------------");
		if (param.indexOf("bomc.ah.amcc") == -1) {
			if (param!=null && !"".equals(param)) {
				for (int i = 0; i < securityList.size(); i++) {
					String value = ((SecurityBean) securityList.get(i)).getValue();
					String mvalue = ((SecurityBean) securityList.get(i)).getMvalue();
					if(param.indexOf("url") != -1){
						return true;
					}
					if (param.indexOf(value) != -1 || param.indexOf(mvalue) != -1) {
						System.out.println("已过滤一个表单入侵，参数param:"+param);
						System.out.println("value:"+value);
						System.out.println("mvalue:"+mvalue);
						return false;
					}
				}
			}
		}
		return true;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) {
		try {
			initSafeFilter(request);
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			if (!ignoreEncoding) {
				String encoding = this.encoding;
				boolean flag = false;
				if (this.ajaxEncoding != null) {
					String ajaxEncoding = httpRequest.getHeader("ajax-encoding");
					if (ajaxEncoding != null) {
						request.setCharacterEncoding(this.ajaxEncoding);
						flag = true;
					}
				}
				if (encoding != null && !flag)
					request.setCharacterEncoding(encoding);
				encoding = null;
			}
			// 添加
			String queryStr = httpRequest.getQueryString();
//			System.out.println("queryStr:"+queryStr);
			// 链接 过滤
			if (queryStr!=null && !"".equals(queryStr)) {
				System.out.println("--------------------queryStr:"+queryStr+"-----------------");
				// 未做FUNCID过滤
				if (!doSafeCheck(queryStr, "", request)) {
					httpResponse.sendRedirect(redirectPath);
					System.out.println("已过滤从" + request.getRemoteAddr() + "的一个危险入侵！");
					System.out.println("入侵链接：" + request.getRealPath("/") + queryStr);
					return;
				}
			}
			// 表单过滤
			//System.out.println("--------------------formCheck:"+formCheck+"-----------------");
			if (formCheck.equals(FORM_CHECK_OPEN)) {
				if (!doFormCheck(httpRequest)) {
					System.out.println("已过滤从" + request.getRemoteAddr() + "的一个表单入侵！");
					httpResponse.sendRedirect(redirectPath);
					return;
				}
			}
			chain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * ; 初始化安全过滤器
	 * 
	 * @param request
	 */
	private void initSafeFilter(ServletRequest request) {
		if (securityList == null) {
			XStream xStream = new XStream(new DomDriver());
			xStream.aliasType("Security", Security.class);
			xStream.aliasType("SecurityBean", SecurityBean.class);
			Security security = null;
			try {
				FileInputStream ops = new FileInputStream(new File(request.getRealPath("/") + filePath));
//				FileInputStream ops = new FileInputStream(new File(filePath));
				security = (Security) xStream.fromXML(ops);
				ops.close();
				ops = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (((String) security.getOpenFlag()).equals(SAFE_CHECK_OPEN)) {
				securityList = security.getSecurityBeanList();
				formCheck = (String) security.getFormCheck();
			}
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.filePath = filterConfig.getInitParameter("filePath");
		this.redirectPath = filterConfig.getInitParameter("redirectPath");
		this.encoding = filterConfig.getInitParameter("encoding");
		this.ajaxEncoding = filterConfig.getInitParameter("ajax-encoding");
		String value = filterConfig.getInitParameter("ignoreEncoding");
		if (value == null)
			this.ignoreEncoding = true;
		else if (value.equalsIgnoreCase("true"))
			this.ignoreEncoding = true;
		else if (value.equalsIgnoreCase("yes"))
			this.ignoreEncoding = true;
		else
			this.ignoreEncoding = false;
	}
	
	public static void main (String[] args) {
		SecurityFilter filter = new SecurityFilter();
		String queryString ="service=http%3A%2F%2Fbomc.ah.amcc%2Fcas%2Fauc%2FtoPortal.jsp&thirdPortalUser=FJfnlv9Tv4PLMs%2Fd%2Fvh%2FABymN%2Bb9wp%2F52sjJhu7c59%2F91rPH";
		System.out.println(filter.doSafeCheck(queryString, "", null));
	}
}

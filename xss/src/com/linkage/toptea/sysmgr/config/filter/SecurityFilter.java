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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SecurityFilter implements Filter {
	
	private Log logger = LogFactory.getLog(SecurityFilter.class);
	private static final String SAFE_CHECK_OPEN = "1";// 1 打开
	private List securityList = null;
	protected FilterConfig filterConfig = null;
	private String filePath = null;
	private String redirectPath = null;

	public void destroy() {
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
	private boolean doSafeCheck(String queryStr, String funcId,ServletRequest request) {
			for (int i = 0; i < securityList.size(); i++) {
				String value = ((SecurityBean) securityList.get(i)).getValue();
				String mvalue = ((SecurityBean) securityList.get(i)).getMvalue();
			
				if (queryStr.toUpperCase().indexOf(value) != -1 || queryStr.toUpperCase().indexOf(mvalue) != -1) {
					logger.error("已过滤一个危险入侵，参数queryStr:"+queryStr);
					return false;
				}
			}
		return true;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) {
		try {
			initSafeFilter(request);
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			httpRequest.setCharacterEncoding("GBK");  //post 
			HttpServletResponse httpResponse = (HttpServletResponse) response; 
			StringBuilder  sb=new StringBuilder();
			Enumeration<String>  myenum=httpRequest.getParameterNames();
			for(;myenum.hasMoreElements();) {
				String name= myenum.nextElement();
				String value=httpRequest.getParameter(name);
				sb.append(name).append("=").append(value).append("&");
			}
			
			
			 String queryStr =sb.toString();
			// 链接 过滤
			if (queryStr!=null && !"".equals(queryStr)) {
				queryStr=sb.substring(0, sb.length()-1);
				System.out.println("queryStr----:"+queryStr);
				// 未做FUNCID过滤
				if (!doSafeCheck(queryStr, "", request)) {
					httpResponse.sendRedirect(redirectPath);
					logger.error("已过滤从" + request.getRemoteAddr() + "的一个危险入侵！");
					logger.error("入侵链接：" + request.getRealPath("/") + queryStr);
					System.out.println("入侵链接：" + request.getRealPath("/") + queryStr);
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
				security = (Security) xStream.fromXML(ops);
				ops.close();
				ops = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (((String) security.getOpenFlag()).equals(SAFE_CHECK_OPEN)) {
				securityList = security.getSecurityBeanList();
			}
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.filePath = filterConfig.getInitParameter("filePath");
		this.redirectPath = filterConfig.getInitParameter("redirectPath");
	}
	
}

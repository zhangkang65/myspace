package com.linkage.toptea.sysmgr.config.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

public class SecurityFilter implements  Filter{
	
	  protected String encoding = null;
	  protected String ajaxEncoding = null;
	  private static final String SAFE_CHECK_OPEN = "1";
	  private static final String FORM_CHECK_OPEN = "1";
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

	  private boolean doSafeCheck(String queryStr, String funcId, ServletRequest request)
	  {
	    if (queryStr.indexOf("bomc.ah.amcc") == -1) {
	      for (int i = 0; i < this.securityList.size(); ++i) {
	        String value = ((SecurityBean)this.securityList.get(i)).getValue();
	        String mvalue = ((SecurityBean)this.securityList.get(i)).getMvalue();
	        if (queryStr.indexOf("url") != -1) {
	          return true;
	        }
	        if ((queryStr.indexOf(value) != -1) || (queryStr.indexOf(mvalue) != -1)) {
	          System.out.println("已过滤一个危险入侵，参数queryStr:" + queryStr);
	          System.out.println("value:" + value);
	          System.out.println("mvalue" + mvalue);
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
	      String paramName = (String)paraenum.nextElement();
	      String paramValue = request.getParameter(paramName);
	      sb.append(paramValue);
	    }
	    String param = sb.toString();

	    if ((param.indexOf("bomc.ah.amcc") == -1) && 
	      (param != null) && (!"".equals(param))) {
	      for (int i = 0; i < this.securityList.size(); ++i) {
	        String value = ((SecurityBean)this.securityList.get(i)).getValue();
	        String mvalue = ((SecurityBean)this.securityList.get(i)).getMvalue();
	        if (param.indexOf("url") != -1) {
	          return true;
	        }
	        if ((param.indexOf(value) != -1) || (param.indexOf(mvalue) != -1)) {
	          System.out.println("已过滤一个表单入侵，参数param:" + param);
	          System.out.println("value:" + value);
	          System.out.println("mvalue:" + mvalue);
	          return false;
	        }
	      }
	    }

	    return true;
	  }

	  
	  
	  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	  {
	    try {
	      initSafeFilter(request);
	      HttpServletRequest httpRequest = (HttpServletRequest)request;
	      HttpServletResponse httpResponse = (HttpServletResponse)response;
	      //如果不是忽略的字符 
	      if (!this.ignoreEncoding) {
	        String encoding = this.encoding;
	        boolean flag = false;
	        if (this.ajaxEncoding != null) {
	          String ajaxEncoding = httpRequest.getHeader("ajax-encoding");
	          if (ajaxEncoding != null) {
	            request.setCharacterEncoding(this.ajaxEncoding);
	            flag = true;
	          }
	        }
	        if ((encoding != null) && (!flag))
	          request.setCharacterEncoding(encoding);
	        encoding = null;
	      }

	      String queryStr = httpRequest.getQueryString();

	      if ((queryStr != null) && (!"".equals(queryStr))) {
	        System.out.println("--------------------queryStr:" + queryStr + "-----------------");
	        
	        
	        //如果请求参数中 包含危险字符   跳转页面到提示页面  
	        if (!doSafeCheck(queryStr, "", request)) {
	          httpResponse.sendRedirect(this.redirectPath);
	          System.out.println("已过滤从" + request.getRemoteAddr() + "的一个危险入侵！");
	          System.out.println("入侵链接：" + request.getRealPath("/") + queryStr);
	          return;
	        }

	      }
	      
	      //如果需要 fromCheck 
	      if (   (this.formCheck.equals("1"))   &&   (!doFormCheck(httpRequest))   ) {
	        System.out.println("已过滤从" + request.getRemoteAddr() + "的一个表单入侵！");
	        httpResponse.sendRedirect(this.redirectPath);
	        return;
	      }

	      chain.doFilter(request, response);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }

	  private void initSafeFilter(ServletRequest request)
	  {
	    if (this.securityList == null) {
	      XStream xStream = new XStream(new DomDriver());
	      xStream.aliasType("Security", Security.class);
	      xStream.aliasType("SecurityBean", SecurityBean.class);
	      Security security = null;
	      try {
	        FileInputStream ops = new FileInputStream(new File(request.getRealPath("/") + this.filePath));

	        security = (Security)xStream.fromXML(ops);
	        ops.close();
	        ops = null;
	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	      if (security.getOpenFlag().equals("1")) {
	        this.securityList = security.getSecurityBeanList();
	        this.formCheck = security.getFormCheck();
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

	  public static void main(String[] args) {
	    SecurityFilter filter = new SecurityFilter();
	    String queryString = "service=http%3A%2F%2Fbom.ah.amcc%2Fcas%2Fauc%2FtoPortal.jsp&thirdPortalUser=FJfnlv9Tv4PLMs%2Fd%2Fvh%2FABymN%2Bb9wp%2F52sjJhu7c59%2F91rPH";
	    filter.doSafeCheck(queryString, "", null);
	  }
	
}

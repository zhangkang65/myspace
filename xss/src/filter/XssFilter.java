package filter;

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
		HttpServletRequest  req=(HttpServletRequest) request; //��װ��ǿ
		
		//ѭ����������� 
		XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(req); 
		Enumeration<String> myEnum=req.getParameterNames();
		while(myEnum.hasMoreElements()){
			String   name=myEnum.nextElement();
			String  myValue=req.getParameter(name);
			System.out.println("request��"+myValue+":len="+myValue.getBytes().length);
			System.out.println("request is:"+req);
			
			
			System.out.println("-----�������� ----------------------------------");
			
			//�������� 
			String  xssValue=xssRequest.getParameter(name);
			System.out.println("xssValue��"+xssValue+":len="+xssValue.getBytes().length);
			System.out.println("xssRequest is:"+xssRequest);
			System.out.println("-----ԭʼ����----------------------------------");
			
			
			//ԭʼ����
		HttpServletRequest	xssreqorg=xssRequest.getOrgRequest(xssRequest);
		System.out.println("xssreqorg req is:"+xssreqorg);
		HttpServletRequest	reqorg=xssRequest.getOrgRequest(req);
		System.out.println("org req is:"+reqorg);
		
			//��ȡ��ǩͷ��Ϣ
		System.out.println("-----��ȡ��ǩͷ��Ϣ----------------------------------");
		Enumeration enumeration=req.getHeaderNames();
		
		while(enumeration.hasMoreElements()){
			String  hname=(String) enumeration.nextElement();
			System.out.println(hname);
			}
		}
		
		//��������  
		chain.doFilter(xssRequest, response);
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		System.out.println("---------filter---------------");
		
	}

}

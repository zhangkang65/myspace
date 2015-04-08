package com.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class XssServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public XssServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*ServletOutputStream  out=response.getOutputStream();
		
		String  myName=request.getParameter("myname");
		out.print("<html>");
		out.print("<a href='http://www.baidu.com'>baidu</a>");
		out.print(myName);
		out.print("</html>");*/
		
		Map<String, String>  map=request.getParameterMap();
		Set<Entry<String, String>>  entries=map.entrySet();
		for(Iterator<Entry<String, String>>  it=entries.iterator();it.hasNext();){
			Entry<String, String>  entry=it.next();
			
			System.out.println(entry.getKey()+"-----------");
			 String [] param = (String [])((Entry)entry).getValue();
			 System.out.println(param[0]+"-------------");
					
		   
			
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

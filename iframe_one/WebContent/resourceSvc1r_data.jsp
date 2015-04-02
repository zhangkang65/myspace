<%@ page contentType="text/html; charset=gb2312" language="java"
	errorPage="/error.jsp"%>
<%
	String con1 = request.getParameter("con1");
	String con2 = request.getParameter("con2");
	request.setAttribute("con1", con1);
	request.setAttribute("con2", con2);
	request.getRequestDispatcher("resourceSvc1r.jsp").forward(request, response);
%>
<%@ page contentType="text/html; charset=gb2312" language="java" errorPage="/error.jsp"%>
<%@ page import="com.linkage.toptea.context.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.concern.*"%>
<%@ page import="com.linkage.toptea.auc.*"%>
<%@ page import="java.util.*"%>
<%
	try{
		ConcernManager cmgr = (ConcernManager)Context.getContext().getAttribute("concernManager");
		String condition = request.getParameter("condition");
		if ( null != condition ) condition = new String (condition.getBytes("iso8859-1"), "gb2312");
		System.out.println("condition=" + condition);
		
		// 页面查询起始位置
		int start = 0;
		if ( null == request.getParameter("start") || 
			   "".equals(request.getParameter("start"))){
		    start = 0;
		} else {
		    try {
			   start = Integer.parseInt(request.getParameter("start"));
			} catch (Exception e) {
				start = 0;
			}
		}
		
		// 页面查询起始位置
		int limit = 0;
		if ( null == request.getParameter("limit") || "".equals(request.getParameter("limit"))){
		    limit = 10;
		} else {
		    try {
			   limit = Integer.parseInt(request.getParameter("limit"));
			} catch (Exception e) {
				limit = 10;
			}
		}
		//查询告警
		List userList = cmgr.getCallUserList(condition ,start, limit);
		int count =  cmgr.getCallUserCount(condition);
		System.out.println("userList=" +  userList.size());
		//检索结果集	     
	 	String json = "";
	    if ( null == userList ) {
	  	   json = "{totalProperty:  0 , root:[]}";
	  	   response.getWriter().write(json);
	  	   return;
	    }
	    
	    json = "{totalProperty:" + count + ",root:[";
	    for (int i = 0; i < userList.size(); i++) {
	    	User info = (User)userList.get(i);
	  	    if ( null == info ) { break; }
	  	    
	      	  
	         json += "{pno:'" + info.getId() +  "',pnm:'" + info.getName() +  "'}";
	         if ( i < userList.size() - 1 ) { json += ","; }
	     }
	    json += "]}";  
	    response.getWriter().write(json);	    
	} catch (Exception e) {
		e.printStackTrace();	
	}
%>


<%@ page language="java" contentType="text/html; charset=GB2312"	pageEncoding="GB2312"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.callkeyword.*"%>
<%@ page import="com.linkage.toptea.context.*"%>
<%@ page import="com.linkage.toptea.auc.*"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.linkage.toptea.sysmgr.cm.*"%>

<%
try {
	String mode = request.getParameter("mode");

    User user = ((User) Context.getContext().getAttribute(Context.USER));
		
	CallKeyWordManager callKeyWordManager = (CallKeyWordManager) Context.getContext().getAttribute("callKeyWordManager");
	ConfigManager configManager = (ConfigManager)Context.getContext().getAttribute("configManager");
	
	if ( "save".equals(mode) ) {
	
		String id = request.getParameter("rid");
		String keyword = request.getParameter("keyword");
		String moId = request.getParameter("moid");
		String isForChild = request.getParameter("forChild");
		int forChild = 1;
		if ( "0".equals(isForChild) ) { forChild = 0; }
		
		String isRegExpr = request.getParameter("isRegExpr");
		int regExpr = 0;
		if ( "1".equals(isRegExpr) ) { regExpr = 1; }
		 
		String responsible = request.getParameter("responsible");
		int caller = -1;
		if ( null != responsible && !"".equals(responsible) ) {
			caller = Integer.parseInt(responsible);
		}
		 
		CallKeyWordInfo info = new CallKeyWordInfo ();
		info.setCaller(caller);
		info.setForChild(forChild);
		info.setId(id);
		info.setKeyword(keyword);
		info.setMoId(moId);
		info.setRegExpr(regExpr);
		info.setTime( new Timestamp(System.currentTimeMillis()) );
		info.setUserId(user.getId() );
		int r = -1;
		if ( null != id && !"".equals(id) ) {
			r = callKeyWordManager.updateCallKeyWordInfo( info );
		} else {
			r = callKeyWordManager.addCallKeyWordInfo( info );
		}
		if ( r > 0 ) {
			out.print("{success:true,msg:''}");
		} else {
			out.print("{success:false,msg:''}");
		}
	} else if ( "keyword".equals(mode) || "storeMo".equals(mode) ) {

		//²éÑ¯Ìõ¼þ
		String condition = request.getParameter("condition");
		String type =  "storeMo".equals(mode) ? "MO":"keyword";
		List<String> keywords = callKeyWordManager.getCallKeyWordInfo(condition , type);
		
	 	String json = "";

	    if ( null == keywords || keywords.size() == 0 ) {
	  	   json = "{totalProperty:  0 , root:[]}";
	  	   response.getWriter().write(json);
	  	   return;
	    }
	    
		json = "{totalProperty:" + keywords.size() + ",root:[";
		for ( int j = 0;  j < keywords.size(); j++ ) {
			if ( "keyword".equals(mode) ) {
		    	json += "{nodeId:'" + keywords.get(j) + "',caption:\"" + keywords.get(j) + "\"}";
			} else {
				json += "{nodeId:'" + keywords.get(j).split("_-_")[0] + "',caption:\"" + keywords.get(j).split("_-_")[1] + "\"}";
			}
		    if ( j != keywords.size() -1 ) { json += ","; }
		}
	    json += "]}"; 
	    
	    response.getWriter().write(json);
	}
} catch (Exception e ) {
	e.printStackTrace();
}
%>
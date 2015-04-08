<%@ page contentType="text/html; charset=gb2312" language="java"
	import="java.util.*,com.linkage.toptea.context.*,com.linkage.toptea.code.*,java.io.*"
	errorPage="/error.jsp"
%>
<%@page import="com.linkage.toptea.sysmgr.fm.*"%>
	
<%!  
	
    public StringBuffer printCode(CodeManager cm, String category,String[][] code, int index) throws IOException {
		StringBuffer sb = new StringBuffer(); 
		sb.append("{'id':'' , 'name':' '}, " ); 
		for(int i=0; i<code.length; i++) {
			sb.append("{'id':'" + code[i][0] + "' , 'name':'" + code[i][1] + "'}, " ); 
			String [][] children = cm.getCodeList(category, code[i][0]);
			//printCode(cm, category, children, index + 1);
		}
		return sb;
   }	
%>
<%

	CodeManager cm = (CodeManager)Context.getContext().getAttribute("codeManager");
	String category =  AlarmManager.ALARM_SOURCE_CODE_CATEGORY;
	StringBuffer sb = printCode( cm, category, cm.getCodeList(category), 0);
	out.print("{data:[" + sb.toString().substring(0,sb.length()-2) + "]}" );
	System.out.println("{data:[" + sb.toString().substring(0,sb.length()-2) + "]}" );
%>

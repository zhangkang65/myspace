<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
 <%@ page import="com.linkage.toptea.sysmgr.fm.assistant.concern.*,java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>�����������</title>
</head>
<body>
<%
Map<String,PhoneFilterInfo> filters = PhoneFilterManager.pFilters;
if (filters!=null &&  filters.size()>0){
%>
<table>
<tr><td>PATH</td><td>ʱ��</td><td>KEY</td><td>�Ƿ������</td></tr>
<%

Iterator<String> it = filters.keySet().iterator(); 
while(it.hasNext()){
   String key = it.next();
   PhoneFilterInfo info = filters.get(key);
   out.print("<tr><td>"+info.getPath()+"</td><td>"+info.getTimelen()+"</td><td>"+info.getKey()+"</td><td>"+info.getIschild()+"</td></tr>");
}
%>
</table>
<%
}else out.print("û�������������");

 %>
</body>
</html>
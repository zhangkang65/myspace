<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ page import="com.linkage.toptea.sysmgr.util.*"%>
<%@ page import="com.linkage.toptea.context.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.concern.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Ìí¼Ó¹Ø×¢</title>
	<link rel="stylesheet" type="text/css" href="/extjs/resources/css/ext-all.css" />
	<script type="text/javascript" src="/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" src="/extjs/ext-all.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/sysmgr.css" type="text/css"/>
<script language="javascript" src="<%=request.getContextPath()%>/webframe/webframe.js" type="text/javascript"></script>
</head>
<body onload="init();">
<%
String alarmId = request.getParameter("alarmId");
ConcernManager cmgr = (ConcernManager)Context.getContext().getAttribute("concernManager");
ConcernFilter filter = new ConcernFilter();
filter.setAlarmId(alarmId);
filter.setIsshow(-1);
int count = cmgr.getConcernCount(filter);
List<ConcernInfo> items = cmgr.findConcernAlarmByFilter(filter,0,100);
ConcernInfo ci = null;
if (count>0) items.get(0);
boolean isShow = (ci==null?false:(ci.getIsshow()==1));
if (count>0&&isShow){
%>
<center>¸Ã¸æ¾¯ÒÑ¾­ÊÇ¹Ø×¢¸æ¾¯ÁË</center>
  <script type="text/javascript">
  function init(){
	  alert('¸Ã¸æ¾¯ÒÑ¾­ÊÇ¹Ø×¢¸æ¾¯ÁË');
	  closeModalDialog();
  }
  </script>
<%
}else{
 %>
 <table border=0 width=100% height=100%>
 <tr><td height=25>¹Ø×¢ÀíÓÉ</td></tr>
 <tr><td><textarea id="ly" style="width:100%;height:100%"></textarea></td></tr>
 </table>
<script type="text/javascript">
var buttonTable = new Array (
	    "±£´æ", new Function("save()"),
	    "¹Ø±Õ", new Function("closeModalDialog()")
	);

function init(){}
function save(){
   if (ly.value==""){
     Ext.Msg.alert('ÌáÊ¾','¹Ø×¢ÀíÓÉ±ØÐëÌîÐ´£¿');
     return;
   }
   Ext.Ajax.request({
       method: 'post',
       params: {
       	  mode:'addconcern',
       	  alarmId:'<%=alarmId%>',
       	  memo:ly.value
       },
       success: function(resp, opts){
           Ext.Msg.alert("ÌáÊ¾","¹Ø×¢¸æ¾¯Ìí¼Ó³É¹¦£¡");
           closeModalDialog();
       },
       failure: function(resp, opts){
          Ext.Msg.alert("ÌáÊ¾","¹Ø×¢¸æ¾¯Ìí¼ÓÊ§°Ü?");
       },
       url: './AlarmAction.jsp'
   });

}
</script>
 <%
}

%>
</body>
</html>
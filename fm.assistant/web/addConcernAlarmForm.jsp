<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ page import="com.linkage.toptea.sysmgr.util.*"%>
<%@ page import="com.linkage.toptea.context.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.concern.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>��ӹ�ע</title>
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
<center>�ø澯�Ѿ��ǹ�ע�澯��</center>
  <script type="text/javascript">
  function init(){
	  alert('�ø澯�Ѿ��ǹ�ע�澯��');
	  closeModalDialog();
  }
  </script>
<%
}else{
 %>
 <table border=0 width=100% height=100%>
 <tr><td height=25>��ע����</td></tr>
 <tr><td><textarea id="ly" style="width:100%;height:100%"></textarea></td></tr>
 </table>
<script type="text/javascript">
var buttonTable = new Array (
	    "����", new Function("save()"),
	    "�ر�", new Function("closeModalDialog()")
	);

function init(){}
function save(){
   if (ly.value==""){
     Ext.Msg.alert('��ʾ','��ע���ɱ�����д��');
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
           Ext.Msg.alert("��ʾ","��ע�澯��ӳɹ���");
           closeModalDialog();
       },
       failure: function(resp, opts){
          Ext.Msg.alert("��ʾ","��ע�澯���ʧ��?");
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
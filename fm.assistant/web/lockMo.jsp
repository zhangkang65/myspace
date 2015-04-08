<%@ page language="java" contentType="text/html; charset=GBK"%>
<%@ page import="java.util.*"%>
<%@ page import="com.linkage.toptea.context.*"%>
<%@ page import="com.linkage.toptea.auc.*"%>
<%@ page import="com.linkage.toptea.sysmgr.cm.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.responsible.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>��������</title>
<link rel="stylesheet" href="<%=request.getContextPath()+"/css/sysmgr.css"%>" type="text/css"/>
</head>
<script type="text/javascript" src=" <%=request.getContextPath()%>/webframe/webframe.js" ></script>
<body>
<%
String moid = request.getParameter("moid");
 User user = (User)Context.getContext().getAttribute(Context.USER); 
 ConfigManager cfmg = (ConfigManager)Context.getContext().getAttribute("configManager");
 UserManager userManager = (UserManager)Context.getContext().getAttribute("userManager");
 BusinessLogger businessLogger = (BusinessLogger)Context.getContext().getAttribute("businessLogger");
 ResponsibleManager rm = (ResponsibleManager)Context.getContext().getAttribute("responsibleManager");
 if(request.getMethod().equals("POST")){
    String islock =  request.getParameter("islock");
    if(islock!=null){
      //����
      String checkbox = request.getParameter("checkbox");
      if (checkbox==null) checkbox="false";
     
      try{
    	  if ( "0".equals(islock) ) { // ���������ĳ���
    		  // ȡ�ö���������
    		  List<ResponsibleMoInfo> infos = rm.getResponsiblesByMo( moid, null,0 ,20 );
    	  	  if ( infos != null && infos.size() > 0 ) {
    	  		  for ( ResponsibleMoInfo info : infos ) {
    	  			 ResponsibleInfo respinfo = info.getRespinfo();
    	  			 if ( respinfo != null ) {
    	  				 if ("".equals(respinfo.getResponsible1())) {respinfo.setResponsible1(null);}
    	  				 if ("".equals(respinfo.getResponsible2())) {respinfo.setResponsible2(null);}
    	  				 if ("".equals(respinfo.getResponsible3())) {respinfo.setResponsible3(null);}
    	  				 if ("".equals(respinfo.getResponsible4())) {respinfo.setResponsible4(null);}
    	  				 if ("".equals(respinfo.getResponsible5())) {respinfo.setResponsible5(null);}
    	  				 if ("".equals(respinfo.getResponsible6())) {respinfo.setResponsible6(null);}
    	  				 if ("".equals(respinfo.getResponsible7())) {respinfo.setResponsible7(null);}
    	  				 if ("".equals(respinfo.getResponsible8())) {respinfo.setResponsible8(null);}
    	  				 if ( respinfo.getResponsible1() == null 
    	  						 && respinfo.getResponsible2() == null 
    	  						 && respinfo.getResponsible3() == null 
    	  						 && respinfo.getResponsible4() == null 
    	  						 && respinfo.getResponsible5() == null 
    	  						 && respinfo.getResponsible6() == null 
    	  						 && respinfo.getResponsible7() == null 
    	  						 && respinfo.getResponsible8() == null ) {
    	  						out.println("<script>alert('����û�����������ˣ����ܽ�����');closeModalDialog();</script>");
    	      	  		 	 return;
    	  				 }
    	  			 } else {
    	  				out.println("<script>alert('����û�����������ˣ����ܽ�����'); closeModalDialog();</script>");
	      	  		 	 return;
    	  			 }
    	  		  }
    	  	  }

    	  }
        cfmg.changeMoStat(("1".equals(islock)?1:0),moid,user.getId(),request.getParameter("textarea"),checkbox.equals("true"));
        businessLogger.log("cm", moid, ("1".equals(islock)?"����������":"���������") + request.getParameter("textarea") );
        out.println("<script>alert('"+("1".equals(islock)?"�����ɹ�":"�����ɹ�")+"');selectionService.reloadView('configTreeView');</script>");
      }catch(Exception e){
         e.printStackTrace();
      }
    }
 }
 MoInfo mi = cfmg.findMoById(moid);
 if(mi!=null){
   int statelock =  cfmg.getMoStateLock(mi.getDotId());
   if (statelock==1){
      
	   if(mi.getLockUser()==null||mi.getLockUser().equals(user.getId())||"root".equals(user.getId())){
	      
	      %>
	      
	      
	       <form action="./lockMo.jsp" name="frm"  method="post">
   <input type="hidden" name="moid" value="<%=moid %>">
    <input type="hidden" name="islock" value="0">
   <table width="100%" border="0" height="100%">
<%if (mi.getLockUser()!=null){%>
   <tr>
   <td width="80" height=30>����ԭ��</td>
    <td>
    <%
    if(!mi.getLockUser().equals(user.getId())){
       User ur = userManager.getUser(mi.getLockUser());
       if (ur!=null) out.print("�����ˣ�"+ur.getName()+";ԭ��"+mi.getLockNotes());
    }else out.print(mi.getLockNotes()); %>
     </td>
   </tr>
<%}%>
  <tr>
    <td width="80" height=30>Ӧ���Ӷ���</td>
    <td>
      <input type="checkbox" name="checkbox" id="checkbox" checked  value="true"/>
     </td>
  </tr>
  <tr>
    <td>����ԭ��</td>
    <td>
      <textarea name="textarea" id="textarea" style="width:100%;height:100%" ></textarea>
</td>
  </tr>
 
</table>
</form>
 <script type="text/javascript">
var buttonTable = new Array (
		"����", new Function("unlockMo()"),
	    "�ر�", new Function("closeModalDialog()")
	);
	
	function unlockMo(){
	  if (frm.textarea.value==""){
	     alert("����д����ԭ��");
	     return;
	  }
	  frm.submit();
	}
	</script>

	      <%
	   }else{
	      User usr = userManager.getUser(mi.getLockUser());
	      if (usr!=null) out.print("�ö����Ѿ���"+usr.getName()+",�����������µ磺"+(user.getMobile()==null?"":user.getMobile()));
              else out.print("");
	      %>
	       <script type="text/javascript">
var buttonTable = new Array (
	    "�ر�", new Function("closeModalDialog()")
	);
	</script>
	      <%
	   }
   }else{
   %>
   <form action="./lockMo.jsp" name="frm"  method="post">
   <input type="hidden" name="moid" value="<%=moid %>">
    <input type="hidden" name="islock" value="1">
   <table width="100%" border="0" height="100%">
  <tr>
    <td width="80" height=30>Ӧ���Ӷ���</td>
    <td>
      <input type="checkbox" name="checkbox" id="checkbox" checked value="true"/>
     </td>
  </tr>
  <tr>
    <td>����ԭ��</td>
    <td>
      <textarea name="textarea" id="textarea" style="width:100%;height:100%" ></textarea>
</td>
  </tr>
 
</table>
</form>
 <script type="text/javascript">
var buttonTable = new Array (
		"����", new Function("lockMo()"),
	    "�ر�", new Function("closeModalDialog()")
	);
	
	function lockMo(){
	  if (frm.textarea.value==""){
	     alert("����д����ԭ��");
	     return;
	  }
	  frm.submit();
	}
	</script>
   <%
   }
 }else{ 
 
    out.print("û��MOIDΪ"+moid+"�Ķ���!");
 %>
 <script type="text/javascript">
var buttonTable = new Array (
	    "�ر�", new Function("closeModalDialog()")
	);
	</script>
 <%
 }
%>
</body>
</html>
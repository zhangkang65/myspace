<%@ page language="java" contentType="text/html; charset=GBK"%>
<%@ page import="java.util.*"%>
<%@ page import="com.linkage.toptea.context.*"%>
<%@ page import="com.linkage.toptea.auc.*"%>
<%@ page import="com.linkage.toptea.sysmgr.cm.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.responsible.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>对象锁定</title>
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
      //加锁
      String checkbox = request.getParameter("checkbox");
      if (checkbox==null) checkbox="false";
     
      try{
    	  if ( "0".equals(islock) ) { // 对象锁定的场合
    		  // 取得对象责任人
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
    	  						out.println("<script>alert('对象没有配置责任人，不能解锁！');closeModalDialog();</script>");
    	      	  		 	 return;
    	  				 }
    	  			 } else {
    	  				out.println("<script>alert('对象没有配置责任人，不能解锁！'); closeModalDialog();</script>");
	      	  		 	 return;
    	  			 }
    	  		  }
    	  	  }

    	  }
        cfmg.changeMoStat(("1".equals(islock)?1:0),moid,user.getId(),request.getParameter("textarea"),checkbox.equals("true"));
        businessLogger.log("cm", moid, ("1".equals(islock)?"对象锁定：":"对象解锁：") + request.getParameter("textarea") );
        out.println("<script>alert('"+("1".equals(islock)?"加锁成功":"解锁成功")+"');selectionService.reloadView('configTreeView');</script>");
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
   <td width="80" height=30>加锁原因</td>
    <td>
    <%
    if(!mi.getLockUser().equals(user.getId())){
       User ur = userManager.getUser(mi.getLockUser());
       if (ur!=null) out.print("加锁人："+ur.getName()+";原因："+mi.getLockNotes());
    }else out.print(mi.getLockNotes()); %>
     </td>
   </tr>
<%}%>
  <tr>
    <td width="80" height=30>应用子对象</td>
    <td>
      <input type="checkbox" name="checkbox" id="checkbox" checked  value="true"/>
     </td>
  </tr>
  <tr>
    <td>解锁原因</td>
    <td>
      <textarea name="textarea" id="textarea" style="width:100%;height:100%" ></textarea>
</td>
  </tr>
 
</table>
</form>
 <script type="text/javascript">
var buttonTable = new Array (
		"解锁", new Function("unlockMo()"),
	    "关闭", new Function("closeModalDialog()")
	);
	
	function unlockMo(){
	  if (frm.textarea.value==""){
	     alert("请填写解锁原因");
	     return;
	  }
	  frm.submit();
	}
	</script>

	      <%
	   }else{
	      User usr = userManager.getUser(mi.getLockUser());
	      if (usr!=null) out.print("该对象已经被"+usr.getName()+",如有疑问请致电："+(user.getMobile()==null?"":user.getMobile()));
              else out.print("");
	      %>
	       <script type="text/javascript">
var buttonTable = new Array (
	    "关闭", new Function("closeModalDialog()")
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
    <td width="80" height=30>应用子对象</td>
    <td>
      <input type="checkbox" name="checkbox" id="checkbox" checked value="true"/>
     </td>
  </tr>
  <tr>
    <td>锁定原因</td>
    <td>
      <textarea name="textarea" id="textarea" style="width:100%;height:100%" ></textarea>
</td>
  </tr>
 
</table>
</form>
 <script type="text/javascript">
var buttonTable = new Array (
		"锁定", new Function("lockMo()"),
	    "关闭", new Function("closeModalDialog()")
	);
	
	function lockMo(){
	  if (frm.textarea.value==""){
	     alert("请填写锁定原因");
	     return;
	  }
	  frm.submit();
	}
	</script>
   <%
   }
 }else{ 
 
    out.print("没有MOID为"+moid+"的对象!");
 %>
 <script type="text/javascript">
var buttonTable = new Array (
	    "关闭", new Function("closeModalDialog()")
	);
	</script>
 <%
 }
%>
</body>
</html>
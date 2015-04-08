<%@ page contentType="text/html; charset=gb2312" language="java" errorPage="/error.jsp" %>
<%@ page import="com.linkage.toptea.sysmgr.cm.*"%>
<%@ page import="com.linkage.toptea.sysmgr.web.*"%>
<%@ page import="com.linkage.toptea.context.*"%>
<%@ page import="com.linkage.toptea.sysmgr.util.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>ѡ�������</title>
</head>
<%
 //����ĳ����
	String dotid=(request.getParameter("dotid")==null)?"":request.getParameter("dotid");
 //�Ƿ�����հ�ť	
	String type=(request.getParameter("type")==null)?"":request.getParameter("type");
 //���Ƿ����ѡ
	final String isroot=(request.getParameter("isroot")==null)?"":request.getParameter("isroot");
 //�����Ƿ��ѡ
  final String ismeta=(request.getParameter("ismeta")==null)?"":request.getParameter("ismeta");
%>
<link rel="stylesheet" href="<%=request.getContextPath()+"/css/sysmgr.css"%>" type="text/css"/>
<link rel="stylesheet" href="<%=request.getContextPath()+"/css/stree_css.jsp"%>" type="text/css"/>
<script type="text/javascript" src=" <%=request.getContextPath()%>/webframe/webframe.js" ></script>
<script type="text/javascript">
var buttonTable = new Array (
<%	
	if(type.equals("clean"))
	{
%>
		"���", new Function("clean()"),
<%}%>		
		"�ر�", new Function("closeModalDialog()")
	);

//ѡ��mo����Ĳ���
function selectedMo(dotid,caption,path) {
    parent.addConfig(dotid,caption,path);
}

//ѡ�����͵Ĳ���
function selectedMetadata(id,caption,path) 
{
	return;
    if(callback(id,caption,path))
    {
			closeModalDialog();
	}
}
function clean()
{
	if(callback("","",""))
	{
		closeModalDialog();
    }  	
}
</script>
<body>

<%
	class MyTreeRenderer extends TreeRenderer2 {
   	  String contextPath;
   	  ConfigManager cm = (ConfigManager)Context.getContext().getAttribute("configManager");
   	  MyTreeRenderer(String contextPath) {
   	     this.contextPath = contextPath;
   	  }
   	  public String getIdentity(Object node) {
   	  	ConfigTreeNode n = (ConfigTreeNode)node;
   	  	if(n.getUserObject() instanceof MoInfo)
   	  		return ((MoInfo)n.getUserObject()).getDotId();
   	  	else if(n.getUserObject() instanceof MetadataInfo)
   	  		return ((MetadataInfo)n.getUserObject()).getType();
   	  	else
   	  		return n.getUserObject().toString();
   	  }

          public String getPath(Object node){
              return BeanUtil.getDotIdbyId(((ConfigTreeNode)node).getNodePath());
          }

	  public String getLinkURL(Object node)
	  {
		  	ConfigTreeNode n = (ConfigTreeNode)node;
	      if(node == n.getRoot()){
	          	return "javascript:selectedMo('-1','"+node.toString()+"','"+node.toString()+"')";
	      }
	 	  	if(n.getUserObject() instanceof MoInfo)
	 	  	{
	 	  		MoInfo mo = (MoInfo)n.getUserObject();
	 	  		return "javascript:selectedMo('"+mo.getDotPath()+ "','"+mo.getCaption()+"','"+mo.getDotId()+"')";
	 	  	}
	 	  	if(n.getUserObject() instanceof MetadataInfo) 
	 	  	{
	 	  		if(ismeta!=null&&ismeta.equals("true"))
	 	  		{
	 	  			MetadataInfo meta = (MetadataInfo)n.getUserObject();
	 	  			return "javascript:selectedMo('"+ this.getPath(node)+"','" + meta.getCaption() + "','"+ cm.getNamingPath(getPath(node)) + "')";
	 	  		}
	    	}
	    	return null;
	  }

	  public String getTarget(Object node){
	  	return null;
	  }

	  public String getIconURL(Object node) {
	  		
	    	return null;
	  }
	 
	  
   }
%>
<%
	ConfigManager configManager = (ConfigManager)Context.getContext().getAttribute("configManager");
	session.setAttribute("selViewTree", configManager.getConfigViewModel());
	if(session.getAttribute("selViewTree.renderer") == null) {
		session.setAttribute("selViewTree.renderer", new MyTreeRenderer(request.getContextPath()));
	}
	if(session.getAttribute("selViewTree.filter") == null) {
		session.setAttribute("selViewTree.filter", new ConfigTreeFilter());
	}
%>
<jsp:include page="/tree_servlet" flush="true">
	<jsp:param name="model" value="selViewTree"/>
	<jsp:param name="renderer" value="selViewTree.renderer"/>
	<jsp:param name="expand" value="0"/>
	<jsp:param name="filter" value="selViewTree.filter"/>
	<jsp:param name="shrink" value="true"/>
</jsp:include>
</body>

</html>

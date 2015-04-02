<%@ page contentType="text/html; charset=gb2312" language="java"	errorPage="/error.jsp"%>

<%
	/**生成页面左侧控制树*/
	try{
		String node = "["
					 +"{text:'应用',id:'resourceSvc',leaf:false,"
			         + "children:["
					 +"{text:'应用状态查询接口',id:'resInsStatusQuery',leaf:true},"
			         + "{text:'应用启停接口',id:'resInsOperate',leaf:true},"
			         + "{text:'集群资源同步接口',id:'clusterInsSync',leaf:true}," 
			       	 + "{text:'节点资源同步接口',id:'resInsSync',leaf:true}"
			         + "]},"
			         +"{text:'路由',id:'RouteSvc',leaf:false,"
			         +"children:["
			         +"{text:'路由查询接口',id:'routeQuerySync',leaf:true},"
			         +"{text:'路由修改接口',id:'routeUpdateSync',leaf:true}"
			         +"]},"  
			         +"{text:'BCN节点',id:'BCNSvc',leaf:false,"
			         +"children:["
			         +"{text:'BCN节点查询接口',id:'bCNQuerySync',leaf:true},"
			         +"{text:'BCN节点修改接口',id:'bCNUpdateSync',leaf:true},"
			         +"{text:'BCN集群查询接口',id:'bCNQuerySyncR',leaf:true},"
					 +"{text:'BCN集群修改接口',id:'bCNUpdateSyncR',leaf:true}"
			         +"]},"  
			         +"{text:'CSN节点',id:'CSNSvc',leaf:false,"
			         +"children:["
			         +"{text:'CSN节点查询接口',id:'cSNQuerySync',leaf:true},"
			         +"{text:'CSN节点修改接口',id:'cSNUpdateSync',leaf:true}"
			         +"]},"  
			         +"{text:'OCN节点',id:'OCNSvc',leaf:false,"
			         +"children:["
			         +"{text:'OCN节点查询接口',id:'oCNQuerySync',leaf:true},"
			         +"{text:'OCN节点修改接口',id:'oCNUpdateSync',leaf:true},"
			        +"{text:'OCN集群查询接口',id:'oCNQuerySyncR',leaf:true},"
			         +"{text:'OCN集群修改接口',id:'oCNUpdateSyncR',leaf:true}"
			         +"]}" 
			         +"]";

	   response.getWriter().write(node);
	   
	} catch (Exception e) {e.printStackTrace();}
%>

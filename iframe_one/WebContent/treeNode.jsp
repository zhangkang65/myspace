<%@ page contentType="text/html; charset=gb2312" language="java"	errorPage="/error.jsp"%>

<%
	/**����ҳ����������*/
	try{
		String node = "["
					 +"{text:'Ӧ��',id:'resourceSvc',leaf:false,"
			         + "children:["
					 +"{text:'Ӧ��״̬��ѯ�ӿ�',id:'resInsStatusQuery',leaf:true},"
			         + "{text:'Ӧ����ͣ�ӿ�',id:'resInsOperate',leaf:true},"
			         + "{text:'��Ⱥ��Դͬ���ӿ�',id:'clusterInsSync',leaf:true}," 
			       	 + "{text:'�ڵ���Դͬ���ӿ�',id:'resInsSync',leaf:true}"
			         + "]},"
			         +"{text:'·��',id:'RouteSvc',leaf:false,"
			         +"children:["
			         +"{text:'·�ɲ�ѯ�ӿ�',id:'routeQuerySync',leaf:true},"
			         +"{text:'·���޸Ľӿ�',id:'routeUpdateSync',leaf:true}"
			         +"]},"  
			         +"{text:'BCN�ڵ�',id:'BCNSvc',leaf:false,"
			         +"children:["
			         +"{text:'BCN�ڵ��ѯ�ӿ�',id:'bCNQuerySync',leaf:true},"
			         +"{text:'BCN�ڵ��޸Ľӿ�',id:'bCNUpdateSync',leaf:true},"
			         +"{text:'BCN��Ⱥ��ѯ�ӿ�',id:'bCNQuerySyncR',leaf:true},"
					 +"{text:'BCN��Ⱥ�޸Ľӿ�',id:'bCNUpdateSyncR',leaf:true}"
			         +"]},"  
			         +"{text:'CSN�ڵ�',id:'CSNSvc',leaf:false,"
			         +"children:["
			         +"{text:'CSN�ڵ��ѯ�ӿ�',id:'cSNQuerySync',leaf:true},"
			         +"{text:'CSN�ڵ��޸Ľӿ�',id:'cSNUpdateSync',leaf:true}"
			         +"]},"  
			         +"{text:'OCN�ڵ�',id:'OCNSvc',leaf:false,"
			         +"children:["
			         +"{text:'OCN�ڵ��ѯ�ӿ�',id:'oCNQuerySync',leaf:true},"
			         +"{text:'OCN�ڵ��޸Ľӿ�',id:'oCNUpdateSync',leaf:true},"
			        +"{text:'OCN��Ⱥ��ѯ�ӿ�',id:'oCNQuerySyncR',leaf:true},"
			         +"{text:'OCN��Ⱥ�޸Ľӿ�',id:'oCNUpdateSyncR',leaf:true}"
			         +"]}" 
			         +"]";

	   response.getWriter().write(node);
	   
	} catch (Exception e) {e.printStackTrace();}
%>

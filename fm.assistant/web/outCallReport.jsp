<%@ page contentType="text/html; charset=gb18030" language="java"
	errorPage="/error.jsp"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.web.*"%>
<%@ page import="com.linkage.toptea.context.*"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map.*"%>
<%@ page import=" java.io.*"%>

<%
	
	VoiceFindManagerImp voiceFindManagerImp = (VoiceFindManagerImp) Context
							.getContext().getAttribute("voiceFindManager");		
			
	ArrayList<OutCall> result = null;

	String startTime = request.getParameter("startTime");
	String endTime = request.getParameter("endTime");
	String flag = request.getParameter("flag");
	if("serch".equals(flag) ){
		result = voiceFindManagerImp.getPhonerStatData( startTime, endTime );
	}else if("daochu".equals(flag)){
		// 生成报表
		voiceFindManagerImp.report(startTime,endTime);
		
		String FS = System.getProperty("file.separator");
		String TOMCAT_HOME = System.getProperty("catalina.home");
		String PATH = "temp";
		String TEMP_PATH = TOMCAT_HOME + FS + PATH + FS;
		File tempDir = new File(TEMP_PATH + "outCallAlarm.xls");

		InputStream in = new FileInputStream(tempDir);
		OutputStream os = response.getOutputStream();
		response.addHeader("Content-Disposition", "attachment;filename=" 
			+ new String(tempDir.getName().getBytes("UTF-8"),"iso-8859-1"));
		response.addHeader("Content-Length", tempDir.length() + "");
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/octet-stream");
		int data = 0;
		while ((data = in.read()) != -1) { os.write(data); }
		os.close();
		in.close();
		out.clear();
		out = pageContext.pushBody();
		return;
	}

		
%>
<html>
	<head>
		<title>报表统计</title>
		<meta http-equiv="Content-Type" content="text/html; charset=GB2312" />
		<link rel="stylesheet"
			href="<%=request.getContextPath()%>/css/ttable_css.jsp"
			type="text/css" />
		<link rel="stylesheet"
			href="<%=request.getContextPath()%>/css/sysmgrcss" type="text/css" />
		<script type="text/javascript" src="../scripts/FusionCharts.js">
	
</script>

	</head>

	<body>
		<table class="standard" cellspacing='0' width='100%'>
			<thead>
				<tr>
					<td>
						所属组织
					</td>
					<td>
						人员
					</td>
					<td>
						失败总数
					</td>
					<td>
						成功总数
					</td>
					<td>
						失败比率
					</td>
					<td>
						成功比率
					</td>
				</tr>
			</thead>
			<tbody>
				<%
			  	  HashMap<String,ArrayList<OutCall>> hashmap = new HashMap<String,ArrayList<OutCall>>();
			  	  ArrayList<OutCall> arryll = null;
					for ( int i=0;i<result.size();i++ ) {
						OutCall oc = result.get(i);
						if ( hashmap.containsKey(oc.getCallerorganize()) ) {
							arryll = hashmap.get(oc.getCallerorganize());
							arryll.add( oc );
							hashmap.put(oc.getCallerorganize(),arryll);
						} else {
							arryll = new ArrayList<OutCall>();
							arryll.add( oc );
							hashmap.put(oc.getCallerorganize(),arryll);
						}
					    
					}
					 
					Iterator<String> iterator = hashmap.keySet().iterator();
                         while(iterator.hasNext()) {
                            String key = iterator.next();
                            ArrayList<OutCall> arroutcall = hashmap.get(key);
                            
                         for(int i=0;i< arroutcall.size();i++){
                         	out.println("<tr>");
                         	if ( i == 0 ) { 
                         	out.println("<td rowspan=" + arroutcall.size() + "  align='left'>" + key +"</td>"); }
                             %>
                           

                    <td align='left'>
					          <%= arroutcall.get(i).getCaller()%></td>
				            <td align='right'>
					          <%=  arroutcall.get(i).getFailnum()%></td>
				            <td align='right'>
					          <%=  arroutcall.get(i).getSunnum()%></td>
				            <td align='right'>
					          <%=  arroutcall.get(i).getFailra()%></td>
				            <td align='right'>
					          <%= arroutcall.get(i).getSunra()%></td>
                      <% 
                      
						out.print("</tr>");
                         }
                          
					}
				%>
			
			<tbody>
		</table>


	</body>
</html>




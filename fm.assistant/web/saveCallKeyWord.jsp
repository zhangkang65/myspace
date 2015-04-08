<%@ page contentType="text/html; charset=GB2312" language="java" errorPage="/error.jsp"%>
<%@ page import="com.linkage.toptea.context.*"%>
<%@page import="com.linkage.toptea.sysmgr.cm.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.callkeyword.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.util.*"%>

<%
  
	String caption = "";

	String id = (String)request.getParameter("id");
	if ( null == id ) { id = ""; }
	CallKeyWordManager callKeyWordManager = (CallKeyWordManager) Context.getContext().getAttribute("callKeyWordManager");
	ConfigManager configManager = (ConfigManager) Context.getContext().getAttribute("configManager");

	
	CallKeyWordInfo info = callKeyWordManager.indexCallKeyWordInfo(id);
	if ( info == null ) {
		info = new CallKeyWordInfo();
	} else {
		caption = configManager.getNamingPathById( info.getMoId() );
	}
	
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=GB2312" />
		<script type="text/javascript">
			Ext.onReady(function(){
				document.getElementById('rid').value = "<%= id%>";
				if ( "<%= id%>" != "" && "<%=( null != info) %>" == 'true') {					
					if ("<%= info.getForChild()%>" == 0 ) {
						document.getElementById('ifcname').checked = false;
					} else {
						document.getElementById('ifcname').checked = true;
					}
					document.getElementById('mname').value="<%=caption%>";
					document.getElementById('mid').value= "<%= info.getMoId()%>";
					//if ( <%= info.getRegExpr()%> == 1 ) {
						//document.getElementById('isRegExpr1').checked= true;
					//} else {
						//document.getElementById('isRegExpr0').checked= true;
					//}
										
					document.getElementById('keylistid').value= "<%= info.getKeyword()%>";
					document.getElementById('responsible').value= "<%= info.getCaller()%>";
				}
			});
		</script>	
	</head>
	<body>
		<form name="keywordsForm" method='post'>
			<table style='width:100%'> 
				<colgroup>
					<col width="60"   align="left"/>
					<col width="280"  align="left"/>
				</colgroup>
		    	<tr style='height:40'>
					<td>&nbsp;选择对象:</td>
					<td>
						<input type='text' readonly name='moName' id='mname' style="width:200" onclick="getCaption()"/>
	
						<input type='checkbox' name='isforchild' id='ifcname' checked />应用于子对象
					</td>
		    	</tr>

		    	<tr style='height:40'>
					<td>&nbsp;关&nbsp;键&nbsp;字: </td>
					<td >
						<input type='text'  name='keylist' style="width:300" id='keylistid' />
					</td>
		    	</tr>
				
		    	<tr style='height:40'>
					<td>&nbsp;责&nbsp;任&nbsp;人: </td>
					<td >
						<select id='responsible' name='responsible' style="width:300">
							<option value=0></option>
							<option value=1>平台类责任人一</option>
							<option value=2>平台类责任人二</option>
							<option value=3>业务类责任人一</option>
							<option value=4>业务类责任人二</option>
							<option value=5>备用类责任人一</option>
							<option value=6>备用类责任人二</option>
						</select>
					</td>
		    	</tr>
		    	<!--tr style='height:40'>
					<td>&nbsp;匹配方式: </td>	
					<td>
						<input type='radio' name='isRegExpr' value='1' id='isRegExpr1'/>正则表达式
						<input type='radio' name='isRegExpr' value='0' id='isRegExpr0'/>非正则表达式
					</td>					
		    	</tr-->
		    	
		    </table>
			 <input type='hidden' name='moid'     value='' id='mid'/>
			 <input type='hidden' name='rid'  value='' id='rid'/>
		</form>
	</body>
</html>
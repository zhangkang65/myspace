<%@ page contentType="text/html; charset=gb2312" language="java"	errorPage="/error.jsp"%>
<html>
	<head>
		<title></title>
		<style type="text/css">
		#from1{
		margin: 20px;  
		}
		
		</style>
		<script type="text/javascript">
		function doSubmit() {
			
			if(document.from1.resInsCode.value==""){
				alert("资源ID 不能为空 ");
				return;
			}
			
			document.from1.target="resForm";  //根据名字获取 
			document.from1.submit();
			return;
		}
		</script>
	</head>
	<body>
		<form  id="from1" name="from1" action="manager/bCNUpdateSync.do">
		
<!--  
	indicatorIsMonitor			?	boolean		指标监控				true/false
	batchNum	   				?	number		批量数				如：200
	dmcServiceThreadpoolSize	?	number		dmc服务暴露的线程池大小	如：200
	lbPackSize					?	number		分组打包大小			如：200
	RpcSendFailTime				?	number		rpc重发次数			如：5
	rpcTimeOut					?	number		rpc超时时间			如：120000
	rpcInterfaceConnections		?	number		rpc连接数				如：300
	workthreadNum				?	number		工作线程数			如：10
	workthreadTimeout			?	number		工作线程超时时间		如：90000000
	resInsCode					1	V20		        资源ID	
-->
		
		
		
			<table>
			<tr><td class="catalogue">指标监控:</td>
			<td  class="content">
			<input  type="radio" name="indicatorIsMonitor" value="false">错误
			<input type="radio"  name="indicatorIsMonitor" value="true">正确
			</td>
			</tr>
			
			<tr><td class="catalogue">批量数:</td>
			<td  class="content"><input type="text"  name="batchNum" value=""></td>
			</tr>
			
			<tr><td class="catalogue">dmc服务暴露的线程池大小:</td>
			<td  class="content"><input type="text"  name="dmcServiceThreadpoolSize" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">分组打包大小:</td>
			<td  class="content"><input type="text"  name="lbPackSize" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">rpc重发次数:</td>
			<td  class="content"><input type="text"  name="RpcSendFailTime" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">rpc超时时间:</td>
			<td  class="content"><input type="text"  name="rpcTimeOut" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">rpc连接数:</td>
			<td  class="content"><input type="text"  name="rpcInterfaceConnections" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">工作线程数:</td>
			<td  class="content"><input type="text"  name="workthreadNum" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">工作线程超时时间:</td>
			<td  class="content"><input type="text"  name="workthreadTimeout" value=""></td>
			</tr>
			
			<tr>
					<td class="catalogue">资源ID<span  style="color: red;">*</span>:</td>
					<td  class="content">
						<input type="text"  name="resInsCode" value="">
					</td>
				</tr>
			
				<tr>
				<td><input type="button" value='提交' onclick="doSubmit()"></td>
				<td><input type="reset" value='重置'></td>
				</tr>
			</table>
		</form>
	</body>
</html>
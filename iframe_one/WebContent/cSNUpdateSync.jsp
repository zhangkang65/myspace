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
	
		<form  id="from1" name="from1" action="manager/cSNUpdateSync.do" >
		
		
<!--  	
	nodeRunState		?	V20			节点运行状态	online/offline
	indicatorIsMonitor	?	V20			bds指标监控	true/false
	providerIothreads	?	number		IO线程		200
	providerThreads		?	number		线程			200
	providerConnections	?	number		连接数		200
	providerAccepts		?	number		最大连接数	300
	resInsCode			1	V20			资源ID	
	
-->
			<table>
			<tr><td class="catalogue">节点运行状态:</td>
			<td  class="content">
			<input  type="radio" name="nodeRunState" value="online">在线
			<input type="radio"  name="nodeRunState" value="offline">离线
			</td>
			</tr>
			
			<tr><td class="catalogue">bds指标监控:</td>
			<td  class="content">
			<input type="radio"  name="indicatorIsMonitor" value="true">正确
			<input type="radio"  name="indicatorIsMonitor" value="false">错误
			</td>
			</tr>
			
			<tr><td class="catalogue">IO线程:</td>
			<td  class="content">
			<input type="text"  name="providerIothreads" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">线程:</td>
			<td  class="content">
			<input type="text"  name="providerThreads" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">连接数:</td>
			<td  class="content">
			<input type="text"  name="providerConnections" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">最大连接数:</td>
			<td  class="content">
			<input type="text"  name="providerAccepts" value=""></td>
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
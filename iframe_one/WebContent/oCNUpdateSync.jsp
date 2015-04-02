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
		<form  id="from1" name="from1" action="manager/oCNUpdateSync.do">
		
<!--  
	indicatorIsMonitor	?	boolean		指标监控	true/false
	nettyWorkprocessNum	?	number		work线程数	如：20
	nettyBossprocessNum	?	number		boss工作线程数	如：20
	nettyBizThreadSize	?	number		业务线程池大小	如：100
	nettyBizThreadQueueSize	?	number		业务线程池缓冲队列大小	如：100
	nettyBizThreadRetryInterval	?	number		业务线程池任务提交失败重试间隔时间	如：20000
	nettyRcvbuf	?	number		netty接收缓冲区大小	如：65536
	nettySndbuf	?	number		netty发送缓冲区大小	如：65536
	resInsCode	1	V20		资源ID	
	
-->
		
		
		
			<table>
			<tr><td class="catalogue">指标监控:</td>
			<td  class="content">
			<input  type="radio" name="indicatorIsMonitor" value="false">错误
			<input type="radio"  name="indicatorIsMonitor" value="true">正确
			</td>
			</tr>
			
			<tr><td class="catalogue">work线程数:</td>
			<td  class="content"><input type="text"  name="nettyWorkprocessNum" value=""></td>
			</tr>
			
			<tr><td class="catalogue">boss工作线程数:</td>
			<td  class="content"><input type="text"  name="nettyBossprocessNum" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">业务线程池大小:</td>
			<td  class="content"><input type="text"  name="nettyBizThreadSize" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">业务线程池缓冲队列大小:</td>
			<td  class="content"><input type="text"  name="nettyBizThreadQueueSize" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">业务线程池任务提交失败重试间隔时间:</td>
			<td  class="content"><input type="text"  name="nettyBizThreadRetryInterval" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">netty接收缓冲区大小:</td>
			<td  class="content"><input type="text"  name="nettyRcvbuf" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">netty发送缓冲区大小:</td>
			<td  class="content"><input type="text"  name="nettySndbuf" value=""></td>
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
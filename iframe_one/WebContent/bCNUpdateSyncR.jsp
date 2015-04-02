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
		<form  id="from1" name="from1" action="manager/bCNUpdateSyncR.do">
			<table>
			<tr><td class="catalogue">信控开关:</td>
			<td  class="content">
			<input  type="radio" name="controlSwitch" value="on">打开
			<input type="radio"  name="controlSwitch" value="off">关闭
			</td>
			</tr>
			
			<tr><td class="catalogue">暴露端口:</td>
			<td  class="content"><input type="text"  name="dmcServicePort" value=""></td>
			</tr>
			
			<tr><td class="catalogue">线程池大小:</td>
			<td  class="content"><input type="text"  name="dmcServiceThreadpoolSize" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">超时时间:</td>
			<td  class="content"><input type="text"  name="dmcConsumerTimeOut" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">输出线程超时时间:</td>
			<td  class="content"><input type="text"  name="exportprocessTimeout" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">服务端最大链接数:</td>
			<td  class="content"><input type="text"  name="rpcInterfaceConnections" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">消息输出数:</td>
			<td  class="content"><input type="text"  name="outputMsgCount" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">批量数:</td>
			<td  class="content"><input type="text"  name="batchNum" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">工作线程超时时间:</td>
			<td  class="content"><input type="text"  name="workthreadTimeout" value=""></td>
			</tr>
			
				<tr><td class="catalogue">工作线程数:</td>
			<td  class="content"><input type="text"  name="workthreadNum" value=""></td>
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
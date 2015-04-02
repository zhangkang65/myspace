<%@ page contentType="text/html; charset=gb2312" language="java"
	errorPage="/error.jsp"%>
<html>
<head>
<title></title>
<style type="text/css">
#from1 {
	margin: 20px;
}
</style>
<script type="text/javascript">
	function doSubmit() {
		
		if(document.from1.resInsCode.value==""){
			alert("资源ID 不能为空 ");
			return;
		}
		
		document.from1.target = "resForm"; //根据名字获取 
		document.from1.submit();
		return;
	}
</script>
</head>
<body>
	<form id="from1" name="from1" action="manager/oCNUpdateSyncR.do">

		<!--  
	controlSwitch			?	V20			信控开关	on/off
	rpcTimeOut	  			?	number		rpc超时时间	如：20
	rpcInterfaceConnections	?	number		rpc连接数	如：20
	RpcSendFailTime			?	number		重发次数	如：100
	epPartMinute			?	number		分区时间	如：100
	epConfigSplitDuration	?	number		切单定时上限	如：20000
	epTimeout				?	number		ep预扣预锁超时	如：65536
	resInsCode				1	V20			资源ID	
-->

		<table>
			<tr>
				<td class="catalogue">信控开关:</td>
				<td class="content"><input type="radio" name="controlSwitch"
					value="on">打开 <input type="radio" name="controlSwitch"
					value="off">关闭</td>
			</tr>

			<tr>
				<td class="catalogue">rpc超时时间:</td>
				<td class="content"><input type="text" name="rpcTimeOut"
					value=""></td>
			</tr>

			<tr>
				<td class="catalogue">rpc连接数 :</td>
				<td class="content"><input type="text"
					name="rpcInterfaceConnections" value=""></td>
			</tr>


			<tr>
				<td class="catalogue">重发次数:</td>
				<td class="content"><input type="text" name="RpcSendFailTime"
					value=""></td>
			</tr>


			<tr>
				<td class="catalogue">分区时间:</td>
				<td class="content"><input type="text" name="epPartMinute"
					value=""></td>
			</tr>


			<tr>
				<td class="catalogue">切单定时上限:</td>
				<td class="content"><input type="text"
					name="epConfigSplitDuration" value=""></td>
			</tr>


			<tr>
				<td class="catalogue">ep预扣预锁超时:</td>
				<td class="content"><input type="text" name="epTimeout"
					value=""></td>
			</tr>

			<tr>
				<td class="catalogue">资源ID<span  style="color: red;">*</span>:</td>
				<td class="content"><input type="text" name="resInsCode"
					value=""></td>
			</tr>

			<tr>
				<td><input type="button" value='提交' onclick="doSubmit()"></td>
				<td><input type="reset" value='重置'></td>
			</tr>
		</table>
	</form>
</body>
</html>
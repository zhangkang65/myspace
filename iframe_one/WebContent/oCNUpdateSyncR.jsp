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
			alert("��ԴID ����Ϊ�� ");
			return;
		}
		
		document.from1.target = "resForm"; //�������ֻ�ȡ 
		document.from1.submit();
		return;
	}
</script>
</head>
<body>
	<form id="from1" name="from1" action="manager/oCNUpdateSyncR.do">

		<!--  
	controlSwitch			?	V20			�ſؿ���	on/off
	rpcTimeOut	  			?	number		rpc��ʱʱ��	�磺20
	rpcInterfaceConnections	?	number		rpc������	�磺20
	RpcSendFailTime			?	number		�ط�����	�磺100
	epPartMinute			?	number		����ʱ��	�磺100
	epConfigSplitDuration	?	number		�е���ʱ����	�磺20000
	epTimeout				?	number		epԤ��Ԥ����ʱ	�磺65536
	resInsCode				1	V20			��ԴID	
-->

		<table>
			<tr>
				<td class="catalogue">�ſؿ���:</td>
				<td class="content"><input type="radio" name="controlSwitch"
					value="on">�� <input type="radio" name="controlSwitch"
					value="off">�ر�</td>
			</tr>

			<tr>
				<td class="catalogue">rpc��ʱʱ��:</td>
				<td class="content"><input type="text" name="rpcTimeOut"
					value=""></td>
			</tr>

			<tr>
				<td class="catalogue">rpc������ :</td>
				<td class="content"><input type="text"
					name="rpcInterfaceConnections" value=""></td>
			</tr>


			<tr>
				<td class="catalogue">�ط�����:</td>
				<td class="content"><input type="text" name="RpcSendFailTime"
					value=""></td>
			</tr>


			<tr>
				<td class="catalogue">����ʱ��:</td>
				<td class="content"><input type="text" name="epPartMinute"
					value=""></td>
			</tr>


			<tr>
				<td class="catalogue">�е���ʱ����:</td>
				<td class="content"><input type="text"
					name="epConfigSplitDuration" value=""></td>
			</tr>


			<tr>
				<td class="catalogue">epԤ��Ԥ����ʱ:</td>
				<td class="content"><input type="text" name="epTimeout"
					value=""></td>
			</tr>

			<tr>
				<td class="catalogue">��ԴID<span  style="color: red;">*</span>:</td>
				<td class="content"><input type="text" name="resInsCode"
					value=""></td>
			</tr>

			<tr>
				<td><input type="button" value='�ύ' onclick="doSubmit()"></td>
				<td><input type="reset" value='����'></td>
			</tr>
		</table>
	</form>
</body>
</html>
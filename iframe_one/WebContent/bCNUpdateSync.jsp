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
				alert("��ԴID ����Ϊ�� ");
				return;
			}
			
			document.from1.target="resForm";  //�������ֻ�ȡ 
			document.from1.submit();
			return;
		}
		</script>
	</head>
	<body>
		<form  id="from1" name="from1" action="manager/bCNUpdateSync.do">
		
<!--  
	indicatorIsMonitor			?	boolean		ָ����				true/false
	batchNum	   				?	number		������				�磺200
	dmcServiceThreadpoolSize	?	number		dmc����¶���̳߳ش�С	�磺200
	lbPackSize					?	number		��������С			�磺200
	RpcSendFailTime				?	number		rpc�ط�����			�磺5
	rpcTimeOut					?	number		rpc��ʱʱ��			�磺120000
	rpcInterfaceConnections		?	number		rpc������				�磺300
	workthreadNum				?	number		�����߳���			�磺10
	workthreadTimeout			?	number		�����̳߳�ʱʱ��		�磺90000000
	resInsCode					1	V20		        ��ԴID	
-->
		
		
		
			<table>
			<tr><td class="catalogue">ָ����:</td>
			<td  class="content">
			<input  type="radio" name="indicatorIsMonitor" value="false">����
			<input type="radio"  name="indicatorIsMonitor" value="true">��ȷ
			</td>
			</tr>
			
			<tr><td class="catalogue">������:</td>
			<td  class="content"><input type="text"  name="batchNum" value=""></td>
			</tr>
			
			<tr><td class="catalogue">dmc����¶���̳߳ش�С:</td>
			<td  class="content"><input type="text"  name="dmcServiceThreadpoolSize" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">��������С:</td>
			<td  class="content"><input type="text"  name="lbPackSize" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">rpc�ط�����:</td>
			<td  class="content"><input type="text"  name="RpcSendFailTime" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">rpc��ʱʱ��:</td>
			<td  class="content"><input type="text"  name="rpcTimeOut" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">rpc������:</td>
			<td  class="content"><input type="text"  name="rpcInterfaceConnections" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">�����߳���:</td>
			<td  class="content"><input type="text"  name="workthreadNum" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">�����̳߳�ʱʱ��:</td>
			<td  class="content"><input type="text"  name="workthreadTimeout" value=""></td>
			</tr>
			
			<tr>
					<td class="catalogue">��ԴID<span  style="color: red;">*</span>:</td>
					<td  class="content">
						<input type="text"  name="resInsCode" value="">
					</td>
				</tr>
			
				<tr>
				<td><input type="button" value='�ύ' onclick="doSubmit()"></td>
				<td><input type="reset" value='����'></td>
				</tr>
			</table>
		</form>
	</body>
</html>
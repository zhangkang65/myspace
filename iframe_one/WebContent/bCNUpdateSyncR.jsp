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
		<form  id="from1" name="from1" action="manager/bCNUpdateSyncR.do">
			<table>
			<tr><td class="catalogue">�ſؿ���:</td>
			<td  class="content">
			<input  type="radio" name="controlSwitch" value="on">��
			<input type="radio"  name="controlSwitch" value="off">�ر�
			</td>
			</tr>
			
			<tr><td class="catalogue">��¶�˿�:</td>
			<td  class="content"><input type="text"  name="dmcServicePort" value=""></td>
			</tr>
			
			<tr><td class="catalogue">�̳߳ش�С:</td>
			<td  class="content"><input type="text"  name="dmcServiceThreadpoolSize" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">��ʱʱ��:</td>
			<td  class="content"><input type="text"  name="dmcConsumerTimeOut" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">����̳߳�ʱʱ��:</td>
			<td  class="content"><input type="text"  name="exportprocessTimeout" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">��������������:</td>
			<td  class="content"><input type="text"  name="rpcInterfaceConnections" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">��Ϣ�����:</td>
			<td  class="content"><input type="text"  name="outputMsgCount" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">������:</td>
			<td  class="content"><input type="text"  name="batchNum" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">�����̳߳�ʱʱ��:</td>
			<td  class="content"><input type="text"  name="workthreadTimeout" value=""></td>
			</tr>
			
				<tr><td class="catalogue">�����߳���:</td>
			<td  class="content"><input type="text"  name="workthreadNum" value=""></td>
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
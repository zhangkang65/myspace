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
		<form  id="from1" name="from1" action="manager/oCNUpdateSync.do">
		
<!--  
	indicatorIsMonitor	?	boolean		ָ����	true/false
	nettyWorkprocessNum	?	number		work�߳���	�磺20
	nettyBossprocessNum	?	number		boss�����߳���	�磺20
	nettyBizThreadSize	?	number		ҵ���̳߳ش�С	�磺100
	nettyBizThreadQueueSize	?	number		ҵ���̳߳ػ�����д�С	�磺100
	nettyBizThreadRetryInterval	?	number		ҵ���̳߳������ύʧ�����Լ��ʱ��	�磺20000
	nettyRcvbuf	?	number		netty���ջ�������С	�磺65536
	nettySndbuf	?	number		netty���ͻ�������С	�磺65536
	resInsCode	1	V20		��ԴID	
	
-->
		
		
		
			<table>
			<tr><td class="catalogue">ָ����:</td>
			<td  class="content">
			<input  type="radio" name="indicatorIsMonitor" value="false">����
			<input type="radio"  name="indicatorIsMonitor" value="true">��ȷ
			</td>
			</tr>
			
			<tr><td class="catalogue">work�߳���:</td>
			<td  class="content"><input type="text"  name="nettyWorkprocessNum" value=""></td>
			</tr>
			
			<tr><td class="catalogue">boss�����߳���:</td>
			<td  class="content"><input type="text"  name="nettyBossprocessNum" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">ҵ���̳߳ش�С:</td>
			<td  class="content"><input type="text"  name="nettyBizThreadSize" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">ҵ���̳߳ػ�����д�С:</td>
			<td  class="content"><input type="text"  name="nettyBizThreadQueueSize" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">ҵ���̳߳������ύʧ�����Լ��ʱ��:</td>
			<td  class="content"><input type="text"  name="nettyBizThreadRetryInterval" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">netty���ջ�������С:</td>
			<td  class="content"><input type="text"  name="nettyRcvbuf" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">netty���ͻ�������С:</td>
			<td  class="content"><input type="text"  name="nettySndbuf" value=""></td>
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
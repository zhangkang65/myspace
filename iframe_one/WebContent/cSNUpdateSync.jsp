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
	
		<form  id="from1" name="from1" action="manager/cSNUpdateSync.do" >
		
		
<!--  	
	nodeRunState		?	V20			�ڵ�����״̬	online/offline
	indicatorIsMonitor	?	V20			bdsָ����	true/false
	providerIothreads	?	number		IO�߳�		200
	providerThreads		?	number		�߳�			200
	providerConnections	?	number		������		200
	providerAccepts		?	number		���������	300
	resInsCode			1	V20			��ԴID	
	
-->
			<table>
			<tr><td class="catalogue">�ڵ�����״̬:</td>
			<td  class="content">
			<input  type="radio" name="nodeRunState" value="online">����
			<input type="radio"  name="nodeRunState" value="offline">����
			</td>
			</tr>
			
			<tr><td class="catalogue">bdsָ����:</td>
			<td  class="content">
			<input type="radio"  name="indicatorIsMonitor" value="true">��ȷ
			<input type="radio"  name="indicatorIsMonitor" value="false">����
			</td>
			</tr>
			
			<tr><td class="catalogue">IO�߳�:</td>
			<td  class="content">
			<input type="text"  name="providerIothreads" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">�߳�:</td>
			<td  class="content">
			<input type="text"  name="providerThreads" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">������:</td>
			<td  class="content">
			<input type="text"  name="providerConnections" value=""></td>
			</tr>
			
			
			<tr><td class="catalogue">���������:</td>
			<td  class="content">
			<input type="text"  name="providerAccepts" value=""></td>
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
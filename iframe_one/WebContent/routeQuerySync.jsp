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
			document.from1.target="resForm";  //�������ֻ�ȡ 
			document.from1.submit();
			return;
		}
		</script>
	</head>
	<body>
		<form  id="from1" name="from1" action="manager/routeQuerySync.do">
			<table>
			<tr>
			<td>
			�˽ӿ��޲����ύ���ύ����ȷ����
			<input type="button" value='ȷ��' onclick="doSubmit()">
			</td>
			</tr>
			
			</table>
		</form>
	</body>
</html>
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
		<form  id="from1" name="from1" action="manager/routeUpdateSync.do">
			<table>
				<tr>
					<td class="catalogue">·�ɱ��ʽ:</td>
					<td  class="content">
						<input type="text"  name="express"  value=""> 
					</td>
				</tr>
				
				<tr>
					<td class="catalogue">·�ɹ���ID:</td>
					<td  class="content"> 
						<input type="text"  name="id" value="">
					</td>
				</tr>
				
				<tr>
					<td class="catalogue">Ŀ��ID:</td>
					<td>
					  	<input  type="text"  name="targetId"  value="">
					</td>
				</tr>
				
				<tr>
					<td  class="catalogue">״̬:</td>
					<td>
					 <input  type="text"  name="status"  value="">
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
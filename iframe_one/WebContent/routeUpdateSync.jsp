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
			document.from1.target="resForm";  //根据名字获取 
			document.from1.submit();
			return;
		}
		</script>
	</head>
	<body>
		<form  id="from1" name="from1" action="manager/routeUpdateSync.do">
			<table>
				<tr>
					<td class="catalogue">路由表达式:</td>
					<td  class="content">
						<input type="text"  name="express"  value=""> 
					</td>
				</tr>
				
				<tr>
					<td class="catalogue">路由规则ID:</td>
					<td  class="content"> 
						<input type="text"  name="id" value="">
					</td>
				</tr>
				
				<tr>
					<td class="catalogue">目标ID:</td>
					<td>
					  	<input  type="text"  name="targetId"  value="">
					</td>
				</tr>
				
				<tr>
					<td  class="catalogue">状态:</td>
					<td>
					 <input  type="text"  name="status"  value="">
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
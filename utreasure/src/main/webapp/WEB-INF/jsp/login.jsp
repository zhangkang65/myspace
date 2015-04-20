<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel='icon' href="<%=basePath%>images/front.ico" type="image/x-ico" />
<title>register</title>
<!-- js  css 信息 -->
<link rel="stylesheet"
	href="<%=basePath%>jquery.mobile-1.3.2/jquery.mobile-1.3.2.css">
<script src="<%=basePath%>jquery.mobile-1.3.2/jquery-1.8.3.min.js"></script>
<script src="<%=basePath%>jquery.mobile-1.3.2/jquery.mobile-1.3.2.js"></script>
<script type="text/javascript">
	
</script>
<style type="text/css">
</style>
</head>
<body>
	<div data-role="page">
		<div data-role="header">
			<h3>登陆信息</h3>
		</div>
		<div data-role="content">
			<form method="post" action="<%=basePath%>user/login.do">
				<label for="fname" class="ui-hidden-accessible">登陆名：</label> 
				<input  type="text" name="loginKey" id="fname" placeholder="姓名...">
				
				<label for="fname" class="ui-hidden-accessible">密码：</label> 
				<input  type="password" name="pwd" id="fname" placeholder="密码...">
				
				
				<input type="submit"  data-icon="check" data-inline="true" value="提交">
				
			</form>


		</div>
	</div>
</body>
</html>
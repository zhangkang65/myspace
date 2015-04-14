<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel='icon' href="<%=basePath%>images/front.ico" type="image/x-ico"/> 
<title>needless treasure</title>
<style type="text/css">
*{
margin: 0px;
padding: 0px;
}

.fullScreen{
	position:absolute;
	width: 100%;
	height: 100%;
	background:#F8F8F8;
}

.head{
	position:relative;
	width: 100%;
	height: 8%;
	background:#E0E0E0; 
}

.head  .head_left{
	position:relative;
	float:left;
	margin-left: 10px;
}

.head  .head_right{
	position:relative;
	margin-left:1000px;
}

</style>
</head>
<body>
	<div  class="fullScreen">
		<div  class="head">
				<div class="head_left">
						<a href="">帮助</a>
				</div>
				
				<div class="head_right">
					<a href="">登陆</a>
					<a href="user/toRegister.do">注册</a>	
				</div>
		</div>
	
	
	
	</div>
</body>
</html>
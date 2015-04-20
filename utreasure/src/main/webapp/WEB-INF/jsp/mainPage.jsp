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
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel='icon' href="<%=basePath%>images/front.ico" type="image/x-ico"/> 
<!-- js  css 信息 -->
<link rel="stylesheet" href="<%=basePath%>jquery.mobile-1.3.2/jquery.mobile-1.3.2.css">
<script src="<%=basePath%>jquery.mobile-1.3.2/jquery-1.8.3.min.js"></script>
<script src="<%=basePath%>jquery.mobile-1.3.2/jquery.mobile-1.3.2.js"></script>
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
}

.head{
	position:relative;
	width: 100%;
	height: 10%;
}

.head  .head_left{
	position:relative;
	float:left;
	margin-left: 10px;
}

.head  .head_right{
	position:relative;
}

</style>
</head>
<body>
	<div  class="fullScreen"  data-role="page">
		<div  class="head"  data-role="header">
				<div class="head_left">
						<a href="#">帮助</a>
				</div>
				
				<div class="head_right">
					<a href="<%=basePath%>user/toLogin.do">登陆</a>
					<a href="<%=basePath%>user/toRegister.do">注册</a>	
				</div>
		</div>
		
		<div>
		
		
		</div>
	
	
	</div>
</body>
</html>
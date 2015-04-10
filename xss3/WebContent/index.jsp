<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action="XssServlet"  method="get"  >
	字符校验1：<input type="text"  name="a"><br>
	字符校验2：<input type="text"  name="b"><br>
	字符校验3：<input type="text"  name="c"><br>
	
	<input type="submit"  value="提交 ">
	 <!-- 点击直接提交尝试  -->
	 输入内容为：<%=request.getAttribute("content1") %>
</form>
</body>
</html>
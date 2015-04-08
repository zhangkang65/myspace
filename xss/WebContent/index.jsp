<%@ page language="java" contentType="text/html; charset=GBK"  pageEncoding="GBK"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<link rel="stylesheet" href="js/resources/css/ext-all.css"  type="text/css" />
<script type="text/javascript" src="js/ext-base.js"></script>
<script type="text/javascript" src="js/ext-all.js"></script>
<script type="text/javascript" src="js/ext-lang-zh_CN.js"></script>
<title>Insert title here</title>
<script type="text/javascript">
Ext.onReady(function(){
});

function  tijiao(){
	Ext.Ajax.request({
		   url:'XssServlet',
		   params:{'a':'<<','b':'>>'},
		   success: function(reg){
			   //打开一个新的窗体显示，返回页面的内容 
			//var  newWin=window.open('','');
			//newWin.document.write(reg);
			//newWin.document.write(reg);
			//newWin.focus(); 
		   },
		   failure: function(reg){
			   var json=eval(reg.responseText);
			   alert("failure"+json);
		   }
		});
}


function  openNew(){  
	myWindow=window.open('b.html','_blank')
	console.info(myWindow);
	myWindow.document.write("This is 'myWindow'")
	myWindow.focus()
}

</script>
</head>
<body>
<form action="XssServlet" method="get">
	1、xss字符校验：<input type="text"  name="b"  value=""><br/>
	2、有返回的text:<input type="text" name="c" value=""><br/>
	3、a标签的存在:<input type="text" name="a" value=""><br/>
	<input type="button"  value="ajax提交数据"  onclick="tijiao()">
	<input type="button"  value="打开新窗体"  onclick="openNew()">
	<br/>
	<br/>
	<hr noshade color="#0066cc">
	<!--
	//如果上面程序使用了如下 就会跳转  
	<input  type="text" name="b"  value="  aa" onfocus="alert('a')  ">

	aa" onmouseout="alert('a')"
	//如果上面focus点击了之后 就会改变地址，跳转到现在的连接 ,如果一条转回来直接获得焦点  
	aa" onfocus="var link=document.getElementsByTagName('a');link[0].href='http://www.sina.com/';"
	
	-->
	<input type="submit"  value="提交">
</form>
</body>
</html>
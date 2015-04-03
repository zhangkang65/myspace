<%@ page contentType="text/html; charset=GBK" language="java"%>

<html>
<head>
<link rel="stylesheet" type="text/css"  href="extjs/resources/css/ext-all.css" />
<script type="text/javascript" src="extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="extjs/ext-all.js"></script>
<script type="text/javascript" src="extjs/ext-ext.js"></script>
<script type="text/javascript" charset="utf-8"src="extjs/ext-lang-zh_CN.js"></script>
<script type="text/javascript">
Ext.onReady(function(){
	document.forms[0].btn.onclick=function(){
		var  fromVal=document.forms[0].myname.value;
		
		Ext.Ajax.request({
			   url: 'XssServlet',
			   params: {myname:fromVal},
			   success: function(resp,opts){
				 var  reText=resp.responseText;
				 document.getElementById("myDiv").innerHTML=reText;
			   }
			});
	};
})
</script> 
</head>
<body>
	<h2>Hello World!</h2>



	<form action="XssServlet" method="get">
		<input type="text" name="myname" ><br />
		 <input type="button" name="btn" onclick="" value="submit">
	</form>
	
	<div id="myDiv">
	<!--  
	<script type="text/javascript">alert("hello")</script>
	-->
	
	
	</div>
</body>
</html>

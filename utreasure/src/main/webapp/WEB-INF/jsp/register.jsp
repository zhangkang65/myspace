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
<script type="text/javascript"  src="jquery.mobile-1.3.2/jquery-1.8.3.min.js"></script>
<script type="text/javascript">
function registerInfo(){
	var  loginName=$("#user_profile_name").val();
	var  email=$("#user_profile_email").val();
	var  password=$("#user_profile_password").val();
	var  password2=$("#user_profile_password2").val();
	 
	/**---------js正则校验-----------
		1、用户名 5-13位 数字 字母 下划线 
	    2、密码三种混合
	    3、邮箱校验 且  发送验证邮件
	**/
	
	var reLoginName = /^[a-z_A-z]\w{4,12}$/;
	    if(reLoginName.test(loginName)){
	        return  true;
	    }else{
	    	return  false;
	    }       
}

   
//ajax 
$(function(){
	$("#user_profile_name").bind("blur", function(){
		var _this=$(this);
		if(_this.val()){   //如果存在值
			$.ajax({
				   type: "POST",
				   url: "user/getUserName.do",
				   data:"loginName="+_this.val(),
				   success: function(msg){
					  var  Otip=$("#user_profile_tip"); 
					  var oDiv= document.createElement('span');
					  if(eval(msg)){
						 oDiv.innerHTML ='此账号可以正常使用 ';
						 oDiv.className="normalTip";
						 $(".btn-primary").attr("disabled",false);  //开启按钮提交  
					  }else{
						 oDiv.innerHTML ='对不起，此账号已经存在';
						 oDiv.className="warnTip";
						$(".btn-primary").attr("disabled",true);
						/// oLay.style.display = "block";
					  }
					  Otip.html(oDiv);
				   }
				});
			}
		});
})
</script>
<style type="text/css">
.warnTip{
color: red;
}


.normalTip{
color:blue;
}

</style>
</head>
<body>
	<div class="boxed-group">
		<h3>注册信息</h3>
		<div>
			<form accept-charset="UTF-8" action="user/doRegister.do"  method="post"  onsubmit="return  registerInfo()">
				<div>
					<dl class="form">
						<dt>
							<label for="user_profile_name">登陆名</label>
						</dt>
						<dd>
							<input id="user_profile_name" name="loginName" size="30" type="text" />
						</dd>
						<dd id="user_profile_tip">
						</dd>
					</dl>
					<dl class="form">
						<dt>
							<label for="user_profile_email">邮箱</label>
						</dt>
						<dd>
							<input id="user_profile_email" name="email" size="30" type="email" />
						</dd>
						<dd>
							<span  id="user_profile_email"></span>
						</dd>
					</dl>
					<dl class="form">
						<dt>
							<label for="user_profile_password">密码</label>
						</dt>
						<dd>
							<input id="user_profile_password" name="password" size="30" type="password" />
						</dd>
					</dl>
					<dl class="form">
						<dt>
							<label for="user_profile_password2">密码确认</label>
						</dt>
						<dd>
							<input id="user_profile_password2" name="password2" size="30" type="password" />
						</dd>
					</dl>
					<p>
						<button type="submit" class="btn-primary" >确认注册</button>
					</p>
				</div>
			</form>
		</div>
	</div>
</body>
</html>
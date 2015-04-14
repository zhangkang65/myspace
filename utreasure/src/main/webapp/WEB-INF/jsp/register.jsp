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
<style type="text/css">
.avatar-upload .upload-state {
    display: none;
    padding: 10px 0;
}


.btn .manual-file-chooser {
    line-height: 34px;
    padding: 0;
    top: 0;
}




</style>
</head>
<body>
	<div class="boxed-group">
		<h3>Public profile</h3>
		<div class="boxed-group-inner clearfix">
			<form accept-charset="UTF-8" action="user/doRegister.do"  method="post">
				<div class="column two-thirds">
					<dl class="form edit-profile-avatar">
						<dd class="avatar-upload-container clearfix">
							<img  class="avatar left"   height="70"  src="images/headicon.png" width="70" />
							<div class="avatar-upload">
								<label  for="upload-profile-picture">
								<input id="upload-profile-picture" type="file"  class="manual-file-chooser js-manual-file-chooser js-avatar-field">
								</label>


								<div class="upload-state loading">
									<span class="btn disabled"> <img alt="" height="16"
										src="https://assets-cdn.github.com/assets/spinners/octocat-spinner-32-e513294efa576953719e4e2de888dd9cf929b7d62ed8d05f25e731d02452ab6c.gif"
										width="16" /> Uploading...
									</span>
								</div>

								<div class="upload-state text-danger file-empty">This file is empty.</div>
								<div class="upload-state text-danger too-big">Please
									upload a picture smaller than 1 MB.</div>

								<div class="upload-state text-danger bad-dimensions">
									Please upload a picture smaller than 10,000x10,000.</div>

								<div class="upload-state text-danger bad-file">
									Unfortunately, we only support PNG, GIF, or JPG pictures.</div>

								<div class="upload-state text-danger bad-browser">This
									browser doesn't support uploading pictures.</div>

								<div class="upload-state text-danger failed-request">
									Something went really wrong and we can't process that picture.
								</div>
							</div>
						</dd>
					</dl>
           
					<dl class="form">
						<dt>
							<label for="user_profile_name">登陆名</label>
						</dt>
						<dd>
							<input id="user_profile_name" name="loginName" size="30" type="text" />
						</dd>
					</dl>
					<dl class="form">
						<dt>
							<label for="user_profile_email">邮箱</label>
						</dt>
						<dd>
							<input id="user_profile_blog" name="email" size="30" type="email" />
						</dd>
					</dl>
					<dl class="form">
						<dt>
							<label for="user_profile_blog">密码</label>
						</dt>
						<dd>
							<input id="user_profile_blog" name="password" size="30" type="password" />
						</dd>
					</dl>
					<dl class="form">
						<dt>
							<label for="user_profile_company">密码确认</label>
						</dt>
						<dd>
							<input id="user_profile_blog" name="password2" size="30" type="password" />
						</dd>
					</dl>
					<p>
						<button type="submit" class="btn btn-primary">submit info</button>
					</p>
				</div>
			</form>
		</div>
	</div>


</body>
</html>
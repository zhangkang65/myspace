<%@ page contentType="text/html; charset=GB2312" errorPage="/error.jsp"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.*" %>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.concern.*" %>
<%@ page import="com.linkage.toptea.auc.SecurityManager"%>
<%@ page import="com.linkage.toptea.context.Context"%>
<%@ page import="com.linkage.toptea.auc.*"%>
<%
/**
*功能：关注跟踪告警页面
*/

SecurityManager securityManager = (SecurityManager)Context.getContext().getAttribute("securityManager");
User user = (User) Context.getContext().getAttribute(Context.USER);

int isEdit = 0;
if (user!=null) {
	isEdit= securityManager.check(user.getId(), "界面功能域", "/default/concernAlarm/concernConfig", '*');
}

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
		<script type='text/javascript'> var isCfg=<%=isEdit%>;</script>
		<script type='text/javascript'> var loginUserId="<%=user.getId()%>";</script>
		
		<link rel="stylesheet" type="text/css" href="/sysmgr/css/sysmgr.css"/>
		<link rel="stylesheet" type="text/css" href="/extjs/resources/css/ext-all.css" />
		<script type="text/javascript" src="/extjs/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="/extjs/ext-all.js"></script>
		<script type="text/javascript" src="/extjs/ext-ext.js"></script>
		<script type="text/javascript" src="/extjs/source/locale/ext-lang-zh_CN.js" ></script>

		<script type='text/javascript' src='/sysmgr/dwr/interface/QLDelegate.js'></script>
		<script type='text/javascript' src='/sysmgr/dwr/interface/ctx.js'></script>
		<script type='text/javascript' src='/sysmgr/dwr/engine.js'></script>
		<script type='text/javascript' src='/sysmgr/dwr/util.js'></script>
		<script type='text/javascript' src='callphone.js'></script>
	</head>
	<style>

		.forward{
		   background-image:url(../../images/icons/relateRule.gif) !important;
		}
		.cannel{
		   background-image:url(../../images/default.gif) !important;
		}
		.track{
		   background-image:url(./images/track.png) !important;
		}

		.modify{
		   background-image:url(../../images/icons/modify.gif) !important;
		}
		.linkback{
		   background-image:url(../../images/icons/hideFwdAlarm.gif) !important;
		}
		.stop{
		   background-image:url(../../images/icons/stop.gif) !important;
		}
		.sms{
		 background-image:url(../../images/icons/sendSms.gif) !important;
		}
		.search{
		   background-image:url(../../images/search.gif) !important;
		}
		.swp{
		   background-image:url(../../images/swp.gif) !important;
		}
		.save{
		   background-image:url(../../images/icons/save.gif) !important;
		}
		.xls{
		   background-image:url(../../images/xls.gif) !important;
		}
		.stop2{
		   background-image:url(./images/stop.gif) !important;
		}
		.manager{
		   background-image:url(./images/manager.gif) !important;
		}
		.del{
		   background-image:url(../../images/icons/cancel.gif) !important;
		}
		.help{
		   background-image:url(./images/help.png) !important;
		}
		.add{
		   background-image:url(../../images/addrule.gif) !important;
		}
		.myLabel{
			border-left:0px;
			border-top:0px;
			border-bottom:0px;
			border-right:0px;
			background:transparent;
			cursor:hand;
		}
		.left{
		   background-image:url(./images/arrow_left.png) !important;
		}
		.right{
		   background-image:url(./images/arrow_right.png) !important;
		}
	</style>
	<script>
		Ext.onReady(function(){
		
			var g = showAllCallPhone();
			var viewport = new Ext.Viewport({ layout:'border', items:[g] });
		});
	</script>
	<body></body>
</html>
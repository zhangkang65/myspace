<%@ page contentType="text/html; charset=GB2312" language="java" errorPage="/error.jsp"%>
<%@ page import="org.springframework.web.bind.ServletRequestUtils"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.*"%>

<html>
  <head>
	<meta http-equiv="Content-Type" content="text/html; charset=GB2312" />
	<link rel="stylesheet" type="text/css" href="/extjs/resources/css/ext-all.css" />
	<script type="text/javascript" src="/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" src="/extjs/ext-all.js"></script>
	<script type="text/javascript" charset="utf-8" src="/extjs/source/locale/ext-lang-zh_CN.js"></script>	
	<script type="text/javascript" src="<%=request.getContextPath()%>/webframe/webframe.js" language="javascript"></script>
	<script type='text/javascript' src='/sysmgr/dwr/interface/ctx.js'></script>
	<script type='text/javascript' src='/sysmgr/dwr/engine.js'></script>
	<script type='text/javascript' src='/sysmgr/dwr/util.js'></script>
	<script type="text/javascript">var contextPath = "<%=request.getContextPath()%>";</script>
	<script type='text/javascript' src='./callKeyWord.js'></script>
	<script type='text/javascript' src='/sysmgr/js/dump.js'></script>
		
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/sysmgr.css" type="text/css"/>
	<style type="text/css" media="all">
		 .txt-field {float:left;padding-left: 10px;  padding-right: 65px;  padding-top: 15px;}
		 .area-field {float:left;padding-left: 10px;  padding-right: 65px;  padding-top: 20px;}
	</style>
	
	<script type="text/javascript">	
				
		Ext.BLANK_IMAGE_URL = '/extjs/resources/images/default/s.gif';		
		Ext.QuickTips.init();
		
		Ext.onReady(function(){
			init();
		    storeMo.load({params:{ start:0,limit:200 }});
			storeKey.load({params:{ start:0,limit:200 }});
			var viewport = new Ext.Viewport({ layout:'border', items:[leftTabs,keyPanel] });
			leftTabs.doLayout();
			leftTabs.activate(1);

	    	gridMo.on("cellclick",showFont);
	    	gridKey.on("cellclick",showFont);
			
		    storeKey.on("load",function (s, r, e) { showFont (gridKey, 0) });
		    storeMo.on("load",function (s, r, e) { showFont (gridMo, 0) });

		    storeMo.on("beforeload",function (s, r, e) {
		    	if (oldRecode) oldRecode.set("caption", oldData); 
		    	oldRecode = false;
		    });
		     storeKey.on("beforeload",function (s, r, e) {
		    	if (oldRecode) oldRecode.set("caption", oldData); 
		    	oldRecode = false;
		    });
		    
	        window.setTimeout(function () {deleteStyle(gridMo.getView());},50);
	        
	        //此处会导致页面闪烁，因此屏蔽
	        gridMo.on("mouseover",function(e){ showPanelTips(gridMo); });
	        gridKey.on("mouseover",function(e){ showPanelTips(gridKey); });
	   		
		});
		
  </script>
</head>
  <body>
  </body>
</html>

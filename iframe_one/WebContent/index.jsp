<%@ page contentType="text/html; charset=gb2312" language="java" errorPage="/error.jsp"%>
<html>
	<head>
		<title>告警管理</title>
		<meta http-equiv="Content-Type" content="text/html; charset=GB2312" />
		<link rel="stylesheet" type="text/css" href="extjs/resources/css/ext-all.css" />
		<script type="text/javascript" src="extjs/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="extjs/ext-all.js"></script>
		<script type="text/javascript" src="extjs/ext-ext.js"></script>
		<script type="text/javascript" charset="utf-8" src="extjs/ext-lang-zh_CN.js"></script>
		<script type="text/javascript">
			Ext.BLANK_IMAGE_URL = 'extjs/resources/images/default/s.gif';
			//页面右边部分
			var tPanel = new Ext.Panel({
				title:"接口条件",
	        	autoScroll:true,
	        	minWidth:800,	
                height: 350,
				html:"<iframe id='condForm' src='' width='100%' height='100%'  frameborder=0></iframe>"
				
			});
			
			//页面右边部分
			var bPanel = new Ext.Panel({
				title:"输出结果",
	        	autoScroll:true,
	        	minWidth:800,	
                height: 200,
				html:"<iframe name='resForm' id='resForm' src='' width='100%' height='100%'  frameborder=0></iframe>"
			});
			
			//页面右边部分
			var mainPanel = new Ext.Panel({
	        	id:"mainP",
	        	collapsible: true,
				autoScroll:true,
	        	minWidth:800,
	        	region:'center',
				items:[tPanel,bPanel]
			});

			//页面左边部分控制树
		    var root = new  Ext.tree.AsyncTreeNode({text:'',id:'root'});
			var tree = '';
				 
			 Ext.onReady(function(){
				tree = new Ext.tree.TreePanel({
					id:'navigation',
					region:'west',
					title:'NGBOSS',
				    autoScroll:true,
				    rootVisible:false,
					root:root,
					lines:false,
					collapsible: true,
					loader: new Ext.tree.TreeLoader({dataUrl:"treeNode.jsp"}),
					collapseFirst:false,
					width:200,
					height: window.document.body.clientHeight
				});


				//页面整体框架
			    var viewport = new Ext.Viewport({
					layout:'border',
					items:[tree,mainPanel]
			    });
			  
				root.expand(true, false);
				tree.on("click",function(node, event){
					if(node.leaf){ //如果是叶子节点 
						htmlTarget = node.id + '.jsp';
						title = node.text;
						
						 if(htmlTarget){
						    event.stopEvent();
							addTab(node.id,title,htmlTarget);
						}
					}
				});

			});
		
			function addTab(tabId,name,href) {
				tPanel.setTitle(name);
				mainPanel.id = tabId;
				document.getElementById("condForm").src=href;
			}
			
			root.on('load', function(){
		    	tree.expandAll();
			});
		</script>
	</head>
	<body>
	</body>
</html>

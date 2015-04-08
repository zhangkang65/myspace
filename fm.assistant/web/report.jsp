<%@ page contentType="text/html; charset=GB2312" language="java" errorPage="/error.jsp"%>
<html>
	<head>
		<title>报表统计</title>
		<meta http-equiv="Content-Type" content="text/html; charset=GB2312" />
		<link rel="stylesheet" type="text/css" href="/extjs/resources/css/ext-all.css" />
		<script type="text/javascript" src="/extjs/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="/extjs/ext-all.js"></script>
		<script type="text/javascript" src="/extjs/ext-ext.js"></script>
		<script type="text/javascript" src=" <%=request.getContextPath()%>/webframe/webframe.js" language="javascript"></script>
		<script type='text/javascript' src='/sysmgr/dwr/interface/ctx.js'></script>
		<script type='text/javascript' src='/sysmgr/dwr/engine.js'></script>
		<script type='text/javascript' src='/sysmgr/dwr/util.js'></script>
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/ttable_css.jsp" type="text/css"/>
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/sysmgr.css" type="text/css"/>
		<script type="text/javascript">
			
	   		
			Ext.onReady(function(){	
				var startTime = new Ext.form.DateField({
					id:'startTime', 
					name:'startTimeNm',
					readOnly:false,
					fieldLabel:'开始时间', 
					editable:false,
					width:90,
					format:'Y-m-d',
					value: new Date()
				});
				
				var endTime = new Ext.form.DateField({
					width:90,
					id:'endTime',  
					editable:false,
					format:'Y-m-d',
					readOnly:false,
					name:'endTimeNm',
					value: new Date(),
					fieldLabel:'结束时间'
				});
				
				
				
				
				var panel = new Ext.Panel({
					id:"pp",
					autoScroll:true,
					region:'center',
					tbar:[
						'-',"选择时间:",startTime,"~",endTime,'-',
						{id:'btnBack',text:'统&nbsp&nbsp计',icon:'../../images/search.gif',iconCls:'x-btn-text-icon',handler:statForms},
						'-',{id:'btnBack',text:'导出报表',icon:'../../images/xls.gif',iconCls:'x-btn-text-icon',handler:downLoad},'-'
				  ]
				});
			    var viewport = new Ext.Viewport({ layout:'border', items:[panel] });			   
		    });
		    
		    function statForms () {
		        var startTime = document.getElementById("startTime").value;
		        var endTime = document.getElementById("endTime").value;

		        var url = "outCallReport.jsp?flag=serch"+"&startTime=" +startTime + 
		        						"&endTime=" + endTime ;
				
		        Ext.getCmp('pp').load({url:url, waitMsg:'Loading', scripts:true});
		        
		    }
		    
		    function downLoad () {
		    	var startTime = document.getElementById("startTime").value;
		        var endTime = document.getElementById("endTime").value;
		    	var url = "outCallReport.jsp?flag=daochu"+"&startTime="+startTime + 
		        						"&endTime=" + endTime ;
		        window.location = url;
		    }
		    
		</script>
	</head>
</html>
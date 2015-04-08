<%@ page contentType="text/html; charset=GB2312" errorPage="/error.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%
/**
*功能：忽略告警页面
*/
String path = request.getParameter("path");
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=GBK">

	<link rel="stylesheet" type="text/css" href="/extjs/resources/css/ext-all.css" />
	<script type="text/javascript" src="/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" src="/extjs/ext-all.js"></script>
	<script type="text/javascript" src="/extjs/ext-ext.js"></script>
	<script type="text/javascript" src="/extjs/source/locale/ext-lang-zh_CN.js" ></script>
		<script type="text/javascript" src="/sysmgr/webframe/webframe.js"></script>
<style>
.search{
   background-image:url(../../images/search.gif) !important;
}
</style>
	</head>
	<body>
	<script language="JavaScript1.2">
	    //忽略告警定时2分钟刷新
	    window.setInterval("refershIgnore()",2*60*1000);
	    
	    function refershIgnore(){
	       if (store1){ 
	         store1.load({params:{
	           start:0
	         }});
	       }
	    }
	    var w = 800; //初始化页面宽度
 		var h = 500; //初始化页面高度
 		function init(){
			w = document.body.clientWidth; //获取页面宽度
            h = document.body.offsetHeight ; //获取页面高度
		}
	
			var selectGrade = 1; //当前选择的级别
			var alarmnr = "";//过滤的告警内容
			var store1 = null;
			var search_type = 1; //查询类型 1、基本查询 2、高级查询
			var start_time = "";
			var end_time = "";
			
			
			Ext.onReady(function(){   
               var sm=new Ext.grid.CheckboxSelectionModel();  
            var cm=new Ext.grid.ColumnModel([   
                
                {header:'ID',dataIndex:'id',width:50,hidden:true},
				{header:'级别',dataIndex:'grad',width:40,renderer:function(a){
    	  				 if (a!="")
    	                     return "<img src='<%=request.getContextPath()%>/images/icons/grade"+a+".gif'>"+a;
    	   	  			 else 
    	   	  	  			 return "";
     	 		}},
				{header:'路径',dataIndex:'path',width:250},
        		{header:'第一次时间',dataIndex:'fdate',width:120},
				{header:'最后一次时间',dataIndex:'ldate',width:120},
				{header:'次数',dataIndex:'count',width:40},
				{header:'内容',dataIndex:'content',width:350}
				
              ]);                
               
              store1 = new Ext.data.Store({ 
                 proxy:new Ext.data.HttpProxy({
                    url:"./AlarmAction.jsp",
                    timeout:1000000
                }),
                reader:new Ext.data.JsonReader({
                        root:"data",
						//转向添加路径
                        totalProperty:"count"
                    },[ //设置格式
                   		{name:'grad'}, 
                   		{name:'path'}, 
                   		{name:'id'}, 
                        {name:"fdate",type:"string"},
						{name:'ldate'},
						{name:'count'},
						{name:'content'},
						{name:'oper'}
                ])
            });
            store1.on('beforeload',function(){Ext.apply(this.baseParams,{ 
	        	mode:'ignore',
	        	limit:20,
	        	grade:selectGrade,
	        	type:search_type,
	        	content:alarmnr,
	        	sdate:start_time,
	        	edate:end_time,
	        	path:'<%=path%>'
	    		});
	    		});
           
	    store1.load({params:{
	       start:0
	    }});
            var grid1=new Ext.grid.GridPanel({   
				title:'',
				collapsible:true,
				region: 'center',
              
                stripeRows:true,//斑马线效果   
                loadMask:true,   
                store: store1,   
               
                cm:cm,
				tbar:['-','内容：<input id="alarmcontent" type="text" size=15>',
				{text:'查询',iconCls:'search',handler:function(){
					search_type=1;
				   if (alarmcontent) alarmnr = alarmcontent.value;
				   store1.load({params:{
	       				start:0
	    			}});
				}},
				' &nbsp;级别过滤：',
				'&nbsp; <img src="../../images/icons/grade1.gif" style="cursor:hand;" title="警告告警" onclick="changeGrade(1)">',
				'&nbsp; <img src="../../images/icons/grade2.gif" style="cursor:hand;" title="一般告警" onclick="changeGrade(2)">',
				'&nbsp; <img src="../../images/icons/grade3.gif" style="cursor:hand;" title="重要告警" onclick="changeGrade(3)">',
				'&nbsp; <img src="../../images/icons/grade4.gif" style="cursor:hand;" title="严重告警" onclick="changeGrade(4)">',
				'&nbsp;当前级别：<img id="currgradimg" src="../../images/icons/grade1.gif" >','-',{text:'高级查询',iconCls:'search',handler:function(){
				   AdvWin();
				   
				}}],
                bbar:new Ext.PagingToolbar({   
                    pageSize:20,   
                    store: store1,   
                    displayInfo:true,   
                    displayMsg:' 显示第{0}条到{1}条记录,一共{2}条',   
                    emptyMsg:"没有记录"   
                })                   
            });
			grid1.on("rowdblclick",function(a,b,c){
			    var data =grid1.getSelectionModel().getSelected().data;
				openModalDialog("<%=request.getContextPath()%>/alarm/alarm.do?method=getAlarmDetail&id="+data["id"]+"&mainType=0&history=2",500,400,null);
			});
			var view =new  Ext.Viewport({
				layout: 'fit',
				items:[grid1]
				
			});
        });  
        function changeGrade(gd){
          selectGrade = gd;
          currgradimg.src="../../images/icons/grade"+gd+".gif";
          search_type=1;
          store1.load({params:{
	       start:0
	      }});
        }
        
        
			//告警查询界面
			var isAdvWin = false;
		    function AdvWin(){
		    if (isAdvWin) return;
		       init();
		      var date = new Date();
		      var sdate = new Date(date.getYear(),date.getMonth(),date.getDate(),0,0,0);
		      var edate = new Date(date.getYear(),date.getMonth(),date.getDate(),23,59,59);
		      
		       var sdf = new Ext.ux.form.DateTime({
					id : "sDate",
					 width:180  ,
					
					fieldLabel : "开始时间"
					});
			   sdf.setValue(sdate);
			   
			   
			   var edf = new Ext.ux.form.DateTime({
					id : "lDate",
					fieldLabel : "截止时间",
					 width:180  ,
					value:edate
					});
			   edf.setValue(edate);
			   
			   
		       var form=new Ext.form.FormPanel({
				labelWidth:60,
				title:'',
				layout:'column',
				frame:true,
				items:[
					{
					columnWidth:.50,
					layout:'form',
					items:[{
					xtype:"textfield",   
					id:'sctnt',
                    fieldLabel: '内容',   
                    name: 'content',   
                    width:180  
					
					},sdf]
				},
				{
				columnWidth:.50,
				layout:'form',
				items:[new Ext.form.ComboBox({
				id:'sgrade',
				hiddeName:'sgrade',
				      triggerAction:"all",
				      fieldLabel:"级别",
				      store:new Ext.data.SimpleStore({ 
				          fields:['caption','value'], 
				          data:[['警告告警','1'],['一般告警','2'],['重要告警','3'],['严重告警','4']]}), 
				          displayField:'caption',
				          valueField:'value',
				          typeAhead: true, 
				          mode: 'local', 
				          value:1,
				          forceSelection:true, 
				          selectOnFocus:true
				      }),edf]
					}  
			 	] 
				
				});

  
		       
		    	
				var clwin = new Ext.Window({
					title:'忽略告警高级查询',
					width:560,
					height:160,
					layout:'fit',
					items:[form],
					buttons:[{text:'查询',handler:function(){
					   search_type=2;
					   start_time = Ext.get("sDate").getValue();
					   end_time = Ext.get("lDate").getValue();
					   selectGrade = Ext.getCmp("sgrade").getValue();
			           alarmnr = Ext.get("sctnt").getValue();
					    store1.load({params:{
	       					start:0
	    				}});
	    				isAdvWin = false;
					    clwin.close(); 
					}},{text:'取消',handler:function(){
						clwin.close();
						isAdvWin = false;
						}}]
			    });
				clwin.show();
				clwin.on('close',function(a){
					isAdvWin = false;
				});
				isAdvWin = true;
			}
        	</script>
		</body>
</html>
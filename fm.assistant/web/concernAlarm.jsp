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
String path = request.getParameter("path");
if(path==null) path ="";

SecurityManager securityManager = (SecurityManager)Context.getContext().getAttribute("securityManager");
User user = (User) Context.getContext().getAttribute(Context.USER);
CallFileManager cfm = (CallFileManager) Context.getContext().getAttribute("callFileManager");
int isEdit = 0;

if (user!=null) isEdit= securityManager.check(user.getId(), "界面功能域", "/default/concernAlarm/concernConfig", '*');
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=GBK">

		<script type='text/javascript'> var loginUserId = "<%= user.getId()%>";</script>
		<link rel="stylesheet" href="<%=request.getContextPath()+"/css/sysmgr.css"%>" type="text/css"/>
		<link rel="stylesheet" type="text/css" href="/extjs/resources/css/ext-all.css" />
		<script type="text/javascript" src="/extjs/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="/extjs/ext-all.js"></script>
		<script type="text/javascript" src="/extjs/ext-ext.js"></script>
		<script type="text/javascript" src="/extjs/source/locale/ext-lang-zh_CN.js" ></script>
		<script type="text/javascript" src="/sysmgr/webframe/webframe.js"></script>
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

		.link{
		   background-image:url(../../images/conclude.gif) !important;
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
	<BGSOUND id="bgsd" src="" loop="-1">

	<body >
	<script language="JavaScript1.2">	
		Ext.BLANK_IMAGE_URL ="/extjs/resources/images/default/s.gif";
		//重写方法，解决datefield在IE浏览器下显示不全的问题
		Ext.override(Ext.menu.Menu, { autoWidth: function () { this.width += "px"; } });
		
 		var w = 800; //初始化页面宽度
 		var h = 500; //初始化页面高度
 		 var store1 = null;
 		 var selectAlarms ;
 		function init(){
			w = document.body.clientWidth; //获取页面宽度
            h = document.body.offsetHeight ; //获取页面高度           
		}
 		var selectGrade = 0;
 		var alarmnr = "";
      	var start_time = "";
      	var end_time = "";
      	var isCfg=<%=isEdit%>; //是否有设置权限
 		//忽略告警定时2分钟刷新 
	    window.setInterval("refershConcern()",<%=AstConfig.CONCREN_TIME%>*60*1000);
	    var isrefersh = true;
	    var isrefershCall = false;
	    function refershConcern(){
	       if (isrefershCall){
	          if (callstore1) callstore1.load({
				params : {
					start : 0
				}
			  });
	          return;
	       }
		   if (!isrefersh) return;
	       if (store1){ 
	         store1.load({params:{
	           start:0
	         }});
	       }
	    }

			Ext.onReady(function(){   
				 Ext.QuickTips.init();
             
              var sm=new Ext.grid.CheckboxSelectionModel();   
              var cm=new Ext.grid.ColumnModel([   
                sm,   
                {header:'ID',dataIndex:'id',width:50,hidden:true},
                {header:'联系/外呼',dataIndex:'oper',width:80,renderer:function(a){
                    var str = a.split("|||");

                    if (str[1]=="0")
                    	return "<b><u><font onclick=\"alarmPhone('"+str[0]+"','"+str[2]+"')\">尚未联系</font></u></b>";
                    else if (str[1]=="2")
  				        return "<b><u><font color='red' onclick=\"alarmPhone('"+str[0]+"','"+str[2]+"')\">联系失败</font></u></b>";
  				    else if (str[1] == '3')
  	  				    return  "<b><font color='gray'>外呼中...</font></b>";
  				    else if (str[1]=="1")
  	  				    return "<b><font color='green'>成功联系</font></b>";
  			   }},
  			   {header:'失败次数',dataIndex:'failnum',width:80},
  			 {header:'工单',dataIndex:'workid',renderer:function(a){
  	  			 if (a!="无")
                    return "<img src='<%=request.getContextPath()%>/images/icons/alarmForward.gif'>"+a;
  	  			 else 
  	  	  			 return a;
    	  			 }},
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
				{header:'内容',dataIndex:'content',width:250}
				
				
              ]);                
               
              store1 = new Ext.data.Store({ 
                url:"./AlarmAction.jsp",
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
						{name:'failnum'},
						{name:'oper'},
						{name:'workid'}
                ])
            });
              
	        store1.on('beforeload',function(){Ext.apply(this.baseParams,{ 
	        	mode:'concern',
	        	limit:20,
	        	grade:selectGrade,
  	        	content:alarmnr,
  	        	sdate:start_time,
  	        	edate:end_time,
	        	path:'<%=path%>'
	    		});
	    		});
	        store1.load({params:{
	 	       start:0
	 	    }});
	        
            var grid1 = new Ext.grid.GridPanel({   
				title:'',
				region: 'center',
				id:'concernAlarmGrid',
                stripeRows:true,//斑马线效果   
                loadMask:true,   
                store: store1,   
                sm:sm,
                cm:cm,
				tbar:['-',{text:'查询',iconCls:'search',handler:function(){
					   AdvWin();
					}},{text:'批量通知',iconCls:'sms',handler:function(){
						var data = grid1.getSelectionModel().getSelections();
						if (data.length<=0) {
							Ext.Msg.alert("提示","请选择需要批量通知的告警？");
							return;
						}
						var ids = "";
						for (var i=0;i<data.length;i++){
							if (i>0) ids += ",";
							ids += data[i].data['id'];
							var opr = data[i].data['oper'];
							
							var oprs = opr.split("|||");
							
							if (data[i].data['workid']=='无'||oprs[1]=='1'||oprs[1]=='3'){
								Ext.Msg.alert('提示','对不起，您所选择的关注告警还没有派发工单或联系过或者正在联系的告警,无法批量联系。');
								return;
							}
						}
						batchAlarmPhone(ids);
						
						
						
					}},{text:'前转工单',iconCls:'forward',handler:function(){
					var data = grid1.getSelectionModel().getSelections();
					if (data.length<=0) {
						Ext.Msg.alert("提示","请选择需要前转的告警？");
						return;
					 }
					openModalDialog('/sysmgr/opt/ovsd/forwardOVSD.jsp?alarmId='+data[0].data["id"], 390, 450,null);
				}},{text:'取消关注',iconCls:'cannel',handler:function(){
					var data = grid1.getSelectionModel().getSelections();
					if (data.length<=0) {
						Ext.Msg.alert("提示","请选择需要取消关注的告警？");
						return;
					}
					cannelConcernWin(data);
			    }},{text:'关联告警',iconCls:'link',handler:function(){
			    	var data = grid1.getSelectionModel().getSelections();
			    	if (data.length<=0) {
						Ext.Msg.alert("提示","请选择需要关联的告警？");
						return;
					}
			    	selectAlarms = data;
					//获取第一条告警的路径
			    	var path =  data[0].data['path'];
			    	var paths = path.split("/");
			    	if (paths.length>=2){
			    		path = "/"+paths[1]+"/"+paths[2]+"/"
				    }
				    var iserr = false;
				    for (var i=0;i<data.length;i++){
						var p = data[i].data['path']+"/";
						
						if (p.indexOf(path)==-1){
							iserr = true;
							break;
						}
					}
			    	if (iserr){
			    		Ext.Msg.alert("提示","告警二级目录不相同，禁止关联工单");
			    		return;
				    }
			    	openModalDialog('/fm/alarmWorkId.jsp?page=concern',300,150);
			    	
				    }},{text:'撤销关联',iconCls:'linkback',handler:function(){
				    	var data = grid1.getSelectionModel().getSelections();
				    	if (data.length<=0) {
							Ext.Msg.alert("提示","请选择需要撤销的关联告警？");
							return;
						}
				    	selectAlarms = data;
				    	openModalDialog('/fm/alarmWorkId.jsp?isDel=true&page=concern',300,150);
				    	
				    }},{id:'stoprefersh',iconCls:'stop',text:'停止刷新',handler:function(){
				    	isrefersh = !isrefersh;
					 	var btn = Ext.getCmp("stoprefersh");
					    if (btn){
					    	if(isrefersh){
								btn.setText("停止刷新");
						    }else 	btn.setText("启动刷新");
						}
					    }},'-',{text:'自动外呼',iconCls:'sms',handler:function(){
							if ( isCfg!=1 ) { return; }
					        if (Ext.getCmp("allCallPhoneWin")) { return; }
							var win = new Ext.Window({
								title : '自动外呼列表',
								id : 'allCallPhoneWin',
								html:"<iframe style='width:100%;height:100%' src='outCall.jsp' borderframe=0></iframe>"
							});
							win.show();
							win.maximize();
					    }},{text:'<b><font id=trackText>跟踪告警</font></b>',iconCls:'track',handler:function(){
			    	trackWindow();
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
				alarmWin(data["id"],1);
			});
			grid1.on('contextmenu',function(e){
				var rightClick = new Ext.menu.Menu({
			         id: 'rightClickCont',  //在HTML文件中必须有个rightClickCont的DIV元素
			         items: [
			                   {
			                   id: "sms",
			                   text: "发送短信",
			                   handler:function(){
			                	   var data = grid1.getSelectionModel().getSelections();
							    	if (data.length<=0) {
										Ext.Msg.alert("提示","请选择需要发送短信的告警？");
										return;
									}
							    	openModalDialog('/fm/sendAlarmBySms.jsp?alarmId='+data[0].data['id'],300,150);
				               },
			                   iconCls: 'sms'
			                  },{text:'查询',iconCls:'search',handler:function(){
								   AdvWin();
								}}
			                ]
			      });
							
				var target = e.getTarget(); 
				e.stopEvent() ;
				rightClick.showAt(e.getXY());
						
				 var view = grid1.getView();
				 
				   var rowIndex = view.findRowIndex(target);
				   if(rowIndex==0||rowIndex){
				    //如果鼠标在有效的grid 范围内，则把当前grid选中
				    grid1.getSelectionModel().selectRow(rowIndex);
				   }
				   else {
				    //清空已经选中的行
				    grid1.getSelectionModel().clearSelections();
				   }

				
			});
			var view =new  Ext.Viewport({
				layout: 'fit',
				items:[grid1]
			});
			checkTrackAlarm();
        });  

	        //取消告警关注页面
	        var isShowCl = false;
	        function cannelConcernWin(data){
	        	if (isShowCl) return;
				var clwin = new Ext.Window({
					title:'填写取消关注原因',
					width:300,
					height:280,
					html:'<textarea id="clmemo" style="width:278px;height:200px" ></textarea>',
					buttons:[{text:'确定',handler:function(){
						if (clmemo.value==""){
							Ext.Msg.alert("提示","请填写取消关注的理由？");
							return;
						}
						var alarmId = "";
						for (var i=0;i<data.length;i++){
 							if (i>0) alarmId+=",";
 							alarmId+=data[i].data["id"];
						}
						 Ext.Ajax.request({
	        	                method: 'post',
	        	                params: {
	        	                	mode:'cancel',
	        	                	alarmIds:alarmId,
	        	                	memo:clmemo.value
	        	                },
	        	                success: function(resp, opts){
	        	                	clwin.close();
	        	                	Ext.Msg.alert("提示","关注告警取消成功！");
	        	                  	store1.load({params:{
	        	         	 	       start:0
	        	        	 	    }});
	        	                },
	        	                failure: function(resp, opts){
	        	                   Ext.Msg.alert("提示","关注告警取消失败?");
	        	                },
	        	                url: './AlarmAction.jsp'
	        	            });

						}},{text:'取消',handler:function(){
						clwin.close();
						isShowCl = false;
						}}]
			    });
				clwin.show();
				clwin.on('close',function(a){
					isShowCl = false;
				});
				isShowCl = true;
		    }

	      //提示告警是否已经经过电话联系了
	        function batchAlarmPhone(ids){
		        var whwin = Ext.getCmp('bapwin');
		        if (whwin) return;
		        
		        var grid = Ext.getCmp('concernAlarmGrid');
		        var data =grid.getSelectionModel().getSelected().data;
		        var store2 = new Ext.data.Store({ 
	                url:"./AlarmAction.jsp",
	                reader:new Ext.data.JsonReader({
	                        root:"data",
							//转向添加路径
	                        totalProperty:"count"
	                    },[ //设置格式
	                   		{name:'userid'}, 
	                   		{name:'name'}, 
	                   		{name:'phone'}
	                ])
	            });
			   store2.on('beforeload',function(){Ext.apply(this.baseParams,{ 
		        	mode:'userlist',
		        	searchtxt:(document.getElementById('searchtxt')?document.getElementById('searchtxt').value:''),
		        	limit:10
		    		});
		    		});
		        store2.load({params:{
		 	       start:0
		 	    }});
		        var grid = new Ext.grid.GridPanel({
		            width: 250,
		            height: 300,
		            title: '',
		            bbar:new Ext.PagingToolbar({   
	                    pageSize:10,   
	                    store: store2,   
	                    displayInfo:true,   
	                    displayMsg:'',
	                    emptyMsg:"没有记录"   
	                }),
		            tbar:['条件:<input type="text" size=15 id="searchtxt">',{text:'查询',iconCls:'search',handler:function(){
			           
		            	store2.load({params:{
		 		 	       start:0
		 		 	    }});
			            }}],
		            store: store2,
		            columns: [
		                {header:'登录ID',dataIndex:'userid',width:80},
		                {header:'用户名',dataIndex:'name',width:80},
		                {header:'手机号',dataIndex:'phone',width:120}
		            ],
		            viewConfig: {
		                forceFit: true
		            }
		        });

		        var selectMenu = new Ext.menu.Menu({
		        	minWidth:250,
		            items: [new Ext.menu.Adapter(grid)]
		        });

		        var field = new Ext.form.TriggerField({
		            fieldLabel: '手机号',
		            anchor:"100%",
		            allowBlank:false,
		            regex:/^0{0,1}(13[4-9]?|15[8-9]|15[0-1]|18[7-8])[0-9]{8}$/,
		            id:'phone',
		            onSelect: function(record){
		            },
		            onTriggerClick: function() {
		                if (this.menu == null) {
		                    this.menu = selectMenu;
		                }
		                this.menu.show(this.el, "tl-bl?");
		            }

		        });
		        grid.on('rowclick', function(grid, rowIndex, e) {
		        selectMenu.hide();
		           var selections = grid.getSelectionModel().getSelections();
		           if (selections.length == 0) { Ext.Msg.alert('提示', "先选择行"); return; }
		           for (var i = 0; i < selections.length; i++) {
		               var record = selections[i];
		               field.setValue(record.get("phone"));
		           }

		        });

				var form = new Ext.form.FormPanel({
				  width:350,
				  labelAlign : 'left',
				  defaultType : 'textfield',
				  timeout:10000,
				  layout:'form',
				  labelWidth : 60,
				  items : [	
				  field ,{
					 xtype:'textarea',
					 anchor:"100%",
					 fieldLabel: "标题",
					 value:data['path'],
					 height:80,
					
					 allowBlank:false,
					 id:'title'
	  				}, {
					  xtype:'textarea',
					  height:80,
				      fieldLabel : '内容',
				      value:data['content'],
				      allowBlank:false,
				      anchor:"100%",
				      id : 'context'
				  }]

				});
		        var win = new Ext.Window({
					title:'批量联系表单填写',
					id:'bapwin',
					width:400,
					height:300,
					layout:'fit',
					items:[form],
					buttons:[{text:'保存',handler:function(){
						if (!form.form.isValid()){
							Ext.MessageBox.alert('提示','请填写完整？');
							return;
						}else{
							
							form.form.submit({
								url:'./AlarmAction.jsp',
								waitMsg: '请稍等,正在外呼和派单...',
								success: function(form, action)
								{
								   Ext.MessageBox.alert("提示","外呼派单成功.");
								   store1.load({params:{start:0}});
								   /*
								    var obj = Ext.util.JSON.decode(form.responseText);
									if (obj.success){
										Ext.MessageBox.alert("提示","外呼派单成功."+obj.msg);
									   store1.load({params:{start:0}});
									   win.close();
									}else{
										Ext.MessageBox.alert("提示", "外呼派单失败:"+obj.msg);
										store1.load({params:{start:0}});
										win.close();
									}
									*/
								},
								failure: function(form, action)
								{
									Ext.MessageBox.alert("提示", "外呼派单失败:"+action.result.msg);
								},
								params:{
								    mode:'batchcallphone',
								    alarmIds:ids,
								    phone:Ext.getCmp('phone').getValue(),
								    personcall:'1',
								    title:Ext.getCmp('title').getValue(),
								    context:Ext.getCmp('context').getValue()
								}
							});
						}
						
					  
					}},{text:'取消',handler:function(){
						win.close();
						
					}}]
			    });
				win.show();
				
		      
	        					
		    }
		    
			//提示告警是否已经经过电话联系了
	        function alarmPhone(id,workid){
		        var whwin = Ext.getCmp('whwin');
		        if (whwin) return;
		        
		        var grid = Ext.getCmp('concernAlarmGrid');
		        var data =grid.getSelectionModel().getSelected().data;
		        var store2 = new Ext.data.Store({ 
	                url:"./AlarmAction.jsp",
	                reader:new Ext.data.JsonReader({
	                        root:"data",
							//转向添加路径
	                        totalProperty:"count"
	                    },[ //设置格式
	                   		{name:'userid'}, 
	                   		{name:'name'}, 
	                   		{name:'phone'}
	                ])
	            });
			   store2.on('beforeload',function(){Ext.apply(this.baseParams,{ 
		        	mode:'userlist',
		        	searchtxt:(document.getElementById('searchtxt')?document.getElementById('searchtxt').value:''),
		        	limit:10
		    		});
		    		});
		        store2.load({params:{
		 	       start:0
		 	    }});
		        var grid = new Ext.grid.GridPanel({
		            width: 250,
		            height: 300,
		            title: '',
		            bbar:new Ext.PagingToolbar({   
	                    pageSize:10,   
	                    store: store2,   
	                    displayInfo:true,   
	                    displayMsg:'',
	                    emptyMsg:"没有记录"   
	                }),
		            tbar:['条件:<input type="text" size=15 id="searchtxt">',{text:'查询',iconCls:'search',handler:function(){
			           
		            	store2.load({params:{
		 		 	       start:0
		 		 	    }});
			            }}],
		            store: store2,
		            columns: [
		                {header:'登录ID',dataIndex:'userid',width:80},
		                {header:'用户名',dataIndex:'name',width:80},
		                {header:'手机号',dataIndex:'phone',width:120}
		            ],
		            viewConfig: {
		                forceFit: true
		            }
		        });

		        var selectMenu = new Ext.menu.Menu({
		        	minWidth:250,
		            items: [new Ext.menu.Adapter(grid)]
		        });

		        var field = new Ext.form.TriggerField({
		            fieldLabel: '手机号',
		            anchor:"100%",
		           
		            allowBlank:false,
		            regex:/^0{0,1}(13[4-9]?|15[7-9]|15[0-2]|18[7-8]|18[2-3]|147)[0-9]{8}$/,
		            id:'phone',
		           
		            onSelect: function(record){
		            },
		            onTriggerClick: function() {
			           
		                if (this.menu == null) {
		                    this.menu = selectMenu;
		                }
		                this.menu.show(this.el, "tl-bl?");
		            }

		        });
		       
		        grid.on('rowclick', function(grid, rowIndex, e) {
		        selectMenu.hide();
		           var selections = grid.getSelectionModel().getSelections();
		           if (selections.length == 0) { Ext.Msg.alert('提示', "先选择行"); return; }
		           for (var i = 0; i < selections.length; i++) {
		               var record = selections[i];
		               field.setValue(record.get("phone"));
		           }

		        });

				var form = new Ext.form.FormPanel({
				  width:350,
				  labelAlign : 'left',
				  defaultType : 'textfield',
				  timeout:10000,
				  layout:'form',
				  labelWidth : 60,
				  items : [		{ xtype: 'checkbox',
					  fieldLabel: "已经联系",
					  value:'1',
					  //checked:true,
					  disabled:<%out.print(cfm.getLastCallCount()<=0?"true":"false");%>,
					  id:'personcall'
},					
				  field ,{
					 xtype:'textarea',
					 anchor:"100%",
					 fieldLabel: "标题<br>(外呼)",
					 height:80,
					 maxLength:60,
					 value:data['path'],
					 allowBlank:false,
					 id:'title'
	  				}, {
					  xtype:'textarea',
					  height:80,
				      fieldLabel : '内容',
				      value:'告警内容:'+data['content']+'\n'+'告警来源于:'+data['path']+'\n'+'第一次发生时间:'+data['fdate']+'\n'+'最后一次发生时间:'+data['ldate']+'\n'+'重复次数:'+data['count'],
				      allowBlank:false,
				      anchor:"100%",
				      id : 'context'
				  },{
					  xtype:'panel',height:40,autoLoad:{
						  url:'./AlarmAction.jsp',
						  scripts: true,
						  params : {mode:'responseible',alarmId:id}
						  
					  }
				  },{xtype:'panel',height:40,html:'<font color="red" size=3>您剩余外呼次数：</font><font color="red" size=4><%=cfm.getLastCallCount()%></font>'}, {
					  xtype:'panel',
					  height:100,
				      html:'<font color="red" size=3>如果已经联系了，系统只记录联系过程，外呼后会自动派单到帮助台,如果您剩余外呼次数为0时，请采用手机联系了。</font>'
				  }]

				});
		        var win = new Ext.Window({
					title:'外呼表单填写',
					id:'whwin',
					width:400,
					height:400,
					layout:'fit',
					items:[form],
					buttons:[{text:'外呼',handler:function(){
						if (!form.form.isValid()){
							Ext.MessageBox.alert('提示','请填写完整，或者标题不能超过53个汉字。');
							return;
						}else{
							
							form.form.submit({
								url:'./AlarmAction.jsp',
								waitMsg: '请稍等,正在外呼和派单...',
								success: function(form, action)
								{
									if (action.result.success){
										Ext.MessageBox.alert("提示","外呼派单成功.");
									   store1.load({params:{start:0}});
									   win.close();
									}else{
										Ext.MessageBox.alert("提示", "外呼派单失败:"+action.result.msg);
									}
								},
								failure: function(form, action)
								{
									Ext.MessageBox.alert("提示", "外呼派单失败:"+action.result.msg);
								},
								params:{
								    mode:'callphone',
								    alarmId:id,
								    phone:Ext.getCmp('phone').getValue(),
								    personcall:Ext.getCmp('personcall').getValue(),
								    title:Ext.getCmp('title').getValue(),
								    context:Ext.getCmp('context').getValue(),
								    workid:workid
								}
							});
						}
						
					  
					}},{text:'取消',handler:function(){
						win.close();
						
					}}]
			    });
				win.show();
				
		      
	        					
		    }

			
		    //定时刷新跟踪字符，闪动显示是否存在告警需要跟踪
	        window.setInterval("changeTxt()",800);
	        //检测是否有跟踪告警
	        window.setInterval("checkTrackAlarm()",5*1000);
	        function checkTrackAlarm(){
	        	ctx.get(checkTrackAlarmBack,"trackManager.isTrack()");
		    }
		    //检测结果
		    function checkTrackAlarmBack(data){
				if (data){ isChange = true;
				   bgsd.src="./ConcernAlarm.WAV";
				}else {
					isChange = false;
					bgsd.src="";
				}
			}
		    var index = 0;
		    var isChange = false;
		    //字符闪烁
		    function changeTxt(){
			    var el = document.getElementById("trackText");
			    if (isChange){
				    if (index==0){
				    	if(el) el.setAttribute("color","red");
				    	index = 1;
					}else{
						if(el) el.setAttribute("color","green");
						index = 0;
					}
			    	
				}else{ 
					if(el) el.setAttribute("color","#000000");
				}
		    	
			}
			var trackStore = null;
			var trackwin = null;
			var isTrackWin =false;
			//显示跟踪告警列表
			function trackWindow(){
				if (isTrackWin) return;
				isTrackWin = true;
				init();


				  var sm=new Ext.grid.CheckboxSelectionModel();   
		            var cm=new Ext.grid.ColumnModel([   
		                sm,   
		                {header:'ID',dataIndex:'id',width:50,hidden:true},
						{header:'级别',dataIndex:'grad',width:40},
						{header:'路径',dataIndex:'path',width:250},
		        		{header:'第一次时间',dataIndex:'fdate',width:120},
						{header:'最后一次时间',dataIndex:'ldate',width:120},
						{header:'次数',dataIndex:'count',width:40},
						{header:'内容',dataIndex:'content',width:250},
						{header:'工单',dataIndex:'workid'}
						
		            ]);                
		               
		              var store1 = new Ext.data.Store({ 
		                url:"./AlarmAction.jsp?mode=tracklist",
		                reader:new Ext.data.JsonReader({
		                        root:"data",
		                        totalProperty:"count"
		                    },[
		                   		{name:'grad'}, 
		                   		{name:'path'}, 
		                   		{name:'id'}, 
		                        {name:"fdate"},
								{name:'ldate'},
								{name:'count'},
								{name:'content'},
								{name:'oper'},
								{name:'workid'}
		                ])
		            });
			    store1.load({params:{start:0,limit:20}});
			    trackStore = store1;
			    var grid1=new Ext.grid.GridPanel({   
					title:'',
					sm:sm,
					region: 'center',
	                stripeRows:true,//斑马线效果   
	                loadMask:true,   
	                store: store1,   
	                cm:cm,
					tbar:["-",{text:'延迟跟踪',handler:function(){
						var data = grid1.getSelectionModel().getSelections();
						if (data.length<=0) {
							Ext.Msg.alert("提示","请选择需要延迟跟踪的告警");
							return;
						}
						
							var ids = "";
						  	for (var i=0;i<data.length;i++){
							   if (i>0) ids += ',';
							   ids += data[i].data['id'];
					 	  	}
					 	  	
							nextTarckWin(ids);
						}},"-",{text:'批量跟踪',handler:function(){
							  var data = grid1.getSelectionModel().getSelections();
							  if (data.length<=0) {
								Ext.Msg.alert("提示","请选择需要跟踪的告警");
								return;
							  }
							  var ids = "";
							  for (var i=0;i<data.length;i++){
								  if (i>0) ids += ',';
								  ids += data[i].data['id'];
						 	  }
						 	  
							  //批量跟踪处理页面
							  selectesTrak(ids);
							}

						},"-", {text:'取消跟踪',handler:function(){
							var data = grid1.getSelectionModel().getSelections();
							if (data.length<=0) {
								Ext.Msg.alert("提示","请选择取消跟踪的告警");
								return;
							}
							var ids = "";
							for (var i=0;i<data.length;i++){
								if (i>0) ids += ',';
								ids += data[i].data['id'];
						 	}
							Ext.Msg.confirm('提示', '您是否确认取消跟踪?', function(btn, text){
								if (btn == 'yes'){
					        		Ext.Ajax.request({
										method: 'post',
										params: {
											mode:'cancelTrack',
											id:ids
										},
										success: function(resp, opts){
										    Ext.Msg.alert("提示","取消跟踪成功");
										    if (trackStore) trackStore.load({params:{start:0,limit:20}});
										},
										failure: function(resp, opts){
										   Ext.Msg.alert("提示","取消跟踪失败");
										},
										url: './AlarmAction.jsp'
									});
					        	}
					        });
							  //批量跟踪处理页面
							  // selectesTrak(ids);
							}

						},"-"
					],
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
					alarmWin(data["id"],2);
				});
				
				trackwin = new Ext.Window({
					title:'跟踪告警列表',
					width:w-50,
					height:h-50,
					layout:'fit',
					items:[grid1]
					
			    });
				trackwin.show();
				trackwin.on("close",function(){
					isTrackWin = false;
					})
				

		    }

		    function selectesTrak(ids){
			    var w = Ext.getCmp("pltrackwin");
			    if (w) return;
		    	var win = new Ext.Window({
					title:'填写跟踪记录',
					width:300,
					id:'pltrackwin',
					height:280,
					html:'<textarea id="trackcontent" style="width:278px;height:200px" ></textarea>',
					buttons:[{text:'确定',handler:function(){
					
						if (trackcontent.value==""){
							Ext.Msg.alert("提示","请填写跟踪记录");
							return;
						}
						Ext.Ajax.request({
        	                method: 'post',
        	                params: {
        	                	mode:'tracknext',
        	                	memo:trackcontent.value,
        	                	type:'1',
        	                	id:ids
        	                },
        	                success: function(resp, opts){
        	                  Ext.Msg.alert("提示","延长跟踪成功！,请等待<%=AstConfig.TRACK_TIME%>小时再跟踪");
        	                  win.close();
        	                  if (trackStore) trackStore.load({params:{start:0,limit:20}});
        	                },
        	                failure: function(resp, opts){
        	                   Ext.Msg.alert("提示","填写跟踪记录失败");
        	                },
        	                url: './AlarmAction.jsp'
        	            });

						}},{text:'取消',handler:function(){
						win.close();

						}}]
			    });
				win.show();
				
			}
		    /**
		    *配置跟踪参数
		    */
			var isconfigTrack = false; //配置页面是否已经显示
		    function showConfig(){
		    	if (isconfigTrack) return;
				var configtrackwin = new Ext.Window({
					title:'参数配置',
					width:400,
					height:260,
					items:[
							{
				id:'simple',
				xtype:"form",
				title:"",
				labelWidth:145,
				labelAlign:"left",
				layout:"form",
				items:[
					new Ext.form.ComboBox({
						anchor:"100%",
						hiddenName:'CONCRENGRADE',
					    triggerAction:"all",
					    fieldLabel:"关注级别(当前及以上)",
					    value:'<%=AstConfig.CONCREN_GRADE%>',
					    store:new Ext.data.SimpleStore({ 
					        fields:['caption','value'], 
					        data:[['警告告警','1'],['一般告警','2'],['重要告警','3'],['严重告警','4']]}), 
					        displayField:'caption',
					        valueField:'value',
					        typeAhead: true, 
					        mode: 'local', 
					        forceSelection:true, 
					        selectOnFocus:true
					}),{
						xtype:"numberfield",
						name:'CONCRENTIME',
						fieldLabel:"关注间隔时长(单位:分钟)",
						value:<%=AstConfig.CONCREN_TIME%>,
						anchor:"100%"
					},
					new Ext.form.ComboBox({
						anchor:"100%",
					    hiddenName:'TRACKGRADE',
				        triggerAction:"all",
				        value:<%=AstConfig.TRACK_GRADE%>,
				        fieldLabel:"跟踪级别(当前及以上)",
				        store:new Ext.data.SimpleStore({ 
				          fields:['caption','value'], 
				          data:[['警告告警','1'],['一般告警','2'],['重要告警','3'],['严重告警','4']]}), 
				          displayField:'caption',
				          valueField:'value',
				          typeAhead: true, 
				          mode: 'local', 
				          forceSelection:true, 
				          selectOnFocus:true
				    }),{
						xtype:"numberfield",
						name:'TRACKTIME',
						 value:<%=AstConfig.TRACK_TIME%>,
						fieldLabel:"跟踪时长(单位:小时)",
						anchor:"100%"
					},
					{
						xtype:"numberfield",
						name:'TRACKNEXTTIME',
						 value:<%=AstConfig.TRACK_NEXT_TIME%>,
						fieldLabel:"延长时长(单位:小时)",
						anchor:"100%"
					},{
						anchor:"100%",
						xtype:"textfield",
	                    fieldLabel: '过滤关注节点（;分隔）',
	                    value:'<%=AstConfig.PATH_FILTER == null ? "" : AstConfig.PATH_FILTER%>',
	                    name: 'PATHFILTER'
					},{
						anchor:"100%",
						xtype:"textfield",
						id:'trackFilterPath',name:'trackFilterPath',value:'<%=AstConfig.TRACK_PATH_FILTER_PATH%>',						
	                    fieldLabel: '过滤跟踪节点（;分隔）',	                   
	                    readOnly:true,
						listeners:{ 
					        focus:function(selectText, delay) 
						    {	
								getTrackPathFilter(Ext.getCmp("TRACKPATHFILTER").getValue());
						    }
		                }
					},{xtype:"hidden",id:'TRACKPATHFILTER',
						name:'TRACKPATHFILTER', value:'<%=AstConfig.TRACK_PATH_FILTER_ID%>',hidden:true}
				]
			}],
					buttons:[{text:'更新',handler:function(){
						Ext.Msg.confirm('提示', '您是否确认更新当前跟踪配置。', function(btn, text){
			        	    if (btn == 'yes'){
								Ext.getCmp("simple").getForm().submit({   
									waitTitle : '提示',//标题   
									waitMsg : '正在提交数据请稍后...',//提示信息   
									url : './AlarmAction.jsp',   
									 method : 'post',   
									 params  : {mode:'config'},   
									 success : function(form, action) {   
										Ext.Msg.alert("提示","参数设置完成,系统马上重新加载参数！");
										window.location.reload();
									 },   
									 failure : function(form,action) {   
										 Ext.Msg.alert("提示","参数设置完成！");
									 }
							   });
			        	    }
			        	});
			        			
						}},{text:'取消',handler:function(){
						configtrackwin.close();
						isconfigTrack = false;
						}}]
			    });
				configtrackwin.show();
				configtrackwin.on('close',function(a){
					isconfigTrack = false;
				});
				isconfigTrack = true;
			}
			//保存跟踪信息
			function saveTrack(id){
				if (trackctnt.value==""){
					Ext.Msg.alert("提示","请填写跟踪记录");
					return;
				}
				Ext.Ajax.request({
	                method: 'post',
	                params: {
	                	mode:'tracknext',
	                	type:'1',
	                	memo:trackctnt.value,
	                	id:id
	                },
	                success: function(resp, opts){
	                  Ext.Msg.alert("提示","延长跟踪成功！,请等待<%=AstConfig.TRACK_TIME%>小时再跟踪");
	                  alarminfowin.close();
	                  if (trackStore) trackStore.load({params:{start:0,limit:20}});
	                },
	                failure: function(resp, opts){
	                   Ext.Msg.alert("提示","延长跟踪失败");
	                },
	                url: './AlarmAction.jsp'
	            });
				
			}
			//显示延迟跟踪的页面
			var isNextTrack = false;
		    function nextTarckWin(id){
	        	if (isNextTrack) return;
				var clwin = new Ext.Window({
					title:'填写延长跟踪的原因',
					width:300,
					height:280,
					html:'<textarea id="trackcontent" style="width:278px;height:200px" ></textarea>',
					buttons:[{text:'确定',handler:function(){
					
						if (trackcontent.value==""){
							Ext.Msg.alert("提示","请填写延迟理由");
							return;
						}
						Ext.Ajax.request({
        	                method: 'post',
        	                params: {
        	                	mode:'tracknext',
        	                	memo:trackcontent.value,
        	                	type:'2',
        	                	id:id
        	                },
        	                success: function(resp, opts){
        	                  Ext.Msg.alert("提示","延长跟踪成功！,请等待<%=AstConfig.TRACK_NEXT_TIME%>小时再跟踪");
        	                  clwin.close();
        	                  if (trackStore) trackStore.load({params:{start:0,limit:20}});
        	                },
        	                failure: function(resp, opts){
        	                   Ext.Msg.alert("提示","延长跟踪失败");
        	                },
        	                url: './AlarmAction.jsp'
        	            });

						}},{text:'取消',handler:function(){
						clwin.close();
						isNextTrack = false;
						}}]
			    });
				clwin.show();
				clwin.on('close',function(a){
					isNextTrack = false;
				});
				isNextTrack = true;
				
		    }

		    //告警信息展示页面
		    var isAlarmInfo = false;
		    var alarminfowin = null;
		    function alarmWin(id,type){
			  // alert(type);
		    	if (isAlarmInfo) return;
				var clwin = new Ext.Window({
					title:'告警信息',
					width:700,
					modal:true,
					height:480,
					items:[{
						xtype:"tabpanel",
						activeTab:(type==2?2:0),
						items:[
							{
								xtype:"panel",
								title:"基本",
								height:h-180,
								autoLoad:{
									url:'./AlarmInfo.jsp?mode=base&alarmId='+id
								    ,scripts:true
								}
							},{
								xtype:"panel",
								title:"关注",
								height:h-180,
							
								autoLoad:{
									url:'./AlarmInfo.jsp?mode=concernhis&alarmId='+id
								    ,scripts:true
								}
								},
							{
								xtype:"panel",
								title:"跟踪",
								height:h-180,
								autoLoad:{
									url:'./AlarmInfo.jsp?istrack='+type+'&mode=track&alarmId='+id
								    ,scripts:true
								}
							},
							{
								xtype:"panel",
								title:"联系",
								height:h-180,
							    autoLoad:{
									url:'./AlarmInfo.jsp?mode=phone&alarmId='+id
									,scripts:true
								}
							},
							{
								xtype:"panel",
								title:"关联知识",
								height:h-120,
							    autoLoad:{
									url:'./AlarmInfo.jsp?mode=kbm&alarmId='+id
									,scripts:true
								}
							}
						]
					}],
					buttons:[{text:'查看案例',handler:function(){ 
							openModalDialog("http://bomc.js.cmcc/kbm2/infoView/-1?taskType=sysmgr&taskOid=" +id +"#",600,500); }
						}, {text:'取消',handler:function(){
						clwin.close();
						isAlarmInfo = false;
						}}]
			    });
				clwin.show();
				alarminfowin = clwin;
				clwin.on('close',function(a){
					isAlarmInfo = false;
				});
				isAlarmInfo = true;
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
					title:'查询',
					width:560,
					height:160,
					layout:'fit',
					items:[form],
					buttons:[{text:'查询',handler:function(){
						
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

			// wangle add 
			// 2012-8-16
			
			//已经过滤的路径
	        var selectedPath = '';
	        // 选中需要过滤的路径ID
	        var selectedDotID = '';
	        // 选中需要删除的路径ID
	        var delDotID = '';
	        // 临时存放过滤路径全称的列表(;分隔)
	        var showPath = '';
			
			var moInfo = new ActiveXObject("Scripting.Dictionary");
	        
			function setMoInfo() {
				selectedDotID = '';
				selectedPath = '';
				delDotID = '';
				showPath = '';
				var moIds =moInfo.Keys().toArray();
				for ( i = 0; i < moIds.length; i++ ) {
					showPath += moInfo.Item(moIds[i]) + ";";
					selectedDotID += moIds[i] + ";";
				}
				moInfo = new ActiveXObject("Scripting.Dictionary");
			}
			
			// 取得过滤跟踪PATH的方法
		    function getTrackPathFilter(selectedPaths)
		    { 
			    var url = "../motree_new.jsp?flag=TrackPath";
			    // 右侧表单，用来展示已选的过滤路径
		        var eastForm=new Ext.form.FormPanel({
	            	labelWidth:60,
	                	title:'',
	                  	id:'eastForm',
	                    layout:'column',
		                height : 335,
		                width: 250,
		                autoScroll : true,
		                frame:true,
		                items:[{ xtype:'textfield', hidden:true }]
	            });

			    // 右侧面板，包括eastForm和两个按钮
			    var eastPanel = new Ext.Panel({
			    	width: 250,
			    	egion:'east',
			    	layout:'fit',
			    	style : "border 0px none;scrollbar:true",
			    	items:[eastForm],
			    	buttons:[{
				    	xtype:'button',text :'确定',
					    listeners:{ 
							click:function(selectText, delay) {
								setMoInfo();
								Ext.getCmp('TRACKPATHFILTER').setValue(selectedDotID);
								Ext.getCmp('trackFilterPath').setValue(showPath);
								selectTrackFilterWin.close();
							}}},{
								xtype:'button',text :'取消',
								listeners:{ 
								click:function(selectText, delay) 
							{
								selectTrackFilterWin.close();
							}}}
					]
			    });

			    // 中部面板，包括左选和右选按钮
			    var centerPanel = new Ext.Panel({
	    	        region:'center',
	    	        style : "border 0px none;scrollbar:true",
	    	        layout:'fit',
	    	        id:'centerPanel',
	    	        width: 100,
	    	        height: 400,
	    	        items:[{
	    	            xtype:'form',
	    	            id:'centerForm',
	    	            height:'100%',
	    	            width:'100%',
	    	            items:[{xtype:'button',iconCls :'right',style:'marginLeft:25px;marginTop:150px',
	    	                listeners:{ 
	    	                click:function(selectText, delay) {
	    	                    if (null == selectedDotID) {
	    	                         return;
	    	                    }
	    	                    addSelectPath();
						
		    	            }}},
				            {xtype:'button',iconCls :'left',style:'marginLeft:25px',
						        listeners:{ 
				    	        click:function(selectText, delay) 
					            {
					                if (null == delDotID || "" == delDotID.trim()) {
						                return;
						            }
									moInfo.Remove(delDotID);
			    	                Ext.getCmp('eastForm').remove(delDotID);
			    	                Ext.getCmp('eastForm').doLayout();
			    	                delDotID = "";
					            }
					        }},
					        {xtype:'textarea',
						     hideLabel : true,
						     width: '90%',
						     height: 120,
						     readOnly:true,
						     value:'在对象树中，部分对象为类型对象，所以不可选。例：/10.主机/SUN主机',
						     style:'marginTop:5px;overflow-y:hidden'
					        }]
		           }]
	            });

			    var selectTrackFilterWin = new Ext.Window({
				    title : '选择过滤对象',
				    width : 600,
				    height : 400,
				    isTopContainer : true,
				    modal : true,
				    resizable : false,
				    layout:'border',
				    items: [{
					    region: 'east',
					    width: 250,
					    items:[eastPanel]},
					    centerPanel,
					    {region:'west',
					     width: 250,
					     contentEl : Ext.DomHelper.append(document.body, {
						 tag : 'iframe',
						 style : "border 0px none;scrollbar:true",
						 src : url,
						 height : "100%",
						 width : "100%"})
		             }]
			    });
				
				// 面板初始化
			    var ids = Ext.getCmp('TRACKPATHFILTER').getValue().split(";"); //字符分割   
			    var paths = Ext.getCmp('trackFilterPath').getValue().split(";");

			    for (i=0;i<ids.length ;i++ )
			    {
				    if (null != ids[i] && "" != ids[i])
				    { 
                        Ext.getCmp('eastForm').add({
                            xtype:'textfield',
                            id:ids[i],
                            value:paths[i],
                            width:'100%',
                            readOnly:true,
                            fieldClass : "myLabel",
                            listeners:{ 
                                render: function(p) {
                                p.getEl().on('click', function(p,e,x){
                                //处理点击事件代码
			                    e.select();
			                    delDotID = e.id;
			                });
			             }
	    	            }});						
						moInfo.Add(ids[i], paths[i]);
				    }
			    } 

			    selectedPath = "";
			    selectedDotID = "";
			    delDotID = "";
			    selectTrackFilterWin.show(); 

			    /*
			     * 把选择的对象路径加入到过滤列表中，并在eastFrom中显示
			     */
			    function addSelectPath()
			    {
					if (null == selectedDotID && "" == selectedDotID.trim()) {
						return;
					}

					if ( moInfo.Exists(selectedDotID) ) {
						Ext.Msg.alert('提示','你已经选择了该对象。');
						return;
					}
	                Ext.getCmp('eastForm').add({
	                    xtype:'textfield',
	                    id:selectedDotID,
	                    value:selectedPath,
	                    width:'100%',
	                    readOnly:true,
	                    fieldClass : "myLabel",
	                    listeners:{
	                    render: function(p) {
	                        p.getEl().on('click', function(p,e,x){
	                        //处理点击事件代码
			                e.select();
			                delDotID = e.id;
			            });
			         }
	    	        }});		           
		            Ext.getCmp('eastForm').doLayout();

					moInfo.Add( selectedDotID,selectedPath );
				}
			}

		    function trackPath(dotid,caption,path)
		    {
			    selectedDotID = dotid
				selectedPath = path;
            }

		    /*
			 * 选中一个路径添加到过滤路径列表之前
			 * 判断过滤路径列表中是否已存在该路径或该路径的父节点
			 */
		    function containedPath(paths,path)
		    {
			    var pathArray = new Array();
			    pathArray = paths.split(";");
			    for (i=0;i<pathArray.length ;i++ )   
			    {
				    if (null != pathArray[i] && "" != pathArray[i]
				            && path.startWith(pathArray[i]))
				    { 
                        return true;
				    }
			    } 
	            return false;
	 		}

		    /*
		     * 选中一个路径添加到过滤路径列表之前
		     * 取出在过滤路径列表中已存在的该路径的所有子节点
		     */
	 		function getSelectedChildNodes(paths,path)
	 		{
			    var pathArray = new Array();
			    var selectedChildNodes = new Array();
			    pathArray = paths.split(";");
			    for (i=0;i<pathArray.length ;i++ )
			    {
				    if (null != pathArray[i] && "" != pathArray[i]
				            && pathArray[i].startWith(path))
				    { 
				    	selectedChildNodes.push(pathArray[i]);
				    }
			    } 
	            return selectedChildNodes;
		    }

		    /*
		     *  String类型扩展startWith方法
		     */
		    String.prototype.startWith=function(str)
		    {
		    	if(str==null||str==""||this.length==0||str.length>this.length)
		    	return false;
		    	if(this.substr(0,str.length)==str)
		    	return true;
		    	else
		    	return false;
		    	return true;
		    }
        	</script>
		</body>
</html>
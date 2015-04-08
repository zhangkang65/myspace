<%@ page contentType="text/html; charset=GB2312" errorPage="/error.jsp"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.*" %>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.concern.*" %>
<%@ page import="com.linkage.toptea.auc.SecurityManager"%>
<%@ page import="com.linkage.toptea.context.Context"%>
<%@ page import="com.linkage.toptea.auc.*"%>
<%
/**
*���ܣ���ע���ٸ澯ҳ��
*/
String path = request.getParameter("path");
if(path==null) path ="";

SecurityManager securityManager = (SecurityManager)Context.getContext().getAttribute("securityManager");
User user = (User) Context.getContext().getAttribute(Context.USER);
CallFileManager cfm = (CallFileManager) Context.getContext().getAttribute("callFileManager");
int isEdit = 0;

if (user!=null) isEdit= securityManager.check(user.getId(), "���湦����", "/default/concernAlarm/concernConfig", '*');
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
		//��д���������datefield��IE���������ʾ��ȫ������
		Ext.override(Ext.menu.Menu, { autoWidth: function () { this.width += "px"; } });
		
 		var w = 800; //��ʼ��ҳ����
 		var h = 500; //��ʼ��ҳ��߶�
 		 var store1 = null;
 		 var selectAlarms ;
 		function init(){
			w = document.body.clientWidth; //��ȡҳ����
            h = document.body.offsetHeight ; //��ȡҳ��߶�           
		}
 		var selectGrade = 0;
 		var alarmnr = "";
      	var start_time = "";
      	var end_time = "";
      	var isCfg=<%=isEdit%>; //�Ƿ�������Ȩ��
 		//���Ը澯��ʱ2����ˢ�� 
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
                {header:'��ϵ/���',dataIndex:'oper',width:80,renderer:function(a){
                    var str = a.split("|||");

                    if (str[1]=="0")
                    	return "<b><u><font onclick=\"alarmPhone('"+str[0]+"','"+str[2]+"')\">��δ��ϵ</font></u></b>";
                    else if (str[1]=="2")
  				        return "<b><u><font color='red' onclick=\"alarmPhone('"+str[0]+"','"+str[2]+"')\">��ϵʧ��</font></u></b>";
  				    else if (str[1] == '3')
  	  				    return  "<b><font color='gray'>�����...</font></b>";
  				    else if (str[1]=="1")
  	  				    return "<b><font color='green'>�ɹ���ϵ</font></b>";
  			   }},
  			   {header:'ʧ�ܴ���',dataIndex:'failnum',width:80},
  			 {header:'����',dataIndex:'workid',renderer:function(a){
  	  			 if (a!="��")
                    return "<img src='<%=request.getContextPath()%>/images/icons/alarmForward.gif'>"+a;
  	  			 else 
  	  	  			 return a;
    	  			 }},
				{header:'����',dataIndex:'grad',width:40,renderer:function(a){
    	  				 if (a!="")
    	                     return "<img src='<%=request.getContextPath()%>/images/icons/grade"+a+".gif'>"+a;
    	   	  			 else 
    	   	  	  			 return "";
     	 		}},
				{header:'·��',dataIndex:'path',width:250},
        		{header:'��һ��ʱ��',dataIndex:'fdate',width:120},
				{header:'���һ��ʱ��',dataIndex:'ldate',width:120},
				{header:'����',dataIndex:'count',width:40},
				{header:'����',dataIndex:'content',width:250}
				
				
              ]);                
               
              store1 = new Ext.data.Store({ 
                url:"./AlarmAction.jsp",
                reader:new Ext.data.JsonReader({
                        root:"data",
						//ת�����·��
                        totalProperty:"count"
                    },[ //���ø�ʽ
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
                stripeRows:true,//������Ч��   
                loadMask:true,   
                store: store1,   
                sm:sm,
                cm:cm,
				tbar:['-',{text:'��ѯ',iconCls:'search',handler:function(){
					   AdvWin();
					}},{text:'����֪ͨ',iconCls:'sms',handler:function(){
						var data = grid1.getSelectionModel().getSelections();
						if (data.length<=0) {
							Ext.Msg.alert("��ʾ","��ѡ����Ҫ����֪ͨ�ĸ澯��");
							return;
						}
						var ids = "";
						for (var i=0;i<data.length;i++){
							if (i>0) ids += ",";
							ids += data[i].data['id'];
							var opr = data[i].data['oper'];
							
							var oprs = opr.split("|||");
							
							if (data[i].data['workid']=='��'||oprs[1]=='1'||oprs[1]=='3'){
								Ext.Msg.alert('��ʾ','�Բ�������ѡ��Ĺ�ע�澯��û���ɷ���������ϵ������������ϵ�ĸ澯,�޷�������ϵ��');
								return;
							}
						}
						batchAlarmPhone(ids);
						
						
						
					}},{text:'ǰת����',iconCls:'forward',handler:function(){
					var data = grid1.getSelectionModel().getSelections();
					if (data.length<=0) {
						Ext.Msg.alert("��ʾ","��ѡ����Ҫǰת�ĸ澯��");
						return;
					 }
					openModalDialog('/sysmgr/opt/ovsd/forwardOVSD.jsp?alarmId='+data[0].data["id"], 390, 450,null);
				}},{text:'ȡ����ע',iconCls:'cannel',handler:function(){
					var data = grid1.getSelectionModel().getSelections();
					if (data.length<=0) {
						Ext.Msg.alert("��ʾ","��ѡ����Ҫȡ����ע�ĸ澯��");
						return;
					}
					cannelConcernWin(data);
			    }},{text:'�����澯',iconCls:'link',handler:function(){
			    	var data = grid1.getSelectionModel().getSelections();
			    	if (data.length<=0) {
						Ext.Msg.alert("��ʾ","��ѡ����Ҫ�����ĸ澯��");
						return;
					}
			    	selectAlarms = data;
					//��ȡ��һ���澯��·��
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
			    		Ext.Msg.alert("��ʾ","�澯����Ŀ¼����ͬ����ֹ��������");
			    		return;
				    }
			    	openModalDialog('/fm/alarmWorkId.jsp?page=concern',300,150);
			    	
				    }},{text:'��������',iconCls:'linkback',handler:function(){
				    	var data = grid1.getSelectionModel().getSelections();
				    	if (data.length<=0) {
							Ext.Msg.alert("��ʾ","��ѡ����Ҫ�����Ĺ����澯��");
							return;
						}
				    	selectAlarms = data;
				    	openModalDialog('/fm/alarmWorkId.jsp?isDel=true&page=concern',300,150);
				    	
				    }},{id:'stoprefersh',iconCls:'stop',text:'ֹͣˢ��',handler:function(){
				    	isrefersh = !isrefersh;
					 	var btn = Ext.getCmp("stoprefersh");
					    if (btn){
					    	if(isrefersh){
								btn.setText("ֹͣˢ��");
						    }else 	btn.setText("����ˢ��");
						}
					    }},'-',{text:'�Զ����',iconCls:'sms',handler:function(){
							if ( isCfg!=1 ) { return; }
					        if (Ext.getCmp("allCallPhoneWin")) { return; }
							var win = new Ext.Window({
								title : '�Զ�����б�',
								id : 'allCallPhoneWin',
								html:"<iframe style='width:100%;height:100%' src='outCall.jsp' borderframe=0></iframe>"
							});
							win.show();
							win.maximize();
					    }},{text:'<b><font id=trackText>���ٸ澯</font></b>',iconCls:'track',handler:function(){
			    	trackWindow();
				}}],
                bbar:new Ext.PagingToolbar({
                    pageSize:20,   
                    store: store1,   
                    displayInfo:true,   
                    displayMsg:' ��ʾ��{0}����{1}����¼,һ��{2}��',   
                    emptyMsg:"û�м�¼"   
                })                   
            });
			grid1.on("rowdblclick",function(a,b,c){
				var data =grid1.getSelectionModel().getSelected().data;
				alarmWin(data["id"],1);
			});
			grid1.on('contextmenu',function(e){
				var rightClick = new Ext.menu.Menu({
			         id: 'rightClickCont',  //��HTML�ļ��б����и�rightClickCont��DIVԪ��
			         items: [
			                   {
			                   id: "sms",
			                   text: "���Ͷ���",
			                   handler:function(){
			                	   var data = grid1.getSelectionModel().getSelections();
							    	if (data.length<=0) {
										Ext.Msg.alert("��ʾ","��ѡ����Ҫ���Ͷ��ŵĸ澯��");
										return;
									}
							    	openModalDialog('/fm/sendAlarmBySms.jsp?alarmId='+data[0].data['id'],300,150);
				               },
			                   iconCls: 'sms'
			                  },{text:'��ѯ',iconCls:'search',handler:function(){
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
				    //����������Ч��grid ��Χ�ڣ���ѵ�ǰgridѡ��
				    grid1.getSelectionModel().selectRow(rowIndex);
				   }
				   else {
				    //����Ѿ�ѡ�е���
				    grid1.getSelectionModel().clearSelections();
				   }

				
			});
			var view =new  Ext.Viewport({
				layout: 'fit',
				items:[grid1]
			});
			checkTrackAlarm();
        });  

	        //ȡ���澯��עҳ��
	        var isShowCl = false;
	        function cannelConcernWin(data){
	        	if (isShowCl) return;
				var clwin = new Ext.Window({
					title:'��дȡ����עԭ��',
					width:300,
					height:280,
					html:'<textarea id="clmemo" style="width:278px;height:200px" ></textarea>',
					buttons:[{text:'ȷ��',handler:function(){
						if (clmemo.value==""){
							Ext.Msg.alert("��ʾ","����дȡ����ע�����ɣ�");
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
	        	                	Ext.Msg.alert("��ʾ","��ע�澯ȡ���ɹ���");
	        	                  	store1.load({params:{
	        	         	 	       start:0
	        	        	 	    }});
	        	                },
	        	                failure: function(resp, opts){
	        	                   Ext.Msg.alert("��ʾ","��ע�澯ȡ��ʧ��?");
	        	                },
	        	                url: './AlarmAction.jsp'
	        	            });

						}},{text:'ȡ��',handler:function(){
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

	      //��ʾ�澯�Ƿ��Ѿ������绰��ϵ��
	        function batchAlarmPhone(ids){
		        var whwin = Ext.getCmp('bapwin');
		        if (whwin) return;
		        
		        var grid = Ext.getCmp('concernAlarmGrid');
		        var data =grid.getSelectionModel().getSelected().data;
		        var store2 = new Ext.data.Store({ 
	                url:"./AlarmAction.jsp",
	                reader:new Ext.data.JsonReader({
	                        root:"data",
							//ת�����·��
	                        totalProperty:"count"
	                    },[ //���ø�ʽ
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
	                    emptyMsg:"û�м�¼"   
	                }),
		            tbar:['����:<input type="text" size=15 id="searchtxt">',{text:'��ѯ',iconCls:'search',handler:function(){
			           
		            	store2.load({params:{
		 		 	       start:0
		 		 	    }});
			            }}],
		            store: store2,
		            columns: [
		                {header:'��¼ID',dataIndex:'userid',width:80},
		                {header:'�û���',dataIndex:'name',width:80},
		                {header:'�ֻ���',dataIndex:'phone',width:120}
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
		            fieldLabel: '�ֻ���',
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
		           if (selections.length == 0) { Ext.Msg.alert('��ʾ', "��ѡ����"); return; }
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
					 fieldLabel: "����",
					 value:data['path'],
					 height:80,
					
					 allowBlank:false,
					 id:'title'
	  				}, {
					  xtype:'textarea',
					  height:80,
				      fieldLabel : '����',
				      value:data['content'],
				      allowBlank:false,
				      anchor:"100%",
				      id : 'context'
				  }]

				});
		        var win = new Ext.Window({
					title:'������ϵ����д',
					id:'bapwin',
					width:400,
					height:300,
					layout:'fit',
					items:[form],
					buttons:[{text:'����',handler:function(){
						if (!form.form.isValid()){
							Ext.MessageBox.alert('��ʾ','����д������');
							return;
						}else{
							
							form.form.submit({
								url:'./AlarmAction.jsp',
								waitMsg: '���Ե�,����������ɵ�...',
								success: function(form, action)
								{
								   Ext.MessageBox.alert("��ʾ","����ɵ��ɹ�.");
								   store1.load({params:{start:0}});
								   /*
								    var obj = Ext.util.JSON.decode(form.responseText);
									if (obj.success){
										Ext.MessageBox.alert("��ʾ","����ɵ��ɹ�."+obj.msg);
									   store1.load({params:{start:0}});
									   win.close();
									}else{
										Ext.MessageBox.alert("��ʾ", "����ɵ�ʧ��:"+obj.msg);
										store1.load({params:{start:0}});
										win.close();
									}
									*/
								},
								failure: function(form, action)
								{
									Ext.MessageBox.alert("��ʾ", "����ɵ�ʧ��:"+action.result.msg);
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
						
					  
					}},{text:'ȡ��',handler:function(){
						win.close();
						
					}}]
			    });
				win.show();
				
		      
	        					
		    }
		    
			//��ʾ�澯�Ƿ��Ѿ������绰��ϵ��
	        function alarmPhone(id,workid){
		        var whwin = Ext.getCmp('whwin');
		        if (whwin) return;
		        
		        var grid = Ext.getCmp('concernAlarmGrid');
		        var data =grid.getSelectionModel().getSelected().data;
		        var store2 = new Ext.data.Store({ 
	                url:"./AlarmAction.jsp",
	                reader:new Ext.data.JsonReader({
	                        root:"data",
							//ת�����·��
	                        totalProperty:"count"
	                    },[ //���ø�ʽ
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
	                    emptyMsg:"û�м�¼"   
	                }),
		            tbar:['����:<input type="text" size=15 id="searchtxt">',{text:'��ѯ',iconCls:'search',handler:function(){
			           
		            	store2.load({params:{
		 		 	       start:0
		 		 	    }});
			            }}],
		            store: store2,
		            columns: [
		                {header:'��¼ID',dataIndex:'userid',width:80},
		                {header:'�û���',dataIndex:'name',width:80},
		                {header:'�ֻ���',dataIndex:'phone',width:120}
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
		            fieldLabel: '�ֻ���',
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
		           if (selections.length == 0) { Ext.Msg.alert('��ʾ', "��ѡ����"); return; }
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
					  fieldLabel: "�Ѿ���ϵ",
					  value:'1',
					  //checked:true,
					  disabled:<%out.print(cfm.getLastCallCount()<=0?"true":"false");%>,
					  id:'personcall'
},					
				  field ,{
					 xtype:'textarea',
					 anchor:"100%",
					 fieldLabel: "����<br>(���)",
					 height:80,
					 maxLength:60,
					 value:data['path'],
					 allowBlank:false,
					 id:'title'
	  				}, {
					  xtype:'textarea',
					  height:80,
				      fieldLabel : '����',
				      value:'�澯����:'+data['content']+'\n'+'�澯��Դ��:'+data['path']+'\n'+'��һ�η���ʱ��:'+data['fdate']+'\n'+'���һ�η���ʱ��:'+data['ldate']+'\n'+'�ظ�����:'+data['count'],
				      allowBlank:false,
				      anchor:"100%",
				      id : 'context'
				  },{
					  xtype:'panel',height:40,autoLoad:{
						  url:'./AlarmAction.jsp',
						  scripts: true,
						  params : {mode:'responseible',alarmId:id}
						  
					  }
				  },{xtype:'panel',height:40,html:'<font color="red" size=3>��ʣ�����������</font><font color="red" size=4><%=cfm.getLastCallCount()%></font>'}, {
					  xtype:'panel',
					  height:100,
				      html:'<font color="red" size=3>����Ѿ���ϵ�ˣ�ϵͳֻ��¼��ϵ���̣��������Զ��ɵ�������̨,�����ʣ���������Ϊ0ʱ��������ֻ���ϵ�ˡ�</font>'
				  }]

				});
		        var win = new Ext.Window({
					title:'�������д',
					id:'whwin',
					width:400,
					height:400,
					layout:'fit',
					items:[form],
					buttons:[{text:'���',handler:function(){
						if (!form.form.isValid()){
							Ext.MessageBox.alert('��ʾ','����д���������߱��ⲻ�ܳ���53�����֡�');
							return;
						}else{
							
							form.form.submit({
								url:'./AlarmAction.jsp',
								waitMsg: '���Ե�,����������ɵ�...',
								success: function(form, action)
								{
									if (action.result.success){
										Ext.MessageBox.alert("��ʾ","����ɵ��ɹ�.");
									   store1.load({params:{start:0}});
									   win.close();
									}else{
										Ext.MessageBox.alert("��ʾ", "����ɵ�ʧ��:"+action.result.msg);
									}
								},
								failure: function(form, action)
								{
									Ext.MessageBox.alert("��ʾ", "����ɵ�ʧ��:"+action.result.msg);
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
						
					  
					}},{text:'ȡ��',handler:function(){
						win.close();
						
					}}]
			    });
				win.show();
				
		      
	        					
		    }

			
		    //��ʱˢ�¸����ַ���������ʾ�Ƿ���ڸ澯��Ҫ����
	        window.setInterval("changeTxt()",800);
	        //����Ƿ��и��ٸ澯
	        window.setInterval("checkTrackAlarm()",5*1000);
	        function checkTrackAlarm(){
	        	ctx.get(checkTrackAlarmBack,"trackManager.isTrack()");
		    }
		    //�����
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
		    //�ַ���˸
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
			//��ʾ���ٸ澯�б�
			function trackWindow(){
				if (isTrackWin) return;
				isTrackWin = true;
				init();


				  var sm=new Ext.grid.CheckboxSelectionModel();   
		            var cm=new Ext.grid.ColumnModel([   
		                sm,   
		                {header:'ID',dataIndex:'id',width:50,hidden:true},
						{header:'����',dataIndex:'grad',width:40},
						{header:'·��',dataIndex:'path',width:250},
		        		{header:'��һ��ʱ��',dataIndex:'fdate',width:120},
						{header:'���һ��ʱ��',dataIndex:'ldate',width:120},
						{header:'����',dataIndex:'count',width:40},
						{header:'����',dataIndex:'content',width:250},
						{header:'����',dataIndex:'workid'}
						
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
	                stripeRows:true,//������Ч��   
	                loadMask:true,   
	                store: store1,   
	                cm:cm,
					tbar:["-",{text:'�ӳٸ���',handler:function(){
						var data = grid1.getSelectionModel().getSelections();
						if (data.length<=0) {
							Ext.Msg.alert("��ʾ","��ѡ����Ҫ�ӳٸ��ٵĸ澯");
							return;
						}
						
							var ids = "";
						  	for (var i=0;i<data.length;i++){
							   if (i>0) ids += ',';
							   ids += data[i].data['id'];
					 	  	}
					 	  	
							nextTarckWin(ids);
						}},"-",{text:'��������',handler:function(){
							  var data = grid1.getSelectionModel().getSelections();
							  if (data.length<=0) {
								Ext.Msg.alert("��ʾ","��ѡ����Ҫ���ٵĸ澯");
								return;
							  }
							  var ids = "";
							  for (var i=0;i<data.length;i++){
								  if (i>0) ids += ',';
								  ids += data[i].data['id'];
						 	  }
						 	  
							  //�������ٴ���ҳ��
							  selectesTrak(ids);
							}

						},"-", {text:'ȡ������',handler:function(){
							var data = grid1.getSelectionModel().getSelections();
							if (data.length<=0) {
								Ext.Msg.alert("��ʾ","��ѡ��ȡ�����ٵĸ澯");
								return;
							}
							var ids = "";
							for (var i=0;i<data.length;i++){
								if (i>0) ids += ',';
								ids += data[i].data['id'];
						 	}
							Ext.Msg.confirm('��ʾ', '���Ƿ�ȷ��ȡ������?', function(btn, text){
								if (btn == 'yes'){
					        		Ext.Ajax.request({
										method: 'post',
										params: {
											mode:'cancelTrack',
											id:ids
										},
										success: function(resp, opts){
										    Ext.Msg.alert("��ʾ","ȡ�����ٳɹ�");
										    if (trackStore) trackStore.load({params:{start:0,limit:20}});
										},
										failure: function(resp, opts){
										   Ext.Msg.alert("��ʾ","ȡ������ʧ��");
										},
										url: './AlarmAction.jsp'
									});
					        	}
					        });
							  //�������ٴ���ҳ��
							  // selectesTrak(ids);
							}

						},"-"
					],
	                bbar:new Ext.PagingToolbar({   
	                    pageSize:20,   
	                    store: store1,   
	                    displayInfo:true,   
	                    displayMsg:' ��ʾ��{0}����{1}����¼,һ��{2}��',   
	                    emptyMsg:"û�м�¼"   
	                })                   
	            });

			    grid1.on("rowdblclick",function(a,b,c){
			    	var data =grid1.getSelectionModel().getSelected().data;
					alarmWin(data["id"],2);
				});
				
				trackwin = new Ext.Window({
					title:'���ٸ澯�б�',
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
					title:'��д���ټ�¼',
					width:300,
					id:'pltrackwin',
					height:280,
					html:'<textarea id="trackcontent" style="width:278px;height:200px" ></textarea>',
					buttons:[{text:'ȷ��',handler:function(){
					
						if (trackcontent.value==""){
							Ext.Msg.alert("��ʾ","����д���ټ�¼");
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
        	                  Ext.Msg.alert("��ʾ","�ӳ����ٳɹ���,��ȴ�<%=AstConfig.TRACK_TIME%>Сʱ�ٸ���");
        	                  win.close();
        	                  if (trackStore) trackStore.load({params:{start:0,limit:20}});
        	                },
        	                failure: function(resp, opts){
        	                   Ext.Msg.alert("��ʾ","��д���ټ�¼ʧ��");
        	                },
        	                url: './AlarmAction.jsp'
        	            });

						}},{text:'ȡ��',handler:function(){
						win.close();

						}}]
			    });
				win.show();
				
			}
		    /**
		    *���ø��ٲ���
		    */
			var isconfigTrack = false; //����ҳ���Ƿ��Ѿ���ʾ
		    function showConfig(){
		    	if (isconfigTrack) return;
				var configtrackwin = new Ext.Window({
					title:'��������',
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
					    fieldLabel:"��ע����(��ǰ������)",
					    value:'<%=AstConfig.CONCREN_GRADE%>',
					    store:new Ext.data.SimpleStore({ 
					        fields:['caption','value'], 
					        data:[['����澯','1'],['һ��澯','2'],['��Ҫ�澯','3'],['���ظ澯','4']]}), 
					        displayField:'caption',
					        valueField:'value',
					        typeAhead: true, 
					        mode: 'local', 
					        forceSelection:true, 
					        selectOnFocus:true
					}),{
						xtype:"numberfield",
						name:'CONCRENTIME',
						fieldLabel:"��ע���ʱ��(��λ:����)",
						value:<%=AstConfig.CONCREN_TIME%>,
						anchor:"100%"
					},
					new Ext.form.ComboBox({
						anchor:"100%",
					    hiddenName:'TRACKGRADE',
				        triggerAction:"all",
				        value:<%=AstConfig.TRACK_GRADE%>,
				        fieldLabel:"���ټ���(��ǰ������)",
				        store:new Ext.data.SimpleStore({ 
				          fields:['caption','value'], 
				          data:[['����澯','1'],['һ��澯','2'],['��Ҫ�澯','3'],['���ظ澯','4']]}), 
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
						fieldLabel:"����ʱ��(��λ:Сʱ)",
						anchor:"100%"
					},
					{
						xtype:"numberfield",
						name:'TRACKNEXTTIME',
						 value:<%=AstConfig.TRACK_NEXT_TIME%>,
						fieldLabel:"�ӳ�ʱ��(��λ:Сʱ)",
						anchor:"100%"
					},{
						anchor:"100%",
						xtype:"textfield",
	                    fieldLabel: '���˹�ע�ڵ㣨;�ָ���',
	                    value:'<%=AstConfig.PATH_FILTER == null ? "" : AstConfig.PATH_FILTER%>',
	                    name: 'PATHFILTER'
					},{
						anchor:"100%",
						xtype:"textfield",
						id:'trackFilterPath',name:'trackFilterPath',value:'<%=AstConfig.TRACK_PATH_FILTER_PATH%>',						
	                    fieldLabel: '���˸��ٽڵ㣨;�ָ���',	                   
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
					buttons:[{text:'����',handler:function(){
						Ext.Msg.confirm('��ʾ', '���Ƿ�ȷ�ϸ��µ�ǰ�������á�', function(btn, text){
			        	    if (btn == 'yes'){
								Ext.getCmp("simple").getForm().submit({   
									waitTitle : '��ʾ',//����   
									waitMsg : '�����ύ�������Ժ�...',//��ʾ��Ϣ   
									url : './AlarmAction.jsp',   
									 method : 'post',   
									 params  : {mode:'config'},   
									 success : function(form, action) {   
										Ext.Msg.alert("��ʾ","�����������,ϵͳ�������¼��ز�����");
										window.location.reload();
									 },   
									 failure : function(form,action) {   
										 Ext.Msg.alert("��ʾ","����������ɣ�");
									 }
							   });
			        	    }
			        	});
			        			
						}},{text:'ȡ��',handler:function(){
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
			//���������Ϣ
			function saveTrack(id){
				if (trackctnt.value==""){
					Ext.Msg.alert("��ʾ","����д���ټ�¼");
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
	                  Ext.Msg.alert("��ʾ","�ӳ����ٳɹ���,��ȴ�<%=AstConfig.TRACK_TIME%>Сʱ�ٸ���");
	                  alarminfowin.close();
	                  if (trackStore) trackStore.load({params:{start:0,limit:20}});
	                },
	                failure: function(resp, opts){
	                   Ext.Msg.alert("��ʾ","�ӳ�����ʧ��");
	                },
	                url: './AlarmAction.jsp'
	            });
				
			}
			//��ʾ�ӳٸ��ٵ�ҳ��
			var isNextTrack = false;
		    function nextTarckWin(id){
	        	if (isNextTrack) return;
				var clwin = new Ext.Window({
					title:'��д�ӳ����ٵ�ԭ��',
					width:300,
					height:280,
					html:'<textarea id="trackcontent" style="width:278px;height:200px" ></textarea>',
					buttons:[{text:'ȷ��',handler:function(){
					
						if (trackcontent.value==""){
							Ext.Msg.alert("��ʾ","����д�ӳ�����");
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
        	                  Ext.Msg.alert("��ʾ","�ӳ����ٳɹ���,��ȴ�<%=AstConfig.TRACK_NEXT_TIME%>Сʱ�ٸ���");
        	                  clwin.close();
        	                  if (trackStore) trackStore.load({params:{start:0,limit:20}});
        	                },
        	                failure: function(resp, opts){
        	                   Ext.Msg.alert("��ʾ","�ӳ�����ʧ��");
        	                },
        	                url: './AlarmAction.jsp'
        	            });

						}},{text:'ȡ��',handler:function(){
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

		    //�澯��Ϣչʾҳ��
		    var isAlarmInfo = false;
		    var alarminfowin = null;
		    function alarmWin(id,type){
			  // alert(type);
		    	if (isAlarmInfo) return;
				var clwin = new Ext.Window({
					title:'�澯��Ϣ',
					width:700,
					modal:true,
					height:480,
					items:[{
						xtype:"tabpanel",
						activeTab:(type==2?2:0),
						items:[
							{
								xtype:"panel",
								title:"����",
								height:h-180,
								autoLoad:{
									url:'./AlarmInfo.jsp?mode=base&alarmId='+id
								    ,scripts:true
								}
							},{
								xtype:"panel",
								title:"��ע",
								height:h-180,
							
								autoLoad:{
									url:'./AlarmInfo.jsp?mode=concernhis&alarmId='+id
								    ,scripts:true
								}
								},
							{
								xtype:"panel",
								title:"����",
								height:h-180,
								autoLoad:{
									url:'./AlarmInfo.jsp?istrack='+type+'&mode=track&alarmId='+id
								    ,scripts:true
								}
							},
							{
								xtype:"panel",
								title:"��ϵ",
								height:h-180,
							    autoLoad:{
									url:'./AlarmInfo.jsp?mode=phone&alarmId='+id
									,scripts:true
								}
							},
							{
								xtype:"panel",
								title:"����֪ʶ",
								height:h-120,
							    autoLoad:{
									url:'./AlarmInfo.jsp?mode=kbm&alarmId='+id
									,scripts:true
								}
							}
						]
					}],
					buttons:[{text:'�鿴����',handler:function(){ 
							openModalDialog("http://bomc.js.cmcc/kbm2/infoView/-1?taskType=sysmgr&taskOid=" +id +"#",600,500); }
						}, {text:'ȡ��',handler:function(){
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

		  //�澯��ѯ����
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
					
					fieldLabel : "��ʼʱ��"
					});
			   sdf.setValue(sdate);
			   var edf = new Ext.ux.form.DateTime({
					id : "lDate",
					fieldLabel : "��ֹʱ��",
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
                    fieldLabel: '����',   
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
				      fieldLabel:"����",
				      store:new Ext.data.SimpleStore({ 
				          fields:['caption','value'], 
				          data:[['����澯','1'],['һ��澯','2'],['��Ҫ�澯','3'],['���ظ澯','4']]}), 
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
					title:'��ѯ',
					width:560,
					height:160,
					layout:'fit',
					items:[form],
					buttons:[{text:'��ѯ',handler:function(){
						
					   start_time = Ext.get("sDate").getValue();
					   end_time = Ext.get("lDate").getValue();
					   selectGrade = Ext.getCmp("sgrade").getValue();
			           alarmnr = Ext.get("sctnt").getValue();
					    store1.load({params:{
	       					start:0
	    				}});
	    				
	    				isAdvWin = false;
					    clwin.close(); 
					}},{text:'ȡ��',handler:function(){
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
			
			//�Ѿ����˵�·��
	        var selectedPath = '';
	        // ѡ����Ҫ���˵�·��ID
	        var selectedDotID = '';
	        // ѡ����Ҫɾ����·��ID
	        var delDotID = '';
	        // ��ʱ��Ź���·��ȫ�Ƶ��б�(;�ָ�)
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
			
			// ȡ�ù��˸���PATH�ķ���
		    function getTrackPathFilter(selectedPaths)
		    { 
			    var url = "../motree_new.jsp?flag=TrackPath";
			    // �Ҳ��������չʾ��ѡ�Ĺ���·��
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

			    // �Ҳ���壬����eastForm��������ť
			    var eastPanel = new Ext.Panel({
			    	width: 250,
			    	egion:'east',
			    	layout:'fit',
			    	style : "border 0px none;scrollbar:true",
			    	items:[eastForm],
			    	buttons:[{
				    	xtype:'button',text :'ȷ��',
					    listeners:{ 
							click:function(selectText, delay) {
								setMoInfo();
								Ext.getCmp('TRACKPATHFILTER').setValue(selectedDotID);
								Ext.getCmp('trackFilterPath').setValue(showPath);
								selectTrackFilterWin.close();
							}}},{
								xtype:'button',text :'ȡ��',
								listeners:{ 
								click:function(selectText, delay) 
							{
								selectTrackFilterWin.close();
							}}}
					]
			    });

			    // �в���壬������ѡ����ѡ��ť
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
						     value:'�ڶ������У����ֶ���Ϊ���Ͷ������Բ���ѡ������/10.����/SUN����',
						     style:'marginTop:5px;overflow-y:hidden'
					        }]
		           }]
	            });

			    var selectTrackFilterWin = new Ext.Window({
				    title : 'ѡ����˶���',
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
				
				// ����ʼ��
			    var ids = Ext.getCmp('TRACKPATHFILTER').getValue().split(";"); //�ַ��ָ�   
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
                                //�������¼�����
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
			     * ��ѡ��Ķ���·�����뵽�����б��У�����eastFrom����ʾ
			     */
			    function addSelectPath()
			    {
					if (null == selectedDotID && "" == selectedDotID.trim()) {
						return;
					}

					if ( moInfo.Exists(selectedDotID) ) {
						Ext.Msg.alert('��ʾ','���Ѿ�ѡ���˸ö���');
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
	                        //�������¼�����
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
			 * ѡ��һ��·����ӵ�����·���б�֮ǰ
			 * �жϹ���·���б����Ƿ��Ѵ��ڸ�·�����·���ĸ��ڵ�
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
		     * ѡ��һ��·����ӵ�����·���б�֮ǰ
		     * ȡ���ڹ���·���б����Ѵ��ڵĸ�·���������ӽڵ�
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
		     *  String������չstartWith����
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
<%@ page contentType="text/html; charset=GB2312" errorPage="/error.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%
/**
*���ܣ����Ը澯ҳ��
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
	    //���Ը澯��ʱ2����ˢ��
	    window.setInterval("refershIgnore()",2*60*1000);
	    
	    function refershIgnore(){
	       if (store1){ 
	         store1.load({params:{
	           start:0
	         }});
	       }
	    }
	    var w = 800; //��ʼ��ҳ����
 		var h = 500; //��ʼ��ҳ��߶�
 		function init(){
			w = document.body.clientWidth; //��ȡҳ����
            h = document.body.offsetHeight ; //��ȡҳ��߶�
		}
	
			var selectGrade = 1; //��ǰѡ��ļ���
			var alarmnr = "";//���˵ĸ澯����
			var store1 = null;
			var search_type = 1; //��ѯ���� 1��������ѯ 2���߼���ѯ
			var start_time = "";
			var end_time = "";
			
			
			Ext.onReady(function(){   
               var sm=new Ext.grid.CheckboxSelectionModel();  
            var cm=new Ext.grid.ColumnModel([   
                
                {header:'ID',dataIndex:'id',width:50,hidden:true},
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
				{header:'����',dataIndex:'content',width:350}
				
              ]);                
               
              store1 = new Ext.data.Store({ 
                 proxy:new Ext.data.HttpProxy({
                    url:"./AlarmAction.jsp",
                    timeout:1000000
                }),
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
              
                stripeRows:true,//������Ч��   
                loadMask:true,   
                store: store1,   
               
                cm:cm,
				tbar:['-','���ݣ�<input id="alarmcontent" type="text" size=15>',
				{text:'��ѯ',iconCls:'search',handler:function(){
					search_type=1;
				   if (alarmcontent) alarmnr = alarmcontent.value;
				   store1.load({params:{
	       				start:0
	    			}});
				}},
				' &nbsp;������ˣ�',
				'&nbsp; <img src="../../images/icons/grade1.gif" style="cursor:hand;" title="����澯" onclick="changeGrade(1)">',
				'&nbsp; <img src="../../images/icons/grade2.gif" style="cursor:hand;" title="һ��澯" onclick="changeGrade(2)">',
				'&nbsp; <img src="../../images/icons/grade3.gif" style="cursor:hand;" title="��Ҫ�澯" onclick="changeGrade(3)">',
				'&nbsp; <img src="../../images/icons/grade4.gif" style="cursor:hand;" title="���ظ澯" onclick="changeGrade(4)">',
				'&nbsp;��ǰ����<img id="currgradimg" src="../../images/icons/grade1.gif" >','-',{text:'�߼���ѯ',iconCls:'search',handler:function(){
				   AdvWin();
				   
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
					title:'���Ը澯�߼���ѯ',
					width:560,
					height:160,
					layout:'fit',
					items:[form],
					buttons:[{text:'��ѯ',handler:function(){
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
        	</script>
		</body>
</html>
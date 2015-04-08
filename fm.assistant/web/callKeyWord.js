var modifyWin;

var gridMo;
var barMo;
var storeMo;

var storeKey; // �ؼ��ִ洢
var barKey;   // �ؼ���grid����8
var gridKey;  // �ؼ���grid

var oldRecode;
var oldData;
			
var leftTabs;

var searchText = new Ext.form.TextField({ id:'tf', emptyText :'', width:170});

// ��ѯ��ť
var searchBut = new Ext.Button({
	text:"��ѯ",
	icon: contextPath + '/images/search.gif',
	iconCls:'x-btn-text-icon',
	handler: doSearch
});

var colM = new Ext.grid.ColumnModel([
	new Ext.grid.RowNumberer(),//�Զ��к�
	{dataIndex:"nodeId",hidden:true},
	{dataIndex:"caption",width:210}
]);
		
var keyPanel = new Ext.Panel({ region:'center', id:'keyPanelId', autoScroll:true, collapsible:true ,	
		tbar:[ '-', { text : '��ӹؼ���', iconCls:'x-btn-text-icon',icon: contextPath + '/images/addrule.gif', handler : function() {addKeyword('')} }, '-']
});

function init() {
	storeMo = new Ext.data.Store({  //����洢
		proxy:  new Ext.data.HttpProxy({url:'/sysmgr/fm/assistant/callKeyWordData.jsp?mode=storeMo'}),
		reader: new Ext.data.JsonReader(
	                { totalProperty: 'totalProperty', root: 'root' }, 
	                [{name: 'nodeId'},{name: 'caption'}])
	});
	storeMo.on('beforeload', function() {
		var condition = document.getElementById('tf');
		//alert(condition ? condition.value : '');
		Ext.apply(this.baseParams, { mode : 'storeMo', limit : 200, condition:condition ? condition.value : '' });
	});
	gridMo = new Ext.grid.GridPanel({
		id:'gridMo', 
		autoScroll:true, 
		title:"�����б�",
		cm:colM,
		store:storeMo,
		disableSelection:true 		
	});
	
	storeKey = new Ext.data.Store({
		url : "/sysmgr/fm/assistant/callKeyWordData.jsp",
		reader: new Ext.data.JsonReader(
	                { totalProperty: 'totalProperty', root: 'root'}, 
	                [{name: 'nodeId'},{name: 'caption'}])
	}); 	
	storeKey.on('beforeload', function() {
		var condition = document.getElementById('tf');
		Ext.apply(this.baseParams, { mode : 'keyword', limit : 200, condition:condition ? condition.value : '' });
	});
	gridKey = new Ext.grid.GridPanel({
		id:'gridKey', autoScroll:true, title:"�ؼ����б�", cm:colM, store:storeKey, disableSelection:true
	});
	
	leftTabs = new Ext.TabPanel({
		split:true,
		width:240,
		activeTab:0,
		region:'west',
		defaults: {autoScroll:false},
		items:[gridMo,gridKey],
		tbar:['-',searchText,searchBut,'-']
	});
}

/*��ݽڵ�ID�͹ؼ���ɾ��ָ���ؼ���*/
function deleteKey( id ) {
	Ext.Msg.confirm('��ʾ:',"ȷ��Ҫɾ����?",
		function(button,text){
			if(button=='yes'){
				ctx.get( function (data) {
					if ( data > 0 ) {
						Ext.Msg.alert('��ʾ:','ɾ�����ɹ���', function(){refreshPage();});
					} else {
						Ext.Msg.alert('��ʾ:','ɾ�����ʧ�ܣ�');
					}
				},"callKeyWordManager.deleteCallKeyWordInfo(\"" + id + "\")"); 				
			}
	});
}

/* ˢ��ҳ�� */
function refreshPage(){ window.document.location.reload();}


/* ��ӹؼ��� */
function addKeyword( id ) {
	var saveBut = new Ext.Button({
		text:"����",
		icon: contextPath + '/images/icons/save.gif',
		iconCls:'x-btn-text-icon',
		handler:saveKeywords
	});
	var closeBut = new Ext.Button({
		text:"�ر�",
		icon: contextPath + '/images/icons/delete.gif',
		iconCls:'x-btn-text-icon',
		handler:function () {modifyWin.hide();}
	});
	if (!modifyWin ) {
		modifyWin = new Ext.Window({
	        height:270,	
	        width:400,
	        modal: true,
	        resizable:false,
			autoScroll:true,
	        closeAction:'hide',
	   	    title:"��ӹؼ���",
	   	    id:'modifyWinId',
	        buttons:[saveBut,closeBut],
	        buttonAlign:'center'
	   	});
	   	modifyWin.setPosition(Ext.get('keyPanelId').getLeft() - 80,Ext.get('keyPanelId').getTop()+60);
	}
	if ( id != "") { modifyWin.setTitle("�޸Ĺؼ���"); }
   	modifyWin.show();
	modifyWin.load({url:'./saveCallKeyWord.jsp?id='+id, waitMsg:'Loading', scripts:true});
}

/* ��ѯ */
function doSearch () {	
	if (leftTabs.getActiveTab().getId() == 'gridMo') {
		storeMo.load({params:{ start:0,limit:200 }});
	} else if (leftTabs.getActiveTab().getId() == 'gridKey') {
		storeKey.load({params:{ start:0,limit:200 }});
	}
	return true;
}

/* ���ƹ����б���ҳ������б����� */
function showFont (grid, rowIndex) {
	if (!grid) grid = gridMo;
	if (!rowIndex) rowIndex = 0;
	var view=grid.getView();

	// �ָ���¼
	if (oldRecode) oldRecode.set("caption", oldData);
    var record = grid.getStore().getAt(rowIndex);
    if (!record) return; //û�м�¼��ʱ�򲻴���
    var data = record.get("caption");
    
    // ���¼�¼
    oldRecode = record; oldData = data;	        
    data = "<font color='blue' size=2><b>"+ data + "</b></font>"
    record.set("caption", data);
    
    var nodeId = record.get("nodeId");
    if (leftTabs.getActiveTab().getId() == 'gridKey') nodeId = oldData;
    var href = "/sysmgr/fm/assistant/viewCallKeyword.jsp";
	keyPanel.load({ url:href,text:'Loading',scripts:true, params:{type: grid.getId(),nodeId:nodeId} });

    /* ����3�д�����4ɾ�����޸ĺ���Ӻ�ɫ��ǵ���ʽ */
    deleteStyle(gridMo.getView());
    deleteStyle(gridKey.getView());
}

/* ��4ɾ�����޸ĺ���Ӻ�ɫ��ǵ���ʽ */
function deleteStyle (view) {
	for ( i = 0; i < 20; i++ ) {
		try {
			var cell=view.getCell(i,2); 
			if ( !cell ) break; 
        	cell.className = cell.className.replace("x-grid3-dirty-cell"," ");	
    	} catch ( Exception ) {
    		break;
    	}
	}
}

function getCaption() {
	var x = Ext.get('modifyWinId').getLeft() + 72;
	var y = Ext.get('modifyWinId').getTop() + 57
    var url = contextPath + "/fm/motree.jsp?ismeta=false";
   	nodeWin = new Ext.Window({
   		title:"�ڵ�ѡ��", 
   		height:350, 
   		width:310,
   		modal:true,
       	resizable:false,
   	    closable: false,
       	buttonAlign:'center',
        buttons:[{ text: '�ر�', handler  : function(){ nodeWin.close(); } }],
   	    html:"<iframe src='"+ url +"' borderframe=0 style='width:100%;height:100%'></iframe>" 
   	}); 	
   	nodeWin.setPosition(x, y); 
   	nodeWin.show();
}

function setNode(id,caption) {	
	window.document.getElementById('mname').value = caption
	window.document.getElementById('mid').value= id;
	nodeWin.hide();
}



function saveKeywords(){
	// ����У��
	if ( document.getElementById('mid').value == '' ) {
		Ext.Msg.alert('����','��ѡ�����',120);
		return;
	}	
	
	// ������У��
	if ( document.getElementById('responsible').value == 0 ) {
		Ext.Msg.alert('����','��ѡ�������ˣ�',120);
		return;
	}	
	
	// �ؼ���У��	
	var list = window.document.getElementById("keylistid").value;
	var reg=new RegExp("\r\n","g"); 
	if( trim(list.replace(reg," ")) == '' ) {
    	Ext.Msg.alert('����:',"�ؼ��ֲ���Ϊ�գ�",120);
    	window.document.getElementById("keylistid").value='';
		return;
	}
		
	//�ύ
	Ext.Ajax.request({
		method : 'post',
		params : {
			mode : 'save',
			isRegExpr:0,
			keyword:list,
			forChild:(document.getElementById('ifcname').checked?1:0),
			moid : document.getElementById('mid').value,
			responsible:document.getElementById('responsible').value,
			rid:document.getElementById('rid').value
		},
		success : function(resp, opts) {
			var obj = Ext.util.JSON.decode(resp.responseText);
			if (obj.success) {
				Ext.Msg.alert('��ʾ', '����ɹ�!',function(){ modifyWin.hide();refresh(); }	);
			} else {
				Ext.Msg.alert('��ʾ', '����ʧ��!');
			}
		},
		failure : function(resp, opts) {},
		url : './callKeyWordData.jsp'
	});
}

//ʵ��GridPanel��ÿһ��Ԫ��tips��ʾ			 
function showPanelTips(gridPanel) {
    //����gridҳ��չʾ�Ķ���   
    var view=gridPanel.getView(); 
    for(var rowIndex=0;rowIndex<gridPanel.getStore().getCount();rowIndex++) {
        var record=gridPanel.getStore().getAt(rowIndex);   
        for(var columnIndex=0; columnIndex < gridPanel.colModel.getColumnCount(); columnIndex++){
            var textDisplay=record.get(gridPanel.colModel.getDataIndex(columnIndex));   
            var cell=view.getCell(rowIndex,columnIndex);   
             
            var cid = Ext.id(); //Ext��̬��ɵ�ID    
            cell.firstChild.setAttribute('id',cid);   
            Ext.QuickTips.register({
                target: cid,   
                text: textDisplay,   
                maxWidth: 100,      //���ú���ʾ���ݿ����Զ�����   
                dismissDelay:20000, //������ʾʱ��                               
                trackMouse: true,   
                autoHide: true,   
                animate: true  
            });
        }   
    } 
}
			
function refresh() {
	if (leftTabs.getActiveTab().getId() == 'gridMo') {
		storeMo.load({params:{ start:0,limit:200 }});
	} else if (leftTabs.getActiveTab().getId() == 'gridKey') {
		storeKey.load({params:{ start:0,limit:200 }});
	}
}

function trim(str) {
	if (typeof(str)=='undefined' || str == '') return "";
	return str.replace(/(^\s*)|(\s*$)/g, "");
}

/* (��������У�� */
function checkBrackets( str ) {
	var flag = 0;
	for ( i = 0;  i < str.length; i++ ) {
		 if ( str.charAt(i) == "(" ) flag++;
		 else if ( str.charAt(i) == ")" )  flag--
		 if ( flag >= 2 ) {
		 	Ext.Msg.alert('����:',"��֧�ָ��ӵĻ�����㣡",120);
		 	return false;
		 }
		 if (flag < 0) {
		 	Ext.Msg.alert('����:',"(�Ų�ƥ�䣬����飡",120);
		 	return false;
		 }
	}
	return flag==0
}

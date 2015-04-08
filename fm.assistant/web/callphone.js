/*
 * ��ʾ�Զ����
 */
var callstore1 = null;
var selectTxt = "";
var isShow = 1;
var reportParam;

function showAllCallPhone() {
	
	var sm = new Ext.grid.CheckboxSelectionModel();

	var cm = new Ext.grid.ColumnModel([sm,
		{ header : 'ID', dataIndex : 'id', width : 50, hidden : true },
		{ header : '���ID', dataIndex : 'callID', width : 50, hidden : true }, 
		{ header : '�����', dataIndex : 'caller', width : 80 }, 
		{ header : '���״̬', dataIndex : 'stat', renderer:setState, width : 80 }, 
		{ header : '���ʱ��', sortable: true, dataIndex : 'ctime', width : 120 }, 
		{ header : '���д���', dataIndex : 'num', align:'center', width : 60 }, 
		{ header : '����', dataIndex : 'grad', width : 40,renderer:setAlarmGradre },
		{ header : '����', dataIndex : 'workid' }, 
		{ header : '·��', dataIndex : 'path', width : 250 }, 
		{ header : '��һ��ʱ��', dataIndex : 'fdate', width : 120 },
		{ header : '���һ��ʱ��', sortable: true, dataIndex : 'ldate', width : 120 }, 
		{ header : '����', dataIndex : 'count', width : 40 }, 
		{ header : '�澯����', dataIndex : 'content', width : 250 }
	]);

	var store1 = new Ext.data.Store({
		url : "./doCallPhoneAction.jsp",
		reader : new Ext.data.JsonReader({
					root : "data",
					totalProperty : "count"
				}, [{ name : 'num' }, 
					{ name : 'callID' },
					{ name : 'ctime' }, 
					{ name : 'caller' }, 
					{ name : 'stat'},
					{ name : 'grad'}, 
					{ name : 'path'},
					{ name : 'id' }, 
					{ name : "fdate" }, 
					{ name : 'ldate' }, 
					{ name : 'count' }, 
					{ name : 'content' },
					{ name : 'oper' }, 
					{ name : 'workid' }
				])
	});

	store1.on('beforeload', function() {
		var stime = field3 ? field3.getValue() : '';
		var etime = field4 ? field4.getValue() : '';

		// ������ʱʹ�á�
		reportParam = "&isShow=" + isShow + "&stime=" + (stime == '' ? stime:stime.format('Y-m-d H:i:s'))
			+ "&etime=" + (etime == '' ? etime:etime.format('Y-m-d H:i:s'))
			+ "&path=" + (field9 ? field9.getValue() : '')
			+ "&grade=" + (field5 ? field5.getValue() : '')
			+ "&caller=" + (field10 ? field10.getValue() : '')
			+ "&code=" + (field7 ? field7.getValue() : '')
			+ "&source=" + (field6 ? field6.getValue() : '')
			+ "&selectTxt=" + selectTxt;

		Ext.apply(this.baseParams, {
			mode : 'calllist',
			limit : 200,
			isShow:isShow,
			stime:stime,
			etime:etime,
			path:field9 ? field9.getValue() : '',
			grade:field5 ? field5.getValue() : '',
			caller:field10 ? field10.getValue() : '',
			code:field7 ? field7.getValue() : '',
			source:field6 ? field6.getValue() : '',
			selectTxt:selectTxt
		});
	});
	store1.load({ params : { start : 0 } });
	
	callstore1 = store1;
	
	var grid1 = new Ext.grid.GridPanel({
			title : '',
			region : 'center',
			stripeRows : true,// ������Ч��
			loadMask : true,
			store : store1,
			cm : cm,
			sm : sm,
			tbar : [
				{ text : '����澯', iconCls : 'del', handler : function () { deleteCall ( grid1 ); }}, '-', 
				{ text : '�澯��ѯ', iconCls : 'search', handler: showSearch }, '-', 
				{ text : '�鿴����', iconCls : 'search', handler : callReport }, '-',
				{ text : '�����趨', iconCls : 'swp', handler : showCallPhoneFilterConfig }, '-', 
				{ text : '��������', iconCls : 'swp', hidden:(isCfg!=1), handler :  showCallPhoneParamConfig }, '-', 
				{ text : '�ؼ�������', iconCls : 'swp', hidden:(isCfg!=1), handler :  showKeyWords }, '-', 
				{ text : '�ݼ�����', iconCls : 'manager', hidden:(isCfg!=1), handler :  showRepConfig }, '-', 
				{ text : '�����ɫ', iconCls : 'manager', hidden:(isCfg!=1), handler :  callUserConfig }, '-', 
				{ text : '��������', iconCls : 'xls', handler : function() {
					window.location="./doCallPhoneAction.jsp?mode=doReport" + reportParam;
				} }, '-' ],
			bbar : new Ext.PagingToolbar({
				pageSize : 20,
				store : store1,
				displayInfo : true,
				displayMsg : ' ��ʾ��{0}����{1}����¼,һ��{2}��',
				emptyMsg : "û�м�¼"
			})
	});

	grid1.on("rowdblclick", function(a, b, c) {
		var data = grid1.getSelectionModel().getSelected().data;
		alarmWin(data["id"], 2);
	});

	return grid1;
	//win.on('close',function(a){ isrefershCall = false; });
}
/**
 * ���������¼
 */
function hideCallPhone(alarmIds){
	Ext.Ajax.request({
		method : 'post',
		params : {
			mode : 'hideCallPhone',
			alarmIds : alarmIds
		},
		success : function(resp, opts) {
			var obj = Ext.util.JSON.decode(resp.responseText);
			if (obj.success) {
				//Ext.Msg.alert('��ʾ', '�澯�����ɣ��б���ˢ��' );
				alert('�澯�����ɣ��б���ˢ��' );
				callstore1.load();
			} else {
				//Ext.Msg.alert('��ʾ', '����ʧ�ܣ�ԭ��' + obj.msg);
				alert('����ʧ�ܣ�ԭ��' + obj.msg);
			}
		},
		failure : function(resp, opts) {},
		url : './doCallPhoneAction.jsp'
	});
}
/**
 * �������������
 */
var filterStore; // ����������
var isStop = false;
function showCallPhoneFilterConfig() {
	if (Ext.getCmp("allCallPhoneConfigWin"))
		return;
	var treePanel = new Ext.Panel({
		title : '������',
		width : 250,
		split : true,
		region : 'west',
		html : '<iframe style="width:100%;height:100%" src="../motree_new.jsp?flag=AutoCall" frameborder=0></iframe>'
	});
	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([sm,
		{
				header : '·��',
				dataIndex : 'pathName',
				width : 150
			}, {
				header : '�Ƿ�����Ӷ���',
				dataIndex : 'ischild',
				width : 80,
				editor : new Ext.form.ComboBox({
							triggerAction : "all",
							store : new Ext.data.SimpleStore({
										fields : ['caption', 'value'],
										data : [['��', '1'], ['��', '0']]
									}),
							displayField : 'caption',
							valueField : 'value',
							typeAhead : true,
							mode : 'local',
							forceSelection : true,
							selectOnFocus : true
						}),
				renderer : function(a) {
					return (a == '1' ? '��' : '��');
				}
			}, {
				header : 'ʱ����',
				dataIndex : 'timelen',
				editor : new Ext.form.TextField({
					readOnly:true,
				    listeners : {
				      focus:function(f){
				      	 writeFeild = f;
				         var href='/sysmgr/toptea/common/timeConfig.jsp?curExp='+f.getValue();
 						 openModalDialog(href,800,650,receiveInTime);

				      }
				    	
				    }
				}),
				renderer : function(a) {
					return (a == '' ? 'ȫ��' : a);
				},
				width : 150
			}, {
				header : '�ؼ���',
				dataIndex : 'key',
				editor : new Ext.form.TextField({}),
				width : 100
			}, {
				header : '�Ƿ�����',
				dataIndex : 'isuse',
				width : 80,
				editor : new Ext.form.ComboBox({
					triggerAction : "all",
					store : new Ext.data.SimpleStore({
								fields : ['caption', 'value'],
								data : [['����', '1'], ['��ͣ', '0'], ['��ֹ', '-1']] }),
					displayField : 'caption',
					valueField : 'value',
					typeAhead : true,
					mode : 'local',
					forceSelection : true,
					selectOnFocus : true
				}),
				renderer : function(a) {
					if( a == 1 ) { return "����"; }
					if( a == 0 ) { return "��ͣ"; }
					if( a == -1 ) { return "��ֹ";}	
					return a;					
				}
			}, {
				header : '��������',
				dataIndex : 'forPara',
				width : 80,
				editor : new Ext.form.ComboBox({
					triggerAction : "all",
					store : new Ext.data.SimpleStore({
						fields : ['caption', 'value'],
						data : [['����', '1'],['����', '0']]
					}),
					displayField : 'caption',
					valueField : 'value',
					typeAhead : true,
					mode : 'local',
					forceSelection : true,
					selectOnFocus : true
				}),
				renderer : function(a) {
					return (a == '1' ? '����' : '����');
				}
			}, {
				header : '��ǰ������',
				dataIndex : 'reper',
				// hidden:true,
				width : 150
			}]);

	var store1 = new Ext.data.Store({
		url : "./doCallPhoneAction.jsp",
		reader : new Ext.data.JsonReader(
			{ root : "data", totalProperty : "count" }, 
			[
				{ name : 'pathName' }, { name : 'moid' }, 
				{ name : 'ischild' }, { name : 'timelen' }, 
				{ name : 'key' }, { name : 'isuse' }, 
				{ name : 'forPara' }, { name : 'reper' }
			]
		)
	});
	store1.on('beforeload', function() {
		Ext.apply(this.baseParams, { mode : 'configlist', limit : 20 });
	});
	store1.load();
	filterStore = store1;
	var grid1 = new Ext.grid.EditorGridPanel({
		title : '�������嵥',
		region : 'center',
		stripeRows : true,// ������Ч��
		sm : sm,
		loadMask : true,
		store : store1,
		clicksToEdit : 1,
		tbar : ['-', {
			text : '��������',
			iconCls : 'save',
			handler : function() {
				var data = grid1.getSelectionModel().getSelections();
				if (data.length <= 0) {
					Ext.Msg.alert("��ʾ", "��ѡ����Ҫ���µ����ã�");
					return;
				}
				var xml = "<datas>";
				for (var i = 0; i < data.length; i++) {
					var r = data[i];
					xml += "<data path=\"" + r.get('moid') + "\" key=\""
							+ (r.get('key') ? r.get('key') : '')
							+ "\" timelen=\"" + r.get('timelen')
							+ "\" ischild=\"" + r.get('ischild')
							+ "\" forPara=\"" + r.get('forPara')
							+ "\" isuse=\"" + r.get('isuse') + "\" />";
				}
				xml += "</datas>";

				Ext.Ajax.request({
					method : 'post',
					params : {
						mode : 'saveConfig',
						xml : xml
					},
					success : function(resp, opts) {
						var obj = Ext.util.JSON
								.decode(resp.responseText);

						if (obj.success) {
							Ext.Msg.alert('��ʾ', '�������');

						} else {
							Ext.Msg.alert('��ʾ', '����ʧ�ܣ�ԭ��' + obj.msg);
						}
					},
					failure : function(resp, opts) {
						// Ext.Msg.alert("��ʾ","��ע�澯ȡ��ʧ��?");
					},
					url : './doCallPhoneAction.jsp'
				});

			}
		}, {
			text : '����',
			hidden : true,
			iconCls : 'xls'
		}, {
			text : '����',
			hidden : true,
			iconCls : 'xls'
		}, {
			text : 'һ����ͣ',
			id : 'stopBtn',
			iconCls : 'stop2',
			handler : function() {
				var obj = Ext.getCmp('stopBtn');
				if (obj)
					obj.disable();
				isStop = (!isStop);
				Ext.Ajax.request({
							method : 'post',
							params : {
								mode : 'keyPause',
								isStop : isStop
							},
							success : function(resp, opts) {
								var obj = Ext.util.JSON
										.decode(resp.responseText);
								if (obj.success) {
									Ext.Msg.alert('��ʾ', '�Ѿ�ȫ����ͣ');
									store1.load();
									var obj = Ext.getCmp('stopBtn');
									if (obj)
										obj.enable();
									if (isStop) {
										obj.setText('һ������');
									} else {
										obj.setText('һ����ͣ');
									}
								} else {
									Ext.Msg.alert('��ʾ', '����ʧ�ܣ�ԭ��' + obj.msg);
								}
							},
							failure : function(resp, opts) {
								// Ext.Msg.alert("��ʾ","��ע�澯ȡ��ʧ��?");
							},
							url : './doCallPhoneAction.jsp'
						});
			}
		}, {
			text : 'ɾ��',
			iconCls : 'del',
			handler : function() {
				var data = grid1.getSelectionModel().getSelections();
				if (data.length <= 0) {
					Ext.Msg.alert("��ʾ", "��ѡ����Ҫɾ�������ã�");
					return;
				}
				var moids = "";
				for (var i = 0; i < data.length; i++) {
					if (i > 0)
						moids += ',';
					var r = data[i];
					moids += r.get('moid');
				}
				Ext.Msg.confirm('��ʾ', '��ȷ��ɾ����Щ������', function(btn, text) {
							if (btn == 'yes') {

								Ext.Ajax.request({
											method : 'post',
											params : {
												mode : 'removeFilters',
												moids : moids
											},
											success : function(resp, opts) {
												var obj = Ext.util.JSON
														.decode(resp.responseText);
												if (obj.success) {
													store1.load();
												} else {
													Ext.Msg.alert('��ʾ',
															'����ʧ�ܣ�ԭ��'
																	+ obj.msg);
												}
											},
											failure : function(resp, opts) {
											},
											url : './doCallPhoneAction.jsp'
										});

							}
						});
			}
		}, {
			text : '�ر�',
			iconCls : 'cannel',
			handler : function() {
				win.close();
			}
		}, '->', {
			text : '����',
			iconCls : 'help',
			hidden:true,
			handler : function() {
				if (MsgTip.ishow)
					return;
				MsgTip.msg('����', msgstr);
			}
		}],
		cm : cm,
		bbar : new Ext.PagingToolbar({
					pageSize : 20,
					store : store1,
					displayInfo : true,
					displayMsg : ' ��ʾ��{0}����{1}����¼,һ��{2}��',
					emptyMsg : "û�м�¼"
				})
	});
	var win = new Ext.Window({
				title : '�Զ������������',
				id : 'allCallPhoneConfigWin',
				width : 900,
				height : 500,
				layout : 'border',
				items : [treePanel, grid1],
				modal : true
			});

	win.show();
}
/**
 * �������ݼ�����
 */
function showRepConfig() {
	if (Ext.getCmp("showRepConfigWin"))
		return;
	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([sm,

	{
				header : '������',
				dataIndex : 'userName',
				width : 100
			}, {
				header : '״̬',
				dataIndex : 'stat',
				hidden : true,
				width : 80,
				renderer : function(a) {
					return (a == 'true' ? '����' : '����');
				}
			}, {
				header : '��ʼ����',
				dataIndex : 'startTime',
				width : 130,
				editor : new Ext.form.DateField({
							format : 'Y-m-d'
						}),
				renderer : function(a) {
					var str = new Date(a);
					return str.getYear() + "-" + (str.getMonth() + 1) + "-"
							+ str.getDate();

				}

			}, {
				header : '��ֹ����',
				dataIndex : 'endTime',
				width : 130,
				editor : new Ext.form.DateField({
							format : 'Y-m-d'
						}),
				renderer : function(a) {
					var str = new Date(a);
					return str.getYear() + "-" + (str.getMonth() + 1) + "-"
							+ str.getDate();

				}
			}, {
				header : '�Ƿ�����',
				dataIndex : 'isuse',
				width : 80,
				editor : new Ext.form.ComboBox({
							triggerAction : "all",
							store : new Ext.data.SimpleStore({
										fields : ['caption', 'value'],
										data : [['����', '1'], ['��ֹ', '-1']]
									}),
							displayField : 'caption',
							valueField : 'value',
							typeAhead : true,
							mode : 'local',
							forceSelection : true,
							selectOnFocus : true
						}),
				renderer : function(a) {
					return (a == '1' ? '����' : '����');
				}
			}]);

	var store1 = new Ext.data.Store({
				url : "./doCallPhoneAction.jsp",
				reader : new Ext.data.JsonReader({
							root : "data",
							// ת�����·��
							totalProperty : "count"
						}, [{
									name : 'userId'
								}, {
									name : 'userName'
								}, {
									name : 'stat'
								}, {
									name : 'startTime'
								}, {
									name : 'isuse'
								}, {
									name : 'endTime'
								}])
			});
	store1.on('beforeload', function() {
				Ext.apply(this.baseParams, {
							mode : 'reperconfig',
							limit : 20
						});
			});
	store1.load();
	var grid1 = new Ext.grid.EditorGridPanel({
		title : '',
		region : 'center',
		stripeRows : true,// ������Ч��
		sm : sm,
		loadMask : true,
		store : store1,
		clicksToEdit : 1,
		tbar : ['-', 'ѡ��������', new Ext.form.ComboBox({
							triggerAction : "all",
							anchor : '90%',
							id : 'reperList',
							width : 150,
							mode : 'remote',
							emptyText : '��ѡ��...', // û��Ĭ��ֵʱ,��ʾ���ַ���
							store : new Ext.data.JsonStore({ // ��������
								url : "./doCallPhoneAction.jsp?mode=replist",
								fields : new Ext.data.Record.create(['text',
										'value']), // Ҳ��ֱ��Ϊ["text","value"]
								root : 'datalist'
							}),
							valueField : 'value', // ���͵�ֵ
							displayField : 'text' // UI�б���ʾ���ı�

						}), {
					text : '���',
					iconCls : 'add',
					handler : function() {
						var obj = Ext.getCmp('reperList');
						if (!obj)
							return;
						if (obj.getValue() == '') {
							return;
						}
						var count = store1.getCount();
						for (var i = 0; i < count; i++) {
							var rr = store1.getAt(i);
							if (rr.get('userId') == obj.getValue()) {
								Ext.Msg.alert('��ʾ', '��ѡ�����Ա�Ѿ����ݼ���');
								return;
							}
						}
						var r = new ReperRecord({
									userId : obj.getValue(),
									userName : obj.lastSelectionText,
									startTime : new Date().getTime(),
									endTime : new Date().getTime() + 1000 * 24
											* 60 * 60,
									isuse : 1

								});

						store1.addSorted(r);
					}
				}, '-', {
					text : '��������',
					iconCls : 'save',
					handler : function() {

						var data = grid1.getSelectionModel().getSelections();
						if (data.length <= 0) {
							Ext.Msg.alert("��ʾ", "��ѡ����Ҫ���µ����ã�");
							return;
						}
						var xml = "<datas>";
						for (var i = 0; i < data.length; i++) {
							var r = data[i];
							if (r.get('startTime') == ''
									|| r.get('endTime') == '') {
								Ext.Msg.alert('��ʾ', '�ݼ���Ҫ�趨��ֹʱ��');
								return;
							}
							var st = new Date(r.get('startTime')).getTime();
							var et = new Date(r.get('endTime')).getTime();
							if (et <= st) {
								Ext.Msg.alert('��ʾ', r.get('userName')
												+ '�ݼٽ�ֹʱ����ڿ�ʼʱ��');
								return;
							}

							xml += "<data userId=\"" + r.get('userId')
									+ "\" startTime=\"" + st + "\" endTime=\""
									+ et + "\" isuse=\"" + r.get('isuse')
									+ "\" />";
						}
						xml += "</datas>";
						Ext.Ajax.request({
									method : 'post',
									params : {
										mode : 'saveRepVacation',
										xml : xml
									},
									success : function(resp, opts) {
										var obj = Ext.util.JSON
												.decode(resp.responseText);

										if (obj.success) {
											Ext.Msg.alert('��ʾ', '�������');

										} else {
											Ext.Msg.alert('��ʾ', '����ʧ�ܣ�ԭ��'
															+ obj.msg);
										}
									},
									failure : function(resp, opts) {
									},
									url : './doCallPhoneAction.jsp'
								});

					}
				}, {
					text : 'ɾ��',
					iconCls : 'del',
					handler : function() {
						var data = grid1.getSelectionModel().getSelections();
						if (data.length <= 0) {
							Ext.Msg.alert("��ʾ", "��ѡ����Ҫɾ�������ã�");
							return;
						}
						var users = "";
						for (var i = 0; i < data.length; i++) {
							var r = data[i];
							if (i > 0)
								users += ",";
							users += r.get('userId');
						}

						Ext.Msg.confirm('��ʾ', '��ȷ��ɾ����Щ�ݼ�', function(btn, text) {
							if (btn == 'yes') {
								Ext.Ajax.request({
											method : 'post',
											params : {
												mode : 'removeRepVacation',
												users : users
											},
											success : function(resp, opts) {
												var obj = Ext.util.JSON
														.decode(resp.responseText);

												if (obj.success) {
													Ext.Msg.alert('��ʾ', 'ɾ�����');
													store1.load();
												} else {
													Ext.Msg.alert('��ʾ',
															'ɾ��ʧ�ܣ�ԭ��'
																	+ obj.msg);
												}
											},
											failure : function(resp, opts) {
												// Ext.Msg.alert("��ʾ","��ע�澯ȡ��ʧ��?");
											},
											url : './doCallPhoneAction.jsp'
										});
							}
						});

					}
				}, {
					text : '�ر�',
					iconCls : 'cannel',
					handler : function() {
						win.close();
					}
				}, {
					text : '����',
					hidden : true
				}],
		cm : cm
	});
	var win = new Ext.Window({
				title : '�Զ�����������ݼ�����',
				id : 'showRepConfigWin',
				width : 600,
				height : 500,
				layout : 'fit',
				items : [grid1],
				modal : true
			});

	win.show();
}
/**
 * �Զ������������
 */
var time_min;
function showCallPhoneParamConfig() {

	if (Ext.getCmp("showCallPhoneParamConfigWin"))
		return;
	var field1 = new Ext.form.NumberField({
				id : 'FAIL_NUM',
				fieldLabel : 'ʧ�ܼ��ν����ע�б�',
				maxValue : 3,
				value : 3
			});
	var field2 = new Ext.form.NumberField({
				id : 'ALARM_NUM',
				fieldLabel : 'һ���Ӳ������ٸ澯�����ע�б�',
				value : 30
			});
	var field3 = new Ext.form.TextField({
				id : 'ALARM_GRADE',
				fieldLabel : '�澯������',
				value : '>=3'
			});

	var field4 = new Ext.form.TextField({
		id : 'callCount',
		fieldLabel : 'ÿ����������'
	});
	time_min = new Ext.form.TextField({
		id : 'time_min',
		fieldLabel : '�뵱ǰʱ����'
	});
	time_run = new Ext.form.TextField({
		id : 'time_run',
		fieldLabel : '���ִ������'
	});
	var field5 = new Ext.form.TextField({
		id : 'field5',
		value:'3:5,10',
		fieldLabel : '�Զ��������'
	});
	
	ctx.get( function (data) { field4.setValue(data) },"concernManager.getCallTotal()"); 

	var fmp = new Ext.form.FormPanel({
		width : 360,
		// region : 'center',
		labelAlign : 'left',
		defaultType : 'textfield',
		frame:true,
		timeout : 10000,
		layout : 'form',
		labelWidth : 190,
		items : [field1, time_min,field2, field3, field4, time_run, field5]
	});

	var win = new Ext.Window({
		title : '�Զ������������',
		id : 'showCallPhoneParamConfigWin',
		width : 360,
		height : 260,
		layout : 'fit',
		items : [fmp],
		buttons : [{
			text : '����',
			iconCls : 'save',
			handler : function() {
				if( !(/^[1-9]+[0-9]*]*$/.test(field4.getValue())) ) {
					Ext.Msg.alert("���棺","��ÿ������������������������!");
					return;
				}
				if( !(/^[1-9]+[0-9]*]*$/.test(time_min.getValue())) ) {
					Ext.Msg.alert("���棺","���뵱ǰʱ������������������!");
					return;
				}
				if( field4.getValue() <= 100 ) {
					Ext.Msg.alert("���棺","��ÿ����������������>100");
					return;
				}

				
				if( field5.getValue()=='' ) {
					Ext.Msg.alert("���棺","���Զ�������ơ�����Ϊ��");
					return;
				}	
				
				// �������
				var num = field5.getValue().split(":")[0];
				if( !(/^[1-9]+[0-9]*]*$/.test(num) ) ) {
					Ext.Msg.alert("���棺","���Զ�������ơ��Զ��������������>0����������");
					return;
				}
				if( num > 5  ) {
					Ext.Msg.alert("���棺","���Զ�������ơ��Զ�����������ܳ���5�Σ�");
					return;
				}
				if( num !=1 && field5.getValue().split(":").length <= 1 ) {
					Ext.Msg.alert("���棺","���Զ�������ơ���ʽ���ԣ�");
					return;
				} else if ( num !=1 ) {
					var times =  field5.getValue().split(":")[1].split(",");
					if( times.length !=  field5.getValue().split(":")[0]-1) {
						Ext.Msg.alert("���棺","���Զ�������ơ�ʱ������������ȷ��");
						return;
					}
					for (var i = 0; i < times.length; i++ ) {
						if ( !(/^[1-9]+[0-9]*]*$/.test(times[i])) ) {
							Ext.Msg.alert("���棺","���Զ�������ơ�ʱ����������>0����������");
							return;
						}
					}
				}

				var obj = Ext.getCmp('ALARM_GRADE');
				if (obj) {
					if (obj.getValue().indexOf('<') >= 0) {
						Ext.Msg.alert('���ĸ澯������д��<���澯��̫�󣬲��ܸ���', '');
						return;

					}

				}
				Ext.Msg.confirm('��ʾ', '��ȷ�ϸ�����Щ������������»�����Ӱ������澯', function(btn,
								text) {
							if (btn == 'yes') {
								Ext.Ajax.request({
											method : 'post',
											params : {
												mode : 'updatePhoneConfig',
												FAIL_NUM : ( Ext.getCmp('FAIL_NUM')? Ext.getCmp('FAIL_NUM').getValue() : ''),
												ALARM_NUM : (Ext.getCmp('ALARM_NUM') ? Ext.getCmp('ALARM_NUM').getValue(): ''),
												ALARM_GRADE : (Ext.getCmp('ALARM_GRADE') ? Ext.getCmp('ALARM_GRADE').getValue() : ''),
												time : time_min.getValue(),
												time_run : time_run.getValue(),
												callCount : field4.getValue(),
												callTime : field5.getValue()
											},
											success : function(resp, opts) {
												var obj = Ext.util.JSON
														.decode(resp.responseText);

												if (obj.success) {
													Ext.Msg.alert('��ʾ', '������');
													win.close();
												} else {
													Ext.Msg.alert('��ʾ',
															'���ʧ�ܣ�ԭ��'
																	+ obj.msg);
												}
											},
											failure : function(resp, opts) {
												// Ext.Msg.alert("��ʾ","��ע�澯ȡ��ʧ��?");
											},
											url : './doCallPhoneAction.jsp'
										});
							}
						});

			}
		}, {
			text : 'ȡ��',
			iconCls : 'cannel',
			handler : function() {
				win.close();
			}
		}],
		modal : false
	});

	win.show();
	
	Ext.get('time_min').on('mouseover',function(e) {
		showMesage('time_min','�����ʱ���뵱ǰʱ��������������ʱ��ʱ(��λ������)����������б����Ƴ��������ע�б�');	
	});
	Ext.get('time_run').on('mouseover',function(e) {
		showMesage('time_run','��λ������');	
	});
	
	Ext.get('field5').on('mouseover',function(e) {
		showMesage('field5','�Զ����������ʱ�������ƣ���ʽ3:5,10(�Զ�������Σ���һ�μ��5���ӣ��ڶ��μ��10����)');	
	});
	
	// ��������
	Ext.Ajax.request({
		method : 'post',
		params : { mode : 'loadPhoneConfig' },
		success : function(resp, opts) {
			var obj = Ext.util.JSON.decode(resp.responseText);
			
			if (obj) {
				time_min.setValue(obj.TIME);
				Ext.getCmp('FAIL_NUM').setValue(obj.FAIL_NUM);
				Ext.getCmp('ALARM_NUM').setValue(obj.ALARM_NUM);
				Ext.getCmp('ALARM_GRADE').setValue(obj.ALARM_GRADE);
				Ext.getCmp('time_run').setValue(obj.TIMERUN);
				Ext.getCmp('field5').setValue(obj.CALL_TIME);
			} 
		},
		failure : function(resp, opts) {},
		url : './doCallPhoneAction.jsp'
	});

}

/**
 * ��ѯ���˴���
 */
var moWin;
var win1;
var field1,field2,field3,field4,field5,field6,field7, field8, field9, field10,comb; 
function showSearch() {
	if (Ext.getCmp("showSearchWin")) return;
	field1 = new Ext.form.TextField({
		id:'key',
		width:345,
		fieldLabel : '�澯����',value:selectTxt
	});
	field2 = new Ext.form.TextField({
		id:'alarmNode',
		width:345,
		readOnly:true, 
		fieldLabel : '����·��'
	});

	field3 = new Ext.form.DateField({
		width:345,
		fieldLabel : '��ʼʱ��',//value: new Date(),
		name:'stime',format:'Y-m-d H:i:s'
	});	

	field4 = new Ext.form.DateField({
		width:345,
		fieldLabel : '����ʱ��',//value: new Date(),
		name:'etime',format:'Y-m-d H:i:s'
	});

	field5 =  new Ext.form.ComboBox({
		width:345,
		triggerAction : "all",
		fieldLabel :'�澯����',
		store : new Ext.data.SimpleStore({
					fields : ['caption', 'value'],
					data : [['δȷ���澯','0'],['����澯', '1'], ['һ��澯', '2'], ['��Ҫ�澯', '3'], ['���ظ澯', '4']]
				}),
		displayField : 'caption',
		valueField : 'value',
		value:0,
		typeAhead : true,
		mode : 'local',
		forceSelection : true,
		selectOnFocus : true
	});	

	var field6store = new Ext.data.Store({
		url: "getAlarmSource.jsp",
		reader: new Ext.data.JsonReader({ root: "data" }, [ { name: 'id' }, { name: 'name' }])
	});

	field6 = new Ext.form.ComboBox({
		width:345,
		triggerAction : "all",
		fieldLabel :'�澯��Դ',
		store : field6store,
		displayField : 'name',
		valueField : 'id',
		mode : 'remote'
	});	

	field7 = new Ext.form.TextField({
		width:345,
		fieldLabel : '�澯����'
	});
		

	field8 = new Ext.form.TextField({
		width:345,
		readOnly:true,
		id:'searchCaller',	
		fieldLabel : '�� �� ��'
	});


	field9 = new Ext.form.Hidden({ id:'moid',value:'' });
	field10 = new Ext.form.Hidden({ id:'user_id',value:'' });

	comb =  new Ext.form.ComboBox({
		id:'isShowComb',
		width:345,
		triggerAction : "all",
		fieldLabel :'�������',
		store : new Ext.data.SimpleStore({
			fields : ['caption', 'value'],
			data : [['����','-1'],['��ǰ', '1'], ['��ʷ', '0']]
		}),
		displayField : 'caption',
		valueField : 'value',
		value:isShow,
		typeAhead : true,
		mode : 'local',
		forceSelection : true,
		selectOnFocus : true
	});		
			
			
	var fmp = new Ext.form.FormPanel({
		width : 450,
		labelAlign : 'left',
		defaultType : 'textfield',
		timeout : 10000,
		layout : 'form',
		border:true,
		labelWidth : 80,
		items : [field2,field3,field4,field5,field6,field7,field1,field8,comb,field9,field10]
	});

	win1 = new Ext.Window({
		title : '�Զ�����澯��ѯ',
		id : 'showSearchWin',
		width : 450,
		height : 320,
		layout : 'fit',
		items : [fmp],
		buttonAlign:'center',
		buttons : [{
			text : '��ѯ',
			iconCls : 'search',handler:function(){
				isShow = (Ext.getCmp('isShowComb')?Ext.getCmp('isShowComb').getValue():'0'),
				selectTxt = (Ext.getCmp('key')?Ext.getCmp('key').getValue():'');
				if (callstore1) callstore1.load({ params : { start : 0 } });
				win1.close();
			}
		}, {
			text : 'ȡ��',
			iconCls : 'cannel',
			handler : function() { 
				field1.setValue('');
				field2.setValue('');
				field3.setValue('');
				field4.setValue('');
				field5.setValue(0);
				field6.setValue('');
				field7.setValue('');
				field8.setValue(''); 
				field9.setValue(''); 
				field10.setValue('');
				comb.setValue(1);
				win1.close(); 
			}
		}],
		modal : true
	});
	win1.show();
	
	Ext.get('alarmNode').on('click',function(){	
		if ( !moWin) {	
			moWin = new Ext.Window({
					title:"�ڵ�ѡ��",
					height:300,	
					width:350,
					buttonAlign: 'center',
					header : true,
					buttons:[
						{text : '���',handler : function() { setNode('','') }},
						{text : 'ȡ��',handler : function() { moWin.hide(); }}					
					],
					html:"<iframe src='../motree_new.jsp?flag=setNode' borderframe=0 style='width:100%;height:100%'></iframe>"
			});
			moWin.setPosition(Ext.get('alarmNode').getLeft(),Ext.get('alarmNode').getTop()+23);
		}
		moWin.show();		
	});	
	Ext.get('alarmNode').on('mousedown',function( e ){ 
		if(e.button == 2) {
			field2.setValue('');
			field9.setValue('');
		}
	});
	
	Ext.get('searchCaller').on('click',function(){ searchCaller( field8, field10, 10 ); });
	Ext.get('searchCaller').on('mousedown',function( e ){ 
		if(e.button == 2) {
			field8.setValue('');
			field10.setValue('');
		}
	});
}

/**
 * ������
 */
var FilterRecord = Ext.data.Record.create([{
			name : 'moid'
		}, {
			name : 'ischild'
		}, {
			name : 'timelen'
		}, {
			name : 'isuse'
		}, {
			name : 'reper'
		}]);

var ReperRecord = Ext.data.Record.create([{
			name : 'userId'
		}, {
			name : 'userName'
		}, {
			name : 'stat'
		}, {
			name : 'startTime'
		}, {
			name : 'isuse'
		}, {
			name : 'endTime'
		}]);
/**
 * ��ӹ�������
 * 
 */
function addConfig(moid, caption, namingPath, path, flag) {
	if ( flag == 'TrackPath' ) {
		// ���ٸ澯����
		trackPath (moid, caption, namingPath);
	} else if ( flag == 'AutoCall' ) {
		// �Զ������������
		autoCall(moid, caption, path);
	} else if ( flag == 'setNode' ) {
		setNode(moid, namingPath);
	}
}


function autoCall(moid, caption, path) {
	var filter = new FilterRecord({
				pathName : caption,
				moid : moid,
				ischild : 1,
				timelen : '',
				isuse : 1,
				forPara : 1,
				reper : ''

			});
	var count = filterStore.getCount();

	for (var i = 0; i < count; i++) {
		var r = filterStore.getAt(i);
		
		if (r.get('path') == moid) {
		alert(r.get('path') + "  " + moid + "  " + i);
			return;
		}
	}

	Ext.Ajax.request({
		method : 'post',
		params : {
			mode : 'checkpath',
			moid : moid
		},
		success : function(resp, opts) {
			var obj = Ext.util.JSON.decode(resp.responseText);
			if (!obj.exist) {
				filterStore.addSorted(filter);
			}
		},
		failure : function(resp, opts) {
			Ext.Msg.alert("��ʾ", "ʧ��");
		},
		url : './doCallPhoneAction.jsp'
	});
}

MsgTip = function() {
	var ishow = false;
	var msgCt;
	function isShow() {
		return this.ishow;
	}

	function createBox(t, s) {
		return [
				'<div class="msg">',
				'<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
				'<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc" style="font-size=12px;"><h3>',
				t,
				'</h3>',
				s,
				'</div></div></div>',
				'<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
				'</div>'].join('');
	}
	return {
		msg : function(title, message, autoHide, pauseTime) {
			if (!msgCt) {
				msgCt = Ext.DomHelper.insertFirst(document.body, {
					id : 'msg-div22',
					style : 'position:absolute;top:10px;width:300px;margin:0 auto;z-index:20000;'
				}, true);
			}
			msgCt.alignTo(document, 't-t');
			// ����Ϣ�����½�����һ���رհ�ť
			message += '<br><span style="text-align:right;font-size:12px; width:100%;">'
					+ '<font color="blank"><u style="cursor:hand;" onclick="MsgTip.hide(this);">�ر�</u></font></span>'
			var m = Ext.DomHelper.append(msgCt, {
						html : createBox(title, message)
					}, true);
			m.slideIn('t');
			if (!Ext.isEmpty(autoHide) && autoHide == true) {
				if (Ext.isEmpty(pauseTime)) {
					pauseTime = 5;
				}
				m.pause(pauseTime).ghost("tr", {
							remove : true
						});
			}
			MsgTip.ishow = true;
		},
		hide : function(v) {

			MsgTip.ishow = false;
			var msg = Ext
					.get(v.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement);
			msg.ghost("tr", {
						remove : true
					});
		}
	};
}();

function checkTime(s) {
	var returnStr = "";
	if (s == '')
		return "";
	if (s.indexOf('~') > 0) {
		var ts = s.split('~');
		for (var i = 0; i < ts.length; i++) {
			if (strDateTime(ts[i]) == false)
				return "����д��ʱ���ʽ�����⣬��ȷ�ĸ�ʽΪ2012-01-01 00:00:01~2012-12-31 23:59:59";
		}
	} else {

	}
	return "";
}

function strDateTime(str) {
	if (str.lenth == 10)
		str += " 00:00:00";
	var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/;
	var r = str.match(reg);
	if (r == null)
		return false;
	var d = new Date(r[1], r[3] - 1, r[4], r[5], r[6], r[7]);
	return (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3]
			&& d.getDate() == r[4] && d.getHours() == r[5]
			&& d.getMinutes() == r[6] && d.getSeconds() == r[7]);
}

function setNode(dotid,caption){
	field2.setValue( caption );
	field9.setValue( dotid==-1 ? "": dotid);
	moWin.hide();
}	

/**
 * ȡ��cas�û���Ϣ
 * nameField Ext.Form.TextField �û���չʾ����
 * idField Ext.Form.TextField/ Ext.Form.Hidden ��IDչʾ����
 * limit ÿҳ��ʾ����
 */
function searchCaller ( nameField, idField, limit ) {
	var col = new Ext.grid.ColumnModel([
		new Ext.grid.RowNumberer(),//�Զ��к�
		{header:"�˺�",dataIndex:"pno",width:80},
		{header:"����",dataIndex:"pnm",width:130}
	]);
	var store = new Ext.data.Store({
		proxy:  new Ext.data.HttpProxy({url:'user.jsp?condition=' }),
		reader: new Ext.data.JsonReader({ totalProperty: 'totalProperty', root: 'root' },[ {name: 'pno'}, {name: 'pnm'} ])
	}); 
	var bar = new Ext.PagingToolbar({//��ҳ����
			pageSize: limit*1,  //ÿҳ��ʾ��������
			store: store, 
			displayInfo: true,
			beforePageText : "��",
			displayMsg: 'һ�� {2} ��'
	});
	var grid = new Ext.grid.GridPanel({
		id:'gridPanel',
		autoScroll:true,
		cm:col,
		store:store,
		disableSelection:true,
		viewConfig: { autoFill: true },
		bbar: bar,
		tbar:["-", {text : '���',handler : function() {
					nameField.setValue( '' );
					idField.setValue( '' );
					win.close();
				}
			}, '-',"��ѯ����:<input type='text' value='' id='conText' style='width:150'>",
			{text : '��ѯ',handler : function() { 
					var condition = document.getElementById('conText').value;
					var url = "user.jsp?condition=" + condition ;		
					store.proxy = new Ext.data.HttpProxy({url:url});
					store.load({params:{start:0,limit:limit}});				
				}
			},'-'
		]
	});	
	store.load( {params:{start:0,limit:limit}} );
	
	grid.on("rowdblclick", function ( g , rowIndex , e ) {
		nameField.setValue( grid.getStore().getAt(rowIndex).get('pnm') );
		idField.setValue( grid.getStore().getAt(rowIndex).get('pno') );
		win.close();
	});
	
	var win = new Ext.Window({
		title:"�û���Ϣ��ѯ",
		height:300,	
		width:350,
		layout:'fit',
		modal : false,
		items:[grid]
	});
	win.show();
}
	

/* ��ʾָ��ID�������ʾ��Ϣ */
function showMesage (cid, message) {
	Ext.QuickTips.register({
        target: cid,   
        text: "<font color='green'>" + message + "</font>",   
        maxWidth: 100,      //���ú���ʾ���ݿ����Զ�����   
        dismissDelay:20000, //������ʾʱ��                               
        trackMouse: true,   
        autoHide: true,   
        animate: true  
    });
}

function callReport () {
   var win = new Ext.Window({
		title:"�����������ͳ��",
		height:400,	
		width:550,
		layout:'fit',
		modal : true,
		html:"<iframe width='100%' height='100%' src='report.jsp'></iframe>"
	});
	win.show();
}	
		
function dump(obj) {
	m = window
			.open(
					'',
					'dump',
					'toolbar=no,directories=no,menubar=no,location=no,scrollbars=yes,resizable=yes,status=no,copyhistory=no,top=300,left=370,width=300,height=150');
	m.document.writeln("<table border=1>");
	for (i in obj) {
		m.document
				.writeln("<tr><td>" + i + "</td><td>" + obj[i] + "</td></tr>");
	}
	m.document.writeln("</table>");
}

var writeFeild ;
function receiveInTime(a){
	 if (writeFeild==null) return;
	 writeFeild.setValue(a);
}
var msgstr = "ʱ������ʽ���£�<br>";
msgstr += "1����������κ�ʱ��<br>";
msgstr += "2��ͨ�õı��ʽ����0 0 9-23 * * ?����ÿ��9����23��<br>";
msgstr += "3��ʱ��Σ���2012-01-01~2012-01-31֧����ϸ��ʱ����<br>";




/** �������״̬ */
function setState ( v ) {
	if (v == '0') {
		return "�������";
	} else if (v == '1') {
		return '����ɹ�';
	} else if (v == '-1') {
		return '���ʧ��';
	} else {
		return v;
	}
}

/** ���ø澯���� */
function setAlarmGradre ( v ) {
	if ( v != "") {
    	 return "<img src='/sysmgr/images/icons/grade" + v + ".gif'>" + v;
	} else {
    	return v;
	}
}

/** ɾ�������¼ */
function deleteCall ( grid1 ) {
	var data = grid1.getSelectionModel().getSelections();
	if (data.length <= 0) {
		Ext.Msg.alert("��ʾ", "��ѡ����Ҫ����ĸ澯��");
		return;
	}
	var alarmIds = '';
	var count = 0;
	for (var i = 0; i < data.length; i++) {
		var r = data[i];
		// ��Ȩ��zangzw��wub ��Ȩɾ�����״̬Ϊ������С��������¼
		if ( loginUserId != 'zangzw' && loginUserId != 'wub' && loginUserId != 'root' ) {
			if (r.get('stat') != 1) continue;
		}
		if ( count > 0 ) { alarmIds += "," };
		alarmIds += r.get('id');
		count++;
	}
	
	if ( alarmIds == '' ) { 
		Ext.Msg.alert("��ʾ", "û����Ҫ������Զ������¼!");
		return;
	}
	Ext.Msg.confirm('��ʾ', '���Ƿ�ȷ�ϲ���Ҫ��ע��Щ�����¼?', function(btn, text) {
		if (btn == 'yes') {
			hideCallPhone(alarmIds);
		}
	});
}

/** �����ɫ���ô��� */
function callUserConfig ( )  {
	
	if (Ext.getCmp("callUserConfigWin")) { return; }

	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([sm,
		{ header : 'rId', dataIndex : 'rId', width : 80 },
		{ header : '�����ԱA', dataIndex : 'userA', width : 80 }, 
		{ header : '�����ԱB', dataIndex : 'userB', width : 80 }, 
		{ header : '�����ԱC', dataIndex : 'userC', width : 80 }, 
		{ header : '������Ա', dataIndex : 'userOP', width : 80 }, 
		{ header : '����ʱ��', dataIndex : 'timeOP', width : 120 }
	]);

	var store = new Ext.data.Store({
		url : "./doCallPhoneAction.jsp",
		reader : new Ext.data.JsonReader(
			{ root : "data", totalProperty : "count" }, 
			[ { name:'rId' }, { name:'userA' }, { name:'userB' }, { name:'userC' }, { name:'userOP' }, { name:'timeOP' }]
		)
	});

	store.on('beforeload', function() {
		var condition = document.getElementById('uconfig');
		Ext.apply(this.baseParams, {
			mode : 'callUserConfig',
			limit : 20,
			condition:condition ? condition.value : ''
		});
	});
	
	store.load({ params : { start : 0,limit : 20 } });

	var grid = new Ext.grid.EditorGridPanel({
		sm : sm,		
		cm : cm,
		store : store,
		region : 'center',
		stripeRows : true,// ������Ч��
		loadMask : true,
		clicksToEdit : 1,
		tbar : [ 		
				'-', '<input type=text id=uconfig width=150>', 
				{ text : '��ѯ', iconCls : 'search', handler:function() { store.load(); } },
				'-',{ text : '����', iconCls : 'add', handler : function() { addUserConfig(store); } }, 
				'-', { text : '�޸�', iconCls : 'modify', handler : function() { modifyCallUserInfo(store, grid); } },  
				'-', { text : 'ɾ��', iconCls : 'del', handler : function() { delUserConfig(store, grid);} }, 	
				'-', { text : '�������', iconCls : 'manager', handler : function() { addUserTimeConfig();} }, 			
				'-', { text : '�ر�', iconCls : 'cannel', handler : function() { win.close(); } },'-'
		],
		bbar : new Ext.PagingToolbar({
			pageSize : 20,
			store : store,
			displayInfo : true,
			displayMsg : ' ��ʾ��{0}����{1}����¼,һ��{2}��',
			emptyMsg : "û�м�¼"
		})
	});
	var win = new Ext.Window({
		title : '�����ɫ����',
		id : 'callUserConfigWin',
		width : 560,
		height : 400,
		layout : 'border',
		items : [ grid ],
		modal : true
	});

	win.show();
}

/** ��������ɫ���� */
function addUserConfig( store ) {

	if (Ext.getCmp("addUserConfigWin")) { return; }
	
	var f_userA = new Ext.form.TextField({ id:'f_userA', fieldLabel:'��һ�����', width:160 });
	var f_userB = new Ext.form.TextField({ id:'f_userB', fieldLabel:'�ڶ������', width:160 });
	var f_userC = new Ext.form.TextField({ id:'f_userC', fieldLabel:'���������', width:160 });
	
	var valueA = new Ext.form.Hidden({ id:'valueA' });
	var valueB = new Ext.form.Hidden({ id:'valueB' });
	var valueC = new Ext.form.Hidden({ id:'valueC' });
	
	var fmp = new Ext.form.FormPanel({
		width:300,
		labelAlign:'center',
		defaultType:'textfield',
		frame:true,
		timeout:10000,
		layout:'form',
		labelWidth:80,
		items:[ f_userA, f_userB,f_userC ]
	});

	var win = new Ext.Window({
		title:'�����ɫ����',
		id:'addUserConfigWin',
		width:300,
		height:200,
		layout:'fit',
		items:[fmp],
		modal:true,
		buttons:[
			{ text : '����', iconCls : 'save', handler : function() {
					ctx.get( function (data) { 
						if ( data == true ) { 
							Ext.Msg.alert("���棺","�û�" + f_userA.getValue() + "�Ѵ��ڡ�");
							return;
						}
						Ext.Ajax.request({
							method : 'post',
							params : {
								mode : 'saveCallUserInfo',
								f_userA : valueA.getValue() + "=" + f_userA.getValue(),
								f_userB : valueB.getValue() + "=" + f_userB.getValue(),
								f_userC : valueC.getValue() + "=" + f_userC.getValue()
							},
							success : function(resp, opts) {
								var obj = Ext.util.JSON.decode(resp.responseText);

								if (obj.success) {
									Ext.Msg.alert('��ʾ', '����ɹ���');
									win.close();
									store.reload();
								} else {
									Ext.Msg.alert( '��ʾ','ERROR:' + obj.msg );
								}
							},
							failure : function(resp, opts) {},
							url : './doCallPhoneAction.jsp'
						});
					
					},"callUserManager.isUserExist( \"" +  valueA.getValue() + "\")"); 
					
				}
			}, {
				text : '�ر�',
				iconCls : 'cannel',
				handler : function() { win.close(); }
			}
		]
	});

	win.show();
	
	Ext.get('f_userA').on('click',function(){ searchCaller( f_userA, valueA, 10 ); });
	Ext.get('f_userB').on('click',function(){ searchCaller( f_userB, valueB, 10 ); });
	Ext.get('f_userC').on('click',function(){ searchCaller( f_userC, valueC, 10 ); });

}


/** ���ʱ�������� */
function addUserTimeConfig() {

	if (Ext.getCmp("addUserTimeConfig")) { return; }
	
	var userTime = new Ext.form.TextField({ id:'userTime', fieldLabel:'���ʱ����', width:160 });
	
	var fmp = new Ext.form.FormPanel({
		width:300,
		labelAlign:'center',
		defaultType:'textfield',
		frame:true,
		timeout:10000,
		layout:'form',
		labelWidth:100,
		items:[ userTime ]
	});

	var win = new Ext.Window({
		title:'���ʱ��������',
		id:'addUserTimeConfig',
		width:300,
		height:100,
		layout:'fit',
		items:[fmp],
		modal:false,
		buttons:[
			{ text : '����', iconCls : 'save', handler : function() {
					ctx.get( function (data) { 
						if ( data > 0 ) { 
							Ext.Msg.alert("��ʾ��","�����Ѿ��޸ģ�");
							win.close();
						} else {
							Ext.Msg.alert("��ʾ��","<font color='red'>�����޸�ʧ�ܣ�</font>");
						}
					},"callUserManager.setUserTime(" +  userTime.getValue() + ")"); 
					
				}
			}, {
				text : '�ر�',
				iconCls : 'cannel',
				handler : function() { win.close(); }
			}
		]
	});
					
	win.show();
	
	// ��ʼ��
	ctx.get( function (data) { userTime.setValue(data); },"callUserManager.getUserTime()"); 
}

/** ɾ�������ɫ���� */
function delUserConfig ( store, grid ) {

	var data = grid.getSelectionModel().getSelections();
	if ( data.length <= 0 ) {
		Ext.Msg.alert("��ʾ", "��ѡ����Ҫɾ��������!");
		return;
	}
	
	var userAs = "";
	for (var i = 0; i < data.length; i++) {
	
		if (i > 0) { userAs += ";"; }
		
		userAs += data[i].get('rId');
	}
	Ext.Msg.confirm('��ʾ', '��ȷ��ɾ����Щ������', function(btn, text) {
		if (btn != 'yes') { return; }
		Ext.Ajax.request({
			method : 'post',
			params : { mode : 'deleteCallUserInfo', userAs : userAs },
			success : function(resp, opts) {
				var obj = Ext.util.JSON.decode(resp.responseText);

				if (obj.success) {
					Ext.Msg.alert('��ʾ', 'ɾ���ɹ���');
					store.load();
				} else {
					Ext.Msg.alert( '��ʾ','ERROR:' + obj.msg );
				}
			},
			failure : function(resp, opts) {},
			url : './doCallPhoneAction.jsp'
		});
	});
}

/** �޸������ɫ���� */
function modifyCallUserInfo ( store, grid ) {

	var data = grid.getSelectionModel().getSelections();
	if ( data.length != 1 ) {
		Ext.Msg.alert("��ʾ", "��ѡ����ѡ��һ�����ü�¼!");
		return;
	}
	
	var userA = data[0].get('rId');

	if (Ext.getCmp("addUserConfigWin")) { return; }
	
	var f_userA = new Ext.form.TextField({ id:'f_userA', fieldLabel:'��һ�����', width:160, disabled:true });
	var f_userB = new Ext.form.TextField({ id:'f_userB', fieldLabel:'�ڶ������', width:160 });
	var f_userC = new Ext.form.TextField({ id:'f_userC', fieldLabel:'���������', width:160 });
	
	var valueA = new Ext.form.Hidden({ id:'valueA',value: userA});
	var valueB = new Ext.form.Hidden({ id:'valueB' });
	var valueC = new Ext.form.Hidden({ id:'valueC' });
	
	var fmp = new Ext.form.FormPanel({
		width:300,
		labelAlign:'center',
		defaultType:'textfield',
		frame:true,
		timeout:10000,
		layout:'form',
		labelWidth:80,
		items:[ f_userA, f_userB,f_userC ]
	});
	
	var win = new Ext.Window({
		title:'�����ɫ����',
		id:'addUserConfigWin',
		width:300,
		height:200,
		layout:'fit',
		items:[fmp],
		modal:true,
		buttons:[
			{ text : '����', iconCls : 'save', handler : function() {
					Ext.Msg.confirm('��ʾ', '��ȷ���޸����������', function(btn, text) {
						if (btn != 'yes') { return; }
						Ext.Ajax.request({
							method : 'post',
							params : {
								mode : 'modifyCallUserInfo',
								f_userA : valueA.getValue(),
								f_userB : valueB.getValue() + "=" + f_userB.getValue(),
								f_userC : valueC.getValue() + "=" + f_userC.getValue()
							},
							success : function(resp, opts) {
								var obj = Ext.util.JSON.decode(resp.responseText);

								if (obj.success) {
									Ext.Msg.alert('��ʾ', '����ɹ���');
									win.close();
									store.reload();
								} else {
									Ext.Msg.alert( '��ʾ','ERROR:' + obj.msg );
								}
							},
							failure : function(resp, opts) {},
							url : './doCallPhoneAction.jsp'
						});
					});
				}
			}, {
				text : 'ȡ��',
				iconCls : 'cannel',
				handler : function() { win.close(); }
			}
		]
	});

	win.show();

	Ext.get('f_userB').on('click',function(){ searchCaller( f_userB, valueB, 10 ); });
	Ext.get('f_userC').on('click',function(){ searchCaller( f_userC, valueC, 10 ); });
	
	// ��������
	Ext.Ajax.request({
		method:'post',
		params:{ mode:'loadCallUserInfo', f_userA:userA},
		success:function(resp, opts) {
			var obj = Ext.util.JSON.decode(resp.responseText);	
			if (obj) {
				f_userA.setValue(obj.f_userA);
				f_userB.setValue(obj.f_userB == null ? '':obj.f_userB);
				f_userC.setValue(obj.f_userC);
				valueB.setValue(obj.valueB);
				valueC.setValue(obj.valueC);
			} 
		},
		failure:function(resp, opts) {},
		url:'./doCallPhoneAction.jsp'
	});
}



function showKeyWords () {
	window.open ('./callKeyWordMain.jsp','newwindow','height=400,width=800,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no'); 
}

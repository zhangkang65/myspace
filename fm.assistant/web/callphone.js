/*
 * 显示自动外呼
 */
var callstore1 = null;
var selectTxt = "";
var isShow = 1;
var reportParam;

function showAllCallPhone() {
	
	var sm = new Ext.grid.CheckboxSelectionModel();

	var cm = new Ext.grid.ColumnModel([sm,
		{ header : 'ID', dataIndex : 'id', width : 50, hidden : true },
		{ header : '外呼ID', dataIndex : 'callID', width : 50, hidden : true }, 
		{ header : '外呼人', dataIndex : 'caller', width : 80 }, 
		{ header : '外呼状态', dataIndex : 'stat', renderer:setState, width : 80 }, 
		{ header : '外呼时间', sortable: true, dataIndex : 'ctime', width : 120 }, 
		{ header : '呼叫次数', dataIndex : 'num', align:'center', width : 60 }, 
		{ header : '级别', dataIndex : 'grad', width : 40,renderer:setAlarmGradre },
		{ header : '工单', dataIndex : 'workid' }, 
		{ header : '路径', dataIndex : 'path', width : 250 }, 
		{ header : '第一次时间', dataIndex : 'fdate', width : 120 },
		{ header : '最后一次时间', sortable: true, dataIndex : 'ldate', width : 120 }, 
		{ header : '次数', dataIndex : 'count', width : 40 }, 
		{ header : '告警内容', dataIndex : 'content', width : 250 }
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

		// 报表导出时使用。
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
			stripeRows : true,// 斑马线效果
			loadMask : true,
			store : store1,
			cm : cm,
			sm : sm,
			tbar : [
				{ text : '清除告警', iconCls : 'del', handler : function () { deleteCall ( grid1 ); }}, '-', 
				{ text : '告警查询', iconCls : 'search', handler: showSearch }, '-', 
				{ text : '查看报表', iconCls : 'search', handler : callReport }, '-',
				{ text : '规则设定', iconCls : 'swp', handler : showCallPhoneFilterConfig }, '-', 
				{ text : '参数设置', iconCls : 'swp', hidden:(isCfg!=1), handler :  showCallPhoneParamConfig }, '-', 
				{ text : '关键字设置', iconCls : 'swp', hidden:(isCfg!=1), handler :  showKeyWords }, '-', 
				{ text : '休假设置', iconCls : 'manager', hidden:(isCfg!=1), handler :  showRepConfig }, '-', 
				{ text : '外呼角色', iconCls : 'manager', hidden:(isCfg!=1), handler :  callUserConfig }, '-', 
				{ text : '导出报表', iconCls : 'xls', handler : function() {
					window.location="./doCallPhoneAction.jsp?mode=doReport" + reportParam;
				} }, '-' ],
			bbar : new Ext.PagingToolbar({
				pageSize : 20,
				store : store1,
				displayInfo : true,
				displayMsg : ' 显示第{0}条到{1}条记录,一共{2}条',
				emptyMsg : "没有记录"
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
 * 隐藏外呼记录
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
				//Ext.Msg.alert('提示', '告警清除完成，列表即将刷新' );
				alert('告警清除完成，列表即将刷新' );
				callstore1.load();
			} else {
				//Ext.Msg.alert('提示', '保存失败，原因：' + obj.msg);
				alert('保存失败，原因：' + obj.msg);
			}
		},
		failure : function(resp, opts) {},
		url : './doCallPhoneAction.jsp'
	});
}
/**
 * 外呼过滤器设置
 */
var filterStore; // 过滤器集合
var isStop = false;
function showCallPhoneFilterConfig() {
	if (Ext.getCmp("allCallPhoneConfigWin"))
		return;
	var treePanel = new Ext.Panel({
		title : '配置树',
		width : 250,
		split : true,
		region : 'west',
		html : '<iframe style="width:100%;height:100%" src="../motree_new.jsp?flag=AutoCall" frameborder=0></iframe>'
	});
	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([sm,
		{
				header : '路径',
				dataIndex : 'pathName',
				width : 150
			}, {
				header : '是否包含子对象',
				dataIndex : 'ischild',
				width : 80,
				editor : new Ext.form.ComboBox({
							triggerAction : "all",
							store : new Ext.data.SimpleStore({
										fields : ['caption', 'value'],
										data : [['是', '1'], ['否', '0']]
									}),
							displayField : 'caption',
							valueField : 'value',
							typeAhead : true,
							mode : 'local',
							forceSelection : true,
							selectOnFocus : true
						}),
				renderer : function(a) {
					return (a == '1' ? '是' : '否');
				}
			}, {
				header : '时间间隔',
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
					return (a == '' ? '全天' : a);
				},
				width : 150
			}, {
				header : '关键字',
				dataIndex : 'key',
				editor : new Ext.form.TextField({}),
				width : 100
			}, {
				header : '是否启用',
				dataIndex : 'isuse',
				width : 80,
				editor : new Ext.form.ComboBox({
					triggerAction : "all",
					store : new Ext.data.SimpleStore({
								fields : ['caption', 'value'],
								data : [['启动', '1'], ['暂停', '0'], ['禁止', '-1']] }),
					displayField : 'caption',
					valueField : 'value',
					typeAhead : true,
					mode : 'local',
					forceSelection : true,
					selectOnFocus : true
				}),
				renderer : function(a) {
					if( a == 1 ) { return "启动"; }
					if( a == 0 ) { return "暂停"; }
					if( a == -1 ) { return "禁止";}	
					return a;					
				}
			}, {
				header : '参数配置',
				dataIndex : 'forPara',
				width : 80,
				editor : new Ext.form.ComboBox({
					triggerAction : "all",
					store : new Ext.data.SimpleStore({
						fields : ['caption', 'value'],
						data : [['启用', '1'],['禁用', '0']]
					}),
					displayField : 'caption',
					valueField : 'value',
					typeAhead : true,
					mode : 'local',
					forceSelection : true,
					selectOnFocus : true
				}),
				renderer : function(a) {
					return (a == '1' ? '启用' : '禁用');
				}
			}, {
				header : '当前责任人',
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
		title : '过滤器清单',
		region : 'center',
		stripeRows : true,// 斑马线效果
		sm : sm,
		loadMask : true,
		store : store1,
		clicksToEdit : 1,
		tbar : ['-', {
			text : '保存设置',
			iconCls : 'save',
			handler : function() {
				var data = grid1.getSelectionModel().getSelections();
				if (data.length <= 0) {
					Ext.Msg.alert("提示", "请选择需要更新的设置？");
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
							Ext.Msg.alert('提示', '保存完成');

						} else {
							Ext.Msg.alert('提示', '保存失败，原因：' + obj.msg);
						}
					},
					failure : function(resp, opts) {
						// Ext.Msg.alert("提示","关注告警取消失败?");
					},
					url : './doCallPhoneAction.jsp'
				});

			}
		}, {
			text : '导入',
			hidden : true,
			iconCls : 'xls'
		}, {
			text : '导出',
			hidden : true,
			iconCls : 'xls'
		}, {
			text : '一键暂停',
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
									Ext.Msg.alert('提示', '已经全部暂停');
									store1.load();
									var obj = Ext.getCmp('stopBtn');
									if (obj)
										obj.enable();
									if (isStop) {
										obj.setText('一键启用');
									} else {
										obj.setText('一键暂停');
									}
								} else {
									Ext.Msg.alert('提示', '保存失败，原因：' + obj.msg);
								}
							},
							failure : function(resp, opts) {
								// Ext.Msg.alert("提示","关注告警取消失败?");
							},
							url : './doCallPhoneAction.jsp'
						});
			}
		}, {
			text : '删除',
			iconCls : 'del',
			handler : function() {
				var data = grid1.getSelectionModel().getSelections();
				if (data.length <= 0) {
					Ext.Msg.alert("提示", "请选择需要删除的设置？");
					return;
				}
				var moids = "";
				for (var i = 0; i < data.length; i++) {
					if (i > 0)
						moids += ',';
					var r = data[i];
					moids += r.get('moid');
				}
				Ext.Msg.confirm('提示', '您确认删除这些过滤器', function(btn, text) {
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
													Ext.Msg.alert('提示',
															'保存失败，原因：'
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
			text : '关闭',
			iconCls : 'cannel',
			handler : function() {
				win.close();
			}
		}, '->', {
			text : '帮助',
			iconCls : 'help',
			hidden:true,
			handler : function() {
				if (MsgTip.ishow)
					return;
				MsgTip.msg('帮助', msgstr);
			}
		}],
		cm : cm,
		bbar : new Ext.PagingToolbar({
					pageSize : 20,
					store : store1,
					displayInfo : true,
					displayMsg : ' 显示第{0}条到{1}条记录,一共{2}条',
					emptyMsg : "没有记录"
				})
	});
	var win = new Ext.Window({
				title : '自动外呼过滤设置',
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
 * 责任人休假设置
 */
function showRepConfig() {
	if (Ext.getCmp("showRepConfigWin"))
		return;
	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([sm,

	{
				header : '责任人',
				dataIndex : 'userName',
				width : 100
			}, {
				header : '状态',
				dataIndex : 'stat',
				hidden : true,
				width : 80,
				renderer : function(a) {
					return (a == 'true' ? '正常' : '禁用');
				}
			}, {
				header : '开始日期',
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
				header : '截止日期',
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
				header : '是否启用',
				dataIndex : 'isuse',
				width : 80,
				editor : new Ext.form.ComboBox({
							triggerAction : "all",
							store : new Ext.data.SimpleStore({
										fields : ['caption', 'value'],
										data : [['启动', '1'], ['禁止', '-1']]
									}),
							displayField : 'caption',
							valueField : 'value',
							typeAhead : true,
							mode : 'local',
							forceSelection : true,
							selectOnFocus : true
						}),
				renderer : function(a) {
					return (a == '1' ? '启用' : '禁用');
				}
			}]);

	var store1 = new Ext.data.Store({
				url : "./doCallPhoneAction.jsp",
				reader : new Ext.data.JsonReader({
							root : "data",
							// 转向添加路径
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
		stripeRows : true,// 斑马线效果
		sm : sm,
		loadMask : true,
		store : store1,
		clicksToEdit : 1,
		tbar : ['-', '选择责任人', new Ext.form.ComboBox({
							triggerAction : "all",
							anchor : '90%',
							id : 'reperList',
							width : 150,
							mode : 'remote',
							emptyText : '请选择...', // 没有默认值时,显示的字符串
							store : new Ext.data.JsonStore({ // 填充的数据
								url : "./doCallPhoneAction.jsp?mode=replist",
								fields : new Ext.data.Record.create(['text',
										'value']), // 也可直接为["text","value"]
								root : 'datalist'
							}),
							valueField : 'value', // 传送的值
							displayField : 'text' // UI列表显示的文本

						}), {
					text : '添加',
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
								Ext.Msg.alert('提示', '您选择的人员已经在休假中');
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
					text : '保存设置',
					iconCls : 'save',
					handler : function() {

						var data = grid1.getSelectionModel().getSelections();
						if (data.length <= 0) {
							Ext.Msg.alert("提示", "请选择需要更新的设置？");
							return;
						}
						var xml = "<datas>";
						for (var i = 0; i < data.length; i++) {
							var r = data[i];
							if (r.get('startTime') == ''
									|| r.get('endTime') == '') {
								Ext.Msg.alert('提示', '休假需要设定起止时间');
								return;
							}
							var st = new Date(r.get('startTime')).getTime();
							var et = new Date(r.get('endTime')).getTime();
							if (et <= st) {
								Ext.Msg.alert('提示', r.get('userName')
												+ '休假截止时间大于开始时间');
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
											Ext.Msg.alert('提示', '保存完成');

										} else {
											Ext.Msg.alert('提示', '保存失败，原因：'
															+ obj.msg);
										}
									},
									failure : function(resp, opts) {
									},
									url : './doCallPhoneAction.jsp'
								});

					}
				}, {
					text : '删除',
					iconCls : 'del',
					handler : function() {
						var data = grid1.getSelectionModel().getSelections();
						if (data.length <= 0) {
							Ext.Msg.alert("提示", "请选择需要删除的设置？");
							return;
						}
						var users = "";
						for (var i = 0; i < data.length; i++) {
							var r = data[i];
							if (i > 0)
								users += ",";
							users += r.get('userId');
						}

						Ext.Msg.confirm('提示', '您确认删除这些休假', function(btn, text) {
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
													Ext.Msg.alert('提示', '删除完成');
													store1.load();
												} else {
													Ext.Msg.alert('提示',
															'删除失败，原因：'
																	+ obj.msg);
												}
											},
											failure : function(resp, opts) {
												// Ext.Msg.alert("提示","关注告警取消失败?");
											},
											url : './doCallPhoneAction.jsp'
										});
							}
						});

					}
				}, {
					text : '关闭',
					iconCls : 'cannel',
					handler : function() {
						win.close();
					}
				}, {
					text : '帮助',
					hidden : true
				}],
		cm : cm
	});
	var win = new Ext.Window({
				title : '自动外呼责任人休假设置',
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
 * 自动外呼参数设置
 */
var time_min;
function showCallPhoneParamConfig() {

	if (Ext.getCmp("showCallPhoneParamConfigWin"))
		return;
	var field1 = new Ext.form.NumberField({
				id : 'FAIL_NUM',
				fieldLabel : '失败几次进入关注列表',
				maxValue : 3,
				value : 3
			});
	var field2 = new Ext.form.NumberField({
				id : 'ALARM_NUM',
				fieldLabel : '一分钟产生多少告警进入关注列表',
				value : 30
			});
	var field3 = new Ext.form.TextField({
				id : 'ALARM_GRADE',
				fieldLabel : '告警级别定义',
				value : '>=3'
			});

	var field4 = new Ext.form.TextField({
		id : 'callCount',
		fieldLabel : '每日最大外呼数'
	});
	time_min = new Ext.form.TextField({
		id : 'time_min',
		fieldLabel : '与当前时间间隔'
	});
	time_run = new Ext.form.TextField({
		id : 'time_run',
		fieldLabel : '外呼执行周期'
	});
	var field5 = new Ext.form.TextField({
		id : 'field5',
		value:'3:5,10',
		fieldLabel : '自动外呼控制'
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
		title : '自动外呼参数设置',
		id : 'showCallPhoneParamConfigWin',
		width : 360,
		height : 260,
		layout : 'fit',
		items : [fmp],
		buttons : [{
			text : '保存',
			iconCls : 'save',
			handler : function() {
				if( !(/^[1-9]+[0-9]*]*$/.test(field4.getValue())) ) {
					Ext.Msg.alert("警告：","【每日最大外呼数】必须是正整数!");
					return;
				}
				if( !(/^[1-9]+[0-9]*]*$/.test(time_min.getValue())) ) {
					Ext.Msg.alert("警告：","【与当前时间间隔】必须是正整数!");
					return;
				}
				if( field4.getValue() <= 100 ) {
					Ext.Msg.alert("警告：","【每日最大外呼数】必须>100");
					return;
				}

				
				if( field5.getValue()=='' ) {
					Ext.Msg.alert("警告：","【自动外呼控制】不能为空");
					return;
				}	
				
				// 外呼次数
				var num = field5.getValue().split(":")[0];
				if( !(/^[1-9]+[0-9]*]*$/.test(num) ) ) {
					Ext.Msg.alert("警告：","【自动外呼控制】自动外呼次数必须是>0的正整数！");
					return;
				}
				if( num > 5  ) {
					Ext.Msg.alert("警告：","【自动外呼控制】自动外呼次数不能超过5次！");
					return;
				}
				if( num !=1 && field5.getValue().split(":").length <= 1 ) {
					Ext.Msg.alert("警告：","【自动外呼控制】格式不对！");
					return;
				} else if ( num !=1 ) {
					var times =  field5.getValue().split(":")[1].split(",");
					if( times.length !=  field5.getValue().split(":")[0]-1) {
						Ext.Msg.alert("警告：","【自动外呼控制】时间间隔数量不正确！");
						return;
					}
					for (var i = 0; i < times.length; i++ ) {
						if ( !(/^[1-9]+[0-9]*]*$/.test(times[i])) ) {
							Ext.Msg.alert("警告：","【自动外呼控制】时间间隔必须是>0的正整数！");
							return;
						}
					}
				}

				var obj = Ext.getCmp('ALARM_GRADE');
				if (obj) {
					if (obj.getValue().indexOf('<') >= 0) {
						Ext.Msg.alert('您的告警级别填写“<”告警量太大，不能更新', '');
						return;

					}

				}
				Ext.Msg.confirm('提示', '您确认更新这些参数，点击更新会立即影响外呼告警', function(btn,
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
													Ext.Msg.alert('提示', '变更完成');
													win.close();
												} else {
													Ext.Msg.alert('提示',
															'变更失败，原因：'
																	+ obj.msg);
												}
											},
											failure : function(resp, opts) {
												// Ext.Msg.alert("提示","关注告警取消失败?");
											},
											url : './doCallPhoneAction.jsp'
										});
							}
						});

			}
		}, {
			text : '取消',
			iconCls : 'cannel',
			handler : function() {
				win.close();
			}
		}],
		modal : false
	});

	win.show();
	
	Ext.get('time_min').on('mouseover',function(e) {
		showMesage('time_min','当外呼时间与当前时间间隔超过此设置时间时(单位：分钟)，将从外呼列表中移除，进入关注列表。');	
	});
	Ext.get('time_run').on('mouseover',function(e) {
		showMesage('time_run','单位：分钟');	
	});
	
	Ext.get('field5').on('mouseover',function(e) {
		showMesage('field5','自动外呼次数及时间间隔控制，格式3:5,10(自动外呼三次，第一次间隔5分钟，第二次间隔10分钟)');	
	});
	
	// 加载配置
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
 * 查询过滤窗口
 */
var moWin;
var win1;
var field1,field2,field3,field4,field5,field6,field7, field8, field9, field10,comb; 
function showSearch() {
	if (Ext.getCmp("showSearchWin")) return;
	field1 = new Ext.form.TextField({
		id:'key',
		width:345,
		fieldLabel : '告警内容',value:selectTxt
	});
	field2 = new Ext.form.TextField({
		id:'alarmNode',
		width:345,
		readOnly:true, 
		fieldLabel : '对象路径'
	});

	field3 = new Ext.form.DateField({
		width:345,
		fieldLabel : '开始时间',//value: new Date(),
		name:'stime',format:'Y-m-d H:i:s'
	});	

	field4 = new Ext.form.DateField({
		width:345,
		fieldLabel : '结束时间',//value: new Date(),
		name:'etime',format:'Y-m-d H:i:s'
	});

	field5 =  new Ext.form.ComboBox({
		width:345,
		triggerAction : "all",
		fieldLabel :'告警级别',
		store : new Ext.data.SimpleStore({
					fields : ['caption', 'value'],
					data : [['未确定告警','0'],['警告告警', '1'], ['一般告警', '2'], ['重要告警', '3'], ['严重告警', '4']]
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
		fieldLabel :'告警来源',
		store : field6store,
		displayField : 'name',
		valueField : 'id',
		mode : 'remote'
	});	

	field7 = new Ext.form.TextField({
		width:345,
		fieldLabel : '告警代码'
	});
		

	field8 = new Ext.form.TextField({
		width:345,
		readOnly:true,
		id:'searchCaller',	
		fieldLabel : '外 呼 人'
	});


	field9 = new Ext.form.Hidden({ id:'moid',value:'' });
	field10 = new Ext.form.Hidden({ id:'user_id',value:'' });

	comb =  new Ext.form.ComboBox({
		id:'isShowComb',
		width:345,
		triggerAction : "all",
		fieldLabel :'外呼类型',
		store : new Ext.data.SimpleStore({
			fields : ['caption', 'value'],
			data : [['不限','-1'],['当前', '1'], ['历史', '0']]
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
		title : '自动外呼告警查询',
		id : 'showSearchWin',
		width : 450,
		height : 320,
		layout : 'fit',
		items : [fmp],
		buttonAlign:'center',
		buttons : [{
			text : '查询',
			iconCls : 'search',handler:function(){
				isShow = (Ext.getCmp('isShowComb')?Ext.getCmp('isShowComb').getValue():'0'),
				selectTxt = (Ext.getCmp('key')?Ext.getCmp('key').getValue():'');
				if (callstore1) callstore1.load({ params : { start : 0 } });
				win1.close();
			}
		}, {
			text : '取消',
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
					title:"节点选择",
					height:300,	
					width:350,
					buttonAlign: 'center',
					header : true,
					buttons:[
						{text : '清空',handler : function() { setNode('','') }},
						{text : '取消',handler : function() { moWin.hide(); }}					
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
 * 条件类
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
 * 添加过滤条件
 * 
 */
function addConfig(moid, caption, namingPath, path, flag) {
	if ( flag == 'TrackPath' ) {
		// 跟踪告警处理
		trackPath (moid, caption, namingPath);
	} else if ( flag == 'AutoCall' ) {
		// 自动外呼过滤设置
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
			Ext.Msg.alert("提示", "失败");
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
			// 给消息框右下角增加一个关闭按钮
			message += '<br><span style="text-align:right;font-size:12px; width:100%;">'
					+ '<font color="blank"><u style="cursor:hand;" onclick="MsgTip.hide(this);">关闭</u></font></span>'
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
				return "您填写的时间格式有问题，正确的格式为2012-01-01 00:00:01~2012-12-31 23:59:59";
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
 * 取得cas用户信息
 * nameField Ext.Form.TextField 用户名展示区域
 * idField Ext.Form.TextField/ Ext.Form.Hidden 用ID展示区域
 * limit 每页显示数量
 */
function searchCaller ( nameField, idField, limit ) {
	var col = new Ext.grid.ColumnModel([
		new Ext.grid.RowNumberer(),//自动行号
		{header:"账号",dataIndex:"pno",width:80},
		{header:"姓名",dataIndex:"pnm",width:130}
	]);
	var store = new Ext.data.Store({
		proxy:  new Ext.data.HttpProxy({url:'user.jsp?condition=' }),
		reader: new Ext.data.JsonReader({ totalProperty: 'totalProperty', root: 'root' },[ {name: 'pno'}, {name: 'pnm'} ])
	}); 
	var bar = new Ext.PagingToolbar({//翻页处理
			pageSize: limit*1,  //每页显示数据条数
			store: store, 
			displayInfo: true,
			beforePageText : "第",
			displayMsg: '一共 {2} 条'
	});
	var grid = new Ext.grid.GridPanel({
		id:'gridPanel',
		autoScroll:true,
		cm:col,
		store:store,
		disableSelection:true,
		viewConfig: { autoFill: true },
		bbar: bar,
		tbar:["-", {text : '清空',handler : function() {
					nameField.setValue( '' );
					idField.setValue( '' );
					win.close();
				}
			}, '-',"查询条件:<input type='text' value='' id='conText' style='width:150'>",
			{text : '查询',handler : function() { 
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
		title:"用户信息查询",
		height:300,	
		width:350,
		layout:'fit',
		modal : false,
		items:[grid]
	});
	win.show();
}
	

/* 显示指定ID对象的提示信息 */
function showMesage (cid, message) {
	Ext.QuickTips.register({
        target: cid,   
        text: "<font color='green'>" + message + "</font>",   
        maxWidth: 100,      //设置后显示内容可以自动换行   
        dismissDelay:20000, //设置显示时间                               
        trackMouse: true,   
        autoHide: true,   
        animate: true  
    });
}

function callReport () {
   var win = new Ext.Window({
		title:"语音外乎报表统计",
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
var msgstr = "时间间隔格式如下：<br>";
msgstr += "1、不填代表任何时间<br>";
msgstr += "2、通用的表达式，如0 0 9-23 * * ?代表每天9点至23点<br>";
msgstr += "3、时间段，如2012-01-01~2012-01-31支持详细至时分秒<br>";




/** 设置外呼状态 */
function setState ( v ) {
	if (v == '0') {
		return "正在外呼";
	} else if (v == '1') {
		return '外呼成功';
	} else if (v == '-1') {
		return '外呼失败';
	} else {
		return v;
	}
}

/** 设置告警级别 */
function setAlarmGradre ( v ) {
	if ( v != "") {
    	 return "<img src='/sysmgr/images/icons/grade" + v + ".gif'>" + v;
	} else {
    	return v;
	}
}

/** 删除外呼记录 */
function deleteCall ( grid1 ) {
	var data = grid1.getSelectionModel().getSelections();
	if (data.length <= 0) {
		Ext.Msg.alert("提示", "请选择需要清除的告警？");
		return;
	}
	var alarmIds = '';
	var count = 0;
	for (var i = 0; i < data.length; i++) {
		var r = data[i];
		// 赋权给zangzw、wub 有权删除外呼状态为【外呼中】的外呼记录
		if ( loginUserId != 'zangzw' && loginUserId != 'wub' && loginUserId != 'root' ) {
			if (r.get('stat') != 1) continue;
		}
		if ( count > 0 ) { alarmIds += "," };
		alarmIds += r.get('id');
		count++;
	}
	
	if ( alarmIds == '' ) { 
		Ext.Msg.alert("提示", "没有需要清除的自动外呼记录!");
		return;
	}
	Ext.Msg.confirm('提示', '您是否确认不需要关注这些外呼记录?', function(btn, text) {
		if (btn == 'yes') {
			hideCallPhone(alarmIds);
		}
	});
}

/** 外呼角色配置窗口 */
function callUserConfig ( )  {
	
	if (Ext.getCmp("callUserConfigWin")) { return; }

	var sm = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel([sm,
		{ header : 'rId', dataIndex : 'rId', width : 80 },
		{ header : '外呼人员A', dataIndex : 'userA', width : 80 }, 
		{ header : '外呼人员B', dataIndex : 'userB', width : 80 }, 
		{ header : '外呼人员C', dataIndex : 'userC', width : 80 }, 
		{ header : '操作人员', dataIndex : 'userOP', width : 80 }, 
		{ header : '操作时间', dataIndex : 'timeOP', width : 120 }
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
		stripeRows : true,// 斑马线效果
		loadMask : true,
		clicksToEdit : 1,
		tbar : [ 		
				'-', '<input type=text id=uconfig width=150>', 
				{ text : '查询', iconCls : 'search', handler:function() { store.load(); } },
				'-',{ text : '增加', iconCls : 'add', handler : function() { addUserConfig(store); } }, 
				'-', { text : '修改', iconCls : 'modify', handler : function() { modifyCallUserInfo(store, grid); } },  
				'-', { text : '删除', iconCls : 'del', handler : function() { delUserConfig(store, grid);} }, 	
				'-', { text : '间隔设置', iconCls : 'manager', handler : function() { addUserTimeConfig();} }, 			
				'-', { text : '关闭', iconCls : 'cannel', handler : function() { win.close(); } },'-'
		],
		bbar : new Ext.PagingToolbar({
			pageSize : 20,
			store : store,
			displayInfo : true,
			displayMsg : ' 显示第{0}条到{1}条记录,一共{2}条',
			emptyMsg : "没有记录"
		})
	});
	var win = new Ext.Window({
		title : '外呼角色设置',
		id : 'callUserConfigWin',
		width : 560,
		height : 400,
		layout : 'border',
		items : [ grid ],
		modal : true
	});

	win.show();
}

/** 添加外呼角色配置 */
function addUserConfig( store ) {

	if (Ext.getCmp("addUserConfigWin")) { return; }
	
	var f_userA = new Ext.form.TextField({ id:'f_userA', fieldLabel:'第一外呼人', width:160 });
	var f_userB = new Ext.form.TextField({ id:'f_userB', fieldLabel:'第二外呼人', width:160 });
	var f_userC = new Ext.form.TextField({ id:'f_userC', fieldLabel:'第三外呼人', width:160 });
	
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
		title:'外呼角色设置',
		id:'addUserConfigWin',
		width:300,
		height:200,
		layout:'fit',
		items:[fmp],
		modal:true,
		buttons:[
			{ text : '保存', iconCls : 'save', handler : function() {
					ctx.get( function (data) { 
						if ( data == true ) { 
							Ext.Msg.alert("警告：","用户" + f_userA.getValue() + "已存在。");
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
									Ext.Msg.alert('提示', '保存成功！');
									win.close();
									store.reload();
								} else {
									Ext.Msg.alert( '提示','ERROR:' + obj.msg );
								}
							},
							failure : function(resp, opts) {},
							url : './doCallPhoneAction.jsp'
						});
					
					},"callUserManager.isUserExist( \"" +  valueA.getValue() + "\")"); 
					
				}
			}, {
				text : '关闭',
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


/** 外呼时间间隔配置 */
function addUserTimeConfig() {

	if (Ext.getCmp("addUserTimeConfig")) { return; }
	
	var userTime = new Ext.form.TextField({ id:'userTime', fieldLabel:'外呼时间间隔', width:160 });
	
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
		title:'外呼时间间隔配置',
		id:'addUserTimeConfig',
		width:300,
		height:100,
		layout:'fit',
		items:[fmp],
		modal:false,
		buttons:[
			{ text : '保存', iconCls : 'save', handler : function() {
					ctx.get( function (data) { 
						if ( data > 0 ) { 
							Ext.Msg.alert("提示：","参数已经修改！");
							win.close();
						} else {
							Ext.Msg.alert("提示：","<font color='red'>参数修改失败！</font>");
						}
					},"callUserManager.setUserTime(" +  userTime.getValue() + ")"); 
					
				}
			}, {
				text : '关闭',
				iconCls : 'cannel',
				handler : function() { win.close(); }
			}
		]
	});
					
	win.show();
	
	// 初始化
	ctx.get( function (data) { userTime.setValue(data); },"callUserManager.getUserTime()"); 
}

/** 删除外呼角色配置 */
function delUserConfig ( store, grid ) {

	var data = grid.getSelectionModel().getSelections();
	if ( data.length <= 0 ) {
		Ext.Msg.alert("提示", "请选择需要删除的设置!");
		return;
	}
	
	var userAs = "";
	for (var i = 0; i < data.length; i++) {
	
		if (i > 0) { userAs += ";"; }
		
		userAs += data[i].get('rId');
	}
	Ext.Msg.confirm('提示', '您确认删除这些配置吗？', function(btn, text) {
		if (btn != 'yes') { return; }
		Ext.Ajax.request({
			method : 'post',
			params : { mode : 'deleteCallUserInfo', userAs : userAs },
			success : function(resp, opts) {
				var obj = Ext.util.JSON.decode(resp.responseText);

				if (obj.success) {
					Ext.Msg.alert('提示', '删除成功！');
					store.load();
				} else {
					Ext.Msg.alert( '提示','ERROR:' + obj.msg );
				}
			},
			failure : function(resp, opts) {},
			url : './doCallPhoneAction.jsp'
		});
	});
}

/** 修改外呼角色配置 */
function modifyCallUserInfo ( store, grid ) {

	var data = grid.getSelectionModel().getSelections();
	if ( data.length != 1 ) {
		Ext.Msg.alert("提示", "请选择请选择一条配置记录!");
		return;
	}
	
	var userA = data[0].get('rId');

	if (Ext.getCmp("addUserConfigWin")) { return; }
	
	var f_userA = new Ext.form.TextField({ id:'f_userA', fieldLabel:'第一外呼人', width:160, disabled:true });
	var f_userB = new Ext.form.TextField({ id:'f_userB', fieldLabel:'第二外呼人', width:160 });
	var f_userC = new Ext.form.TextField({ id:'f_userC', fieldLabel:'第三外呼人', width:160 });
	
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
		title:'外呼角色设置',
		id:'addUserConfigWin',
		width:300,
		height:200,
		layout:'fit',
		items:[fmp],
		modal:true,
		buttons:[
			{ text : '保存', iconCls : 'save', handler : function() {
					Ext.Msg.confirm('提示', '您确认修改这个配置吗？', function(btn, text) {
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
									Ext.Msg.alert('提示', '保存成功！');
									win.close();
									store.reload();
								} else {
									Ext.Msg.alert( '提示','ERROR:' + obj.msg );
								}
							},
							failure : function(resp, opts) {},
							url : './doCallPhoneAction.jsp'
						});
					});
				}
			}, {
				text : '取消',
				iconCls : 'cannel',
				handler : function() { win.close(); }
			}
		]
	});

	win.show();

	Ext.get('f_userB').on('click',function(){ searchCaller( f_userB, valueB, 10 ); });
	Ext.get('f_userC').on('click',function(){ searchCaller( f_userC, valueC, 10 ); });
	
	// 加载配置
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

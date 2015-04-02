<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="../extjs/resources/css/ext-all.css" />
		<script type="text/javascript" src="../extjs/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="../extjs/ext-all.js"></script>
		<script type="text/javascript" src="../extjs/ext-ext.js"></script>
		<script type="text/javascript" charset="utf-8" src="../extjs/ext-lang-zh_CN.js"></script>
<script type="text/javascript">
 Ext.onReady(function(){ 
	 Ext.QuickTips.init(); 
	 // turn on validation errors beside the field globally
	 Ext.form.Field.prototype.msgTarget = 'side'; 
	 var bd = Ext.getBody();
	 /* * ================ Simple form ======================= */
	 bd.createChild({tag: 'h2', html: 'Form 1 - Very Simple'});
	 var simple = new Ext.FormPanel({ 
		 labelWidth: 75, // label settings here cascade unless overridden 
		 frame:true, 
		 title: 'Simple Form', 
		 bodyStyle:'padding:5px 5px 0',
		 width: 350, 
		 defaults: {width: 230},
		 defaultType: 'textfield', 
		 items: [{ fieldLabel: 'First Name', name: 'first', allowBlank:false },
		         { fieldLabel: 'Last Name', name: 'last' }, 
		         { fieldLabel: 'Date Time', name: 'datetime1', xtype:'datetimefield' }],
		         buttons: [{ text: 'Save' },{ text: 'Cancel' }]
		 });
	 		simple.render(document.body); 
	 		}); 
</script>
</head>
<body>

</body>
</html>
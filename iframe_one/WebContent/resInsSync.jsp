<%@ page contentType="text/html; charset=gb2312" language="java"	errorPage="/error.jsp"%>
<html>
	<head>
		<title></title>
		<link rel="stylesheet" type="text/css" href="extjs/resources/css/ext-all.css" />
		<script type="text/javascript" src="extjs/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="extjs/ext-all.js"></script>
		<script type="text/javascript" src="extjs/ext-ext.js"></script>
		<style type="text/css">
		#from1{
		margin: 20px;  
		}
		
		
		</style>
		<script type="text/javascript">
		Ext.BLANK_IMAGE_URL = 'extjs/resources/images/default/s.gif';
		Ext.onReady(function(){
			field3 = new Ext.form.DateField({
				width:200,
				fieldLabel:'��ʼʱ��',
				name:'startTime',
				format:'Y-m-d H:i:s'
			});
			
			field4 = new Ext.form.DateField({
				width:200,
				fieldLabel:'��ʼʱ��',
				name:'endTime',
				format:'Y-m-d H:i:s'
			});
			
			field3.render("startTime");
			field4.render("endTime");
		})
		
		//ȥ�����ҿո�  
		 String.prototype.trim=function(){
			   return this.replace(/(^\s*)|(\s*$)/g,"");
			 }
		
		//У�鲻Ϊ�յķ��� 
		function validatefrom(parm,tipInfo){
			if(parm.value.trim()==""){
				alert(tipInfo);
				return false;
			}else{
				return true;
			}
		}
	
		
		
		function doSubmit() {
			if(!validatefrom(document.from1.startTime,"����ʱ�䲻��Ϊ��"))  return;
			if(!validatefrom(document.from1.endTime,"����ʱ�䲻��Ϊ��"))       return ;
			
			document.from1.target="resForm";  //�������ֻ�ȡ 
			document.from1.submit();
			return;
		}
		</script>
	</head>
	<body>
		<form  id="from1" name="from1" action="manager/resInsSync.do">
			<table>
			<tr>
			<td>����ʱ��<span  style="color: red;">*</span> :</td>
			<td><span  id="startTime"></span>  </td>
			<td>����ʱ��<span  style="color: red;">*</span> : </td>
			<td><span  id="endTime"></span></td>
			<td><input type="button" value='�ύ' onclick="doSubmit()"></td>
			</tr>
			
			</table>
		</form>
	</body>
</html>
<%@ page contentType="text/html; charset=gb2312" language="java"	errorPage="/error.jsp"%>
<html>
	<head>
		<title></title>
		<style type="text/css">
		#from1{
		margin: 20px;  
		}
		
		
		</style>
		<script type="text/javascript">
		
		var addCodeNum=1;  
		
		
	
		// ���� codeText 
		function addCodeText(){
			var  addCodebnt=document.getElementById("codeArray");
			if(addCodeNum<=6){
				++addCodeNum;
				 	var oDiv= document.createElement('div');
					 oDiv.innerHTML ='code '+addCodeNum+' <input  type="text"  name="resList" ><br>';
			        addCodebnt.appendChild(oDiv);
			}else{
				alert("���������");
			}
		}
	
		
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
			//������У�� 
		if(!validatefrom(document.getElementById("resList"),"��Դ�б���Ϊ��"))    return ;
		
		
		//�������ύ 
		document.from1.target="resForm";  //�������ֻ�ȡ 
		document.from1.submit();
	}
		</script>
	</head>
	<body>
		<form  id="from1" name="from1" action="manager/resInsOperate.do">
			<table>
			<tr>
					<td>��������<span  style="color: red;">*</span>:</td>
					<td style="width: 300px;"> 
					<input type="radio"  name="actionType"  value="1"  checked> ����
					<input type="radio"  name="actionType"  value="2">ֹͣ
					</td>
				</tr>
			
				<tr>
					<td >��Դ����<span  style="color: red;">*</span>:</td>
					<td style="width: 300px;"> 
					<input type="radio"  name="type" value="1"  checked>������ 
					<input type="radio"  name="type" value="2">����Ⱥ
					<input type="radio"  name="type" value="3">���ڵ�
					</td>
				</tr>
				<tr>
					<td >��Դ�б�<span  style="color: red;">*</span>:</td>
					<td >
					  	code 1 <input  type="text"  name="resList"  id="resList" value="">
					</td>
					<td>
					<input type="button"  value="���code" onclick="addCodeText()">
					</td> 
				</tr>
				<tr>
				<td></td>
				<td  id="codeArray">
				 
				</td>
				</tr>
				
				<tr>
				<td><input type="button" value='�ύ' onclick="doSubmit()"></td>
				<td><input type="reset" value='����'></td>
				</tr>
			</table>
			
		</form>
	</body>
</html>
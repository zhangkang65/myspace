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
		
			//ȥ�����ҿո�  
			 String.prototype.trim=function(){
				   return this.replace(/(^\s*)|(\s*$)/g,"");
				 }
		
			
		function addCodeText(){
			var  addCodebnt=document.getElementById("codeArray");
			if(addCodeNum<=6){
				++addCodeNum;
				var oDiv= document.createElement('div');
			 	//oinput.setAttribute('type', 'text');
				//oinput.setAttribute('name', 'resList')
				//alert(oinput);
				 oDiv.innerHTML ='code '+addCodeNum+' <input  type="text"  name="resList" ><br>';
		        addCodebnt.appendChild(oDiv);
			}else{
				alert("���������");
			}
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
			
			document.from1.target="resForm";  //�������ֻ�ȡ 
			//parent.document.getElementById("resForm").src="resourceSvc1r.jsp";
			 document.from1.submit();
			return;
		}
		</script>
	</head>
	<body>
		<form  id="from1" name="from1" action="manager/resInsStatusQuery.do">
			<table>
				<tr>
					<td >��Դ����<span  style="color: red;">*</span>:</td>
					<td style="width: 300px;text-align: left;"> 
					<input type="radio"  name="type" value="1"  checked="checked">������ 
					<input type="radio"  name="type"  value="2">����Ⱥ
					<input type="radio"  name="type"  value="3">���ڵ�
					</td>
				</tr>
				<tr>
					<td >��Դ�б�<span  style="color: red;">*</span>:</td>
					<td  style="width: 300px;text-align: left;" >
					  	code 1 <input  type="text" id="resList"  name="resList"  value="">
					</td>
					<td>
					<input type="button"  value="���code" onclick="addCodeText()">
					</td> 
				</tr>
				<tr>
				<td></td>
				<td id="codeArray">
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
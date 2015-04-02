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
		
		
	
		// 增加 codeText 
		function addCodeText(){
			var  addCodebnt=document.getElementById("codeArray");
			if(addCodeNum<=6){
				++addCodeNum;
				 	var oDiv= document.createElement('div');
					 oDiv.innerHTML ='code '+addCodeNum+' <input  type="text"  name="resList" ><br>';
			        addCodebnt.appendChild(oDiv);
			}else{
				alert("超过额定数量");
			}
		}
	
		
		//去除左右空格  
		 String.prototype.trim=function(){
			   return this.replace(/(^\s*)|(\s*$)/g,"");
			 }
		
		//校验不为空的方法 
		function validatefrom(parm,tipInfo){
			if(parm.value.trim()==""){
				alert(tipInfo);
				return false;
			}else{
				return true;
			}
		}
	
		
		
		function doSubmit() {
			//表单数据校验 
		if(!validatefrom(document.getElementById("resList"),"资源列表不能为空"))    return ;
		
		
		//表单数据提交 
		document.from1.target="resForm";  //根据名字获取 
		document.from1.submit();
	}
		</script>
	</head>
	<body>
		<form  id="from1" name="from1" action="manager/resInsOperate.do">
			<table>
			<tr>
					<td>操作类型<span  style="color: red;">*</span>:</td>
					<td style="width: 300px;"> 
					<input type="radio"  name="actionType"  value="1"  checked> 启动
					<input type="radio"  name="actionType"  value="2">停止
					</td>
				</tr>
			
				<tr>
					<td >资源类型<span  style="color: red;">*</span>:</td>
					<td style="width: 300px;"> 
					<input type="radio"  name="type" value="1"  checked>按主机 
					<input type="radio"  name="type" value="2">按集群
					<input type="radio"  name="type" value="3">按节点
					</td>
				</tr>
				<tr>
					<td >资源列表<span  style="color: red;">*</span>:</td>
					<td >
					  	code 1 <input  type="text"  name="resList"  id="resList" value="">
					</td>
					<td>
					<input type="button"  value="添加code" onclick="addCodeText()">
					</td> 
				</tr>
				<tr>
				<td></td>
				<td  id="codeArray">
				 
				</td>
				</tr>
				
				<tr>
				<td><input type="button" value='提交' onclick="doSubmit()"></td>
				<td><input type="reset" value='重置'></td>
				</tr>
			</table>
			
		</form>
	</body>
</html>
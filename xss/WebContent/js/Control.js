var canmove=false;
var scaleFactor=10.26;
var scaleFactor2=10;

var tempOffsetX=0;
var tempOffsetY=0;

var mapOffsetX=0;
var mapOffsetY=0;
var Starline;
var activeConcept;
var thisobj=null   //为了完成各种基本编辑功能，如“置前”“复制”“删除”等
var StartPointX;
var StartPointY;
var MaxID=1;

var Start2X=100;
var Start2Y=100;
var End2X=0;
var End2Y=0;
var curSelectObjectId;

//var candrag=false;
var candraw=false;

var dash = null;
var startLeft = 0;
var startTop = 0;

//为了防止抖动，我们定义鼠标移动1-2两个像数不属于正常移动
var ObjectStartX = 0;
var ObjectStartY = 0;
var ObjectEndX = 0;
var ObjectEndY = 0;
function MoveOver() {
  //判断当前状态为是否为编辑状态
  if (parent.currentState==false){ 
  	 event.srcElement.style.cursor="";
  	 return false; 
  }
  
  if (event.srcElement.className=="WorkFlowItem") {
     
    //编辑器当前状态
    if (getDrag()) event.srcElement.style.cursor="move";
    else event.srcElement.style.cursor="";
    //当前是否为移动的图形
    if (event.srcElement.key == "move"){
    	hiderect(event.srcElement);
    }
    line2.to="0,0";
	dash = document.getElementById("showimg_dash");
	
	var work= null;
	work = event.srcElement.parentElement;
	
	
	dash.style.pixelLeft = work.style.pixelLeft;
	dash.style.pixelTop = work.style.pixelTop;
	var work2 = null;
	work2 = event.srcElement;
	if (canmove==false){
	if(work2.tagName=="image"){
	   dash.style.pixelWidth = work.style.pixelWidth * (3/5);
	   dash.style.pixelHeight = work.style.pixelHeight;
	}else{
	   dash.style.pixelWidth=work.style.pixelWidth*scaleFactor2;
	   dash.style.pixelHeight=work.style.pixelHeight*scaleFactor2;
	}
	}
  }
}
//设置可以移动状态
function setMove() {
	canmove=true;
}

var lineStyle="";
var curDragSelectObjectId=null;

function MoveStart() {
    if (parent.currentState==false) return false; 
	try {
		if (window.parent.parent.arrow=="classic"){
			lineStyle="<v:stroke EndArrow='classic'>";
			line2.innerHTML=lineStyle;
		}else if (window.parent.parent.arrow=="normal"){
			lineStyle="<v:stroke LineStyle='Single'>";
			line2.innerHTML=lineStyle;
		}else if (window.parent.parent.arrow=="dash"){
			lineStyle="<v:stroke dashstyle='LongDash' EndArrow='classic'>";
			line2.innerHTML=lineStyle;
		}
		
		if(event.srcElement.className!="WorkFlowItem" && event.srcElement.className!="EndLine") return;
		if (event.button == 2) return;
		
		if (window.parent.parent.canDrag) {
			dash = document.getElementById("showimg_dash");
			
			startLeft = event.x;
			startTop = event.y;
			
			tempOffsetX = event.offsetX;
			tempOffsetY = event.offsetY;
			
			mapOffsetX = GetMapOffsetY();
			mapOffsetY = GetMapOffsetX();
			//dash.style.pixelLeft = (event.x - tempOffsetX   + Chart.scrollLeft ) * scaleFactor2; 
			//dash.style.pixelTop = (event.y - tempOffsetY + Chart.scrollTop ) * scaleFactor2 ; 
			if (event.srcElement.className=="WorkFlowItem") {
				 //开始移动
				ismoving = true;
				event.srcElement.parentElement.style.position="absolute";
				activeConcept=event.srcElement.parentElement;
		        curSelectObjectId=activeConcept.id;
		        document.selection.empty();
		        dash.style.pixelLeft = (event.x - tempOffsetX   + Chart.scrollLeft ) * scaleFactor2; 
		        //window.status = "dash:"+dash.style.pixelLeft+"|x:"+event.x+"|offsetx:"+tempOffsetX;
			    dash.style.pixelTop = (event.y - tempOffsetY + Chart.scrollTop ) * scaleFactor2 ; 
			    dash.style.visibility = 'visible';
			}
			
			
			
			
			ObjectStartX = ((event.x - tempOffsetX + Chart.scrollLeft ) * scaleFactor2); // - tempOffsetX -mapOffsetX + document.body.scrollLeft
			ObjectStartY =((event.y - tempOffsetY + Chart.scrollTop   ) * scaleFactor2); //- tempOffsetY - mapOffsetY+ document.body.scrollTop
		} else {
			if (event.srcElement.className=="WorkFlowItem") {
			    var ent=event.srcElement;
			    Start2X=((event.x  - tempOffsetX  + Chart.scrollLeft ) * scaleFactor2); // -mapOffsetX
		 		Start2Y=((event.y - tempOffsetY  + Chart.scrollTop ) * scaleFactor2); //- mapOffsetY + document.body.scrollTop
				
				curDragSelectObjectId=event.srcElement.parentElement.id;
			    line1.style.display='';
				Starline=event.srcElement;
				
				var stline = event.srcElement; 
				var stlineid = stline.getAttribute("Refid");
				var startobj = eval(stlineid);
				StartPointX=startobj.style.pixelLeft+320;
				StartPointY=startobj.style.pixelTop+230;
				
			    
			    line2.style.left=StartPointX;
			    line2.style.top=StartPointY;
			    var TagName=event.srcElement.tagName;
			    var name=event.srcElement.name;
			    if (TagName!="oval"&&TagName!="roundrect"&&TagName!="rect"&&name!="link"){
			       line2.style.display='';
			       candraw=true;
			    }else{ candraw=false;
			       line2.style.display='none';
			    }
			}
		}
		Chart.attachEvent('onmousemove',Moving); 
	}catch(e){}
}

function Moving() {
	if (event.button !=1) return;
	
  	try {	
  		if(canmove) {
  		   
  		    if (parent.currentState==false) {
  		       
  		       return;
            }
	  		dash = document.getElementById("showimg_dash");
	  		
			dash.style.pixelLeft = (event.x - tempOffsetX + Chart.scrollLeft ) * scaleFactor2;  //- tempOffsetX -mapOffsetX
			dash.style.pixelTop = (event.y - tempOffsetY + Chart.scrollTop ) * scaleFactor2 ;  // - tempOffsetY - mapOffsetY
		} else {
			End2X=((event.x + Chart.scrollLeft ) * scaleFactor2);
			End2Y=((event.y + Chart.scrollTop ) * scaleFactor2);
			line2.to="\""+(End2X-StartPointX)+","+(End2Y-StartPointY)+"\"";
	
			if(event.srcElement.className=="StartLine") {
				event.srcElement.style.cursor="hand";
				event.srcElement.title='放置在此处将建立流程';
			}	
		}
	}catch(e){}
}

function MoveAllLine() {
	try {
		var allLine = document.body.all.item('ConnectLine');
		var i;
		if (allLine!=null) {
			var count=allLine.length;
			if (count) {
				for (i=count-1; i>=0; i--) {
					UpdateOneLinePos(allLine[i]);
					allLine[i].className = "NormalLine";
				}
			} else {
				UpdateOneLinePos(allLine);
				allLine.className="NormalLine";	
			}
		}
	}catch(e){}
}

//显示并调整所有的线段
function ShowAllLine() {
	var allLine = document.body.all.item('ConnectLine');
	var i;
	
	if (allLine!=null) {
	    
		var count=allLine.length;
		
		if (count) {
		    
			for (i=count-1; i>=0; i--) {
				UpdateOneLinePos(allLine[i]);
				allLine[i].className = "NormalLine";
			}
		} else {
			UpdateOneLinePos(allLine);
			allLine.className="NormalLine";	
		}
	}
}
function UpdateOneLinePos(line) {
	var beginShape;
	var endShape;
	try{
		beginShape = line.getAttribute("BeginShape");
		endShape = line.getAttribute("EndShape");
		
		var obj=eval(curSelectObjectId);
		
		var endobj=eval(endShape);
		var startobj=eval(beginShape);
		
		if (beginShape==curSelectObjectId) {
	        var dot =  new getLine(beginShape,endShape); //new LineDot(beginShape,endShape);
	        line.style.left=dot.x1;
	        line.style.top=dot.y1;
	        line.to="\""+(dot.x2-dot.x1)+","+(dot.y2-dot.y1)+"\"";
		}
		if (endShape==curSelectObjectId) {
		    var dot = new getLine(beginShape,endShape);//new LineDot(beginShape,endShape);
	        line.style.left=dot.x1;
	        line.style.top=dot.y1;
	        line.to="\""+(dot.x2-dot.x1)+","+(dot.y2-dot.y1)+"\"";
		}
	}catch(e){
	 
	}
}



function GetCenterX(shape) {
	return shape.style.pixelLeft + shape.style.pixelWidth / 2;
}

function GetCenterY(shape) {
	return shape.style.pixelTop + shape.style.pixelHeight / 2;
}

function EndMove() {
	canmove=false;
	document.selection.empty();
}

function GetMapOffsetY() {
	var tempMap = Chart;
	var tempY = 0;
	
	while (tempMap.tagName!="BODY") {		
		tempY = tempY + tempMap.offsetTop;
		tempMap = tempMap.offsetParent;
	}
	
	return 	tempY + document.body.topMargin ;
}

function GetMapOffsetX() {
	var tempMap = Chart;
	var tempX=0;
	
	while (tempMap.tagName!="BODY") {		
		tempX = tempX + tempMap.offsetLeft;
		tempMap = tempMap.offsetParent;
	}
	
	return 	tempX + document.body.leftMargin ;
}

//需要网格定位
function moveGridPoint(xy){
	if (parent.isgridding == true){
       var num = xy % grid_value;
       var num2 = xy - num;
       var grid_value_2 = grid_value/2;
       if (num < grid_value_2){
       	  return num2;
       }else{
       	  var newvalue = num2*1 + grid_value;
       	  return newvalue;
       }
       
    }else{
    	return xy;
    }
}
function showBorder(obj){
	//dump(obj.style);
	obj.style.border = "1px solid #ff0000";
	//obj.style.borderColor = "red";
}
function MouseUP() {
   
	if (event.button==2) return ;
	//移动截至
	ismoving = false;
	line2.to="0,0";
	if (!window.parent.parent.canDrag){
		if (curDragSelectObjectId!=event.srcElement.parentElement.id){
			if (event.srcElement.className=="WorkFlowItem"){			
			    var TagName=event.srcElement.tagName;
			    var name=event.srcElement.name;
			    if (TagName=="oval"||TagName=="roundrect"||TagName=="rect"||name=="link"){
			    	line2.style.display='none';
			    	line1.style.display='none';
			       alert("不能画线");
			       return ;	
		        }
		    }
		} else {
			return;
		}
	}
	line1.style.display='none';
	line2.style.display='none';
	 
	if (canmove){
		
		if (parent.currentState==false){ 
		   //alert("对不起，你没有权限，你可以将该拓扑另存为。");
		   return;
		}
		//防止滑动
        ObjectEndX = (event.x  - tempOffsetX  + Chart.scrollLeft ) * scaleFactor2;  //-mapOffsetX
        ObjectEndY = (event.y - tempOffsetY  + Chart.scrollTop ) * scaleFactor2;   //- mapOffsetY
        var obj = activeConcept.getAttribute("id");
        dash = document.getElementById("showimg_dash");
        
        
        
        if (obj.indexOf("item_")>=0){
		    activeConcept.style.pixelLeft = moveGridPoint((event.x  - tempOffsetX  - 16 + Chart.scrollLeft ) * scaleFactor2);  //-mapOffsetX
        }else {
        	activeConcept.style.pixelLeft = moveGridPoint(dash.style.pixelLeft); //(event.x  - tempOffsetX + 320  + Chart.scrollLeft ) * scaleFactor2; 
        }
		activeConcept.style.pixelTop = moveGridPoint((event.y - tempOffsetY  + Chart.scrollTop ) * scaleFactor2 );//- mapOffsetY
		//showBorder(activeConcept);
		isChange = true;
		
		
		
	}
	var tempEnd=event.srcElement;
	
	if((tempEnd.className=="WorkFlowItem"  ||tempEnd.className=="NormalLine") && event.button==2 ) {
		document.all.item('SelDep').style.display='';
		if(tempEnd.className=="NormalLine") {
			 document.all.item('SelDep').style.display='none';
		}
		thisobj=event.srcElement;
	} else {
		if(tempEnd.className=="StartLine"||tempEnd.className=="WorkFlowItem") {
			if (tempEnd.className=="StartLine") {
				End2X=((event.x  - tempOffsetX -mapOffsetX + document.body.scrollLeft ) * scaleFactor+80);
		      	End2Y=((event.y - tempOffsetY - mapOffsetY + document.body.scrollTop ) * scaleFactor+40);
		   	  	toObject=null;
		   	  	if (!ContainLine(Starline,tempEnd)) {
        	  		var lin = new getLine(Starline,toObject); //new LineDot(Starline,toObject);
        	  		var name=event.srcElement.name;
        	  		var TagName=event.srcElement.tagName;
        	        if (TagName!="oval"&&TagName!="roundrect"&&TagName!="rect"&&name!="link") {
        	           DrawLine2(lin.x1,lin.y1,lin.x2,lin.y2,lineStyle,window.parent.parent.arrow,"");
        	           isChange = true;
        	        }
        	  	} else {
        	  		window.alert('无法建立关系，可能是关系已经存在或在同一节点上建立关系');
				}
		   	} else {
		    	if (candraw==true){
			    	var obj = event.srcElement.parentElement;
			        
			      	var endpointx=obj.style.pixelLeft+250;
	        	  	var endpointy=obj.style.pixelTop;
	        	  	toObject=obj.children[1];
	        	  	var toId=obj.getAttribute("id");
	        	  	var formId=Starline.parentElement.getAttribute("id");
	        	  	if (!ContainLine(Starline,toObject)) { 
	        	  		
	        	        var lin = new getLine(formId,toId);//(Starline,toObject); //new LineDot(formId,toId);
	        	        var name=event.srcElement.name;
	        	        var TagName=event.srcElement.tagName;
	        	        if (TagName!="oval"&&TagName!="roundrect"&&TagName!="rect"&&name!="link") {
	        	           //alert("x1:"+lin.x1+"|y1:"+lin.y1+"|x2:"+lin.x2+"|y2:"+lin.y2);
	        	           DrawLine2(lin.x1,lin.y1,lin.x2,lin.y2,lineStyle,window.parent.parent.arrow,"");
	        	           
	        	           isChange = true;
	        	           
	        	        }
	        	  	} else {
	        	  		window.alert('无法建立关系，可能是关系已经存在或在同一节点上建立关系');
	        	  	}
	        	    candraw=false;
	        	    toObject=null;
				}
			}
		}		
	}
	
	ShowAllLine();
	
	dash = document.getElementById("showimg_dash");
	dash.style.visibility = 'hidden';
}
var ismoving = false;
var edittx = null;
function hiderect(obj){
	
	if (parent.currentState==true&&window.parent.parent.canDrag==true){
		   obj.firstChild.style.display = "none";
		   edittx = obj.firstChild;
		
	}
}

function showrect(){
	
	if (parent.currentState==true){
		if ((window.parent.parent.canDrag==true&&ismoving == false)||(window.parent.parent.canDrag==false)){
			if (edittx!=null){
				edittx.style.display = "";
				edittx = null;
			}
		}
	}
	
}

//判断节点间是否已经有关系了
function ContainLine(fromline,Toline) {
	var allLine = document.body.all.item('ConnectLine');
	var i;
	
	if (allLine!=null) {
		var count=allLine.length;
		if (count) {
			for (i=count-1; i>=0; i--) {
				if((allLine[i].BeginShape==fromline.Refid && allLine[i].EndShape==Toline.Refid)||(allLine[i].BeginShape==Toline.Refid && allLine[i].EndShape==fromline.Refid)||fromline.Refid==Toline.Refid)
				return true;
			}
		} else {
			if((allLine.BeginShape==fromline.Refid && allLine.EndShape==Toline.Refid)||(allLine.BeginShape==Toline.Refid && allLine.EndShape==fromline.Refid)||fromline.Refid==Toline.Refid)
			return true;
		}
		return false;
	}
}
document.onclick = EndMove;
Chart.onmousedown = MoveStart;
Chart.onmouseup=MouseUP;



<!--
	var toObject=null;
	var allSelectedObject="";

	function ishave(objid){

    	if (allSelectedObject=="") {
    		return false;
    	}
    	var str=allSelectedObject;
    	str=","+allSelectedObject+",";
    	var str2=","+objid+",";
    	var i=str.indexOf(str2);
    	if (i==-1) {
    		return false;
    	} else {
    		alert("该对象已存在,请重新选择");
    		return true;
    	}
	}
	function DrawLine2(x1,y1,x2,y2,typeStr,lineType){
	    DrawLine2(x1,y1,x2,y2,typeStr,lineType,"");
	}
	
    function DrawLine2(x1,y1,x2,y2,typeStr,lineType,linename){
        
		var tempEndPoint;
		if (toObject==null) tempEndPoint=event.srcElement;
		else tempEndPoint=toObject;
		
		   //添加对象关联
		   ctx.get(DrawLineResult, "topoManager.drawLink('"+Starline.Refid+"','"+tempEndPoint.Refid+"','"+lineType+"','')");
    	
    	var strstroke=typeStr;
		var tempstroke=document.createElement(strstroke);
		var strElement="<v:line lineType='"+lineType+"' title='"+linename+"'  name='"+Starline.Refid+"."+tempEndPoint.Refid+"' id ='ConnectLine' class= NormalLine style=\"Z-INDEX:5;LEFT:" + x1 + ";POSITION:absolute;TOP:"+y1+"\" form='0,0' to=\"" + (x2-x1) + "," + (y2-y1) + "\"  EndShape="+tempEndPoint.Refid+" BeginShape="+Starline.Refid+"></v:line>";
        //alert(strElement);
    	var newPoint = document.createElement(strElement);
    	newPoint.appendChild(tempstroke);
    	WorkFlowGroup.appendChild(newPoint);
    	
    	/*  线上的字
    	var WorkFlowTextRect="<v:rect style=\"position:relative;left:-160px;top:320px;WIDTH:960px;HEIGHT:300px;\" filled=\"f\" fillcolor=\"white\" stroked=\"f\" />";
    	var WorkFlowTextRectElement=document.createElement(WorkFlowTextRect);
    	*/
    	
    }
    function DrawLineResult(data){
    }
    //画折线的函数
	function DrawLine() {
		var tempEndPoint = event.srcElement;
		var strstroke="<v:stroke color='bule' EndArrow='classic'></v:stroke>";
		var tempstroke=document.createElement(strstroke);
		var strElement="<v:polyline style='Z-Index:-1' points='"+line1.points.value+"' id ='ConnectLine' class= NormalLine  EndShape="+tempEndPoint.Refid+" BeginShape="+Starline.Refid+"></v:polyline>";
		var newPoint = document.createElement(strElement);
		newPoint.appendChild(tempstroke);
		WorkFlowGroup.appendChild(newPoint);
	}

	function DrawWorkFlowItem(id,name,x,y,icon,metadata,inheritType) {
	    
		
		DrawWorkFlowImage(id,name,x,y,icon,metadata,inheritType,"","true");
	}
	var SystemLoadStart = true;
	function DrawWorkFlowImage(id,name,x,y,icon,metadata,inheritType,tpid,state) {
		if (ishave(id)){    
		    return false;
		}
		var topoid=tpid;
		if (tpid == "undefined" ) topoid="";
		if (id == null) {
			name = "";
		} else {
			allSelectedObject += ","+id;
		}
		var tempg="<v:Group style=\"LEFT:"+10*x+"px;TOP:"+10*y+"px;WIDTH: 640px; POSITION: absolute; HEIGHT: 640px;z-index:5\" id =\"item_"+id+"\" coordsize=\"640,640\" onmousedown=\"selectWorkFlowItemGroup(this);\" ></v:Group>";	
		var tempgroupel;
		tempgroupel=document.createElement(tempg);
		WorkFlowGroup.appendChild(tempgroupel);

		var tempinLine="<v:line Refid='item_"+id+"' class=\"EndLine\" Style=\"cursor:hand;;display:none\" from=\"320,500\" to=\"320,540\"></v:line>";
		var tempinLineElement=document.createElement(tempinLine);
		var tempinLineStyle="<v:stroke color=\"black\" EndArrow=\"Diamond\"></v:stroke>";
		tempinLineElement.appendChild(document.createElement(tempinLineStyle));
		tempgroupel.appendChild(tempinLineElement);

		var tempoutLine="<v:line Refid='item_"+id+"' class=\"StartLine\" Style=\"Cursor:cross;display:none\" from=\"250,40\" to=\"250,100\"></v:line>";
		var tempoutLineElement=document.createElement(tempoutLine);
		var tempoutLineStyle="<v:stroke color=\"black\" StartArrow=\"oval\"></v:stroke>";
		tempoutLineElement.appendChild(document.createElement(tempoutLineStyle));
		tempgroupel.appendChild(tempoutLineElement);

		var WorkFlowImages = "<v:image Refid='item_"+id+"' tpid='"+topoid+"' moid='"+id.replace(/_/g,".")+"' class=\"WorkFlowItem\" metadata='"+metadata+"' inheritType='"+inheritType+"' title='' objectname='"+name+"' view='false' child='' id='showimg_"+id+"' name=\"showimg\" src=\""+icon+"32\" style=\"position:relative;top:0px;left:160px;WIDTH:320px;HEIGHT:320px;\" ";
		if (inheritType != "") WorkFlowImages += " onclick=\"parent.enter('"+id+"',this);\" onmousedown=\"\" " ;
		WorkFlowImages += " onmouseover=\"MoveOver()\"/>";
		tempgroupel.appendChild(document.createElement(WorkFlowImages));

		var WorkFlowAlarm="<v:image class=\"WorkFlowItem\" id=\"showalarm_"+id+"\" style=\"LEFT:320px;WIDTH:120px;POSITION:relative;TOP:220px;HEIGHT:120px\"  src=\"\"/>";
		tempgroupel.appendChild(document.createElement(WorkFlowAlarm));
		
		var WorkFlowLock = "<v:image class=\"WorkFlowItem\" id=\"showlock_"+id+"\" style=\"LEFT:160px;WIDTH:120px;POSITION:relative;TOP:220px;HEIGHT:120px\"  src=\"\"/>";
		tempgroupel.appendChild(document.createElement(WorkFlowLock));
		
		
		var stateimg = "";
		if (state == "false") stateimg = "/sysmgr/images/icons/disable_b.gif";
		
		var WorkFlowState="<v:image class=\"WorkFlowItem\" id=\"showstatus_"+id+"\" style=\"LEFT:160px;WIDTH:120px;POSITION:relative;TOP:0px;HEIGHT:120px\"  src=\"" + stateimg + "\" />";
		tempgroupel.appendChild(document.createElement(WorkFlowState));
		
		var WorkFlowTextRect="<v:rect  style=\"position:relative;left:-160px;top:320px;WIDTH:960px;HEIGHT:350px;\" filled=\"f\" fillcolor=\"white\" stroked=\"f\" />";
		
		var WorkFlowTextRectElement=document.createElement(WorkFlowTextRect);
		tempgroupel.appendChild(WorkFlowTextRectElement);
		
		var WorkFlowTextCenter="<center></center>";
		var WorkFlowTextCenterElement=document.createElement(WorkFlowTextCenter);
		WorkFlowTextRectElement.appendChild(WorkFlowTextCenterElement);
		
		var WorkFlowText="<v:TextBox style='FONT-SIZE:12px' id='showtext_"+id+"'></v:TextBox>";
		var WorkFlowTextElement=document.createElement(WorkFlowText);
		WorkFlowTextCenterElement.appendChild(WorkFlowTextElement);
        
		var hf="";
		if (tpid!="" ){
		   hf=" href='ViewEdit.jsp?id="+tpid+"' onclick=\"parent.enter('"+id+"',showimg_"+id+");return pageGoto('"+tpid+"')\" style=\"color: Blue;\"";
		}else{
		   hf = " onclick=\"parent.enter('"+id+"',showimg_"+id+");\" ";
		}
		if (inheritType == "") hf = "";
		//onclick=\"updateItems('"+id+"');\"  style=\"color:blue\"
		var WorkFlowTextFont = "<a id='font_"+id+"' title='"+name+"' "+hf+" ></a>";
		var WorkFlowTextFontElement = document.createElement(WorkFlowTextFont);
		WorkFlowTextElement.appendChild(WorkFlowTextFontElement);
		
		WorkFlowTextFontElement.appendChild(document.createTextNode(filterStr(name,20)));
	}

	function DrawProtocolImage(id,name,x,y,icon) {
		if (ishave(id)){    
		    return false;
		}
		if (id == null) {
			name = "";
		} else {
			allSelectedObject += ","+id;
		}
		var tempg="<v:Group style=\"LEFT:"+10*x+"px;TOP:"+10*y+"px;WIDTH: 640px; POSITION: absolute; HEIGHT: 640px;z-index:5\" id =\"item_"+id+"\" coordsize=\"640,640\" onmousedown=\"selectWorkFlowItemGroup(this);\" ></v:Group>";	
		var tempgroupel;
		tempgroupel=document.createElement(tempg);
		WorkFlowGroup.appendChild(tempgroupel);

		var tempinLine="<v:line Refid='item_"+id+"' class=\"EndLine\" Style=\"cursor:hand;\" from=\"160,600\" to=\"160,680\"></v:line>";
		var tempinLineElement=document.createElement(tempinLine);
		var tempinLineStyle="<v:stroke color=\"black\" EndArrow=\"classic\"></v:stroke>";
		tempinLineElement.appendChild(document.createElement(tempinLineStyle));
		tempgroupel.appendChild(tempinLineElement);

		var tempoutLine="<v:line Refid='item_"+id+"' class=\"StartLine\" Style=\"Cursor:cross;display:none\" from=\"250,40\" to=\"250,100\"></v:line>";
		var tempoutLineElement=document.createElement(tempoutLine);
		var tempoutLineStyle="<v:stroke color=\"black\" StartArrow=\"oval\"></v:stroke>";
		tempoutLineElement.appendChild(document.createElement(tempoutLineStyle));
		tempgroupel.appendChild(tempoutLineElement);

		var WorkFlowImages="<v:image class=\"WorkFlowItem\" view='false' child='' id='showimg_"+id+"' name=\"showimg\" src=\""+icon+"32\" style=\"position:relative;top:0px;left:160px;WIDTH:320px;HEIGHT:320px;\" onclick=\"\" ondblclick=\"parent.enter('"+id+"');\" onmousedown=\"\" onmouseover=\"MoveOver()\"/>";
		tempgroupel.appendChild(document.createElement(WorkFlowImages));

		var WorkFlowAlarm="<v:image class=\"WorkFlowItem\" id=\"showalarm_"+id+"\" style=\"LEFT:0px;WIDTH:80px;POSITION:relative;TOP:0px;HEIGHT:80px\" src=\"\"/>";
		tempgroupel.appendChild(document.createElement(WorkFlowAlarm));

		var WorkFlowTextRect="<v:rect style=\"position:relative;left:-160px;top:320px;WIDTH:960px;HEIGHT:300px;\"  fillcolor=\"white\" stroked=\"f\" />";
		
		var WorkFlowTextRectElement=document.createElement(WorkFlowTextRect);
		tempgroupel.appendChild(WorkFlowTextRectElement);
		
		var WorkFlowTextCenter="<center></center>";
		var WorkFlowTextCenterElement=document.createElement(WorkFlowTextCenter);
		WorkFlowTextRectElement.appendChild(WorkFlowTextCenterElement);
		
		var WorkFlowText="<v:TextBox style='FONT-SIZE:12px' id='showtext_"+id+"'></v:TextBox>";
		var WorkFlowTextElement=document.createElement(WorkFlowText);
		WorkFlowTextCenterElement.appendChild(WorkFlowTextElement);
		
		var WorkFlowTextFont="<font id='font_"+id+"'></font>";
		var WorkFlowTextFontElement=document.createElement(WorkFlowTextFont);
		WorkFlowTextElement.appendChild(WorkFlowTextFontElement);

		WorkFlowTextFontElement.appendChild(document.createTextNode(name));
	}

	function DeleteRelLine(WorkFlowItem) {
		
		var tempitemid=WorkFlowItem.id;
		WorkFlowItem.outerHTML="";
		var allLine = document.body.all.item('ConnectLine');
		var i;
		
		if (allLine!=null) {
			var count=allLine.length;
			if (count) {
				for (i=count-1; i>=0; i--) {
					if(allLine[i].BeginShape==tempitemid||allLine[i].EndShape==tempitemid){
					   //移走对象不一定删除关系
					   //ctx.get(RemoveLinkResult, "topoManager.removeLink('"+allLine[i].BeginShape+"','"+allLine[i].EndShape+"','"+allLine[i].lineType+"')");
					   allLine[i].outerHTML="";
					}
				}
			} else {
				if(allLine.BeginShape==tempitemid|| allLine.EndShape==tempitemid){
				   //移走对象不一定删除关系
				   //ctx.get(RemoveLinkResult, "topoManager.removeLink('"+allLine.BeginShape+"','"+allLine.EndShape+"','"+allLine.lineType+"')");
				   allLine.outerHTML="";
				}
			}
		}
	}
	
	
//-->
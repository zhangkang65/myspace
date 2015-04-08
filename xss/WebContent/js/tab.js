
var __tabs = new Array();
var lastTab = null;		
/*
??????table??????
<body onLoad="initTabs();" style="overflow:hidden" onresize="initTabs();">

<table width="100%" height="100%" border="0"	cellpadding="0" cellspacing="0">
	<colgroup width="*" />
	<tbody id="tabBody">
	<tr id="main" height="*">
	<td><div title="t1" class="tab" id="t1"></div> 
	    <div title="t2" class="tab" id="t2"></div></td>
	</tr>
</tbody>		
</table>
*/
//
function initTabs(tabId) {
			//alert("init");

			var tabBar = document.getElementById("tabBar");
			var buttonBar = document.getElementById("buttonBar");
			var _divs = document.getElementsByTagName("div");
			var main = document.getElementById("main");
			var tabBody = document.getElementById("tabBody");
			if(tabBar)
				tabBody.removeChild(tabBar);
			if(buttonBar)
				tabBody.removeChild(buttonBar);
				
			//document.body.style.overflow="hidden";
			
			var divs = new Array();
			for(var i=0; i<_divs.length; i++) {
				if(_divs[i].className == "tab") {
					divs[divs.length] = _divs[i];
				}
			}		
			//alert(divs.length);
			if(divs.length > 1) {
				tabBar = document.createElement("tr");
				tabBar.id = "tabBar";
				tabBody.appendChild(tabBar);
				
				var td = document.createElement("td");
				td.vAlign = "bottom";
				td.height = 24;				
				tabBar.appendChild(td);
				
				var tabContainer = document.createElement("table");

				tabContainer.id = "tabContainer";
				tabContainer.cellSpacing = "0";	
				tabContainer.cellPadding = "0";
				tabContainer.border = "0";
				tabContainer.width="100%";
				td.appendChild(tabContainer);
				
				var tbody = document.createElement("tbody");
				tabContainer.appendChild(tbody);
				
				var tr = document.createElement("tr");
				tbody.appendChild(tr);
				lastTab = null;
				__tabs.length = 0;
				for(var i=0; i<divs.length; i++) {
					__tabs[__tabs.length] = divs[i];
					divs[i].style.position = "absolute";
					divs[i].style.overflow = "auto";
					divs[i].style.height=(window.frameElement.offsetHeight-24)+"px";
					//divs[i].style.visibility= "hidden";	
					divs[i].style.display="none";
					
					var sep = document.createElement("td");
					sep.className = "separator";
					sep.style.width="1px";
					var idiv = document.createElement("div");
					idiv.style.width="1px";
					idiv.style.height="1px";
					idiv.style.display="block";
					sep.appendChild(idiv);
					tr.appendChild(sep);
					
					var button = document.createElement("td");
					button.className = "npressed";
					button.id = "btn_"+divs[i].id;
					button.innerHTML = divs[i].title;
					button.onclick = new Function("	showTab('"+divs[i].id+"');");
					tr.appendChild(button);					
				}
				var sep = document.createElement("td");
				sep.className = "separator";
				sep.style.width="1px";
				var idiv = document.createElement("div");
				idiv.style.width="1px";
				idiv.style.height="1px";
				idiv.style.display="block";
				sep.appendChild(idiv);
				tr.appendChild(sep);
				
				var button = document.createElement("td");
				button.className = "tab-empty";
				button.width="100%";
				button.innerHTML="&nbsp;";
				tr.appendChild(button);
				
				if(tabId=="")
				{
					var ViewTabId=__tabs[0].id;
					for(var i=0; i<__tabs.length; i++) 
					{
						if(__tabs[i].style.display == "")
						{
							ViewTabId=__tabs[i].id;
							break;
						}	
					}
					//alert("showTab: "+ViewTabId);
					showTab(ViewTabId,"");
				}
				else
				{
					//alert("showTab: "+tabId);
					showTab(tabId,"");
				}	
			} else if(divs.length == 1){
				//divs[0].style.visibility = "visible"; 
				divs[0].style.display = "";
			}
			
			
		}
	
	/* 
 * Switch tabs.
 */ 
function showTab(tab, isManual)
{ 	
	//alert(tab);
	if(lastTab == tab)
		return;
		
	lastTab = tab;
	
	var table = document.getElementById("tabContainer");
	
 	// show the appropriate pressed tab
  	var buttons = table.getElementsByTagName("TD");
  	for (var i=0; i<buttons.length; i++)
  	{
  		if (buttons[i].id == "btn_"+tab && __tabs.length > 1) { 
			buttons[i].className = "pressed";
			buttons[i-1].className = (i-1)==0? "left_separator_pressed":"separator_pressed";
			buttons[i+1].className = "separator_pressed";
		} else if (buttons[i].className == "pressed") {
			buttons[i].className = "npressed";
			if (i > 1 && buttons[i-2].id =="btn_"+ tab) 
				buttons[i-1].className = "separator_pressed";
			else
				buttons[i-1].className = "separator";
			if (i<buttons.length-2 && buttons[i+2].id == "btn_"+tab) 
				buttons[i+1].className = "separator_pressed";
			else
				buttons[i+1].className = "separator";
		}
 	 }
	 
	 for(var i=0; i<__tabs.length; i++) {
			if(__tabs[i].id == tab) {
				//__tabs[i].style.visibility = "visible";
				__tabs[i].style.display = "";
				if(document.all) //IE??bug, ????????
					__tabs[i].savedTop = __tabs[i].style.top;
					__tabs[i].style.top = 0;				
			} else {
				__tabs[i].style.display = "none";
				if(document.all) //IE??bug, ????????
				  if(__tabs[i].savedTop)
						__tabs[i].style.top = __tabs[i].savedTop;				
			}
		}
		//alert("end");
	}
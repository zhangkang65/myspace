
function mouseOverQQMenu(menuid,qqfont){
   menuid.className="MenuItemOver";
   qqfont.color="blue";
   
}
function mouseOutQQMenu(menuid,qqfont){
   qqfont.color="#000000";
   menuid.className="MenuItemOut";
}


function showQQMenu(id) 
{   
  try{
   if (event.button==1) return ;
	var qqmenu=eval(id);
	var rightedge = document.body.clientWidth-event.clientX;
	var bottomedge = document.body.clientHeight-event.clientY;
	 
	if (rightedge < qqmenu.offsetWidth)
		qqmenu.style.left = document.body.scrollLeft + event.clientX - qqmenu.offsetWidth;
	else
		qqmenu.style.left = document.body.scrollLeft + event.clientX;
	if (bottomedge < qqmenu.offsetHeight)
		qqmenu.style.top = document.body.scrollTop + event.clientY - qqmenu.offsetHeight;
	else
		qqmenu.style.top = document.body.scrollTop + event.clientY;
		
		qqmenu.style.display = "";
	return false;
  }catch(e){}
}


function windowevent(){
  if (document.all && window.print) {
     document.body.onclick = hideQQMenu;
  }
}

function hideQQMenu(){
  try{
  	var qqmenu=eval("Menu");
  	var e= window.event.srcElement.name;
  	if (e != "showimg"){
     qqmenu.style.display = "none";
  	}
  }catch(e){}
}

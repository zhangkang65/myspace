document.writeln('<div id=meizzDateLayer style="position: absolute; width: 165; height: 195; z-index: 9998; display: none" onselectstart="return false"><iframe  frameborder=0 width=165 height=195; id="dayframe" src="/sysmgr/js/setdaytime.jsp" scrolling="no"></iframe></div>');


function setday(tt,layer,obj) //Ö÷µ÷º¯Êý
{
	document.getElementById('dayframe').contentWindow.setday(tt, layer, obj);
}


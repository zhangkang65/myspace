<!--
//----------------------------------------------------------------------------

//  这是我做的一个日历 Javascript 页面脚本控件，适用于微软的 IE （5.0以上）浏览器
//  主调用函数是 setday(this,[object])和setday(this)，[object]是控件输出的控件名，举两个例子：
//  一、<input name=txt><input type=button value=setday onclick="setday(this,document.all.txt)">
//  二、<input onfocus="setday(this)">
//  若有什么不足的地方，或者您有更好的建议，请与我联系：mail: meizz@hzcnc.com
//  本日历的年份限制是（1000 - 9999）
//  按ESC键关闭该控件
//  在年和月的显示地方点击时会分别出年与月的下拉框
//  控件外任意点击一点即可关闭该控件
/* 以下为walkingpoison的修改说明
walkingpoison联系方式：wayx@kali.com.cn

Ver 1.3
修改日期：2002-11-29
修改内容：
1.*空白部分用灰色显示上个月的最后几天和下个月的前几天
2.*每个日期上面加上鼠标提示
3.修改使得当前日期和当前选择的日期的背景色在灰色日期部分也能正常显示

Ver 1.2
修改日期：2002-11-28
修改内容：
1.*修改年和月的点击都把中文包含在内，增大点击的空间
2.当前选择的日期在列表中显示不同的背景色
3.修正了点击单元格之间的分隔线导致控件关闭的问题

Ver 1.1
修改日期：2002-11-15
修改内容：
1.修正了方法二按Esc键关闭以后再次点击不会显示日历的问题
2.点击today直接选中当前的日期并关闭控件
3.*如果调用控件的输入框含有合法日期，则自动显示输入框的日期部分。
4.修改程序统一使用关闭的函数closeLayer()来关闭日历控件，这样可以通过自定义关闭函数来完成用户自定义的功能。
例如为了在显示日历的时候隐藏select框（select框永远在层的上面，所以显示层的时候需要隐藏），需要定义关闭层的时候再显示select框
自定义的方法是在引用这个文件的后面加上对关闭函数的重定义
示例：
<scr ipt>
function closeLayer()               //自定义日历层的关闭
  {
    document.all.meizzDateLayer.style.display="none";
    //下面放置自定义的代码，在这一示例中，还需要在调用setday函数前面加上隐藏select的代码：form1.select1.style.display='none'
    form1.select1.style.display='';
    //自定义代码结束
  }

</scr ipt>

注：*号表示比较关键的改动

本控件还需要改进的部分：
1.可能还有一些比较小的bug和操作不便的地方
*/
//==================================================== WEB 页面显示部分 =====================================================
document.writeln('<div id=meizzDateLayer style="position: absolute; width: 142; height: 166; z-index: 9998; display: none" onselectstart="return false">');
document.writeln('<span id=tmpSelectYearLayer  style="z-index: 9999;position: absolute;top: 2; left: 18;display: none"></span>');
document.writeln('<span id=tmpSelectMonthLayer style="z-index: 9999;position: absolute;top: 2; left: 75;display: none"></span>');
document.writeln('<table border=0 cellspacing=1 cellpadding=0 width=142 height=160 bgcolor=#21284A Author="wayx">');
document.writeln('  <tr Author="wayx"><td width=142 height=23 Author="wayx" bgcolor=#FFFFFF><table border=0 cellspacing=1 cellpadding=0 width=140 Author="wayx" height=23>');
document.writeln('      <tr align=center Author="wayx"><td width=16 align=center bgcolor=#21284A style="font-size:12px;cursor: hand;color: #B5BE63" ');
document.writeln('        onclick="meizzPrevM()" title="向前翻 1 月" Author=meizz><b Author=meizz>&lt;</b>');
document.writeln('        </td><td width=108 align=center style="font-size:12px;cursor:default" Author=meizz><span ');
document.writeln('onmouseover="style.backgroundColor=\'#B5BE63\'" onmouseout="style.backgroundColor=\'white\'" title="点击这里选择年份"');
document.writeln('         Author=meizz id=meizzYearHead onclick="tmpSelectYearInnerHTML(this.innerText.substring(0,4))"></span>&nbsp;&nbsp;<span');
document.writeln('onmouseover="style.backgroundColor=\'#B5BE63\'" onmouseout="style.backgroundColor=\'white\'" title="点击这里选择月份"');
document.writeln('         id=meizzMonthHead Author=meizz onclick="tmpSelectMonthInnerHTML(this.innerText.length==3?this.innerText.substring(0,1):this.innerText.substring(0,2))"></span></td>');
document.writeln('        <td width=16 bgcolor=#21284A align=center style="font-size:12px;cursor: hand;color: #B5BE63" ');
document.writeln('         onclick="meizzNextM()" title="往后翻 1 月" Author=meizz><b Author=meizz>&gt;</b></td></tr>');
document.writeln('    </table></td></tr>');
document.writeln('  <tr Author="wayx"><td width=142 height=18 bgcolor=#21284A Author="wayx">');
document.writeln('<table border=0 cellspacing=0 cellpadding=0 width=140 height=1 Author="wayx" style="cursor:default">');
document.writeln('<tr Author="wayx" align=center><td style="font-size:12px;color:#FFFFFF" Author=meizz>日</td>');
document.writeln('<td style="font-size:12px;color:#FFFFFF" Author=meizz>一</td><td style="font-size:12px;color:#FFFFFF" Author=meizz>二</td>');
document.writeln('<td style="font-size:12px;color:#FFFFFF" Author=meizz>三</td><td style="font-size:12px;color:#FFFFFF" Author=meizz>四</td>');
document.writeln('<td style="font-size:12px;color:#FFFFFF" Author=meizz>五</td><td style="font-size:12px;color:#FFFFFF" Author=meizz>六</td></tr>');
document.writeln('</table></td></tr><!-- Author:F.R.Huang(meizz) http://www.meizz.com/ mail: meizz@hzcnc.com 2002-10-8 -->');
document.writeln('  <tr Author="wayx"><td width=142 height=120 Author="wayx">');
document.writeln('    <table border=0 cellspacing=1 cellpadding=0 width=140 height=120 Author="wayx" bgcolor=#FFFFFF>');
var n=0; for (j=0;j<5;j++){ document.writeln (' <tr align=center Author="wayx">'); for (i=0;i<7;i++){
document.writeln('<td width=20 height=20 id=meizzDay'+n+' style="font-size:12px" Author=meizz onclick=meizzDayClick(this.innerText,0)></td>');n++;}
document.writeln('</tr>');}
document.writeln('      <tr align=center Author="wayx">');
for (i=35;i<39;i++)document.write('<td width=20 height=20 id=meizzDay'+i+' style="font-size:12px" Author=wayx onclick="meizzDayClick(this.innerText,0)"></td>');
document.writeln('        <td colspan=3 align=right Author=meizz><span onclick=closeLayer() style="font-size:12px;cursor: hand"');
document.writeln('         Author=meizz title="Version:1.3&#13;作者: F.R.Huang(meizz)&#13;MAIL: meizz@hzcnc.com&#13;修改:walkingpoison"><u>关闭</u></span>&nbsp;</td></tr>');
document.writeln('    </table></td></tr><tr Author="wayx"><td Author="wayx">');
document.writeln('        <table border=0 cellspacing=1 cellpadding=0 width=100% Author="wayx" bgcolor=#FFFFFF>');
document.writeln('          <tr Author="wayx"><td Author=meizz align=left><input Author=meizz type=button value="<<" title="向前翻 1 年" onclick="meizzPrevY()" ');
document.writeln('             onfocus="this.blur()" style="font-size: 12px; height: 20px"><input Author=meizz title="向前翻 1 月" type=button ');
document.writeln('             value="< " onclick="meizzPrevM()" onfocus="this.blur()" style="font-size: 12px; height: 20px"></td><td ');
document.writeln('             Author=meizz align=center><input Author=meizz type=button value=Today onclick="meizzToday()" ');
document.writeln('             onfocus="this.blur()" title="当前日期" style="font-size: 12px; height: 20px"></td><td ');
document.writeln('             Author=meizz align=right><input Author=meizz type=button value=" >" onclick="meizzNextM()" ');
document.writeln('             onfocus="this.blur()" title="往后翻 1 月" style="font-size: 12px; height: 20px"><input ');
document.writeln('             Author=meizz type=button value=">>" title="往后翻 1 年" onclick="meizzNextY()"');
document.writeln('             onfocus="this.blur()" style="font-size: 12px; height: 20px"></td>');
document.writeln('</tr></table></td></tr></table></div>');
//==================================================== WEB 页面显示部分 ======================================================
var outObject;
var outDate="";		//存放对象的日期
function setday(tt,obj) //主调函数
{
	if (arguments.length >  2){alert("对不起！传入本控件的参数太多！");return;}
	if (arguments.length == 0){alert("对不起！您没有传回本控件任何参数！");return;}
	var dads  = document.all.meizzDateLayer.style;var th = tt;
	var ttop  = tt.offsetTop;     //TT控件的定位点高
	var thei  = tt.clientHeight;  //TT控件本身的高
	var tleft = tt.offsetLeft;    //TT控件的定位点宽
	var ttyp  = tt.type;          //TT控件的类型
	while (tt = tt.offsetParent){ttop+=tt.offsetTop; tleft+=tt.offsetLeft;}
	dads.top  = (ttyp=="image")? ttop+thei : ttop+thei+6;
	dads.left = tleft;
	outObject = (arguments.length == 1) ? th : obj;
	//根据当前输入框的日期显示日历的年月
	var reg = /^(\d+)-(\d{1,2})-(\d{1,2})$/; 
	var r = outObject.value.match(reg); 
	if(r!=null){
		r[2]=r[2]-1; 
		var d= new Date(r[1], r[2],r[3]); 
		if(d.getFullYear()==r[1] && d.getMonth()==r[2] && d.getDate()==r[3]){
			outDate=d;		//保存外部传入的日期
		}
		else outDate="";
			meizzSetDay(r[1],r[2]+1);
	}
	else{
		outDate="";
		meizzSetDay(new Date().getFullYear(), new Date().getMonth() + 1);
	}
	dads.display = '';
	event.returnValue=false;
}

var MonHead = new Array(12);    		   //定义阳历中每个月的最大天数
    MonHead[0] = 31; MonHead[1] = 28; MonHead[2] = 31; MonHead[3] = 30; MonHead[4]  = 31; MonHead[5]  = 30;
    MonHead[6] = 31; MonHead[7] = 31; MonHead[8] = 30; MonHead[9] = 31; MonHead[10] = 30; MonHead[11] = 31;

var meizzTheYear=new Date().getFullYear(); //定义年的变量的初始值
var meizzTheMonth=new Date().getMonth()+1; //定义月的变量的初始值
var meizzWDay=new Array(39);               //定义写日期的数组

function document.onclick() //任意点击时关闭该控件
{ 
  with(window.event.srcElement)
  { if (getAttribute("Author")==null && tagName != "INPUT")
    closeLayer();
    //outObject.value=tagName + "-" + getAttribute("Author");	//用来调试点击关闭功能
  }
}

function meizzWriteHead(yy,mm)  //往 head 中写入当前的年与月
  { document.all.meizzYearHead.innerText  = yy + " 年";
    document.all.meizzMonthHead.innerText = mm + " 月";
  }
function tmpSelectYearInnerHTML(strYear) //年份的下拉框
{
  if (strYear.match(/\D/)!=null){alert("年份输入参数不是数字！");return;}
  var m = (strYear) ? strYear : new Date().getFullYear();
  if (m < 1000 || m > 9999) {alert("年份值不在 1000 到 9999 之间！");return;}
  var n = m - 10;
  if (n < 1000) n = 1000;
  if (n + 26 > 9999) n = 9974;
  var s = "<select Author=meizz name=tmpSelectYear style='font-size: 12px' "
     s += "onblur='document.all.tmpSelectYearLayer.style.display=\"none\"' "
     s += "onchange='document.all.tmpSelectYearLayer.style.display=\"none\";"
     s += "meizzTheYear = this.value; meizzSetDay(meizzTheYear,meizzTheMonth)'>\r\n";
  var selectInnerHTML = s;
  for (var i = n; i < n + 26; i++)
  {
    if (i == m)
       {selectInnerHTML += "<option value='" + i + "' selected>" + i + "年" + "</option>\r\n";}
    else {selectInnerHTML += "<option value='" + i + "'>" + i + "年" + "</option>\r\n";}
  }
  selectInnerHTML += "</select>";
  document.all.tmpSelectYearLayer.style.display="";
  document.all.tmpSelectYearLayer.innerHTML = selectInnerHTML;
  document.all.tmpSelectYear.focus();
}

function tmpSelectMonthInnerHTML(strMonth) //月份的下拉框
{
  if (strMonth.match(/\D/)!=null){alert("月份输入参数不是数字！");return;}
  var m = (strMonth) ? strMonth : new Date().getMonth() + 1;
  var s = "<select Author=meizz name=tmpSelectMonth style='font-size: 12px' "
     s += "onblur='document.all.tmpSelectMonthLayer.style.display=\"none\"' "
     s += "onchange='document.all.tmpSelectMonthLayer.style.display=\"none\";"
     s += "meizzTheMonth = this.value; meizzSetDay(meizzTheYear,meizzTheMonth)'>\r\n";
  var selectInnerHTML = s;
  for (var i = 1; i < 13; i++)
  {
    if (i == m)
       {selectInnerHTML += "<option value='"+i+"' selected>"+i+"月"+"</option>\r\n";}
    else {selectInnerHTML += "<option value='"+i+"'>"+i+"月"+"</option>\r\n";}
  }
  selectInnerHTML += "</select>";
  document.all.tmpSelectMonthLayer.style.display="";
  document.all.tmpSelectMonthLayer.innerHTML = selectInnerHTML;
  document.all.tmpSelectMonth.focus();
}

function closeLayer()               //这个层的关闭
  {
    document.all.meizzDateLayer.style.display="none";
  }

function document.onkeydown()		//按Esc键关闭
  {
    if (window.event.keyCode==27){
		closeLayer();
		outObject.blur();
	}
  }

function IsPinYear(year)            //判断是否闰平年
  {
    if (0==year%4&&((year%100!=0)||(year%400==0))) return true;else return false;
  }

function GetMonthCount(year,month)  //闰年二月为29天
  {
    var c=MonHead[month-1];if((month==2)&&IsPinYear(year)) c++;return c;
  }

function GetDOW(day,month,year)     //求某天的星期几
  {
    var dt=new Date(year,month-1,day).getDay()/7; return dt;
  }

function meizzPrevY()  //往前翻 Year
  {
    if(meizzTheYear > 999 && meizzTheYear <10000){meizzTheYear--;}
    else{alert("年份超出范围（1000-9999）！");}
    meizzSetDay(meizzTheYear,meizzTheMonth);
  }
function meizzNextY()  //往后翻 Year
  {
    if(meizzTheYear > 999 && meizzTheYear <10000){meizzTheYear++;}
    else{alert("年份超出范围（1000-9999）！");}
    meizzSetDay(meizzTheYear,meizzTheMonth);
  }
function meizzToday()  //Today Button
  {
	var today;
    meizzTheYear = new Date().getFullYear();
    meizzTheMonth = new Date().getMonth()+1;
    today=new Date().getDate();
    //meizzSetDay(meizzTheYear,meizzTheMonth);
    if(outObject){
		outObject.value=meizzTheYear + "-" + meizzTheMonth + "-" + today;
    }
    closeLayer();
  }
function meizzPrevM()  //往前翻月份
  {
    if(meizzTheMonth>1){meizzTheMonth--}else{meizzTheYear--;meizzTheMonth=12;}
    meizzSetDay(meizzTheYear,meizzTheMonth);
  }
function meizzNextM()  //往后翻月份
  {
    if(meizzTheMonth==12){meizzTheYear++;meizzTheMonth=1}else{meizzTheMonth++}
    meizzSetDay(meizzTheYear,meizzTheMonth);
  }

function meizzSetDay(yy,mm)   //主要的写程序**********
{
  meizzWriteHead(yy,mm);
  //设置当前年月的公共变量为传入值
  meizzTheYear=yy;
  meizzTheMonth=mm;
  
  for (var i = 0; i < 39; i++){meizzWDay[i]=""};  //将显示框的内容全部清空
  var day1 = 1,day2=1,firstday = new Date(yy,mm-1,1).getDay();  //某月第一天的星期几
  for (i=0;i<firstday;i++)meizzWDay[i]=GetMonthCount(mm==1?yy-1:yy,mm==1?12:mm-1)-firstday+i+1	//上个月的最后几天
  for (i = firstday; day1 < GetMonthCount(yy,mm)+1; i++){meizzWDay[i]=day1;day1++;}
  for (i=firstday+GetMonthCount(yy,mm);i<39;i++){meizzWDay[i]=day2;day2++}
  for (i = 0; i < 39; i++)
  { var da = eval("document.all.meizzDay"+i)     //书写新的一个月的日期星期排列
    if (meizzWDay[i]!="")
      { 
		if(i<firstday)		//上个月的部分
		{
			da.innerHTML="<b><font color=gray>" + meizzWDay[i] + "</font></b>";
			da.title=(mm==1?12:mm-1) +"月" + meizzWDay[i] + "日";
			da.onclick=Function("meizzDayClick(this.innerText,-1)");
			if(!outDate)
				da.style.backgroundColor = ((mm==1?yy-1:yy) == new Date().getFullYear() && 
					(mm==1?12:mm-1) == new Date().getMonth()+1 && meizzWDay[i] == new Date().getDate()) ?
					 "#B5BE63":"#EAEAEA";
			else
				da.style.backgroundColor =((mm==1?yy-1:yy)==outDate.getFullYear() && (mm==1?12:mm-1)== outDate.getMonth() + 1 && 
				meizzWDay[i]==outDate.getDate())? "#42678C" :
				(((mm==1?yy-1:yy) == new Date().getFullYear() && (mm==1?12:mm-1) == new Date().getMonth()+1 && 
				meizzWDay[i] == new Date().getDate()) ? "#B5BE63":"#EAEAEA");
		}
		else if (i>=firstday+GetMonthCount(yy,mm))		//下个月的部分
		{
			da.innerHTML="<b><font color=gray>" + meizzWDay[i] + "</font></b>";
			da.title=(mm==12?1:mm+1) +"月" + meizzWDay[i] + "日";
			da.onclick=Function("meizzDayClick(this.innerText,1)");
			if(!outDate)
				da.style.backgroundColor = ((mm==12?yy+1:yy) == new Date().getFullYear() && 
					(mm==12?1:mm+1) == new Date().getMonth()+1 && meizzWDay[i] == new Date().getDate()) ?
					 "#B5BE63":"#EAEAEA";
			else
				da.style.backgroundColor =((mm==12?yy+1:yy)==outDate.getFullYear() && (mm==12?1:mm+1)== outDate.getMonth() + 1 && 
				meizzWDay[i]==outDate.getDate())? "#42678C" :
				(((mm==12?yy+1:yy) == new Date().getFullYear() && (mm==12?1:mm+1) == new Date().getMonth()+1 && 
				meizzWDay[i] == new Date().getDate()) ? "#B5BE63":"#EAEAEA");
		}
		else		//本月的部分
		{
			da.innerHTML="<b>" + meizzWDay[i] + "</b>";
			da.title=mm +"月" + meizzWDay[i] + "日";
			da.onclick=Function("meizzDayClick(this.innerText,0)");		//给td赋予onclick事件的处理
			//如果是当前选择的日期，则显示亮绿色的背景；如果是当前日期，则显示暗黄色背景
			if(!outDate)
				da.style.backgroundColor = (yy == new Date().getFullYear() && mm == new Date().getMonth()+1 && meizzWDay[i] == new Date().getDate())?
					"#B5BE63":"#EAEAEA";
			else
				da.style.backgroundColor =(yy==outDate.getFullYear() && mm== outDate.getMonth() + 1 && meizzWDay[i]==outDate.getDate())?
					"#42678C":((yy == new Date().getFullYear() && mm == new Date().getMonth()+1 && meizzWDay[i] == new Date().getDate())?
					"#B5BE63":"#EAEAEA");
		}
        
        da.style.cursor="hand"
      }
    else{da.innerHTML="";da.style.backgroundColor="";da.style.cursor="default"}
  }
}

function meizzDayClick(n,ex)  //点击显示框选取日期，主输入函数*************
{
  var yy=meizzTheYear;
  var mm = parseInt(meizzTheMonth)+ex;	//ex表示偏移量，用于选择上个月份和下个月份的日期
	//判断月份，并进行对应的处理
	if(mm<1){
		yy--;
		mm=12+mm;
	}
	else if(mm>12){
		yy++;
		mm=mm-12;
	}
	
  if (mm < 10){mm = "0" + mm;}
  if (outObject)
  {
    if (!n) {//outObject.value=""; 
      return;}
    if ( n < 10){n = "0" + n;}
    outObject.value= yy + "-" + mm + "-" + n ; //注：在这里你可以输出改成你想要的格式
    closeLayer(); 
  }
  else {closeLayer(); alert("您所要输出的控件对象并不存在！");}
}

//meizzSetDay(meizzTheYear,meizzTheMonth);
// -->

<!--
//----------------------------------------------------------------------------

//  ����������һ������ Javascript ҳ��ű��ؼ���������΢��� IE ��5.0���ϣ������
//  �����ú����� setday(this,[object])��setday(this)��[object]�ǿؼ�����Ŀؼ��������������ӣ�
//  һ��<input name=txt><input type=button value=setday onclick="setday(this,document.all.txt)">
//  ����<input onfocus="setday(this)">
//  ����ʲô����ĵط����������и��õĽ��飬��������ϵ��mail: meizz@hzcnc.com
//  ����������������ǣ�1000 - 9999��
//  ��ESC���رոÿؼ�
//  ������µ���ʾ�ط����ʱ��ֱ�������µ�������
//  �ؼ���������һ�㼴�ɹرոÿؼ�
/* ����Ϊwalkingpoison���޸�˵��
walkingpoison��ϵ��ʽ��wayx@kali.com.cn

Ver 1.3
�޸����ڣ�2002-11-29
�޸����ݣ�
1.*�հײ����û�ɫ��ʾ�ϸ��µ��������¸��µ�ǰ����
2.*ÿ������������������ʾ
3.�޸�ʹ�õ�ǰ���ں͵�ǰѡ������ڵı���ɫ�ڻ�ɫ���ڲ���Ҳ��������ʾ

Ver 1.2
�޸����ڣ�2002-11-28
�޸����ݣ�
1.*�޸�����µĵ���������İ������ڣ��������Ŀռ�
2.��ǰѡ����������б�����ʾ��ͬ�ı���ɫ
3.�����˵����Ԫ��֮��ķָ��ߵ��¿ؼ��رյ�����

Ver 1.1
�޸����ڣ�2002-11-15
�޸����ݣ�
1.�����˷�������Esc���ر��Ժ��ٴε��������ʾ����������
2.���todayֱ��ѡ�е�ǰ�����ڲ��رտؼ�
3.*������ÿؼ���������кϷ����ڣ����Զ���ʾ���������ڲ��֡�
4.�޸ĳ���ͳһʹ�ùرյĺ���closeLayer()���ر������ؼ�����������ͨ���Զ���رպ���������û��Զ���Ĺ��ܡ�
����Ϊ������ʾ������ʱ������select��select����Զ�ڲ�����棬������ʾ���ʱ����Ҫ���أ�����Ҫ����رղ��ʱ������ʾselect��
�Զ���ķ���������������ļ��ĺ�����϶Թرպ������ض���
ʾ����
<scr ipt>
function closeLayer()               //�Զ���������Ĺر�
  {
    document.all.meizzDateLayer.style.display="none";
    //��������Զ���Ĵ��룬����һʾ���У�����Ҫ�ڵ���setday����ǰ���������select�Ĵ��룺form1.select1.style.display='none'
    form1.select1.style.display='';
    //�Զ���������
  }

</scr ipt>

ע��*�ű�ʾ�ȽϹؼ��ĸĶ�

���ؼ�����Ҫ�Ľ��Ĳ��֣�
1.���ܻ���һЩ�Ƚ�С��bug�Ͳ�������ĵط�
*/
//==================================================== WEB ҳ����ʾ���� =====================================================
document.writeln('<div id=meizzDateLayer style="position: absolute; width: 142; height: 166; z-index: 9998; display: none" onselectstart="return false">');
document.writeln('<span id=tmpSelectYearLayer  style="z-index: 9999;position: absolute;top: 2; left: 18;display: none"></span>');
document.writeln('<span id=tmpSelectMonthLayer style="z-index: 9999;position: absolute;top: 2; left: 75;display: none"></span>');
document.writeln('<table border=0 cellspacing=1 cellpadding=0 width=142 height=160 bgcolor=#21284A Author="wayx">');
document.writeln('  <tr Author="wayx"><td width=142 height=23 Author="wayx" bgcolor=#FFFFFF><table border=0 cellspacing=1 cellpadding=0 width=140 Author="wayx" height=23>');
document.writeln('      <tr align=center Author="wayx"><td width=16 align=center bgcolor=#21284A style="font-size:12px;cursor: hand;color: #B5BE63" ');
document.writeln('        onclick="meizzPrevM()" title="��ǰ�� 1 ��" Author=meizz><b Author=meizz>&lt;</b>');
document.writeln('        </td><td width=108 align=center style="font-size:12px;cursor:default" Author=meizz><span ');
document.writeln('onmouseover="style.backgroundColor=\'#B5BE63\'" onmouseout="style.backgroundColor=\'white\'" title="�������ѡ�����"');
document.writeln('         Author=meizz id=meizzYearHead onclick="tmpSelectYearInnerHTML(this.innerText.substring(0,4))"></span>&nbsp;&nbsp;<span');
document.writeln('onmouseover="style.backgroundColor=\'#B5BE63\'" onmouseout="style.backgroundColor=\'white\'" title="�������ѡ���·�"');
document.writeln('         id=meizzMonthHead Author=meizz onclick="tmpSelectMonthInnerHTML(this.innerText.length==3?this.innerText.substring(0,1):this.innerText.substring(0,2))"></span></td>');
document.writeln('        <td width=16 bgcolor=#21284A align=center style="font-size:12px;cursor: hand;color: #B5BE63" ');
document.writeln('         onclick="meizzNextM()" title="���� 1 ��" Author=meizz><b Author=meizz>&gt;</b></td></tr>');
document.writeln('    </table></td></tr>');
document.writeln('  <tr Author="wayx"><td width=142 height=18 bgcolor=#21284A Author="wayx">');
document.writeln('<table border=0 cellspacing=0 cellpadding=0 width=140 height=1 Author="wayx" style="cursor:default">');
document.writeln('<tr Author="wayx" align=center><td style="font-size:12px;color:#FFFFFF" Author=meizz>��</td>');
document.writeln('<td style="font-size:12px;color:#FFFFFF" Author=meizz>һ</td><td style="font-size:12px;color:#FFFFFF" Author=meizz>��</td>');
document.writeln('<td style="font-size:12px;color:#FFFFFF" Author=meizz>��</td><td style="font-size:12px;color:#FFFFFF" Author=meizz>��</td>');
document.writeln('<td style="font-size:12px;color:#FFFFFF" Author=meizz>��</td><td style="font-size:12px;color:#FFFFFF" Author=meizz>��</td></tr>');
document.writeln('</table></td></tr><!-- Author:F.R.Huang(meizz) http://www.meizz.com/ mail: meizz@hzcnc.com 2002-10-8 -->');
document.writeln('  <tr Author="wayx"><td width=142 height=120 Author="wayx">');
document.writeln('    <table border=0 cellspacing=1 cellpadding=0 width=140 height=120 Author="wayx" bgcolor=#FFFFFF>');
var n=0; for (j=0;j<5;j++){ document.writeln (' <tr align=center Author="wayx">'); for (i=0;i<7;i++){
document.writeln('<td width=20 height=20 id=meizzDay'+n+' style="font-size:12px" Author=meizz onclick=meizzDayClick(this.innerText,0)></td>');n++;}
document.writeln('</tr>');}
document.writeln('      <tr align=center Author="wayx">');
for (i=35;i<39;i++)document.write('<td width=20 height=20 id=meizzDay'+i+' style="font-size:12px" Author=wayx onclick="meizzDayClick(this.innerText,0)"></td>');
document.writeln('        <td colspan=3 align=right Author=meizz><span onclick=closeLayer() style="font-size:12px;cursor: hand"');
document.writeln('         Author=meizz title="Version:1.3&#13;����: F.R.Huang(meizz)&#13;MAIL: meizz@hzcnc.com&#13;�޸�:walkingpoison"><u>�ر�</u></span>&nbsp;</td></tr>');
document.writeln('    </table></td></tr><tr Author="wayx"><td Author="wayx">');
document.writeln('        <table border=0 cellspacing=1 cellpadding=0 width=100% Author="wayx" bgcolor=#FFFFFF>');
document.writeln('          <tr Author="wayx"><td Author=meizz align=left><input Author=meizz type=button value="<<" title="��ǰ�� 1 ��" onclick="meizzPrevY()" ');
document.writeln('             onfocus="this.blur()" style="font-size: 12px; height: 20px"><input Author=meizz title="��ǰ�� 1 ��" type=button ');
document.writeln('             value="< " onclick="meizzPrevM()" onfocus="this.blur()" style="font-size: 12px; height: 20px"></td><td ');
document.writeln('             Author=meizz align=center><input Author=meizz type=button value=Today onclick="meizzToday()" ');
document.writeln('             onfocus="this.blur()" title="��ǰ����" style="font-size: 12px; height: 20px"></td><td ');
document.writeln('             Author=meizz align=right><input Author=meizz type=button value=" >" onclick="meizzNextM()" ');
document.writeln('             onfocus="this.blur()" title="���� 1 ��" style="font-size: 12px; height: 20px"><input ');
document.writeln('             Author=meizz type=button value=">>" title="���� 1 ��" onclick="meizzNextY()"');
document.writeln('             onfocus="this.blur()" style="font-size: 12px; height: 20px"></td>');
document.writeln('</tr></table></td></tr></table></div>');
//==================================================== WEB ҳ����ʾ���� ======================================================
var outObject;
var outDate="";		//��Ŷ��������
function setday(tt,obj) //��������
{
	if (arguments.length >  2){alert("�Բ��𣡴��뱾�ؼ��Ĳ���̫�࣡");return;}
	if (arguments.length == 0){alert("�Բ�����û�д��ر��ؼ��κβ�����");return;}
	var dads  = document.all.meizzDateLayer.style;var th = tt;
	var ttop  = tt.offsetTop;     //TT�ؼ��Ķ�λ���
	var thei  = tt.clientHeight;  //TT�ؼ�����ĸ�
	var tleft = tt.offsetLeft;    //TT�ؼ��Ķ�λ���
	var ttyp  = tt.type;          //TT�ؼ�������
	while (tt = tt.offsetParent){ttop+=tt.offsetTop; tleft+=tt.offsetLeft;}
	dads.top  = (ttyp=="image")? ttop+thei : ttop+thei+6;
	dads.left = tleft;
	outObject = (arguments.length == 1) ? th : obj;
	//���ݵ�ǰ������������ʾ����������
	var reg = /^(\d+)-(\d{1,2})-(\d{1,2})$/; 
	var r = outObject.value.match(reg); 
	if(r!=null){
		r[2]=r[2]-1; 
		var d= new Date(r[1], r[2],r[3]); 
		if(d.getFullYear()==r[1] && d.getMonth()==r[2] && d.getDate()==r[3]){
			outDate=d;		//�����ⲿ���������
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

var MonHead = new Array(12);    		   //����������ÿ���µ��������
    MonHead[0] = 31; MonHead[1] = 28; MonHead[2] = 31; MonHead[3] = 30; MonHead[4]  = 31; MonHead[5]  = 30;
    MonHead[6] = 31; MonHead[7] = 31; MonHead[8] = 30; MonHead[9] = 31; MonHead[10] = 30; MonHead[11] = 31;

var meizzTheYear=new Date().getFullYear(); //������ı����ĳ�ʼֵ
var meizzTheMonth=new Date().getMonth()+1; //�����µı����ĳ�ʼֵ
var meizzWDay=new Array(39);               //����д���ڵ�����

function document.onclick() //������ʱ�رոÿؼ�
{ 
  with(window.event.srcElement)
  { if (getAttribute("Author")==null && tagName != "INPUT")
    closeLayer();
    //outObject.value=tagName + "-" + getAttribute("Author");	//�������Ե���رչ���
  }
}

function meizzWriteHead(yy,mm)  //�� head ��д�뵱ǰ��������
  { document.all.meizzYearHead.innerText  = yy + " ��";
    document.all.meizzMonthHead.innerText = mm + " ��";
  }
function tmpSelectYearInnerHTML(strYear) //��ݵ�������
{
  if (strYear.match(/\D/)!=null){alert("�����������������֣�");return;}
  var m = (strYear) ? strYear : new Date().getFullYear();
  if (m < 1000 || m > 9999) {alert("���ֵ���� 1000 �� 9999 ֮�䣡");return;}
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
       {selectInnerHTML += "<option value='" + i + "' selected>" + i + "��" + "</option>\r\n";}
    else {selectInnerHTML += "<option value='" + i + "'>" + i + "��" + "</option>\r\n";}
  }
  selectInnerHTML += "</select>";
  document.all.tmpSelectYearLayer.style.display="";
  document.all.tmpSelectYearLayer.innerHTML = selectInnerHTML;
  document.all.tmpSelectYear.focus();
}

function tmpSelectMonthInnerHTML(strMonth) //�·ݵ�������
{
  if (strMonth.match(/\D/)!=null){alert("�·���������������֣�");return;}
  var m = (strMonth) ? strMonth : new Date().getMonth() + 1;
  var s = "<select Author=meizz name=tmpSelectMonth style='font-size: 12px' "
     s += "onblur='document.all.tmpSelectMonthLayer.style.display=\"none\"' "
     s += "onchange='document.all.tmpSelectMonthLayer.style.display=\"none\";"
     s += "meizzTheMonth = this.value; meizzSetDay(meizzTheYear,meizzTheMonth)'>\r\n";
  var selectInnerHTML = s;
  for (var i = 1; i < 13; i++)
  {
    if (i == m)
       {selectInnerHTML += "<option value='"+i+"' selected>"+i+"��"+"</option>\r\n";}
    else {selectInnerHTML += "<option value='"+i+"'>"+i+"��"+"</option>\r\n";}
  }
  selectInnerHTML += "</select>";
  document.all.tmpSelectMonthLayer.style.display="";
  document.all.tmpSelectMonthLayer.innerHTML = selectInnerHTML;
  document.all.tmpSelectMonth.focus();
}

function closeLayer()               //�����Ĺر�
  {
    document.all.meizzDateLayer.style.display="none";
  }

function document.onkeydown()		//��Esc���ر�
  {
    if (window.event.keyCode==27){
		closeLayer();
		outObject.blur();
	}
  }

function IsPinYear(year)            //�ж��Ƿ���ƽ��
  {
    if (0==year%4&&((year%100!=0)||(year%400==0))) return true;else return false;
  }

function GetMonthCount(year,month)  //�������Ϊ29��
  {
    var c=MonHead[month-1];if((month==2)&&IsPinYear(year)) c++;return c;
  }

function GetDOW(day,month,year)     //��ĳ������ڼ�
  {
    var dt=new Date(year,month-1,day).getDay()/7; return dt;
  }

function meizzPrevY()  //��ǰ�� Year
  {
    if(meizzTheYear > 999 && meizzTheYear <10000){meizzTheYear--;}
    else{alert("��ݳ�����Χ��1000-9999����");}
    meizzSetDay(meizzTheYear,meizzTheMonth);
  }
function meizzNextY()  //���� Year
  {
    if(meizzTheYear > 999 && meizzTheYear <10000){meizzTheYear++;}
    else{alert("��ݳ�����Χ��1000-9999����");}
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
function meizzPrevM()  //��ǰ���·�
  {
    if(meizzTheMonth>1){meizzTheMonth--}else{meizzTheYear--;meizzTheMonth=12;}
    meizzSetDay(meizzTheYear,meizzTheMonth);
  }
function meizzNextM()  //�����·�
  {
    if(meizzTheMonth==12){meizzTheYear++;meizzTheMonth=1}else{meizzTheMonth++}
    meizzSetDay(meizzTheYear,meizzTheMonth);
  }

function meizzSetDay(yy,mm)   //��Ҫ��д����**********
{
  meizzWriteHead(yy,mm);
  //���õ�ǰ���µĹ�������Ϊ����ֵ
  meizzTheYear=yy;
  meizzTheMonth=mm;
  
  for (var i = 0; i < 39; i++){meizzWDay[i]=""};  //����ʾ�������ȫ�����
  var day1 = 1,day2=1,firstday = new Date(yy,mm-1,1).getDay();  //ĳ�µ�һ������ڼ�
  for (i=0;i<firstday;i++)meizzWDay[i]=GetMonthCount(mm==1?yy-1:yy,mm==1?12:mm-1)-firstday+i+1	//�ϸ��µ������
  for (i = firstday; day1 < GetMonthCount(yy,mm)+1; i++){meizzWDay[i]=day1;day1++;}
  for (i=firstday+GetMonthCount(yy,mm);i<39;i++){meizzWDay[i]=day2;day2++}
  for (i = 0; i < 39; i++)
  { var da = eval("document.all.meizzDay"+i)     //��д�µ�һ���µ�������������
    if (meizzWDay[i]!="")
      { 
		if(i<firstday)		//�ϸ��µĲ���
		{
			da.innerHTML="<b><font color=gray>" + meizzWDay[i] + "</font></b>";
			da.title=(mm==1?12:mm-1) +"��" + meizzWDay[i] + "��";
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
		else if (i>=firstday+GetMonthCount(yy,mm))		//�¸��µĲ���
		{
			da.innerHTML="<b><font color=gray>" + meizzWDay[i] + "</font></b>";
			da.title=(mm==12?1:mm+1) +"��" + meizzWDay[i] + "��";
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
		else		//���µĲ���
		{
			da.innerHTML="<b>" + meizzWDay[i] + "</b>";
			da.title=mm +"��" + meizzWDay[i] + "��";
			da.onclick=Function("meizzDayClick(this.innerText,0)");		//��td����onclick�¼��Ĵ���
			//����ǵ�ǰѡ������ڣ�����ʾ����ɫ�ı���������ǵ�ǰ���ڣ�����ʾ����ɫ����
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

function meizzDayClick(n,ex)  //�����ʾ��ѡȡ���ڣ������뺯��*************
{
  var yy=meizzTheYear;
  var mm = parseInt(meizzTheMonth)+ex;	//ex��ʾƫ����������ѡ���ϸ��·ݺ��¸��·ݵ�����
	//�ж��·ݣ������ж�Ӧ�Ĵ���
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
    outObject.value= yy + "-" + mm + "-" + n ; //ע�����������������ĳ�����Ҫ�ĸ�ʽ
    closeLayer(); 
  }
  else {closeLayer(); alert("����Ҫ����Ŀؼ����󲢲����ڣ�");}
}

//meizzSetDay(meizzTheYear,meizzTheMonth);
// -->

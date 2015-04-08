var table;
var period;
var listener;	
var head=null;
var freshable=true;
function setfreshable(ff)
{
	freshable=ff;
}
function refreshpage(tableid, timeInMillis, listenerId){
	table = document.getElementById(tableid);
	period= timeInMillis;
	listener = listenerId;
	setTimeout("refreshTable()", timeInMillis);
}
function refreshTable(){
	if(listener != "null"){
		ctx.get(display, "topicEventCenter.getListener('" + listener + "').getChangedTable()");
	}
}
var display = function(data){
	if(head==null)
		head=table.rows[0];
	for(var i in data)
	{
		if(data[i].length>200)
		{
 	 		fresh();
			break;
		}
		changeTable(i,data[i]);
	}
	if(freshable)
		setTimeout("refreshTable()", period);
}

var buffer;
function bufferWrite(str) {
	buffer += str;
}

function changeTable(key ,pvalue)
{
	var tr = document.getElementById(key);
	if(tr!=null)
	{
		var tbody=tr.parentNode;
		var num=tr.cells[0].rowSpan;
		var caption=tr.cells[0].innerHTML;
		//存放需要删除的行
		var origTableValue=new Array();
		origTableValue[0]=tr;
		for(var i=1;i<num;i++)
		{
			tr=tr.nextSibling;
			origTableValue[i]=tr;
		}
		//存放需要添加的位置
		var insertBeforeTr=origTableValue[origTableValue.length-1].nextSibling;
		//删除行
		for(var i=0;i<origTableValue.length;i++)
		{
			tbody.removeChild(origTableValue[i]);
		}
		//添加新行
		for(var i=0;i<pvalue.length-1;i++)
		{
			var insertRow=pvalue[i];
			var time=pvalue[pvalue.length-1];
			var vTr = document.createElement("tr");
			if(i==0)
			{
				vTr.id=key;
				var vTd = document.createElement("td");
				vTd.rowSpan=pvalue.length-1;
				vTd.innerHTML=caption;
				vTr.appendChild(vTd);
			}
			else
			{
				vTr.id=i+"_"+key;
			}
			for(var j=0;j<insertRow.length;j++)
			{
				if(insertRow[j]==null||insertRow[j].length<1||insertRow[j]=="null")
				{
					insertRow[j]="&nbsp;";
				}		
				var vTd = document.createElement("td");
				vTd.title=time[j];
				
				if(head.cells[j+1].type!="aa")
				{					
					vTd.innerHTML=insertRow[j];
				}
				else 
				{
						if(insertRow[j]!="&nbsp;")
						{
							vTd.width="130";
							buffer = "";
							writebar('30%',insertRow[j], bufferWrite);
							vTd.innerHTML=buffer;
						}
				}
				vTr.appendChild(vTd);
			}
			tbody.insertBefore(vTr, insertBeforeTr) ;
		}
	}
}

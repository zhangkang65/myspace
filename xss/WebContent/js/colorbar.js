function bar(vwidth,value)
{
	var v=value;	
	v=v.replace(/\,/g,"");
	if(!isNaN(v-1))
	{
		if(v<1&&v>0)
			v=1;
		document.writeln('<table width="100%" border="0" cellpadding="0" cellspacing="0">');
		document.writeln('<tr>');
		document.writeln('<td class="none" width="'+vwidth+'">'+value+'%</td>');
		document.writeln('<td class="none" >');
		document.writeln('<table class="bar" height="16" width="100%" border="0" cellpadding="0" cellspacing="0">');
		document.writeln('<tr>');
		if(v==100)
			document.writeln('<td class="fill" width="'+v+'%"></td>');	
		else if(v>100)
			document.writeln('<td class="red" width="'+v+'%"></td>');		
		else if(v==0)
			document.writeln('<td class="none"></td>');
		else
		{
			document.writeln('<td class="fill" width="'+v+'%"></td>');
			document.writeln('<td class="none"></td>');
		}	
		
		document.writeln('</tr>');
		document.writeln('</table></td>');
		document.writeln('</tr>');
		document.writeln('</table>');
	}
	else
	{
		document.writeln('&nbsp;');
	}
}

function cbar(vwidth,value,cmp)
{
	var v=value;	
	v=v.replace(/\,/g,"");
	if(!isNaN(v-1))
	{
		if(v<1&&v>0)
			v=1;
		document.writeln('<table width="100%" border="0" cellpadding="0" cellspacing="0">');
		document.writeln('<tr>');
		document.writeln('<td class="none" width="'+vwidth+'">'+value+'%</td>');
		document.writeln('<td class="none" >');
		document.writeln('<table class="bar" height="16" width="100%" border="0" cellpadding="0" cellspacing="0">');
		document.writeln('<tr>');
		if(value==100)
			document.writeln('<td class="fill" width="'+v+'%"></td>');	
		else if(v>100)
			document.writeln('<td class="red" width="'+v+'%"></td>');			
		else if(value==0)
			document.writeln('<td class="none"></td>');
		else
		{
			if(value-cmp<0)
				document.writeln('<td class="fill" width="'+v+'%"></td>');
			else
				document.writeln('<td class="red" width="'+v+'%"></td>');
			document.writeln('<td class="none"></td>');
		}	
		
		document.writeln('</tr>');
		document.writeln('</table></td>');
		document.writeln('</tr>');
		document.writeln('</table>');
	}
	else
	{
		document.writeln('&nbsp;');
	}
}

function writebar(vwidth,value,mwrite)
{
	var v=value;	
	v=v.replace(/\,/g,"");
	if(!isNaN(v-1))
	{
		if(v<1&&v>0)
			v=1;	
		mwrite('<table width="100%" border="0" cellpadding="0" cellspacing="0">');
		mwrite('<tr>');
		mwrite('<td class="none" width="'+vwidth+'">'+value+'%</td>');
		mwrite('<td class="none" >');
		mwrite('<table class="bar" height="16" width="100%" border="0" cellpadding="0" cellspacing="0">');
		mwrite('<tr>');
		if(value==100)
			mwrite('<td class="fill" width="'+v+'%"></td>');	
		else if(v>100)
			mwrite('<td class="red" width="'+v+'%"></td>');		
		else if(value==0)
			mwrite('<td class="none"></td>');
		else
		{
			mwrite('<td class="fill" width="'+v+'%"></td>');
			mwrite('<td class="none"></td>');
		}	
		mwrite('</tr>');
		mwrite('</table></td>');
		mwrite('</tr>');
		mwrite('</table>');
	}
	else
	{
		mwrite('&nbsp;');
	}
}
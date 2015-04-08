function dump(obj) {
	m=window.open('','dump','toolbar=no,directories=no,menubar=no,location=no,scrollbars=yes,resizable=yes,status=no,copyhistory=no,top=300,left=370,width=300,height=150');
	m.document.writeln("<table border=1>");
	for(i in obj) {
		m.document.writeln("<tr><td>"+i+"</td><td>"+obj[i]+"</td></tr>");
	}
	m.document.writeln("</table>");
}

function debug(message) {
  m=window.open('','dump','toolbar=no,directories=no,menubar=no,location=no,scrollbars=yes,resizable=yes,status=no,copyhistory=no,top=300,left=370,width=300,height=150');
  m.document.writeln("<p>");
  m.document.writeln(message);	
  m.document.writeln("</p>");
}
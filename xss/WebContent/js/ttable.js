/* http://www.alistapart.com/articles/zebratables/ */
function removeClassName (elem, className) {
	elem.className = elem.className.replace(className, "").trim();
}

function addCSSClass (elem, className) {
	removeClassName (elem, className);
	elem.className = (elem.className + " " + className).trim();
}

String.prototype.trim = function() {
	return this.replace( /^\s+|\s+$/, "" );
}
/**
 * 支持提起td中子节点的文本，可解决超链之类问题。
 * compfun是自定义比较函数，可以是比较函数数组，或比较函数。
 * 如果是数组，只要在需要自定义的比较的列添加比较函数，此时的函数形式：function f(row1, row2)；
 *   如new Array(null, f1, null); 指只需要自定义第一列
 * 如果是方法，形式如：function f(row1, row2, index);
 */
function sortedTable(tableId, compfun) {
	var table = document.getElementById(tableId);
	
	var thead = table.getElementsByTagName("thead")[0];	
	if(!thead) return;
	
	var headline = thead.getElementsByTagName("tr")[0];
	if(!headline) return;
	
	if(compfun)
		table.compfun = compfun;
	
	var ths = headline.getElementsByTagName("th");	
	for (var j = 0; j < ths.length; j+= 1) {
		var textNode = ths[j].firstChild;
		if(textNode.tagName) continue;
		ths[j].removeChild(textNode);
		var a = document.createElement("A");
		a.href = "javascript:sortTable('"+table.id +"'," + j +")";
		a.onclick = new Function("this.blur();");
		a.appendChild(textNode);
		ths[j].appendChild(a);
	}
}

function stripedTable(tableId) {
	var table = document.getElementById(tableId);
	var tbody = table.getElementsByTagName("tbody")[0];
	
	table.setAttribute("striped", true);
	
	if(!tbody) return;
	
	var trs = tbody.getElementsByTagName("tr");
	for (var j = 0; j < trs.length; j+= 2) {
		removeClassName(trs[j], 'row1');
		addCSSClass(trs[j], 'row0');
	}
	for (var k = 1; k < trs.length; k += 2) {
		removeClassName(trs[k], 'row0');
		addCSSClass(trs[k], 'row1');
	}
}

function fixedTableold(tableId, width, height) {
	var table = document.getElementById(tableId);
	var thead = table.getElementsByTagName("thead")[0];
	var tbody = table.getElementsByTagName("tbody")[0];
	thead.className = "fixedHeader";
	tbody.className = "scrollContent";

	var container = document.createElement("div");
	container.className="tableContainer";

	if(document.all) { //IE
		container.style.width = width;
		container.style.height = height;
		table.style.width = width - 16;
	} else { //不是IE, FireFox
		document.styleSheets[0].insertRule("html>body div.tableContainer{height: "+height+"px;width: "+width+"px; overflow: hidden}", 1);
		document.styleSheets[0].insertRule("html>body div.tableContainer table {width: "+(width+1)+"px}", 3);
		document.styleSheets[0].insertRule("html>body tbody.scrollContent {	height: "+(height-7)+"px;overflow:auto;width: 100%}",	9);
	}

	var tableParent = table.parentNode;
	tableParent.removeChild(table);
	container.appendChild(table);
	tableParent.appendChild(container);
}

function fixedTable(tableId, width, height, border) {
	var table = document.getElementById(tableId);
	var container=null;

	if(table.parentNode.className != "tableContainer") {
		var thead = table.getElementsByTagName("thead")[0];
		var tbody = table.getElementsByTagName("tbody")[0];
		thead.className = "fixedHeader";
		tbody.className = "scrollContent";
		table.style.border = "0px";

		container = document.createElement("div");
		container.className="tableContainer";

		var tableParent = table.parentNode;
		tableParent.removeChild(table);
		container.appendChild(table);
		tableParent.appendChild(container);
	} else {
		container = table.parentNode;
	}
	var tableContainerCss = findCssStyle("div.tableContainer");
	if(!border) {
		tableContainerCss.border = "0px";
	}
	tableContainerCss.width = width + "px";
	tableContainerCss.height = height + "px";
	if(document.all) { //IE
		findCssStyle("div.tableContainer table").width = (width - 16) + "px";
	} else { //不是IE, FireFox
		findCssStyle("html > body div.tableContainer table").width = width + "px";
		findCssStyle("html > body tbody.scrollContent").height = (height-7) + "px";
	}
}

function defaultCompareRow(row1, row2, index) {	
	var v1 = document.all? row1.cells[index].innerText : row1.cells[index].textContent;
	var v2 = document.all? row2.cells[index].innerText : row2.cells[index].textContent;

	v1 = v1.trim();
	v2 = v2.trim();
	
	var r = 0;
    var min = Math.min(v1,v2);
    if(isNaN(min)){
        if(v1 < v2){
            r = -1;
        }else if(v1 > v2){
            r = 1;
        }
    }else{
        if(min != v1){
            r = 1;
        }else if(min != v2){
            r = -1;
        }
    }
    
    return r;
}
function compare(v1,v2)
{
		var r = 0;
    var min = Math.min(v1,v2);
    if(isNaN(min)){
        if(v1 < v2){
            r = -1;
        }else if(v1 > v2){
            r = 1;
        }
    }else{
        if(min != v1){
            r = 1;
        }else if(min != v2){
            r = -1;
        }
    }
    return r;
}

function compareRow(row1, row2) {
	var r = 0;
	var index = row1.parentNode.sortIndex;
	var compfun = row1.parentNode.parentNode.compfun;
	if(compfun) {
		if(compfun instanceof Array) {
			if(compfun[index])
				r = compfun[index](row1, row2);
			else
				r = defaultCompareRow(row1, row2, index);
		} else {
			r = compfun(row1, row2, index);
		}
	} else {
		r = defaultCompareRow(row1, row2, index);
	}
	return row1.parentNode.sortState[row1.parentNode.sortIndex]? -r : r;
}

function sortTable(tableId, colIndex) {
	var table = document.getElementById(tableId);
	var tbody = table.getElementsByTagName("tbody")[0];
	var rows =  table.rows;
	var rows2 = new Array();
	
	table.setAttribute("sorted", true);
	
	//忽略表头
	for(var i=1; i<rows.length; i++) {
		rows2[i-1] = rows[i];
	}

	if(!tbody.sortState) {
		tbody.sortState = new Array();
	}

	tbody.sortState[colIndex] = !tbody.sortState[colIndex];
	tbody.sortIndex = colIndex;
	
	rows2.sort(compareRow);
	
	for(var i=0; i<rows2.length; i++) {
		tbody.removeChild(rows2[i]);
	}

	for(var i=0; i<rows2.length; i++) {
		tbody.appendChild(rows2[i]);
	}
	
	if(table.getAttribute("striped"))
		stripedTable(tableId);
}

function findCssStyle(selectorText) {
	selectorText = selectorText.toUpperCase();
	var sheets = document.styleSheets;
	for(var i=0; i<sheets.length; i++) {
		var rules = null;
		if(document.all) {
			rules = sheets[i].rules;
		} else {
			rules = sheets[i].cssRules;
		}
		for(var j=0; j<rules.length; j++) {
			if(rules[j].selectorText.toUpperCase() == selectorText) {
				return rules[j].style;
			}
		}
	}
	return null;
}

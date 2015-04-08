	var table;
	var period;
	var decorateFunc;
	var listener;
	var pkIndex;
	function refresh(tableid, timeInMillis, listenerId, func){
		table = document.getElementById(tableid);
		period = timeInMillis;
		listener = listenerId;
		setTimeout("refreshTable()", timeInMillis);
		decorateFunc = (!func)? defaultDecorate : func;
	}
	function refreshTable(){
		if(listener != "null"){
			ctx.get(display, "topicEventCenter.getListener('" + listener + "').getChangeEvents()");
			ctx.get(setPkIndex,"topicEventCenter.getListener('" + listener + "').getTableModel().getPkIndex()");
		}
	}
	function setPkIndex(data){
		if(data != null){
			pkIndex = data;
		}
	}
	function getCellValue(row, index){
		var cell = row.cells[index];
		return cell.id;
	}
	function getPkValue(row){
		var cell = row.cells[pkIndex];
		return cell.id;
	}
	var display = function(data){
		if(data != null && typeof data == 'object'){
			for(var i = 0 ; i < data.length ;  i ++){
				var event = data[i];
				var type = event['type'];
				if(type == 0){
					onAdd(event);
				}else if(type == 1){
					onRemove(event);
				}else if(type == 2){
					onUpdate(event);
				}
			}
		}
		setTimeout("refreshTable()", period);
	}
	function onAdd(event){
		var rowData = event['row'];
		var tr = table.insertRow();
		for(var i = 0 ; i < rowData.length ; i ++){
			var td = tr.insertCell(i);
			decorate(tr, i, rowData[i]);
		}
	}
	function onRemove(event){
		var rownum = event['rownum'];
		table.deleteRow(rownum);
	}
	function onUpdate(event){
		var rownum = event['rownum'];
		var rows = table.rows;
		var row = rows[rownum+1];
		var cells = row.cells;
		var updates = event['updates'];
		for(var i in updates){
			decorate(row, i, updates[i]);
		}
	}
	function decorate(row, index, value){ 
		decorateFunc(row, index, value);
	}
	function defaultDecorate(row, index ,value){
		var td = row.cells[index];
		td.innerHTML = value;
	}
	
	function dwrErrorHandler(message) {
	    //ºöÂÔ
	}
	
	DWREngine.setErrorHandler(dwrErrorHandler);
	
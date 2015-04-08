// extjs 扩展代码

Ext.BLANK_IMAGE_URL="/sysmgr/js/resources/images/default/s.gif";

// 读取表单提交后的返回
Ext.form.XmlErrorReader = function() {
    Ext.form.XmlErrorReader.superclass.constructor.call(this, {
            record : 'field',
            success: '@success'
        }, [
            'id', 'msg'
        ]
    );
};
Ext.extend(Ext.form.XmlErrorReader, Ext.data.XmlReader);

// 临时对象，空属性
Ext.util.TempObject =  function(){ }

function closeDlg()
{
	this.destroy(true);
}

var window_upload = null;

function uploadFile(cb, sc)
{
	var form = new Ext.form.FormPanel({
		baseCls: 'x-plain',
		method: 'POST',
		fileUpload: true,
		defaultType: 'textfield',
		errorReader: new Ext.form.XmlErrorReader(),
		labelWidth: 75, // label settings here cascade unless overridden
		labelAlign: 'top',
		url:'/sysmgr/itsm/upload.jsp'
	});

    form.add(
        new Ext.form.TextField({
            fieldLabel: '请选择文件',
            name: 'first',
			inputType: 'file',
            width:175,
            allowBlank:false
        })
	);

	if (window_upload)
		window_upload.close();
    window_upload = new Ext.Window({
        title: '上传文件',
		width:250,
		height:120,
        layout: 'fit',
        plain:true,
        bodyStyle:'padding:5px;',
        buttonAlign:'center',
        items: form,

        buttons: [{
            text: '确定',
            handler: function() {
				var form = window_upload._form.form;
				if (!form.isValid()) {
					Ext.MessageBox.alert("提示", "请选择上传文件");
					return;
				}
				form.submit({
					waitMsg: '正在上传....',
					success: function(form, action) 
					{ 
						if (window_upload._cb)
							window_upload._cb(window_upload._sc, form.errorReader.xmlData.documentElement.text);
						if (window_upload) {
							window_upload.close();
							window_upload = null;
						}
					},
					failure: function(form, action)
					{
						Ext.MessageBox.alert("上传失败", form.errorReader.xmlData.documentElement.text);
					}
				});
            }
        },{
            text: '取消',
            handler: function() {
               window_upload.close();
			   window_upload = null;
            }
        }]
    });
	window_upload._form = form;
	window_upload._cb = cb;
	window_upload._sc = sc;

    window_upload.show();
}

Ext.tree.FilterTreeLoader = function(config){
	Ext.tree.FilterTreeLoader.superclass.constructor.call(this, config);
}

Ext.extend(Ext.tree.FilterTreeLoader, Ext.tree.TreeLoader, {
    createNode : function(attr){
        if (attr._click && this.regexId && !this.regexId.test(attr.id))
            return null;
        if (attr._click && this.regexText && !this.regexText.test(attr.text))
            return null;
		if (this.box && this.box.singleMode == false)
		{
			if (attr._click)
			{
				attr.checked = false;
				var v = this.box.getValue();
				if (v == attr.id || v.indexOf(attr.id + ",") != -1
					|| v.indexOf("," + attr.id + ",") != -1 || v.indexOf("," + attr.id) != -1)
				attr.checked = true;
			}
		}
		var ret = Ext.tree.FilterTreeLoader.superclass.createNode.call(this, attr);
		ret._click = attr._click;
		if (this.box && this.box.singleMode == false)
		{
			ret.on("checkchange", this.box.checkChanged, this.box);
			ret.on("dblclick", this.box.dblClick, this.box);
		}
		return ret;
    }
});

Ext.form.TreeBox = function(config){
	Ext.form.TreeBox.superclass.constructor.call(this, config);
	this.regexId = config.regexId;
	this.regexText = config.regexText;
}

Ext.form.TreeBox = Ext.extend(Ext.form.TriggerField, {
    regexId : null,
    regexText : null,
    defaultAutoCreate : {tag: "input", type: "text", size: "24", autocomplete: "off"},
    listWidth: undefined,
    hiddenName: undefined,
    listClass: '',
    selectedClass: 'x-combo-selected',
    triggerClass : 'x-form-arrow-trigger',
    shadow:'sides',
	lastSelection: null,
	singleMode: true,
    listAlign: 'tl-bl?',
    maxHeight: 300,
    resizable: true,
    handleHeight : 8,
    minListWidth : 70,
	viewLoader : null,

	initComponent : function(){
		Ext.form.ComboBox.superclass.initComponent.call(this);
		this.addEvents({
			'expand' : true,
			'collapse' : true,
			'beforeselect' : true,
			'select' : true
		});

		if(this.transform){
			this.allowDomMove = false;
			var s = Ext.getDom(this.transform);
			this.hiddenName = s.name;
			s.name = Ext.id(); // wipe out the name in case somewhere else they have a reference
			if(!this.lazyRender){
				this.target = true;
				this.el = Ext.DomHelper.insertBefore(s, this.autoCreate || this.defaultAutoCreate);
				s.parentNode.removeChild(s); // remove it
				this.render(this.el.parentNode);
			}else{
				s.parentNode.removeChild(s); // remove it
			}
		}
	},

	// private
    onRender : function(ct, position){
        Ext.form.TreeBox.superclass.onRender.call(this, ct, position);
        if(this.hiddenName){
            this.hiddenField = this.el.insertSibling({tag:'input', type:'hidden', name: this.hiddenName, id:  (this.hiddenId||this.hiddenName)},
                    'before', true);
            this.hiddenField.value =
                this.hiddenValue !== undefined ? this.hiddenValue :
                this.value !== undefined ? this.value : '';
            // prevent input submission
            this.el.dom.removeAttribute('name');
        }
        if(Ext.isGecko){
            this.el.dom.setAttribute('autocomplete', 'off');
        }

        var cls = 'x-combo-list';

        this.list = new Ext.Layer({
            shadow: this.shadow, cls: [cls, this.listClass].join(' '), constrain:false
        });

        var lw = this.listWidth || Math.max(this.wrap.getWidth(), this.minListWidth);
        this.list.setWidth(lw);
        this.list.swallowEvent('mousewheel');
		this.list.dom.style.overflow = 'auto';

		if (!this.treeData)
		{
			this.view = new Ext.tree.TreePanel({
				el:this.list,
				animate:false, 
				autoHeight : true,
				rootVisible: false,
				loader: this.viewLoader,
				containerScroll: true
			});
			this.view.loader.regexId = this.regexId;
			this.view.loader.regexText = this.regexText;
			this.view.loader.box = this;
			var root = new Ext.tree.AsyncTreeNode({
				text: 'Ext JS',
				id:'_',
				draggable:false
			});
			this.view.setRootNode(root);
		} else {
			this.view = new Ext.tree.TreePanel({
				el:this.list,
				animate:false, 
				rootVisible: false,
				autoHeight : true,
				containerScroll: true
			});
			var root = new Ext.tree.AsyncTreeNode({
				text: 'Ext JS',
				id:'_',
				draggable:false
			});
			this.view.setRootNode(root);
			this.loadTree(root, this.treeData);
		}

		this.view.getSelectionModel().on("selectionchange", this.selectionChange, this);

		this.view.on('expand', function() { this.view.root.expand(); }, this);
		this.view.on('expandnode', this.recalcSize, this);
		this.view.on('collapsenode', this.recalcSize, this);

		this.view.on('click', this.onItemSelect, this);
		this.renderTree();

		this.el.dom.setAttribute('readOnly', true);
		this.el.on('mousedown', this.onTriggerClick,  this);
		this.el.addClass('x-combo-noedit');
    },

	loadTree : function(node, data)
	{
		for (var i = 0; i < data.length; i++)
		{
			var attr = data[i];
			if (this.regexId && !this.regexId.test(attr.id))
				continue;
			if (this.regexText && !this.regexText.test(attr.text))
				continue;
			var param = { id: attr.id, text: attr.text };
			var newNode = new Ext.tree.TreeNode(param);
			newNode._click = attr._click;
			node.appendChild(newNode);
			if (attr.children)
				this.loadTree(newNode, attr.children);
		}
	},

	selectionChange : function(mode, node) {
		this.lastSelection = node;
		return true;
	},

	renderTree : function() {
		this.view.render();
	},

	dblClick : function(node, e) {
		if (node.ui.checkbox)
			this.checkChanged(node, node.ui.checkbox.checked);
	},

	checkChanged : function(node, checked) {
		if (checked)
			this.addValue(node.id, node.text);
		else
			this.removeValue(node.id, node.text);
	},

	addValue : function(id, text) {
		var v = this.getValue();
		vals = v.split(",");
		var found = false;
		for (var i = 0; i < vals.length; i++) {
			if (vals[i] == id) {
				found = true;
				break;
			}
		}
		if (!found) {
			if (v.length > 0)
			{
				v = "";
				for (var i = 0; i < vals.length; i++)
					v += vals[i] + "=" + this._text_cache[i] + ",";
				v += id + "=" + text;
			}
			else
			{
				v = id + "=" + text;
			}
		}
		this.setValue(v);
	},

	removeValue : function(id, text) {
		var vals = this.getValue();
		if (vals == id) {
			this.setValue("");
			return;
		}
		vals = vals.split(",");
		var v = "";
		for (var i = 0; i < vals.length; i++) {
			if (vals[i] == id)
				continue;
			if (v.length > 0)
				v += ",";
			v += vals[i] + "=" + this._text_cache[i];
		}
		this.setValue(v);
	},
	
	onItemSelect : function(node, e) {
		if (this.singleMode) {
			if (node._click) {
				this.setValue(node.id + "=" + node.text);
				this.collapse();
			}
		}
	},

    // private
    initEvents : function(){
        Ext.form.TreeBox.superclass.initEvents.call(this);

        this.keyNav = new Ext.KeyNav(this.el, {
            "down" : function(e){
                if(!this.isExpanded()){
                    this.onTriggerClick();
                }
				else
					this.selectNext();
            },

            "right" : function(e){
				this.expandNode();
            },

            "left" : function(e){
				this.selectParent();
            },

			"up" : function(e){
                if(this.isExpanded()){
					this.selectPrev();
                }
            },

            "enter" : function(e){
                this.selectValue();
            },

			"esc" : function(e){
                this.collapse();
            },

            "tab" : function(e){
                this.collapse();
                return true;
            },

            scope : this,

            forceKeyDown: true
        });
		
    },

	expandNode : function() {
		if (this.lastSelection) {
			if (!this.lastSelection.isExpanded()) {
				this.lastSelection.expand();
				this.expandNode();
			}
			else if (this.lastSelection.childNodes.length > 0) {
				this.lastSelection = this.lastSelection.childNodes[0];
				this.lastSelection.select();
			}
		}
	},

	selectParent : function() {
		if (this.lastSelection) {
			if (this.lastSelection.parentNode && this.lastSelection.parentNode.getDepth() > 0) {
				this.lastSelection = this.lastSelection.parentNode;
				this.lastSelection.select();
			}
		}
	},

	selectValue : function() {
		if (this.lastSelection) {
			if (this.singleMode) {
				this.onItemSelect(this.lastSelection);
			}
			else {
				if (this.lastSelection.ui.checkbox) {
					this.lastSelection.ui.checkbox.checked = !this.lastSelection.ui.checkbox.checked;
					this.checkChanged(this.lastSelection, this.lastSelection.ui.checkbox.checked);
				}
			}
		}
	},

	selectNext : function() {
		if (this.lastSelection == null) {
			this.lastSelection = this.treeData[0]._node_object;
			this.lastSelection.select();
		}
		else {
			if (this.lastSelection.nextSibling) {
				this.lastSelection = this.lastSelection.nextSibling;
				this.lastSelection.select();
			}
			else
				this.expandNode();
		}
	},

	selectPrev : function() {
		if (this.lastSelection) {
			if (this.lastSelection.previousSibling) {
				this.lastSelection = this.lastSelection.previousSibling;
				this.lastSelection.select();
			}
			else
				this.selectParent();
		}
	},

	onDestroy : function(){
        if(this.view){
            this.view.destroy();
        }
        if(this.list){
            this.list.destroy();
        }
        Ext.form.TreeBox.superclass.onDestroy.call(this);
    },


    /**
     * Returns the currently selected field value or empty string if no value is set.
     * @return {String} value The selected value
     */
    getValue : function(){
        if(this.hiddenField) {
            return this.hiddenField.value;
        }
        return typeof this.value != 'undefined' ? this.value : '';
    },

    /**
     * Clears any text/value currently set in the field
     */
    clearValue : function(){
        if(this.hiddenField){
            this.hiddenField.value = '';
        }
        this.setRawValue('');
        this.lastSelectionText = '';
        this.applyEmptyText();
    },

	findRecord : function(id) {
		var i = id.indexOf("=");
		if (i == -1)
			return id;
		return id.substr(i + 1);
	},

    setValue : function(v) {
        var text = v;
		if (this.singleMode)
		{
			var pos = text.indexOf("=");
			if (pos != -1) {
				v = text.substr(0, pos);
				text = text.substr(pos + 1);
			}
		}
		else
		{
			v = "" + v;
			var vals = v.split(",");
			text = "";
			var i;
			this._text_cache = [];
			v = "";
			for (var i = 0; i < vals.length; i++)
			{
				if (i > 0) {
					text += ",";
					v += ",";
				}
				var pos = vals[i].indexOf("=");
				if (pos == -1) {
					this._text_cache[i] = vals[i];
					v += vals[i];
				} else {
					v += vals[i].substr(0, pos);
					this._text_cache[i] = vals[i].substr(pos + 1);
				}
				text += this._text_cache[i];
			}
		}
        if(this.hiddenField){
           this.hiddenField.value = v;
        }
		else
			this.hiddenValue = v;
        Ext.form.TreeBox.superclass.setValue.call(this, text);
        this.value = v;
    },

    // private
    onEmptyResults : function(){
        this.collapse();
    },

    /**
     * Returns true if the dropdown list is expanded, else false.
     */
    isExpanded : function(){
        return this.list.isVisible();
    },

    // private
    validateBlur : function(){
        return !this.list || !this.list.isVisible();   
    },

    /**
     * Hides the dropdown list if it is currently expanded. Fires the 'collapse' event on completion.
     */
    collapse : function(){
        if(!this.isExpanded()){
            return;
        }
        this.list.hide();
        Ext.get(document).un('mousedown', this.collapseIf, this);
        Ext.get(document).un('mousewheel', this.collapseIf, this);
        this.fireEvent('collapse', this);
    },

    // private
    collapseIf : function(e){
        if(!e.within(this.wrap) && !e.within(this.list)){
            this.collapse();
        }
    },

	recalcSize : function() {
        var inner = this.view.getTreeEl().dom;
        var lw = this.listWidth || Math.max(this.wrap.getWidth(), this.minListWidth);
        this.list.setWidth(lw);
        var h = Math.max(inner.clientHeight, inner.offsetHeight, inner.scrollHeight);
        this.list.beginUpdate();
        this.list.setHeight(h < this.maxHeight ? 'auto' : this.maxHeight);
        this.list.setHeight(this.list.getHeight()+this.list.getFrameWidth('tb'));
        this.list.alignTo(this.el, this.listAlign);
        this.list.endUpdate();
	},

    /**
     * Expands the dropdown list if it is currently hidden. Fires the 'expand' event on completion.
     */
    expand : function(){
        if(this.isExpanded() || !this.hasFocus){
            return;
        }
        this.list.alignTo(this.el, this.listAlign);
		this.recalcSize();
        this.list.show();
        Ext.get(document).on('mousedown', this.collapseIf, this);
        Ext.get(document).on('mousewheel', this.collapseIf, this);
        this.fireEvent('expand', this);
    },

    // private
    // Implements the default empty TriggerField.onTriggerClick function
    onTriggerClick : function(){
        if(this.disabled){
            return;
        }
        if(this.isExpanded()){
            this.collapse();
            this.el.focus();
        }else {
            this.hasFocus = true;
            this.expand();
            this.el.focus();
        }
    }
});

Ext.form.UploadField = function(config){
    Ext.form.UploadField.superclass.constructor.call(this, config);
};

Ext.extend(Ext.form.UploadField, Ext.form.TextField,  {
    /**
     * @cfg {String} triggerClass A CSS class to apply to the trigger
     */
    /**
     * @cfg {String/Object} autoCreate A DomHelper element spec, or true for a default element spec (defaults to
     * {tag: "input", type: "text", size: "16", autocomplete: "off"})
     */
    defaultAutoCreate : {tag: "input", type: "hidden", size: "16", autocomplete: "off"},
    /**
     * @cfg {Boolean} hideTrigger True to hide the trigger element and display only the base text field (defaults to false)
     */
    hideTrigger:false,

    /** @cfg {Boolean} grow @hide */
    /** @cfg {Number} growMin @hide */
    /** @cfg {Number} growMax @hide */

    /**
     * @hide 
     * @method
     */
    autoSize: Ext.emptyFn,
    // private
    monitorTab : true,
    // private
    deferHeight : true,

    // private
    onResize : function(w, h){
        Ext.form.UploadField.superclass.onResize.apply(this, arguments);
        if(typeof w == 'number'){
            this.el.setWidth(this.adjustWidth('input', w - this.trigger.getWidth()));
        }
    },

    // private
    adjustSize : Ext.BoxComponent.prototype.adjustSize,

    // private
    getResizeEl : function(){
        return this.wrap;
    },

    // private
    getPositionEl : function(){
        return this.wrap;
    },

    // private
    alignErrorIcon : function(){
        this.errorIcon.alignTo(this.wrap, 'tl-tr', [2, 0]);
    },

    // private
    onRender : function(ct, position){
        Ext.form.UploadField.superclass.onRender.call(this, ct, position);
        this.wrap = this.el.wrap({cls: "x-form-field-wrap"});
        this.trigger = this.wrap.createChild(this.triggerConfig ||
                {tag: "img", src: "/sysmgr/itsm/images/add.gif", style: "cursor:hand;vertical-align:text-bottom" });
				this.files = [];
				this.fileList = this.wrap.createChild({tag: "div"});

        if(this.hideTrigger){
            this.trigger.setDisplayed(false);
        }
        this.initTrigger();
        if(!this.width){
            this.wrap.setWidth(this.el.getWidth()+this.trigger.getWidth());
        }
		this.renderFiles();
    },

    // private
    initTrigger : function(){
        this.trigger.on("click", this.onTriggerClick, this, {preventDefault:true});
    },

    // private
    onDestroy : function(){
        if(this.trigger){
            this.trigger.removeAllListeners();
            this.trigger.remove();
        }
        if(this.wrap){
            this.wrap.remove();
        }
        Ext.form.UploadField.superclass.onDestroy.call(this);
    },

    // private
    onDisable : function(){
        Ext.form.UploadField.superclass.onDisable.call(this);
        if(this.wrap){
            this.wrap.addClass('x-item-disabled');
        }
    },

    // private
    onEnable : function(){
        Ext.form.UploadField.superclass.onEnable.call(this);
        if(this.wrap){
            this.wrap.removeClass('x-item-disabled');
        }
    },

    // private
    onShow : function(){
        if(this.wrap){
            this.wrap.dom.style.display = '';
            this.wrap.dom.style.visibility = 'visible';
        }
    },

    // private
    onHide : function(){
        this.wrap.dom.style.display = 'none';
    },

	removeFile : function(i) {
		var n = this.files.length;
		for (var j = i; j < n - 1; j++)
			this.files[j] = this.files[j + 1];
		this.files.length = n - 1;
		this.renderFiles();
		this.calcValue();
	},

	calcValue : function() {
		var v = "";
		for (var i = 0; i < this.files.length; i++) {
			if (i > 0)
				v += ";";
			v += this.files[i][1];
		}
        Ext.form.UploadField.superclass.setValue.call(this, v);
	},

	setValue : function(v) {
		v = "" + v;
		var s = v.split(";");
		this.files = [];
		for (var i = 0; i < s.length; i++) {
			if (s[i] == "")
				continue;
			this.files[this.files.length] = [this.getFileName(s[i]), s[i]];
		}
		this.renderFiles();
		this.calcValue();
	},

	getFileName : function(t) {
		var t0 = t;
		pos = t.lastIndexOf("\\");
		if (pos == -1)
			pos = t.lastIndexOf("/");
		if (pos != -1)
			t0 = t.substring(pos + 1);
		return t0;
	},

	renderFiles : function() {
		if (!this.fileList)
			return;
		this.fileList.dom.innerHTML = "";
		for (var i = 0; i < this.files.length; i++)
		{
			if (i > 0)
				this.fileList.createChild({tag: "br"});
			var p = this.fileList.createChild({tag: "img", src: "/sysmgr/itsm/images/delete.gif",
				style: "cursor:hand;vertical-align:text-bottom"});
			p._id = i;
			p._obj = this;
			p.on("click", function() { this._obj.removeFile(this._id); }, p);
			this.fileList.createChild({tag: "span"}).dom.innerText = " ";
			this.fileList.createChild({tag: "a", targer: "_blank", href: "/sysmgr/" + this.files[i][1]}).dom.innerText = this.files[i][0];
		}
	},

	onUpload : function(obj, t) {
		var t0 = obj.getFileName(t);

		obj.files[obj.files.length] = [t0, t];
		obj.renderFiles();
		obj.calcValue();
	},

    onTriggerClick : function()
	{
		uploadFile(this.onUpload, this);
	}
});


Ext.form.IdField = function(config){
    Ext.form.TreeBox.superclass.constructor.call(this, config);
};

Ext.extend(Ext.form.IdField, Ext.form.TriggerField, {
    defaultAutoCreate : {tag: "input", type: "text", size: "24", autocomplete: "off"},
    shadow:'sides',
	prefix:'',
	autoId:'id',
	getAutoId: function(obj, val) { return val; },
	getId: function(obj, val) { return obj.prefix + val; },
	
    // private
    onRender : function(ct, position){
        Ext.form.TreeBox.superclass.onRender.call(this, ct, position);

		if(Ext.isGecko){
            this.el.dom.setAttribute('autocomplete', 'off');
        }

		this.el.dom.setAttribute('readOnly', true);
		this.el.addClass('x-combo-noedit');
    },

    // private
    // Implements the default empty TriggerField.onTriggerClick function
    onTriggerClick : function(){
        if(this.disabled){
            return;
        }
		var postId = this.getAutoId(this, this.autoId);
        var simple = new Ext.form.Form({
			method: 'POST',
			baseParams: {id: postId},
			errorReader: new Ext.form.XmlErrorReader(),
			url:'/sysmgr/itsm/getId.jsp'
		});
		simple._obj = this;
		simple._el = document.createElement("div");
		simple.render(simple._el);
		simple.submit({
			waitMsg: '请稍候...',
			success: function(form, action) 
			{
				form._obj.setValue(form._obj.getId(form._obj, form.errorReader.xmlData.documentElement.text));
				form._el.outerHTML = "";
			},
			failure: function(form, action)
			{
				Ext.MessageBox.alert("失败", form.errorReader.xmlData.documentElement.text);
			}
		});
    }
});

Ext.override(Ext.Window, {
    addButton : function(config, handler, scope){
		var btn = Ext.Window.superclass.addButton.call(this, config, handler, scope);
		if (this.rendered) {
			var tb = this.footer.dom.getElementsByTagName('table')[0];
			var tr = tb.getElementsByTagName('tr')[0];
			var td = document.createElement('td');
			td.className = 'x-panel-btn-td';
			btn.render(tr.appendChild(td));
		}
		return btn;
    }
});

Ext.loadRemoteScript = function(url, params) {
	var elem = document.createElement("div");
	document.body.appendChild(elem);
	var cp = new Ext.Panel({el: elem, border: false, hidden: true });
	Ext.getBody().mask("loading", 'x-mask-loading');
	cp.render();
	cp.load({
		'url': url,
		'params': params,
		callback: function() { this.destroy(true); Ext.getBody().unmask(); },
		scope: cp,
		discardUrl: false,
		nocache: true,
		timeout: 30,
		scripts: true
	});
}

// 复制于Ext的示例
Ext.grid.RowExpander = function(config){
    Ext.apply(this, config);

    this.addEvents({
        beforeexpand : true,
        expand: true,
        beforecollapse: true,
        collapse: true
    });

    Ext.grid.RowExpander.superclass.constructor.call(this);

    if(this.tpl){
        if(typeof this.tpl == 'string'){
            this.tpl = new Ext.Template(this.tpl);
        }
        this.tpl.compile();
    }

    this.state = {};
    this.bodyContent = {};
};

Ext.extend(Ext.grid.RowExpander, Ext.util.Observable, {
    header: "",
    width: 20,
    sortable: false,
    fixed:true,
    dataIndex: '',
    id: 'expander',
    lazyRender : true,
    enableCaching: true,

    getRowClass : function(record, rowIndex, p, ds){
        p.cols = p.cols-1;
        var content = this.bodyContent[record.id];
        if(!content && !this.lazyRender){
            content = this.getBodyContent(record, rowIndex);
        }
        if(content){
            p.body = content;
        }
        return this.state[record.id] ? 'x-grid3-row-expanded' : 'x-grid3-row-collapsed';
    },

    init : function(grid){
        this.grid = grid;

        var view = grid.getView();
        view.getRowClass = this.getRowClass.createDelegate(this);

        view.enableRowBody = true;

        grid.on('render', function(){
            view.mainBody.on('mousedown', this.onMouseDown, this);
        }, this);
    },

    getBodyContent : function(record, index){
        if(!this.enableCaching){
            return this.tpl.apply(record.data);
        }
        var content = this.bodyContent[record.id];
        if(!content){
            content = this.tpl.apply(record.data);
            this.bodyContent[record.id] = content;
        }
        return content;
    },

    onMouseDown : function(e, t){
        if(t.className == 'x-grid3-row-expander'){
            e.stopEvent();
            var row = e.getTarget('.x-grid3-row');
            this.toggleRow(row);
        }
    },

    renderer : function(v, p, record){
        p.cellAttr = 'rowspan="2"';
        return '<div class="x-grid3-row-expander">&#160;</div>';
    },

    beforeExpand : function(record, body, rowIndex){
        if(this.fireEvent('beforeexpand', this, record, body, rowIndex) !== false){
            if(this.tpl && this.lazyRender){
                body.innerHTML = this.getBodyContent(record, rowIndex);
            }
            return true;
        }else{
            return false;
        }
    },

    toggleRow : function(row){
        if(typeof row == 'number'){
            row = this.grid.view.getRow(row);
        }
        this[Ext.fly(row).hasClass('x-grid3-row-collapsed') ? 'expandRow' : 'collapseRow'](row);
    },

    expandRow : function(row){
        if(typeof row == 'number'){
            row = this.grid.view.getRow(row);
        }
        var record = this.grid.store.getAt(row.rowIndex);
        var body = Ext.DomQuery.selectNode('tr:nth(2) div.x-grid3-row-body', row);
        if(this.beforeExpand(record, body, row.rowIndex)){
            this.state[record.id] = true;
            Ext.fly(row).replaceClass('x-grid3-row-collapsed', 'x-grid3-row-expanded');
            this.fireEvent('expand', this, record, body, row.rowIndex);
        }
    },

    collapseRow : function(row){
        if(typeof row == 'number'){
            row = this.grid.view.getRow(row);
        }
        var record = this.grid.store.getAt(row.rowIndex);
        var body = Ext.fly(row).child('tr:nth(1) div.x-grid3-row-body', true);
        if(this.fireEvent('beforcollapse', this, record, body, row.rowIndex) !== false){
            this.state[record.id] = false;
            Ext.fly(row).replaceClass('x-grid3-row-expanded', 'x-grid3-row-collapsed');
            this.fireEvent('collapse', this, record, body, row.rowIndex);
        }
    }
});

/*
 * Ext JS Library 2.0
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

Ext.tree.ColumnTree = Ext.extend(Ext.tree.TreePanel, {
    lines:false,
    borderWidth: Ext.isBorderBox ? 0 : 2, // the combined left/right border for each cell
    cls:'x-column-tree',
    
    onRender : function(){
        Ext.tree.ColumnTree.superclass.onRender.apply(this, arguments);
        this.headers = this.body.createChild(
            {cls:'x-tree-headers'},this.innerCt.dom);

        var cols = this.columns, c;
        var totalWidth = 0;

        for(var i = 0, len = cols.length; i < len; i++){
             c = cols[i];
             totalWidth += c.width;
             this.headers.createChild({
                 cls:'x-tree-hd ' + (c.cls?c.cls+'-hd':''),
                 cn: {
                     cls:'x-tree-hd-text',
                     html: c.header
                 },
                 style:'width:'+(c.width-this.borderWidth)+'px;'
             });
        }
        this.headers.createChild({cls:'x-clear'});
        // prevent floats from wrapping when clipped
        this.headers.setWidth(totalWidth);
        this.innerCt.setWidth(totalWidth);
    }
});

Ext.tree.ColumnNodeUI = Ext.extend(Ext.tree.TreeNodeUI, {
    focus: Ext.emptyFn, // prevent odd scrolling behavior

    renderElements : function(n, a, targetNode, bulkRender){
        this.indentMarkup = n.parentNode ? n.parentNode.ui.getChildIndent() : '';

        var t = n.getOwnerTree();
        var cols = t.columns;
        var bw = t.borderWidth;
        var c = cols[0];

        var buf = [
             '<li class="x-tree-node"><div ext:tree-node-id="',n.id,'" class="x-tree-node-el x-tree-node-leaf ', a.cls,'">',
                '<div class="x-tree-col" style="width:',c.width-bw,'px;">',
                    '<span class="x-tree-node-indent">',this.indentMarkup,"</span>",
                    '<img src="', this.emptyIcon, '" class="x-tree-ec-icon x-tree-elbow">',
                    '<img src="', a.icon || this.emptyIcon, '" class="x-tree-node-icon',(a.icon ? " x-tree-node-inline-icon" : ""),(a.iconCls ? " "+a.iconCls : ""),'" unselectable="on">',
                    '<a hidefocus="on" class="x-tree-node-anchor" href="',a.href ? a.href : "#",'" tabIndex="1" ',
                    a.hrefTarget ? ' target="'+a.hrefTarget+'"' : "", '>',
                    '<span unselectable="on">', n.text || (c.renderer ? c.renderer(a[c.dataIndex], n, a) : a[c.dataIndex]),"</span></a>",
                "</div>"];
         for(var i = 1, len = cols.length; i < len; i++){
             c = cols[i];

             buf.push('<div class="x-tree-col ',(c.cls?c.cls:''),'" style="width:',c.width-bw,'px;">',
                        '<div class="x-tree-col-text">',(c.renderer ? c.renderer(a[c.dataIndex], n, a) : a[c.dataIndex]),"</div>",
                      "</div>");
         }
         buf.push(
            '<div class="x-clear"></div></div>',
            '<ul class="x-tree-node-ct" style="display:none;"></ul>',
            "</li>");

        if(bulkRender !== true && n.nextSibling && n.nextSibling.ui.getEl()){
            this.wrap = Ext.DomHelper.insertHtml("beforeBegin",
                                n.nextSibling.ui.getEl(), buf.join(""));
        }else{
            this.wrap = Ext.DomHelper.insertHtml("beforeEnd", targetNode, buf.join(""));
        }

        this.elNode = this.wrap.childNodes[0];
        this.ctNode = this.wrap.childNodes[1];
        var cs = this.elNode.firstChild.childNodes;
        this.indentNode = cs[0];
        this.ecNode = cs[1];
        this.iconNode = cs[2];
        this.anchor = cs[3];
        this.textNode = cs[3].firstChild;
    }
});

Ext.form.SearchField = Ext.extend(Ext.form.TwinTriggerField, {
    initComponent : function(){
        Ext.form.SearchField.superclass.initComponent.call(this);
        this.on('specialkey', function(f, e){
            if(e.getKey() == e.ENTER){
                this.onTrigger2Click();
            }
        }, this);
    },

    validationEvent:false,
    validateOnBlur:false,
    trigger1Class:'x-form-clear-trigger',
    trigger2Class:'x-form-search-trigger',
    hideTrigger1:true,
    width:180,
    hasSearch : false,
    paramName : 'query',

    onTrigger1Click : function(){
        if(this.hasSearch){
            this.el.dom.value = '';
            var o = {start: 0};
            this.store.baseParams = this.store.baseParams || {};
            this.store.baseParams[this.paramName] = '';
            this.store.reload({params:o});
            this.triggers[0].hide();
            this.hasSearch = false;
        }
    },

    onTrigger2Click : function(){
        var v = this.getRawValue();
        if(v.length < 1){
            this.onTrigger1Click();
            return;
        }
        var o = {start: 0};
        this.store.baseParams = this.store.baseParams || {};
        this.store.baseParams[this.paramName] = v;
        this.store.reload({params:o});
        this.hasSearch = true;
        this.triggers[0].show();
    }
});

Ext.form.TextButtonField = Ext.extend(Ext.form.TextField, {		
		defaultAutoCreate : {tag: "input", type: "text", size: "16", autocomplete: "off"},
		hideTrigger:false,
		// private
	  onRender : function(ct, position){
	      Ext.form.TextButtonField.superclass.onRender.call(this, ct, position);
	      this.wrap = this.el.wrap({cls: "x-form-field-wrap"});
	      var confTrigger = {tag:'span', cls:'x-form-twin-triggers', cn: []};
	      
				if (this.buttons){
					if (this.buttons instanceof Array) {
						for(var i = 0, len = this.buttons.length; i < len; i++) {
							confTrigger.cn[confTrigger.cn.length] = {tag:'span',cls: "x-form-trigger"};
						}
						var trigger = this.wrap.createChild(confTrigger, null, true);
            for(var i = 0, len = this.buttons.length; i < len; i++) {
            	var span = trigger.getElementsByTagName('span')[i];
                var cfg = {};
	            	Ext.apply(cfg, this.buttons[i]);
	              var b = new Ext.Button(cfg);
                var td = document.createElement('span');
                b.render(span);
            }
            
					}
				}
	
	  }
});

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
});


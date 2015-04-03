// extjs 扩展代码

var ITSM_HOME = "/qc";
var ITSM_HOME_PARENT = "/";

Ext.BLANK_IMAGE_URL="/extjs/resources/images/default/s.gif";

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
		url:sc.uploadUrl?sc.uploadUrl:(ITSM_HOME+'/upload.jsp')
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
							window_upload.hide();
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


//////////////////////////////////////////////////////////////Ext.form.UploadField//////////////////////////////

Ext.form.UploadField = function(config){
    Ext.form.UploadField.superclass.constructor.call(this, config);
};

Ext.extend(Ext.form.UploadField, Ext.form.TwinTriggerField,  {
    /**
     * @cfg {String} triggerClass A CSS class to apply to the trigger
     */
    /**
     * @cfg {String/Object} autoCreate A DomHelper element spec, or true for a default element spec (defaults to
     * {tag: "input", type: "text", size: "16", autocomplete: "off"})
     */
    //defaultAutoCreate : {tag: "input", type: "hidden", size: "16", autocomplete: "off"},
    /**
     * @cfg {Boolean} hideTrigger True to hide the trigger element and display only the base text field (defaults to false)
     */
    hideTrigger:false,
    hideTriggers:[true,false],

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

    maxHeight:150,
    trigger2Class:'x-form-download-trigger',
    trigger1Class:'x-form-upload-trigger',
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
        //this.wrap = this.el.wrap({cls: "x-form-field-wrap"});

        //this.trigger = this.wrap.createChild(
        //        {tag: "img", src: ITSM_HOME+"/images/add.gif", style: "cursor:hand;vertical-align:text-bottom" });
				//this.files = new Array();
				var hiddenName = this.hiddenName?this.hiddenName:this.name;
				var hiddenId = (this.hiddenId|this.hiddenName)?(this.hiddenId|this.hiddenName):(this.id||this.name);
        this.hiddenField = this.el.insertSibling({tag:'input', type:'hidden', name: hiddenName, id: hiddenId},
                'before', true);
        this.hiddenField.value =
            this.hiddenValue !== undefined ? this.hiddenValue :
            this.value !== undefined ? this.value : '';
        // prevent input submission
        this.el.dom.removeAttribute('name');
        
      	var cls = 'x-combo-list';

        this.list = new Ext.Layer({
            shadow: this.shadow, cls: [cls, this.listClass].join(' '), constrain:false
        });

        var lw = this.listWidth || Math.max(this.wrap.getWidth(), 20);
        this.list.setWidth(lw);
        this.list.swallowEvent('mousewheel');
				this.list.dom.style.overflow = 'auto';
				this.view = new Ext.Panel({
					el:this.list,
					animate:false,
					autoHeight : true
				});
				this.fileList = this.view.getEl();//this.wrap.createChild({tag: "div"});

        if(this.hideTrigger){
            this.trigger.setDisplayed(false);
        }
        //this.initTrigger();
        if(!this.width){
            this.wrap.setWidth(this.el.getWidth()+this.trigger.getWidth());
        }
			this.renderFiles();
			this.el.dom.setAttribute('readOnly', true);
			this.el.on('mousedown', function(){if (this.files && this.files.length>0 || this.readOnly)this.onTrigger2Click();else this.onTrigger1Click();},  this);
			this.el.addClass('x-combo-noedit');
    },

	setReadOnly : function(r){
  	Ext.form.UploadField.superclass.setReadOnly.call(this,r);
  	this.renderFiles();
  },
	removeFile : function(i) {
		var n = this.files.length;
		for (var j = i; j < n - 1; j++)
			this.files[j] = this.files[j + 1];
		this.files.length = n - 1;
		this.renderFiles();
		this.calcValue();
	},
	
  onDisable : function(){
      Ext.form.UploadField.superclass.onDisable.call(this);
      this.triggers[0].hide();
  },

  onEnable : function(){
      Ext.form.UploadField.superclass.onEnable.call(this);
      this.triggers[0].show();
  },

	calcValue : function() {
		var v = "";
		var vv = "";
		for (var i = 0; i < this.files.length; i++) {
			if (i > 0) {
				v += ";";
				vv += ";";
			}
			v += this.files[i][0];
			vv += this.files[i][1];
		}
		this.hiddenField.value = vv;
    Ext.form.UploadField.superclass.setValue.call(this, v);
	},

	getValue :function () {
		if (this.hiddenField && this.hiddenField.value)
			return this.hiddenField.value;
		else
      return Ext.form.UploadField.superclass.getValue.call(this);
	},
	setValue : function(v) {
		v = "" + v;
		var s = v.split(";");
		this.files = new Array();
		var index = 0;
		for (var i = 0; i < s.length; i++) {
			if (s[i]!=null && s[i] != ""){
			//setTimeout("alert('d')",100);
			//alert("fd");
			var fn = this.getFileName(s[i]);
			this.files[index] = [fn, s[i]];
			index++;
		}
			//alert("setValue-"+this.files.length);
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

	renderFiles : function(fa) {
		if (!this.fileList)
			return;
		if (!this.files)
			return;
		this.fileList.dom.innerHTML = "";
		for (var i = 0; i < this.files.length; i++)
		{
			if (i > 0)
				this.fileList.createChild({tag: "br"});
			
			if (this.readOnly && this.readOnly === true)
				;
			else {
				var p = this.fileList.createChild({tag: "img", src: ITSM_HOME+"/images/delete.gif",
					style: "cursor:hand;vertical-align:text-bottom"});
				p._id = i;
				p._obj = this;
				p.on("click", function() { this._obj.removeFile(this._id); }, p);
			}
			this.fileList.createChild({tag: "span"}).dom.innerText = " ";
			var fileName_ = this.files[i][0];
			var filePath_ = this.files[i][1];
			//if (fileName_.length>20)
			//	fileName_ = fileName_.substring(0,20)+"...";
			var reg = /\(\d+\)\./;
			var upTimeReg = /\(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\)$/
			fileName_ = fileName_.replace(reg,".");
			filePath_ = filePath_.replace(upTimeReg,"");
			this.fileList.createChild({tag: "a", cls:"textOverflow", targer: "_blank",title:this.files[i][0], href: (this.downLoadUrl?this.downLoadUrl:(ITSM_HOME+"/download.jsp?"))+"fileName=" + filePath_}).dom.innerText = fileName_;
			//this.fileList.createChild({tag: "a", targer: "_blank", title:this.files[i][0], href: ITSM_HOME+"/download.jsp?fileName=" + this.files[i][1]}).dom.innerText = fileName_;
		}

	},

	onUpload : function(obj, t) {
		var t0 = obj.getFileName(t);
		obj.files[obj.files.length] = [t0, t];
		obj.renderFiles();
		obj.calcValue();
	},

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

	recalcSize : function(pNode) {
    var inner = this.view.getEl().dom;
    var lw = this.listWidth || Math.max(this.wrap.getWidth(), 70);
    this.list.setWidth(lw);
    var h = Math.max(inner.clientHeight, inner.offsetHeight, inner.scrollHeight);
    this.list.beginUpdate();
    this.list.setHeight(h < this.maxHeight ? 'auto' : this.maxHeight);
    this.list.setHeight(this.list.getHeight()+this.list.getFrameWidth('tb'));
    this.list.alignTo(this.wrap, this.listAlign);
    this.list.endUpdate();

	},
	expand : function(){
    if(this.isExpanded() || !this.hasFocus){
        return;
    }
    this.list.alignTo(this.wrap, this.listAlign);
		this.recalcSize();
    this.list.show();
    Ext.get(document).on('mousedown', this.collapseIf, this);
    Ext.get(document).on('mousewheel', this.collapseIf, this);

    this.fireEvent('expand', this);
  },
	isExpanded : function(){
      return this.list.isVisible();
  },
  onTrigger1Click : function()
	{
		if (!this.files)
			this.files = new Array();
		uploadFile(this.onUpload, this);
	},
	onTrigger2Click : function(){
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

//////////////////////////////////////////////////////////////////Ext.form.UploadField2/////////////////////

Ext.form.UploadField2 = Ext.extend(Ext.form.UploadField, {
	
	//统一存储的模块名，用来标识是哪个模块的附件
	module:'NO',
	calcValue : function() {
		var v = Ext.encode(this.files);
		var vv = "";
		for (var i = 0; i < this.files.length; i++)
		{
			if (i > 0) {
				vv += ";";
			}
			vv += this.files[i].name;
		}
		this.hiddenField.value = v;
		
   	Ext.form.UploadField.superclass.setValue.call(this, vv);
	},
	renderFiles:function(fat) {
		if (!this.fileList)
			return;
		if (!this.files)
			return;
		this.fileList.dom.innerHTML = "";
		for (var i = 0; i < this.files.length; i++)
		{
			if (i > 0)
				this.fileList.createChild({tag: "br"});
			if (this.readOnly && this.readOnly === true)
				;
			else {
				var p = this.fileList.createChild({tag: "img", src: ITSM_HOME+"/images/delete.gif",
					style: "cursor:hand;vertical-align:text-bottom"});
				p._id = i;
				p._obj = this;
				p.on("click", function() { this._obj.removeFile(this._id); }, p);
			}
			this.fileList.createChild({tag: "span"}).dom.innerText = " ";
			
			if (this.files[i].oldFormat){
				var fileName_ = this.files[i].fn;
				var filePath_ = this.files[i].fp;
				var reg = /\(\d+\)\./;
				var upTimeReg = /\(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\)$/
				fileName_ = fileName_.replace(reg,".");
				filePath_ = filePath_.replace(upTimeReg,"");
				this.fileList.createChild({tag: "a", cls:"textOverflow", targer: "_blank",title:this.files[i].fn, href: (this.downLoadUrl?this.downLoadUrl:(ITSM_HOME+"/download.jsp?"))+"fileName=" + filePath_}).dom.innerText = fileName_;

			} else {
				var fileId_ = this.files[i].oid;
				var fileName_ = this.files[i].name;
				this.fileList.createChild({tag: "a", cls:"textOverflow", targer: "_blank",title:fileName_, href: (this.downLoadUrl?this.downLoadUrl+"?oid=":("/common/download?oid="))+ fileId_}).dom.innerText = fileName_;
			}
		}
	},

	setValue : function(v) {
		//if (!this.files)
		this.files = new Array();
		if (v ==null || v == ""||v=='null'){
	//	if (v ==null || v == ""){
		//	v = "[]" + v;
		
			v = "[]";
		}
		if(v.charAt(0) == '/' || v.indexOf("uploads/") == 0){//兼容老版本的结构
			var s = v.split(";");
			var index = 0;
			for (var i = 0; i < s.length; i++) {
				if (s[i]!=null && s[i] != ""){
					
					var fn = this.getFileName(s[i]);
					var jsonObj = {fn:fn,fp:s[i],oldFormat:true,newAdd:false};
					this.files[index] = jsonObj;
					index++;
				}
			}
		} else {//-----------新版本的结构
			var jsonS = Ext.decode(v);
			var index = 0;
			for (var i = 0; i < jsonS.length; i++) {
				if (jsonS[i]!=null){
					jsonS[i].newAdd = false;
					if(jsonS[i].oldFormat)
						jsonS[i].oldFormat = true;
					else
						jsonS[i].oldFormat = false;
					this.files[index] = jsonS[i];
					index++;
				}
			}
		}
		this.renderFiles();
		this.calcValue();
	},
	onUpload : function (obj,v) {
		if (!obj.files)
			obj.files = new Array();
		var jsonS = Ext.decode(v);
		var index = obj.files.length;
		for (var i = 0; i < jsonS.length; i++) {
			if (jsonS[i]!=null){
				jsonS[i].newAdd = true;
				jsonS[i].oldFormat = false;
				obj.files[index] = jsonS[i];
				index++;
			}
		}
		obj.renderFiles();
		obj.calcValue();
	},
	onTrigger1Click : function(){
		if (!this.files)
			this.files = new Array();
		var form = new Ext.form.FormPanel({
			baseCls: 'x-plain',
			method: 'POST',
			fileUpload: true,
			defaultType: 'textfield',
			baseParams:{module:this.module?this.module:"NO"},
			errorReader: new Ext.form.XmlErrorReader(),
			labelWidth: 75, // label settings here cascade unless overridden
			labelAlign: 'top',
			url:this.uploadUrl?this.uploadUrl:('/common/upload')
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
							window_upload.onUpload(window_upload.obj,form.errorReader.xmlData.documentElement.text);
							if (window_upload) {
								window_upload.hide();
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
		window_upload.onUpload = this.onUpload;
		window_upload.obj =  this;
    window_upload.show();
	}
});

Ext.reg('uploadfield2', Ext.form.UploadField2);

Ext.tree.FilterTreeLoader = function(config){
	Ext.tree.FilterTreeLoader.superclass.constructor.call(this, config);
}

Ext.extend(Ext.tree.FilterTreeLoader, Ext.tree.TreeLoader, {
    createNode : function(attr){
      if (attr._click && this.regexId && !this.regexId.test(attr.id))
          return null;
      if (attr._click && this.regexText && !this.regexText.test(attr.text))
          return null;
			if (this.box && this.box.singleMode == false) {
				if (attr._click) {
					attr.checked = false;
				}
			}
			var ret = Ext.tree.FilterTreeLoader.superclass.createNode.call(this, attr);
			ret._click = attr._click;
			ret.alertMsg = attr.alertMsg;
			if (attr && attr.otherParams && (attr.otherParams instanceof Object)) {
				for (var attrop in attr.otherParams) {
					ret[attrop] = attr.otherParams[attrop];
				}
			}
			if (this.box && this.box.singleMode == false)
			{
				ret.on("checkchange", this.box.checkChanged, this.box);
				ret.on("dblclick", this.box.dblClick, this.box);
				ret.on("expand",this.box.afterExpand,this.box);
			}
			return ret;
    }
});

Ext.form.TreeBox = function(config){
	Ext.form.TreeBox.superclass.constructor.call(this, config);
	this.regexId = config.regexId;
	this.regexText = config.regexText;
}

Ext.form.TreeBox = Ext.extend(Ext.form.TwinTriggerField, {
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
	pathMode:false,
	readOnly:false,
  listAlign: 'tl-bl?',
  maxHeight: 150,
  resizable: true,
  handleHeight : 8,
  minListWidth : 70,
	viewLoader : null,
	trigger1Class:'x-form-clear-trigger',
  trigger2Class:'x-form-trigger',
	hideTriggers:[true,true],

	initComponent : function(){
		Ext.form.ComboBox.superclass.initComponent.call(this);
		this.addEvents({
			'expand' : true,
			'collapse' : true,
			'beforeselect' : true,
			'change' : true,
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
		this.triggerConfig = {
            tag:'span', cls:'x-form-twin-triggers', cn:[
            {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger1Class},
            {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger2Class}
        ]};
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
		// this.list.dom.style.overflow = 'auto';
		this.view = new Ext.tree.TreePanel({
			el:this.list,
			animate:false,
			// autoHeight : true,
			autoScroll: true,
			rootVisible: false,
			//loader: this.viewLoader,
			containerScroll: true
			//,tbar:['->','-',{text:'清空',ownerC:this,handler:function(){this.ownerC.setValue("=");}}]
		});
		var root = null;
		if (!this.treeData)
			root = new Ext.tree.AsyncTreeNode({
				text: 'Ext JS',
				id:'_',
				draggable:false
			});
		else
			root = new Ext.tree.TreeNode({
				text: 'Ext JS',
				id:'_',
				draggable:false
			});
		this.view.setRootNode(root);

		if (!this.treeData)
		{
			this.view.loader = this.viewLoader;
			this.view.loader.regexId = this.regexId;
			this.view.loader.regexText = this.regexText;
			this.view.loader.box = this;
			this.view.ownerC = this;
			this.view.loader.on('beforeload', this.beforeload, this);

		} else {
			this.loadTree(root, this.treeData);
			if (!this.singleMode) {
				this.view.on("dblclick", this.dblClick, this);
				this.view.on("checkchange", this.checkChanged, this);
				this.view.on("expand",this.afterExpand,this);
			}
		}

		this.view.getSelectionModel().on("selectionchange", this.selectionChange, this);

		this.view.on('expand', function() { this.view.root.expand(); }, this);
		this.view.on('expandnode', this.recalcSize, this);
		this.view.on('collapsenode', this.recalcSize, this);

		this.view.on('click', this.onItemSelect, this);
		this.renderTree();

		this.el.dom.setAttribute('readOnly', true);
		this.el.on('mousedown', function(){if (!this.readOnly)this.onTriggerClick();},  this);
		this.el.addClass('x-combo-noedit');

		this.resizer = new Ext.Resizable(this.list,  {
                   pinned:true, handles:'se'
                });
		
		this.resizer.on('resize', function(r, w, h){
					this.list.beginUpdate();
					this.list.setHeight(h < 150 ? 150 : h);
					this.view.setHeight(h < 150 ? 150 : h);
					this.list.setWidth(w < 150 ? 150 : w);
					this.view.setWidth((w  < 150 ? 150 : w) - this.list.getFrameWidth('lr'));
					this.list.alignTo(this.wrap, this.listAlign);
					this.list.endUpdate();
                }, this);
		this.list.setHeight(150);
		this.view.setHeight(150);
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
			if (attr._click && !this.singleMode)
				param.checked = false;
			var newNode = new Ext.tree.TreeNode(param);
			newNode._click = attr._click;
			newNode.alertMsg = attr.alertMsg;
			node.appendChild(newNode);
			attr.obj = newNode;
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

	getView : function() {
		return this.view;
	},

	dblClick : function(node, e) {
		if (node.ui.checkbox)
			this.checkChanged(node, node.ui.checkbox.checked);
	},

	checkChanged : function(node, checked) {
		if (checked)
			this.addValue(node.getPath(),node.id, node.text);
		else
			this.removeValue(node.getPath(),node.id, node.text);
		
		this.fireEvent("select", this, node);
	},

	addValue : function(path,id, text) {
		var v = this.getValue();
		vals = v.split(",");
		var found = false;
		for (var i = 0; i < vals.length; i++) {
			if (this.pathMode){
				if (vals[i] == path){
					found = true;
					break;
				}
			} else {
				if (vals[i] == id) {
					found = true;
					break;
				}
			}
		}
		if (!found) {
			if (v.length > 0)
			{
				v = "";
				for (var i = 0; i < vals.length; i++)
					v += vals[i] + "=" + this._text_cache[i] + ",";
			}
			if (this.pathMode)
				v += path + "=" + text;
			else
				v += id + "=" + text;
			this.setValue(v);
		}

	},

	removeValue : function(path,id, text) {
		var vals = this.getValue();
		if ((!this.pathMode && vals == id) || (this.pathMode && vals == path)) {
			this.setValue("");
			return;
		}
		vals = vals.split(",");
		var v = "";
		for (var i = 0; i < vals.length; i++) {
			if ((!this.pathMode && vals[i] == id) || (this.pathMode && vals[i] == path))
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
				if (this.pathMode)
					this.setValue(node.getPath() + "=" + node.text);
				else
					this.setValue(node.id + "=" + node.text);
				this.collapse();
			}
		}
		this.fireEvent("select", this, node);
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
  afterExpand : function(pNode){
    if (!this.singleMode){
			var val = this.getValue();
			if (!val)
				return;
			val = val.split(",");
			for (var i = 0; i < val.length; i++){
				if (val[i].indexOf("/")!=-1) {
					var subVal = val[i].substr(val[i].lastIndexOf("/")+1);
					var cNode = pNode.findChild("id",subVal);
					var path;
					if (this.pathMode)
						path = cNode.getPath();
					else
						path = cNode.id();
					if (path == val[i]){
						if(cNode.ui.checkbox)
							cNode.ui.checkbox.checked = true;
					}
				} else {
					//var nid = cNode.id;
					var cNode = pNode.findChild("id",val[i]);
					//alert(nid+"==="+val[i]);
					if (cNode){
						if(cNode.ui.checkbox)
							cNode.ui.checkbox.checked = true;
					}
				}
			}
		}
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

	beforeload : function(ol, node, cb) {
		ol.baseParams.nodePath = node.getPath();
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
        	this.view.purgeListeners();
            this.view.destroy();
        }
        if(this.list){
            this.list.destroy();
        }
        Ext.form.TreeBox.superclass.onDestroy.call(this);
    },
    
    onDisable : function(){
        Ext.form.TreeBox.superclass.onDisable.call(this);
				this.triggers[0].hide();
    },
    // private
    onEnable : function(){
        Ext.form.TreeBox.superclass.onEnable.call(this);
        this.triggers[0].show();
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

	findValue : function(val, data, sel)
	{
		for (var i = 0; i < data.length; i++)
		{
			var attr = data[i];
			if (attr.id == val)
			{
				if (sel) {
					this.view.getSelectionModel().select(attr.obj);
				}
				return attr.text;
			}
			if (attr.children)
			{
				var s = this.findValue(val, attr.children, sel);
				if (s != null)
					return s;
			}
		}
		return null;
	},

  setValue : function(v) {
  	v = "" + v;
    var text = v;
		if (this.singleMode)
		{
			var pos = text.indexOf("=");
			if (pos != -1) {
				v = text.substr(0, pos);
				text = text.substr(pos + 1);
			} else if (this.treeData) {
				var s = this.findValue(text, this.treeData, true);
				if (s != null)
					text = s;
			}
//			if (!this.pathMode) {
//				v = v.substr(v.lastIndexOf("/")+1);
//			}
		}
		else
		{
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
					if (!this.pathMode){
						vals[i] = vals[i].substr(vals[i].lastIndexOf("/")+1);
					}
					this._text_cache[i] = vals[i];
					v += vals[i];
				} else {
					var vStr = vals[i].substr(0, pos);
					if (!this.pathMode){
						vStr = vStr.substr(vStr.lastIndexOf("/")+1);
					}
					v += vStr;
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
		//this.fireEvent("select", this, this.value);挪至onItemSelect方法里
		if (!this.disabled) {
			if (this.value == "")
				this.triggers[0].hide();
			else if (!this.readOnly) {
				this.triggers[0].show();
			}
		}
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

	recalcSize : function(pNode) {
		//var inner = this.view.getTreeEl().dom;
		//var lw = this.listWidth || Math.max(this.wrap.getWidth(), this.minListWidth);
		//this.list.setWidth(lw);
		//var h = Math.max(inner.clientHeight, inner.offsetHeight, inner.scrollHeight);
		//this.list.beginUpdate();
		//this.list.setHeight(h < this.maxHeight ? 'auto' : this.maxHeight);
		//this.list.setHeight(this.list.getHeight()+this.list.getFrameWidth('tb'));
		//this.list.alignTo(this.wrap, this.listAlign);
		//this.list.endUpdate();
	},

    /**
     * Expands the dropdown list if it is currently hidden. Fires the 'expand' event on completion.
     */
    expand : function(){
      if(this.isExpanded() || !this.hasFocus){
          return;
      }
      this.list.alignTo(this.wrap, this.listAlign);
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
    },
    onTrigger1Click : function(){
    	this.setValue("");
    },

    onTrigger2Click : function(){
       this.onTriggerClick();
    }
});
Ext.reg('treebox', Ext.form.TreeBox);

Ext.form.TreeBoxExtend = Ext.extend(Ext.form.TreeBox, {
	url:'',
	params:{},
	trigger1Class:'x-form-arrow-trigger',
  trigger2Class:'x-form-search2-trigger',
	onTrigger1Click : function(){
  	this.onTriggerClick();
  },

  onTrigger2Click : function(){
  	if (this.getValue() && this.getValue().length>0) {
      var height=480,width=640;
  	  var ua = navigator.userAgent;
		  if(ua.lastIndexOf("MSIE 6.0") != -1){
			  if(ua.lastIndexOf("Windows NT 5.1") != -1){
			    height += 49;
			  }
			  else if(ua.lastIndexOf("Windows NT 5.0") != -1){
			    height += 49;
			  }
			}
  	  var xposition = (screen.width - width) / 2;
		  var yposition = (screen.height - height) / 2;
		  this.params.s_value = this.getValue();
		  var urls = this.url+"?"+Ext.urlEncode(this.params);
  		window.showModalDialog(urls,'','dialogWidth='+width+'px;dialogHeight='+height+'px;dialogLeft='+xposition+'px;dialogTop='+yposition+'px;center=yes;status=no;help=no;resizable=no;scroll=no');
		}
  }
});
Ext.reg('treeboxext', Ext.form.TreeBoxExtend);


Ext.AttachmentsPanel = Ext.extend(Ext.form.FormPanel,  {
	 onRender : function(ct, position){
      Ext.AttachmentsPanel.superclass.onRender.call(this, ct, position);
//<table width='100%' height='100%'><tbody><tr><td width='50'><div class='x-panel-tbar'>附件</div></td><td>s</td></tr></tbody></table>
      /*this.el = ct.createChild({
                id: this.id,
                cls: 'x-panel'
            }, position);
      var el = document.createElement('div');
      el.className = 'x-panel-bwrap';
      this.bwrap = Ext.get(this.el.appendChild(el));
*/
			var tbdiv = this.body.createChild({tag:'div',style:'border-bottom:1px solid #99bbe8;'}, this.body.dom.firstChild
      );
      var tb = tbdiv.createChild({cls:'x-panel-btns-ct', cn: {
          html:'<table cellspacing="0" cellPadding="0" border="0"><tbody><tr></tr></tbody></table><div class="x-clear"></div>'
      	}}, null, true
      );

     	var tr = tb.getElementsByTagName('tr')[0];
     	var b;
     	if (this.uploadButton)
     		b = new Ext.Button(this.uploadButton);
     	else{
     		var args = {
     			text:'增加附件',
     			owner:this,
     			handler:function(){
	     			if (!this.owner.attachments)
							this.owner.attachments = new Array();
						uploadFile(this.owner.uploadCallback, this.owner);
	     		}
	     	};
     		b = new Ext.Button(args);
	    }
      var td = document.createElement('td');
      td.className = 'x-panel-btn-td';
      b.render(tr.appendChild(td));

      td = document.createElement('td');
      this.attachmentRegion = Ext.get(tr.appendChild(td));

      if (this.attachments){
      	if (Ext.isArray(this.attachments)){
					for (var i = 0; i < this.attachments.length; i++){
						this.addAttachment(this.attachments[i],false);
					}
				} else {
					this.setAttachment(this.attachments);
				}
      }
    },

    uploadCallback : function(obj, t) {
			var t0 = obj.getFileName(t);
			obj.addAttachment({href:t,text:t0},true);
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

		setAttachment:function(attrs){

			var attachs_ = attrs.split(";");
			this.attachments = new Array();
			this.attachmentRegion.dom.innerHTML = "";
			for (var i = 0; i < attachs_.length; i++){
				if (attachs_[i] == null || attachs_[i] =="")
					continue;
				var fn = this.getFileName(attachs_[i]);
				this.addAttachment({href:attachs_[i],text:fn},true);
			}
		},

    addAttachment:function(att,addTo){
    	this.attachmentRegion.createChild({tag:'a',html:"<a href="+ITSM_HOME+"/download.jsp?fileName="+att.href+">"+att.text+"</a>&nbsp;"}, null, true);

    	if (addTo){
    		if (!this.attachments)
    			this.attachments = new Array();
    		this.attachments[this.attachments.length] = att;
    	}
    },

    getAttachment:function(){
    	var retStr = "";
    	if (this.attachments){
	    	for (var i = 0; i < this.attachments.length; i++){
	    		if(i>0)
	    			retStr += ";";
	    		retStr += this.attachments[i].href;
	    	}
	    }
    	return retStr;
    }
});



Ext.form.IdField = Ext.extend(Ext.form.TriggerField, {
	defaultAutoCreate : {tag: "input", type: "text", size: "24", autocomplete: "off"},
	triggerClass : 'x-form-arrow-trigger',
	prefix:'',
	autoId:'id',
	getAutoId: function(obj, val) { return val; },
	getIdValue: function(obj, val) { return obj.prefix + val; },
    initComponent : function(){
        Ext.form.IdField.superclass.initComponent.call(this);
	},

    // private
    onRender : function(ct, position){
        Ext.form.IdField.superclass.onRender.call(this, ct, position);

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
        var simple1 = new Ext.form.FormPanel({
			method: 'POST',
			baseParams: {id: postId},
			errorReader: new Ext.form.XmlErrorReader(),
			items: [{
					xtype: 'hidden',
					name: '_temp_hidden_field'
				}],
			url:ITSM_HOME+'/getId.jsp'
		});
		var e0 = document.createElement("div");
		simple1.form._obj = this;
		simple1.form._el = e0;
		simple1.render(simple1.form._el);
		simple1.form.submit({
			waitMsg: '正在生成编号...',
			success: function(form, action)
			{
				form._obj.setValue(form._obj.getIdValue(form._obj, form.errorReader.xmlData.documentElement.text));
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

Ext.loadRemoteScript = function(url, params, cb, cbparam) {
	var elem = document.createElement("div");
	document.body.appendChild(elem);
	var cp = new Ext.Panel({el: elem, border: false, hidden: true });
	Ext.getBody().mask("loading", 'x-mask-loading');
	cp.render();
	if (cb) {
		cp._cb = cb;
		cp._cbparam = cbparam;
	}
	cp.load({
		'url': url,
		'params': params,
//		callback: function() { this.destroy(true); Ext.getBody().unmask(); },
		callback: function() { Ext.getBody().unmask(); if (this._cb) this._cb(this._cbparam); },
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
 * Ext JS Library 2.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
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

Ext.form.URLField = Ext.extend(Ext.form.TriggerField, {
		defaultAutoCreate : {tag: "a",cls:"textOverflow", target: "_blank"},
		// private
		triggerClass:'x-form-link-trigger',
    initValue : function(){

    },

		// private
	  onRender : function(ct, position){
	      Ext.form.URLField.superclass.onRender.call(this, ct, position);

				this.el.dom.name = "";
				this.urlElField = this.wrap.createChild({tag:'input', type:'hidden', name: this.name});
				if(this.value){
					this.value = (this.value === undefined || this.value === null) ? '': this.value;
					this.linkURL = (this.linkURL === undefined || this.linkURL === null) ? '': this.linkURL;
          this.setValue(this.value+"|_|"+this.linkURL);
        }
	      if (this.readOnly && this.readOnly==true){
	      	this.trigger.setDisplayed(false);
	      }

	  },
	  
	  validate : function(){
	  	return true;
	  },

	  setValue : function(v) {
	  	//Ext.form.URLField.superclass.setValue.apply(this, arguments);
	  	if(v){
	  		var vs = v.split("|_|");
	  		this.el.dom.innerHTML = vs[0];
    		this.el.dom.title = vs[0];
    		this.value = vs[0];
    		if (vs.length > 1){
    			this.el.dom.href = vs[1];
    			this.linkURL = vs[1];
    		}
    		this.urlElField.dom.value= v;
	  	}
	  },
	  getLinkURL : function(){
	  	return this.linkURL;
	  },
	  getValue : function(){
	  	return this.value+"|_|"+this.linkURL;
	  },

	  updateValue : function (obj,displayName,urlValue){
	  	obj.setValue(displayName+"|_|"+urlValue);
	  },
	  onTriggerClick : function(){
        var form = new Ext.form.FormPanel({
					baseCls: 'x-plain',
					method: 'POST',
					fileUpload: true,
					defaultType: 'textfield',
					errorReader: new Ext.form.XmlErrorReader(),
					labelWidth: 75, // label settings here cascade unless overridden
					labelAlign: 'left'
				});

	    	form.add(
	        new Ext.form.TextField({
	            fieldLabel: '显示名称',
	            name: 'displayName',
	            width:175,
	            allowBlank:true,
	            value:this.value
	        }),
	        new Ext.form.TextField({
	            fieldLabel: '链接地址',
	            name: 'urlValue',
	            width:175,
	            allowBlank:false,
	            value:this.linkURL
	        })
				);
			var window_url = null;
			if (window_url)
				window_url.close();
	    window_url = new Ext.Window({
        title: '超链接',
				width:300,
				height:150,
        layout: 'fit',
        plain:true,
        bodyStyle:'padding:5px;',
        buttonAlign:'center',
        items: form,

        buttons: [{
          text: '确定',
          handler: function() {
						var submitForm = window_url._form.form;
						if (!submitForm.isValid()) {
							Ext.MessageBox.alert("提示", "请填写链接地址");
							return;
						}
						window_url.setValue(window_url.obj,submitForm.findField("displayName").getValue(),submitForm.findField("urlValue").getValue());
          	window_url.close();
				   	window_url = null;
          }
        },{
          text: '取消',
          handler: function() {
	          window_url.close();
				   	window_url = null;
	        }
      	}]
    	});
			window_url._form = form;
			window_url.setValue = this.updateValue;
			window_url.obj = this;
	    window_url.show();
    }
});

//带frame的tabPanel
Ext.ux.IFrameComponent = Ext.extend(Ext.BoxComponent, {
		//updateManager:new Ext.Updater(this),
		onRender : function(ct, position){
		    this.el = ct.createChild({tag: 'iframe', id: 'iframe-'+ this.id, frameBorder: 0, src: this.url});
		},
		onDestroy : function(){
			this.el.dom.suc = "javascript:false";
		  Ext.ux.IFrameComponent.superclass.onDestroy.call(this);
		},
		setTitle :function(newTitle) {
			this.title = newTitle;
		},
		isCloseable :function() {
			return this.closable;
		},
		load:function(url){
			if (url)
				this.el.dom.src=url;
			else
				this.el.dom.src=this.url;
		},
    getIframeComponent: function() {
     	return this.el;
    }
});

//增加GridPanel方法
Ext.override(Ext.grid.GridPanel, {
		setValue:function(obj){
			this.store.loadData(obj,false);
		},
		getValue:function(){
			var n = this.store.getCount();
			var ds = new Array();
			for (var i = 0; i < n; i++)
				ds[i] = this.store.getAt(i).data
			return Ext.util.JSON.encode(ds);
		},
		appendValue:function(obj){
			this.store.loadData(obj,true);
		}
});

/**
 * @author Robert Williams (vtswingkid)
 * @version 1.0.4
 */
Ext.namespace('Ext.ux');
Ext.ux.RadioGroup = Ext.extend(Ext.form.Field,  {
    /**
     * @cfg {String} focusClass The CSS class to use when the checkbox receives focus (defaults to undefined)
     */
    focusClass : undefined,
    /**
     * @cfg {String} fieldClass The default CSS class for the checkbox (defaults to "x-form-field")
     */
    fieldClass: "x-form-field",
    /**
     * @cfg {Boolean} checked True if the the checkbox should render already checked (defaults to false)
     */
    checked: false,
    /**
     * @cfg {String/Object} autoCreate A DomHelper element spec, or true for a default element spec (defaults to
     * {tag: "input", type: "radio", autocomplete: "off"})
     */
    defaultAutoCreate : { tag: "input", type: 'radio', autocomplete: "off"},
    /**
     * @cfg {String} boxLabel The text that appears beside the checkbox
     */

	getId:function(){
		//if multiple radios are defined use this information
		if(this.radios && this.radios instanceof Array){
			if(this.radios.length){
				var r=this.radios[0];
				this.value=r.value;
				this.boxLabel=r.boxLabel;
				this.checked=r.checked || false;
				this.readOnly=r.readOnly || false;
				this.disabled=r.disabled || false;
				this.tabIndex=r.tabIndex;
				this.cls=r.cls;
				this.listeners=r.listeners;
				this.style=r.style;
				this.bodyStyle=r.bodyStyle;
				this.hideParent=r.hideParent;
				this.hidden=r.hidden;
			}
		}
		Ext.ux.RadioGroup.superclass.getId.call(this);
	},

	// private
    initComponent : function(){
        Ext.ux.RadioGroup.superclass.initComponent.call(this);
        this.addEvents(
            /**
             * @event change
             * Fires when the radio value changes.
             * @param {Ext.vx.RadioGroup} this This radio
             * @param {Boolean} checked The new checked value
             */
            'check'
        );
    },

    // private
    onResize : function(){
        Ext.ux.RadioGroup.superclass.onResize.apply(this, arguments);
        if(!this.boxLabel){
            this.el.alignTo(this.wrap, 'c-c');
        }
    },

    // private
    initEvents : function(){
        Ext.ux.RadioGroup.superclass.initEvents.call(this);
        this.el.on("click", this.onClick,  this);
        this.el.on("change", this.onClick,  this);
    },

	// private
    getResizeEl : function(){
        return this.wrap;
    },

    // private
    getPositionEl : function(){
        return this.wrap;
    },

    /**
     * Overridden and disabled. The editor element does not support standard valid/invalid marking. @hide
     * @method
     */
    markInvalid : Ext.emptyFn,
    /**
     * Overridden and disabled. The editor element does not support standard valid/invalid marking. @hide
     * @method
     */
    clearInvalid : Ext.emptyFn,

    // private
    onRender : function(ct, position){
        Ext.ux.RadioGroup.superclass.onRender.call(this, ct, position);
        this.wrap = this.el.wrap({cls: "x-form-check-wrap"});
        if(this.boxLabel){
            this.wrap.createChild({tag: 'label', htmlFor: this.el.id, cls: 'x-form-cb-label', html: this.boxLabel});
        }
		if(!this.isInGroup){
			this.wrap.applyStyles({'padding-top':'2px'});
		}
        if(this.checked){
            this.setChecked(true);
        }else{
            this.checked = this.el.dom.checked;
        }
		if (this.radios && this.radios instanceof Array) {
			this.els=new Array();
			this.els[0]=this.el;
			for(var i=1;i<this.radios.length;i++){
				var r=this.radios[i];
				this.els[i]=new Ext.ux.RadioGroup({
					renderTo:this.wrap,
					hideLabel:true,
					boxLabel:r.boxLabel,
					checked:r.checked || false,
					value:r.value,
					name:this.name || this.id,
					readOnly:r.readOnly || false,
					disabled:r.disabled || false,
					tabIndex:r.tabIndex,
					cls:r.cls,
					listeners:r.listeners,
					style:r.style,
					bodyStyle:r.bodyStyle,
					hideParent:r.hideParent,
					hidden:r.hidden,
					isInGroup:true
				});
				if (this.horizontal) {
					this.els[i].el.up('div.x-form-check-wrap').applyStyles({
						'display': 'inline',
						'padding-left': '5px'
					});
				}
			}
			if(this.hidden)this.hide();
		}
    },

    initValue : function(){
        if(this.value !== undefined){
            this.el.dom.value=this.value;
        }else if(this.el.dom.value.length > 0){
            this.value=this.el.dom.value;
        }
    },

    // private
    onDestroy : function(){
		if (this.radios && this.radios instanceof Array) {
			var cnt = this.radios.length;
			for(var x=1;x<cnt;x++){
				this.els[x].destroy();
			}
		}
        if(this.wrap){
            this.wrap.remove();
        }
        Ext.ux.RadioGroup.superclass.onDestroy.call(this);
    },

	setChecked:function(v){
        if(this.el && this.el.dom){
			var fire = false;
			if(v != this.checked)fire=true;
			this.checked=v;
            this.el.dom.checked = this.checked;
            this.el.dom.defaultChecked = this.checked;
    	    if(fire)this.fireEvent("check", this, this.checked);
	    }
    },
    /**
     * Returns the value of the checked radio.
     * @return {Mixed} value
     */
    getValue : function(){
        if(!this.rendered) {
            return this.value;
        }
        var p=this.el.up('form');//restrict to the form if it is in a form
		if(!p)p=Ext.getBody();
		var c=p.child('input[name='+escape(this.el.dom.name)+']:checked', true);
		return (c)?c.value:this.value;
    },

	// private
    onClick : function(){
        if(this.el.dom.checked != this.checked){
			var p = this.el.up('form');
			if (!p)
				p = Ext.getBody();
			var els = p.select('input[name=' + escape(this.el.dom.name) + ']');
			els.each(function(el){
				if (el.dom.id == this.id) {
					this.setChecked(true);
				}
				else {
					var e = Ext.getCmp(el.dom.id);
					e.setChecked.apply(e, [false]);
				}
			}, this);
        }
    },

    /**
     * Checks the radio box with the matching value
     * @param {Mixed} v
     */

    setValue : function(v){
        if(!this.rendered) {
            this.value=v;
            return;
        }
        var p=this.el.up('form');//restrict to the form if it is in a form
        if(!p)p=Ext.getBody();
        var target = p.child('input[name=' + escape(this.el.dom.name) + '][value=' + v + ']', true);
        if (target) target.checked = true;
    },

	clear: function(){
		if (!this.rendered) return;
		var p = this.el.up('form');//restrict to the form if it is in a form
		if (!p) p = Ext.getBody();
		var c = p.child('input[name=' + escape(this.el.dom.name) + ']:checked', true);
		if (c) c.checked = false;
	},

	disable: function(){
		if (!this.rendered) return;
		var p = this.el.up('form');//restrict to the form if it is in a form
		if (!p) p = Ext.getBody();
		var els = p.select('input[name=' + escape(this.el.dom.name) + ']');
		els.each(function(el){
			if (el.dom.id == this.id) {
				Ext.ux.RadioGroup.superclass.disable.call(this);
			}
			else {
				var e = Ext.getCmp(el.dom.id);
				Ext.ux.RadioGroup.superclass.disable.call(e);
			}
		}, this);
	},

	enable: function(){
		if (!this.rendered) return;
		var p = this.el.up('form');//restrict to the form if it is in a form
		if (!p) p = Ext.getBody();
		var els = p.select('input[name=' + escape(this.el.dom.name) + ']');
		els.each(function(el){
			if (el.dom.id == this.id) {
				Ext.ux.RadioGroup.superclass.enable.call(this);
			}
			else {
				var e = Ext.getCmp(el.dom.id);
				Ext.ux.RadioGroup.superclass.enable.call(e);
			}
		}, this);
	},

	hide: function(){
		if (!this.rendered) return;
		this.wrap.hide();
		this.wrap.parent().parent().hide();
	},

	show: function(){
		if (!this.rendered) return;
		this.wrap.show();
		this.wrap.parent().parent().show();
	}
});
Ext.reg('ux-radiogroup', Ext.ux.RadioGroup);



Ext.form.SelectDialogField = Ext.extend(Ext.form.ComboBox, {
	trigger1Class:'x-form-clear-trigger',
	trigger2Class:'x-form-adv-trigger',
	
	queryParam:'id',
	minChars:2,
	/**
		弹出的目标页面
	**/
	selectUrl : '',
	typeAheadUrl:'',//敲开头自动搜索地址页面
	params:{},
	dialogHeight:480,
	dialogWidth:640,
	
	valueField: 'id',
	displayField:'name',
	forceSelection:false,
	/**
		当前值传入目标页面时，所使用的变量
	**/
	valueParam:'s_value',
	hideTriggers:[true,true],
	initComponent : function(){
		Ext.form.SelectDialogField.superclass.initComponent.call(this);
		this.triggerConfig = {
            tag:'span', cls:'x-form-twin-triggers', cn:[
            {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger1Class},
            {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger2Class}
        ]};
    this.hiddenName = this.hiddenName === undefined ? this.name : this.hiddenName;
    if (!this.store) {
	    this.store = new Ext.data.Store({
		    proxy: new Ext.data.HttpProxy({
		        url: this.typeAheadUrl
		    }),
				baseParams:this.params,
		    reader: new Ext.data.JsonReader({
		        root: 'records',
		        totalProperty: 'totalCount'
		    }, [
		    		{name: this.valueField, mapping: this.valueField},
		    		{name: this.displayField, mapping: this.displayField}
		    ])
		  });
		}
		
		if(this.typeAheadUrl && this.typeAheadUrl!='')
			this.typeAhead = true;
	},
	
	getTrigger : function(index){
      return this.triggers[index];
  },

  initTrigger : function(){
    var ts = this.trigger.select('.x-form-trigger', true);
    this.wrap.setStyle('overflow', 'hidden');
    var triggerField = this;
    ts.each(function(t, all, index){
        t.hide = function(){
            var w = triggerField.wrap.getWidth();
            this.dom.style.display = 'none';
            triggerField.el.setWidth(w-triggerField.trigger.getWidth());
        };
        t.show = function(){
            var w = triggerField.wrap.getWidth();
            this.dom.style.display = '';
            triggerField.el.setWidth(w-triggerField.trigger.getWidth());
        };
        var triggerIndex = 'Trigger'+(index+1);

        if(this['hide'+triggerIndex]){
            t.dom.style.display = 'none';
        }
        t.on("click", this['on'+triggerIndex+'Click'], this, {preventDefault:true});
        t.addClassOnOver('x-form-trigger-over');
        t.addClassOnClick('x-form-trigger-click');
    }, this);
    this.triggers = ts.elements;
  },
	// private
    onRender : function(ct, position){
        Ext.form.SelectDialogField.superclass.onRender.call(this, ct, position);
	      if (this.readOnly) {
			  	for(var index = 0; index < this.triggers.length; index++){
			    	if (this.hideTriggers[index] == true)
		    			this.triggers[index].setDisplayed(false);
		    		else
		    			this.triggers[index].setDisplayed(true);
			    }
			  }
    },
    setEditable : function(value){
      if(value == this.editable){
          return;
      }
      this.editable = value;
      if(!value){
          this.el.dom.setAttribute('readOnly', true);
          this.el.on('mousedown', this.onTriggerClick,  this);
          this.el.addClass('x-combo-noedit');
      }else{
          this.el.dom.setAttribute('readOnly', false);
          this.el.un('mousedown', this.onTriggerClick,  this);
          this.el.removeClass('x-combo-noedit');
      }
    },
    
    /**
     * Returns the currently selected field value or empty string if no value is set.
     * @return {String} value The selected value
     */
		getValue : function(){
			if(this.hiddenField && this.hiddenField.value && this.hiddenField.value!='') {
				return this.hiddenField.value;
			}
			return Ext.form.ComboBox.superclass.getValue.call(this);
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
        //this.applyEmptyText();
    },
    
  setValue : function(v) {
  	var val = "";
    var text = "";
    this.valueStr = [];
		this.nameStr = [];

		var vals = v.split(",");
		for (var i = 0; i < vals.length; i++) {
			if (i > 0) {
				text += ",";
				val += ",";
			}
			var pos = vals[i].indexOf("=");
			if (pos == -1) {
				this.valueStr[i] = vals[i];
				this.nameStr[i] = vals[i];
				val += vals[i];
				text += vals[i];
			} else {
				var vStr = vals[i].substr(0, pos);
				val += vStr;
	
				text += vals[i].substr(pos + 1);
				this.valueStr[i] = vStr;
				this.nameStr[i] = vals[i].substr(pos + 1);
			}
		}
	  if(this.hiddenField){
       this.hiddenField.value = val;
    }
		if (v == "")
			this.triggers[0].hide();
		else {
			if (!this.readOnly)
				this.triggers[0].show();
		}
		Ext.form.ComboBox.superclass.setValue.call(this, text);
		this.value = val;
    //Ext.form.SelectDialogField.superclass.setValue.call(this, text);
  },
 
  doForce : function(){
      if(this.el.dom.value.length > 0){
          if (this.hiddenField.value=='')
          	this.setValue("");
      }
  },
  onSelect : function(record, index){
      if(this.fireEvent('beforeselect', this, record, index) !== false){
          this.setValue(record.data[this.valueField]+"="+ record.data[this.displayField]);
          this.collapse();
          this.fireEvent('select', this, record, index);
      }
  },
  
  onTrigger1Click : function(){
  	this.setValue("");
  },

  onTrigger2Click : function(){
       	  var height=this.dialogHeight,width=this.dialogWidth;
	  var ua = navigator.userAgent;
	  if(ua.lastIndexOf("MSIE 6.0") != -1){
		  if(ua.lastIndexOf("Windows NT 5.1") != -1){
		    height += 49;
		  }
		  else if(ua.lastIndexOf("Windows NT 5.0") != -1){
		    height += 49;
		  }
		}
	  var xposition = (screen.width - width) / 2;
	  var yposition = (screen.height - height) / 2;
	  var vp = this.valueParam;
	  this.params[vp] = this.getValue();
	  var urls = this.selectUrl+"?"+Ext.urlEncode(this.params);
		var rv = window.showModalDialog(urls,'','dialogWidth='+width+'px;dialogHeight='+height+'px;dialogLeft='+xposition+'px;dialogTop='+yposition+'px;center=yes;status=no;help=no;resizable=no;scroll=no');
		if (rv)
			this.setValue(rv);
  }
});
Ext.reg('selDlgfield', Ext.form.SelectDialogField);

Ext.form.CIField = Ext.extend(Ext.form.SelectDialogField, {
	editable:false,
	hideTriggers:[true,true],
  initComponent : function(){
      Ext.form.CIField.superclass.initComponent.call(this);

      this.triggerConfig = {
          tag:'span', cls:'x-form-twin-triggers', cn:[
          {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger1Class},
          {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger2Class,title: "选择"}
      ]};
  },
  onTriggerClick : function() {
  	this.onTrigger3Click();
  },
  onTrigger2Click : function(){
  	  var height=480,width=640;
  	  var ua = navigator.userAgent;
		  if(ua.lastIndexOf("MSIE 6.0") != -1){
			  if(ua.lastIndexOf("Windows NT 5.1") != -1){
			    height += 49;
			  }
			  else if(ua.lastIndexOf("Windows NT 5.0") != -1){
			    height += 49;
			  }
			}
  	  var xposition = (screen.width - width) / 2;
		  var yposition = (screen.height - height) / 2;
		  var urls = (this.selectUrl?this.selectUrl:"/cmdb")+"/ciSearchDialog.jsp?oid=" + this.getValue();
		  if (this.cicategory)
			  urls = (this.selectUrl?this.selectUrl:"/cmdb")+"/ciSearchList.jsp?id=" + this.cicategory + "&oid=" + this.getValue();

  		var rv = window.showModalDialog(urls,'','dialogWidth='+width+'px;dialogHeight='+height+'px;dialogLeft='+xposition+'px;dialogTop='+yposition+'px;center=yes;status=no;help=no;resizable=no;scroll=no');
			if (rv)
				this.setValue(rv);
  },
  onTrigger3Click : function(){

  	if (this.value && this.value.length>0) {
  		var height=500,width=700;
  	  var xposition = (screen.width - width) / 2;
		  var yposition = (screen.height - height) / 2;
		  var urls = (this.selectUrl?this.selectUrl:"/cmdb")+"/ciView.jsp?oid=" + this.getValue();
			window.showModalDialog(urls,'','dialogWidth='+width+'px;dialogHeight='+height+'px;dialogLeft='+xposition+'px;dialogTop='+yposition+'px;center=yes;status=no;help=no;resizable=no;scroll=no');
  	}
  }
});

Ext.reg('cifield', Ext.form.CIField);


Ext.override(Ext.Panel, {
    setIcon : function(icon){
		if(this.rendered && this.header) {
			var hd = this.header.dom;
			var img = hd.firstChild && String(hd.firstChild.tagName).toLowerCase() == 'img' ? hd.firstChild : null;
			if (img){
				img.src = icon;
			} else {
				Ext.DomHelper.insertBefore(hd.firstChild, {
					tag:'img', src: icon, cls:'x-panel-inline-icon'
				});
			 }
		}

    }
});


Ext.override(Ext.form.Field, {
   setReadOnly : function(r){
		if(r == true) {
			this.readOnly = true;
			if(this.rendered)
				this.el.dom.readOnly = true;
    } else if (r == false) {
    	this.readOnly = false;
    	if(this.rendered)
    		this.el.dom.readOnly = false;
    }
  }
});

Ext.override(Ext.form.TriggerField, {
	initComponent:function() {
		// call parent initComponent
		Ext.form.TriggerField.superclass.initComponent.call(this);
		if (this.readOnly)
			this.hideTrigger = true;
	},
  setReadOnly : function(r){
  	Ext.form.TriggerField.superclass.setReadOnly.call(this,r);
		if(r) {
			this.hideTrigger = true;
			if(this.rendered)
				this.trigger.setDisplayed(false);
    } else {
    	this.hideTrigger = false;
    	if(this.rendered)
  			this.trigger.setDisplayed(true);
  	}
  	if(this.rendered){
  		this.el.setWidth(this.wrap.getWidth()-this.trigger.getWidth());
  		if (r)
	  		this.el.un('mousedown', this.onTriggerClick,  this);
	  	else
	  		this.el.on('mousedown', this.onTriggerClick,  this);
	  }

  },
  afterRender : function(){
  	Ext.form.TriggerField.superclass.afterRender.call(this);
  	if (this.readOnly)
  		this.el.un('mousedown', this.onTriggerClick,  this);
  },
  onDisable : function(){
    Ext.form.TriggerField.superclass.onDisable.call(this);
    if(this.wrap){
        this.wrap.addClass(this.disabledClass);
        this.el.removeClass(this.disabledClass);
    }
    if(this.rendered)
      this.trigger.setDisplayed(false);
  },
  onEnable : function(){
    Ext.form.TriggerField.superclass.onEnable.call(this);
    if(this.wrap){
        this.wrap.removeClass(this.disabledClass);
    }
    if(this.rendered)
      this.trigger.setDisplayed(true);
  }
});

Ext.override(Ext.form.TwinTriggerField, {
	hideTriggers:[false,false],
  setReadOnly : function(r){
  	Ext.form.TriggerField.superclass.setReadOnly.call(this,r);
  	var hideTriggers = this.hideTriggers;
  	if (this.rendered){
	    for(var index = 0; index < this.triggers.length; index++){
	    	if (hideTriggers[index] == true){
	    		if (r == true)
	    			this.triggers[index].hide();
	    		else if (r == false)
	    			this.triggers[index].show();
	    	}
	    }
	  }
  },
  onRender : function(ct, position){
  	Ext.form.TwinTriggerField.superclass.onRender.call(this, ct, position);
  	if (this.readOnly) {
	  	for(var index = 0; index < this.triggers.length; index++){
	    	if (this.hideTriggers[index] == true)
    			this.triggers[index].setDisplayed(false);
    		else
    			this.triggers[index].setDisplayed(true);
	    }
	  }
  		
  },
  onDisable : function(){
    Ext.form.TwinTriggerField.superclass.onDisable.call(this);
    if(this.wrap){
        this.wrap.addClass(this.disabledClass);
        this.el.removeClass(this.disabledClass);
    }
    var hideTriggers = this.hideTriggers;
    if (this.rendered){
	    for(var index = 0; index < this.triggers.length; index++){
	    	if (hideTriggers[index] == true){
	    			this.triggers[index].hide();
	    	}
	    }
	  }
  },
  onEnable : function(){
    Ext.form.TwinTriggerField.superclass.onEnable.call(this);
    if(this.wrap){
        this.wrap.removeClass(this.disabledClass);
    }
    
    var hideTriggers = this.hideTriggers;
    if(this.rendered) {
      for(var index = 0; index < this.triggers.length; index++){
	    	if (hideTriggers[index] == true){
	    			this.triggers[index].show();
	    	}
	    }
    }
  }
});

Ext.override(Ext.form.ComboBox, {
  doForce : function(){
        if(this.el.dom.value.length > 0){
            this.el.dom.value =
                this.lastSelectionText === undefined ? '' : this.lastSelectionText;
            this.applyEmptyText();
        } else {
        	this.clearValue();
        }
    },
    postBlur : function(){
       
    }
});

Ext.override(Ext.layout.FormLayout, {
  fieldTpl:new Ext.Template(
      '<div class="x-form-item {5}" tabIndex="-1">',
          '<label for="{0}" style="{2}" desc="{7}" onmouseover="'+
          'if (this.scrollWidth>this.style.pixelWidth || this.desc!=\'\') {'+
          'var t = document.getElementById(\'x-form-el-title-note\');'+
          ' theEvent = window.event || event; t.style.display=\'\';'+
          ' var p = this.parentElement;while(true){if (p.tagName==\'FORM\'){break;}p = p.parentElement;}'+
          't.style.left=(theEvent.clientX+p.scrollLeft)+\'px\';'+
          't.style.top=(theEvent.clientY+p.scrollTop)+\'px\';'+
          'if(this.desc==\'\')t.innerHTML=\'<b>{1}</b>\'; else t.innerHTML=\'<b>{1}:</b><br><i>\'+this.desc+\'</i>\'; '+
          'var w = (t.clientWidth||t.offsetWidth);'+
         	't.style.width = w>200?200:(w+5);}" '+
          'onmouseout="document.getElementById(\'x-form-el-title-note\').style.display=\'none\';" class="x-form-item-label">{1}{4}</label>',
          '<div class="x-form-element" id="x-form-el-{0}" style="{3}">',
          '</div><div class="{6}"></div>',
      '</div>'
  ),
  // private
  renderItem : function(c, position, target){
      if(c && !c.rendered && c.isFormField && c.inputType != 'hidden'){
         var args = [
                 c.id, c.fieldLabel,
                 c.labelStyle||this.labelStyle||'',
                 this.elementStyle||'',
                 typeof c.labelSeparator == 'undefined' ? this.labelSeparator : c.labelSeparator,
                 (c.itemCls||this.container.itemCls||'') + (c.hideLabel ? ' x-hide-label' : ''),
                 c.clearCls || 'x-form-clear-left',
                 (typeof c.desc=='undefined') ? "" : c.desc//字段描述
          ];

          if(typeof position == 'number'){
              position = target.dom.childNodes[position] || null;
          }
          if(position){
              this.fieldTpl.insertBefore(position, args);
          }else{
              this.fieldTpl.append(target, args);
          }
          c.render('x-form-el-'+c.id);
      }else {
          Ext.layout.FormLayout.superclass.renderItem.apply(this, arguments);
      }
  }
});


Ext.override(Ext.form.FormPanel, {
	titleTipEnable:false,
	 // private
    onRender : function(ct, position){
        this.initFields();
				
        Ext.FormPanel.superclass.onRender.call(this, ct, position);
        this.form.initEl(this.body);
        var alertNote = this.body.createChild({tag:'div',id:'x-form-el-title-note',style:"display:none;position:absolute;border:1px #99bbe8 solid;background-color: #ffffff;font-size:9pt;padding:2px;z-index:99999;"});
    }

});

// 从ext2.2的示例复制
Ext.grid.CheckColumn = function(config){
    Ext.apply(this, config);
    if(!this.id){
        this.id = Ext.id();
    }
    this.renderer = this.renderer.createDelegate(this);
};

Ext.grid.CheckColumn.prototype ={
    init : function(grid){
        this.grid = grid;
        this.grid.on('render', function(){
            var view = this.grid.getView();
            view.mainBody.on('mousedown', this.onMouseDown, this);
        }, this);
    },

    onMouseDown : function(e, t){
        if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1){
            e.stopEvent();
            var index = this.grid.getView().findRowIndex(t);
            var record = this.grid.store.getAt(index);
            record.set(this.dataIndex, !record.data[this.dataIndex]);
        }
    },

    renderer : function(v, p, record){
        p.css += ' x-grid3-check-col-td'; 
        return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
    }
};

Ext.LoadMask.prototype.msg = '正在加载数据，请稍候...';

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
});

//重载 ext中的applySort，使得字符串按字母的顺序排序
Ext.data.Store.prototype.sortData=function(f, direction){
  direction = direction || 'ASC';
  var st = this.fields.get(f).sortType;
  var fn = function(r1, r2){
    var v1 = st(r1.data[f]), v2 = st(r2.data[f]);
		if(typeof(v1) == "string"){ //若为字符串，
			return v1.localeCompare(v2);//则用本地特定的顺序来比较汉字字符串, Firefox 与 IE 均支持
		}
    return v1 > v2 ? 1 : (v1 < v2 ? -1 : 0);
  };
  this.data.sort(direction, fn);
  if(this.snapshot && this.snapshot != this.data){
      this.snapshot.sort(direction, fn);
  }
};


Ext.ns('Ext.ux.form');
Ext.ux.form.DateTime = Ext.extend(Ext.form.Field, {
	/**
	 * @cfg {String/Object} defaultAutoCreate DomHelper element spec
	 * Let superclass to create hidden field instead of textbox. Hidden will be submittend to server
	 */
	 defaultAutoCreate:{tag:'input', type:'hidden'}
	/**
	 * @cfg {Number} timeWidth Width of time field in pixels (defaults to 100)
	 */
	,timeWidth:80
	,dateWidth:100
	/**
	 * @cfg {String} dtSeparator Date - Time separator. Used to split date and time (defaults to ' ' (space))
	 */
	,dtSeparator:' '
	/**
	 * @cfg {String} hiddenFormat Format of datetime used to store value in hidden field
	 * and submitted to server (defaults to 'Y-m-d H:i:s' that is mysql format)
	 */
	,hiddenFormat:'Y-m-d H:i:s'
	/**
	 * @cfg {Boolean} otherToNow Set other field to now() if not explicly filled in (defaults to true)
	 */
	,otherToNow:true
	/**
	 * @cfg {Boolean} emptyToNow Set field value to now on attempt to set empty value.
	 * If it is true then setValue() sets value of field to current date and time (defaults to false)
	 */
	/**
	 * @cfg {String} timePosition Where the time field should be rendered. 'right' is suitable for forms
	 * and 'below' is suitable if the field is used as the grid editor (defaults to 'right')
	 */
	,timePosition:'right' // valid values:'below', 'right'
	/**
	 * @cfg {String} dateFormat Format of DateField. Can be localized. (defaults to 'm/y/d')
	 */
	,dateFormat:'Y-m-d'
	/**
	 * @cfg {String} timeFormat Format of TimeField. Can be localized. (defaults to 'g:i A')
	 */
	,timeFormat:'H:i'
	/**
	 * @cfg {Object} dateConfig Config for DateField constructor.
	 */
	/**
	 * @cfg {Object} timeConfig Config for TimeField constructor.
	 */
	,allowBlank:true
	/**
	 * @private
	 * creates DateField and TimeField and installs the necessary event handlers
	 */
	,initComponent:function() {
		
		// call parent initComponent
		
		Ext.ux.form.DateTime.superclass.initComponent.call(this);
		
		// create DateField
		
		var dateConfig = Ext.apply({}, {
			id : this.id + '-date',
			format : this.dateFormat || Ext.form.DateField.prototype.format,
			width : this.dateWidth,
			readOnly:this.readOnly,
			allowBlank:this.allowBlank,
			selectOnFocus : this.selectOnFocus,
			listeners : {
				blur : {scope : this, fn : this.onBlur},
				focus : {scope : this, fn : this.onFocus}
			}
		},this.dateConfig);
		
		this.df = new Ext.form.DateField(dateConfig);
		
		this.df.ownerCt = this;
		
		delete(this.dateFormat);
		
		// create TimeField
		
		var timeConfig = Ext.apply({}, {
			id : this.id + '-time',
			format : this.timeFormat || Ext.form.TimeField.prototype.format,
			width : this.timeWidth,
			readOnly:this.readOnly,
			allowBlank:this.allowBlank,
			selectOnFocus : this.selectOnFocus,
			listeners : {
				blur : {scope : this, fn : this.onBlur},
				focus : {scope : this, fn : this.onFocus}
			}
		}, this.timeConfig);
		
		this.tf = new Ext.form.TimeField(timeConfig);
		
		this.tf.ownerCt = this;
		
		delete(this.timeFormat);
		
		// relay events
		this.relayEvents(this.df, ['focus', 'specialkey', 'invalid', 'valid']);
		
		this.relayEvents(this.tf, ['focus', 'specialkey', 'invalid', 'valid']);
	} // eo function initComponent
	
	,setReadOnly : function(ro){
		this.df.setReadOnly(ro);
		this.tf.setReadOnly(ro);
	}
	/**
	 * @private
	 * Renders underlying DateField and TimeField and provides a workaround for side error icon bug
	 */
	,onRender : function(ct, position) {
	
		// don't run more than once
		if(this.isRendered) {
			return;
		}
	
		// render underlying hidden field
		Ext.ux.form.DateTime.superclass.onRender.call(this, ct, position);
	
		// render DateField and TimeField
		// create bounding table
		var t;
	
		if('below' === this.timePosition || 'bellow' === this.timePosition) {
			t = Ext.DomHelper.append(ct, {tag:'table',style:'border-collapse:collapse',
				children:[
					{tag:'tr',
						children:[
							{tag:'td', style:'padding-bottom:1px', cls:'ux-datetime-date'}
						]
					},
					{tag:'tr',
						children:[
							{tag:'td', cls:'ux-datetime-time'}
						]
					}
				]}, true);
	
		} else {
			
			t = Ext.DomHelper.append(ct, {tag:'table',style:'border-collapse:collapse',
				children:[
					{tag:'tr',
						children:[
							{tag:'td',style:'padding-right:4px;', cls:'ux-datetime-date'},
							{tag:'td', cls:'ux-datetime-time'}
						]
					}
				]}, true);
		}
	
		this.tableEl = t;//
		
		this.wrap = t.wrap({cls:'x-form-field-wrap'});
		
		this.wrap = t.wrap();
		
		this.wrap.on("mousedown", this.onMouseDown, this, {delay:10});
		
		// render DateField & TimeField
		this.df.render(t.child('td.ux-datetime-date'));
		this.tf.render(t.child('td.ux-datetime-time'));
	
		// workaround for IE trigger misalignment bug
		//if(Ext.isIE && Ext.isStrict) {
		//	t.select('input').applyStyles({top:0});
		//}
	
		this.on('specialkey', this.onSpecialKey, this);
		
		this.df.el.swallowEvent(['keydown', 'keypress']);
		
		this.tf.el.swallowEvent(['keydown', 'keypress']);
	
		// create icon for side invalid errorIcon
		
		if('side' === this.msgTarget) {
			var elp = this.el.findParent('.x-form-element', 10, true);
			this.errorIcon = elp.createChild({cls:'x-form-invalid-icon'});
			this.df.errorIcon = this.errorIcon;
			this.tf.errorIcon = this.errorIcon;
		}
	
		// setup name for submit
		
		this.el.dom.name = this.hiddenName || this.name || this.id;
		
		// prevent helper fields from being submitted
		this.df.el.dom.removeAttribute("name");
		this.tf.el.dom.removeAttribute("name");
		
		// we're rendered flag
		this.isRendered = true;
		
		// update hidden field
		this.updateHidden();
	} // eo function onRender
	
	/**
	 * @private
	 */
	,adjustSize:Ext.BoxComponent.prototype.adjustSize
	
	/**
	 * @private
	 */
	,alignErrorIcon:function() {
	
		this.errorIcon.alignTo(this.tableEl, 'tl-tr', [2, 0]);
		
	}
	
	/**
	 * @private initializes internal dateValue
	 */
	,initDateValue:function() {
	
		this.dateValue = this.otherToNow ? new Date() : new Date(1970, 0, 1, 0, 0, 0);
		
	}
	
	/**
	 * Calls clearInvalid on the DateField and TimeField
	 */
	,clearInvalid:function(){
	
		this.df.clearInvalid();
		this.tf.clearInvalid();
	
	} // eo function clearInvalid
	
	/**
	 * @private
	 * called from Component::destroy.
	  * Destroys all elements and removes all listeners we've created.
	 */
	,beforeDestroy:function() {
		
		if(this.isRendered) {//
		//	this.removeAllListeners();
			this.wrap.removeAllListeners();
			this.wrap.remove();
			this.tableEl.remove();
			this.df.destroy();
			this.tf.destroy();
		}
	} // eo function beforeDestroy
	
	/**
	 * Disable this component.
	 * @return {Ext.Component} this
	 */
	,disable:function() {
	
		if(this.isRendered) {
			this.df.disabled = this.disabled;
			this.df.onDisable();
			this.tf.onDisable();
		}
		
		this.disabled = true;
		this.df.disabled = true;
		this.tf.disabled = true;
		this.fireEvent("disable", this);
		return this;
	} // eo function disable
	
	/**
	 * Enable this component.
	 * @return {Ext.Component} this
	 */
	,enable:function() {
	
		if(this.rendered){
			this.df.onEnable();
			this.tf.onEnable();
		}
		
		this.disabled = false;
		this.df.disabled = false;
		this.tf.disabled = false;
		this.fireEvent("enable", this);
		return this;
	} // eo function enable
	
	/**
	 * @private Focus date filed
	 */
	,focus:function() {
	
		this.df.focus();
	} // eo function focus
	
	/**
	 * @private
	 */
	,getPositionEl:function() {
	
		return this.wrap;
	}
	
	/**
	 * @private
	 */
	,getResizeEl:function() {
	
		return this.wrap;
	}
	
	/**
	 * @return {Date/String} Returns value of this field
	 */
	,getValue:function() {
	
		// create new instance of date
		return this.dateValue ? new Date(this.dateValue) : '';
	} // eo function getValue
	
	/**
	 * @return {Boolean} true = valid, false = invalid
	 * @private Calls isValid methods of underlying DateField and TimeField and returns the result
	 */
	,isValid:function() {
	
		return this.df.isValid() && this.tf.isValid();
	}
	
	/**
	 * Returns true if this component is visible
	 * @return {boolean}
	  */
	,isVisible : function(){
	
		return this.df.rendered && this.df.getActionEl().isVisible();
	}
	
	/**
	  * @private Handles blur event
	 */
	,onBlur:function(f) {
	
		// called by both DateField and TimeField blur events
		// revert focus to previous field if clicked in between
		if(this.wrapClick) {
			f.focus();
			this.wrapClick = false;
		}
	
		// update underlying value
		if(f === this.df) {
			this.updateDate();
		} else {
			this.updateTime();
		}
		
		this.updateHidden();
		
		// fire events later
		(function() {
			if(!this.df.hasFocus && !this.tf.hasFocus) {
				var v = this.getValue();
				if(String(v) !== String(this.startValue)) {
					this.fireEvent("change", this, v, this.startValue);
				}
				this.hasFocus = false;
				this.fireEvent('blur', this);
			}
		}).defer(100, this);
	} // eo function onBlur
	
	/**
	 * @private Handles focus event
	 */
	,onFocus:function() {
	
		if(!this.hasFocus){
			this.hasFocus = true;
			this.startValue = this.getValue();
			this.fireEvent("focus", this);
		}
	}
	
	/**
	 * @private Just to prevent blur event when clicked in the middle of fields
	 */
	,onMouseDown:function(e) {
	
		if(!this.disabled) {
			this.wrapClick = 'td' === e.target.nodeName.toLowerCase();
		}
	}
	
	/**
	 * @private
	 * Handles Tab and Shift-Tab events
	 */
	,onSpecialKey:function(t, e) {
	
		var key = e.getKey();
		if(key === e.TAB) {
			
			if(t === this.df && !e.shiftKey) {
				e.stopEvent();
				this.tf.focus();
			}
			
			if(t === this.tf && e.shiftKey) {
				e.stopEvent();
				this.df.focus();
			}
		}
	
		// otherwise it misbehaves in editor grid
		if(key === e.ENTER) {
			this.updateValue();
		}
	} // eo function onSpecialKey
	
	/**
	 * @private Sets the value of DateField
	 */
	,setDate:function(date) {
	
		this.df.setValue(date);
	} // eo function setDate
	
	/**
	  * @private Sets the value of TimeField
	 */
	,setTime:function(date) {
	
		this.tf.setValue(date);
	} // eo function setTime
	
	/**
	 * @private
	 * Sets correct sizes of underlying DateField and TimeField
	 * With workarounds for IE bugs
	 */
	,setSize:function(w, h) {
	
		if(!w) {
			return;
		}
		
		if('below' === this.timePosition) {
			this.df.setSize(w, h);
			this.tf.setSize(w, h);
			if(Ext.isIE) {
				this.df.el.up('td').setWidth(w);
				this.tf.el.up('td').setWidth(w);
			}
		} else {
			this.df.setSize(w - this.timeWidth - 4, h);
			this.tf.setSize(this.timeWidth, h);
			if(Ext.isIE) {
				this.df.el.up('td').setWidth(w - this.timeWidth - 4);
				this.tf.el.up('td').setWidth(this.timeWidth);
			}
		}
	} // eo function setSize
	
	/**
	 * @param {Mixed} val Value to set
	 * Sets the value of this field
	 */
	,setValue:function(val) {
	
		if(!val && true === this.emptyToNow) {
			this.setValue(new Date());
			return;
		} else if(!val) {
			this.setDate('');
			this.setTime('');
			this.updateValue();
			return;
		}
	
		if ('number' === typeof val) {
		  val = new Date(val);
		} else if('string' === typeof val && this.hiddenFormat) {
			val = Date.parseDate(val, this.hiddenFormat)
		}
	
		val = val ? val : new Date(1970, 0 ,1, 0, 0, 0);
		var da, time;
	
		if(val instanceof Date) {
			this.setDate(val);
			this.setTime(val);
			this.dateValue = new Date(val);
		} else {
			da = val.split(this.dtSeparator);
			this.setDate(da[0]);
			if(da[1]) {
				if(da[2]) {
					// add am/pm part back to time
					da[1] += da[2];
				}
				this.setTime(da[1]);
			}
		}
		this.updateValue();
	} // eo function setValue
	
	/**
	 * Hide or show this component by boolean
	 * @return {Ext.Component} this
	 */
	,setVisible: function(visible){
	
		if(visible) {
			this.df.show();
			this.tf.show();
		} else {
			this.df.hide();
			this.tf.hide();
		}
		return this;
	} // eo function setVisible
	
	
	,show:function() {
		return this.setVisible(true);
	} // eo function show
	
	
	,hide:function() {
		return this.setVisible(false);
	} // eo function hide
	
	
	/**
	 * @private Updates the date part
	 */
	,updateDate:function() {
	
		var d = this.df.getValue();
		if(d) {
			if(!(this.dateValue instanceof Date)) {
				this.initDateValue();
				if(!this.tf.getValue()) {
					this.setTime(this.dateValue);
				}
			}
			this.dateValue.setMonth(0); // because of leap years
			this.dateValue.setFullYear(d.getFullYear());
			this.dateValue.setMonth(d.getMonth(), d.getDate());//
			this.dateValue.setDate(d.getDate());
		} else {
		this.dateValue = '';
		this.setTime('');
		}
	} // eo function updateDate
	
	/**
	 * @private
	 * Updates the time part
	 */
	,updateTime:function() {
	
		var t = this.tf.getValue();
		if(t && !(t instanceof Date)) {
			t = Date.parseDate(t, this.tf.format);
		}
		
		if(t && !this.df.getValue()) {
			this.initDateValue();
			this.setDate(this.dateValue);
		}
		
		if(this.dateValue instanceof Date) {
			if(t) {
				this.dateValue.setHours(t.getHours());
				this.dateValue.setMinutes(t.getMinutes());
				this.dateValue.setSeconds(t.getSeconds());
			} else {
				this.dateValue.setHours(0);
				this.dateValue.setMinutes(0);
				this.dateValue.setSeconds(0);
			}
		}
	} // eo function updateTime
	
	/**
	 * @private Updates the underlying hidden field value
	 */
	,updateHidden:function() {
	
		if(this.isRendered) {
			var value = this.dateValue instanceof Date ? this.dateValue.format(this.hiddenFormat) : '';
			this.el.dom.value = value;
		}
	}
	
	/**
	 * @private Updates all of Date, Time and Hidden
	 */
	,updateValue:function() {
	
		this.updateDate();
		
		this.updateTime();
		
		this.updateHidden();
		
		return;
	} // eo function updateValue
	
	/**
	 * @return {Boolean} true = valid, false = invalid
	 * calls validate methods of DateField and TimeField
	 */
	,validate:function() {
	
		return this.df.validate() && this.tf.validate();
	} // eo function validate
	
	/**
	 * Returns renderer suitable to render this field
	 * @param {Object} Column model config
	 */
	,renderer: function(field) {
	
		var format = field.editor.dateFormat || Ext.ux.form.DateTime.prototype.dateFormat;
		format += ' ' + (field.editor.timeFormat || Ext.ux.form.DateTime.prototype.timeFormat);
		var renderer = function(val) {
			var retval = Ext.util.Format.date(val, format);
			return retval;
		};
		return renderer;
	} // eo function renderer

}); // eo extend// register xtype
Ext.reg('xdatetime', Ext.ux.form.DateTime);

/*Zhujf added*/
Ext.override(Ext.menu.DateMenu,{
	render : function(){
		Ext.menu.DateMenu.superclass.render.call(this);
		if(!Ext.isIE){
			this.picker.el.dom.childNodes[0].style.width = '178px';
			this.picker.el.dom.style.width = '178px';
		}
	}
});

Ext.ux.MonthPicker=Ext.extend(Ext.Component,{
	format:"M-y",
	okText:Ext.DatePicker.prototype.okText,
	cancelText:Ext.DatePicker.prototype.cancelText,
	constrainToViewport:true,
	monthNames:Date.monthNames,
	startDay:0,
	value:0,
	noPastYears:false,
	initComponent:function () {
		Ext.ux.MonthPicker.superclass.initComponent.call(this);
		this.value=this.value?
		this.value.clearTime():new Date().clearTime();
		this.addEvents(
		'select'
		);
		if(this.handler) {
			this.on("select",this.handler,this.scope||this);
		}
	},
	focus:function () {
		if(this.el) {
			this.update(this.activeDate);
		}
	},
	onRender:function (container,position) {
		var m=['<div style="width: 200px; height:175px;"></div>'];
		m[m.length]='<div class="x-date-mp"></div>';
		var el=document.createElement("div");
		el.className="x-date-picker";
		el.innerHTML=m.join("");
		container.dom.insertBefore(el,position);
		this.el=Ext.get(el);
		this.monthPicker=this.el.down('div.x-date-mp');
		this.monthPicker.enableDisplayMode('block');
		this.el.unselectable();
		this.showMonthPicker();
		if(Ext.isIE) {
			this.el.repaint();
		}
		this.update(this.value);
	},
	createMonthPicker:function () {
		if(!this.monthPicker.dom.firstChild) {
			var buf=['<table border="0" cellspacing="0">'];
			for(var i=0;i<6;i++) {
				buf.push(
				'<tr><td class="x-date-mp-month"><a href="#">',this.monthNames[i].substr(0,3),'</a></td>',
				'<td class="x-date-mp-month x-date-mp-sep"><a href="#">',this.monthNames[i+6].substr(0,3),'</a></td>',
				i==0?
				'<td class="x-date-mp-ybtn" align="center"><a class="x-date-mp-prev"></a></td><td class="x-date-mp-ybtn" align="center"><a class="x-date-mp-next"></a></td></tr>':
				'<td class="x-date-mp-year"><a href="#"></a></td><td class="x-date-mp-year"><a href="#"></a></td></tr>'
				);
			}
			buf.push(
			'<tr class="x-date-mp-btns"><td colspan="4"><button type="button" class="x-date-mp-ok">',
			this.okText,
			'</button><button type="button" class="x-date-mp-cancel">',
			this.cancelText,
			'</button></td></tr>',
			'</table>'
			);
			this.monthPicker.update(buf.join(''));
			this.monthPicker.on('click',this.onMonthClick,this);
			this.monthPicker.on('dblclick',this.onMonthDblClick,this);
			this.mpMonths=this.monthPicker.select('td.x-date-mp-month');
			this.mpYears=this.monthPicker.select('td.x-date-mp-year');
			this.mpMonths.each(function (m,a,i) {
				i+=1;
				if((i%2)==0) {
					m.dom.xmonth=5+Math.round(i*.5);
				}else {
					m.dom.xmonth=Math.round((i-1)*.5);
				}
			});
		}
	},
	showMonthPicker:function () {
		this.createMonthPicker();
		var size=this.el.getSize();
		this.monthPicker.setSize(size);
		this.monthPicker.child('table').setSize(size);
		this.mpSelMonth=(this.activeDate||this.value).getMonth();
		this.updateMPMonth(this.mpSelMonth);
		this.mpSelYear=(this.activeDate||this.value).getFullYear();
		this.updateMPYear(this.mpSelYear);
		this.monthPicker.show();
		//this.monthPicker.slideIn('t', {duration:.2});
	},
	updateMPYear:function (y) {
		if(this.noPastYears) {
			var minYear=new Date().getFullYear();
			if(y<(minYear+4)) {
				y=minYear+4;
			}
		}
		this.mpyear=y;
		var ys=this.mpYears.elements;
		for(var i=1;i<=10;i++) {
			var td=ys[i-1],y2;
			if((i%2)==0) {
				y2=y+Math.round(i*.5);
				td.firstChild.innerHTML=y2;
				td.xyear=y2;
			}else {
				y2=y-(5-Math.round(i*.5));
				td.firstChild.innerHTML=y2;
				td.xyear=y2;
			}
			this.mpYears.item(i-1)[y2==this.mpSelYear?'addClass':'removeClass']('x-date-mp-sel');
		}
	},
	updateMPMonth:function (sm) {
		this.mpMonths.each(function (m,a,i) {
			m[m.dom.xmonth==sm?'addClass':'removeClass']('x-date-mp-sel');
		});
	},
	selectMPMonth:function (m) {
	},
	onMonthClick:function (e,t) {
		e.stopEvent();
		var el=new Ext.Element(t),pn;
		if(el.is('button.x-date-mp-cancel')) {
			this.hideMonthPicker();
			//this.fireEvent("select", this, this.value);
		}
		else if(el.is('button.x-date-mp-ok')) {
			this.update(new Date(this.mpSelYear,this.mpSelMonth,(this.activeDate||this.value).getDate()));
			//this.hideMonthPicker();
			this.fireEvent("select",this,this.value);
		}
		else if(pn=el.up('td.x-date-mp-month',2)) {
			this.mpMonths.removeClass('x-date-mp-sel');
			pn.addClass('x-date-mp-sel');
			this.mpSelMonth=pn.dom.xmonth;
		}
		else if(pn=el.up('td.x-date-mp-year',2)) {
			this.mpYears.removeClass('x-date-mp-sel');
			pn.addClass('x-date-mp-sel');
			this.mpSelYear=pn.dom.xyear;
		}
		else if(el.is('a.x-date-mp-prev')) {
			this.updateMPYear(this.mpyear-10);
		}
		else if(el.is('a.x-date-mp-next')) {
			this.updateMPYear(this.mpyear+10);
		}
	},
	onMonthDblClick:function (e,t) {
		e.stopEvent();
		var el=new Ext.Element(t),pn;
		if(pn=el.up('td.x-date-mp-month',2)) {
			this.update(new Date(this.mpSelYear,pn.dom.xmonth,(this.activeDate||this.value).getDate()));
			//this.hideMonthPicker();
			this.fireEvent("select",this,this.value);
		}
		else if(pn=el.up('td.x-date-mp-year',2)) {
			this.update(new Date(pn.dom.xyear,this.mpSelMonth,(this.activeDate||this.value).getDate()));
			//this.hideMonthPicker();
			this.fireEvent("select",this,this.value);
		}
	},
	hideMonthPicker:function (disableAnim) {
		Ext.menu.MenuMgr.hideAll();
	},
	showPrevMonth:function (e) {
		this.update(this.activeDate.add("mo",-1));
	},
	showNextMonth:function (e) {
		this.update(this.activeDate.add("mo",1));
	},
	showPrevYear:function () {
		this.update(this.activeDate.add("y",-1));
	},
	showNextYear:function () {
		this.update(this.activeDate.add("y",1));
	},
	update:function (date) {
		this.activeDate=date;
		this.value=date;
		if(!this.internalRender) {
			var main=this.el.dom.firstChild;
			var w=main.offsetWidth;
			this.el.setWidth(w+this.el.getBorderWidth("lr"));
			Ext.fly(main).setWidth(w);
			this.internalRender=true;
			if(Ext.isOpera&&!this.secondPass) {
				main.rows[0].cells[1].style.width=(w-(main.rows[0].cells[0].offsetWidth+main.rows[0].cells[2].offsetWidth))+"px";
				this.secondPass=true;
				this.update.defer(10,this,[date]);
			}
		}
	}
});
Ext.reg('monthpicker',Ext.ux.MonthPicker);

Ext.ux.MonthItem=function (config) {
	Ext.ux.MonthItem.superclass.constructor .call(this,new Ext.ux.MonthPicker(config),config);
	this.picker=this.component;
	this.addEvents('select');
	this.picker.on("render",function (picker) {
		picker.getEl().swallowEvent("click");
		picker.container.addClass("x-menu-date-item");
	});
	this.picker.on("select",this.onSelect,this);
};
Ext.extend(Ext.ux.MonthItem,Ext.menu.Adapter,{
	onSelect:function (picker,date) {
		this.fireEvent("select",this,date,picker);
		Ext.ux.MonthItem.superclass.handleClick.call(this);
	}
});
Ext.ux.MonthMenu=function (config) {
	Ext.ux.MonthMenu.superclass.constructor .call(this,config);
	this.plain=true;
	var mi=new Ext.ux.MonthItem(config);
	this.add(mi);
	this.picker=mi.picker;
	this.relayEvents(mi,["select"]);
};
Ext.extend(Ext.ux.MonthMenu,Ext.menu.Menu,{
	cls:'x-date-menu'
});
Ext.ux.MonthField=function (cfg) {
	Ext.ux.MonthField.superclass.constructor .call(this,Ext.apply({
	},cfg||{
	}));
};
Ext.extend(Ext.ux.MonthField,Ext.form.DateField,{
	format:"Y-m",
	triggerClass:"x-form-date-trigger",
	menuListeners:{
		select:function (m,d) {
			this.setValue(d.format(this.format));
		},
		show:function () {
			this.onFocus();
		},
		hide:function () {
			this.focus.defer(10,this);
			var ml=this.menuListeners;
			this.menu.un("select",ml.select,this);
			this.menu.un("show",ml.show,this);
			this.menu.un("hide",ml.hide,this);
		}
	},
	onTriggerClick:function () {
		if(this.disabled) {
			return ;
		}
		if(this.menu==null) {
			this.menu=new Ext.ux.MonthMenu();
		}
		Ext.apply(this.menu.picker,{
		});
		this.menu.on(Ext.apply({
		},this.menuListeners,{
			scope:this
		}));
		this.menu.show(this.el,"tl-bl?");
	}
});
Ext.reg("monthfield",Ext.ux.MonthField);
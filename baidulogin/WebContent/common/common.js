/**
 * Created with JetBrains PhpStorm.
 * User: huangjin02
 * Date: 13-8-28
 * Time: 下午1:56
 * To change this template use File | Settings | File Templates.
 */
;
// 生成头部
(function(){
  var tpl = '<header class="col-md-12"><h1>Frontia Demo</h1></header>';
  (document.getElementById('header') || {}).innerHTML = tpl;
}());

// 生成导航
(function () {
  "use strict";
  var navData = { children: [ {  href: 'social/baidu.html', title: '社会化登录', active: false, children: [
          {
            href: 'social/baidu.html',
            title: '百度',
            active: false,
            children: []
          },
          {
            href: 'social/qqdenglu.html',
            title: 'QQ账号登录',
            active: false,
            children: []
          },
          {
            href: 'social/sinaweibo.html',
            title: '新浪微博',
            active: false,
            children: []
          },
          {
            href: 'social/qqweibo.html',
            title: '腾讯微博',
            active: false,
            children: []
          },
          {
            href: 'social/renren.html',
            title: '人人网',
            active: false,
            children: []
          },
          {
            href: 'social/kaixin.html',
            title: '开心网',
            active: false,
            children: []
          }
        ]
      },{
        href: 'storage/bcs/upload.html',
        title: '应用数据存储',
        active: false,
        children: [
          {
            href: 'storage/bcs/upload.html',
            title: '文件存储 (BCS)',
            active: false,
            children: [
              {
                href: 'storage/bcs/upload.html',
                title: '上传一个文件',
                active: false,
                children: []
              },
              {
                href: 'storage/bcs/list.html',
                title: '列出所有文件',
                active: false,
                children: []
              },
              {
                href: 'storage/bcs/delete.html',
                title: '删除一个文件',
                active: false,
                children: []
              },
              {
                href: 'storage/bcs/download.html',
                title: '下载一个文件',
                active: false,
                children: []
              }
            ]
          },
          {
            href: 'storage/bss/insert.html',
            title: '结构化数据存储 (BSS)',
            active: false,
            children: [
              {
                href: 'storage/bss/insert.html',
                title: '插入数据',
                active: false,
                children: []
              },
              {
                href: 'storage/bss/find.html',
                title: '查找数据',
                active: false,
                children: []
              },
              {
                href: 'storage/bss/update.html',
                title: '更新数据',
                active: false,
                children: []
              },
              {
                href: 'storage/bss/delete.html',
                title: '删除数据',
                active: false,
                children: []
              }
            ]
          }
        ]
      },
      {
        href: 'storage/pcs/upload.html',
        title: '用户数据存储',
        active: false,
        children: [
          {
            href: 'storage/pcs/upload.html',
            title: '个人云存储 (PCS)',
            active: false,
            children: [
              {
                href: 'storage/pcs/upload.html',
                title: '上传文件',
                active: false,
                children: []
              },
              {
                href: 'storage/pcs/list.html',
                title: '获取文件列表',
                active: false,
                children: []
              },
              {
                href: 'storage/pcs/listStream.html',
                title: '获取流文件列表',
                active: false,
                children: []
              },
              {
                href: 'storage/pcs/download.html',
                title: '下载文件',
                active: false,
                children: []
              },
              {
                href: 'storage/pcs/delete.html',
                title: '删除文件',
                active: false,
                children: []
              },
              {
                href: 'storage/pcs/quota.html',
                title: '获取空间配额信息',
                active: false,
                children: []
              }
            ]
          }
        ]
      },
      {
        href: 'push/index.html',
        title: '消息推送',
        active: false,
        children: [
          {
            href: 'push/index.html',
            title: '消息推送',
            active: false,
            children: []
          }
        ]
      }
    ]
  };

  function htmlspecialchars (string, quote_style, charset, double_encode) {
    // http://kevin.vanzonneveld.net
    // +   original by: Mirek Slugen
    // +   improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +   bugfixed by: Nathan
    // +   bugfixed by: Arno
    // +    revised by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +    bugfixed by: Brett Zamir (http://brett-zamir.me)
    // +      input by: Ratheous
    // +      input by: Mailfaker (http://www.weedem.fr/)
    // +      reimplemented by: Brett Zamir (http://brett-zamir.me)
    // +      input by: felix
    // +    bugfixed by: Brett Zamir (http://brett-zamir.me)
    // %        note 1: charset argument not supported
    // *     example 1: htmlspecialchars("<a href='test'>Test</a>", 'ENT_QUOTES');
    // *     returns 1: '&lt;a href=&#039;test&#039;&gt;Test&lt;/a&gt;'
    // *     example 2: htmlspecialchars("ab\"c'd", ['ENT_NOQUOTES', 'ENT_QUOTES']);
    // *     returns 2: 'ab"c&#039;d'
    // *     example 3: htmlspecialchars("my "&entity;" is still here", null, null, false);
    // *     returns 3: 'my &quot;&entity;&quot; is still here'
    var optTemp = 0,
      i = 0,
      noquotes = false;
    if (typeof quote_style === 'undefined' || quote_style === null) {
      quote_style = 2;
    }
    string = string.toString();
    if (double_encode !== false) { // Put this first to avoid double-encoding
      string = string.replace(/&/g, '&amp;');
    }
    string = string.replace(/</g, '&lt;').replace(/>/g, '&gt;');

    var OPTS = {
      'ENT_NOQUOTES': 0,
      'ENT_HTML_QUOTE_SINGLE': 1,
      'ENT_HTML_QUOTE_DOUBLE': 2,
      'ENT_COMPAT': 2,
      'ENT_QUOTES': 3,
      'ENT_IGNORE': 4
    };
    if (quote_style === 0) {
      noquotes = true;
    }
    if (typeof quote_style !== 'number') { // Allow for a single string or an array of string flags
      quote_style = [].concat(quote_style);
      for (i = 0; i < quote_style.length; i++) {
        // Resolve string input to bitwise e.g. 'ENT_IGNORE' becomes 4
        if (OPTS[quote_style[i]] === 0) {
          noquotes = true;
        }
        else if (OPTS[quote_style[i]]) {
          optTemp = optTemp | OPTS[quote_style[i]];
        }
      }
      quote_style = optTemp;
    }
    if (quote_style & OPTS.ENT_HTML_QUOTE_SINGLE) {
      string = string.replace(/'/g, '&#039;');
    }
    if (!noquotes) {
      string = string.replace(/"/g, '&quot;');
    }

    return string;
  }

  function buildList(list) {
    list = list || [];
    var item, active = false;
    var tpl = [];
    tpl.push('<ul class="nav">');
    for(var i = 0, n = list.length; i < n; i ++) {
      item = list[i];
      active = location.href.indexOf(item.href) > -1;
      if(item.active || active) {
        tpl.push('<li class="active">')
      } else {
        tpl.push('<li class="">')
      }
      tpl.push('<a target="demo-frame" href="');
      tpl.push(item.href || '');
      tpl.push('">');
      tpl.push(item.title || '');
      tpl.push('</a>');
      if(item.children && item.children.length && item.children.length > 0) {
        tpl.push(buildList(item.children));
      }
    }
    tpl.push('</ul>');
    return tpl.join('');
  }

  function buildHtml(data) {
    var tpl = [];
    tpl.push('<nav class="hidden-print doc-sidebar">');
    tpl.push(buildList(data.children || []));
    tpl.push('</nav>');
    return tpl.join('');
  }

  var showResourceBtn = document.getElementById('show-resource');
  var demoFrame = document.getElementById('demo-frame');
  var resource = document.getElementById('resource');
  var nav = document.getElementById('nav');
  var childNav = document.getElementById('func-examples');

  var isOpenNewIframe = false;
  var codeCache = '';

  demoFrame.onload = function() {
    if(isOpenNewIframe) {
      codeCache = htmlspecialchars("<!DOCTYPE html>\r\n" + demoFrame.contentDocument.documentElement.outerHTML);
      isOpenNewIframe = false;
    }
  };

  showResourceBtn.addEventListener('click', function() {
    resource.innerHTML = '<pre class="prettyprint"><code>' + codeCache +'</code></pre>';
    prettyPrint();
  });


  function delegateClick(ev) {

    var target = ev.target;
    if(target.tagName.toLowerCase() === 'a') {
      isOpenNewIframe = true;
      resource.innerHTML = '';

      if(!(target.compareDocumentPosition(nav) & 8)) {
        return ;
      }

      var ns = target.nextElementSibling;
      if(!ns) {
        childNav.innerHTML = '';
        return ;
      }

      if(ns.tagName.toLowerCase() != 'ul' || !ns.classList.contains('nav')) {
        childNav.innerHTML = '';
        return ;
      }

      childNav.innerHTML = ns.outerHTML;

    }
  }

  if(nav) {
    nav.innerHTML = buildHtml(navData);
    nav.addEventListener('click', delegateClick)
  }
  if(childNav) {
    childNav.addEventListener('click', delegateClick);
  }

}());
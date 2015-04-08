/*
 * 创建日期 2005-5-17
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.linkage.toptea.sysmgr.fm.assistant.web;

import java.io.PrintWriter;

import javax.swing.tree.TreeModel;

import com.linkage.toptea.sysmgr.cm.ConfigTreeNode;
import com.linkage.toptea.sysmgr.cm.MoInfo;
import com.linkage.toptea.sysmgr.web.TreeFilter;
import com.linkage.toptea.sysmgr.web.TreeRenderer;

/**
 * @author luzl2net
 *
 * TODO 要更改此生成的类型注释的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
public  class TreeRenderer3 implements TreeRenderer {
        private TreeRenderer treeRenderer;
        
       
        
        public TreeRenderer3() {
        }

        /**
         * 兼容原来的接口
         * @param tr
         */
        public TreeRenderer3(TreeRenderer tr) {
             this.treeRenderer = tr;
        }

        /**
         * 通过标识, 查找路径
         * @param indentity
         * @return
         */
        public Object[] getObjectPath(String indentity) {
            return null;
        }

        /**
         * 取标识
         */
      public String getIdentity(Object node) {
          if(treeRenderer != null)
            return treeRenderer.getIdentity(node);
          return null;
      }

      public String getLinkURL(Object node) {
        if(treeRenderer != null)
            return treeRenderer.getLinkURL(node);
          return null;
      }

      public String getTarget(Object node) {
        if(treeRenderer != null)
            return treeRenderer.getTarget(node);
          return null;
      }

      public String getIconURL(Object node) {
        if(treeRenderer != null)
            return treeRenderer.getIconURL(node);
          return null;
      }

    
      public void drowOverlayIcon(String ctxPath, Object node, PrintWriter out) {
          out.print("<img src=\"" + ctxPath +  "/images/empty.gif\"/>");
      }

      /**
       * 获取使图标处于关闭状态的语句.
       * 其中有一个固定的参数id, 是node的id.
       */
      public String closeImageFunction() {
          return "function closeImage(id) {var _bg = document.getElementById('bgimg_'+id); _bg.style.backgroundImage = _bg.style.backgroundImage.replace(/folderopen.gif/, 'folder.gif');}";
      }

      public String selectNodeByIdentityFunction() {
          return "function selectNodeByIdentity(identity) {" +
                "var _a = document.getElementById(identity); " +
                "if(_a) return selectNodeById(_a.getAttribute('node_id')); " +
                "return false;}";
      }
      public String selectNodeByIdFunction() {
          return "var currentNode; " +
                "function selectNodeById(id) {"+
                "  if(currentNode)" +
                "     currentNode.className = \"node\"; " +
                "  currentNode = document.getElementById('node_'+id);" +
                "  if(currentNode) {" +
                "     currentNode.className = \"nodeSel\";" +
                "     var topheight=4;"+
                "     if(document.getElementById('configTree'))"+
                "        topheight=document.getElementById('configTree').offsetTop;"+
                "     var scrolltop=0,scrollleft=0,winheight=0;" +
                "     if(document.documentElement&&document.documentElement.scrollTop){" +
                "         scrolltop=document.documentElement.scrollTop;" +
                "         scrollleft=document.documentElement.scrollLeft;" +
                "     }" +
                "     else if(document.body){" +
                "         scrolltop=document.body.scrollTop;" +
                "         scrollleft=document.body.scrollLeft;" +
                "     }" +
                "     if(document.compatMode == 'BackCompat')"+
                "         winheight=document.body.clientHeight  ;"+
                "     else "+
                "         winheight=document.documentElement.clientHeight;"+
                "     var elemtop=currentNode.getBoundingClientRect().top;"+
                "     if(elemtop<topheight)"+
                "         window.scrollTo(scrollleft,scrolltop-(topheight-elemtop));"+
                "     if(elemtop>(winheight-10))"+
                "         window.scrollTo(scrollleft,scrolltop+(elemtop-winheight)+10);"+
                "     return true;" +
                "  }"+
                "  return false;"+
                "}";
      }

      public void draw(String ctxPath, TreeModel tm, String id, Object node, PrintWriter out, int passedState, boolean nodeState){
            //如果没有通过检查, 就把连接设为空
            String link = getLinkURL(node);
            String icon = getIconURL(node);
            String identity = getIdentity(node);
            String target = getTarget(node);
            String dblclick = "";// getDBClick(node);

           
            out.print("<table cellSpacing=\"0\"><tr><td id=\"bgimg_"+id+"\" style=\"background-repeat:no-repeat;display: block;background-image:url(");

            if (node.equals(tm.getRoot())) {
              out.print(icon != null ? icon : ctxPath + "/images/base.gif");
            }
            else if (!tm.isLeaf(node)) {
              if (nodeState) {
                out.print(icon != null && !icon.equals(ctxPath + "/images/folder.gif")? icon : ctxPath + "/images/folderopen.gif");
              }
              else {
                out.print(icon != null && !icon.equals(ctxPath + "/images/folderopen.gif")? icon : ctxPath + "/images/folder.gif");
              }
            }
            else {
              out.print(icon != null ? icon : ctxPath + "/images/page.gif");
            }
            out.print(");\">");
            out.print("<a ");
            if(identity != null) {
                out.print("id = \"");
                out.print(identity);
                out.print("_img\" ");
            }
            if(passedState == TreeFilter.CHK_PASSED && link != null) {
                out.print("href = \"");
                out.print(link);
                out.print("\" ");
            }
            if (passedState == TreeFilter.CHK_PASSED && target != null) {
                out.print("target = \"");
                out.print(link);
                out.print("\" ");
            }
            if (passedState == TreeFilter.CHK_PASSED) {
                out.print("  onClick=\"selectNodeById(");
                out.print(id);
                out.print(");\"");
            }
           
            out.print(">");
            drowOverlayIcon(ctxPath, node, out);
            out.print("</A>");

            out.print("</td><td>");

            out.print("<a node_id=\"");
            out.print(id);
            out.print("\" ");
            if(identity != null) {
                out.print("id = \"");
                out.print(identity);
                out.print("\" ");
            }
            if(passedState == TreeFilter.CHK_PASSED && link != null) {
                out.print("href = \"");
                out.print(link);
                out.print("\" ");
            }
            if (passedState == TreeFilter.CHK_PASSED && target != null) {
                out.print("target = \"");
                out.print(link);
                out.print("\" ");
            }
            if (passedState == TreeFilter.CHK_PASSED) {
                out.print("  onClick=\"selectNodeById(");
                out.print(id);
                out.print(");\"");
            }
            
            
            //	out.print(" ondblclick=\"alert(1);");
            	//out.print(dblclick);
            //	out.print("\"");
            
            out.print(">");
            out.print("<span id=\"node_" + id + "\" class=\"node\">");
            out.print(node);
            out.print("</span>");
            out.print("</A>");
          out.print("</td></tr></table>");
      }
}

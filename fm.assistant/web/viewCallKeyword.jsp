 <%@page language="java" contentType="text/html; charset=gb2312"%>

<%@page import="java.util.*"%>
<%@page import="com.linkage.toptea.auc.*" %>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.linkage.toptea.sysmgr.fm.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.util.*"%>
<%@page import="com.linkage.toptea.context.Context,com.linkage.toptea.sysmgr.cm.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.callkeyword.*"%>



<html>
  <head>
  	<meta http-equiv="pragma" content="no-cache">
	<script type="text/javascript" src="<%=request.getContextPath()%>/webframe/webframe.js" language="javascript"></script>	
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/ttable_css.jsp" type="text/css"/>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/sysmgr.css" type="text/css"/>
<%

	UserManager userManager = (UserManager) Context.getContext().getAttribute("userManager");
    ConfigManager configManager = (ConfigManager)Context.getContext().getAttribute("configManager");
    CallKeyWordManager ckm = (CallKeyWordManager) Context.getContext().getAttribute("callKeyWordManager");

    final String[] responsibleArr = {"-","平台类责任人一","平台类责任人二","业务类责任人一","业务类责任人二","备用类责任人一","备用类责任人二"};
	String parameter = request.getParameter( "nodeId" );
	
	String type = request.getParameter( "type" );
	 
	if ( null == parameter || "".equals(parameter) ) {
	    out.println("<br><font size=3><I>没有找到任何规则，规则可能已经被删除!</I></font>");
	    return;
	}

	ArrayList<CallKeyWordInfo> allKeywords = null;
	if ( "gridMo".equals(type) ) { //关键字查询的场合
	    allKeywords = ckm.indexCallKeyWordInfo( parameter,"MO" );
	} else if ( "gridKey".equals(type) ) { //关键字查询的场合
	    allKeywords = ckm.indexCallKeyWordInfo( parameter,"key" );
	} else if ( "gridId".equals(type) ) {
	    allKeywords = new ArrayList<CallKeyWordInfo>();
	    allKeywords.add( ckm.indexCallKeyWordInfo(parameter) );
	} else {
	    out.println("<br><font color='red'size=3>[" + parameter + "]参数错误！</font>");
	    return;
	}     
	if ( null == allKeywords || allKeywords.size() == 0 ) {
	    out.println("<br><font color='red'size=3>没有找到任何规则，规则可能已经被删除!</font>");
	    return;
	}
%>
</head>
<body style="margin:10px 0px 10px 10px">
    <table width="100%" class="standard" cellspacing="0">

        <thead>
            <tr>
                <th>对象名称</th>             
                <th>关键字</th>
                <th>应用于子对象</th> 
                <th>外呼责任人</th> 
                <th>修改时间</th>
                <th>修改者</th>               
                <th>操作</th>
            </tr>
        </thead> 

        <tbody>
		
        <%
        	for( int i =0; i < allKeywords.size(); i++ ){
				out.println( "<tr>");
				CallKeyWordInfo keyword = (CallKeyWordInfo)allKeywords.get(i);
				
				// 对象
				out.println( "<td>" 
					+ AlarmUtil.getNamingPathFromNodeId(keyword.getMoId(), "red")
					+ "</td>" );

				
			    //关键字
				out.println( "<td>" + keyword.getKeyword() + "</td>" );
			    
				
				// 是否应用于子对象
				if ( keyword.getForChild() == 0 ) {
				    out.println( "<td align='center'> 否 </td>" );
				} else {
				    out.println( "<td align='center'> 是 </td>" );
				}
				
			    //责任人
			    if ( keyword.getCaller() >=0 && keyword.getCaller() <= 6 ) {
			    	out.println( "<td>" + responsibleArr[keyword.getCaller()] + "</td>" );
			    } else {
					out.println( "<td>" + keyword.getCaller() + "</td>" );			    	
			    }
				
				//修订时间
				long time = keyword.getTime().getTime();
				SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				out.println( "<td align='center'>" + timeFormat.format(time) + "</td>" );
				
				// 修订者
				User user = userManager.getUser(keyword.getUserId());
				String userName = "<font color='red'>" + keyword.getUserId() + ":可能已经被删除!</font>";
				if ( user != null ) {
				    userName = user.getName();
				}
				out.println( "<td align='center'>" + userName + "</td>" );
				String  modifyImage = "<img src='" + request.getContextPath() 
		                  + "/images/icons/modifyGradeMap.gif' onclick='addKeyword(\"" + keyword.getId()  + "\")' alt='修改'/>";
				String  deleteImage = "<img src='" + request.getContextPath() 
		                  + "/images/icons/delete.gif' onclick='deleteKey(\""+ keyword.getId() +"\")' alt='删除'/>";
		        
		        if ( "gridId".equals(type) ) {
					out.println( "<td align='center' style='display:none'>" + 
					    				modifyImage + "&nbsp;&nbsp;" + deleteImage + "</td>" );
                } else {
                    out.println( "<td align='center'>" + modifyImage + "&nbsp;&nbsp;" + deleteImage + "</td>" );
        
                }
		                          
				out.println( "<tr>");
        	}
        %>
        </tbody>
    </table>
</body>
</html>
<%!
/** 将字节字符串(字节间用 ; 分割)转换成中文, 用于解决页面中文乱码问题 */
public String bytesToChineseString ( String bytesStr ) {
	byte [] result  = null;
	try {
		String [] f = bytesStr.split(";");
		result = new byte [f.length];
		for ( int i = 0; i< f.length; i++  ) {
			result[i] = new Byte(f[i]);
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	return new String(result);
}
%>

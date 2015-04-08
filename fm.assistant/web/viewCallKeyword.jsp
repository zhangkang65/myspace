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

    final String[] responsibleArr = {"-","ƽ̨��������һ","ƽ̨�������˶�","ҵ����������һ","ҵ���������˶�","������������һ","�����������˶�"};
	String parameter = request.getParameter( "nodeId" );
	
	String type = request.getParameter( "type" );
	 
	if ( null == parameter || "".equals(parameter) ) {
	    out.println("<br><font size=3><I>û���ҵ��κι��򣬹�������Ѿ���ɾ��!</I></font>");
	    return;
	}

	ArrayList<CallKeyWordInfo> allKeywords = null;
	if ( "gridMo".equals(type) ) { //�ؼ��ֲ�ѯ�ĳ���
	    allKeywords = ckm.indexCallKeyWordInfo( parameter,"MO" );
	} else if ( "gridKey".equals(type) ) { //�ؼ��ֲ�ѯ�ĳ���
	    allKeywords = ckm.indexCallKeyWordInfo( parameter,"key" );
	} else if ( "gridId".equals(type) ) {
	    allKeywords = new ArrayList<CallKeyWordInfo>();
	    allKeywords.add( ckm.indexCallKeyWordInfo(parameter) );
	} else {
	    out.println("<br><font color='red'size=3>[" + parameter + "]��������</font>");
	    return;
	}     
	if ( null == allKeywords || allKeywords.size() == 0 ) {
	    out.println("<br><font color='red'size=3>û���ҵ��κι��򣬹�������Ѿ���ɾ��!</font>");
	    return;
	}
%>
</head>
<body style="margin:10px 0px 10px 10px">
    <table width="100%" class="standard" cellspacing="0">

        <thead>
            <tr>
                <th>��������</th>             
                <th>�ؼ���</th>
                <th>Ӧ�����Ӷ���</th> 
                <th>���������</th> 
                <th>�޸�ʱ��</th>
                <th>�޸���</th>               
                <th>����</th>
            </tr>
        </thead> 

        <tbody>
		
        <%
        	for( int i =0; i < allKeywords.size(); i++ ){
				out.println( "<tr>");
				CallKeyWordInfo keyword = (CallKeyWordInfo)allKeywords.get(i);
				
				// ����
				out.println( "<td>" 
					+ AlarmUtil.getNamingPathFromNodeId(keyword.getMoId(), "red")
					+ "</td>" );

				
			    //�ؼ���
				out.println( "<td>" + keyword.getKeyword() + "</td>" );
			    
				
				// �Ƿ�Ӧ�����Ӷ���
				if ( keyword.getForChild() == 0 ) {
				    out.println( "<td align='center'> �� </td>" );
				} else {
				    out.println( "<td align='center'> �� </td>" );
				}
				
			    //������
			    if ( keyword.getCaller() >=0 && keyword.getCaller() <= 6 ) {
			    	out.println( "<td>" + responsibleArr[keyword.getCaller()] + "</td>" );
			    } else {
					out.println( "<td>" + keyword.getCaller() + "</td>" );			    	
			    }
				
				//�޶�ʱ��
				long time = keyword.getTime().getTime();
				SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				out.println( "<td align='center'>" + timeFormat.format(time) + "</td>" );
				
				// �޶���
				User user = userManager.getUser(keyword.getUserId());
				String userName = "<font color='red'>" + keyword.getUserId() + ":�����Ѿ���ɾ��!</font>";
				if ( user != null ) {
				    userName = user.getName();
				}
				out.println( "<td align='center'>" + userName + "</td>" );
				String  modifyImage = "<img src='" + request.getContextPath() 
		                  + "/images/icons/modifyGradeMap.gif' onclick='addKeyword(\"" + keyword.getId()  + "\")' alt='�޸�'/>";
				String  deleteImage = "<img src='" + request.getContextPath() 
		                  + "/images/icons/delete.gif' onclick='deleteKey(\""+ keyword.getId() +"\")' alt='ɾ��'/>";
		        
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
/** ���ֽ��ַ���(�ֽڼ��� ; �ָ�)ת��������, ���ڽ��ҳ�������������� */
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

<%@ page language="java" contentType="text/html; charset=GBK"%>
<%@ page language="java" contentType="text/html; charset=GBK"%>
<%@ page import="com.linkage.toptea.sysmgr.util.*"%>
<%@ page import="com.linkage.toptea.context.*"%>
<%@ page import="com.linkage.toptea.sysmgr.cm.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.concern.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.track.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.*,com.linkage.toptea.sysmgr.fm.util.*"%>
<%@ page import="com.linkage.toptea.auc.*,com.linkage.toptea.code.CodeManager" %>
<%@ page import="com.linkage.toptea.sysmgr.ovsd.OvsdInteraction"%>
<%@page import="java.net.URLEncoder"%>
<%!

//������
    public String getOvsdIncAssignToPerson(String workId){
        String person = null;
        //person = alarmTransmitterManager.getWorkToPerson(workId);
        return (person == null || person.length() == 0)?"&nbsp;":person;
    }
    //״̬
    public String getOvsdIncStatus(String workId){
        String status = null;
        //status = alarmTransmitterManager.getWorkStatus(workId);
        return (status == null || status.length() == 0)?"&nbsp;":status;
    }
     %>
<%
/**
*�澯��ϸ��Ϣ
*/
String mode =  request.getParameter("mode");
String alarmId = request.getParameter("alarmId");
String istrack =  request.getParameter("istrack");

String datePattern = "yyyy-MM-dd HH:mm:ss";

if (mode==null) mode = "";
UserManager userManager = (UserManager)Context.getContext().getAttribute("userManager");



if (mode.equals("base")){
    AlarmManager am = (AlarmManager)Context.getContext().getAttribute("alarmManager");
    AlarmFilterInfo filter = new AlarmFilterInfo();
     ConfigManager cfmg = (ConfigManager)Context.getContext().getAttribute("configManager");
    filter.setAlarmIds(new String[]{alarmId});
    AlarmInfo[] ais = am.fecthAlarm(filter);
   String[] gradeNames = AlarmManager.gradeNames;
       String[] states = {"�", "�ǻ","��ɾ��"};
    CodeManager codeManager = (CodeManager)Context.getContext().getAttribute("codeManager");
    AlarmOwnerRuleManager arm =   (AlarmOwnerRuleManager)Context.getContext().getAttribute("alarmOwnerRuleManager");
    
  if (ais!=null){
      AlarmInfo ci = ais[0];
%>
<div width="100%" style="OVERFLOW: auto;" id="alarmlistdiv">
<table width="100%" border="0"  style="background-color:#ffffff" >
  <tr>
    <td height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;" width=80><font color="white">&nbsp;&nbsp;����</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;" colspan=3>&nbsp;<%=cfmg.getNamingPath(ci.getPath()) %> </td>
    
  </tr>
  <tr>
    <td height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;��Դ</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%
      String source = ci.getSource();
      try{
     source= codeManager.getCodeCaption(AlarmManager.ALARM_SOURCE_CODE_CATEGORY, source);
     }catch(Exception e){
     }
     out.print(source);
     %></td>
  <td style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;����</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%=ci.getCode() %></td>
  </tr>
  <tr>
    <td height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;��ǰ����</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%=gradeNames[ci.getCurrentGrade()] %></td>
  <td style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;ԭʼ����</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%=gradeNames[ci.getOriginalGrade()] %></td>
  </tr>
  <tr>
    <td height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;��һ��ʱ��</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%=ChangeDateType.parseLongToStr(ci.getFirstOccurTime(),datePattern) %></td>
  <td style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;���һ��ʱ��</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%=ChangeDateType.parseLongToStr(ci.getLastOccurTime(),datePattern) %></td>
  </tr>
  <tr>
    <td height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;�ظ�����</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%=ci.getCount() %></td>
  <td style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;״̬</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%=states[ci.getState()] %></td>
  </tr>
  <tr>
    <td height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;������</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%
                    String[] owners =null;
                    StringBuffer ownersname=new StringBuffer();
                    try{
                        owners = arm.getAlarmOwner(ci);
                        if(null!=owners){
                            for(int i=0;i<owners.length;i++){
                            	User user=userManager.getUser(owners[i]);
                            	if(user!=null){	                            	
	                            	if(null!=user.getName()){
	                            		ownersname.append(user.getName());
	                            	}else{
	                            		ownersname.append(owners[i]);
	                            	}
	                            	if(i<owners.length-1){
	                            		ownersname.append(";");
	                            	}	                            	
                            	}
                            }
                        }
                        else{
                        	ownersname.append("��");
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    out.print(ownersname.toString());
                %></td>
  <td style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;�¼���ID</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%
    out.print((ci.getWorkId()==null?"��δ�ɷ�������<input type='button' value='ǰת����' onclick=\"openModalDialog('/sysmgr/opt/ovsd/forwardOVSD.jsp?alarmId="+ci.getId()+"', 390, 450,null);\">":ci.getWorkId()));
     %></td>
  </tr>
  <%if (ci.getWorkId()!=null){ %>
  <tr>
    <td height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;�¼���״̬</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%=getOvsdIncStatus(ci.getWorkId())%></td>
  <td style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;������</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%=getOvsdIncAssignToPerson(ci.getWorkId())%></td>
  </tr>
  <%} %>
  <tr>
    <td height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;"  colspan=4><font color="white">&nbsp;&nbsp;�澯����</font></td>
  </tr>
  <tr>
    <td height=110 style="border-bottom:dashed #99bbe8 1px;" colspan=4 valign=top><%=ci.getContent() %></td>
  </tr>
</table>
</div>
 <script type="text/javascript">
alarmlistdiv.style.height=320;
</script>
<%
}
}else if (mode.equals("track")){
   TrackManager tmgr =  (TrackManager)Context.getContext().getAttribute("trackManager");
   List<TrackInfo> tracks = tmgr.getTracks(alarmId);
%>
<div width="100%" style="OVERFLOW: auto;" id="tracklistdiv">
<table width="98%" border=0>
<%if (istrack.equals("2")){ %>
   <tr>
    <td height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;" width=120><font color="white">&nbsp;&nbsp;���ټ�¼</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;" colspan=3 height=30><textarea id="trackctnt" style="width:100%;height:30"></textarea> </td>
  </tr>
  <tr>
    <td align=right colspan=4><button onclick="saveTrack('<%=alarmId %>')">����</button></td>
   
  </tr>
  <%} %>
  <%if (tracks!=null){ 
  if (tracks.size()==0){
    out.print(" <tr>");
    out.print(" <td style=\"border-bottom:dashed #99bbe8 1px;\" colspan=4><font color=\"red\">��δ�и��ټ�¼</font> </td>");
    out.print(" </tr>");
  }
  for (int i=0;i<tracks.size();i++){
  TrackInfo ti = tracks.get(i);
 
  %>
  <tr>
    <td height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;" width=120><font color="white">&nbsp;&nbsp;<font color="yellow"><b><%=(i+1) %>:</b></font>������</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%
    User ur = userManager.getUser(ti.getTracker());
    if (ur!=null) out.print(ur.getName());
     %></td>
  <td style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;����ʱ��</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;">&nbsp;<%=ChangeDateType.parseLongToStr(ti.getTrackdate(),datePattern) %></td>
  </tr>
  <tr>
    <td height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;" width=120><font color="white">&nbsp;&nbsp;���ټ�¼</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;" colspan=3 >&nbsp;<%=ti.getContent() %></td>
  </tr>
  <%
     out.print("<tr>    <td height=2 colspan=4><hr></td>  </tr>");
    }
    
  }else{  %>
  <tr>
    <td style="border-bottom:dashed #99bbe8 1px;" colspan=4 height=100><font color="red">��δ�и��ټ�¼</font> </td>
  </tr>
  <%} %>
  
  </table>
  </div>
  <%if  (tracks!=null){ 
    if (tracks.size()>3){
    %>
   <script type="text/javascript">
tracklistdiv.style.height=320;
//alert(tracklistdiv.outerHTML);
</script>
    <%}
  }%>
<%

}else if(mode.equals("phone")){
	ConcernManager cmgr = (ConcernManager)Context.getContext().getAttribute("concernManager");
	AlarmManager am = (AlarmManager)Context.getContext().getAttribute("alarmManager");
	AlarmFilterInfo filter1 = new AlarmFilterInfo();
	filter1.setAlarmIds(new String[]{alarmId});
	AlarmInfo[] ais = am.fecthAlarm(filter1);
	ConcernFilter filter = new ConcernFilter();
	filter.setIsshow(-1);
	filter.setAlarmId(alarmId);
	
	List<ConcernInfo> items = cmgr.findConcernAlarmByFilter(filter,0,100);
	AutoCallAlarmFilter filter2 = new AutoCallAlarmFilter();
	filter2.setId(alarmId);
	filter2.setIsshow(-1);
	List<AutoCallAlarmInfo> items2 = cmgr.findAutoCallAlarmInfo(filter2, 0, 1000);
	if (items2 != null && items2.size() > 0){
	  AutoCallAlarmInfo info = items2.get(0);
	   if (info !=null){
	%>
	<table width="100%" border="0"   height=20>
	 <tr>
	   <td >�Զ������</td>
	    </tr>
	   <tr><td >����ˣ�<%
	   User user = userManager.getUser(info.getCaller());
	   out.print((user==null?"":user.getName()));
	    %>&nbsp;&nbsp;�ֻ��ţ�<%=info.getPhone() %>&nbsp;&nbsp;״̬��<%switch(info.getStatus()){
	      case -1:{
	        out.print("<font color=red>���ʧ��</font>");
	        break;
	      }
	      case 0:{
	         out.print("�������");
	         break;
	      }
	      case 1:{
	         out.print("<font color=green>����ɹ�</font>");
	         break;
	      }
	    } %></td></tr>
   </table>
   <hr>
	<%}
	}
    if (items!=null&&items.size()>0){
     ConcernInfo ci = items.get(0); 
%>
<table width="100%" border="0"   height=20>
 <tr>
   <td >�ֶ���ע�����<%
	   if (items2==null || items2.size()==0){
		   if(ci.getStatus()==0){
		      out.print("<button onclick=\"this.disabled=true;alarmPhone('"+ci.getAlarmId()+"','"+(ais==null?"":(ais[0].getWorkId()==null?"":ais[0].getWorkId()))+"')\">��δ��ϵ</button>");
		   }else if(ci.getStatus()==2){
		      out.print("<button onclick=\"this.disabled=true;alarmPhone('"+ci.getAlarmId()+"','"+(ais==null?"":(ais[0].getWorkId()==null?"":ais[0].getWorkId()))+"')\"><font color='red'>��ϵʧ��</font></button>");
		   }else if(ci.getStatus()==1){
		      out.print("<font color='green'>��ϵ�ɹ�</font>");
		   }
	   }
   %></td>
    </tr>
   <tr><td ><hr></td></tr>
   <%
	   List<Call> calls = cmgr.getPhone(alarmId);
	   int cindex = 0;
	   if (calls!=null){
	      for(Call s:calls){
	          out.print("<tr><td >");
	          User phoner = userManager.getUser(s.getPhoner());
	          out.print(phoner.getName()+"��"+ChangeDateType.parseLongToStr(s.getCalltime(), datePattern)+(s.getStatus()==1?"<font color='green'>�ɹ�</font>":"<font color='red'>ʧ��</font>")+"����ϵ��"+s.getPhone());
	          out.print("</td></tr>");
	          if (cindex<(calls.size()-1)){
	            out.print("<tr><th colSpan=4 ><hr></th></tr>");
	          } 
	          cindex++;
	          
	      }
	     
	   }
    %>
 
</table>
<%}else out.print("�ǹ�ע�澯û����ϵ");
}else if (mode.equals("concernhis")){
   ConcernManager cmgr = (ConcernManager)Context.getContext().getAttribute("concernManager");
   List<ConcernInfo> items = cmgr.findHisConrecnById(alarmId);
   if (items!=null){
    %>
    <div width="100%" style="OVERFLOW: auto;" id="concrenlistdiv">
 <table width="98%" border="0"  >
 <%for (int i=0;i<items.size();i++){
    ConcernInfo ci = items.get(i);
  %>
  <tr>
    <td width=100 height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;<font color="yellow"><b><%=(i+1) %>:</b></font>��עʱ��</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;" colspan=3>&nbsp;<%=ChangeDateType.parseLongToStr(ci.getCtime(),datePattern) %></td>
  </tr>
  <tr>
    <td width=100 height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;��עԭ��</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;"  width=200>&nbsp;<%=(ci.getReason()==null?"���ݹ�ע����ϵͳ�Զ����롣":ci.getReason()) %></td>
    <td width=100 height=26 style="border:dashed #99bbe8 1px;background-color:#99bbe8;"><font color="white">&nbsp;&nbsp;ȡ����עԭ��</font></td>
    <td style="border-bottom:dashed #99bbe8 1px;" >&nbsp;<%=(ci.getMemo()==null?(ci.getIsshow()==0?"����ȡ����ע����ϵͳ�Զ�ȡ��":"&nbsp;"):ci.getMemo())%></td>
    
  </tr>
  <%} %>
</table>
</div>
    <%  
   }%>
    <%if  (items!=null){ 
    if (items.size()>=3){
    %>
   <script type="text/javascript">
concrenlistdiv.style.height=320;
//alert(tracklistdiv.outerHTML);
</script>
    <%}
  }
} else if ("kbm".equals( mode) ) {
	AlarmManager am = (AlarmManager)Context.getContext().getAttribute("alarmManager");
    AlarmFilterInfo filter = new AlarmFilterInfo();
    ConfigManager cfmg = (ConfigManager)Context.getContext().getAttribute("configManager");
    filter.setAlarmIds(new String[]{alarmId});
    AlarmInfo[] ais = am.fecthAlarm(filter);
    AlarmInfo ci = null;
	if (ais!=null){ ci = ais[0]; }
	String keyword="";
	MoInfo mo = cfmg.findMoById(ci.getMoId());
	
	if(mo != null){
		if(ci.getSource().equals("threshold-checker")){//��ֵ�澯
			keyword=mo.getType().getCaption()+" "+ci.getCreatorId();
		} else {
			keyword=mo.getType().getCaption()+" "+ci.getSource()+" "+ci.getCode();
		}
	}
	keyword = URLEncoder.encode(keyword, "utf-8");
	%>
	<iframe src="http://bomc.js.cmcc/kbm2/search?keyword=<%=keyword%>&flowId=<%=ci.getId()%>&flag=1&filter=type_info" width="100%" height="420"></iframe>
	<%
}
%>
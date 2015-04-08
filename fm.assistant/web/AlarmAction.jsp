<%@ page language="java" contentType="text/html; charset=GBK"%>
<%@ page import="com.linkage.toptea.sysmgr.util.*"%>
<%@ page import="com.linkage.toptea.context.*"%>
<%@ page import="java.util.*"%>
<%@ page import="net.sf.json.*" %>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.track.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.concern.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.util.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.*"%>
<%@ page import="com.linkage.toptea.sysmgr.cm.*"%>
<%@ page import="com.linkage.toptea.auc.*" %>
<%@ page import="com.linkage.toptea.sysmgr.ovsd.OvsdInteraction"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.responsible.*"%>

<%
/**
*
*功能：处理关注告警的页面
*作者：潘华
*时间：20110211
*/
String mode  = request.getParameter("mode");
String datePattern = "yyyy-MM-dd HH:mm:ss";
String path = request.getParameter("path");
//关注告警查询列表
if (mode.equals("concern")){
  
   ConcernManager cmgr = (ConcernManager)Context.getContext().getAttribute("concernManager");
   ConfigManager cfmg = (ConfigManager)Context.getContext().getAttribute("configManager");
  
   ConcernFilter filter = new ConcernFilter();
   filter.setContent(request.getParameter("content"));
   filter.setGrade(Integer.parseInt(request.getParameter("grade")));
   filter.setStarttime(request.getParameter("sdate"));
   filter.setEndtime(request.getParameter("edate"));
   //filter.setIsshow(-1);
   filter.setPath(path);
   int start = 0;
   int limit = 20;
   start = Integer.parseInt(request.getParameter("start")==null?"0":request.getParameter("start"));
   limit = Integer.parseInt(request.getParameter("limit")==null?"20":request.getParameter("limit"));
   int count = cmgr.getConcernCount(filter);
   List<ConcernInfo> items = cmgr.findConcernAlarmByFilter(filter,start,limit);
   out.print("{count:"+count+",data:[");
   if (items!=null){
     for (int i=0;i<items.size();i++){
         ConcernInfo ci = items.get(i);
        if(ci!=null && ci.getMoId()!=null)  ci.setPathname(cfmg.getNamingPath(ci.getMoId()));
         if (i>0) out.print(",");
          String worker = "无";
         try{
          if(ci.getAlarm().getWorkId()!=null && !ci.getAlarm().getWorkId().equals("")){
           OvsdInteraction oi = (OvsdInteraction)Context.getContext().getAttribute("ovsdInteraction");
            String name = oi.getIncidentAssignToPerson(ci.getAlarm().getWorkId());
             if( null == name || "".equals( name ) ) {
             	worker = "无";
             }else{
                worker = ci.getAlarm().getWorkId()+name ;
             }
          }else worker = "无";
          }catch(Exception e){
            
          }
          try{
          JSONObject jo = new JSONObject();
           jo.put("id",ci.getAlarmId());
           jo.put("path",ci.getPathname());
           jo.put("grad",ci.getAlarm().getCurrentGrade());
           jo.put("fdate",ChangeDateType.parseLongToStr(ci.getAlarm().getFirstOccurTime(),datePattern));
           jo.put("ldate",ChangeDateType.parseLongToStr(ci.getAlarm().getLastOccurTime(),datePattern));
           jo.put("count",ci.getAlarm().getCount());
           jo.put("content",ci.getAlarm().getContent());
           jo.put("workid",worker);
           jo.put("oper",ci.getAlarmId()+"|||"+ci.getStatus()+"|||"+(ci.getAlarm().getWorkId()==null?"":ci.getAlarm().getWorkId()));
           jo.put("ph",ci.getAlarm().getPath());
           jo.put("failnum",ci.getFailNum());
           out.print(jo.toString());
          // System.out.println(jo.toString());
         }catch(Exception e){
           e.printStackTrace();
         }
     }
   }
   out.print("]}");
 //批量电话
 
}else if (mode.equals("batchphone")){   
    String[] alarmids = null;
    String s =  request.getParameter("ids");
    if (s!=null){
      alarmids = s.split(",");
       User user = (User)Context.getContext().getAttribute(Context.USER); 
      String id = request.getParameter("id");
      ConcernManager cmgr = (ConcernManager)Context.getContext().getAttribute("concernManager");
      String str =  cmgr.batchPhone(alarmids);
      if (str==null||str.equals("")){
        out.print("{success:true,msg:''}");
      }else{
         out.print("{success:false,msg:'"+str+"'}");
      }
      
    }else out.print("{success:false,msg:''}");
 //添加联系电话
/*
}else if (mode.equals("phone")){
      User user = (User)Context.getContext().getAttribute(Context.USER); 
      String id = request.getParameter("id");
      ConcernManager cmgr = (ConcernManager)Context.getContext().getAttribute("concernManager");
      String str =  cmgr.addPhone(id,user.getId());
      if (str==null||str.equals("")){
        out.print("{success:true,msg:''}");
      }else{
         out.print("{success:false,msg:'"+str+"'}");
      }
 */
}else if (mode.equals("cancel")){
   String alarmIds = request.getParameter("alarmIds");
   String memo = request.getParameter("memo");
    ConcernManager cmgr = (ConcernManager)Context.getContext().getAttribute("concernManager");
   try{
      if (alarmIds!=null)    cmgr.removeConcern(alarmIds.split(","),memo);
      out.print("{success:true,msg:''}");
   }catch(Exception e){
      out.print("{success:false,msg:'"+e.getMessage()+"'}");
   }
 //添加关注告警
}else if (mode.equals("addconcern")){
   String alarmId = request.getParameter("alarmId");
   String memo = request.getParameter("memo");
   ConcernManager cmgr = (ConcernManager)Context.getContext().getAttribute("concernManager");
   try{
      cmgr.addConcernAlarm(alarmId,memo);
      out.print("{success:true,msg:''}");
   } catch(Exception e){
      out.print("{success:false,msg:'"+e.getMessage()+"'}");
   }
//修改配置信息
}else if (mode.equals("config")){
   String cg = request.getParameter("CONCRENGRADE");
   String ct = request.getParameter("CONCRENTIME");
   String tg = request.getParameter("TRACKGRADE");
   String tt = request.getParameter("TRACKTIME");
   String tnt = request.getParameter("TRACKNEXTTIME");
   String pf = request.getParameter("PATHFILTER");
   String tp = request.getParameter("TRACKPATHFILTER");

   ConcernManager cmgr = (ConcernManager)Context.getContext().getAttribute("concernManager");
   try{
		cmgr.setConfig((cg==null?3:Integer.parseInt(cg)),(ct==null?2:Integer.parseInt(ct)),
			(tg==null?3:Integer.parseInt(tg)),(tt==null?2:Integer.parseInt(tt)),(tnt==null?4:Integer.parseInt(tnt)),pf,tp);
		out.print("{success:true,msg:''}");
   }catch(Exception e){
		out.print("{success:false,msg:'"+e.getMessage()+"'}");
   }
}else if (mode.equals("tracklist")){
    TrackManager tmgr = (TrackManager)Context.getContext().getAttribute("trackManager");
    ConfigManager cfmg = (ConfigManager)Context.getContext().getAttribute("configManager");
    int start = 0;
    int limit = 20;
    start = Integer.parseInt(request.getParameter("start")==null?"0":request.getParameter("start"));
    limit = Integer.parseInt(request.getParameter("limit")==null?"20":request.getParameter("limit"));
    int count = tmgr.getTrackAlarmCount();
    List<AlarmInfo> items =  tmgr.getTrackAlarms(start,limit);
    out.print("{count:"+count+",data:[");
   if (items!=null){
     for (int i=0;i<items.size();i++){
         AlarmInfo ci = items.get(i);
         if (i>0) out.print(",");
          String worker = "无";
         try{
          if(ci.getWorkId()!=null && !ci.getWorkId().equals("")){
           OvsdInteraction oi = (OvsdInteraction)Context.getContext().getAttribute("ovsdInteraction");
            String name = oi.getIncidentAssignToPerson(ci.getWorkId());
             if( null == name || "".equals( name ) ) {
             	worker = "无";
             }else{
                worker = ci.getWorkId()+name ;
             }
          }
          }catch(Exception e){
            
          }
           JSONObject jo = new JSONObject();
           jo.put("id",ci.getId());
           jo.put("path",cfmg.getNamingPath(ci.getPath()));
           jo.put("grad",ci.getCurrentGrade());
           jo.put("fdate",ChangeDateType.parseLongToStr(ci.getFirstOccurTime(),datePattern));
           jo.put("ldate",ChangeDateType.parseLongToStr(ci.getLastOccurTime(),datePattern));
           jo.put("count",ci.getCount());
           jo.put("content",ci.getContent());
           jo.put("workid",worker);
           jo.put("oper","");
             
           out.print(jo.toString());
       
     }
   }
   out.print("]}");

}else if (mode.equals("tracknext")){
   TrackManager tmgr = (TrackManager)Context.getContext().getAttribute("trackManager");
   User user = (User)Context.getContext().getAttribute(Context.USER); 
   String ids = request.getParameter("id");
   String err = "";
   String[]  items = ids.split(",");
   for (int i=0;i<items.length;i++){
	   TrackInfo ti = new TrackInfo();
	   ti.setAlarmId(items[i]);
	   ti.setContent(request.getParameter("memo"));
	   ti.setTracker(user.getId());
	   ti.setId(new UID().toString());
	   ti.setType(Integer.parseInt((request.getParameter("type")==null?"1":request.getParameter("type"))));
	   try{
	      tmgr.addTrack(ti);
	   }catch(Exception e){
	      err = e.getMessage();
	   }
   }
   if (err.equals("")){
     out.print("{success:true,msg:''}");
   }else{
     out.print("{success:false,msg:'"+err+"'}");
   }
// add by huangzh
}else if (mode.equals("cancelTrack")){
	String ids = request.getParameter("id");
	TrackManager tmgr = (TrackManager)Context.getContext().getAttribute("trackManager");
	TrackInfo trackInfo = null;
	List<TrackInfo> trackList = new ArrayList<TrackInfo>();
	try {
	    for (String id : ids.split(",")) {
		    trackInfo = new TrackInfo();
		    trackInfo.setAlarmId(id);
		    trackList.add(trackInfo);
	    }
		tmgr.cancelTrack(trackList);
	    out.print("{success:true,msg:''}");
	} catch (Exception e) {
		out.print("{success:false,msg:'"+e.getMessage()+"'}");
	}    
} else if (mode.equals("ignore")){
   AlarmManager alarmManager  = (AlarmManager)Context.getContext().getAttribute("alarmManager");
   ConfigManager cfmg = (ConfigManager)Context.getContext().getAttribute("configManager");
   int start = 0;
   int limit = 20;
   AlarmInfo[] alarmInfos = {};
   start = Integer.parseInt(request.getParameter("start")==null?"0":request.getParameter("start"));
   limit = Integer.parseInt(request.getParameter("limit")==null?"20":request.getParameter("limit"));
   AlarmFilterInfo filter = new AlarmFilterInfo();
   filter.setIgnore(true);
   
   if (request.getParameter("content")!=null&&!"".equals(request.getParameter("content")))  filter.setAlarmContent(request.getParameter("content"));
   filter.setGrade(Integer.parseInt((request.getParameter("grade")==null?"1":request.getParameter("grade"))));
  
   String type = request.getParameter("type");
   filter.setPaths(new String[]{path});
   
   if(type.equals("2")){
      
      if (request.getParameter("sdate")!=null&&!"".equals(request.getParameter("sdate"))){
      	long st = ChangeDateType.parseStrToLong(request.getParameter("sdate"),datePattern);
      	filter.setStartTime(st);
      }
      if (request.getParameter("edate")!=null&&!"".equals(request.getParameter("edate"))){
      	long et = ChangeDateType.parseStrToLong(request.getParameter("edate"),datePattern);
      	filter.setEndTime(et);
      }
      
   }    
  

   long  count = alarmManager.countAlarm(filter);  
   filter.setPosition(start);   
   filter.setLength(limit);
   alarmInfos = alarmManager.fecthAlarm(filter); 
   int len = alarmInfos.length;
   out.print("{count:"+count+",data:[");
   for (int i=0;i<len;i++){
       AlarmInfo ci = alarmInfos[i];
       if (i>0) out.print(",");
       
       String pth = cfmg.getNamingPath(ci.getPath());
        JSONObject jo = new JSONObject();
           jo.put("id",ci.getId());
           jo.put("path",(pth==null?ci.getPath():pth));
           jo.put("grad",ci.getCurrentGrade());
           jo.put("fdate",ChangeDateType.parseLongToStr(ci.getFirstOccurTime(),datePattern));
           jo.put("ldate",ChangeDateType.parseLongToStr(ci.getLastOccurTime(),datePattern));
           jo.put("count",ci.getCount());
           jo.put("content",ci.getContent());
            jo.put("oper","");
           out.print(jo.toString());
   }
   out.print("]}");
}else if (mode.equals("callphone")){
	String phone = request.getParameter("phone");
	String context = request.getParameter("context");
	String personcall = request.getParameter("personcall");
	String title = request.getParameter("title");
	if (personcall == null || personcall.equals("false")) personcall="0";
	else personcall ="1";
	String workid = request.getParameter("workid");
	String alarmId = request.getParameter("alarmId");
	ConcernManager cmgr = (ConcernManager)Context.getContext().getAttribute("concernManager");
	User user = (User)Context.getContext().getAttribute(Context.USER); 
	try{
		cmgr.callPhone(phone,alarmId,workid,context,user.getId(),personcall,title);
	    out.print("{success:true,msg:''}");
	}catch(Exception e){
	    JSONObject jo = new JSONObject();
	    jo.put("success","false");
	    jo.put("msg",e.getMessage());
	    out.print(jo.toString());
	}
	
   //out.print("{success:false,msg:'sss'}");
}else if (mode.equals("userlist")){
	//获取外呼人员的列表
    ConcernManager cmgr = (ConcernManager)Context.getContext().getAttribute("concernManager");
    
    String searchtxt = request.getParameter("searchtxt");
    int start = Integer.parseInt(request.getParameter("start")==null?"0":request.getParameter("start"));
    int limit = Integer.parseInt(request.getParameter("limit")==null?"20":request.getParameter("limit"));
    List<User> items =  cmgr.getCallUserList(searchtxt,start,limit);
    int count = cmgr.getCallUserCount(searchtxt);
    out.print("{count:"+count+",data:[");
    for (int i=0;i<items.size();i++){
       User user = items.get(i);
       if (i>0) out.print(",");
        JSONObject jo = new JSONObject();
        jo.put("userid",user.getId());
        jo.put("name",user.getName());
        jo.put("phone",user.getMobile());
        out.print(jo.toString());
    }
    out.print("]}");
}else if (mode.equals("batchcallphone")){
	String phone = request.getParameter("phone");
	String context = request.getParameter("context");
	String personcall = request.getParameter("personcall");
	String title = request.getParameter("title");
	personcall ="1";
	String alarmIds = request.getParameter("alarmIds");
	ConcernManager cmgr = (ConcernManager)Context.getContext().getAttribute("concernManager");
	User user = (User)Context.getContext().getAttribute(Context.USER); 
	try{
		cmgr.callPhone(phone,alarmIds.split(","),context,user.getId(),personcall,title);
	    out.print("{success:true,msg:''}");
	}catch(Exception e){
	     e.printStackTrace();
	    JSONObject jo = new JSONObject();
	    jo.put("success","false");
	    jo.put("msg",e.getMessage());
	    out.print(jo.toString());
	}
	
}else if (mode.equals("responseible")){
	%>
	<script type="text/javascript">
	function chooseUser(id,name,mobile){
		var obj = Ext.getCmp('phone');
		if (obj) obj.setValue(mobile);
	}
	</script>
	<%
     String alarmId = request.getParameter("alarmId");
     AlarmManager alarmManager  = (AlarmManager)Context.getContext().getAttribute("alarmManager");
     ConfigManager cm = (ConfigManager)Context.getContext().getAttribute("configManager");
     ResponsibleManager rm = (ResponsibleManager)Context.getContext().getAttribute("responsibleManager");
     UserManager userManager = (UserManager)Context.getContext().getAttribute("userManager");
     AlarmInfo alarm = null;
     ResponsibleInfo info = null;
     AlarmFilterInfo filter = new AlarmFilterInfo();
     filter.setAlarmIds(new String[]{alarmId});
     AlarmInfo[] ais = alarmManager.fecthAlarm(filter);
     if (ais!=null && ais.length>0) alarm = ais[0];
     String moid = (alarm!=null?alarm.getMoId():"");
     if (!moid.equals("")){
         MoInfo mo = cm.findMoById(moid);
         if (mo!=null) info = rm.getResponsibleInfo(mo.getId());
     }
     if (info!=null){
       if (info.getResponsible1()!=null||info.getResponsible2()!=null) {
          out.print("&nbsp;平台类:");
          if (info.getResponsible1()!=null){
             User user = userManager.getUser(info.getResponsible1());
             if (user!=null){
               out.print("<a href='#' onclick=\"chooseUser('"+user.getId()+"','"+user.getName()+"','"+user.getMobile()+"');return false;\">"+user.getName()+"</a>&nbsp;&nbsp;");
             }
          }
          if (info.getResponsible2()!=null){
             User user = userManager.getUser(info.getResponsible2());
             if (user!=null){
            	 out.print("<a href='#' onclick=\"chooseUser('"+user.getId()+"','"+user.getName()+"','"+user.getMobile()+"');return false;\">"+user.getName()+"</a>&nbsp;&nbsp;");
             }
          }
          out.print(";");
       }
       if (info.getResponsible3()!=null||info.getResponsible4()!=null) {
    	   out.print("&nbsp;&nbsp;业务类:");
    	   if (info.getResponsible3()!=null){
               User user = userManager.getUser(info.getResponsible3());
               if (user!=null){
            	   out.print("<a href='#' onclick=\"chooseUser('"+user.getId()+"','"+user.getName()+"','"+user.getMobile()+"');return false;\">"+user.getName()+"</a>&nbsp;&nbsp;");
               }
            }
            if (info.getResponsible4()!=null){
               User user = userManager.getUser(info.getResponsible4());
               if (user!=null){
            	   out.print("<a href='#' onclick=\"chooseUser('"+user.getId()+"','"+user.getName()+"','"+user.getMobile()+"');return false;\">"+user.getName()+"</a>&nbsp;&nbsp;");
               }
            }
            out.print(";");
       }
       if (info.getResponsible5()!=null||info.getResponsible6()!=null) {
    	   out.print("&nbsp;&nbsp;备用类:");
    	   if (info.getResponsible5()!=null){
               User user = userManager.getUser(info.getResponsible5());
               if (user!=null){
            	   out.print("<a href='#' onclick=\"chooseUser('"+user.getId()+"','"+user.getName()+"','"+user.getMobile()+"');return false;\">"+user.getName()+"</a>&nbsp;&nbsp;");
               }
            }
            if (info.getResponsible6()!=null){
               User user = userManager.getUser(info.getResponsible6());
               if (user!=null){
            	   out.print("<a href='#' onclick=\"chooseUser('"+user.getId()+"','"+user.getName()+"','"+user.getMobile()+"');return false;\">"+user.getName()+"</a>&nbsp;&nbsp;");
               }
            }
    	   
       }
     }
      
      
}

%>


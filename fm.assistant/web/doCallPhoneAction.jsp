<%@ page language="java" contentType="text/html; charset=GB2312"
	pageEncoding="GB2312"%>
<%@ page import="net.sf.json.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.concern.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.assistant.calluser.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.*,com.linkage.toptea.sysmgr.fm.util.*"%>
<%@ page import="com.linkage.toptea.sysmgr.fm.responsible.*"%>
<%@ page import="com.linkage.toptea.sysmgr.util.*"%>
<%@ page import="com.linkage.toptea.context.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="com.linkage.toptea.auc.*"%>
<%@ page import="com.linkage.toptea.sysmgr.cm.*"%>
<%@ page import="com.linkage.toptea.sysmgr.ovsd.OvsdInteraction"%>
<%
	String mode = request.getParameter("mode");

	String datePattern = "yyyy-MM-dd HH:mm:ss";
	ConcernManager cmgr = (ConcernManager) Context.getContext().getAttribute("concernManager");
	FindCallAlarm  fca  = (FindCallAlarm) Context.getContext().getAttribute("findCallAlarm");
	CallUserManager  cum  = (CallUserManager) Context.getContext().getAttribute("callUserManager");
	PhoneFilterManager  pfm  = (PhoneFilterManager) Context.getContext().getAttribute("phoneFilterManager");
	//过滤设置-列表
	if (mode.equals("configlist")) {
		try {
			ConfigManager cfmg = (ConfigManager)Context.getContext().getAttribute("configManager");
			PhoneFilter filter = new PhoneFilter();
			filter.setIsuse(-100);
			int start = Integer
					.parseInt(request.getParameter("start") == null ? "0"
							: request.getParameter("start"));
			int limit = Integer
					.parseInt(request.getParameter("limit") == null ? "20"
							: request.getParameter("limit"));
			UserManager userManager = (UserManager) Context.getContext()
					.getAttribute("userManager");
			ResponsibleManager rm = (ResponsibleManager) Context
					.getContext().getAttribute("responsibleManager");
			int count = pfm.getPhoneFilterCount(filter);
			List<PhoneFilterInfo> items = pfm.findFilter(filter, start, limit);
			
			out.print("{count:" + count + ",data:[");
			if (items != null) {
				for (int i = 0; i < items.size(); i++) {
					if (i > 0) out.print(",");
					PhoneFilterInfo info = items.get(i);
					JSONObject jo = new JSONObject();
					jo.put("moid", info.getMoId());
					jo.put("pathName", cfmg.getNamingPathById(info.getMoId()));
					jo.put("ischild", info.getIschild());
					jo.put("timelen", info.getTimelen()); //0 0 9 * * ?
					jo.put("key", info.getKey());
					jo.put("isuse", info.getIsuse());
					jo.put("forPara", info.getForPara());
					// todo huangzh
					//ResponsibleInfo ri = rm.getResponsibleInfo(info.getMoId());
					ResponsibleInfo ri = rm.getResponsibleInfo("22.44");
					String reper = "";
					
					if (ri != null) {
						reper = (ri.getResponsible1() == null ? "" : ri.getResponsible1());
						if (!reper.equals("")) {
							User user = userManager.getUser(reper);
							if (null != user ) reper = user.getName();
						}
						String reper2 = (ri.getResponsible2() == null ? ""
								: ri.getResponsible2());
						if (!reper2.equals("")) {
							User user = userManager.getUser(reper2);
							if (!reper.equals(""))
								reper += ";";
							reper += user.getName();
						}

						reper2 = (ri.getResponsible3() == null ? "" : ri
								.getResponsible3());
						if (!reper2.equals("")) {
							User user = userManager.getUser(reper2);
							if (!reper.equals(""))
								reper += ";";
							reper += user.getName();
						}

						reper2 = (ri.getResponsible4() == null ? "" : ri
								.getResponsible4());
						if (!reper2.equals("")) {
							User user = userManager.getUser(reper2);
							if (!reper.equals(""))
								reper += ";";
							reper += user.getName();
						}
					}
					jo.put("reper", reper);
					out.print(jo.toString());
				}
			}

			out.print("]}");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//责任人休假设置
	} else if (mode.equals("reperconfig")) {
		UserManager userManager = (UserManager) Context.getContext()
		.getAttribute("userManager");
		List<RepVacation> items = cmgr.findRepVacation(0,1000);
		int count = (items==null?0:items.size()) ;
		
		out.print("{count:"+count+",data:[");
		if (items!=null){
		   for (int i = 0; i < items.size(); i++) {
				if (i > 0)
					out.print(",");
					RepVacation rv  = items.get(i);
					JSONObject jo = new JSONObject();
					User ur = userManager.getUser(rv.getUserId());
					jo.put("userId",rv.getUserId());
					jo.put("userName",(ur==null?"":ur.getName()));
					jo.put("startTime",rv.getStartTime());
					jo.put("endTime",rv.getEndTime());
					jo.put("stat", "");
					jo.put("isuse", rv.getIsUse());
					out.print(jo.toString());
					
		    }
		
		}
		out.print("]}");
	} else if (mode.equals("calllist")) {
		
		UserManager userManager = (UserManager) Context.getContext().getAttribute("userManager");
	    ConfigManager cfmg = (ConfigManager)Context.getContext().getAttribute("configManager");
	    OvsdInteraction oi = (OvsdInteraction)Context.getContext().getAttribute("ovsdInteraction");
	    String selectTxt  = request.getParameter("selectTxt");
	    String isShow = request.getParameter("isShow");
	    if (isShow==null) isShow="1";
	    
	    String stime = request.getParameter("stime");
	    if ( null != stime ) { stime= stime.replace('T',' '); }
	    String etime = request.getParameter("etime");
	    if ( null != etime ) { etime= etime.replace('T',' '); }
	    String path = request.getParameter("path");

	    MoInfo mo = cfmg.findMoById(path);  
		if ( mo != null ) { path = mo.getDotPath(); }

	    String grade = request.getParameter("grade");
	    if ( grade == null || "".equals(grade) ) { grade = "0"; }
	    String caller = request.getParameter("caller");
	    String code = request.getParameter("code");
	    String source = request.getParameter("source");
		
	    AutoCallAlarmFilter filter = new AutoCallAlarmFilter();
	    filter.setIsshow(Integer.parseInt(isShow));
	    filter.setContent(selectTxt);
	    filter.setStime(stime);
	    filter.setEtime(etime);
	    filter.setPath(path);
	    filter.setCurrentGrade( Integer.parseInt(grade) );
	    filter.setCaller(caller);
	    filter.setCode(code);
	    filter.setSource(source);
	    
	    // 自动外呼告警数量
	    int count = fca.findAutoCallAlarmCount(filter);
	    int start = Integer
				.parseInt(request.getParameter("start") == null ? "0"
						: request.getParameter("start"));
		int limit = Integer
				.parseInt(request.getParameter("limit") == null ? "20"
						: request.getParameter("limit"));
		// 自动外呼告警列表
	    List<AutoCallAlarmInfo> items = fca.findAutoCallAlarmInfo(filter, start, limit);
		out.print("{count:"+count+",data:[");
		for (int i=0;i<items.size();i++){
		   if (i>0) out.print(",");
			JSONObject jo = new JSONObject();
			AutoCallAlarmInfo info = items.get(i);
			
			jo.put("id", info.getId());
			jo.put("callID", info.getCallid());
			jo.put("ctime", ChangeDateType.parseLongToStr(info.getCallTime().getTime(),datePattern));
			jo.put("path", cfmg.getNamingPath(info.getMoId()));
			jo.put("grad", info.getCurrentGrade());
			jo.put("fdate", ChangeDateType.parseLongToStr(info.getFirstOccurTime(),datePattern));
			jo.put("ldate", ChangeDateType.parseLongToStr(info.getLastOccurTime(),datePattern));
			jo.put("count", info.getCount());
			jo.put("content", info.getContent());
			String worker = "";
			 try{
		          if(info.getWorkId()!=null && !info.getWorkId().equals("")){
		            
		            String name = oi.getIncidentAssignToPerson(info.getWorkId());
		             if( null == name || "".equals( name ) ) {
		             	worker = "无";
		             }else{
		                worker = info.getWorkId()+name ;
		             }
		          }else worker = "无";
	          }catch(Exception e){
	            
	          }
			jo.put("workid", worker);
			jo.put("oper", "a");
			jo.put("ph", "/主机");
			User reper = userManager.getUser(info.getCaller());
			jo.put("caller", (reper==null?"":reper.getName()));
			jo.put("stat", info.getStatus());
			jo.put("num", info.getNum()-1);
			out.print(jo.toString());
		}
		out.print("]}");

	} else if ( "doReport".equals(mode) ) {
	    String selectTxt  = request.getParameter("selectTxt");
	    String isShow = request.getParameter("isShow");
	    if (isShow==null) isShow="1";
	    
	    String stime = request.getParameter("stime");
	    if ( null != stime ) { stime= stime.replace('T',' '); }
	    String etime = request.getParameter("etime");
	    if ( null != etime ) { etime= etime.replace('T',' '); }
	    String path = request.getParameter("path");

	    String grade = request.getParameter("grade");
	    if ( grade == null || "".equals(grade) ) { grade = "0"; }
	    String caller = request.getParameter("caller");
	    String code = request.getParameter("code");
	    String source = request.getParameter("source");
		
	    AutoCallAlarmFilter filter = new AutoCallAlarmFilter();
	    filter.setIsshow(Integer.parseInt(isShow));
	    filter.setContent(selectTxt);
	    filter.setStime(stime);
	    filter.setEtime(etime);
	    filter.setPath(path);
	    filter.setCurrentGrade( Integer.parseInt(grade) );
	    filter.setCaller(caller);
	    filter.setCode(code);
	    filter.setSource(source);
	    
		// 生成报表
	    String fimeName = fca.report(filter);
		
		String FS = System.getProperty("file.separator");
		String TOMCAT_HOME = System.getProperty("catalina.home");
		String TEMP_PATH = TOMCAT_HOME + FS + "temp" + FS;
		File tempDir = new File(TEMP_PATH + fimeName);
		
		InputStream in = new FileInputStream(tempDir);
		
		OutputStream os = response.getOutputStream();
		response.addHeader("Content-Disposition", "attachment;filename="
				+ new String(tempDir.getName().getBytes("UTF-8"),"iso-8859-1"));
		response.addHeader("Content-Length", tempDir.length() + "");
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/octet-stream");
		int data = 0;
		while ((data = in.read()) != -1) {
			os.write(data);
		}
		os.close();
		in.close();
		out.clear();
		out = pageContext.pushBody();
		
		tempDir.delete();

	} else if (mode.equals("checkpath")) {
		String moId = request.getParameter("moid");
		PhoneFilter filter = new PhoneFilter();
		filter.setMoId(moId);
		int count = pfm.getPhoneFilterCount(filter);
		if (count > 0) {
			out.println("{exist:true}");
		} else
			out.println("{exist:false}");
	//保存过滤器
	} else if (mode.equals("saveConfig")) {
		String xml = request.getParameter("xml");
		try {
			pfm.updateFilters(xml);
			out.print("{success:true,msg:''}");
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("success", "false");
			jo.put("msg", e.getMessage());
			out.print(jo.toString());
		}
	//一键暂停和启动
	}else if (mode.equals("keyPause")){
	   String isStop =  request.getParameter("isStop");
	   if (isStop!=null){
		  try{
		      if (isStop.equals("true")){
		    	  pfm.pauseFilters();
		      }else{
		    	  pfm.startFilters();
		      }
		      out.print("{success:true,msg:''}");
	      } catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("success", "false");
			jo.put("msg", e.getMessage());
			out.print(jo.toString());
		   }
	   }
	//删除过滤器
	}else if (mode.equals("removeFilters")){
	   String moidStr = request.getParameter("moids");
	   System.out.println("moidStr = " + moidStr);
	   if (moidStr == null) moidStr = "";
	   String[] moIds = moidStr.split(",");
	   if (!moidStr.equals("")  && moIds.length >0){
		   pfm.removeFilters(moIds);
			 out.print("{success:true,msg:''}");	
	   }else{
	        JSONObject jo = new JSONObject();
			jo.put("success", "false");
			jo.put("msg","没有需要删除的过滤器");
			out.print(jo.toString());
	   }
	//保存责任休假
	}else if (mode.equals("saveRepVacation")){
	   String xml = request.getParameter("xml");
		try {
			cmgr.updateRepVacation(xml);
			out.print("{success:true,msg:''}");
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("success", "false");
			jo.put("msg", e.getMessage());
			out.print(jo.toString());
		}
	//获取责任人列表
	}else if (mode.equals("replist")){
	    
         Map<String,String> items = cmgr.getAllReper(RepVacation.RepVacation_NOCHOOSE);
         out.print("{datalist:[");
         int count =0 ;
         if (items != null){
	         Iterator<String> it = items.keySet().iterator();
	         while(it.hasNext()){
	           String key  = it.next();
	           String value = items.get(key);
	           if (count>0) out.print(",");
	           out.print("{text:'"+value+"',value:'"+key+"'}");
	           count++;
	         }
         }
         out.print("]}");
		
	//删除责任人休假			
	}else if (mode.equals("removeRepVacation")){
	   String urs = request.getParameter("users");
	   String[] users = urs.split(",");
	   try {
			cmgr.removeRepVacation(users);
			out.print("{success:true,msg:''}");
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("success", "false");
			jo.put("msg", e.getMessage());
			out.print(jo.toString());
		}
	//更新外呼参数
	}else if (mode.equals("updatePhoneConfig")){
	    String FAIL_NUM = request.getParameter("FAIL_NUM");
	    String ALARM_NUM = request.getParameter("ALARM_NUM");
	    String ALARM_GRADE = request.getParameter("ALARM_GRADE");
		String callCount = request.getParameter("callCount");
		String time = request.getParameter("time");
		String timeRun = request.getParameter("time_run");
		// 外呼次数
		String callTime = request.getParameter("callTime");

		if ( null == callCount || "".equals(callCount) ) { callCount = "200"; }
	    Map<String,String> map = new HashMap<String,String>();
	    try {
	        map.put("FAIL_NUM", FAIL_NUM);
	        map.put("ALARM_NUM", ALARM_NUM);
	        map.put("ALARM_GRADE", ALARM_GRADE);
			map.put("TIME", time);
			map.put("TIMERUN", timeRun);

			map.put("CALL_TIME", callTime);
		
			cmgr.updatePhoneConfig(map);
			cmgr.resetCallCount( Integer.parseInt(callCount) );
			out.print("{success:true,msg:''}");
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("success", "false");
			jo.put("msg", e.getMessage());
			out.print(jo.toString());
		}
	//获取配置数据
	}else if (mode.equals("loadPhoneConfig")){
		String callTime = "'" + PhoneConfigManager.CALL_TIME;
		for ( int i = 0; i < PhoneConfigManager.CALL_TIME - 1; i++ ) {
			if ( i == 0 ) { callTime +=  ":"; }
			callTime += PhoneConfigManager.CALL_TIME_INTERVEL[i];
			if ( i != PhoneConfigManager.CALL_TIME - 2 ) { callTime +=  ","; }
		}
		callTime += "'";
		
	   out.print("{FAIL_NUM:" + PhoneConfigManager.FAIL_NUM
	    + ",ALARM_NUM:" + PhoneConfigManager.ALARM_NUM 
		+ ",TIMERUN:" + PhoneConfigManager.TIME_RUN 
		+ ",TIME:" + PhoneConfigManager.TIME
		+ ",CALL_TIME:" + callTime
		+ ",ALARM_GRADE:'" + PhoneConfigManager.ALARM_GRADE
		+"'}");
	}else if (mode.equals("hideCallPhone")){
	   String alarmIds = request.getParameter("alarmIds");
	    try {
	        
		    cmgr.hideAutoCallAlarmInfos(alarmIds.split(","));
			out.print("{success:true,msg:''}");
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("success", "false");
			jo.put("msg", e.getMessage());
			out.print(jo.toString());
		}
	}else if (mode.equals("loadPhoneConfig")){
	   out.print("{FAIL_NUM:" + PhoneConfigManager.FAIL_NUM
	    + ",ALARM_NUM:" + PhoneConfigManager.ALARM_NUM 
		+ ",TIMERUN:" + PhoneConfigManager.TIME_RUN 
		+ ",TIME:" + PhoneConfigManager.TIME
		+ ",ALARM_GRADE:'" + PhoneConfigManager.ALARM_GRADE+"'}");
	} else if ( "saveCallUserInfo".equals(mode) ){
		// 添加外呼角色配置信息
	    String f_userA = request.getParameter("f_userA");
	    String f_userB = request.getParameter("f_userB");
	    String f_userC = request.getParameter("f_userC");
	    User user = (User) Context.getContext().getAttribute(Context.USER);
		
	    try {
	        CallUserInfo info = new CallUserInfo();
			if ( f_userA.split("=").length > 1 ) {
				info.setUserA( f_userA.split("=")[0] );
				info.setAName( f_userA.split("=")[1] );
				// A的配角默认为A，即不设置B的场合对A呼叫六次
				info.setUserB( f_userA.split("=")[0] );
				info.setBName( f_userA.split("=")[1] );					
			}

			if ( f_userB.split("=").length > 1 ) {
				info.setUserB( f_userB.split("=")[0] );
				info.setBName( f_userB.split("=")[1] );			
			}
			if ( f_userC.split("=").length > 1 ) {
				info.setUserC( f_userC.split("=")[0] );
				info.setCName( f_userC.split("=")[1] );			
			}
			info.setUser( user.getId() );
		    boolean r = cum.saveCallUserInfo(info);
			out.print("{success:" + r + ",msg:''}");
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("success", "false");
			jo.put("msg", e.getMessage());
			out.print(jo.toString());
			e.printStackTrace();
		}
	} else if ( "callUserConfig".equals(mode) ){

		// 查询外呼角色配置信息
	    String condition = request.getParameter("condition");

		UserManager userManager = (UserManager) Context.getContext().getAttribute("userManager");
		try {

			int start = Integer.parseInt(request.getParameter("start") == null ? "0"
							: request.getParameter("start"));
			int limit = Integer.parseInt(request.getParameter("limit") == null ? "20"
							: request.getParameter("limit"));
			
			List<CallUserInfo> items = cum.getCallUser( condition, limit, start  );
			int count =  cum.getCallUserCount(condition);
			if ( condition != null ) {
				condition = new String ( condition.getBytes("gb2312"), "utf-8");
			}

			out.print("{count:" + count + ",data:[");
			if (items != null) {
				for (int i = 0; i < items.size(); i++) {
					if (i > 0) out.print(",");
					CallUserInfo info = items.get(i);
					JSONObject jo = new JSONObject();
					jo.put("rId", info.getUserA() );
					jo.put("userA", info.getAName() );
					jo.put("userB", info.getBName() );
					jo.put("userC", info.getCName() );
					User user = userManager.getUser( info.getUser() );
					if ( null != user ) {
						jo.put("userOP", user.getName() );
					} else {
						jo.put("userOP", info.getUser() );
					}
					jo.put("timeOP", info.getTime().toLocaleString() );					
					out.print(jo.toString());
				}
			}
			out.print("]}");
		} catch (Exception e) {
			e.printStackTrace();
		}
	} else if ( "deleteCallUserInfo".equals(mode) ){
		// 删除外呼角色配置信息
	    String userAs = request.getParameter("userAs");
	    try {
		    boolean r = cum.deleteCallUserInfo(userAs.split(";"));
			out.print("{success:" + r + ",msg:''}");
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("success", "false");
			jo.put("msg", e.getMessage());
			out.print(jo.toString());
		}
	} else if ( "modifyCallUserInfo".equals(mode) ){
		// 修改外呼角色配置信息
	    String f_userA = request.getParameter("f_userA");
	    String f_userB = request.getParameter("f_userB");
	    String f_userC = request.getParameter("f_userC");
	    User user = (User) Context.getContext().getAttribute(Context.USER);
		
	    try {
	        CallUserInfo info = new CallUserInfo();
	        info.setUserA( f_userA );
			if ( f_userB.split("=").length > 1 ) {
				info.setUserB( f_userB.split("=")[0] );
				info.setBName( f_userB.split("=")[1] );			
			}
			if ( f_userC.split("=").length > 1 ) {
				info.setUserC( f_userC.split("=")[0] );
				info.setCName( f_userC.split("=")[1] );			
			}
			info.setUser( user.getId() );
		    boolean r = cum.modifyCallUserInfo(info);
			out.print("{success:" + r + ",msg:''}");
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("success", "false");
			jo.put("msg", e.getMessage());
			out.print(jo.toString());
			e.printStackTrace();
		}
	} else if ( "loadCallUserInfo".equals(mode) ) {
		String f_userA = request.getParameter("f_userA");
		CallUserInfo info = cum.getCallUserByUserA( f_userA );
		if ( null == info ) { out.print( "{}" ); }
	    out.print("{f_userA:'" + info.getAName()
	    	+ "', f_userB:'" + info.getBName()
			+ "', f_userC:'" + info.getCName()
			+ "', valueB:'" + info.getUserB()
			+ "', valueC:'" + info.getUserB()+"'}");
	}
%>
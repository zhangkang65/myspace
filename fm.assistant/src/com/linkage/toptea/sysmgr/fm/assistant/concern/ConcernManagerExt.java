package com.linkage.toptea.sysmgr.fm.assistant.concern;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.linkage.toptea.auc.User;
import com.linkage.toptea.auc.UserManager;
import com.linkage.toptea.sysmgr.event.Listener;
import com.linkage.toptea.sysmgr.event.TopicEventCenter;
import com.linkage.toptea.sysmgr.fm.AlarmEventObject;
import com.linkage.toptea.sysmgr.fm.AlarmInfo;
import com.linkage.toptea.sysmgr.fm.AlarmManager;
import com.linkage.toptea.sysmgr.fm.responsible.ResponsibleInfo;
import com.linkage.toptea.sysmgr.fm.responsible.ResponsibleManager;
import com.linkage.toptea.util.BeanUtil;

/**
 * 自动外呼管理扩展类
 * 对于为启用自动外呼参数配置的规则，进行校验和自动外呼
 * @author huangzh
 * @since 2012-11-14
 */
public class ConcernManagerExt implements Listener {

	private Log logger = LogFactory.getLog(getClass());
	
	// 连接数据库
	private DataSource dataSource;
	
	// 事件中心
	private TopicEventCenter eventCenter;
	
	// 外呼管理
	private ConcernManager concernManager;
	
	// 用户管理
	private UserManager userManager;
	
	// 外呼配置目录
	private String callPath;

	// 外呼文件存放路径
	private String callto;

	// 责任人管理
	private ResponsibleManager responsibleManager;
	
	private CallFileManager callFileManager;	

	public void setCallFileManager( CallFileManager callFileManager ) {
		this.callFileManager = callFileManager;
	}
	
	/** 设置责任人管理 */
	public void setResponsibleManager(ResponsibleManager responsibleManager){
		this.responsibleManager = responsibleManager;
	}

	/** 设置外呼文件存放目录 */
	public void setCallto(String callto) {
		this.callto = callto;
	}

	/** 设置外呼配置目录 */
	public void setCallPath(String callpath) {
		this.callPath = callpath;
	}

	/** 设置数据库资源 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/** 设置事件中心 */
	public void setEventCenter(TopicEventCenter eventCenter) {
		this.eventCenter = eventCenter;
	}

	/** 设置外呼管理 */
	public void setConcernManager(ConcernManager concernManager) {
		this.concernManager = concernManager;
	}

	/** 初始化自动外呼扩展类 */
	public void init() {
		// 初始化事件监听 : 11.22日暂不上线，以后上线打开此处注释
		//eventCenter.addListener( AlarmManager.ALARM_TOPIC, this);
	}
	

	/**
	 * 响应告警事件
	 * @param event  告警事件
	 */
	public void onEvent(EventObject event) {
		try {
			AlarmEventObject alarmEvent = (AlarmEventObject) event;
			AlarmInfo[] infos = (AlarmInfo[]) alarmEvent.getMessage();
			List<AutoCallAlarmInfo> callList = new ArrayList<AutoCallAlarmInfo> ();
			for (int i = 0; i < infos.length; i++) {
				AlarmInfo info = infos[i];
				// 需要外呼的场合
				if ( check( info.getMoId() ) == true ){
					// 自动外呼
					AutoCallAlarmInfo call = outCall( info.getId(), info.getContent(), info.getMoId() );
					if ( call != null ) { callList.add(call); }
				}
			}
			addCalInfoToDB( callList );
		} catch ( Exception e ) {
			logger.error("告警自动外呼事件处理失败：", e);
		}
	}
	
	/**
	 * 校验是否可以直接自动外呼
	 * @param moId 告警对象ID
	 * @return true 可以直接外呼， false 不要直接外呼
	 */
	public boolean check( String moId ) {
		try {
			// 判定是否已达最大外呼数量
			if (Call.callTotal <= callFileManager.getCallCount() ) {
				logger.error("您已经达到外呼最大次数" + Call.callTotal + "无法再自动外呼了");
				return false;
			}
			// 取得外呼规则
			PhoneFilterInfo filter = PhoneFilterManager.getFilter(moId, PhoneFilterInfo.FILTER_USE);
			if (filter == null) { return false; }
			System.out.println("moId= " + moId + "  " + filter.getForPara() );
			// 禁用外呼参数的场合 ： 校验通过
			if ( filter.getForPara() == 0 ) {
				return true;
			}
		} catch (Exception e) {
			logger.error( " 自动外呼规则校验失败： ", e );
		}
		return false;
	}

	/**
	 * 自动外呼
	 * @param alarmId 告警ID
	 * @param moId  告警对象ID
	 * @param alarmContent 告警内容
	 * @return true 外呼成功， false 外呼失败
	 */
	public AutoCallAlarmInfo outCall( String alarmId, String alarmContent, String moId ) {

		try {
			User user = getResponsible( moId );
			if (alarmId == null || alarmContent == null || user == null ) {
				logger.info("==========outCall 参数错误！ ===========");
				return null;
			}
			String phone =  user.getMobile();
			if (phone == null || "".equals(phone)) {
				logger.info("对" + user.getName() + "外呼告警失败，因为他没有手机号");
				return null;
			}
			long callid = System.currentTimeMillis();
			String fileName = callFileManager.getCallFileName();
			logger.info("=================" + callid + "=====================");
			logger.info("对告警ID = " + alarmId + "进行外呼，外呼人：" + user.getName() + "手机号码：" + user.getMobile());
			logger.info("自动外呼文件名称： " + fileName);
			logger.info("");
			FileWriter writer = new FileWriter(callPath + "/" + callto + "/" + fileName + ".txt", false);

			if (alarmContent.length() > 60) {
				writer.write(callid + "~" + phone + "~" + "您有一条告警" + alarmContent.substring(0, 60));
			} else {
				writer.write(callid + "~" + phone + "~" + "您有一条告警" + alarmContent);
			}
			writer.close();
			
			AutoCallAlarmInfo callInfo = new AutoCallAlarmInfo();
			callInfo.setId( alarmId );
			callInfo.setCallid( callid + "" );
			callInfo.setCaller( user.getId() );
			callInfo.setPhone(phone);
			return callInfo;

		} catch (Exception e) {
			logger.error("生成外呼文件失败：", e);
		}
		return null;
	} 
	
	/**
	 * 取得外呼责任人
	 * @param moId  告警对象ID
	 * @return user 被外呼用户信息
	 */
	public User getResponsible(String moId) {
		
		try {
			long nowTime = System.currentTimeMillis();
						
			ResponsibleInfo info = responsibleManager.getResponsibleInfo(moId);
			// 该对象没有设置责任人
			if (info == null) { return null; }
			
			// 该对象平台类、应用类责任人都不存在 不外呼
			if ((info.getResponsible1() == null || "".equals(info.getResponsible1()))
					&& (info.getResponsible3() == null || "".equals(info.getResponsible3()))) {
				return null;
			}
			// 该对象平台类、应用类责任人都存在 也不外呼
			if ((info.getResponsible1() != null && !"".equals(info.getResponsible1()))
					&& (info.getResponsible3() != null && !"".equals(info.getResponsible3()))) {
				return null;
			}
			
			// 外呼责任人
			String reper = "";
			
			// 优先外呼应用类责任人
			if (info.getResponsible3() != null && !"".equals(info.getResponsible3())) {
				
				RepVacation rv = RepVacationManager.getVacation(info.getResponsible3());
				
				if (rv != null) {
					// 查看当应用类责任人是否休假
					if (rv.getStartTime() < nowTime && rv.getEndTime() > nowTime) {
						return null;
					}
				}
				reper = info.getResponsible3();
				logger.debug("自动外呼告警 使用应用类责任人=" + reper);
				
			// 应用责任人不存在的场合外呼平台类责任人
			} else if (info.getResponsible1() != null && !"".equals(info.getResponsible1())) {
				
				RepVacation rv = RepVacationManager.getVacation(info.getResponsible1());
				
				if (rv != null) {
					// 查看当前平台类责任人是否休假
					if (rv.getStartTime() < nowTime && rv.getEndTime() > nowTime) {
						return null;
					}
				}
				reper = info.getResponsible1();
				logger.debug("自动外呼告警 使用平台类责任人=" + reper);
			} 
			
			return  userManager.getUser(reper);
		} catch (Exception e) {
			logger.error( "取得外呼责任人失败： ",e );
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 向外呼表中添加外呼记录
	 * @param callList 外呼记录列表
	 */
	public void addCalInfoToDB(List<AutoCallAlarmInfo> callList) {
		
		if ( callList == null || callList.size() == 0 ) { return; }
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "INSERT INTO TF_TT_ALARM_CALLPHONE (ALARM_ID,CALLID, "
				+ " CALLER,PHONE,STATUS,NUM,ISCONCERN,ISHOW) VALUES(?,?,?,?,0,0,0,1)";
		try {
			conn = this.dataSource.getConnection();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (AutoCallAlarmInfo call : callList) {
				ps.setString(1, call.getId());
				ps.setString(2, call.getCallid() );
				ps.setString(3, call.getCaller() );
				ps.setString(4, call.getPhone() );
				ps.addBatch();
			}

			ps.executeBatch();
			conn.commit();

		} catch (Exception e) {
			logger.error("addCalInfoToDB", e);
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e) {
				logger.error("setAutoCommit", e);
			}
			BeanUtil.closeResource(ps, conn);
		}
	}
	 
	/**
	 * 判定是否接收该事件
	 * 
	 * @param event
	 *            事件对象
	 * @return true 接收该事件 false 拒绝处理该事件
	 */
	public boolean accepts( EventObject event ) {
		// 取得告警事件
		AlarmEventObject alarmEvent = (AlarmEventObject) event;
		
		// 只接收新增告警事件
		if (alarmEvent.getType() == AlarmEventObject.ADD) {
			return true;
		}
		return false;
	}

	 /**  取得该类类名 */
	public String getId() {
		return this.getClass().getName();
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

}

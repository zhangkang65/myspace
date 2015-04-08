package com.linkage.toptea.sysmgr.fm.assistant.calluser;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.linkage.toptea.context.Context;
import com.linkage.toptea.sysmgr.cm.ConfigManager;
import com.linkage.toptea.sysmgr.fm.AlarmFilterInfo;
import com.linkage.toptea.sysmgr.fm.AlarmInfo;
import com.linkage.toptea.sysmgr.fm.AlarmManager;
import com.linkage.toptea.sysmgr.fm.assistant.concern.AutoCallAlarmInfo;
import com.linkage.toptea.sysmgr.fm.assistant.concern.PhoneConfigManager;
import com.linkage.toptea.sysmgr.fm.assistant.concern.PhoneFilterInfo;
import com.linkage.toptea.sysmgr.fm.assistant.concern.RepVacation;
import com.linkage.toptea.sysmgr.fm.assistant.concern.RepVacationManager;
import com.linkage.toptea.sysmgr.fm.responsible.ResponsibleInfo;
import com.linkage.toptea.sysmgr.fm.responsible.ResponsibleManager;
import com.linkage.toptea.util.TimeExpressionUtil;

/**
 * 外呼角色配置管理类
 * @author huangzh
 *
 */
public class CallUserManager {

	/** 系统日志 */
	private Log logger = LogFactory.getLog( CallUserManager.class );
	
	/** 对象责任人 */
	private ResponsibleManager responsibleManager;
	public void setResponsibleManager(ResponsibleManager responsibleManager){
		this.responsibleManager = responsibleManager;
	}
	
	public static boolean IS_CONF = false;
	
	/**
	 * 根据外呼规则判定外呼责任人
	 * @param filter
	 * @param info
	 * @return
	 */
	public String check (PhoneFilterInfo filter, ResponsibleInfo info) {
		
		long nowTime = System.currentTimeMillis();

		try {

			String timelen = filter.getTimelen();
			// 如果不是任意时间都有效
			if (timelen != null && !"".equals(timelen.trim()) ) {
				if ( TimeExpressionUtil.isSatisfied(new Date(nowTime), timelen) == false ) {
					return null;
				}
			}
			
			// 平台类责任人存在的场合
			if ( info.checkResponsible(0) != -1 ) {
				if ( checkResponsible(info.getResponsible1(), nowTime) == true ) {
					return info.getResponsible1();
				} else if ( checkResponsible(info.getResponsible2(), nowTime) == true ) {
					return info.getResponsible2();
				} else if ( checkResponsible(info.getResponsible7(), nowTime) == true ) {
					return info.getResponsible7();
				} else {
					return null;
				}
				//  业务类责任人存在的场合
			} else if ( info.checkResponsible(1) != -1 ) {
				if ( checkResponsible(info.getResponsible3(), nowTime) == true ) {
					return info.getResponsible3();
				} else if ( checkResponsible(info.getResponsible4(), nowTime) == true ) {
					return info.getResponsible4();
				} else if ( checkResponsible(info.getResponsible8(), nowTime) == true ) {
					return info.getResponsible8();
				} else {
					return null;
				}
			} else {
				// 其他的场合
				return null;
			}
		} catch (Exception e) {
			logger.error("check", e);
		}

		return null;
	}
	
	public String getNextCallUser( AutoCallAlarmInfo callInfo ) {
		try {
			String user = callInfo.getCaller();
			if ( null == user ) { return null; }
			
			// 外呼次数不符合条件时，不处理
			if ( callInfo.getNum() < PhoneConfigManager.FAIL_NUM ){
				return null;
			}
			
			ResponsibleInfo info = responsibleManager.getResponsibleInfo( callInfo.getMoId() );
			// 责任人已经不存在时，不处理
			if ( info == null ) { 
				logger.info("三层外呼时，取得责任人不存在！外呼id= " + callInfo.getCallid() );
				return null; 
			}
			
			// 取得下个责任人
			for ( int i = 1; i <= 5; i++ ) {
				if ( user.equals( info.getResponsible(i)) ) {
					// 1、2、7平台类 3、4、8业务量 5、6备用类
					switch ( i ) { 
						case 1: return info.getResponsible2();
						case 2: return info.getResponsible7();
						case 3: return info.getResponsible4();
						case 4: return info.getResponsible8();
						case 5: return info.getResponsible6();
						default:  return null;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * 校验责任人是否为空和休假
	 * @param userId
	 * @param nowTime
	 * @return false 责任人为空，或已休假
	 */
	public boolean checkResponsible ( String userId, long nowTime ) {
		if ( userId == null  || "".equals(userId) ) { return false; }
		RepVacation rv = RepVacationManager.getVacation(userId);
		if (rv == null) { return true; }
		// 正在休假
		if ( rv.getStartTime() < nowTime && rv.getEndTime() > nowTime ) { return false; }
		return true;
	}

	
	/**
	 * 根据外呼ID取得短信信息
	 * @param callId 外呼ID
	 * @return 短信信息
	 */
	public String getSmsInfoByAlarmId ( String alarmId ) {

		try {
			
		} catch (Exception e) {
			logger.error("getSmsInfoByCallId", e);
		}
		return null;

	}

	
	/** 向短信表插数据，发短信 */
	public boolean sendSms(Connection conn, String phone, String alarmId, String user ) {
		CallableStatement ps = null;
		try {
			AlarmManager am = (AlarmManager)Context.getContext().getAttribute("alarmManager");
			ConfigManager cm = (ConfigManager)Context.getContext().getAttribute("configManager");
			AlarmFilterInfo filter = new AlarmFilterInfo();
			filter.setAlarmIds( new String[] {alarmId} );
			AlarmInfo[] alarms = am.fecthAlarm(filter);
			if ( alarms == null ) { return false; }
			String msg =  "告警内容:" + alarms[0].getContent() + "\n" 
					+ "告警对象:" + cm.getNamingPathById(alarms[0].getMoId()) ;
			msg = "因为自动外呼 " + user + " 失败，下面告警需要您来处理：" + msg;
			logger.info(phone + " : " + msg);
			ps = conn.prepareCall("{ call sp_send_sms(?,?,77)}");
			ps.setString(1, phone);
			ps.setString(2, msg);
			ps.execute();
		} catch (Exception e) {
			logger.error("sendSms", e);
		}
		return true;
	}
}

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
 * �����ɫ���ù�����
 * @author huangzh
 *
 */
public class CallUserManager {

	/** ϵͳ��־ */
	private Log logger = LogFactory.getLog( CallUserManager.class );
	
	/** ���������� */
	private ResponsibleManager responsibleManager;
	public void setResponsibleManager(ResponsibleManager responsibleManager){
		this.responsibleManager = responsibleManager;
	}
	
	public static boolean IS_CONF = false;
	
	/**
	 * ������������ж����������
	 * @param filter
	 * @param info
	 * @return
	 */
	public String check (PhoneFilterInfo filter, ResponsibleInfo info) {
		
		long nowTime = System.currentTimeMillis();

		try {

			String timelen = filter.getTimelen();
			// �����������ʱ�䶼��Ч
			if (timelen != null && !"".equals(timelen.trim()) ) {
				if ( TimeExpressionUtil.isSatisfied(new Date(nowTime), timelen) == false ) {
					return null;
				}
			}
			
			// ƽ̨�������˴��ڵĳ���
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
				//  ҵ���������˴��ڵĳ���
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
				// �����ĳ���
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
			
			// �����������������ʱ��������
			if ( callInfo.getNum() < PhoneConfigManager.FAIL_NUM ){
				return null;
			}
			
			ResponsibleInfo info = responsibleManager.getResponsibleInfo( callInfo.getMoId() );
			// �������Ѿ�������ʱ��������
			if ( info == null ) { 
				logger.info("�������ʱ��ȡ�������˲����ڣ����id= " + callInfo.getCallid() );
				return null; 
			}
			
			// ȡ���¸�������
			for ( int i = 1; i <= 5; i++ ) {
				if ( user.equals( info.getResponsible(i)) ) {
					// 1��2��7ƽ̨�� 3��4��8ҵ���� 5��6������
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
	 * У���������Ƿ�Ϊ�պ��ݼ�
	 * @param userId
	 * @param nowTime
	 * @return false ������Ϊ�գ������ݼ�
	 */
	public boolean checkResponsible ( String userId, long nowTime ) {
		if ( userId == null  || "".equals(userId) ) { return false; }
		RepVacation rv = RepVacationManager.getVacation(userId);
		if (rv == null) { return true; }
		// �����ݼ�
		if ( rv.getStartTime() < nowTime && rv.getEndTime() > nowTime ) { return false; }
		return true;
	}

	
	/**
	 * �������IDȡ�ö�����Ϣ
	 * @param callId ���ID
	 * @return ������Ϣ
	 */
	public String getSmsInfoByAlarmId ( String alarmId ) {

		try {
			
		} catch (Exception e) {
			logger.error("getSmsInfoByCallId", e);
		}
		return null;

	}

	
	/** ����ű�����ݣ������� */
	public boolean sendSms(Connection conn, String phone, String alarmId, String user ) {
		CallableStatement ps = null;
		try {
			AlarmManager am = (AlarmManager)Context.getContext().getAttribute("alarmManager");
			ConfigManager cm = (ConfigManager)Context.getContext().getAttribute("configManager");
			AlarmFilterInfo filter = new AlarmFilterInfo();
			filter.setAlarmIds( new String[] {alarmId} );
			AlarmInfo[] alarms = am.fecthAlarm(filter);
			if ( alarms == null ) { return false; }
			String msg =  "�澯����:" + alarms[0].getContent() + "\n" 
					+ "�澯����:" + cm.getNamingPathById(alarms[0].getMoId()) ;
			msg = "��Ϊ�Զ���� " + user + " ʧ�ܣ�����澯��Ҫ��������" + msg;
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

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
 * �Զ����������չ��
 * ����Ϊ�����Զ�����������õĹ��򣬽���У����Զ����
 * @author huangzh
 * @since 2012-11-14
 */
public class ConcernManagerExt implements Listener {

	private Log logger = LogFactory.getLog(getClass());
	
	// �������ݿ�
	private DataSource dataSource;
	
	// �¼�����
	private TopicEventCenter eventCenter;
	
	// �������
	private ConcernManager concernManager;
	
	// �û�����
	private UserManager userManager;
	
	// �������Ŀ¼
	private String callPath;

	// ����ļ����·��
	private String callto;

	// �����˹���
	private ResponsibleManager responsibleManager;
	
	private CallFileManager callFileManager;	

	public void setCallFileManager( CallFileManager callFileManager ) {
		this.callFileManager = callFileManager;
	}
	
	/** ���������˹��� */
	public void setResponsibleManager(ResponsibleManager responsibleManager){
		this.responsibleManager = responsibleManager;
	}

	/** ��������ļ����Ŀ¼ */
	public void setCallto(String callto) {
		this.callto = callto;
	}

	/** �����������Ŀ¼ */
	public void setCallPath(String callpath) {
		this.callPath = callpath;
	}

	/** �������ݿ���Դ */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/** �����¼����� */
	public void setEventCenter(TopicEventCenter eventCenter) {
		this.eventCenter = eventCenter;
	}

	/** ����������� */
	public void setConcernManager(ConcernManager concernManager) {
		this.concernManager = concernManager;
	}

	/** ��ʼ���Զ������չ�� */
	public void init() {
		// ��ʼ���¼����� : 11.22���ݲ����ߣ��Ժ����ߴ򿪴˴�ע��
		//eventCenter.addListener( AlarmManager.ALARM_TOPIC, this);
	}
	

	/**
	 * ��Ӧ�澯�¼�
	 * @param event  �澯�¼�
	 */
	public void onEvent(EventObject event) {
		try {
			AlarmEventObject alarmEvent = (AlarmEventObject) event;
			AlarmInfo[] infos = (AlarmInfo[]) alarmEvent.getMessage();
			List<AutoCallAlarmInfo> callList = new ArrayList<AutoCallAlarmInfo> ();
			for (int i = 0; i < infos.length; i++) {
				AlarmInfo info = infos[i];
				// ��Ҫ����ĳ���
				if ( check( info.getMoId() ) == true ){
					// �Զ����
					AutoCallAlarmInfo call = outCall( info.getId(), info.getContent(), info.getMoId() );
					if ( call != null ) { callList.add(call); }
				}
			}
			addCalInfoToDB( callList );
		} catch ( Exception e ) {
			logger.error("�澯�Զ�����¼�����ʧ�ܣ�", e);
		}
	}
	
	/**
	 * У���Ƿ����ֱ���Զ����
	 * @param moId �澯����ID
	 * @return true ����ֱ������� false ��Ҫֱ�����
	 */
	public boolean check( String moId ) {
		try {
			// �ж��Ƿ��Ѵ�����������
			if (Call.callTotal <= callFileManager.getCallCount() ) {
				logger.error("���Ѿ��ﵽ���������" + Call.callTotal + "�޷����Զ������");
				return false;
			}
			// ȡ���������
			PhoneFilterInfo filter = PhoneFilterManager.getFilter(moId, PhoneFilterInfo.FILTER_USE);
			if (filter == null) { return false; }
			System.out.println("moId= " + moId + "  " + filter.getForPara() );
			// ������������ĳ��� �� У��ͨ��
			if ( filter.getForPara() == 0 ) {
				return true;
			}
		} catch (Exception e) {
			logger.error( " �Զ��������У��ʧ�ܣ� ", e );
		}
		return false;
	}

	/**
	 * �Զ����
	 * @param alarmId �澯ID
	 * @param moId  �澯����ID
	 * @param alarmContent �澯����
	 * @return true ����ɹ��� false ���ʧ��
	 */
	public AutoCallAlarmInfo outCall( String alarmId, String alarmContent, String moId ) {

		try {
			User user = getResponsible( moId );
			if (alarmId == null || alarmContent == null || user == null ) {
				logger.info("==========outCall �������� ===========");
				return null;
			}
			String phone =  user.getMobile();
			if (phone == null || "".equals(phone)) {
				logger.info("��" + user.getName() + "����澯ʧ�ܣ���Ϊ��û���ֻ���");
				return null;
			}
			long callid = System.currentTimeMillis();
			String fileName = callFileManager.getCallFileName();
			logger.info("=================" + callid + "=====================");
			logger.info("�Ը澯ID = " + alarmId + "�������������ˣ�" + user.getName() + "�ֻ����룺" + user.getMobile());
			logger.info("�Զ�����ļ����ƣ� " + fileName);
			logger.info("");
			FileWriter writer = new FileWriter(callPath + "/" + callto + "/" + fileName + ".txt", false);

			if (alarmContent.length() > 60) {
				writer.write(callid + "~" + phone + "~" + "����һ���澯" + alarmContent.substring(0, 60));
			} else {
				writer.write(callid + "~" + phone + "~" + "����һ���澯" + alarmContent);
			}
			writer.close();
			
			AutoCallAlarmInfo callInfo = new AutoCallAlarmInfo();
			callInfo.setId( alarmId );
			callInfo.setCallid( callid + "" );
			callInfo.setCaller( user.getId() );
			callInfo.setPhone(phone);
			return callInfo;

		} catch (Exception e) {
			logger.error("��������ļ�ʧ�ܣ�", e);
		}
		return null;
	} 
	
	/**
	 * ȡ�����������
	 * @param moId  �澯����ID
	 * @return user ������û���Ϣ
	 */
	public User getResponsible(String moId) {
		
		try {
			long nowTime = System.currentTimeMillis();
						
			ResponsibleInfo info = responsibleManager.getResponsibleInfo(moId);
			// �ö���û������������
			if (info == null) { return null; }
			
			// �ö���ƽ̨�ࡢӦ���������˶������� �����
			if ((info.getResponsible1() == null || "".equals(info.getResponsible1()))
					&& (info.getResponsible3() == null || "".equals(info.getResponsible3()))) {
				return null;
			}
			// �ö���ƽ̨�ࡢӦ���������˶����� Ҳ�����
			if ((info.getResponsible1() != null && !"".equals(info.getResponsible1()))
					&& (info.getResponsible3() != null && !"".equals(info.getResponsible3()))) {
				return null;
			}
			
			// ���������
			String reper = "";
			
			// �������Ӧ����������
			if (info.getResponsible3() != null && !"".equals(info.getResponsible3())) {
				
				RepVacation rv = RepVacationManager.getVacation(info.getResponsible3());
				
				if (rv != null) {
					// �鿴��Ӧ�����������Ƿ��ݼ�
					if (rv.getStartTime() < nowTime && rv.getEndTime() > nowTime) {
						return null;
					}
				}
				reper = info.getResponsible3();
				logger.debug("�Զ�����澯 ʹ��Ӧ����������=" + reper);
				
			// Ӧ�������˲����ڵĳ������ƽ̨��������
			} else if (info.getResponsible1() != null && !"".equals(info.getResponsible1())) {
				
				RepVacation rv = RepVacationManager.getVacation(info.getResponsible1());
				
				if (rv != null) {
					// �鿴��ǰƽ̨���������Ƿ��ݼ�
					if (rv.getStartTime() < nowTime && rv.getEndTime() > nowTime) {
						return null;
					}
				}
				reper = info.getResponsible1();
				logger.debug("�Զ�����澯 ʹ��ƽ̨��������=" + reper);
			} 
			
			return  userManager.getUser(reper);
		} catch (Exception e) {
			logger.error( "ȡ�����������ʧ�ܣ� ",e );
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * �����������������¼
	 * @param callList �����¼�б�
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
	 * �ж��Ƿ���ո��¼�
	 * 
	 * @param event
	 *            �¼�����
	 * @return true ���ո��¼� false �ܾ�������¼�
	 */
	public boolean accepts( EventObject event ) {
		// ȡ�ø澯�¼�
		AlarmEventObject alarmEvent = (AlarmEventObject) event;
		
		// ֻ���������澯�¼�
		if (alarmEvent.getType() == AlarmEventObject.ADD) {
			return true;
		}
		return false;
	}

	 /**  ȡ�ø������� */
	public String getId() {
		return this.getClass().getName();
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

}

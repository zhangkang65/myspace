package com.linkage.toptea.sysmgr.fm.assistant.concern;
/**
 * ���ܣ���ע�澯�ӿ���
 * ���ߣ��˻�
 * ʱ�䣺20110211
 */
import java.util.List;
import java.util.Map;

public interface ConcernManager {
	

	/**
	 * ������ӵ绰��ϵ
	 * @param alarmId  �澯���
	 * @param phoner  ��ϵ��
	 * @return ���ش�������
	 */
	public String batchPhone(String[] alarmIds) throws Exception;

	/**
	 * ��Ӹ澯�����ע�澯
	 * @param alarmIds
	 * @param memo
	 * @throws Exception
	 */
	/**
	 * ��ȡ��ϵ
	 * @param alarmId
	 */
	public List<Call> getPhone(String alarmId) throws Exception;
	
	public void addConcernAlarm(String alarmId,String memo) throws Exception;
	/**
	 * ��ȡ���������ļ�¼����
	 * @param filter ������
	 * @return
	 */
	public int getConcernCount(ConcernFilter filter);
	
	/**
	 * ���ع��������ļ���
	 * @param filter ������
	 * @param start ���Ͽ�ʼ��
	 * @param limit ÿҳ��ʾ����
	 * @return
	 */
	public List<ConcernInfo> findConcernAlarmByFilter(ConcernFilter filter,int start,int limit);
	/**
	 * �鿴�澯��ʷ��ע
	 * @param alarmId
	 * @return
	 * @throws Exception
	 */
	public  List<ConcernInfo>  findHisConrecnById(String alarmId) throws Exception;
	
	/**
	 * ȡ����ע�澯
	 * @param alarmIds �澯���
	 * @param memo ȡ��ԭ��
	 * @throws Exception
	 */
	public void removeConcern(String[] alarmIds,String memo) throws Exception;
	/**
	 * �޸�����
	 * @param cg
	 * @param ct
	 * @param tg
	 * @param tt
	 * @param tnt
	 */
	public void setConfig(int cg,int ct,int tg,int tt,int tnt,String pf, String tp) throws Exception;
	
	/**
	 * ����ӿ�
	 * @param phone
	 * @param alarmId
	 * @param workid
	 * @param context
	 * @param userId
	 * @param personcall �Ƿ��ֶ����
	 * @throws Exception
	 */
	public void callPhone(String phone,String alarmId,String workid,String context,String userId,String personcall,String title) throws Exception;
	public void callPhone(String phone, String alarmId, String workid,String context, String userId, String personcall, String title,String caller)	throws Exception;
	public void callPhone(String phone,String[] alarmIds,String context,String userId,String personcall,String title) throws Exception;
	/**
	 * ��ȡ�����Ա�б�
	 * @param search
	 * @param start
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	public List getCallUserList(String search,int start ,int limit) throws Exception;
	/**
	 * ��ȡ�����Ա�ܸ���
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public int getCallUserCount(String search) throws Exception;

	 /**
	  * �����������ݼ�
	  * @param xml
	  * @throws Exception
	  */
	 public void updateRepVacation(String xml) throws Exception;
	 /**
	  * �����������ݼ�
	  * @param items
	  * @throws Exception
	  */
	 public void updateRepVacation(List<RepVacation> items) throws Exception;
	 /**
	  * ɾ���ݼ�
	  * @param userid
	  * @throws Exception
	  */
	 public void removeRepVacation(String userid) throws Exception;
	 /**
	  * ɾ���ݼ�
	  * @param users
	  * @throws Exception
	  */
	 public void removeRepVacation(String[] users) throws Exception;
	 /**
	  * ��ȡ�ݼ�������
	  * @return
	  * @throws Exception
	  */
	 public int getRepVacationCount() throws Exception;
	 /**
	  * ��ȡ�ݼ�
	  * @param start
	  * @param limit
	  * @return
	  * @throws Exception
	  */
	 public List<RepVacation> findRepVacation(int start,int limit) throws Exception;
	 
	 /**
	  * ��ȡ����������
	  * @return
	  * @throws Exception
	  */
	 public Map<String,String> getAllReper(int type) throws Exception;
	 
	 /**
	  * ����������
	  * @param items
	  */
	 public void updatePhoneConfig(Map<String,String> items) throws Exception;
	 
	 /**
	  * ��ȡ�������
	  * @param filter
	  * @return
	  * @throws Exception
	  */
	 public int getAutoCallAlarmInfoCount(AutoCallAlarmFilter filter)  throws Exception;

	 /**
	  * �������������
	  * @param alarmIds
	  * @throws Exception
	  */
	 public void hideAutoCallAlarmInfos(String[] alarmIds)  throws Exception;
	 
	/** ����ÿ������澯���� */
	public void resetCallCount( int count );
	
	public String getCallTotal();
	
	public Map<String, List<AutoCallAlarmInfo>>  getCallInfo();
	
	/** ��ʼ���Զ�����ڴ����� */
	public void initCache( );
	 
}

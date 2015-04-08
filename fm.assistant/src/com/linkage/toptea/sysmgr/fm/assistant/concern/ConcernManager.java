package com.linkage.toptea.sysmgr.fm.assistant.concern;
/**
 * 功能：关注告警接口类
 * 作者：潘华
 * 时间：20110211
 */
import java.util.List;
import java.util.Map;

public interface ConcernManager {
	

	/**
	 * 批量添加电话联系
	 * @param alarmId  告警编号
	 * @param phoner  联系人
	 * @return 返回错误描述
	 */
	public String batchPhone(String[] alarmIds) throws Exception;

	/**
	 * 添加告警进入关注告警
	 * @param alarmIds
	 * @param memo
	 * @throws Exception
	 */
	/**
	 * 获取联系
	 * @param alarmId
	 */
	public List<Call> getPhone(String alarmId) throws Exception;
	
	public void addConcernAlarm(String alarmId,String memo) throws Exception;
	/**
	 * 获取过滤条件的记录总数
	 * @param filter 过滤器
	 * @return
	 */
	public int getConcernCount(ConcernFilter filter);
	
	/**
	 * 返回过滤条件的集合
	 * @param filter 过滤器
	 * @param start 集合开始点
	 * @param limit 每页显示条数
	 * @return
	 */
	public List<ConcernInfo> findConcernAlarmByFilter(ConcernFilter filter,int start,int limit);
	/**
	 * 查看告警历史关注
	 * @param alarmId
	 * @return
	 * @throws Exception
	 */
	public  List<ConcernInfo>  findHisConrecnById(String alarmId) throws Exception;
	
	/**
	 * 取消关注告警
	 * @param alarmIds 告警编号
	 * @param memo 取消原因
	 * @throws Exception
	 */
	public void removeConcern(String[] alarmIds,String memo) throws Exception;
	/**
	 * 修改配置
	 * @param cg
	 * @param ct
	 * @param tg
	 * @param tt
	 * @param tnt
	 */
	public void setConfig(int cg,int ct,int tg,int tt,int tnt,String pf, String tp) throws Exception;
	
	/**
	 * 外呼接口
	 * @param phone
	 * @param alarmId
	 * @param workid
	 * @param context
	 * @param userId
	 * @param personcall 是否手动外呼
	 * @throws Exception
	 */
	public void callPhone(String phone,String alarmId,String workid,String context,String userId,String personcall,String title) throws Exception;
	public void callPhone(String phone, String alarmId, String workid,String context, String userId, String personcall, String title,String caller)	throws Exception;
	public void callPhone(String phone,String[] alarmIds,String context,String userId,String personcall,String title) throws Exception;
	/**
	 * 获取外呼人员列表
	 * @param search
	 * @param start
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	public List getCallUserList(String search,int start ,int limit) throws Exception;
	/**
	 * 获取外呼人员总个数
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public int getCallUserCount(String search) throws Exception;

	 /**
	  * 更新责任人休假
	  * @param xml
	  * @throws Exception
	  */
	 public void updateRepVacation(String xml) throws Exception;
	 /**
	  * 更新责任人休假
	  * @param items
	  * @throws Exception
	  */
	 public void updateRepVacation(List<RepVacation> items) throws Exception;
	 /**
	  * 删除休假
	  * @param userid
	  * @throws Exception
	  */
	 public void removeRepVacation(String userid) throws Exception;
	 /**
	  * 删除休假
	  * @param users
	  * @throws Exception
	  */
	 public void removeRepVacation(String[] users) throws Exception;
	 /**
	  * 获取休假人总数
	  * @return
	  * @throws Exception
	  */
	 public int getRepVacationCount() throws Exception;
	 /**
	  * 获取休假
	  * @param start
	  * @param limit
	  * @return
	  * @throws Exception
	  */
	 public List<RepVacation> findRepVacation(int start,int limit) throws Exception;
	 
	 /**
	  * 获取所有责任人
	  * @return
	  * @throws Exception
	  */
	 public Map<String,String> getAllReper(int type) throws Exception;
	 
	 /**
	  * 更新配置项
	  * @param items
	  */
	 public void updatePhoneConfig(Map<String,String> items) throws Exception;
	 
	 /**
	  * 获取外呼次数
	  * @param filter
	  * @return
	  * @throws Exception
	  */
	 public int getAutoCallAlarmInfoCount(AutoCallAlarmFilter filter)  throws Exception;

	 /**
	  * 外呼结束后隐藏
	  * @param alarmIds
	  * @throws Exception
	  */
	 public void hideAutoCallAlarmInfos(String[] alarmIds)  throws Exception;
	 
	/** 重置每日外呼告警总数 */
	public void resetCallCount( int count );
	
	public String getCallTotal();
	
	public Map<String, List<AutoCallAlarmInfo>>  getCallInfo();
	
	/** 初始化自动外呼内存数据 */
	public void initCache( );
	 
}

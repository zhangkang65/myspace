package com.linkage.toptea.sysmgr.fm.assistant.concern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.hp.itsm.api.ApiSDSession;
import com.hp.itsm.api.interfaces.IIncident;
import com.hp.itsm.api.interfaces.IIncidentHome;
import com.linkage.toptea.auc.BusinessLogger;
import com.linkage.toptea.auc.User;
import com.linkage.toptea.auc.UserManager;
import com.linkage.toptea.auc.domain.UserInfo;
import com.linkage.toptea.sysmgr.cm.ConfigManager;
import com.linkage.toptea.sysmgr.fm.AlarmFilterInfo;
import com.linkage.toptea.sysmgr.fm.AlarmInfo;
import com.linkage.toptea.sysmgr.fm.AlarmManager;
import com.linkage.toptea.sysmgr.fm.assistant.AstConfig;
import com.linkage.toptea.sysmgr.fm.assistant.UserTransaction;
import com.linkage.toptea.sysmgr.fm.assistant.callconfig.CallConfigManager;
import com.linkage.toptea.sysmgr.fm.assistant.callkeyword.CallKeyWordManager;
import com.linkage.toptea.sysmgr.fm.assistant.calluser.CallUserManager;
import com.linkage.toptea.sysmgr.fm.assistant.specialcall.SpecialCallManager;
import com.linkage.toptea.sysmgr.fm.responsible.ResponsibleInfo;
import com.linkage.toptea.sysmgr.fm.responsible.ResponsibleManager;
import com.linkage.toptea.sysmgr.fm.util.ChangeDateType;
import com.linkage.toptea.sysmgr.ovsd.OvsdInteraction;
import com.linkage.toptea.sysmgr.ovsd.Passwd;
import com.linkage.toptea.util.BeanUtil;

/**
 * ���ܣ���ע�澯������ ���ߣ� �˻� ʱ�䣺2011-02-10
 */
public class ConcernManagerImpl implements ConcernManager {

	// ��־��ӡ
	private Log logger = LogFactory.getLog(ConcernManagerImpl.class);	
	
	private static final String LOG_TYPE = "assistant";

	/** ҵ����־ */
	private BusinessLogger bussinessLogger;
	
	private final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

	/** ����ļ������� */
	private CallFileManager callFileManager;

	/** ��ɫ���Զ��������� */
	private CallUserManager callUserManager;
	
	/** �ؼ����Զ���������� */
	private CallKeyWordManager callKeyWordManager;

	/** �ؼ����Զ���������� */
	private CallConfigManager callConfigManager;
	
	private SpecialCallManager specialCallManager;

	public void setCallConfigManager( CallConfigManager callConfigManager ) {
		this.callConfigManager = callConfigManager;
	}
	
	public void setCallFileManager( CallFileManager callFileManager ) {
		this.callFileManager = callFileManager;
	}
	
	public void setCallUserManager( CallUserManager callUserManager ) {
		this.callUserManager = callUserManager;
	}

	/** ����ҵ����־ģ���� */
	public void setBusinessLogger(BusinessLogger bussinessLogger) {
		this.bussinessLogger = bussinessLogger;
	}
	
	/** �Զ������Ϣ */
	private static ConcurrentHashMap<String, List<AutoCallAlarmInfo>> autoCallInfo 
									= new ConcurrentHashMap<String, List<AutoCallAlarmInfo>>();
	
	public Map<String, List<AutoCallAlarmInfo>>  getCallInfo() {
		return autoCallInfo;
	}
	
	// Spring�������ݿ�
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private String callPath;

	public void setCallPath(String _callpath) {
		this.callPath = _callpath;
	}

	public String getCallPath() {
		return this.callPath;
	}

	private String callto;
	private String callback;

	public String getCallto() {
		return callto;
	}

	public void setCallto(String callto) {
		this.callto = callto;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	private String callTotal;

	/**
	 * ����ÿ���������
	 * �ȴӱ��ж�ȡ����һ���Ѿ��趨�õ����ݣ����û���趨ֵȡ�����ļ�ֵ
	 * @param _callTotal
	 */
	public void setCallTotal(String _callTotal) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " SELECT TOTAL FROM TF_TT_ALARM_CALLCOUNT ORDER BY  CTIME DESC ";
		ResultSet rs = null;
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				Call.callTotal = rs.getInt("TOTAL") <= 0 ? Integer.parseInt(_callTotal) : rs.getInt("TOTAL");
				this.callTotal = Call.callTotal + "";
			} else {
				try {
					Call.callTotal = Integer.parseInt(_callTotal);
					this.callTotal = _callTotal;
				} catch (Exception e) {
					logger.error("", e);
				}
			}

		} catch (Exception e) {
			logger.error("setCallTotal", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
	}

	private String backTemp = "";

	public void setBackTemp(String _backTemp) {
		this.backTemp = _backTemp;
	}

	public String getBackTemp() {
		return this.backTemp;
	}

	public String getCallTotal() {
		return this.callTotal;
	}

	private OvsdInteraction ovsdInteraction;

	public OvsdInteraction getOvsdInteraction() {
		return this.ovsdInteraction;
	}

	public void setOvsdInteraction(OvsdInteraction _ovsdInteraction) {
		this.ovsdInteraction = _ovsdInteraction;
	}

	private AlarmManager alarmManager;

	public void setAlarmManager(AlarmManager _alarmManager) {
		this.alarmManager = _alarmManager;
	}

	public AlarmManager getAlarmManager() {
		return this.alarmManager;
	}

	private ResponsibleManager responsibleManager;
	public void setResponsibleManager(ResponsibleManager _responsibleManager){
		this.responsibleManager = _responsibleManager;
	}
	
	private UserManager userManager ;
	public void setUserManager(UserManager  _userManager){
		this.userManager = _userManager;
	}
	
	private ConfigManager configManager;
	public void setConfigManager(ConfigManager _configManager){
		this.configManager = _configManager;
	}
	
	// ��ʼ����
	public void init() {
		
		initCache();
		
		initPhoneConfig();

		//��������������ݼ�
		initRepVacation();
		
		// ɨ�赱ǰ�б�澯�����¼���״̬���¼���״̬�ǡ��ѽ���������رա��������¼������ʷ��
		new MoveConcernAlarmThread().start();
		
		// �Զ�����ļ�����
		new ConcernAlarmThread().start();
		
		// �����ִ�ļ�����
		new CheckCallBackThread().start();
		
		// �������
		new CallControlThread().start();
	}
	
	/** ��ʼ���Զ�����ڴ����� */
	public void initCache( ) {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " SELECT * FROM TF_TT_ALARM_CALLPHONE WHERE STATUS <> 1 AND ISHOW =1 AND MO IS NOT NULL ";
		ConcurrentHashMap<String, List<AutoCallAlarmInfo>> temp
				= new ConcurrentHashMap<String, List<AutoCallAlarmInfo>>();
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				AutoCallAlarmInfo info = new AutoCallAlarmInfo();
				info.setId( rs.getString("ALARM_ID") );
				info.setCallid( rs.getString("CALLID") );
				info.setCaller( rs.getString("CALLER") );
				info.setPhone( rs.getString("PHONE") );
				info.setNum(  rs.getInt("NUM")  );
				info.setStatus( rs.getInt("STATUS") );
				info.setIsconcern(  rs.getInt("ISCONCERN")  );
				info.setIsshow(  rs.getInt("ISHOW")  );
				info.setCallTime( rs.getTimestamp("CTIME") );
				info.setMoId( rs.getString("MO") );
				List<AutoCallAlarmInfo> list = temp.get( info.getCallid() );
				if ( list == null ) {
					list = new ArrayList<AutoCallAlarmInfo>();
				}
				list.add( info );
				temp.put( info.getCallid(), list );
			}
			autoCallInfo.clear();
			autoCallInfo = temp;
		} catch (Exception e) {
			logger.error("initCache", e);
		} finally {
			BeanUtil.closeResource(rs, ps, conn);
		}
		
	
		
	}

	/** ����ÿ������澯���� */
	public void resetCallCount(int count) {
		Connection conn = null;
		PreparedStatement ps = null;

		// ����ͳ��
		String sql = "merge into tf_tt_alarm_callcount t1"
				+ " using (select "
				+ "? as ctime,  ? as total from dual) t2"
				+ " on (to_char(t1.ctime,'yyyymmdd') = t2.ctime)"
				+ " when matched then"
				+ " update  set t1.total = t2.total"
				+ " when not matched then"
				+ " insert (ctime,num, total) values (to_date(t2.ctime,'yyyymmdd'),0, t2.total)";
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, df.format(new Date()));
			ps.setInt(2, count);
			ps.executeUpdate();
			Call.callTotal = count;
			this.callTotal = count+ "";
		} catch (Exception e) {
			logger.error("resetCallCount", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
	}
	
	public String batchPhone(String[] alarmIds) throws Exception {
		Connection conn = null;
		String str = "";
		PreparedStatement ps = null;
		String sql = "";
		try {
			conn = this.dataSource.getConnection();
			sql = "update TF_TT_ALARM_CONCERN set isshow=0 where alarmid=?";
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < alarmIds.length; i++) {
				ps.setString(1, alarmIds[i]);
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (Exception e) {
			str = e.getMessage();
			logger.error("batchPhone", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return str;
	}
	//��ʼ���������ݼ�
	private void initRepVacation(){
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "select * from TF_TT_ALARM_REPVACATION where ISUSE=1";
		ResultSet rs = null;
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				RepVacation rv  = new RepVacation();
				rv.setUserId(rs.getString("userId"));
				if (rs.getTimestamp("startTime")!=null)	rv.setStartTime(rs.getTimestamp("startTime").getTime());
				if (rs.getTimestamp("endTime")!=null)	rv.setEndTime(rs.getTimestamp("endTime").getTime());
				rv.setIsUse(1);
				RepVacationManager.addVacation(rv.getUserId(), rv);
			}

		} catch (Exception e) {
			logger.error("initConfigFilter", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		
	}
	

	//�����������
	private void initPhoneConfig(){
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		ResultSet rs = null;
		try {
			conn = this.dataSource.getConnection();
			sql = "select * from TF_TT_ALARM_PHONECONF";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString("configid") == null) continue;
				if (rs.getString("configid").equals("FAIL_NUM")){
					if (rs.getString("value")==null) PhoneConfigManager.FAIL_NUM = 3;
					else PhoneConfigManager.FAIL_NUM = Integer.parseInt(rs.getString("value"));						
				}else if (rs.getString("configid").equals("ALARM_NUM")){
					if (rs.getString("value")==null) PhoneConfigManager.ALARM_NUM = 30;
					else PhoneConfigManager.ALARM_NUM = Integer.parseInt(rs.getString("value"));
				}else if (rs.getString("configid").equals("ALARM_GRADE")){
					if (rs.getString("value")==null) PhoneConfigManager.ALARM_GRADE = ">=3";
					else PhoneConfigManager.ALARM_GRADE = rs.getString("value");
				} else if ( "TIME".equals(rs.getString("configid")) ) {
					if (rs.getString("value")==null) PhoneConfigManager.TIME = 40;
					else PhoneConfigManager.TIME = Integer.parseInt(rs.getString("value"));
				} else if ( "TIMERUN".equals(rs.getString("configid")) ) {
					if (rs.getString("value")==null) PhoneConfigManager.TIME_RUN = 1;					
					else PhoneConfigManager.TIME_RUN = Integer.parseInt(rs.getString("value"));
				} else if ( "CALL_TIME".equals(rs.getString("configid")) ) {
					if (rs.getString("value")==null) {
						PhoneConfigManager.CALL_TIME = 1;					
					} else {
						String callTime = rs.getString("value").split(":")[0];
						if ( Integer.parseInt(callTime) == 1 ) {
							PhoneConfigManager.CALL_TIME = 1;
							for ( int i = 0; i < PhoneConfigManager.CALL_TIME_INTERVEL.length; i++ ) {
								PhoneConfigManager.CALL_TIME_INTERVEL[i] = 0;
							}
						} else {
							PhoneConfigManager.CALL_TIME = Integer.parseInt(callTime);
							String[] intervels = rs.getString("value").split(":")[1].split(",");
							for ( int i = 0; i < PhoneConfigManager.CALL_TIME_INTERVEL.length; i++ ) {
								if ( i < intervels.length ) {
									PhoneConfigManager.CALL_TIME_INTERVEL[i] = Integer.parseInt( intervels[i] );
								}  else  {
									PhoneConfigManager.CALL_TIME_INTERVEL[i] = 0;
								}
								
							}
						}
					}
				}
			}
			rs.close();
		} catch (Exception e) {
			logger.error("initPhoneConfig", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		
	}

	/**
	 * �����ϵ�绰
	 * 
	 * @param phoner
	 *            ��绰��
	 * @param alarmId
	 *            �澯���
	 * @return
	 */
	public List<Call> getPhone(String alarmId) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		List<Call> items = new ArrayList<Call>();
		ResultSet rs = null;
		Map<String, String> map = getCallUser(alarmId);
		try {
			conn = this.dataSource.getConnection();
			sql = "select * from TF_TT_ALARM_CONCERN_CALL where ALARMID=? order by calltime asc";
			ps = conn.prepareStatement(sql);
			ps.setString(1, alarmId);
			rs = ps.executeQuery();
			while (rs.next()) {
				Call c = new Call();
				c.setAlarmId(rs.getString("alarmId"));
				c.setPhoner(rs.getString("phoner"));
				c.setCalltime(rs.getTimestamp("calltime").getTime());
				c.setContext(rs.getString("context"));
				c.setMsg(rs.getString("msg"));
				String name = map.get(rs.getString("phone"));
				if (name == null)
					c.setPhone(rs.getString("phone"));
				else
					c.setPhone(name + "[" + rs.getString("phone") + "]");
				c.setStatus(rs.getInt("STATUS"));
				items.add(c);
			}
		} catch (Exception e) {
			logger.error("getPhone", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return items;
	}

	private Map<String, String> getCallUser(String alarmId) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		Map<String, String> items = new HashMap<String, String>();
		ResultSet rs = null;
		try {
			conn = this.dataSource.getConnection();
			sql = "select u.* from TF_TT_ALARM_CONCERN_CALL c,uportal.cas_user u where c.phone=u.MOBILE and c.ALARMID=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, alarmId);
			rs = ps.executeQuery();
			while (rs.next()) {
				items.put(rs.getString("MOBILE"), rs.getString("name"));
			}
		} catch (Exception e) {
			logger.error("getPhone", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return items;
	}

	/**
	 * ��ȡ������SQL
	 * 
	 * @param filter
	 * @return
	 */
	private String getWhereSQL(ConcernFilter filter) {
		String sql = "";
		if (filter == null)
			return "";
		if (filter.getIsshow() != -1)
			sql += " and b.isshow=" + filter.getIsshow();
		if (filter.getAlarmId() != null) {
			sql += " and a.alarm_id='" + filter.getAlarmId() + "'";
		}
		if (filter.getPath() != null && !filter.getPath().equals("")) {
			sql += " and (a.path='" + filter.getPath() + "' or a.path like '"
					+ filter.getPath() + ".%')";
		}
		if (filter.getContent() != null && !filter.getContent().equals("")) {
			sql += " and a.ALARM_CONTENT like '%" + filter.getContent() + "%'";
		}
		sql += " and a.ALARM_CURRENTGRADE>=" + filter.getGrade();
		if (filter.getStarttime() != null && !filter.getStarttime().equals("")) {
			sql += " and a.ALARM_LASTOCCURTIME>=to_date('"
					+ filter.getStarttime() + "','yyyy-mm-dd hh24:mi:ss')";
		}

		if (filter.getEndtime() != null && !filter.getEndtime().equals("")) {
			sql += " and a.ALARM_LASTOCCURTIME<=to_date('"
					+ filter.getEndtime() + "','yyyy-mm-dd hh24:mi:ss')";
		}
		return sql;
	}

	/**
	 * 
	 * @param rsת��ɸ澯��
	 * @return com.linkage.toptea.sysmgr.fm.AlarmInfo
	 * @throws SQLException
	 */
	private AlarmInfo setAlarmInfoFromResultSet(ResultSet rs)
			throws SQLException {
		
		AlarmInfo alarm = new AlarmInfo();
		try{
			alarm.setId(rs.getString("ALARM_ID"));
			alarm.setMoId(rs.getString("MO_ID"));
			alarm.setSource(rs.getString("SOURCE"));
			alarm.setCode(rs.getString("ALARM_CODE"));
			alarm.setContent(rs.getString("ALARM_CONTENT"));
			alarm.setOriginalGrade(rs.getInt("ALARM_ORIGINALGRADE"));
			alarm.setCurrentGrade(rs.getInt("ALARM_CURRENTGRADE"));
	
			Timestamp ts = rs.getTimestamp("ALARM_FIRSTOCCURTIME");
			alarm.setFirstOccurTime((ts == null) ? 0 : ts.getTime());
	
			ts = rs.getTimestamp("ALARM_LASTOCCURTIME");
			alarm.setLastOccurTime((ts == null) ? 0 : ts.getTime());
	
			alarm.setCount(rs.getInt("ALARM_COUNT"));
			alarm.setState(rs.getInt("ALARM_STATE"));
	
			alarm.setDischargeUser(rs.getString("ALARM_DISCHARGEUSER"));
	
			ts = rs.getTimestamp("ALARM_DISCHARGETIME");
			alarm.setDischargeTime((ts == null) ? 0 : ts.getTime());
	
			alarm.setWorkId(rs.getString("WORK_ID"));
			alarm.setOwnerId(rs.getString("OWNER_ID"));
			alarm.setMainType(rs.getInt("MAIN"));
			alarm.setMethod(rs.getString("METHOD"));
	
			alarm.setCaseId(rs.getString("CASE_ID"));
	
			alarm.setCreatorId(rs.getString("CREATOR_ID"));

			alarm.setAdditionalInfo(rs.getString("ADDITIONAL_INFO"));
			if (rs.getMetaData().getColumnCount() >= 22) {
				alarm.setPath(rs.getString("PATH"));
			}
		}catch(SQLException e){
			throw e;
		}
		return alarm;
	}

	/**
	 * ��ȡ����������
	 * 
	 * @param filter
	 * @return
	 */
	public int getConcernCount(ConcernFilter filter) {
		int count = 0;
		String sql = "select count(*) as c from TF_TT_ALARM a,TF_TT_ALARM_CONCERN b where a.alarm_id=b.alarmid "
				+ getWhereSQL(filter);
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next())
				count = rs.getInt("c");
			rs.close();

		} catch (Exception e) {
			logger.error("��ȡ����������", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return count;
	}

	/**
	 * ��Ӹ澯�����ע�澯
	 * 
	 * @param alarmIds
	 * @param memo
	 * @throws Exception
	 */
	public void addConcernAlarm(String alarmId, String memo) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		ResultSet rs = null;
		UserTransaction ut = null;
		boolean isRemove = false;
		boolean isShow = false;
		try {

			conn = this.dataSource.getConnection();
			ut = new UserTransaction(conn);
			ut.begin();

			sql = "select * from TF_TT_ALARM_CONCERN where ALARMID=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, alarmId);
			rs = ps.executeQuery();
			if (rs.next()) {
				isRemove = true;
				isShow = (rs.getInt("isshow") == 1 ? true : false);
			}
			rs.close();
			ps.close();

			// �澯�Ѿ��ڹ�ע�б��д��ڲ����Ѿ�������ˣ��������ԭ�澯����ʷ���ٽ��뵱ǰ��ע��
			if (isRemove && isShow == false) {
				sql = "insert into tf_tt_alarm_concern_his select * from tf_tt_alarm_concern a where a.ALARMID=? and a.isshow=0";
				ps = conn.prepareStatement(sql);
				ps.setString(1, alarmId);
				ps.executeUpdate();
				ps.close();

				sql = "delete from tf_tt_alarm_concern a where a.ALARMID=? and a.isshow=0";
				ps = conn.prepareStatement(sql);
				ps.setString(1, alarmId);
				ps.executeUpdate();
				ps.close();

				sql = "insert into TF_TT_ALARM_CONCERN(ALARMID,isshow,REASON) values(?,1,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, alarmId);
				ps.setString(2, memo);
				ps.executeUpdate();
				ps.close();

			} else if (isRemove == false) {

				sql = "insert into TF_TT_ALARM_CONCERN(ALARMID,isshow,REASON) values(?,1,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, alarmId);
				ps.setString(2, memo);
				ps.executeUpdate();
				ps.close();
			}

			ut.commit();

		} catch (Exception e) {
			logger.error("��Ӹ澯�����ע�澯", e);
			try {
				ut.rollback();
			} catch (Exception ex) {
				logger.error("��Ӹ澯�����ע�澯", ex);
			}
		} finally {
			BeanUtil.closeResource(ps, conn);
		}

	}

	/**
	 * ��ѯ��ע�澯��Ϣ
	 * 
	 * @param AlarmFilterInfo
	 *            �澯������
	 * @param start
	 *            ��ʼ
	 * @param limit
	 *            ÿҳ��ʾ����
	 */
	public List<ConcernInfo> findConcernAlarmByFilter(ConcernFilter filter,
			int start, int limit) {
		List<ConcernInfo> items = new ArrayList<ConcernInfo>();
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";

		ResultSet rs = null;
		try {
			conn = this.dataSource.getConnection();
			sql = "select a.*,b.* from TF_TT_ALARM a,TF_TT_ALARM_CONCERN b where a.alarm_id=b.alarmid "
					+ getWhereSQL(filter)
					+ " order by a.WORK_ID asc,b.ISPHONE asc,a.ALARM_LASTOCCURTIME desc";
			sql = "SELECT t2.*,FN_GET_MOPATH(T2.path) as pathname FROM (SELECT ROWNUM NO,T.* FROM ("
					+ sql
					+ ") T WHERE ROWNUM < "
					+ (start + limit)
					+ ") T2 WHERE NO>=" + start;

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				ConcernInfo ci = new ConcernInfo();
				ci.setAlarmId(rs.getString("ALARMID"));
				ci.setIsPhone(rs.getInt("ISPHONE"));
				ci.setIsshow(rs.getInt("isshow"));
				ci.setMoId(rs.getString("MO_ID"));
				ci.setPhoner(rs.getString("PHONER"));
				ci.setStatus(rs.getInt("STATUS"));
				ci.setFailNum(rs.getInt("FAILNUM"));
				ci.setPathname(rs.getString("pathname"));
				Timestamp ts = rs.getTimestamp("PHONETIME");
				if (ts != null)
					ci.setPhonetime(ts.getTime());
				ci.setAlarm(setAlarmInfoFromResultSet(rs));
				items.add(ci);
			}
			rs.close();
		} catch (Exception e) {
			logger.error("��ѯ��ע�澯��Ϣ", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return items;
	}

	/**
	 * �鿴�澯��ʷ��ע
	 * 
	 * @param alarmId
	 * @return
	 * @throws Exception
	 */
	public List<ConcernInfo> findHisConrecnById(String alarmId)
			throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		ResultSet rs = null;
		List<ConcernInfo> items = new ArrayList<ConcernInfo>();
		try {
			conn = this.dataSource.getConnection();
			sql = "select * from TF_TT_ALARM_CONCERN where ALARMID=? union select * from TF_TT_ALARM_CONCERN_his where ALARMID=?";
			sql = "select * from (" + sql + ") order by ctime";
			ps = conn.prepareStatement(sql);
			ps.setString(1, alarmId);
			ps.setString(2, alarmId);
			rs = ps.executeQuery();
			while (rs.next()) {
				ConcernInfo ci = new ConcernInfo();
				ci.setAlarmId(rs.getString("ALARMID"));
				ci.setIsPhone(rs.getInt("ISPHONE"));
				ci.setIsshow(rs.getInt("isshow"));
				ci.setPhoner(rs.getString("PHONER"));

				Timestamp ts = rs.getTimestamp("PHONETIME");
				if (ts != null)
					ci.setPhonetime(ts.getTime());

				Timestamp ts2 = rs.getTimestamp("ctime");
				if (ts2 != null)
					ci.setCtime(ts2.getTime());

				if (rs.getString("MEMO") != null)
					ci.setMemo(rs.getString("MEMO"));
				if (rs.getString("REASON") != null)
					ci.setReason(rs.getString("REASON"));
				items.add(ci);
			}
			rs.close();

		} catch (Exception e) {
			logger.error("�鿴�澯��ʷ��ע", e);
			throw e;
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return items;
	}

	/**
	 * ��ע�澯��ͬ��
	 */
	public void ConcernSync() {
		
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		String sql = "";
		UserTransaction ut = null;
		ResultSet rs = null;
		int count = 0;
		try {
			
			conn = this.dataSource.getConnection();
			// �����澯�����ע�澯
            sql = "select count(*) as c from tf_tt_alarm a where ALARM_CURRENTGRADE"+PhoneConfigManager.ALARM_GRADE
            		+ " and  not exists (select * from tf_tt_alarm_concern b where a.alarm_id=b.alarmid) "
            		+ " and not exists (select * from tf_tt_alarm_callphone c where a.alarm_id=c.alarm_id)"
            		+ " and not exists(select * from tf_tt_alarm_concern_his d where a.alarm_id=d.alarmid)"
            		+ " and a.alarm_receivetime > sysdate - 0.1 ";

            ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) count = rs.getInt("c");
			rs.close();
			ps.close();

			logger.debug("�Զ�����澯 ��ʼ����="+count+"�趨����="+PhoneConfigManager.ALARM_NUM);
			
			// �鿴������������Ƿ��ʼ����û�г�ʼ������ʼ��ʼ��
			lookTodayCall();

			//���С�ڹ涨������
			if (count<=PhoneConfigManager.ALARM_NUM && count>0){
	
				
				 String sql2 = "INSERT INTO TF_TT_ALARM_CALLPHONE" +
				 		"(ALARM_ID,CALLID,CALLER,PHONE,STATUS,NUM,ISCONCERN,ISHOW,CTIME,MO) VALUES ( ?,?,?,?,0,1,0,1,?,? )";
				 ps2 = conn.prepareStatement(sql2);
				 sql = "select  * from tf_tt_alarm a where ALARM_CURRENTGRADE"+PhoneConfigManager.ALARM_GRADE +
		            		" and  not exists (select * from tf_tt_alarm_concern b where a.alarm_id=b.alarmid) "
		            		+" and not exists (select * from tf_tt_alarm_callphone c where a.alarm_id=c.alarm_id)"
		            		+ " and not exists(select * from tf_tt_alarm_concern_his d where a.alarm_id=d.alarmid)";
				 ps = conn.prepareStatement(sql);
				 rs = ps.executeQuery();
				 while (rs.next()) {
					String moId = rs.getString("MO_ID");

					logger.debug("�Զ�����澯 alarmId=" + rs.getString("ALARM_ID") + ";moId=" + moId);
					
					PhoneFilterInfo filter = PhoneFilterManager.getFilter(moId, PhoneFilterInfo.FILTER_USE );

				
					// û�й�����
					if (filter == null) { continue; }
					
					logger.info("�Զ�����澯 ͨ������Ϊ=" + filter.getMoId());
					String key = filter.getKey();

					// �����в������ؼ��ֵ�
					if (key != null && !key.equals("")) {
						if (rs.getString("ALARM_CONTENT").indexOf(key) == -1)
							continue;
					}
					logger.debug("�Զ�����澯 ͨ���ؼ���Ϊ=" + key);
					
					// ������
					String reper = null;	
					ResponsibleInfo info = responsibleManager.getResponsibleInfo(moId);

					// �ö���û������������
					if (info == null) { continue; }

					//ƽ̨���ҵ���������˶����ڵĳ��ϣ����ؼ������
					if ((info.getResponsible1() != null && !info.getResponsible1().equals(""))
							&& (info.getResponsible3() != null && !info.getResponsible3().equals(""))) {
						//continue; delete by huangzh ���ؼ������
						AlarmInfo alarm =  new AlarmInfo();
						alarm.setId( rs.getString("ALARM_ID") );
						alarm.setMoId( rs.getString("MO_ID") );
						alarm.setPath( rs.getString("PATH") );
						alarm.setContent( rs.getString("ALARM_CONTENT") );
						alarm.setSource( rs.getString("SOURCE") );
						alarm.setCode( rs.getString("ALARM_CODE") );

						// �Ƿ�����ؼ���У��
						int r = callKeyWordManager.check(alarm);
						if ( r == 1 ) { reper = info.getResponsible1(); }
						if ( r == 2 ) { reper = info.getResponsible2(); }
						if ( r == 3 ) { reper = info.getResponsible3(); }
						if ( r == 4 ) { reper = info.getResponsible4(); }
						if ( r == 5 ) { reper = info.getResponsible5(); }
						if ( r == 6 ) { reper = info.getResponsible6(); }
						if ( r == 7 ) { reper = info.getResponsible7(); }
						if ( r == 8 ) { reper = info.getResponsible8(); }
					} else {
						reper = callUserManager.check(filter, info);
					}
					if ( null == reper ) {
						logger.info(" reper == null, continue ");
						continue; 
					}

					User user = userManager.getUser(reper);

					if (user == null) {
						logger.info(" user == null, continue ");
						continue; 
					}
					if (user.getMobile() == null) {
						logger.info("��" + user.getName() + "����澯ʧ�ܣ���Ϊ��û���ֻ���");
						continue;
					}

					if (Call.callTotal <= callFileManager.getCallCount()) {
						logger.error("���Ѿ��ﵽ���������" + Call.callTotal + "�޷����Զ������");
						break;
					}

					long  callid = callFileManager.writeCallFile(user.getMobile());
					
					
					logger.info("------------------" + callid + "------------------");
					if ( callid == 0 ) { continue; }
					logger.info("------------------׼�����͹���------------------");
					this.sendOvsd( rs.getString("ALARM_ID"),reper );
					logger.info("------------------���͹������------------------");

					AlarmInfo alarm = setAlarmInfoFromResultSet(rs);
					String context = "�澯����:" + alarm.getContent() + "\n" + "�澯��Դ��:"
							+ configManager.getNamingPathById(alarm.getMoId()) + "\n��һ�η���ʱ��:"
							+ ChangeDateType.parseLongToStr(alarm.getFirstOccurTime(), "yyyy-MM-dd HH:mm:ss")
							+ "\n���һ�η���ʱ��:"
							+ ChangeDateType.parseLongToStr(alarm.getLastOccurTime(), "yyyy-MM-dd HH:mm:ss")
							+ "\n�ظ�����:" + alarm.getCount();
					// ���Ͷ���
					sendSms(conn, user.getMobile(), context);
					
					logger.debug("�Զ�����澯 --���Ͷ��ųɹ�");

					// ��������ϸ
					AutoCallAlarmInfo callInfo = new AutoCallAlarmInfo ();
					callInfo.setId( alarm.getId() );
					callInfo.setCallid(callid+"");
					callInfo.setCaller(reper);
					Timestamp callTime = new Timestamp( System.currentTimeMillis() );
					callInfo.setCallTime(callTime);
					callInfo.setMoId(alarm.getMoId());
					callInfo.setPhone(user.getMobile());
					// �������
					callInfo.setNum( 1 );
					// ���״̬������� 0
					callInfo.setStatus( 0 );
					// �Ƿ���ʾ 1����ʾ
					callInfo.setIsshow(1);
					// �Ƿ��ѹ�ע 0 �� δ��ע
					callInfo.setIsconcern(0);
					
					// �Զ������¼
					ps2.setString(1, callInfo.getId() );
					ps2.setString(2, callInfo.getCallid() );
					ps2.setString(3, callInfo.getCaller() );
					ps2.setString(4, callInfo.getPhone() );
					ps2.setTimestamp(5, callTime );
					ps2.setString(6, callInfo.getMoId() );
					ps2.addBatch();
					// �������У�飨�Զ����ͬʱ���ע�澯�б�
					if ( specialCallManager.check(alarm.getPath() )) {
						addConcernAlarm( alarm.getId() ); 
					}
					logger.debug("�Զ�����澯 --�Զ�����������ݿ����");
		
					List<AutoCallAlarmInfo> callInfos = autoCallInfo.get(callInfo.getCallid());
					if ( callInfos == null ) {
						callInfos = new ArrayList<AutoCallAlarmInfo>();
						callInfos.add(callInfo);
						autoCallInfo.put(callInfo.getCallid(), callInfos);
					} else {
						callInfos.add(callInfo);
					}
					
					addCallDetail( callInfo );

				}
				 rs.close();
				 ps.close();
				 ps2.executeBatch();
				 ps2.close();
				
			} else {
				if (count>0) logger.info( "ȡ�ø澯���� " + count + " >= �趨���� " + PhoneConfigManager.ALARM_NUM );
			}
			
			//�����ע�澯
			sql = "insert into tf_tt_alarm_concern(alarmid) select ALARM_ID from tf_tt_alarm a where a.ALARM_CURRENTGRADE>=? "
					+ AstConfig.getPathFilterSQL()
					+ " and not exists (select * from tf_tt_alarm_concern b where a.alarm_id=b.alarmid) and not exists (select * from tf_tt_alarm_concern_his h where  h.ctime>sysdate-"
					+ AstConfig.TRACK_TIME
					+ "/24 and isphone=1 and h.alarmid=a.alarm_id) "
					+ "and not exists (select * from tf_tt_alarm_callphone c where a.alarm_id=c.alarm_id)";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, AstConfig.CONCREN_GRADE);
			ps.executeUpdate();

			try {
				ut = new UserTransaction(conn);
				ut.begin();
	
				// �澯�Զ������--��ע�澯������ʷ��
				sql = "insert into tf_tt_alarm_concern_his select * from tf_tt_alarm_concern a where ";
				// �澯�Ѿ����ڸ澯�����ˣ��������
				sql += " not exists (select * from tf_tt_alarm b where b.alarm_id=a.alarmid)";
				// �澯��������
				sql += " or exists (select * from tf_tt_alarm c where c.ALARM_CURRENTGRADE<? and c.alarm_id=a.alarmid and a.REASON is null)";
				// �澯���Ѿ���ע����
				// sql+=" or (a.ISPHONE=1  and exists (select * from tf_tt_alarm d where d.alarm_id=a.alarmid and d.WORK_ID is not null) )";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, AstConfig.CONCREN_GRADE);
				ps.executeUpdate();
	
				// ɾ����ǰ��ע�澯
				// ���Ѿ�������ʷ��ɾ�� ������˼����½��ĸ澯ɾ��
				sql = "delete from tf_tt_alarm_concern a where  ";
				// �澯�Ѿ����ڸ澯�����ˣ��������
				sql += " not exists (select * from tf_tt_alarm b where  b.alarm_id=a.alarmid) ";
				// �澯��������
				sql += " or exists (select * from tf_tt_alarm c where c.ALARM_CURRENTGRADE<? and c.alarm_id=a.alarmid and a.REASON is null)";
				// �澯���Ѿ���ע����
				// sql+=" or (a.ISPHONE=1  and exists (select * from tf_tt_alarm d where d.alarm_id=a.alarmid and d.WORK_ID is not null) )";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, AstConfig.CONCREN_GRADE);
				ps.executeUpdate();
				
				ut.commit();
			} catch (Exception ex) {
				ut.rollback();
				logger.error("ɾ���澯�����ע�澯", ex);
			}
			
		} catch (Exception e) {
			logger.error("��ע�澯��ͬ��", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
	}

	private void addConcernAlarm(String alarmId) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "insert into tf_tt_alarm_concern(alarmid) values ( ? ) ";
		try {

			conn = this.dataSource.getConnection();
			// �����ע�澯
			ps = conn.prepareStatement(sql);
			ps.setString(1, alarmId);
			ps.executeUpdate();
		} catch (Exception e) {
			logger.error("��ӹ�ע�澯", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
	}
	
	/**
	 * ȡ����ע�澯
	 * 
	 * @param alarmIds
	 *            �澯���
	 * @param memo
	 *            ȡ��ԭ��
	 * @throws Exception
	 */
	public void removeConcern(String[] alarmIds, String memo) throws Exception {
		String sql = "update TF_TT_ALARM_CONCERN set isshow=0,memo=? where alarmid=?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < alarmIds.length; i++) {
				ps.setString(1, memo);
				ps.setString(2, alarmIds[i]);
				ps.addBatch();
			}
			ps.executeBatch();

		} catch (Exception e) {
			logger.error("ȡ����ע�澯", e);
			throw e;
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
	}

	/**
	 * �޸�����
	 * 
	 * @param cg
	 * @param ct
	 * @param tg
	 * @param tt
	 * @param tnt
	 */
	public void setConfig(int cg, int ct, int tg, int tt, int tnt, String pf, String tp)
			throws Exception {
		AstConfig.CONCREN_TIME = ct;
		AstConfig.CONCREN_GRADE = cg;
		AstConfig.TRACK_GRADE = tg;
		AstConfig.TRACK_NEXT_TIME = tnt;
		AstConfig.TRACK_TIME = tt;
		AstConfig.PATH_FILTER = pf;
		AstConfig.TRACK_PATH_FILTER_PATH = "";
		if ( null != tp && !"".equals(tp)) {
			String[] moIds = tp.split(";");
			for ( String moId : moIds ) {
				AstConfig.TRACK_PATH_FILTER_PATH += configManager.getNamingPathById(moId) + ";";
			}
		}
		AstConfig.TRACK_PATH_FILTER_ID = tp;
		try {
			this.changeConfig();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * �޸ı���Ĳ��������ݿ�
	 */
	private void changeConfig() throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		try {
			conn = this.dataSource.getConnection();
			sql = "update TF_TT_ALARM_ASSISTANT_CONF set CONCRENGRADE=?,CONCRENTIME=?," +
					"TRACKGRADE=?,TRACKNEXTTIME=?,TRACKTIME=?,PATHFILTER=?, TRACKPATHFILTER=?";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, AstConfig.CONCREN_GRADE);
			ps.setInt(2, AstConfig.CONCREN_TIME);
			ps.setInt(3, AstConfig.TRACK_GRADE);
			ps.setInt(4, AstConfig.TRACK_NEXT_TIME);
			ps.setInt(5, AstConfig.TRACK_TIME);
			ps.setString(6, AstConfig.PATH_FILTER);
			ps.setString(7, AstConfig.TRACK_PATH_FILTER_ID);
			ps.executeUpdate();
			StringBuffer logStr = new StringBuffer();
			logStr.append("[CONCRENGRADE = " + AstConfig.CONCREN_GRADE + "],");
			logStr.append("[CONCRENTIME = " + AstConfig.CONCREN_TIME + "],");
			logStr.append("[TRACKGRADE = " + AstConfig.TRACK_GRADE + "],");
			logStr.append("[TRACKNEXTTIME = " + AstConfig.TRACK_NEXT_TIME + "],");
			logStr.append("[TRACKTIME = " + AstConfig.TRACK_TIME + "],");
			logStr.append("[PATHFILTER = " + AstConfig.PATH_FILTER + "],");
			logStr.append("[TRACKPATHFILTER = " + AstConfig.TRACK_PATH_FILTER_ID + "],");
			//  ��־
			bussinessLogger.log( LOG_TYPE, "TF_TT_ALARM_ASSISTANT_CONF", "�޸Ĺ�ע�澯���ã� " + logStr.toString() );
		} catch (Exception e) {
			logger.error("�澯�����ά����ʧ��", e);
			throw e;
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
	}

	public static boolean isBackCall = false;
	
	
	private void sendOvsd(String alarmId, String caller) throws Exception {
		Long workNumberLong;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			conn = this.dataSource.getConnection();
			sql = " SELECT * FROM TF_TT_ALARM WHERE ALARM_ID = ? AND  WORK_ID  IS NULL ";
			logger.info(alarmId + " ==== " + sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, alarmId);
			rs = ps.executeQuery();
			if (rs.next()) {
				AlarmInfo alarm = setAlarmInfoFromResultSet(rs);
				String title = rs.getString("ALARM_CONTENT");
				if ( title != null && title.length() > 80 ) {
					title = title.substring(0,70) + "...";
					logger.info("--------title----------" + title);
				}
				String context = "�澯����:" + alarm.getContent() + "\n" + "�澯��Դ��:"
						+ configManager.getNamingPathById(alarm.getMoId()) + "\n��һ�η���ʱ��:"
						+ ChangeDateType.parseLongToStr(alarm.getFirstOccurTime(), "yyyy-MM-dd HH:mm:ss")
						+ "\n���һ�η���ʱ��:"
						+ ChangeDateType.parseLongToStr(alarm.getLastOccurTime(), "yyyy-MM-dd HH:mm:ss") + "\n�ظ�����:"
						+ alarm.getCount();

				String ovsdUser = getOvsdUser(caller);

				workNumberLong = ovsdInteraction.addIncident(context, title, 
						alarm.getCurrentGrade(), alarm.getLastOccurTime(), "", "һ��", "��", null, null, ovsdUser);

				// �ɵ�ʧ������һ��
				if ( workNumberLong == null || workNumberLong == 0 ) {
					workNumberLong = ovsdInteraction.addIncident(context, title, 
						alarm.getCurrentGrade(), alarm.getLastOccurTime(), "", "һ��", "��", null, null, ovsdUser);
					logger.info("�ڶ����ɷ�������"+workNumberLong);
				}
				logger.debug(" �Զ�����澯 --�����ˣ� " + caller);
				logger.info(caller + " - " + ovsdUser + "--------������----------" + workNumberLong);
				if (workNumberLong != null) {
					// add by huangzh
					setWorkProcess(workNumberLong);
					alarm.setWorkId(Long.toString(workNumberLong));
					alarmManager.updateAlarm(alarm);
				}
			}
		} catch (Exception e) {
			logger.error("�ɷ���������", e);
			throw e;
		} finally {
			BeanUtil.closeResource(rs, ps, conn);
		}
	}
	
	/**
	 * ��cas�û�IDȡ��ovsd�û�
	 * @param casUser cas�û�ID
	 * @return ovsd�û�ID
	 */
	private String getOvsdUser( String casUser ) {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = dataSource.getConnection();
			String sql = " SELECT OVSD_USER_ID FROM UOVSD.CAS_OVSD_USER_MAPPING M WHERE M.CAS_USER_ID = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, casUser);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("OVSD_USER_ID");				
			}
		} catch (Exception e) {
			logger.error("getOvsdUser", e);
		} finally {
			BeanUtil.closeResource(rs, ps, conn);
		}
		return casUser;
	}

	// ����ص�
	public void backCall() throws Exception {

		// ���ص��߳�
		if (isBackCall) { return; } 
		
		isBackCall = true;

		Connection conn = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;

		// ���¹�ע�澯
		String sql1 = "update tf_tt_alarm_concern set status=?,ISSHOW=?,failNum=failNum+? where callid=? and ISSHOW=1";
		// ����ҳ���ֶ�����澯
		String sql2 = "update tf_tt_alarm_concern_call set STATUS=? where callid=?";
		// �����Զ�����澯��Ϣ
		String sql3 = "UPDATE TF_TT_ALARM_CALLPHONE SET STATUS=?, CTIME = ?  WHERE CALLID=?";

		String sql4 = " UPDATE TF_TT_CALLPHONE_DETAIL SET STATUS=?, BTIME=? WHERE CALLID=? ";

		BufferedReader reader = null;// ����BufferedReadker
		try {
			conn = this.dataSource.getConnection();
			conn.setAutoCommit(false);
			
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			
			// ȡ�÷����ļ�
			File file = new File(this.callPath + "/" + this.getCallback());
			// �����ļ����ڵĳ���
			if (file.exists()) {
				File[] fs = file.listFiles();
				try{
					if (fs != null && fs.length > 0) {
						
						ps1 = conn.prepareStatement(sql1);
						ps2 = conn.prepareStatement(sql2);
						ps3 = conn.prepareStatement(sql3);
						ps4 = conn.prepareStatement(sql4);

						for (int i = 0; i < fs.length; i++) {
	
							String fileName = fs[i].getName();

							logger.debug("���ڴ����ִ�ļ�" + fileName);
							String line = "";
							try {
								String extName = fileName.substring(fileName.length() - 3, fileName.length());
								
								if (!extName.equals("txt")) { continue; }
	
								int status = 0;
								reader = new BufferedReader(new FileReader(fs[i].getPath()));
								logger.info("[��ִ�ļ�]" + fileName  );								 
								while ((line = reader.readLine()) != null) {
									logger.debug("���ڴ����ִ����" + line);
									String[] call = line.split("~");
									if (call == null || call.length < 3) {
										reader.close();
										continue;
									}
									
									logger.info("[��ִ����]" + line);
									status = Integer.parseInt( call[1] );

									// �ж��������Ϊ1 ˵������ɹ���
									if (call != null && call.length >= 3 && call[1].equals("1")) {
										
										// ���¹�ע�澯
										ps1.setInt(1, 1);
										ps1.setInt(2, 0);
										ps1.setInt(3, 0);
										ps1.setLong(4, Long.parseLong(call[2]));
		
										// ����ҳ���ֶ�����澯
										ps2.setInt(1, status);
										ps2.setLong(2, Long.parseLong(call[2]));
										
										// �����Զ�����澯��Ϣ
										ps3.setInt(1, 1);
										ps3.setTimestamp(2, ts);
										ps3.setString(3, call[2]);

										ps1.addBatch();
										ps2.addBatch();
										ps3.addBatch();

										// ����������ļ�ɾ��
										callFileManager.deleteCallCtrlFile( Long.parseLong(call[2]) );
										
									} else { // ���ʧ�ܵĳ���
										// ���¹�ע�澯
										ps1.setInt(1, (status == 1 ? 1 : 2));
										ps1.setInt(2, (status == 1 ? 0 : 1));
										ps1.setInt(3, (status == 1 ? 0 : 1));
										ps1.setLong(4, Long.parseLong(call[2]));
		
										// ����ҳ���ֶ�����澯
										ps2.setInt(1, status);
										ps2.setLong(2, Long.parseLong(call[2]));
										
										// �����Զ�����澯��Ϣ
										ps3.setInt(1, -1);
										ps3.setTimestamp(2, ts);
										ps3.setString(3, call[2]);
										
										ps1.addBatch();
										ps2.addBatch();
										ps3.addBatch();

									}
									ps4.setInt(1, (status == 1 ? 1 : -1));
									ps4.setString(2, call[3] );
									ps4.setString(3, call[2] );
									ps4.addBatch();
									
									// �ڴ����ݴ���
									if ( status == 1 ) {
										autoCallInfo.remove(call[2]);
									} else {
										List<AutoCallAlarmInfo> callInfos = autoCallInfo.get(call[2]);
										if ( callInfos != null ) {
											for ( int m = 0; m < callInfos.size(); m++ ) {
												AutoCallAlarmInfo info = callInfos.get(m);
												info.setStatus(-1);
											}
										}
									}										
										
								}
								reader.close();
							} catch (Exception ex) {
								logger.error("��ִ���⣬��ִ�ļ���==" + fileName + "����ִ���ݣ�" + line, ex);
							}
						}
						ps1.executeBatch();
						ps2.executeBatch();
						ps3.executeBatch();	
						ps4.executeBatch();
						// add by huangzh �����������
						lookTodayCall();
						
					}
					for (int i = 0; i < fs.length; i++) {
						if ( null == fs[i] ) { continue; }
						fs[i].renameTo(new File(this.callPath + "/" + this.backTemp + "/" + fs[i].getName()));
						fs[i].delete();
					}
				} catch(Exception ex) {
					logger.error("",ex);
			    }
			}
			
			conn.commit();

			isBackCall = false;

		} catch (Exception e) {
			try{
				conn.rollback();
			}catch(Exception ee){
				logger.error("",ee);
			}
			logger.error("",e);
			throw e;
		} finally {
			isBackCall = false;
		    conn.setAutoCommit(true);
		    BeanUtil.closeResource(ps3);
			BeanUtil.closeResource(ps1, ps2, conn);
		}
	}

	/**
	 * ����������������Ƿ��Ѿ���ʼ����û�г�ʼ������ʼ��ʼ��
	 *
	 */
	public synchronized void lookTodayCall () {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = " SELECT * FROM TF_TT_ALARM_CALLCOUNT WHERE TO_CHAR(CTIME,'YYYYMMDD') = ? ";

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, df.format(new Date()));
			rs = ps.executeQuery();
			if ( !rs.next() ) {
				sql = " INSERT INTO TF_TT_ALARM_CALLCOUNT ( CTIME,NUM,TOTAL ) VALUES (?,0,?) ";
				ps = conn.prepareStatement(sql);
				ps.setTimestamp(1,  new Timestamp(System.currentTimeMillis()) );
				ps.setInt(2,  Call.callTotal );
				ps.executeUpdate();
				// ��ʼ�������������
				callFileManager.setFileCount(0);

			}
		} catch ( Exception e ) {
			logger.error( "lookTodayCall", e );
		} finally {
			BeanUtil.closeResource(rs, ps, conn);
		}
	}
	
	/**
	 * ��������ִ�߳�
	 */
	class CheckCallBackThread extends Thread {
		public CheckCallBackThread() {
			this.setName("CheckCallBackThread");
		}

		public void run() {

			while (true) {
				try {
					logger.debug("�����ִ�߳̿�ʼ-----");
					backCall();
					logger.debug("�����ִ�߳̽���-----");
				} catch (Exception e) {
					logger.error("CheckCallBackThread--�����ִ�ļ�ʧ�ܣ�", e);
				}
				try {
					// ����ִ��
					Thread.sleep(60 * 2 * 1000);
				} catch (Exception e) {
					logger.error("CheckCallBackThread--����ʧЧ", e);
				}
			}

		}

	}

	/**
	 * ���ܣ���ע�澯�߳�
	 * 
	 */
	class ConcernAlarmThread extends Thread {
		public ConcernAlarmThread() {
			this.setName("Concern_Thread");
		}

		public void run() {
			while (true) {
				try {
					logger.debug("��ע�澯ͬ���߳̿�ʼ-----");
					ConcernSync();
				} catch (Exception e) {
					logger.error("��ע�澯�߳�", e);
				}
				try {
					// ����ִ��
					Thread.sleep(AstConfig.CONCREN_TIME * 60 * 1000);
				} catch (Exception e) {
					logger.error("����ʧЧ", e);
				} finally {

				}
			}

		}
	}

	public void callPhone(String phone, String[] alarmIds, String context,
			String userId, String personcall, String title) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		UserTransaction ut = null;

		try {

			conn = this.dataSource.getConnection();
			ut = new UserTransaction(conn);
			ut.begin();
			//long callid = new Date().getTime();
			long callid = callFileManager.getCallId()[0];

			for (int i = 0; i < alarmIds.length; i++) {
				// ������״̬
				sql = "update tf_tt_alarm_concern set isshow=0,ISPHONE=1,PHONER=?, " 
					+ " PHONETIME = DECODE ( PHONETIME, null , sysdate,PHONETIME ),"
					+ " STATUS=?,callid=? where ALARMID=? and PHONER is null";
				ps = conn.prepareStatement(sql);
				ps.setString(1, userId);
				ps.setInt(2, 1);
				ps.setLong(3, callid + i);
				ps.setString(4, alarmIds[i]);
				ps.executeUpdate();

				// ��������¼
				sql = "insert into TF_TT_ALARM_CONCERN_CALL(ALARMID,PHONER,PHONE,STATUS,CONTEXT,MSG,TYPE,CALLID) values(?,?,?,?,?,?,?,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, alarmIds[i]);
				ps.setString(2, userId);
				ps.setString(3, phone);
				ps.setInt(4, 1);
				ps.setString(5, context);
				ps.setString(6, "��ϵ�ɹ�");
				ps.setString(7, personcall);
				ps.setLong(8, callid + i);
				ps.executeUpdate();
			}

			ut.commit();
		} catch (Exception e) {

			try {
				ut.rollback();
			} catch (Exception ex) {
				logger.error("callPhone", ex);
			}
			throw e;
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
	}

	private void sendSms(Connection conn, String phone, String content) {
		CallableStatement ps = null;
		try {
			ps = conn.prepareCall("{ call sp_send_sms(?,?,77)}");
			ps.setString(1, phone);
			ps.setString(2, content);
			ps.execute();
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	public void callPhone(String phone, String alarmId, String workid,String context, String userId, String personcall, String title) throws Exception {
		callPhone(phone,alarmId,workid,context,userId,personcall,title,null);
	}
	
	public void callPhone(String phone, String alarmId, String workid,String context, String userId, String personcall, String title,String caller)	throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		UserTransaction ut = null;
		ResultSet rs = null;
		boolean isCreatWork = false;
		String calluserId = caller;
		try {

			if (phone == null) { return; }
				
			conn = this.dataSource.getConnection();
			ut = new UserTransaction(conn);
			ut.begin();
			if (calluserId == null){
				sql = "select id from uportal.cas_user where MOBILE=?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, phone);
				rs = ps.executeQuery();
				if (rs.next()) calluserId = rs.getString("id");
				rs.close();
				ps.close();
			}

			if (!personcall.equals("1")) {
				if (Call.callTotal <= callFileManager.getCallCount()) {
					throw new Exception("���Ѿ��ﵽ���������" + Call.callTotal
							+ ",�޷��ٴ�����ˣ�������ֻ���ϵ��ʽ��");
				}
			}
			
			// ���ö��Ŵ洢����
			sendSms(conn, phone, context);
			
			int isCall = 1; // �Ƿ���ϵ�ɹ�
			String Msg = "";
			long callid = 0;
			
			// �Ƿ��ֶ���ϵ����
			if (!personcall.equals("1")) { // û��
				isCall = 3;
				if (title != null) {
					callid = callFileManager.writeCallFile(phone);
				} else {
					isCall = 2;
					Msg = "���ʧ�ܣ�û�к�������";
				}
			}
			
			// ������״̬
			sql = "update tf_tt_alarm_concern set ISPHONE=1,PHONER=?, "
				 + " PHONETIME = DECODE ( PHONETIME, null , sysdate,PHONETIME ), "
				 + " STATUS=?,callid=? where ALARMID=? and ISSHOW=1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setInt(2, isCall);
			ps.setLong(3, callid);
			ps.setString(4, alarmId);
			ps.executeUpdate();
			ps.close();

			// ��������¼
			sql = "insert into TF_TT_ALARM_CONCERN_CALL(ALARMID,PHONER,PHONE,STATUS,CONTEXT,MSG,TYPE,CALLID) values(?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, alarmId);
			ps.setString(2, userId);
			ps.setString(3, phone);
			ps.setInt(4, isCall);
			ps.setString(5, context);
			ps.setString(6, Msg);
			ps.setString(7, personcall);
			ps.setLong(8, callid);
			ps.executeUpdate();
			ps.close();
			
			Long workNumberLong;
			// �ж��Ƿ��Ѿ��ɵ���
			if (workid == null || workid.equals("") ) {
				logger.debug("û���ɷ�����");
				
				try {
					AlarmFilterInfo filter = new AlarmFilterInfo();
					filter.setAlarmIds(new String[] { alarmId });
					AlarmInfo[] ais = alarmManager.fecthAlarm(filter);
					if (ais != null && ais.length > 0 ) {
						AlarmInfo ai = ais[0];
						if (ai.getWorkId() == null || ai.getWorkId().equals("")) {
							if (calluserId == null || calluserId.equals("")){
						        workNumberLong = ovsdInteraction.addIncident(context, title, ai.getCurrentGrade(),	ai.getLastOccurTime(), "");
							}else{
								String ovsdUser = getOvsdUser(calluserId);
								workNumberLong = ovsdInteraction.addIncident(context, title, ai.getCurrentGrade(), ai.getLastOccurTime(), "", "һ��", "��", null, null,ovsdUser);
							}
							// �ɵ�ʧ������һ��
							if ( workNumberLong == null || workNumberLong == 0 ) {
								if (calluserId == null || calluserId.equals("")){
							        workNumberLong = ovsdInteraction.addIncident(context, title, ai.getCurrentGrade(),	ai.getLastOccurTime(), "");
								}else{
									String ovsdUser = getOvsdUser(calluserId);
									workNumberLong = ovsdInteraction.addIncident(context, title, ai.getCurrentGrade(), ai.getLastOccurTime(), "", "һ��", "��", null, null,ovsdUser);
								}
								logger.info("�ڶ����ɷ�������"+workNumberLong);
							}
							if (workNumberLong != null) {
                                // add by huangzh
                                setWorkProcess( workNumberLong );
								ai.setWorkId(Long.toString(workNumberLong));
								alarmManager.updateAlarm(ai);
								isCreatWork = true;
							}
						}
					}
				} catch (Exception e) {
					logger.error("alarmId==" + alarmId + "�ɷ�����ʧ�ܣ�", e);
				}
				
			} else
				isCreatWork = true;
			// ���¹�ע�澯���Ƿ���ʾ
			if (personcall.equals("1")) {
				sql = "update tf_tt_alarm_concern c  set c.ISSHOW=0 where STATUS=1 and c.alarmid=? and exists (select * from tf_tt_alarm a where a.alarm_id=? and a.WORK_ID is not null)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, alarmId);
				ps.setString(2, alarmId);
				ps.executeUpdate();
				ps.close();
			}
			ut.commit();
		} catch (Exception e) {
			logger.error("", e);
			try {
				ut.rollback();
			} catch (Exception ex) {
				logger.error("callPhone", ex);
			}
			throw e;
		} finally {
			BeanUtil.closeResource(ps, conn);
			if (isCreatWork == false)
				throw new Exception("�ɷ�����ʧ��");

		}
	}

	/*
	 * ��ȡ��Ա����
	 * 
	 * @see com.linkage.toptea.sysmgr.fm.assistant.concern.ConcernManager#
	 * getCallUserCount(java.lang.String)
	 */
	public int getCallUserCount(String search) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		ResultSet rs = null;
		int count = 0;
		try {
			conn = this.dataSource.getConnection();
			sql = "select count(*) as c from uportal.cas_user where MOBILE is not null";
			if (search != null && !search.equals("")) {
				sql += " and (name like '%" + search + "%' or MOBILE like '%"
						+ search + "%' or id like '%" + search + "%')";
			}
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt("c");
			}
			rs.close();
		} catch (Exception e) {
			logger.error("getCallUserCount", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return count;
	}

	/*
	 * ��ȡ��Ա����
	 * 
	 * @see
	 * com.linkage.toptea.sysmgr.fm.assistant.concern.ConcernManager#getCallUserList
	 * (java.lang.String, int, int)
	 */
	public List<User> getCallUserList(String search, int start, int limit)
			throws Exception {
		List<User> items = new ArrayList<User>();
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		ResultSet rs = null;
		try {
			conn = this.dataSource.getConnection();
			sql = "select * from uportal.cas_user where MOBILE is not null";
			if (search != null && !search.equals("")) {
				sql += " and (name like '%" + search + "%' or MOBILE like '%"
						+ search + "%' or id like '%" + search + "%')";
			}
			sql = "SELECT t2.* FROM (SELECT ROWNUM NO,T.* FROM (" + sql
					+ ") T WHERE ROWNUM <=" + (start + limit)
					+ ") T2 WHERE NO>=" + start;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				User ci = new UserInfo();
				ci.setId(rs.getString("ID"));
				ci.setName(rs.getString("name"));
				ci.setMobile(rs.getString("MOBILE"));
				items.add(ci);
			}
			rs.close();
		} catch (Exception e) {
			logger.error("getCallUserList", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return items;
	}

	 /**
	  * �����������ݼ�
	  * @param xml
	  * @throws Exception
	  */
	 public void updateRepVacation(String xml) throws Exception{
		 Reader reader = null;
		 List<RepVacation> items = new ArrayList<RepVacation>();
		try{
			
			 reader = new StringReader("<?xml version=\"1.0\" encoding=\"utf-8\" ?>"+xml);
	         DOMParser p = new DOMParser();
	         p.parse(new InputSource(reader));
	         Element root = p.getDocument().getDocumentElement();
	         NodeList nl = root.getChildNodes();
	         for (int i = 0; i < nl.getLength(); i++) {
	             Node node = nl.item(i);
	             Element el = (Element) node;
	             RepVacation rv = new RepVacation();
	             rv.setUserId(el.getAttribute("userId"));
	             rv.setStartTime(Long.parseLong((String)el.getAttribute("startTime")));
	             rv.setEndTime(Long.parseLong((String)el.getAttribute("endTime")));
	             rv.setIsUse(Integer.parseInt((String)el.getAttribute("isuse")));
	             items.add(rv);
	         }
	         updateRepVacation(items);
	         
		}catch(Exception e){
			logger.error("updateRepVacation", e);
		}
	 }
	 /**
	  * �����������ݼ�
	  * @param items
	  * @throws Exception
	  */
	 public void updateRepVacation(List<RepVacation> items) throws Exception{
		    Connection conn = null;
		    PreparedStatement ps = null;
			String sql = "merge into TF_TT_ALARM_REPVACATION t using (select ? as userid,? as starttime,? as endtime,? as ISUSE from dual) t2";
			sql += " on (t.userid=t2.userid) "
			 +" WHEN MATCHED THEN "
			 +" update set t.starttime=t2.starttime,t.endtime=t2.endtime,t.ISUSE=t2.ISUSE"
			 +" WHEN NOT MATCHED THEN "
			 +" INSERT (userid,starttime,endtime,ISUSE) values(t2.userid,t2.starttime,t2.endtime,t2.ISUSE)";
			
			try {
				conn = this.dataSource.getConnection();
				conn.setAutoCommit(false);
				ps = conn.prepareStatement(sql);
				for(RepVacation info:items){
					ps.setString(1,info.getUserId());
					Date sd = new Date(info.getStartTime());
					Date ed = new Date(info.getEndTime());
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd"); 
					sd = sf.parse(sf.format(sd));
					ps.setTimestamp(2, new Timestamp(sd.getTime())); 
					ed = sf.parse(sf.format(ed));
					
					ps.setTimestamp(3,  new Timestamp(ed.getTime()));
					ps.setInt(4, info.getIsUse());
					ps.addBatch();
				}
				ps.executeBatch();
				for(RepVacation info:items){
					if (info.getIsUse()==1)	RepVacationManager.addVacation(info.getUserId(),info);
					else RepVacationManager.removeVacation(info.getUserId());
				}
				
				
				conn.commit();
			} catch (Exception e) {
				logger.error("updateRepVacation", e);
				try{
					conn.rollback();
				}catch(Exception ex){
					logger.error("", e);
				}
				throw e;
			} finally {
				conn.setAutoCommit(true);
				BeanUtil.closeResource(ps, conn);
			}
	 }
	 /**
	  * ɾ���ݼ�
	  * @param userid
	  * @throws Exception
	  */
	 public void removeRepVacation(String userid) throws Exception{
		 Connection conn = null;
			PreparedStatement ps = null;
			String sql = "delete from TF_TT_ALARM_REPVACATION where userid=?";
			
			try {
				conn = this.dataSource.getConnection();
				conn.setAutoCommit(false);
				ps = conn.prepareStatement(sql);
				ps.setString(1, userid);
				ps.executeUpdate();
				RepVacationManager.removeVacation(userid);
						
				conn.commit();
			} catch (Exception e) {
				logger.error("removeFilter", e);
				try{
					conn.rollback();
				}catch(Exception ex){
					logger.error("", e);
				}
				throw e;
			} finally {
				conn.setAutoCommit(true);
				BeanUtil.closeResource(ps, conn);
			}
	 }
	 /**
	  * ɾ���ݼ�
	  * @param users
	  * @throws Exception
	  */
	 public void removeRepVacation(String[] users) throws Exception{
		 Connection conn = null;
			PreparedStatement ps = null;
			if (users == null)  return;
			String sql = "delete from TF_TT_ALARM_REPVACATION where userid=?";
			try {
				conn = this.dataSource.getConnection();
				conn.setAutoCommit(false);
				ps = conn.prepareStatement(sql);
				for(String user:users){
				   ps.setString(1, user);
				   ps.addBatch();
				}
				ps.executeBatch();
				for(String user:users){
					RepVacationManager.removeVacation(user);
				}
				conn.commit();
			} catch (Exception e) {
				logger.error("removeFilter", e);
				try{
					conn.rollback();
				}catch(Exception ex){
					logger.error("", e);
				}
				throw e;
			} finally {
				conn.setAutoCommit(true);
				BeanUtil.closeResource(ps, conn);
			}
	 }
	 /**
	  * ��ȡ�ݼ�
	  * @param start
	  * @param limit
	  * @return
	  * @throws Exception
	  */
	 public List<RepVacation> findRepVacation(int start,int limit) throws Exception{
		 List<RepVacation> items = new ArrayList<RepVacation>();
			Connection conn = null;
			PreparedStatement ps = null;
			String sql = "";
			ResultSet rs = null;
			try {
				conn = this.dataSource.getConnection();
				sql = "select * from TF_TT_ALARM_REPVACATION ";
				if (limit >0 ){
					sql = "SELECT t2.* FROM (SELECT ROWNUM NO,T.* FROM (" + sql
							+ ") T WHERE ROWNUM <=" + (start + limit)
							+ ") T2 WHERE NO>=" + start;
				}
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					RepVacation rv = new RepVacation();
					rv.setUserId(rs.getString("userId"));
					if (rs.getTimestamp("startTime")!=null)	rv.setStartTime(rs.getTimestamp("startTime").getTime());
					if (rs.getTimestamp("endTime")!=null)	rv.setEndTime(rs.getTimestamp("endTime").getTime());
					rv.setIsUse(rs.getInt("isuse"));
					items.add(rv);
				}

			} catch (Exception e) {
				logger.error("findRepVacation", e);
				throw e;
			} finally {
				BeanUtil.closeResource(ps, conn);
			}
			return items;
	 }
	 /**
	  * ��ȡ�ݼ�������
	  * @return
	  * @throws Exception
	  */
	 public int getRepVacationCount() throws Exception{
		    Connection conn = null;
			PreparedStatement ps = null;
			String sql = "";
			ResultSet rs = null;
			int count = 0;
			try {
				conn = this.dataSource.getConnection();
				sql = "select count(*) as c from TF_TT_ALARM_REPVACATION";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					count = rs.getInt("c");
				}

			} catch (Exception e) {
				logger.error("getRepVacationCount", e);
				throw e;
			} finally {
				BeanUtil.closeResource(ps, conn);
			}
			return count;
	 }
	 
	 public Map<String,String> getAllReper(int type) throws Exception{
		 
		    Map<String,String> items = new HashMap<String,String>();
			Connection conn = null;
			PreparedStatement ps = null;
			String sql = "";
			ResultSet rs = null;
			try {
				conn = this.dataSource.getConnection();
				
				sql = "select distinct(t.responsible1) as id from tf_tt_alarm_responsible t where t.responsible1 is not null ";
				sql += " union ";
				sql += "select distinct(t.responsible3) as id from tf_tt_alarm_responsible t where t.responsible3 is not null";
				if (type == RepVacation.RepVacation_NOCHOOSE){
					sql="select b.id,b.name from ("+sql+") a ,uportal.cas_user b where a.id=b.id and a.id not in (select userid from tf_tt_alarm_repvacation) order by b.name  ";
				}
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					items.put(rs.getString("id"), rs.getString("name"));
				}

			} catch (Exception e) {
				logger.error("getAllReper", e);
				throw e;
			} finally {
				BeanUtil.closeResource(ps, conn);
			}
			return items;
	 }
	 
	 public void updatePhoneConfig(Map<String,String> items) throws Exception{
		 if (items == null) return;
		    Connection conn = null;
		    PreparedStatement ps = null;
			String sql = "merge into TF_TT_ALARM_PHONECONF t using (select ? as configid,? as value from dual) t2";
			sql += " on (t.configid=t2.configid) "
			 + " WHEN MATCHED THEN "
			 + " update set t.value=t2.value"
			 + " WHEN NOT MATCHED THEN "
			 + " INSERT (configid,value) values(t2.configid,t2.value)";
			
			try {
				conn = this.dataSource.getConnection();
				conn.setAutoCommit(false);
				ps = conn.prepareStatement(sql);
				Iterator<String> it = items.keySet().iterator();
				// ��¼ҵ����־
				StringBuffer logStr = new StringBuffer();
				while(it.hasNext()){
					String key = it.next();
					String value = items.get(key);
					ps.setString(1, key);
					ps.setString(2, value);
					ps.addBatch();
					
					logStr.append("[" + key + " = " + value + "], ");
					if (key.equals("FAIL_NUM")){
						if (value==null) PhoneConfigManager.FAIL_NUM = 3;
						else PhoneConfigManager.FAIL_NUM = Integer.parseInt(value);
							
					}else if (key.equals("ALARM_NUM")){
						if (value==null) PhoneConfigManager.ALARM_NUM = 30;
						else PhoneConfigManager.ALARM_NUM = Integer.parseInt(value);
					}else if (key.equals("ALARM_GRADE")){
						if (value==null) PhoneConfigManager.ALARM_GRADE = ">=3";
						else PhoneConfigManager.ALARM_GRADE = value;
					}else if ( "TIME".equals(key) ){// add by huangzh
						if (value==null) PhoneConfigManager.TIME = 40;
						else PhoneConfigManager.TIME = Integer.parseInt(value);
					}else if ( "TIMERUN".equals(key) ){// add by huangzh
						if (value==null) PhoneConfigManager.TIME_RUN = 1;
						else PhoneConfigManager.TIME_RUN = Integer.parseInt(value);
					}else if ( "CALL_TIME".equals(key) ){// add by huangzh
						String callTime = value.split(":")[0];
						PhoneConfigManager.CALL_TIME = Integer.parseInt(callTime);

						if ( Integer.parseInt(callTime) == 1 ) {							
							for ( int i = 0; i < PhoneConfigManager.CALL_TIME_INTERVEL.length;i++ ) {
								PhoneConfigManager.CALL_TIME_INTERVEL[i] = 0;
							}
						} else {
							String[] intervels = value.split(":")[1].split(",");
							for ( int i = 0; i < PhoneConfigManager.CALL_TIME_INTERVEL.length; i++ ) {
								if ( i < intervels.length ) {
									PhoneConfigManager.CALL_TIME_INTERVEL[i] = Integer.parseInt( intervels[i] );
								} else {
									PhoneConfigManager.CALL_TIME_INTERVEL[i] = 0;
								}
								
							}
						}
					}
				}
				ps.executeBatch();				
				
				conn.commit();
				
				// ��־
				bussinessLogger.log( LOG_TYPE, "TF_TT_ALARM_PHONECONF", "�޸��Զ�������ò�����  " + logStr.toString() );
			} catch (Exception e) {
				logger.error("updateRepVacation", e);
				try{
					conn.rollback();
				}catch(Exception ex){
					logger.error("", e);
				}
				throw e;
			} finally {
				conn.setAutoCommit(true);
				BeanUtil.closeResource(ps, conn);
			}
	 }
	 
	 /**
	  * ��ȡ�������
	  * @param filter
	  * @return
	  * @throws Exception
	  */
	 public int getAutoCallAlarmInfoCount(AutoCallAlarmFilter filter)  throws Exception{
            int count = 0;
		    Connection conn = null;
			PreparedStatement ps = null;
			String sql = "";
			ResultSet rs = null;
			try {
				conn = this.dataSource.getConnection();
				sql = "(select a.* from TF_TT_ALARM_CALLPHONE a, TF_TT_ALARM c where  a.alarm_id = c.ALARM_ID union select a.* from TF_TT_ALARM_CALLPHONE a,TF_TT_ALARM_HIS b where b.ALARM_ID = a.alarm_id)";
				sql= "select count(*) as c from ("+sql+") a where 1=1 "+getWhereAutoCallAlarmInfo(filter) +" ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					count = rs.getInt("c");
				}
				rs.close();
			} catch (Exception e) {
				logger.error("getAutoCallAlarmInfoCount", e);
			} finally {
				BeanUtil.closeResource(ps, conn);
			}
			return count;
	 }
	
	 
	 private String getWhereAutoCallAlarmInfo(AutoCallAlarmFilter filter){
		 String sql = "";
		 if  (filter.getIsshow()!=-1){
			 sql+=" and a.ISHOW ="+filter.getIsshow();
		 }
		 if(filter.getId()!=null && !filter.getId().equals("")){
			 sql += " and a.alarm_id='"+filter.getId()+"'";
		 }
		 if (filter.getContent() != null && !filter.getContent().equals("")){
			 sql += " and a.ALARM_CONTENT like '%"+filter.getContent()+"%'";
		 }
		 return sql;
	 }
	 
	 /**
	  * �������������
	  * @param alarmIds
	  * @throws Exception
	  */
	 public void hideAutoCallAlarmInfos(String[] alarmIds) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		try {
			conn = this.dataSource.getConnection();
			conn.setAutoCommit(false);
			sql = "update TF_TT_ALARM_CALLPHONE set ISHOW=0 where ALARM_ID=?";
			ps = conn.prepareStatement(sql);
			for (String alarmid : alarmIds) {
				ps.setString(1, alarmid);
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();

		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception ex) {
				logger.error("", e);
			}
			logger.error("hideAutoCallAlarmInfos", e);
			throw e;
		} finally {
			conn.setAutoCommit(true);
			BeanUtil.closeResource(ps, conn);
		}
	}
     
     /**
		 * ����OVSDĬ�ϴ������
		 * 
		 * @param workId
		 *            ������
		 * @author huangzh
		 */
     public void setWorkProcess ( long workId ) {
         
         // OVSD�û��� Ĭ����system�������û��޴�Ȩ��
         String user = "system";         
         // ȡ��OVSD����
         String server =ovsdInteraction.getServer();
         // ȡ���û�����
         String passwd = getOvsdPassword( user );
         // ȡ���û�session
         ApiSDSession session = ApiSDSession.openSession(server, user, passwd);
         
         IIncidentHome incidentHome = session.getIncidentHome();
         IIncident incident = incidentHome.openIncident(workId);
         // ȡ�ô������
         String oldComment = incident.getIncidentText64kB();
         // ���������̲�Ϊ�գ�������
         if ( oldComment != null && !"".equals(oldComment)) { return; }
         // ����Ĭ��ֵ
         incident.setIncidentText64kB("[�澯̨]����Э������\r\n");
         try {
             // ���洦�����
             incident.save();
         } catch ( Exception e ) {
             logger.error( " ����OVSD�������ʧ�� ", e );
         }
     }
     
     
     /**
      * ȡ��OVSD�û�����
      * @param user OVSD�û���
      * @return OVSD�û�����
      * @author huangzh
      */
     private String getOvsdPassword ( String user ) {
         // ���ݿ���Դ
         Connection conn = null;
         PreparedStatement ps = null;
         ResultSet rs = null;
         // SQL
         String sql = "select t.acc_password from " 
             + ovsdInteraction.ovsduser + ".rep_accounts t where t.acc_loginname = ?";
         try{
             conn = dataSource.getConnection();
             ps = conn.prepareStatement(sql);
             ps.setString(1, user);
             rs = ps.executeQuery();
             if (rs.next()){
                 String password = rs.getString(1);
                 if (password == null){
                     logger.error("�޷�ȡ��" + user + "�û�������" );
                     return null;
                 }
                 return  Passwd.DeCrypt(password);
             }
             
         }catch(Exception ex){
             logger.error("OVSD:ȡ���û�������ʧ��" ,ex);
         }finally{
             BeanUtil.closeResource(rs, ps, conn);
         }
         return null;
     }
     

     /**
      * �����״̬Ϊ������С����ʧ�ܡ������ʱ�䳬���趨ʱ��������¼¼���ע��
      * ����״̬Ϊ�����ʧ�ܡ����������������
      * �������״̬Ϊ������С����ʧ�ܡ������ʱ�䳬���趨ʱ��������¼������Ϊ����ʾ
      */
     public void resetCallState() {
		// ���ݿ���Դ
		Connection conn = null;
		PreparedStatement ps = null;

		PreparedStatement ps1 = null;

		// SQL
		String sql = " INSERT INTO TF_TT_ALARM_CONCERN (ALARMID) "
			+ " SELECT ALARM_ID FROM  TF_TT_ALARM_CALLPHONE A WHERE ISCONCERN=0 "
			// ���״̬����к����ʧ�ܵ��ҳ���ָ��ʱ�䣬����״̬Ϊ�����ʧ�ܡ��� ������������� ������ҳ��չʾ��������ע�澯�б���
			+ " AND  ((STATUS <> 1 AND ISHOW= 1 AND (sysdate - ctime)*24*60 >= ?) OR (STATUS=-1 AND NUM>=?) "
			// ���������
			+ "  OR ( EXISTS (SELECT ALARM_ID FROM  TF_TT_CALL_CONF T WHERE T.OBJ_VALUE=A.PHONE AND T.NUM <= A.NUM AND A.STATUS=-1) )) "
			+ " AND  NOT EXISTS (SELECT * FROM TF_TT_ALARM_CONCERN B WHERE A.ALARM_ID=B.ALARMID) "
    		+ " AND NOT EXISTS(SELECT * FROM TF_TT_ALARM_CONCERN_HIS D WHERE A.ALARM_ID=D.ALARMID)";

		
		String update = " UPDATE  TF_TT_ALARM_CALLPHONE A SET ISHOW = 0, ISCONCERN=1 "
			 + " WHERE  ISCONCERN=0 AND  ((STATUS <> 1 AND ISHOW= 1 AND (sysdate - ctime)*24*60 >= ?) OR (STATUS=-1 AND NUM>=?)"
			   +  " OR ( EXISTS (SELECT ALARM_ID FROM  TF_TT_CALL_CONF T WHERE T.OBJ_VALUE=A.PHONE AND T.NUM <= A.NUM AND A.STATUS=-1) )) ";

		try {

			// ȡ�����ݿ�����
			conn = dataSource.getConnection();

			// �����״̬Ϊ������С������ʱ�䳬���趨ʱ��������¼¼���ע��
			ps = conn.prepareStatement(sql);
			ps.setInt(1, PhoneConfigManager.TIME);
			ps.setInt(2, PhoneConfigManager.FAIL_NUM);
			ps.executeUpdate();

			// �������ʱ�����ݸ���Ϊ������ʾ��
			ps1 = conn.prepareStatement( update );
			ps1.setInt(1, PhoneConfigManager.TIME);
			ps1.setInt(2, PhoneConfigManager.FAIL_NUM);
			ps1.executeUpdate();
			
		} catch (Exception ex) {
			logger.error("�������ʧ��", ex);
		} finally {
			BeanUtil.closeResource(ps1, ps, conn);
		}

	}

 	/**
 	 * ���ܣ���ע�澯�߳�
 	 * 
 	 */
 	class MoveConcernAlarmThread extends Thread {
 		/**  �̴߳��� */
 		public void run() {
 			while (true) {
 				try {
 					Thread.sleep(10 * 60 * 1000);
 					business();
 				} catch (Exception e) {
 					logger.error("��ע�澯�̴߳���ʧ��", e);
 				} 
 			}

 		}
 		
 		/**  ҵ����:�¼���״̬�ǡ��ѽ���������رա��������¼������ʷ�� */
 		public void business() {
			// ���ݿ���Դ
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;

			PreparedStatement ps1 = null;
			
		 	/*
		 	 * [3094610250,"�ѵǼ�"],[3094610251,"���䵽����̨"]
		 	 * [3120234522,"���䵽һ��"],[3094610252,"���䵽����"]
		 	 * [3094610253,"���䵽����"],[3094610254,"һ�ߴ�����"]
		 	 * [281478298599683,"���ߴ�����"],[281478298599686,"���ߴ�����"]
		 	 * [281478298599689,"�ѽ��"],[281486658506735,"�����"]
		 	 * [281478298599692,"�ر�"],[281526637625373,"�ѷ���"]
		 	 * [281526637625376,"������"]]
		 	 */
			// ��ѯ�Զ�����澯�����Ƿ��ѹرջ��ѽ��
			String sql = " SELECT A.*, TT.INC_STA_OID FROM (SELECT ALARM_ID, WORK_ID "
				 + " FROM TF_TT_ALARM M WHERE M.WORK_ID IS NOT NULL "
				 + " AND M.ALARM_ID IN (SELECT ALARM_ID FROM TF_TT_ALARM_CALLPHONE "
				 + " WHERE ISHOW = 1)) A, UOVSD.ITSM_INCIDENTS TT"
				 + " WHERE TT.INC_ID = A.WORK_ID AND "
				 + "( TT.INC_STA_OID = 281478298599689 OR TT.INC_STA_OID = 281478298599692 )"
			     + " UNION "
			     + " SELECT A.*, TT.INC_STA_OID FROM (SELECT ALARM_ID, WORK_ID "
				 + " FROM TF_TT_ALARM_HIS M WHERE M.WORK_ID IS NOT NULL "
				 + " AND M.ALARM_ID IN (SELECT ALARM_ID FROM TF_TT_ALARM_CALLPHONE "
				 + " WHERE ISHOW = 1)) A, UOVSD.ITSM_INCIDENTS TT"
				 + " WHERE TT.INC_ID = A.WORK_ID AND "
				 + "( TT.INC_STA_OID = 281478298599689 OR TT.INC_STA_OID = 281478298599692 )";
			logger.debug( "======business=======" + sql);

			// ɾ����ǰ��ע�澯
			String update = " UPDATE TF_TT_ALARM_CALLPHONE SET ISHOW = 0 WHERE ALARM_ID = ? ";

			try {
				conn = dataSource.getConnection();
				
				// ��ѯ��ע�澯�Ƿ��Ѿ��ɹ���
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				conn.setAutoCommit( false );
				
				ps1 = conn.prepareStatement(update);
				
				boolean hasNext = false;
				while (rs.next()) {
				
					ps1.setString(1, rs.getString("ALARM_ID"));
					ps1.addBatch();
					
					hasNext = true;
					logger.info("-------------��ʱɨ�账��---------------" + rs.getString("ALARM_ID") );
				}
				if ( hasNext == true ) {
					ps1.executeBatch();				
					conn.commit();
				}
			} catch (Exception ex) {
				logger.error("��ʱɨ�账��ʧ��", ex);
				ex.printStackTrace();
			} finally {
				try {
					conn.setAutoCommit( true );
				} catch (SQLException e) {
					logger.error( "", e );
				}
				BeanUtil.closeResource(ps1);
				BeanUtil.closeResource(rs, ps, conn);
			}		
			
		}
 	}
 	
 	/**
 	 * �Զ���������߳�
 	 * @author huangzh
 	 *
 	 */
 	class CallControlThread extends Thread {
 		
 		 boolean flag = true;
 		 
 		/**  �̴߳��� */
 		public void run() {
 			while (true) {
 				try {
 					Thread.sleep(60 * 1000);
 					Calendar c = Calendar.getInstance();
 					//ÿ������18��ˢ�»���
 					if ( c.get(Calendar.HOUR_OF_DAY) == 18 && c.get(Calendar.MINUTE) >= 0 && c.get(Calendar.MINUTE) < 1 ) {
 						initCache();
 						logger.info( "===============init cache ============" );
 					}
 					control();
 				} catch (Exception e) {
 					logger.error("��ע�澯�̴߳���ʧ��", e);
 				} 
 			}
 		}
 		
 		public void control () {
 			// �Ѿ��������У���������
 			if ( flag == false ) { return; }
 			// �߳�����
 			flag = false;
 			
 			Connection conn = null;
 			String sql = " UPDATE TF_TT_ALARM_CALLPHONE SET STATUS = 0,CALLER= ?, PHONE = ?,  CTIME=?, NUM = ?, CALLID = ? WHERE ALARM_ID= ? ";
			PreparedStatement ps1 = null;

 			try {
				conn = dataSource.getConnection();
				ps1 = conn.prepareStatement(sql);
				conn.setAutoCommit(false);
				Timestamp nts = new Timestamp(System.currentTimeMillis());
				// �����ڴ��е��������
				for (Iterator it = autoCallInfo.keySet().iterator(); it.hasNext();) {
					String key = (String)it.next();
					List<AutoCallAlarmInfo> callInfos = autoCallInfo.get( key );
					if ( null == callInfos || callInfos.size() == 0 ) { 
						it.remove(); 
						continue;
					}
					boolean isFail = false;
					for ( int m = 0; m < callInfos.size(); m++ ) {
						
						AutoCallAlarmInfo callInfo = callInfos.get(m);
						// �����ʧ�ܵĳ��ϣ�������
						if ( callInfo == null || callInfo.getStatus() != -1) { continue; }


						// �ﵽ���ʧ�ܴ����������ʧ�ܵĳ��ϣ������������
						if ( callInfo.getNum() >= PhoneConfigManager.FAIL_NUM   && CallUserManager.IS_CONF) {
							isFail = true;
							String userId = callUserManager.getNextCallUser( callInfo );

							logger.info( "�����������:�澯ID��" + callInfo.getId() 
									+ ", ��ǰ����ˣ�" + callInfo.getCaller() + ", ��һ������ˣ� " +  userId );
							if (userId == null || "".equals(userId)) { continue; }
							
							User user = userManager.getUser(userId);
							if (user == null || user.getMobile() == null ) { continue; }
							if (user.getMobile() == null) {
								logger.info("��" + user.getName() + "����澯ʧ�ܣ���Ϊ��û���ֻ���");
								continue;
							}

							// ���͸澯����
							callUserManager.sendSms(conn, user.getMobile(),callInfo.getId(), callInfo.getCaller() );
							
							//д����ļ����������
							long callId = callFileManager.writeCallFile(user.getMobile());

							ps1.setString(1, userId);
							ps1.setString(2, user.getMobile());
							ps1.setTimestamp(3, nts);
							ps1.setInt(4, 1);
							ps1.setString(5, callId+"");
							ps1.setString(6, callInfo.getId());
							ps1.addBatch();
							
							callInfo.setCallid(callId+"");
							callInfo.setPhone(user.getMobile());
							callInfo.setCaller(userId);
							callInfo.setCallTime(nts);
							callInfo.setNum( 1 );
							callInfo.setStatus(0);
							
							List<AutoCallAlarmInfo> newInfos = autoCallInfo.get(callId + "");
							if (newInfos == null) { 
								newInfos = new ArrayList<AutoCallAlarmInfo>();
							}
							newInfos.add(callInfo); 
							autoCallInfo.put(callId + "", newInfos);
							addCallDetail(callInfo);
						}
					}
					// ͬһ�����ʧ�ܣ��Ƴ����ڴ�
					if ( isFail == true ) { it.remove(); }
				}
				ps1.executeBatch();
				conn.commit();
				
				
				//�ж��Ƿ���Ҫ�Ե�ǰ�������ٴ����
				callCtrl ( nts );

				conn.setAutoCommit(true);
				
//				//����Զ�����Ƿ񳬹�ָ����������Ҫת�Ƶ���ע��
//				String sql1 = "insert into tf_tt_alarm_concern a (ALARMID) "
//				  + " select b.ALARM_ID from TF_TT_ALARM_CALLPHONE b where b.STATUS=-1 and b.num>=? and b.ISCONCERN=0 "
//				  + " and not exists (select * from tf_tt_alarm_concern c where c.ALARMID=b.ALARM_ID) "
//				  + " and not exists (select * from tf_tt_alarm_concern_his d where d.ALARMID = b.ALARM_ID) ";
//				ps1 = conn.prepareStatement(sql1);
//				ps1.setInt(1, PhoneConfigManager.FAIL_NUM);
//				ps1.executeUpdate();
//				
//				// �������ʧ���ҳ�����������������¼״̬Ϊ����ʾ
//				sql1 = "UPDATE TF_TT_ALARM_CALLPHONE SET ISCONCERN=1,ISHOW=0 WHERE  STATUS=-1 AND NUM>=? AND ISCONCERN=0";
//				ps1 = conn.prepareStatement(sql1);
//				ps1.setInt(1, PhoneConfigManager.FAIL_NUM);
//				ps1.executeUpdate();
				
				// ��״̬Ϊ����еģ��ҳ���ָ��ʱ����Զ������¼�ƶ�����ע�澯�У�״̬����Ϊ����ʾ
				resetCallState();
				
				// ��״̬Ϊ������С�������20����δ�յ���ִ���澯��Ȼ���ڵ������¼��Ϊ[���ʧ��]
				updatePageCall();
			} catch (Exception e ) {
 				logger.error("", e);
 				e.printStackTrace();
 			} finally {
 				flag = true;
				BeanUtil.closeResource( ps1, conn);
 			}
 		}
 		
 		/**
 		 * �������
 		 * @param nts
 		 */
 		private void callCtrl( Timestamp nts ) {
 			Connection conn = null;
 			String sql = " UPDATE TF_TT_ALARM_CALLPHONE SET STATUS = 0,  CTIME=?, NUM = ?, CALLID = ? WHERE CALLID= ? ";
			PreparedStatement ps = null;
			try {
				conn = dataSource.getConnection();
				ps = conn.prepareStatement(sql);
				
				for (Iterator it = autoCallInfo.keySet().iterator(); it.hasNext();) {
					String key = (String)it.next();
					
					List<AutoCallAlarmInfo> callInfos = autoCallInfo.get( key );
					if ( null == callInfos || callInfos.size() == 0 ) { 
						it.remove(); 
						continue;
					}
					
					// ��ʱ����ĸ澯���ڴ����Ƴ�
					for (int i = 0; i < callInfos.size();i++ ) {
						AutoCallAlarmInfo info = callInfos.get(i);
						long dd = System.currentTimeMillis() - info.getCallTime().getTime();
						if ( dd  >= PhoneConfigManager.TIME*60*1000L ) { callInfos.remove(i--); }
						
						// �����������
						if ( i>=0 && !callConfigManager.check(info.getPhone(), info.getNum() ) ) {
							callInfos.remove(i--);
						}
					}
					// �������¼���ڴ����Ƴ�
					if ( null == callInfos || callInfos.size() == 0 ) {
						it.remove();
						continue;
					} 
					
					// ͬһ�����״̬��ͬ�����ֻȡ��һ��
					AutoCallAlarmInfo callInfo = callInfos.get(0);				
					// �����ʧ�ܵĳ��ϣ�������
					if ( callInfo == null || callInfo.getStatus() != -1) { continue; }

					// ������ƴ���
					int num = callInfo.getNum();

					
					Timestamp ts = callInfo.getCallTime();
					
					// ���������һ�Σ���˴�1��ʼ // ������󣬸���Btime��ÿ��У��btime�͵�ǰʱ����
					for (int i = 1; i < PhoneConfigManager.CALL_TIME; i++) {
						if (num == i) {
							long now = nts.getTime() - ts.getTime();
							// �ж�ʱ����
							if (now >= PhoneConfigManager.CALL_TIME_INTERVEL[i-1]* 1000*60L ) {

								long callId = 0L;
/** �ϲ��������Ŀǰ�����ᣬ��ʱ����,�������ʱ�öδ���������⣬Ӧ��callInfos����ѭ������Ӧ��ֻȡ����һ��
								// �ٴεȴ��ϲ����
								if (callFileManager.fileExists(callInfo.getPhone(), "callTemp")) {
									// ���ϲ�����ļ����ڣ��ϲ����
									callId = callFileManager.writeCallFile(callInfo.getPhone());
								} else {
									// ���ϲ�����ļ������ڣ�ֱ�����
									callId = callFileManager.getCallId()[0];
									callFileManager.moveCtrlFile(Long.parseLong(callInfo.getCallid()), callId);
								}
*/
								callId = callFileManager.getCallId()[0];
								callFileManager.moveCtrlFile(Long.parseLong(callInfo.getCallid()), callId);
								
								logger.info(" oldCallId = " + callInfo.getCallid() + " newCallId= " + callId + "  num = "
										+ i + " INTERVEL = " + PhoneConfigManager.CALL_TIME_INTERVEL[i - 1] + " now= "
										+ (now / 1000 / 60));

								ps.setTimestamp(1, nts);
								ps.setInt(2, callInfo.getNum() + 1);
								ps.setString(3, callId + "");
								ps.setString(4, key);
								ps.executeUpdate();
								
								for (AutoCallAlarmInfo info : callInfos ) {
									info.setNum(info.getNum() + 1);
									info.setCallTime(nts);
									info.setCallid(callId + "");
									info.setStatus(0);	
									addCallDetail(info);
								}
								List<AutoCallAlarmInfo> newInfos = autoCallInfo.get(callId + "");
								if (newInfos != null) { callInfos.addAll(newInfos); }
								autoCallInfo.put(callId + "", callInfos);
								if ( !key.equals(callId + "") ) { it.remove(); }
								return;
							}
						}
					}					
				}
			} catch ( Exception e) {
				logger.error("", e);
 				e.printStackTrace();
			} finally {
				BeanUtil.closeResource( ps, conn);
			}
		}
 		
 		/**
 		 * ��״̬Ϊ������С�������20����δ�յ���ִ���澯��Ȼ���ڵ������¼��Ϊ[���ʧ��]
 		 */
 		public void updatePageCall () {
			// ���ݿ���Դ
			Connection conn = null;
			PreparedStatement ps = null;


			// ��״̬Ϊ������С�������20����δ�յ���ִ���澯��Ȼ���ڵ������¼��Ϊ[���ʧ��]
			String sql = " UPDATE TF_TT_ALARM_CONCERN SET STATUS=2 WHERE ALARMID IN (  "
				// ״̬Ϊ�����
				 + " SELECT C.ALARMID FROM TF_TT_ALARM_CONCERN C, TF_TT_ALARM A WHERE C.STATUS = 3 "
				 // ����20����δ�յ���ִ
				 + " AND (SYSDATE - (SELECT MAX(CALLTIME) FROM TF_TT_ALARM_CONCERN_CALL WHERE ALARMID = C.ALARMID))*24*60 > 20 "
				 // �澯��Ȼ����
				 + " AND A.ALARM_ID=C.ALARMID )";


			try {
				conn = dataSource.getConnection();				
				
				ps = conn.prepareStatement(sql);
				
				int r = ps.executeUpdate();
				if ( r > 0 ) {
					logger.info("��" + r + "��״̬Ϊ������С���ҳ�������Ϊ�����ʧ�ܡ���");
				}
				
			} catch (Exception ex) {
				logger.error("updatePageCall", ex);
			} finally {
				BeanUtil.closeResource( ps, conn);
			}		
			
		}
 	}

	public void setCallKeyWordManager(CallKeyWordManager callKeyWordManager) {
		this.callKeyWordManager = callKeyWordManager;
	}

	/**
	 * ���Զ�������в���һ����¼
	 * @param alarmId �澯ID
	 * @param callId ���ID
	 * @param userId ����û�ID
	 * @param mobile ����û�mobile
	 * @return 0 ʧ��
	 * @throws Exception
	 */
	public int addCallDetail(AutoCallAlarmInfo callInfo) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "INSERT INTO TF_TT_CALLPHONE_DETAIL(ALARM_ID,CALLID,CALLER,PHONE,STATUS,NUM,ISCONCERN,ISHOW,CTIME) VALUES(?,?,?,?,0,1,0,1,?)";

		int result = 0;
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, callInfo.getId());
			ps.setString(2, callInfo.getCallid() );
			ps.setString(3, callInfo.getCaller());
			ps.setString(4, callInfo.getPhone() );
			ps.setTimestamp(5,callInfo.getCallTime() );
			result = ps.executeUpdate();
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return result;
	}

	public void setSpecialCallManager(SpecialCallManager specialCallManager) {
		this.specialCallManager = specialCallManager;
	}
}


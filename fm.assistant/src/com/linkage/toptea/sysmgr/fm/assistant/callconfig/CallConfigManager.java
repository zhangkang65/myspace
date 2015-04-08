package com.linkage.toptea.sysmgr.fm.assistant.callconfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.linkage.toptea.auc.BusinessLogger;


/**
 * ����Ա�����������������
 * @author huangzh
 *
 */
public class CallConfigManager {


	// ��־��ӡ
	private Log logger = LogFactory.getLog(CallConfigManager.class);
	
	private static final String LOG_TYPE = "assistant";	

	/** ҵ����־ */
	private BusinessLogger bussinessLogger;
	
	
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/** ����ҵ����־ģ�� */
	public void setBusinessLogger(BusinessLogger bussinessLogger) {
		this.bussinessLogger = bussinessLogger;
	}
	
	/** �Զ������Ϣ */
	private static ConcurrentHashMap<String, Integer> config = new ConcurrentHashMap<String, Integer>();
	
	public void init () {
		ConcurrentHashMap<String, Integer> temp = new ConcurrentHashMap<String, Integer>();
		List<CallConfigInfo> configs = getConfig ( null, null );
		if ( configs == null || configs.size() < 1 ) { return; }
		for ( CallConfigInfo info : configs ) {
			System.out.println(info.getObjValue() + " = " + info.getNum() );
			temp.put(info.getObjValue(), info.getNum() );
		}
		config.clear();
		config = temp;
	}

	/**
	 * �����ֻ�����ȡ���������
	 * @param mobile �ֻ�����
	 * @return �������
	 */
	public boolean check ( String mobile, int count ) {
		Integer num = config.get(mobile);
		if ( null == num ) { return true; }
		//System.out.println("mobile = " + mobile + " num = " + num.toString() + " count = " + count );
		if ( num <= count) { return false; }
		return true;
	}
	
	/**
	 * ����������
	 * @param info
	 * @return
	 */
	public int addConfig ( CallConfigInfo info  ) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " INSERT INTO TF_TT_CALL_CONF (TYPE,NUM,TIME,OBJ_VALUE,OBJ_NAME,OBJ_ID) VALUES (?,?,SYSDATE,?,?,? )";
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt( 1, info.getType() );
			ps.setInt(2, info.getNum() );
			ps.setString(3, info.getObjValue() );
			ps.setString(4, info.getObjName() );
			ps.setString(5, info.getObjId() );
			int r = ps.executeUpdate();
			if ( r > 0 ) {
				init ();
				bussinessLogger.log(LOG_TYPE, info.getObjId(), "�����Զ�������ù���" + ToStringBuilder.reflectionToString(info));
			}
			return r;
		} catch ( Exception e ) {
			logger.error("addConfig", e);
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * �޸��������
	 * @param info
	 * @return
	 */
	public int updateConfig ( CallConfigInfo info  ) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " UPDATE TF_TT_CALL_CONF SET TYPE=?,NUM=?,TIME=SYSDATE,OBJ_VALUE=?,OBJ_NAME=? WHERE OBJ_ID = ? ";
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt( 1, info.getType() );
			ps.setInt(2, info.getNum() );
			ps.setString(3, info.getObjValue() );
			ps.setString(4, info.getObjName() );
			ps.setString(5, info.getObjId() );
			int r = ps.executeUpdate();
			if ( r > 0 ) {
				init ();
				bussinessLogger.log(LOG_TYPE, info.getObjId(), "�����Զ�������ù���" + ToStringBuilder.reflectionToString(info));
			}
			return r;
		} catch ( Exception e ) {
			logger.error("updateConfig", e);
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * �޸��������
	 * @param info
	 * @return
	 */
	public int deleteConfig ( String objId  ) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " DELETE TF_TT_CALL_CONF  WHERE OBJ_ID = ? ";
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, objId );
			int r = ps.executeUpdate();
			if ( r > 0 ) {
				init ();
				bussinessLogger.log(LOG_TYPE, objId, "ɾ���Զ�������ù���" + objId);
			}
			return r;
		} catch ( Exception e ) {
			logger.error("deleteConfig", e);
			e.printStackTrace();
		}
		return 0;
	}
	

	/**
	 * �����������
	 * @param id
	 * @param condition
	 * @return
	 */
	public List<CallConfigInfo> getConfig ( String id, String condition  ) {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " SELECT * FROM  TF_TT_CALL_CONF  ";
		if ( id != null ) {
			sql = sql + " WHERE OBJ_ID =  '" + id + "' ";
		} else if ( condition != null ) {
			sql = sql + " WHERE OBJ_ID like '%" + condition + "%' ";
			sql = sql + "OR OBJ_NAME like '%" + condition + "%' ";
			sql = sql + "OR OBJ_VALUE like '%" + condition + "%' ";
		} 
		sql = sql + " ORDER BY TIME DESC ";
		List<CallConfigInfo> result = new ArrayList<CallConfigInfo>();
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			CallConfigInfo info = null;
			while ( rs.next() ) {
				info = new CallConfigInfo ();
				info.setNum(rs.getInt("NUM"));
				info.setObjId(rs.getString("OBJ_ID"));
				info.setObjName(rs.getString("OBJ_NAME"));
				info.setObjValue(rs.getString("OBJ_VALUE"));
				info.setTime(rs.getTimestamp("TIME"));
				info.setType(rs.getInt("TYPE"));
				result.add(info);
			}
		} catch ( Exception e ) {
			logger.error("addConfig", e);
			e.printStackTrace();
		}
		return result;
	}
}

package com.linkage.toptea.sysmgr.fm.assistant.specialcall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.linkage.toptea.auc.BusinessLogger;
import com.linkage.toptea.sysmgr.cm.ConfigManager;
import com.linkage.toptea.sysmgr.cm.MoInfo;
import com.linkage.toptea.util.BeanUtil;

public class SpecialCallManager {
//	 ��־��ӡ
	private Log logger = LogFactory.getLog(SpecialCallManager.class);	
	
	private static final String LOG_TYPE = "assistant";

	/** ҵ����־ */
	private BusinessLogger bussinessLogger;
	
	// Spring�������ݿ�
	private DataSource dataSource;

	private ConfigManager configManager;
	public void setConfigManager(ConfigManager configManager){
		this.configManager = configManager;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setBusinessLogger(BusinessLogger bussinessLogger) {
		this.bussinessLogger = bussinessLogger;
	}
	
	/** �����ڴ� */
	private static Map<String, SpecialCallInfo> SPECIAL_CALLINFO = new ConcurrentHashMap<String, SpecialCallInfo>();
	
	/**
	 * ��ʼ���ڴ�
	 *
	 */
	public void init () {
		List<SpecialCallInfo> infos = getRule();
		if ( null != infos && infos.size() > 0 ) {
			for ( SpecialCallInfo info : infos ) {
				SPECIAL_CALLINFO.put(info.getMoId(), info);
			}
		}
	}
	
	/**
	 * ���»���
	 * @param moId ����ID
	 * @param flag �������ͣ�0����ӡ� 1�����¡� 2��ɾ��
	 */
	public void updateCache( String moId, int flag ) {
		if ( flag == 0 || flag == 1 ) {
			SPECIAL_CALLINFO.put(moId, getRule(moId));
		} else if ( flag == 2 ) {
			SPECIAL_CALLINFO.remove(moId);
		}
			
	}
	
	/**
	 * ����У��
	 * @param apath �澯����·��
	 * @return true У��ͨ���� false ��ͨ��
	 */
	public boolean check ( String apath ) {
		// ��������ĳ���
		if ( null == apath || "".equals(apath) ) { return false ; }
		// ��������
		for ( Iterator<String> it = SPECIAL_CALLINFO.keySet().iterator(); it.hasNext(); ) {
			// ȡ��moid
			String key = it.next();
			SpecialCallInfo info = SPECIAL_CALLINFO.get(key);
			// ����Ϊ�յĳ���
			if ( null == info ) { 
				SPECIAL_CALLINFO.remove(key); 
				continue;
			}
			// ȡ�ø澯����
			MoInfo mo = configManager.findMoById(key);
			// �澯����Ϊ�յĳ���
			if ( null == mo ) { 
				SPECIAL_CALLINFO.remove(key); 
				continue;
			}
			//ȡ�ø߽�������·��
			String path = mo.getDotPath();
			// У�����
			if ( info.getIsch() == 1 ) { // �����ӽڵ�ĳ���
				if ( path.equals(apath) || apath.startsWith(path+".") ) {
					return true;
				}
			} else {
				if ( path.equals(apath) ) { return true; }
			}
			
		}
		return false;
	}
	
	/**
	 * ��ӹ���
	 * @param moId ����ID
	 * @param userId ������
	 * @param isch �Ƿ�����Ӷ��� 1����������������
	 * @return 1;�ɹ���0ʧ��
	 */
	public int addRule ( String moId, String userId, int isch ) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " INSERT INTO TF_TT_SPECIALCALL ( MOID, ISCH, USERID, TIME ) VALUES ( ?, ?, ?, SYSDATE ) ";
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, moId);
			ps.setInt(2, isch);
			ps.setString(3, userId);
			int r = ps.executeUpdate();
			if ( r > 0 ) {
				updateCache( moId, 0 );
				bussinessLogger.log( LOG_TYPE, "TF_TT_SPECIALCALL", "��������������  " + moId );
				return r;
			}
		} catch (Exception e) {
			logger.error("addRule", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return 0;
	}
	
	/**
	 * �޸Ĺ���
	 * @param moId ����ID
	 * @param userId ������
	 * @param isch �Ƿ�����Ӷ��� 1����������������
	 * @return 1;�ɹ���0ʧ��
	 */
	public int updateRule ( String moId, String userId, int isch ) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " UPDATE TF_TT_SPECIALCALL SET ISCH = ?, USERID =? , TIME = SYSDATE WHERE MOID = ? ";
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, isch);
			ps.setString(2, userId);
			ps.setString(3, moId);
			int r = ps.executeUpdate();
			if ( r > 0 ) {
				updateCache( moId, 1 );
				bussinessLogger.log( LOG_TYPE, "TF_TT_SPECIALCALL", "�޸������������  " + moId );
				return r;
			}
		} catch (Exception e) {
			logger.error("updateRule", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return 0;
	}
	
	/**
	 * ɾ������
	 * @param moId ����ID
	 * @return 1;�ɹ���0ʧ��
	 */
	public int delRule ( String moId ) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " DELETE TF_TT_SPECIALCALL  WHERE MOID = ? ";
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, moId);
			int r = ps.executeUpdate();
			if ( r > 0 ) {
				updateCache( moId, 2 );
				bussinessLogger.log( LOG_TYPE, "TF_TT_SPECIALCALL", "ɾ�������������  " + moId );
				return r;
			}
		} catch (Exception e) {
			logger.error("delRule", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return 0;
	}
	
	/**
	 * ���ݲ�ѯ����
	 * @param moId ����ID
	 * @return SpecialCallInfo
	 */
	public SpecialCallInfo getRule ( String moId ) {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " select * from TF_TT_SPECIALCALL  WHERE MOID = ? ";
		SpecialCallInfo info = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, moId);
			rs = ps.executeQuery();
			if ( rs.next() ) {
				info = new SpecialCallInfo ();
				info.setIsch(rs.getInt("ISCH"));
				info.setMoId(rs.getString("MOID"));
				info.setTime(rs.getTimestamp("TIME"));
				info.setUserId(rs.getString("USERID"));
			}
		} catch (Exception e) {
			logger.error("getRule", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return info;
	}
	
	
	/**
	 * ȡ��ȫ������
	 * @return SpecialCallInfo
	 */
	public List<SpecialCallInfo> getRule () {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " SELECT * FROM TF_TT_SPECIALCALL  ORDER BY TIME DESC ";
		List<SpecialCallInfo> infos = new ArrayList<SpecialCallInfo>();
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while ( rs.next() ) {
				SpecialCallInfo info = new SpecialCallInfo ();
				info.setIsch(rs.getInt("ISCH"));
				info.setMoId(rs.getString("MOID"));
				info.setTime(rs.getTimestamp("TIME"));
				info.setUserId(rs.getString("USERID"));
				infos.add(info);
			}
		} catch (Exception e) {
			logger.error("getRule", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return infos;
	}
}

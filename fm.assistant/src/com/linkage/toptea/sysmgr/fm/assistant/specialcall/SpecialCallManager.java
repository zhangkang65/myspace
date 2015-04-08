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
//	 日志打印
	private Log logger = LogFactory.getLog(SpecialCallManager.class);	
	
	private static final String LOG_TYPE = "assistant";

	/** 业务日志 */
	private BusinessLogger bussinessLogger;
	
	// Spring连接数据库
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
	
	/** 规则内存 */
	private static Map<String, SpecialCallInfo> SPECIAL_CALLINFO = new ConcurrentHashMap<String, SpecialCallInfo>();
	
	/**
	 * 初始化内存
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
	 * 更新缓存
	 * @param moId 规则ID
	 * @param flag 操作类型，0：添加、 1：更新、 2：删除
	 */
	public void updateCache( String moId, int flag ) {
		if ( flag == 0 || flag == 1 ) {
			SPECIAL_CALLINFO.put(moId, getRule(moId));
		} else if ( flag == 2 ) {
			SPECIAL_CALLINFO.remove(moId);
		}
			
	}
	
	/**
	 * 规则校验
	 * @param apath 告警对象路径
	 * @return true 校验通过、 false 不通过
	 */
	public boolean check ( String apath ) {
		// 参数错误的场合
		if ( null == apath || "".equals(apath) ) { return false ; }
		// 遍历规则
		for ( Iterator<String> it = SPECIAL_CALLINFO.keySet().iterator(); it.hasNext(); ) {
			// 取得moid
			String key = it.next();
			SpecialCallInfo info = SPECIAL_CALLINFO.get(key);
			// 规则为空的场合
			if ( null == info ) { 
				SPECIAL_CALLINFO.remove(key); 
				continue;
			}
			// 取得告警对象
			MoInfo mo = configManager.findMoById(key);
			// 告警对象为空的场合
			if ( null == mo ) { 
				SPECIAL_CALLINFO.remove(key); 
				continue;
			}
			//取得高进个对象路径
			String path = mo.getDotPath();
			// 校验规则
			if ( info.getIsch() == 1 ) { // 包含子节点的场合
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
	 * 添加规则
	 * @param moId 对象ID
	 * @param userId 操作者
	 * @param isch 是否包含子对象 1包含、其他不包含
	 * @return 1;成功，0失败
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
				bussinessLogger.log( LOG_TYPE, "TF_TT_SPECIALCALL", "添加特殊外呼规则：  " + moId );
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
	 * 修改规则
	 * @param moId 对象ID
	 * @param userId 操作者
	 * @param isch 是否包含子对象 1包含、其他不包含
	 * @return 1;成功，0失败
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
				bussinessLogger.log( LOG_TYPE, "TF_TT_SPECIALCALL", "修改特殊外呼规则：  " + moId );
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
	 * 删除规则
	 * @param moId 对象ID
	 * @return 1;成功，0失败
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
				bussinessLogger.log( LOG_TYPE, "TF_TT_SPECIALCALL", "删除特殊外呼规则：  " + moId );
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
	 * 根据查询规则
	 * @param moId 对象ID
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
	 * 取得全部规则
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

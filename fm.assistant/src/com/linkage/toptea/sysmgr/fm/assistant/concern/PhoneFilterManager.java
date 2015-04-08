package com.linkage.toptea.sysmgr.fm.assistant.concern;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.linkage.toptea.auc.BusinessLogger;
import com.linkage.toptea.context.Context;
import com.linkage.toptea.sysmgr.cm.ConfigManager;
import com.linkage.toptea.sysmgr.cm.MoInfo;
import com.linkage.toptea.util.BeanUtil;

/**
 * 过滤器内存管理器
 * @author huangzh
 *
 */
public class PhoneFilterManager {
	
	// Spring连接数据库 
	private DataSource dataSource;
	
	private static final String LOG_TYPE = "assistant";
	
	/** 业务日志 */
	private static BusinessLogger bussinessLogger;
	 
	/** 日志  */
	private Log logger = LogFactory.getLog(PhoneFilterManager.class);
	
	/** 内存中的自动外呼规则  */
	public static ArrayList<PhoneFilterInfo> ALL_FILTER = new ArrayList<PhoneFilterInfo>();
	
	private static ConfigManager configManager = (ConfigManager) Context.getContext().getAttribute("configManager");

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/** 设置业务日志模块句柄 */
	public void setBusinessLogger(BusinessLogger bussinessLogger) {
		PhoneFilterManager.bussinessLogger = bussinessLogger;
	}

	/** 初始化过滤器 */
	public void init() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = " SELECT * FROM TF_TT_ALARM_PHONEFILTER ORDER BY CTIME DESC";
		
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ArrayList<PhoneFilterInfo> list = new ArrayList<PhoneFilterInfo>();
			while (rs.next()) {
				PhoneFilterInfo info = new PhoneFilterInfo();
				info.setTimelen(rs.getString("timelen"));
				info.setIschild(rs.getInt("ischild"));
				info.setKey(rs.getString("key"));
				info.setIsuse(rs.getInt("isuse"));
				info.setForPara(rs.getInt("FORPARA"));
				info.setMoId(rs.getString("MO_ID"));
				list.add( info);
			}
			
			synchronized (ALL_FILTER) { 
				ALL_FILTER.clear(); 
				ALL_FILTER = list; 
				ALL_FILTER = sortPhoneFilterInfo( ALL_FILTER );
			}
			for ( PhoneFilterInfo info : ALL_FILTER ) {
				logger.info("  #   " + configManager.findMoById(info.getMoId()).getDotPath() );
			}
		} catch (Exception e) {
			logger.error("initConfigFilter", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
	}
	
	/**
	 * 添加自动外呼规则
	 * @param info  自动外呼规则
	 */
	public static synchronized void addFilter( PhoneFilterInfo info){
		if ( null == info ) { return; }
		// 添加自动外呼规则
		ALL_FILTER.add( info );
    	// 重新排序
		sortPhoneFilterInfo( ALL_FILTER );
		//  日志
		bussinessLogger.log( LOG_TYPE, info.getMoId(), "添加自动外呼规则： " + ToStringBuilder.reflectionToString(info) );
	}
	
	/**
	 * 按路径删除自动外呼规则
	 * @param moId 自动外呼规则对应的对象ID
	 */
    public static synchronized void rmFilter(String moId) {
    	// 参数为空的场合
    	if ( null == moId || "".equals( moId ) ) { return; }
    	// 遍历规则
    	for ( PhoneFilterInfo info : ALL_FILTER ) {
    		//按路径删除自动外呼规则
    		if ( null != info && moId.equals( info.getMoId() ) ) {
        		ALL_FILTER.remove(info); 
        		break;
    		}    			
    	}
    	// 重新排序
    	sortPhoneFilterInfo( ALL_FILTER );
		//  日志
		bussinessLogger.log( LOG_TYPE, moId, "删除自动外呼规则,路径:" + moId );
	}
	
    /**
     * 根据路径，及规则状态取得过滤规则
     * @param moId 对象ID
     * @param state 规则状态
     * @return
     */
    public static synchronized PhoneFilterInfo getFilter(String moId, int state ){
    	//  参数为空的场合
    	if ( null == moId || "".equals( moId ) ) { return null; }
    	
    	//遍历规则
    	for ( PhoneFilterInfo info : ALL_FILTER ) {
			if ( null == info ) { continue; }
    		// 路径相同的场合
    		if ( moId.equals( info.getMoId() ) ) {
    			// 状态匹配的场合：返回规则，否则结束
    			if ( info.getIsuse() == state ) { 
    				return info; 
    			} else {
    				break;
    			}
        		// 路径不相同的场合：包含子节点
    		} else if ( info.getIschild() == PhoneFilterInfo.FILTER_CHILD_YES ) {    			
    			MoInfo moA = configManager.findMoById( moId );
    			MoInfo moR = configManager.findMoById( info.getMoId() );
    			if ( moA != null ) {
    				String path = moA.getDotPath();
        			// 节点路径匹配的场合
        			if ( path.startsWith( moR.getDotPath() + "." ) ) {
        				// 状态匹配的场合：返回规则，否则结束
            			if ( info.getIsuse() == state ) { 
            				return info; 
            			} else {
            				break;
            			} 
        			}
    			}
    		}
    	}
 	   return null;
    }
    
	public static ArrayList<PhoneFilterInfo> sortPhoneFilterInfo ( List<PhoneFilterInfo> list) {
		PhoneFilterInfo info = new PhoneFilterInfo();
		PhoneFilterInfo[] array = (PhoneFilterInfo[]) list.toArray(new PhoneFilterInfo[list.size()]);
		for ( int i = 0; i < array.length; i++ ) {
			for ( int j = i+1; j < array.length; j++ ) {
				if ( array[i].compareTo(array[j]) > 0 ) {
					info = array[i];
					array[i] = array[j];
					array[j] = info;
				}
			}
		}
		list =  Arrays.asList(array);
		ArrayList<PhoneFilterInfo> infos = new ArrayList<PhoneFilterInfo>();
		infos.addAll(list);
		return infos;
	}
	
	/**
	 * 根据对象路径删除一组外呼规则
	 * @param moIds 对象ID
	 * @throws Exception
	 */
	public void removeFilters(String[] moIds) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " DELETE FROM TF_TT_ALARM_PHONEFILTER WHERE MO_ID = ? ";
		
		if ( moIds == null ) { return; }
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (String moId : moIds) {
				ps.setString(1, moId);
				ps.addBatch();
			}
			ps.executeBatch();
			for (String moId : moIds) {
				PhoneFilterManager.rmFilter(moId);
			}
			conn.commit();
		} catch (Exception e) {
			logger.error("removeFilter", e);
			try {
				conn.rollback();
			} catch (Exception ex) {
				logger.error("", e);
			}
			throw e;
		} finally {
			conn.setAutoCommit(true);
			BeanUtil.closeResource(ps, conn);
		}
	}
	
	/**
	 * 根据对象路径删除一条外呼规则
	 * @param moId 对象ID
	 * @throws Exception
	 */
	public void removeFilter(String moId) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " DELETE FROM TF_TT_ALARM_PHONEFILTER WHERE MO_ID=? ";
		
		try {
			conn = this.dataSource.getConnection();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			ps.setString(1, moId);
			ps.executeUpdate();
			
			PhoneFilterManager.rmFilter(moId);
			
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
	 * 更新外呼参数配置信息
	 * @param List<PhoneFilterInfo> items 更新列表
	 * @throws Exception
	 */
	public void updateFilters(List<PhoneFilterInfo> items) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "merge into TF_TT_ALARM_PHONEFILTER t  using "
			 + "(select ? as moid,? as key,? as TIMELEN,? as ISCHILD,? as ISUSE,? as FORPARA from dual) t2";
		sql += " on (t.mo_id=t2.moid) "
		 +" WHEN MATCHED THEN "
		 +" update set t.key=t2.key,t.TIMELEN=t2.TIMELEN,t.ISCHILD=t2.ISCHILD,t.ISUSE=t2.ISUSE,t.FORPARA=t2.FORPARA"
		 +" WHEN NOT MATCHED THEN "
		 +" INSERT (MO_ID,TIMELEN,ISCHILD,KEY,ISUSE,FORPARA) values(t2.moid,t2.TIMELEN,t2.ISCHILD,t2.key,t2.ISUSE,t2.FORPARA)";
		
		try {
			conn = this.dataSource.getConnection();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for(PhoneFilterInfo info:items){
				ps.setString(1,info.getMoId());
				ps.setString(2, info.getKey());
				ps.setString(3, info.getTimelen());
				ps.setInt(4, info.getIschild());
				ps.setInt(5, info.getIsuse());
				ps.setInt(6, info.getForPara());
				ps.addBatch();
			}
			ps.executeBatch();			
			conn.commit();
			for(PhoneFilterInfo info:items){ 
				//  日志
				bussinessLogger.log( LOG_TYPE, info.getMoId(), 
						"更新外呼参数配置信息： " + ToStringBuilder.reflectionToString(info) );
			}
			// 重新初始化
			init();
		} catch (Exception e) {
			logger.error("updateFilter", e);
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
	
	public void updateFilters(String xml) throws Exception {
		Reader reader = null;
		List<PhoneFilterInfo> items = new ArrayList<PhoneFilterInfo>();
		try {

			reader = new StringReader("<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + xml);
			DOMParser p = new DOMParser();
			p.parse(new InputSource(reader));
			Element root = p.getDocument().getDocumentElement();
			NodeList nl = root.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				Element el = (Element) node;
				PhoneFilterInfo info = new PhoneFilterInfo();
				info.setMoId((String) el.getAttribute("path"));
				info.setKey((String) el.getAttribute("key"));
				info.setTimelen((String) el.getAttribute("timelen"));
				info.setIschild(Integer.parseInt((String) el.getAttribute("ischild")));
				info.setIsuse((el.getAttribute("isuse") == null ? 1 : Integer.parseInt((String) el.getAttribute("isuse"))));
				info.setForPara((el.getAttribute("forPara") == null ? 1 : Integer.parseInt(el.getAttribute("forPara"))));
				items.add(info);
			}
			updateFilters(items);

		} catch (Exception e) {
			logger.error("updateFilters", e);
		}

	}
	
	/**
	 * 判定两个对象是否父子或相等关系
	 * @param fMoId 父对象ID
	 * @param cMoId 子对象ID
	 * @return true 是否父子或相等关系
	 */
	public boolean ischild ( String fMoId, String cMoId ) {
		MoInfo fmo = configManager.findMoById(fMoId);
		MoInfo cmo = configManager.findMoById(cMoId);
		if (fmo != null && cmo != null ) {
			String fpath = fmo.getDotPath();
			String cpath = cmo.getDotPath();
			if ( null != cpath && !"".equals(cpath) ) {
				if ( cpath.equals(fpath) || cpath.startsWith(fpath + ".") ) { return true; }
			}
		}
		return false;
	}
	
	/**
	 * 获取通过条件过滤器
	 * @param filter
	 * @param start
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	public List<PhoneFilterInfo> findFilter(PhoneFilter filter, int start, int limit) throws Exception {
		List<PhoneFilterInfo> items = new ArrayList<PhoneFilterInfo>();
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		ResultSet rs = null;
		try {			
			MoInfo rmo = configManager.findMoById(filter.getMoId());
			String rpath  = "";
			if ( null != rmo ) { rpath  = rmo.getDotPath(); }
			
			conn = this.dataSource.getConnection();
			sql = "select a.*,FN_GET_MOPATH(m.path) as pathname from TF_TT_ALARM_PHONEFILTER a, tf_tt_mo m  where a.mo_id = m.id ";
			
			if ( rpath != null && !"".equals(rpath) ) {
				if ( filter.isInherit() == true ) {
					sql += " and  ( m.path like '" + rpath + ".%' or m.path = '" + rpath + "') ";
				} else {
					sql += " and   m.path = '" + rpath + "' ";
				}
			}
				
			if (filter.getIsuse() != -100) {
				sql += " and a.isuse=" + filter.getIsuse();
			}
			sql += "order by a.ctime desc";
			if (limit > 0) {
				sql = "SELECT t2.* FROM (SELECT ROWNUM NO,T.* FROM (" + sql + ") T WHERE ROWNUM <=" + (start + limit)
						+ ") T2 WHERE NO>=" + start;
			}
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				PhoneFilterInfo info = new PhoneFilterInfo();
				info.setMoId(rs.getString("mo_id"));
				info.setPathName(rs.getString("pathname"));
				info.setIschild(rs.getInt("ischild"));
				info.setKey(rs.getString("key"));
				info.setIsuse(rs.getInt("ISUSE"));
				info.setTimelen(rs.getString("timelen"));
				info.setForPara(rs.getInt("FORPARA"));
				items.add(info);
			}

		} catch (Exception e) {
			logger.error("findFilter", e);
			throw e;
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return items;
	}
	
    public void startFilters() throws Exception{
    	PhoneFilter filter = new PhoneFilter();
    	filter.setInherit( true );
		filter.setIsuse(PhoneFilterInfo.FILTER_PAUSE);
		try{
			List <PhoneFilterInfo> items = findFilter(filter,0,0);
			for(PhoneFilterInfo info:items){
				info.setIsuse(PhoneFilterInfo.FILTER_USE);
			}
			updateFilters(items);
		}catch(Exception e){
			throw e;
		}
	}
	public void pauseFilters() throws Exception{
		PhoneFilter filter = new PhoneFilter();
		filter.setIsuse(PhoneFilterInfo.FILTER_USE);
		filter.setInherit( true );
		try{
			List <PhoneFilterInfo> items = findFilter(filter,0,0);
			for(PhoneFilterInfo info:items){
				info.setIsuse(PhoneFilterInfo.FILTER_PAUSE);
			}
			updateFilters(items);
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * 获取过滤的条数
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public int getPhoneFilterCount(PhoneFilter filter) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		ResultSet rs = null;
		int count = 0;
		try {
			MoInfo rmo = configManager.findMoById(filter.getMoId());
			String rpath  = "";
			if ( null != rmo ) { rpath  = rmo.getDotPath(); }
			
			conn = this.dataSource.getConnection();
			sql = "select count(*) as c from TF_TT_ALARM_PHONEFILTER a, tf_tt_mo m  where a.mo_id = m.id ";
			if ( rpath != null && !"".equals(rpath)) {
				if ( filter.isInherit() == true ) {
					sql += " and  ( m.path like '" + rpath + ".%' or m.path = '" + rpath + "') ";
				} else {
					sql += " and   m.path = '" + rpath + "' ";
				}
			}
			if (filter.getIsuse()!=-100){
				sql += " and isuse="+filter.getIsuse();
			}
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt("c");
			}

		} catch (Exception e) {
			logger.error("getPhoneFilter", e);
			e.printStackTrace();
			throw e;
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return count;
	}  
}

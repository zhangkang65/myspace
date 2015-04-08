package com.linkage.toptea.sysmgr.fm.assistant.callkeyword;

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

import com.linkage.toptea.auc.BusinessLogger;
import com.linkage.toptea.context.Context;
import com.linkage.toptea.sysmgr.cm.ConfigManager;
import com.linkage.toptea.sysmgr.cm.MoInfo;
import com.linkage.toptea.sysmgr.fm.AlarmInfo;
import com.linkage.toptea.sysmgr.fm.util.AlarmUtil;
import com.linkage.toptea.util.BeanUtil;
import com.linkage.toptea.util.StringUtil;

import de.jbader.util.uuid.util.UUIDFactory;

/**
 * 按关键字自动外呼管理类
 * @author huangzh
 *
 */
public class CallKeyWordManager {
	

	private Log logger = LogFactory.getLog(CallKeyWordManager.class);

	/** 业务日志 */
	private BusinessLogger bussinessLogger;

	/** Spring连接数据库 */
	private DataSource dataSource;
	
	private final String LOG_TYPE = "assistant";
	
	/** 内存中的规则 */
	private static ArrayList<CallKeyWordInfo>  ALL_KEYWORD = new ArrayList<CallKeyWordInfo>();
	
	ConfigManager cm = (ConfigManager)Context.getContext().getAttribute("configManager");
	
	private UUIDFactory UUID = UUIDFactory.create();
	
	/** 设置业务日志模块句柄 */
	public void setBusinessLogger(BusinessLogger bussinessLogger) {
		this.bussinessLogger = bussinessLogger;
	}	

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
		
	public void init() {

		ArrayList<CallKeyWordInfo> infos = indexCallKeyWordInfo(null, null);
		synchronized ( ALL_KEYWORD ) {
			ALL_KEYWORD.clear();
			ALL_KEYWORD = infos;
			ALL_KEYWORD = sortPhoneFilterInfo(ALL_KEYWORD);
		}
		for ( int i = 0; i < ALL_KEYWORD.size(); i++ ) {
			logger.info( " callKeyWordInfo: " + cm.getNamingPathById(ALL_KEYWORD.get(i).getMoId()));
		}
	}
	
	public int check ( AlarmInfo alarm ) {
		// 告警为空，不处理
		if ( alarm == null  ) { return -1; }
				
		//遍历规则
		for ( int i = 0; i < ALL_KEYWORD.size(); i++ ) {
			CallKeyWordInfo info = ALL_KEYWORD.get(i);
			if ( validateMo (info, alarm.getMoId()) == false ) {
				continue;
			// 告警内容包含关键字的场合
			} else if ( alarm.getContent().indexOf( info.getKeyword() ) >=0  ) {
				
				return info.getCaller();
			}			
		}

		return -1;
	}
	
	/**
	 * 增加自动外呼关键字规则
	 * 
	 * @param info
	 *            自动外呼关键字规则
	 * @return >0 成功
	 */
	public int addCallKeyWordInfo ( CallKeyWordInfo info ) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " INSERT INTO  TF_TT_ALARM_CALL_KEYWORD "
			 + " (ID, MOID, KEYWORD, FORCHILD, REGEXPR, USERID, CALLER) VALUES (?,?,?,?,?,?,?) ";
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			String id = StringUtil.encodeBase64(UUID.createUUID().getBytes()).replace('+', '@').replace('/', '$');
			ps.setString( 1, id );
			ps.setString( 2, info.getMoId() );
			ps.setString( 3, info.getKeyword() );
			ps.setInt( 4, info.getForChild() );
			ps.setInt( 5, info.getRegExpr() );
			ps.setString( 6, info.getUserId() );
			ps.setInt( 7, info.getCaller());
			int r = ps.executeUpdate();
			if ( r > 0 ) {
				init();
				bussinessLogger.log(LOG_TYPE, info.getMoId(), 
						"新增自动外呼关键字规则，" + ToStringBuilder.reflectionToString(info));
			}
			return r;

		} catch (Exception e) {
			logger.error("addCallKeyWordInfo", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return 0;
	}
	
	/**
	 * 修改自动外呼关键字规则
	 * @param info 自动外呼关键字规则
	 * @return >0 成功
	 */
	public int updateCallKeyWordInfo ( CallKeyWordInfo info ) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " UPDATE  TF_TT_ALARM_CALL_KEYWORD SET  MOID = ?,  KEYWORD = ?, " 
				+ " FORCHILD = ?, REGEXPR = ?, USERID = ? , CALLER = ? , TIME = SYSDATE WHERE ID = ? ";
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString( 1, info.getMoId() );
			ps.setString( 2, info.getKeyword() );
			ps.setInt( 3, info.getForChild() );
			ps.setInt( 4, info.getRegExpr() );
			ps.setString( 5, info.getUserId() );
			ps.setInt( 6, info.getCaller());
			ps.setString( 7, info.getId() );
			int r = ps.executeUpdate();
			if ( r > 0 ) {
				init();
				bussinessLogger.log(LOG_TYPE, info.getMoId(), 
						"更新自动外呼关键字规则，" + ToStringBuilder.reflectionToString(info));
			}
			return r;

		} catch (Exception e) {
			logger.error("updateCallKeyWordInfo", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return 0;
	}
	
	/**
	 * 删除自动外呼关键字规则
	 * @param id 自动外呼关键字规则ID
	 * @return >0 成功
	 */
	public int deleteCallKeyWordInfo ( String id ) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = " DELETE  TF_TT_ALARM_CALL_KEYWORD  WHERE ID = ? ";
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString( 1, id );
			int r = ps.executeUpdate();
			if ( r > 0 ) {
				init();
				bussinessLogger.log(LOG_TYPE, id, "删除自动外呼关键字规则，" + id);
			}
			return r;

		} catch (Exception e) {
			logger.error("deleteCallKeyWordInfo", e);
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return 0;
	}
	
	/**
	 * 查询自动外呼关键字规则
	 * @param condtion 查询条件
	 * @param type 查询类型
	 * @return 自动外呼关键字规则
	 */
	public ArrayList<CallKeyWordInfo> indexCallKeyWordInfo ( String condtion, String type ) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sql = " SELECT * FROM  TF_TT_ALARM_CALL_KEYWORD ";
		if (null != condtion && !"".equals(condtion) ) {
			if ( "MO".equals(type) ) {
				sql = sql + " WHERE MOID = '" + condtion + "'";
			} else {
				sql = sql + " WHERE KEYWORD = '" + condtion + "'";				
			}
		}
		
		ArrayList<CallKeyWordInfo> result = new ArrayList<CallKeyWordInfo>();
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while ( rs.next() ) {
				CallKeyWordInfo info = new CallKeyWordInfo();
				info.setCaller(rs.getInt("CALLER"));
				info.setForChild(rs.getInt("FORCHILD"));
				info.setId(rs.getString("ID"));
				info.setKeyword(rs.getString("KEYWORD"));
				info.setMoId(rs.getString("MOID"));
				info.setRegExpr(rs.getInt("REGEXPR"));
				info.setTime(rs.getTimestamp("TIME"));
				info.setUserId(rs.getString("USERID"));
				result.add( info );
			}

		} catch (Exception e) {
			logger.error("indexCallKeyWordInfo", e);
		} finally {
			BeanUtil.closeResource(rs, ps, conn);
		}
		return result;
	}
	
	/**
	 * 查询自动外呼关键字左侧显示列表
	 * @param condtion 查询条件
	 * @param type 查询类型
	 * @return 自动外呼关键字规则
	 */
	public List<String> getCallKeyWordInfo ( String condtion, String type ) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sql = " SELECT DISTINCT KEYWORD FROM  TF_TT_ALARM_CALL_KEYWORD ";

		if ( "MO".equals(type) ) {
			sql = " SELECT DISTINCT MOID FROM  TF_TT_ALARM_CALL_KEYWORD ";
		}
		
		List<String> result = new ArrayList<String>();
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while ( rs.next() ) {
				result.add( rs.getString(1) );
			}
			
		} catch (Exception e) {
			logger.error("indexCallKeyWordInfo", e);
		} finally {
			BeanUtil.closeResource(rs, ps, conn);
		}
		return getKeyWord(result, condtion, type);
	}
	
	public List<String> getKeyWord( List<String> infos, String condtion, String type ) {
		if ( null == infos || infos.size() == 0 ) { return null; }		
		for ( int i = 0; i < infos.size(); i++ ) {
			String object = infos.get( i );
			if ( "MO".equals(type) ) {
				String caption = cm.getNamingPathById(object);
				if ( null == condtion || "".equals(condtion) ) {
					infos.set(i, object + "_-_" + caption);
				} else if ( caption.indexOf(condtion) == -1 ) {
					infos.remove(i);
					i--;
				} else {
					infos.set(i, object + "_-_" + caption);
				}
			} else {
				if ( null == condtion || "".equals(condtion) ) { return infos; }
				if ( object.indexOf(condtion) ==-1 ) {
					infos.remove(i);
					i--;
				}
			}
		}
		return infos;
	}
	
	/**
	 * 查询自动外呼关键字规则
	 * @param id 规则条件
	 * @return 自动外呼关键字规则
	 */
	public CallKeyWordInfo indexCallKeyWordInfo ( String id ) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sql = " SELECT * FROM  TF_TT_ALARM_CALL_KEYWORD WHERE ID = '" + id + "'";

		
		CallKeyWordInfo info = null;
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if ( rs.next() ) {
				info = new CallKeyWordInfo();
				info.setCaller(rs.getInt("CALLER"));
				info.setForChild(rs.getInt("FORCHILD"));
				info.setId(rs.getString("ID"));
				info.setKeyword(rs.getString("KEYWORD"));
				info.setMoId(rs.getString("MOID"));
				info.setRegExpr(rs.getInt("REGEXPR"));
				info.setTime(rs.getTimestamp("TIME"));
				info.setUserId(rs.getString("USERID"));
			}

		} catch (Exception e) {
			logger.error("indexCallKeyWordInfo", e);
		} finally {
			BeanUtil.closeResource(rs, ps, conn);
		}
		return info;
	}
	
	
	/**
	 * 按路径排序
	 * @param list
	 * @return
	 */
	public static ArrayList<CallKeyWordInfo> sortPhoneFilterInfo ( List<CallKeyWordInfo> list) {
		CallKeyWordInfo info = new CallKeyWordInfo();
		CallKeyWordInfo[] array = (CallKeyWordInfo[]) list.toArray(new CallKeyWordInfo[list.size()]);
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
		ArrayList<CallKeyWordInfo> infos = new ArrayList<CallKeyWordInfo>();
		infos.addAll(list);
		return infos;
	}
	
	/**
	 * 告警对象校验
	 * @param ruleMoCaptions 规则对象标签
	 * @param ruleMoState 规则对象状态
	 * @param alarmMoId 告警对象ID
	 * @return true 校验通过 false 校验未通过
	 */
	private boolean validateMo(CallKeyWordInfo info, String alarmMoId) {
		// 应用于子节点的场合
		if (info.getForChild() == 1) {
			// moid校验
			MoInfo alarmMo =  cm.findMoById( alarmMoId );			
			if (  alarmMo == null  ) { return false; }
			
			String alarmPath = alarmMo.getDotPath();
			
			String rulePath = AlarmUtil.getDotPathFromNodeId(info.getMoId());
			if(rulePath == null) return false;
			
			// 规则设定为根节点的场合，全部生效
			if (AlarmUtil.PATH_ROOT.equals( rulePath ) ) { return true;}

			if ( alarmPath.startsWith(rulePath + ".") || alarmPath.equals(rulePath)) {
				return true; 
			}
			
		} else { // 非应用于子节点的场合
			// moId完全匹配返回true
			if (info.getMoId().equals(alarmMoId)) { return true; }
		}
		return false;
	}
}

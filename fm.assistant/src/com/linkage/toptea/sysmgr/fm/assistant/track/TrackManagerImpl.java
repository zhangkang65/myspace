package com.linkage.toptea.sysmgr.fm.assistant.track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Category;

import com.linkage.toptea.sysmgr.fm.AlarmInfo;
import com.linkage.toptea.sysmgr.fm.assistant.AstConfig;
import com.linkage.toptea.sysmgr.fm.assistant.UserTransaction;
import com.linkage.toptea.util.BeanUtil;

/**
 * 功能：跟踪告警的实现类 作者：潘华 时间：20110214
 */
public class TrackManagerImpl implements TrackManager {

	// 日志打印
	private Category logger = Category.getInstance(TrackManagerImpl.class);

	// Spring连接数据库
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	// 初始化类
	public void init() {
		new TrackAlarmThread().start();
	}

	   /**
	    * 获取一个告警的所有跟踪
	    * @param alarmId
	    * @return
	    * @throws Exception
	    */
	   public List<TrackInfo> getTracks(String alarmId)throws Exception{
		   List<TrackInfo> items = new ArrayList();
		   Connection conn = null;
			PreparedStatement ps = null;
			String sql = "";
			ResultSet rs = null;
			try {
				conn = this.dataSource.getConnection();
				sql = "select * from tf_tt_alarm_track_list t where t.alarmid=?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, alarmId);
		        rs = ps.executeQuery();
		        while(rs.next()){
		        	TrackInfo info = new TrackInfo();
		        	//ALARMID,ID,TRACKER,CONTENT,TRACKDATE,TYPE
		        	info.setAlarmId(rs.getString("ALARMID"));
		        	info.setId(rs.getString("ID"));
		        	info.setContent(rs.getString("CONTENT"));
		        	info.setTracker(rs.getString("TRACKER"));
		        	if (rs.getTimestamp("TRACKDATE")!=null){
		        		Timestamp ts = rs.getTimestamp("TRACKDATE");
		        		info.setTrackdate(ts.getTime());
		        	}
		        	info.setType(rs.getInt("type"));
		        	items.add(info);
		        }
		        rs.close();
			} catch (Exception e) {
				logger.error("跟踪告警同步失败", e);

			} finally {
				BeanUtil.closeResource(ps, conn);
			}

		   return items;
	   }
	/**
	 * 添加跟踪
	 * 
	 * @param info
	 * @throws Exception
	 */
	public void addTrack(TrackInfo info) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		UserTransaction ut = null;
		try {
			conn = this.dataSource.getConnection();
			ut = new UserTransaction(conn);
			ut.begin();
			sql = "insert into TF_TT_ALARM_TRACK_LIST(ALARMID,ID,TRACKER,CONTENT,TRACKDATE,TYPE) values(?,?,?,?,(select NEXTTIME from TF_TT_ALARM_TRACK where ALARMID=?),?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, info.getAlarmId());
			ps.setString(2, info.getId());
			ps.setString(3,info.getTracker());
			ps.setString(4,info.getContent());
			ps.setString(5, info.getAlarmId());
			ps.setInt(6,info.getType());
			ps.executeUpdate();
			ps.close();
			
			if (info.getType()==1)
				sql = "update TF_TT_ALARM_TRACK set nexttime=sysdate+"+AstConfig.TRACK_TIME+"/24 where alarmid=?";
			else 
				sql = "update TF_TT_ALARM_TRACK set nexttime=sysdate+"+AstConfig.TRACK_NEXT_TIME+"/24 where alarmid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1,info.getAlarmId());
			ps.executeUpdate();
			ut.commit();
	        
		} catch (Exception e) {
			ut.rollback();
			logger.error("跟踪告警同步失败", e);
			throw e;
		} finally {
			BeanUtil.closeResource(ps, conn);
		}

	}

/**
    * 取消跟踪告警
    * @param list 取消跟踪的告警列表
    */
    public void cancelTrack(List<TrackInfo> list) throws Exception
   {
    	logger.debug("cancelTrack start");
    	Connection conn = null;
		PreparedStatement ps = null;
		String cancelTrackSql = "update TF_TT_ALARM_TRACK set ISTRACK='0' where ALARMID = ?";
		UserTransaction ut = null;
		
		if (null == list || list.isEmpty())
		{
			return;
		}
		
		try {
			conn = this.dataSource.getConnection();
			ut = new UserTransaction(conn);
			ut.begin();
			ps = conn.prepareStatement(cancelTrackSql);
			for (TrackInfo info : list)
			{
				ps.setString(1, info.getAlarmId());
				ps.addBatch();
			}
			int[] results = ps.executeBatch();
			ut.commit();
			logger.debug("取消跟踪告警结束。SQL执行结果：" + results);
		} catch (Exception e) {
			ut.rollback();
			logger.error("取消跟踪告警失败", e);
			throw e;
		} finally {
			BeanUtil.closeResource(ps, conn);
		}

   }
    
    /**
     * 根据跟踪过滤路径列表，过滤跟踪告警
     * @return
     */
    private void filtrateTrackAlarmByPath()
    {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement cancelPS = null;
		String sql = "select alarmid, path from tf_tt_alarm_track t, tf_tt_alarm a where t.alarmid=a.alarm_id";
		String cancelTrackSql = "update TF_TT_ALARM_TRACK set ISTRACK=? where ALARMID = ?";
		ResultSet rs = null;
		try 
		{
			String[] filters = AstConfig.TRACK_PATH_FILTER_ID.split(";");
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			cancelPS = conn.prepareStatement(cancelTrackSql);
	        rs = ps.executeQuery();
	        while (rs.next())
	        {
	        	if (isTrackFiltrated(rs.getString("path"), filters))
	        	{
	        	    cancelPS.setString(1, "0");
	        	    cancelPS.setString(2, rs.getString("alarmid"));
	        	    cancelPS.addBatch();
	        	}
	        }
            int[] count = cancelPS.executeBatch();
            conn.commit();
            logger.debug("过滤跟踪告警结束。SQL执行结果：" + count);
		} 
		catch (Exception e) 
		{
			logger.error("过滤跟踪告警失败", e);
		} 
		finally 
		{
			BeanUtil.closeResource(ps, conn);
			BeanUtil.closeResource(cancelPS, conn);
		}
    }
    
    /**
     * 判断一个告警来源是否为被过滤的对象或者被过滤对象的子对象
     * @param path 告警来源path
     * @param filters 被过滤对象数组
     * @return
     */
    private boolean isTrackFiltrated(String path, String[] filters)
    {
    	for (String filter : filters)
    	{
    		// change by huangzh 2013-08-19
    		//if (!"".equals(filter.trim()) && path.startsWith(filter))
    		if (!"".equals(filter.trim()) && (path.startsWith(filter+".") || path.equals(filter )) )
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }

	/**
	 * 当前需要跟踪
	 * 
	 * @return
	 */
	public boolean isTrack() {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";
		ResultSet rs = null;
		boolean ishave = false;
		try {
			conn = this.dataSource.getConnection();
			sql = "select * from tf_tt_alarm_track t where t.nexttime<=sysdate and isTrack ='1'";
			ps = conn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        ishave = rs.next();
			rs.close();
		} catch (Exception e) {
			logger.error("跟踪告警同步失败", e);

		} finally {
			BeanUtil.closeResource(ps, conn);
		}

		return ishave;
	}
	
	 /**
    *
    * @param rs转变成告警类
    * @return com.linkage.toptea.sysmgr.fm.AlarmInfo
    * @throws SQLException
    */
   private AlarmInfo setAlarmInfoFromResultSet(ResultSet rs) throws SQLException {
       AlarmInfo alarm = new AlarmInfo();
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

       alarm.setAdditionalInfo ( rs.getString("ADDITIONAL_INFO") );
       if ( rs.getMetaData().getColumnCount() >=22 ) {
           alarm.setPath( rs.getString("PATH") );        	
       }
       return alarm;
   }
   /**
    * 获取所有跟踪告警总数
    */
   public int getTrackAlarmCount() throws Exception{
	   Connection conn = null;
		PreparedStatement ps = null;
		int count = 0;
		String sql = "";
		ResultSet rs = null;
		try {
			conn = this.dataSource.getConnection();
			sql = "select count(*) as c from tf_tt_alarm a,tf_tt_alarm_track b where a.alarm_id=b.alarmid and b.nexttime<=sysdate and ISTRACK='1'";
			ps = conn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        if (rs.next()) count = rs.getInt("c");
			rs.close();
		} catch (Exception e) {
			logger.error("获取跟踪告警列表", e);

		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return count;
   }
	/**
	   *获取跟踪告警列表
	   *
	   */
	 public List<AlarmInfo> getTrackAlarms(int start,int limit) throws Exception{
		  List<AlarmInfo> items = new ArrayList();
		  Connection conn = null;
			PreparedStatement ps = null;
			String sql = "";
			ResultSet rs = null;
			try {
				conn = this.dataSource.getConnection();
				sql = "select a.* from tf_tt_alarm a,tf_tt_alarm_track b where a.alarm_id=b.alarmid and b.nexttime<=sysdate  and b.ISTRACK='1'";
				sql = "SELECT t2.* FROM (SELECT ROWNUM NO,T.* FROM (" + sql
				+ ") T WHERE ROWNUM < " + (start + limit)
				+ ") T2 WHERE NO>=" + start;
				ps = conn.prepareStatement(sql);
		        rs = ps.executeQuery();
		        while(rs.next()){
		        	items.add(setAlarmInfoFromResultSet(rs));
		        }
				rs.close();
			} catch (Exception e) {
				logger.error("获取跟踪告警列表", e);

			} finally {
				BeanUtil.closeResource(ps, conn);
			}
		  return items;
	 }

	/**
	 * 跟踪告警同步
	 */
	public void TackSync() {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		String sql = "";
		ResultSet rs = null;
		try{
			conn = this.dataSource.getConnection();
			try {
				/*
				int count = 0;
				String sql1 = "insert into TF_TT_ALARM_TRACK(ALARMID,NEXTTIME) values(?,?)";
				ps1 = conn.prepareStatement(sql1);
				//添加告警进入跟踪库
				sql = " select a.alarm_id,a.ALARM_FIRSTOCCURTIME from TF_TT_ALARM a where a.ALARM_CURRENTGRADE>=? "+AstConfig.getPathFilterSQL()+" and  a.alarm_firstoccurtime<=sysdate-" + AstConfig.TRACK_TIME + "/24 and not exists (select * from TF_TT_ALARM_TRACK b where b.alarmId=a.alarm_id)";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, AstConfig.TRACK_GRADE);
				rs = ps.executeQuery();
				while(rs.next()){
					ps1.setString(1, rs.getString("ALARM_ID"));
					ps1.setTimestamp(2, rs.getTimestamp("ALARM_FIRSTOCCURTIME"));
					ps1.addBatch();
					count++;
					if (count>=500) break;
					
				}
				rs.close();
				ps.close();
				if (count>0) ps1.executeBatch();
		        ps1.close();
		        */
				sql = "insert into TF_TT_ALARM_TRACK(ALARMID,NEXTTIME)  select a.alarm_id,a.ALARM_FIRSTOCCURTIME from TF_TT_ALARM a where a.ALARM_CURRENTGRADE>=? "+AstConfig.getPathFilterSQL()+" and  a.alarm_firstoccurtime<=sysdate-" + AstConfig.TRACK_TIME + "/24 and not exists (select * from TF_TT_ALARM_TRACK b where b.alarmId=a.alarm_id)";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, AstConfig.TRACK_GRADE);
				ps.executeUpdate();
				ps.close();
		            
			} catch (Exception e) {
				logger.error("添加告警进入跟踪库失败", e);
			} 
			
			try {
				//清除跟踪库中的告警
				sql = "delete from TF_TT_ALARM_TRACK a where not exists (select * from TF_TT_ALARM b  where b.alarm_id=a.alarmid) or a.alarmid=(select c.alarm_id from TF_TT_ALARM c where c.alarm_id=a.alarmid and c.ALARM_CURRENTGRADE<?) ";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, AstConfig.TRACK_GRADE);
				ps.executeUpdate();
				
			} catch (Exception e) {
				logger.error("添加告警进入跟踪库失败", e);
			} 
		} catch (Exception e) {
			logger.error("跟踪告警同步失败", e);

		} finally {
			BeanUtil.closeResource(ps, conn);
		}

		filtrateTrackAlarmByPath();
	}

	/**
	 * 功能：关注告警线程
	 * 
	 */
	class TrackAlarmThread extends Thread {
		public TrackAlarmThread() {
			this.setName("Track_Thread");
		}

		public void run() {
			while (true) {
				try{
					TackSync();
				}catch(Exception ex){
					logger.error("关注告警线程1", ex);
				}
			
				try {
				
					// 定期执行
					Thread.sleep(2 * 60 * 1000);
				} catch (Exception e) {
					logger.error("关注告警线程", e);
				}
			}

		}
	}
}

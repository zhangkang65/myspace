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
 * ���ܣ����ٸ澯��ʵ���� ���ߣ��˻� ʱ�䣺20110214
 */
public class TrackManagerImpl implements TrackManager {

	// ��־��ӡ
	private Category logger = Category.getInstance(TrackManagerImpl.class);

	// Spring�������ݿ�
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	// ��ʼ����
	public void init() {
		new TrackAlarmThread().start();
	}

	   /**
	    * ��ȡһ���澯�����и���
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
				logger.error("���ٸ澯ͬ��ʧ��", e);

			} finally {
				BeanUtil.closeResource(ps, conn);
			}

		   return items;
	   }
	/**
	 * ��Ӹ���
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
			logger.error("���ٸ澯ͬ��ʧ��", e);
			throw e;
		} finally {
			BeanUtil.closeResource(ps, conn);
		}

	}

/**
    * ȡ�����ٸ澯
    * @param list ȡ�����ٵĸ澯�б�
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
			logger.debug("ȡ�����ٸ澯������SQLִ�н����" + results);
		} catch (Exception e) {
			ut.rollback();
			logger.error("ȡ�����ٸ澯ʧ��", e);
			throw e;
		} finally {
			BeanUtil.closeResource(ps, conn);
		}

   }
    
    /**
     * ���ݸ��ٹ���·���б����˸��ٸ澯
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
            logger.debug("���˸��ٸ澯������SQLִ�н����" + count);
		} 
		catch (Exception e) 
		{
			logger.error("���˸��ٸ澯ʧ��", e);
		} 
		finally 
		{
			BeanUtil.closeResource(ps, conn);
			BeanUtil.closeResource(cancelPS, conn);
		}
    }
    
    /**
     * �ж�һ���澯��Դ�Ƿ�Ϊ�����˵Ķ�����߱����˶�����Ӷ���
     * @param path �澯��Դpath
     * @param filters �����˶�������
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
	 * ��ǰ��Ҫ����
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
			logger.error("���ٸ澯ͬ��ʧ��", e);

		} finally {
			BeanUtil.closeResource(ps, conn);
		}

		return ishave;
	}
	
	 /**
    *
    * @param rsת��ɸ澯��
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
    * ��ȡ���и��ٸ澯����
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
			logger.error("��ȡ���ٸ澯�б�", e);

		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return count;
   }
	/**
	   *��ȡ���ٸ澯�б�
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
				logger.error("��ȡ���ٸ澯�б�", e);

			} finally {
				BeanUtil.closeResource(ps, conn);
			}
		  return items;
	 }

	/**
	 * ���ٸ澯ͬ��
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
				//��Ӹ澯������ٿ�
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
				logger.error("��Ӹ澯������ٿ�ʧ��", e);
			} 
			
			try {
				//������ٿ��еĸ澯
				sql = "delete from TF_TT_ALARM_TRACK a where not exists (select * from TF_TT_ALARM b  where b.alarm_id=a.alarmid) or a.alarmid=(select c.alarm_id from TF_TT_ALARM c where c.alarm_id=a.alarmid and c.ALARM_CURRENTGRADE<?) ";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, AstConfig.TRACK_GRADE);
				ps.executeUpdate();
				
			} catch (Exception e) {
				logger.error("��Ӹ澯������ٿ�ʧ��", e);
			} 
		} catch (Exception e) {
			logger.error("���ٸ澯ͬ��ʧ��", e);

		} finally {
			BeanUtil.closeResource(ps, conn);
		}

		filtrateTrackAlarmByPath();
	}

	/**
	 * ���ܣ���ע�澯�߳�
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
					logger.error("��ע�澯�߳�1", ex);
				}
			
				try {
				
					// ����ִ��
					Thread.sleep(2 * 60 * 1000);
				} catch (Exception e) {
					logger.error("��ע�澯�߳�", e);
				}
			}

		}
	}
}

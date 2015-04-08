package com.linkage.toptea.sysmgr.fm.assistant.track;

import java.util.List;

import com.linkage.toptea.sysmgr.fm.AlarmInfo;

/**
 * 功能：跟踪接口类
 * 作者：潘华
 * 时间：2011-02-11
 *
 */
public interface TrackManager {
	/**
	 * 当前需要跟踪
	 * @return
	 */
   public boolean isTrack();
   /**
    * 添加跟踪
    * @param info
    * @throws Exception
    */
   public void addTrack(TrackInfo info) throws Exception;
  
   /**
    * 获取一个告警的所有跟踪
    * @param alarmId
    * @return
    * @throws Exception
    */
   public List<TrackInfo> getTracks(String alarmId) throws Exception;
   /**
   *获取跟踪告警列表
   *
   */
   public List<AlarmInfo> getTrackAlarms(int start,int limit) throws Exception;
   /**
    * 获取跟踪告警总数
    * @return
    * @throws Exception
    */
   public int getTrackAlarmCount() throws Exception;
   
   /**
    * 取消跟踪告警
    * @param info 跟踪告警
    */
   public void cancelTrack(List<TrackInfo> list) throws Exception;
}

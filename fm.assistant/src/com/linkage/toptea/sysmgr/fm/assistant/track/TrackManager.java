package com.linkage.toptea.sysmgr.fm.assistant.track;

import java.util.List;

import com.linkage.toptea.sysmgr.fm.AlarmInfo;

/**
 * ���ܣ����ٽӿ���
 * ���ߣ��˻�
 * ʱ�䣺2011-02-11
 *
 */
public interface TrackManager {
	/**
	 * ��ǰ��Ҫ����
	 * @return
	 */
   public boolean isTrack();
   /**
    * ��Ӹ���
    * @param info
    * @throws Exception
    */
   public void addTrack(TrackInfo info) throws Exception;
  
   /**
    * ��ȡһ���澯�����и���
    * @param alarmId
    * @return
    * @throws Exception
    */
   public List<TrackInfo> getTracks(String alarmId) throws Exception;
   /**
   *��ȡ���ٸ澯�б�
   *
   */
   public List<AlarmInfo> getTrackAlarms(int start,int limit) throws Exception;
   /**
    * ��ȡ���ٸ澯����
    * @return
    * @throws Exception
    */
   public int getTrackAlarmCount() throws Exception;
   
   /**
    * ȡ�����ٸ澯
    * @param info ���ٸ澯
    */
   public void cancelTrack(List<TrackInfo> list) throws Exception;
}

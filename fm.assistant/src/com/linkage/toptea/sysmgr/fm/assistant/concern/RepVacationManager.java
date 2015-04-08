package com.linkage.toptea.sysmgr.fm.assistant.concern;

import java.util.HashMap;
import java.util.Map;

/**
 * �ݼ���Ա�ڴ������
 * @author panhua
 *
 */
public class RepVacationManager {

	
	public static Map<String,RepVacation> RepVacations = new HashMap<String,RepVacation>();
	
	 public static  synchronized void addVacation(String userId,RepVacation rep){
		 RepVacations.put(userId, rep);
	   }
	   public static synchronized void removeVacation(String userId){
		   RepVacations.remove(userId);
	   }
	   public static synchronized RepVacation getVacation(String userId){
		  return  RepVacations.get(userId);
	   }
	   
	   //�жϵ�ǰ�û��Ƿ��ݼ�
	   public static boolean isVacation(String userId){
		   boolean isV = false;
		   RepVacation rep =  getVacation(userId);
		   if (rep == null) return false;
		   long startTime = rep.getStartTime();
		   long endTime = rep.getEndTime();
		   long nowTime = System.currentTimeMillis();
		   return (startTime<nowTime && endTime>nowTime);
	   }
}

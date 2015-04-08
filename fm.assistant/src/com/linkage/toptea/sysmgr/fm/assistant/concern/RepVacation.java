package com.linkage.toptea.sysmgr.fm.assistant.concern;

/**
 * 
 * 责任人休假
 * 
 * @author panhua
 * 
 */
public class RepVacation {
	public static int RepVacation_ALL = 1; //所有人
	public static int RepVacation_NOCHOOSE = 0; //没有被选择的
	
	private String userId; // 用户编号
	private long startTime; // 休假开始时间
	private long endTime; // 休假截至时间
	private int isUse; // 规则启用
	
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public int getIsUse() {
		return isUse;
	}
	public void setIsUse(int isUse) {
		this.isUse = isUse;
	}

}

package com.linkage.toptea.sysmgr.fm.assistant.calluser;

import java.sql.Timestamp;

/**
 * 按角色外呼记录类
 * @author huangzh
 *
 */
public class CallTempInfo {
	
	/** 外呼状态：外呼userA */
	public static final int CALL_STATE_0 = 0;
	/** 外呼状态：外呼userB */
	public static final int CALL_STATE_1 = 1;
	/** 外呼状态：外呼userC */
	public static final int CALL_STATE_2 = 2;
	/** 外呼状态：外呼失败 */
	public static final int CALL_STATE_3 = -1;
	/** 外呼状态：外呼成功 */
	public static final int CALL_STATE_4 = 99;

	// 第一外呼人
	private String userA = "";

	// 第二外呼人
	private String userB = "";

	// 第三外呼人
	private String userC = "";


	// 外呼记录信息
	private String remark = "";
	

	// 外呼ID
	private String alarmId = "";
	
	// 外呼时间
	private Timestamp callTime;	

	// 上一次返回值时间
	private Timestamp backTime;
	
	// 0：外呼userA ,    1：外呼userB,   2：外呼userC, -1:外呼失败 
	private int callState = 0;


	public Timestamp getBackTime() {
		return backTime;
	}


	public void setBackTime(Timestamp backTime) {
		this.backTime = backTime;
	}


	public Timestamp getCallTime() {
		return callTime;
	}


	public void setCallTime(Timestamp callTime) {
		this.callTime = callTime;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getUserA() {
		return userA;
	}


	public void setUserA(String userA) {
		this.userA = userA;
	}


	public String getUserB() {
		return userB;
	}


	public void setUserB(String userB) {
		this.userB = userB;
	}


	public String getUserC() {
		return userC;
	}


	public void setUserC(String userC) {
		this.userC = userC;
	}


	public int getCallState() {
		return callState;
	}


	public void setCallState(int callState) {
		this.callState = callState;
	}


	public String getAlarmId() {
		return alarmId;
	}


	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}
}

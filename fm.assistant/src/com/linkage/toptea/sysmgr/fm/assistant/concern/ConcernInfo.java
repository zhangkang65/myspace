package com.linkage.toptea.sysmgr.fm.assistant.concern;

/**
 * 
 * 功能：关注告警类
 * 作者：潘华
 * 时间：2011-02-10
 * 
 */

import com.linkage.toptea.sysmgr.fm.AlarmInfo;

public class ConcernInfo {
	
	//告警编号
	private String alarmId;
	//是否电话联系
	private int isPhone = 0;
	//电话联系人
	private String phoner;
	//联系记录时间
	private long phonetime;
	//告警内容
	private AlarmInfo alarm;
	//是否显示
	private int isshow;
    //路径名称
	private String pathname;
	//关注时间
	private long ctime;
	//关注原因
	private String memo;
	//取消关注原因
	private String reason;
	//外呼状态成功
	private int status = 0;
	//失败次数
	private int failNum = 0;
	//告警ID
	private String moId;
	
	
	

	public String getMoId() {
		return moId;
	}

	public int getFailNum() {
		return failNum;
	}

	public void setFailNum(int failNum) {
		this.failNum = failNum;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}

	public String getPathname() {
		return pathname;
	}

	public void setPathname(String pathname) {
		this.pathname = pathname;
	}

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public int getIsPhone() {
		return isPhone;
	}

	public void setIsPhone(int isPhone) {
		this.isPhone = isPhone;
	}

	public String getPhoner() {
		return phoner;
	}

	public void setPhoner(String phoner) {
		this.phoner = phoner;
	}

	public long getPhonetime() {
		return phonetime;
	}

	public void setPhonetime(long phonetime) {
		this.phonetime = phonetime;
	}

	public AlarmInfo getAlarm() {
		return alarm;
	}

	public void setAlarm(AlarmInfo alarm) {
		this.alarm = alarm;
	}

	public int getIsshow() {
		return isshow;
	}

	public void setIsshow(int isshow) {
		this.isshow = isshow;
	}

}

package com.linkage.toptea.sysmgr.fm.assistant.concern;

import java.sql.Timestamp;

import com.linkage.toptea.sysmgr.fm.AlarmInfo;

/**
 * 自动外呼告警信息类
 * 
 * @author panhua
 * 
 */
public class AutoCallAlarmInfo extends AlarmInfo implements Cloneable  {
	public AutoCallAlarmInfo() {
		super();
	}

	private String callid;// 外呼编号
	private String caller; // 自动外呼人
	private String phone; // 外呼手机号
	private int status; // 外呼状态 //0正在外呼 1外呼成功 -1失败
	private int num; // 外呼次数
	private int isconcern; // 是否进入关注 0 未关注 1 已关注
	private int isshow;// 是否显示 0不显示, 1 显示
	
	// 外呼开始时间
	private String stime;
	// 外呼结束时间
	private String etime;
	
	// 自动外呼时间
	private Timestamp callTime = null;
	
	// 自动外呼返回时间
	private Timestamp backTime = null;
	

	public String getCallid() {
		return callid;
	}

	public void setCallid(String callid) {
		this.callid = callid;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getIsconcern() {
		return isconcern;
	}

	public void setIsconcern(int isconcern) {
		this.isconcern = isconcern;
	}

	public int getIsshow() {
		return isshow;
	}

	public void setIsshow(int isshow) {
		this.isshow = isshow;
	}

	public Timestamp getCallTime() {
		return callTime;
	}

	public void setCallTime(Timestamp callTime) {
		this.callTime = callTime;
	}

	public String getEtime() {
		return etime;
	}

	public void setEtime(String etime) {
		this.etime = etime;
	}

	public String getStime() {
		return stime;
	}

	public void setStime(String stime) {
		this.stime = stime;
	}

	public  AutoCallAlarmInfo clone() {
		
		AutoCallAlarmInfo object = new AutoCallAlarmInfo();
		object.caller = this.caller;
		object.callid = this.callid;
		object.callTime = this.callTime;
		object.etime = this.etime;
		object.isconcern = this.isconcern;
		object.isshow = this.isshow;
		object.num = this.num;
		object.phone = this.phone;
		object.status = this.status;
		object.stime = this.etime;
		object.setMoId(this.getMoId());
		object.setId( this.getId() );
		return  object;
	}

	public Timestamp getBackTime() {
		return backTime;
	}

	public void setBackTime(Timestamp backTime) {
		this.backTime = backTime;
	}
}

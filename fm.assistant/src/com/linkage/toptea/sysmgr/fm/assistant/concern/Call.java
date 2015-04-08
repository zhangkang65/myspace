package com.linkage.toptea.sysmgr.fm.assistant.concern;
/**
 * 联系类
 * @author Administrator
 *
 */
public class Call {
	/** 已经外呼次数 */
    //public static int CallCount = 0; 
    /** 总的外呼次数 */
    public static int callTotal = 200;
    //获取剩下的次数
//    public static int getLastCallCount(){
//    	return Call.callTotal - Call.CallCount;
//    }
	private String alarmId;
	private String phoner;
	private String phone;
	private int status;
	private String context;
	private String msg;
	private long calltime;
		
	public String getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}
	public String getPhoner() {
		return phoner;
	}
	public void setPhoner(String phoner) {
		this.phoner = phoner;
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
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public long getCalltime() {
		return calltime;
	}
	public void setCalltime(long calltime) {
		this.calltime = calltime;
	}
}

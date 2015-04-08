package com.linkage.toptea.sysmgr.fm.assistant.web;

public class OutCall {

	/**
	 * @param args
	 */
	/* 人员 */
	private String caller;
	// 接通数
	private int sumnum;
	// 告警数
	private int alarmnum;
	private int failnum;
	private int sunnum;
	private String failra ;
	private String sunra ;
	//状态
	public int status;
	public  static String starttime;
	public static String endtime;
	public String callerorganize;
	/**
	 * @return the callerorganize
	 */
	public String getCallerorganize() {
		return callerorganize;
	}
	/**
	 * @param callerorganize the callerorganize to set
	 */
	public void setCallerorganize(String callerorganize) {
		this.callerorganize = callerorganize;
	}
	/**
	 * @return the failra
	 */
	public String getFailra() {
		return failra;
	}
	/**
	 * @param failra the failra to set
	 */
	public void setFailra(String failra) {
		this.failra = failra;
	}
	/**
	 * @return the sunra
	 */
	public String getSunra() {
		return sunra;
	}
	/**
	 * @param sunra the sunra to set
	 */
	public void setSunra(String sunra) {
		this.sunra = sunra;
	}
	/**
	 * @return the failnum
	 */
	public int getFailnum() {
		return failnum;
	}
	/**
	 * @param failnum the failnum to set
	 */
	public void setFailnum(int failnum) {
		this.failnum = failnum;
	}
	/**
	 * @return the sunnum
	 */
	public int getSunnum() {
		return sunnum;
	}
	/**
	 * @param sunnum the sunnum to set
	 */
	public void setSunnum(int sunnum) {
		this.sunnum = sunnum;
	}
	/**
	 * @return the caller
	 */
	public String getCaller() {
		return caller;
	}
	/**
	 * @param caller the caller to set
	 */
	public void setCaller(String caller) {
		this.caller = caller;
	}
	/**
	 * @return the sumnum
	 */
	public int getSumnum() {
		return sumnum;
	}
	/**
	 * @param sumnum the sumnum to set
	 */
	public void setSumnum(int sumnum) {
		this.sumnum = sumnum;
	}
	/**
	 * @return the alarmnum
	 */
	public int getAlarmnum() {
		return alarmnum;
	}
	/**
	 * @param alarmnum the alarmnum to set
	 */
	public void setAlarmnum(int alarmnum) {
		this.alarmnum = alarmnum;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the starttime
	 */
	public static String getStarttime() {
		return starttime;
	}
	/**
	 * @param starttime the starttime to set
	 */
	public static void setStarttime(String starttime) {
		OutCall.starttime = starttime;
	}
	/**
	 * @return the endtime
	 */
	public static String getEndtime() {
		return endtime;
	}
	/**
	 * @param endtime the endtime to set
	 */
	public static void setEndtime(String endtime) {
		OutCall.endtime = endtime;
	}
	
	

}

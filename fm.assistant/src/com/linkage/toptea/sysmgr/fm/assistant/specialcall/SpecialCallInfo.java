package com.linkage.toptea.sysmgr.fm.assistant.specialcall;

import java.sql.Timestamp;

public class SpecialCallInfo {

	private String moId = "";
	private String userId = "";
	private int isch = 1;
	private Timestamp time;
	public int getIsch() {
		return isch;
	}
	public void setIsch(int isch) {
		this.isch = isch;
	}
	public String getMoId() {
		return moId;
	}
	public void setMoId(String moId) {
		this.moId = moId;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}

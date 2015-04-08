package com.linkage.toptea.sysmgr.fm.assistant.track;
/**
 * 功能：跟踪类
 * 作者：潘华
 * 时间：20110214
 */
public class TrackInfo {
	
	//告警编号
	private String alarmId;
	//跟踪编号
	private String id;
	//跟踪人
	private String tracker;
	//跟踪记录
	private String content;
	//跟踪时间
	private long trackdate;
	//跟踪类型:1、正常 2、延迟
	private int type = 1;

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTracker() {
		return tracker;
	}

	public void setTracker(String tracker) {
		this.tracker = tracker;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTrackdate() {
		return trackdate;
	}

	public void setTrackdate(long trackdate) {
		this.trackdate = trackdate;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}

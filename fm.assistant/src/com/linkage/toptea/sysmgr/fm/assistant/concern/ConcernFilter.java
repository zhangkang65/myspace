package com.linkage.toptea.sysmgr.fm.assistant.concern;
/**
 * 功能：关注告警过滤器
 * 作者：潘华
 * 时间：20110211
 */


public class ConcernFilter {
	
	//告警编号
	private String alarmId;

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}
	//是否显示
	private int isshow = 1;

	public int getIsshow() {
		return isshow;
	}

	public void setIsshow(int isshow) {
		this.isshow = isshow;
	}
	//路径
	private String path="" ;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	//级别
	private int grade = 0;

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}
	//内容
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
    //开始时间
	private String starttime;
	//截止时间
	private String endtime;

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
}

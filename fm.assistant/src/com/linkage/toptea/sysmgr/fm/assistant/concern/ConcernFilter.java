package com.linkage.toptea.sysmgr.fm.assistant.concern;
/**
 * ���ܣ���ע�澯������
 * ���ߣ��˻�
 * ʱ�䣺20110211
 */


public class ConcernFilter {
	
	//�澯���
	private String alarmId;

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}
	//�Ƿ���ʾ
	private int isshow = 1;

	public int getIsshow() {
		return isshow;
	}

	public void setIsshow(int isshow) {
		this.isshow = isshow;
	}
	//·��
	private String path="" ;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	//����
	private int grade = 0;

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}
	//����
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
    //��ʼʱ��
	private String starttime;
	//��ֹʱ��
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

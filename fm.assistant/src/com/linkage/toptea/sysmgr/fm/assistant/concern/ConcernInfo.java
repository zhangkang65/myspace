package com.linkage.toptea.sysmgr.fm.assistant.concern;

/**
 * 
 * ���ܣ���ע�澯��
 * ���ߣ��˻�
 * ʱ�䣺2011-02-10
 * 
 */

import com.linkage.toptea.sysmgr.fm.AlarmInfo;

public class ConcernInfo {
	
	//�澯���
	private String alarmId;
	//�Ƿ�绰��ϵ
	private int isPhone = 0;
	//�绰��ϵ��
	private String phoner;
	//��ϵ��¼ʱ��
	private long phonetime;
	//�澯����
	private AlarmInfo alarm;
	//�Ƿ���ʾ
	private int isshow;
    //·������
	private String pathname;
	//��עʱ��
	private long ctime;
	//��עԭ��
	private String memo;
	//ȡ����עԭ��
	private String reason;
	//���״̬�ɹ�
	private int status = 0;
	//ʧ�ܴ���
	private int failNum = 0;
	//�澯ID
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

package com.linkage.toptea.sysmgr.fm.assistant.concern;

import java.sql.Timestamp;

import com.linkage.toptea.sysmgr.fm.AlarmInfo;

/**
 * �Զ�����澯��Ϣ��
 * 
 * @author panhua
 * 
 */
public class AutoCallAlarmInfo extends AlarmInfo implements Cloneable  {
	public AutoCallAlarmInfo() {
		super();
	}

	private String callid;// ������
	private String caller; // �Զ������
	private String phone; // ����ֻ���
	private int status; // ���״̬ //0������� 1����ɹ� -1ʧ��
	private int num; // �������
	private int isconcern; // �Ƿ�����ע 0 δ��ע 1 �ѹ�ע
	private int isshow;// �Ƿ���ʾ 0����ʾ, 1 ��ʾ
	
	// �����ʼʱ��
	private String stime;
	// �������ʱ��
	private String etime;
	
	// �Զ����ʱ��
	private Timestamp callTime = null;
	
	// �Զ��������ʱ��
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

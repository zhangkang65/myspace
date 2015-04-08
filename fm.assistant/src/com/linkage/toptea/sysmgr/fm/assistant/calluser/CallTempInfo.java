package com.linkage.toptea.sysmgr.fm.assistant.calluser;

import java.sql.Timestamp;

/**
 * ����ɫ�����¼��
 * @author huangzh
 *
 */
public class CallTempInfo {
	
	/** ���״̬�����userA */
	public static final int CALL_STATE_0 = 0;
	/** ���״̬�����userB */
	public static final int CALL_STATE_1 = 1;
	/** ���״̬�����userC */
	public static final int CALL_STATE_2 = 2;
	/** ���״̬�����ʧ�� */
	public static final int CALL_STATE_3 = -1;
	/** ���״̬������ɹ� */
	public static final int CALL_STATE_4 = 99;

	// ��һ�����
	private String userA = "";

	// �ڶ������
	private String userB = "";

	// ���������
	private String userC = "";


	// �����¼��Ϣ
	private String remark = "";
	

	// ���ID
	private String alarmId = "";
	
	// ���ʱ��
	private Timestamp callTime;	

	// ��һ�η���ֵʱ��
	private Timestamp backTime;
	
	// 0�����userA ,    1�����userB,   2�����userC, -1:���ʧ�� 
	private int callState = 0;


	public Timestamp getBackTime() {
		return backTime;
	}


	public void setBackTime(Timestamp backTime) {
		this.backTime = backTime;
	}


	public Timestamp getCallTime() {
		return callTime;
	}


	public void setCallTime(Timestamp callTime) {
		this.callTime = callTime;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getUserA() {
		return userA;
	}


	public void setUserA(String userA) {
		this.userA = userA;
	}


	public String getUserB() {
		return userB;
	}


	public void setUserB(String userB) {
		this.userB = userB;
	}


	public String getUserC() {
		return userC;
	}


	public void setUserC(String userC) {
		this.userC = userC;
	}


	public int getCallState() {
		return callState;
	}


	public void setCallState(int callState) {
		this.callState = callState;
	}


	public String getAlarmId() {
		return alarmId;
	}


	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}
}

package com.linkage.toptea.sysmgr.fm.assistant.calluser;

import java.sql.Timestamp;


/**
 * �����ɫ������Ϣ��
 * @author huangzh
 *
 */
public class CallUserInfo {

	// ��һ�����
	private String userA = "";
	private String AName = "";
	private String phoneA = "";

	// �ڶ������
	private String userB = "";
	private String BName = "";
	private String phoneB = "";

	// ���������
	private String userC = "";
	private String CName = "";	
	private String phoneC = "";

	// ������
	private String user = "";

	// ����ʱ��
	private Timestamp time ;

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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

	public String getAName() {
		return AName;
	}

	public void setAName(String name) {
		AName = name;
	}

	public String getBName() {
		return BName;
	}

	public void setBName(String name) {
		BName = name;
	}

	public String getCName() {
		return CName;
	}

	public void setCName(String name) {
		CName = name;
	}

	public String getPhoneA() {
		return phoneA;
	}

	public void setPhoneA(String phoneA) {
		this.phoneA = phoneA;
	}

	public String getPhoneB() {
		return phoneB;
	}

	public void setPhoneB(String phoneB) {
		this.phoneB = phoneB;
	}

	public String getPhoneC() {
		return phoneC;
	}

	public void setPhoneC(String phoneC) {
		this.phoneC = phoneC;
	}
}

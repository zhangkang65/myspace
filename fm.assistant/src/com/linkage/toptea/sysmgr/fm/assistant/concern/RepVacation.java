package com.linkage.toptea.sysmgr.fm.assistant.concern;

/**
 * 
 * �������ݼ�
 * 
 * @author panhua
 * 
 */
public class RepVacation {
	public static int RepVacation_ALL = 1; //������
	public static int RepVacation_NOCHOOSE = 0; //û�б�ѡ���
	
	private String userId; // �û����
	private long startTime; // �ݼٿ�ʼʱ��
	private long endTime; // �ݼٽ���ʱ��
	private int isUse; // ��������
	
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public int getIsUse() {
		return isUse;
	}
	public void setIsUse(int isUse) {
		this.isUse = isUse;
	}

}

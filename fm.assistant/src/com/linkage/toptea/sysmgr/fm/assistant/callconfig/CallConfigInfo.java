package com.linkage.toptea.sysmgr.fm.assistant.callconfig;

import java.sql.Timestamp;

/**
 * �Զ������������
 * @author huangzh
 *
 */
public class CallConfigInfo {
	
	//0 �� ���ֻ����� 1������֯�ܹ� 2�������Ŀ¼
	private int type = 0;
	
	// �������
	private int num = -1;

	// ���ö���ID
	private String objId= "";

	// ���ö�������
	private String objName = "";

	// ���ö�������
	private String objValue = "";

	// �޸�ʱ��
	private Timestamp time = null;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public String getObjValue() {
		return objValue;
	}

	public void setObjValue(String objValue) {
		this.objValue = objValue;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}

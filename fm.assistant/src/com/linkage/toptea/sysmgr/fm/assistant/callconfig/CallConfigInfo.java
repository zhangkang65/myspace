package com.linkage.toptea.sysmgr.fm.assistant.callconfig;

import java.sql.Timestamp;

/**
 * 自动外呼次数配置
 * @author huangzh
 *
 */
public class CallConfigInfo {
	
	//0 ： 按手机号码 1：按组织架构 2：按监控目录
	private int type = 0;
	
	// 外呼次数
	private int num = -1;

	// 配置对象ID
	private String objId= "";

	// 配置对象名称
	private String objName = "";

	// 配置对象内容
	private String objValue = "";

	// 修改时间
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

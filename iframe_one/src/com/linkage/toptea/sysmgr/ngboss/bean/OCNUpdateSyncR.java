package com.linkage.toptea.sysmgr.ngboss.bean;

public class OCNUpdateSyncR {

	private String controlSwitch;// V20 �ſؿ��� on/off
	private String rpcTimeOut;// number rpc��ʱʱ�� �磺20
	private String rpcInterfaceConnections;// number rpc������ �磺20
	private String RpcSendFailTime;// number �ط����� �磺100
	private String epPartMinute;// number ����ʱ�� �磺100
	private String epConfigSplitDuration;// number �е���ʱ���� �磺20000
	private String epTimeout;// number epԤ��Ԥ����ʱ �磺65536
	private String resInsCode;// V20 ��ԴID

	public String getControlSwitch() {
		return controlSwitch;
	}

	public void setControlSwitch(String controlSwitch) {
		this.controlSwitch = controlSwitch;
	}

	public String getRpcTimeOut() {
		return rpcTimeOut;
	}

	public void setRpcTimeOut(String rpcTimeOut) {
		this.rpcTimeOut = rpcTimeOut;
	}

	public String getRpcInterfaceConnections() {
		return rpcInterfaceConnections;
	}

	public void setRpcInterfaceConnections(String rpcInterfaceConnections) {
		this.rpcInterfaceConnections = rpcInterfaceConnections;
	}

	public String getRpcSendFailTime() {
		return RpcSendFailTime;
	}

	public void setRpcSendFailTime(String rpcSendFailTime) {
		RpcSendFailTime = rpcSendFailTime;
	}

	public String getEpPartMinute() {
		return epPartMinute;
	}

	public void setEpPartMinute(String epPartMinute) {
		this.epPartMinute = epPartMinute;
	}

	public String getEpConfigSplitDuration() {
		return epConfigSplitDuration;
	}

	public void setEpConfigSplitDuration(String epConfigSplitDuration) {
		this.epConfigSplitDuration = epConfigSplitDuration;
	}

	public String getEpTimeout() {
		return epTimeout;
	}

	public void setEpTimeout(String epTimeout) {
		this.epTimeout = epTimeout;
	}

	public String getResInsCode() {
		return resInsCode;
	}

	public void setResInsCode(String resInsCode) {
		this.resInsCode = resInsCode;
	}

	@Override
	public String toString() {
		return "OCNUpdateSyncR [controlSwitch=" + controlSwitch
				+ ", rpcTimeOut=" + rpcTimeOut + ", rpcInterfaceConnections="
				+ rpcInterfaceConnections + ", RpcSendFailTime="
				+ RpcSendFailTime + ", epPartMinute=" + epPartMinute
				+ ", epConfigSplitDuration=" + epConfigSplitDuration
				+ ", epTimeout=" + epTimeout + ", resInsCode=" + resInsCode
				+ "]";
	}

	
	
}

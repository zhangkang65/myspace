package com.linkage.toptea.sysmgr.ngboss.bean;

public class OCNUpdateSyncR {

	private String controlSwitch;// V20 信控开关 on/off
	private String rpcTimeOut;// number rpc超时时间 如：20
	private String rpcInterfaceConnections;// number rpc连接数 如：20
	private String RpcSendFailTime;// number 重发次数 如：100
	private String epPartMinute;// number 分区时间 如：100
	private String epConfigSplitDuration;// number 切单定时上限 如：20000
	private String epTimeout;// number ep预扣预锁超时 如：65536
	private String resInsCode;// V20 资源ID

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

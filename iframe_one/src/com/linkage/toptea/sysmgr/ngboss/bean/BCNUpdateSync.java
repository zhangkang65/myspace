package com.linkage.toptea.sysmgr.ngboss.bean;

public class BCNUpdateSync {

	private String indicatorIsMonitor; // boolean 指标监控 true/false
	private String batchNum;// number 批量数 如：200
	private String dmcServiceThreadpoolSize;// number dmc服务暴露的线程池大小 如：200
	private String lbPackSize;// number 分组打包大小 如：200
	private String RpcSendFailTime;// number rpc重发次数 如：5
	private String rpcTimeOut;// number rpc超时时间 如：120000
	private String rpcInterfaceConnections;// number rpc连接数 如：300
	private String workthreadNum;// number 工作线程数 如：10
	private String workthreadTimeout;// number 工作线程超时时间 如：90000000
	private String resInsCode;// V20 资源ID
	
	
	public String getIndicatorIsMonitor() {
		return indicatorIsMonitor;
	}
	public void setIndicatorIsMonitor(String indicatorIsMonitor) {
		this.indicatorIsMonitor = indicatorIsMonitor;
	}
	public String getBatchNum() {
		return batchNum;
	}
	public void setBatchNum(String batchNum) {
		this.batchNum = batchNum;
	}
	public String getDmcServiceThreadpoolSize() {
		return dmcServiceThreadpoolSize;
	}
	public void setDmcServiceThreadpoolSize(String dmcServiceThreadpoolSize) {
		this.dmcServiceThreadpoolSize = dmcServiceThreadpoolSize;
	}
	public String getLbPackSize() {
		return lbPackSize;
	}
	public void setLbPackSize(String lbPackSize) {
		this.lbPackSize = lbPackSize;
	}
	public String getRpcSendFailTime() {
		return RpcSendFailTime;
	}
	public void setRpcSendFailTime(String rpcSendFailTime) {
		RpcSendFailTime = rpcSendFailTime;
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
	public String getWorkthreadNum() {
		return workthreadNum;
	}
	public void setWorkthreadNum(String workthreadNum) {
		this.workthreadNum = workthreadNum;
	}
	public String getWorkthreadTimeout() {
		return workthreadTimeout;
	}
	public void setWorkthreadTimeout(String workthreadTimeout) {
		this.workthreadTimeout = workthreadTimeout;
	}
	public String getResInsCode() {
		return resInsCode;
	}
	public void setResInsCode(String resInsCode) {
		this.resInsCode = resInsCode;
	}
	@Override
	public String toString() {
		return "BCNUpdateSync [indicatorIsMonitor=" + indicatorIsMonitor
				+ ", batchNum=" + batchNum + ", dmcServiceThreadpoolSize="
				+ dmcServiceThreadpoolSize + ", lbPackSize=" + lbPackSize
				+ ", RpcSendFailTime=" + RpcSendFailTime + ", rpcTimeOut="
				+ rpcTimeOut + ", rpcInterfaceConnections="
				+ rpcInterfaceConnections + ", workthreadNum=" + workthreadNum
				+ ", workthreadTimeout=" + workthreadTimeout + ", resInsCode="
				+ resInsCode + "]";
	}
	
	
	
}

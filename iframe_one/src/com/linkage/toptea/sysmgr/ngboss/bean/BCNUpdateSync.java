package com.linkage.toptea.sysmgr.ngboss.bean;

public class BCNUpdateSync {

	private String indicatorIsMonitor; // boolean ָ���� true/false
	private String batchNum;// number ������ �磺200
	private String dmcServiceThreadpoolSize;// number dmc����¶���̳߳ش�С �磺200
	private String lbPackSize;// number ��������С �磺200
	private String RpcSendFailTime;// number rpc�ط����� �磺5
	private String rpcTimeOut;// number rpc��ʱʱ�� �磺120000
	private String rpcInterfaceConnections;// number rpc������ �磺300
	private String workthreadNum;// number �����߳��� �磺10
	private String workthreadTimeout;// number �����̳߳�ʱʱ�� �磺90000000
	private String resInsCode;// V20 ��ԴID
	
	
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

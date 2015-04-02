package com.linkage.toptea.sysmgr.ngboss.bean;

public class BCNUpdateSyncR {

	private String controlSwitch;
	private String batchNum;
	private String dmcServiceThreadpoolSize;
	private String dmcServicePort;
	private String dmcConsumerTimeOut;
	private String exportprocessTimeout;
	private String rpcInterfaceConnections;
	private String workthreadNum;
	private String workthreadTimeout;
	private String outputMsgCount;
	private String resInsCode;
	
	
	
	public String getControlSwitch() {
		return controlSwitch;
	}
	public void setControlSwitch(String controlSwitch) {
		this.controlSwitch = controlSwitch;
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
	public String getDmcServicePort() {
		return dmcServicePort;
	}
	public void setDmcServicePort(String dmcServicePort) {
		this.dmcServicePort = dmcServicePort;
	}
	public String getDmcConsumerTimeOut() {
		return dmcConsumerTimeOut;
	}
	public void setDmcConsumerTimeOut(String dmcConsumerTimeOut) {
		this.dmcConsumerTimeOut = dmcConsumerTimeOut;
	}
	public String getExportprocessTimeout() {
		return exportprocessTimeout;
	}
	public void setExportprocessTimeout(String exportprocessTimeout) {
		this.exportprocessTimeout = exportprocessTimeout;
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
	public String getOutputMsgCount() {
		return outputMsgCount;
	}
	public void setOutputMsgCount(String outputMsgCount) {
		this.outputMsgCount = outputMsgCount;
	}
	public String getResInsCode() {
		return resInsCode;
	}
	public void setResInsCode(String resInsCode) {
		this.resInsCode = resInsCode;
	}
	@Override
	public String toString() {
		return "BCNUpdateSyncR [controlSwitch=" + controlSwitch + ", batchNum="
				+ batchNum + ", dmcServiceThreadpoolSize="
				+ dmcServiceThreadpoolSize + ", dmcServicePort="
				+ dmcServicePort + ", dmcConsumerTimeOut=" + dmcConsumerTimeOut
				+ ", exportprocessTimeout=" + exportprocessTimeout
				+ ", rpcInterfaceConnections=" + rpcInterfaceConnections
				+ ", workthreadNum=" + workthreadNum + ", workthreadTimeout="
				+ workthreadTimeout + ", outputMsgCount=" + outputMsgCount
				+ ", resInsCode=" + resInsCode + "]";
	}
	
	
}

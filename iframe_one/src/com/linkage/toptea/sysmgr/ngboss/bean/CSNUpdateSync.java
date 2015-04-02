package com.linkage.toptea.sysmgr.ngboss.bean;

public class CSNUpdateSync {

	private  String  nodeRunState;		//	节点运行状态	online/offline
	private  String   indicatorIsMonitor;	//	bds指标监控	true/false
	private  String   providerIothreads;	//	IO线程		200
	private  String   providerThreads;		//	线程			200
	private  String   providerConnections;	//	连接数		200
	private  String   providerAccepts;		//	最大连接数	300
	private  String   resInsCode;			//	资源ID	
	

	public String getNodeRunState() {
		return nodeRunState;
	}
	public void setNodeRunState(String nodeRunState) {
		this.nodeRunState = nodeRunState;
	}
	public String getIndicatorIsMonitor() {
		return indicatorIsMonitor;
	}
	public void setIndicatorIsMonitor(String indicatorIsMonitor) {
		this.indicatorIsMonitor = indicatorIsMonitor;
	}
	public String getProviderIothreads() {
		return providerIothreads;
	}
	public void setProviderIothreads(String providerIothreads) {
		this.providerIothreads = providerIothreads;
	}
	public String getProviderThreads() {
		return providerThreads;
	}
	public void setProviderThreads(String providerThreads) {
		this.providerThreads = providerThreads;
	}
	public String getProviderConnections() {
		return providerConnections;
	}
	public void setProviderConnections(String providerConnections) {
		this.providerConnections = providerConnections;
	}
	public String getProviderAccepts() {
		return providerAccepts;
	}
	public void setProviderAccepts(String providerAccepts) {
		this.providerAccepts = providerAccepts;
	}
	public String getResInsCode() {
		return resInsCode;
	}
	public void setResInsCode(String resInsCode) {
		this.resInsCode = resInsCode;
	}
	
	
	
}

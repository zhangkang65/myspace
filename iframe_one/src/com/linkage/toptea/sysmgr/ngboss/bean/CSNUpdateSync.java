package com.linkage.toptea.sysmgr.ngboss.bean;

public class CSNUpdateSync {

	private  String  nodeRunState;		//	�ڵ�����״̬	online/offline
	private  String   indicatorIsMonitor;	//	bdsָ����	true/false
	private  String   providerIothreads;	//	IO�߳�		200
	private  String   providerThreads;		//	�߳�			200
	private  String   providerConnections;	//	������		200
	private  String   providerAccepts;		//	���������	300
	private  String   resInsCode;			//	��ԴID	
	

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

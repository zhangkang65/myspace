package com.linkage.toptea.sysmgr.ngboss.bean;

public class OCNUpdateSync {                        
	
	private String	indicatorIsMonitor;              //		boolean		ָ����	true/false
	private String nettyWorkprocessNum;              //		number		work�߳���	�磺20
	private String nettyBossprocessNum;              //		number		boss�����߳���	�磺20
	private String nettyBizThreadSize;               //		number		ҵ���̳߳ش�С	�磺100
	private String nettyBizThreadQueueSize;          //		number		ҵ���̳߳ػ�����д�С	�磺100
	private String nettyBizThreadRetryInterval;      //		number		ҵ���̳߳������ύʧ�����Լ��ʱ��	�磺20000
	private String nettyRcvbuf;                      //		number		  netty���ջ�������С	�磺65536
	private String nettySndbuf;                      //		number		netty���ͻ�������С	�磺65536
	private String resInsCode;                       //		1	V20		��ԴID	
	                                               
	                                                
	                                               
	
	public String getIndicatorIsMonitor() {
		return indicatorIsMonitor;
	}
	public void setIndicatorIsMonitor(String indicatorIsMonitor) {
		this.indicatorIsMonitor = indicatorIsMonitor;
	}
	public String getNettyWorkprocessNum() {
		return nettyWorkprocessNum;
	}
	public void setNettyWorkprocessNum(String nettyWorkprocessNum) {
		this.nettyWorkprocessNum = nettyWorkprocessNum;
	}
	public String getNettyBossprocessNum() {
		return nettyBossprocessNum;
	}
	public void setNettyBossprocessNum(String nettyBossprocessNum) {
		this.nettyBossprocessNum = nettyBossprocessNum;
	}
	public String getNettyBizThreadSize() {
		return nettyBizThreadSize;
	}
	public void setNettyBizThreadSize(String nettyBizThreadSize) {
		this.nettyBizThreadSize = nettyBizThreadSize;
	}
	public String getNettyBizThreadQueueSize() {
		return nettyBizThreadQueueSize;
	}
	public void setNettyBizThreadQueueSize(String nettyBizThreadQueueSize) {
		this.nettyBizThreadQueueSize = nettyBizThreadQueueSize;
	}
	public String getNettyBizThreadRetryInterval() {
		return nettyBizThreadRetryInterval;
	}
	public void setNettyBizThreadRetryInterval(String nettyBizThreadRetryInterval) {
		this.nettyBizThreadRetryInterval = nettyBizThreadRetryInterval;
	}
	public String getNettyRcvbuf() {
		return nettyRcvbuf;
	}
	public void setNettyRcvbuf(String nettyRcvbuf) {
		this.nettyRcvbuf = nettyRcvbuf;
	}
	public String getNettySndbuf() {
		return nettySndbuf;
	}
	public void setNettySndbuf(String nettySndbuf) {
		this.nettySndbuf = nettySndbuf;
	}
	public String getResInsCode() {
		return resInsCode;
	}
	public void setResInsCode(String resInsCode) {
		this.resInsCode = resInsCode;
	}
	
	
	
}

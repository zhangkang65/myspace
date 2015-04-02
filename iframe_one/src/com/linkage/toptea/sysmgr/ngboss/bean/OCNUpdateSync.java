package com.linkage.toptea.sysmgr.ngboss.bean;

public class OCNUpdateSync {                        
	
	private String	indicatorIsMonitor;              //		boolean		指标监控	true/false
	private String nettyWorkprocessNum;              //		number		work线程数	如：20
	private String nettyBossprocessNum;              //		number		boss工作线程数	如：20
	private String nettyBizThreadSize;               //		number		业务线程池大小	如：100
	private String nettyBizThreadQueueSize;          //		number		业务线程池缓冲队列大小	如：100
	private String nettyBizThreadRetryInterval;      //		number		业务线程池任务提交失败重试间隔时间	如：20000
	private String nettyRcvbuf;                      //		number		  netty接收缓冲区大小	如：65536
	private String nettySndbuf;                      //		number		netty发送缓冲区大小	如：65536
	private String resInsCode;                       //		1	V20		资源ID	
	                                               
	                                                
	                                               
	
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

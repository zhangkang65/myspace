package com.linkage.toptea.sysmgr.fm.assistant.concern;
/**
 * 参数配置内存管理
 * @author panhua
 *
 */
public class PhoneConfigManager {

	/** 外呼几次失败进入关注告警 */
	public static int FAIL_NUM = 3;

	/** 一分钟产生多少告警进入关注告警 */
	public static int ALARM_NUM = 30;

	/** 设置几级告警进行外呼 */
	public static String ALARM_GRADE = ">=3";
	
	/** 外呼时间与当前时间间隔时常(min)进入关注告警 */
	public static int TIME = 40;
	
	/** 外呼执行周期，单位：分钟 */
	public static int TIME_RUN = 1;	
	
	/** 外呼次数  */
	public static int CALL_TIME = 1;
	
	/** 外呼时间间隔，单位：分钟 */
	public static int CALL_TIME_INTERVEL[] = {1,0,0,0,0};

}

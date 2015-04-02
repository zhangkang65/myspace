package com.linkage.toptea.sysmgr.ngboss.utils;

public class NgStrValidate {

	
	/**
	 * 如果对象为空，则给对象复制空字符串 
	 * @param obj
	 * @author zhangkk
	 */
	public static String  nullToStr(String strObj){
		return strObj==null?"":strObj;
	}
	
	/**
	 * 判断对象是否为空 
	 * @param strObj
	 * @return
	 */
	public static  boolean  isnull(String  strObj){
		return (strObj==null||strObj.equals(""));
	}
	
	
	
}

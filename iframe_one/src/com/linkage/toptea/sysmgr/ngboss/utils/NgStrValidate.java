package com.linkage.toptea.sysmgr.ngboss.utils;

public class NgStrValidate {

	
	/**
	 * �������Ϊ�գ���������ƿ��ַ��� 
	 * @param obj
	 * @author zhangkk
	 */
	public static String  nullToStr(String strObj){
		return strObj==null?"":strObj;
	}
	
	/**
	 * �ж϶����Ƿ�Ϊ�� 
	 * @param strObj
	 * @return
	 */
	public static  boolean  isnull(String  strObj){
		return (strObj==null||strObj.equals(""));
	}
	
	
	
}

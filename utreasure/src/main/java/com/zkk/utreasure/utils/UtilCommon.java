package com.zkk.utreasure.utils;


import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UtilCommon {
	private static final Log logger = LogFactory.getLog(UtilCommon.class);

	public static String md5Encryption(String plainText) {
		StringBuffer buf = new StringBuffer("");
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return buf.toString());// 32位的加密
		return buf.toString().substring(8, 24);// 16位的加密
	}

	/**
	 * 判断字符串是否是整数
	 * 
	 * @author 汤太�?
	 * @since Aug 2, 2013
	 * @param value
	 * @return
	 */
	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 判断字符串是否是浮点�?
	 * 
	 * @author 汤太�?
	 * @since Aug 2, 2013
	 * @param value
	 * @return
	 */
	public static boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
			if (value.contains("."))
				return true;
			return false;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 判断字符串是否是数字
	 * 
	 * @author 汤太�?
	 * @since Aug 2, 2013
	 * @param value
	 * @return
	 */
	public static boolean isNumber(String value) {
		return isInteger(value) || isDouble(value);
	}

	/**
	 * 判断是否为空
	 * 
	 * @author 汤太�?
	 * @since Jul 22, 2013
	 * @param o
	 * @return
	 */
	public static boolean isEmptyOrNull(Object o) {
		if (null == o) {
			return true;
		} else if ("".equals(o.toString().trim())
				|| "null".equalsIgnoreCase(o.toString())) {
			return true;
		} else if (o.getClass().isArray() && Array.getLength(o) == 0) {
			return true;
		} else if (o instanceof Collection<?>
				&& ((Collection<?>) o).size() == 0) {
			return true;
		} else if (o instanceof Map<?, ?> && ((Map<?, ?>) o).isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断集合的每个元素是不是空的(Array，Collection，Map)
	 * @param o
	 * @return
	 * @author 汤太�?
	 * @since 2014-1-25 下午10:35:13
	 */
	public static boolean isEleOfCollectionAllEmpty(Object o) {
		if(o.getClass().isArray()) {
			if(Array.getLength(o) == 0) {
				return true;
			}
			boolean flag = true;
			Object[] array = (Object[])o;
			for(Object elem:array) {
				flag&=isEmptyOrNull(elem);
			}
			return flag;
		}
		if(o instanceof Collection<?>) {
			Collection<?> collection = (Collection<?>) o;
			if(collection.size() == 0) {
				return true;
			}
			boolean flag = true;
			for(Object elem:collection ) {
				flag&=isEmptyOrNull(elem);
			}
			return flag;
		}
		if(o instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) o;
			if(map.isEmpty()||map.keySet().isEmpty()) {
				return true;
			}
			boolean flag = true;
			Set<?> key = map.keySet();
	        for (Iterator<?> it = key.iterator(); it.hasNext();) {
	            Object elem = (Object) map.get(it.next());
	            flag&=isEmptyOrNull(elem);
	        }
			return flag;
		}
		return false;
	}
	

	/**
	 * 判断是否为空或为零或为false
	 * 
	 * @author 汤太�?
	 * @since Jul 22, 2013
	 * @param o
	 * @return
	 */
	public static boolean isEmptyOrNullOr0OrFalse(Object o) {
		if (isEmptyOrNull(o)) {
			return true;
		} else if (o.toString().equals("0")) {
			return true;
		} else if (o.toString().equals("false")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNotEmptyOrNull(Object o) {
		return !isEmptyOrNull(o);
	}

	public static boolean isNotEmptyOrNullOr0OrFalse(Object o) {
		return !isEmptyOrNullOr0OrFalse(o);
	}
	
	public static boolean isEleOfCollectionNotAllEmpty(Object o) {
		return !isEleOfCollectionAllEmpty(o);
	}

	/**
	 * 通过set来打乱list的顺序，不过会会导致重复数据丢失
	 * 
	 * @author 汤太�?
	 * @since Sep 2, 2013
	 * @param list
	 * @return
	 */
	public static List<Object> outOrderListBySet(List<Object> list) {
		Set<Object> set = new HashSet<Object>();
		set.addAll(list);
		list.clear();
		list.addAll(set);
		return list;
	}

	/**
	 * 通过map来打乱list的顺序，不过会会导致重复数据丢失
	 * 
	 * @author 汤太�?
	 * @since Sep 2, 2013
	 * @param list
	 * @return
	 */
	public static List<Object> outOrderListByMap(List<Object> list) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listMapNew = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("obj", list.get(i));
			map.put("isInOrderList", "false");
			listMap.add(map);
		}

		Random r = new Random();
		while (listMap.size() > 0) {
			int index = r.nextInt(listMap.size());
			Map<String, Object> tempMap = listMap.get(index);
			if (tempMap.get("isInOrderList").equals("false")) {
				tempMap.put("isInOrderList", "true");
				listMapNew.add(tempMap);
				listMap.remove(index);
			}
		}

		List<Object> listNew = new ArrayList<Object>();
		for (int i = 0; i < listMapNew.size(); i++) {
			listNew.add(listMapNew.get(i).get("obj"));
		}

		return listNew;

	}

	/**
	 * 验证邮箱地址是否正确
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email.trim());
			flag = matcher.matches();
		} catch (Exception e) {
			logger.error("验证邮箱地址错误", e);
			flag = false;
		}

		return flag;
	}

	/**
	 * 验证手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		boolean flag = false;
		try {
			Pattern p = Pattern
					.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(mobiles.trim());
			flag = m.matches();
		} catch (Exception e) {
			logger.error("验证手机号码错误", e);
			flag = false;
		}
		return flag;
	}

	/**
	 * �?��输入的数据中是否有特殊字�?
	 * 
	 * @param qString
	 *            要检查的数据
	 * @param regx
	 *            特殊字符正则表达�?
	 * @return boolean 如果包含正则表达�?<code> regx </code> 中定义的特殊字符，返回true�?否则返回false
	 */
	public static boolean hasCrossScriptRisk(String qString, String regx) {
		if (isEmptyOrNull(regx)) {
			regx = "[\\W&&[^?=&-.:.+.!#@/]&&[^\\u4e00-\\u9fa5]]";//非数字字母或者非&?#或�?非汉�?
//			regx = "\\d*\\w*[-]*[?]*[\\u4e00-\\u9fa5]*";
		}
		if (qString != null) {
			qString = qString.trim();
			Pattern p = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(qString);
			return m.find();
		}
		return false;
	}
	
	/**
	 * 判断map里面的元素有没有空�?
	 * @author 汤太�?
	 * @since Jan 4, 2014
	 * @param map
	 * @return
	 */
	public static boolean containsEmptyValue(Map<?,?> map) {
		return (map.containsValue(null) || map.containsValue(""));
	}
	
	/**
	 * 判断map里面的元素有没有空�?�?
	 * @author 汤太�?
	 * @since Jan 4, 2014
	 * @param map
	 * @return
	 */
	public static boolean containsEmptyOr0Value(Map<?,?> map) {
		return (containsEmptyValue(map) || map.containsValue(0));
	}
	
	/**
	 * 判断map里面�?��元素都不为空�?
	 * @author 汤太�?
	 * @since Jan 4, 2014
	 * @param map
	 * @return
	 */
	public static boolean notContainsEmptyValue(Map<?,?> map) {
		return !containsEmptyValue(map);
	}
	
	/**
	 * 判断map里面�?��元素都不为空值或0
	 * @author 汤太�?
	 * @since Jan 4, 2014
	 * @param map
	 * @return
	 */
	public static boolean notContainsEmptyOr0Value(Map<?,?> map) {
		return !containsEmptyOr0Value(map);
	}

	public static void main(String agrs[]) {
//		System.out.println(hasCrossScriptRisk("14?s1",null));
//		System.out.println(hasCrossScriptRisk("14&s1",null));
//		System.out.println(hasCrossScriptRisk("14#s1",null));
//		System.out.println(hasCrossScriptRisk("14,s1",null));
//		System.out.println(hasCrossScriptRisk("14汗s1",null));
//		System.out.println(hasCrossScriptRisk("",null));
//		System.out.println(hasCrossScriptRisk("/",null));
		
		String[] strs = {"",""};
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("s", "");
		map.put("d", "");
		List<String> list = new ArrayList<String>();
		list.add("");
		list.add("");
//		System.out.println(isEleOfCollectionAllEmpty(strs));
		System.out.println(isEleOfCollectionAllEmpty(map));
//		System.out.println(isEleOfCollectionAllEmpty(list));
	}
}

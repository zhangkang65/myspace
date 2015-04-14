package com.zkk.utreasure.service.basic;

import java.util.List;
import java.util.Map;

public interface BaseServiceI<T> {
	/**
	 * 查询单个
	 * 
	 */
	T get(Long id);

	/**
	 * 根据条件查找列表的个数
	 * 
	 */
	int findListCount(Map<String, Object> params);

	/**
	 * 根据条件查找列表
	 * 
	 */
	List<T> findList(Map<String, Object> params);

	/**
	 * 添加
	 */
	int add(T t);

	/**
	 * 修改
	
	 */
	void modify(T t);
	
	/**
	 * 删除
	 */
	void remove(Long id);
	
	
	

}

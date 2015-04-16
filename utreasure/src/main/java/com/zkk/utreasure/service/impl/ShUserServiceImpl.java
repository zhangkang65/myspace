package com.zkk.utreasure.service.impl;



import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zkk.utreasure.dao.ShUserMapper;
import com.zkk.utreasure.entity.ShUser;
import com.zkk.utreasure.service.ShUserServiceI;
import com.zkk.utreasure.service.basic.BaseServiceImpl;

@Service
public class ShUserServiceImpl extends  BaseServiceImpl<ShUser> implements ShUserServiceI {
	
	@Autowired
	private ShUserMapper shUserMapper;

	

	public int RegisterUser(ShUser shUser) {
		return shUserMapper.insertSelective(shUser);
	}



	public ShUser validateRepeatName(String fieldName,String fieldValue) {
		//ʹ��map��װ�����ֵ
		Map   paramMap =new HashMap<String,String>();
		paramMap.put("fieldName", fieldName);
		paramMap.put("fieldValue", fieldValue);
		
		//�����ֶ������Զ���ȡ����
		return shUserMapper.getByName(paramMap);
	}

	


}

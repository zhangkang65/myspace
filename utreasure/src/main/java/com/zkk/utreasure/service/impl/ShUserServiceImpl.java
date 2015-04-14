package com.zkk.utreasure.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zkk.utreasure.dao.ShUserMapper;
import com.zkk.utreasure.service.ShUserServiceI;

@Service
public class ShUserServiceImpl implements ShUserServiceI {
	
	@Autowired
	private ShUserMapper shUserMapper;

	public void addUser(String str, String str2) {
		System.out.println(str+"----"+str2);
		shUserMapper.insertUser(str,str2);
	}

}

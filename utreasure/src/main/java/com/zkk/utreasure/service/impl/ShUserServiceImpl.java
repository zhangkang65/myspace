package com.zkk.utreasure.service.impl;



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

	

	public void RegisterUser(ShUser shUser) {
		shUserMapper.insertSelective(shUser);
	}

	


}

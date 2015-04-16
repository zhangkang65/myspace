package com.zkk.utreasure.service;

import com.zkk.utreasure.entity.ShUser;



public interface ShUserServiceI {

	int RegisterUser(ShUser shUser);

	ShUser validateRepeatName(String loginName, String loginName2);
	
}

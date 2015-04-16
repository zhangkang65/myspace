package com.zkk.utreasure.service;

import javax.servlet.http.HttpSession;

import com.zkk.utreasure.entity.ShUser;



public interface ShUserServiceI {

	int RegisterUser(ShUser shUser);

	ShUser validateRepeatName(String loginName, String loginName2);

	boolean sendEmail(String email, HttpSession session);
	
}

package com.zkk.utreasure.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.zkk.utreasure.dto.RegisterUser;
import com.zkk.utreasure.entity.ShUser;
import com.zkk.utreasure.service.ShUserServiceI;
import com.zkk.utreasure.utils.UtilCommon;

@Controller
@RequestMapping("/user")
public class ShUserController {
	
	
	private Logger log = Logger.getLogger(ShUserController.class); 
	
	@Autowired
	private  ShUserServiceI  shUserServicei;

	/**
	 * 跳转到注册界面
	 * @return
	 */
	@RequestMapping(value="/toRegister")
	public ModelAndView toRegister(){
		return new ModelAndView("register");
	}
	
	/**
	 * 注册界面
	 * @return
	 */
	@RequestMapping(value="/doRegister")
	public ModelAndView   doRegister(RegisterUser  registerUser,HttpSession  session){
		
		ShUser  shUser=new ShUser();

		//密码加密 
		//BeanUtils.copyProperties(registerUser, shUser);
		shUser.setEmail(registerUser.getEmail());
		shUser.setLoginName(registerUser.getLoginName());
		shUser.setPassword(UtilCommon.md5Encryption(registerUser.getPassword()));
		shUser.setCreateTime(new Date(System.currentTimeMillis()));
		shUser.setUpdateTime(new Date(System.currentTimeMillis()));
		//
		int count=shUserServicei.RegisterUser(shUser);
		
		//获取用户信息
		//System.out.println("the count is:"+count+",getid is:"+shUser.getId());
		
		
		//跳转到登陆页面 
		return new ModelAndView("mainPage");
	}
	
	@RequestMapping(value="/getUserName")
	public ModelAndView getUserName(HttpServletRequest  request) {
		
		return new ModelAndView();
	}
	
}

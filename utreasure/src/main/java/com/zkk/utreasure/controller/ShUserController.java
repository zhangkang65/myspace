package com.zkk.utreasure.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.zkk.utreasure.entity.ShUser;
import com.zkk.utreasure.service.ShUserServiceI;

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
	public ModelAndView   doRegister(ShUser  shUser){
		return null;
	}
	
	
	
	
}

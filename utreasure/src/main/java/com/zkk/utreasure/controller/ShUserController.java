package com.zkk.utreasure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zkk.utreasure.service.ShUserServiceI;

@Controller
@RequestMapping("/user")
public class ShUserController {
	
	@Autowired
	private  ShUserServiceI  shUserServicei;

	@RequestMapping(value="/getInfo")
	public void getUserInfo(){
		System.out.println("-------getinfo---------");
		//±£¥Ê ˝æ›≤‚ ‘
		shUserServicei.addUser("11","zkk");
	}
	
}

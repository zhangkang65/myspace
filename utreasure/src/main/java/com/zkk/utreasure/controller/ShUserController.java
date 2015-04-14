package com.zkk.utreasure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class ShUserController {

	@RequestMapping(value="/getInfo")
	public void getUserInfo(){
		System.out.println("-------getinfo---------");
		
		//±£¥Ê ˝æ›≤‚ ‘
		
	}
	
}

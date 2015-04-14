package com.zkk.utreasure.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.zkk.utreasure.service.ShUserServiceI;

@Controller
@RequestMapping("/user")
public class ShUserController {
	
	
	 private Logger log = Logger.getLogger(ShUserController.class); 
	
	@Autowired
	private  ShUserServiceI  shUserServicei;


	@RequestMapping(value="/getAllInfo")
	public ModelAndView getUserInfo(){
		//±£¥Ê ˝æ›≤‚ ‘
		//shUserServicei.addUser("11","zkk");
		
		return new ModelAndView();
	}
	
}

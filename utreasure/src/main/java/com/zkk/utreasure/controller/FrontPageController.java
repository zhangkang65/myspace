package com.zkk.utreasure.controller;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FrontPageController {
	
	@RequestMapping(value="/toMainPage")
	public ModelAndView  toMainPage(HttpServletRequest  request){
		//参数是否正常 
	
		return new ModelAndView("mainPage");
	}

}

package com.zkk.utreasure.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.zkk.utreasure.common.Constant;
import com.zkk.utreasure.dto.LoginFrom;
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
	 * @since  2015-4-16 15:44:25
	 */
	@RequestMapping(value="/toRegister")
	public ModelAndView toRegister(){
		return new ModelAndView("register");
	}
	
	
	
	
	
	
	/**
	 * 注册界面
	 * @return
	 * @since  2015-4-16 15:44:09
	 */
	@RequestMapping(value="/doRegister")
	public ModelAndView   doRegister(RegisterUser  registerUser,HttpSession  session){
		//创建对象  接受参数
		ShUser  shUser=new ShUser();
		//密码加密 
		//BeanUtils.copyProperties(registerUser, shUser);
		shUser.setEmail(registerUser.getEmail());
		shUser.setLoginName(registerUser.getLoginName());
		shUser.setPassword(UtilCommon.md5Encryption(registerUser.getPassword()));
		shUser.setCreateTime(new Date(System.currentTimeMillis()));
		shUser.setUpdateTime(new Date(System.currentTimeMillis()));
	
		//异常如违反了主键唯一性条件等
		try {
			int count=shUserServicei.RegisterUser(shUser);
			if(count!=0){ //注册成功 发送邮件 
				//SendEmailThread
				Thread  thread=new Thread(new SendEmailThread("zhangkang65@qq.com",session));
				thread.start();	
			}
		} catch (Exception e) {
			log.error("doRegister",e);
		}
		
		//获取用户信息
		//System.out.println("the count is:"+count+",getid is:"+shUser.getId());
		//跳转到登陆页面 
		return new ModelAndView("mainPage");
	}
	
	
	
	/**
	 * 内部类发送邮件的线程 
	 * @author Administrator
	 *
	 */
	class  SendEmailThread  implements  Runnable{
		private  String  email;
		private  HttpSession  session;
		//无参构造
		public SendEmailThread() {}

		public SendEmailThread(String email, HttpSession session) {
			this.email=email;
			this.session=session;
		}

		public void run() {
			boolean  isSend=shUserServicei.sendEmail(email,session);
		}
		
	}
	
	/**
	 * 获取用户名是否存在
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value="/getUserName")
	public void getUserName(HttpServletRequest  request,HttpServletResponse  response) throws IOException {
		boolean  flag=false;
		PrintWriter  writer=response.getWriter();
		String loginName=request.getParameter("loginName");
		ShUser  shUser=shUserServicei.validateRepeatName("loginName",loginName);
		//对象输出
		if(shUser==null) flag=true;
		else  flag=false;	
		writer.println(flag);	
	}
	
	
	/**
	 * 跳转到登陆界面 
	 * @return
	 * @since  2015-4-20 09:59:44
	 */
	@RequestMapping(value="/toLogin")
	public ModelAndView toLogin(){
		return new ModelAndView("login");
	}
	
	
	
	/**
	 * 登陆模块
	 * @return
	 * @since  2015-4-20 10:35:51
	 */
	@RequestMapping(value="/login")
	public  ModelAndView userLogin(HttpSession session,@ModelAttribute("loginForm") LoginFrom loginForm){
		
		Map<String, Object> modle = new HashMap<String, Object>();
		ShUser  shUser=(ShUser) session.getAttribute(Constant.USER);
		//如果session 中存在此用户
		if(!UtilCommon.isEmptyOrNull(shUser)){
			//跳转到主页
			return new ModelAndView("redirect:/toMainPage.do");
		}
		//如果session中不存在此用户
		modle.put("loginkey", loginForm.getLoginKey());
		modle.put("pwd", loginForm.getPwd());
		
		//如果账户为空
		if(UtilCommon.isEmptyOrNull(modle.get("loginkey"))){
			return new ModelAndView("login",modle);
		}
		//
		shUser=shUserServicei.login(loginForm);
		log.info(shUser.toString());
		session.setAttribute(Constant.USER, shUser);
		//
		return new ModelAndView("redirect:/toMainPage.do");
	}
	
	
}

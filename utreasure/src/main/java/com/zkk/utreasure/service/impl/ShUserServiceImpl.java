package com.zkk.utreasure.service.impl;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.zkk.utreasure.dao.ShUserMapper;
import com.zkk.utreasure.dto.LoginFrom;
import com.zkk.utreasure.entity.ShUser;
import com.zkk.utreasure.mail.MailSenderInfo;
import com.zkk.utreasure.mail.SimpleMailSender;
import com.zkk.utreasure.service.ShUserServiceI;
import com.zkk.utreasure.service.basic.BaseServiceImpl;
import com.zkk.utreasure.utils.UtilCommon;

@Service
public class ShUserServiceImpl extends  BaseServiceImpl<ShUser> implements ShUserServiceI {
	
	@Autowired
	private ShUserMapper shUserMapper;

	

	public int RegisterUser(ShUser shUser) {
		return shUserMapper.insertSelective(shUser);
	}



	public ShUser validateRepeatName(String fieldName,String fieldValue) {
		//使用map封装对象的值
		Map   paramMap =new HashMap<String,String>();
		paramMap.put("fieldName", fieldName);
		paramMap.put("fieldValue", fieldValue);
		
		//根据字段名，自动获取对象
		return shUserMapper.getByName(paramMap);
	}


	/**
	 * 发送邮件
	 */
	public boolean sendEmail(String email, HttpSession session) {

		Map<String, Object> model = new HashMap<String, Object>();
		session.setAttribute("email", email);
		if(false){
			model.put("message", "邮箱不存在");
		}
		else {
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.126.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("zhangkang65@126.com");
		mailInfo.setPassword("zkk6167489");// 您的邮箱密码
		mailInfo.setFromAddress("zhangkang65@126.com");
		mailInfo.setToAddress(email);
		mailInfo.setSubject("show treasure");
		mailInfo.setContent("欢迎注册您的用户名是：888888，密码是：888888");
		// 这个类主要来发送邮件
		SimpleMailSender sms = new SimpleMailSender();
		sms.sendHtmlMail(mailInfo);// 发送html格式
		model.put("email", email);
		}
		return true;
	}

		

		/**
		 * 登陆
		 */
		public ShUser login(LoginFrom loginForm) {
		String	loginName="";
		String	email="";
		String	telePhone="";
		String  password=UtilCommon.md5Encryption(loginForm.getPwd());   
		
		//
		Map<String,Object> params = new HashMap<String,Object>();
		if(UtilCommon.isNotEmptyOrNull(password)){
			params.put("password", password);
			if(UtilCommon.isEmail(loginForm.getLoginKey())){
				email=loginForm.getLoginKey();
				params.put("email", email);
			}else if(UtilCommon.isMobileNO(loginForm.getLoginKey())){
				telePhone=loginForm.getLoginKey();
				params.put("telePhone", telePhone);
			}
			//	
			params.put("loginName", loginForm.getLoginKey());
			//
			
		}else{
			return  null;
 		}
		
		List<ShUser>  shUsers=shUserMapper.findUser(params);
		if(!UtilCommon.isEmptyOrNull(shUsers)){
			return shUsers.get(0);
		}
			
			return null;
		}



}



	
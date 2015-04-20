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
		//ʹ��map��װ�����ֵ
		Map   paramMap =new HashMap<String,String>();
		paramMap.put("fieldName", fieldName);
		paramMap.put("fieldValue", fieldValue);
		
		//�����ֶ������Զ���ȡ����
		return shUserMapper.getByName(paramMap);
	}


	/**
	 * �����ʼ�
	 */
	public boolean sendEmail(String email, HttpSession session) {

		Map<String, Object> model = new HashMap<String, Object>();
		session.setAttribute("email", email);
		if(false){
			model.put("message", "���䲻����");
		}
		else {
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.126.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("zhangkang65@126.com");
		mailInfo.setPassword("zkk6167489");// ������������
		mailInfo.setFromAddress("zhangkang65@126.com");
		mailInfo.setToAddress(email);
		mailInfo.setSubject("show treasure");
		mailInfo.setContent("��ӭע�������û����ǣ�888888�������ǣ�888888");
		// �������Ҫ�������ʼ�
		SimpleMailSender sms = new SimpleMailSender();
		sms.sendHtmlMail(mailInfo);// ����html��ʽ
		model.put("email", email);
		}
		return true;
	}

		

		/**
		 * ��½
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



	
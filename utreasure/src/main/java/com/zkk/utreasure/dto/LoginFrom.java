package com.zkk.utreasure.dto;

public class LoginFrom {

	private String loginKey;
	private String pwd;
	private String validateCode;
	private Long id;
	private String ip;
	
	public String getLoginKey() {
		return loginKey;
	}

	public void setLoginKey(String loginKey) {
		this.loginKey = loginKey;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}

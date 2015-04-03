package com.linkage.toptea.sysmgr.config.filter;

import java.util.List;

public class Security {
	
	private String openFlag;
	private String formCheck;
	public String getFormCheck() {
		return formCheck;
	}
	public void setFormCheck(String formCheck) {
		this.formCheck = formCheck;
	}
	private List securityBeanList;
	
	public String getOpenFlag() {
		return openFlag;
	}
	public void setOpenFlag(String openFlag) {
		this.openFlag = openFlag;
	}
	public List getSecurityBeanList() {
		return securityBeanList;
	}
	public void setSecurityBeanList(List securityBeanList) {
		this.securityBeanList = securityBeanList;
	}


}

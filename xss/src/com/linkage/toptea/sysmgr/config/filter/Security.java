package com.linkage.toptea.sysmgr.config.filter;

import java.util.List;

public class Security {

	  private String openFlag;
	  private String formCheck;
	  private List securityBeanList;

	  public String getFormCheck()
	  {
	    return this.formCheck;
	  }
	  public void setFormCheck(String formCheck) {
	    this.formCheck = formCheck;
	  }

	  public String getOpenFlag()
	  {
	    return this.openFlag;
	  }
	  public void setOpenFlag(String openFlag) {
	    this.openFlag = openFlag;
	  }
	  public List getSecurityBeanList() {
	    return this.securityBeanList;
	  }
	  public void setSecurityBeanList(List securityBeanList) {
	    this.securityBeanList = securityBeanList;
	  }
}

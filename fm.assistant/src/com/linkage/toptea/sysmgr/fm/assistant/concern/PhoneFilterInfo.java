package com.linkage.toptea.sysmgr.fm.assistant.concern;

import com.linkage.toptea.context.Context;
import com.linkage.toptea.sysmgr.cm.ConfigManager;
import com.linkage.toptea.sysmgr.cm.MoInfo;


/**
 * 过滤器信息类
 * @author panhua
 *
 */
public class PhoneFilterInfo {
	
	public static int FILTER_USE = 1; //启用
	public static int FILTER_STOP = -1; //禁止
	public static int FILTER_PAUSE = 0; //暂停
	
	public static int FILTER_CHILD_YES = 1; //包含
	public static int FILTER_CHILD_NO =0; //不包含

	private String pathName; //路径名称
	private String key; //关键字
	private String timelen; //时长
	private int ischild = 1; //是否包含子对象
	private int isuse = 1; //是否启用
    private String erper ; //责任人
    private boolean isInherit = false;//是继承的
    
    // 对象路径
    private String moId = "";
    
    // 是否启用配置参数 1:启用 ， 0：禁用
    private int forPara = 1;
    
    
    
	public boolean isInherit() {
		return isInherit;
	}

	public void setInherit(boolean isInherit) {
		this.isInherit = isInherit;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTimelen() {
		return timelen;
	}

	public void setTimelen(String timelen) {
		this.timelen = timelen;
	}

	public int getIschild() {
		return ischild;
	}

	public void setIschild(int ischild) {
		this.ischild = ischild;
	}

	public int getIsuse() {
		return isuse;
	}

	public void setIsuse(int isuse) {
		this.isuse = isuse;
	}

	public String getErper() {
		return erper;
	}

	public void setErper(String erper) {
		this.erper = erper;
	}

	public int getForPara() {
		return forPara;
	}

	public void setForPara(int forPara) {
		this.forPara = forPara;
	}

	/**
	 *  类比较，用于排序，规则适用原则：路径近者优先
	 * @param info
	 * @return
	 */
	public int compareTo(PhoneFilterInfo info) {
		ConfigManager configManager = (ConfigManager) Context.getContext().getAttribute("configManager");
		try {
			if (info == null) { return -1; }
			
			if (info.getMoId() == null || "".equals(info.getMoId())) {
				return -1;
			}
			if (this.moId == null || "".equals(this.moId)) {
				return 1;
			}
			//  路径相同
			if (this.moId.equals(info.getMoId())) { return 0; }
			
			MoInfo pmo = configManager.findMoById(info.getMoId());
			MoInfo tmo = configManager.findMoById(this.moId);
			
			if ( pmo == null ) { return -1; }
			if ( tmo == null ) { return 1; }
			
			short[] ppath = pmo.getPath();
			short[] tpath = tmo.getPath();
			
			if ( ppath == null || "".equals(ppath)) { return -1; }
			if ( tpath == null || "".equals(tpath)) { return 1; }
			
			int l = Math.min(tpath.length, ppath.length);
			for (int i = 0; i < l; i++) {
				if (tpath[i] > ppath[i]) {	return 2; }
				if (tpath[i] < ppath[i]) {	return -2; }
			}
			// 本路径比比较路径长，排在前面
			if (ppath.length > tpath.length) {
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;

	}

	public String getMoId() {
		return moId;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}
}

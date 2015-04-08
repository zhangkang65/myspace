package com.linkage.toptea.sysmgr.fm.assistant.callkeyword;

import java.sql.Timestamp;

import com.linkage.toptea.context.Context;
import com.linkage.toptea.sysmgr.cm.ConfigManager;
import com.linkage.toptea.sysmgr.cm.MoInfo;


/**
 * ���ؼ����Զ����������
 * @author huangzh
 * TF_TT_ALARM_CALL_KEYWORD
 */
public class CallKeyWordInfo {

	// ����ID
	private String id = "";

	// ����ID
	private String moId = "";

	// ����ؼ���
	private String keyword = "";

	// �����޸���
	private String userId = "";

	// �Ƿ�Ӧ�����Ӷ��� 1 �ǣ�0 ��
	private int forChild = 1;

	// �����Ƿ�������ʽУ�� 0 �� ��1 �� 
	private int regExpr = 0;	

	// ��������� 1 ���������1, 2���������2 ......
	private int caller = -1;	

	// ������ʱ��
	private Timestamp time;

	public int getCaller() {
		return caller;
	}

	public void setCaller(int caller) {
		this.caller = caller;
	}

	public int getForChild() {
		return forChild;
	}

	public void setForChild(int forChild) {
		this.forChild = forChild;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getMoId() {
		return moId;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getRegExpr() {
		return regExpr;
	}

	public void setRegExpr(int regExpr) {
		this.regExpr = regExpr;
	}
	
	/**
	 *  ��Ƚϣ��������򣬹�������ԭ��·����������
	 * @param info
	 * @return
	 */
	public int compareTo(CallKeyWordInfo info) {
		ConfigManager configManager = (ConfigManager) Context.getContext().getAttribute("configManager");
		try {
			if (info == null) { return -1; }
			
			if (info.getMoId() == null || "".equals(info.getMoId())) {
				return -1;
			}
			if (this.moId == null || "".equals(this.moId)) {
				return 1;
			}
			//  ·����ͬ
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
			// ��·���ȱȽ�·����������ǰ��
			if (ppath.length > tpath.length) {
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;

	}
		
}

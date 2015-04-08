package com.linkage.toptea.sysmgr.fm.assistant.concern;

import com.linkage.toptea.context.Context;
import com.linkage.toptea.sysmgr.cm.ConfigManager;
import com.linkage.toptea.sysmgr.cm.MoInfo;


/**
 * ��������Ϣ��
 * @author panhua
 *
 */
public class PhoneFilterInfo {
	
	public static int FILTER_USE = 1; //����
	public static int FILTER_STOP = -1; //��ֹ
	public static int FILTER_PAUSE = 0; //��ͣ
	
	public static int FILTER_CHILD_YES = 1; //����
	public static int FILTER_CHILD_NO =0; //������

	private String pathName; //·������
	private String key; //�ؼ���
	private String timelen; //ʱ��
	private int ischild = 1; //�Ƿ�����Ӷ���
	private int isuse = 1; //�Ƿ�����
    private String erper ; //������
    private boolean isInherit = false;//�Ǽ̳е�
    
    // ����·��
    private String moId = "";
    
    // �Ƿ��������ò��� 1:���� �� 0������
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
	 *  ��Ƚϣ��������򣬹�������ԭ��·����������
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

	public String getMoId() {
		return moId;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}
}

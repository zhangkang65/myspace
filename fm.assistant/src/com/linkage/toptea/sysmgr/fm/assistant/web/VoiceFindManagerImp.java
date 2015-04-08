package com.linkage.toptea.sysmgr.fm.assistant.web;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import com.linkage.toptea.auc.User;
import com.linkage.toptea.auc.UserManager;
import com.linkage.toptea.util.BeanUtil;

/**
 * �Զ��������ͳ�ƹ�����
 * @author luhl
 *
 */
public class VoiceFindManagerImp {

	/** ���ݿ���Դ */
	private DataSource dataSource;

	private Category logger = Logger.getLogger(VoiceFindManagerImp.class);

	private UserManager userManager;

	/**
	 * @return the userManager
	 */
	public UserManager getUserManager() {
		return userManager;
	}

	/**
	 * @param userManager
	 *            the userManager to set
	 */
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	/**
	 * ȡ�����ݿ�������Դ
	 * 
	 * @return dataSource ���ݿ�������Դ
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * �������ݿ�������Դ
	 * 
	 * @param dataSource
	 *            ���ݿ�������Դ
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * ͳ������ - ����Աͳ��
	 * 
	 * @return
	 */
	public ArrayList<OutCall> getPhonerStatData(String starttime, String endTime) {

		ArrayList<OutCall> dataList = new ArrayList<OutCall>();
		java.sql.Connection con = null;
		PreparedStatement ps = null;
		ResultSet rset = null;
		try {
			logger.debug("----->---getPhonerStatData---->------");
			// ������SQL���
			StringBuffer sb = new StringBuffer();
			sb.append("  select t2.name,t1.caller,(t1.total-t1.succ) fail,t1.succ,  ");
			sb.append("  round((t1.total-t1.succ)*100/t1.total,2)||'%' rate_fail ,round(t1.succ*100/t1.total,2)||'%' rate_succ from (  ");
			sb.append("  select caller, sum(decode(t.status,'-1',0,1)) succ,sum(t.num) total  ");
			sb.append("  from tf_tt_alarm_callphone t where t.status <> 0  ");
			sb.append("  and ctime < to_date(?,'yyyy-mm-dd hh24:mi:ss') + 1   ");
			sb.append("  and ctime >= to_date(?,'yyyy-mm-dd hh24:mi:ss')   ");
			sb.append("  and  num > 0  ");
			sb.append("  group by caller ) t1,  ");
			sb.append("  (select t.name,o.userid from uportal.cas_organization t,uportal.cas_user_organization o  ");
			sb.append("  where t.id = o.organizationid) t2 where t1.caller = t2.userid  ");

			logger.debug("----->------->------" + sb.toString());

			// ȡ��DB����
			con = dataSource.getConnection();
			// Ԥ����SQL
			ps = con.prepareStatement(sb.toString());
			ps.setString(1, endTime);
			ps.setString(2, starttime);
			// ִ��
			rset = ps.executeQuery();
			while (rset.next()) {
				OutCall outcall = new OutCall();
				// ������֯
				outcall.setCallerorganize(rset.getString("name"));
				// �����Ա
				User user = userManager.getUser(rset.getString("caller"));

				if (user == null) {
					outcall.setCaller(rset.getString("caller"));
				} else {

					user.getOrganizationDomain();
					outcall.setCaller(user.getName());

				}
				// ʧ�ܴ���
				outcall.setFailnum(rset.getInt("fail"));
				// �ɹ�����
				outcall.setSunnum(rset.getInt("succ"));
				// ʧ����
				outcall.setFailra(rset.getString("rate_fail"));
				// �ɹ���
				outcall.setSunra(rset.getString("rate_succ"));
				// ���������dataList��
				dataList.add(outcall);
			}
			logger.debug("-----<---getTodayStatData----<------");
		} catch (Exception e) {
			logger.error("", e);
			e.printStackTrace();
		} finally {
			// �ر������Դ
			BeanUtil.closeResource(rset, ps, con);
		}

		return dataList;

	}
	
	

	/**
	 * ������¼����
	 * 
	 * @param filter
	 */
	public void report(String starttime, String endTime) {
		ArrayList<OutCall> dataList = getPhonerStatData(starttime, endTime);
		try {
			String FS = System.getProperty("file.separator");
			String TOMCAT_HOME = System.getProperty("catalina.home");
			String TEMP_PATH = TOMCAT_HOME + FS + "temp" + FS;
			File tempDir = new File(TEMP_PATH + "outCallAlarm.xls");

			if (null == dataList || dataList.size() == 0) {
				return;
			}
			WritableWorkbook wwb = Workbook.createWorkbook(tempDir);
			WritableSheet ws = wwb.createSheet("����ͳ��", 0);
			Label label = null;
			WritableCellFormat format1 = new WritableCellFormat();

			format1.setVerticalAlignment(VerticalAlignment.CENTRE);
			format1.setAlignment(Alignment.CENTRE);

			WritableFont font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD);
			font.setColour(Colour.BLUE);
			WritableCellFormat format2 = new WritableCellFormat();
			format2.setVerticalAlignment(VerticalAlignment.CENTRE);
			format2.setAlignment(Alignment.CENTRE);
			format2.setFont(font);
			HashMap<String, ArrayList<OutCall>> hashmap = new HashMap<String, ArrayList<OutCall>>();
			ArrayList<OutCall> arral = null;
			for (int i = 0; i < dataList.size(); i++) {
				OutCall outcall = dataList.get(i);
				if (hashmap.containsKey(outcall.getCallerorganize())) {
					arral = hashmap.get(outcall.getCallerorganize());
					arral.add(outcall);
					hashmap.put(outcall.getCallerorganize(), arral);
				} else {
					arral = new ArrayList<OutCall>();
					arral.add(outcall);
					hashmap.put(outcall.getCallerorganize(), arral);
				}
			}
			label = new Label(0, 0, "������֯", format2);
			ws.addCell(label);
			label = new Label(1, 0, "�����", format2);
			ws.addCell(label);
			label = new Label(2, 0, "ʧ������", format2);
			ws.addCell(label);
			label = new Label(3, 0, "�ɹ�����", format2);
			ws.addCell(label);
			label = new Label(4, 0, "ʧ����", format2);
			ws.addCell(label);
			label = new Label(5, 0, "�ɹ���", format2);
			ws.addCell(label);

			Iterator<String> iter = hashmap.keySet().iterator();
			int i = 1;
			while (iter.hasNext()) {
				String key = iter.next();
				ArrayList<OutCall> arrout = hashmap.get(key);
				for (int j = 0; j < arrout.size(); j++) {
					if (j == 0) {
						label = new Label(0, i, key + "", format1);
						ws.addCell(label);
						ws.mergeCells(0, i, 0, i + arrout.size() - 1);
					}
					String value = "";
					User user = userManager.getUser(arrout.get(j).getCaller());
					if (null != user) {
						value = user.getName();
					} else {
						value = arrout.get(j).getCaller();
					}
					label = new Label(1, i + j, value, format1);
					ws.addCell(label);

					label = new Label(2, i + j, arrout.get(j).getFailnum() + "", format1);
					ws.addCell(label);

					label = new Label(3, i + j, arrout.get(j).getSunnum() + "", format1);
					ws.addCell(label);

					label = new Label(4, i + j, arrout.get(j).getFailra() + "", format1);
					ws.addCell(label);

					label = new Label(5, i + j, arrout.get(j).getSunra() + "", format1);
					ws.addCell(label);
				}
				i += arrout.size();
			}

			// ����һ�еĸ߶���Ϊ2000
			// ws.setRowView(0, 2000);
			// ����һ�еĿ����Ϊ20
			ws.setColumnView(0, 16);
			ws.setColumnView(1, 16);
			ws.setColumnView(2, 16);
			ws.setColumnView(3, 16);
			ws.setColumnView(4, 16);

			// д��Exel������
			wwb.write();
			// �ر�Excel����������
			wwb.close();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
		}
	}

	

}

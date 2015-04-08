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
 * 自动外呼报表统计功能类
 * @author luhl
 *
 */
public class VoiceFindManagerImp {

	/** 数据库资源 */
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
	 * 取得数据库链接资源
	 * 
	 * @return dataSource 数据库链接资源
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * 设置数据库链接资源
	 * 
	 * @param dataSource
	 *            数据库链接资源
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 统计数据 - 按人员统计
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
			// 检索用SQL语句
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

			// 取得DB链接
			con = dataSource.getConnection();
			// 预编译SQL
			ps = con.prepareStatement(sb.toString());
			ps.setString(1, endTime);
			ps.setString(2, starttime);
			// 执行
			rset = ps.executeQuery();
			while (rset.next()) {
				OutCall outcall = new OutCall();
				// 所属组织
				outcall.setCallerorganize(rset.getString("name"));
				// 外呼人员
				User user = userManager.getUser(rset.getString("caller"));

				if (user == null) {
					outcall.setCaller(rset.getString("caller"));
				} else {

					user.getOrganizationDomain();
					outcall.setCaller(user.getName());

				}
				// 失败次数
				outcall.setFailnum(rset.getInt("fail"));
				// 成功次数
				outcall.setSunnum(rset.getInt("succ"));
				// 失败率
				outcall.setFailra(rset.getString("rate_fail"));
				// 成功率
				outcall.setSunra(rset.getString("rate_succ"));
				// 将结果放入dataList中
				dataList.add(outcall);
			}
			logger.debug("-----<---getTodayStatData----<------");
		} catch (Exception e) {
			logger.error("", e);
			e.printStackTrace();
		} finally {
			// 关闭相关资源
			BeanUtil.closeResource(rset, ps, con);
		}

		return dataList;

	}
	
	

	/**
	 * 导出记录报表
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
			WritableSheet ws = wwb.createSheet("报表统计", 0);
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
			label = new Label(0, 0, "所属组织", format2);
			ws.addCell(label);
			label = new Label(1, 0, "外呼人", format2);
			ws.addCell(label);
			label = new Label(2, 0, "失败总数", format2);
			ws.addCell(label);
			label = new Label(3, 0, "成功总数", format2);
			ws.addCell(label);
			label = new Label(4, 0, "失败率", format2);
			ws.addCell(label);
			label = new Label(5, 0, "成功率", format2);
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

			// 将第一行的高度设为2000
			// ws.setRowView(0, 2000);
			// 将第一列的宽度设为20
			ws.setColumnView(0, 16);
			ws.setColumnView(1, 16);
			ws.setColumnView(2, 16);
			ws.setColumnView(3, 16);
			ws.setColumnView(4, 16);

			// 写入Exel工作表
			wwb.write();
			// 关闭Excel工作薄对象
			wwb.close();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
		}
	}

	

}

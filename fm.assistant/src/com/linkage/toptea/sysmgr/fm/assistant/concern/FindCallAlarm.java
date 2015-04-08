package com.linkage.toptea.sysmgr.fm.assistant.concern;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.linkage.toptea.auc.User;
import com.linkage.toptea.auc.UserManager;
import com.linkage.toptea.context.Context;
import com.linkage.toptea.sysmgr.cm.ConfigManager;
import com.linkage.toptea.util.BeanUtil;

/**
 * ����澯����ע�澯��ѯ��
 * @author huangzh
 * @since 2012-111-02
 */
public class FindCallAlarm {
	//	 ��־��ӡ
	private Log logger = LogFactory.getLog(FindCallAlarm.class);
	
	UserManager userManager = (UserManager) Context.getContext().getAttribute("userManager");
	
	ConfigManager cm = (ConfigManager)Context.getContext().getAttribute("configManager");

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	
	/**
	 * ����澯��ѯ
	 * @param filter ��ѯ������
	 * @param start ��ʼҳ��
	 * @param limit ÿҳ��ʾ����
	 * @return ����澯
	 */
	public List<AutoCallAlarmInfo> findAutoCallAlarmInfo(AutoCallAlarmFilter filter,int start,int limit) {
		 List<AutoCallAlarmInfo> items = new ArrayList<AutoCallAlarmInfo>();
			Connection conn = null;
			PreparedStatement ps = null;
			String sql = "( SELECT A.*,C.ALARM_CONTENT,C.ALARM_FIRSTOCCURTIME,C.ALARM_LASTOCCURTIME, "
			 + " C.ALARM_CURRENTGRADE,C.ALARM_COUNT,C.WORK_ID,C.SOURCE,C.CASE_ID,C.MO_ID,C.PATH, C.ALARM_CODE "
			 + " FROM TF_TT_ALARM_CALLPHONE A, TF_TT_ALARM C WHERE  A.ALARM_ID = C.ALARM_ID "
			 + " UNION SELECT A.*,B.ALARM_CONTENT,B.ALARM_FIRSTOCCURTIME,B.ALARM_LASTOCCURTIME,"
			 + " B.ALARM_CURRENTGRADE,B.ALARM_COUNT,B.WORK_ID,B.SOURCE,B.CASE_ID,B.MO_ID,B.PATH, B.ALARM_CODE  "
			 + " FROM TF_TT_ALARM_CALLPHONE A,TF_TT_ALARM_HIS B WHERE B.ALARM_ID = A.ALARM_ID)";
			sql= " SELECT * FROM " + sql + " A WHERE 1=1 " + setCondition(filter) 
				+ " ORDER BY ISCONCERN ASC,CTIME DESC, STATUS ASC,NUM DESC";
			sql = " SELECT t2.* FROM (SELECT ROWNUM NO,T.* FROM (" + sql + ") T WHERE ROWNUM <=" 
				+ (start + limit) + ") T2 WHERE NO>" + start;

//			System.out.println(sql);
			ResultSet rs = null;
			try {
				conn = this.dataSource.getConnection();
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					AutoCallAlarmInfo info = new AutoCallAlarmInfo();
					info.setId(rs.getString("ALARM_ID"));
					info.setCaller(rs.getString("CALLER"));
					info.setCallid(rs.getString("CALLID"));
					info.setCallTime(rs.getTimestamp("CTIME"));
					info.setPhone(rs.getString("PHONE"));
					info.setIsconcern(rs.getInt("ISCONCERN"));
					info.setIsshow(rs.getInt("ISHOW"));
					info.setContent(rs.getString("ALARM_CONTENT"));
					info.setFirstOccurTime((rs.getTimestamp("ALARM_FIRSTOCCURTIME")==null?0:rs.getTimestamp("ALARM_FIRSTOCCURTIME").getTime()));
					info.setLastOccurTime((rs.getTimestamp("ALARM_LASTOCCURTIME")==null?0:rs.getTimestamp("ALARM_LASTOCCURTIME").getTime()));
					info.setCurrentGrade(rs.getInt("ALARM_CURRENTGRADE"));
					info.setCount(rs.getInt("ALARM_COUNT"));
					info.setWorkId(rs.getString("WORK_ID"));
					info.setSource(rs.getString("SOURCE"));
					info.setCaseId(rs.getString("CASE_ID"));
					info.setNum(rs.getInt("NUM"));
					info.setStatus(rs.getInt("status"));
					info.setMoId(rs.getString("MO_ID"));
					info.setPath(rs.getString("PATH"));
					items.add(info);
				}
			} catch (Exception e) {
				logger.error("findAutoCallAlarmInfo", e);
			} finally {
				BeanUtil.closeResource(rs, ps, conn);
			}
			return items;
		 
	 }
	
	/**
	 * ����澯������ѯ
	 * @param filter ��ѯ������
	 * @return ����澯����
	 */
	public int findAutoCallAlarmCount(AutoCallAlarmFilter filter ) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "( SELECT A.*,C.ALARM_CONTENT,C.ALARM_FIRSTOCCURTIME,C.ALARM_LASTOCCURTIME, "
		 + " C.ALARM_CURRENTGRADE,C.ALARM_COUNT,C.WORK_ID,C.SOURCE,C.CASE_ID,C.MO_ID,C.PATH "
		 + " FROM TF_TT_ALARM_CALLPHONE A, TF_TT_ALARM C WHERE  A.ALARM_ID = C.ALARM_ID "
		 + " UNION SELECT A.*,B.ALARM_CONTENT,B.ALARM_FIRSTOCCURTIME,B.ALARM_LASTOCCURTIME,"
		 + " B.ALARM_CURRENTGRADE,B.ALARM_COUNT,B.WORK_ID,B.SOURCE,B.CASE_ID,B.MO_ID,B.PATH "
		 + " FROM TF_TT_ALARM_CALLPHONE A,TF_TT_ALARM_HIS B WHERE B.ALARM_ID = A.ALARM_ID)";
		sql= " SELECT count(*) FROM " + sql + " A WHERE 1=1 " + setCondition(filter);
		
		ResultSet rs = null;
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) { return rs.getInt(1); }
		} catch (Exception e) {
			logger.error("findAutoCallAlarmInfo", e);
		} finally {
			BeanUtil.closeResource(rs, ps, conn);
		}
		return 0;		 
	}
	
	/**
	 * ��������澯��ѯ����
	 * @param filter ��ѯ������
	 * @return ����澯��ѯ����String
	 */
	public String setCondition(AutoCallAlarmFilter filter) {
		StringBuffer condition = new StringBuffer();
		if (filter.getIsshow() != -1) {
			condition.append(" AND A.ISHOW =" + filter.getIsshow());
		}
		if (!isNull(filter.getId())) {
			condition.append(" AND A.ALARM_ID='" + filter.getId() + "'");
		}
		if (!isNull(filter.getContent())) {
			condition.append(" AND A.ALARM_CONTENT LIKE '%"
					+ filter.getContent() + "%'");
		}
		if (!isNull(filter.getSource())) {
			condition.append(" AND SOURCE ='" + filter.getSource() + "'");
		}
		if (!isNull(filter.getCode())) {
			condition.append(" AND ALARM_CODE ='" + filter.getCode() + "'");
		}
		if (!isNull(filter.getCaller())) {
			condition.append(" AND CALLER ='" + filter.getCaller() + "'");
		}
		if (!isNull(filter.getStime())) {
			condition.append(" AND CTIME >= TO_DATE('" + filter.getStime()
					+ "','YYYY-MM-DD hh24:mi:ss') ");
		}
		if (!isNull(filter.getEtime())) {
			condition.append(" AND CTIME <= TO_DATE('" + filter.getEtime()
					+ "','YYYY-MM-DD hh24:mi:ss') ");
		}
		if (filter.getCurrentGrade() != 0) {
			condition.append(" AND ALARM_CURRENTGRADE >="
					+ filter.getCurrentGrade());
		}
		if (!isNull(filter.getPath())) {
			condition.append(" AND ( PATH ='" + filter.getPath()
					+ "' OR PATH LIKE '" + filter.getPath() + ".%') ");
		}

		return condition.toString();
	}
	
	/**
	 * �����¼����
	 * @param filter
	 */
	public String report (AutoCallAlarmFilter filter) {
		List<AutoCallAlarmInfo> callAlarms = findAutoCallAlarmInfo( filter, 0, 10000);
		String fileName =  System.currentTimeMillis() + ".xls";
		try {
			String FS = System.getProperty("file.separator");
			String TOMCAT_HOME = System.getProperty("catalina.home");
			String TEMP_PATH = TOMCAT_HOME + FS + "temp" + FS;
			File tempDir = new File(TEMP_PATH + fileName );

			WritableWorkbook wwb = Workbook.createWorkbook( tempDir );
			WritableSheet ws = wwb.createSheet("����ͳ��", 0);
			
			if ( null == callAlarms || callAlarms.size() <= 0 ) { 
				// д��Exel������
				wwb.write();
				// �ر�Excel����������
				wwb.close();
				return fileName;
			}
			
			Label label = null;
			WritableCellFormat format1 = new WritableCellFormat();
			// ����ΪTIMES���ֺ�16���Ӵ���ʾ��
			//WritableFont font1 = new WritableFont(WritableFont.TIMES, 16, WritableFont.BOLD);			
			// ��ˮƽ���뷽ʽָ��Ϊ����    
			//format1.setAlignment(jxl.format.Alignment.CENTRE);
			//format1.setBackground(Colour.RED);
			// �Ѵ�ֱ���뷽ʽָ��Ϊ����    
			format1.setVerticalAlignment(VerticalAlignment.CENTRE);
			format1.setAlignment(Alignment.CENTRE);
			
			WritableFont font = new WritableFont(WritableFont.TIMES, 11, WritableFont.BOLD);
			font.setColour(Colour.BLUE);
			WritableCellFormat format2 = new WritableCellFormat();
			format2.setVerticalAlignment(VerticalAlignment.CENTRE);
			format2.setAlignment(Alignment.CENTRE);
			format2.setFont(font);
			
			WritableCellFormat format3 = new WritableCellFormat();
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			format3.setAlignment(Alignment.LEFT);

			
			for ( int i = 0 ; i < callAlarms.size(); i++ ) {	
				AutoCallAlarmInfo info = callAlarms.get(i);
				if ( i == 0 ) {
					label = new Label(0, i, "�����", format2);	
					ws.addCell( label );
					label = new Label(1, i, "�Ƿ�ɹ�", format2);	
					ws.addCell( label );
					label = new Label(2, i, "���ʱ��", format2);
					ws.addCell( label );
					label = new Label(3, i, "���д���", format2);	
					ws.addCell( label );
					label = new Label(4, i, "�澯����", format2);	
					ws.addCell( label );
					label = new Label(5, i, "�澯����", format2);	
					ws.addCell( label );
					label = new Label(6, i, "����·��", format2);	
					ws.addCell( label );
					label = new Label(7, i, "��һ��ʱ��", format2);	
					ws.addCell( label );
					label = new Label(8, i, "���һ��ʱ��", format2);	
					ws.addCell( label );
					label = new Label(9, i, "����", format2);	
					ws.addCell( label );
					label = new Label(10, i, "����", format2);	
					ws.addCell( label );
				}

				String value= "";
				User user = userManager.getUser(info.getCaller());
				if ( null != user ) {
					value = user.getName();
				} else {
					value = info.getCaller();
				}				
				label = new Label(0, i+1, value, format1);	
				ws.addCell( label );
				
				if ( -1 == info.getStatus() ){
					value = "���ʧ��";
				} else if (  0 == info.getStatus()  ) {
					value = "�������";
				} else if (  1 == info.getStatus()  ) {
					value = "����ɹ�";
				}				
				label = new Label(1, i+1, value, format1);	
				ws.addCell( label );
				
				DateFormat formatter = DateFormat.getDateTimeInstance();
				value = formatter.format( info.getCallTime() );
				label = new Label(2, i+1, value, format1);
				ws.addCell( label );
				
				label = new Label(3, i+1, info.getNum() + "", format1);	
				ws.addCell( label );
				
				label = new Label(4, i+1, info.getCurrentGrade() + "", format1);	
				ws.addCell( label );
				
				label = new Label(5, i+1, info.getWorkId(), format1);	
				ws.addCell( label );
				
				value = cm.getNamingPathById(info.getMoId());
				label = new Label(6, i+1, value, format3);	
				ws.addCell( label );
				
				value = formatter.format( info.getFirstOccurTime() );
				label = new Label(7, i+1, value, format1);	
				ws.addCell( label );

				value = formatter.format( info.getLastOccurTime() );
				label = new Label(8, i+1, value, format1);	
				ws.addCell( label );
				
				label = new Label(9, i+1, info.getCount() + "", format1);	
				ws.addCell( label );
				
				label = new Label(10, i+1, info.getContent(), format3);	
				ws.addCell( label );
			}
			
			// ����һ�еĸ߶���Ϊ2000    
			//ws.setRowView(0, 2000);
			// ����һ�еĿ����Ϊ20  
			ws.setColumnView(2, 18);
			ws.setColumnView(6, 40);
			ws.setColumnView(7, 18);
			ws.setColumnView(8, 18);
			ws.setColumnView(10, 60);
			// д��Exel������
			wwb.write();
			// �ر�Excel����������
			wwb.close();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error( "", e );
		}
		
		return fileName;
	}
	 
	/**
	 * ����澯��ѯ
	 * @param filter ��ѯ������
	 * @param start ��ʼҳ��
	 * @param limit ÿҳ��ʾ����
	 * @return ����澯
	 */
	public List<AutoCallAlarmInfo> findCallDetailInfo( String alarmId ) {
		 List<AutoCallAlarmInfo> items = new ArrayList<AutoCallAlarmInfo>();
			Connection conn = null;
			ResultSet rs = null;
			PreparedStatement ps = null;
			String sql = " SELECT * FROM TF_TT_CALLPHONE_DETAIL WHERE ALARM_ID = ? ORDER BY CTIME DESC ";
			try {
				conn = this.dataSource.getConnection();
				ps = conn.prepareStatement(sql);
				ps.setString(1, alarmId);
				rs = ps.executeQuery();
				while (rs.next()) {
					AutoCallAlarmInfo info = new AutoCallAlarmInfo();
					info.setId(rs.getString("ALARM_ID"));
					info.setCaller(rs.getString("CALLER"));
					info.setCallid(rs.getString("CALLID"));
					info.setCallTime(rs.getTimestamp("CTIME"));
					info.setPhone(rs.getString("PHONE"));
					info.setIsconcern(rs.getInt("ISCONCERN"));
					info.setIsshow(rs.getInt("ISHOW"));
					info.setBackTime(rs.getTimestamp("BTIME"));
					info.setStatus(rs.getInt("status"));
					items.add(info);
				}
			} catch (Exception e) {
				logger.error("findCallDetailInfo", e);
			} finally {
				BeanUtil.closeResource(rs, ps, conn);
			}
			return items;		 
	 }
	
	 private boolean isNull ( String str ) {
		 if ( null == str || "".equals(str) ) { return true; }
		 return false;
	 }
}

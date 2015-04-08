package com.linkage.toptea.sysmgr.fm.assistant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.log4j.Category;

import com.linkage.toptea.sysmgr.cm.ConfigManager;
import com.linkage.toptea.util.BeanUtil;
/**
 * ���ܣ��澯���������
 * ���ߣ��˻�
 * ʱ�䣺2011-02-10
 * 
 */

public class AstConfig {
	
	//��־��ӡ
	private Category logger = Category.getInstance(AstConfig.class);
	
	// ��ע���𣨵�ǰ�������ϼ���
	public static int CONCREN_GRADE = 3; 
	// ��ע���ʱ��
	public static int CONCREN_TIME = 2;
	//���ټ���
	public static int TRACK_GRADE = 3;
	
	//����ʱ����Ĭ��2Сʱ��
	public static int TRACK_TIME = 2;
	
	//�ӳ�ʱ����Ĭ��4Сʱ��
	public static int TRACK_NEXT_TIME = 4;
	
	//���˹�ע�ڵ㣨;�ָ���
	public static String PATH_FILTER = "22.32";
		
	/**
	 * ���˸��ٽڵ�dotID
	 */
	public static String TRACK_PATH_FILTER_ID = "";
	
	/**
	 * ���˸��ٽڵ�path
	 */
	public static String TRACK_PATH_FILTER_PATH = "";
	
	/**
	 * cm
	 */
	private ConfigManager cm;
	
	/**
	 * ���ü��ʱ��
	 * @param minuts ����
	 */
	public void setConcrenTime(int minuts){
		AstConfig.CONCREN_TIME = minuts;
		//this.changeConfig();
	}
	
	/**
	 * ���ù�ע����
	 * @param grade �澯����
	 */
	
	public void setConcrenGrade(int grade){
		AstConfig.CONCREN_GRADE = grade;
	}
	
	
	/**
	 * ��ȡ�������˽ڵ��SQL
	 * @return
	 */
	public static String getPathFilterSQL(){
		String sql = "";
		if (AstConfig.PATH_FILTER==null) return "";
		String[] items = AstConfig.PATH_FILTER.split(";");
		for (int i=0;i<items.length;i++){
			sql+=" and a.path!='"+items[i]+"' and a.path not like '"+items[i]+".%' ";
		}
		return sql;
	}
	
	//�Ƿ��Ѿ����ù��� 
	private boolean isConfig = false; 
	
	//Spring�������ݿ�
	private DataSource dataSource;
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * ��ʼ����ά����
	 */
	public void init() {
		Connection conn = null;
		String str = "";
		PreparedStatement ps = null;
		String sql = "";
		ResultSet rs = null;
		try {	
			conn = this.dataSource.getConnection();
			sql = "select * from TF_TT_ALARM_ASSISTANT_CONF";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()){
				isConfig = true;
				AstConfig.CONCREN_GRADE = rs.getInt("CONCRENGRADE");
				AstConfig.CONCREN_TIME = rs.getInt("CONCRENTIME");
				AstConfig.TRACK_GRADE = rs.getInt("TRACKGRADE");
				AstConfig.TRACK_NEXT_TIME = rs.getInt("TRACKNEXTTIME");
				AstConfig.TRACK_TIME = rs.getInt("TRACKTIME");
				AstConfig.PATH_FILTER = rs.getString("PATHFILTER");
				AstConfig.TRACK_PATH_FILTER_ID = rs.getString("TRACKPATHFILTER") == null ? "":rs.getString("TRACKPATHFILTER");
			}
			rs.close();
			
			if (null != AstConfig.TRACK_PATH_FILTER_ID
					&& !"".equals(AstConfig.TRACK_PATH_FILTER_ID.trim()))
			{
				String[] moIds = AstConfig.TRACK_PATH_FILTER_ID.split(";");
				for ( String id : moIds )
				{
					AstConfig.TRACK_PATH_FILTER_PATH += cm.getNamingPathById(id) + ';';
					
				}
			}
			
			//���û�г�ʼ������
			if (!isConfig){
				sql = "insert into TF_TT_ALARM_ASSISTANT_CONF(CONCRENGRADE,CONCRENTIME,TRACKGRADE,TRACKNEXTTIME,TRACKTIME,PATHFILTER) values(?,?,?,?,?,?)";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, AstConfig.CONCREN_GRADE);
				ps.setInt(2, AstConfig.CONCREN_TIME);
				ps.setInt(3, AstConfig.TRACK_GRADE );
				ps.setInt(4, AstConfig.TRACK_NEXT_TIME );
				ps.setInt(5, AstConfig.TRACK_TIME );
				ps.setString(6, AstConfig.PATH_FILTER );
				ps.executeUpdate();
			}
    	   
       }catch(Exception e){
    	   logger.error("�澯��ά��ʼ������ʧ��",e);
       }finally {
			BeanUtil.closeResource(ps, conn);
		}
	}

	public ConfigManager getCm() {
		return cm;
	}

	public void setCm(ConfigManager cm) {
		this.cm = cm;
	}
}

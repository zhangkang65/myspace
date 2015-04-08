package com.linkage.toptea.sysmgr.fm.assistant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JdbcRac {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "(DESCRIPTION=" 
		+ "(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST =10.32.113.24)(PORT =1521))(ADDRESS=(PROTOCOL=TCP)(HOST =10.32.113.23)(PORT =1521)))" + "(CONNECT_DATA=" 
		+ "(SERVER=DEDICATED)" + "(SERVICE_NAME=bomcdb)" + ")" 
		+ ")"; 
		String a4Url = "jdbc:oracle:thin:@"+url;
		
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(a4Url, "utoptea", "iuzjn84l");
			
			String sql = "select * from tf_tt_alarm t where  rownum=1";
			PreparedStatement ps = null;
			ps = conn.prepareStatement(sql);
			ResultSet rs = null;
			rs = ps.executeQuery();
			if (rs.next()){
				
				System.out.println("moId="+rs.getString("mo_id"));
				System.out.println("alarmId="+rs.getString("alarm_id"));
				System.out.println("content="+rs.getString("alarm_content"));
			}else{
				System.out.println("没有任何记录");
				
			}
			
		}catch(Exception  e){
			e.printStackTrace();
		}

	}

}

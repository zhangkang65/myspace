package com.linkage.toptea.sysmgr.fm.assistant.concern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.linkage.toptea.auc.User;
import com.linkage.toptea.auc.UserManager;
import com.linkage.toptea.util.BeanUtil;
/**
 * 外呼文件管理类
 * 实现合并外呼等功能
 * @author huangzh
 *
 */
public class CallFileManager extends Thread {

	/** 系统日志 */
	private Log logger = LogFactory.getLog( CallFileManager.class );
	
	/** 外呼配置路径 */
	private String callPath;
	
	/** 外呼文件存放路径 */
	private String callto;
	
	/** 外呼内容 */
	private final String CALL_MESSAGE = "您当前有#count#条告警需要处理，详情请查阅告警短信。";
	
	/** 当天外呼文件数 */
	private int FILE_COUNT = 1;
	
	private final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	/** 外呼间隔毫秒数 */
	private static long CALL_ID  = 0L;
	
	private static final byte[] SYNC_FLAG = new byte[1];
	
	/**
	 * 取得外呼ID，避免短时内重复，影响外呼接口
	 * @return
	 */
	public long[] getCallId() {
		// 避免重复的CALL_ID
		synchronized(SYNC_FLAG){
			CALL_ID = CALL_ID + 3000L;
			// 使用400次后重新计算
			if (CALL_ID > 300 * 3000L) { CALL_ID = 0L; }
		}
		long callId = System.currentTimeMillis() + CALL_ID;
		logger.info("生成外呼ID：" + callId + " ， 间隔毫秒数：" +  CALL_ID);
		return new long[] {callId, CALL_ID};
	}

	/** 用户管理类 */
	private UserManager userManager ;
	
	public void setUserManager(UserManager  _userManager){
		this.userManager = _userManager;
	}

	//	 Spring连接数据库
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * 取得外呼配置路径
	 * @return 外呼配置路径
	 */
	public String getCallPath() {
		return this.callPath;
	}

	public void setCallPath(String _callpath) {
		this.callPath = _callpath;
	}
	
	/**
	 * 取得外呼文件存放路径
	 * @return 外呼文件存放路径
	 */
	public String getCallto() {
		return callto;
	}
	
	public void setCallto( String callto) {
		this.callto = callto;
	}
	
	/** 系统初始化 */
	public void init () {
		File file = new File ( callPath + "/callTemp/" );
		if ( !file.exists() ) { file.mkdir(); }

		file = new File ( callPath + "/allCall/" );
		if ( !file.exists() ) { file.mkdir(); }
		
		// 初始化外呼文件数量
		this.FILE_COUNT = getFileCount();		
		this.start();
	}
	
	public void run () {
		while ( true ) {
			try {
				sleep (1*60*1000l);
				moveCallFile();
			} catch (Exception e) {
				logger.error("CallFileManager线程运行错误：", e);
			}
		}
	}
	
	
	/**
	 *  外呼合并
	 * @param phone 外呼人号码
	 * @param callid 外呼ID
	 * @return 外呼ID
	 * 
	 */
	synchronized public long writeCallFile( String phone ) {
		String fileName = phone + ".txt";
		InputStreamReader isr = null;
		FileInputStream  fis = null;
		BufferedReader br = null;
		int count = 0;
		long callid = 0;
		// callId与当前系统时间，时差
		long intervel = 0;
		try {
			
			// 文件名称
			File file = new File ( callPath + "/callTemp/" + fileName );	
			
			// 文件存在的场合：读取文件中外呼合并次数
			if ( file.exists() == true ) {
				fis = new FileInputStream(file);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);
				String line = br.readLine();
				logger.debug(" line = " + line );
				if ( line != null ) {
					// 外呼ID置为同一个
					callid = Long.parseLong ( line.split("~")[0] );
					count = Integer.parseInt( line.split("。")[1].split("~")[0] );
					intervel = Long.parseLong ( line.split("~")[3] );
				} else {
					long [] call = getCallId();
					callid = call[0];
					intervel=call[1];
				}
			} else {
				// 文件不存在的场合：创建文件
				file.createNewFile();
				long [] call = getCallId();
				callid = call[0];
				intervel=call[1];
			}
			
			FileWriter writer = new FileWriter(file, false);			
			// 合并数量加一
			writer.write( callid + "~" + phone + "~" + CALL_MESSAGE + (count+1) + "~" + intervel );
			logger.info("合并外呼： " +  callid + "~" + phone + "~" + CALL_MESSAGE + (count+1) +"~" + intervel );
			writer.close();

		} catch (Exception e) {
			logger.error("", e);
			callid = 0;
		} finally {
			try { if ( null != br ) br.close(); } catch ( Exception e ) {}
			try { if ( null != isr ) isr.close(); } catch ( Exception e ) {}
			try { if ( null != fis ) fis.close(); } catch ( Exception e ) {}
		}
		return callid;
	} 
	
	/**
	 * 指定目录下的文件是否存在
	 * @param name 文件名称
	 * @param dir 目录
	 * @return
	 */
	public boolean fileExists( String name, String dir ) {
		String fileName = name + ".txt";

		try {			
			// 文件名称
			File file = new File ( callPath + "/" + dir + "/" + fileName );	
			return file.exists();

		} catch (Exception e) {
			logger.error("", e);
		}
		return true;
	} 
	
	/**
	 * 按配置周期，将合并的外呼文件写入待外呼目录中
	 *
	 */
	public void moveCallFile() {
		InputStreamReader isr = null;
		FileInputStream  fis = null;
		BufferedReader br = null;
		try {
			// 文件名称
			File file = new File ( callPath + "/callTemp" );	
			// 文件不存在的场合：不处理
			if ( file.exists() == false ) { return; }
			
			File[] files = file.listFiles();
			for (File f : files) {
				 String phone = f.getName().split("\\.")[0];
				// 非TXT文件的场合
				if (!f.getName().endsWith(".txt")) {
					continue;
				}
				logger.debug(" file = " +  f.getName() );
				fis = new FileInputStream(f);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);

				//callid + "~" + phone + "~" + CALL_MESSAGE + (count) + "~" + intervel
				String line = br.readLine();
				// 文件格式不正确的场合
				if (line == null || line.split("~").length < 4 ) { 
					logger.info(" file = " +  f.getName() + " 格式不正确，被删除。" );
					f.deleteOnExit();
					continue; 
				}
				
				
				long callid = Long.parseLong(line.split("~")[0]);
				long intervel = Long.parseLong(line.split("~")[3]);
				logger.debug("当前系统时间： " + System.currentTimeMillis() + " callid： " + callid + " intervel： " + intervel);
				// 外呼周期未满的，不处理
				if ( (System.currentTimeMillis() - (callid-intervel)) < PhoneConfigManager.TIME_RUN*60*1000 ) {
					continue;
				}
				// 外呼
				String fileName = getCallFileName();
	
				logger.info("对 " + phone + " 进行自动外呼。自动外呼文件名称： " + fileName);
				logger.info("文件内容" + line);


				line = line.replace("#count#", line.split("。")[1].split("~")[0]);
				
				// 向外呼目录写入外呼文件，等待外呼
				FileWriter writer = new FileWriter(callPath + "/" + callto + "/" + fileName, false);
				writer.write( line.split("。")[0] );
				writer.close();
				
				//向外呼控制目录中，写外呼文件。
				writer = new FileWriter(callPath + "/callCtrl/" + callid + ".txt", false);
				writer.write( line.split("。")[0] );
				writer.close();
				

				//向备份目录中，写外呼文件。
				writer = new FileWriter(callPath + "/allCall/" + fileName + ".txt", false);
				writer.write( line.split("。")[0] );
				writer.close();
				
				fis.close();
				// 删除本批合并外呼文件
				f.delete();
			}
		} catch ( Exception e ) {
			logger.error("", e);
		} finally {
			try { if ( null != br ) br.close(); } catch ( Exception e ) {}
			try { if ( null != isr ) isr.close(); } catch ( Exception e ) {}
			try { if ( null != fis ) fis.close(); } catch ( Exception e ) {}		
		}
	} 
	
	/**
	 * 外呼控制，将外呼文件从外呼控制目录移到待外呼目录中
	 * @param callId 外呼ID(文件名称)
	 */
	public void moveCtrlFile( long oldCallId, long newCallId) {
		InputStreamReader isr = null;
		FileInputStream  fis = null;
		BufferedReader br = null;
		try {
			// 文件名称
			File file = new File ( callPath + "/callCtrl" );	
			// 文件不存在的场合：不处理
			if ( file.exists() == false ) { return; }
			
			File[] files = file.listFiles();
			for (File f : files) {
				
				// 删除2天前的外呼控制文件
				long modifiedTime = System.currentTimeMillis() - f.lastModified();
				if ( modifiedTime/1000/60/60 > 48 ) { 
					f.delete(); 
					continue;
				}
				
				// 非指定文件的场合
				if (!f.getName().equals( oldCallId + ".txt")) { continue; }

				

				logger.debug(" file = " +  f.getName() );
				fis = new FileInputStream(f);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);

				String line = br.readLine();
				
				// 外呼
				String fileName = getCallFileName();
				line = line.replace(oldCallId+"", newCallId+"");
				logger.info("自动外呼文件名称： " + fileName + "[外呼控制]");
				logger.info("文件内容" + line + "[外呼控制]");

				// 向外呼目录写入外呼文件，等待外呼
				FileWriter writer = new FileWriter(callPath + "/" + callto + "/" + fileName, false);
				writer.write( line );
				writer.close();
				
				writer = new FileWriter(callPath + "/callCtrl/" + newCallId + ".txt", false);
				writer.write( line );
				writer.close();
				

				//向备份目录中，写外呼文件。
				writer = new FileWriter(callPath + "/allCall/" + fileName + ".txt", false);
				writer.write( line );
				writer.close();
						
				fis.close();
				f.delete();

			}
		} catch ( Exception e ) {
			logger.error("", e);
		} finally {
			try { if ( null != br ) br.close(); } catch ( Exception e ) {}
			try { if ( null != isr ) isr.close(); } catch ( Exception e ) {}
			try { if ( null != fis ) fis.close(); } catch ( Exception e ) {}		
		}
	}
	
	/**
	 * 取得当天外呼告警次数
	 * @return 当天外呼告警次数
	 */
	public int getCallCount() {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement ps = null;

	
		String sql = " SELECT *  FROM ( SELECT VARCHAR_0, TIME "
			 + " FROM TF_TT_PERFORMANCE p"
			 + " WHERE MO_ID = '3ba0622c6f9843179737abacb5626674'"
			 + " ORDER BY TIME DESC) t"
			 + " WHERE ROWNUM = 1";
		
		int callCount = 0;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				callCount = rs.getInt(1);
				logger.info(" 取得当天外呼告警次数 : " + callCount );
			}
		} catch (Exception e) {
			logger.error("getCallCount", e);
		} finally {
			BeanUtil.closeResource(rs, ps, conn);
		}
		return callCount;
	}
	
	/**
	 * 设置当天外呼文件数量
	 * @param 当天外呼文件数量
	 */
	public void setFileCount( int count ) {
		Connection conn = null;
		PreparedStatement ps = null;
		String date = df.format(Calendar.getInstance().getTime());
		String sql = " UPDATE TF_TT_ALARM_CALLCOUNT SET NUM = ? WHERE TO_CHAR(CTIME,'YYYYMMDD')= '" + date + "'";
		logger.info(" 设置 " + date + " 外呼文件数量 : " + count );
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt( 1, count );
			ps.executeUpdate();
			this.FILE_COUNT = count;
		} catch (Exception e) {
			logger.error("设置当天外呼文件数量", e);
		} finally {
			BeanUtil.closeResource( ps, conn);
		}
	}
	
	/**
	 * 取得当天外呼文件数量
	 * @return 当天外呼文件数量
	 */
	public int getFileCount() {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement ps = null;
		String date = df.format(Calendar.getInstance().getTime());
		String sql = " SELECT NUM FROM  TF_TT_ALARM_CALLCOUNT  WHERE TO_CHAR(CTIME,'YYYYMMDD')= '" + date + "'";
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next() ) { return rs.getInt("NUM"); }
		} catch (Exception e) {
			logger.error("设置当天外呼告警次数", e);
		} finally {
			BeanUtil.closeResource( ps, conn);
		}
		logger.info(" 取得 " + date + " 外呼文件数量 : " + this.FILE_COUNT );
		return this.FILE_COUNT;
	}

	/**
	 * 获取剩下的外呼次数
	 * @return 剩下的外呼次数
	 */
	public int getLastCallCount() {
		return Call.callTotal - getCallCount();
	}
	
	/**
	 * 写直接外呼文件
	 * @param filePath 文件全路径名称
	 */
	public void writeUserCallFile( String phone, long  callid ) {

		logger.info(" callUserFile = " +  phone );
		FileWriter writer = null;
		try {
			//			 外呼
			String fileName = getCallFileName();

			logger.info("对 " + phone + " 进行自动外呼。自动外呼文件名称： " +callPath + "/" + callto + "/"  + fileName);
			// 文件名称
			File file = new File ( callPath  + "/" + callto + "/" + fileName );
			// 向外呼目录写入外呼文件，等待外呼
			writer = new FileWriter(file);
			writer.write( callid + "~" + phone + "~" + "您有告警需要处理，详情请查阅告警短信。" );
			writer.close();

		} catch ( Exception e ) {
			logger.error(" callUserFile ", e);
		} finally {
			try { if ( null != writer ) writer.close(); } catch ( Exception e ) {}		
		}
	} 
	
	/***
	 * 获取外呼文件名称
	 * @return
	 */
	synchronized public String getCallFileName() {
		
		String fileName = "00000".substring(0, 4 - Integer.toString(FILE_COUNT).length()) + ++FILE_COUNT;

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		fileName = "bomc_" + df.format(c.getTime()) +  fileName + ".txt";

		// 更新外呼文件数量
		setFileCount( FILE_COUNT );
		return fileName;
	} 
	
	public void deleteCallCtrlFile( long callId ) {
		try {
			// 文件名称
			File file = new File ( callPath + "/callCtrl/" + callId + ".txt" );	
			// 文件不存在的场合：不处理
			if ( file.exists() == false ) { return; }
			
			file.delete();

		} catch ( Exception e ) {
			logger.error("", e);
		}
	}
	
	/**
	 *  第三方外呼 【不合并】
	 * @param userId 外呼人号码
	 * @param msg 外呼信息
	 * @param alarmId 告警ID
	 * @return 外呼ID 0 外呼失败
	 * 
	 */
	synchronized public long writeCallFile(String userId, String msg, String alarmId) {

		String fileName = getCallFileName();

		long call [] = getCallId();
		long callId = call[0];
		try {

			User user = userManager.getUser(userId);

			if (user == null || user.getMobile() == null) {
				logger.info("对" + user + "外呼告警失败，因为他没有手机号");
				return 0;
			}

			// 文件名称
			File file = new File(callPath + "/call/" + fileName);

			FileWriter writer = new FileWriter(file, false);
			// 合并数量加一
			writer.write(callId + "~" + user.getMobile() + "~" + msg);
			logger.info("第三方外呼： " + callId + "~" + user.getMobile() + "~" + msg);
			writer.close();
			
			addcallRecordToDB(alarmId, callId, userId, user.getMobile());

		} catch (Exception e) {
			logger.error("writeCallFile", e);
			callId = 0;
		}
		return callId;
	}
	
	/**
	 * 向自动外呼表中插入一条记录
	 * @param alarmId 告警ID
	 * @param callId 外呼ID
	 * @param userId 外呼用户ID
	 * @param mobile 外呼用户mobile
	 * @return 0 失败
	 * @throws Exception
	 */
	public int addcallRecordToDB(String alarmId, long callId, String userId, String mobile) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "INSERT INTO TF_TT_ALARM_CALLPHONE(ALARM_ID,CALLID,CALLER,PHONE,STATUS,NUM,ISCONCERN,ISHOW) VALUES(?,?,?,?,0,1,0,1)";

		int result = 0;
		try {
			conn = this.dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, alarmId);
			ps.setString(2, Long.toString(callId));
			ps.setString(3, userId);
			ps.setString(4, mobile);
			result = ps.executeUpdate();
		} finally {
			BeanUtil.closeResource(ps, conn);
		}
		return result;
	}

	/**
	 * 外呼控制，将外呼文件从外呼控制目录移到待外呼目录中,同时更新外呼控制文件名称
	 * @param oldCallId  原外呼ID(文件名称)
	 * @param newCallId 新外呼ID
	 * @param phone 新外呼号码
	 */
	public void moveCallFile( long oldCallId, long newCallId, String phone ) {
		InputStreamReader isr = null;
		FileInputStream  fis = null;
		BufferedReader br = null;
		try {
			// 文件名称
			File file = new File ( callPath + "/callCtrl" );	
			// 文件不存在的场合：不处理
			if ( file.exists() == false ) { return; }
			
			File[] files = file.listFiles();
			for (File f : files) {
				
				// 删除2天前的外呼控制文件
				long modifiedTime = System.currentTimeMillis() - f.lastModified();
				if ( modifiedTime/1000/60/60 > 48 ) { f.delete(); }
				
				// 非指定文件的场合
				if (!f.getName().equals( oldCallId + ".txt")) { continue; }

				logger.debug(" file = " +  f.getName() );
				fis = new FileInputStream(f);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);

				String line = br.readLine();
				
				// 外呼
				String fileName = getCallFileName();
				line = line.replace(oldCallId+"", newCallId+"");
				line = line.replace(line.split("~")[1], phone);

				logger.info("文件名称： " + fileName + "[角色外呼]");
				logger.info("文件内容" + line + "[角色外呼]");

				// 向外呼目录写入外呼文件，等待外呼
				FileWriter writer = new FileWriter(callPath + "/" + callto + "/" + fileName, false);
				writer.write( line );
				writer.close();
				
				// 重写外呼控制文件
				writer = new FileWriter(callPath + "/callCtrl/" + newCallId + ".txt", false);
				writer.write( line );
				writer.close();
				
				fis.close();
				f.delete();

			}
		} catch ( Exception e ) {
			logger.error("", e);
		} finally {
			try { if ( null != br ) br.close(); } catch ( Exception e ) {}
			try { if ( null != isr ) isr.close(); } catch ( Exception e ) {}
			try { if ( null != fis ) fis.close(); } catch ( Exception e ) {}		
		}
	}
}

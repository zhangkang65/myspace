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
 * ����ļ�������
 * ʵ�ֺϲ�����ȹ���
 * @author huangzh
 *
 */
public class CallFileManager extends Thread {

	/** ϵͳ��־ */
	private Log logger = LogFactory.getLog( CallFileManager.class );
	
	/** �������·�� */
	private String callPath;
	
	/** ����ļ����·�� */
	private String callto;
	
	/** ������� */
	private final String CALL_MESSAGE = "����ǰ��#count#���澯��Ҫ������������ĸ澯���š�";
	
	/** ��������ļ��� */
	private int FILE_COUNT = 1;
	
	private final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	/** ������������ */
	private static long CALL_ID  = 0L;
	
	private static final byte[] SYNC_FLAG = new byte[1];
	
	/**
	 * ȡ�����ID�������ʱ���ظ���Ӱ������ӿ�
	 * @return
	 */
	public long[] getCallId() {
		// �����ظ���CALL_ID
		synchronized(SYNC_FLAG){
			CALL_ID = CALL_ID + 3000L;
			// ʹ��400�κ����¼���
			if (CALL_ID > 300 * 3000L) { CALL_ID = 0L; }
		}
		long callId = System.currentTimeMillis() + CALL_ID;
		logger.info("�������ID��" + callId + " �� �����������" +  CALL_ID);
		return new long[] {callId, CALL_ID};
	}

	/** �û������� */
	private UserManager userManager ;
	
	public void setUserManager(UserManager  _userManager){
		this.userManager = _userManager;
	}

	//	 Spring�������ݿ�
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * ȡ���������·��
	 * @return �������·��
	 */
	public String getCallPath() {
		return this.callPath;
	}

	public void setCallPath(String _callpath) {
		this.callPath = _callpath;
	}
	
	/**
	 * ȡ������ļ����·��
	 * @return ����ļ����·��
	 */
	public String getCallto() {
		return callto;
	}
	
	public void setCallto( String callto) {
		this.callto = callto;
	}
	
	/** ϵͳ��ʼ�� */
	public void init () {
		File file = new File ( callPath + "/callTemp/" );
		if ( !file.exists() ) { file.mkdir(); }

		file = new File ( callPath + "/allCall/" );
		if ( !file.exists() ) { file.mkdir(); }
		
		// ��ʼ������ļ�����
		this.FILE_COUNT = getFileCount();		
		this.start();
	}
	
	public void run () {
		while ( true ) {
			try {
				sleep (1*60*1000l);
				moveCallFile();
			} catch (Exception e) {
				logger.error("CallFileManager�߳����д���", e);
			}
		}
	}
	
	
	/**
	 *  ����ϲ�
	 * @param phone ����˺���
	 * @param callid ���ID
	 * @return ���ID
	 * 
	 */
	synchronized public long writeCallFile( String phone ) {
		String fileName = phone + ".txt";
		InputStreamReader isr = null;
		FileInputStream  fis = null;
		BufferedReader br = null;
		int count = 0;
		long callid = 0;
		// callId�뵱ǰϵͳʱ�䣬ʱ��
		long intervel = 0;
		try {
			
			// �ļ�����
			File file = new File ( callPath + "/callTemp/" + fileName );	
			
			// �ļ����ڵĳ��ϣ���ȡ�ļ�������ϲ�����
			if ( file.exists() == true ) {
				fis = new FileInputStream(file);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);
				String line = br.readLine();
				logger.debug(" line = " + line );
				if ( line != null ) {
					// ���ID��Ϊͬһ��
					callid = Long.parseLong ( line.split("~")[0] );
					count = Integer.parseInt( line.split("��")[1].split("~")[0] );
					intervel = Long.parseLong ( line.split("~")[3] );
				} else {
					long [] call = getCallId();
					callid = call[0];
					intervel=call[1];
				}
			} else {
				// �ļ������ڵĳ��ϣ������ļ�
				file.createNewFile();
				long [] call = getCallId();
				callid = call[0];
				intervel=call[1];
			}
			
			FileWriter writer = new FileWriter(file, false);			
			// �ϲ�������һ
			writer.write( callid + "~" + phone + "~" + CALL_MESSAGE + (count+1) + "~" + intervel );
			logger.info("�ϲ������ " +  callid + "~" + phone + "~" + CALL_MESSAGE + (count+1) +"~" + intervel );
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
	 * ָ��Ŀ¼�µ��ļ��Ƿ����
	 * @param name �ļ�����
	 * @param dir Ŀ¼
	 * @return
	 */
	public boolean fileExists( String name, String dir ) {
		String fileName = name + ".txt";

		try {			
			// �ļ�����
			File file = new File ( callPath + "/" + dir + "/" + fileName );	
			return file.exists();

		} catch (Exception e) {
			logger.error("", e);
		}
		return true;
	} 
	
	/**
	 * ���������ڣ����ϲ�������ļ�д������Ŀ¼��
	 *
	 */
	public void moveCallFile() {
		InputStreamReader isr = null;
		FileInputStream  fis = null;
		BufferedReader br = null;
		try {
			// �ļ�����
			File file = new File ( callPath + "/callTemp" );	
			// �ļ������ڵĳ��ϣ�������
			if ( file.exists() == false ) { return; }
			
			File[] files = file.listFiles();
			for (File f : files) {
				 String phone = f.getName().split("\\.")[0];
				// ��TXT�ļ��ĳ���
				if (!f.getName().endsWith(".txt")) {
					continue;
				}
				logger.debug(" file = " +  f.getName() );
				fis = new FileInputStream(f);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);

				//callid + "~" + phone + "~" + CALL_MESSAGE + (count) + "~" + intervel
				String line = br.readLine();
				// �ļ���ʽ����ȷ�ĳ���
				if (line == null || line.split("~").length < 4 ) { 
					logger.info(" file = " +  f.getName() + " ��ʽ����ȷ����ɾ����" );
					f.deleteOnExit();
					continue; 
				}
				
				
				long callid = Long.parseLong(line.split("~")[0]);
				long intervel = Long.parseLong(line.split("~")[3]);
				logger.debug("��ǰϵͳʱ�䣺 " + System.currentTimeMillis() + " callid�� " + callid + " intervel�� " + intervel);
				// �������δ���ģ�������
				if ( (System.currentTimeMillis() - (callid-intervel)) < PhoneConfigManager.TIME_RUN*60*1000 ) {
					continue;
				}
				// ���
				String fileName = getCallFileName();
	
				logger.info("�� " + phone + " �����Զ�������Զ�����ļ����ƣ� " + fileName);
				logger.info("�ļ�����" + line);


				line = line.replace("#count#", line.split("��")[1].split("~")[0]);
				
				// �����Ŀ¼д������ļ����ȴ����
				FileWriter writer = new FileWriter(callPath + "/" + callto + "/" + fileName, false);
				writer.write( line.split("��")[0] );
				writer.close();
				
				//���������Ŀ¼�У�д����ļ���
				writer = new FileWriter(callPath + "/callCtrl/" + callid + ".txt", false);
				writer.write( line.split("��")[0] );
				writer.close();
				

				//�򱸷�Ŀ¼�У�д����ļ���
				writer = new FileWriter(callPath + "/allCall/" + fileName + ".txt", false);
				writer.write( line.split("��")[0] );
				writer.close();
				
				fis.close();
				// ɾ�������ϲ�����ļ�
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
	 * ������ƣ�������ļ����������Ŀ¼�Ƶ������Ŀ¼��
	 * @param callId ���ID(�ļ�����)
	 */
	public void moveCtrlFile( long oldCallId, long newCallId) {
		InputStreamReader isr = null;
		FileInputStream  fis = null;
		BufferedReader br = null;
		try {
			// �ļ�����
			File file = new File ( callPath + "/callCtrl" );	
			// �ļ������ڵĳ��ϣ�������
			if ( file.exists() == false ) { return; }
			
			File[] files = file.listFiles();
			for (File f : files) {
				
				// ɾ��2��ǰ����������ļ�
				long modifiedTime = System.currentTimeMillis() - f.lastModified();
				if ( modifiedTime/1000/60/60 > 48 ) { 
					f.delete(); 
					continue;
				}
				
				// ��ָ���ļ��ĳ���
				if (!f.getName().equals( oldCallId + ".txt")) { continue; }

				

				logger.debug(" file = " +  f.getName() );
				fis = new FileInputStream(f);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);

				String line = br.readLine();
				
				// ���
				String fileName = getCallFileName();
				line = line.replace(oldCallId+"", newCallId+"");
				logger.info("�Զ�����ļ����ƣ� " + fileName + "[�������]");
				logger.info("�ļ�����" + line + "[�������]");

				// �����Ŀ¼д������ļ����ȴ����
				FileWriter writer = new FileWriter(callPath + "/" + callto + "/" + fileName, false);
				writer.write( line );
				writer.close();
				
				writer = new FileWriter(callPath + "/callCtrl/" + newCallId + ".txt", false);
				writer.write( line );
				writer.close();
				

				//�򱸷�Ŀ¼�У�д����ļ���
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
	 * ȡ�õ�������澯����
	 * @return ��������澯����
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
				logger.info(" ȡ�õ�������澯���� : " + callCount );
			}
		} catch (Exception e) {
			logger.error("getCallCount", e);
		} finally {
			BeanUtil.closeResource(rs, ps, conn);
		}
		return callCount;
	}
	
	/**
	 * ���õ�������ļ�����
	 * @param ��������ļ�����
	 */
	public void setFileCount( int count ) {
		Connection conn = null;
		PreparedStatement ps = null;
		String date = df.format(Calendar.getInstance().getTime());
		String sql = " UPDATE TF_TT_ALARM_CALLCOUNT SET NUM = ? WHERE TO_CHAR(CTIME,'YYYYMMDD')= '" + date + "'";
		logger.info(" ���� " + date + " ����ļ����� : " + count );
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt( 1, count );
			ps.executeUpdate();
			this.FILE_COUNT = count;
		} catch (Exception e) {
			logger.error("���õ�������ļ�����", e);
		} finally {
			BeanUtil.closeResource( ps, conn);
		}
	}
	
	/**
	 * ȡ�õ�������ļ�����
	 * @return ��������ļ�����
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
			logger.error("���õ�������澯����", e);
		} finally {
			BeanUtil.closeResource( ps, conn);
		}
		logger.info(" ȡ�� " + date + " ����ļ����� : " + this.FILE_COUNT );
		return this.FILE_COUNT;
	}

	/**
	 * ��ȡʣ�µ��������
	 * @return ʣ�µ��������
	 */
	public int getLastCallCount() {
		return Call.callTotal - getCallCount();
	}
	
	/**
	 * дֱ������ļ�
	 * @param filePath �ļ�ȫ·������
	 */
	public void writeUserCallFile( String phone, long  callid ) {

		logger.info(" callUserFile = " +  phone );
		FileWriter writer = null;
		try {
			//			 ���
			String fileName = getCallFileName();

			logger.info("�� " + phone + " �����Զ�������Զ�����ļ����ƣ� " +callPath + "/" + callto + "/"  + fileName);
			// �ļ�����
			File file = new File ( callPath  + "/" + callto + "/" + fileName );
			// �����Ŀ¼д������ļ����ȴ����
			writer = new FileWriter(file);
			writer.write( callid + "~" + phone + "~" + "���и澯��Ҫ������������ĸ澯���š�" );
			writer.close();

		} catch ( Exception e ) {
			logger.error(" callUserFile ", e);
		} finally {
			try { if ( null != writer ) writer.close(); } catch ( Exception e ) {}		
		}
	} 
	
	/***
	 * ��ȡ����ļ�����
	 * @return
	 */
	synchronized public String getCallFileName() {
		
		String fileName = "00000".substring(0, 4 - Integer.toString(FILE_COUNT).length()) + ++FILE_COUNT;

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		fileName = "bomc_" + df.format(c.getTime()) +  fileName + ".txt";

		// ��������ļ�����
		setFileCount( FILE_COUNT );
		return fileName;
	} 
	
	public void deleteCallCtrlFile( long callId ) {
		try {
			// �ļ�����
			File file = new File ( callPath + "/callCtrl/" + callId + ".txt" );	
			// �ļ������ڵĳ��ϣ�������
			if ( file.exists() == false ) { return; }
			
			file.delete();

		} catch ( Exception e ) {
			logger.error("", e);
		}
	}
	
	/**
	 *  ��������� �����ϲ���
	 * @param userId ����˺���
	 * @param msg �����Ϣ
	 * @param alarmId �澯ID
	 * @return ���ID 0 ���ʧ��
	 * 
	 */
	synchronized public long writeCallFile(String userId, String msg, String alarmId) {

		String fileName = getCallFileName();

		long call [] = getCallId();
		long callId = call[0];
		try {

			User user = userManager.getUser(userId);

			if (user == null || user.getMobile() == null) {
				logger.info("��" + user + "����澯ʧ�ܣ���Ϊ��û���ֻ���");
				return 0;
			}

			// �ļ�����
			File file = new File(callPath + "/call/" + fileName);

			FileWriter writer = new FileWriter(file, false);
			// �ϲ�������һ
			writer.write(callId + "~" + user.getMobile() + "~" + msg);
			logger.info("����������� " + callId + "~" + user.getMobile() + "~" + msg);
			writer.close();
			
			addcallRecordToDB(alarmId, callId, userId, user.getMobile());

		} catch (Exception e) {
			logger.error("writeCallFile", e);
			callId = 0;
		}
		return callId;
	}
	
	/**
	 * ���Զ�������в���һ����¼
	 * @param alarmId �澯ID
	 * @param callId ���ID
	 * @param userId ����û�ID
	 * @param mobile ����û�mobile
	 * @return 0 ʧ��
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
	 * ������ƣ�������ļ����������Ŀ¼�Ƶ������Ŀ¼��,ͬʱ������������ļ�����
	 * @param oldCallId  ԭ���ID(�ļ�����)
	 * @param newCallId �����ID
	 * @param phone ���������
	 */
	public void moveCallFile( long oldCallId, long newCallId, String phone ) {
		InputStreamReader isr = null;
		FileInputStream  fis = null;
		BufferedReader br = null;
		try {
			// �ļ�����
			File file = new File ( callPath + "/callCtrl" );	
			// �ļ������ڵĳ��ϣ�������
			if ( file.exists() == false ) { return; }
			
			File[] files = file.listFiles();
			for (File f : files) {
				
				// ɾ��2��ǰ����������ļ�
				long modifiedTime = System.currentTimeMillis() - f.lastModified();
				if ( modifiedTime/1000/60/60 > 48 ) { f.delete(); }
				
				// ��ָ���ļ��ĳ���
				if (!f.getName().equals( oldCallId + ".txt")) { continue; }

				logger.debug(" file = " +  f.getName() );
				fis = new FileInputStream(f);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);

				String line = br.readLine();
				
				// ���
				String fileName = getCallFileName();
				line = line.replace(oldCallId+"", newCallId+"");
				line = line.replace(line.split("~")[1], phone);

				logger.info("�ļ����ƣ� " + fileName + "[��ɫ���]");
				logger.info("�ļ�����" + line + "[��ɫ���]");

				// �����Ŀ¼д������ļ����ȴ����
				FileWriter writer = new FileWriter(callPath + "/" + callto + "/" + fileName, false);
				writer.write( line );
				writer.close();
				
				// ��д��������ļ�
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

/*
 * �������� 2005-11-15
 *�� �� Java �� ������ʽ �� ����ģ��
 */
package com.linkage.toptea.sysmgr.fm.assistant;

import java.sql.Connection;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

/**
 * @author �˻�
 * �� ����ģ��
 */    
public class UserTransaction  implements javax.transaction.UserTransaction {
   private Connection con=null;
   public UserTransaction(Connection con){
      this.con=con;	
   }
	/* ���� Javadoc��
	 * @see javax.transaction.UserTransaction#begin()
	 */
	public void begin() throws NotSupportedException, SystemException {
		try{
			con.setAutoCommit(false);
		}catch(Exception ex){
			throw new SystemException("SQLException:"+ex.getMessage());
		}
	}

	/* ���� Javadoc��
	 * @see javax.transaction.UserTransaction#commit()
	 */
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
		// TODO �Զ���ɷ������
		try{
			con.commit();
		}catch(Exception ex){
			throw new SystemException("SQLException:"+ex.getMessage());
		}
		
	}

	/* ���� Javadoc��
	 * @see javax.transaction.UserTransaction#rollback()
	 */
	public void rollback() throws IllegalStateException, SecurityException, SystemException {
		// TODO �Զ���ɷ������
		try{
			con.rollback();
		}catch(Exception ex){
			throw new SystemException("SQLException:"+ex.getMessage());
		}
		
	}

	/* ���� Javadoc��
	 * @see javax.transaction.UserTransaction#setRollbackOnly()
	 */
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		// TODO �Զ���ɷ������
	}

	/* ���� Javadoc��
	 * @see javax.transaction.UserTransaction#getStatus()
	 */
	public int getStatus() throws SystemException {
		// TODO �Զ���ɷ������
		
		return 0;
	}

	/* ���� Javadoc��
	 * @see javax.transaction.UserTransaction#setTransactionTimeout(int)
	 */
	public void setTransactionTimeout(int arg0) throws SystemException {
		// TODO �Զ���ɷ������
		
	}

}

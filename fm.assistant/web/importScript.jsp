<%@page contentType="text/html; charset=gb2312" language="java"%>
<%@page import="com.linkage.toptea.context.*"%>
<%@page import="java.util.*,com.linkage.toptea.util.BeanUtil"%>
<%@page import="java.sql.*,javax.sql.DataSource"%>

<!--       ���棺�˽ű�ֻ������һ�Σ�����������     -->
<%
  addRule();
  out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
%>

<%!
DataSource dataSource = (DataSource) Context.getContext().getAttribute("dataSource");

public void addRule ( ) {
    Connection con = null;
    PreparedStatement ps = null;
    

    String sql = " insert into TF_TT_ALARM_CALLUSER ( USERA,USERB, OP_USER, TIME, ANAME, BNAME ) values ( ?, ?, 'root', sysdate, ?, ? )";


    try {
    	
    	 Map<String, String> map = getResponsibles ();
    	 
        // ȡ��DB����
        con = dataSource.getConnection();
        con.setAutoCommit(false);
        // Ԥ����SQL
        ps = con.prepareStatement(sql);

        for ( Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            String value = map.get(key);
            if (key==null || value==null ) { continue; }
            int index = 1;
           
            //���ȼ���Ĭ���½��Ĺ������ȼ����
            ps.setString(index++, key);
            ps.setString(index++, key);
            ps.setString(index++, value);
            ps.setString(index++, value);
          
            ps.addBatch() ;
            
        }

        // ִ��
        ps.executeBatch() ;
        con.commit();


    } catch ( Exception e ) {
        e.printStackTrace();
    } finally {
    	BeanUtil.closeResource( ps, con);
    }


}


/** ȡ�ø澯���������й��� */
public Map<String, String> getResponsibles () {
	Map<String, String> result = new HashMap<String, String>();
	
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = " select distinct (a.responsible) id, c.name "
    	 + " from (select distinct (r.responsible1) responsible"
    	 + " from tf_tt_alarm_responsible r"
    	 + " where r.responsible1 is not null"
    	 + " union all"
    	 + " select distinct (r.responsible2) responsible"
    	 + " from tf_tt_alarm_responsible r"
    	 + " where r.responsible2 is not null"
    	 + " union all"
    	 + " select distinct (r.responsible3) responsible"
    	 + " from tf_tt_alarm_responsible r"
    	 + " where r.responsible3 is not null"
    	 + " union all"
    	 + " select distinct (r.responsible4) responsible"
    	 + " from tf_tt_alarm_responsible r"
    	 + " where r.responsible4 is not null"
    	 + " union all"
    	 + " select distinct (r.responsible5) responsible"
    	 + " from tf_tt_alarm_responsible r"
    	 + " where r.responsible5 is not null"
    	 + " union all"
    	 + " select distinct (r.responsible6) responsible"
    	 + " from tf_tt_alarm_responsible r"
    	 + " where r.responsible6 is not null"
    	 + " union all"
    	 + " select distinct (r.responsible7) responsible"
    	 + " from tf_tt_alarm_responsible r"
    	 + " where r.responsible7 is not null"
    	 + " union all"
    	 + " select distinct (r.responsible8) responsible"
    	 + " from tf_tt_alarm_responsible r"
    	 + " where r.responsible8 is not null) a,"
    	 + " uportal.cas_user c"
    	 + " where a.responsible = c.id ";

    try {
        // ȡ��DB����
        con = dataSource.getConnection();
        // Ԥ����SQL
        ps = con.prepareStatement( sql );

        // ִ��
        rs = ps.executeQuery();
        while ( rs.next() ) {
           result.put( rs.getString("ID"), rs.getString("NAME") );
        }
    } catch ( Exception e ) {
        e.printStackTrace();
    } finally {
    	BeanUtil.closeResource(rs, ps, con);
    }
    return result;
}


%>
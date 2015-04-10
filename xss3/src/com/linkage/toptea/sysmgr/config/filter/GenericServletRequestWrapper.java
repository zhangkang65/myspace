package com.linkage.toptea.sysmgr.config.filter;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;





public class GenericServletRequestWrapper extends HttpServletRequestWrapper {
	
	private HttpServletRequest  orgRequest=null;
	private boolean hasEncode=false; 

	public GenericServletRequestWrapper(HttpServletRequest request) {
		super(request);
		this.orgRequest=request;
	}

	
	// ����Ҫ��ǿ���� ���и���  
    @Override  
    public Map getParameterMap() {  
        // �Ȼ������ʽ  
        String method =orgRequest.getMethod();  
        if (method.equalsIgnoreCase("post")) {  
            // post����  
            try {  
                // ����post����  
            	orgRequest.setCharacterEncoding("utf-8");  
                return orgRequest.getParameterMap();  
            } catch (UnsupportedEncodingException e) {  
                e.printStackTrace();  
            }  
        } else if (method.equalsIgnoreCase("get")) {  
            // get����  
            Map<String, String[]> parameterMap = orgRequest.getParameterMap();  
            if (!hasEncode) { // ȷ��get�ֶ������߼�ֻ����һ��  
                for (String parameterName : parameterMap.keySet()) { 
                    String[] values = parameterMap.get(parameterName);  
                    if (values != null) {  
                        for (int i = 0; i < values.length; i++) {  
                            try {  
                                // ����get����  
                                values[i] = new String(values[i].getBytes("ISO-8859-1"), "utf-8");  
                            } catch (UnsupportedEncodingException e) {  
                                e.printStackTrace();  
                            }  
                        }  
                    }  
                }  
                hasEncode = true;  
            }  
            return parameterMap;  
        }  
        return super.getParameterMap();  
    }  
    
    
    
    
    @Override  
    public String getParameter(String name) {  
        Map<String, String[]> parameterMap = getParameterMap();  
        String[] values = parameterMap.get(name);  
        if (values == null) {  
            return null;  
        }  
        return values[0]; // ȡ�ز����ĵ�һ��ֵ  
    }  
    
    
    
    
    @Override  
    public String[] getParameterValues(String name) {  
        Map<String, String[]> parameterMap = getParameterMap();  
        String[] values = parameterMap.get(name);  
        return values;  
    }  
	

}

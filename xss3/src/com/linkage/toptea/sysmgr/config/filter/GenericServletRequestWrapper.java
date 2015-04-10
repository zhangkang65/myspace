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

	
	// 对需要增强方法 进行覆盖  
    @Override  
    public Map getParameterMap() {  
        // 先获得请求方式  
        String method =orgRequest.getMethod();  
        if (method.equalsIgnoreCase("post")) {  
            // post请求  
            try {  
                // 处理post乱码  
            	orgRequest.setCharacterEncoding("utf-8");  
                return orgRequest.getParameterMap();  
            } catch (UnsupportedEncodingException e) {  
                e.printStackTrace();  
            }  
        } else if (method.equalsIgnoreCase("get")) {  
            // get请求  
            Map<String, String[]> parameterMap = orgRequest.getParameterMap();  
            if (!hasEncode) { // 确保get手动编码逻辑只运行一次  
                for (String parameterName : parameterMap.keySet()) { 
                    String[] values = parameterMap.get(parameterName);  
                    if (values != null) {  
                        for (int i = 0; i < values.length; i++) {  
                            try {  
                                // 处理get乱码  
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
        return values[0]; // 取回参数的第一个值  
    }  
    
    
    
    
    @Override  
    public String[] getParameterValues(String name) {  
        Map<String, String[]> parameterMap = getParameterMap();  
        String[] values = parameterMap.get(name);  
        return values;  
    }  
	

}

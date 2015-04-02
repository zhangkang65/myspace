package com.linkage.toptea.sysmgr.ngboss.control;
import java.io.IOException;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import com.google.gson.Gson;
import com.linkage.toptea.sysmgr.ngboss.bean.BCNUpdateSync;
import com.linkage.toptea.sysmgr.ngboss.bean.BCNUpdateSyncR;
import com.linkage.toptea.sysmgr.ngboss.bean.CSNUpdateSync;
import com.linkage.toptea.sysmgr.ngboss.bean.OCNUpdateSync;
import com.linkage.toptea.sysmgr.ngboss.bean.OCNUpdateSyncR;
import com.linkage.toptea.sysmgr.ngboss.utils.NgStrValidate;


@Controller
@RequestMapping("/manager")
public class NGBossManager {

	private static String server = null;

	public void setSocket(String ip, int port) {
		server = "http://" + ip + ":" + port;
	}

	public String getSocket() {
		return server;
	}

	public String parseResult(String result) {
		return result;
	}

	/**
	 * 3.2.1.1 应用状态查询接口
	 * 
	 * @param type
	 * 1: 按主机\2: 按集群\3、按节点
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/resInsStatusQuery")
	public String resInsStatusQuery(String type, String[] resList)  throws Exception {
		String body = "{'type':'"+type+"','resList':[";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < resList.length; i++) {
			if(resList[i]!=""){  //数组里面的内容不为空  
				if (i!=0) {
					sb.append(",");
				}
				sb.append("{'code':'" + resList[i] + "'}");
			}	
		}

		body = body + sb.toString() + "]}";

		Gson gson = new Gson();
		String obj = (String) gson.fromJson(body, Object.class);
		System.out.println(obj);

		String url = server + "/ResourceSvc/resInsStatusQuery";
		ClientResource client = new ClientResource(url);
		Representation representation = client.post(body);

		return body;
	}

	/**
	 * 3.2.1.2 应用启停接口
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/resInsOperate")
	public String resInsOperate(String actionType, String type, String[] resList)throws IOException {
		
		String body = "{'actionType':'" + actionType + "','type':'" + type
				+ "','resList':[";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < resList.length; i++) {
			
			if(resList[i]!=""){  //数组里面的内容不为空  
				if (i!=0) {
					sb.append(",");
				}
				sb.append("{'code':'" + resList[i] + "'}");
			}	
		}
		body = body + sb.toString() + "]}";
		

		Gson gson = new Gson();
		String obj = (String) gson.fromJson(body, Object.class);
		System.out.println(obj);

		String url = server + "/ResourceSvc/resInsOperate";

		ClientResource client = new ClientResource(url);
		Representation representation = client.post(body);

		return body;
	}

	/**
	 * 3.2.1.3 集群资源同步接口
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/clusterInsSync")
	public String clusterInsSync(String startTime, String endTime)
			throws IOException {

		String body = "{'startTime':'" + startTime + "','endTime':'" + endTime
				+ "'}";

		String url = server + "/ResourceSvc/clusterInsSync";

		Gson gson = new Gson();
		String obj = (String) gson.fromJson(body, Object.class);
		System.out.println(obj);

		ClientResource client = new ClientResource(url);
		Representation representation = client.post(body);

		return representation.getText();
	}

	/**
	 * 3.2.1.4 节点资源同步接口
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/resInsSync")
	public String resInsSync(String startTime, String endTime)
			throws IOException {
		String url = server + "/ResourceSvc/resInsSync";
		String body = "{'startTime':'" + startTime + "','endTime':'" + endTime
				+ "'}";

		Gson gson = new Gson();
		String obj = (String) gson.fromJson(body, Object.class);
		System.out.println(obj);
		
		ClientResource client = new ClientResource(url);
		Representation representation = client.post(body);

		return representation.getText();
	}

	/**
	 * 3.2.1.5 路由查询接口
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/routeQuerySync")
	public String routeQuerySync() throws IOException {
		String url = server + "/RouteSvc/RouteQuerySync";
		
		System.out.println("no  body routeQuerySync ");
		
		ClientResource client = new ClientResource(url);
		Representation representation = client.post(null); // 没有body 体
		return representation.getText();
	}

	/**
	 * 3.2.1.6 路由修改接口
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/routeUpdateSync")
	public String routeUpdateSync(String express, String id, String targetId,
			String status) throws IOException {

		if ("".equals(express) || null == express) {
			express = "";
		}
		if ("".equals(id) || null == id) {
			id = "";
		}
		if ("".equals(targetId) || targetId == null) {
			targetId = "";
		}
		if ("".equals(status) || status == null) {
			status = "";
		}

		String body = "{'express':'" + express + "','id':'" + id
				+ "','targetId':'" + targetId + "','status':'" + status + "'}";
		
		Gson gson = new Gson();
		String obj = (String) gson.fromJson(body, Object.class);
		System.out.println(obj);

		String url = server + "/RouteSvc/RouteUpdateSync";
		ClientResource client = new ClientResource(url);
		Representation representation = client.post(body);

		return representation.getText();
	}

	/**
	 * 3.2.1.7 BCN节点运行时参数查询接口
	 * 
	 * @param args
	 * @throws IOException
	 */
	@RequestMapping("/bCNQuerySync")
	public String bCNQuerySync(String resInsCode) throws IOException {

		String body = "{'resInsCode':'" + resInsCode + "'}";

		Gson gson = new Gson();
		String obj = (String) gson.fromJson(body, Object.class);
		System.out.println(obj);

		String url = server + "/BCNSvc/BCNQuerySync";

		ClientResource client = new ClientResource(url);
		Representation representation = client.post(body);

		return representation.getText();
	}

	/**
	 * 3.2.1.8 BCN节点运行时参数修改接口
	 * 
	 * @param args
	 * @throws IOException
	 */
	@RequestMapping("/bCNUpdateSync")
	public String bCNUpdateSync(BCNUpdateSync  bcnUpdateSync) throws IOException {

		
		bcnUpdateSync.setBatchNum(NgStrValidate.nullToStr(bcnUpdateSync.getBatchNum()));
		bcnUpdateSync.setDmcServiceThreadpoolSize(NgStrValidate.nullToStr(bcnUpdateSync.getDmcServiceThreadpoolSize()));
		bcnUpdateSync.setIndicatorIsMonitor(NgStrValidate.nullToStr(bcnUpdateSync.getIndicatorIsMonitor()));
		bcnUpdateSync.setLbPackSize(NgStrValidate.nullToStr(bcnUpdateSync.getLbPackSize()));
		bcnUpdateSync.setRpcInterfaceConnections(NgStrValidate.nullToStr(bcnUpdateSync.getRpcInterfaceConnections()));
		bcnUpdateSync.setRpcSendFailTime(NgStrValidate.nullToStr(bcnUpdateSync.getRpcSendFailTime()));
		bcnUpdateSync.setRpcTimeOut(NgStrValidate.nullToStr(bcnUpdateSync.getRpcTimeOut()));
		bcnUpdateSync.setWorkthreadNum(NgStrValidate.nullToStr(bcnUpdateSync.getWorkthreadNum()));
		bcnUpdateSync.setWorkthreadTimeout(NgStrValidate.nullToStr(bcnUpdateSync.getWorkthreadTimeout()));
		
		String body = "{'batchNum':'" + bcnUpdateSync.getBatchNum()//
				+ "','dmcServiceThreadpoolSize':'" + bcnUpdateSync.getDmcServiceThreadpoolSize()//
				+ "','indicatorIsMonitor':'" + bcnUpdateSync.getIndicatorIsMonitor()//
				+ "','lbPackSize':'" + bcnUpdateSync.getLbPackSize()// 
				+ "','rpcTimeOut':'"+ bcnUpdateSync.getRpcTimeOut() //
				+ "','rpcInterfaceConnections':'"+ bcnUpdateSync.getRpcInterfaceConnections() //
				+ "','workthreadNum':'"+ bcnUpdateSync.getWorkthreadNum() //
				+ "','workthreadTimeout':'" + bcnUpdateSync.getWorkthreadTimeout()//
				+ "','RpcSendFailTime':'" + bcnUpdateSync.getRpcSendFailTime()//
				+ "','resInsCode':'" + bcnUpdateSync.getResInsCode() + "'}";

		Gson gson = new Gson();
		BCNUpdateSync obj = (BCNUpdateSync) gson.fromJson(body, BCNUpdateSync.class);
		System.out.println(obj);

		String url = server + "/BCNSvc/BCNUpdateSync";
		ClientResource client = new ClientResource(url);
		Representation representation = client.post(body);

		return representation.getText();
	}

	/**
	 * 3.2.1.9 BCN集群运行时参数查询接口
	 * 
	 * @param args
	 *     TODO 方法同 3.2.1.7
	 */

	/**
	 * 3.2.1.10 BCN集群运行时参数修改接口
	 * 
	 * @param args
	 *            
	 */
	@RequestMapping("/bCNUpdateSyncR")
	public String bCNUpdateSync(BCNUpdateSyncR  bcnUpdateSyncR) throws IOException {

		bcnUpdateSyncR.setBatchNum(NgStrValidate.nullToStr(bcnUpdateSyncR.getBatchNum()));
		bcnUpdateSyncR.setControlSwitch(NgStrValidate.nullToStr(bcnUpdateSyncR.getControlSwitch()));
		bcnUpdateSyncR.setDmcConsumerTimeOut(NgStrValidate.nullToStr(bcnUpdateSyncR.getDmcConsumerTimeOut()));
		bcnUpdateSyncR.setDmcServicePort(NgStrValidate.nullToStr(bcnUpdateSyncR.getDmcServicePort()));
		bcnUpdateSyncR.setDmcServiceThreadpoolSize(NgStrValidate.nullToStr(bcnUpdateSyncR.getDmcServiceThreadpoolSize()));
		bcnUpdateSyncR.setExportprocessTimeout(NgStrValidate.nullToStr(bcnUpdateSyncR.getExportprocessTimeout()));
		bcnUpdateSyncR.setOutputMsgCount(NgStrValidate.nullToStr(bcnUpdateSyncR.getOutputMsgCount()));
		bcnUpdateSyncR.setRpcInterfaceConnections(NgStrValidate.nullToStr(bcnUpdateSyncR.getRpcInterfaceConnections()));
		bcnUpdateSyncR.setWorkthreadNum(NgStrValidate.nullToStr(bcnUpdateSyncR.getWorkthreadNum()));
		bcnUpdateSyncR.setWorkthreadTimeout(NgStrValidate.nullToStr(bcnUpdateSyncR.getWorkthreadTimeout()));

		String body = "{"
				+"'batchNum':'" + bcnUpdateSyncR.getBatchNum()//
				+ "','dmcServiceThreadpoolSize':'" + bcnUpdateSyncR.getDmcConsumerTimeOut()//
				+ "','controlSwitch':'" + bcnUpdateSyncR.getControlSwitch()//
				+ "','dmcServicePort':'" + bcnUpdateSyncR.getDmcServicePort() //
				+ "','dmcConsumerTimeOut':'"+ bcnUpdateSyncR.getDmcConsumerTimeOut() //
				+ "','rpcInterfaceConnections':'"+ bcnUpdateSyncR.getRpcInterfaceConnections() //
				+ "','workthreadNum':'"+ bcnUpdateSyncR.getWorkthreadNum() //
				+ "','workthreadTimeout':'" + bcnUpdateSyncR.getWorkthreadTimeout()//
				+ "','exportprocessTimeout':'" + bcnUpdateSyncR.getExportprocessTimeout()//
				+ "','outputMsgCount':'" + bcnUpdateSyncR.getOutputMsgCount()//
				+ "','resInsCode':'" + bcnUpdateSyncR.getResInsCode()//
				+ "'}";

		Gson gson = new Gson();
		BCNUpdateSyncR obj = (BCNUpdateSyncR) gson.fromJson(body, BCNUpdateSyncR.class);
		System.out.println(obj);

		String url = server + "/BCNSvc/BCNUpdateSync";
		ClientResource client = new ClientResource(url);
		Representation representation = client.post(body);

		return representation.getText();
	}
	
	
	
	/**
	 * 3.2.1.11 CSN 节点运行时参数查询接口
	 * 
	 * @param args
	 * @throws IOException
	 */
	@RequestMapping("/cSNQuerySync")
	public String cSNQuerySync(String resInsCode) throws IOException {
		String body = "{'resInsCode':'" + resInsCode + "'}";
		
		Gson gson = new Gson();
		String obj = (String) gson.fromJson(body, Object.class);
		System.out.println(obj);
		
		String url = server + "/CSNSvc/CSNQuerySync";
		ClientResource client = new ClientResource(url);
		Representation representation = client.post(body);
		return representation.getText();
	}

	/**
	 * 3.2.1.12 CSN 节点运行时参数修改接口
	 * 
	 * @param args
	 * @throws IOException 
	 */
	@RequestMapping("/cSNUpdateSync")
	public String cSNUpdateSync(@ModelAttribute("cSNUpdateSync") CSNUpdateSync  cSNUpdateSync) throws IOException {

		if ("".equals(cSNUpdateSync.getNodeRunState()) || null == cSNUpdateSync.getNodeRunState())
			cSNUpdateSync.setNodeRunState("");
		if ("".equals(cSNUpdateSync.getIndicatorIsMonitor())|| null == cSNUpdateSync.getIndicatorIsMonitor())
			cSNUpdateSync.setIndicatorIsMonitor("");
		if ("".equals(cSNUpdateSync.getIndicatorIsMonitor()) || cSNUpdateSync.getIndicatorIsMonitor() == null)
			cSNUpdateSync.setIndicatorIsMonitor("");
		if ("".equals(cSNUpdateSync.getProviderIothreads()) || cSNUpdateSync.getProviderIothreads() == null)
			cSNUpdateSync.setProviderIothreads("");
		if ("".equals(cSNUpdateSync.getProviderThreads()) ||cSNUpdateSync.getProviderThreads() == null)
			cSNUpdateSync.setProviderThreads("");
		if ("".equals(cSNUpdateSync.getProviderConnections())
				|| cSNUpdateSync.getProviderConnections() == null)
			cSNUpdateSync.setProviderConnections("");
		if ("".equals(cSNUpdateSync.getProviderAccepts()) || cSNUpdateSync.getProviderAccepts() == null)
			cSNUpdateSync.setProviderAccepts("");
		
		
		String body = "{'resInsCode':'" + cSNUpdateSync.getResInsCode() //
				+ "','nodeRunState':'" + cSNUpdateSync.getNodeRunState()//
				+ "','indicatorIsMonitor':'" + cSNUpdateSync.getIndicatorIsMonitor()//
				+ "','providerIothreads':'" + cSNUpdateSync.getProviderIothreads()//
				+ "','providerThreads':'" + cSNUpdateSync.getProviderThreads()//
				+ "','providerConnections':'" + cSNUpdateSync.getProviderConnections()//
				+ "','providerAccepts':'" + cSNUpdateSync.getProviderAccepts()//
				+ "'}";
		
		String url = server + "/CSNSvc/CSNUpdateSync";
		
		Gson gson = new Gson();
		String obj = (String) gson.fromJson(body, Object.class);
		System.out.println(obj);

		ClientResource client = new ClientResource(url);
		Representation representation = client.post(body);

		return representation.getText();

	}

	/**
	 * 3.2.1.13	 OCN节点运行时参数查询接口
	 * @param args
	 * @throws IOException 
	 */
	
	@RequestMapping("/oCNQuerySync")
	public String oCNQuerySync(String resInsCode) throws IOException{
		String body = "{'resInsCode':'" + resInsCode + "'}";
		Gson gson = new Gson();
		String obj = (String) gson.fromJson(body, Object.class);
		System.out.println(obj);
		String url = server + "/OCNSvc/OCNQuerySync";
		ClientResource client = new ClientResource(url);
		Representation representation = client.post(body);

		return representation.getText();
	}
	
	/**
	 * 3.2.1.14	 OCN节点运行时参数修改接口
	 * @param args
	 * @throws IOException 
	 */
	
	@RequestMapping("/oCNUpdateSync")
	public String oCNUpdateSync(OCNUpdateSync  ocnUpdateSync) throws IOException{
		
		if ("".equals(ocnUpdateSync.getIndicatorIsMonitor()) || null == ocnUpdateSync.getIndicatorIsMonitor())
			ocnUpdateSync.setIndicatorIsMonitor("");
		if ("".equals(ocnUpdateSync.getNettyWorkprocessNum())
				|| null == ocnUpdateSync.getNettyWorkprocessNum())
			ocnUpdateSync.setNettyWorkprocessNum("");
		if ("".equals(ocnUpdateSync.getNettyBossprocessNum()) || ocnUpdateSync.getNettyBossprocessNum() == null)
			ocnUpdateSync.setNettyBossprocessNum("");
		if ("".equals(ocnUpdateSync.getNettyBizThreadSize()) || ocnUpdateSync.getNettyBizThreadSize() == null)
			ocnUpdateSync.setNettyBizThreadSize("");
		if ("".equals(ocnUpdateSync.getNettyBizThreadQueueSize()) || ocnUpdateSync.getNettyBizThreadQueueSize() == null)
			ocnUpdateSync.setNettyBizThreadQueueSize("") ;
		if ("".equals(ocnUpdateSync.getNettyBizThreadRetryInterval())
				|| ocnUpdateSync.getNettyBizThreadRetryInterval() == null)
			ocnUpdateSync.setNettyBizThreadRetryInterval("");
		if ("".equals(ocnUpdateSync.getNettyRcvbuf()) || ocnUpdateSync.getNettyRcvbuf() == null)
			ocnUpdateSync.setNettyRcvbuf("");
		if ("".equals(ocnUpdateSync.getNettySndbuf()) ||ocnUpdateSync.getNettySndbuf() == null)
			ocnUpdateSync.setNettySndbuf("");
		
		String body = "{'resInsCode':'" + ocnUpdateSync.getResInsCode()//
				+ "','indicatorIsMonitor':'" + ocnUpdateSync.getIndicatorIsMonitor()//
				+ "','nettyWorkprocessNum':'" + ocnUpdateSync.getNettyWorkprocessNum()//
				+ "','nettyBossprocessNum':'" + ocnUpdateSync.getNettyBossprocessNum()//
				+ "','nettyBizThreadSize':'" + ocnUpdateSync.getNettyBizThreadSize()//
				+ "','nettyBizThreadQueueSize':'" + ocnUpdateSync.getNettyBizThreadQueueSize()//
				+ "','nettyBizThreadRetryInterval':'" + ocnUpdateSync.getNettyBizThreadRetryInterval()//
				+ "','nettyRcvbuf':'" + ocnUpdateSync.getNettyRcvbuf()//
				+ "','nettySndbuf':'" + ocnUpdateSync.getNettySndbuf()//
				+ "'}";
		
		String url = server + "/OCNSvc/OCNUpdateSync";
		
		Gson gson = new Gson();
		String obj = (String) gson.fromJson(body, Object.class);
		System.out.println(obj);

		ClientResource client = new ClientResource(url);
		Representation representation = client.post(body);

		return representation.getText();
	}
	
	
	
	/**
	 * 3.2.1.15	 OCN集群运行时参数查询接口
	 * @param args
		TODO  同   3.2.1.13

	 */
	

	
	/**
	 * 3.2.1.16	 OCN集群运行时参数修改接口
	 * @param args
	 * OCNSvc
	 */
	@RequestMapping("/oCNUpdateSyncR")
	public String oCNUpdateSync(OCNUpdateSyncR ocnUpdateSyncR) throws IOException{
		
		if ("".equals(ocnUpdateSyncR.getControlSwitch()) || null == ocnUpdateSyncR.getControlSwitch())
			ocnUpdateSyncR.setControlSwitch("");
		if ("".equals(ocnUpdateSyncR.getRpcTimeOut())
				|| null == ocnUpdateSyncR.getRpcTimeOut())
			ocnUpdateSyncR.setRpcTimeOut("");;
		if ("".equals(ocnUpdateSyncR.getRpcInterfaceConnections()) || ocnUpdateSyncR.getRpcInterfaceConnections() == null)
			ocnUpdateSyncR.setRpcInterfaceConnections("");
		if ("".equals(ocnUpdateSyncR.getRpcSendFailTime()) || ocnUpdateSyncR.getRpcSendFailTime() == null)
			ocnUpdateSyncR.setRpcSendFailTime("");
		if ("".equals(ocnUpdateSyncR.getEpPartMinute()) || ocnUpdateSyncR.getEpPartMinute() == null)
			ocnUpdateSyncR.setEpPartMinute("");
		if ("".equals(ocnUpdateSyncR.getEpConfigSplitDuration())
				|| ocnUpdateSyncR.getEpConfigSplitDuration() == null)
			ocnUpdateSyncR.setEpConfigSplitDuration("");
		if ("".equals(ocnUpdateSyncR.getRpcTimeOut()) || ocnUpdateSyncR.getRpcTimeOut() == null)
			ocnUpdateSyncR.setRpcTimeOut("");
		
		
		String body = "{'resInsCode':'" + ocnUpdateSyncR.getResInsCode() //
				+ "','controlSwitch':'" +        ocnUpdateSyncR.getControlSwitch()//
				+ "','rpcTimeOut':'" +        ocnUpdateSyncR.getRpcTimeOut()//
				+ "','rpcInterfaceConnections':'" +    ocnUpdateSyncR.getRpcInterfaceConnections()//
				+ "','epPartMinute':'" +         ocnUpdateSyncR.getEpPartMinute()//
				+ "','epConfigSplitDuration':'" +       ocnUpdateSyncR.getEpConfigSplitDuration()//
				+ "','epTimeout':'" +    ocnUpdateSyncR.getEpTimeout()//
				+ "','RpcSendFailTime':'" +        ocnUpdateSyncR.getRpcSendFailTime()//
				+ "'}";
		
		String url = server + "/OCNSvc/OCNUpdateSync";
		
		Gson gson = new Gson();
		OCNUpdateSyncR obj = (OCNUpdateSyncR) gson.fromJson(body, OCNUpdateSyncR.class);
		System.out.println(obj);

		ClientResource client = new ClientResource(url);
		Representation representation = client.post(body);

		return representation.getText();
	}
}

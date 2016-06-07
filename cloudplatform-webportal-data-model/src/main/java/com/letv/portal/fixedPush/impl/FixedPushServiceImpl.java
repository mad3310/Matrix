package com.letv.portal.fixedPush.impl;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.letv.common.exception.ValidateException;
import com.letv.common.result.ApiResultObject;
import com.letv.common.util.HttpClient;
import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.ContainerPush;
import com.letv.portal.model.fixed.FixedPushModel;



/**
 * Program Name: FixedPush <br>
 * Description:  与固资系统交互实现接口
 * @author name: wujun <br>
 * Written Date: 2014年10月14日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@Service("fixedPushService")
public class FixedPushServiceImpl implements IFixedPushService{
	
	
	private final static Logger logger = LoggerFactory.getLogger(FixedPushServiceImpl.class);
	
	@Value("${fixedpush.sn.url}")
	private String fixedPushSnUrl;//查询sn地址
	@Value("${fixedpush.add.url}")
	private String fixedPushAddUrl;//灵枢新增固资推送地址
	@Value("${fixedpush.add.token}")
	private String fixedPushAddToken;//灵枢新增固资推送地址token
	@Value("${fixedpush.del.url}")
	private String fixedPushDelUrl;//灵枢删除固资推送地址
	@Value("${fixedpush.del.token}")
	private String fixedPushDelToken;//灵枢删除固资推送地址token

//	public ApiResultObject createMutilContainerPushFixedInfo(List<ContainerModel> containers){
//		ApiResultObject ret = null;
//		List<ContainerModel> success = new ArrayList<ContainerModel>();
//		for(ContainerModel c:containers) {
//			ret = sendFixedInfo(c.getHostIp(), c.getContainerName(), c.getIpAddr(), "add");
//			if(!ret.getAnalyzeResult()) {//添加失败
//				for (ContainerModel containerModel : success) {//把添加成功的删除
//            		sendFixedInfo(containerModel.getHostIp(), containerModel.getContainerName(), containerModel.getIpAddr(), "delete");
//				}
//				break;
//			} else {
//            	success.add(c);
//            }
//		}
//		return ret;
//	}
//	
//
//	@Override
//	public ApiResultObject deleteMutilContainerPushFixedInfo(List<ContainerModel> containers){
//		ApiResultObject ret = null;
//		List<ContainerModel> success = new ArrayList<ContainerModel>();
//		for(ContainerModel c:containers) {
//			ret = sendFixedInfo(c.getHostIp(), c.getContainerName(), c.getIpAddr(), "delete");
//            if(!ret.getAnalyzeResult()) {//删除失败
//            	for (ContainerModel containerModel : success) {//把删除成功的再添加上去
//            		sendFixedInfo(containerModel.getHostIp(), containerModel.getContainerName(), containerModel.getIpAddr(), "add");
//				}
//            	break;
//            } else {
//            	success.add(c);
//            }
//		}
//		return ret;
//	}
//	
//	
//	public ApiResultObject sendFixedInfo(String hostIp,String name,String ip,String type) {
//		ApiResultObject apiResult = new ApiResultObject();
//		String sn = getSnByHostIp(hostIp);
//		logger.debug("推送固资根据HostIp:[{}}获取sn:[{}]", hostIp, sn);
//		if(StringUtils.isEmpty(sn)) {
//			logger.error("根据hostIp未获取到sn，hostIp:{}", hostIp);
//			apiResult.setAnalyzeResult(false);
//			apiResult.setResult("根据hostIp未获取到sn，hostIp:"+ hostIp);
//			return apiResult;
//		}
//		
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("ip", ip);
//		params.put("name", name);
//		params.put("hostIp", sn);
//		String ret;
//		if("add".equals(type)) {//新增固资
//			params.put("link_token", fixedPushAddToken);
//			ret = HttpClient.get(fixedPushAddUrl, params, 5000, 10000);
//		} else if("delete".equals(type)) {//删除固资
//			params.put("link_token", fixedPushDelToken);
//			ret = HttpClient.get(fixedPushDelUrl, params, 5000, 10000);
//		} else {
//			logger.error("由于type未识别，未推送固资");
//			throw new ValidateException("由于type未识别，未推送固资， type:" + type);
//		}
//		
//		logger.debug("固资推送结果：{}", ret);
//		Map<String, Object> resultMap = JSON.parseObject(ret, Map.class);
//		if(null == resultMap) {
//			apiResult.setAnalyzeResult(false);
//			apiResult.setResult("调用固资系统失败");
//			return apiResult;
//		}
//		
//		Object code = resultMap.get("Code");
//		if(null!=code && (Integer)code==0) {
//			logger.debug("固资推送成功hostIp:[{}],name:[{}],ip:[{}],type:[{}]", hostIp, name, ip, type);
//			apiResult.setAnalyzeResult(true);
//			apiResult.setResult((String) resultMap.get("Msg"));
//		} else {
//			logger.error("固资推送失败结果：{}", ret);
//			logger.error("固资推送失败hostIp:[{}],name:[{}],ip:[{}],type:[{}]", hostIp, name, ip, type);
//			apiResult.setAnalyzeResult(false);
//			StringBuilder builder = new StringBuilder();
//			builder.append("固资推送失败,")
//			.append("ip[").append(ip).append("]")
//			.append(",type[").append(type).append("]")
//			.append(",失败原因:").append((String) resultMap.get("Msg"));
//			apiResult.setResult(builder.toString());
//		}
//		return apiResult;
//	}
	
	/**
	 * 根据宿主机ip获取对应的sn
	 * @param hostIp
	 * @return
	 */
//	private String getSnByHostIp(String hostIp) {
//		return HttpClient.get(fixedPushSnUrl + hostIp);			
//	}
//	
	
	private String FIXEDPUSH_GET = "http://oss.letv.cn:9310/comm_searchMachineSnrByIp.action?ip=";
	private String FIXEDPUSH_SOCKET_IP = "10.154.29.106";
	private int FIXEDPUSH_SOCKET_PORT = 29350;

	public ApiResultObject createMutilContainerPushFixedInfo(List<ContainerModel> containers){
		ApiResultObject api = new ApiResultObject();
		for(ContainerModel c:containers) {
			api = sendFixedInfo(c.getHostIp(), c.getContainerName(), c.getIpAddr(), "add");
            if(!api.getAnalyzeResult())
                break;
		}
		return api;
	}

	@Override
	public ApiResultObject deleteMutilContainerPushFixedInfo(List<ContainerModel> containers){
		ApiResultObject api = new ApiResultObject();
		for(ContainerModel c:containers) {
			api = sendFixedInfo(c.getHostIp(), c.getContainerName(), c.getIpAddr(), "delete");
            if(!api.getAnalyzeResult())
                break;
		}
		return api;
	}
	public ApiResultObject sendFixedInfo(String serverTag,String name,String ip,String type) {
		ApiResultObject api = new ApiResultObject();
		boolean flag = true;
		try {
			List<ContainerPush> list = new ArrayList<ContainerPush>();
			ContainerPush containerMode = new ContainerPush();
			containerMode.setName(name);
			containerMode.setIp(ip);
			list.add(containerMode);

			FixedPushModel fixedPushModel = new FixedPushModel();
			fixedPushModel.setServertag(serverTag);
			fixedPushModel.setType(type);
			fixedPushModel.setIpaddress(list);
			sendFixedInfo(fixedPushModel);
		} catch (Exception e) {
			api.setAnalyzeResult(false);
		} finally {
			api.setAnalyzeResult(flag);
		}
		return api;
	}

	private String sendFixedInfo(FixedPushModel fixedPushModel)throws Exception{
	    String sn =	receviceFixedInfo(fixedPushModel);
	    fixedPushModel.setServertag(sn);
	    String pushString =  JSON.toJSONString(fixedPushModel);
	    sendSocket(pushString);
        return null;
	}
 
	private String receviceFixedInfo(FixedPushModel fixedPushModel) throws Exception{
		if(fixedPushModel!=null){
			String hostIp = fixedPushModel.getServertag();
			String url = FIXEDPUSH_GET+hostIp;
			String sn=HttpClient.get(url);			
			return sn;
		}else {
			return null;
		}      
	}

	private void sendSocket(String pushString) throws IOException{
        Socket s1 = new Socket(FIXEDPUSH_SOCKET_IP, FIXEDPUSH_SOCKET_PORT);
	    InputStream is = s1.getInputStream();
	    DataInputStream dis = new DataInputStream(is);
	    OutputStream os = s1.getOutputStream();	
		try{
			if(null == pushString ||"".equals(pushString)){
			}else{
			os.write(int2byte(pushString.getBytes().length));
			os.write(pushString.getBytes());
			}
            os.flush();            
        } catch (Exception e) {
           logger.debug("socket发送出错");
        }finally{
        	 dis.close();
        	 s1.close();
        }
	}
    private static byte[] int2byte(int i) {
        return new byte[] { (byte) ((i >> 24) & 0xFF),
                (byte) ((i >> 16) & 0xFF), (byte) ((i >> 8) & 0xFF),
                (byte) (i & 0xFF) };
    }


}

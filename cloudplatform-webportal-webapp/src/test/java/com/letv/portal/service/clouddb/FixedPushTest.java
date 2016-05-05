package com.letv.portal.service.clouddb;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.AssertTrue;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.letv.portal.fixedPush.IFixedPushService;
import com.letv.portal.junitBase.AbstractTest;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.ContainerPush;
import com.letv.portal.model.fixed.FixedPushModel;

public class FixedPushTest extends AbstractTest{
	private final static Logger logger = LoggerFactory
			.getLogger(UserLoginTest.class);
	@Resource
	private IFixedPushService fixedPushService;

	@Test
	@Ignore
	public void createContainerPushFixedInfoTest() {

		// get()获得snr号然后做拼接
		// post()发送拼接好的信息
		// String url = "http://localhost:8080/webTest2/";
//		ContainerPush containerMode1 = new ContainerPush();
//		ContainerPush containerMode2 = new ContainerPush();
//		containerMode1.setName("container");
//		containerMode2.setName("container");
//		containerMode1.setIp("10.19.18.73");
//		containerMode2.setIp("10.19.18.72");
//		List<ContainerPush> list = new ArrayList<ContainerPush>();
//		list.add(containerMode1);
//		list.add(containerMode2);
//		FixedPushModel fixedPushModel1 = new FixedPushModel();
//		fixedPushModel1.setServertag("10.100.91.73");
//		fixedPushModel1.setIpaddress(list);
  	    List<ContainerModel> containers = new ArrayList<ContainerModel>();
  	    ContainerModel containerModel1 = new ContainerModel();
  	    ContainerModel containerModel2 = new ContainerModel();
  	    containerModel1.setContainerName("container1");
  	    containerModel1.setHostIp("10.200.91.156");
  	    containerModel1.setIpAddr("192.168.1.34");
  	    containerModel2.setContainerName("container2");
  	    containerModel2.setIpAddr("192.168.1.35");
  	    containerModel2.setHostIp("10.200.91.157");
  	    containers.add(containerModel1);  
  	    containers.add(containerModel2);   
		try {
			fixedPushService.createMutilContainerPushFixedInfo(containers);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}

	}
	
	@Test
	public void testSendFixedInfo() {
		boolean ret = fixedPushService.sendFixedInfo("10.154.156.129", "lisx-test", "10.200.83.1", "add");
		Assert.assertTrue(ret);
		ret = fixedPushService.sendFixedInfo("10.154.156.129", "lisx-test", "10.200.83.1", "delete");
		Assert.assertTrue(ret);
	}

	@Test
	@Ignore
	public void testSocket() {
		try {
			// Socket s1 = new Socket("123.126.33.215", 29450);
			Socket s1 = new Socket("123.126.32.147", 39450);
			InputStream is = s1.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			OutputStream os = s1.getOutputStream();
			String sds2 = "{'hostname': 'Store1111-BJ-CTC-old-98', 'serverhardtype': 'HP ProLiant DL180 G6', 'factname': 'Store-BJ-CTC-old-98', 'os': {'osKernel': '2.6.32-letv.2.220.4.1.el6.x86_64', 'osBit': 'x86_64', 'osName': 'CentOS 6.2'}, 'servertag': 'CNG951S5YL', 'servertype': 'physical', 'gatewayIP': '220.181.117.97', 'manageip': {}, 'dns': [{'nameserver': '10.200.0.3'}, {'nameserver': '219.141.136.10'}, {'nameserver': '219.141.244.2'}], 'memory': [{'Serial Number': '7C072A4B', 'Manufacturer': 'Nanya', 'Speed': '800 MHz', 'Size': '2048 MB'}, {'Serial Number': '7C072A1D', 'Manufacturer': 'Nanya', 'Speed': '800 MHz', 'Size': '2048 MB'}, {'Serial Number': '7E072A59', 'Manufacturer': 'Nanya', 'Speed': '800 MHz', 'Size': '2048 MB'}, {'Serial Number': '00000000', 'Manufacturer': 'Manufacturer03', 'Speed': 'Unknown', 'Size': 'No Module Installed'}, {'Serial Number': '7B072A2B', 'Manufacturer': 'Nanya', 'Speed': '800 MHz', 'Size': '2048 MB'}, {'Serial Number': '00000000', 'Manufacturer': 'Manufacturer05', 'Speed': 'Unknown', 'Size': 'No Module Installed'}, {'Serial Number': '00000000', 'Manufacturer': 'Manufacturer06', 'Speed': 'Unknown', 'Size': 'No Module Installed'}, {'Serial Number': '00000000', 'Manufacturer': 'Manufacturer07', 'Speed': 'Unknown', 'Size': 'No Module Installed'}, {'Serial Number': '00000000', 'Manufacturer': 'Manufacturer08', 'Speed': 'Unknown', 'Size': 'No Module Installed'}, {'Serial Number': '00000000', 'Manufacturer': 'Manufacturer09', 'Speed': 'Unknown', 'Size': 'No Module Installed'}, {'Serial Number': '00000000', 'Manufacturer': 'Manufacturer10', 'Speed': 'Unknown', 'Size': 'No Module Installed'}], 'raid': [], 'serverproduct': 'HP', 'ipaddress': [{'ip': '220.181.117.98', 'mac': '18:A9:05:74:2B:64', 'name': 'eth0'}, {'ip': '', 'mac': '18:A9:05:74:2B:65', 'name': 'eth1'}], 'cpu': {'cpuCoreNum': '4', 'cpuBit': '64', 'cpuClockSpeed': '2.00GHz', 'cpupynum': '1', 'cpuName': 'Intel Xeon', 'band': 'Intel', 'model': 'E5504'}}";
			if (null == sds2 || "".equals(sds2)) {
			} else {
				os.write(sds2.getBytes());
			}
			os.flush();
			// 关闭输入流
			dis.close();
			// 关闭打开的socket对象
			s1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
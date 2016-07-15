package com.letv.common.util;

import org.junit.Test;

import com.letv.common.util.AWSS3Util.AWS3SConn;

/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */

/**
 * 测试AWS3SUtil类下对外接口
 * 
 * @author linzhanbo .
 * @since 2016年6月22日, 下午12:50:40 .
 * @version 1.0 .
 */
public class AWS3SUtilTest {
	private AWS3SConn conn = null;
	/**
	 * 创建连接
	 * 
	 * @author linzhanbo .
	 * @since 2016年6月22日, 下午2:34:38 .
	 * @version 1.0 .
	 */
	public void getConn() {
		String endpoint = "http://s3.lecloud.com";
		String accessKey = "EH18VA68TUPMOF4L5MK3";
		String secretKey = "Y3KW8LAyVcTNS1cAnPEv847lUmtFXILVg+8gXaIo";
		AWS3SConn.ConnBuilder builder = new AWS3SConn.ConnBuilder();
		conn = builder.setEndpoint(endpoint).setAccessKey(accessKey)
				.setSecretKey(secretKey).build();
	}
	@Test
	public void testCreateBucket() {
		getConn();
		String bucketName = "testlyn1";
		try {
			AWSS3Util.getInstance(conn).addBucket(bucketName);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
	}
	@Test
	public void testDeleteBucket() {
		getConn();
		String bucketName = "testlyn1";
		try {
			AWSS3Util.getInstance(conn).deleteBucket(bucketName);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
	}
	@Test
	public void testUpload(){
		getConn();
		String bucketName = "testlyn12";
		String key = "1111";
		String filePath = "F:/Backup Softwares/YoudaoDict.exe";
		try {
			AWSS3Util.getInstance(conn).upload(bucketName,key,filePath);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
	}
}

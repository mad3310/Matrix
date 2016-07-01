/**
 *
 *  Copyright (c) 2016 乐视云计算有限公司（lecloud.com）. All rights reserved
 *
 */
package com.letv.common.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.letv.common.exception.CommonException;
import com.letv.common.exception.ServiceException;
import com.letv.common.exception.ValidateException;

/**
 * AWS S3常用操作封装
 * 
 * @author linzhanbo .
 * @since 2016年6月22日, 上午11:27:49 .
 * @version 1.0 .
 */
public enum AWSS3Util {
	INSTANCE;
	private final static Logger logger = LoggerFactory
			.getLogger(AWSS3Util.class);

	public static AWSS3Util getInstance(AWS3SConn conn) {
		INSTANCE.conn = conn;
		return INSTANCE;
	}
	
	/**
	 * 查看bucket是否存在
	 * @param bucketName	bucket名称
	 * @return
	 * @throws ServiceException
	 * @throws CommonException
	 * @author linzhanbo .
	 * @since 2016年6月22日, 下午3:12:35 .
	 * @version 1.0 .
	 */
	public boolean isExistBucket(String bucketName) throws ServiceException, CommonException{
		return this.isExistBucket(bucketName, false);
	}
	

	/**
	 * 上传文件 <br/>
	 * <b>注意：如果key-vaue已经存在,AWS S3会覆盖该key下的value</b>
	 * 
	 * @param bucketName
	 * @param key
	 * @param filePath
	 * @throws ServiceException
	 * @throws CommonException
	 * @author linzhanbo .
	 * @since 2016年6月22日, 下午3:07:04 .
	 * @version 1.0 .
	 */
	public void upload(String bucketName, String key, String filePath)
			throws ServiceException, CommonException {
		logger.debug("初始化AWS S3连接");
		INSTANCE.initConn();
		// 检查该bucket是否存在,不存在先创建
		boolean isExist = this.isExistBucket(bucketName, true);
		//如果Bucket不存在，则先创建
		if(!isExist){
			this.addBucket(bucketName,true);
		}
		logger.debug("上传文件到bucket: bucketName: " + bucketName + ", key:" + key
				+ ", filePath:" + filePath + "");
		try {
			s3.putObject(bucketName, key, new File(filePath));
			logger.debug("上传成功");
		} catch (AmazonServiceException ase) {
			String errorMsg = "上传失败: StatusCode:" + ase.getStatusCode()
					+ " ,ErrorCode:" + ase.getErrorCode() + " ,ErrorMsg: "
					+ ase.getMessage();
			logger.error(errorMsg);
			ase.printStackTrace();
			throw new ServiceException(errorMsg);
		} catch (Exception ace) {
			String errorMsg = "上传请求连接失败: " + ace.getMessage();
			logger.error(errorMsg);
			ace.printStackTrace();
			throw new CommonException(errorMsg);
		}
	}

	/**
	 * 删除bucket
	 * 
	 * @param bucketName
	 * @throws ServiceException
	 * @throws CommonException
	 * @author linzhanbo .
	 * @since 2016年6月22日, 下午3:06:46 .
	 * @version 1.0 .
	 */
	public void deleteBucket(String bucketName) throws ServiceException,
			CommonException {
		logger.debug("初始化AWS S3连接");
		INSTANCE.initConn();
		logger.debug("删除bucket:" + bucketName);
		try {
			s3.deleteBucket(bucketName);
			logger.debug("删除成功");
		} catch (AmazonServiceException ase) {
			String errorMsg = "删除AWS S3 bucket失败: StatusCode:"
					+ ase.getStatusCode() + " ,ErrorCode:" + ase.getErrorCode()
					+ " ,ErrorMsg: " + ase.getMessage();
			logger.error(errorMsg);
			ase.printStackTrace();
			throw new ServiceException(errorMsg);
		} catch (Exception ace) {
			String errorMsg = "删除AWS S3 bucket请求连接失败: " + ace.getMessage();
			logger.error(errorMsg);
			ace.printStackTrace();
			throw new CommonException(errorMsg);
		}
	}

	/**
	 * 添加bucket <br/>
	 * <b>注意：如果已经存在,AWS S3不做任何操作,不会影响该bucket已经存在的所有key</b>
	 * 
	 * @param bucketName
	 *            bucket名称
	 * @throws ServiceException
	 * @throws CommonException
	 * @author linzhanbo .
	 * @since 2016年6月22日, 下午3:05:43 .
	 * @version 1.0 .
	 */
	public void addBucket(String bucketName) throws ServiceException,
			CommonException {
		this.addBucket(bucketName, false);
	}
	
	/**
	 * 添加bucket <br/>
	 * <b>注意：如果已经存在,AWS S3不做任何操作,不会影响该bucket已经存在的所有key</b>
	 * 
	 * @param bucketName	bucket名称
	 * @param isConnected	是否建立网络连接
	 * @throws ServiceException
	 * @throws CommonException
	 * @author linzhanbo .
	 * @since 2016年6月22日, 下午3:17:43 .
	 * @version 1.0 .
	 */
	private void addBucket(String bucketName,boolean isConnected) throws ServiceException,
			CommonException {
		if(!isConnected){
			logger.debug("初始化AWS S3连接");
			INSTANCE.initConn();
		}
		logger.debug("创建bucket:" + bucketName);
		try {
			s3.createBucket(bucketName);
			logger.debug("创建成功");
		} catch (AmazonServiceException ase) {
			String errorMsg = "创建AWS S3 bucket失败: StatusCode:"
					+ ase.getStatusCode() + " ,ErrorCode:" + ase.getErrorCode()
					+ " ,ErrorMsg: " + ase.getMessage();
			logger.error(errorMsg);
			ase.printStackTrace();
			throw new ServiceException(errorMsg);
		} catch (Exception ace) {
			String errorMsg = "创建AWS S3 bucket请求连接失败: " + ace.getMessage();
			logger.error(errorMsg);
			ace.printStackTrace();
			throw new CommonException(errorMsg);
		}
	}
	/**
	 * 查看bucket是否存在
	 * @param bucketName	bucket名称
	 * @param isConnected	是否建立网络连接
	 * @return
	 * @throws ServiceException
	 * @throws CommonException
	 * @author linzhanbo .
	 * @since 2016年6月22日, 下午3:16:25 .
	 * @version 1.0 .
	 */
	private boolean isExistBucket(String bucketName,boolean isConnected) throws ServiceException, CommonException {
		if(!isConnected){
			logger.debug("初始化AWS S3连接");
			INSTANCE.initConn();
		}
		boolean isExist = false;
		try {
			isExist = s3.doesBucketExist(bucketName);
			return isExist;
		} catch (AmazonServiceException ase) {
			String errorMsg = "查看失败: StatusCode:" + ase.getStatusCode()
					+ " ,ErrorCode:" + ase.getErrorCode() + " ,ErrorMsg: "
					+ ase.getMessage();
			logger.error(errorMsg);
			ase.printStackTrace();
			throw new ServiceException(errorMsg);
		} catch (Exception ace) {
			String errorMsg = "请求连接失败: " + ace.getMessage();
			logger.error(errorMsg);
			ace.printStackTrace();
			throw new CommonException(errorMsg);
		}
	}
	/**
	 * 初始化AWS S3连接
	 * 
	 * @author linzhanbo .
	 * @since 2016年6月22日, 下午3:15:48 .
	 * @version 1.0 .
	 */
	private void initConn() {
		AWSCredentials credentials = new BasicAWSCredentials(
				conn.getAccessKey(), conn.getSecretKey());

		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setProtocol(Protocol.HTTP);

		s3 = new AmazonS3Client(credentials, clientConfig);
		s3.setEndpoint(conn.getEndpoint());
	}
	private AWS3SConn conn;
	private AmazonS3 s3;
	public static class AWS3SConn {

		private AWS3SConn() {
		}

		/**
		 * s3地址
		 */
		private String endpoint;
		/**
		 * s3请求访问key
		 */
		private String accessKey;
		/**
		 * s3客户端key
		 */
		private String secretKey;

		public static class ConnBuilder {
			private AWS3SConn conn = new AWS3SConn();

			public ConnBuilder() {
			}

			public ConnBuilder(String endpoint, String accessKey, String secretKey) {
				conn.endpoint = endpoint;
				conn.accessKey = accessKey;
				conn.secretKey = secretKey;
			}

			public ConnBuilder setEndpoint(String endpoint) {
				conn.endpoint = endpoint;
				return this;
			}

			public ConnBuilder setAccessKey(String accessKey) {
				conn.accessKey = accessKey;
				return this;
			}

			public ConnBuilder setSecretKey(String secretKey) {
				conn.secretKey = secretKey;
				return this;
			}

			public AWS3SConn build() throws ValidateException {
				if (StringUtils.isEmpty(conn.endpoint)) {
					throw new ValidateException("endpoint不能为空");
				}
				if (StringUtils.isEmpty(conn.accessKey)) {
					throw new ValidateException("accessKey不能为空");
				}
				if (StringUtils.isEmpty(conn.secretKey)) {
					throw new ValidateException("secretKey不能为空");
				}
				return conn;
			}
		}

		public String getEndpoint() {
			return endpoint;
		}

		public String getAccessKey() {
			return accessKey;
		}

		public String getSecretKey() {
			return secretKey;
		}

	}
}

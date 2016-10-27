package com.letv.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

// 将一个字符串按照zip方式压缩和解压缩
public class ZipUtil {

	private static final String CHARSET = "UTF-8";
	// 压缩
	public static String compress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes(CHARSET));
		gzip.close();
//		return out.toString(CHARSET);
		return bytesToHexString(out.toByteArray());
	}

	// 解压缩
	public static String uncompress(String str) throws Exception  {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		byte[] strBytes = str.getBytes(CHARSET);
		byte[] strBytes = hexStringToBytes(str);
		ByteArrayInputStream in = new ByteArrayInputStream(strBytes);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		// toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)
		return out.toString(CHARSET);
	}

	public static String bytesToHexString(byte[] src){
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	/**
	 * Convert hex string to byte[]
	 * @param hexString the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * @param c char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	// 测试方法
	public static void main(String[] args) throws Exception {

		String encodeContent = "";
		String realContent = "";
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			encodeContent = ZipUtil.compress("cloud_22989u8324312323423423|clientid_232409098weszdaasda|clientsecret_12sdsadsf|1029293891283911902"+i);
			realContent = ZipUtil.uncompress(encodeContent);
			System.out.println(",value="+realContent+",encode="+encodeContent.length());
		}
		long end = System.currentTimeMillis();
		System.out.println("time="+(end-start)+",value="+realContent+",encode="+encodeContent.length());
		// 测试字符串
//		String str = "%5B%7B%22lastUpdateTime%22%3A%222011-10-28+9%3A39%3A41%22%2C%22smsList%22%3A%5B%7B%22liveState%22%3A%221";
//
//		System.out.println("原长度：" + str.length());
//
//		System.out.println("压缩后：" + ZipUtil.compress(str).length());
//
//		System.out
//				.println("解压缩：" + ZipUtil.uncompress(ZipUtil.compress(str)));
	}

}
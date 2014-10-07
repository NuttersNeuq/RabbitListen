package com.lz.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	// 32位md5加密
	public static String Bit32(String sourceStr)
			throws NoSuchAlgorithmException {
		
		//拿到加密对象
		MessageDigest digest = MessageDigest.getInstance("MD5");
		//设置要加密的字节数组
		digest.update(sourceStr.getBytes());
		//加密
		byte[] messageDigest = digest.digest();
		
		return toHexString(messageDigest);
	}

	// 16位md5加密
	public static String Bit16(String sourceStr)
			throws NoSuchAlgorithmException {
		return Bit32(sourceStr).substring(8, 24);
	}

	// 将字节数组转成16进制中对应字符
	private static String toHexString(byte[] b) {

		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);

		}
		return sb.toString();

	}
}
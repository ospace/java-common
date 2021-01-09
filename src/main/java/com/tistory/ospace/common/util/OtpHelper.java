package com.tistory.ospace.common.util;

public class OtpHelper {

	public static String generateOtpAuth(String id, byte[] secret) {
		String secretBase32 = CmmUtils.encodeBase32(secret);
		
		return String.format("otpauth://totp/%s?secret=%s", id, secretBase32);
	}
	
	public static String generateQRCode(String id, byte[] secret) {
		String otpauth = generateOtpAuth(id, secret);
		
		return CmmUtils.generateQRCode(otpauth);
	}

	public static boolean validate(byte[] secret, long code, int window) {
		long update = System.currentTimeMillis() / 30000;
		for(int i=-window; i <= window; ++i) {
			if (code == SecurityUtils.generateTotp(secret, update+i)) return true;
		}
		
		return false;
	}

	public static boolean validate(byte[] secret, long code) {
		return validate(secret, code, 0);
	}
}

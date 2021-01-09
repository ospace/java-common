package com.tistory.ospace.common.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.primitives.Longs;

public class SecurityUtils {
	public static class Key {
		private SecretKeySpec secretKey;
	    private IvParameterSpec ivParameter;
	    
	    public Key(SecretKeySpec secretKey, IvParameterSpec ivParameter) {
	    	this.secretKey = secretKey;
	    	this.ivParameter = ivParameter;
	    }

		public SecretKeySpec getSecretKey() {
			return secretKey;
		}

		public IvParameterSpec getIvParameter() {
			return ivParameter;
		}
	}
	
    /*
     * 발생할수 있는 예외:
     * - NoSuchAlgorithmException
     * - NoSuchPaddingException
     * - InvalidKeyException
     * - IllegalBlockSizeException
     * - BadPaddingException
     * - UnsupportedEncodingException
     */
    public static byte[] encryptAES(SecretKeySpec key, IvParameterSpec iv, byte[] plain) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            return cipher.doFinal(plain);
        } catch (Exception e) {
            throw new RuntimeException("encryptAES", e);
        } 
    }

    /*
     * 발생할수 있는 예외:
     * - NoSuchAlgorithmException
     * - NoSuchPaddingException
     * - InvalidKeyException
     * - IllegalBlockSizeException
     * - BadPaddingException
     */
    public static byte[] decryptAES(SecretKeySpec key, IvParameterSpec iv, byte[] encrypt) {
        try {
            //AES/CBC/PKCS5Padding
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return cipher.doFinal(encrypt);
        } catch (Exception e) {
            throw new RuntimeException("decryptAES", e);
        }
    }
    
    public static String sha256(String data) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(data.getBytes("utf8"));
            return String.format("%064x", new BigInteger(1, digest.digest()));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException("sha256", e);
        }
    }
    
    /*
     * RFC6238 : REF 4226 implementation
     * update을 hamc으로 해시값 생성
     */
    public static long generateTotp(byte[] key, long data) {
    	return generateTotp(key, Longs.toByteArray(data));
    }
    
    public static long generateTotp(byte[] key, byte[] data) {
    	byte[] hash = hmacSha1(key, data);
    	int offset = hash[20-1]&0xF;
    	
    	long truncatedHash = 0;
    	for(int i=0; i<4; ++i) {
    		truncatedHash  <<= 8;
    		truncatedHash |= (hash[offset+i]&0xFF);
    	}
    	
		truncatedHash &= 0x7FFFFFFF;
		truncatedHash %= 1000000;
		
		return truncatedHash;
    }

    /* data 변조 확인을 위해 key와 data로 hash값 생성.
     * key는 서로 교환하고 data와 hash을 보내고 받는 곳에서
     * data와 key로 hash생성하고 비교
     * MAC: Message Authentication Code(데이터 무결성 지원)
     * Ref: https://ldap.or.kr/1373-2/
     */
    public static byte[] hmacSha1(byte[] key, byte[] data) {
    	final String algorithm  = "HmacSHA1";
    	
    	SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
    	try {
	    	Mac mac = Mac.getInstance(algorithm);
	    	mac.init(keySpec);
	    	
	    	return mac.doFinal(data);
    	} catch (NoSuchAlgorithmException | InvalidKeyException e) {
    		throw new RuntimeException("hmacSha1", e);
    	}
    }
}

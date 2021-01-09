package com.tistory.ospace.common.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityHelper {
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
     */
	public static Key createAesKey(String secret) {
		assert null != secret && !secret.isEmpty() : "secret must not empty";
		
		try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] sha1 = sha.digest(secret.getBytes());
            SecretKeySpec secretKey = new SecretKeySpec(Arrays.copyOf(sha1, 16), "AES");
            IvParameterSpec ivParameter = new IvParameterSpec(Arrays.copyOfRange(sha1, sha1.length-16, sha1.length));
            
            return new Key(secretKey, ivParameter);
        } catch (Exception e) {
            throw new RuntimeException("createKey", e);
        }
	}
	
    public static byte[] encryptAES(Key key, byte[] plain) {
    	assert null != key : "key must not null";
    	
        try {
            return SecurityUtils.encryptAES(key.getSecretKey(), key.getIvParameter(), plain);
        } catch (Exception e) {
            throw new RuntimeException("encryptAES", e);
        }
    }
    
    public static byte[] decryptAES(Key key, byte[] encrypt) {assert null != key : "key must not null";
    	assert null != key : "key must not null";
    	
        return SecurityUtils.decryptAES(key.getSecretKey(), key.getIvParameter(), encrypt);
    }
    
    public static String encryptAESBase64(Key key, String data) {
    	assert null != key : "key must not null";
    	
		try {
			byte[] encrypted = encryptAES(key, data.getBytes("UTF-8"));
			return CmmUtils.encodeBase64(encrypted);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("encryptAESBase64", e);
		}
    }
    
    public static String decryptAESBase64(Key key, String data) {
    	assert null != key : "key must not null";
    	
        try {
            byte[] encrypted = CmmUtils.decodeBase64(data);
            byte[] plain =  decryptAES(key, encrypted);
            
            return new String(plain);
        } catch (Exception e) {
            return data;
        }
    }
}

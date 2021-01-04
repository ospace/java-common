package com.tistory.ospace.common.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtils {
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
    
    /*
     * 발생할수 있는 예외:
     * - NoSuchAlgorithmException
     */
    private static SecretKeySpec aesKey = null;
    private static IvParameterSpec ivSpec = null;
    public static void setAesKey(String key) {
        if(null==key || key.isEmpty()) return;
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] sha1 = sha.digest(key.getBytes());
            aesKey = new SecretKeySpec(Arrays.copyOf(sha1, 16), "AES");
            ivSpec = new IvParameterSpec(Arrays.copyOfRange(sha1, sha1.length-16, sha1.length));
        } catch (Exception e) {
            throw new RuntimeException("setAesKey", e);
        }
    }
    
    public static byte[] encryptAES(byte[] plain) {
        if(null == aesKey) throw new RuntimeException("AES Key is not created");
        try {
            return encryptAES(aesKey, ivSpec, plain);
        } catch (Exception e) {
            throw new RuntimeException("encryptAES", e);
        }
    }
    
    public static byte[] decryptAES(byte[] encrypt) {
        if(null == aesKey) throw new RuntimeException("AES Key is not created");
        try {
            return decryptAESImpl(encrypt);
        } catch(Exception e) {
            return  encrypt;
        }
    }
    
    public static String encryptAES(String plain) {
        try {
            byte[] encrypted = encryptAES(plain.getBytes("UTF-8"));
            return new String(Base64.getEncoder().encodeToString(encrypted));
        } catch (Exception e) {
            throw new RuntimeException("encryptAES", e);
        }
    }
    
    public static String decryptAES(String encrypt) {
        try {
            byte[] encrypted = Base64.getDecoder().decode(encrypt);
            byte[] plain =  decryptAESImpl(encrypted);
            return new String(plain);
        } catch (Exception e) {
            return encrypt;
        }
    }
    
    private static byte[] decryptAESImpl(byte[] encrypt) {
        if(null == aesKey) throw new RuntimeException("AES Key is not created");
        return decryptAES(aesKey, ivSpec, encrypt);
    }
    
    public static String sha256(String data) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(data.getBytes("utf8"));
            return String.format("%064x", new BigInteger(1, digest.digest()));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException("failed to generate a sha256", e);
        }
    }
}

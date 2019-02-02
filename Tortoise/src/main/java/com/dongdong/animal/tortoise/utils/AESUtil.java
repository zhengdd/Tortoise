package com.dongdong.animal.tortoise.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Aes加解密工具类
 * Created by dongdongzheng on 2017/2/24.
 */

public class AESUtil {

    private static final String AES_MODE_OAEP = "AES/CBC/PKCS5Padding";
    private final static String ALGORITHM = "SHA1PRNG";
    private static final String CRYPTO = "Crypto";
    private static final String HEX = "0123456789ABCDEF";


    /**
     * AES加密算法加密
     *
     * @param seed 种子
     * @param intext 原文
     * @return 密文
     */
    public static String encode(String seed, String intext) {
        try {
            byte[] rawKey = getRawKey(seed.getBytes());
            return encode(rawKey, intext);
        } catch (Exception e) {
            e.printStackTrace();
            return intext;
        }


    }

    /**
     * AES加密算法加密
     *
     * @param key key
     * @param intext 原文
     * @return 密文
     */
    public static String encode(SecretKey key, String intext) {
        if (key == null) {
            return intext;
        }
        try {
            byte[] rawKey = key.getEncoded();
            return encode(rawKey, intext);
        } catch (Exception e) {
            e.printStackTrace();
            return intext;
        }

    }


    private static String encode(byte[] raw, String cleartext) {
        try {
            byte[] rawKey = raw;
            byte[] result = encrypt(rawKey, cleartext.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(result, Base64.NO_WRAP);
        } catch (Exception e) {
            return cleartext;
        }
    }

    /**
     *
     * @return AES加密算法加密
     * @throws Exception
     */
    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KeyProperties.KEY_ALGORITHM_AES);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(raw);
        Cipher cipher = Cipher.getInstance(AES_MODE_OAEP);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }


    /**
     * AES解密算法解密
     *
     * @param seed 种子
     * @param encrypted 密文
     * @return 原文
     */
    public static String decode(String seed, String encrypted) {
        try {
            byte[] rawKey = getRawKey(seed.getBytes());
            return encode(rawKey, encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return encrypted;
        }

    }

    /**
     * AES解密算法解密
     *
     * @param key key
     * @param encrypted 密文
     * @return 原文
     */
    public static String decode(SecretKey key, String encrypted) {
        if (key == null) {
            return encrypted;
        }
        try {
            byte[] rawKey = key.getEncoded();
            return decode(rawKey, encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return encrypted;
        }
    }

    public static String decode(byte[] raw, String encrypted) {
        try {
            byte[] rawKey = raw;
            byte[] enc = Base64.decode(encrypted, Base64.NO_WRAP);
            byte[] result = decrypt(rawKey, enc);
            return new String(result);
        } catch (Exception e) {
            return encrypted;
        }
    }


    private static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KeyProperties.KEY_ALGORITHM_AES);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(raw);
        Cipher cipher = Cipher.getInstance(AES_MODE_OAEP);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }


    private static String toHex(byte[] buf) {
        if (buf == null) {
            return "";
        }
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            result.append(HEX.charAt((buf[i] >> 4) & 0x0f)).append(
                    HEX.charAt(buf[i] & 0x0f));
        }
        return result.toString();
    }


    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        }
        return result;
    }


    /**
     * 生成密钥
     *
     * @param seed 种子
     * @return 密钥窜
     * @throws Exception
     */
    @SuppressLint("DeletedProvider")
    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES);
        // SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
        SecureRandom sr = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sr = SecureRandom.getInstance(ALGORITHM, new CryptoProvider());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            sr = SecureRandom.getInstance(ALGORITHM, CRYPTO);
        } else {
            sr = SecureRandom.getInstance(ALGORITHM);
        }

        sr.setSeed(seed);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;

    }


}

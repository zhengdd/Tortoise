package com.dongdong.animal.tortoise.keystorage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Base64;

import com.dongdong.animal.tortoise.utils.CryptoProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;


public class KeyStoreManager {
    private final static String AndroidKeyStore = "AndroidKeyStore";
    private final static String SpName = "Sp_Name_Aes_Key";
    private final static String SP_KEY_NAME_AES = "Sp_Key_Name_Aes";
    private static final String RSA_MODE_OAEP = "RSA/ECB/PKCS1Padding";
    private static final String ALGORITHM = "SHA1PRNG";
    private static final String CRYPTO = "Crypto";
    private static Context appContext;
    private static KeyStore keyStore;
    private static SharedPreferences preferences;


    public static void turnInit(Context context) {
        if (context == null) {
            throw new NullPointerException("The Context cannot be null");
        }

        appContext = context;

        try {
            keyStore = KeyStore.getInstance(AndroidKeyStore);
            keyStore.load(null);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static SecretKey getSecretKey(String alias) {
        try {
            String enSeed = getAesKey();
            String deSeed = "";
            //加密种子已存在
            if (!TextUtils.isEmpty(enSeed)) {
                deSeed = deSeed(alias, enSeed);
            } else {
                deSeed = enSeed(alias, getRandomString());
            }
            return getSecretKeyOfSeed(deSeed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SharedPreferences getSp() {
        if (preferences == null) {
            preferences = appContext.getSharedPreferences(SpName, Context.MODE_PRIVATE);
        }
        return preferences;
    }
    private static String getAesKey() {
        return getSp().getString(SP_KEY_NAME_AES, "");
    }

    private static void setAesKey(String seed) {
        getSp().edit().putString(SP_KEY_NAME_AES, seed).apply();
    }


    @SuppressLint("DeletedProvider")
    private static SecretKey getSecretKeyOfSeed(String seed) throws Exception {
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
        sr.setSeed(seed.getBytes());
        //256 bits or 128 bits,192bits
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        return skey;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static String enSeed(String alias, String seed) {
        PublicKey key = null;
        //Rsa已经存在
        if (!hasAlias(alias)) {
            key = createKeyPair(alias).getPublic();
        } else {
            try {
                key = keyStore.getCertificate(alias).getPublicKey();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }

        if (key != null) {
            String encode = seed;
            try {
                encode = encryptRSA(seed, key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            setAesKey(encode);
        }
        return seed;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static KeyPair createKeyPair(String alias) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties
                    .KEY_ALGORITHM_RSA, AndroidKeyStore);

            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 30);

            AlgorithmParameterSpec spec;
            spec = new KeyPairGeneratorSpec.Builder(appContext)
                    //使用别名来检索的关键。这是一个关键的关键!
                    .setAlias(alias)
                    // 用于生成自签名证书的主题 X500Principal 接受 RFC 1779/2253的专有名词
                    .setSubject(new X500Principal("CN=" + alias))
                    //用于自签名证书的序列号生成的一对。
                    .setSerialNumber(BigInteger.TEN)
                    // 签名在有效日期范围内
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();
            keyPairGenerator.initialize(spec);
            return keyPairGenerator.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String encryptRSA(String plainText, PublicKey key) throws Exception {

        Cipher cipher = Cipher.getInstance(RSA_MODE_OAEP);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedByte = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.encodeToString(encryptedByte, Base64.NO_WRAP);
    }

    private static String deSeed(String alias, String enseed) {
        //Rsa已经存在
        if (hasAlias(alias)) {
            KeyStore.PrivateKeyEntry privateKeyEntry = null;
            try {
                Cipher cipher = Cipher.getInstance(RSA_MODE_OAEP);
                privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore
                        .getEntry(alias, null);
                PrivateKey privateKey = privateKeyEntry.getPrivateKey();
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] encryptedByte = Base64.decode(enseed, Base64.NO_WRAP);
                return new String(cipher.doFinal(encryptedByte));
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }

            return enseed;
        } else {
            return enseed;
        }
    }


    /**
     * 判断当前别名是否存在
     * @param alias
     * @return
     */
    private static boolean hasAlias(String alias) {
        try {
            return keyStore != null && keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取随机字符串
     *
     * @return
     */
    private static String getRandomString() {
        //定义一个字符串（A-Z，a-z，0-9）即62位；
        String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        //由Random生成随机数
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        //长度为几就循环几次
        for (int i = 0; i < 16; ++i) {
            //产生0-61的数字
            int number = random.nextInt(62);
            //将产生的数字通过length次承载到sb中
            sb.append(str.charAt(number));
        }
        //将承载的字符转换成字符串
        return sb.toString();
    }


}

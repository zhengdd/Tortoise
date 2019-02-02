package com.dongdong.animal.tortoise.safestorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.dongdong.animal.tortoise.utils.AESUtil;
import com.dongdong.animal.tortoise.utils.MD5Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.crypto.SecretKey;


/**
 * Created by dongdongzheng on 2019/1/24.
 */

public class SafeSpManager {

    private static final String KEY_AES_MD5 = "key_aes_md5";
    private static Map<String, SafeSpManager> instanceMap;

    public static Context appContext;
    private String spName;
    private SecretKey aesKey;
    private SharedPreferences mSetSp;


    protected SafeSpManager(Context context, String spname, SecretKey key) {
        appContext = context.getApplicationContext();
        this.spName = spname;
        this.aesKey = key;

        String key_md5 = getShardPreferences().getString(KEY_AES_MD5, "");
        if (!TextUtils.isEmpty(key_md5)) {
            if (!key_md5.equals(MD5Util.bytes2Md5(aesKey.getEncoded()))) {
                throw new RuntimeException("Key error, initialization failed");
            }
        } else {
            if (isOldData()) {
                upOldData();
            } else {
                //存的只是key的MD5值 用于识别是否是同一个Key
                mSetSp.edit().putString(KEY_AES_MD5, MD5Util.bytes2Md5(aesKey.getEncoded())).commit();
            }

        }

    }

    public static void turnInit(Context context, SecretKey key) {
        turnInit(context, null, key);
    }

    public static void turnInit(Context context, String spname, SecretKey key) {
        if (context == null) {
            throw new NullPointerException("The context can not be Null！");
        }
        if (key == null) {
            throw new NullPointerException("The key can not be Null！");
        }
        if (TextUtils.isEmpty(spname)) {
            spname = context.getApplicationContext().getPackageName();
        }

        if (instanceMap == null) {
            instanceMap = new HashMap<>();
            SafeSpManager controller = new SafeSpManager(context, spname, key);
            instanceMap.put(spname, controller);

        } else {
            if (!instanceMap.containsKey(spname)) {
                instanceMap.put(spname, new SafeSpManager(context, spname, key));
            }
        }

    }

    public static SafeSpManager getInstance(String spName) {
        if (instanceMap == null) {
            return null;
        } else {
            if (TextUtils.isEmpty(spName)) {
                spName = appContext.getPackageName();
            }
            if (instanceMap.containsKey(spName)) {
                return instanceMap.get(spName);
            } else {
                return null;
            }
        }
    }

    private SharedPreferences getShardPreferences() {
        mSetSp = appContext.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return mSetSp;
    }

    private boolean isOldData() {
        return mSetSp.getAll().size() > 0 ? true : false;
    }

    private void upOldData() {
        Map<String, ?> oldData = mSetSp.getAll();
        Set<String> keys = oldData.keySet();
        mSetSp.edit().clear().commit();
        SharedPreferences.Editor editor = mSetSp.edit();
        for (String key : keys) {
            editor.putString(MD5Util.str2Md5(key), AESUtil.encode(aesKey, oldData.get(key).toString
                    ()));
        }
        editor.putString(KEY_AES_MD5, MD5Util.bytes2Md5(aesKey.getEncoded()));
        editor.apply();
    }


    private void putSpString(String key, String value) {
        mSetSp.edit().putString(MD5Util.str2Md5(key), AESUtil.encode(aesKey, value)).apply();
    }

    private String getSpValue(String key) {
        String value = mSetSp.getString(MD5Util.str2Md5(key), "");
        if (!TextUtils.isEmpty(value)) {
            return AESUtil.decode(aesKey, value);
        }
        return "";

    }

    public void putInt(String key, int value) {
        putSpString(key, String.valueOf(value));
    }

    public void putString(String key, String value) {
        putSpString(key, String.valueOf(value));
    }

    public void putBoolean(String key, boolean value) {
        putSpString(key, String.valueOf(value));
    }

    public void putFloat(String key, float value) {
        putSpString(key, String.valueOf(value));
    }

    public void putLong(String key, long value) {
        putSpString(key, String.valueOf(value));
    }

    public int getInt(String name) {
        return getInt(name, 0);
    }

    public int getInt(String name, int def) {

        String out = getSpValue(name);
        if (TextUtils.isEmpty(out)) {
            return def;
        } else {
            return Integer.valueOf(out).intValue();
        }

    }

    public long getLong(String name) {
        return getLong(name, 0L);
    }


    public long getLong(String name, long def) {
        String out = getSpValue(name);
        if (TextUtils.isEmpty(out)) {
            return def;
        } else {
            return Long.valueOf(out).longValue();
        }
    }

    public float getFloat(String name) {
        return getFloat(name, 0.0f);
    }

    public float getFloat(String name, float def) {
        String out = getSpValue(name);
        if (TextUtils.isEmpty(out)) {
            return def;
        } else {
            return Float.valueOf(out).floatValue();
        }
    }


    public boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    public boolean getBoolean(String name, boolean def) {
        String out = getSpValue(name);
        if (TextUtils.isEmpty(out)) {
            return def;
        } else {
            return Boolean.valueOf(out).booleanValue();
        }
    }

    public String getString(String name) {
        return getString(name, "");
    }

    public String getString(String name, String def) {
        String out = getSpValue(name);
        if (TextUtils.isEmpty(out)) {
            return def;
        } else {
            return out;
        }
    }

    public boolean contains(String name) {
        return mSetSp.contains(MD5Util.str2Md5(name));
    }

    public void getAll() {
        mSetSp.getAll();
    }

    public void clean() {
        String key_md5 = mSetSp.getString(KEY_AES_MD5, "");
        if (mSetSp.edit().clear().commit()) {
            mSetSp.edit().putString(KEY_AES_MD5, key_md5).apply();
        }
    }


}

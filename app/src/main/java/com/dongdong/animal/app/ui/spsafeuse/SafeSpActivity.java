package com.dongdong.animal.app.ui.spsafeuse;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dongdong.animal.app.App;
import com.dongdong.animal.app.R;
import com.dongdong.animal.tortoise.keystorage.KeyStoreManager;
import com.dongdong.animal.tortoise.safestorage.SafeSpManager;
import com.dongdong.animal.tortoise.utils.MD5Util;

import javax.crypto.SecretKey;

public class SafeSpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etSpName;
    Button btnInit;

    EditText etKey;
    EditText etValue;
    Button btnSetValue;

    EditText getKey;
    TextView getValue;
    Button btnGetValue;

    TextView getValueN;
    Button btnGetValueN;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_sp);
        etSpName = (EditText) findViewById(R.id.etSpMame);
        btnInit = (Button) findViewById(R.id.spInit);
        btnInit.setOnClickListener(this);

        etKey = (EditText) findViewById(R.id.etSetKey);
        etValue = (EditText) findViewById(R.id.etSetValue);
        btnSetValue = (Button) findViewById(R.id.btnSet);
        btnSetValue.setOnClickListener(this);

        getKey = (EditText) findViewById(R.id.getKey);
        getValue = (TextView) findViewById(R.id.getValue);
        btnGetValue = (Button) findViewById(R.id.btnGet);
        btnGetValue.setOnClickListener(this);

        getValueN = (TextView) findViewById(R.id.getValueN);
        btnGetValueN = (Button) findViewById(R.id.btnGetN);
        btnGetValueN.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.spInit:
                String name = etSpName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    SafeSpManager.turnInit(App.getContext(), name, "asfdfdffdf");
                }
                break;
            case R.id.btnSet:
                String name1 = etSpName.getText().toString().trim();
                String key = etKey.getText().toString().trim();
                String value = etValue.getText().toString().trim();
                if (!TextUtils.isEmpty(name1)
                        && !TextUtils.isEmpty(key)
                        && !TextUtils.isEmpty(value)) {

                    SafeSpManager.getInstance(name1).putString(key, value);
                }
                break;
            case R.id.btnGet:
                String name2 = etSpName.getText().toString().trim();
                String getkey = getKey.getText().toString().trim();
                String getvalue = "";
                if (!TextUtils.isEmpty(name2)
                        && !TextUtils.isEmpty(getkey)) {

                    getvalue = SafeSpManager.getInstance(name2).getString(getkey);
                    getValue.setText(getvalue);

                }
                break;
            case R.id.btnGetN:
                String name3 = etSpName.getText().toString().trim();
                String getkeyN = getKey.getText().toString().trim();
                if (!TextUtils.isEmpty(name3)
                        && !TextUtils.isEmpty(getkeyN)) {
                    getValueN.setText(getspofkey(name3, getkeyN));

                }
                break;
            default:
                break;
        }
    }


    private SecretKey getSecretKey() {
        return KeyStoreManager.getSecretKey("zhengdd");
    }


    private String getspofkey(String name, String key) {
        SharedPreferences preferences = App.getContext().getSharedPreferences(name, Context
                .MODE_PRIVATE);
        return preferences.getString(MD5Util.str2Md5(key), "");

    }


}

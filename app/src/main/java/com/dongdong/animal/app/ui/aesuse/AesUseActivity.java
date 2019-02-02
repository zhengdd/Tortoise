package com.dongdong.animal.app.ui.aesuse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dongdong.animal.app.R;
import com.dongdong.animal.tortoise.keystorage.KeyStoreManager;
import com.dongdong.animal.tortoise.utils.AESUtil;

import javax.crypto.SecretKey;

public class AesUseActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText etAlias;
    private EditText etInText;
    private TextView btnEncode;
    private EditText etOutEnText;
    private TextView btnDecode;
    private TextView outDeText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keytore);
        etAlias = (EditText) findViewById(R.id.etAlias);
        etInText = (EditText) findViewById(R.id.etInText);
        btnEncode = (TextView) findViewById(R.id.btnEnCode);
        btnDecode = (TextView) findViewById(R.id.btnDeCode);
        etOutEnText = (EditText) findViewById(R.id.etOutEnText);
        outDeText = (TextView) findViewById(R.id.tvOutDeText);

        btnEncode.setOnClickListener(this);
        btnDecode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnCode:
                String intText = etInText.getText().toString().trim();
                if (!TextUtils.isEmpty(intText)) {
                    String out = AESUtil.encode(getSecreKey(), intText);
                    etOutEnText.setText(out);
                }

                break;
            case R.id.btnDeCode:
                String intText2 = etOutEnText.getText().toString().trim();
                if (!TextUtils.isEmpty(intText2)) {
                    String out2 = AESUtil.decode(getSecreKey(), intText2);
                    outDeText.setText(out2);
                }
                break;
            default:
                break;
        }
    }

    private SecretKey getSecreKey() {
        String alias = etAlias.getText().toString().trim();
        if (TextUtils.isEmpty(alias)) {
            alias = "zhengdd";
        }
        return KeyStoreManager.getSecretKey(alias);
    }
}

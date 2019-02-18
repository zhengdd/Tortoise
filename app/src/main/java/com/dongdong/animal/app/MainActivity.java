package com.dongdong.animal.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dongdong.animal.app.ui.aesuse.AesUseActivity;
import com.dongdong.animal.app.ui.spsafeuse.SafeSpActivity;
import com.dongdong.animal.tortoise.confusion.ConfusionUtil;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tvToAesUtil).setOnClickListener(this);
        findViewById(R.id.tvToSPUtil).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvToAesUtil:
                Intent toKeyStore = new Intent(MainActivity.this, AesUseActivity.class);
                startActivity(toKeyStore);

                break;
            case R.id.tvToSPUtil:
                Intent toSafesp = new Intent(MainActivity.this, SafeSpActivity.class);
                startActivity(toSafesp);
                break;
            default:
                break;
        }
    }
}

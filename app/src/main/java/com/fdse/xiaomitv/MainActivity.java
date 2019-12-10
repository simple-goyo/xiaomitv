package com.fdse.xiaomitv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fdse.xiaomitv.activity.WebViewActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //显示方式声明Intent，直接启动SecondActivity
        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
        startActivity(intent);
    }
}
//wjw test 2019/12/09
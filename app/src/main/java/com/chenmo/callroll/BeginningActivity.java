package com.chenmo.callroll;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class BeginningActivity extends AppCompatActivity {
    MyHandle mh=new MyHandle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beginning);
        new MyThread().start();
    }

    class MyThread extends Thread{
        @Override
        public void run() {
            try {
                sleep(800);
                mh.sendEmptyMessage(40);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    class MyHandle extends Handler{
        public String FIRST_RUN="FIRST_RUN";
        public String PREFERENCE_FILE="PREFERENCE_FILE";
        public boolean isFirst;
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==40) {
                SharedPreferences sp = BeginningActivity.this.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
                isFirst = sp.getBoolean(FIRST_RUN, true);
                if (isFirst) {
                    Intent intent1 = new Intent();
                    intent1.setClass(BeginningActivity.this, FirstStartActivity.class);
                    BeginningActivity.this.startActivity(intent1);
                    BeginningActivity.this.finish();
                } else {
                    Intent intent2 = new Intent();
                    intent2.setClass(BeginningActivity.this, Login.class);
                    BeginningActivity.this.startActivity(intent2);
                    BeginningActivity.this.finish();
                }
            }
        }
    }

}



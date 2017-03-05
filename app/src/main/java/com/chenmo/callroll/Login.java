package com.chenmo.callroll;

import android.app.AlertDialog;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.PrintStreamPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    private ImageView logo;
    private EditText userId;
    private EditText userPwd;
    private Button loginButton;
    private String sId;
    private String sPwd;
    private LoginHandler lh;
    private ProgressBar pb;

    private String ID="ID";
    private String PASS_WORD="PASS_WORD";
    private String FIRST_RUN="FIRST_RUN";
    private String PREFERENCE_FILE="PREFERENCE_FILE";
    private boolean isFirst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logo= (ImageView) findViewById(R.id.logo);
        userId= (EditText) findViewById(R.id.userId);
        userPwd= (EditText) findViewById(R.id.userPwd);
        loginButton= (Button) findViewById(R.id.login_button);
        pb= (ProgressBar) findViewById(R.id.progressBar);
        lh=new LoginHandler();



        //Log.e(Double.toString(lt.latitude)+" 主程序里 ",Double.toString(lt.longitude));
        //此时进行判断是不是第一次访问
        //主要处理不是第一次的情况
        SharedPreferences sp=getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        isFirst=sp.getBoolean(FIRST_RUN,true);
        if(isFirst){
            //e。。。没什么事了 您登陆吧
            //噢不对 我得提醒你这玩意儿就能登陆一次
            AlertDialog.Builder builder=new AlertDialog.Builder(Login.this).setTitle("注意").setIcon(R.drawable.attention).setMessage("为防止同手机登录多账号\n登录后无法更改账户信息\n请确保是本人操作");
            builder.setPositiveButton("哦", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(Login.this,"请输入您的账号密码",Toast.LENGTH_SHORT).show();
                }
            }).create().show();
        }
        else{
            //不是第一次登录，系统自动登陆
            userId.setText(sp.getString(ID,""));
            userPwd.setText(sp.getString(PASS_WORD,""));
            onLoginButtonClick(loginButton);//自动调用按钮
        }
    }
    public void savePreferenceChange(){
        //super.onStop();
        SharedPreferences sp=getSharedPreferences(PREFERENCE_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        if(isFirst){
            editor.putBoolean(FIRST_RUN, false);
            editor.putString(ID,sId);               //要改！
            editor.putString(PASS_WORD,sPwd);
        }
        editor.commit();
    }
    public void UIChange(){
        pb.setVisibility(View.VISIBLE);
        loginButton.setText("正在登录···");
        loginButton.setClickable(false);
    }
    public void UIChangeBack(){
        pb.setVisibility(View.INVISIBLE);
        loginButton.setText("登录");
        loginButton.setClickable(true);
    }
    public void onLoginButtonClick(View view){
        sId=userId.getText().toString();
        sPwd=userPwd.getText().toString();
        Log.e("AAAAAAAAAAAAAAAAAAAA",sPwd);
        if(sId.equals("")||sPwd.equals("")){
            Toast.makeText(this,"",Toast.LENGTH_SHORT).show();
            return;
        }
        UIChange(); //正在点名。。一些列的UI变化
        Log.e("这个估计是主线程哈哈哈哈哈哈", Thread.currentThread().getName());
        new LoginThread().start();//启动新线程 进行网络访问
        //new Thread(new LoginThread()).start();
    }
    class LoginHandler extends Handler{
        public void handleMessage(Message msg){
            Log.e("让我来看看这个线程接收到没有", Thread.currentThread().getName());
            if(msg.what>=0&&msg.what<=4) {
                startOtherActivity(msg.what);
            }
        }
    }
    class LoginThread extends Thread{
        public void run(){
            Log.e("啊啊啊啊我要看看你到底是什么gui", Thread.currentThread().getName());
            HttpUtils httpUtils = new HttpUtils();//访问服务器
            /**在这里修改back的值 改为本地数据 做测试*/
            int back = httpUtils.LogIn(sId, sPwd);//得到服务器返回值
            //int back=;
            //int back=2;  //记得这里改回来呵呵呵呵呵呵
            Log.e("测试一下啊啊啊啊啊啊", Thread.currentThread().getName());
            lh.sendEmptyMessage(back);//使用主线程的handler 将数据传给主线程
            Log.e("测试一下", Thread.currentThread().getName());
        }
    }
    public void startOtherActivity(int answer){
        //  0  账号密码不对 1  老师 有课       2 老师，无课 3  学生 有课       4 学生，无课  5 乱七八糟莫名其妙的错误呵呵呵呵呵
        UIChangeBack();//点名结束，一系列UI变化
        if(answer==0){          //假设  0 密码错误
            Toast.makeText(Login.this,"密码错误",Toast.LENGTH_SHORT).show();
        }
        else if(answer==-1){
            Toast.makeText(Login.this,"出错了，再试一下吧",Toast.LENGTH_SHORT);
        }
        else if(answer==2){          //假设 老师，有课
            savePreferenceChange();
            Intent intent=new Intent();
            ComponentName cn=new ComponentName(Login.this,TeacherActivity.class);
            intent.setComponent(cn);
            Bundle bundle=new Bundle();
            bundle.putString("userId",sId);
            bundle.putString("userPwd",sPwd);
            intent.putExtras(bundle);
            startActivity(intent);
            Login.this.finish();
        }
        else if(answer==3){          //假设  老师 没课
            savePreferenceChange();
            Intent intent=new Intent();
            ComponentName cn=new ComponentName(Login.this,TeacherEmptyActivity.class);
            intent.setComponent(cn);
            Bundle bundle=new Bundle();
            bundle.putString("userId",sId);
            bundle.putString("userPwd",sPwd);
            intent.putExtras(bundle);
            startActivity(intent);
            Login.this.finish();
        }
        else if(answer==1){           //学生情况
            savePreferenceChange();
            Intent intent=new Intent();
            ComponentName cn=new ComponentName(Login.this,StudentActivity.class);
            intent.setComponent(cn);
            Bundle bundle=new Bundle();
            bundle.putString("userId",sId);
            bundle.putString("userPwd",sPwd);
            intent.putExtras(bundle);
            startActivity(intent);
            Login.this.finish();
        }
    }
}
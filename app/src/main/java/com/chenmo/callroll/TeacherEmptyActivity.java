package com.chenmo.callroll;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TeacherEmptyActivity extends AppCompatActivity {
    private String userId;
    private String userPwd;
    private String[] nameAndCourse;
    private HttpUtils httpUtils;
    private TextView tv_teacher_name;
    private TextView tv_course_name;
    private ImageView pic;
    private TeacherEmptyHandler teh=new TeacherEmptyHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_empty);

        Bundle bundle=getIntent().getExtras(); //接受从登录界面传过来的账号密码信息
        userId=bundle.getString("userId");
        userPwd=bundle.getString("userPwd");

        tv_teacher_name= (TextView) findViewById(R.id.teacher_name);
        tv_course_name= (TextView) findViewById(R.id.course_name);
        pic= (ImageView) findViewById(R.id.pic);
        pic.setAlpha(60);

        httpUtils=new HttpUtils();
        new TeacherEmptyThread().start();
    }
    class TeacherEmptyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==20){
                tv_course_name.setText(nameAndCourse[1]);
                tv_teacher_name.setText(nameAndCourse[0]);
            }
        }
    }
    class TeacherEmptyThread extends Thread{
        @Override
        public void run() {
            nameAndCourse=httpUtils.TeacherCallTheRollInit1(userId,userPwd);
            teh.sendEmptyMessage(20);
        }
    }
}

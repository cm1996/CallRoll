package com.chenmo.callroll;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StudentActivity extends AppCompatActivity {

    private String userId;
    private String userPwd;
    private String student_name;
    private String course_name;
    private String[] nameAndCourse;
    private HttpUtils httpUtils=new HttpUtils();
    private TextView tv_student_name;
    private TextView tv_course_name;
    private TextView tv_text;
    private ImageView iv_answer;
    private StudentHandler sh=new StudentHandler();
    private int feedBack;
    private LocationTools lt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student);

        Bundle bundle=getIntent().getExtras(); //接受从登录界面传过来的账号密码信息
        userId=bundle.getString("userId");
        userPwd=bundle.getString("userPwd");

        tv_student_name= (TextView) findViewById(R.id.student_name);
        tv_course_name= (TextView) findViewById(R.id.course_name);
        tv_text= (TextView) findViewById(R.id.text);
        iv_answer= (ImageView) findViewById(R.id.answer);

        lt=new LocationTools(this);
        lt.getLocation();

        new StudentThread().start();

    }
    class StudentHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==30){
                tv_student_name.setText(student_name);
                tv_course_name.setText(course_name);
            }
            if(msg.what==31){
                switch (feedBack){
                    case -1:
                        Toast.makeText(StudentActivity.this,"与服务器连接有问题",Toast.LENGTH_SHORT).show();
                        break;
                    case 0:
                        Toast.makeText(StudentActivity.this,"尚未开始点名或点名已结束",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(StudentActivity.this,"点名成功",Toast.LENGTH_SHORT).show();
                        tv_text.setText("答到成功");
                        iv_answer.setImageResource(R.drawable.btn_grey);
                        iv_answer.setClickable(false);
                        break;
                    case 2:
                        Toast.makeText(StudentActivity.this,"距离太远了，点名失败",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }
    class StudentThread extends Thread{
        @Override
        public void run() {
            nameAndCourse=httpUtils.TeacherCallTheRollInit1(userId,userPwd);
            student_name=nameAndCourse[0];
            course_name=nameAndCourse[1];
            sh.sendEmptyMessage(30);
        }
    }
    class StudentAnswerThread extends Thread{
        @Override
        public void run() {
            /**
             * 这里修改临时值 做测试*/
            feedBack=httpUtils.StudentCallTheRoll(userId,userPwd,lt.longitude,lt.latitude);
            //feedBack=1;
            sh.sendEmptyMessage(31);
        }
    }
    public void onAnswer(View v){
        if(lt.hasGetLocation) {


            new StudentAnswerThread().start();
        }
        else{
            Toast.makeText(StudentActivity.this,"尚未定位成功，请稍等",Toast.LENGTH_SHORT).show();

        }
    }
}

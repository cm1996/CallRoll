package com.chenmo.callroll;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TeacherActivity extends AppCompatActivity {
    private String userId;
    private String userPwd;
    private String[] nameAndCourse;
    public String teacherName;
    public String courseName;
    public TextView tv_teacher;
    public TextView tv_course;
    List<Student> student_list;
    List<Student> temp;
    private ListView lv_student_list;
    private Button btn_cr;
    private boolean isCallingRoll=false;
    public TeacherHandler th=new TeacherHandler();
    private HttpUtils httpUtils = new HttpUtils();
    SimpleAdapter sa_student_list;
    private int[] isHere;
    List<Map<String,Object>> listItems;
    private LocationTools lt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher);
        tv_course= (TextView) findViewById(R.id.course_name);
        tv_teacher= (TextView) findViewById(R.id.teacher_name);
        lv_student_list= (ListView) findViewById(R.id.student_list);
        btn_cr= (Button) findViewById(R.id.call_roll);

        Bundle bundle=getIntent().getExtras();        //接受从登录界面传过来的账号密码信息
        userId=bundle.getString("userId");
        userPwd=bundle.getString("userPwd");

        lt=new LocationTools(this);
        lt.getLocation();

        lv_student_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("卧槽怎么没反应啊","我要zui了");
                if(isHere[position]==0){
                    isHere[position]=1;
                    Map<String,Object> m=listItems.get(position);
                    m.put("isHere", R.drawable.check);
                    sa_student_list.notifyDataSetChanged();
                }
                else{
                    isHere[position]=0;
                    Map<String,Object> m=listItems.get(position);
                    m.put("isHere", R.drawable.error);
                    sa_student_list.notifyDataSetChanged();
                }
            }
        });
        new TeacherThread().start();                //开启新线程去访问网络  获取当前人物和课程信息

    }

    public void onCRButtonClick(View v){
        if(!isCallingRoll){
            if(lt.hasGetLocation) {
                isCallingRoll = true;
                btn_cr.setText("停止点名");
                //向 服务器 发送 点名请求
                //开启新线程去持续请求服务器，当isCallingRoll等于false时停止
                Toast.makeText(TeacherActivity.this, "点名开始，您也可以通过点击列表项手动修改", Toast.LENGTH_SHORT).show();
                new CallRollThread().start();
            }
            else{
                Toast.makeText(TeacherActivity.this,"尚未定位成功，请稍等",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            //向服务器发送 停止点名请求
            isCallingRoll=false;
            btn_cr.setClickable(false);
            btn_cr.setText("点名结束");
            int presence=0;
            for(int i=0;i<isHere.length;i++)
                presence+=isHere[i];
            new LastCallRollThread().start();
            String show="点名结束，"+"应道"+isHere.length+"人，实到"+presence+"人"+",您可以手动修改";
            Toast.makeText(TeacherActivity.this, show, Toast.LENGTH_SHORT).show();
        }
    }
    class TeacherHandler extends Handler {
        public void handleMessage(Message msg){
            if(msg.what==10){
                //现在就是说服务器把老师个人资料和课程信息数据已经传回来了，开始修改UI
                tv_course.setText(courseName);
                tv_teacher.setText(teacherName);
            }
            else if(msg.what==11){
                //说明服务器还把那啥学生名单给我返回来了哈哈哈哈哈 可以配置那啥listview了
                //开始填充适配学生名单
                listItems=new ArrayList<Map<String,Object>>();
                for(int i=0;i<student_list.size();i++){
                    Map<String,Object> item=new HashMap<String,Object>();
                    item.put("s_name",student_list.get(i).getsName());
                    item.put("s_id",student_list.get(i).getsName());
                    item.put("isHere",R.drawable.error);
                    listItems.add(item);
                }
                sa_student_list=new SimpleAdapter(TeacherActivity.this,listItems,R.layout.student_list_item,new String[]{"s_name","s_id","isHere"},new int[]{R.id.stuName,R.id.stuId,R.id.flag});
                lv_student_list.setAdapter(sa_student_list);
            }
            else if(msg.what==12){
                //此处应 根据 temp 的值修改或者重新加载 listview
                if(temp!=null) {
                    Log.e(temp.toString(), Integer.toString(temp.size()));
                    for (int i = 0; i < temp.size(); i++) {
                        for (int j = 0; j < student_list.size(); j++) {
                            if (temp.get(i).getsId().equals(student_list.get(j).getsId())) {
                                isHere[j] = 1;
                                Map<String, Object> m = listItems.get(j);
                                m.put("isHere", R.drawable.check);
                                break;
                            }
                        }
                    }
                    sa_student_list.notifyDataSetChanged();   //让适配器 注意 数据 已经改变  这个可能出错 那就把它移动到for for循环里面
                }
                //当教师还未点第二次按钮时，处理完一次学生数据后，我们继续请求服务器
                if(isCallingRoll){
                    new CallRollThread().start();
                }
            }
        }
    }
    class TeacherThread extends Thread{
        public void run(){
            nameAndCourse=httpUtils.TeacherCallTheRollInit1(userId,userPwd);
            teacherName=nameAndCourse[0];
            courseName=nameAndCourse[1];
            th.sendEmptyMessage(10);
            /***  注意在这里修改向服务器的请求，然后改成本地数据做小测试*/
            student_list=httpUtils.TeacherCallTheRollInit2(userId,userPwd);
            Log.e("这里应该没执行","呵呵呵呵呵呵呵呵");
            /**
            student_list = new ArrayList<Student>();
            for(int i=0;i<18;i++) {
                student_list.add(new Student("1", "2", "3", "4"));
            }
             */

            isHere=new int[student_list.size()];
            for(int i=0;i<isHere.length;i++)
                isHere[i]=0;
            th.sendEmptyMessage(11);
        }
    }
    class CallRollThread extends Thread{
        public void run(){
            //请求服务器以获取学生数据
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            temp=httpUtils.TeacherAskForData(userId,userPwd,lt.longitude,lt.latitude);
            th.sendEmptyMessage(12);
        }
    }
    class LastCallRollThread extends Thread{
        @Override
        public void run() {
            temp=httpUtils.TeacherAskForData(userId,"",lt.longitude,lt.latitude);
        }
    }
}

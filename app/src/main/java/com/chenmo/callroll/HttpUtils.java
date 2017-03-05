package com.chenmo.callroll;


import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtils {
    //服务器的地址
    private URL url;
    private static String LOGIN_PATH = "http://192.168.253.1:8080/dianming2/Login";        //登录请求
    private static String INFO_PATH = "http://192.168.253.1:8080/dianming2/Userinfo";         //学生和老师的四个界面初始化时请求sName/tName，cName
    private static String STUDENTLIST_PATH = "http://192.168.253.1:8080/dianming2/Getstudentlist";  //TeacherActivity初始化时请求List<Student>
    private static String TEACHER_PATH = "http://192.168.253.1:8080/dianming2/Calltherolls";      //老师点名请求
    private static String STUDENT_PATH = "http://192.168.253.1:8080/dianming2/Calltherolls";      //学生点名的请求

    public HttpUtils(){
    }

    /**
     向服务器端发送用户填写的账号userId和密码userPwd并判断，根据返回值（int）提示信息：
     0：用户名或密码错误
     1：学生验证完成
     2：教师验证完成
     3: 教师验证完成
     -1: error
     */
    public int LogIn(String userId,String userPwd) {

        try {
            url = new URL(LOGIN_PATH);
            HttpURLConnection huc = (HttpURLConnection)url.openConnection();
            huc.setDoOutput(true);
            huc.setDoInput(true);
            huc.setRequestMethod("POST");
            huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            huc.setRequestProperty("Charset", "utf-8");

            Map<String,String> map = new HashMap<String, String>();
            map.put("username",userId);
            map.put("password",userPwd);
            String path = "";
            try {
                path = Tools.sendPOSTPath(map);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("5","tools.sendpostpath有问题");
            }
            if(!"".equals(path)){
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(huc.getOutputStream()));
                bw.write(path);
                bw.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                int flag = Integer.parseInt(br.readLine());

                //关闭流
                bw.close();
                br.close();

                //根据返回值判断登录情况
                if(flag == 0){
                    Log.e("5","用户名或密码错误！");
                    return 0;
                }
                else if (flag == 1){
                    Log.e("4","学生登录成功！");
                    return 1;
                }
                else if (flag == 2){
                    Log.e("4","教师登录成功！");
                    return 2;
                }
                else if(flag == 3){
                    Log.e("4","教师登录成功！");
                    return 3;
                }
                else{
                    Log.e("5","登录系统发生错误！");
                    return -1;
                }
            }
            else{
                Log.e("5","path 有问题");
            }


        } catch (IOException e) {
            e.printStackTrace();
            Log.e("5","catch IOException");
        }

        return -1;
    }

    /**
     *老师点名客户端初始化调用的第一个函数
     参数：教师id，教师pwd
     返回值：教师名tName，课程名cName
     学生点名客户端初始化调用的函数
     参数：学生id，学生pwd
     返回值：学生名sName，课程名cName
     */
    public String[] TeacherCallTheRollInit1(String userId,String userPwd) {
        String temp;
        String[] result = new String[2];

        try {
            url = new URL(INFO_PATH);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setDoOutput(true);
            huc.setDoInput(true);
            huc.setRequestMethod("POST");
            huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            Map<String, String> map = new HashMap<String, String>();
            map.put("username", userId);
            map.put("password", userPwd);
            String path = "";
            try {
                path = Tools.sendPOSTPath(map);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("5", "tools.sendpostpath有问题");
            }
            if (!"".equals(path)) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(huc.getOutputStream()));
                bw.write(path);
                bw.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                temp = br.readLine();

                temp = URLDecoder.decode(temp, "utf-8");

                //服务器端将两个String合并，中间有一个空格作为分隔符
                result = temp.split(",");
                result[0] = URLDecoder.decode(result[0], "utf-8");
                result[1] = URLDecoder.decode(result[1], "utf-8");

                //关闭流
                bw.close();
                br.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     老师点名客户端初始化调用的第二个函数
     参数：教师id，教师pwd
     返回值：学生名单（含sId和sName）
     */
    public List<Student> TeacherCallTheRollInit2(String userId,String userPwd){
        //String temp;
        String result = null;
        try {
            url = new URL(STUDENTLIST_PATH);
            HttpURLConnection huc = (HttpURLConnection)url.openConnection();
            huc.setDoOutput(true);
            huc.setDoInput(true);
            huc.setRequestMethod("POST");
            huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            huc.setRequestProperty("Charset", "utf-8");

            Map<String,String> map = new HashMap<String, String>();
            map.put("username", userId);
            map.put("password", userPwd);
            String path = "";
            try {
                path = Tools.sendPOSTPath(map);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("5","tools.sendpostpath有问题");
            }
            if(!"".equals(path)) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(huc.getOutputStream()));
                bw.write(path);
                bw.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream()));

                result = br.readLine();
                result = URLDecoder.decode(result, "UTF-8");

                //关闭流
                bw.close();
                br.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        List<Student> list = gson.fromJson(result,new TypeToken<List<Student>>(){}.getType());
        return list;
    }

    /**
     * 在学生点名界面点击按钮时触发，将学生id和密码发送给服务器进行点名：
     *      与服务器连接有问题，返回-1；
     *      如果尚未开始点名或点名已结束，返回0；
     *      点名成功，返回1；
     *      点名失败（网络超时），返回2
     */
    public int StudentCallTheRoll(String userId,String userPwd,double longitude,double latitude){
        int flag = -1;
        try {
            url = new URL(STUDENT_PATH);
            HttpURLConnection huc = (HttpURLConnection)url.openConnection();
            huc.setDoOutput(true);
            huc.setDoInput(true);
            huc.setRequestMethod("POST");
            huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            huc.setRequestProperty("Charset", "utf-8");

            Map<String,String> map = new HashMap<String, String>();
            map.put("x",Double.toString(latitude));

            map.put("y",Double.toString(longitude));
            map.put("username", userId);
            map.put("password", userPwd);
            String path = "";
            try {
                path = Tools.sendPOSTPath(map);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("5","tools.sendpostpath有问题");
            }
            if(!"".equals(path)) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(huc.getOutputStream()));
                bw.write(path);
                bw.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                flag = br.read();

                //关闭流
                bw.close();
                br.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 教师端在点名的过程中，不断向服务器请求数据所调用的函数
     * 参数：教师id，密码
     * 返回值：当前已点名的学生List
     */
    public List<Student> TeacherAskForData(String userId,String userPwd,double longitude,double latitude){
        String temp;
        String result = null;
        try {
            url = new URL(TEACHER_PATH);
            HttpURLConnection huc = (HttpURLConnection)url.openConnection();
            huc.setDoOutput(true);
            huc.setDoInput(true);
            huc.setRequestMethod("POST");
            huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            huc.setRequestProperty("Charset", "utf-8");

            Map<String,String> map = new HashMap<String, String>();
            map.put("x",Double.toString(latitude));
            map.put("y",Double.toString(longitude));
            map.put("username", userId);
            map.put("password", userPwd);
            String path = "";
            try {
                path = Tools.sendPOSTPath(map);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("5","tools.sendpostpath有问题");
            }
            if(!"".equals(path)) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(huc.getOutputStream()));
                bw.write(path);
                bw.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream()));

                result = br.readLine();
                result = URLDecoder.decode(result,"utf-8");

                //关闭流
                bw.close();
                br.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        List<Student> list = gson.fromJson(result,new TypeToken<List<Student>>(){}.getType());
        return list;
    }

}

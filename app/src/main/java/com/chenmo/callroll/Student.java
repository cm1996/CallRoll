package com.chenmo.callroll;

public class Student {
    private String sId;
    private String sName;

    public Student(String a,String b,String c,String d){
        sId = a;
        sName = b;
    }


    public String getsId(){
        return sId;
    }
    public String getsName(){
        return sName;
    }
    public void setsId(String id){
        sId = id;
    }
    public void setsName(String name){
        sName = name;
    }
}

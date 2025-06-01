package com.example.myapplication;
import android.os.Build;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class User {
    public String status = "";//登陆状态
    public String name = "";//儿童姓名
    public String birthday = "2024-05-03";//儿童生日
    public int gender =0;//0女
    public int age = 0;//儿童年龄
    public String school = "xxx";//儿童学校
    public String schoolClass = "xxx";//儿童学校
    public int totalTime = 0;//总体使用时间
    public int todayTime = 0;//今日使用时间
    public int Time1 = 0;//昨日使用时间
    public int Time2 = 0;//前天使用时间
    public int Time3 = 0;//大前天
    public int Time4= 0;//大大前天
    public int child_id = 0;

    public void initUser(String status, String name, String gender,String birthday, String school, String schoolClass,int todayTime, int Time1, int Time2, int Time3, int Time4,int child_id) {
        this.status = status;
        this.name = name;
        if(gender.equals("M"))
            this.gender = 1;
        this.birthday = birthday;
        this.age = getAge(birthday);
        this.school = school;
        this.schoolClass = schoolClass;

        this.todayTime = todayTime;
        this.Time1 = Time1;
        this.Time2 = Time2;
        this.Time3 = Time3;
        this.Time4 = Time4;
        this.totalTime = todayTime+Time1+Time2+Time3+Time4;
        this.child_id = child_id;
    }
    public int getAge(String birthday){
        int age=0;
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }
        LocalDate birthDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            birthDate = LocalDate.parse(birthday, formatter);
        }
        LocalDate currentDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        Period period = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            period = Period.between(birthDate, currentDate);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            age = period.getYears();
        }
        return age;
    }
}

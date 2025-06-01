package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class title extends AppCompatActivity {


    private String account = "admin";
    private String password = "admin";                //输入的用户名和密码
    private int count = 0;   //用来判断错误次数

    String status = "";//登陆状态

    public static User currentUser = new User();

    OkHttpClient client = new OkHttpClient().newBuilder()
            .build();

    String user = "";
    String Password = "";

    @SuppressLint("ClickableViewAccessibility")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = findViewById(R.id.view10);
                linearLayout.setVisibility(View.VISIBLE);
            }
        });
        Button certain = (Button) findViewById(R.id.change_con);
        certain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRegister();
                resetData();
                LinearLayout linearLayout = findViewById(R.id.view10);
                linearLayout.setVisibility(View.GONE);
            }
        });
        Button reset =(Button)findViewById(R.id.change_reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetData();
            }
        });
// 获取 LinearLayout 对象
        LinearLayout linearLayout = findViewById(R.id.linear);
        linearLayout.setAlpha(0f);

        LinearLayout linearLayout1 = findViewById(R.id.linear_logo);
// 获取 LinearLayout 对象
        FrameLayout frameLayout = findViewById(R.id.frame);
        frameLayout.setAlpha(0f);
// 创建一个属性动画对象，设置动画的属性为 "translationY"，即垂直方向的平移
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(frameLayout, "alpha", 0f, 1f);
        ObjectAnimator animator = ObjectAnimator.ofFloat(frameLayout, "translationY", -800f, 0f);
// 设置动画的持续时间
        animator.setDuration(1000);
        animator3.setDuration(1500);
// 启动动画
        animator.start();
        animator3.start();

// 创建一个透明度动画，将 LinearLayout 的透明度从 0 渐变到 1
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(linearLayout, "alpha", 0f, 1f);
// 设置动画的持续时间
        animator2.setDuration(1000);
// 启动动画
        animator2.start();
//登录用户与密码实例
        EditText accountET = findViewById(R.id.account);    //获取控件
        EditText passwordET = findViewById(R.id.password);
        Button login = findViewById(R.id.login);

        // SharedPreferences sp = getSharedPreferences("tao", MODE_PRIVATE);
        //获得SharedPreferences，并创建文件名为tao
        //SharedPreferences.Editor editor = sp.edit();
        //获得Editor对象，用于储存用户信息


        login.setOnClickListener(new View.OnClickListener() {         //点击事件监听器
            @Override
            public void onClick(View view) {
                user = accountET.getText().toString();
                password = passwordET.getText().toString();
                if (user.isEmpty() || password.isEmpty()) {
                    Toast.makeText(title.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    //同为admin为管理员 否则调用APILogin发送login请求，如果后端返回了登录成功
                    if (user.equals("admin") && password.equals("admin") || APILogin(user, password)) {
                        //弹窗
                        Toast.makeText(title.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        //当前页面逐渐消失
                        ObjectAnimator animator11 = ObjectAnimator.ofFloat(frameLayout, "alpha", 1f, 0f);
                        ObjectAnimator animator12 = ObjectAnimator.ofFloat(linearLayout, "alpha", 1f, 0f);
                        AnimatorSet animatorSet = new AnimatorSet();
                        // 设置两个动画同时执行
                        animatorSet.playTogether(animator11, animator12);
                        // 设置动画的持续时间
                        animatorSet.setDuration(1000);
                        // 启动动画
                        animatorSet.start();

                        //跳转登陆后页面
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//这俩可以简单理解为：如果有该activity则直接跳转，避免重复创建很多MainActivity
                        intent.setClass(title.this, Mainactivity.class);//设置跳转到哪儿
                        startActivity(intent);
                        finish();
                    }

                    else {
                        count += 1;
                        Toast.makeText(title.this, "用户名或密码错误，登陆失败,已错误" + count + "次", Toast.LENGTH_SHORT).show();
                        if (count == 10) {
                            Toast.makeText(title.this, "账户密码输入错误累计10次，已退出活动", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
            }

        });
    }

    private void resetData() {
        EditText Edit_name = findViewById(R.id.change_name);
        EditText Edit_bir = findViewById(R.id.change_birthday);
        EditText Edit_user = findViewById(R.id.change_user);
        EditText Edit_password = findViewById(R.id.change_password);
        Edit_user.setText("");
        Edit_name.setText("");
        Edit_bir.setText("");
        Edit_password.setText("");
    }

    private void postRegister() {
        EditText Edit_name = findViewById(R.id.change_name);
        EditText Edit_bir = findViewById(R.id.change_birthday);
        EditText Edit_user = findViewById(R.id.change_user);
        EditText Edit_password = findViewById(R.id.change_password);
        if(Edit_name.getText().toString().isEmpty()||Edit_bir.getText().toString().isEmpty()||Edit_user.getText().toString().isEmpty()||Edit_password.getText().toString().isEmpty()) {
            Toast.makeText(title.this, "注册信息不可为空", Toast.LENGTH_SHORT).show();
            return;
        }
        int age =0;
        age = currentUser.getAge(Edit_bir.getText().toString());

        int finalAge1 = age;
        Thread thread = new Thread(() -> {
            // 创建一个RequestBody对象，包含你要发送的数据
            RequestBody body = new FormBody.Builder()
                    .add("user_type","child")
                    .add("username", Edit_user.getText().toString())
                    .add("password", Edit_password.getText().toString())
                    .add("age", String.valueOf(finalAge1))
                    .add("birth_date",Edit_bir.getText().toString())
                    .build();
            // 创建一个Request对象
            Request request = new Request.Builder()
                    .url("http://120.53.102.245:8000/register/")
                    .post(body)
                    .build();
            // 使用OkHttpClient发送请求
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(title.this, "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join(); // 主线程会等待thread线程执行完成后再继续执行
        } catch (InterruptedException e) {
            // 处理中断异常
            e.printStackTrace();
        }


    }

    public boolean APILogin(String user, String password) {
        System.out.println("User");
        Thread thread = new Thread(() -> {
            // 创建一个RequestBody对象，包含你要发送的数据
            RequestBody body = new FormBody.Builder()
                    .add("username","mancheems")
                    .add("password","mancheems")
                    .build();
            // 创建一个Request对象
            Request request = new Request.Builder()
                    .url("http://120.53.102.245:8000/login/")
                    .post(body)
                    .build();
            // 使用OkHttpClient发送请求
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    status = jsonObject.getString("status");
                    currentUser.initUser(
                            status,jsonObject.getString("name"),
                            jsonObject.getString("gender"),
                            jsonObject.getString("birth_date"),
                            jsonObject.getString("school"),
                            jsonObject.getString("class_name"),
                            Integer.parseInt(jsonObject.getString("usage_1")),
                            Integer.parseInt(jsonObject.getString("usage_2")),
                            Integer.parseInt(jsonObject.getString("usage_3")),
                            Integer.parseInt(jsonObject.getString("usage_4")),
                            Integer.parseInt(jsonObject.getString("usage_5")),
                            Integer.parseInt(jsonObject.getString("child_id")));

                    System.out.println("User"+currentUser.name);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join(); // 主线程会等待thread线程执行完成后再继续执行
        } catch (InterruptedException e) {
            // 处理中断异常
            e.printStackTrace();
        }
        return status.equals("success");
    }

}
package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import androidx.appcompat.widget.AppCompatButton;

// 现在直接使用 title.currentUser

public class PersonInformation extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
    TextView today_hour,total_hour,today_min,total_min;
    TextView name,gender,data,age,school,classroom;
    EditText change_name,change_birthday,change_gender,change_school,change_class;
    AppCompatButton change_reset,change_con,change;
    String changedName,changedBirthday,changedSchool,changedClass;
    int changedAge,changedGender;

    ImageButton backButton;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jianduertong);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        today_hour = findViewById(R.id.today_hours);
        today_min = findViewById(R.id.today_mins);
        total_hour = findViewById(R.id.total_hours);
        total_min = findViewById(R.id.total_mins);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        school = findViewById(R.id.school);
        data = findViewById(R.id.data);
        classroom = findViewById(R.id.classroom);
        change_name = findViewById(R.id.change_name);
        change_birthday = findViewById(R.id.change_birthday);
        change_gender = findViewById(R.id.change_gender);
        change_school = findViewById(R.id.change_school);
        change_class = findViewById(R.id.change_class);
        change_reset = (AppCompatButton) findViewById(R.id.change_reset);
        change_con = (AppCompatButton) findViewById(R.id.change_con);
        change = (AppCompatButton)findViewById(R.id.change);

        try {
            initInformation();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        // 创建一个日期格式化器
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日", Locale.CHINA);
// 获取当前日期
        Calendar calendar = Calendar.getInstance();
        String today = sdf.format(calendar.getTime());
// 获取前一天的日期
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String yesterday = sdf.format(calendar.getTime());
// 获取前两天的日期
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String dayBeforeYesterday = sdf.format(calendar.getTime());
// 获取前三天的日期
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String threeDaysAgo = sdf.format(calendar.getTime());
// 获取前四天的日期
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String fourDaysAgo = sdf.format(calendar.getTime());
        BarChart chart = (BarChart) findViewById(R.id.barChart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, title.currentUser.Time4));
        entries.add(new BarEntry(1, title.currentUser.Time3));
        entries.add(new BarEntry(2, title.currentUser.Time2));
        entries.add(new BarEntry(3, title.currentUser.Time1));
        entries.add(new BarEntry(4, title.currentUser.todayTime));
        int color = ContextCompat.getColor(PersonInformation.this, R.color.blue_bg);
        BarDataSet dataset = new BarDataSet(entries, "使用时间/分钟");
        dataset.setColor(color );
        dataset.setValueTextColor(color);
        dataset.setValueTextSize(10.2f);
        ArrayList<String> labels = new ArrayList<String>();
        labels.add(fourDaysAgo);
        labels.add(threeDaysAgo);
        labels.add(dayBeforeYesterday);
        labels.add(yesterday);
        labels.add(today);

        BarData data = new BarData(dataset);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(color);
        xAxis.setGranularity(1f);
        YAxis yAxisRight = chart.getAxisRight();
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setEnabled(false);
        yAxisLeft.setTextColor(color );

        chart.setData(data);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        Legend legend = chart.getLegend();
        legend.setTextColor(color );

        ImageButton imageButton = findViewById(R.id.backbutton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 直接返回上一个页面，更简单的实现
                finish();
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changedName = title.currentUser.name;
                changedBirthday = title.currentUser.birthday;
                changedAge = title.currentUser.age;
                changedGender = title.currentUser.gender;
                changedSchool = title.currentUser.school;
                changedClass = title.currentUser.schoolClass;
                LinearLayout linearLayout = findViewById(R.id.view10);
                linearLayout.setVisibility(View.VISIBLE);

            }
        });
        change_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    changeData();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                LinearLayout linearLayout = findViewById(R.id.view10);
                linearLayout.setVisibility(View.GONE);
                resetEdit();
            }
        });
        change_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetData();
            }
        });
    }



    @SuppressLint("SetTextI18n")
    public void initInformation() throws ParseException {
        updateData();
    }
    public void changeData() throws ParseException {
        if(!String.valueOf(change_name.getText()).isEmpty()) {
            changedName = String.valueOf(change_name.getText());
            title.currentUser.name = changedName;
        }
        System.out.println(changedBirthday);
        if(!String.valueOf(change_birthday.getText()).isEmpty()) {
            changedBirthday = String.valueOf(change_birthday.getText());
            title.currentUser.birthday = changedBirthday;
        }
        if(!String.valueOf(change_birthday.getText()).isEmpty()) {
            changedAge = title.currentUser.getAge(changedBirthday);
            title.currentUser.age = changedAge;
        }
        if(!String.valueOf(change_school.getText()).isEmpty()) {
            changedSchool = String.valueOf(change_school.getText());
            title.currentUser.school = changedSchool;
        }
        if(!String.valueOf(change_class.getText()).isEmpty()) {
            changedClass = String.valueOf(change_class.getText());
            title.currentUser.schoolClass = changedClass;
        }
        if(!String.valueOf(change_gender.getText()).isEmpty()) {
            if (String.valueOf(change_gender.getText()).equals("女")) {
                changedGender = 0;
            } else {
                changedGender = 1;
            }
            title.currentUser.gender = changedGender;
        }
        updateData();
        postData();

    }

    private void postData() {
        Thread thread = new Thread(() -> {
            String g = "M";
            if(title.currentUser.gender==0)
                g = "F";
            // 创建一个RequestBody对象，包含你要发送的数据
            RequestBody body = new FormBody.Builder()
                    .add("name", title.currentUser.name)
                    .add("birthday", title.currentUser.birthday)
                    .add("gender",g)
                    .add("school", title.currentUser.school)
                    .add("class_name", title.currentUser.schoolClass)
                    .build();
            // 创建一个Request对象
            Request request = new Request.Builder()
                    .url("http://120.53.102.245:8000/update_info/4/")
                    .post(body)
                    .build();
            // 使用OkHttpClient发送请求
            try {
                Response response = client.newCall(request).execute();
                System.out.println("response:"+ response.isSuccessful());
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();

    }

    @SuppressLint("SetTextI18n")
    public void updateData() throws ParseException {
        today_hour.setText(Integer.toString(title.currentUser.todayTime/60));
        System.out.println(title.currentUser.todayTime/60);
        today_min.setText(Integer.toString(title.currentUser.todayTime- title.currentUser.todayTime/60*60));
        total_hour.setText(Integer.toString(title.currentUser.totalTime/60));
        total_min.setText(Integer.toString(title.currentUser.totalTime- title.currentUser.totalTime/60*60));
        name.setText(title.currentUser.name);
        String oldFormat = "yyyy-MM-dd";
        String newFormat = "yyyy年MM月dd日";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf1 = new SimpleDateFormat(oldFormat);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf2 = new SimpleDateFormat(newFormat);
        data.setText(sdf2.format(Objects.requireNonNull(sdf1.parse(title.currentUser.birthday))));
        age.setText(Integer.toString(title.currentUser.age));
        if(title.currentUser.gender == 0)
            gender.setText("女");
        else
            gender.setText("男");
        school.setText(title.currentUser.school);
        classroom.setText(title.currentUser.schoolClass);

    }
    public void resetEdit(){
        change_name.setText("");
        change_birthday.setText("");
        change_gender.setText("");
        change_school.setText("");
        change_class.setText("");

    }
    private void resetData() {
    }
}
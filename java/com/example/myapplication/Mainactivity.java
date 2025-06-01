package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Mainactivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private View btnSucaicangku;
    private View btnDianpingseka;
    private View btnJianduertong;
    private View btnHechengchipin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        // 检查并请求权限
        checkAndRequestPermissions();

        initViews();
        setupListeners();
    }

    private void checkAndRequestPermissions() {
        // 需要请求的权限列表
        String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        // 检查是否已经获得权限
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        // 如果有权限未获得，则请求权限
        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                Toast.makeText(this, "需要相机和录音权限才能使用完整功能", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initViews() {
        btnSucaicangku = findViewById(R.id.sucaicangku);
        btnDianpingseka = findViewById(R.id.dianpingseka);
        btnJianduertong = findViewById(R.id.jianduertong);
        btnHechengchipin = findViewById(R.id.hechengchipin);

        // 添加调试代码
        if (btnDianpingseka == null) {
            Toast.makeText(this, "点评色卡按钮初始化失败", Toast.LENGTH_SHORT).show();
        }
        if (btnJianduertong == null) {
            Toast.makeText(this, "监督儿童按钮初始化失败", Toast.LENGTH_SHORT).show();
        }
        if (btnHechengchipin == null) {
            Toast.makeText(this, "合成视频按钮初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        // 素材仓库按钮点击事件
        btnSucaicangku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查权限是否已授予
                if (ContextCompat.checkSelfPermission(Mainactivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(Mainactivity.this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Mainactivity.this, "请先授予相机和录音权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Mainactivity.this, activity_sucaicangku.class);
                startActivity(intent);
            }
        });

        // 点评色卡按钮点击事件
        btnDianpingseka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Mainactivity.this, myLoveCard.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Mainactivity.this,
                        "跳转失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 监督儿童按钮点击事件
        btnJianduertong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Mainactivity.this, PersonInformation.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Mainactivity.this,
                        "跳转失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 合成视频按钮点击事件
        btnHechengchipin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Mainactivity.this, VideoSynthesisActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Mainactivity.this,
                        "跳转失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

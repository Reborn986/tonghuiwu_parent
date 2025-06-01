package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class VideoSynthesisActivity extends AppCompatActivity {
    private AppCompatButton selectImageBtn, selectVoiceBtn, synthesizeAudioBtn, synthesizeVideoBtn;
    private ImageButton backButton;
    private ImageView imagePreview;
    private EditText inputText;
    private ProgressBar progressBar;
    private VideoView videoView;
    private Uri selectedImageUri;
    private Uri selectedVoiceUri;
    private Handler handler;
    private int selectedImageResId = -1;
    private String selectedEmotion = "";

    // 添加预置图片资源ID数组
    private int[] presetImages = {
        R.drawable.happy_1,    // 开心表情
        R.drawable.encourage_1, // 鼓励表情
        R.drawable.sad_1,      // 悲伤表情
        R.drawable.serious_1   // 严厉表情
    };

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imagePreview.setImageURI(uri);
                    imagePreview.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                }
            }
    );

    // 注册从SelectImageActivity返回的结果处理
    private final ActivityResultLauncher<Intent> selectImageActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    selectedImageResId = data.getIntExtra("selected_image_res_id", -1);
                    selectedEmotion = data.getStringExtra("selected_emotion");

                    if (selectedImageResId != -1) {
                        // 显示选中的图片
                        imagePreview.setImageResource(selectedImageResId);
                        imagePreview.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.GONE);

                        // 显示选中的情绪
                        String emotionName = getEmotionName(selectedEmotion);
                        Toast.makeText(this, "已选择" + emotionName + "表情", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> voicePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedVoiceUri = uri;
                    Toast.makeText(this, "语音文件已选择", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_synthesis);

        handler = new Handler(Looper.getMainLooper());
        initViews();
        setupButtonListeners();
        checkPermissions();
        setupVideoView();
    }

    private void initViews() {
        selectImageBtn = findViewById(R.id.selectImageBtn);
        selectVoiceBtn = findViewById(R.id.selectVoiceBtn);
        synthesizeAudioBtn = findViewById(R.id.synthesizeAudioBtn);
        synthesizeVideoBtn = findViewById(R.id.synthesizeVideoBtn);
        backButton = findViewById(R.id.back_button);
        imagePreview = findViewById(R.id.imagePreview);
        inputText = findViewById(R.id.inputText);
        progressBar = findViewById(R.id.progressBar);
        videoView = findViewById(R.id.videoView);
    }

    private void setupVideoView() {
        videoView.setVisibility(View.GONE);
        videoView.setOnCompletionListener(mp -> {
            // 视频播放完成后的处理
            mp.start(); // 循环播放
        });
    }

    private void setupButtonListeners() {
        selectImageBtn.setOnClickListener(v -> {
            // 直接显示情绪选择对话框
            showEmotionSelectDialog();
        });
        selectVoiceBtn.setOnClickListener(v -> {
            Intent intent = new Intent(VideoSynthesisActivity.this, SelectAudioActivity.class);
            startActivity(intent);
        });
        synthesizeAudioBtn.setOnClickListener(v -> synthesizeAudio());
        synthesizeVideoBtn.setOnClickListener(v -> synthesizeVideo());
        backButton.setOnClickListener(v -> finish()); // 返回上一个页面
    }

    // 显示情绪选择对话框
    private void showEmotionSelectDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
            new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_emotion_select, null);
        bottomSheetDialog.setContentView(dialogView);

        // 设置圆角背景
        View parentView = (View) dialogView.getParent();
        parentView.setBackgroundResource(R.drawable.bottom_sheet_background);

        // 获取预览图片视图
        ImageView previewPhoto = dialogView.findViewById(R.id.preview_photo);

        // 如果已经选择了图片，显示在预览区域
        if (selectedImageResId != -1) {
            previewPhoto.setImageResource(selectedImageResId);
        }

        // 关闭按钮
        dialogView.findViewById(R.id.btn_close).setOnClickListener(v -> bottomSheetDialog.dismiss());

        // 设置情绪按钮点击事件
        dialogView.findViewById(R.id.btn_happy).setOnClickListener(v -> {
            selectEmotionImage("happy");
            bottomSheetDialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_encourage).setOnClickListener(v -> {
            selectEmotionImage("encourage");
            bottomSheetDialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_sad).setOnClickListener(v -> {
            selectEmotionImage("sad");
            bottomSheetDialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_serious).setOnClickListener(v -> {
            selectEmotionImage("serious");
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    // 选择情绪图片
    private void selectEmotionImage(String emotion) {
        selectedEmotion = emotion;

        // 根据情绪选择对应的图片资源ID
        switch (emotion) {
            case "happy":
                selectedImageResId = R.drawable.happy_1;
                break;
            case "encourage":
                selectedImageResId = R.drawable.encourage_1;
                break;
            case "sad":
                selectedImageResId = R.drawable.sad_1;
                break;
            case "serious":
                selectedImageResId = R.drawable.serious_1;
                break;
        }

        // 显示选中的图片
        if (selectedImageResId != -1) {
            imagePreview.setImageResource(selectedImageResId);
            imagePreview.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);

            // 显示选中的情绪
            String emotionName = getEmotionName(selectedEmotion);
            Toast.makeText(this, "已选择" + emotionName + "表情", Toast.LENGTH_SHORT).show();
        }
    }

    // 获取情绪名称
    private String getEmotionName(String emotion) {
        if (emotion == null) return "";

        switch (emotion) {
            case "happy": return "开心";
            case "encourage": return "鼓励";
            case "sad": return "伤心";
            case "serious": return "严肃";
            default: return "";
        }
    }

    // 合成视频方法
    private void synthesizeVideo() {
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "正在合成视频...", Toast.LENGTH_SHORT).show();

        // 模拟合成视频过程
        handler.postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "视频合成完成！", Toast.LENGTH_SHORT).show();

            // 播放raw文件夹中的video1视频
            playVideo("video1");
        }, 2000);
    }

    // 播放指定名称的视频
    private void playVideo(String videoName) {
        // 隐藏图片预览，显示视频
        imagePreview.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        try {
            // 获取视频资源ID
            int videoResourceId = getResources().getIdentifier(videoName, "raw", getPackageName());
            if (videoResourceId == 0) {
                Toast.makeText(this, "未找到视频资源，请确保在raw文件夹中添加" + videoName + "视频文件", Toast.LENGTH_LONG).show();
                return;
            }

            String videoPath = "android.resource://" + getPackageName() + "/" + videoResourceId;
            videoView.setVideoURI(Uri.parse(videoPath));
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);  // 设置循环播放
                videoView.start();
            });

            videoView.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(VideoSynthesisActivity.this, "视频播放出错，请检查视频文件", Toast.LENGTH_SHORT).show();
                return true;
            });
        } catch (Exception e) {
            Toast.makeText(this, "视频播放失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }

    private void synthesizeAudio() {
        String text = inputText.getText().toString();
        if (text.isEmpty()) {
            Toast.makeText(this, "请输入合成语音的内容", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "正在合成语音...", Toast.LENGTH_SHORT).show();

        // 模拟合成语音过程
        handler.postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "语音合成完成！", Toast.LENGTH_SHORT).show();
            // 模拟播放音频
            simulateVideo();
        }, 3000);
        }

    // 模拟视频播放
    private void simulateVideo() {
        // 使用通用的播放视频方法
        playVideo("preset_video");
    }

    // 设置图片到预览区域
    private void setPhotoToPlaceholder(Bitmap photo, int index) {
        if (photo != null) {
            imagePreview.setImageBitmap(photo);
            imagePreview.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);

            // 这里可以保存图片路径等信息
            // 由于这是一个演示，我们只是显示图片
            Toast.makeText(this, "已加载图片 " + (index + 1), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (videoView != null) {
            videoView.stopPlayback();
        }
        super.onDestroy();
    }
}
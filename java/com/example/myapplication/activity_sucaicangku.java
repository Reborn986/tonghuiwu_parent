package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class activity_sucaicangku extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView currentPhotoPlaceholder;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;
    private boolean isRecording = false;
    private ImageButton btnMic;
    private ImageButton btnPlay;
    private SeekBar seekbarVoice;
    private View[] photoPlaceholders = new View[4];
    private ArrayList<String> selectedPhotoPaths = new ArrayList<>();
    private ArrayList<String> selectedAudioPaths = new ArrayList<>();

    // 添加预置图片资源ID数组
    private int[] presetImages = {
        R.drawable.happy_1,    // 开心表情
        R.drawable.encourage_1, // 鼓励表情
        R.drawable.sad_1,      // 悲伤表情
        R.drawable.serious_1   // 严厉表情
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucaicangku);

        initViews();
        setupListeners();
    }

    private void initViews() {
        photoPlaceholders[0] = findViewById(R.id.photo_placeholder_1);
        photoPlaceholders[1] = findViewById(R.id.photo_placeholder_2);
        photoPlaceholders[2] = findViewById(R.id.photo_placeholder_3);
        photoPlaceholders[3] = findViewById(R.id.photo_placeholder_4);
        
        btnMic = findViewById(R.id.btn_mic);
        btnPlay = findViewById(R.id.btn_play);
        seekbarVoice = findViewById(R.id.seekbar_voice);

        // 设置音频文件路径
        audioFilePath = getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/voice_record.mp3";
    }

    private void setupListeners() {
        // 设置返回按钮点击事件
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            // 返回主页面
            finish();
        });
        
        // 设置拍照按钮点击事件
        findViewById(R.id.btn_camera2).setOnClickListener(v -> dispatchTakePictureIntent());

        // 设置预置图片按钮点击事件
        findViewById(R.id.btn_load_preset).setOnClickListener(v -> importPresetImages());

        // 设置录音按钮点击事件
        btnMic.setOnClickListener(v -> {
            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        });

        // 设置播放按钮点击事件
        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                pauseAudio();
            } else {
                playAudio();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                showEmotionSelectDialog(imageBitmap);
            }
        }
    }

    private void showEmotionSelectDialog(final Bitmap photo) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_emotion_select, null);
        bottomSheetDialog.setContentView(dialogView);

        // 设置圆角背景
        View parentView = (View) dialogView.getParent();
        parentView.setBackgroundResource(R.drawable.bottom_sheet_background);

        // 设置预览图片
        ImageView previewPhoto = dialogView.findViewById(R.id.preview_photo);
        previewPhoto.setImageBitmap(photo);

        // 关闭按钮
        dialogView.findViewById(R.id.btn_close).setOnClickListener(v -> bottomSheetDialog.dismiss());

        // 设置情绪按钮点击事件
        dialogView.findViewById(R.id.btn_happy).setOnClickListener(v -> {
            setPhotoToPlaceholder(photo, 0);
            bottomSheetDialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_encourage).setOnClickListener(v -> {
            setPhotoToPlaceholder(photo, 1);
            bottomSheetDialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_sad).setOnClickListener(v -> {
            setPhotoToPlaceholder(photo, 2);
            bottomSheetDialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_serious).setOnClickListener(v -> {
            setPhotoToPlaceholder(photo, 3);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void setPhotoToPlaceholder(Bitmap photo, int index) {
        if (index >= 0 && index < photoPlaceholders.length) {
            if (photoPlaceholders[index] instanceof ImageView) {
                ((ImageView) photoPlaceholders[index]).setImageBitmap(photo);
                // 保存图片到内部存储并获取路径
                String imagePath = saveImageToInternalStorage(photo, "photo_" + index + ".jpg");
                if (imagePath != null) {
                    selectedPhotoPaths.add(imagePath);
                }
            } else {
                // 如果是View，需要先将其转换为ImageView
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(photoPlaceholders[index].getLayoutParams());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageBitmap(photo);
                
                // 替换原来的View
                View originalView = photoPlaceholders[index];
                ((android.view.ViewGroup) originalView.getParent()).addView(imageView, 
                    ((android.view.ViewGroup) originalView.getParent()).indexOfChild(originalView));
                ((android.view.ViewGroup) originalView.getParent()).removeView(originalView);
                photoPlaceholders[index] = imageView;
            }
        }
    }

    // 添加保存图片的方法
    private String saveImageToInternalStorage(Bitmap bitmap, String fileName) {
        try {
            // 获取应用的内部存储目录
            File directory = getDir("images", Context.MODE_PRIVATE);
            File file = new File(directory, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setMaxDuration(50000); // 50秒
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            btnMic.setImageResource(R.drawable.ic_mic_recording); // 需要添加录音中的图标
            Toast.makeText(this, "开始录音，最长50秒", Toast.LENGTH_SHORT).show();

            // 50秒后自动停止
            new android.os.Handler().postDelayed(() -> {
                if (isRecording) {
                    stopRecording();
                }
            }, 50000);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "录音失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
                btnMic.setImageResource(R.drawable.ic_mic);
                // 保存录音文件路径
                selectedAudioPaths.add(audioFilePath);
                Toast.makeText(this, "录音完成", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void playAudio() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioFilePath);
                mediaPlayer.prepare();
                
                // 设置进度条
                seekbarVoice.setMax(mediaPlayer.getDuration());
                seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser && mediaPlayer != null) {
                            mediaPlayer.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        mediaPlayer.start();
        btnPlay.setImageResource(R.drawable.ic_pause); // 需要添加暂停图标

        // 更新进度条
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    seekbarVoice.setProgress(mediaPlayer.getCurrentPosition());
                    new android.os.Handler().postDelayed(this, 100);
                }
            }
        }, 100);
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlay.setImageResource(R.drawable.ic_play);
        }
    }

    // 导入预置图片的方法
    private void importPresetImages() {
        try {
            // 清空之前的选择
            selectedPhotoPaths.clear();
            
            // 导入预置图片
            for (int i = 0; i < presetImages.length; i++) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), presetImages[i]);
                if (bitmap != null) {
                    // 保存图片并获取路径
                    String imagePath = saveImageToInternalStorage(bitmap, "preset_photo_" + i + ".jpg");
                    if (imagePath != null) {
                        selectedPhotoPaths.add(imagePath);
                        
                        // 更新UI显示
                        if (photoPlaceholders[i] instanceof ImageView) {
                            ((ImageView) photoPlaceholders[i]).setImageBitmap(bitmap);
                        } else {
                            ImageView imageView = new ImageView(this);
                            imageView.setLayoutParams(photoPlaceholders[i].getLayoutParams());
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imageView.setImageBitmap(bitmap);
                            
                            View originalView = photoPlaceholders[i];
                            ((android.view.ViewGroup) originalView.getParent()).addView(imageView, 
                                ((android.view.ViewGroup) originalView.getParent()).indexOfChild(originalView));
                            ((android.view.ViewGroup) originalView.getParent()).removeView(originalView);
                            photoPlaceholders[i] = imageView;
                        }
                    }
                }
            }
            Toast.makeText(this, "预置图片导入成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "预置图片导入失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void startVideoSynthesis() {
        if (selectedPhotoPaths.isEmpty()) {
            Toast.makeText(this, "请先选择图片", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建Intent并传递选中的图片和音频路径
        Intent intent = new Intent(this, VideoSynthesisActivity.class);
        intent.putStringArrayListExtra("selected_photos", selectedPhotoPaths);
        intent.putStringArrayListExtra("selected_audios", selectedAudioPaths);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

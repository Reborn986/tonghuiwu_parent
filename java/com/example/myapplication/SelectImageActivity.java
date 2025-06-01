package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;

public class SelectImageActivity extends AppCompatActivity {
    private RecyclerView imageRecyclerView;
    private List<Integer> emotionImages;

    // 预置的情绪图片资源ID
    private int[] presetImages = {
        R.drawable.happy_1,    // 开心表情
        R.drawable.encourage_1, // 鼓励表情
        R.drawable.sad_1,      // 悲伤表情
        R.drawable.serious_1   // 严厉表情
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);

        // 设置返回按钮
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回上一个页面
            }
        });

        // 初始化情绪图片列表
        initEmotionImages();

        // 设置RecyclerView
        imageRecyclerView = findViewById(R.id.imageRecyclerView);
        imageRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2列网格布局

        // 创建并设置适配器
        EmotionImageAdapter adapter = new EmotionImageAdapter(emotionImages);
        imageRecyclerView.setAdapter(adapter);
    }

    // 初始化情绪图片列表
    private void initEmotionImages() {
        emotionImages = new ArrayList<>();
        for (int imageRes : presetImages) {
            emotionImages.add(imageRes);
        }
    }

    // 显示情绪选择对话框
    private void showEmotionSelectDialog(int imageResId) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_emotion_select, null);
        bottomSheetDialog.setContentView(dialogView);

        // 设置圆角背景
        View parentView = (View) dialogView.getParent();
        parentView.setBackgroundResource(R.drawable.bottom_sheet_background);

        // 设置预览图片
        ImageView previewPhoto = dialogView.findViewById(R.id.preview_photo);
        Bitmap photo = BitmapFactory.decodeResource(getResources(), imageResId);
        previewPhoto.setImageBitmap(photo);

        // 关闭按钮
        dialogView.findViewById(R.id.btn_close).setOnClickListener(v -> bottomSheetDialog.dismiss());

        // 设置情绪按钮点击事件
        dialogView.findViewById(R.id.btn_happy).setOnClickListener(v -> {
            selectEmotionImage(imageResId, "happy");
            bottomSheetDialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_encourage).setOnClickListener(v -> {
            selectEmotionImage(imageResId, "encourage");
            bottomSheetDialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_sad).setOnClickListener(v -> {
            selectEmotionImage(imageResId, "sad");
            bottomSheetDialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_serious).setOnClickListener(v -> {
            selectEmotionImage(imageResId, "serious");
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    // 选择情绪图片并返回结果
    private void selectEmotionImage(int imageResId, String emotion) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected_image_res_id", imageResId);
        resultIntent.putExtra("selected_emotion", emotion);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "已选择" + getEmotionName(emotion) + "表情", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 获取情绪名称
    private String getEmotionName(String emotion) {
        switch (emotion) {
            case "happy": return "开心";
            case "encourage": return "鼓励";
            case "sad": return "伤心";
            case "serious": return "严肃";
            default: return "";
        }
    }

    // 情绪图片适配器
    private class EmotionImageAdapter extends RecyclerView.Adapter<EmotionImageAdapter.ViewHolder> {
        private List<Integer> images;

        public EmotionImageAdapter(List<Integer> images) {
            this.images = images;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_emotion_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int imageResId = images.get(position);
            holder.imageView.setImageResource(imageResId);

            holder.itemView.setOnClickListener(v -> {
                showEmotionSelectDialog(imageResId);
            });
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.emotion_image);
            }
        }
    }
}
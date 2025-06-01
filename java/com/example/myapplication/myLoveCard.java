package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.widget.VideoView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.bumptech.glide.Glide;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.widget.TextView;
import android.widget.EditText;

public class myLoveCard extends AppCompatActivity {
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int REQUEST_VIDEO_PICK = 2;

    private List<ImageCard> dataSource = new ArrayList<>();
    private CommonAdapter<ImageCard> adapter;
    private ImageButton back_button;
    private MediaRecorder mediaRecorder;
    private String currentAudioPath;
    private String currentVideoPath;
    private Dialog commentDialog;
    private VideoView videoPreview;

    // 存储评论的Map，key是图片ID，value是评论列表
    private Map<Integer, List<String>> commentsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_love_card);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化返回按钮
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> {
            // 返回主页面
            finish();
        });

        adapter = new CommonAdapter<ImageCard>(this, R.layout.item_card, dataSource) {
            @Override
            protected void convert(ViewHolder viewHolder, ImageCard picture, int i) {
                // 获取图片视图但不加载图片内容
                ImageView imageView = viewHolder.getView(R.id.picture);
                // 设置为透明背景或浅灰色背景
                imageView.setImageResource(android.R.color.transparent);
                // 可以设置边框或其他样式，使其看起来像一个空白的图片框

                // 设置点赞按钮状态
                ImageButton btnLike = viewHolder.getView(R.id.btn_like);
                // 根据点赞状态设置图标
                if(picture.status == 1) {
                    // 已点赞状态 - 使用实心的点赞图标
                    btnLike.setImageResource(R.drawable.ic_like_filled);
                } else {
                    // 未点赞状态 - 使用空心的点赞图标
                    btnLike.setImageResource(R.drawable.ic_like_outline);
                }

                // 点赞按钮点击事件
                viewHolder.setOnClickListener(R.id.btn_like, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(picture.status == 0) {
                            // 本地处理点赞状态，不调用后端
                            picture.status = 1;
                            // 切换为实心点赞图标
                            ((ImageButton)view).setImageResource(R.drawable.ic_like_filled);
                            Toast.makeText(myLoveCard.this, "点赞成功", Toast.LENGTH_SHORT).show();
                        } else {
                            // 取消点赞
                            picture.status = 0;
                            // 切换为空心点赞图标
                            ((ImageButton)view).setImageResource(R.drawable.ic_like_outline);
                            Toast.makeText(myLoveCard.this, "已取消点赞", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // 评论按钮点击事件
                viewHolder.setOnClickListener(R.id.btn_comment, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 显示文本评论对话框
                        showTextCommentDialog(picture);
                    }
                });

                // 拍摄按钮点击事件
                viewHolder.setOnClickListener(R.id.btn_camera, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 启动相机录制视频
                        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                        } else {
                            Toast.makeText(myLoveCard.this, "没有可用的相机应用", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // 上传按钮点击事件
                viewHolder.setOnClickListener(R.id.btn_upload, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 显示视频评论列表对话框
                        showCommentLists(picture);
                    }
                });

                // 添加查看评论的功能
                viewHolder.setOnClickListener(R.id.text_input_hint, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 显示该卡片的所有评论
                        showAllComments(picture);
                    }
                });
            }
        };
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        // 修改为1列布局，使单个卡片占据整个宽度
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        initData();


    }



    // 已移除changeStar方法，改为在adapter中直接处理点赞状态
    private void initData() {
        // 使用本地测试数据，不依赖后端
        // 只添加一个测试卡片
        int[] testImages = {
            R.drawable.happy_1  // 只保留一个图片
        };

        // 只添加一个卡片
        ImageCard imageCard = new ImageCard();
        // 使用资源ID作为URL（特殊处理）
        imageCard.imageUrl = "resource://" + testImages[0];
        imageCard.status = 0; // 初始状态：未点赞
        imageCard.setId(0);
        dataSource.add(imageCard);

        // 通知适配器数据已更新
        adapter.notifyDataSetChanged();
    }

    private void showCommentDialog(ImageCard picture) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_comment, null);
        bottomSheetDialog.setContentView(dialogView);

        // 设置圆角背景
        View parentView = (View) dialogView.getParent();
        parentView.setBackgroundResource(R.drawable.bottom_sheet_background);

        // 初始化视图
        View previewContainer = dialogView.findViewById(R.id.preview_container);
        VideoView videoPreview = dialogView.findViewById(R.id.video_preview);
        TextView audioStatus = dialogView.findViewById(R.id.text_audio_status);

        // 关闭按钮
        dialogView.findViewById(R.id.btn_close).setOnClickListener(v -> {
            stopMediaRecording();
            bottomSheetDialog.dismiss();
        });

        // 录音评论
        dialogView.findViewById(R.id.card_record_audio).setOnClickListener(v -> {
            previewContainer.setVisibility(View.VISIBLE);
            videoPreview.setVisibility(View.GONE);
            audioStatus.setVisibility(View.VISIBLE);
            startAudioRecording();
        });

        // 录制视频
        dialogView.findViewById(R.id.card_record_video).setOnClickListener(v -> {
            startVideoRecording();
        });

        // 选择视频
        dialogView.findViewById(R.id.card_select_video).setOnClickListener(v -> {
            selectVideo();
        });

        // 取消按钮
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            stopMediaRecording();
            bottomSheetDialog.dismiss();
        });

        // 发布按钮
        dialogView.findViewById(R.id.btn_submit).setOnClickListener(v -> {
            if (currentAudioPath != null || currentVideoPath != null) {
                uploadComment(picture.getId(), currentAudioPath != null ? Comment.TYPE_AUDIO : Comment.TYPE_VIDEO);
            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void startAudioRecording() {
        try {
            File audioFile = new File(getExternalCacheDir(), "audio_comment_" + System.currentTimeMillis() + ".mp3");
            currentAudioPath = audioFile.getAbsolutePath();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(currentAudioPath);
            mediaRecorder.prepare();
            mediaRecorder.start();

            Toast.makeText(this, "开始录音...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "录音失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void startVideoRecording() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void selectVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "选择视频"), REQUEST_VIDEO_PICK);
    }

    private void stopMediaRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaRecorder = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_CAPTURE || requestCode == REQUEST_VIDEO_PICK) {
                Uri videoUri = data.getData();
                currentVideoPath = videoUri.toString();

                // Show video preview
                if (videoPreview != null) {
                    videoPreview.setVisibility(View.VISIBLE);
                    videoPreview.setVideoURI(videoUri);
                    videoPreview.start();
                }
            }
        }
    }

    private void uploadComment(int imageId, int commentType) {
        // 本地模拟评论上传成功
        Toast.makeText(this, "评论上传成功", Toast.LENGTH_SHORT).show();

        // 清除临时文件路径
        currentAudioPath = null;
        currentVideoPath = null;
    }

    /**
     * 显示文本评论对话框
     */
    private void showTextCommentDialog(ImageCard picture) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_text_comment, null);
        bottomSheetDialog.setContentView(dialogView);

        // 获取评论输入框
        EditText editComment = dialogView.findViewById(R.id.edit_comment);

        // 取消按钮
        dialogView.findViewById(R.id.btn_cancel_comment).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        // 提交按钮
        dialogView.findViewById(R.id.btn_submit_comment).setOnClickListener(v -> {
            String commentText = editComment.getText().toString().trim();
            if (!commentText.isEmpty()) {
                // 保存评论
                saveComment(picture.getId(), commentText);
                Toast.makeText(this, "评论已提交", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }

    /**
     * 保存评论到本地
     */
    private void saveComment(int imageId, String comment) {
        // 获取该图片的评论列表，如果不存在则创建
        List<String> comments = commentsMap.get(imageId);
        if (comments == null) {
            comments = new ArrayList<>();
            commentsMap.put(imageId, comments);
        }

        // 添加新评论
        comments.add(comment);
    }

    /**
     * 显示所有评论
     */
    private void showAllComments(ImageCard picture) {
        List<String> comments = commentsMap.get(picture.getId());

        if (comments == null || comments.isEmpty()) {
            Toast.makeText(this, "暂无评论", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建评论列表对话框
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_comments_list);
        dialog.setTitle("所有评论");

        // 获取评论列表视图
        RecyclerView recyclerView = dialog.findViewById(R.id.rv_comments_list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        // 创建评论适配器
        CommonAdapter<String> commentsAdapter = new CommonAdapter<String>(this, R.layout.item_comment, comments) {
            @Override
            protected void convert(ViewHolder viewHolder, String comment, int position) {
                viewHolder.setText(R.id.text_comment, comment);
                viewHolder.setText(R.id.text_comment_time, "家长点评 " + (position + 1));
            }
        };

        recyclerView.setAdapter(commentsAdapter);

        // 关闭按钮
        dialog.findViewById(R.id.btn_close_comments).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * 显示视频评论对话框
     */
    private void showVideoComments(ImageCard picture) {
        // 创建评论列表对话框
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_video_comments);
        dialog.setTitle("视频评论");

        // 获取视频播放器
        VideoView videoView = dialog.findViewById(R.id.video_view);

        // 设置视频源 - 使用raw目录下的video1.mp4
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video1;
        Uri videoUri = Uri.parse(videoPath);
        videoView.setVideoURI(videoUri);

        // 设置视频准备完成后自动播放
        videoView.setOnPreparedListener(mp -> {
            // 设置循环播放
            mp.setLooping(true);
        });

        // 设置双击播放/暂停
        videoView.setOnClickListener(new View.OnClickListener() {
            private long lastClickTime = 0;
            @Override
            public void onClick(View v) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < 300) { // 双击判断 (300ms内)
                    if (videoView.isPlaying()) {
                        videoView.pause();
                    } else {
                        videoView.start();
                    }
                }
                lastClickTime = clickTime;
            }
        });

        // 获取评论列表视图
        RecyclerView recyclerView = dialog.findViewById(R.id.rv_video_comments_list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        // 创建一些示例评论
        List<String> videoComments = new ArrayList<>();
        videoComments.add("这个视频很有趣！");
        videoComments.add("孩子很喜欢这个内容");
        videoComments.add("希望能有更多类似的视频");

        // 创建评论适配器
        CommonAdapter<String> commentsAdapter = new CommonAdapter<String>(this, R.layout.item_comment, videoComments) {
            @Override
            protected void convert(ViewHolder viewHolder, String comment, int position) {
                viewHolder.setText(R.id.text_comment, comment);
                viewHolder.setText(R.id.text_comment_time, "视频评论 " + (position + 1));
            }
        };

        recyclerView.setAdapter(commentsAdapter);

        // 关闭按钮
        dialog.findViewById(R.id.btn_close_video_comments).setOnClickListener(v -> {
            videoView.stopPlayback();
            dialog.dismiss();
        });

        // 对话框关闭时停止视频播放
        dialog.setOnDismissListener(dialogInterface -> {
            videoView.stopPlayback();
        });

        dialog.show();
    }

    /**
     * 显示评论列表对话框
     */
    private void showCommentLists(ImageCard picture) {
        // 创建评论列表对话框
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_video_comments);
        dialog.setTitle("视频评论");

        // 获取视频播放器
        VideoView videoView = dialog.findViewById(R.id.video_view);

        // 设置视频源 - 使用raw目录下的video1.mp4
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video1;
        Uri videoUri = Uri.parse(videoPath);
        videoView.setVideoURI(videoUri);

        // 设置视频准备完成后自动播放
        videoView.setOnPreparedListener(mp -> {
            // 设置循环播放
            mp.setLooping(true);
        });

        // 设置双击播放/暂停
        videoView.setOnClickListener(new View.OnClickListener() {
            private long lastClickTime = 0;
            @Override
            public void onClick(View v) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < 300) { // 双击判断 (300ms内)
                    if (videoView.isPlaying()) {
                        videoView.pause();
                    } else {
                        videoView.start();
                    }
                }
                lastClickTime = clickTime;
            }
        });

        // 获取评论列表视图
        RecyclerView recyclerView = dialog.findViewById(R.id.rv_video_comments_list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        // 创建一些示例评论
        List<String> videoComments = new ArrayList<>();
        videoComments.add("这个视频很有趣！");
        videoComments.add("孩子很喜欢这个内容");
        videoComments.add("希望能有更多类似的视频");

        // 创建评论适配器
        CommonAdapter<String> commentsAdapter = new CommonAdapter<String>(this, R.layout.item_comment, videoComments) {
            @Override
            protected void convert(ViewHolder viewHolder, String comment, int position) {
                viewHolder.setText(R.id.text_comment, comment);
                viewHolder.setText(R.id.text_comment_time, "视频评论 " + (position + 1));
            }
        };

        recyclerView.setAdapter(commentsAdapter);

        // 关闭按钮
        dialog.findViewById(R.id.btn_close_video_comments).setOnClickListener(v -> {
            videoView.stopPlayback();
            dialog.dismiss();
        });

        // 对话框关闭时停止视频播放
        dialog.setOnDismissListener(dialogInterface -> {
            videoView.stopPlayback();
        });

        dialog.show();
    }

    private void uploadEmotionData(List<String> imagePaths, List<String> audioPaths) {
        // 创建MultipartBody
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        // 添加图片文件
        for (int i = 0; i < imagePaths.size(); i++) {
            File imageFile = new File(imagePaths.get(i));
            builder.addFormDataPart("images",
                imageFile.getName(),
                RequestBody.create(MediaType.parse("image/*"), imageFile));
        }

        // 添加音频文件
        for (int i = 0; i < audioPaths.size(); i++) {
            File audioFile = new File(audioPaths.get(i));
            builder.addFormDataPart("audios",
                audioFile.getName(),
                RequestBody.create(MediaType.parse("audio/*"), audioFile));
        }

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url("http://your-local-server:port/upload")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(myLoveCard.this,
                    "上传失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(myLoveCard.this,
                        "上传成功", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
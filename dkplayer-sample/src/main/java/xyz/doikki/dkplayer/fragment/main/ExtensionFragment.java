package xyz.doikki.dkplayer.fragment.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.activity.LoginActivity;
import xyz.doikki.dkplayer.activity.MainActivity_;
import xyz.doikki.dkplayer.bean.User;
import xyz.doikki.dkplayer.dataSource.DBOpenHelper;
import xyz.doikki.dkplayer.dataSource.DbContect;
import xyz.doikki.dkplayer.dataSource.DbcUtils;
import xyz.doikki.dkplayer.fragment.BaseFragment;
import xyz.doikki.dkplayer.util.ToastUtil;

public class ExtensionFragment extends BaseFragment implements View.OnClickListener {

    DbContect helper;
    String oldPass;
    String user_name;
    private static final int REQUEST_PICK_VIDEO = 1;
    private Uri selectedVideoUri;  // 成员变量用于保存选定的视频文件的URI
    private VideoView videoView;
    // 在类的成员变量中添加 ImageView 变量
    private ImageView pauseIcon;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoView = view.findViewById(R.id.videoView); // 替换为你实际的VideoView的ID

    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_extension;
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.update).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
        findViewById(R.id.aboutUs).setOnClickListener(this);
        findViewById(R.id.upload).setOnClickListener(this);
        TextView username = findViewById(R.id.yonghuxingming);



        Bundle arguments = getArguments();
        if(arguments != null) {
            ToastUtil.ShowMsg(getContext(),"获取到用户信息");
            user_name = (String) arguments.get("username");
            username.setText((CharSequence) arguments.get("username"));
            oldPass = (String) arguments.get("pass");
            Log.d("oldPassword",oldPass);
        }
        else ToastUtil.ShowMsg(getContext(),"获取用户信息失败");
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()){
            case R.id.aboutUs:
                showAboutUsDialog();
                break;
            case R.id.update:
                Toast.makeText(getContext(),"密码更新", Toast.LENGTH_SHORT).show();
                showUpdatePasswordDialog();
                break;
            case R.id.logout:
                showLogoutConfirmationDialog();
                break;
            case R.id.upload:
                openFilePicker();
                break;
        }

    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_PICK_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_VIDEO && resultCode == Activity.RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            uploadAndDisplayVideo();
        }
    }

    /**
     * 文件上传本地
     */
    private void uploadAndDisplayVideo() {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(selectedVideoUri);

            File outputVideoDirectory = new File(requireContext().getFilesDir(), "Movies");
            if (!outputVideoDirectory.exists()) {
                // 如果目录不存在，创建目录
                outputVideoDirectory.mkdirs();
            }

            // 修改为将文件保存在目录下，而不是目录本身
            File outputVideoFile = new File(outputVideoDirectory, "uploaded_video.mp4");
            OutputStream outputStream = new FileOutputStream(outputVideoFile, true);

            byte[] buffer = new byte[4 * 1024]; // 4 KB buffer
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            // 显示本地视频文件
            displayLocalVideo(outputVideoFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常
            Toast.makeText(getContext(), "上传和显示视频时出错", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayLocalVideo(String videoPath) {
        // 使用VideoView播放本地视频
        videoView.setVideoPath(videoPath);
        videoView.setAlpha(1);
        videoView.start();
        // 添加 ImageView 用于显示暂停图标
        pauseIcon = new ImageView(getContext());
        pauseIcon.setImageResource(R.drawable.selector_mute_icon); // 替换为你的暂停图标资源
        pauseIcon.setScaleType(ImageView.ScaleType.CENTER);
        pauseIcon.setVisibility(View.GONE); // 初始时隐藏

        // 将暂停图标添加到布局中
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        ViewGroup parentView = (ViewGroup) videoView.getParent();

        if (parentView instanceof FrameLayout) {
            ((FrameLayout) parentView).addView(pauseIcon, params);
        } else {
            // 创建一个新的 FrameLayout 包裹 videoView 和 pauseIcon
            FrameLayout frameLayout = new FrameLayout(getContext());
            frameLayout.setLayoutParams(videoView.getLayoutParams());

            // 移除原先的 videoView，将其添加到新的 FrameLayout 中
            parentView.removeView(videoView);
            frameLayout.addView(videoView);

            // 将 pauseIcon 添加到新的 FrameLayout 中
            frameLayout.addView(pauseIcon, params);

            // 将新的 FrameLayout 添加回原先的父布局中
            parentView.addView(frameLayout);
        }


        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    // 如果视频正在播放，暂停视频并显示暂停图标
                    videoView.pause();
                    pauseIcon.setVisibility(View.VISIBLE);
                } else {
                    // 如果视频没有播放，开始播放视频并隐藏暂停图标
                    videoView.start();
                    pauseIcon.setVisibility(View.GONE);
                }
            }
        });

        // 添加监听视频播放完成的监听器，用于重新显示暂停图标
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                pauseIcon.setVisibility(View.VISIBLE);
            }
        });
    }

    // 显示密码更新的信息框
    private void showUpdatePasswordDialog() {
        // 创建一个布局，包含原密码、新密码和确认新密码的输入框
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_password, null);

        // 构建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("密码更新");  // 设置对话框标题
        builder.setView(view); // 将自定义的视图设置给对话框

        // 设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 在这里处理确定按钮的点击事件
                // 可以获取输入框中的内容并进行密码更新的相关逻辑
                EditText oldPasswordEditText = view.findViewById(R.id.editTextOldPassword);
                EditText newPasswordEditText = view.findViewById(R.id.editTextNewPassword);
                EditText confirmNewPasswordEditText = view.findViewById(R.id.editTextConfirmNewPassword);

                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmNewPassword = confirmNewPasswordEditText.getText().toString();

                // 在这里处理密码更新的逻辑
                if(!oldPass.equals(oldPassword)){
                    ToastUtil.ShowMsg(getContext(),"原密码错误，无法修改");
                }else if(!newPassword.equals(confirmNewPassword)){
                    ToastUtil.ShowMsg(getContext(),"两次新密码不一致，无法修改");
                }else{
                    // 进行数据库更新操作
                    helper = new DbContect(getContext());
                    DbcUtils.updatePassword(helper,user_name,newPassword);
                }

                dialog.dismiss(); // 关闭对话框
            }
        });

        // 设置取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 在这里处理取消按钮的点击事件
                dialog.dismiss(); // 关闭对话框
            }
        });

        // 创建并显示对话框
        builder.create().show();
    }
    private void showLogoutConfirmationDialog() {
        // 创建一个确认登出的对话框
        AlertDialog.Builder logoutDialogBuilder = new AlertDialog.Builder(getContext());
        logoutDialogBuilder.setTitle("确认登出");  // 设置对话框标题
        logoutDialogBuilder.setMessage("确定要登出吗？");  // 设置对话框消息

        // 设置确定按钮
        logoutDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 处理确定按钮点击事件
                performLogout(); // 执行登出操作
                dialog.dismiss(); // 关闭对话框
            }
        });

        // 设置取消按钮
        logoutDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 处理取消按钮点击事件
                dialog.dismiss(); // 关闭对话框
            }
        });

        // 显示确认登出对话框
        logoutDialogBuilder.show();
    }

    private void performLogout() {
        // 处理登出操作
        Intent intent = new Intent();
        intent.setClass(getContext(), LoginActivity.class);
        Toast.makeText(getContext(), "用户登出", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
    private void showAboutUsDialog() {
        // 创建一个关于信息的对话框
        AlertDialog.Builder aboutUsDialogBuilder = new AlertDialog.Builder(getContext());
        aboutUsDialogBuilder.setTitle("关于我们");  // 设置对话框标题
        aboutUsDialogBuilder.setMessage("猪猪视频创作者：朱正阳 杨大宇");  // 设置对话框消息

        // 设置确定按钮
        aboutUsDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 处理确定按钮点击事件
                dialog.dismiss(); // 关闭对话框
            }
        });

        // 显示关于信息对话框
        aboutUsDialogBuilder.show();
    }


}

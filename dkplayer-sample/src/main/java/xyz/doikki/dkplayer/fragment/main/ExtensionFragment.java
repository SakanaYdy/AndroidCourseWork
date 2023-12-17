package xyz.doikki.dkplayer.fragment.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


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
        }

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

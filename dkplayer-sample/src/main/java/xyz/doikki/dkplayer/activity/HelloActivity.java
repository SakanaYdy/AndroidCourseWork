package xyz.doikki.dkplayer.activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.bean.User;
import xyz.doikki.dkplayer.bean.Video;
import xyz.doikki.dkplayer.dataSource.DbContect;
import xyz.doikki.dkplayer.dataSource.DbcUtils;
import xyz.doikki.dkplayer.util.ToastUtil;

public class HelloActivity extends AppCompatActivity {

    DbContect helper;

    String img_1 = "https://ydy-sky.oss-cn-beijing.aliyuncs.com/64493bd2-63de-4e0f-8646-8fcefd6c0003.jpg?Expires=1702383691&OSSAccessKeyId=TMP.3KkU6RL6rKGcPY5PZCz3PCkQuCoDqgGo4ytbo23nkULtJkDfsggLm4uqfGST3kzctLcuiJvH2wYYKupEuUKLCC1qzTS5Dq&Signature=DBzFD5UJPqd%2Bf9bey8aSO9qPLbg%3D";
    String img_2 = "https://ydy-sky.oss-cn-beijing.aliyuncs.com/ecad7c6d-758f-4c00-9496-6be46e7c7938.jpg?Expires=1702384051&OSSAccessKeyId=TMP.3KkU6RL6rKGcPY5PZCz3PCkQuCoDqgGo4ytbo23nkULtJkDfsggLm4uqfGST3kzctLcuiJvH2wYYKupEuUKLCC1qzTS5Dq&Signature=9nrnMWKg1je8SCdoul6k2RWNfUM%3D";
    String video_url = "https://ydy-sky.oss-cn-beijing.aliyuncs.com/1.mp4?Expires=1702384068&OSSAccessKeyId=TMP.3KkU6RL6rKGcPY5PZCz3PCkQuCoDqgGo4ytbo23nkULtJkDfsggLm4uqfGST3kzctLcuiJvH2wYYKupEuUKLCC1qzTS5Dq&Signature=li%2B3X0huPVfm1lQWegdqAhq2Ltg%3D";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "MyApp Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        helper=new DbContect(  HelloActivity.this);

        Button create = findViewById(R.id.creates);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
//                SQLiteDatabase db=helper.getWritableDatabase();
//                helper.onCreate(db);
//                ToastUtil.ShowMsg(getBaseContext(),"建立表成功");
//                User user = new User(1,"ydy","1234");
//                DbcUtils.insert(helper,user);
//                ToastUtil.ShowMsg(getBaseContext(),"插入数据成功");
//                Video video = new Video(1,1,img_1,video_url);
//                DbcUtils.insertVideo(helper,video);
//                ToastUtil.ShowMsg(getBaseContext(),"插入数据成功");
//                DbcUtils.queryVideo(helper,1);
//                ToastUtil.ShowMsg(getBaseContext(),"查询数据成功");
            }
        });

        Button login = findViewById(R.id.login);

        /**
         * 登录跳转
         */
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(HelloActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(HelloActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}

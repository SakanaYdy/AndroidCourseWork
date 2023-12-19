package xyz.doikki.dkplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;

import xyz.doikki.dkplayer.R;

public class YoutubeService extends Service
{
    private String _title;
    private String _description;
    private MediaPlayer mediaPlayer;
    private boolean isRemove = false;
    private ProgressBar progressBar;
    private static final int NOTIFICATION_ID = 1;

    // Handler用于更新进度条
    private final Handler handler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(@NonNull Message msg)
        {
            if (mediaPlayer != null)
            {
                int progress = (int) (
                        ((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration())
                                * 100);
                updateNotification(progress);
                handler.sendEmptyMessageDelayed(0, 1000); // 每隔1秒更新一次
            }
            return false;
        }
    });

    public YoutubeService()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate()
    {
        mediaPlayer = MediaPlayer.create(this, R.raw.ayasa);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        handler.sendEmptyMessage(0); // 开始更新进度条
        Toast.makeText(this, "Youtube Service 创建", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        _title = intent.getExtras().getString("title");
        _description = intent.getExtras().getString("description");

        int cmd = intent.getExtras().getInt("cmd");
        if (cmd == 0)
        {
            if (!isRemove)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    createNotification();
                    mediaPlayer.start();
                }
            }
            isRemove = true;
        }
        else
        {
            if (isRemove)
            {
                stopForeground(true);
                mediaPlayer.pause();
            }
            isRemove = false;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        isRemove = false;
        handler.removeCallbacksAndMessages(null); // 移除Handler的所有消息和回调
        mediaPlayer.release();
        super.onDestroy();
    }

    private void createNotification()
    {
        String channelId = "your_channel_id";
        String channelName = "Your Channel Name";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            channel = new NotificationChannel(channelId, channelName, importance);
        }

        NotificationManager notificationManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            notificationManager = getSystemService(NotificationManager.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notificationManager.createNotificationChannel(channel);
        }

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        remoteViews.setTextViewText(R.id.notificationTitle, _title);
        remoteViews.setTextViewText(R.id.description, _description);


        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            notification = new Notification.Builder(this, "your_channel_id").setContentTitle("Your Notification Title")
                    .setContentText(_description).setContentTitle("正在播放：" + _title)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_hello))
                    .setSmallIcon(R.drawable.ic_hello).setCustomContentView(remoteViews)
                    .setCustomBigContentView(remoteViews).build();
        }

        startForeground(NOTIFICATION_ID, notification);
        Toast.makeText(this, "Youtube Service 在后台运行", Toast.LENGTH_SHORT).show();
    }

    private void updateNotification(int progress)
    {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        remoteViews.setProgressBar(R.id.progressBar, 100, progress, false);
        remoteViews.setTextViewText(R.id.notificationTitle, _title);
        remoteViews.setTextViewText(R.id.description, _description);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            notification = new Notification.Builder(this, "your_channel_id").setContentTitle("Your Notification Title")
                    .setContentText(_description).setContentTitle("正在播放：" + _title)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_hello))
                    .setSmallIcon(R.drawable.ic_hello).setCustomContentView(remoteViews)
                    .setCustomBigContentView(remoteViews).build();
        }

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            notificationManager = getSystemService(NotificationManager.class);
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}

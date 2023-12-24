package xyz.doikki.dkplayer.Service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

//public class AudioPlayService extends Service {
//
//    private MediaPlayer mediaPlayer;
////    private String videoUrl;
////
////    public AudioPlayService(String videoUrl) {
////        this.videoUrl = videoUrl;
////    }
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mediaPlayer = new MediaPlayer();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .setUsage(AudioAttributes.USAGE_MEDIA)
//                    .build());
//        }
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null && intent.getAction() != null) {
//            if (intent.getAction().equals("ACTION_PLAY")) {
//                // 播放音频
//                playAudio();
//            } else if (intent.getAction().equals("ACTION_STOP")) {
//                // 停止音频播放
//                stopAudio();
//            }
//        }
//
//        return START_STICKY;
//    }
//
//    private void playAudio() {
//        try {
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource("/data/user/0/xyz.doikki.dkplayer/files/Movies/uploaded_video.mp4");  // 替换为你的音频文件的 URL 或本地路径
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void stopAudio() {
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.stop();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//        }
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//}
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import xyz.doikki.dkplayer.R;

public class AudioPlayService extends Service {

    private static final String CHANNEL_ID = "channel_id";
    private static final int NOTIFICATION_ID = 1;

    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        AudioPlayService getService() {
            // Return this instance of LocalService so clients can call public methods
            return AudioPlayService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case "ACTION_PLAY":
                    startForegroundService();
                    // 执行播放操作
                    break;
                case "ACTION_STOP":
                    stopForegroundService();
                    // 执行停止操作
                    break;
                // 其他操作...
            }
        }
        return START_NOT_STICKY;
    }

    private void startForegroundService() {
        // 创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MyApp Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.shape_ad_bg)
                .setContentTitle("My App")
                .setContentText("正在播放音频")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // 启动 Foreground Service
        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void stopForegroundService() {
        // 停止 Foreground Service
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        // 在服务销毁时停止播放音频等清理操作
        super.onDestroy();
    }

    // 提供其他公共方法供外部调用

}

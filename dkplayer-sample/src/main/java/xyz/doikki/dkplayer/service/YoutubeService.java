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
    private boolean isPlaying = false;
    private RemoteViews _remoteViews;
    private Handler _progressHandler;

    public YoutubeService()
    {
    }

    /**
     * 启动服务onBind方法是省略的
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * onCreate方法在首次启动服务时调用一次
     */
    @Override
    public void onCreate()
    {
        mediaPlayer = MediaPlayer.create(this, R.raw.ayasa);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);

        /*
         在 Service 中使用 Handler 通常用于在服务的后台线程中执行异步任务、定时任务或处理其他一些与服务相关的逻辑。

        Service的生命周期独立于创建它的Activity。Handler的生命周期与所在Service关联。
        而Service在主线程中执行（默认情况下，除非需要在Service中执行后台耗时操作），

        Handler 被创建时没有显式传递 Looper，因此它默认与当前线程的 Looper 相关联，Handler的Looper即主线程的Looper。

        Looper：Looper 是一个消息循环器，它不断地从消息队列中取出消息并传递给相应的 Handler 处理。
        每个线程只能有一个 Looper。

        MessageQueue：MessageQueue 是消息队列，存储着待处理的消息。Looper 从队列中取出消息，
        并将其传递给关联的 Handler 处理。

         Handler 本身并不是多线程的，但它能够在多线程环境中实现线程间通信。
         通过在不同线程上创建 Handler，可以在一个线程中投递消息或任务，然后在关联的线程上处理它们。
         这种机制有助于避免直接在不同线程之间进行共享数据，从而减少了多线程编程中的竞态条件和死锁等问题。


    * */
        // Handler用于更新进度条
        // 创建Handler对象，并传入一个 Handler.Callback 的接口的实现（匿名接口）作为构造函数参数。
        // 重写handleMessage方法， 该方法定义了在接收到消息时的处理逻辑。
        // 消息的获取由主线程的Looper从主线程的消息队列中完成
        _progressHandler = new Handler(new Handler.Callback()
        {
            @Override
            public boolean handleMessage(@NonNull Message msg)
            {
                if (mediaPlayer != null)
                {
                    int progress = (int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100);
                    updateNotification(progress);

                    // 这行代码会向与 Handler 相关联的消息队列（主线程中的消息队列）发送一条空消息，
                    // 并且要求在指定的延迟时间后执行。
                    // 具体来说，这里是在 1000 毫秒（1秒）后执行。
                    // 发送的消息会进入消息队列，消息不会立即被处理。只有当循环到达消息的执行时间时，才会调用 handleMessage 方法。
                    // 相当于，每次接收消息对会发送一条在1秒后执行的，what为0的消息，实现后台任务的一直执行
                    _progressHandler.sendEmptyMessageDelayed(0, 1000); // 每隔1秒更新一次
                }
                return false;
            }
        });

        Toast.makeText(this, "Youtube Service 创建", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

    /**
     * 当反复启动服务时调用，接收当前需要开启/关闭后台播放服务的命令，以及显示的标题和描述
     *
     * @param intent  本次启动服务接受的意图，包含了标题，描述和命令信息
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        int cmd = intent.getExtras().getInt("cmd");

        // cmd 为0， 表示应用进入后台，开启后台服务
        if (cmd == 0)
        {
            if (!isRemove)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    _title = intent.getExtras().getString("title");
                    _description = intent.getExtras().getString("description");
                    start();
                }
            }
            isRemove = true;
        }
        else    // cmd为1，表示应用进入前台，关闭后台服务
        {
            if (isRemove)
            {
                stop();
            }
            isRemove = false;
        }
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 应用进入后台，启动后台服务时调用
     * 创建通知，更新remoteViews界面，并且播放（或者继续播放）媒体音乐
     * 然后立即向消息队列发送一条没有延迟的空消息，从而开始更新进度条
     */
    private void start()
    {
        isPlaying = true;
        createNotification();
        mediaPlayer.start();
        _progressHandler.sendEmptyMessage(0); // 立即发送第一条消息

    }

    /**
     * 应用进入前台，关闭后台服务
     * 暂停媒体音乐播放，调用Service类方法stopForeground，关闭通知
     * 并通过handler.removeMessages方法，将消息队列中所有what字段为0的消息都移除
     * 从而实现停止handle处理消息，停止进度条的更新的效果
     */
    private void stop()
    {
        isPlaying = false;
        stopForeground(true);
        mediaPlayer.pause();

        // handler.removeMessages(0) 则用于停止这个定时任务，即移除所有待处理的空消息。
        // 通过移除所有what字段为0的消息，可以实现停止定时任务的继续产生。
        _progressHandler.removeMessages(0); // 停止Handler的消息发送
    }


    /**
     * 在stopService或者stopSelf调用
     */
    @Override
    public void onDestroy()
    {
        isRemove = false;
        _progressHandler.removeCallbacksAndMessages(null); // 移除Handler的所有消息和回调
        mediaPlayer.release();
        super.onDestroy();
    }


    /**
     * 创建任务栏通知
     */
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

        _remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        _remoteViews.setTextViewText(R.id.notificationTitle, _title);
        _remoteViews.setTextViewText(R.id.description, _description);


        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            notification = new Notification.Builder(this, "your_channel_id").setContentTitle("Your Notification Title")
                    .setContentText(_description).setContentTitle("正在播放：" + _title)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_hello))
                    .setSmallIcon(R.drawable.ic_hello).setCustomContentView(_remoteViews)
                    .setCustomBigContentView(_remoteViews).build();
        }

        startForeground(NOTIFICATION_ID, notification);
        Toast.makeText(this, "Youtube Service 在后台运行", Toast.LENGTH_SHORT).show();
    }

    /**
     * 更新任务栏通知，更新进度条进度
     * @param progress 进度
     */
    private void updateNotification(int progress)
    {
        _remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        _remoteViews.setProgressBar(R.id.progressBar, 100, progress, false);
        _remoteViews.setTextViewText(R.id.notificationTitle, _title);
        _remoteViews.setTextViewText(R.id.description, _description);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            notification = new Notification.Builder(this, "your_channel_id").setContentTitle("Your Notification Title")
                    .setContentText(_description).setContentTitle("正在播放：" + _title)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_hello))
                    .setSmallIcon(R.drawable.ic_hello).setCustomContentView(_remoteViews)
                    .setCustomBigContentView(_remoteViews).build();
        }

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            notificationManager = getSystemService(NotificationManager.class);
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}

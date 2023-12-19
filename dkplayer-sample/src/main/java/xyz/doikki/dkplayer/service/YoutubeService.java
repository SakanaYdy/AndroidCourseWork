package xyz.doikki.dkplayer.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import xyz.doikki.dkplayer.R;

public class YoutubeService extends Service
{
    /**
     * id不可设置为0,否则不能设置为前台service
     */
    private static final int NOTIFICATION_DOWNLOAD_PROGRESS_ID = 1;
    private boolean isRemove = false;//是否需要移除

    public YoutubeService()
    {
    }

    /**
     * 当另一个组件想通过调用 bindService() 与服务绑定系统将调用此方法。
     * 在此方法的实现中，必须返回 一个IBinder 接口的实现类，供客户端用来与服务进行通信。
     * 无论是启动状态还是绑定状态，此方法必须重写，但在启动状态的情况下直接返回 null。
     *
     * @param intent 通过bindService()传递过来的Intent，携带数据
     * @return
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand()或onBind() 之前）
     * 如果服务已在运行，则不会调用此方法，该方法只调用一次
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    /**
     * 当另一个组件（如 Activity）通过调用 startService() 请求启动服务时，
     * 系统将调用此方法。一旦执行此方法，服务即会启动并可在后台无限期运行。
     * 如果自己实现此方法，则需要在服务工作完成后，通过调用 stopSelf()
     * 或 stopService() 来停止服务。
     * 在绑定状态下，无需实现此方法。
     *
     * @param intent 启动组件传递过来的Intent，如Activity可利用Intent封装所需要的参数并传递给Service
     * @param flags  表示启动请求时是否有额外数据，可选值有 0，START_FLAG_REDELIVERY，START_FLAG_RETRY，
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        int i = intent.getExtras().getInt("cmd");
        if (i == 0)
        {
            if (!isRemove)
            {
                // 只有在服务启动时才创建通知
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    createNotification();
                }
            }
            isRemove = true;
        }
        else
        {
            //移除前台服务
            if (isRemove)
            {
                // 关闭通知
                NotificationManager notificationManager = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
                {
                    notificationManager = getSystemService(NotificationManager.class);
                } notificationManager.cancel(/*notificationId*/ 1);

            }
            isRemove = false;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 当服务不再使用且将被销毁时，系统将调用此方法。
     * 服务应该实现此方法来清理所有资源，如线程、注册的侦听器、接收器等，这是服务接收的最后一个调用。
     */
    @Override
    public void onDestroy()
    {
        //移除前台服务
        isRemove = false;
        super.onDestroy();
    }

    /**
     * Notification
     */
    public void createNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "your_channel_id"; // 替换为你想要的通知渠道ID
            String channelName = "Your Channel Name"; // 替换为你想要的通知渠道名称
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            // 设置通知渠道的其他属性，如描述，LED 灯等

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            // 创建通知实例
            Notification notification = new Notification.Builder(this, channelId)
                    .setContentTitle("Your Notification Title")
                    .setContentText("Your Notification Content")
                    .setSmallIcon(R.drawable.ic_hello) // 替换为你的通知图标
                    .build();

            // 显示通知
            notificationManager.notify(/*notificationId*/ 1, notification);

            Toast.makeText(this, "Youtube Service 在后台运行", Toast.LENGTH_SHORT).show();
        }
    }
}
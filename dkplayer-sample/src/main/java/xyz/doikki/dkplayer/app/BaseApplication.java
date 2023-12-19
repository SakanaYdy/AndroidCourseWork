package xyz.doikki.dkplayer.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BaseApplication extends Application
{
    private boolean isForeground = false;//是否在前台 默认false
    public boolean IS_FOREGROUND()
    {
        return isForeground;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
    }

    /**
     * Activity生命周期回调，用于判断应用是否在前台
     */
    private class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks
    {

        /**
         * Activity创建时调用
         *
         * @param activity
         * @param bundle
         */
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle)
        {

        }

        /**
         * Activity 启动时调用
         *
         * @param activity
         */
        @Override
        public void onActivityStarted(@NonNull Activity activity)
        {
            if (!isForeground)
            {
                // 进入前台
                isForeground = true;
                // TODO: 进入前台后的操作
                Toast.makeText(activity, "进入前台", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Activity 变为前台时调用
         *
         * @param activity
         */
        @Override
        public void onActivityResumed(@NonNull Activity activity)
        {

        }

        /**
         * Activity 变为后台时调用
         *
         * @param activity
         */
        @Override
        public void onActivityPaused(@NonNull Activity activity)
        {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity)
        {
            if (activity.isChangingConfigurations())
            {
                // 配置变化，如屏幕旋转等不算进入后台
                return;
            }

            if (!activity.isFinishing())
            {
                // 从前台进入后台
                isForeground = false;
                Toast.makeText(activity, "进入后台", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle)
        {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity)
        {

        }
    }

}

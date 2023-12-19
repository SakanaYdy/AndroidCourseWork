package xyz.doikki.dkplayer.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BaseActivity extends Application
{
    private boolean isForeground = false;//是否在前台 默认false

    @Override
    public void onCreate()
    {
        super.onCreate();
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
    }

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
         * @param activity
         */
        @Override
        public void onActivityStarted(@NonNull Activity activity)
        {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity)
        {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity)
        {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity)
        {

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

    // 用于监听应用的生命周期变化。
}

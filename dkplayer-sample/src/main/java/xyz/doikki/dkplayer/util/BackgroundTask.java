package xyz.doikki.dkplayer.util;

import android.app.Activity;


/**
 * BackgroundTask 是一个抽象类，旨在替代 AsyncTask 用于处理 Android 应用中的后台任务。
 * 它提供了在后台执行任务并在完成后在主线程上更新 UI 的简单机制。
 * <p>
 * 与 AsyncTask 相比的优势：
 * - 允许更细粒度的控制线程和执行。
 * - 避免了 AsyncTask 及其对封闭 Activity 的隐式引用可能导致的内存泄漏问题。
 * <p>
 * 注意：此类中的所有方法应该从主线程中调用。
 */
public abstract class BackgroundTask
{

    /**
     * 与此后台任务关联的 Activity。
     */
    private Activity _activity;

    /**
     * 使用指定的 Activity 构造一个新的 BackgroundTask 实例。
     *
     * @param activity 与此后台任务关联的 Activity 实例。
     */
    public BackgroundTask(Activity activity)
    {
        this._activity = activity;
    }

    /**
     * 在后台线程中调用 doInBackground 方法，然后在主线程中调用 onPostExecute。
     */
    private void startBackground()
    {
        // 实现一个匿名内部类，传递给接口参数，“函数式编程“
        new Thread(new Runnable()
        {
            public void run()
            {
                // 首先在子线程启动后台任务
                doInBackground();

                // 然后在主线程中调用 onPostExecute 方法
                _activity.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        onPostExecute();
                    }
                });
            }
        }).start();
    }

    /**
     * 执行后台任务的入口方法，由子类实现。
     * 该方法将在后台子线程中执行，用于处理耗时任务。
     */
    public abstract void doInBackground();

    /**
     * 后台任务执行完成后在主线程中调用的方法，由子类实现。
     * 用于更新 UI 或处理执行结果。
     */
    public abstract void onPostExecute();

    /**
     * 执行后台任务入口，启动后台线程执行 doInBackground 方法。
     */
    public void execute()
    {
        startBackground();
    }
}

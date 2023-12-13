package xyz.doikki.dkplayer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.fragment.main.ExtensionFragment;
import xyz.doikki.dkplayer.fragment.main.ListFragment;
import xyz.doikki.dkplayer.fragment.main.PipFragment;
import xyz.doikki.dkplayer.util.Tag;

import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.player.VideoViewManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity_ extends BaseActivity<VideoView> implements NavigationBarView.OnItemSelectedListener {

    private final List<Fragment> mFragments = new ArrayList<>();
    public static int mCurrentIndex = 0;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean enableBack() {
        return false;
    }

    @Override
    public void initView() {
        super.initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10000);
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnItemSelectedListener(this);
        mFragments.add(new ListFragment());
        mFragments.add(new ExtensionFragment());
        mFragments.add(new PipFragment());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_content, mFragments.get(0))
                .commitAllowingStateLoss();
        mCurrentIndex = 0;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int index;
        int itemId = menuItem.getItemId();
        if (itemId == R.id.tab_list) {
            index = 0;
        } else if (itemId == R.id.tab_extension) {
            index = 1;
        } else if (itemId == R.id.tab_pip) {
            index = 2;
        } else {
            index = 0;
        }
        if (mCurrentIndex != index) {
            if (mCurrentIndex == 1) {
                VideoViewManager.instance().releaseByTag(Tag.LIST);
                VideoViewManager.instance().releaseByTag(Tag.SEAMLESS, false);
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment fragment = mFragments.get(index);
            Fragment curFragment = mFragments.get(mCurrentIndex);
            if (fragment.isAdded()) {
                transaction.hide(curFragment).show(fragment);
            } else {
                transaction.add(R.id.layout_content, fragment).hide(curFragment);
            }
            transaction.commitAllowingStateLoss();
            mCurrentIndex = index;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (VideoViewManager.instance().onBackPress(Tag.LIST)) return;
        if (VideoViewManager.instance().onBackPress(Tag.SEAMLESS)) return;
        super.onBackPressed();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // 保存活动状态
    }

    public static int getCurrentIndex() {
        return mCurrentIndex;
    }

    public static void setCurrentIndex(int currentIndex) {
        mCurrentIndex = currentIndex;
    }
}

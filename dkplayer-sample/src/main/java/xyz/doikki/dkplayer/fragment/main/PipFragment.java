package xyz.doikki.dkplayer.fragment.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.adapter.ListPagerAdapter;
import xyz.doikki.dkplayer.bean.SharedViewModel;
import xyz.doikki.dkplayer.bean.User;
import xyz.doikki.dkplayer.dataSource.DbContect;
import xyz.doikki.dkplayer.dataSource.DbcUtils;
import xyz.doikki.dkplayer.fragment.BaseFragment;
import xyz.doikki.dkplayer.util.Tag;

public class PipFragment extends BaseFragment implements View.OnClickListener,ListPagerAdapter.OnMessageReceivedListener {

    private ListPagerAdapter pagerAdapter;
    DbContect helper;
    private SharedViewModel sharedViewModel;
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list;
    }


    @Override
    protected void initView() {
        super.initView();

        Bundle arguments = getArguments();
        String username = (String) arguments.get("username");
        helper = new DbContect(getContext());
        User query = DbcUtils.query(helper, username);
        int id = query.getId();

        ViewPager viewPager = findViewById(R.id.view_pager);

        List<String> titles = new ArrayList<>();
        titles.add("个人上传视频" + id);
        Log.d("个人id",id+"");
        pagerAdapter = new ListPagerAdapter(getChildFragmentManager(),titles,this,-1);
        pagerAdapter.setListFragmentMessage(username);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private final BroadcastReceiver uploadCompletedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String uploadedVideoPath = intent.getStringExtra("videoPath");
            assert uploadedVideoPath != null;
            Log.d("接收到广播消息",uploadedVideoPath);
            // 在这里执行刷新数据的逻辑
            pagerAdapter.refreshData(uploadedVideoPath);
        }
    };

    @Override
    public void onResume() {
        Log.d("广播注册","success");
        super.onResume();
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(uploadCompletedReceiver, new IntentFilter("UPLOAD_VIDEO_COMPLETED"));
    }

//    @Override
//    public void onPause() {
//        Log.d("广播销毁","success");
//        super.onPause();
//        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(uploadCompletedReceiver);
//    }


    @Override
    public void onDetach() {
        super.onDetach();
        getVideoViewManager().releaseByTag(Tag.LIST);
        getVideoViewManager().releaseByTag(Tag.SEAMLESS);
    }
    @Override
    public void onClick(View v) {
    }

    @Override
    public void onMessageReceived(String message) {

    }
}

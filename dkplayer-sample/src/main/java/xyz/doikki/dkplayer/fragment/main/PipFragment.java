package xyz.doikki.dkplayer.fragment.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.adapter.ListPagerAdapter;
import xyz.doikki.dkplayer.bean.User;
import xyz.doikki.dkplayer.dataSource.DbContect;
import xyz.doikki.dkplayer.dataSource.DbcUtils;
import xyz.doikki.dkplayer.fragment.BaseFragment;
import xyz.doikki.dkplayer.util.Tag;

public class PipFragment extends BaseFragment implements View.OnClickListener,ListPagerAdapter.OnMessageReceivedListener {

    private ListPagerAdapter pagerAdapter;
    DbContect helper;
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

        pagerAdapter = new ListPagerAdapter(getChildFragmentManager(),titles,this);
        pagerAdapter.setListFragmentMessage(username);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getVideoViewManager().releaseByTag(Tag.LIST);
        getVideoViewManager().releaseByTag(Tag.SEAMLESS);
    }
    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.test1:
//                Toast.makeText(getContext(),"test1", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.test2:
//                Toast.makeText(getContext(),"test2", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.test3:
//                Toast.makeText(getContext(),"test3", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.test4:
//                Toast.makeText(getContext(),"test4", Toast.LENGTH_SHORT).show();
//                break;
//        }
    }

    @Override
    public void onMessageReceived(String message) {

    }
}

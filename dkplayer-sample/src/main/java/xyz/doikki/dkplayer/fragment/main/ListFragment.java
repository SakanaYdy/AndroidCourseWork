package xyz.doikki.dkplayer.fragment.main;

import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.adapter.ListPagerAdapter;
import xyz.doikki.dkplayer.bean.User;
import xyz.doikki.dkplayer.dataSource.DbContect;
import xyz.doikki.dkplayer.dataSource.DbcUtils;
import xyz.doikki.dkplayer.fragment.BaseFragment;
import xyz.doikki.dkplayer.util.Tag;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 注意：RecyclerView demo 我采用继承的方式实现，
 * 实际开发中可以不需要这样。
 * 我这样做仅仅只为代码复用，方便维护
 * @noinspection ALL
 */
public class ListFragment extends BaseFragment implements ListPagerAdapter.OnMessageReceivedListener{

    private ListPagerAdapter pagerAdapter;
    DbContect helper;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list;
    }

    @Override
    protected void initView() {
        super.initView();

        ViewPager viewPager = findViewById(R.id.view_pager);

        Bundle arguments = getArguments();
        // 从 Bundle 中获取用户名信息
        String username = "";
        if (arguments != null) {
            username = arguments.getString("username", "");
        }
        helper = new DbContect(getContext());
        User query = DbcUtils.query(helper, username);
        int id = query.getId();


        List<String> titles = new ArrayList<>();

        titles.add(getString(R.string.str_list_view));
        titles.add(getString(R.string.str_recycler_view));
        titles.add("抖音");

        pagerAdapter = new ListPagerAdapter(getChildFragmentManager(),titles,this,0);
        pagerAdapter.setListFragmentMessage(username);
        // 将当前 ListFragment 注册为消息接收监听器
        // viewPager.setAdapter(new ListPagerAdapter(getChildFragmentManager(), titles,this));
        viewPager.setAdapter(pagerAdapter);

        // viewPager.setAdapter(new ListPagerAdapter(getChildFragmentManager(), titles));

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
    public void onMessageReceived(String message) {

    }
}

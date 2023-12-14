package xyz.doikki.dkplayer.adapter;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import xyz.doikki.dkplayer.fragment.ChannelFragment;
import xyz.doikki.dkplayer.fragment.list.ListViewFragment;
import xyz.doikki.dkplayer.fragment.list.RecyclerViewFragment;
import xyz.doikki.dkplayer.fragment.list.TikTokListFragment;

import java.util.List;

/**
 * List主页适配器
 * Created by Doikki on 2018/1/3.
 */
public class ListPagerAdapter extends FragmentStatePagerAdapter {

    public interface OnMessageReceivedListener {
        void onMessageReceived(String message);
    }

    private List<String> mTitles;
    private SparseArrayCompat<Fragment> mFragments = new SparseArrayCompat<>();
    private OnMessageReceivedListener messageListener;

    public ListPagerAdapter(FragmentManager fm, List<String> titles) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mTitles = titles;
    }

    public ListPagerAdapter(FragmentManager fm, List<String> titles, OnMessageReceivedListener listener) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mTitles = titles;
        this.messageListener = listener;
    }

    /**
     * 信息接收
     */
    private String listFragmentMessage;
    // 用于接收ListFragment的消息
    public void setListFragmentMessage(String message) {
        listFragmentMessage = message;
    }

    @Override
    @NonNull
    public Fragment getItem(int position) {
        Fragment fragment = mFragments.get(position);
        if (fragment == null) {
            switch (position) {
                default:
                case 0:
                    fragment = new ListViewFragment();
                    // 将信息传递给ListViewFragment
                    Bundle args = new Bundle();
                    args.putString("username",listFragmentMessage);
                    Log.d("get_username",listFragmentMessage);
                    fragment.setArguments(args);
                    break;
                case 1:
                    fragment = new ChannelFragment();
                    break;
                case 2:
                    fragment = new TikTokListFragment();
            }
            mFragments.put(position, fragment);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}

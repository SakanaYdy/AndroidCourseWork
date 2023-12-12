package xyz.doikki.dkplayer.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import xyz.doikki.dkplayer.fragment.ChannelFragment;
import xyz.doikki.dkplayer.fragment.LiveFragment;
import xyz.doikki.dkplayer.fragment.PlayListFragment;


// 用于多个Fragment切换的管理
public class PagerAdapter extends FragmentStatePagerAdapter
{
    int _mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int numOfTabs)
    {
        super(fm);
        this._mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                ChannelFragment tab1 = new ChannelFragment();
                return tab1;
            case 1:
                PlayListFragment tab2 = new PlayListFragment();
                return tab2;
            case 2:
                LiveFragment tab3 = new LiveFragment();
                return tab3;
            default:
                return null;
        }
    }


    @Override
    public int getCount()
    {
        return _mNumOfTabs;
    }
}

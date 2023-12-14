package xyz.doikki.dkplayer.fragment.list;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.adapter.ListPagerAdapter;
import xyz.doikki.dkplayer.adapter.VideoListViewAdapter;
import xyz.doikki.dkplayer.adapter.listener.OnItemChildClickListener;
import xyz.doikki.dkplayer.bean.User;
import xyz.doikki.dkplayer.bean.VideoBean;
import xyz.doikki.dkplayer.dataSource.DbContect;
import xyz.doikki.dkplayer.dataSource.DbcUtils;
import xyz.doikki.dkplayer.fragment.BaseFragment;
import xyz.doikki.dkplayer.util.DataUtil;
import xyz.doikki.dkplayer.util.Tag;
import xyz.doikki.dkplayer.util.Utils;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videocontroller.component.CompleteView;
import xyz.doikki.videocontroller.component.ErrorView;
import xyz.doikki.videocontroller.component.GestureView;
import xyz.doikki.videocontroller.component.TitleView;
import xyz.doikki.videocontroller.component.VodControlView;
import xyz.doikki.videoplayer.player.VideoView;

/**
 * ListView demo，不推荐，建议使用{@link RecyclerViewFragment}
 */
public class ListViewFragment extends BaseFragment implements OnItemChildClickListener, ListPagerAdapter.OnMessageReceivedListener {

    private List<VideoBean> mVideos = new ArrayList<>();
    private VideoListViewAdapter mAdapter;

    private VideoView mVideoView;
    private StandardVideoController mController;
    private int mCurPosition = -1;
    private TitleView mTitleView;

    private DbContect helper = new DbContect(getContext());

    private int receivedId;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list_view;
    }

    /**
     * 接收用户信息
     */
    private String listViewFragmentMessage;

    // 用于接收来自ListPagerAdapter的消息
    public void setListViewFragmentMessage(String message) {
        listViewFragmentMessage = message;
        // 在这里处理接收到的消息
        Log.d("recerver_username",message);
        User query = DbcUtils.query(helper, message);
        assert query != null;
        receivedId = query.getId();
    }

    @Override
    protected void initView() {
        super.initView();

        Bundle arguments = getArguments();
        String username = (String) arguments.get("username");
        String id = (String) arguments.get("userId");
        Log.d("receivedId",id);
        receivedId = Integer.parseInt(id);
        Log.d("receivedId",receivedId+"");
        // int id = (int) arguments.get("id");
        // Log.d("recerver_username",username + "  " + id);
        // User query = DbcUtils.query(helper, username);
        // Log.d("query_id",query.getId() + "");

        mVideoView = new VideoView(getActivity());
        mVideoView.setOnStateChangeListener(new VideoView.SimpleOnStateChangeListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == VideoView.STATE_IDLE) {
                    Utils.removeViewFormParent(mVideoView);
                    mCurPosition = -1;
                }
            }
        });
        mController = new StandardVideoController(getActivity());
        mController.addControlComponent(new ErrorView(getActivity()));
        mController.addControlComponent(new CompleteView(getActivity()));
        mController.addControlComponent(new GestureView(getActivity()));
        mTitleView = new TitleView(getActivity());
        mController.addControlComponent(mTitleView);
        mController.addControlComponent(new VodControlView(getActivity()));
        mController.setEnableOrientation(true);
        mVideoView.setVideoController(mController);

        ListView listView = findViewById(R.id.lv);
        mAdapter = new VideoListViewAdapter(mVideos);
        mAdapter.setOnItemChildClickListener(this);
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private View firstView; //记录当前屏幕中第一个可见的item对象
            private View lastView; //记录当前屏幕中最后个可见的item对象
            private int lastFirstVisibleItem; //记录当前屏幕中第一个可见的item的position
            private int lastVisibleItem; // 记录屏幕中最后一个可见的item的position
            private boolean scrollFlag;// 记录滑动状态

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        scrollFlag = false;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        scrollFlag = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        scrollFlag = true;
                        break;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollFlag) { // 避免不必要的执行
                    //如果记录的 屏幕中第一个可见的item的position 已经小于当前屏幕中第一个可见item的position，表示item已经完全滑出屏幕了
                    //这种情况一般出现在ListView上滑的时候，故此时我们可以把firstView上的播放器停止
                    if (lastFirstVisibleItem < firstVisibleItem) {
                        gcView(firstView);
                        //通过firstVisibleItem + visibleItemCount - 1我们可以得到当前屏幕上最后一个item的position
                        //如果屏幕中最后一个可见的item的position已经大于当前屏幕上最后一个item的position，表示item已经完全滑出屏幕了
                        //这种情况一般出现在ListView下滑的时候，故此时我们可以把lastView上的播放器停止
                    } else if (lastVisibleItem > firstVisibleItem + visibleItemCount - 1) {
                        gcView(lastView);
                    }
                    lastFirstVisibleItem = firstVisibleItem;
                    lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
                    firstView = view.getChildAt(0);
                    lastView = view.getChildAt(visibleItemCount - 1);
                }
            }

            private void gcView(View gcView) {
                if (gcView != null) {
                    FrameLayout playerContainer = gcView.findViewById(R.id.player_container);
                    View view = playerContainer.getChildAt(0);
                    if (view != null && view == mVideoView && !mVideoView.isFullScreen()) {
                        releaseVideoView();
                    }
                }
            }
        });
    }
    private void initData(int id) {
        // 使用接收到的 id 进行初始化
        List<VideoBean> videoList = DataUtil.getVideoList(new DbContect(getContext()), id);
        mVideos.addAll(videoList);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    protected void initData() {
        super.initData();
//        List<VideoBean> videoList = DataUtil.getVideoList(new DbContect(getContext()),1);
//        mVideos.addAll(videoList);
//        mAdapter.notifyDataSetChanged();
        initData(receivedId);
        Log.d("initData_id" ,"" + receivedId);
    }

    @Override
    protected boolean isLazyLoad() {
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseVideoView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mVideoView.resume();
    }

    @Override
    public void onItemChildClick(int position) {
        if (mCurPosition == position) return;
        if (mCurPosition != -1) {
            releaseVideoView();
        }
        VideoBean videoBean = mVideos.get(position);
        mVideoView.setUrl(videoBean.getUrl());
        mTitleView.setTitle(videoBean.getTitle());
        View itemView = mAdapter.getItemView(position);
        VideoListViewAdapter.ViewHolder viewHolder = (VideoListViewAdapter.ViewHolder) itemView.getTag();
        //请点进去看isDissociate的解释
        mController.addControlComponent(viewHolder.mPrepareView, true);
        Utils.removeViewFormParent(mVideoView);
        viewHolder.mPlayerContainer.addView(mVideoView, 0);
        //播放之前将VideoView添加到VideoViewManager以便在别的页面也能操作它
        getVideoViewManager().add(mVideoView, Tag.LIST);
        mVideoView.start();
        mCurPosition = position;
    }

    private void releaseVideoView() {
        mVideoView.release();
        if (mVideoView.isFullScreen()) {
            mVideoView.stopFullScreen();
        }
        if(getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mCurPosition = -1;
    }

    @Override
    public void onMessageReceived(String message) {
        // 在这里处理接收到的消息
        Log.d("ListViewFragment_info", "Received message: " + message);
    }
}

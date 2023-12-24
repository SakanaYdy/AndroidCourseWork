package xyz.doikki.dkplayer.fragment.list;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
import xyz.doikki.dkplayer.util.ToastUtil;
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

    String url = "https://ydy-sky.oss-cn-beijing.aliyuncs.com/0.jpg";

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


//    private void initData(int id) {
//
//        List<VideoBean> videoList = new ArrayList<>();
//        if(id == -1){
//            // 从虚拟机文件存储位置获取到视频信息
//            // 获取内部存储目录
//            File filesDir = getContext().getFilesDir();
//            Log.d("fileDir", String.valueOf(filesDir));
//            // 构建视频文件夹的绝对路径
//            String videoFolderPath = filesDir.getAbsolutePath() + File.separator + "Movies";
//
//            // 构建视频文件夹的 File 对象
//            File videoFolder = new File(videoFolderPath);
//
//            // 检查视频文件夹是否存在
//            if (videoFolder.exists() && videoFolder.isDirectory()) {
//                // 获取视频文件夹下所有文件
//                File[] videoFiles = videoFolder.listFiles();
//                int cnt = 0;
//                // 遍历文件列表，处理视频文件
//                for (File videoFile : videoFiles) {
//                    // videoFile 是视频文件，可以处理它的路径或其他操作
//                    String videoFilePath = videoFile.getAbsolutePath();
//                    // 处理视频文件路径，例如添加到列表中或进行其他操作
//                    VideoBean videoBean = new VideoBean("文件视频"+cnt,"https://ydy-sky.oss-cn-beijing.aliyuncs.com/0.jpg",videoFilePath);
//                    videoList.add(videoBean);
//                }
//            } else {
//                // 视频文件夹不存在或不是一个目录
//                // 处理相应的逻辑，例如创建文件夹等
//                ToastUtil.ShowMsg(getContext(),"文件错误");
//            }
//
//        }else{
//            // 使用接收到的 id 进行初始化
//            videoList = DataUtil.getVideoList(new DbContect(getContext()), id);
//        }
//        mVideos.addAll(videoList);
//        mAdapter.notifyDataSetChanged();
//
//    }
private void initData(int id) {
    List<VideoBean> videoList = new ArrayList<>();

    if (id == -1) {
        // 从虚拟机文件存储位置获取到视频信息
        // 获取内部存储目录
        File filesDir = getContext().getFilesDir();
        Log.d("fileDir", String.valueOf(filesDir));

        // 构建视频文件夹的绝对路径
        String videoFolderPath = filesDir.getAbsolutePath() + File.separator + "Movies";

        // 构建视频文件夹的 File 对象
        File videoFolder = new File(videoFolderPath);

        // 检查视频文件夹是否存在
        if (videoFolder.exists() && videoFolder.isDirectory()) {
            // 获取视频文件夹下所有文件
            File[] videoFiles = videoFolder.listFiles();
            int cnt = 0;

            // 遍历文件列表，处理视频文件
            for (File videoFile : videoFiles) {
                // videoFile 是视频文件，可以处理它的路径或其他操作
                String videoFilePath = videoFile.getAbsolutePath();
                Log.d("视频文件路径,",videoFilePath);
                // 获取视频首帧路径
                String videoCoverPath = getVideoCoverPath(videoFilePath);
                // Log.d("封面图片路径",videoCoverPath);
                // 处理视频文件路径，例如添加到列表中或进行其他操作
                VideoBean videoBean = new VideoBean("文件视频" + cnt, videoCoverPath, videoFilePath);
                videoList.add(videoBean);

                cnt++;
            }
        } else {
            // 视频文件夹不存在或不是一个目录
            // 处理相应的逻辑，例如创建文件夹等
            ToastUtil.ShowMsg(getContext(), "文件错误");
        }

    } else {
        // 使用接收到的 id 进行初始化
        videoList = DataUtil.getVideoList(new DbContect(getContext()), id);
    }

    mVideos.addAll(videoList);
    mAdapter.notifyDataSetChanged();
}

     // 获取视频首帧路径
    private String getVideoCoverPath(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            // 设置数据源
            retriever.setDataSource(videoPath, new HashMap<>());
            // 获取首帧
            Bitmap videoCoverBitmap = retriever.getFrameAtTime();
            // 将首帧图片保存到 "imgs" 文件夹下
            File imgsDirectory = new File(getContext().getCacheDir(), "imgs");
            if (!imgsDirectory.exists()) {
                imgsDirectory.mkdirs();
            }
            // 构建文件名，使用视频文件名作为图片文件名
            String videoFileName = new File(videoPath).getName();
            String imageFileName = videoFileName.substring(0, videoFileName.lastIndexOf('.')) + ".jpg";

            // 构建图片文件路径
            File coverFile = new File(imgsDirectory, imageFileName);
            // 将图片保存到文件
            saveBitmapToFile(videoCoverBitmap, coverFile);
            return coverFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            // Log.e("VideoCoverPathError", "Error getting video cover path: " + e.getMessage());
        } finally {
            // 释放资源
            retriever.release();
        }

        return null;
    }
//    private String getVideoCoverPath(String videoPath) {
//        try {
//            // 使用 Glide 加载视频封面
//            Bitmap bitmap = Glide.with(this)
//                    .asBitmap()
//                    .load(videoPath)
//                    .submit()
//                    .get();
//
//            // 将首帧图片保存到 "imgs" 文件夹下
//            File imgsDirectory = new File(getContext().getCacheDir(), "imgs");
//            if (!imgsDirectory.exists()) {
//                imgsDirectory.mkdirs();
//            }
//
//            // 构建文件名，使用视频文件名作为图片文件名
//            String videoFileName = new File(videoPath).getName();
//            String imageFileName = videoFileName.substring(0, videoFileName.lastIndexOf('.')) + ".jpg";
//
//            // 构建图片文件路径
//            File coverFile = new File(imgsDirectory, imageFileName);
//            // 将图片保存到文件
//            saveBitmapToFile(bitmap, coverFile);
//
//            return coverFile.getAbsolutePath();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("获取视频封面异常", "Error: " + e.getMessage());
//        }
//
//        return null;
//    }


    // 将 Bitmap 保存到文件
    private void saveBitmapToFile(Bitmap bitmap, File file) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void refreshData(String filePath) {
        Log.d("new_add_video",filePath);
        mVideos.add(new VideoBean("新增数据",url,filePath));
        mAdapter.notifyDataSetChanged();
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

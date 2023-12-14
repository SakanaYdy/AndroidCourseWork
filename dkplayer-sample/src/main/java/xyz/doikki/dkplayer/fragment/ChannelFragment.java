package xyz.doikki.dkplayer.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.adapter.VideoPostAdapter;
import xyz.doikki.dkplayer.dataModel.YoutubeDataModel;

public class ChannelFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    // Google
    //private static String GOOGLE_YOUTUBE_API_KEY = "AIzaSyBUGTiAIYAEN7tuwzAx0wyX6Bd1eLENR4E";
    private static String GOOGLE_YOUTUBE_API_KEY = "AIzaSyAV9KXI6EFqwiTars_sCuuJxvDGDmXtLtg";

    //private static String CHANNEL_ID = "UC3KFV0PRieM4GoH8P5v6r0w";
    private static String CHANNEL_ID = "UCb3TZ4SD_Ys3j4z0-8o6auA";
    //private static String CHANNEL_GET_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&channelId=" + CHANNEL_ID + "&maxResults=20&key=" + GOOGLE_YOUTUBE_API_KEY;
    private static String CHANNEL_GET_URL() {
        return  "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&channelId=" + CHANNEL_ID + "&maxResults=20&key=" + GOOGLE_YOUTUBE_API_KEY;
    }
    private RecyclerView _recyclerView= null;
    private VideoPostAdapter _adapter = null;
    private ArrayList<YoutubeDataModel> _data= new ArrayList<>();

    /** @noinspection deprecation*/
    // Fragment View 视图层创建时调用
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 通过xml布局文件，初始化view视图
        View view = inflater.inflate(R.layout.fragment_channel, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // 下拉刷新时的监听事件
        swipeRefreshLayout.setOnRefreshListener(this::refreshContent);

        _recyclerView = view.findViewById(R.id.listVideos);
        new RequestYoutubeAPI().execute();

        return view;
    }


    /**
     * 下拉刷新：随机获取ChannelId -> 清空数据 -> 网络请求
     */
    private void refreshContent() {

        updateChannelId();

        _data.clear();
        new RequestYoutubeAPI().execute();
    }


    /**
     * 从资源文件中获取随机chanel Id
     */
    private void updateChannelId() {

        String[] channelIds = getResources().getStringArray(R.array.channel_ids);
        String newChannelId;

        do {
            newChannelId = channelIds[new Random().nextInt(channelIds.length)];
            Log.d("updateChannelId", newChannelId);
        } while (newChannelId.equals(CHANNEL_ID));

        CHANNEL_ID = newChannelId;
    }


    /**
     * @param data Youtube视频数据
     */
    // 初始化 RecyclerView，VideoPostAdapter
    private void init(ArrayList<YoutubeDataModel> data) {
        _recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        _adapter = new VideoPostAdapter(data, getActivity());
        _recyclerView.setAdapter(_adapter);
    }

    private class RequestYoutubeAPI extends AsyncTask<Void, String, String> {

        // 最大网络请求次数3次
        private static final int MAX_RETRIES = 3;

        /** @noinspection deprecation*/
        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            // 使用okHTTP进行网络请求
            Request request = new Request.Builder().url(CHANNEL_GET_URL()).build();
            Log.d("URL2", CHANNEL_GET_URL());


            for (int retry = 0; retry < MAX_RETRIES; retry++) {
                try {
                    Response response = client.newCall(request).execute();
                    Log.d("URL2" , "请求结果" + response.toString());
                    if (response.isSuccessful()) {
                        return response.body().string();
                    }
                } catch (IOException e) {
                    Log.e("URL2", "IOException: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            return null;
        }

        /** @noinspection deprecation*/
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    _data= parseVideoListFromResponse(jsonObject);

                    for (YoutubeDataModel item : _data) {
                        Log.d("URL2", item.toString());
                    }
                    init(_data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // Stop the refresh animation
            swipeRefreshLayout.setRefreshing(false);
        }

        /**
         * @param jsonObject 序列化Json数据对象
         * @return YoutubeData列表
         */
        // Json 数据解析
        private ArrayList<YoutubeDataModel> parseVideoListFromResponse(JSONObject jsonObject) {
            ArrayList<YoutubeDataModel> list = new ArrayList<>();
            if (jsonObject.has("items")) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        if (json.has("id")) {
                            JSONObject jsonID = json.getJSONObject("id");
                            if (jsonID.has("kind")) {
                                if (jsonID.getString("kind").equals("youtube#video")) {
                                    JSONObject jsonSnippet = json.getJSONObject("snippet");
                                    String title = jsonSnippet.getString("title");
                                    String description = jsonSnippet.getString("description");
                                    String publishedAt = jsonSnippet.getString("publishedAt");
                                    String thumbNails = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");
                                    // 获得视频Id
                                    String videoId = jsonID.getString("videoId");

                                    YoutubeDataModel youtube = new YoutubeDataModel();
                                    youtube.setTitle(title);
                                    youtube.setDescription(description);
                                    youtube.setPublishedAt(publishedAt);
                                    youtube.setThumbNail(thumbNails);
                                    youtube.setVideoId(videoId);
                                    // 插入列表
                                    list.add(youtube);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            return list;
        }
    }
}

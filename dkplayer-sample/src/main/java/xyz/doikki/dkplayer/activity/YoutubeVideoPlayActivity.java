package xyz.doikki.dkplayer.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.adapter.VideoCommentAdapter;
import xyz.doikki.dkplayer.dataModel.CommentDataModel;

/** @noinspection ALL*/

public class YoutubeVideoPlayActivity extends AppCompatActivity
{
    private String _videoId = "";

    // Google
    //private static String GOOGLE_YOUTUBE_API_KEY = "AIzaSyBUGTiAIYAEN7tuwzAx0wyX6Bd1eLENR4E";
    private static String GOOGLE_YOUTUBE_API_KEY = "AIzaSyAV9KXI6EFqwiTars_sCuuJxvDGDmXtLtg";

    private static String VIDEO_COMMENTS_URL(String videoId) {
        return "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&videoId="
                + videoId + "&maxResults=20&key=" + GOOGLE_YOUTUBE_API_KEY;
    }

    private WebView _webView;
    private ActionBar actionBar;

    private RecyclerView _recyclerView = null;
    private VideoCommentAdapter _adapter = null;
    private ArrayList<CommentDataModel> _data = new ArrayList<>();

    private void init(ArrayList<CommentDataModel> data)
    {
        _recyclerView.setLayoutManager(new LinearLayoutManager(this));

        _adapter = new VideoCommentAdapter(data,this);
        _recyclerView.setAdapter(_adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_video_play);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        _recyclerView = findViewById(R.id.recyclerViewComments);

        _webView = findViewById(R.id.webView);
        ArrayList<CommentDataModel> datas = new ArrayList<>();


        //CommentDataModel data = new CommentDataModel("1", "1", "1");

        //datas.add(data);

        //init(datas);

        // 获取状态栏
        // getSupportActionBar 是 父类AppCompatActivity的函数
        actionBar = getSupportActionBar();

        // 获取传递过来的视频 ID
        // getIntent()是最顶层父类Activity中的函数
        _videoId = getIntent().getStringExtra("videoId");


        // 设置 WebView 播放视频
        _webView.getSettings().setJavaScriptEnabled(true);



        String data_portait = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "  <body>\n"
                + "    <div id=\"player\"></div>\n"
                + "\n"
                + "    <script>\n"
                + "      var tag = document.createElement('script');\n"
                + "      tag.src = \"https://www.youtube.com/iframe_api\";\n"
                + "      var firstScriptTag = document.getElementsByTagName('script')[0];\n"
                + "      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n"
                + "\n"
                + "      var player;\n"
                + "      function onYouTubeIframeAPIReady() {\n"
                + "        player = new YT.Player('player', {\n"
                + "          height: '200',\n"
                + "          width: '100%',\n"
                + "          videoId: '"
                + _videoId
                + "',\n"
                + "          events: {\n"
                + "            'onReady': onPlayerReady,\n"
                + "            'onStateChange': onPlayerStateChange\n"
                + "          }\n"
                + "        });\n"
                + "      }\n"
                + "\n"
                + "      function onPlayerReady(event) {\n"
                + "        event.target.playVideo();\n"
                + "      }\n"
                + "\n"
                + "      function onPlayerStateChange(event) {\n"
                + "        // Handle player state changes if needed\n"
                + "      }\n"
                + "    </script>\n"
                + "  </body>\n"
                + "</html>";

        String data_landscape = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "  <body>\n"
                + "    <div id=\"player\"></div>\n"
                + "\n"
                + "    <script>\n"
                + "      var tag = document.createElement('script');\n"
                + "      tag.src = \"https://www.youtube.com/iframe_api\";\n"
                + "      var firstScriptTag = document.getElementsByTagName('script')[0];\n"
                + "      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n"
                + "\n"
                + "      var player;\n"
                + "      function onYouTubeIframeAPIReady() {\n"
                + "        player = new YT.Player('player', {\n"
                + "          height: 380,\n"
                + "          width: '100%',\n"
                + "          videoId: '"
                + _videoId
                + "',\n"
                + "          events: {\n"
                + "            'onReady': onPlayerReady,\n"
                + "            'onStateChange': onPlayerStateChange\n"
                + "          }\n"
                + "        });\n"
                + "      }\n"
                + "\n"
                + "      function onPlayerReady(event) {\n"
                + "        event.target.playVideo();\n"
                + "      }\n"
                + "\n"
                + "      function onPlayerStateChange(event) {\n"
                + "        // Handle player state changes if needed\n"
                + "      }\n"
                + "    </script>\n"
                + "  </body>\n"
                + "</html>";
        _webView.loadDataWithBaseURL(null, data_portait, "text/html", "UTF-8", null);

        _webView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            // xxx
            RelativeLayout layout = findViewById(R.id.relativeLayout);
            RecyclerView recyclerView = findViewById(R.id.recyclerViewComments);
            RelativeLayout.LayoutParams recyclerViewParams = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();


            @Override
            public void onGlobalLayout() {
                // 横屏时，全屏
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (actionBar != null) {
                        actionBar.hide();   // 隐藏状态栏
                    }
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏标志

                    // 移除评论区
                    if (recyclerView.getParent() != null) {
                        ((ViewGroup) recyclerView.getParent()).removeView(recyclerView);
                    }

                    _webView.loadDataWithBaseURL(null, data_landscape, "text/html", "UTF-8", null);
                } else {
                    // 竖屏
                    if (actionBar != null) {
                        actionBar.show();   // 显示状态栏
                    }
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏全屏标志

                    if (recyclerView.getParent() != null) {
                        ((ViewGroup) recyclerView.getParent()).removeView(recyclerView);
                    }


                    layout.addView(recyclerView, recyclerViewParams);

                    _webView.setLayoutParams(new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        });


        // 获取评论数据
        new RequestComment().execute();


    }

    private class RequestComment extends AsyncTask<Void, String, String>
    {

        // 最大网络请求次数3次
        private static final int MAX_RETRIES = 3;

        /**
         * @noinspection deprecation
         * doInBackground(Params... params): 在后台线程中执行耗时任务的地方。
         * 在这个方法中执行网络请求、文件读写等耗时操作。
         */
        @Override
        protected String doInBackground(Void... params)
        {
            OkHttpClient client = new OkHttpClient();
            // 使用okHTTP进行网络请求
            Request request = new Request.Builder().url(VIDEO_COMMENTS_URL(_videoId)).build();

            Log.d("Comment", VIDEO_COMMENTS_URL(_videoId));
            for (int retry = 0; retry < MAX_RETRIES; retry++)
            {
                try
                {
                    Response response = client.newCall(request).execute();
                    Log.d("Comment", "请求结果" + response.toString());
                    if (response.isSuccessful())
                    {
                        return response.body().string();
                    }
                } catch (IOException e)
                {
                    Log.e("Comment", "IOException: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * @noinspection deprecation
         * onPostExecute(Result result): 在 doInBackground 方法执行完毕后调用，
         * 用于处理执行结果。在这个方法中更新 UI，处理执行结果。
         */
        @Override
        protected void onPostExecute(String response)
        {
            super.onPostExecute(response);
            if (response != null)
            {
                try
                {
                    // doInBackground 中的response返回的数据（String) 会传进onPostExecute(String response)
                    // response转换成JSONObject即为其中的Json序列化数据
                    JSONObject jsonObject = new JSONObject(response);

                    // Json序列化解析
                    _data = parseCommentListFromJson(jsonObject);

                    for (CommentDataModel item : _data)
                    {
                        Log.d("Comment", item.toString());
                    }
                    init(_data);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

//            // 停止播放刷新动画
//            swipeRefreshLayout.setRefreshing(false);
        }

        /**
         * @param jsonObject 序列化Json数据对象
         * @return YoutubeData列表
         */
        // Json 数据解析
        private ArrayList<CommentDataModel> parseCommentListFromJson(JSONObject jsonObject) {
            ArrayList<CommentDataModel> list = new ArrayList<>();
            if (jsonObject.has("items")) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonItem = jsonArray.getJSONObject(i);
                        if (jsonItem.has("kind") && jsonItem.getString("kind").equals("youtube#commentThread")) {
                            JSONObject jsonSnippet = jsonItem.getJSONObject("snippet");
                            if (jsonSnippet.has("topLevelComment")) {
                                JSONObject jsonTopComment = jsonSnippet.getJSONObject("topLevelComment");
                                JSONObject jsonSnippetComment = jsonTopComment.getJSONObject("snippet");

                                String textDisplay = jsonSnippetComment.getString("textDisplay");
                                String authorDisplayName = jsonSnippetComment.getString("authorDisplayName");
                                String publishedAt = jsonSnippetComment.getString("publishedAt");


                                CommentDataModel commentData = new CommentDataModel(authorDisplayName, textDisplay, publishedAt);
                                list.add(commentData);
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


package xyz.doikki.dkplayer.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.adapter.VideoCommentAdapter;
import xyz.doikki.dkplayer.adapter.VideoPostAdapter;
import xyz.doikki.dkplayer.dataModel.CommentDataModel;
import xyz.doikki.dkplayer.dataModel.YoutubeDataModel;

public class YoutubeVideoPlayActivity extends AppCompatActivity
{
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        _recyclerView = findViewById(R.id.recyclerViewComments);

        _webView = findViewById(R.id.webView);
        ArrayList<CommentDataModel> datas = new ArrayList<CommentDataModel>();
        CommentDataModel data = new CommentDataModel("1", "1", "1");
        datas.add(data);
        init(datas);

        // 获取状态栏
        // getSupportActionBar 是 父类AppCompatActivity的函数
        actionBar = getSupportActionBar();

        // 获取传递过来的视频 ID
        // getIntent()是最顶层父类Activity中的函数
        String videoId = getIntent().getStringExtra("videoId");

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
                + videoId
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
                + videoId
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

        // 监听屏幕方向变化
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

                    // Remove RecyclerView from its parent if it has one
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

                    // Remove RecyclerView from its parent if it has one
                    if (recyclerView.getParent() != null) {
                        ((ViewGroup) recyclerView.getParent()).removeView(recyclerView);
                    }

                            // Add RecyclerView back to the layout
                    layout.addView(recyclerView, recyclerViewParams);

                    _webView.setLayoutParams(new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        });



    }
}


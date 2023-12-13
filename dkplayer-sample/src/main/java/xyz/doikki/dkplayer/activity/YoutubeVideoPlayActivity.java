package xyz.doikki.dkplayer.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import xyz.doikki.dkplayer.R;

public class YoutubeVideoPlayActivity extends AppCompatActivity
{
    private WebView _webView;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_video_play);

        _webView = findViewById(R.id.webView);

        actionBar = getSupportActionBar();

        // 获取传递过来的视频 ID
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
                + "          height: '360',\n"
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
            @Override
            public void onGlobalLayout() {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (actionBar != null) {
                        actionBar.hide();
                    }
                    _webView.loadDataWithBaseURL(null, data_landscape, "text/html", "UTF-8", null);
                } else {
                    if (actionBar != null) {
                        actionBar.show();
                    }
                    _webView.setLayoutParams(new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        });
    }
}


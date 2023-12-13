package xyz.doikki.dkplayer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import xyz.doikki.dkplayer.R;

public class YoutubeVideoPlayActivity extends AppCompatActivity
{
    private WebView _webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_video_play);

        _webView = findViewById(R.id.webView);

        // 获取传递过来的视频 ID
        String videoId = getIntent().getStringExtra("videoId");

        // 设置 WebView 播放视频
        _webView.getSettings().setJavaScriptEnabled(true);
        String data = "<!DOCTYPE html>\n" + "<html>\n" + "  <body>\n" + "    <div id=\"player\"></div>\n" + "\n" + "    <script>\n" + "      var tag = document.createElement('script');\n" + "      tag.src = \"https://www.youtube.com/iframe_api\";\n" + "      var firstScriptTag = document.getElementsByTagName('script')[0];\n" + "      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n" + "\n" + "      var player;\n" + "      function onYouTubeIframeAPIReady() {\n" + "        player = new YT.Player('player', {\n" + "          height: '200',\n" + "          width: '400',\n" + "          videoId: '" + videoId + "',\n" + "          events: {\n" + "            'onReady': onPlayerReady,\n" + "            'onStateChange': onPlayerStateChange\n" + "          }\n" + "        });\n" + "      }\n" + "\n" + "      function onPlayerReady(event) {\n" + "        event.target.playVideo();\n" + "      }\n" + "\n" + "      function onPlayerStateChange(event) {\n" + "        // Handle player state changes if needed\n" + "      }\n" + "    </script>\n" + "  </body>\n" + "</html>";
        _webView.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
    }
}

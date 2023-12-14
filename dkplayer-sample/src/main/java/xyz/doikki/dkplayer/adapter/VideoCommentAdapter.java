package xyz.doikki.dkplayer.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.dataModel.CommentDataModel;

public class VideoCommentAdapter extends RecyclerView.Adapter<VideoCommentAdapter.CommentHolder>
{

    private ArrayList<CommentDataModel> _commentListDatas;  // 请替换为你的评论数据模型类

    private Context _context = null;

    // 设置评论数据
    public VideoCommentAdapter(ArrayList<CommentDataModel> comments, Context _context)
    {
        this._commentListDatas = comments;
        this._context = _context;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        // LayoutInflater 是 Android 中用于将 XML 布局文件转换为实际视图的类。
        // inflate 是将 XML 布局文件转换为实际的 View 对象的过程。


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_youtube_comment, parent, false);

        // 将view 传递给YoutubePostHolder的构造函数
        CommentHolder holderView = new CommentHolder((view));
        return holderView;
    }

    // 绑定评论数据到视图
    @Override
    public void onBindViewHolder(@NonNull VideoCommentAdapter.CommentHolder holder, int position)
    {


        TextView textContent = holder._textContent;
        TextView textAuthorDisplayName = holder._textAuthorDisplayName;
        TextView textPublishedAt = holder._textPublishedAt;


        CommentDataModel dataObject = _commentListDatas.get(position);


        textContent.setText(dataObject.getTextDisplay());
        textAuthorDisplayName.setText(dataObject.getAuthorDisplayName());
        textPublishedAt.setText(dataObject.getPublishedAt());


    }

    @Override
    public int getItemCount()
    {
        return _commentListDatas.size();
    }

    // 评论视图持有者类
    public static class CommentHolder extends RecyclerView.ViewHolder
    {

        private TextView _textAuthorDisplayName;
        private TextView _textContent;
        private TextView _textPublishedAt;

        public CommentHolder(@NonNull View itemView)
        {
            super(itemView);
            _textAuthorDisplayName = (TextView) itemView.findViewById(R.id.textAuthorDisplayName);
            _textContent = (TextView) itemView.findViewById(R.id.textDisplay);
            _textPublishedAt = (TextView) itemView.findViewById(R.id.textPublishedAt);
        }


    }
}

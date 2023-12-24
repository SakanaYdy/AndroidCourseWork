package xyz.doikki.dkplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.activity.YoutubeVideoPlayActivity;
import xyz.doikki.dkplayer.dataModel.YoutubeDataModel;


// 用于实现一个RecyclerView视图，里面的元素项是自定义的YoutubePostHolder
// 适配器中，泛型参数 <VideoPostAdapter.YoutubePostHolder> 指定了适配器使用的 ViewHolder 类型，
public class VideoPostAdapter extends RecyclerView.Adapter<VideoPostAdapter.YoutubePostHolder>
{
    // 存储了要在 RecyclerView 中显示的数据
    private ArrayList<YoutubeDataModel> _listVideoDatas;
    // 存储 Android 应用程序的上下文对象。在 Android 中，Context 对象提供了应用程序的全局信息，
    // 包括访问资源、启动活动等。在适配器中，通常使用上下文来获取系统服务、加载布局资源等。
    private Context _context = null;

    public VideoPostAdapter(ArrayList<YoutubeDataModel> listVideoDatas, Context _context)
    {
        this._listVideoDatas = listVideoDatas;
        this._context = _context;
    }


    // 当RecyclerView 需要展示一个新的项时，它会调用适配器的 onCreateViewHolder 方法来创建一个新的 ViewHolder 实例
    // 该实例用于表示列表中的一个项。
    //  ViewGroup parent：父视图，即 RecyclerView。
    // int viewType：项的类型。在一个 RecyclerView 中可能有多种类型的项，这个参数用于标识当前项的类型。
    @NonNull
    @Override
    public YoutubePostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // LayoutInflater 是 Android 中用于将 XML 布局文件转换为实际视图的类。
        // inflate 是将 XML 布局文件转换为实际的 View 对象的过程。
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_youtube_post, parent, false);

        // 将view 传递给YoutubePostHolder的构造函数
        YoutubePostHolder holderView = new YoutubePostHolder((view));
        return holderView;
    }

    @Override
    public int getItemCount()
    {
        return _listVideoDatas.size();
    }


    // 在onBindViewHolder里设置视频项的具体信息
    // position是当前项的索引
    @Override
    public void onBindViewHolder(@NonNull YoutubePostHolder holder, int position)
    {
        TextView textViewTitle = holder._textViewTitle;
        TextView textViewDes = holder._textViewDes;
        TextView textViewData = holder._textViewData;
        ImageView imageThumb = holder._imageThumb;

        YoutubeDataModel dataObject = _listVideoDatas.get(position);

        // 根据相应的数据设置视图的Text
        textViewTitle.setText(dataObject.getTitle());
        textViewDes.setText(dataObject.getDescription());
        textViewData.setText(dataObject.getPublishedAt());
        // TODO: 图片将会从url下载
        Picasso.get().load(dataObject.getThumbNail()).into(imageThumb);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Toast.makeText(_context,"textViewTitle" ,Toast.LENGTH_LONG).show();
                String videoId = dataObject.getVideoId();

                // 启动新的 Activity，将视频 ID 传递给该 Activity
                Intent intent = new Intent(_context, YoutubeVideoPlayActivity.class);
                intent.putExtra("videoId", videoId);
                intent.putExtra("title", dataObject.getTitle());
                intent.putExtra("description", dataObject.getDescription());
                intent.putExtra("thumbNail", dataObject.getThumbNail());


                _context.startActivity(intent);
            }
        });
    }


    // 一个Holder 代表RecyclerView中的一个项目，表示一个视频
    public static class YoutubePostHolder extends RecyclerView.ViewHolder
    {
        TextView _textViewTitle;
        TextView _textViewDes;
        TextView _textViewData;
        ImageView _imageThumb;

        public YoutubePostHolder(@NonNull View itemView)
        {
            super(itemView);
            this._textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            this._textViewDes = (TextView) itemView.findViewById(R.id.textViewDes);
            this._textViewData = (TextView) itemView.findViewById(R.id.textViewData);
            this._imageThumb = (ImageView) itemView.findViewById(R.id.imageThumb);
        }
    }

}

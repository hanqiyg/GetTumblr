package com.icesoft.gettumblr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.icesoft.gettumblr.Constant;
import com.icesoft.gettumblr.R;
import com.icesoft.gettumblr.Utils.TumblrUtils;
import com.icesoft.gettumblr.models.VideoDetail;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;

public class VideoSelectAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private OnClickListener mListener;
    private LayoutInflater mLayoutInflater;
    private VideoDetail[] details;
    public VideoSelectAdapter(Context context, OnClickListener listener)
    {
        this.mContext = context;
        this.mListener = listener;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }
    public void setDetails(VideoDetail[] details){
        this.details = details;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.video_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ViewHolder && details!= null && details.length > position)
        {
            final VideoDetail detail = details[position];
            ViewHolder viewHolder = (ViewHolder) holder;


            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(Constant.LOAD_WAIT_DRAWABLE);
            requestOptions.error(Constant.LOAD_ERROR_DRAWABLE);

            Glide.with(mContext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(detail.posterFilePath)
                    .into(viewHolder.ivPoster);
            RequestOptions requestOptionsSmall = new RequestOptions();
            requestOptionsSmall.placeholder(Constant.LOAD_WAIT_DRAWABLE);
            requestOptionsSmall.error(Constant.LOAD_ERROR_DRAWABLE);

            Glide.with(mContext)
                    .setDefaultRequestOptions(requestOptionsSmall)
                    .load(detail.blogAvatarFilePath)
                    .into(viewHolder.ivBlogAvatar);

             viewHolder.tvBlogName.setText(detail.blogName);

            viewHolder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDownloadClick(detail);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return details==null?0:details.length;
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivBlogAvatar;
        public TextView tvBlogName;
        public Button btnDownload;
        public ImageView ivPoster;

        public ViewHolder(View v) {
            super(v);
            ivBlogAvatar = v.findViewById(R.id.ivBlogAvatar);
            tvBlogName = v.findViewById(R.id.tvBlogName);
            btnDownload = v.findViewById(R.id.btnDownload);
            ivPoster = v.findViewById(R.id.ivPoster);
        }
    }
    public interface OnClickListener{
        void onDownloadClick(VideoDetail detail);
    }
}

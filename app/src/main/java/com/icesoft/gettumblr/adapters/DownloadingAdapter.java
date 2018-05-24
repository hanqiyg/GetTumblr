package com.icesoft.gettumblr.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.icesoft.gettumblr.App;
import com.icesoft.gettumblr.Constant;
import com.icesoft.gettumblr.R;
import com.icesoft.gettumblr.models.BaseDetail;
import com.icesoft.gettumblr.models.VideoDetail;

import org.apache.commons.lang3.StringUtils;

public class DownloadingAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private OnClickListener mListener;
    private LayoutInflater mLayoutInflater;
    public DownloadingAdapter(Context context, OnClickListener listener)
    {
        this.mContext = context;
        this.mListener = listener;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.downloading_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final BaseDetail detail = App.getInstance().getDownloadDetail(VideoDetail.TYPE,position);
        if(holder instanceof ViewHolder && detail instanceof VideoDetail) {
            final VideoDetail videoDetail = (VideoDetail) detail;
            final ViewHolder viewHolder = (ViewHolder) holder;
            DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            if (videoDetail != null && videoDetail.downloadId != -1) {
                DownloadManager.Query query = new DownloadManager.Query().setFilterById(videoDetail.downloadId);
                Cursor c = dm.query(query);
                if (c != null) {
                    if (c.moveToFirst()) {
                        final String fileUri = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIAPROVIDER_URI));
                        String url = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_URI));
                        final String mimeType = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE));
                        long downloadedBytes = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        long totalBytes = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));

                        //printUri(c);
                        RequestOptions request = new RequestOptions()
                                .placeholder(Constant.LOAD_WAIT_DRAWABLE)
                                .error(Constant.LOAD_ERROR_DRAWABLE)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate();
                        Glide.with(mContext)
                                .setDefaultRequestOptions(request)
                                .load(videoDetail.posterFilePath)
                                .into(viewHolder.ivPoster);

                        Glide.with(mContext)
                                .setDefaultRequestOptions(request)
                                .load(videoDetail.blogAvatarFilePath)
                                .into(viewHolder.ivBlogAvatar);
                        viewHolder.tvBlogName.setText(videoDetail.blogName);

                        if (videoDetail.postNoteCount != -1) {
                            viewHolder.tvNoteCount.setVisibility(View.VISIBLE);
                            String notecountTitle = mContext.getResources().getString(R.string.notecount_post);
                            viewHolder.tvNoteCount.setText(String.valueOf(videoDetail.postNoteCount) + notecountTitle);
                        }
                        if (videoDetail.postSlug != null) {
                            viewHolder.tvSlug.setVisibility(View.VISIBLE);
                            String slug = StringUtils.replace(videoDetail.postSlug, "-", "\n");
                            //System.out.println(detail.getSlug());
                            viewHolder.tvSlug.setText(slug);
                        }

                        if (videoDetail.postSource != null) {
                            viewHolder.tvSource.setVisibility(View.VISIBLE);
                            //System.out.println(detail.getSource());
                            String sourceTitle = mContext.getResources().getString(R.string.source_post);
                            viewHolder.tvSource.setText(sourceTitle + videoDetail.postSource);
                        }

                        viewHolder.ivPoster.setVisibility(View.VISIBLE);
                        viewHolder.tvStatus.setVisibility(View.VISIBLE);
                        viewHolder.ibPlay.setVisibility(View.GONE);

                        switch (status) {
                            case DownloadManager.STATUS_PENDING:
                                viewHolder.tvStatus.setText("PENDING");
                                viewHolder.tvStatus.setBackgroundColor(Color.YELLOW);
                                viewHolder.progress.setVisibility(View.VISIBLE);
                                viewHolder.progress.setProgress((int) (downloadedBytes * 100 / totalBytes));
                                viewHolder.tvProgress.setVisibility(View.VISIBLE);
                                viewHolder.tvProgress.setText((int) (downloadedBytes * 100 / totalBytes) + "%");
                                break;
                            case DownloadManager.STATUS_PAUSED:
                                viewHolder.tvStatus.setText("PAUSED");
                                viewHolder.tvStatus.setBackgroundColor(Color.GRAY);
                                viewHolder.progress.setVisibility(View.VISIBLE);
                                viewHolder.progress.setProgress((int) (downloadedBytes * 100 / totalBytes));
                                viewHolder.tvProgress.setVisibility(View.VISIBLE);
                                viewHolder.tvProgress.setText((int) (downloadedBytes * 100 / totalBytes) + "%");
                                break;
                            case DownloadManager.STATUS_RUNNING:
                                viewHolder.tvStatus.setText("RUNNING");
                                viewHolder.tvStatus.setBackgroundColor(Color.GREEN);
                                viewHolder.progress.setVisibility(View.VISIBLE);
                                viewHolder.progress.setProgress((int) (downloadedBytes * 100 / totalBytes));
                                viewHolder.tvProgress.setVisibility(View.VISIBLE);
                                viewHolder.tvProgress.setText((int) (downloadedBytes * 100 / totalBytes) + "%");
                                break;
                            case DownloadManager.STATUS_SUCCESSFUL:
                                viewHolder.tvStatus.setText("COMPLETE");
                                viewHolder.tvStatus.setVisibility(View.GONE);
                                viewHolder.progress.setVisibility(View.GONE);
                                viewHolder.tvProgress.setVisibility(View.GONE);
                                viewHolder.ibPlay.setVisibility(View.VISIBLE);
                                viewHolder.ibPlay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openWithMediaPlayer(fileUri,mimeType);
/*                                        viewHolder.videoView.setVisibility(View.VISIBLE);
                                        viewHolder.ibPlay.setVisibility(View.GONE);
                                        viewHolder.ivPoster.setVisibility(View.GONE);
                                        openWithVideoView(fileUri,mimeType,viewHolder.videoView);*/
                                    }
                                });
                                break;
                            case DownloadManager.STATUS_FAILED:
                                viewHolder.tvStatus.setText("FAILED");
                                viewHolder.tvStatus.setBackgroundColor(Color.RED);
                                viewHolder.progress.setVisibility(View.GONE);
                                viewHolder.tvProgress.setVisibility(View.GONE);
                                break;
                        }
                    }
                }
                c.close();
            }
        }

    }

    private void printUri(Cursor c) {
        for(int i=0;i<c.getColumnCount();i++)
        {
            try {
                String name = c.getColumnName(i);
                String value = c.getString(i);
                System.out.println(name + "\t\t" + value);
            }catch (Exception e){}
        }
    }

    public static void setTextMarquee(TextView textView) {
        if (textView != null) {
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView.setSingleLine(true);
            textView.setSelected(true);
            textView.setFocusable(true);
            textView.setFocusableInTouchMode(true);
        }
    }

    @Override
    public int getItemCount() {
        return App.getInstance().getDownloadCountByType(VideoDetail.TYPE);
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivBlogAvatar;
        public TextView tvBlogName,tvStatus,tvSlug,tvNoteCount,tvSource,tvProgress;
        public Button btnDownload;
        public ImageView ivPoster;
        public ProgressBar progress;
        public ImageButton ibPlay;
        public VideoView videoView;

        public ViewHolder(View v) {
            super(v);
            ivBlogAvatar = v.findViewById(R.id.ivBlogAvatar);
            tvBlogName = v.findViewById(R.id.tvBlogName);
            btnDownload = v.findViewById(R.id.btnDownload);
            ivPoster = v.findViewById(R.id.ivPoster);
            progress = v.findViewById(R.id.progress);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvSlug = v.findViewById(R.id.tvSlug);
            tvNoteCount = v.findViewById(R.id.tvNoteCount);
            tvSource = v.findViewById(R.id.tvSource);
            tvProgress = v.findViewById(R.id.tvProgress);
            ibPlay = v.findViewById(R.id.ibPlay);
            videoView = v.findViewById(R.id.videoView);
        }
    }
    public interface OnClickListener{
        void onDownloadClick(String url, String filename, String ext);
    }
    public void openWithMediaPlayer(String uriString,String mimeType)
    {
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mimeType);
            mContext.startActivity(intent);
     }
     public void openWithVideoView(String uriString,String mimeType,VideoView videoView)
     {
         Uri uri = Uri.parse(uriString);
         videoView.setMediaController(new MediaController(mContext));
         videoView.setVideoURI(uri);
         videoView.start();
         videoView.requestFocus();
     }
}

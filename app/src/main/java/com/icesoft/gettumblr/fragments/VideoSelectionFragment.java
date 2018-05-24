package com.icesoft.gettumblr.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.icesoft.gettumblr.App;
import com.icesoft.gettumblr.Constant;
import com.icesoft.gettumblr.R;
import com.icesoft.gettumblr.Utils.Downloader;
import com.icesoft.gettumblr.Utils.TumblrUtils;
import com.icesoft.gettumblr.adapters.VideoSelectAdapter;
import com.icesoft.gettumblr.models.VideoDetail;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.util.List;

public class VideoSelectionFragment extends Fragment
{
    private Context mContext;
    public static final String ARGUMENTS = "VideoDownloadFragment";
    private View rootView;
    private RecyclerView recyclerView;
    private VideoSelectAdapter adapter;

    public static VideoSelectionFragment newInstance(VideoDetail[] details)
    {
        VideoSelectionFragment fragment = new VideoSelectionFragment();
        Gson gson = new Gson();
        Bundle bundle = new Bundle();
        String resultJson = gson.toJson(details);
        bundle.putString(ARGUMENTS,resultJson);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.videodownload_fragment,container,false);
            recyclerView = rootView.findViewById(R.id.recycler);
            adapter = new VideoSelectAdapter(mContext, new VideoSelectAdapter.OnClickListener() {
                @Override
                public void onDownloadClick(final VideoDetail detail) {
                    download(detail);
                }
            });
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        }
        Bundle bundle = getArguments();
        if(bundle != null)
        {
            String json = bundle.getString(ARGUMENTS);
            Gson gson = new Gson();
            VideoDetail[] details = gson.fromJson(json,VideoDetail[].class);
            adapter.setDetails(details);
        }
        return rootView;
    }

    public void download(VideoDetail detail)
    {
        if(detail != null)
        {
            String souceUrl = TumblrUtils.getUrlFromEmbed(detail.embed_code);
            String ext          = TumblrUtils.getVideoExtFilename(detail.embed_code);
            long id = Downloader.download(mContext,
                    souceUrl,Constant.DEFAULT_DOWNLOAD_PATH ,
                    File.separator + "tumblr" + File.separator + "videos" + File.separator +detail.blogName + File.separator +detail.postId + Constant.FILE_EXT_DOT + ext);
            detail.downloadId = id;
            App.getInstance().addDownloadId(detail);
            ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new DownloadFragment(),DownloadFragment.ARGUMENTS).commit();
        }
    }
}

package com.icesoft.gettumblr.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.icesoft.gettumblr.App;
import com.icesoft.gettumblr.Constant;
import com.icesoft.gettumblr.R;
import com.icesoft.gettumblr.Utils.Downloader;
import com.icesoft.gettumblr.adapters.DownloadingAdapter;
import com.icesoft.gettumblr.adapters.VideoSelectAdapter;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DownloadFragment extends Fragment
{
    private Context mContext;
    public static final String ARGUMENTS = "DownloadFragment";

    private View rootView;
    private RecyclerView recyclerView;
    private DownloadingAdapter adapter;

    private Handler refreshUIHandler = new Handler();

    private long DELAY = 2000l;

    private  Runnable runnable;

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
            adapter = new DownloadingAdapter(mContext, new DownloadingAdapter.OnClickListener() {
                @Override
                public void onDownloadClick(String url, String filename, String ext) {

                }
            });
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        runnable = new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                System.out.println("notify");
                refreshUIHandler.postDelayed(this,DELAY);
            }
        };
        runnable.run();
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshUIHandler.removeCallbacks(runnable);
    }
}



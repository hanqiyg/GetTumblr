package com.icesoft.gettumblr.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.icesoft.gettumblr.Constant;
import com.icesoft.gettumblr.MainActivity;
import com.icesoft.gettumblr.R;

public class TestVideoFragment extends Fragment
{
    public static final String TAG = "TestFragment";
    private static final String T = "TestFragment";
    public static final String BUNDLE_TITLE = "TestFragment.bundle.title";
    private View rootView;
    private VideoView videoView;
    private MediaController controller;
    private String url;

    public static TestVideoFragment newInstance(String url) {
        TestVideoFragment fragment = new TestVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, url);
        fragment.setArguments(bundle);   //设置参数
        System.out.println(T + "@Param[" + url + "].");
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if(null == rootView)
        {
            rootView = inflater.inflate(R.layout.test_video_fragment,container,false);
            videoView = rootView.findViewById(R.id.videoView);
            controller = rootView.findViewById(R.id.controller);
            Uri uri = Uri.parse(url);
            videoView.setMediaController(new MediaController(this.getActivity()));
            videoView.setVideoURI(uri);
            videoView.start();
            videoView.requestFocus();
        }
       return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getArguments() != null) {
            url = getArguments().getString(BUNDLE_TITLE);
        }
    }
}

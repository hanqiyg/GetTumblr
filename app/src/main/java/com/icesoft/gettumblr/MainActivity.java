package com.icesoft.gettumblr;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.icesoft.gettumblr.Utils.TumblrUtils;
import com.icesoft.gettumblr.asynctasks.GetTumblrLinksAsyncTask;
import com.icesoft.gettumblr.fragments.DownloadFragment;
import com.icesoft.gettumblr.fragments.TestFragment;
import com.icesoft.gettumblr.fragments.dialogs.RedownloadDialog;
import com.icesoft.gettumblr.models.BaseDetail;
import com.tumblr.jumblr.types.Post;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

public class MainActivity extends AppCompatActivity{
    private AdView mAdView;
    private AsyncTask<String,Integer,Post> task;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = TestFragment.newInstance("home");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment,fragment)
                            .commit();
                    return true;
                case R.id.navigation_dashboard:
                    fragment = new DownloadFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment,fragment)
                            .commit();
                    return true;
                case R.id.navigation_notifications:
                    fragment = TestFragment.newInstance("notifications");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment,fragment)
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().addTestDevice("A5E542BA67DCA678814B3C4A493D5EFC").build();

        mAdView.loadAd(adRequest);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleTextMessage(intent);
            }
        }
        askPermission();
        TestFragment fragment = TestFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,fragment,TestFragment.TAG).commit();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    public void askPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            AndPermission.with(this).requestCode(100).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE).callback(new PermissionListener() {
                @Override
                public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {

                }

                @Override
                public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                    MainActivity.this.finish();
                }
            }).start();
        }
    }

    private void handleTextMessage(Intent intent)
    {
        askPermission();
        final String share = intent.getStringExtra(Intent.EXTRA_TEXT);
        if(share != null && !share.trim().equals(""))
        {
            final BaseDetail detail = App.getInstance().getDownloadBySource(share);
            if(detail != null)
            {
                RedownloadDialog dialog = new RedownloadDialog();
                dialog.setListener(new RedownloadDialog.OnClickListener() {
                    @Override
                    public void redownload() {
                        App.getInstance().deleteDownloadBySource(share);
                        detail.delete();
                        DownloadManager dm = (DownloadManager) MainActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
                        dm.remove(detail.getDownloadId());
                        getSourceUrl(share);
                    }
                    @Override
                    public void cancel() {

                    }
                });
                dialog.show(getSupportFragmentManager(),"redownload");
            }else{
                getSourceUrl(share);
            }
        }
    }
    private void getSourceUrl(String url)
    {
        if(TumblrUtils.isTumblrLink(url)){
            // Share: https://chicaoni.tumblr.com/post/173956820507	Title: null
            GetTumblrLinksAsyncTask getTumblrLinksAsyncTask = new GetTumblrLinksAsyncTask(this);
            getTumblrLinksAsyncTask.execute(url);
        }else{
            String format = getResources().getString(R.string.link_not_recognized);
            Toast.makeText(this,String.format(format,url), Toast.LENGTH_LONG).show();
        }
    }
    public void test(String url)
    {
        getSourceUrl(url);
    }
}

package com.icesoft.gettumblr.asynctasks;

import android.Manifest;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.icesoft.gettumblr.App;
import com.icesoft.gettumblr.Constant;
import com.icesoft.gettumblr.R;
import com.icesoft.gettumblr.TumblrApiService;
import com.icesoft.gettumblr.Utils.Downloader;
import com.icesoft.gettumblr.Utils.TumblrUtils;
import com.icesoft.gettumblr.exceptions.NotSupportedPostTypeException;
import com.icesoft.gettumblr.fragments.DownloadFragment;
import com.icesoft.gettumblr.fragments.VideoSelectionFragment;
import com.icesoft.gettumblr.fragments.dialogs.NotFoundDialog;
import com.icesoft.gettumblr.fragments.dialogs.ProgressDialog;
import com.icesoft.gettumblr.models.PhotoTumblrResult;
import com.icesoft.gettumblr.models.TumblrResult;
import com.icesoft.gettumblr.models.VideoDetail;
import com.icesoft.gettumblr.models.VideoTumblrResult;
import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GetTumblrLinksAsyncTask extends AsyncTask<String,Integer,TumblrResult> {
    private final WeakReference<AppCompatActivity> mainActivityWeakReference;
    private ProgressDialog progressDialog;
    private NotFoundDialog notFoundDialog;

    public GetTumblrLinksAsyncTask(AppCompatActivity mainActivity) {
        this.mainActivityWeakReference = new WeakReference<>(mainActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDialog == null) {
            progressDialog = new ProgressDialog();
        }
        progressDialog.setListener(new ProgressDialog.OnCancelListener() {
            @Override
            public void cancel() {
                if (!GetTumblrLinksAsyncTask.this.isCancelled()) {
                    GetTumblrLinksAsyncTask.this.cancel(true);
                }
            }
        });
        if (mainActivityWeakReference.get() != null) {
            progressDialog.show(mainActivityWeakReference.get().getSupportFragmentManager(), "");
        }
    }

    @Override
    protected TumblrResult doInBackground(String... strings) {
        TumblrResult result = null;
        if (strings != null && strings.length > 0) {
            try {
                String link = strings[0];
                Post post = TumblrApiService.getPost(link);
                if (post != null && post instanceof VideoPost) {
                    result = getVideoDetails(link,(VideoPost) post);
                }else if(post != null && post instanceof PhotoPost){

                }else{
                    throw new NotSupportedPostTypeException("The post type is not support by GetTumblr. For now we only support VideoPost and PhotoPost.");
                }
            } catch (Exception e) {
                result = new TumblrResult(strings[0], e);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(TumblrResult result) {
        if (progressDialog != null && progressDialog.isVisible()) {
            progressDialog.dismissAllowingStateLoss();
        }
        if (mainActivityWeakReference.get() == null) {
            return;
        }
        if (result.exception != null) {
            if (result.exception instanceof JumblrException && ((JumblrException)result.exception).getResponseCode()==404) {
                if (notFoundDialog == null) {
                    notFoundDialog = new NotFoundDialog();
                }
                if (mainActivityWeakReference.get() != null) {
                    notFoundDialog.show(mainActivityWeakReference.get().getSupportFragmentManager(), "notfound");
                }
            }else{
                Toast.makeText(mainActivityWeakReference.get(), result.exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if (result instanceof VideoTumblrResult) {
            VideoTumblrResult videoTumblrResult = (VideoTumblrResult) result;
            if(videoTumblrResult.getVideoDetails().length ==1){
                final VideoDetail detail = videoTumblrResult.getVideoDetails()[0];
                if(detail != null)
                {
                    final String souceUrl = TumblrUtils.getUrlFromEmbed(detail.embed_code);
                    final String ext          = TumblrUtils.getVideoExtFilename(detail.embed_code);
                    if (mainActivityWeakReference.get() != null) {
                        download(mainActivityWeakReference.get(), souceUrl, ext, detail);
                    }
                }
            }else{
                Fragment fragment = VideoSelectionFragment.newInstance(videoTumblrResult.getVideoDetails());
                mainActivityWeakReference.get().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit();
            }
        } else if (result instanceof PhotoTumblrResult) {
                System.out.println("photo post");
        }
        //System.out.println("onPostExecute end");
    }
    private VideoTumblrResult getVideoDetails(String sourceUrl,VideoPost post) throws InterruptedException, ExecutionException, TimeoutException {
        Set<VideoDetail> videoDetailSet = new HashSet<>();
        VideoDetail videoDetail = null;
        for (Video v : post.getVideos()) {
            videoDetail = new VideoDetail();
            videoDetail.urlQueryString = sourceUrl;
            videoDetail.embed_code = v.getEmbedCode();
            videoDetail.width = v.getWidth();

            videoDetail.postId = post.getId();
            videoDetail.blogName = post.getBlogName();
            videoDetail.postNoteCount = post.getNoteCount();
            videoDetail.postSlug = post.getSlug();
            videoDetail.postSource = post.getSourceTitle();

            final String posterUrl = TumblrUtils.getPosterFromEmbed(v.getEmbedCode());
            if (mainActivityWeakReference.get() != null) {
                File posterFile =
                        Glide.with(mainActivityWeakReference.get())
                                .asFile()
                                .load(posterUrl)
                                .submit().get(1, TimeUnit.MINUTES);
                videoDetail.posterFilePath = posterFile.getAbsolutePath();
            }
            final String avatarUrl = String.format(Constant.AVATAR_FORMATER, post.getBlogName());
            if (mainActivityWeakReference.get() != null) {
                File avatarFile =
                        Glide.with(mainActivityWeakReference.get())
                                .asFile()
                                .load(avatarUrl)
                                .submit().get(1, TimeUnit.MINUTES);
                videoDetail.blogAvatarFilePath = avatarFile.getAbsolutePath();
            }
            videoDetailSet.add(videoDetail);
        }
        return new VideoTumblrResult(sourceUrl, null, videoDetailSet.toArray(new VideoDetail[videoDetailSet.size()]));
    }
    public void download(AppCompatActivity context, String souceUrl, String ext,VideoDetail detail)
    {
        long id = Downloader.download(context,
                souceUrl,Constant.DEFAULT_DOWNLOAD_PATH ,
                File.separator + "tumblr" + File.separator + "videos" + File.separator +detail.blogName + File.separator +detail.postId + Constant.FILE_EXT_DOT + ext);
        detail.downloadId = id;
        App.getInstance().addDownloadId(detail);
        context.getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new DownloadFragment(),DownloadFragment.ARGUMENTS).commit();
    }
}

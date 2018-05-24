package com.icesoft.gettumblr.fragments.dialogs;


import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.icesoft.gettumblr.R;

/**
 * Created by Administrator on 2018/5/8.
 */

public class RedownloadDialog extends DialogFragment
{
    private View rootView;
    private TextView tvMessage;
    private Button btnRedownload,btnCancel;
    private OnClickListener listener;
    public interface OnClickListener
    {
        void redownload();
        void cancel();
    }
    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    cancel();
                    getDialog().dismiss();
                    return true;
                }
                return false;
            }
        });
        if(null == rootView){
            rootView = inflater.inflate(R.layout.redownload_dialog_fragment,container,false);

            tvMessage = rootView.findViewById(R.id.tvMessage);
            tvMessage.setText(getResources().getString(R.string.redownload_message));

            btnRedownload = rootView.findViewById(R.id.btnRedownload);
            btnRedownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    redownload();
                    getDialog().dismiss();
                }
            });
            btnCancel  = rootView.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    cancel();
                    getDialog().dismiss();
                }
            });
        }
        return rootView;
    }

    private void redownload() {
        if(listener != null)
        {
            listener.redownload();
        }
    }
    public void cancel()
    {
        if(listener != null)
        {
            listener.cancel();
        }
    }
}

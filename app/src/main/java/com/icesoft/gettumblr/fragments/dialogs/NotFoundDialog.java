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

public class NotFoundDialog extends DialogFragment
{
    private View rootView;
    private TextView tvMessage;
    private Button btnCancel;

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
                    getDialog().dismiss();
                    return true;
                }
                return false;
            }
        });
        if(null == rootView){
            rootView = inflater.inflate(R.layout.notfound_dialog_fragment,container,false);
            btnCancel  = rootView.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });
        }
        tvMessage = rootView.findViewById(R.id.tvMessage);
        tvMessage.setText(getResources().getString(R.string.not_found));
        return rootView;
    }
}

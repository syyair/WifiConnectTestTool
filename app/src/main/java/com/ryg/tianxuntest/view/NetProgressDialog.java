package com.ryg.tianxuntest.view;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by renyiguang on 2015/7/17.
 */
public class NetProgressDialog extends ProgressDialog {

    public NetProgressDialog(Context context, int messageId) {
        this(context,context.getResources().getString(messageId));
    }

    public NetProgressDialog(Context context, String message) {
        super(context);
        setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setMessage(message);
        setIndeterminate(false);
        setCancelable(false);
    }
}

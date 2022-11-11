package com.ilinklink.tg.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import com.ilinklink.app.fw.R;

/**
 * 上传数据成功dialog
 */
public class DialogUploadSuccess {
    AlertDialog dialog;

    public DialogUploadSuccess(Activity context) {

        View inflate = context.getLayoutInflater().inflate(R.layout.dialog_upload_success, null, false);
        inflate.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(context).setView(inflate).create();


    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}

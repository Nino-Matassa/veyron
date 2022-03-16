package ie.aranearum.tela.veyron;

import android.content.Context;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UIMessage {
    private SweetAlertDialog dialogMsg = null;
    private Context context = null;

    public UIMessage(Context context) {
        this.context = context;
        dialogMsg = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        dialogMsg.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    }

    private void informationBox(String title, String msg) {
        ((AppCompatActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(msg == null) {
                    dialogMsg.dismiss();
                } else {
                    dialogMsg.setTitleText(msg);
                    dialogMsg.setCancelable(true);
                    dialogMsg.show();
                }
            }
        });
    }
}
package ie.aranearum.tela.veyron;

import android.content.Context;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UIMessage {
    private static SweetAlertDialog dialogMsg = null;


    public static void eyeCandy(Context context, String message) {
        ((AppCompatActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(dialogMsg == null) {
                    dialogMsg = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
                    dialogMsg.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                }
                if(message == null) {
                    dialogMsg.hide();
                } else {
                    dialogMsg.setTitleText(message);
                    dialogMsg.setCancelable(true);
                    dialogMsg.show();
                }
            }
        });
    }
}
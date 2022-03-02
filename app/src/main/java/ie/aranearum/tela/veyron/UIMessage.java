package ie.aranearum.tela.veyron;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UIMessage {
    private static SweetAlertDialog pDialog = null;

    public static void informationBox(final Context context, final String msg) {
        if(msg == null) {
            pDialog.dismiss();
        } else {
            pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText(msg);
            pDialog.setCancelable(true);
            pDialog.show();
        }
    }

    public static String abbreviate(String text, int length) {
        return text.length() < Constants.abbreviate ? text: text.substring(0, Constants.abbreviate - 3) + "...";
    }
}
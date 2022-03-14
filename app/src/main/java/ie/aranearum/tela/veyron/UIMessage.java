package ie.aranearum.tela.veyron;

import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UIMessage {
    private static SweetAlertDialog dialogMsg = null;
    private static boolean csvIsUpdated = false;

    public static void informationBox(final Context context, final String msg) {
        if(msg == null) {
            dialogMsg.dismiss();
        } else {
            dialogMsg = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            dialogMsg.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            dialogMsg.setTitleText(msg);
            dialogMsg.setCancelable(true);
            dialogMsg.show();
        }
    }

    public static String abbreviate(String text, int length) {
        return text.length() < Constants.abbreviate ? text: text.substring(0, Constants.abbreviate - 3) + "...";
    }

    public static boolean isCsvIsUpdated() {
        return csvIsUpdated;
    }

    public static void setCsvIsUpdated(boolean csvIsUpdated) {
        UIMessage.csvIsUpdated = csvIsUpdated;
    }
}
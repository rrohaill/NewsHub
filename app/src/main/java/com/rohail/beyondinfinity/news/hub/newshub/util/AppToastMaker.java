package com.rohail.beyondinfinity.news.hub.newshub.util;

import android.content.Context;
import android.widget.Toast;


public class AppToastMaker {

    public static void showLongToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

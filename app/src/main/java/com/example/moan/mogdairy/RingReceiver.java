package com.example.moan.mogdairy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

//let user to choose vibrate or not / allow light or not sometime later

public class RingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ShowNotification showNotification = new ShowNotification(context,
                "Time up",
                intent.getStringExtra("clock_title"),
                "CLOCK",
                "clock info",
                false,
                false,
                false,
                true,
                5);
        Log.d("moanbigking", intent.getStringExtra("clock_title"));
    }

}
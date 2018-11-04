package com.example.moan.mogdairy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
    }



//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String ChannelId = "CLOCK";
//            String ChannelName = "clock info";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            createNotificationChannel(context, false, false, false, ChannelId, ChannelName, importance);
//        }
//        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CLOCK");
//        Notification notification = builder
//                .setContentTitle("Time up !")
//                .setContentText(intent.getStringExtra("clock_title"))
//                .setSmallIcon(R.mipmap.alert)
//                .setAutoCancel(true)
//                .build();
//        notificationManager.notify(5, notification);

    //the former "}" located here.


//    @TargetApi(Build.VERSION_CODES.O)
//    public void createNotificationChannel(Context context,
//                                          boolean isVibrate,
//                                          boolean hasSound,
//                                          boolean allowLighted,
//                                          String ChannelId,
//                                          String ChannelName,
//                                          int importance) {
//        NotificationChannel notificationChannel = new NotificationChannel(ChannelId, ChannelName, importance);
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        notificationChannel.enableVibration(isVibrate);
//        notificationChannel.enableLights(allowLighted);
//        if (!hasSound) {
//            notificationChannel.setSound(null, null);
//        }
//        if (notificationManager != null) {
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
//    }
}
package com.example.moan.mogdairy;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ShowNotification {
    public ShowNotification(Context context,
                            String title,
                            String info,
                            String channelId,
                            String channelName,
                            boolean isVibrate,
                            boolean hasSound,
                            boolean allowLighted,
                            boolean autoCancel,
                            int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(context, isVibrate, hasSound, allowLighted, channelId, channelName, importance);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);


        Notification notification = builder
                .setContentTitle(title)
                .setContentText(info)
                .setSmallIcon(R.mipmap.alert)
                .setAutoCancel(autoCancel)
                .build();
        notificationManager.notify(id, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationChannel(Context context,
                                          boolean isVibrate,
                                          boolean hasSound,
                                          boolean allowLighted,
                                          String ChannelId,
                                          String ChannelName,
                                          int importance) {
        NotificationChannel notificationChannel = new NotificationChannel(ChannelId, ChannelName, importance);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationChannel.enableVibration(isVibrate);
        notificationChannel.enableLights(allowLighted);
        if (!hasSound) {
            notificationChannel.setSound(null, null);
        }
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}

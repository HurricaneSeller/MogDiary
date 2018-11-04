package com.example.moan.mogdairy;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        try {
            // place the music in assets folder
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd("first.mp3");
            mediaPlayer.setDataSource(assetFileDescriptor);
            mediaPlayer.prepare();
            mediaPlayer.start();
            ShowNotification musicNotification = new ShowNotification(this,"music paly",
                    "listen","MUSIC","MUSIC START",false,
                    false,false,true,4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }
}

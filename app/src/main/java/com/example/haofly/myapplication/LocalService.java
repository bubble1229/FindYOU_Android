package com.example.haofly.myapplication;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by haofly on 15/8/23.
 */
public class LocalService extends Service{
    private IBinder binder = new LocalService.LocalBinder();

    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }

    @Override
    public void onCreate(){
        Log.v("haofly", "create");
        int a = 1000;
        while(a > 0){
            Log.v("haofly", "success");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            a = a-1;
        }
    }


    public class LocalBinder extends Binder {
        LocalService getService(){
            return LocalService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

}

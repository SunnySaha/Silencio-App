package com.m.timepicker;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class Backgroundservice extends Service implements MediaPlayer.OnPreparedListener {
    int delay1, state, index = 0;

    public boolean isr = false;
    static int lol;
    List<ScheduleInform> scheduleinfolist;
    PowerManager.WakeLock wakelock;
    Intent intent;
    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    private CountDownTimer countDownTimer;
    MediaPlayer mediaPlayer;


    //    public class Timethread extends Thread {
//        int delay, stid;
//        Context context;
//
//        Timethread(int d, Context c, int st) {
//            context = c;
//            delay = d;
//            lol=d;
//            stid = st;
//        }
//
//        @Override
//        public void run() {
//
//            Log.e("this","before thread"+delay);
//            final AudioManager ad = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//           if(scheduleinfolist.get(index).getVol()==2) ad.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//           else {
//               ad.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//           }
//            for (int i = 1000; i <= delay; i+=1000) {
//                try {
//                    Log.e("this",""+lol);
//
//                    lol-=1000;
//
//                    savelstvalue(lol);
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            Log.e("this","after thread");
//            ad.setRingerMode(AudioManager.RINGER_MODE_NORMAL);//norm(ad,chkk,intent);
//            stopSelf(stid); //service cancelling
//
//        }
//    }
    public void Timethread(int d, Context c, int st) {
        isr = false;
        final int delay, stid;
        final Context context;
        context = c;
        delay = d;
        lol = d;
        stid = st;
       // Log.e("this", "before thread" + delay);
        final AudioManager ad = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (scheduleinfolist.get(index).getVol() == 2) {

            ad.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

        }
        else {
            ad.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        countDownTimer = new CountDownTimer(delay, 1000) {
            @Override
            public void onTick(long l) {
                lol = (int) l;
                savelstvalue(lol);
                isr = true;
               // Log.e("lol", l + "<->" + lol+ " delay= "+delay);
            }

            @Override
            public void onFinish() {

                countDownTimer.cancel();
                isr = false;
              //  Log.e("this", "after thread");

                ad.setRingerMode(AudioManager.RINGER_MODE_NORMAL);//norm(ad,chkk,intent);
                playMusic(context);
                stopSelf(stid); //service cancelling
            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // Log.e("wakelock","wakelock acquire1");
        this.context = this;
        this.isRunning = false;
        loadData(context);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getCanonicalName());
        wakelock.acquire();

    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
        if (isr == true) {
            countDownTimer.cancel();
        }
        if(wakelock.isHeld()){
            wakelock.release();
           // Log.e("wakelock", "wakelock release 2");
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e("asdf","onstart");
        this.intent = intent;
        if (intent != null) {
            index = intent.getIntExtra("value", 0);
            delay1 = scheduleinfolist.get(index).getDelay();
            state = scheduleinfolist.get(index).getVol();
            savelstvalue(delay1);
            delay1 = getlastvalue();
        } else {
            delay1 = getlastvalue();
        }


        if (!isRunning) {
            //Toast.makeText(context, "Kaj korsay reh", Toast.LENGTH_SHORT).show();
            isRunning = true;
//            Timethread timethread = new Timethread(delay1, context, startId);
//            timethread.start();
            Timethread(delay1, context, startId);
        }
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        if (isr == true) {
            countDownTimer.cancel();
        }
       // Toast.makeText(context, "ontaskcommand working", Toast.LENGTH_SHORT).show();

    }

    public void loadData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Infos", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("lists", null);
        Type type = new TypeToken<ArrayList<ScheduleInform>>() {
        }.getType();
        scheduleinfolist = gson.fromJson(json, type);
        if (scheduleinfolist == null) {
            scheduleinfolist = new ArrayList<>();
        }

        // Log.e("context","context "+s);
    }

    public void savelstvalue(int d) {//service restart oilay abar ou vaue takia restart oibo
        SharedPreferences sharedPreferences = this.getSharedPreferences("Lastvalue", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("last", d);
        editor.putInt("index", index);
        editor.commit();

    }

    public int getlastvalue() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("Lastvalue", Context.MODE_PRIVATE);
        int t = sharedPreferences.getInt("last", 0);
        index = sharedPreferences.getInt("index", 0);
        //Log.e("t=",""+t);
        return t;
    }




    private void playMusic(Context context) {
        mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.successsound);
       // mediaPlayer.start();
        mediaPlayer.setOnPreparedListener(this);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
            }
        });

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}

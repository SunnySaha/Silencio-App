package com.m.timepicker;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class SetBroadcast extends BroadcastReceiver implements MediaPlayer.OnPreparedListener {
    static int count = 0;
    AlarmManager mgrAlarm;
    PowerManager.WakeLock wakelock;
    Context context;
    int delay;
    List<ScheduleInform> scheduleinfolist;
    private Button button;
    private final String CHANNEL_ID = "Primary Notifications";
    private final int Notification_id = 001;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Kaj korsay reh", Toast.LENGTH_SHORT).show();
        // loadData(context);
        // Log.e("Onrecieve","onreceive "+(++count));
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getCanonicalName());
        wakelock.acquire();
        mgrAlarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        this.context = context;
        loadData();
        int d = intent.getIntExtra("values", 0);
        // delay=scheduleinfolist.get(d).getDelay();
        playMusic(context);
        Intent back = new Intent(context, Backgroundservice.class);
        back.putExtra("value", d);
        notifi(context, d);
        context.startService(back);
        setAgainAlaram(d, context);

        //  silent(context);
    }


    private void notifi(Context context, int index) {

        notificationChannel(context,index);
        //CREATE INTENT
        Intent intent = new Intent(context.getApplicationContext(), ShowEvents.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //END OF INTENT

        //CREATE A PENDING INTENT

        PendingIntent pendingIntent = PendingIntent.getActivities(context.getApplicationContext(), 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);
        //END OF PENDING INTENT
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.smallicon);
        String s = "";
        if (scheduleinfolist.get(index).getVol() == 1) s = "Your phone is in silent mode";
        else s = "Your phone is in vibrate mode";
        builder.setContentTitle("Silencio");
        builder.setContentText(s);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
        notificationManagerCompat.notify(Notification_id, builder.build());
    }

    private void notificationChannel(Context context,int index) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Silencio";
            String s = "";
            if (scheduleinfolist.get(index).getVol() == 1) s = "Your phone is in silent mode";
            else s = "Your phone is in vibrate mode";


            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // create notification channel object
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(s);

            //create notification manager object which hold notification channel

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel); // create notification channel through manager object


        }
    }

    /*
    private void silent(final Context context) {

        final AudioManager ad= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ad.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        final Handler handler = new Handler();

        Runnable run = new Runnable() {
            @Override
            public void run() {
                // arr[chkk]=arr[chkk]-1000;
                delay-=1000;
                handler.postDelayed(this,1000);
                if(delay<=0){
                    Toast.makeText(context, "thik thak", Toast.LENGTH_SHORT).show();
                    handler.removeCallbacks(this);
                    ad.setRingerMode(AudioManager.RINGER_MODE_NORMAL);//norm(ad,chkk,intent);

                }
            }
        };
        handler.post(run);
    }
    public void loadData(Context context) {
        // Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = context.getSharedPreferences("Infos", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("lists", null);
        Type type = new TypeToken<ArrayList<ScheduleInform>>() {
        }.getType();
        scheduleinfolist = gson.fromJson(json, type);
        if (scheduleinfolist == null) {
            scheduleinfolist = new ArrayList<>();
        }

    }*/

    private void playMusic(Context context) {
        mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.bottleopen);
        // mediaPlayer.start();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
            }
        });

    }

    private void setAgainAlaram(int index, Context context) {
        Log.e("setagain", "setagain");
        Calendar calendar = Calendar.getInstance();
        String date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        String[] s1 = date.split(",");
        String day = s1[0];
        String s, p, q;
        s = scheduleinfolist.get(index).getStartTime();
        p = q = s;
        s = s.substring(0, 2);// time hr
        p = p.substring(3, 5);  //time mn
        q = q.substring(6, 8); //peram Am/Pm
        int t1 = Integer.parseInt(s);
        int t2 = Integer.parseInt(p);
        if (q.equals("PM") && t1 != 12) t1 += 12;
        if (q.equals("AM") && t1 == 12) t1 = 0;
        Intent intent = new Intent(context.getApplicationContext(), SetBroadcast.class);
        intent.putExtra("values", index);
        getnsetIndividualIds(intent, index, t1, t2, day);

    }

    private void getnsetIndividualIds(Intent intent, int ide, int t1, int t2, String day) {
        // Log.e("getnsindividula","getnsindividula");
        Intent in = intent;
        int id = 0, d = 0;
        if (day.equals("Saturday")) {
            id = scheduleinfolist.get(ide).getSaturday();
            d = 7;
            // Log.e("Day = ",day);
        } else if (day.equals("Sunday")) {
            id = scheduleinfolist.get(ide).getSunday();
            d = 1;
            // Log.e("Day = ",day);
        } else if (day.equals("Monday")) {
            id = scheduleinfolist.get(ide).getMonday();
            d = 2;
            // Log.e("Day = ",day);
        } else if (day.equals("Tuesday")) {
            id = scheduleinfolist.get(ide).getTuesday();
            d = 3;
            //Log.e("Day = ",day);
        } else if (day.equals("Wednesday")) {
            id = scheduleinfolist.get(ide).getWednesday();
            d = 4;
            // Log.e("Day = ",day);
        } else if (day.equals("Thursday")) {
            id = scheduleinfolist.get(ide).getThursday();
            d = 5;
            // Log.e("Day = ",day);
        } else if (day.equals("Friday")) {
            id = scheduleinfolist.get(ide).getFriday();
            d = 6;
            // Log.e("Day = ",day);
        }

        boolean bol = scheduleinfolist.get(ide).isSwi();
        // Toast.makeText(this, "id-> " + ide + " bol-> " + bol, Toast.LENGTH_LONG).show();

        if (bol == true) {
            setTime(t1, t2, id, d, in);
        }


    }


    public void setTime(int hr, int min, int id, int day, Intent intent) {
        // Log.e("setTime ",""+hr+":"+min);
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.DAY_OF_WEEK, day);  //here pass week number
        calender.set(Calendar.HOUR_OF_DAY, hr);  //pass hour which you have select
        calender.set(Calendar.MINUTE, min);  //pass min which you have select
        calender.set(Calendar.SECOND, 0);
        calender.set(Calendar.MILLISECOND, 0);

        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);


        calender.add(Calendar.DATE, 7);


        Intent intentt = intent;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), id, intentt, PendingIntent.FLAG_UPDATE_CURRENT);
        //mgrAlarm.setRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pendingIntent);
        // mgrAlarm.set(AlarmManager.RTC_WAKEUP,calender.getTimeInMillis(), pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mgrAlarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
            //Log.e("Marshmello",hr+" marsh "+min);
        } else {
            mgrAlarm.setExact(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
            //Log.e(" not Marshmello",hr+" not marsh "+min );

        }

        if (wakelock.isHeld()) wakelock.release();
    }

    public void loadData() {
        // Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = context.getSharedPreferences("Infos", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("lists", null);
        Type type = new TypeToken<ArrayList<ScheduleInform>>() {
        }.getType();
        scheduleinfolist = gson.fromJson(json, type);
        if (scheduleinfolist == null) {
            scheduleinfolist = new ArrayList<>();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}

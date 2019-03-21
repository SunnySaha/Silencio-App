package com.m.timepicker;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RestartAlarmsService extends IntentService {
    AlarmManager mgrAlarm;
    List<ScheduleInform> scheduleinfolist;

    public RestartAlarmsService() {
        super("RestartAlarmsService");
        scheduleinfolist=new ArrayList<>();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        scheduleinfolist=new ArrayList<>();
        mgrAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        loadData();
       // Toast.makeText(this, "After Reboot", Toast.LENGTH_SHORT).show();
        Log.e("reboot","after reboot "+scheduleinfolist.size());
        for(int i=0;i<scheduleinfolist.size();i++){
            String s, p, q;
            s = scheduleinfolist.get(i).getStartTime();
            p = q = s;
            s = s.substring(0, 2);// time hr
            p = p.substring(3, 5);  //time mn
            q = q.substring(6, 8); //peram Am/Pm
            int t1 = Integer.parseInt(s);
            int t2 = Integer.parseInt(p);
            if (q.equals("PM")&&t1!=12) t1 += 12;
            if(q.equals("AM")&&t1==12)t1=0;
            intent=new Intent(getApplicationContext(),SetBroadcast.class);
            intent.putExtra("values",i);
            getnsetIndividualIds(intent,i,t1,t2);
        }


        //rest code write here


    }


    private void getnsetIndividualIds(Intent intent,int ide, int t1, int t2) {
        Intent in=intent;
        int stid, suid, moid, tueid, wedid, thuid, friid;
        stid = scheduleinfolist.get(ide).getSaturday();
        suid = scheduleinfolist.get(ide).getSunday();
        moid = scheduleinfolist.get(ide).getMonday();
        tueid = scheduleinfolist.get(ide).getTuesday();
        wedid = scheduleinfolist.get(ide).getWednesday();
        thuid = scheduleinfolist.get(ide).getThursday();
        friid = scheduleinfolist.get(ide).getFriday();
        boolean bol = scheduleinfolist.get(ide).isSwi();
        Toast.makeText(this, "id-> " + ide + " bol-> " + bol, Toast.LENGTH_LONG).show();

        if(stid!=-1&&bol==true){
            setTime(t1,t2,stid,7,in);
        }
        if(suid!=-1&&bol==true){
            setTime(t1,t2,suid,1,in);
        }
        if(moid!=-1&&bol==true){
            setTime(t1,t2,moid,2,in);
        }
        if(tueid!=-1&&bol==true){
            setTime(t1,t2,tueid,3,in);
        }
        if(wedid!=-1&&bol==true){
            setTime(t1,t2,wedid,4,in);
        }
        if(thuid!=-1&&bol==true){
            setTime(t1,t2,thuid,5,in);
        }
        if(friid!=-1&&bol==true){
            setTime(t1,t2,friid,6,in);
        }

    }


    public void setTime(int hr, int min, int id, int day,Intent intent) {
     //   Log.e("setTime ",""+hr+":"+min);
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.DAY_OF_WEEK, day);  //here pass week number
        calender.set(Calendar.HOUR_OF_DAY, hr);  //pass hour which you have select
        calender.set(Calendar.MINUTE, min);  //pass min which you have select
        calender.set(Calendar.SECOND, 0);
        calender.set(Calendar.MILLISECOND, 0);

        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        if (calender.before(now)) {    //past alarm jatey fire na oyy
            calender.add(Calendar.DATE, 7);
        }

        Intent intentt = intent;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intentt, PendingIntent.FLAG_UPDATE_CURRENT);
        //mgrAlarm.setRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pendingIntent);
        // mgrAlarm.set(AlarmManager.RTC_WAKEUP,calender.getTimeInMillis(), pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            mgrAlarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
        else
            mgrAlarm.setExact(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
    }

    public void loadData() {
        // Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = getSharedPreferences("Infos", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("lists", null);
        Type type = new TypeToken<ArrayList<ScheduleInform>>() {
        }.getType();
        scheduleinfolist = gson.fromJson(json, type);
        if (scheduleinfolist == null) {
            scheduleinfolist = new ArrayList<>();
        }
    }

}

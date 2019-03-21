package com.m.timepicker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class CustomAdaptar extends BaseAdapter {
    Intent uniintent;
    Switch stc;
    LayoutInflater layoutInflater;
    List<ScheduleInform> slist;
    Context context;
    AlarmManager mgrAlarm;
    HashMap<Integer, PendingIntent> pendinintent;
    boolean isLoading = true;

    int size;

    CustomAdaptar(Context context, List<ScheduleInform> sc) {
        slist = sc;
        this.context = context;
        pendinintent = new HashMap<>();

        size = sc.size();
        //Log.e("Hah ", "asfak-> " + size);
        loadData();
    }

    @Override
    public int getCount() {
        return slist.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        isLoading = true;
        loadData();
        if (view == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = layoutInflater.inflate(R.layout.custom_layout, null, false);

        }

        mgrAlarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);


        final TextView name, day, times;
        name = view.findViewById(R.id.name);
        day = view.findViewById(R.id.days);
        times = view.findViewById(R.id.times);

        stc = view.findViewById(R.id.swibtn);
        String s = "";

        s = slist.get(i).getName();
        name.setText(s);
        s = "";
        if (slist.get(i).getSaturday() != -1) s = s + "Sat";
        if (slist.get(i).getSunday() != -1) {
            if (!s.equals("")) s += ",";
            s += "Sun";
        }
        if (slist.get(i).getMonday() != -1) {
            if (!s.equals("")) s += ",";
            s += "Mon";
        }
        if (slist.get(i).getTuesday() != -1) {
            if (!s.equals("")) s += ",";
            s += "Tue";
        }
        if (slist.get(i).getWednesday() != -1) {
            if (!s.equals("")) s += ",";
            s += "Wed";
        }
        if (slist.get(i).getThursday() != -1) {
            if (!s.equals("")) s += ",";
            s += "Thu";
        }
        if (slist.get(i).getFriday() != -1) {
            if (!s.equals("")) s += ",";
            s += "Fri";
        }

        day.setText(s);
        day.setTextColor(Color.parseColor("#fefeff"));
        s = "";
        s += slist.get(i).getStartTime();
        s += " - ";
        s += slist.get(i).getEndTime();
        times.setText(s);
       // Log.e("index",""+slist.get(i).isSwi());
        if (slist.get(i).isSwi()) {
            name.setTextColor(Color.parseColor("#f9f0f0"));
            day.setTextColor(Color.parseColor("#f9f0f0"));
            times.setTextColor(Color.parseColor("#f9f0f0"));
        } else {
            name.setTextColor(Color.parseColor("#78909C"));//#f5ebeb
            day.setTextColor(Color.parseColor("#78909C"));//#67edd7
            times.setTextColor(Color.parseColor("#78909C"));//#f5ebeb
        }
        stc.setOnCheckedChangeListener(null);
        stc.setChecked(slist.get(i).isSwi());
        stc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!isLoading) {
                    if (b) {
                       // loadData();
                        name.setTextColor(Color.parseColor("#f9f0f0"));
                        day.setTextColor(Color.parseColor("#f9f0f0"));
                        times.setTextColor(Color.parseColor("#f9f0f0"));
                        slist.get(i).setSwi(true);
                        setSharedpref();
                      //  Log.e("bhiteyon",""+slist.get(i).isSwi());
                        setTimee(i, 0);

                    } else {
                        // Toast.makeText(context, "of", Toast.LENGTH_SHORT).show();
                        //loadData();
                        name.setTextColor(Color.parseColor("#78909C"));//#f5ebeb
                        day.setTextColor(Color.parseColor("#78909C"));//#67edd7
                        times.setTextColor(Color.parseColor("#78909C"));//#f5ebeb
                        slist.get(i).setSwi(false);
                        setSharedpref();
                       // Log.e("bhiteyoff",""+slist.get(i).isSwi());
                        setTimee(i, 1);

                    }
                } else {
                    //nothing
                }
            }
        });
        isLoading=false;
        return view;

    }


    public void setSharedpref() {
        Log.e("shared", "sharedpref");
        SharedPreferences sharedPreferences = context.getSharedPreferences("Infos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(slist);
        editor.putString("lists", json);
        editor.commit();
        loadData();
    }

    public void loadData() {
        // Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = context.getSharedPreferences("Infos", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("lists", null);
        Type type = new TypeToken<ArrayList<ScheduleInform>>() {
        }.getType();
        slist = gson.fromJson(json, type);
        if (slist == null) {
            slist = new ArrayList<>();
        }

    }


    public void setTimee(int id, int chk) {
        String s, q;
        s = slist.get(id).getStartTime();
        q = s;
        q = q.substring(6, 8); //peram Am/Pm
        int t1 = Integer.parseInt(s.substring(0, 2));//hour
        int t2 = Integer.parseInt(s.substring(3, 5));//minute
        if (q.equals("PM") && t1 != 12) t1 += 12;
        if (q.equals("AM") && t1 == 12) t1 = 0;
        getnsetIndividualIds(id, t1, t2, chk);
        // Toast.makeText(this, " ogu "+t1+t2, Toast.LENGTH_SHORT).show();
    }


    //chk oisay delete na abar set kora or alarm er day
    private void getnsetIndividualIds(int ide, int t1, int t2, int chk) {

        int stid, suid, moid, tueid, wedid, thuid, friid;
        stid = slist.get(ide).getSaturday();
        suid = slist.get(ide).getSunday();
        moid = slist.get(ide).getMonday();
        tueid = slist.get(ide).getTuesday();
        wedid = slist.get(ide).getWednesday();
        thuid = slist.get(ide).getThursday();
        friid = slist.get(ide).getFriday();
        if (stid != -1) {
            if (chk == 0)
                setTime(ide,t1, t2, stid, 7);
            else updatePendingIntent(stid);
        }

        if (suid != -1) {
            if (chk == 0)
                setTime(ide,t1, t2, suid, 1);
            else updatePendingIntent(suid);
        }
        if (moid != -1) {
            if (chk == 0)
                setTime(ide,t1, t2, moid, 2);
            else updatePendingIntent(moid);
        }
        if (tueid != -1) {
            if (chk == 0)
                setTime(ide,t1, t2, tueid, 3);
            else updatePendingIntent(tueid);
        }
        if (wedid != -1) {
            if (chk == 0)
                setTime(ide,t1, t2, wedid, 4);
            else updatePendingIntent(wedid);
        }
        if (thuid != -1) {
            if (chk == 0)
                setTime(ide,t1, t2, thuid, 5);
            else updatePendingIntent(thuid);
        }
        if (friid != -1) {
            if (chk == 0)
                setTime(ide,t1, t2, friid, 6);
            else updatePendingIntent(friid);
        }
    }

    public void setTime(int ide,int hr, int min, int id, int day) {
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
           // Log.e("past","past "+day);
        }
        Intent intent = new Intent(context.getApplicationContext(), SetBroadcast.class);
        intent.putExtra("values",ide);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            mgrAlarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
           // Log.e("Marshmello",hr+" marsh "+min);
        }
        else{
            mgrAlarm.setExact(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
            //Log.e(" not Marshmello",hr+" not marsh "+min );

        }        // mgrAlarm.set(AlarmManager.RTC_WAKEUP,calender.getTimeInMillis(), pendingIntent);
    }

    public void updatePendingIntent(int id) {
        //Log.e("showevent ",""+ShowEvents.this);
        Intent intent = new Intent(context.getApplicationContext(), SetBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mgrAlarm.cancel(pendingIntent);
        pendingIntent.cancel();
        // Toast.makeText(this, "dekhajouk " + id, Toast.LENGTH_SHORT).show();

    }
}

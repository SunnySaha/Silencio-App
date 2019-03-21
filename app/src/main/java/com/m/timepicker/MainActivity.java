package com.m.timepicker;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import android.app.NotificationManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.Toolbar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {

    boolean[] daysarr = new boolean[9];
    Toolbar toolbar;
    int st = -1, su = -1, mo = -1, tu = -1, we = -1, th = -1, volume = 2;
    int fr = -1, hr1, hr2, mn1, mn2, chkEdit = -1, finaltime = 0;
    int[] arr = new int[160];
    private TextView eventshow;
    private ImageView slnt, vbrt;
    private EditText startTime, endTime, eventname;

    private String Starttime, Endtime, Name, peram1, peram2, stg = "", changablest = "";

    private CheckBox sat, sun, mon, tue, wed, thu, fri, every;

    List<ScheduleInform> scheduleinfolist;
    List<Integer> ids = new ArrayList<>(160);

    LinearLayout layout11, layout12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getThemes()) setTheme(R.style.MyTheme3);
        else setTheme(R.style.MyTheme2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Arrays.fill(daysarr, false);
        String edit = getIntent().getStringExtra("edit");
        chkinit();
        if (stg == null) initid();//just ekbar init korar lagia
        loadData();
        loadIds();
        initialize();
        setThemes(getThemes());
        chkEdit = getIntent().getIntExtra("Edit", -1);
        if (chkEdit != -1) {
            setwidgetsForEdit(chkEdit);
            eventshow.setText("Edit Event");
        } else eventshow.setText("Add Event");

    }

    private void setwidgetsForEdit(int pos) {
        String s = scheduleinfolist.get(pos).getStartTime(), q;
        q = s;
        peram1 = q.substring(6, 8); //peram Am/Pm
        hr1 = Integer.parseInt(s.substring(0, 2));
        mn1 = Integer.parseInt(s.substring(3, 5));
        s = scheduleinfolist.get(pos).getEndTime();
        q = s;
        peram2 = q.substring(6, 8); //peram Am/Pm
        hr2 = Integer.parseInt(s.substring(0, 2));
        mn2 = Integer.parseInt(s.substring(3, 5));
//        Log.e("Ampm->", peram1 + " " + peram2);
//        Log.e("time1", hr1 + ":" + mn1);
//        Log.e("time2", hr2 + ":" + mn2);
        eventname.setText(scheduleinfolist.get(pos).getName());
        startTime.setText(scheduleinfolist.get(pos).getStartTime());
        endTime.setText(scheduleinfolist.get(pos).getEndTime());
        int u = scheduleinfolist.get(pos).getVol();
        if (u == 1) {
            slnt.setColorFilter(Color.rgb(75, 251, 24));
            vbrt.setColorFilter(Color.rgb(238, 251, 248));
        } else {
            vbrt.setColorFilter(Color.rgb(75, 251, 24));
            slnt.setColorFilter(Color.rgb(238, 251, 248));
        }
        int cnt = 0;
        if (scheduleinfolist.get(pos).getSaturday() != -1) {
            ++cnt;
            sat.setChecked(true);
            daysarr[1]=true;
            //Log.e("sat",""+scheduleinfolist.get(pos).getSaturday());
        }
        else  daysarr[1]=false;

        if (scheduleinfolist.get(pos).getSunday() != -1) {
            ++cnt;
            sun.setChecked(true);
            daysarr[2]=true;

            //  Log.e("sun",""+scheduleinfolist.get(pos).getSunday());
        }
        else   daysarr[2]=false;

        if (scheduleinfolist.get(pos).getMonday() != -1) {
            ++cnt;
            mon.setChecked(true);
            daysarr[3]=true;

            // Log.e("mon",""+scheduleinfolist.get(pos).getMonday());

        }
        else daysarr[3]=false;
        if (scheduleinfolist.get(pos).getTuesday() != -1) {
            ++cnt;
            tue.setChecked(true);
            daysarr[4]=true;
            // Log.e("tues",""+scheduleinfolist.get(pos).getTuesday());

        }
        else daysarr[4]=false;
        if (scheduleinfolist.get(pos).getWednesday() != -1) {
            ++cnt;
            wed.setChecked(true);
            daysarr[5]=true;
            // Log.e("wed",""+scheduleinfolist.get(pos).getWednesday());

        }
        else daysarr[5]=false;
        if (scheduleinfolist.get(pos).getThursday() != -1) {
            ++cnt;
            thu.setChecked(true);
            daysarr[6]=true;
            //Log.e("thur",""+scheduleinfolist.get(pos).getThursday());

        }
        else daysarr[6]=false;
        if (scheduleinfolist.get(pos).getFriday() != -1) {
            ++cnt;
            fri.setChecked(true);
            // Log.e("fri",""+scheduleinfolist.get(pos).getFriday());
            daysarr[7]=true;
        }
        else daysarr[7]=false;
        // Log.e("days ",pos+" "+cnt);
        if (cnt == 7) {
            every.setChecked(true);
            daysarr[0]=true;
        }
        else daysarr[0]=false;
        volume = scheduleinfolist.get(pos).getVol();
        if (volume == 1) {
            //Log.e("volume", "si");
            slnt.setColorFilter(Color.rgb(75, 251, 24));
            vbrt.setColorFilter(Color.rgb(238, 251, 248));

        } else {
            // Log.e("volume", "vibrate");

            vbrt.setColorFilter(Color.rgb(75, 251, 24));
            slnt.setColorFilter(Color.rgb(238, 251, 248));

        }

    }

    //new time er shatey agergular comp+update
    private void chkDifoldandNew(int pos) {
        changablest = scheduleinfolist.get(pos).getStartTime();
        if (!Name.equals(scheduleinfolist.get(pos).getName())) {
            scheduleinfolist.get(pos).setName(Name);
        }
        if (!Starttime.equals(scheduleinfolist.get(pos).getStartTime())) {
            int interval = 0;
            if (!Endtime.equals(scheduleinfolist.get(pos).getEndTime())) {
                scheduleinfolist.get(pos).setEndTime(Endtime);
            }
            interval = calTime();
            changablest = scheduleinfolist.get(pos).getStartTime();
            scheduleinfolist.get(pos).setStartTime(Starttime);
            scheduleinfolist.get(pos).setDelay(interval);

        } else if (Starttime.equals(scheduleinfolist.get(pos).getStartTime())) {
            if (!Endtime.equals(scheduleinfolist.get(pos).getEndTime())) {
                scheduleinfolist.get(pos).setEndTime(Endtime);
                int interval = calTime();
                scheduleinfolist.get(pos).setDelay(interval);
            }
        }
        if (volume != scheduleinfolist.get(pos).getVol()) {
            scheduleinfolist.get(pos).setVol(volume);
        }
        setSharedpref();

    }

    //this chk is for edited days and upadated those are no longer or newly added
    private void chkdays(int chkEdit) {
        Arrays.fill(arr, -1);
        boolean chksttime = false;
        // Log.e("Time",Starttime+" <->"+changablest);
        if (Starttime.equals(changablest)) chksttime = true;
        // Log.e("agertime", "" + chksttime + " " + Starttime + " " + changablest);
        if (sat.isChecked()) {
            if (scheduleinfolist.get(chkEdit).getSaturday() == -1) {//new select hoisay
                int id = checkid();
                scheduleinfolist.get(chkEdit).setSaturday(id);
                //showEvents.setTime(h, m, id, 7, chkEdit + 1);null pointer exception dekhay karon setTime showeents ta empty thakey.
                arr[id] = 17;//for setTimefunc
            } else if (chksttime == false) {//time differs from old
                //showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getSaturday());
                // showEvents.setTime(h, m, scheduleinfolist.get(chkEdit).getSaturday(), 7, chkEdit + 1);
                arr[scheduleinfolist.get(chkEdit).getSaturday()] = 37;//update then setTime
                // Log.e("sattt", "chked");
            }

        } else if (scheduleinfolist.get(chkEdit).getSaturday() != -1) {//agey silo but ekhon disselct hoisay
            // Log.e("saturday", chkEdit + " <-chk" + scheduleinfolist.get(chkEdit).getSaturday());
            //showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getSaturday());
            arr[scheduleinfolist.get(chkEdit).getSaturday()] = 27;
            updateids(scheduleinfolist.get(chkEdit).getSaturday());
            scheduleinfolist.get(chkEdit).setSaturday(-1);
        }

        if (sun.isChecked()) {
            if (scheduleinfolist.get(chkEdit).getSunday() == -1) {
                int id = checkid();
                scheduleinfolist.get(chkEdit).setSunday(id);
                arr[id] = 11;
                //showEvents.setTime(h, m, id, 1, chkEdit + 1);
            } else if (chksttime == false) {//time differs from old
                //showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getSunday());
                //showEvents.setTime(h, m, scheduleinfolist.get(chkEdit).getSunday(), 1, chkEdit + 1);
                arr[scheduleinfolist.get(chkEdit).getSunday()] = 31;
            }

        } else if (scheduleinfolist.get(chkEdit).getSunday() != -1) {
            //showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getSunday());
            arr[scheduleinfolist.get(chkEdit).getSunday()] = 21;
            updateids(scheduleinfolist.get(chkEdit).getSunday());
            scheduleinfolist.get(chkEdit).setSunday(-1);
        }
        if (mon.isChecked()) {
            if (scheduleinfolist.get(chkEdit).getMonday() == -1) {
                int id = checkid();
                scheduleinfolist.get(chkEdit).setMonday(id);
                // showEvents.setTime(h, m, id, 2, chkEdit + 1);
                arr[id] = 12;
            } else if (chksttime == false) {//time differs from old
                //showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getMonday());
                // showEvents.setTime(h, m, scheduleinfolist.get(chkEdit).getMonday(), 2, chkEdit + 1);
                arr[scheduleinfolist.get(chkEdit).getMonday()] = 32;
            }

        } else if (scheduleinfolist.get(chkEdit).getMonday() != -1) {
            // showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getMonday());
            updateids(scheduleinfolist.get(chkEdit).getMonday());
            arr[scheduleinfolist.get(chkEdit).getMonday()] = 22;
            scheduleinfolist.get(chkEdit).setMonday(-1);
        }
        if (tue.isChecked()) {
            if (scheduleinfolist.get(chkEdit).getTuesday() == -1) {
                int id = checkid();
                scheduleinfolist.get(chkEdit).setTuesday(id);
                //showEvents.setTime(h, m, id, 3, chkEdit + 1);
                arr[id] = 13;
            } else if (chksttime == false) {//time differs from old
                //showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getTuesday());
                //showEvents.setTime(h, m, scheduleinfolist.get(chkEdit).getTuesday(), 3, chkEdit + 1);
                arr[scheduleinfolist.get(chkEdit).getTuesday()] = 33;
            }

        } else if (scheduleinfolist.get(chkEdit).getTuesday() != -1) {
            //showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getTuesday());
            updateids(scheduleinfolist.get(chkEdit).getTuesday());
            arr[scheduleinfolist.get(chkEdit).getTuesday()] = 23;
            scheduleinfolist.get(chkEdit).setTuesday(-1);
        }
        if (wed.isChecked()) {
            if (scheduleinfolist.get(chkEdit).getWednesday() == -1) {
                int id = checkid();
                scheduleinfolist.get(chkEdit).setWednesday(id);
                //showEvents.setTime(h, m, id, 4, chkEdit + 1);
                arr[id] = 14;
            } else if (chksttime == false) {//time differs from old
                //  showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getWednesday());
                //showEvents.setTime(h, m, scheduleinfolist.get(chkEdit).getWednesday(), 4, chkEdit + 1);
                arr[scheduleinfolist.get(chkEdit).getWednesday()] = 34;
            }

        } else if (scheduleinfolist.get(chkEdit).getWednesday() != -1) {
            //showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getWednesday());
            updateids(scheduleinfolist.get(chkEdit).getWednesday());
            arr[scheduleinfolist.get(chkEdit).getWednesday()] = 24;
            scheduleinfolist.get(chkEdit).setWednesday(-1);
        }
        if (thu.isChecked()) {
            if (scheduleinfolist.get(chkEdit).getThursday() == -1) {
                int id = checkid();
                scheduleinfolist.get(chkEdit).setThursday(id);
                //showEvents.setTime(h, m, id, 5, chkEdit + 1);
                arr[id] = 15;
            } else if (chksttime == false) {//time differs from old
                // showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getThursday());
                //showEvents.setTime(h, m, scheduleinfolist.get(chkEdit).getThursday(), 5, chkEdit + 1);
                arr[scheduleinfolist.get(chkEdit).getThursday()] = 35;
            }

        } else if (scheduleinfolist.get(chkEdit).getThursday() != -1) {
            //showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getThursday());
            updateids(scheduleinfolist.get(chkEdit).getThursday());
            arr[scheduleinfolist.get(chkEdit).getThursday()] = 25;
            scheduleinfolist.get(chkEdit).setThursday(-1);

        }
        if (fri.isChecked()) {
            if (scheduleinfolist.get(chkEdit).getFriday() == -1) {
                int id = checkid();
                scheduleinfolist.get(chkEdit).setFriday(id);
                //showEvents.setTime(h, m, id, 6, chkEdit + 1);
                arr[id] = 16;
            } else if (chksttime == false) {//time differs from old
                //showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getFriday());
                //showEvents.setTime(h, m, scheduleinfolist.get(chkEdit).getFriday(), 6, chkEdit + 1);
                arr[scheduleinfolist.get(chkEdit).getFriday()] = 36;
            }

        } else if (scheduleinfolist.get(chkEdit).getFriday() != -1) {
            //  showEvents.updatePendingIntent(scheduleinfolist.get(chkEdit).getFriday());
            updateids(scheduleinfolist.get(chkEdit).getFriday());
            arr[scheduleinfolist.get(chkEdit).getFriday()] = 26;
            scheduleinfolist.get(chkEdit).setFriday(-1);

        }
        setSharedpref();

    }

    private void updateids(int id) {
        ids.set(id, -1);
        saveIds();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    public void start(View v) {
        show(1);
    }

    public void finish(View v) {
        show(2);
    }

    public void save(View v) {

        Name = eventname.getText().toString().trim();
        Starttime = startTime.getText().toString().trim();
        Endtime = endTime.getText().toString().trim();
        //Toast.makeText(this, Starttime + " " + chkEdit, Toast.LENGTH_SHORT).show();

        if (chkEdit != -1) {

            boolean bl = checkValidity();
            if (bl == true) {
                Bundle b = new Bundle();
                b.putIntArray("from", arr);
                //Log.e("hello", "" + chkEdit);
                chkDifoldandNew(chkEdit);  //old r new er maje ki change hoisay
                chkdays(chkEdit);
                Intent intent = new Intent(MainActivity.this, ShowEvents.class);
                intent.putExtra("afteredit", chkEdit);
                intent.putExtras(b);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                finish();
            } else
                Toast.makeText(this, "Please check all fields filled up correctly", Toast.LENGTH_SHORT).show();

        } else {
            //checkValidity()->shob fill up oisay ni?
            if (checkValidity()) {

                saveIds();
                if (sat.isChecked()) {
                    st = checkid();
                }
                if (sun.isChecked()) {
                    su = checkid();

                }
                if (mon.isChecked()) {
                    mo = checkid();

                }
                if (tue.isChecked()) {
                    tu = checkid();
                }
                if (wed.isChecked()) {
                    we = checkid();

                }
                if (thu.isChecked()) {
                    th = checkid();
                }
                if (fri.isChecked()) {
                    fr = checkid();
                }
                int timee = calTime();
                //Toast.makeText(this, "time " + timee, Toast.LENGTH_SHORT).show();

                prepareObject(timee);
                Intent intent = new Intent(MainActivity.this, ShowEvents.class);
                intent.putExtra("keyy", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                finish();
            }
        }

    }


    public void cancel(View view) {
        Intent intent = new Intent(MainActivity.this, ShowEvents.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        finish();

    }

    public void silent(View view) {
        slnt.setColorFilter(Color.rgb(75, 251, 24));
        vbrt.setColorFilter(Color.rgb(238, 251, 248));
        volume = 1;
        requestMutePermissions();

    }

    public void vibrate(View view) {
        vbrt.setColorFilter(Color.rgb(75, 251, 24));
        slnt.setColorFilter(Color.rgb(238, 251, 248));
        volume = 2;
    }


    public void prepareObject(int timee) {

        ScheduleInform scheduleInform = new ScheduleInform(st, su, mo, tu, we, th, fr, timee, Name, Starttime, Endtime, volume, true);
        scheduleinfolist.add(scheduleInform);
        setSharedpref();
        saveIds();
    }

    public void initialize() {
        eventname = (EditText) findViewById(R.id.name);
        startTime = (EditText) findViewById(R.id.startTime);
        endTime = (EditText) findViewById(R.id.endTime);
        every = (CheckBox) findViewById(R.id.Everyd);
        sat = (CheckBox) findViewById(R.id.satd);
        sun = (CheckBox) findViewById(R.id.sund);
        mon = (CheckBox) findViewById(R.id.mond);
        tue = (CheckBox) findViewById(R.id.tued);
        wed = (CheckBox) findViewById(R.id.wedd);
        thu = (CheckBox) findViewById(R.id.thud);
        fri = (CheckBox) findViewById(R.id.frid);
        eventshow = (TextView) findViewById(R.id.event);
        slnt = findViewById(R.id.silent);
        vbrt = findViewById(R.id.vibrate);
        layout11 = findViewById(R.id.layout11);
        layout12 = findViewById(R.id.layout12);

    }


    public void setAllDay(int a) {
        if (a == 1) {
            sat.setChecked(true);
            sun.setChecked(true);
            mon.setChecked(true);
            tue.setChecked(true);
            wed.setChecked(true);
            thu.setChecked(true);
            fri.setChecked(true);
            for (int i = 1; i <= 7; i++) {
                daysarr[i] = true;
            }
        } else {
            sat.setChecked(false);
            sun.setChecked(false);
            mon.setChecked(false);
            tue.setChecked(false);
            wed.setChecked(false);
            thu.setChecked(false);
            fri.setChecked(false);
            for (int i = 1; i <= 7; i++) {
                daysarr[i] = false;
            }
        }
    }

    public boolean checkValidity() {
        int check = 0;
        if (sat.isChecked()) {
            ++check;
        }
        if (sun.isChecked()) {
            ++check;
        }
        if (mon.isChecked()) {
            ++check;
        }
        if (tue.isChecked()) {
            ++check;
        }
        if (wed.isChecked()) {
            ++check;
        }
        if (thu.isChecked()) {
            ++check;
        }
        if (fri.isChecked()) {
            ++check;
        }

        int chk = 3;
        String s = "";
        boolean bl = true;

        if (Name.equals("")) {
            --chk;
        }
        if (Starttime.equals("")) --chk;
        if (Endtime.equals("")) --chk;
        if (chk < 3 || check < 1) {
            Toast.makeText(this, "Please check all fields filled up correctly", Toast.LENGTH_SHORT).show();
            bl = false;
        }
        return bl;

    }

    public int calTime() {
        int hour1 = hr1, hour2 = hr2;
        if (peram1.equals("PM")) {
            if (hour1 == 12) hour1 = (hour1 * 60) + mn1;
            else {
                hour1 += 12;
                hour1 = (hour1 * 60) + mn1;
            }
        }

        if (peram2.equals("PM")) {
            if (hour2 == 12) hour2 = (hour2 * 60) + mn2;
            else {
                hour2 += 12;
                hour2 = (hour2 * 60) + mn2;
            }
        }
        if (peram1.equals("AM")) {
            if (hour1 == 12) hour1 = (0 * 60) + mn1;
            else {
                hour1 = (hour1 * 60) + mn1;
            }
        }

        if (peram2.equals("AM")) {
            if (hour2 == 12) hour2 = (0 * 60) + mn2;
            else {
                hour2 = (hour2 * 60) + mn2;
            }
        }
        finaltime = 0;
        if (hour1 == hour2) {
            finaltime = 24 * 60 * 60 * 1000; //mili second;
        } else if (hour1 > hour2) {
            finaltime = ((1440 - hour1) + hour2) * 60 * 1000;
        } else finaltime = (hour2 - hour1) * 60 * 1000;

        return finaltime;

    }

    public void setSharedpref() {
        SharedPreferences sharedPreferences = getSharedPreferences("Infos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scheduleinfolist);
        editor.putString("lists", json);
        editor.commit();
        loadData();
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

    //just load id's
    public void loadIds() {

        SharedPreferences sharedPreferences2 = getSharedPreferences("EventIds", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        Gson gson2 = new Gson();

        String json = sharedPreferences2.getString("Arrays", null);
        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        ids = gson2.fromJson(json, type);
        if (ids == null) {
            ids = new ArrayList<>(160);
        }
//        for(int i=0;i<ids.size();i++){
//            Log.e("idis-> ",""+ids.get(i));
//        }
    }

    //if->id(update)then;
    public void saveIds() {
        SharedPreferences sharedPreferences2 = getSharedPreferences("EventIds", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        Gson gson2 = new Gson();
        String json = gson2.toJson(ids);
        editor2.putString("Arrays", json);
        editor2.commit();
        loadIds();
    }

    //set all value initially -1
    public void initid() {
        for (int i = 0; i < 160; i++) {
            ids.add(-1);
        }
        saveIds();
        SharedPreferences sharedPreferences2 = getSharedPreferences("EventIds", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor2.putString("initchk", "true");
        editor2.commit();

    }

    //chk and return the availabe id's
    public int checkid() {
        int u = 0;
        for (int i = 1; i < ids.size(); i++) {
            if (ids.get(i) == -1) {
                ids.set(i, 1);
                u = i;
                break;
            }
        }
        saveIds();
        // Toast.makeText(this, "Ides " + u, Toast.LENGTH_SHORT).show();
        return u;
    }

    //chk state that initid()has finished once
    public void chkinit() {
        SharedPreferences sharedPreferences2 = getSharedPreferences("EventIds", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        stg = sharedPreferences2.getString("initchk", null);
    }

    public void show(final int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.timepickerr, null);
        final TimePicker tm = view.findViewById(R.id.timee);

        AlertDialog.Builder alertDialogBuidler = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        alertDialogBuidler.setView(view);
        alertDialogBuidler.setCancelable(false)

                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ttlTime = "";
                        int hour = tm.getCurrentHour();
                        int minutes = tm.getCurrentMinute();
                        String perams = "AM";
                        if (hour == 0) {
                            hour = hour + 12;
                        } else if (hour >= 12) {
                            if (hour != 12) hour = hour - 12;
                            perams = "PM";
                        }

                        if (hour < 10) ttlTime += "0" + hour + ":";
                        else ttlTime += hour + ":";

                        if (minutes < 10) ttlTime += "0" + minutes;
                        else ttlTime += minutes;

                        ttlTime += " " + perams;

                        if (i == 1) {
                            hr1 = hour;
                            mn1 = minutes;
                            peram1 = perams;
                            startTime.setText(ttlTime);
                        } else {
                            hr2 = hour;
                            mn2 = minutes;
                            peram2 = perams;
                            endTime.setText(ttlTime);
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    private void requestMutePermissions() {
        try {
            if (Build.VERSION.SDK_INT < 23) {
                AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else if (Build.VERSION.SDK_INT >= 23) {
                this.requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp();
            }
        } catch (SecurityException e) {

        }
    }

    private void requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }
    }

    private boolean getThemes() {
        SharedPreferences sharedPreferences2 = getSharedPreferences("Themes", Context.MODE_PRIVATE);

        boolean bl = sharedPreferences2.getBoolean("theme", false);
        return bl;
    }

    private void setThemes(boolean a) {
        if (a == false) {
            setTheme(R.style.MyTheme2);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7E57C2")));
            layout11.setBackgroundColor(Color.parseColor("#673AB7"));
            layout12.setBackgroundColor(Color.parseColor("#5E35B1"));
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(every, ColorStateList.valueOf(Color.parseColor("#00BCD4")));
                CompoundButtonCompat.setButtonTintList(sat, ColorStateList.valueOf(Color.parseColor("#00BCD4")));
                CompoundButtonCompat.setButtonTintList(sun, ColorStateList.valueOf(Color.parseColor("#00BCD4")));
                CompoundButtonCompat.setButtonTintList(mon, ColorStateList.valueOf(Color.parseColor("#00BCD4")));
                CompoundButtonCompat.setButtonTintList(wed, ColorStateList.valueOf(Color.parseColor("#00BCD4")));
                CompoundButtonCompat.setButtonTintList(thu, ColorStateList.valueOf(Color.parseColor("#00BCD4")));
                CompoundButtonCompat.setButtonTintList(tue, ColorStateList.valueOf(Color.parseColor("#00BCD4")));
                CompoundButtonCompat.setButtonTintList(fri, ColorStateList.valueOf(Color.parseColor("#00BCD4")));

            } else {
                every.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00BCD4")));//setButtonTintList is accessible directly on API>19
                sat.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00BCD4")));//setButtonTintList is accessible directly on API>19
                sun.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00BCD4")));//setButtonTintList is accessible directly on API>19
                mon.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00BCD4")));//setButtonTintList is accessible directly on API>19
                tue.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00BCD4")));//setButtonTintList is accessible directly on API>19
                wed.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00BCD4")));//setButtonTintList is accessible directly on API>19
                thu.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00BCD4")));//setButtonTintList is accessible directly on API>19
                fri.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00BCD4")));//setButtonTintList is accessible directly on API>19
            }


        } else {
            setTheme(R.style.MyTheme3);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1b1931")));
            layout11.setBackgroundColor(Color.parseColor("#17152c"));
            layout12.setBackgroundColor(Color.parseColor("#1b1931"));
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(every, ColorStateList.valueOf(Color.parseColor("#6e528d")));
                CompoundButtonCompat.setButtonTintList(sat, ColorStateList.valueOf(Color.parseColor("#6e528d")));
                CompoundButtonCompat.setButtonTintList(sun, ColorStateList.valueOf(Color.parseColor("#6e528d")));
                CompoundButtonCompat.setButtonTintList(mon, ColorStateList.valueOf(Color.parseColor("#6e528d")));
                CompoundButtonCompat.setButtonTintList(wed, ColorStateList.valueOf(Color.parseColor("#6e528d")));
                CompoundButtonCompat.setButtonTintList(thu, ColorStateList.valueOf(Color.parseColor("#6e528d")));
                CompoundButtonCompat.setButtonTintList(tue, ColorStateList.valueOf(Color.parseColor("#6e528d")));
                CompoundButtonCompat.setButtonTintList(fri, ColorStateList.valueOf(Color.parseColor("#6e528d")));

            } else {
                every.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#6e528d")));//setButtonTintList is accessible directly on API>19
                sat.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#6e528d")));//setButtonTintList is accessible directly on API>19
                sun.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#6e528d")));//setButtonTintList is accessible directly on API>19
                mon.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#6e528d")));//setButtonTintList is accessible directly on API>19
                tue.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#6e528d")));//setButtonTintList is accessible directly on API>19
                wed.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#6e528d")));//setButtonTintList is accessible directly on API>19
                thu.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#6e528d")));//setButtonTintList is accessible directly on API>19
                fri.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#6e528d")));//setButtonTintList is accessible directly on API>19
            }
        }
    }

    public void Every(View v) {
        if (daysarr[0] == false) {
            daysarr[0] = true;
            setAllDay(1);
        } else {
            daysarr[0] = false;
            setAllDay(2);
        }
    }

    public void Sat(View v) {
        if (daysarr[1] == false) {
            daysarr[1] = true;

        } else {
            daysarr[1] = false;
        }
        calldays();//chk to select every days;
    }

    public void Sun(View v) {
        if (daysarr[2] == false) {
            daysarr[2] = true;

        } else {
            daysarr[2] = false;
        }
        calldays();//chk to select every days;

    }

    public void Mon(View v) {
        if (daysarr[3] == false) {
            daysarr[3] = true;

        } else {
            daysarr[3] = false;
        }
        calldays();//chk to select every days;

    }

    public void Tue(View v) {
        if (daysarr[4] == false) {
            daysarr[4] = true;

        } else {
            daysarr[4] = false;
        }
        calldays();//chk to select every days;
    }

    public void Wed(View v) {
        if (daysarr[5] == false) {
            daysarr[5] = true;

        } else {
            daysarr[5] = false;
        }
        calldays();//chk to select every days;
    }

    public void Thu(View v) {
        if (daysarr[6] == false) {
            daysarr[6] = true;

        } else {
            daysarr[6] = false;
        }
        calldays();//chk to select every days;
    }

    public void Fri(View v) {
        if (daysarr[7] == false) {
            daysarr[7] = true;

        } else {
            daysarr[7] = false;
        }
        calldays();//chk to select every days;
    }

    private void calldays() {
        int variable = 0;
        for (int i = 1; i <= 7; i++) {
            if (daysarr[i] == true) {
                Log.e("daysarr", i + "->" + daysarr[i]);
                ++variable;
            } else {
                Log.e("daysarrel", i + "->" + daysarr[i]);
            }
        }
        if (variable == 7) {
            every.setChecked(true);
            daysarr[0] = true;
        } else {
            every.setChecked(false);
            daysarr[0] = false;
        }
    }


}

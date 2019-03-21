package com.m.timepicker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.app.AlertDialog.Builder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ShowEvents extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    int finaltime = 0;
    Intent uniintent;
    Toolbar toolbar;
    String name;
    List<ScheduleInform> scheduleinfolist;
    List<PendingIntent> pendingIntents;
    HashMap<Integer, PendingIntent> pendinintent;
    ListView listView;
    CustomAdaptar customAdaptar;
    List<Integer> ids = new ArrayList<>(160);
    AlarmManager mgrAlarm;
    LinearLayout layout21;

    public ShowEvents() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getThemes()) setTheme(R.style.MyTheme3);
        else setTheme(R.style.MyTheme2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);
        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawyer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(ShowEvents.this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_closed);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(this);
        initialization();
        setThemes(getThemes());
        loadData();
        loadIds();
        pendinintent = new HashMap<>();
        mgrAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        listView = findViewById(R.id.listview);
        customAdaptar = new CustomAdaptar(ShowEvents.this, scheduleinfolist);
        listView.setAdapter(customAdaptar);
        customAdaptar.notifyDataSetChanged();
        int afteredit = getIntent().getIntExtra("afteredit", -1);
        int f = getIntent().getIntExtra("keyy", -1);

        if (afteredit != -1) {
            finaltime = scheduleinfolist.get(afteredit).getDelay();
            name = scheduleinfolist.get(afteredit).getName();
            showinfo();
            Bundle b = this.getIntent().getExtras();
            int[] array = b.getIntArray("from");
            //Log.e("afteredit ", array.length + " " + afteredit);
            regenerateEditvalue(afteredit, array);//edit theke agoto value gular update and deletion

        }
        if (scheduleinfolist.size() != 0 && f == 1) {//last value broadcast a add korar jonno r keyy dia new added value chek kora hoisa
            //Log.e("LOl", "jj" + f);
            finaltime = scheduleinfolist.get(scheduleinfolist.size() - 1).getDelay();
            name = scheduleinfolist.get(scheduleinfolist.size() - 1).getName();
            showinfo();
            setBroadcast();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showActionDialog(i);
            }
        });


    }

    private void initialization() {
        layout21 = findViewById(R.id.layout21);
    }

    private void regenerateEditvalue(int index, int arr[]) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] != -1) {
                Log.e("arr", "" + arr[i]);
                String s, p, q;
                s = p = q = scheduleinfolist.get(index).getStartTime();
                s = s.substring(0, 2);// time hr
                p = p.substring(3, 5);  //time mn
                q = q.substring(6, 8); //peram Am/Pm
                int h, m, per;
                h = Integer.parseInt(s);
                m = Integer.parseInt(p);
                if (q.equals("PM") && h != 12) h += 12;
                if (q.equals("AM") && h == 12) h = 0;   //last update kora
                int day = arr[i] % 10;
                int chk = arr[i] / 10;
                boolean bol = scheduleinfolist.get(index).isSwi();
                if (chk == 1 && bol == true) setTime(h, m, i, day, index + 1);
                else if (chk == 2) updatePendingIntent(i);
                else if (chk == 3) {
                    updatePendingIntent(i);
                    if (bol == true)
                        setTime(h, m, i, day, index + 1);
                }
            } else {
                //nothing;
            }
        }
    }


    private void showActionDialog(final int position) {
        loadData();

        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder alertDialogBuidler = new AlertDialog.Builder(this);

        alertDialogBuidler.setTitle("Choose Option")
                .setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent ed = new Intent(ShowEvents.this, MainActivity.class);
                            ed.putExtra("Edit", position);
                            startActivity(ed);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                            finish();

                        } else {
                            maintainingindex(position);
                            updateIdandPendingInt(position, -1);
                            scheduleinfolist.remove(scheduleinfolist.get(position));
                            setSharedpref(); //new scheduleinfolist save kora
                            ListView l = findViewById(R.id.listview);
                            customAdaptar = new CustomAdaptar(ShowEvents.this, scheduleinfolist);
                            l.setAdapter(customAdaptar);
                            customAdaptar.notifyDataSetChanged();

                        }
                    }
                })
                .show();

    }

    public void setBroadcast() {
        String s, q;
        s = scheduleinfolist.get(scheduleinfolist.size() - 1).getStartTime();
        q = s;
        q = q.substring(6, 8); //peram Am/Pm
        int t1 = Integer.parseInt(s.substring(0, 2));//hour
        int t2 = Integer.parseInt(s.substring(3, 5));//minute
        if (q.equals("PM") && t1 != 12) t1 += 12;
        if (q.equals("AM") && t1 == 12) t1 = 0;
        int ide = scheduleinfolist.size() - 1;
        getnsetIndividualIds(ide, t1, t2, -1);

    }

    public void maintainingindex(int pos) {
        for (int i = pos; i < scheduleinfolist.size() - 1; i++) {
            String s, p, q;
            s = scheduleinfolist.get(i + 1).getStartTime();
            Toast.makeText(this, "start t= " + s, Toast.LENGTH_SHORT).show();
            p = q = s;
            s = s.substring(0, 2);// time hr
            p = p.substring(3, 5);  //time mn
            q = q.substring(6, 8); //peram Am/Pm
            int t1 = Integer.parseInt(s);
            int t2 = Integer.parseInt(p);
            if (q.equals("PM") && t1 != 12) t1 += 12;
            if (q.equals("AM") && t1 == 12) t1 = 0;
            updateIdandPendingInt(i + 1, 0);//agergula delte kora oisay karon on receive method o values update oibo
            getnsetIndividualIds(i + 1, t1, t2, 0);//again set after delete
        }
    }

    //chk -1 na oilay hoy e func.. dia edit/delete or kaj kora oisay.
    //ide->obj er index
    private void getnsetIndividualIds(int ide, int t1, int t2, int chk) {

        int stid, suid, moid, tueid, wedid, thuid, friid;
        stid = scheduleinfolist.get(ide).getSaturday();
        suid = scheduleinfolist.get(ide).getSunday();
        moid = scheduleinfolist.get(ide).getMonday();
        tueid = scheduleinfolist.get(ide).getTuesday();
        wedid = scheduleinfolist.get(ide).getWednesday();
        thuid = scheduleinfolist.get(ide).getThursday();
        friid = scheduleinfolist.get(ide).getFriday();
        boolean bol = scheduleinfolist.get(ide).isSwi();
        if (stid != -1 && chk == -1 && bol == true) {
            setTime(t1, t2, stid, 7, chk);
        } else if (stid != -1 && bol == true) setTime(t1, t2, stid, 7, ide);

        if (suid != -1 && chk == -1 && bol == true) {
            setTime(t1, t2, suid, 1, chk);
        } else if (suid != -1 && bol == true) setTime(t1, t2, suid, 1, ide);

        if (moid != -1 && chk == -1 && bol == true) {
            setTime(t1, t2, moid, 2, chk);
        } else if (moid != -1 && bol == true) setTime(t1, t2, moid, 2, ide);
        if (tueid != -1 && chk == -1 && bol == true) {
            setTime(t1, t2, tueid, 3, chk);
        } else if (tueid != -1 && bol == true) setTime(t1, t2, tueid, 3, ide);

        if (wedid != -1 && chk == -1 && bol == true) {
            setTime(t1, t2, wedid, 4, chk);
        } else if (wedid != -1 && bol == true) setTime(t1, t2, wedid, 4, ide);

        if (thuid != -1 && chk == -1 && bol == true) {
            setTime(t1, t2, thuid, 5, chk);
        } else if (thuid != -1 && bol == true) setTime(t1, t2, thuid, 5, ide);

        if (friid != -1 && chk == -1 && bol == true) {
            setTime(t1, t2, friid, 6, chk);
        } else if (friid != -1 && bol == true) setTime(t1, t2, friid, 6, ide);

    }


    public void updateid(int pos) {
        ids.set(pos, -1);

    }

    public void updateIdandPendingInt(int pos, int chk) {

        int stid, suid, moid, tueid, wedid, thuid, friid;
        stid = scheduleinfolist.get(pos).getSaturday();
        suid = scheduleinfolist.get(pos).getSunday();
        moid = scheduleinfolist.get(pos).getMonday();
        tueid = scheduleinfolist.get(pos).getTuesday();
        wedid = scheduleinfolist.get(pos).getWednesday();
        thuid = scheduleinfolist.get(pos).getThursday();
        friid = scheduleinfolist.get(pos).getFriday();

        if (stid != -1) {
            updatePendingIntent(stid);//delete kora or ou id'r alarm r pendinintent
            if (chk == -1) updateid(stid); //free kora or j id delete oiges
        }
        if (suid != -1) {
            updatePendingIntent(suid);
            if (chk == -1) updateid(suid);
        }
        if (moid != -1) {
            updatePendingIntent(moid);
            if (chk == -1) updateid(moid);
        }
        if (tueid != -1) {
            updatePendingIntent(tueid);
            if (chk == -1) updateid(tueid);
        }
        if (wedid != -1) {
            updatePendingIntent(wedid);
            if (chk == -1) updateid(wedid);
        }
        if (thuid != -1) {
            updatePendingIntent(thuid);
            if (chk == -1) updateid(thuid);
        }
        if (friid != -1) {
            updatePendingIntent(friid);
            if (chk == -1) updateid(friid);
        }
        if (chk == -1) saveIds();//notun korey abar save&&-1 dia delete taki call oisay ni dekhar or
    }

    public void updatePendingIntent(int id) {
        Intent intent = new Intent(getApplicationContext(), SetBroadcast.class);
        //loadPendingIntent();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mgrAlarm.cancel(pendingIntent);
        pendingIntent.cancel();
        //savePendingIntent();

    }

    public void setTime(int hr, int min, int id, int day, int chk) {
        // Log.e("setTime ", "" + hr + ":" + min);
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

        Intent intent = new Intent(getApplicationContext(), SetBroadcast.class);
        if (chk == -1)
            intent.putExtra("values", scheduleinfolist.size() - 1);
        else {
            intent.putExtra("values", chk - 1);
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // mgrAlarm.setRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000, pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mgrAlarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
            // Log.e("Marshmello",hr+" marsh "+min);
        } else {
            mgrAlarm.setExact(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
            // Log.e(" not Marshmello",hr+" not marsh "+min );

        }
        // mgrAlarm.set(AlarmManager.RTC_WAKEUP,calender.getTimeInMillis(), pendingIntent);
    }

        //+ button
    public void add(View view) {
        Intent intent = new Intent(ShowEvents.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        finish();

    }

//oncreate option menu khatsi just return true aslo bhitre

    public void loadData() {

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


    public void setSharedpref() {
        SharedPreferences sharedPreferences = getSharedPreferences("Infos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(scheduleinfolist);
        editor.putString("lists", json);
        editor.commit();
        loadData();
    }


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
    }

    public void saveIds() {
        SharedPreferences sharedPreferences2 = getSharedPreferences("EventIds", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        Gson gson2 = new Gson();
        String json = gson2.toJson(ids);
        editor2.putString("Arrays", json);
        editor2.commit();
        loadIds();
    }

    private void showinfo() {
        final AlertDialog.Builder alertDialogBuidler = new AlertDialog.Builder(this, android.app.AlertDialog.THEME_HOLO_LIGHT);
        int hr = finaltime / 3600000;

        int mn = (finaltime - (hr * 3600000)) / 60000;
        alertDialogBuidler.setCancelable(false);
        alertDialogBuidler.setMessage("" + name + " is set for " + hr + " hour and " + mn + " minutes");
        alertDialogBuidler.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialogBuidler.show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.feedbackId:


                AlertDialog.Builder builder = new AlertDialog.Builder(ShowEvents.this);
                View view = getLayoutInflater().inflate(R.layout.feedback, null);
                final EditText name = view.findViewById(R.id.editnameId);
                final EditText message = view.findViewById(R.id.editmessageId);

                builder.setTitle("Feedback");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setCancelable(false);
                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (!name.getText().toString().isEmpty() && !message.getText().toString().isEmpty()) {

                            String text = name.getText().toString();
                            String feedback = message.getText().toString();
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("message/rfc822"); // remember to open email apps
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"scsahasunny@gmail.com"});
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback From Ds Note App");
                            intent.putExtra(Intent.EXTRA_TEXT, "Name: " + text + "\n Message: " + feedback);
                            startActivity(Intent.createChooser(intent, "Choose app to send Email"));
                        } else {
                            Toast.makeText(ShowEvents.this, "You should fillup all the fields", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
//permission if
                break;
        }
        switch (item.getItemId()) {
            case R.id.shareId:

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plane");
                String subject = "Schedule Maker";
                String body = "It will help your SmartPhone keep silent according to Your Schedule.\n Let's use Schedule Maker.\n com.example.hp.schedulemakerapp";
                i.putExtra(Intent.EXTRA_SUBJECT, subject);
                i.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(i, "Share With"));
                break;
        }
        switch (item.getItemId()) {
            case R.id.Lighttheme:
                setThemes(false);
                break;
        }
        switch (item.getItemId()) {
            case R.id.Darktheme:
                setThemes(true);
                break;
        }

        switch (item.getItemId()) {
            case R.id.Exit:
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
                finish();
                System.exit(0);
                break;
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setThemes(boolean a) {
        Window window;
        window = getWindow();
        if (a == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.setStatusBarColor(Color.parseColor("#7E57C2"));
            } else {
                window.setStatusBarColor(Color.parseColor("#7E57C2"));
            }
            setTheme(R.style.MyTheme2);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7E57C2")));
            layout21.setBackgroundColor(Color.parseColor("#673AB7"));
            saveTheme(a);
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.setStatusBarColor(Color.parseColor("#1b1931"));
            } else {
                window.setStatusBarColor(Color.parseColor("#1b1931"));
            }
            setTheme(R.style.MyTheme3);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1b1931")));
            layout21.setBackgroundColor(Color.parseColor("#1b1931"));
            saveTheme(a);
        }
    }

    private void saveTheme(boolean a) {
        SharedPreferences sharedPreferences2 = getSharedPreferences("Themes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor2.putBoolean("theme", a);
        editor2.commit();
    }

    private boolean getThemes() {
        SharedPreferences sharedPreferences2 = getSharedPreferences("Themes", Context.MODE_PRIVATE);

        boolean bl = sharedPreferences2.getBoolean("theme", false);
        return bl;
    }


}

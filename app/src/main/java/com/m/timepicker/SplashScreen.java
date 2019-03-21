package com.m.timepicker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    Button button;
    Thread splashTread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        /** Making notification bar transparent **/
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        /** making notification bar transparent **/
        changeStatusBarColor();

        StartAnimations();
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        Animation animleft = AnimationUtils.loadAnimation(this, R.anim.alpha);
        Animation animright = AnimationUtils.loadAnimation(this, R.anim.alpha);
        Animation animfromtop = AnimationUtils.loadAnimation(this, R.anim.alpha);

        anim.reset();
        animleft.reset();
        animright.reset();
        animfromtop.reset();

       /* LinearLayout l = (LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);
        */

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        animleft = AnimationUtils.loadAnimation(this, R.anim.leftside);
        animright = AnimationUtils.loadAnimation(this, R.anim.rightside);
        animfromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);

        anim.reset();
        animleft.reset();
        animright.reset();

        ImageView iv = (ImageView) findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);

        TextView textViewLeft = (TextView) findViewById(R.id.spTextLeft);
        TextView textViewRight = (TextView) findViewById(R.id.spTextRight);

        textViewLeft.clearAnimation();
        textViewLeft.startAnimation(animleft);
        textViewRight.clearAnimation();
        textViewRight.startAnimation(animright);

        button = findViewById(R.id.startButton);
        button.clearAnimation();
        button.startAnimation(animfromtop);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                splashTread = new Thread() {
                    @Override
                    public void run() {
                        try {

                            // Splash screen pause time

                            splashTread.sleep(100);



                            Intent intent = new Intent(SplashScreen.this,ShowEvents.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                            finish();

                        } catch (InterruptedException e) {
                            // do nothing
                        } finally {
                            SplashScreen.this.finish();
                        }

                    }
                };
                splashTread.start();
            }

            /**
             * Making Status bar transparent
             */



        });

    }
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}

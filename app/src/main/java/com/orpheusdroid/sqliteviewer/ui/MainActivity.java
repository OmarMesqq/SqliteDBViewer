package com.orpheusdroid.sqliteviewer.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.daimajia.androidanimations.library.Techniques;
import com.orpheusdroid.sqliteviewer.R;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class MainActivity extends AwesomeSplash {
    @Override
    public void initSplash(ConfigSplash configSplash) {
        //Customize Circular Reveal

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getBoolean(getString(R.string.preference_settings_splash_screen_key), true)) {
            startApp();
            return;
        }
        configSplash.setBackgroundColor(R.color.colorAccent); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(1000); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.splash_screen_logo); //or any other drawable
        configSplash.setAnimLogoSplashDuration(1000); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeIn);

        //Customize Title
        configSplash.setTitleSplash(getResources().getString(R.string.app_name));
        configSplash.setTitleTextColor(android.R.color.primary_text_dark);
        configSplash.setTitleTextSize(30f); //float value
        configSplash.setAnimTitleDuration(700);
        configSplash.setAnimTitleTechnique(Techniques.FadeIn);
        configSplash.setTitleFont("font/Aspire-DemiBold.ttf");
    }

    @Override
    public void animationsFinished() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startApp();
            }
        }, 500);
    }

    private void startApp() {
        startActivity(new Intent(this, FileManagerActivity.class));
        finish();
    }
}

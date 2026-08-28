/*
 * Copyright (c) 2016-2018. Vijai Chandra Prasad R.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses
 */

package com.orpheusdroid.sqliteviewer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import org.solovyev.android.checkout.Billing;


/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class SQLiteViewerApp extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private static SQLiteViewerApp sInstance;

    private final Billing mBilling = new Billing(this, new Billing.DefaultConfiguration() {
        @Override
        public String getPublicKey() {
            return BuildConfig.APP_PUB_KEY;
        }
    });

    public SQLiteViewerApp() {
        sInstance = this;
    }

    public static SQLiteViewerApp get() {
        return sInstance;
    }

    public Billing getBilling() {
        return mBilling;
    }
}

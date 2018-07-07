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

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.topjohnwu.superuser.BusyBox;
import com.topjohnwu.superuser.Shell;

import org.solovyev.android.checkout.Billing;

import static com.orpheusdroid.sqliteviewer.Const.TAG;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class ScreenCamApp extends Shell.ContainerApp {

    @Override
    public void onCreate() {
        super.onCreate();
        Shell.setFlags(Shell.FLAG_REDIRECT_STDERR);
        //Shell.verboseLogging(BuildConfig.DEBUG);
        Shell.setInitializer(ExampleInitializer.class);
    }

    private static ScreenCamApp sInstance;

    private final Billing mBilling = new Billing(this, new Billing.DefaultConfiguration() {
        @Override
        public String getPublicKey() {
            return BuildConfig.APP_PUB_KEY;
        }
    });

    public ScreenCamApp() {
        sInstance = this;
    }

    public static ScreenCamApp get() {
        return sInstance;
    }

    public Billing getBilling() {
        return mBilling;
    }

    private static class ExampleInitializer extends Shell.Initializer {
        @Override
        public boolean onShellInit(Context context, Shell shell) {
            Log.d(TAG, "onShellInit");
            // Use internal busybox
            BusyBox.setup(context);
            return true;
        }

        @Override
        public boolean onRootShellInit(Context context, Shell shell) {
            Log.d(TAG, "onRootShellInit");
            return true;
        }
    }
}

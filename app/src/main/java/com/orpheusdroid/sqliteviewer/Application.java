package com.orpheusdroid.sqliteviewer;

import android.content.Context;
import android.util.Log;

import com.topjohnwu.superuser.BusyBox;
import com.topjohnwu.superuser.Shell;

import static com.orpheusdroid.sqliteviewer.Const.TAG;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class Application extends Shell.ContainerApp {

    @Override
    public void onCreate() {
        super.onCreate();
        Shell.setFlags(Shell.FLAG_REDIRECT_STDERR);
        //Shell.verboseLogging(BuildConfig.DEBUG);
        Shell.setInitializer(ExampleInitializer.class);
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

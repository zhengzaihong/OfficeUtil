package com.hefu.office;

import android.app.Application;
import android.content.Context;

import com.zzh.office.OfficeHelper;

import static com.dz.utlis.JavaUtils.isdebug;

public class App extends Application {
    private static App application;
    @Override
    public void onCreate() {
        super.onCreate();
        this.application = this;
        isdebug = true;

        OfficeHelper.init(this);

    }

    public static App getinstance() {
        return application;
    }

    public static Context getContext() {
        return getinstance().getApplicationContext();
    }


}

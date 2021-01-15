package com.hefu.office;

import com.dz.utlis.IOUtils;
import com.dz.utlis.SdCardUtil;

import java.io.File;

public class AppConfig {

    public final String PATH_APP_ROOT;
    public final String PATH_APP_DOWNLOAD;


    private static AppConfig sInstance;

    public static AppConfig get() {
        if (sInstance == null) {
            synchronized (AppConfig.class) {
                if (sInstance == null) sInstance = new AppConfig();
            }
        }
        return sInstance;
    }

    private AppConfig() {

        this.PATH_APP_ROOT = SdCardUtil.getAppRootPath(App.getinstance()).getAbsolutePath() + File.separator + "ssssssss";
        this.PATH_APP_DOWNLOAD = PATH_APP_ROOT + File.separator + "Download";
    }


    public void initFileDir() {

        IOUtils.createFolder(PATH_APP_ROOT);
        IOUtils.createFolder(PATH_APP_DOWNLOAD);
    }
}

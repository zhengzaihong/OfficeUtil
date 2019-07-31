package com.zzh.office;

import android.content.Context;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import static com.zzh.office.Log.outRedPrint;


/**
 * creat_user: zhengzaihong
 * email:1096877329@qq.com
 * creat_date: 2019/4/18 0018
 * creat_time: 13:38
 * describe: 初始化tbs
 **/

public class OfficeConfig {

    public static void initWebX5(Context context) {

        QbSdk.setDownloadWithoutWifi(true);
        //x5内核初始化接口
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                outRedPrint(" QbSdk onViewInitFinished is " + arg0);
            }
            @Override
            public void onCoreInitFinished() {
                outRedPrint(" QbSdk onCoreInitFinished is ");
            }
        });

        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                //tbs内核下载完成回调
                outRedPrint(" onDownloadFinish ");
            }

            @Override
            public void onInstallFinish(int i) {
                //内核安装完成回调，
                outRedPrint(" onInstallFinish ");
            }
            @Override
            public void onDownloadProgress(int i) {
                //下载进度监听
                outRedPrint(" onDownloadProgress ");
            }
        });

    }

}

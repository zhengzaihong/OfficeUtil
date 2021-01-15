package com.zzh.office;

public class Log {

    public static void outRedPrint(String s) {
        if (OfficeHelper.isDebug())
            android.util.Log.e("输出信息：", s);
    }
}

package com.zzh.office.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

import static com.zzh.office.Log.outRedPrint;


/**
 * creat_user: zhengzaihong
 * email:1096877329@qq.com
 * creat_date: 2019/4/18 0018
 * creat_time: 13:39
 * describe: 预览文档的view.TbsReaderView 支持 doc，pdf,xlsx,txt,pptx
 **/

public class OfficeFileView extends FrameLayout implements TbsReaderView.ReaderCallback {

    private TbsReaderView mTbsReaderView;
    private Context context;

    public OfficeFileView(Context context) {
        this(context, null, 0);
    }

    public OfficeFileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OfficeFileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTbsReaderView = new TbsReaderView(context, this);
        this.addView(mTbsReaderView, new LinearLayout.LayoutParams(-1, -1));
        this.context = context;
    }


    private OnGetFilePathListener mOnGetFilePathListener;


    public void setOnGetFilePathListener(OnGetFilePathListener mOnGetFilePathListener) {
        this.mOnGetFilePathListener = mOnGetFilePathListener;
    }


    private TbsReaderView getTbsReaderView(Context context) {
        return new TbsReaderView(context, this);
    }

    /**
     * 不要手动触发该方法
     *
     * @param mFile
     */
    protected void displayFile(File mFile) {

        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {

            String bsReaderTemp = Environment.getExternalStorageDirectory() + "/" + context.getApplicationInfo().packageName + "/TbsReaderTemp";
            File bsReaderTempFile = new File(bsReaderTemp);

            if (!bsReaderTempFile.exists()) {
                outRedPrint("准备创建：" + bsReaderTemp);
                boolean mkdir = bsReaderTempFile.mkdir();
                if (!mkdir) {
                    outRedPrint("创建失败：" + bsReaderTemp);
                }
            }

            //加载文件
            Bundle localBundle = new Bundle();
            outRedPrint(mFile.toString());
            localBundle.putString("filePath", mFile.toString());
            localBundle.putString("tempPath",bsReaderTemp);

            if (this.mTbsReaderView == null)
                this.mTbsReaderView = getTbsReaderView(context);
            boolean bool = this.mTbsReaderView.preOpen(getFileType(mFile.toString()), false);
            outRedPrint("是否支持预览：" + bool);
            if (bool) {
                this.mTbsReaderView.openFile(localBundle);
            }
        } else {
            outRedPrint("文件路径无效！");
        }

    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            outRedPrint("paramString---->null");
            return str;
        }
        outRedPrint("paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            outRedPrint("i <= -1");
            return str;
        }


        str = paramString.substring(i + 1);
        outRedPrint("paramString.substring(i + 1)------>" + str);
        return str;
    }

    protected void show() {
        if (mOnGetFilePathListener != null) {
            mOnGetFilePathListener.onGetFilePath(this);
        }
    }

    protected interface OnGetFilePathListener {
        void onGetFilePath(OfficeFileView mSuperFileView);
    }


    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {
        outRedPrint("****************************************************" + integer);
    }

    protected void onStopDisplay() {
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }
}

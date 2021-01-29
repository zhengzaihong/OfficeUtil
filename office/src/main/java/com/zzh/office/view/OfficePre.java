package com.zzh.office.view;

import android.os.Handler;
import android.text.TextUtils;

import com.zzh.office.ParameterBuilder;
import com.zzh.office.utils.HttpUrlConnectionAsyncTask;
import com.zzh.office.utils.LoadStatus;

import java.io.File;
import java.net.HttpURLConnection;

import static com.zzh.office.Log.outRedPrint;


/**
 * create_user: zhengzaihong
 * email:1096877329@qq.com
 * create_date: 2019/4/18 0018
 * create_time: 13:58
 * describe: 本地或在线文档加载工具
 **/

public class OfficePre {


    public static String[] prefixs = new String[]{"http", "mms", "ftp", "socks"};

    private ParameterBuilder builder;
    //TBS view
    private OfficeFileView mOfficeFileView;


    private static OfficePre officePre;

    //下载任务
    private HttpUrlConnectionAsyncTask asyncTask;

    //是否开启下载询问
    private boolean isEnableAskUpdate;


    private OfficePre() {
    }


    public static OfficePre get() {
        if (null == officePre) {
            synchronized (OfficePre.class) {
                if (null == officePre) {
                    officePre = new OfficePre();
                }
            }
        }
        return officePre;
    }


    public OfficePre init(OfficeFileView mOfficeFileView) {
        this.mOfficeFileView = mOfficeFileView;
        return this;
    }


    public void show(ParameterBuilder builder) {

        if (null == builder) {
            outRedPrint("请先配置参数 ParameterBuilder ");
            return;
        }
        if (null == mOfficeFileView) {
            new RuntimeException("请先初始化,调用init()");
        }
        this.builder = builder;

        //网络地址要先下载
        boolean isNetFile = false;
        for (String prefix : prefixs) {
          if( builder.getFileUrl().startsWith(prefix)){
              isNetFile = true;
              break;
          }
        }
        if (isNetFile) {
            File file = getCacheFile();
            if (file.isFile()) {
                outRedPrint("onGetFilePath缓存文件：" + file.getAbsolutePath());
                if (null != checkLocalFileListener && isEnableAskUpdate) {
                    //这里添加了监听 则认为是想提示用户 当有本地缓存则需要重新下载 否则直接加载缓存文档
                    //提示了 则需要外部手动触发overrideDownload() 方法
                    checkLocalFileListener.localHasFile();
                } else {
                    //如果本地已经存在，则直接解析文档。
                    displayFile(file);
                }
            } else {
                downLoadFromNet();
            }
        } else {
            displayFile(new File(builder.getFileUrl()));
        }
    }

    /**
     * 停止加载，退出界面时调用
     */
    public void destroy() {
        if (mOfficeFileView != null) {
            mOfficeFileView.onStopDisplay();
        }
        if (null != asyncTask) {
            asyncTask.cancel(true);
        }
    }

    private void downLoadFromNet() {
        //1.网络下载、存储路径、
        File cacheFile = getCacheFile();
        if (cacheFile.exists()) {
            if (cacheFile.length() <= 0) {
                cacheFile.delete();
                return;
            }
        }
        asyncTask = new HttpUrlConnectionAsyncTask();
        asyncTask.downloadFile(builder);
        asyncTask.setOnHttpProgressUtilListener(new HttpUrlConnectionAsyncTask.OnHttpProgressUtilListener() {

            @Override
            public void onProgress(final Integer progress) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != downloadListener) {
                            downloadListener.progress(progress);
                        }
                    }
                });
            }

            @Override
            public void onStatus(final LoadStatus status, final Object obj) {

                switch (status) {
                    case ON_START: {
                        File cacheDir = getCacheDir();
                        if (!cacheDir.exists()) {
                            cacheDir.mkdirs();
                        }
                        break;
                    }
                    case ON_SUCCESS: {
                        displayFile(new File(obj.toString()));
                        break;
                    }
                    case ON_ERROR: {
                        File file = getCacheFile();
                        if (file.exists()) {
                            file.delete();
                        }
                        break;
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != downloadListener) {
                            downloadListener.onStatus(status, obj);
                        }
                    }
                });
            }

            @Override
            public void onHandleHttpURLConnection(HttpURLConnection httpURLConnection) {
                if (null != downloadListener) {
                    downloadListener.onHandConnection(httpURLConnection);
                }
            }
        });
    }


    /**
     * 直接读取旧文档
     */
    public void readOldFile() {
        File file = getCacheFile();
        if (null != file) {
            if (null == mOfficeFileView) {
                new RuntimeException("请先初始化init方法");
            }
            displayFile(file);
        }
    }


    /**
     * @param isEnableAskUpdate true 会提示用户是否需要重新下载
     * @return 当前对象
     */
    public OfficePre setEnableAskUpdate(boolean isEnableAskUpdate) {
        this.isEnableAskUpdate = isEnableAskUpdate;
        return this;
    }


    /**
     * @param file 加载本地文件
     */
    public void loadLocalFile(File file) {
        if (null == mOfficeFileView) {
            new RuntimeException("请先初始化init方法");
        }
        displayFile(file);
    }

    private void displayFile(File file) {
        mOfficeFileView.displayFile(onReadStatusListener, file);
    }

    /***
     * 获取缓存目录
     * @return 返回缓存文件
     */
    public File getCacheDir() {
        return new File(builder.getSavePath());

    }

    /***
     * @return 绝对路径获取缓存文件
     */
    public File getCacheFile() {
        File cacheFile = new File(builder.getSavePath() + "/"
                + builder.getFileName()
        );
        return cacheFile;
    }

    /**
     * 检查是否本地已经有同名文件，提示用户是否下载更新覆盖之前的
     */
    private CheckLocalFileListener checkLocalFileListener;

    public OfficePre setOnCheckLocalFileListener(CheckLocalFileListener checkLocalFileListener) {
        this.checkLocalFileListener = checkLocalFileListener;
        return this;
    }

    public interface CheckLocalFileListener {
        /**
         * 回调时可提示用户 是否需要重新下载，这里就不做提示了，交由外部定制界面
         */
        void localHasFile();

    }


    /**
     * 监听读取文件是否成功
     */
    private OnReadStatusListener onReadStatusListener;

    public OfficePre addOnReadStatusListener(OnReadStatusListener onReadStatusListener) {
        this.onReadStatusListener = onReadStatusListener;
        return this;
    }

    public interface OnReadStatusListener {

        void readStatus(boolean isSuccess);

    }

    /**
     * 设置下载监听的回调
     */

    private OnDownloadListener downloadListener;

    public OfficePre setOnDownloadListener(OnDownloadListener listener) {
        this.downloadListener = listener;
        return this;

    }

    public interface OnDownloadListener {

        void progress(Integer progress);

        void onStatus(LoadStatus status, Object obj);

        void onHandConnection(HttpURLConnection httpURLConnection);
    }


    /**
     * 下载覆盖
     */
    public void overrideDownload() {
        //先删除旧文件
        File loaclFile = getCacheFile();
        if (null != loaclFile && loaclFile.isFile()) {
            loaclFile.delete();
        }
        downLoadFromNet();
    }


    private Handler handler = new Handler();

}

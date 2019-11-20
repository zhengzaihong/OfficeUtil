package com.zzh.office.view;

import android.os.Handler;
import android.text.TextUtils;

import com.zzh.office.utils.HttpUrlConnectionAsyncTask;

import java.io.File;

import static com.zzh.office.Log.outRedPrint;


/**
 * creat_user: zhengzaihong
 * email:1096877329@qq.com
 * creat_date: 2019/4/18 0018
 * creat_time: 13:58
 * describe: 本地或在线文档加载工具
 **/

public class OfficePre {

    //TBS view
    private OfficeFileView mOfficeFileView;

    //本地或网络地址
    private String fileUrl;
    //存储的地址
    private String savePath;
    //存储的文件名
    private String fileName;

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

        mOfficeFileView.setOnGetFilePathListener(new OfficeFileView.OnGetFilePathListener() {
            @Override
            public void onGetFilePath(OfficeFileView fileView) {
                //网络地址要先下载
                if (fileUrl.startsWith("http")) {
                    File file = getCacheFile();
                    if (file.isFile()) {

                        outRedPrint("onGetFilePath缓存文件：" + file.getAbsolutePath());
                        //TODO 这里还可以加入询问用户是否重新下载

                        if (null != checkLocalFileListener && isEnableAskUpdate) {
                            //这里添加了监听 则认为是想提示用户 当有本地缓存则需要重新下载 否则直接加载缓存文档
                            //提示了 则需要外部手动触发overrideDownload() 方法
                            checkLocalFileListener.localHasFile();
                        } else {
                            //如果本地已经存在，则直接解析文档。
                            fileView.displayFile(file);
                        }

                    } else {
                        downLoadFromNet();
                    }

                } else {
                    fileView.displayFile(new File(fileUrl));
                }
            }
        });
        return this;
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
        asyncTask.downloadFile(fileUrl, savePath + "/" + fileName);
        asyncTask.setOnHttpProgressUtilListener(new HttpUrlConnectionAsyncTask.OnHttpProgressUtilListener() {
            @Override
            public void onStart() {
                File file1 = getCacheDir();
                if (!file1.exists()) {
                    file1.mkdirs();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != downloadListener) {
                            downloadListener.onStart();
                        }
                    }
                });
            }

            @Override
            public void onError() {
                File file = getCacheFile();
                if (file.exists()) {
                    file.delete();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != downloadListener) {
                            downloadListener.onException();
                        }
                    }
                });
            }

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
            public void canclelled(String msg) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != downloadListener) {
                            downloadListener.onCancel();
                        }
                    }
                });
            }

            @Override
            public void onSuccess(final String url) {

                mOfficeFileView.displayFile(new File(url));

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != downloadListener) {
                            downloadListener.onFinish(url);
                        }
                    }
                });
            }
        });
    }


    /**
     * @param url      本地或者网络文档的url 需要带有后缀的文档 如http://xxxxxxxx/xx/kotlin.pdf
     * @param savePath 存储到手机的地址 如：/storage/emulated/0/pdf/
     *                 如：kotlin.pdf  最后完整路径：savePath/kotlin.pdf
     * @return 对象
     */

    public OfficePre showPre(String url, String savePath) {

        checkInit(url);
        this.fileUrl = url;
        this.savePath = savePath;
        this.fileName = url.substring(url.lastIndexOf("/") + 1);
        mOfficeFileView.show();
        return this;
    }


    /**
     * @param url      本地或者网络文档的url 没有后缀的文档 如http://documnet/file/1
     * @param fileName kotlin.pdf
     * @param savePath 最后完整路径：savePath/kotlin.pdf
     * @return 当前对象
     */
    public OfficePre showPre(String url, String fileName, String savePath) {

        checkInit(url);
        this.fileUrl = url;
        this.savePath = savePath;
        this.fileName = fileName;
        mOfficeFileView.show();

        return this;
    }

    private void checkInit(String url) {

        if (null == mOfficeFileView) {
            new RuntimeException("请先初始化,调用init()");
        }
        if (TextUtils.isEmpty(url)) {
            new RuntimeException("请检查参数，url地址不正确");
        }
    }

    /**
     * 直接读取旧文档
     */
    public void readOldFile() {

        File file = getCacheFile();
        if (null != file) {
            if (null == mOfficeFileView) {
                new RuntimeException("请先初始化init方法");
                return;
            }
            mOfficeFileView.displayFile(file);
        }
    }


    /**
     *
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
    public void preLocalFile(File file) {

        if (null == mOfficeFileView) {
            new RuntimeException("请先初始化init方法");
            return;
        }
        mOfficeFileView.displayFile(file);
    }

    /***
     * 获取缓存目录
     * @return 返回缓存文件
     */
    public File getCacheDir() {
        return new File(savePath);

    }

    /***
     * @return 绝对路径获取缓存文件
     */
    public File getCacheFile() {
        File cacheFile = new File(savePath + "/"
                + fileName
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
     * 设置下载监听的回调
     */

    private OnDownloadListener downloadListener;

    public OfficePre setOnDownloadListener(OnDownloadListener listener) {
        this.downloadListener = listener;
        return this;

    }

    public interface OnDownloadListener {

        void onStart();

        void progress(Integer progress);

        void onFinish(String url);

        void onException();

        void onCancel();
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

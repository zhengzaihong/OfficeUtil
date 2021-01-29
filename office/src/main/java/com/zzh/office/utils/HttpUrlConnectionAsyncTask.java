package com.zzh.office.utils;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.zzh.office.Log;
import com.zzh.office.ParameterBuilder;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * create_user: zhengzaihong
 * Email:1096877329@qq.com
 * create_date: 2019/4/18 0018
 * create_time: 4:15
 * describe 文件下载
 **/
public class HttpUrlConnectionAsyncTask extends AsyncTask<Integer, Integer, String> {

    private OnHttpProgressUtilListener onHttpProgressUtilListener;

    private ParameterBuilder pBuilder;

    public void downloadFile(ParameterBuilder builder) {
        this.pBuilder = builder;
        execute();
    }

    public void setOnHttpProgressUtilListener(OnHttpProgressUtilListener onHttpProgressUtilListener) {
        this.onHttpProgressUtilListener = onHttpProgressUtilListener;
    }

    @Override
    protected String doInBackground(Integer... integers) {
        return download();
    }

    private String download() {

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            callBack(LoadStatus.ON_START, "start download....");
            //获得URL对象
            URL url = new URL(pBuilder.getFileUrl());
            //返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
            connection = (HttpURLConnection) url.openConnection();
            if (onHttpProgressUtilListener != null) {
                //能回调回去设置请求头等信息
                onHttpProgressUtilListener.onHandleHttpURLConnection(connection);
            }
            //建立实际链接
            connection.connect();
            inputStream = connection.getInputStream();
            //获取文件长度
            Double size = (double) connection.getContentLength();
            Map<String, List<String>> headMap = connection.getHeaderFields();
            if (pBuilder.isAutoJoinFileName()) {
                List<String> values = headMap.get(pBuilder.getJoinHeadParamKey());
                for (String value : values) {
                    String[] vas = value.split(";");
                    for (String va : vas) {
                        String pattenKey = pBuilder.getJoinHeadFileKey() + pBuilder.getJoinSymbol();
                        if ((!TextUtils.isEmpty(va)) && va.startsWith(pattenKey)) {
                            pBuilder.setFileName(va.substring(pattenKey.length()));
                        }
                    }
                }
            }
            pBuilder.setSavePath(pBuilder.getSavePath() + "/" + pBuilder.getFileName());
            outputStream = new FileOutputStream(pBuilder.getSavePath());


            int count;
            Long progress = 0L;
            byte[] bytes = new byte[2048];
            while ((count = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, count);
                progress = progress + count;
                //换算进度
                double d = (new BigDecimal(progress / size).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                double d1 = d * 100;
                //传入的值为1-100
                onProgressUpdate((int) d1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            callBack(LoadStatus.ON_ERROR, "download fail ....");

        } finally {
            //关闭
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
            callBack(LoadStatus.ON_FILNISH, pBuilder.getSavePath());
        }
        return pBuilder.getSavePath();
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (null != onHttpProgressUtilListener) {
            onHttpProgressUtilListener.onProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        callBack(LoadStatus.ON_SUCCESS, s);
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
        callBack(LoadStatus.ON_CANCLE, s);
    }


    private void callBack(LoadStatus status, Object obj) {
        if (null != onHttpProgressUtilListener) {
            onHttpProgressUtilListener.onStatus(status, obj);
        }
    }


    public interface OnHttpProgressUtilListener {

        /**
         * 当前下载进度
         *
         * @param length
         */
        void onProgress(Integer length);

        /**
         * @param status 状态
         * @param obj    信息
         */
        void onStatus(LoadStatus status, Object obj);


        /**
         * 一些地方需要特殊处理的 拿到  HttpURLConnection获取
         */
        void onHandleHttpURLConnection(HttpURLConnection httpURLConnection);

    }
}

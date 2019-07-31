package com.zzh.officeLib.utils;

import android.os.AsyncTask;
import com.dz.utlis.JavaUtils;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * creat_user: zhengzaihong
 * Email:1096877329@qq.com
 * creat_date: 2019/4/18 0018
 * creat_time: 4:15
 * describe
 **/
public class HttpUrlConnectionAsyncTask extends AsyncTask<Integer, Integer, String> {

    private OnHttpProgressUtilListener onHttpProgressUtilListener;
    private String urlPath;
    private String filePath;

    public void downloadFile(String urlPath, String filePath) {
        this.urlPath = urlPath;
        this.filePath = filePath;
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
            if (null != onHttpProgressUtilListener) {
                onHttpProgressUtilListener.onStart();
            }
            //获得URL对象
            URL url = new URL(urlPath);
            //返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
            connection = (HttpURLConnection) url.openConnection();
            //建立实际链接
            connection.connect();
            inputStream = connection.getInputStream();
            //获取文件长度
            Double size = (double) connection.getContentLength();

            outputStream = new FileOutputStream(filePath);
            int count;
            Long progress = 0L;
            byte[] bytes = new byte[2048];
            while ((count = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, count);
                progress = progress+count;
                //换算进度
                double d = (new BigDecimal(progress / size).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                double d1 = d * 100;
                //传入的值为1-100
                onProgressUpdate((int) d1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (null != onHttpProgressUtilListener) {
                onHttpProgressUtilListener.onError();
            }

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
        }
        return filePath;
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
        if (null != onHttpProgressUtilListener) {
            onHttpProgressUtilListener.onSuccess(s);
        }
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
        if (null != onHttpProgressUtilListener) {
            onHttpProgressUtilListener.canclelled(s);
        }

    }


    public interface OnHttpProgressUtilListener {

        void onStart();

        void onError();

        void onProgress(Integer length);

        void canclelled(String msg);

        void onSuccess(String json);
    }
}

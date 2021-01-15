package com.zzh.office;

import android.text.TextUtils;

/**
 * create_user: zhengzaihong
 * email:1096877329@qq.com
 * create_date: 2019/4/18 0018
 * create_time: 13:39
 * describe: 文档加载的基本参数
 **/

public class ParameterBuilder {

    //本地或网络地址
    private String fileUrl;

    //存储的地址
    private String savePath;

    //存储的文件名
    private String fileName;


    // 是否开启 自动拼接文件后缀
    // 此参数慎用  在网络文件中 无后缀文件地址或者 不能判断文档类型时 使用

    private boolean autoJoinFileName;

    // 这几个个参数 autoJoinFileName 为true 时生效，尝试从报文头中截取 信息的字段
    private String joinHeadParamKey;
    private String joinHeadFileKey;
    private String joinSymbol;



    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isAutoJoinFileName() {
        return autoJoinFileName;
    }

    public void setAutoJoinFileName(boolean autoJoinFileName) {
        this.autoJoinFileName = autoJoinFileName;
    }

    public String getJoinHeadParamKey() {
        return joinHeadParamKey;
    }

    public void setJoinHeadParamKey(String joinHeadParamKey) {
        this.joinHeadParamKey = joinHeadParamKey;
    }

    public String getJoinHeadFileKey() {
        return joinHeadFileKey;
    }

    public void setJoinHeadFileKey(String joinHeadFileKey) {
        this.joinHeadFileKey = joinHeadFileKey;
    }

    public String getJoinSymbol() {
        return joinSymbol;
    }

    public void setJoinSymbol(String joinSymbol) {
        this.joinSymbol = joinSymbol;
    }

    /**
     * @param fileUrl  本地或者网络文档的url 没有后缀的文档 如http://documnet/file/1 则需要配置 fileName
     *                 如http://xxxxxxxx/xx/kotlin.pdf 则不用设置 fileName 参数
     * @param fileName kotlin.pdf 如未配置，则 fileName默认截取路径后 / 名称
     * @param savePath 最后完整路径：savePath/kotlin.pdf
     * @return 当前对象
     */

    public ParameterBuilder build() {
        if (TextUtils.isEmpty(fileUrl)) {
            new RuntimeException("请检查参数，url地址不正确");
        }
        if (TextUtils.isEmpty(fileName)) {
            this.fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        }

        return this;
    }

}

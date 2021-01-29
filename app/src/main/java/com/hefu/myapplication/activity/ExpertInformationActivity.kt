package com.hefu.myapplication.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dz.utlis.JavaUtils
import com.dz.utlis.JavaUtils.outPrint
import com.dz.utlis.JavaUtils.outRedPrint
import com.dz.utlis.UiCompat
import com.hefu.myapplication.R
import com.hefu.office.AppConfig
import com.hefu.myapplication.bean.BooksBean
import com.zzh.office.Log
import com.zzh.office.ParameterBuilder
import com.zzh.office.utils.LoadStatus
import com.zzh.office.view.OfficePre
import dz.solc.viewtool.dialog.LoadingDialog
import dz.solc.viewtool.dialog.OftenDialog
import kotlinx.android.synthetic.main.activity_layout_expertinformation.*
import kotlinx.android.synthetic.main.public_titile.*
import java.net.HttpURLConnection

/**
 *creat_user: zhengzaihong
 *email:1096877329@qq.com
 *creat_date: 2019/4/17 0017
 *creat_time: 13:31
 *describe: 专家资料详情
 **/

class ExpertInformationActivity : AppCompatActivity() {

    var mOfficePre: OfficePre = OfficePre.get()

    var booksBean: BooksBean? = null

    var loadingDialog: LoadingDialog? = null

    var oftenDialog: OftenDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_expertinformation)



        ivBack.visibility = View.VISIBLE
        tvTitle.visibility = View.VISIBLE
        tvTitle.text = "专家资料详情"
        ivBack.setOnClickListener {
            finish()
        }


        booksBean = intent.extras?.getSerializable("bean") as BooksBean


        loadingDialog = LoadingDialog(this)
            .setShowMaxTime(Int.MAX_VALUE)
            .setLoadingTips("下载中,请稍等...")
            .setLoadingTipsColor(UiCompat.getColor(this.resources, R.color.white))

        oftenDialog = OftenDialog(this)
            .initData("温馨提示", "是否重新下载该文档")
            .setAutoClose(true)
            .setOnClickButtonListener(object : OftenDialog.OnClickButtonListener {
                override fun oftenSure() {
                    mOfficePre?.overrideDownload()
                    loadingDialog?.showDialog()
                }

                override fun oftenCancle() {
                    mOfficePre.readOldFile()
                }
            })

        mOfficePre?.let {
            var builder = ParameterBuilder().apply {
                fileUrl = booksBean?.url
                savePath = AppConfig.get().PATH_APP_DOWNLOAD

                isAutoJoinFileName = true
                joinHeadParamKey = "Content-Disposition"
                joinHeadFileKey = "fileName"
                joinSymbol = "="


            }.build()

            loadingDialog?.showDialog()
            it.init(fileView)
                .setEnableAskUpdate(true)
                .setOnCheckLocalFileListener {
                    oftenDialog?.showDialog()
                }
                .addOnReadStatusListener { isSuccess ->
                    loadingDialog?.dismiss()
                    Log.outRedPrint("---------addOnReadStatusListener ${isSuccess}>")
                }
                .setOnDownloadListener(object : OfficePre.OnDownloadListener {
                    override fun progress(progress: Int?) {
                        loadingDialog?.setLoadingTips("下载中,请稍等: $progress")
                    }

                    override fun onStatus(status: LoadStatus?, obj: Any?) {
                    }

                    override fun onHandConnection(httpURLConnection: HttpURLConnection) {
                        //如果请求需要设置请求头等信息
                        httpURLConnection.setRequestProperty(
                            "hefu",
                            "35c98e9f350f4840af4453acbf8766b7"
                        )
                    }

                })
            it.show(builder)
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        mOfficePre?.destroy()
        loadingDialog = null
    }

}
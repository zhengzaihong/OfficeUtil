package com.hefu.office.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.dz.utlis.JavaUtils.*
import com.dz.utlis.UiCompat
import com.hefu.office.AppConfig
import com.hefu.office.R
import com.hefu.office.bean.BooksBean
import com.zzh.office.view.OfficePre
import dz.solc.viewtool.dialog.LoadingDialog
import dz.solc.viewtool.dialog.OftenDialog
import kotlinx.android.synthetic.main.activity_layout_expertinformation.*
import kotlinx.android.synthetic.main.public_titile.*

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

        if (null != intent.extras) {
            booksBean = intent.extras.getSerializable("bean") as BooksBean
        }


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
                        mOfficePre.preLocalFile()
                    }
                })

        mOfficePre?.let {

                    it.init(fileView)
                    .setOnCheckLocalFileListener {
                        oftenDialog?.showDialog()
                    }
                    .showPre(booksBean?.url, AppConfig.get().PATH_APP_DOWNLOAD)
                    .setOnDownloadListener(object : OfficePre.OnDownloadListener {
                                override fun progress(progress: Int?) {
                                    outRedPrint("下载进度：$progress")
                                }

                                override fun onStart() {
                                    outPrint("开始网络下载： ")
                                    runOnUiThread { loadingDialog?.showDialog() }
                                }

                                override fun onFinish(url: String?) {
                                    outRedPrint("下载完毕：$url")
                                    runOnUiThread { loadingDialog?.dismiss() }
                                }

                                override fun onException() {
                                    outRedPrint("下载失败")
                                    runOnUiThread { loadingDialog?.dismiss() }
                                }

                                override fun onCancel() {
                                    runOnUiThread { loadingDialog?.dismiss() }
                                }
                            })

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        mOfficePre?.destroy()
    }

}
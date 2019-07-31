package com.hefu.office.activity

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import com.dz.utlis.ClassTools
import com.dz.utlis.PermissionUtils
import com.dz.utlis.TimeUtil
import com.hefu.office.AppConfig
import com.hefu.office.R
import com.hefu.office.bean.BooksBean
import dz.solc.viewtool.adapter.CommonAdapter
import kotlinx.android.synthetic.main.activity_layout_expert_book.*
import kotlinx.android.synthetic.main.public_titile.*

/**
 *creat_user: zhengzaihong
 *email:1096877329@qq.com
 *creat_date: 2019/4/17 0017
 *creat_time: 13:31
 *describe: 专家资料
 **/

class ExpertBookListActivity : AppCompatActivity() {

    var ur1 = "https://www.kotlincn.net/docs/kotlin-docs.pdf"
    var ur2 = "http://zyweike.cdn.bcebos.com/zyweike/ep1/2018/04/02/周末加班统计.xlsx"

    private var listBooksAdapter: ListBooksAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_layout_expert_book)

        requestPermission()
        tvTitle.visibility = View.VISIBLE
        tvTitle.text = "专家资料"

        listBooksAdapter = ListBooksAdapter(this, R.layout.item_layout_expert_book)
        listViewExperBook.adapter = listBooksAdapter

        var data = arrayListOf(
                BooksBean("Kotlin基础文档", ur1),
                BooksBean("周末加班统计", ur2))


        listBooksAdapter!!.setNewData(data)

    }


    inner class ListBooksAdapter(mContext: Context, xmlId: Int) : CommonAdapter<BooksBean>(mContext, xmlId) {

        override fun convert(holder: ViewHolder, position: Int, bean: BooksBean) {
            holder.setText(R.id.tvBookName, bean.title)
                    .setText(R.id.tvBookTime, TimeUtil.stampstoTime("${TimeUtil.currentTimeStamp()}", "yyyy/MM/dd"))

            holder.getView<ViewGroup>(R.id.llIteme).setOnClickListener {

                var b = Bundle()
                b.putSerializable("bean",bean)
                ClassTools.toAct(mContext, ExpertInformationActivity::class.java,b)
            }
        }
    }

    //请求权限
    private fun requestPermission() {
        PermissionUtils.getInstance().requestPermission(this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),
                "主人,我需要权限哦！",
                object : PermissionUtils.CallBackListener() {

                    override fun onResult(granted: Boolean) {
                        if (granted) {
                            AppConfig.get().initFileDir()
                        }
                    }
                    override fun notAskPermission(permission: List<String>, goSetting: Boolean) {
                        if (goSetting) {
                            PermissionUtils.getInstance().toAppSetting(this@ExpertBookListActivity)
                        }
                    }
                })
    }

}
package com.frame.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ObjectUtils
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.frame.R
import com.frame.adapter.TestMultiQuickAdapter
import com.frame.common.CommonData
import com.frame.dataclass.DataClass
import com.frame.dataclass.bean.GotoMI
import com.frame.dataclass.bean.NameValue
import com.frame.dataclass.bean.PickerItem
import com.frame.dataclass.bean.PickerValue
import com.frame.httputils.HttpUtil2
import com.frame.httputils.OkHttpUtil2.IRequestFileCallback
import com.frame.httputils.RequestBuilder.RequestObject
import com.frame.httputils.other.AdapterClickListener
import com.frame.utils.CU
import com.frame.view.dialog.CustomPopView
import com.frame.view.dialog.PhotoDialog
import kotlinx.android.synthetic.main.activity_comm_recycleview.*
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by dongxie on 2017/3/22.
 */
class ExampleActivity : BaseTitleActivity() {

    private var mPhotoDialog: PhotoDialog? = null
    var mPvExample: CustomPopView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comm_recycleview)
        initControls()
    }

    private fun initControls() {
        setTitleText("EXAMPLE")

        if (mPvExample == null) {
            mPvExample = CustomPopView.Builder(mBActivity, R.layout.common_popview_example) { popView, h, adapter ->
                h.setOnClickListener(R.id.ivCloseDetail) { v: View? -> mPvExample!!.dismiss() }
            }
                .setOutsideTouchDismiss(false)
                .seBackDismiss(true)
                .create()
            mPvExample?.animationStyle = R.style.AnimationBottomInOut
        }

        recyclerView?.adapter = TestMultiQuickAdapter(mBActivity, null, getListItems())
            .setAdapterClickListener(object : AdapterClickListener<GotoMI, BaseViewHolder> {
                override fun onClick(view: View, holder: BaseViewHolder, item: GotoMI) {

                    val className = item.cls?.name ?: return
                    if (Activity::class.java.isAssignableFrom(Class.forName(className))) {
                        val intent = Intent(mBActivity, item.cls)
                        when (item.extend) {
                            "image" -> {
                                val imgList = ArrayList<String>()
                                imgList.add("https://pics5.baidu.com/feed/0d338744ebf81a4c9f3f23ea1e4cdb5c252da675.jpeg")
                                imgList.add("https://pics6.baidu.com/feed/c2fdfc039245d6887d1325116ca4c61bd31b24da.jpeg")
                                ActivityUtils.startActivity(Intent(mBActivity, BigPicActivity::class.java)
                                    .putExtra("index", 0)
                                    .putExtra("picUrls", imgList)
                                    .putExtra("picIds", ""))
                                return
                            }
                        }
                        ActivityUtils.startActivity(intent)
                    }
                }
            })
    }

    private fun showPhotoPickDialog(filepath: String) {
        if (mPhotoDialog == null) mPhotoDialog = PhotoDialog(mBActivity!!)
        if (mPhotoDialog?.isShowing == true) mPhotoDialog?.dismiss()
        mPhotoDialog?.setCaptureFile(filepath)?.show()
    }

    private fun uploadImg(path: String) {
        showProgressDialog()
        val filePath = CU.bitmap2File(path, CU.FILE_PATH, CU.randomFileName(".jpg"))
        val request = RequestObject()
        request.method = "imagesUpload"
        val params = HashMap<String?, Any?>()
        params["file"] = File(filePath)
        params["use"] = "download"
        uploadFile(request, params, DataClass::class.java, object : IRequestFileCallback {
            override fun <T> ObjResponse(isSuccess: Boolean, responseObj: T, ioException: IOException) {
                dismissProgressDialog()
                HttpUtil2.handleResponse(mBActivity, isSuccess, responseObj)
            }

            override fun ProgressResponse(progress: Long, total: Long) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CommonData.PHOTO_CAMERA -> uploadImg(mPhotoDialog!!.getCaptureFilePath())
                CommonData.PHOTO_GALLERY -> {
                    val sdPath = CU.getImagePathFromGallery(intent)
                    if (!ObjectUtils.isEmpty(sdPath)) {
                        uploadImg(sdPath)
                    }
                }
            }
        }
    }

    private fun getListItems(): MutableList<GotoMI> {
        val list: MutableList<GotoMI> = ArrayList()
        list.add(GotoMI(R.drawable.ic_group_tab1_pressed, "时间", "", GotoMI.TIME_PICKER, "", null))
        list.add(GotoMI(0, "", "", GotoMI.SPACE, "", null))

        val list1 = arrayListOf(NameValue("黑龙江", "3585"), NameValue("宁夏", "3742"), NameValue("山西", "3802"))
        val list2 = arrayListOf(NameValue("哈尔滨", "3586"), NameValue("北安", "3587"), NameValue("大庆", "3588"))
        val list22 = arrayListOf(NameValue("宁夏1", "3586"), NameValue("宁夏2", "3587"))
        val list3 = arrayListOf(NameValue("哈尔滨", "4464"), NameValue("松北", "4465"), NameValue("道里", "4466"))
        val list4 = arrayListOf(NameValue("萨尔图", "4580"), NameValue("龙凤", "4581"))
        val pickerValue = PickerValue().apply {
            isRelation = true
            this.list1 = list1
            map2 = hashMapOf("3585" to list2, "3742" to list22)
            map3 = hashMapOf("3586" to list3, "3588" to list4)
        }
        val preData = PickerItem(NameValue("重庆", ""), NameValue("渝北", ""), NameValue("金星", ""))
        list.add(GotoMI(R.drawable.ic_group_tab2_pressed, "地址", preData, GotoMI.MULTI_PICKER, pickerValue, null))
        list.add(GotoMI(0, "", "", GotoMI.SPACE, "", null))
        list.add(GotoMI(R.drawable.ic_group_tab3_pressed, "图片", "", GotoMI.IMAGE, "image", BigPicActivity::class.java))
        return list
    }

}
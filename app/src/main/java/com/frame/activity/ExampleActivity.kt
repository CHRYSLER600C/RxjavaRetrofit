package com.frame.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ObjectUtils
import com.frame.R
import com.frame.adapter.CommVHolder
import com.frame.adapter.ItemListAdapter
import com.frame.adapter.ItemListDataClass
import com.frame.common.CommonData
import com.frame.dataclass.DataClass
import com.frame.dataclass.bean.NameValue
import com.frame.dataclass.bean.PickerItem
import com.frame.dataclass.bean.PickerValue
import com.frame.httputils.HttpUtil2
import com.frame.httputils.OkHttpUtil2.IRequestFileCallback
import com.frame.httputils.RequestBuilder.RequestObject
import com.frame.observers.ProgressObserver
import com.frame.utils.CU
import com.frame.utils.JU
import com.frame.view.dialog.CustomPopView
import com.frame.view.dialog.CustomPopView.ICallBackPopView
import com.frame.view.dialog.PhotoDialog
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_common_sv_ll.*
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by dongxie on 2017/3/22.
 */
class ExampleActivity : BaseTitleActivity() {

    private val mILdc = ItemListDataClass()
    private var mILAdapter: ItemListAdapter? = null
    private var mPhotoDialog: PhotoDialog? = null
    var mPvExample: CustomPopView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_sv_ll)
        initControls()
    }

    private fun initControls() {
        setTitleText("EXAMPLE")
        addItemListDataClass()
        if (mPvExample == null) {
            mPvExample = CustomPopView.Builder(mBActivity, R.layout.common_popview_example, mCallBackPopView)
                .setOutsideTouchDismiss(false)
                .seBackDismiss(true)
                .create()
            mPvExample?.animationStyle = R.style.AnimationBottomInOut
        }
    }

    private fun addItemListDataClass() {
        mILdc.addCustomItem(KEY_CUSTOM, R.layout.common_item_text_text) { convertView: View?, h: CommVHolder ->
            h.setText(R.id.tvItemNameTt,
                "自定义View")
        }
        mILdc.addText(KEY_LINE, "我是分割线", "120-20", "#ff0000-#ccffcc", Gravity.RIGHT or Gravity.CENTER_VERTICAL)
        mILdc.addTextEditText(KEY_TET_SCHOOL, "学校", "重庆三中", "", "请填写学校", 0, 0)
        mILdc.addTextEditText(KEY_TET_GRADE, "年级", "5", "年级", "请填写年级", 1, 1)
        mILdc.addTextSelectTime(KEY_TEXT_SELECT_TIME, "入学时间", "", 1, 1)
        val list1 = ArrayList<NameValue>()
        list1.add(NameValue("黑龙江", "3585"))
        list1.add(NameValue("宁夏", "3742"))
        list1.add(NameValue("山西", "3802"))
        val list2 = ArrayList<NameValue>()
        list2.add(NameValue("哈尔滨", "3586"))
        list2.add(NameValue("北安", "3587"))
        list2.add(NameValue("大庆", "3588"))
        val list22 = ArrayList<NameValue>()
        list22.add(NameValue("宁夏1", "3586"))
        list22.add(NameValue("宁夏2", "3587"))
        val list3 = ArrayList<NameValue>()
        list3.add(NameValue("哈尔滨", "4464"))
        list3.add(NameValue("松北", "4465"))
        list3.add(NameValue("道里", "4466"))
        val list4 = ArrayList<NameValue>()
        list4.add(NameValue("萨尔图", "4580"))
        list4.add(NameValue("龙凤", "4581"))
        val pickerValue = PickerValue()
        pickerValue.isRelation = true
        pickerValue.list1 = list1
        pickerValue.map2 = HashMap()
        pickerValue.map2["3585"] = list2
        pickerValue.map2["3742"] = list22
        pickerValue.map3 = HashMap()
        pickerValue.map3["3586"] = list3
        pickerValue.map3["3588"] = list4
        mILdc.addTextSelect(KEY_TEXT_SELECT, "居住地址", PickerItem(NameValue("重庆", ""), NameValue("渝北", ""),
            NameValue("金星", "")), "栋", pickerValue, 1, 1)
        mILdc.addTextText(KEY_IMAGE_TEXT_TEXT, "图片管理", "查看", R.drawable.ic_default, 1) { value1: Any?, value2: Any? ->
            val imgList = ArrayList<String>()
            imgList.add("https://pics5.baidu.com/feed/0d338744ebf81a4c9f3f23ea1e4cdb5c252da675" +
                    ".jpeg?token=79091d07753b5b65acb005b78f73bfcb&s=86BC7C84C6CB1747788D50960300D080")
            imgList.add("https://pics6.baidu.com/feed/c2fdfc039245d6887d1325116ca4c61bd31b24da" +
                    ".jpeg?token=b2c1c0e64c38691d7ed12b72a2f65309&s=A32A68A5565311D2183BA0810300308B")
            val intent = Intent(mBActivity, BigPicActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("picUrls", imgList)
            intent.putExtra("picIds", "")
            ActivityUtils.startActivity(intent)
        }
        mILdc.addText(KEY_LINE2, " ", "80", "#00000000-#00000000", -1)
        mILdc.addButton(KEY_BUTTON, "保存", "20", "30", -1, 1) { value1: Any?, value2: Any? ->
            mILdc.addTextEditText(KEY_TET_CLASS, "班级", "三班", "", "请填写班级", 1, 0)
            mILAdapter!!.addItemView(KEY_TET_CLASS) //新增
            mILAdapter!!.removeItemView(KEY_CUSTOM) //删除
            mILdc.getItemInfo(KEY_LINE).name = "View.GONE"
            mILAdapter!!.refreshItemView(KEY_LINE) // 刷新
            doCommonGet("https://api.xiniuzc.com/data/city", null, object : ProgressObserver<DataClass>(mBActivity, true) {
                override fun onNext(dc: DataClass) {
                    val list = JU.al<ArrayList<LinkedTreeMap<String, Any>>>(dc.obj)
                    val list11 = ArrayList<NameValue>()
                    for (i in list.indices) {
                        list11.add(NameValue(JU.s(list[i], "name"), JU.s(list[i], "id")))
                    }
                    CU.showSinglePickerDialog(mBActivity, mTitleBar!!.getTitleText(), PickerValue(list11))
                }
            })
        }
        mILdc.addTextEditButton(KEY_TEB_VERIFY_CODE, "手机号码", "", "请输入手机号码", 11, 0
        ) { value1: Any?, value2: Any? -> mPvExample!!.showOrDismiss(llSvContainer) }
        mILAdapter = ItemListAdapter(mBActivity, llSvContainer, mILdc)
        mILAdapter!!.refreshAllItemView()
    }

    private fun showPhotoPickDialog(filepath: String) {
        if (mPhotoDialog == null) {
            mPhotoDialog = PhotoDialog(mBActivity!!)
        }
        if (mPhotoDialog!!.isShowing) {
            mPhotoDialog!!.dismiss()
        }
        mPhotoDialog!!.setCaptureFile(filepath)
        mPhotoDialog!!.show()
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

    var mCallBackPopView =
        ICallBackPopView { popView, h, adapter -> h.setOnClickListener(R.id.ivCloseDetail) { v: View? -> mPvExample!!.dismiss() } }

    companion object {
        const val KEY_TET_SCHOOL = 10
        const val KEY_TET_GRADE = 20
        const val KEY_TEB_VERIFY_CODE = 22
        const val KEY_LINE = 30
        const val KEY_CUSTOM = 40
        const val KEY_TEXT_SELECT_TIME = 50
        const val KEY_TEXT_SELECT = 60
        const val KEY_IMAGE_TEXT_TEXT = 70
        const val KEY_LINE2 = 75
        const val KEY_BUTTON = 80
        const val KEY_TET_CLASS = 100
    }
}
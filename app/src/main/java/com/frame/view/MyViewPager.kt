/*
 * 功能描述：扩展ViewPager
 */
package com.frame.view

import android.content.Context
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.RelativeLayout
import androidx.viewpager.widget.ViewPager
import com.frame.R
import com.frame.adapter.ViewPagerAdapter
import com.frame.utils.logi
import kotlin.math.sqrt

/**
 * 扩展ViewPager
 *
 */
class MyViewPager : ViewPager {

    private val TAG = "MyViewPager"
    var mScrollEnable = true
    private var mOnSimpleTouchListener: OnSimpleTouchListener? = null
    private var mImageView: ImageView? = null

    private var mViewSize: Point? = null // 图片控件的大小（x记录宽、y记录高）
    private var mImageSize: Point? = null // 原始图片大小（同样适用x记录宽、y记录高）
    private var mCurrentImageSize: Point? = null// 当前图片大小（缩放后会更新该值，同样适用x记录宽、y记录高）
    private var mCurrentPadding: Rect? = null// 当前图片距离图片控件的边距

    private val mMaxBig = 20 // 图片放大的最大倍数（以图片控件为基准）
    private val mMatrix = Matrix()
    private val savedMatrix = Matrix()
    private var mode = NONE
    var mStartPoint = PointF()
    var mMiddlePoint = PointF()
    var oldDist = 1f

    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
    }

    fun setOnSimpleOnTouchListener(listener: OnSimpleTouchListener?) {
        mOnSimpleTouchListener = listener
    }

    fun setHorizontalScrollEnable(enable: Boolean) {
        mScrollEnable = enable
    }

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        var flag = false
        val adapter = adapter as? ViewPagerAdapter ?: return super.dispatchTouchEvent(event)

        adapter.getItem(currentItem)?.run {
            if (this is RelativeLayout) { // 加了转圈圈
                mImageView = this.findViewById(R.id.ivBigPicLoading)
            } else if (this is ImageView) {
                mImageView = this
            }
        }

        if (event.action == MotionEvent.ACTION_DOWN) {
            mOnSimpleTouchListener?.onKeyDown()
        } else if (event.action == MotionEvent.ACTION_UP) {
            mOnSimpleTouchListener?.onKeyUp()
        }

        if ( /*mImageView.getTag() == null || */mImageView?.drawable == null) {
            return super.dispatchTouchEvent(event)
        }
        val values = FloatArray(9)
        mImageView?.imageMatrix?.getValues(values)
        if (mViewSize == null) {
            // 图片控件的高宽
            mViewSize = Point(mImageView!!.width, mImageView!!.height)
            logi(TAG, "test(控件宽高):" + mImageView!!.width + "," + mImageView!!.height)

            // 当前图片的高宽和间距
            saveCurrentImageSizeAndPadding(mImageView, values)
            // 原始图片的高宽
            mImageSize = Point(mCurrentImageSize!!.x, mCurrentImageSize!!.y)
        }
        mImageView?.scaleType = ScaleType.MATRIX
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mMatrix.set(mImageView!!.imageMatrix)
                savedMatrix.set(mMatrix)
                mStartPoint[event.x] = event.y
                mode = DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    savedMatrix.set(mMatrix)
                    midPoint(mMiddlePoint, event)
                    mode = ZOOM
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mode == DRAG) {
                    // 当前图片的高宽和间距
                    saveCurrentImageSizeAndPadding(mImageView, values)
                    // 居中显示
                    movieImageToCenter()
                }
                mode = NONE
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if (mode == ZOOM) {
                    // 当前图片的高宽和间距
                    saveCurrentImageSizeAndPadding(mImageView, values)
                    // 如果图片小于了原始大小，设置成原始大小
                    if (mCurrentImageSize!!.x < mImageSize!!.x) {
                        // matrix.reset();
                        val scale = mImageSize!!.x / 1f / mCurrentImageSize!!.x
                        logi(TAG, "放大(缩放比例)=$scale")
                        mMatrix.postScale(scale, scale, mMiddlePoint.x, mMiddlePoint.y)
                    } else {
                        // 如果图片放大超过图片控件的最大倍数
                        logi(TAG, "放大(当前尺寸)=" + mCurrentImageSize!!.x + "," + mCurrentImageSize!!.y)
                        logi(TAG, "放大(最大尺寸)=" + mViewSize!!.x * 2 + "," + mViewSize!!.y * 2)
                        if (mCurrentImageSize!!.x > mViewSize!!.x * mMaxBig) {
                            val scale = mViewSize!!.x * mMaxBig / 1f / mCurrentImageSize!!.x
                            logi(TAG, "放大(缩放比例)=$scale")
                            mMatrix.postScale(scale, scale, mMiddlePoint.x, mMiddlePoint.y)
                        }
                    }

                    // 当前图片的高宽和间距
                    mMatrix.getValues(values)
                    saveCurrentImageSizeAndPadding(mImageView, values)
                    movieImageToCenter()
                }
                mode = NONE
            }
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                val dx = event.x - mStartPoint.x
                val dy = event.y - mStartPoint.y
                flag = if (dx > 0 && mCurrentPadding!!.left >= 0) { // 拖动达到最左边
                    logi(TAG, "hit=left")
                    return super.dispatchTouchEvent(event)
                } else if (dx < 0 && mCurrentPadding!!.right >= 0) { // 拖动达到最右边
                    logi(TAG, "hit=right")
                    return super.dispatchTouchEvent(event)
                } else {
                    true
                }
                mMatrix.set(savedMatrix)
                mMatrix.postTranslate(dx, dy)
                mMatrix.getValues(values)
                saveCurrentImageSizeAndPadding(mImageView, values)
            } else if (mode == ZOOM) {
                val newDist = spacing(event)
                if (newDist > 10f) {
                    mMatrix.set(savedMatrix)
                    val scale = newDist / oldDist
                    mMatrix.postScale(scale, scale, mMiddlePoint.x, mMiddlePoint.y)
                    flag = true
                }
            }
        }
        mImageView!!.imageMatrix = mMatrix
        return if (flag) {
            true
        } else {
            super.dispatchTouchEvent(event)
        }
    }

    interface OnSimpleTouchListener {
        fun onKeyDown()
        fun onKeyUp()
    }

    /**
     * 移动图片到中心
     */
    private fun movieImageToCenter() {
        // 居中显示
        var dx = 0f
        if (mViewSize!!.x >= mCurrentImageSize!!.x) { // 图片宽度小于控件宽度
            dx = ((mViewSize!!.x - mCurrentImageSize!!.x) / 2 - mCurrentPadding!!.left).toFloat()
        } else {
            if (mCurrentPadding!!.left > 0) { // 左边拖出留了空
                dx = -mCurrentPadding!!.left.toFloat()
            } else if (mCurrentPadding!!.right > 0) { // 右边拖出留了空
                dx = mCurrentPadding!!.right.toFloat()
            }
        }
        var dy = 0f
        if (mViewSize!!.y >= mCurrentImageSize!!.y) { // 图片高度小于控件高度
            dy = ((mViewSize!!.y - mCurrentImageSize!!.y) / 2 - mCurrentPadding!!.top).toFloat()
        } else {
            if (mCurrentPadding!!.top > 0) { // 上边拖出留了空
                dy = -mCurrentPadding!!.top.toFloat()
            } else if (mCurrentPadding!!.bottom > 0) { // 下边拖出留了空
                dy = mCurrentPadding!!.bottom.toFloat()
            }
        }
        mMatrix.postTranslate(dx, dy)
        temp()
    }

    fun temp() {
        logi(TAG, "test-movie(目标位置)=" + (mViewSize!!.x - mCurrentImageSize!!.x) / 2 + ","
                + (mViewSize!!.y - mCurrentImageSize!!.y) / 2)
        logi(TAG, "test-movie(当前间距)=" + +mCurrentPadding!!.left + "," + mCurrentPadding!!.top)
        val dx = ((mViewSize!!.x - mCurrentImageSize!!.x) / 2 - mCurrentPadding!!.left).toFloat()
        val dy = ((mViewSize!!.y - mCurrentImageSize!!.y) / 2 - mCurrentPadding!!.top).toFloat()
        logi(TAG, "test-movie(移动距离)$dx,$dy")
    }

    /**
     * 记录当前图片与图片控件的间距
     *
     * @param imageView
     * @param values
     */
    private fun saveCurrentImageSizeAndPadding(imageView: ImageView?, values: FloatArray) {
        if (mCurrentImageSize == null) {
            mCurrentImageSize = Point()
        }
        val rect = imageView!!.drawable.bounds
        mCurrentImageSize!!.x = (rect.width() * values[0]).toInt()
        mCurrentImageSize!!.y = (rect.height() * values[0]).toInt()
        logi(TAG, "当前图片大小：" + mCurrentImageSize!!.x + "," + mCurrentImageSize!!.y)
        if (mCurrentPadding == null) {
            mCurrentPadding = Rect()
        }
        mCurrentPadding!!.left = values[2].toInt()
        mCurrentPadding!!.top = values[5].toInt()

        // 图片控件宽度 - 图片距离左边的边距 - 图片自身的宽度 = 图片距离右边的边距
        mCurrentPadding!!.right = mViewSize!!.x - mCurrentPadding!!.left - mCurrentImageSize!!.x
        mCurrentPadding!!.bottom = mViewSize!!.y - mCurrentPadding!!.top - mCurrentImageSize!!.y
        logi(TAG, "test(边距):" + mCurrentPadding!!.left + "," + mCurrentPadding!!.right + "," + mCurrentPadding!!.top
                + "," + mCurrentPadding!!.bottom)
    }

    /**
     * 计算两触摸点之间的距离
     *
     * @param event
     * @return
     */
    private fun spacing(event: MotionEvent): Float {
        val x = (event.getX(0) - event.getX(1)).toDouble()
        val y = (event.getY(0) - event.getY(1)).toDouble()
        return sqrt(x * x + y * y).toFloat()
    }

    /**
     * 计算两点之间的中间点
     *
     * @param point
     * @param event
     */
    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }
}
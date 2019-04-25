/*
 * 功能描述：扩展ViewPager
 */
package com.frame.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.frame.R;
import com.frame.adapter.ViewPagerAdapter;
import com.frame.utils.Logger;

/**
 * 扩展ViewPager
 * 
 */
public class MyViewPager extends ViewPager {

	private final String TAG = "MyViewPager";

	public boolean mScrollEnable = true;
	private OnSimpleOnTouchListener mOnSimpleOnTouchListener;
	private ImageView imgView;

	private Point mViewSize; // 图片控件的大小（x记录宽、y记录高）
	private Point mImageSize; // 原始图片大小（同样适用x记录宽、y记录高）
	private Point mCurrentImageSize; // 当前图片大小（缩放后会更新该值，同样适用x记录宽、y记录高）
	private Rect mCurrentPadding; // 当前图片距离图片控件的边距
	private int mMaxBig = 20; // 图片放大的最大倍数（以图片控件为基准）

	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	PointF mStartPoint = new PointF();
	PointF mMidddlePoint = new PointF();
	float oldDist = 1f;

	public void setOnSimpleOnTouchListener(OnSimpleOnTouchListener listener) {
		this.mOnSimpleOnTouchListener = listener;
	}

	/**
	 * 是否可以左右滑动
	 * 
	 * @param enable
	 */
	public void setScrollEnable(boolean enable) {
		this.mScrollEnable = enable;
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		boolean flag = false;
		ViewPagerAdapter adapter = (ViewPagerAdapter) getAdapter();
		if (adapter == null) {
			return super.dispatchTouchEvent(event);
		}
		View v = (View) adapter.getItem(getCurrentItem());
		if (v instanceof RelativeLayout) { // 加了转圈圈
			imgView = (ImageView) v.findViewById(R.id.ivBigPicLoading);
		} else if (v instanceof ImageView) {
			imgView = (ImageView) adapter.getItem(getCurrentItem());
		}

		if (mOnSimpleOnTouchListener != null) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mOnSimpleOnTouchListener.onKeyDown();

			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mOnSimpleOnTouchListener.onKeyUp();

			}
		}

		if (imgView.getTag() == null || imgView.getDrawable() == null) {
			return super.dispatchTouchEvent(event);
		}

		float[] values = new float[9];
		imgView.getImageMatrix().getValues(values);

		if (mViewSize == null) {
			// 图片控件的高宽
			mViewSize = new Point(imgView.getWidth(), imgView.getHeight());
			Logger.i(TAG, "test(控件宽高):" + imgView.getWidth() + "," + imgView.getHeight());

			// 当前图片的高宽和间距
			saveCurrentImageSizeAndPadding(imgView, values);
			// 原始图片的高宽
			mImageSize = new Point(mCurrentImageSize.x, mCurrentImageSize.y);
		}

		imgView.setScaleType(ScaleType.MATRIX);
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			matrix.set(imgView.getImageMatrix());
			savedMatrix.set(matrix);
			mStartPoint.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mMidddlePoint, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mode == DRAG) {
				// 当前图片的高宽和间距
				saveCurrentImageSizeAndPadding(imgView, values);
				// 居中显示
				movieImageToCenter();
			}
			mode = NONE;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			if (mode == ZOOM) {
				// 当前图片的高宽和间距
				saveCurrentImageSizeAndPadding(imgView, values);
				// 如果图片小于了原始大小，设置成原始大小
				if (mCurrentImageSize.x < mImageSize.x) {
					// matrix.reset();
					float scale = mImageSize.x / 1f / mCurrentImageSize.x;
					Logger.i(TAG, "放大(缩放比例)=" + scale);
					matrix.postScale(scale, scale, mMidddlePoint.x, mMidddlePoint.y);

				} else {
					// 如果图片放大超过图片控件的最大倍数
					Logger.i(TAG, "放大(当前尺寸)=" + mCurrentImageSize.x + "," + mCurrentImageSize.y);
					Logger.i(TAG, "放大(最大尺寸)=" + (mViewSize.x * 2) + "," + (mViewSize.y * 2));
					if (mCurrentImageSize.x > mViewSize.x * mMaxBig) {
						float scale = mViewSize.x * mMaxBig / 1f / mCurrentImageSize.x;
						Logger.i(TAG, "放大(缩放比例)=" + scale);
						matrix.postScale(scale, scale, mMidddlePoint.x, mMidddlePoint.y);
					}

				}

				// 当前图片的高宽和间距
				matrix.getValues(values);
				saveCurrentImageSizeAndPadding(imgView, values);
				movieImageToCenter();
			}
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				float dx = event.getX() - mStartPoint.x;
				float dy = event.getY() - mStartPoint.y;
				if (dx > 0 && mCurrentPadding.left >= 0) { // 拖动达到最左边
					Logger.i(TAG, "hit=left");

					return super.dispatchTouchEvent(event);

				} else if (dx < 0 && mCurrentPadding.right >= 0) { // 拖动达到最右边
					Logger.i(TAG, "hit=right");

					return super.dispatchTouchEvent(event);

				} else {
					flag = true;
				}

				matrix.set(savedMatrix);
				matrix.postTranslate(dx, dy);
				matrix.getValues(values);
				saveCurrentImageSizeAndPadding(imgView, values);

			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mMidddlePoint.x, mMidddlePoint.y);
					flag = true;
				}
			}
			break;
		}

		imgView.setImageMatrix(matrix);
		if (flag) {
			return true;
		} else {
			return super.dispatchTouchEvent(event);
		}
	}

	public interface OnSimpleOnTouchListener {
		void onKeyDown();

		void onKeyUp();
	}

	/**
	 * 移动图片到中心
	 */
	private void movieImageToCenter() {
		// 居中显示
		float dx = 0;
		if (mViewSize.x >= mCurrentImageSize.x) { // 图片宽度小于控件宽度
			dx = (mViewSize.x - mCurrentImageSize.x) / 2 - mCurrentPadding.left;

		} else {
			if (mCurrentPadding.left > 0) { // 左边拖出留了空
				dx = -mCurrentPadding.left;

			} else if (mCurrentPadding.right > 0) { // 右边拖出留了空
				dx = mCurrentPadding.right;
			}
		}

		float dy = 0;
		if (mViewSize.y >= mCurrentImageSize.y) { // 图片高度小于控件高度
			dy = (mViewSize.y - mCurrentImageSize.y) / 2 - mCurrentPadding.top;

		} else {
			if (mCurrentPadding.top > 0) { // 上边拖出留了空
				dy = -mCurrentPadding.top;

			} else if (mCurrentPadding.bottom > 0) { // 下边拖出留了空
				dy = mCurrentPadding.bottom;

			}
		}
		matrix.postTranslate(dx, dy);
		temp();
	}

	public void temp() {
		Logger.i(TAG, "test-movie(目标位置)=" + ((mViewSize.x - mCurrentImageSize.x) / 2) + ","
				+ ((mViewSize.y - mCurrentImageSize.y) / 2));
		Logger.i(TAG, "test-movie(当前间距)=" + +mCurrentPadding.left + "," + mCurrentPadding.top);
		float dx = (mViewSize.x - mCurrentImageSize.x) / 2 - mCurrentPadding.left;
		float dy = (mViewSize.y - mCurrentImageSize.y) / 2 - mCurrentPadding.top;
		Logger.i(TAG, "test-movie(移动距离)" + dx + "," + dy);
	}

	/**
	 * 记录当前图片与图片控件的间距
	 * 
	 * @param imgView
	 * @param values
	 */
	private void saveCurrentImageSizeAndPadding(ImageView imgView, float[] values) {

		if (mCurrentImageSize == null) {
			mCurrentImageSize = new Point();
		}

		Rect rect = imgView.getDrawable().getBounds();
		mCurrentImageSize.x = (int) (rect.width() * values[0]);
		mCurrentImageSize.y = (int) (rect.height() * values[0]);
		Logger.i(TAG, "当前图片大小：" + mCurrentImageSize.x + "," + mCurrentImageSize.y);

		if (mCurrentPadding == null) {
			mCurrentPadding = new Rect();
		}

		mCurrentPadding.left = (int) values[2];
		mCurrentPadding.top = (int) values[5];

		// 图片控件宽度 - 图片距离左边的边距 - 图片自身的宽度 = 图片距离右边的边距
		mCurrentPadding.right = mViewSize.x - mCurrentPadding.left - mCurrentImageSize.x;
		mCurrentPadding.bottom = mViewSize.y - mCurrentPadding.top - mCurrentImageSize.y;
		Logger.i(TAG, "test(边距):" + mCurrentPadding.left + "," + mCurrentPadding.right + "," + mCurrentPadding.top
				+ "," + mCurrentPadding.bottom);
	}

	/**
	 * 计算两触摸点之间的距离
	 * 
	 * @param event
	 * @return
	 */
	private float spacing(MotionEvent event) {
		double x = event.getX(0) - event.getX(1);
		double y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * 计算两点之间的中间点
	 * 
	 * @param point
	 * @param event
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}

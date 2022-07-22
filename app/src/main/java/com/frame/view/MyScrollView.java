package com.frame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

	private GestureDetector mGestureDetector;

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(new YScrollDetector());
	}

	public MyScrollView(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
	}

	@Override
	public void requestChildFocus(View child, View focused) {
		if (focused instanceof WebView) return;
		super.requestChildFocus(child, focused);
	}

	class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (Math.abs(distanceY) >= Math.abs(distanceX) + 20) {
				return true;
			}
			return false;
		}
	}

}

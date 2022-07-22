package com.frame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquareRelLayout extends RelativeLayout {

	public SquareRelLayout(Context context) {
		super(context);
	}

	public SquareRelLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareRelLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

		// Children are just made to fill our space.
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
		int smallSize = Math.min(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(smallSize, smallSize);
	}
}

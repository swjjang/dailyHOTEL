package com.twoheart.dailyhotel.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BottomCropImageView extends ImageView
{
	public BottomCropImageView(Context context)
	{
		super(context);
		initLayout(context);
	}

	public BottomCropImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initLayout(context);
	}

	public BottomCropImageView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initLayout(context);
	}

	public BottomCropImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		initLayout(context);
	}

	private void initLayout(Context context)
	{
		setScaleType(ScaleType.MATRIX);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		recomputeImgMatrix();
	}

	@Override
	protected boolean setFrame(int l, int t, int r, int b)
	{
		recomputeImgMatrix();
		return super.setFrame(l, t, r, b);
	}

	private void recomputeImgMatrix()
	{
		if (getDrawable() == null)
		{
			return;
		}

		final Matrix matrix = getImageMatrix();

		float scale;
		final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
		final int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
		final int drawableWidth = getDrawable().getIntrinsicWidth();
		final int drawableHeight = getDrawable().getIntrinsicHeight();

		if (drawableWidth * viewHeight > drawableHeight * viewWidth)
		{
			scale = (float) viewHeight / (float) drawableHeight;
		} else
		{
			scale = (float) viewWidth / (float) drawableWidth;
		}

		//Define the rect to take image portion from
		RectF drawableRect = new RectF(0, drawableHeight - (viewHeight / scale), drawableWidth, drawableHeight);
		RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
		matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.FILL);

		setImageMatrix(matrix);
	}
}

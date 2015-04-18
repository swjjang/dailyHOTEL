/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

/**
 * Draws a bubble with a shadow, filled with any color.
 */
class BubbleDrawable extends Drawable
{

	private final Drawable mShadow;
	private final Drawable mMask;
	private final Drawable mMask01;
	private int mColor = Color.WHITE;

	public BubbleDrawable(Resources res)
	{
		mMask = res.getDrawable(R.drawable.bubble_mask);
		mMask01 = res.getDrawable(R.drawable.bubble_mask01);
		mShadow = res.getDrawable(R.drawable.bubble_shadow);
	}

	public void setColor(int color)
	{
		mColor = color;
	}

	@Override
	public void draw(Canvas canvas)
	{
		mMask01.draw(canvas);
		
		Bitmap split = Bitmap.createBitmap(canvas.getWidth() - 8, canvas.getHeight() - 8, Bitmap.Config.ARGB_8888);
		
		Canvas dstCanvas = new Canvas(split);
		
		Rect rect = mMask.getBounds();
		mMask.setBounds(rect.left + 4, rect.top + 4, rect.right - 4, rect.bottom - 4);
		mMask.draw(dstCanvas);
		dstCanvas.drawColor(mColor, PorterDuff.Mode.SRC_IN);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawBitmap(split, 0, 0, paint);
		
		mShadow.draw(canvas);
	}

	@Override
	public void setAlpha(int alpha)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setColorFilter(ColorFilter cf)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int getOpacity()
	{
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom)
	{
		mMask.setBounds(left, top, right, bottom);
		mMask01.setBounds(left, top, right, bottom);
		mShadow.setBounds(left, top, right, bottom);
	}
	
	@Override
	public void setBounds(Rect bounds)
	{
		mMask.setBounds(bounds);
		mMask01.setBounds(bounds);
		mShadow.setBounds(bounds);
	}

	@Override
	public boolean getPadding(Rect padding)
	{
		return mMask.getPadding(padding);
	}
}

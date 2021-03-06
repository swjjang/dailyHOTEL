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

package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;

/**
 * IconGenerator generates icons that contain text (or custom content) within an
 * info window-like shape.
 * <p>
 * The icon {@link Bitmap}s generated by the factory should be used in
 * conjunction with a
 * {@link com.google.android.gms.maps.model.BitmapDescriptorFactory}.
 * <p>
 * This class is not thread safe.
 */
public class RegionIconGenerator
{
    private final Context mContext;

    private ViewGroup mContainer;
    private TextView mTextView;
    private View mContentView;

    /**
     * Creates a new IconGenerator with the default style.
     */
    public RegionIconGenerator(Context context)
    {
        mContext = context;
        mContainer = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.marker_region, null);
        mContentView = mTextView = mContainer.findViewById(R.id.text);
    }

    /**
     * Sets the text content, then creates an icon with the current style.
     *
     * @param text the text content to display inside the icon.
     */
    public Bitmap makeIcon(String text)
    {
        if (mTextView != null)
        {
            mTextView.setText(text);
        }

        return makeIcon();
    }

    /**
     * Creates an icon with the current content and style.
     * <p>
     * This method is useful if a custom view has previously been set, or if
     * text content is not applicable.
     */
    public Bitmap makeIcon()
    {
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mContainer.measure(measureSpec, measureSpec);

        int measuredWidth = mContainer.getMeasuredWidth();
        int measuredHeight = mContainer.getMeasuredHeight();

        if (measuredWidth > measuredHeight)
        {
            measuredWidth = measuredHeight;
        } else
        {
            //			measuredWidth = measuredHeight;
        }

        mContainer.layout(0, 0, measuredWidth, measuredWidth);

        int left = (measuredWidth - mTextView.getMeasuredWidth()) / 2 + mTextView.getPaddingLeft();
        int top = (measuredWidth - mTextView.getMeasuredHeight()) / 2 + mTextView.getPaddingTop();

        mTextView.setPadding(left, top, 0, 0);

        Bitmap r = Bitmap.createBitmap(measuredWidth, measuredWidth, Bitmap.Config.ARGB_8888);
        r.eraseColor(Color.TRANSPARENT);

        Canvas canvas = new Canvas(r);

        mContainer.draw(canvas);
        return r;
    }

    /**
     * Sets the text color, size, style, hint color, and highlight color from
     * the specified <code>TextAppearance</code> resource.
     *
     * @param resId the identifier of the resource.
     */
    public void setTextAppearance(Context context, int resId)
    {
        if (mTextView != null)
        {
            mTextView.setTextAppearance(context, resId);
        }
    }

    public void setTextColor(int resId)
    {
        if (mTextView != null)
        {
            mTextView.setTextColor(resId);
        }
    }

    public void setTextPadding(int value)
    {
        if (mTextView != null)
        {
            mTextView.setPadding(value, value, value, value);
        }
    }

    public void setTextSize(int dp)
    {
        if (mTextView != null)
        {
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dp);
        }
    }

    public void setLayoutParams(ViewGroup.LayoutParams layoutParams)
    {
        if (layoutParams == null)
        {
            return;
        }

        if (mTextView != null)
        {
            mTextView.setLayoutParams(layoutParams);
        }
    }

    /**
     * Sets the text color, size, style, hint color, and highlight color from
     * the specified <code>TextAppearance</code> resource.
     *
     * @param resId the identifier of the resource.
     */
    public void setTextAppearance(int resId)
    {
        setTextAppearance(mContext, resId);
    }

    /**
     * Sets the padding of the content view. The default padding of the content
     * view (i.e. text view) is 5dp top/bottom and 10dp left/right.
     *
     * @param left   the left padding in pixels.
     * @param top    the top padding in pixels.
     * @param right  the right padding in pixels.
     * @param bottom the bottom padding in pixels.
     */
    public void setContentPadding(int left, int top, int right, int bottom)
    {
        mContentView.setPadding(left, top, right, bottom);
    }
}

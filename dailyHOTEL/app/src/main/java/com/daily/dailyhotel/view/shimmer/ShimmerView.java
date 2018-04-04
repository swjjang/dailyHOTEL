package com.daily.dailyhotel.view.shimmer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class ShimmerView extends View implements ShimmerViewInterface
{
    private ShimmerViewHelper shimmerViewHelper;

    public ShimmerView(Context context)
    {
        super(context);
        shimmerViewHelper = new ShimmerViewHelper(this, null);
    }

    public ShimmerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        shimmerViewHelper = new ShimmerViewHelper(this, attrs);
    }

    public ShimmerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        shimmerViewHelper = new ShimmerViewHelper(this, attrs);
    }

    @Override
    public float getGradientX()
    {
        return shimmerViewHelper.getGradientX();
    }

    @Override
    public void setGradientX(float gradientX)
    {
        shimmerViewHelper.setGradientX(gradientX);
    }

    @Override
    public boolean isShimmering()
    {
        return shimmerViewHelper.isShimmering();
    }

    @Override
    public void setShimmering(boolean isShimmering)
    {
        shimmerViewHelper.setShimmering(isShimmering);
    }

    @Override
    public boolean isSetUp()
    {
        return shimmerViewHelper.isSetUp();
    }

    @Override
    public void setAnimationSetupCallback(ShimmerViewHelper.AnimationSetupCallback callback)
    {
        shimmerViewHelper.setAnimationSetupCallback(callback);
    }

    @Override
    public int getPrimaryColor()
    {
        return shimmerViewHelper.getPrimaryColor();
    }

    @Override
    public void setPrimaryColor(int primaryColor)
    {
        shimmerViewHelper.setPrimaryColor(primaryColor);
    }

    @Override
    public int getReflectionColor()
    {
        return shimmerViewHelper.getReflectionColor();
    }

    @Override
    public void setReflectionColor(int reflectionColor)
    {
        shimmerViewHelper.setReflectionColor(reflectionColor);
    }

    @Override
    public void setLinearGradientWidth(float width)
    {
        shimmerViewHelper.setLinearGradientWidth(width);
    }

    @Override
    public void setBackgroundColor(int color)
    {
        super.setBackgroundColor(color);
        if (shimmerViewHelper != null)
        {
            shimmerViewHelper.setPrimaryColor(color);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        if (shimmerViewHelper != null)
        {
            shimmerViewHelper.onSizeChanged();
        }
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        if (shimmerViewHelper != null)
        {
            shimmerViewHelper.onDraw(canvas);
        }
        super.onDraw(canvas);
    }
}


package com.twoheart.dailyhotel.screen.home;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by android_sam on 2017. 1. 19..
 */

public class HomeCoverView extends View
{
    private Context mContext;
    private PaintDrawable mPaintDrawable;
    private RectShape mRactShape;
    private ShapeDrawable.ShaderFactory mShaderFactory;
    private ValueAnimator mAnimator;

    private int mDuration = 1000;
    private int mRepeatCount = ValueAnimator.INFINITE;

    private boolean mIsAnimateEnable;

    private float mDefaultCenterX = 0;
    private float mCenterX = mDefaultCenterX;

    private int[] mColorList = new int[]{Color.GRAY, Color.DKGRAY, Color.BLACK, Color.DKGRAY, Color.GRAY};
    private float[] mPointList = new float[]{0f, mCenterX - 0.2f, mCenterX, mCenterX + 0.2f, 1f};

    public HomeCoverView(Context context)
    {
        super(context);
        mContext = context;
        init();
    }

    public HomeCoverView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        init();
    }

    public HomeCoverView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public HomeCoverView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    private void init()
    {
        mCenterX = mDefaultCenterX;

        // 아래 방법으로 일단 비스무리 성공
        mShaderFactory = new ShapeDrawable.ShaderFactory()
        {
            @Override
            public Shader resize(int width, int height)
            {
                LinearGradient linearGradient = new LinearGradient(0 - (HomeCoverView.this.getWidth() * 0.5f),//
                    0, HomeCoverView.this.getWidth() * 1.5f, 0,//
                    mColorList, //substitute the correct colors for these
                    mPointList, Shader.TileMode.REPEAT);//
                return linearGradient;
            }
        };

        mPaintDrawable = new PaintDrawable();
        mRactShape = new RectShape();
        mPaintDrawable.setShape(mRactShape);
        mPaintDrawable.setShaderFactory(mShaderFactory);
    }

    public float getCenterX()
    {
        return mCenterX;
    }

    public void setCenterX(float centerX)
    {
        this.mCenterX = centerX;
    }

    public float getDefaultCenterX()
    {
        return mDefaultCenterX;
    }

    public void setDefaultCenterX(float defaultCenterX)
    {
        this.mDefaultCenterX = defaultCenterX;
    }

    public void setDuration(int duration)
    {
        mDuration = duration;
    }

    public int getDuration()
    {
        return mDuration;
    }

    public int getRepeatCount()
    {
        return mRepeatCount;
    }

    public void setRepeatCount(int repeatCount)
    {
        this.mRepeatCount = repeatCount;
    }

    public void initAnimator()
    {
        if (mAnimator != null)
        {
            mAnimator.cancel();
            mAnimator = null;
        }

        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(2000);
        mAnimator.setRepeatCount(10);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mCenterX = (float) animation.getAnimatedValue();
                mPaintDrawable.setShape(mRactShape); // this makes the shader recreate the lineargradient
            }
        });

        mAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mAnimator = null;
                mCenterX = mDefaultCenterX;
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                mAnimator = null;
                mCenterX = mDefaultCenterX;
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });
    }

    public void start()
    {
        if (mAnimator == null)
        {
            initAnimator();
        } else
        {
            mAnimator.cancel();
        }

        mAnimator.start();
    }

    public void stop()
    {
        if (mAnimator != null)
        {
            mAnimator.end();
        }
    }
}

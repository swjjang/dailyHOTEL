package com.twoheart.dailyhotel.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.twoheart.dailyhotel.place.activity.PlaceDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.HotelDetailActivity;

public class AnimationImageView extends ImageView
{
    private static final int ANIMATION_DURATION = 10000;

    private int mHeight;
    private int mWidth;
    private ValueAnimator mValueAnimator;
    private int mScaledWidth;
    private int mScaledHeight;
    private float mScale;
    private int mTranslateDistance;
    private long mAnimationPlayTime;
    private boolean mReservationAnimation;
    private boolean mIsRightAnimation;
    private boolean mIsImageNone;

    private Matrix mMatrix;

    private HotelDetailActivity.OnUserActionListener mOnUserActionListener;
    private PlaceDetailActivity.OnImageActionListener mOnImageActionListener;

    private Handler mAnimationHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            final int width = msg.arg1;
            final int height = msg.arg2;
            final Matrix matrix = (Matrix) msg.obj;

            final boolean isVerticalTranslate;
            int moveDistance = 0;

            if (width > height)
            {
                isVerticalTranslate = false;
                moveDistance = width - mWidth;
            } else if (width < height)
            {
                isVerticalTranslate = true;
                moveDistance = height - mHeight;
            } else
            {
                return;
            }

            if (mValueAnimator == null)
            {
                mValueAnimator = ValueAnimator.ofInt(mTranslateDistance, moveDistance);
            } else
            {
                stopAnimation(true);
            }

            mAnimationPlayTime = ANIMATION_DURATION - mAnimationPlayTime;

            if (mAnimationPlayTime <= 0)
            {
                mAnimationPlayTime = ANIMATION_DURATION;
            }

            mValueAnimator.setDuration(mAnimationPlayTime).addUpdateListener(new AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    int value = (Integer) animation.getAnimatedValue();
                    int translateValue;

                    mAnimationPlayTime = animation.getCurrentPlayTime();

                    if (mTranslateDistance == 0)
                    {
                        translateValue = value;
                    } else
                    {
                        translateValue = value - mTranslateDistance;
                    }

                    mTranslateDistance = value;

                    if (mIsRightAnimation == false)
                    {
                        if (isVerticalTranslate == true)
                        {
                            matrix.postTranslate(0, -translateValue);
                        } else
                        {
                            matrix.postTranslate(-translateValue, 0);
                        }
                    } else
                    {
                        if (isVerticalTranslate == true)
                        {
                            matrix.postTranslate(0, translateValue);
                        } else
                        {
                            matrix.postTranslate(translateValue, 0);
                        }
                    }

                    setImageMatrix(matrix);
                    invalidate();
                }
            });

            mValueAnimator.addListener(new AnimatorListener()
            {
                private boolean mIsCancel;

                @Override
                public void onAnimationStart(Animator animation)
                {
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mIsCancel == false)
                    {
                        requestNextImage();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mIsCancel = true;
                }
            });

            mValueAnimator.start();
        }
    };

    public AnimationImageView(Context context, int height, int width, boolean isRightAnimation)
    {
        super(context);

        mHeight = height;
        mWidth = width;
        mIsRightAnimation = isRightAnimation;

        initLayout(context);
    }

    //	public AnimationImageView(Context mContext, AttributeSet attrs)
    //	{
    //		super(mContext, attrs);
    //		initLayout(mContext);
    //	}
    //
    //	public AnimationImageView(Context mContext, AttributeSet attrs, int defStyleAttr)
    //	{
    //		super(mContext, attrs, defStyleAttr);
    //		initLayout(mContext);
    //	}
    //
    //	public AnimationImageView(Context mContext, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    //	{
    //		super(mContext, attrs, defStyleAttr, defStyleRes);
    //		initLayout(mContext);
    //	}

    private void initLayout(Context context)
    {
        setScaleType(ScaleType.MATRIX);

        mReservationAnimation = false;
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        super.setImageBitmap(bm);

        if (bm == null)
        {
            mIsImageNone = true;
            return;
        }

        mIsImageNone = false;

        float xScale = ((float) mWidth) / bm.getWidth();
        float yScale = ((float) mHeight) / bm.getHeight();
        mScale = Math.max(xScale, yScale);

        mScaledWidth = (int) (mScale * bm.getWidth());
        mScaledHeight = (int) (mScale * bm.getHeight());

        mTranslateDistance = 0;

        initAnimation(mIsRightAnimation);

        if (mReservationAnimation == true)
        {
            mReservationAnimation = false;

            startAnimation();
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable)
    {
        super.setImageDrawable(drawable);

        if (drawable == null)
        {
            mIsImageNone = true;
            return;
        }

        mIsImageNone = false;

        float xScale = ((float) mWidth) / drawable.getIntrinsicWidth();
        float yScale = ((float) mHeight) / drawable.getIntrinsicHeight();
        mScale = Math.max(xScale, yScale);

        mScaledWidth = (int) (mScale * drawable.getIntrinsicWidth());
        mScaledHeight = (int) (mScale * drawable.getIntrinsicHeight());

        mTranslateDistance = 0;

        initAnimation(mIsRightAnimation);

        if (mReservationAnimation == true)
        {
            mReservationAnimation = false;

            startAnimation();
        }
    }

    public synchronized void startAnimation()
    {
        if (mScaledWidth == 0 && mScaledHeight == 0)
        {
            if (mIsImageNone == true)
            {
                requestNextImage();
            } else
            {
                mReservationAnimation = true;
            }
            return;
        }

        if (mValueAnimator != null || mAnimationHandler.hasMessages(0))
        {
            return;
        }

        mReservationAnimation = false;

        Message message = new Message();
        message.what = 0;
        message.arg1 = mScaledWidth;
        message.arg2 = mScaledHeight;
        message.obj = mMatrix;

        mAnimationHandler.removeMessages(0);
        mAnimationHandler.sendMessageDelayed(message, 1000);
    }

    public synchronized void stopAnimation(boolean initDuration)
    {
        if (initDuration == true)
        {
            mAnimationPlayTime = 0;
            mTranslateDistance = 0;
        }

        mReservationAnimation = false;
        mAnimationHandler.removeMessages(0);

        if (mValueAnimator != null)
        {
            mValueAnimator.cancel();
            mValueAnimator.removeAllListeners();
        }

        mValueAnimator = null;
    }

    private void requestNextImage()
    {
        mAnimationPlayTime = 0;

        // 다음 이미지를 호출한다.
        if (mOnUserActionListener != null)
        {
            if (mIsRightAnimation == false)
            {
                mOnUserActionListener.nextSlide();
            } else
            {
                mOnUserActionListener.prevSlide();
            }
        }

        if (mOnImageActionListener != null)
        {
            if (mIsRightAnimation == false)
            {
                mOnImageActionListener.nextSlide();
            } else
            {
                mOnImageActionListener.prevSlide();
            }
        }
    }

    public void initAnimation(boolean isRightAnimation)
    {
        mIsRightAnimation = isRightAnimation;

        mTranslateDistance = 0;

        mMatrix = getImageMatrix();
        mMatrix.reset();
        mMatrix.postScale(mScale, mScale);

        if (isRightAnimation == false)
        {
            mMatrix.postTranslate(0.0f, 0.0f);
        } else
        {
            if (mScaledWidth >= mScaledHeight)
            {
                float moveDistance = mScaledWidth - mWidth;

                mMatrix.postTranslate(-moveDistance, 0.0f);

            } else if (mScaledWidth < mScaledHeight)
            {
                float moveDistance = mScaledHeight - mHeight;

                mMatrix.postTranslate(0.0f, -moveDistance);
            }
        }

        setImageMatrix(mMatrix);
    }

    public void setOnAnimationListener(HotelDetailActivity.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    public void setOnImageActionListener(PlaceDetailActivity.OnImageActionListener listener)
    {
        mOnImageActionListener = listener;
    }
}

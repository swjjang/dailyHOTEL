package com.twoheart.dailyhotel.ui;

import com.twoheart.dailyhotel.activity.HotelDetailActivity;
import com.twoheart.dailyhotel.util.ExLog;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

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
	private boolean mIsLeftAnimation;

	private Matrix mMatrix;

	private HotelDetailActivity.OnUserActionListener mOnUserActionListener;

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
				stopAnimation();
			}

			ExLog.d("moveDistance : " + moveDistance);

			mAnimationPlayTime = ANIMATION_DURATION - mAnimationPlayTime;

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

					if (mIsLeftAnimation == true)
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
					ExLog.d("onAnimationStart");
				}

				@Override
				public void onAnimationRepeat(Animator animation)
				{
					ExLog.d("onAnimationRepeat");
				}

				@Override
				public void onAnimationEnd(Animator animation)
				{
					ExLog.d("onAnimationEnd");

					if (mIsCancel == false)
					{
						// 다음 이미지를 호출한다.
						if (mOnUserActionListener != null)
						{
							if (mIsLeftAnimation == true)
							{
								mOnUserActionListener.nextSlide();
							} else
							{
								mOnUserActionListener.prevSlide();
							}
						}
					}
				}

				@Override
				public void onAnimationCancel(Animator animation)
				{
					ExLog.d("onAnimationCancel");

					mIsCancel = true;
				}
			});

			mValueAnimator.start();
		}
	};

	public AnimationImageView(Context context, int height, int width)
	{
		super(context);

		mHeight = height;
		mWidth = width;

		initLayout(context);
	}

	//	public AnimationImageView(Context context, AttributeSet attrs)
	//	{
	//		super(context, attrs);
	//		initLayout(context);
	//	}
	//
	//	public AnimationImageView(Context context, AttributeSet attrs, int defStyleAttr)
	//	{
	//		super(context, attrs, defStyleAttr);
	//		initLayout(context);
	//	}
	//
	//	public AnimationImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	//	{
	//		super(context, attrs, defStyleAttr, defStyleRes);
	//		initLayout(context);
	//	}

	private void initLayout(Context context)
	{
		setScaleType(ScaleType.MATRIX);

		mReservationAnimation = false;
		mIsLeftAnimation = true;
	}

	@Override
	public void setImageBitmap(Bitmap bm)
	{
		super.setImageBitmap(bm);

		if (bm == null)
		{
			return;
		}

		float xScale = ((float) mWidth) / bm.getWidth();
		float yScale = ((float) mHeight) / bm.getHeight();
		mScale = Math.max(xScale, yScale);

		mScaledWidth = (int) (mScale * bm.getWidth());
		mScaledHeight = (int) (mScale * bm.getHeight());

		mTranslateDistance = 0;

		initAnimation(mIsLeftAnimation);

		if (mReservationAnimation == true)
		{
			mReservationAnimation = false;

			startAnimation();
		}
	}

	public void startAnimation()
	{
		if (mScaledWidth == 0 && mScaledHeight == 0)
		{
			mReservationAnimation = true;
			return;
		}

		if (mValueAnimator != null)
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

	public void stopAnimation()
	{
		mReservationAnimation = false;
		mAnimationHandler.removeMessages(0);

		if (mValueAnimator != null)
		{
			mValueAnimator.cancel();
			mValueAnimator.removeAllListeners();
		}

		mValueAnimator = null;
	}

	public void initAnimation(boolean isLeftAnimation)
	{
		mIsLeftAnimation = isLeftAnimation;

		ExLog.d("isLeftAnimation : " + isLeftAnimation);

		mTranslateDistance = 0;

		mMatrix = getImageMatrix();
		mMatrix.reset();
		mMatrix.postScale(mScale, mScale);

		if (isLeftAnimation == true)
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
}

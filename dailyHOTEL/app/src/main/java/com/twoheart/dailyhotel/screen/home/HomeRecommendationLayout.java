package com.twoheart.dailyhotel.screen.home;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyTextView;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 1. 17..
 */

public class HomeRecommendationLayout extends LinearLayout
{
    private static final int LAYOUT_ANIMATION_DURATION = 200;
    private static final int MAX_RECOMMENDATION_SIZE = Integer.MAX_VALUE;

    private Context mContext;
    LinearLayout mContentLayout;
    HomeRecommendationListener mListener;
    ValueAnimator mValueAnimator;

    private boolean mIsUseAnimation;
    private int mImageWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    private int mImageHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mExpectedItemHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mTitleLayoutHeight = 0;

    private ArrayList<Recommendation> mRecommendationList;

    public interface HomeRecommendationListener
    {
        void onRecommendationClick(View view, Recommendation recommendation, int position);
    }

    public HomeRecommendationLayout(Context context)
    {
        super(context);

        mContext = context;
        initLayout();
    }

    public HomeRecommendationLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;
        initLayout();
    }

    public HomeRecommendationLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initLayout();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HomeRecommendationLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
        initLayout();
    }

    public void setListener(HomeRecommendationListener listener)
    {
        mListener = listener;
    }

    private void initLayout()
    {
        LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_row_home_recommendation_layout, this);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setBackgroundResource(R.color.white);

        mTitleLayoutHeight = ScreenUtils.dpToPx(mContext, (21 + 15 + 15 + 1)); // title height(반올림) + title top margin + title + bottom margin + bottom divider height
        mImageWidth = ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dpToPx(mContext, 30);
        mImageHeight = ScreenUtils.getRatioHeightType21x9(mImageWidth);
        mExpectedItemHeight = mImageHeight + ScreenUtils.dpToPx(mContext, 78d);

        mContentLayout = view.findViewById(R.id.contentLayout);

        setVisibility(View.GONE);

        clearAll();
    }

    public void setData(ArrayList<Recommendation> list)
    {
        clearAll();

        mRecommendationList = list;

        if (mContentLayout == null || mContext == null)
        {
            return;
        }

        boolean hasData = list != null && list.size() > 0;
        // 데이터가 없는 경우
        if (hasData == false)
        {
            setVisibility(View.GONE);
            return;
        }

        int size = list.size();
        int itemCount = Math.min(size, MAX_RECOMMENDATION_SIZE);
        for (int i = 0; i < itemCount; i++)
        {
            Recommendation recommendation = mRecommendationList.get(i);
            addRecommendationItemView(recommendation, i);
        }

        // 에니메이션을 사용하지 않는 경우
        if (mIsUseAnimation == false)
        {
            setVisibility(View.VISIBLE);
            return;
        }

        // 에니메이션을 사용하는 경우
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout()
            {
                mContentLayout.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        startAnimation(true);
                    }
                });

                if (VersionUtils.isOverAPI16() == true)
                {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else
                {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        setVisibility(View.VISIBLE);
    }

    public void clearAll()
    {
        mRecommendationList = null;

        if (mContentLayout != null && mContentLayout.getChildCount() > 0)
        {
            mContentLayout.removeAllViews();
        }
    }

    public void setUseAnimation(boolean isUse)
    {
        mIsUseAnimation = isUse;
    }

    private void addRecommendationItemView(final Recommendation recommendation, final int position)
    {
        if (recommendation == null)
        {
            return;
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_recommendation_item_layout, null);
        view.setTag(recommendation);

        SimpleDraweeView imageView = view.findViewById(R.id.contentImageView);
        imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        imageView.setTag(imageView.getId(), position);
        imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mImageWidth, mImageHeight);
        imageView.setLayoutParams(layoutParams);

        if (ScreenUtils.getScreenWidth(mContext) < 1440)
        {
            Util.requestImageResize(mContext, imageView, recommendation.lowResolutionImageUrl);
        } else
        {
            Util.requestImageResize(mContext, imageView, recommendation.defaultImageUrl);
        }

        DailyTextView titleView = view.findViewById(R.id.contentTextView);
        DailyTextView descriptionView = view.findViewById(R.id.contentDescriptionView);
        DailyTextView countView = view.findViewById(R.id.contentCountView);

        titleView.setText(recommendation.title);
        descriptionView.setText(recommendation.subtitle);
        countView.setText(mContext.getResources().getString(R.string.label_booking_count, recommendation.countOfItems));

        view.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mListener != null)
                {
                    mListener.onRecommendationClick(v, recommendation, position);
                }
            }
        });

        mContentLayout.addView(view);
    }

    public int getCount()
    {
        if (mRecommendationList == null || mRecommendationList.size() == 0)
        {
            return 0;
        }

        return mRecommendationList.size();
    }

    void setHeight(int height)
    {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null)
        {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        } else
        {
            params.height = height;
        }

        setLayoutParams(params);
    }

    void startAnimation(boolean isShow)
    {
        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            mValueAnimator.cancel();
        }

        int height = getHeight();
        int start = isShow == false ? height : 0;
        int end = isShow == false ? 0 : height;

        mValueAnimator = ValueAnimator.ofInt(start, end);
        mValueAnimator.setDuration(LAYOUT_ANIMATION_DURATION);
        mValueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = value;
                setLayoutParams(params);

                if (isShow == true)
                {
                    float alpha = (float) ((double) value / (double) height);
                    setAlpha(alpha);
                }
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                setHeight(start);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mValueAnimator != null)
                {
                    mValueAnimator.removeAllUpdateListeners();
                    mValueAnimator.removeAllListeners();
                    mValueAnimator = null;
                }

                setHeight(end);

                if (isShow == true)
                {
                    setVisibility(View.VISIBLE);
                } else
                {
                    setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mValueAnimator.start();
    }
}

package com.twoheart.dailyhotel.screen.home;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 1. 17..
 */

public class HomeRecommendationLayout extends LinearLayout
{
    private static final int LAYOUT_ANIMATION_DURATION = 200;
    private static final int MAX_RECOMMENDATION_SIZE = 6;

    private Context mContext;
    private LinearLayout mContentLayout;
    private HomeRecommendationListener mListener;
    private ValueAnimator mValueAnimator;

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
        setVisibility(View.VISIBLE);

        mTitleLayoutHeight = Util.dpToPx(mContext, (21 + 15 + 15 + 1)); // title height(반올림) + title top margin + title + bottom margin + bottom divider height
        mImageWidth = Util.getLCDWidth(mContext) - Util.dpToPx(mContext, 30);
        mImageHeight = Util.getRatioHeightType21x9(mImageWidth);
        mExpectedItemHeight = mImageHeight + Util.dpToPx(mContext, 78d);

        mContentLayout = (LinearLayout) view.findViewById(R.id.contentLayout);

        setHeight(0);

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

        int itemCount = 0;

        if (mRecommendationList != null)
        {
            int size = mRecommendationList.size();
            if (size > 0)
            {
                itemCount = Math.min(size, MAX_RECOMMENDATION_SIZE);

                for (int i = 0; i < itemCount; i++)
                {
                    Recommendation recommendation = mRecommendationList.get(i);
                    addRecommendationItemView(recommendation, i);
                }
            }

            startLayoutHeightAnimation(itemCount);
        }
    }

    public void clearAll()
    {
        mRecommendationList = null;

        if (mContentLayout != null)
        {
            mContentLayout.removeAllViews();
        }
    }

    private void addRecommendationItemView(final Recommendation recommendation, final int position)
    {
        if (recommendation == null)
        {
            return;
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_recommendation_item_layout, null);
        view.setTag(recommendation);

        SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.contentImageView);
        imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        imageView.setTag(imageView.getId(), position);
        imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mImageWidth, mImageHeight);
        imageView.setLayoutParams(layoutParams);

        if (Util.getLCDWidth(mContext) < 1440)
        {
            Util.requestImageResize(mContext, imageView, recommendation.lowResolutionImageUrl);
        } else
        {
            Util.requestImageResize(mContext, imageView, recommendation.defaultImageUrl);
        }

        DailyTextView titleView = (DailyTextView) view.findViewById(R.id.contentTextView);
        DailyTextView descriptionView = (DailyTextView) view.findViewById(R.id.contentDescriptionView);
        DailyTextView countView = (DailyTextView) view.findViewById(R.id.contentCountView);

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

    private void setHeight(int height)
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

    private int getExpectedHeight(int itemCount)
    {
        if (itemCount < 1)
        {
            return 0;
        }

        return mTitleLayoutHeight + (mExpectedItemHeight * itemCount);
    }

    private void startLayoutHeightAnimation(int itemCount)
    {
        int height = getHeight();
        final int expectedHeight = getExpectedHeight(itemCount);

        if (height == expectedHeight)
        {
            return;
        }

        if (mValueAnimator != null)
        {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }

        final int alphaGap = height != 0 && expectedHeight != 0 ? 0 : Math.abs(expectedHeight - height);

        mValueAnimator = ValueAnimator.ofInt(height, expectedHeight);
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

                if (alphaGap != 0)
                {
                    float alpha = (float) ((double) value / (double) alphaGap);
                    setAlpha(alpha);
                }
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                clearAnimation();

                setHeight(expectedHeight);

                mValueAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                clearAnimation();

                setHeight(expectedHeight);

                mValueAnimator = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mValueAnimator.start();
    }
}

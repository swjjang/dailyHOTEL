package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
    private Context mContext;
    private LinearLayout mContentLayout;
    private HomeRecommendationListener mListener;

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_recommendation_layout, this);

        mContentLayout = (LinearLayout) view.findViewById(R.id.contentLayout);
        mContentLayout.removeAllViews();
    }

    public void setData(ArrayList<Recommendation> list)
    {
        clearAll();

        if (list == null || list.size() == 0)
        {
            setVisibility(View.GONE);
            return;
        }

        mRecommendationList = list;

        if (mContentLayout == null || mContext == null)
        {
            setVisibility(View.GONE);
            return;
        }

        if (mRecommendationList == null || mRecommendationList.size() == 0)
        {
            setVisibility(View.GONE);
        } else
        {
            int size = mRecommendationList.size();
            if (size > 0)
            {
                for (int i = 0; i < size; i++)
                {
                    Recommendation recommendation = mRecommendationList.get(i);
                    addRecommendationItemView(recommendation, i);
                }
                setVisibility(View.VISIBLE);
            } else
            {
                setVisibility(View.GONE);
            }

        }
    }

    public void clearAll()
    {
        mRecommendationList = null;

        if (mContentLayout != null)
        {
            mContentLayout.removeAllViews();
        }

        if (getVisibility() != View.VISIBLE)
        {
            setVisibility(View.VISIBLE);
        }
    }

    public void addRecommendationItemView(final Recommendation recommendation, final int position)
    {
        if (recommendation == null)
        {
            return;
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_recommendation_item_layout, null);
        view.setTag(recommendation);

        int width = Util.getLCDWidth(mContext) - Util.dpToPx(mContext, 30);
        int height = Util.getRatioHeightType21x9(width);

        SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.contentImageView);
        imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        imageView.setTag(imageView.getId(), position);
        imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
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
}

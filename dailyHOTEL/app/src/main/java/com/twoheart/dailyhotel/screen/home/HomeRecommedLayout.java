package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.HomeRecommed;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 1. 17..
 */

public class HomeRecommedLayout extends LinearLayout
{
    private Context mContext;
    private LinearLayout mContentLayout;
    private HomeRecommendListener mListener;

    private ArrayList<HomeRecommed> mRecommedList;

    public interface HomeRecommendListener
    {
        void onRecommedClick(HomeRecommed recommed, int position);
    }

    public HomeRecommedLayout(Context context)
    {
        super(context);

        mContext = context;
        initLayout();
    }

    public HomeRecommedLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;
        initLayout();
    }

    public HomeRecommedLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initLayout();
    }

    public HomeRecommedLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
        initLayout();
    }

    public void setListener(HomeRecommendListener listener)
    {
        mListener = listener;
    }

    private void initLayout()
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_recommed_layout, this);

        mContentLayout = (LinearLayout) view.findViewById(R.id.contentLayout);
        mContentLayout.removeAllViews();
    }

    public void setData(ArrayList<HomeRecommed> list, boolean isShow)
    {
        if (list == null || list.size() == 0)
        {
            clearAll();
            return;
        }

        mRecommedList = list;

        if (isShow == true)
        {
            showRecommedView();
        }
    }

    public void clearAll()
    {
        mRecommedList = null;

        if (mContentLayout != null)
        {
            mContentLayout.removeAllViews();
        }
    }

    private void showRecommedView()
    {
        if (mContentLayout == null || mContext == null)
        {
            return;
        }

        mContentLayout.removeAllViews();

        if (mRecommedList == null || mRecommedList.size() == 0)
        {
            // TODO : default loading View or Remove All View
        } else
        {
            int size = mRecommedList.size();
            for (int i = 0; i < size; i++)
            {
                HomeRecommed recommed = mRecommedList.get(i);
                addRecommedItemView(recommed, i);
            }
        }
    }

    public void addRecommedItemView(final HomeRecommed homeRecommed, final int position)
    {
        if (homeRecommed == null)
        {
            return;
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_recommend_item_layout, null);
        view.setTag(homeRecommed);

        int width = Util.getLCDWidth(mContext) - Util.dpToPx(mContext, 30);
        int height = Util.getRatioHeightType21x9(width);

        SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.contentImageView);
        imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        imageView.setTag(imageView.getId(), position);
        imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        imageView.setLayoutParams(layoutParams);

        if (Util.isTextEmpty(homeRecommed.imageUrl) == false)
        {
            Util.requestImageResize(mContext, imageView, homeRecommed.imageUrl);
        }

        DailyTextView titleView = (DailyTextView) view.findViewById(R.id.contentTextView);
        DailyTextView descriptionView = (DailyTextView) view.findViewById(R.id.contentDescriptionView);
        DailyTextView countView = (DailyTextView) view.findViewById(R.id.contentCountView);

        titleView.setText(homeRecommed.title);
        descriptionView.setText(homeRecommed.description);
        countView.setText(mContext.getResources().getString(R.string.label_booking_count, homeRecommed.count));

        view.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mListener != null)
                {
                    mListener.onRecommedClick(homeRecommed, position);
                }
            }
        });

        this.addView(view);
    }


    public int getCount()
    {
        if (mRecommedList == null || mRecommedList.size() == 0)
        {
            return 0;
        }

        return mRecommedList.size();
    }
}

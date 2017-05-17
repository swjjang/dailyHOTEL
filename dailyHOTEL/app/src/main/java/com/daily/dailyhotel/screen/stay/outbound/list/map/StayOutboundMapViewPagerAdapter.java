package com.daily.dailyhotel.screen.stay.outbound.list.map;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.StayOutbound;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ViewpagerColumnStayDataBinding;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class StayOutboundMapViewPagerAdapter extends PagerAdapter
{
    protected Context mContext;
    protected List<StayOutbound> mStayOutboundList;
    protected OnPlaceMapViewPagerAdapterListener mOnPlaceMapViewPagerAdapterListener;

    public interface OnPlaceMapViewPagerAdapterListener
    {
        void onStayClick(View view, StayOutbound stayOutbound);

        void onCloseClick();
    }

    public StayOutboundMapViewPagerAdapter(Context context)
    {
        mContext = context;
        mStayOutboundList = new ArrayList<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mStayOutboundList == null || mStayOutboundList.size() < position)
        {
            return null;
        }

        ViewpagerColumnStayDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.viewpager_column_stay_data, container, false);

        StayOutbound stayOutbound = mStayOutboundList.get(position);

        dataBinding.addressTextView.setText(stayOutbound.locationDescription);
        dataBinding.nameTextView.setText(stayOutbound.name);

        // 가격
        if (stayOutbound.promo == true)
        {
            dataBinding.priceTextView.setVisibility(View.VISIBLE);
            dataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayOutbound.nightlyBaseRateKrw, false));
            dataBinding.priceTextView.setPaintFlags(dataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
        {
            dataBinding.priceTextView.setVisibility(View.INVISIBLE);
            dataBinding.priceTextView.setText(null);
        }

        dataBinding.discountPriceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayOutbound.nightlyRateKrw, false));

        // 만족도
        dataBinding.satisfactionView.setVisibility(View.GONE);

        // 1박인 경우 전체가격과 1박가격이 같다.
        if (stayOutbound.nightlyRateKrw == stayOutbound.totalKrw)
        {
            dataBinding.averageTextView.setVisibility(View.GONE);
        } else
        {
            dataBinding.averageTextView.setVisibility(View.VISIBLE);
        }

        // grade
        dataBinding.gradeTextView.setText(Integer.toString(stayOutbound.rating));
        dataBinding.gradeTextView.setBackgroundResource(Stay.Grade.special.getColorResId());

        Util.requestImageResize(mContext, dataBinding.simpleDraweeView, stayOutbound.hdpiImageUrl);

        if (DailyTextUtils.isTextEmpty(stayOutbound.promoDescription) == false)
        {
            dataBinding.dBenefitLayout.setVisibility(View.VISIBLE);
            dataBinding.dBenefitTextView.setText(stayOutbound.promoDescription);
        } else
        {
            dataBinding.dBenefitLayout.setVisibility(View.GONE);
        }

        dataBinding.nameTextView.setSelected(true); // Android TextView marquee bug

        dataBinding.closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onCloseClick();
                }
            }
        });

        dataBinding.simpleDraweeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onStayClick(v, stayOutbound);
                }
            }
        });

        container.addView(dataBinding.getRoot(), 0);

        return dataBinding.getRoot();
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        if (mStayOutboundList != null)
        {
            return mStayOutboundList.size();
        } else
        {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    public void setData(List<StayOutbound> list)
    {
        if (mStayOutboundList == null)
        {
            mStayOutboundList = new ArrayList<>();
        }

        mStayOutboundList.clear();

        if (list != null)
        {
            mStayOutboundList.addAll(list);
        }
    }

    public StayOutbound getItem(int position)
    {
        if (mStayOutboundList == null || mStayOutboundList.size() == 0 || mStayOutboundList.size() <= position)
        {
            return null;
        }

        return mStayOutboundList.get(position);
    }

    public void clear()
    {
        if (mStayOutboundList == null)
        {
            return;
        }

        mStayOutboundList.clear();
    }

    public void setOnPlaceMapViewPagerAdapterListener(OnPlaceMapViewPagerAdapterListener listener)
    {
        mOnPlaceMapViewPagerAdapterListener = listener;
    }
}

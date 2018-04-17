package com.daily.dailyhotel.screen.home.stay.inbound.list.map;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.view.DailyStayMapCardView;

import java.util.ArrayList;
import java.util.List;

public class StayMapViewPagerAdapter extends PagerAdapter
{
    protected Context mContext;
    protected List<Stay> mStayList;
    protected OnPlaceMapViewPagerAdapterListener mOnPlaceMapViewPagerAdapterListener;

    private boolean mNightsEnabled; // 연박 여부
    private boolean mRewardEnabled;

    public interface OnPlaceMapViewPagerAdapterListener
    {
        void onStayClick(View view, Stay stay);

        void onWishClick();
    }

    public StayMapViewPagerAdapter(Context context)
    {
        mContext = context;
        mStayList = new ArrayList<>();
    }

    public void setNightsEnabled(boolean enabled)
    {
        mNightsEnabled = enabled;
    }

    public void setRewardEnabled(boolean enabled)
    {
        mRewardEnabled = enabled;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mStayList == null || mStayList.size() < position)
        {
            return null;
        }

        DailyStayMapCardView stayMapCardView = new DailyStayMapCardView(mContext);
        Stay stay = getItem(position);

        stayMapCardView.setAddressText(stay.addressSummary);
        stayMapCardView.setStayNameText(stay.name);
        stayMapCardView.setBenefitText(stay.dBenefitText);
        stayMapCardView.setWishVisible(true);
        stayMapCardView.setWish(stay.myWish);

        if (stay.soldOut == true)
        {
            stayMapCardView.setPriceText(0, 0, 0, null, false);
        } else
        {
            stayMapCardView.setPriceText(stay.discountRate, stay.discountPrice, stay.price, stay.couponDiscountText, mNightsEnabled);
        }

        stayMapCardView.setReviewText(stay.satisfaction);
        stayMapCardView.setGradeText(stay.grade.getName(mContext));
        stayMapCardView.setImage(stay.imageUrl);
        stayMapCardView.setStickerVisible(mRewardEnabled && stay.provideRewardSticker);

        stayMapCardView.setOnWishClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onWishClick();
                }
            }
        });

        stayMapCardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onStayClick(stayMapCardView, stay);
                }
            }
        });

        container.addView(stayMapCardView);

        return stayMapCardView;
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        if (mStayList != null)
        {
            return mStayList.size();
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

    public void setData(List<Stay> list)
    {
        if (mStayList == null)
        {
            mStayList = new ArrayList<>();
        }

        mStayList.clear();

        if (list != null)
        {
            mStayList.addAll(list);
        }
    }

    public Stay getItem(int position)
    {
        if (mStayList == null || mStayList.size() == 0 || mStayList.size() <= position)
        {
            return null;
        }

        return mStayList.get(position);
    }

    public void clear()
    {
        if (mStayList == null)
        {
            return;
        }

        mStayList.clear();
    }

    public void setOnPlaceMapViewPagerAdapterListener(OnPlaceMapViewPagerAdapterListener listener)
    {
        mOnPlaceMapViewPagerAdapterListener = listener;
    }
}

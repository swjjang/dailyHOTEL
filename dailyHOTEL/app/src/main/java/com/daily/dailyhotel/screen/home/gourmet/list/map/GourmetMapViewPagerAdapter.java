package com.daily.dailyhotel.screen.home.gourmet.list.map;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.Gourmet;
import com.daily.dailyhotel.view.DailyGourmetMapCardView;

import java.util.ArrayList;
import java.util.List;

public class GourmetMapViewPagerAdapter extends PagerAdapter
{
    protected Context mContext;
    protected List<Gourmet> mGourmetList;
    protected OnPlaceMapViewPagerAdapterListener mOnPlaceMapViewPagerAdapterListener;

    public interface OnPlaceMapViewPagerAdapterListener
    {
        void onGourmetClick(View view, Gourmet gourmet);

        void onWishClick(int position, Gourmet gourmet);
    }

    public GourmetMapViewPagerAdapter(Context context)
    {
        mContext = context;
        mGourmetList = new ArrayList<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mGourmetList == null || mGourmetList.size() < position)
        {
            return null;
        }

        DailyGourmetMapCardView gourmetMapCardView = new DailyGourmetMapCardView(mContext);
        Gourmet gourmet = getItem(position);

        gourmetMapCardView.setAddressText(gourmet.addressSummary);
        gourmetMapCardView.setNameText(gourmet.name);
        gourmetMapCardView.setBenefitText(gourmet.dBenefitText);

        // 인원
        if (gourmet.persons > 0)
        {
            gourmetMapCardView.setPersonVisible(true);
            gourmetMapCardView.setPerson(gourmet.persons);
        } else
        {
            gourmetMapCardView.setPersonVisible(false);
        }

        if (gourmet.soldOut == true)
        {
            gourmetMapCardView.setPriceText(0, 0, 0, null);
        } else
        {
            gourmetMapCardView.setPriceText(gourmet.discountRate, gourmet.discountPrice, gourmet.price, gourmet.couponDiscountText);
        }

        gourmetMapCardView.setReviewText(gourmet.rating);

        if (DailyTextUtils.isTextEmpty(gourmet.subCategory) == false)
        {
            gourmetMapCardView.setGradeText(gourmet.subCategory);
        } else
        {
            gourmetMapCardView.setGradeText(gourmet.category);
        }

        gourmetMapCardView.setImage(gourmet.imageUrl);

        gourmetMapCardView.setOnCloseClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onWishClick(position, gourmet);
                }
            }
        });

        gourmetMapCardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnPlaceMapViewPagerAdapterListener != null)
                {
                    mOnPlaceMapViewPagerAdapterListener.onGourmetClick(gourmetMapCardView, gourmet);
                }
            }
        });

        container.addView(gourmetMapCardView);

        return gourmetMapCardView;
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        if (mGourmetList != null)
        {
            return mGourmetList.size();
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

    public void setData(List<Gourmet> list)
    {
        if (mGourmetList == null)
        {
            mGourmetList = new ArrayList<>();
        }

        mGourmetList.clear();

        if (list != null)
        {
            mGourmetList.addAll(list);
        }
    }

    public Gourmet getItem(int position)
    {
        if (mGourmetList == null || mGourmetList.size() == 0 || mGourmetList.size() <= position)
        {
            return null;
        }

        return mGourmetList.get(position);
    }

    public void clear()
    {
        if (mGourmetList == null)
        {
            return;
        }

        mGourmetList.clear();
    }

    public void setOnPlaceMapViewPagerAdapterListener(OnPlaceMapViewPagerAdapterListener listener)
    {
        mOnPlaceMapViewPagerAdapterListener = listener;
    }
}

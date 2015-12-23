package com.twoheart.dailyhotel.screen.hotellist;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;
import com.twoheart.dailyhotel.model.EventBanner;

import java.util.ArrayList;
import java.util.List;

public class EventBannerViewPagerAdapter extends PagerAdapter
{
    private Context mContext;
    private List<EventBanner> mEventBannerList;

    public EventBannerViewPagerAdapter(Context context, List<EventBanner> list)
    {
        mContext = context;

        if (list != null)
        {
            mEventBannerList = new ArrayList<>();
            mEventBannerList.addAll(list);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mEventBannerList == null)
        {
            return null;
        }

        final ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ScaleType.CENTER_CROP);
        imageView.setTag(imageView.getId(), position);

        if (mEventBannerList.size() > position)
        {
            String url = mEventBannerList.get(position).link;
            Glide.with(mContext).load(url).crossFade().into(imageView);
        }

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(imageView, layoutParams);

        return imageView;
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        return mEventBannerList.size();
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
}

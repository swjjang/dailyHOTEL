package com.twoheart.dailyhotel.screen.hotellist;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.twoheart.dailyhotel.model.EventBanner;

import java.util.ArrayList;
import java.util.List;

public class EventBannerViewPagerAdapter extends PagerAdapter
{
    private Context mContext;
    private List<EventBanner> mEventBannerList;
    private View.OnClickListener mOnClickListener;

    public EventBannerViewPagerAdapter(Context context, List<EventBanner> list, View.OnClickListener listener)
    {
        mContext = context;

        if (list != null)
        {
            mEventBannerList = new ArrayList<>();
            mEventBannerList.addAll(list);
        }

        mOnClickListener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mEventBannerList == null)
        {
            return null;
        }

        com.facebook.drawee.view.SimpleDraweeView imageView = new com.facebook.drawee.view.SimpleDraweeView(mContext);
        imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        imageView.setTag(imageView.getId(), position);

        if (mEventBannerList.size() > position)
        {
            String url = mEventBannerList.get(position).imageUrl;
            imageView.setImageURI(Uri.parse(url));
        }

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(imageView, layoutParams);

        imageView.setOnClickListener(mOnClickListener);

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

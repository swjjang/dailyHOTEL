package com.daily.dailyhotel.screen.home.stay.inbound.detail;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.facebook.drawee.drawable.ScalingUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.util.Util;

import java.util.List;

public class StayDetailImageViewPagerAdapter extends PagerAdapter
{
    private Context mContext;
    private List<DetailImageInformation> mImageList;

    public StayDetailImageViewPagerAdapter(Context context)
    {
        mContext = context;
    }

    public void setData(List<DetailImageInformation> imageList)
    {
        mImageList = imageList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        final int width = ScreenUtils.getScreenWidth(mContext);
        final int height = PlaceDetailLayout.getImageLayoutHeight(mContext);
        final com.facebook.drawee.view.SimpleDraweeView imageView = new com.facebook.drawee.view.SimpleDraweeView(mContext);

        if (mImageList == null || mImageList.size() == 0 || position < 0)
        {
            imageView.setBackgroundResource(R.color.default_background);
            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            imageView.setTag(imageView.getId(), position);
            imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
            container.addView(imageView, 0, layoutParams);

            return imageView;
        }

        if (position < mImageList.size())
        {
            imageView.setBackgroundResource(R.color.default_background);
            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            imageView.setTag(imageView.getId(), position);
            imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            Util.requestImageResize(mContext, imageView, mImageList.get(position).url);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
            container.addView(imageView, 0, layoutParams);
        } else
        {
            Util.restartApp(mContext);
        }

        return imageView;
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    public DetailImageInformation getImageInformation(int position)
    {
        if (mImageList == null)
        {
            return null;
        }

        return mImageList.get(position);
    }

    @Override
    public int getCount()
    {
        if (mImageList != null)
        {
            if (mImageList.size() == 0)
            {
                return 1;
            } else
            {
                return mImageList.size();
            }
        } else
        {
            return 1;
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
}

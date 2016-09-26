package com.twoheart.dailyhotel.place.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.util.Util;

import java.util.List;

public class PlaceDetailImageViewPagerAdapter extends PagerAdapter
{
    private Context mContext;
    private List<ImageInformation> mImageInformationList;

    public PlaceDetailImageViewPagerAdapter(Context context)
    {
        mContext = context;
    }

    public void setData(List<ImageInformation> list)
    {
        mImageInformationList = list;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        final int width = Util.getLCDWidth(mContext);
        final com.facebook.drawee.view.SimpleDraweeView imageView = new com.facebook.drawee.view.SimpleDraweeView(mContext);

        if (mImageInformationList == null || mImageInformationList.size() == 0 || position < 0)
        {
            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            imageView.setTag(imageView.getId(), position);
            imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, width);
            container.addView(imageView, 0, layoutParams);

            return imageView;
        }

        if (position < mImageInformationList.size())
        {
            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            imageView.setTag(imageView.getId(), position);
            imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            Util.requestImageResize(mContext, imageView, mImageInformationList.get(position).url);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, width);
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

    @Override
    public int getCount()
    {
        if (mImageInformationList != null)
        {
            if (mImageInformationList.size() == 0)
            {
                return 1;
            } else
            {
                return mImageInformationList.size();
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

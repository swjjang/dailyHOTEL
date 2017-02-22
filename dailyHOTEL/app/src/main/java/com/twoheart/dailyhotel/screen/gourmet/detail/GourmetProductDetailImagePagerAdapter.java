package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.ProductImageInformation;
import com.twoheart.dailyhotel.util.Util;

import java.util.List;

public class GourmetProductDetailImagePagerAdapter extends PagerAdapter
{
    private Context mContext;
    private List<ProductImageInformation> mImageInformationList;
    private View.OnClickListener mOnClickListener;

    public GourmetProductDetailImagePagerAdapter(Context context)
    {
        mContext = context;
    }

    public void setData(List<ProductImageInformation> list, View.OnClickListener listener)
    {
        mImageInformationList = list;
        mOnClickListener = listener;
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
            imageView.setOnClickListener(null);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, width);
            container.addView(imageView, 0, layoutParams);

            return imageView;
        }

        if (position < mImageInformationList.size())
        {
            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            imageView.setTag(imageView.getId(), position);
            imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            Util.requestImageResize(mContext, imageView, mImageInformationList.get(position).imageUrl);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, width);
            container.addView(imageView, 0, layoutParams);

            imageView.setOnClickListener(mOnClickListener);
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

    public ProductImageInformation getImageInformation(int position)
    {
        if (mImageInformationList == null)
        {
            return null;
        }

        return mImageInformationList.get(position);
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

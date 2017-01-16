package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.util.Util;

import java.util.List;

/**
 * Created by android_sam on 2017. 1. 13..
 * placeDetail image Viewpager 참고
 */

public class HomeEventImageViewPagerAdapter extends PagerAdapter
{
    public static final String DEFAULT_EVENT_IMAGE_URL = "defaultImageUrl";

    private Context mContext;
    private List<ImageInformation> mImageList;

    public HomeEventImageViewPagerAdapter(Context context)
    {
        mContext = context;
    }

    public void setData(List<ImageInformation> list)
    {
        mImageList = list;
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
    public Object instantiateItem(ViewGroup container, int position)
    {
        final int width = Util.getLCDWidth(mContext);
        final int height = Util.getRatioHeightType16x9(width);

        final SimpleDraweeView imageView = new SimpleDraweeView(mContext);

        if (mImageList == null || mImageList.size() == 0 || position < 0)
        {
            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            imageView.setTag(imageView.getId(), position);
            imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
            container.addView(imageView, 0, layoutParams);

            return imageView;
        }

        if (position < mImageList.size())
        {
            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            imageView.setTag(imageView.getId(), position);
            imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            String url = mImageList.get(position).url;
            if (DEFAULT_EVENT_IMAGE_URL.equalsIgnoreCase(url) == true)
            {
                // TODO : R.drawable.banner 의 경우 임시 테스트로 들어간 이미지로 1월 30일 이후에 growth 에서 전달받은 이미지로 적용해야 함
                imageView.setImageResource(R.drawable.banner);
            } else
            {
                Util.requestImageResize(mContext, imageView, url);
            }

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
            container.addView(imageView, 0, layoutParams);
        } else
        {
            Util.restartApp(mContext);
        }

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }
}

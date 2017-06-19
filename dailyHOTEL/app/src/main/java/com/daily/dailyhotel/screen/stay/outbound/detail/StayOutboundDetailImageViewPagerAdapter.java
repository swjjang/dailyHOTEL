package com.daily.dailyhotel.screen.stay.outbound.detail;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.StayOutboundDetailImage;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.util.Util;

import java.io.IOException;
import java.util.List;

public class StayOutboundDetailImageViewPagerAdapter extends PagerAdapter
{
    private Context mContext;
    private List<StayOutboundDetailImage> mImageList;

    public StayOutboundDetailImageViewPagerAdapter(Context context)
    {
        mContext = context;
    }

    public void setData(List<StayOutboundDetailImage> imageList)
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

            StayOutboundDetailImage stayOutboundDetailImage = mImageList.get(position);

            if (stayOutboundDetailImage == null || stayOutboundDetailImage.getImageMap() == null)
            {
                return imageView;
            }

            ImageMap imageMap = stayOutboundDetailImage.getImageMap();
            String url;

            if (ScreenUtils.getScreenWidth(mContext) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
            {
                if (DailyTextUtils.isTextEmpty(imageMap.bigUrl) == true)
                {
                    url = imageMap.smallUrl;
                } else
                {
                    url = imageMap.bigUrl;
                }
            } else
            {
                if (DailyTextUtils.isTextEmpty(imageMap.mediumUrl) == true)
                {
                    url = imageMap.smallUrl;
                } else
                {
                    url = imageMap.mediumUrl;
                }
            }

            ControllerListener controllerListener = new BaseControllerListener<ImageInfo>()
            {
                @Override
                public void onFailure(String id, Throwable throwable)
                {
                    if (throwable instanceof IOException == true)
                    {
                        if (url.equalsIgnoreCase(imageMap.bigUrl) == true)
                        {
                            imageMap.bigUrl = null;
                        } else if (url.equalsIgnoreCase(imageMap.mediumUrl) == true)
                        {
                            imageMap.mediumUrl = null;
                        } else
                        {
                            // 작은 이미지를 로딩했지만 실패하는 경우.
                            return;
                        }

                        imageView.setImageURI(imageMap.smallUrl);
                    }
                }
            };

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
            container.addView(imageView, 0, layoutParams);

            DraweeController draweeController = Fresco.newDraweeControllerBuilder()//
                .setControllerListener(controllerListener).setUri(url).build();

            imageView.setController(draweeController);
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

    public StayOutboundDetailImage getImageInformation(int position)
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

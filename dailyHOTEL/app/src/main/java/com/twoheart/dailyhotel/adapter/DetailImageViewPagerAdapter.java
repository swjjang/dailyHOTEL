package com.twoheart.dailyhotel.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.util.Util;

import java.util.List;

public class DetailImageViewPagerAdapter extends PagerAdapter
{
    private Context mContext;
    private List<ImageInformation> mImageInformationList;

    public DetailImageViewPagerAdapter(Context context)
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
        if (mImageInformationList == null || position < 0)
        {
            return null;
        }

        final int width = Util.getLCDWidth(mContext);
        final com.facebook.drawee.view.SimpleDraweeView imageView = new com.facebook.drawee.view.SimpleDraweeView(mContext);

        if (position < mImageInformationList.size())
        {
            Uri imageUri = Util.isTextEmpty(mImageInformationList.get(position).url) == true ? null : Uri.parse(mImageInformationList.get(position).url);

            if (mImageInformationList.size() == 1)
            {
                final int height = Util.dpToPx(mContext, 202);

                DraweeController controller = Fresco.newDraweeControllerBuilder().setControllerListener(new BaseControllerListener<ImageInfo>()
                {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable)
                    {
                        if (imageInfo == null)
                        {
                            return;
                        }

                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                        layoutParams.height = width;
                        imageView.setLayoutParams(layoutParams);
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable)
                    {
                        super.onFailure(id, throwable);

                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                        layoutParams.height = width;
                        imageView.setLayoutParams(layoutParams);
                    }
                }).setUri(imageUri).build();

                imageView.setController(controller);
                imageView.setTag(imageView.getId(), position);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, height);
                container.addView(imageView, 0, layoutParams);
            } else
            {
                imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
                imageView.setTag(imageView.getId(), position);

                Util.requestImageResize(mContext, imageView, mImageInformationList.get(position).url);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, width);
                container.addView(imageView, 0, layoutParams);
            }
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

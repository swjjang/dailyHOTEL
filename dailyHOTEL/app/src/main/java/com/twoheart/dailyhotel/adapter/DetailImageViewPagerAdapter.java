package com.twoheart.dailyhotel.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.twoheart.dailyhotel.activity.HotelDetailActivity;
import com.twoheart.dailyhotel.activity.PlaceDetailActivity;
import com.twoheart.dailyhotel.util.FileLruCache;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.AnimationImageView;

import java.io.File;
import java.util.List;

public class DetailImageViewPagerAdapter extends PagerAdapter
{
    private Context mContext;
    private List<String> mImageUrlList;
    private int mDirection;

    private HotelDetailActivity.OnUserActionListener mOnUserActionListener;
    private PlaceDetailActivity.OnImageActionListener mOnImageActionListener;

    public DetailImageViewPagerAdapter(Context context)
    {
        mContext = context;
    }

    public void setData(List<String> list)
    {
        mImageUrlList = list;
    }

    public void setDirection(int direction)
    {
        mDirection = direction;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mImageUrlList == null)
        {
            return null;
        }

        final ImageView imageView;

        int width = Util.getLCDWidth(mContext);

        if (Util.isOverAPI11() == true)
        {
            imageView = new AnimationImageView(mContext, width, width, mDirection < 0);
            ((AnimationImageView) imageView).setOnAnimationListener(mOnUserActionListener);
            ((AnimationImageView) imageView).setOnImageActionListener(mOnImageActionListener);
        } else
        {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ScaleType.CENTER_CROP);
        }

        imageView.setTag(imageView.getId(), position);

        String url = mImageUrlList.get(position);
        String imageFileName = FileLruCache.getInstance().get(url);
        boolean isExist = false;

        if (Util.isTextEmpty(imageFileName) == false)
        {
            File file = new File(Glide.getPhotoCacheDir(mContext).getAbsoluteFile() + "/" + imageFileName);

            if (file.isFile() == true && file.exists() == true)
            {
                imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                isExist = true;
            }
        }

        if (isExist == false)
        {
            RequestListener<String, GlideDrawable> glideDrawableRequestListener = new RequestListener<String, GlideDrawable>()
            {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource)
                {
                    imageView.setImageDrawable(null);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
                {
                    return false;
                }
            };

            SimpleTarget<GlideDrawable> glideDrawableSimpleTarget = new SimpleTarget<GlideDrawable>()
            {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation)
                {
                    imageView.setImageDrawable(resource);
                }
            };

            if (Util.getLCDWidth(mContext) < 720)
            {
                Glide.with(mContext).load(url).override(360, 240).listener(glideDrawableRequestListener).into(glideDrawableSimpleTarget);
            } else
            {
                Glide.with(mContext).load(url).listener(glideDrawableRequestListener).into(glideDrawableSimpleTarget);
            }
        }

        LayoutParams layoutParams = new LayoutParams(width, width);
        container.addView(imageView, 0, layoutParams);

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
        if (mImageUrlList != null)
        {
            if (mImageUrlList.size() == 0)
            {
                return 1;
            } else
            {
                return mImageUrlList.size();
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

    public void setOnAnimationListener(HotelDetailActivity.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    public void setOnImageActionListener(PlaceDetailActivity.OnImageActionListener listener)
    {
        mOnImageActionListener = listener;
    }
}

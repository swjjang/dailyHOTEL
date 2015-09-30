package com.twoheart.dailyhotel.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.twoheart.dailyhotel.activity.HotelDetailActivity;
import com.twoheart.dailyhotel.activity.PlaceDetailActivity;
import com.twoheart.dailyhotel.util.DrawableLruCache;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.AnimationImageView;

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

        Drawable drawable = DrawableLruCache.getInstance().get(url);

        if (drawable != null)
        {
            imageView.setImageDrawable(drawable);
        } else
        {
            if (Util.getLCDWidth(mContext) < 720)
            {
                Glide.with(mContext).load(url).asBitmap().override(360, 240).listener(new RequestListener<String, Bitmap>()
                {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource)
                    {
                        imageView.setImageBitmap(null);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource)
                    {
                        return false;
                    }
                }).into(imageView);
            } else
            {
                Glide.with(mContext).load(url).asBitmap().listener(new RequestListener<String, Bitmap>()
                {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource)
                    {
                        imageView.setImageBitmap(null);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource)
                    {
                        return false;
                    }
                }).into(imageView);
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

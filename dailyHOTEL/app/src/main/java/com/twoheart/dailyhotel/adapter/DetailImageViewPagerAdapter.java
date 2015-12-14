package com.twoheart.dailyhotel.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;
import com.twoheart.dailyhotel.activity.HotelDetailActivity;
import com.twoheart.dailyhotel.activity.PlaceDetailActivity;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.util.FileLruCache;
import com.twoheart.dailyhotel.util.Util;

import java.io.File;
import java.util.List;

public class DetailImageViewPagerAdapter extends PagerAdapter
{
    private Context mContext;
    private List<ImageInformation> mImageInformationList;

    private HotelDetailActivity.OnUserActionListener mOnUserActionListener;
    private PlaceDetailActivity.OnImageActionListener mOnImageActionListener;

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
        if (mImageInformationList == null)
        {
            return null;
        }

        int width = Util.getLCDWidth(mContext);

        final ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ScaleType.CENTER_CROP);
        imageView.setTag(imageView.getId(), position);

        String url = mImageInformationList.get(position).url;
        String imageFilePath = FileLruCache.getInstance().get(url);
        boolean isExist = false;

        if (Util.isTextEmpty(imageFilePath) == false)
        {
            File file = new File(imageFilePath);

            if (file.isFile() == true && file.exists() == true)
            {
                imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                isExist = true;
            }
        }

        if (isExist == false)
        {
            if (Util.getLCDWidth(mContext) < 720)
            {
                Glide.with(mContext).load(url).override(360, 240).crossFade().into(imageView);
            } else
            {
                Glide.with(mContext).load(url).crossFade().into(imageView);
            }
        }

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, width);
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

    public void setOnAnimationListener(HotelDetailActivity.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    public void setOnImageActionListener(PlaceDetailActivity.OnImageActionListener listener)
    {
        mOnImageActionListener = listener;
    }
}

package com.twoheart.dailyhotel.place.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daily.base.util.ScreenUtils;
import com.facebook.imagepipeline.nativecode.NativeBlurFilter;
import com.twoheart.dailyhotel.R;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseBlurLayout extends BaseLayout
{
    protected ImageView mBlurImageView;

    public BaseBlurLayout(Context context, @NonNull OnBaseEventListener listener)
    {
        super(context, listener);
    }

    public boolean getBlurVisibility()
    {
        if (mBlurImageView == null)
        {
            return false;
        }

        return mBlurImageView.getVisibility() == View.VISIBLE;
    }

    public void setBlurVisibility(Activity activity, boolean visible)
    {
        if (activity == null)
        {
            return;
        }

        if (visible == true)
        {
            if (mBlurImageView == null)
            {
                mBlurImageView = new ImageView(activity);
                activity.getWindow().addContentView(mBlurImageView//
                    , new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                mBlurImageView.setImageResource(R.color.black_a40);
                mBlurImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            mBlurImageView.setVisibility(View.VISIBLE);

            Observable.just(ScreenUtils.takeScreenShot(activity)).subscribeOn(Schedulers.io()).map(bitmap ->
            {
                if (bitmap == null)
                {
                    return null;
                }

                NativeBlurFilter.iterativeBoxBlur(bitmap, 2, 60);

                return bitmap;
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(bitmap ->
            {
                if (bitmap == null)
                {
                    return;
                }

                mBlurImageView.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
            });
        } else
        {
            if (mBlurImageView != null)
            {
                mBlurImageView.setBackgroundDrawable(null);
                mBlurImageView.setVisibility(View.GONE);
            }
        }
    }
}

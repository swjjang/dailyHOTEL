package com.twoheart.dailyhotel.place.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.facebook.imagepipeline.nativecode.NativeBlurFilter;
import com.twoheart.dailyhotel.R;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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

            Bitmap bitmap = ScreenUtils.takeScreenShot(activity);

            if (bitmap == null)
            {
                mBlurImageView.setImageResource(R.color.black_a75);
            } else
            {
                Observable.just(ScreenUtils.takeScreenShot(activity)).subscribeOn(Schedulers.io()).map(new Function<Bitmap, Bitmap>()
                {
                    @Override
                    public Bitmap apply(@io.reactivex.annotations.NonNull Bitmap bitmap) throws Exception
                    {
                        try
                        {
                            NativeBlurFilter.iterativeBoxBlur(bitmap, 2, 60);
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }

                        return bitmap;
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Bitmap>()
                {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Bitmap bitmap) throws Exception
                    {
                        mBlurImageView.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        if (mBlurImageView != null)
                        {
                            mBlurImageView.setBackgroundDrawable(null);
                            mBlurImageView.setVisibility(View.GONE);
                        }
                    }
                });
            }
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

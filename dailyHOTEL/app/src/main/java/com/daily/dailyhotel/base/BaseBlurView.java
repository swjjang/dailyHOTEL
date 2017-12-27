package com.daily.dailyhotel.base;

import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.facebook.imagepipeline.nativecode.NativeBlurFilter;
import com.twoheart.dailyhotel.R;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseBlurView<T1 extends OnBaseEventListener, T2 extends ViewDataBinding> extends BaseDialogView<T1, T2> implements BaseBlurViewInterface
{
    ImageView mBlurImageView;

    public BaseBlurView(BaseActivity activity, T1 listener)
    {
        super(activity, listener);
    }

    @Override
    public boolean isBlurVisible()
    {
        if (mBlurImageView == null)
        {
            return false;
        }

        return mBlurImageView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setBlurVisible(BaseActivity activity, boolean visible)
    {
        if (activity == null)
        {
            return;
        }

        if (visible == true)
        {
            activity.setScreenTouchEnabled(false);

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
                        mBlurImageView.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
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

            activity.setScreenTouchEnabled(true);
        }
    }
}

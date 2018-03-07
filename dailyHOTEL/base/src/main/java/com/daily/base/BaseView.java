package com.daily.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.daily.base.util.VersionUtils;

public abstract class BaseView<T1 extends OnBaseEventListener, T2 extends ViewDataBinding> implements BaseViewInterface
{
    private BaseActivity mActivity;
    private T2 mViewDataBinding;
    private T1 mOnEventListener;

    protected abstract void setContentView(T2 viewDataBinding);

    public BaseView(BaseActivity activity, T1 listener)
    {
        if (activity == null || listener == null)
        {
            throw new NullPointerException();
        }

        mActivity = activity;
        mOnEventListener = listener;
    }

    @Override
    public final void setContentView(int layoutResID)
    {
        if (layoutResID != 0)
        {
            mViewDataBinding = DataBindingUtil.setContentView(mActivity, layoutResID);
        }

        setContentView(mViewDataBinding);
    }

    @Override
    public final void setContentView(int layoutResID, ViewGroup viewGroup)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), layoutResID, viewGroup, false);

        setContentView(mViewDataBinding);
    }

    protected void setVisibility(int visibility)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.getRoot().setVisibility(visibility);
    }

    protected @NonNull
    Context getContext()
    {
        return mActivity;
    }

    protected T2 getViewDataBinding()
    {
        return mViewDataBinding;
    }

    protected @NonNull
    T1 getEventListener()
    {
        return mOnEventListener;
    }

    protected Window getWindow()
    {
        return mActivity.getWindow();
    }

    protected int getColor(int resId)
    {
        if (VersionUtils.isOverAPI23() == true)
        {
            return mActivity.getColor(resId);
        } else
        {
            return mActivity.getResources().getColor(resId);
        }
    }

    protected ColorStateList getColorStateList(int resId)
    {
        if (VersionUtils.isOverAPI23() == true)
        {
            return mActivity.getColorStateList(resId);
        } else
        {
            return mActivity.getResources().getColorStateList(resId);
        }
    }

    protected String getString(int resId)
    {
        return mActivity.getString(resId);
    }

    protected String getString(int resId, Object... formatArgs)
    {
        return mActivity.getString(resId, formatArgs);
    }

    protected Drawable getDrawable(int id)
    {
        if (VersionUtils.isOverAPI21() == true)
        {
            return mActivity.getDrawable(id);
        } else
        {
            return mActivity.getResources().getDrawable(id);
        }
    }

    protected int getDimensionPixelSize(int id)
    {
        return mActivity.getResources().getDimensionPixelSize(id);
    }

    protected int getDpi()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.densityDpi;
    }

    @NonNull
    BaseActivity getActivity()
    {
        return mActivity;
    }

    protected View getCurrentFocus()
    {
        return mActivity == null ? null : mActivity.getCurrentFocus();
    }

    protected Fragment findFragmentById(int id)
    {
        return mActivity.getSupportFragmentManager().findFragmentById(id);
    }

    protected FragmentManager getSupportFragmentManager()
    {
        return mActivity.getSupportFragmentManager();
    }

    interface VersionApi23
    {
        int getColor(int resId);

        ColorStateList getColorStateList(int resId);
    }

    interface VersionApi21
    {
        Drawable getDrawable(int id);
    }

    interface VersionApi15
    {
        int getColor(int resId);

        ColorStateList getColorStateList(int resId);

        Drawable getDrawable(int id);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setStatusBarColor(int color)
    {
        if (VersionUtils.isOverAPI21() == true)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}

package com.daily.base;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.daily.base.util.VersionUtils;

public abstract class BaseFragmentView<T1 extends OnBaseEventListener, T2 extends ViewDataBinding> implements BaseFragmentViewInterface
{
    private BaseActivity mActivity;
    private T2 mViewDataBinding;
    private T1 mOnEventListener;

    protected abstract void setContentView(T2 viewDataBinding);

    public BaseFragmentView(T1 listener)
    {
        if (listener == null)
        {
            throw new NullPointerException();
        }

        mOnEventListener = listener;
    }

    @Override
    public final View getContentView(LayoutInflater layoutInflater, int layoutResID, ViewGroup viewGroup)
    {
        mViewDataBinding = DataBindingUtil.inflate(layoutInflater, layoutResID, viewGroup, false);

        return mViewDataBinding.getRoot();
    }

    @Override
    public void setActivity(BaseActivity activity)
    {
        mActivity = activity;

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

    @NonNull
    BaseActivity getActivity()
    {
        return mActivity;
    }

    protected Fragment findFragmentById(int id)
    {
        return mActivity.getSupportFragmentManager().findFragmentById(id);
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
}

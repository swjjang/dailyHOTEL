package com.daily.base;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.daily.base.util.VersionUtils;

public abstract class BaseSubView<T1 extends OnBaseSubEventListener, T2 extends ViewDataBinding> implements BaseSubViewInterface
{
    private Context mContext;
    private T2 mViewDataBinding;
    private T1 mOnEventListener;

    protected abstract void setContentView(T2 viewDataBinding);

    public BaseSubView(@NonNull Context context, T1 listener)
    {
        if (context == null || listener == null)
        {
            throw new NullPointerException();
        }

        mContext = context;
        mOnEventListener = listener;
    }

    @Override
    public final void setContentView(int layoutResID, ViewGroup viewGroup, boolean attachToParent)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), layoutResID, viewGroup, attachToParent);

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
        return mContext;
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

    protected int getColor(int resId)
    {
        return mContext.getResources().getColor(resId);
    }

    protected ColorStateList getColorStateList(int resId)
    {
        return mContext.getResources().getColorStateList(resId);
    }

    protected String getString(int resId)
    {
        return mContext.getString(resId);
    }

    protected String getString(int resId, Object... formatArgs)
    {
        return mContext.getString(resId, formatArgs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected Drawable getDrawable(int id)
    {
        if (VersionUtils.isOverAPI21() == true)
        {
            return mContext.getDrawable(id);
        } else
        {
            return mContext.getResources().getDrawable(id);
        }
    }

    protected int getDimensionPixelSize(int id)
    {
        return mContext.getResources().getDimensionPixelSize(id);
    }
}

package com.daily.base;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

public abstract class BasePresenter<T1 extends BaseActivity, T2 extends BaseViewInterface> implements BaseActivityInterface, OnBaseEventListener
{
    private BaseActivity mActivity;

    private BaseViewInterface mOnViewInterface;

    public BasePresenter(BaseActivity activity)
    {
        mActivity = activity;

        createInstanceViewInterface();
    }

    protected abstract
    @NonNull
    BaseViewInterface createInstanceViewInterface();

    public abstract void setOnAnalyticsListener(OnBaseAnalyticsListener listener);

    public T1 getActivity()
    {
        return (T1) mActivity;
    }

    public void setContentView(@LayoutRes int layoutResID)
    {
        if (mOnViewInterface == null)
        {
            throw new NullPointerException("mOnViewInterface is null");
        } else
        {
            getActivity().setContentView(mOnViewInterface.onCreateView(layoutResID));
        }
    }

    public
    @NonNull
    T2 getViewInterface()
    {
        if (mOnViewInterface == null)
        {
            mOnViewInterface = createInstanceViewInterface();
        }

        return (T2) mOnViewInterface;
    }
}

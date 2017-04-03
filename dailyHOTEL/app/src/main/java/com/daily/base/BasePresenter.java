package com.daily.base;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

public abstract class BasePresenter<T1 extends BaseActivity, T2 extends BaseViewInterface> implements BaseActivityInterface
{
    private T1 mActivity;

    private T2 mOnViewInterface;

    public BasePresenter(@NonNull T1 activity)
    {
        mActivity = activity;

        createInstanceViewInterface();

        initialize(activity);
    }

    protected abstract
    @NonNull
    T2 createInstanceViewInterface();

    public abstract void initialize(T1 activity);

    public abstract void setOnAnalyticsListener(OnBaseAnalyticsInterface listener);

    public T1 getActivity()
    {
        return mActivity;
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

        return mOnViewInterface;
    }
}

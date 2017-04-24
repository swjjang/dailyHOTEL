package com.twoheart.dailyhotel.place.base;

import android.view.ViewGroup;

public abstract class BaseMenuNavigationFragment extends BaseFragment
{
    public interface OnScreenScrollChangeListener
    {
        void onScrollChange(ViewGroup scrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY);

        /**
         * @param disabled true 이면 스크로를 멈춘다.
         */
        void onScrollState(boolean disabled);
    }

    protected OnScreenScrollChangeListener mOnScreenScrollChangeListener;

    public abstract void setOnScrollChangedListener(BaseMenuNavigationFragment.OnScreenScrollChangeListener listener);

    public abstract void scrollTop();

    public BaseMenuNavigationFragment()
    {
    }
}

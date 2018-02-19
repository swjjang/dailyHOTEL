package com.daily.dailyhotel.base;

import com.daily.base.BaseFragment;
import com.daily.base.OnBaseFragmentEventListener;

public abstract class BasePagerFragment<T1 extends BasePagerFragmentPresenter, T2 extends OnBaseFragmentEventListener> extends BaseFragment<T1, T2>
{
    public BasePagerFragment()
    {
        super();
    }

    public void onSelected()
    {
        if (mPresenter != null)
        {
            mPresenter.onSelected();
        }
    }

    public void onUnselected()
    {
        if (mPresenter != null)
        {
            mPresenter.onUnselected();
        }
    }

    public void onRefresh()
    {
        if (mPresenter != null)
        {
            mPresenter.onRefresh();
        }
    }

    public void scrollTop()
    {
        if (mPresenter != null)
        {
            mPresenter.scrollTop();
        }
    }

    public boolean onBackPressed()
    {
        if (mPresenter != null)
        {
            return mPresenter.onBackPressed();
        }

        return false;
    }
}

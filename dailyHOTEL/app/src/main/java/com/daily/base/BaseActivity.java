package com.daily.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity<T1 extends BasePresenter> extends AppCompatActivity
{
    private BasePresenter mPresenter;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mFragmentManager = getSupportFragmentManager();

        createInstancePresenter();

        mPresenter.onIntent(getIntent());
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID)
    {
        if (mPresenter != null)
        {
            mPresenter.setContentView(layoutResID);
        }
    }

    protected abstract
    @NonNull
    BasePresenter createInstancePresenter();

    public
    @NonNull
    T1 getPresenter()
    {
        if (mPresenter == null)
        {
            mPresenter = createInstancePresenter();
        }

        return (T1) mPresenter;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mPresenter != null)
        {
            mPresenter.onResume();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if (mPresenter != null)
        {
            mPresenter.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        if (mPresenter != null)
        {
            mPresenter.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (mPresenter != null)
        {
            mPresenter.onPause();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mPresenter != null)
        {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mPresenter != null)
        {
            if (mPresenter.onBackPressed() == false)
            {
                super.onBackPressed();
            }
        } else
        {
            super.onBackPressed();
        }
    }

    protected void setAnalyticsListener(OnBaseAnalyticsListener listener)
    {
        if (listener == null || mPresenter == null)
        {
            return;
        }

        mPresenter.setOnAnalyticsListener(listener);
    }

    /**
     * Fragment 컨테이너의 표시되는 Fragment를 변경할 때 Fragment 컨테이너에 적재된 Fragment들을 정리한다.
     */
    private void clearFragmentBackStack()
    {
        if (mFragmentManager == null)
        {
            return;
        }

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); ++i)
        {
            mFragmentManager.popBackStackImmediate();
        }

        fragmentTransaction.commitAllowingStateLoss();
    }

    public void replaceFragment(@IdRes int containerViewId, Fragment fragment, @Nullable String tag)
    {
        if (isFinishing() == true || mFragmentManager == null)
        {
            return;
        }

        try
        {
            clearFragmentBackStack();

            mFragmentManager.beginTransaction().replace(containerViewId, fragment, tag).commitAllowingStateLoss();
        } catch (Exception e)
        {

        }
    }

    public void addFragment(@IdRes int containerViewId, Fragment fragment, @Nullable String tag)
    {
        if (isFinishing() == true || mFragmentManager == null)
        {
            return;
        }

        try
        {
            mFragmentManager.beginTransaction().add(containerViewId, fragment, tag).commitAllowingStateLoss();
        } catch (Exception e)
        {

        }
    }
}

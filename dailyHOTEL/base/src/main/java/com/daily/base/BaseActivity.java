package com.daily.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity<T1 extends BasePresenter> extends AppCompatActivity
{
    public static final String INTENT_EXTRA_DATA_DEEPLINK = "deepLink";

    private BasePresenter mPresenter;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mFragmentManager = getSupportFragmentManager();

        mPresenter = createInstancePresenter();

        if (mPresenter.onIntent(getIntent()) == false)
        {
            finish();
            return;
        }

        mPresenter.onIntentAfter();
    }

    protected abstract
    @NonNull
    T1 createInstancePresenter();

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
    protected void onStart()
    {
        super.onStart();

        if (mPresenter != null)
        {
            mPresenter.onStart();
        }
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
        if (mPresenter != null)
        {
            mPresenter.onSaveInstanceState(outState);
        }

        super.onSaveInstanceState(outState);
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

    @Override
    public void finish()
    {
        super.finish();

        if (mPresenter != null)
        {
            mPresenter.onFinish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (mPresenter != null)
        {
            mPresenter.onActivityResult(requestCode, resultCode, data);
        }
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

    public void removeFragment(Fragment fragment)
    {
        if (isFinishing() == true || mFragmentManager == null || fragment == null)
        {
            return;
        }

        try
        {
            mFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        } catch (Exception e)
        {

        }
    }
}

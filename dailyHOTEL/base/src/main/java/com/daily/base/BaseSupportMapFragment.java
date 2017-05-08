package com.daily.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseSupportMapFragment<T1 extends BaseFragmentPresenter> extends com.google.android.gms.maps.SupportMapFragment
{
    private BaseFragmentPresenter mPresenter;

    public BaseSupportMapFragment()
    {
        mPresenter = createInstancePresenter();
    }

    protected abstract
    @NonNull
    T1 createInstancePresenter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (mPresenter != null)
        {
            mPresenter.onStart();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mPresenter != null)
        {
            mPresenter.onResume();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mPresenter != null)
        {
            mPresenter.onPause();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mPresenter != null)
        {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (mPresenter != null)
        {
            mPresenter.onActivityCreated(savedInstanceState);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (mPresenter != null)
        {
            mPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();

        System.gc();
    }
}

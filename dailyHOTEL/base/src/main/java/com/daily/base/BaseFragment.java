package com.daily.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

public abstract class BaseFragment<T1 extends BaseFragmentPresenter, T2 extends OnBaseFragmentEventListener> extends Fragment
{
    protected T1 mPresenter;
    private T2 mOnFragmentEventListener;

    private PublishSubject<Boolean> mCompleteCreatedSubject;

    public BaseFragment()
    {
        mCompleteCreatedSubject = PublishSubject.create();
        mPresenter = createInstancePresenter();
    }

    protected abstract @NonNull
    T1 createInstancePresenter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return mPresenter.onCreateView(inflater, container, savedInstanceState);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (mPresenter != null)
        {
            mPresenter.onViewCreated(view, savedInstanceState);
        }

        if (mCompleteCreatedSubject != null)
        {
            mCompleteCreatedSubject.onNext(true);
            mCompleteCreatedSubject.onComplete();
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

    protected T2 getFragmentEventListener()
    {
        return mOnFragmentEventListener;
    }

    public void setOnFragmentEventListener(T2 listener)
    {
        mOnFragmentEventListener = listener;
    }

    public Observable getCompleteCreatedObservable()
    {
        return Observable.just(mCompleteCreatedSubject).subscribeOn(AndroidSchedulers.mainThread());
    }
}

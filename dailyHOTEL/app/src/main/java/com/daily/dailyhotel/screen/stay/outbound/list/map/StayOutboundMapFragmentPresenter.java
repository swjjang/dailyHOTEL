package com.daily.dailyhotel.screen.stay.outbound.list.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseFragment;
import com.daily.base.BaseFragmentInterface;
import com.daily.base.BaseFragmentPresenter;
import com.daily.base.BasePresenter;
import com.daily.base.BaseSupportMapFragment;
import com.daily.base.BaseViewInterface;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class StayOutboundMapFragmentPresenter extends BaseFragmentPresenter<StayOutboundMapFragment, StayOutboundMapViewInterface>
{
    public StayOutboundMapFragmentPresenter(@NonNull StayOutboundMapFragment fragment)
    {
        super(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }

    @NonNull
    @Override
    protected StayOutboundMapViewInterface createInstanceViewInterface()
    {
        return null;
    }

    @Override
    public void initialize()
    {

    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {

    }

    @Override
    protected void onHandleError(Throwable throwable)
    {

    }
}

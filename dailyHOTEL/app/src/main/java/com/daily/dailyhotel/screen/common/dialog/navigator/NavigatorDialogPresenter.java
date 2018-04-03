package com.daily.dailyhotel.screen.common.dialog.navigator;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.parcel.analytics.NavigatorAnalyticsParam;
import com.twoheart.dailyhotel.util.Util;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class NavigatorDialogPresenter extends BaseExceptionPresenter<NavigatorDialogActivity, NavigatorDialogInterface> implements NavigatorDialogView.OnEventListener
{
    private NavigatorDialogAnalyticsInterface mAnalytics;

    private boolean mOverseas;
    private double mLatitude;
    private double mLongitude;
    private String mTitle;

    public interface NavigatorDialogAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(NavigatorAnalyticsParam analyticsParam);

        void onEventKakaoMapClick(Activity activity);

        void onEventNaverMapClick(Activity activity);

        void onEventGoogleMapClick(Activity activity);

        void onEventTMapMapClick(Activity activity);

        void onEventKakaoNaviClick(Activity activity);
    }

    public NavigatorDialogPresenter(@NonNull NavigatorDialogActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected NavigatorDialogInterface createInstanceViewInterface()
    {
        return new NavigatorDialogView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(NavigatorDialogActivity activity)
    {
        setContentView(0);

        mAnalytics = new NavigatorDialogAnalyticsImpl();

        setRefresh(false);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mTitle = intent.getStringExtra(NavigatorDialogActivity.INTENT_EXTRA_DATA_TITLE);
        mLatitude = intent.getDoubleExtra(NavigatorDialogActivity.INTENT_EXTRA_DATA_LATITUDE, 0.0d);
        mLongitude = intent.getDoubleExtra(NavigatorDialogActivity.INTENT_EXTRA_DATA_LONGITUDE, 0.0d);
        mOverseas = intent.getBooleanExtra(NavigatorDialogActivity.INTENT_EXTRA_DATA_OVERSEAS, false);

        mAnalytics.setAnalyticsParam(intent.getParcelableExtra(NavigatorDialogActivity.INTENT_EXTRA_DATA_ANALYTICS));

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        if (mOverseas == true)
        {
            getViewInterface().showNavigatorOutboundDialog();
        } else
        {
            getViewInterface().showNavigatorInboundDialog(Util.isSktNetwork(getActivity()));
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onKakaoMapClick()
    {
        Util.shareDaumMap(getActivity(), Double.toString(mLatitude), Double.toString(mLongitude));

        mAnalytics.onEventKakaoMapClick(getActivity());
    }

    @Override
    public void onNaverMapClick()
    {
        Util.shareNaverMap(getActivity(), mTitle, Double.toString(mLatitude), Double.toString(mLongitude));

        mAnalytics.onEventNaverMapClick(getActivity());
    }

    @Override
    public void onGoogleMapClick()
    {
        Util.shareGoogleMap(getActivity(), mTitle, Double.toString(mLatitude), Double.toString(mLongitude));

        mAnalytics.onEventGoogleMapClick(getActivity());
    }

    @Override
    public void onTMapMapClick()
    {
        Util.shareTMapNavi(getActivity(), mTitle, (float) mLatitude, (float) mLongitude);

        mAnalytics.onEventTMapMapClick(getActivity());
    }

    @Override
    public void onKakaoNaviClick()
    {
        Util.shareKakaoNavi(getActivity(), mTitle, Double.toString(mLatitude), Double.toString(mLongitude));

        mAnalytics.onEventKakaoNaviClick(getActivity());
    }
}

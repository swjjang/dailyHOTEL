package com.daily.dailyhotel.screen.home.stay.outbound.filter;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundFilterPresenter extends BaseExceptionPresenter<StayOutboundFilterActivity, StayOutboundFilterViewInterface> implements StayOutboundFilterView.OnEventListener
{
    private StayOutboundFilterAnalyticsInterface mAnalytics;
    private StayOutboundFilters mStayOutboundFilters;
    private StayOutboundFilters.SortType mPrevSortType;
    private boolean[] mEnabledLines;

    private DailyLocationExFactory mDailyLocationExFactory;

    public interface StayOutboundFilterAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onSortClick(Activity activity, StayOutboundFilters.SortType sortType);

        void onRatingClick(Activity activity, int rating);
    }

    public StayOutboundFilterPresenter(@NonNull StayOutboundFilterActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundFilterViewInterface createInstanceViewInterface()
    {
        return new StayOutboundFilterView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundFilterActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_filter_data);

        setAnalytics(new StayOutboundFilterAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundFilterAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (mStayOutboundFilters == null)
        {
            mStayOutboundFilters = new StayOutboundFilters();
        }

        try
        {
            mStayOutboundFilters.sortType = StayOutboundFilters.SortType.valueOf(intent.getStringExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_SORT));
        } catch (Exception e)
        {
            mStayOutboundFilters.sortType = StayOutboundFilters.SortType.RECOMMENDATION;
        }

        mStayOutboundFilters.rating = intent.getIntExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_RATING, -1);
        mEnabledLines = intent.getBooleanArrayExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_ENABLEDLINES);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.activity_curation_title));
        getViewInterface().setSort(mStayOutboundFilters.sortType);
        getViewInterface().setRating(mStayOutboundFilters.rating);
        getViewInterface().setEnabledLines(mEnabledLines);
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

        if (mDailyLocationExFactory != null)
        {
            mDailyLocationExFactory.stopLocationMeasure();
        }
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

        switch (requestCode)
        {
            case StayOutboundFilterActivity.REQUEST_CODE_STAYOUTBOUND_PERMISSION_MANAGER:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        checkLocationManager();
                        break;

                    default:
                        onReverseSort();
                        break;
                }
                break;
            }

            case StayOutboundFilterActivity.REQUEST_CODE_STAYOUTBOUND_SETTING_LOCATION:
                checkLocationManager();
                break;
        }
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
    public void onSortClick(StayOutboundFilters.SortType sortType)
    {
        if (sortType == null || lock() == true)
        {
            return;
        }

        if (mPrevSortType != null && mPrevSortType != sortType)
        {
            mAnalytics.onSortClick(getActivity(), sortType);
        }

        mPrevSortType = mStayOutboundFilters.sortType;
        mStayOutboundFilters.sortType = sortType;

        if (sortType == StayOutboundFilters.SortType.DISTANCE)
        {
            checkLocationManager();
        } else
        {
            unLockAll();
        }
    }

    @Override
    public void onRatingClick(int rating)
    {
        mStayOutboundFilters.rating = rating;

        mAnalytics.onRatingClick(getActivity(), rating);
    }

    @Override
    public void onResetClick()
    {
        if (mStayOutboundFilters == null)
        {
            mStayOutboundFilters = new StayOutboundFilters();
        }

        mStayOutboundFilters.sortType = StayOutboundFilters.SortType.RECOMMENDATION;
        mStayOutboundFilters.rating = -1;

        getViewInterface().setSort(mStayOutboundFilters.sortType);
        getViewInterface().setRating(mStayOutboundFilters.rating);
    }

    @Override
    public void onResultClick()
    {
        Intent intent = new Intent();
        intent.putExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_SORT, mStayOutboundFilters.sortType.name());
        intent.putExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_RATING, mStayOutboundFilters.rating);

        setResult(Activity.RESULT_OK, intent);
        onBackClick();
    }

    void onReverseSort()
    {
        mStayOutboundFilters.sortType = mPrevSortType;
        getViewInterface().setSort(mStayOutboundFilters.sortType);
    }

    private void checkLocationManager()
    {
        if (mDailyLocationExFactory == null)
        {
            mDailyLocationExFactory = new DailyLocationExFactory(getActivity());
        }

        mDailyLocationExFactory.checkLocationMeasure(new DailyLocationExFactory.OnCheckLocationListener()
        {
            @Override
            public void onRequirePermission()
            {
                unLockAll();

                requestLocationPermission();
            }

            @Override
            public void onFailed()
            {
                unLockAll();

                onReverseSort();
                DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
            }

            @Override
            public void onProviderDisabled()
            {
                unLockAll();

                onReverseSort();
                requestLocationProvider();
            }

            @Override
            public void onProviderEnabled()
            {
                unLockAll();


            }
        });
    }

    void requestLocationPermission()
    {
        Intent intent = PermissionManagerActivity.newInstance(getActivity(), PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
        startActivityForResult(intent, StayOutboundFilterActivity.REQUEST_CODE_STAYOUTBOUND_PERMISSION_MANAGER);
    }

    void requestLocationProvider()
    {
        getViewInterface().showSimpleDialog(getString(R.string.dialog_title_used_gps)//
            , getString(R.string.dialog_msg_used_gps), //
            getString(R.string.dialog_btn_text_dosetting), //
            getString(R.string.dialog_btn_text_cancel), //
            new View.OnClickListener()//
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, StayOutboundFilterActivity.REQUEST_CODE_STAYOUTBOUND_SETTING_LOCATION);
                }
            }, new View.OnClickListener()//
            {
                @Override
                public void onClick(View v)
                {
                    DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                }
            }, new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                }
            }, null, true);
    }
}

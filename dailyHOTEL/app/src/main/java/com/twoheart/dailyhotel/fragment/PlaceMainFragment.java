package com.twoheart.dailyhotel.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.EventWebActivity;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class PlaceMainFragment extends BaseFragment
{
    protected SaleTime mTodaySaleTime;
    protected VIEW_TYPE mViewType = VIEW_TYPE.LIST;
    protected boolean mMapEnabled;

    protected boolean mMenuEnabled;
    protected boolean mDontReloadAtOnResume;

    public enum VIEW_TYPE
    {
        LIST,
        MAP,
        GONE, // 목록이 비어있는 경우.
    }

    public abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract void onNavigationItemSelected(Province province, boolean isSelectionTop);

    public abstract void setNavigationItemSelected(Province province);

    public abstract void requestRegionList(BaseActivity baseActivity);

    public abstract void refreshList(Province province, boolean isSelectionTop);

    public abstract void refreshAll();

    public abstract void selectPlace(int index, long dailyTime, int dailyDayOfDays, int nights);

    public abstract void makeTabLayout();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mViewType = VIEW_TYPE.LIST;

        mTodaySaleTime = new SaleTime();

        View view = createView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);//프래그먼트 내에서 옵션메뉴를 지정하기 위해

        setMenuEnabled(false);

        return view;
    }

    @Override
    public void onResume()
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        if (mDontReloadAtOnResume == true)
        {
            mDontReloadAtOnResume = false;
        } else
        {
            lockUI();
            DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, baseActivity);
        }

        super.onResume();
    }

    public void setMenuEnabled(boolean enabled)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || (enabled == true && mMapEnabled == false) || mMenuEnabled == enabled)
        {
            return;
        }

        mMenuEnabled = enabled;

        baseActivity.invalidateOptionsMenu();
    }

    protected void deepLinkGourmetDetail(BaseActivity baseActivity)
    {
        try
        {
            // 신규 타입의 화면이동
            int fnbIndex = Integer.parseInt(DailyDeepLink.getInstance().getIndex());
            long dailyTime = mTodaySaleTime.getDailyTime();
            int nights = Integer.parseInt(DailyDeepLink.getInstance().getNights());

            String date = DailyDeepLink.getInstance().getDate();
            SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
            Date schemeDate = format.parse(date);
            Date dailyDate = format.parse(mTodaySaleTime.getDayOfDaysDateFormat("yyyyMMdd"));

            int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

            if (dailyDayOfDays < 0)
            {
                throw new NullPointerException("dailyDayOfDays < 0");
            }

            selectPlace(fnbIndex, dailyTime, dailyDayOfDays, nights);

            DailyDeepLink.getInstance().clear();
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            //탭에 들어갈 날짜를 만든다.
            makeTabLayout();

            // 지역 리스트를 가져온다
            requestRegionList(baseActivity);
        }
    }

    private void deepLinkEventBannerWeb(BaseActivity baseActivity)
    {
        String url = DailyDeepLink.getInstance().getUrl();
        DailyDeepLink.getInstance().clear();

        if (Util.isTextEmpty(url) == false)
        {
            Intent intent = EventWebActivity.newInstance(baseActivity, url);
            startActivity(intent);
        } else
        {
            //탭에 들어갈 날짜를 만든다.
            makeTabLayout();

            // 지역 리스트를 가져온다
            requestRegionList(baseActivity);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NetworkActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        private boolean processDeepLink(BaseActivity baseActivity)
        {
            if (DailyDeepLink.getInstance().isGourmetDetailView() == true)
            {
                unLockUI();
                deepLinkGourmetDetail(baseActivity);
                return true;
            } else if (DailyDeepLink.getInstance().isGourmetEventBannerWebView() == true)
            {
                unLockUI();
                deepLinkEventBannerWeb(baseActivity);
                return true;
            } else
            {
                // 더이상 진입은 없다.
                if (DailyDeepLink.getInstance().isGourmetListView() == false//
                    && DailyDeepLink.getInstance().isGourmetRegionListView() == false)
                {
                    DailyDeepLink.getInstance().clear();
                }
            }

            return false;
        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            try
            {
                mTodaySaleTime.setCurrentTime(response.getLong("currentDateTime"));
                mTodaySaleTime.setDailyTime(response.getLong("dailyDateTime"));

                if (DailyDeepLink.getInstance().isValidateLink() == true //
                    && processDeepLink(baseActivity) == true)
                {

                } else
                {
                    //탭에 들어갈 날짜를 만든다.
                    makeTabLayout();

                    // 지역 리스트를 가져온다
                    requestRegionList(baseActivity);
                }
            } catch (Exception e)
            {
                onError(e);
                unLockUI();
            }
        }
    };
}

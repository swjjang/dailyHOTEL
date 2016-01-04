package com.twoheart.dailyhotel.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.DailyPreference;
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

    public abstract void activityResult(int requestCode, int resultCode, Intent data);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        unLockUI();

        switch (requestCode)
        {
            // 지역을 선택한 후에 되돌아 온경우.
            case CODE_REQUEST_ACTIVITY_REGIONLIST:
            {
                mDontReloadAtOnResume = true;

                if (resultCode == Activity.RESULT_OK)
                {
                    if (data != null)
                    {
                        if (data.hasExtra(NAME_INTENT_EXTRA_DATA_PROVINCE) == true)
                        {
                            Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);

                            setNavigationItemSelected(province);

                            refreshAll();
                        } else if (data.hasExtra(NAME_INTENT_EXTRA_DATA_AREA) == true)
                        {
                            Province province = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_AREA);

                            setNavigationItemSelected(province);

                            refreshAll();
                        }
                    }
                }
                break;
            }

            default:
            {
                activityResult(requestCode, resultCode, data);
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NetworkActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
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

                String deepLink = DailyPreference.getInstance(baseActivity).getDeepLink();

                if (Util.isTextEmpty(deepLink) == false)
                {
                    DailyPreference.getInstance(baseActivity).removeDeepLink();

                    unLockUI();

                    try
                    {
                        String previousType = Util.getValueForLinkUrl(deepLink, "fnbIndex");

                        if (Util.isTextEmpty(previousType) == false)
                        {
                            // 이전 타입의 화면 이동
                            int fnbIndex = Integer.parseInt(previousType);
                            long dailyTime = Long.parseLong(Util.getValueForLinkUrl(deepLink, "dailyTime"));
                            int dailyDayOfDays = Integer.parseInt(Util.getValueForLinkUrl(deepLink, "dailyDayOfDays"));
                            int nights = Integer.parseInt(Util.getValueForLinkUrl(deepLink, "nights"));

                            if (dailyDayOfDays < 0)
                            {
                                throw new NullPointerException("dailyDayOfDays < 0");
                            }

                            selectPlace(fnbIndex, dailyTime, dailyDayOfDays, nights);
                        } else
                        {
                            // 신규 타입의 화면이동
                            int fnbIndex = Integer.parseInt(Util.getValueForLinkUrl(deepLink, "idx"));
                            long dailyTime = mTodaySaleTime.getDailyTime();
                            int nights = Integer.parseInt(Util.getValueForLinkUrl(deepLink, "nights"));

                            String date = Util.getValueForLinkUrl(deepLink, "date");
                            SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
                            Date schemeDate = format.parse(date);
                            Date dailyDate = format.parse(mTodaySaleTime.getDayOfDaysHotelDateFormat("yyyyMMdd"));

                            int dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

                            if (dailyDayOfDays < 0)
                            {
                                throw new NullPointerException("dailyDayOfDays < 0");
                            }

                            selectPlace(fnbIndex, dailyTime, dailyDayOfDays, nights);
                        }
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());

                        //탭에 들어갈 날짜를 만든다.
                        makeTabLayout();

                        // 지역 리스트를 가져온다
                        requestRegionList(baseActivity);
                    }
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

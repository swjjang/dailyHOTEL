package com.twoheart.dailyhotel.screen.search.gourmet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.fragment.PlaceSearchFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceSearchNetworkController;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.search.gourmet.result.GourmetSearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

public class GourmetSearchFragment extends PlaceSearchFragment
{
    private SaleTime mSaleTime;

    private Handler mAnalyticsHandler;

    @Override
    protected void initContents()
    {
        super.initContents();

        mAnalyticsHandler = new AnalyticsHandler(this);

        if (mSaleTime == null)
        {
            Util.restartApp(mBaseActivity);
        } else
        {
            setDateText(mSaleTime);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mSaleTime == null)
        {
            Util.restartApp(mBaseActivity);
        } else
        {
            setDateText(mSaleTime);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_ACTIVITY_CALENDAR:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    SaleTime saleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);

                    setDateText(saleTime);

                    mPlaceSearchLayout.requestUpdateAutoCompleteLayout();
                }

                mShowSearchKeyboard = true;
                break;
            }
        }
    }

    @Override
    protected PlaceSearchLayout getPlaceSearchLayout(Context context)
    {
        return new GourmetSearchLayout(context, mOnEventListener);
    }

    @Override
    protected PlaceSearchNetworkController getPlaceSearchNetworkController(Context context)
    {
        return new GourmetSearchNetworkController(mBaseActivity, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected String getRecentSearches()
    {
        return DailyPreference.getInstance(mBaseActivity).getGourmetRecentSearches();
    }

    @Override
    protected void writeRecentSearches(String text)
    {
        DailyPreference.getInstance(mBaseActivity).setGourmetRecentSearches(text);
    }

    @Override
    protected PlaceSearchLayout getLayout()
    {
        return new GourmetSearchLayout(mBaseActivity, mOnEventListener);
    }

    @Override
    protected void onSearch(Location location)
    {
        if (mIsScrolling == true)
        {
            return;
        }

        Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mSaleTime, location);
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
    }

    @Override
    public void startSearchResultActivity()
    {
        if (mIsScrolling == true)
        {
            return;
        }

        String text = mPlaceSearchLayout.getSearchKeyWord();

        if (Util.isTextEmpty(text) == true)
        {
            return;
        }

        Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mSaleTime, text);
        startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
    }

    @Override
    public void setSaleTime(SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
    {
        mSaleTime = checkInSaleTime;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(mBaseActivity).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_SEARCH);
    }

    private void setDateText(SaleTime saleTime)
    {
        if (saleTime == null || mPlaceSearchLayout == null)
        {
            return;
        }

        mSaleTime = saleTime;

        mPlaceSearchLayout.setDataText(saleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OnEventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private PlaceSearchLayout.OnEventListener mOnEventListener = new PlaceSearchLayout.OnEventListener()
    {
        @Override
        public void onResetKeyword()
        {
            if (mIsScrolling == true)
            {
                return;
            }

            mPlaceSearchLayout.resetSearchKeyword();

            //            AnalyticsManager.getInstance(GourmetSearchActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
            //                , AnalyticsManager.Action.GOURMET_KEYWORD_RESET_CLICKED, AnalyticsManager.Label.SEARCH_KEYWORD_RESET, null);
        }

        @Override
        public void onSearchMyLocation()
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = PermissionManagerActivity.newInstance(mBaseActivity, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
        }

        @Override
        public void onDeleteRecentSearches()
        {
            if (mIsScrolling == true)
            {
                return;
            }

            mDailyRecentSearches.clear();
            DailyPreference.getInstance(mBaseActivity).setGourmetRecentSearches("");

            mPlaceSearchLayout.updateRecentSearchesLayout(null);

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                , AnalyticsManager.Action.GOURMET_KEYWORD_HISTORY_DELETED, AnalyticsManager.Label.DELETE_ALL_KEYWORDS, null);
        }

        @Override
        public void onAutoCompleteKeyword(String keyword)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (mSaleTime == null)
            {
                Util.restartApp(mBaseActivity);
                return;
            }

            ((GourmetSearchNetworkController) mPlaceSearchNetworkController).requestAutoComplete(mSaleTime, keyword);
        }

        @Override
        public void onSearch(String text)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (Util.isTextEmpty(text) == true)
            {
                return;
            }

            Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mSaleTime, text);
            startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
        }

        @Override
        public void onSearch(String text, Keyword keyword)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (keyword == null)
            {
                return;
            }

            if (keyword.price < 0)
            {
                Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mSaleTime, keyword, Constants.SearchType.RECENT);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            } else
            {
                Intent intent = GourmetSearchResultActivity.newInstance(mBaseActivity, mSaleTime, text, keyword, Constants.SearchType.AUTOCOMPLETE);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCHRESULT);
            }
        }

        @Override
        public void onCalendarClick(boolean isAnimation)
        {
            if (mIsScrolling == true)
            {
                return;
            }

            if (isAnimation == true)
            {
                AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.SEARCH, null);
            }

            Intent intent = GourmetCalendarActivity.newInstance(mBaseActivity, mSaleTime, AnalyticsManager.ValueType.SEARCH, true, isAnimation);
            startActivityForResult(intent, REQUEST_ACTIVITY_CALENDAR);
        }

        @Override
        public void onSearchEnabled(boolean enabled)
        {
            if (mOnSearchFragmentListener == null)
            {
                return;
            }

            mOnSearchFragmentListener.onSearchEnabled(enabled);
        }

        @Override
        public void finish()
        {
            mBaseActivity.finish();

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                , AnalyticsManager.Action.GOURMET_SEARCH_BACK_BUTTON_CLICKED, AnalyticsManager.Label.KEYWORD_BACK_BUTTON_CLICKED, null);
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OnNetworkControllerListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private GourmetSearchNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new GourmetSearchNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onResponseAutoComplete(String keyword, List<Keyword> list)
        {
            if (isFinishing() == true)
            {
                return;
            }

            mAnalyticsHandler.removeMessages(0);

            if (list != null && list.size() == 0)
            {
                Message message = mAnalyticsHandler.obtainMessage(0, keyword);
                mAnalyticsHandler.sendMessageDelayed(message, 1000);
            }

            mPlaceSearchLayout.updateAutoCompleteLayout(keyword, list);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            unLockUI();
            mBaseActivity.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            unLockUI();
            mBaseActivity.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            unLockUI();
            mBaseActivity.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            unLockUI();
            mBaseActivity.onErrorToastMessage(message);
        }
    };

    private static class AnalyticsHandler extends Handler
    {
        private final WeakReference<GourmetSearchFragment> mWeakReference;

        public AnalyticsHandler(GourmetSearchFragment activity)
        {
            mWeakReference = new WeakReference<>(activity);
        }

        private String getSearchDate(GourmetSearchFragment gourmetSearchFragment)
        {
            String saleDate = gourmetSearchFragment.mSaleTime.getDayOfDaysDateFormat("yyMMdd");

            //            Calendar calendar = Calendar.getInstance();
            //            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmm", Locale.KOREA);
            //
            //            return String.format("%s-%s", saleDate, simpleDateFormat.format(calendar.getTime()));
            return String.format("%s-%s", saleDate, DailyCalendar.format(new Date(), "yyMMddHHmm"));
        }

        @Override
        public void handleMessage(Message msg)
        {
            GourmetSearchFragment gourmetSearchFragment = mWeakReference.get();

            if (gourmetSearchFragment == null)
            {
                return;
            }

            String label = String.format("%s-%s", msg.obj, getSearchDate(gourmetSearchFragment));
            AnalyticsManager.getInstance(gourmetSearchFragment.getContext()).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                , AnalyticsManager.Action.GOURMET_AUTOCOMPLETED_KEYWORD_NOTMATCHED, label, null);
        }
    }
}

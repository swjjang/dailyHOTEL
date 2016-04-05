package com.twoheart.dailyhotel.screen.hotel.search;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.screen.hotel.detail.HotelDetailActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HotelSearchResultActivity extends PlaceSearchResultActivity
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_NIGHTS = "nights";
    private static final String INTENT_EXTRA_DATA_LOCATION = "location";
    private static final String INTENT_EXTRA_DATA_SEARCHTYPE = "searcyType";

    private static final int COUNT_PER_TIMES = 30;

    public static final int SEARCHTYPE_SEARCHES = 0;
    public static final int SEARCHTYPE_AUTOCOMPLETE = 1;
    public static final int SEARCHTYPE_RECENT = 2;
    public static final int SEARCHTYPE_LOCATION = 3;

    private SaleTime mSaleTime;
    private int mNights;
    private Keyword mKeyword;
    private Location mLocation;
    private String mCustomerSatisfactionTimeMessage;

    private int mOffset, mTotalCount;
    private int mSearchType;
    private HotelSearchResultNetworkController mNetworkController;

    public static Intent newInstance(Context context, SaleTime saleTime, int nights, Keyword keyword, int searchType)
    {
        Intent intent = new Intent(context, HotelSearchResultActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_NIGHTS, nights);
        intent.putExtra(INTENT_EXTRA_DATA_KEYWORD, keyword);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, searchType);

        return intent;
    }

    public static Intent newInstance(Context context, SaleTime saleTime, int nights, String text)
    {
        return newInstance(context, saleTime, nights, new Keyword(0, text), SEARCHTYPE_SEARCHES);
    }

    public static Intent newInstance(Context context, SaleTime saleTime, int nights, Location location)
    {
        Intent intent = new Intent(context, HotelSearchResultActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_NIGHTS, nights);
        intent.putExtra(INTENT_EXTRA_DATA_LOCATION, location);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, SEARCHTYPE_LOCATION);

        return intent;
    }

    @Override
    protected PlaceSearchResultLayout getLayout()
    {
        return new HotelSearchResultLayout(this, mOnEventListener);
    }

    @Override
    protected void initIntent(Intent intent)
    {
        mSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALETIME);
        mNights = intent.getIntExtra(INTENT_EXTRA_DATA_NIGHTS, 1);

        if (intent.hasExtra(INTENT_EXTRA_DATA_KEYWORD) == true)
        {
            mKeyword = intent.getParcelableExtra(INTENT_EXTRA_DATA_KEYWORD);
        } else if (intent.hasExtra(INTENT_EXTRA_DATA_LOCATION) == true)
        {
            mLocation = intent.getParcelableExtra(INTENT_EXTRA_DATA_LOCATION);
        }

        mSearchType = intent.getIntExtra(INTENT_EXTRA_DATA_SEARCHTYPE, SEARCHTYPE_SEARCHES);
        mOffset = 0;
    }

    @Override
    protected void initContents()
    {
        super.initContents();

        String checkInDate = mSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd");
        SaleTime checkOutSaleTime = mSaleTime.getClone(mSaleTime.getOffsetDailyDay() + mNights);
        String checkOutDate = checkOutSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd");

        if (mSearchType == SEARCHTYPE_LOCATION)
        {
            mPlaceSearchResultLayout.setToolbarText("", String.format("%s - %s", checkInDate, checkOutDate));
        } else
        {
            mPlaceSearchResultLayout.setToolbarText(mKeyword.name, String.format("%s - %s", checkInDate, checkOutDate));
        }

        mNetworkController = new HotelSearchResultNetworkController(this, mNetworkTag, mOnNetworkControllerListener);
        mNetworkController.requestCustomerSatisfactionTimeMessage();
    }

    @Override
    protected void requestSearchResultList()
    {
        if ((mOffset > 0 && mOffset >= mTotalCount) || mOffset == -1)
        {
            return;
        }

        if (mOffset == 0)
        {
            lockUI();
        }

        if (mSearchType == SEARCHTYPE_LOCATION)
        {
            mNetworkController.requestSearchResultList(mSaleTime, mNights, mLocation, mOffset, COUNT_PER_TIMES);
        } else
        {
            mNetworkController.requestSearchResultList(mSaleTime, mNights, mKeyword.name, mOffset, COUNT_PER_TIMES);
        }
    }

    @Override
    protected Keyword getKeyword()
    {
        return mKeyword;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnEventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private PlaceSearchResultLayout.OnEventListener mOnEventListener = new PlaceSearchResultLayout.OnEventListener()
    {
        @Override
        public void finish()
        {
            // 사용하지 않음
        }

        @Override
        public void finish(int resultCode)
        {
            HotelSearchResultActivity.this.finish(resultCode);

            if (resultCode == Constants.CODE_RESULT_ACTIVITY_HOME)
            {
                AnalyticsManager.getInstance(HotelSearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , AnalyticsManager.Action.HOTEL_SEARCH_RESULT_CANCELED, AnalyticsManager.Label.SEARCH_RESULT_CANCELED, null);
            } else
            {
                AnalyticsManager.getInstance(HotelSearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , AnalyticsManager.Action.HOTEL_SEARCH_BACK_BUTTON_CLICKED, AnalyticsManager.Label.RESULT_BACK_BUTTON_CLICKED, null);
            }
        }

        @Override
        public void research(int resultCode)
        {
            HotelSearchResultActivity.this.finish(resultCode);

            AnalyticsManager.getInstance(HotelSearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                , AnalyticsManager.Action.HOTEL_SEARCH_AGAIN_CLICKED, AnalyticsManager.Label.HOTEL_SEARCH_AGAIN_CLICKED, null);
        }

        @Override
        public void onItemClick(PlaceViewItem placeViewItem)
        {
            if (placeViewItem == null || placeViewItem.getType() != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            Hotel hotel = placeViewItem.<Hotel>getItem();

            Intent intent = new Intent(HotelSearchResultActivity.this, HotelDetailActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mSaleTime);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, hotel.index);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, hotel.nights);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, hotel.name);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, hotel.imageUrl);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);
        }

        @Override
        public void onShowCallDialog()
        {
            showCallDialog(mCustomerSatisfactionTimeMessage);

            AnalyticsManager.getInstance(HotelSearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                , AnalyticsManager.Action.CALL_INQUIRY_CLICKED, AnalyticsManager.Label.CALL_KEYWORD_HOTEL, null);
        }

        @Override
        public void onLoadMoreList()
        {
            requestSearchResultList();
        }

        @Override
        public void onShowProgressBar()
        {
            lockUI();
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnNetworkControllerListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HotelSearchResultNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new HotelSearchResultNetworkController.OnNetworkControllerListener()
    {
        private String mAddress;
        private int mSize = -100;

        private String getSearchDate()
        {
            String checkInDate = mSaleTime.getDayOfDaysDateFormat("yyMMdd");
            SaleTime checkOutSaleTime = mSaleTime.getClone(mSaleTime.getOffsetDailyDay() + mNights);
            String checkOutDate = checkOutSaleTime.getDayOfDaysDateFormat("yyMMdd");

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmm");

            return String.format("%s-%s-%s", checkInDate, checkOutDate, simpleDateFormat.format(calendar.getTime()));
        }

        private void analyticsOnResponseSearchResultListForSearches(String keyword, int totalCount)
        {
            String action;

            if (totalCount == 0)
            {
                switch (mSearchType)
                {
                    case SEARCHTYPE_SEARCHES:
                        action = AnalyticsManager.Action.HOTEL_KEYWORD_SEARCH_NOT_FOUND;
                        break;

                    case SEARCHTYPE_AUTOCOMPLETE:
                        action = AnalyticsManager.Action.HOTEL_AUTOCOMPLETE_KEYWORD_NOT_FOUND;
                        break;

                    case SEARCHTYPE_RECENT:
                        action = AnalyticsManager.Action.HOTEL_RECENT_KEYWORD_NOT_FOUND;
                        break;

                    default:
                        action = AnalyticsManager.Action.HOTEL_KEYWORD_SEARCH_NOT_FOUND;
                        break;
                }

                String label = String.format("%s-%s", keyword, getSearchDate());
                AnalyticsManager.getInstance(HotelSearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , action, label, null);

                AnalyticsManager.getInstance(HotelSearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH_RESULT_EMPTY, null);
            } else
            {
                switch (mSearchType)
                {
                    case SEARCHTYPE_SEARCHES:
                        action = AnalyticsManager.Action.HOTEL_KEYWORD_SEARCH_CLICKED;
                        break;

                    case SEARCHTYPE_AUTOCOMPLETE:
                        action = AnalyticsManager.Action.HOTEL_AUTOCOMPLETED_KEYWORD_CLICKED;
                        break;

                    case SEARCHTYPE_RECENT:
                        action = AnalyticsManager.Action.HOTEL_RECENT_KEYWORD_SEARCH_CLICKED;
                        break;

                    default:
                        action = AnalyticsManager.Action.HOTEL_KEYWORD_SEARCH_CLICKED;
                        break;
                }

                String label;

                if (totalCount == -1)
                {
                    label = String.format("%s-Los-%s", keyword, getSearchDate());
                } else
                {
                    label = String.format("%s-%d-%s", keyword, totalCount, getSearchDate());
                }

                AnalyticsManager.getInstance(HotelSearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , action, label, null);

                AnalyticsManager.getInstance(HotelSearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH_RESULT, null);
            }
        }

        private void analyticsOnResponseSearchResultListForLocation()
        {
            if (Util.isTextEmpty(mAddress) == true || mSize == -100)
            {
                return;
            }

            if (mSize == 0)
            {
                String label = String.format("%s-%s", mAddress, getSearchDate());
                AnalyticsManager.getInstance(HotelSearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , AnalyticsManager.Action.HOTEL_AROUND_SEARCH_NOT_FOUND, label, null);

                AnalyticsManager.getInstance(HotelSearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH_RESULT_EMPTY, null);
            } else
            {
                String label;

                if (mSize == -1)
                {
                    label = String.format("%s-Los-%s", mAddress, getSearchDate());
                } else
                {
                    label = String.format("%s-%d-%s", mAddress, mSize, getSearchDate());
                }

                AnalyticsManager.getInstance(HotelSearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , AnalyticsManager.Action.HOTEL_AROUND_SEARCH_CLICKED, label, null);

                AnalyticsManager.getInstance(HotelSearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH_RESULT, null);
            }
        }

        private void distanceBetween(Location location, ArrayList<PlaceViewItem> placeViewItemList)
        {
            ((HotelSearchResultLayout) mPlaceSearchResultLayout).setSortType(SortType.DISTANCE);

            Hotel hotel;
            float[] results = new float[3];

            for (PlaceViewItem placeViewItem : placeViewItemList)
            {
                hotel = placeViewItem.<Hotel>getItem();

                Location.distanceBetween(location.getLatitude(), location.getLongitude(), hotel.latitude, hotel.longitude, results);
                hotel.distance = results[0];
            }
        }

        private void responseSearchResultList(int totalCount, ArrayList<PlaceViewItem> placeViewItemList)
        {
            mTotalCount = totalCount;

            if (totalCount == 0 || (mOffset == 0 && (placeViewItemList == null || placeViewItemList.size() == 0)))
            {
                mPlaceSearchResultLayout.showEmptyLayout();
            } else
            {
                if (placeViewItemList != null)
                {
                    int size = placeViewItemList.size();
                    if (size < COUNT_PER_TIMES)
                    {
                        mOffset = -1;
                    } else
                    {
                        mOffset += placeViewItemList.size();
                    }

                    // 위치 요청 타입인 경우에는 위치를 계산해 주어야 한다.
                    if (mLocation != null)
                    {
                        distanceBetween(mLocation, placeViewItemList);
                    }
                } else
                {
                    mOffset = -1;
                }

                mPlaceSearchResultLayout.showListLayout();
                ((HotelSearchResultLayout) mPlaceSearchResultLayout).addSearchResultList(placeViewItemList);
            }

            mPlaceSearchResultLayout.updateResultCount(totalCount);
            unLockUI();
        }

        @Override
        public void onResponseSearchResultList(int totalCount, ArrayList<PlaceViewItem> placeViewItemList)
        {
            if (isFinishing() == true)
            {
                return;
            }

            if (mOffset == 0)
            {
                analyticsOnResponseSearchResultListForSearches(mKeyword.name, totalCount);
            }

            responseSearchResultList(totalCount, placeViewItemList);
        }

        @Override
        public void onResponseLocationSearchResultList(int totalCount, ArrayList<PlaceViewItem> placeViewItemList)
        {
            if (isFinishing() == true)
            {
                return;
            }

            if (mOffset == 0)
            {
                if (placeViewItemList != null)
                {
                    mSize = totalCount;
                } else
                {
                    mSize = 0;
                }

                analyticsOnResponseSearchResultListForLocation();
            }

            responseSearchResultList(totalCount, placeViewItemList);
        }

        @Override
        public void onResponseCustomerSatisfactionTimeMessage(String message)
        {
            mCustomerSatisfactionTimeMessage = message;
        }

        @Override
        public void onResponseAddress(String address)
        {
            if (isFinishing() == true)
            {
                return;
            }

            mAddress = address;

            mPlaceSearchResultLayout.setToolbarTitle(address);

            analyticsOnResponseSearchResultListForLocation();
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            unLockUI();

            HotelSearchResultActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            unLockUI();

            HotelSearchResultActivity.this.onError(e);
        }

        @Override
        public void onErrorMessage(int msgCode, String message)
        {
            unLockUI();

            HotelSearchResultActivity.this.onErrorMessage(msgCode, message);
        }
    };
}

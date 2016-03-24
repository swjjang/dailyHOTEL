package com.twoheart.dailyhotel.screen.hotel.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.screen.hotel.detail.HotelDetailActivity;

import java.util.ArrayList;

public class HotelSearchResultActivity extends PlaceSearchResultActivity
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_NIGHTS = "nights";

    private SaleTime mSaleTime;
    private int mNights;
    private Keyword mKeyword;
    private String mCustomerSatisfactionTimeMessage;

    private HotelSearchResultPresenter mHotelSearchResultPresenter;

    public static Intent newInstance(Context context, SaleTime saleTime, int nights, Keyword keyword)
    {
        Intent intent = new Intent(context, HotelSearchResultActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_NIGHTS, nights);
        intent.putExtra(INTENT_EXTRA_DATA_KEYWORD, keyword);

        return intent;
    }

    public static Intent newInstance(Context context, SaleTime saleTime, int nights, String text)
    {
        return newInstance(context, saleTime, nights, new Keyword(text));
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
        mKeyword = intent.getParcelableExtra(INTENT_EXTRA_DATA_KEYWORD);
    }

    @Override
    protected void initContents()
    {
        super.initContents();

        String checkInDate = mSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd");
        SaleTime checkOutSaleTime = mSaleTime.getClone(mSaleTime.getOffsetDailyDay() + mNights);
        String checkOutDate = checkOutSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd");

        mPlaceSearchResultLayout.setToolbarText(mKeyword.name, String.format("%s - %s", checkInDate, checkOutDate));

        mHotelSearchResultPresenter = new HotelSearchResultPresenter(this, mNetworkTag, mOnPresenterListener);
        mHotelSearchResultPresenter.requestCustomerSatisfactionTimeMessage();
    }

    @Override
    protected void requestSearchResultList()
    {
        mHotelSearchResultPresenter.requestSearchResultList(mSaleTime, mNights, mKeyword.name);
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
            HotelSearchResultActivity.this.finish(Activity.RESULT_CANCELED);
        }

        @Override
        public void finish(int resultCode)
        {
            HotelSearchResultActivity.this.finish(resultCode);
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
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnPresenterListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HotelSearchResultPresenter.OnPresenterListener mOnPresenterListener = new HotelSearchResultPresenter.OnPresenterListener()
    {
        @Override
        public void onResponseSearchResultList(ArrayList<PlaceViewItem> placeViewItemList)
        {
            if (placeViewItemList == null || placeViewItemList.size() == 0)
            {
                ((HotelSearchResultLayout) mPlaceSearchResultLayout).setSearchResultList(null);
            } else
            {
                ((HotelSearchResultLayout) mPlaceSearchResultLayout).setSearchResultList(placeViewItemList);
            }

            unLockUI();
        }

        @Override
        public void onResponseCustomerSatisfactionTimeMessage(String message)
        {
            mCustomerSatisfactionTimeMessage = message;
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            HotelSearchResultActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            HotelSearchResultActivity.this.onError(e);
        }

        @Override
        public void onErrorMessage(String message)
        {
            HotelSearchResultActivity.this.onErrorMessage(message);
        }
    };
}

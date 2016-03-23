package com.twoheart.dailyhotel.screen.hotel.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.HotelDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HotelSearchResultActivity extends PlaceSearchResultActivity
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_NIGHTS = "nights";

    private HotelSearchResultListAdapter mListAdapter;

    private SaleTime mSaleTime;
    private int mNights;
    private Keyword mKeyword;

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
    protected void initIntent(Intent intent)
    {
        mSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALETIME);
        mNights = intent.getIntExtra(INTENT_EXTRA_DATA_NIGHTS, 1);
        mKeyword = intent.getParcelableExtra(INTENT_EXTRA_DATA_KEYWORD);
    }

    @Override
    protected void initLayout()
    {
        super.initLayout();

        mListAdapter = new HotelSearchResultListAdapter(this, new ArrayList<PlaceViewItem>(), mOnItemClickListener);
        mRecyclerView.setAdapter(mListAdapter);
    }

    @Override
    protected void initToolbarLayout(View view)
    {
        TextView titleView = (TextView) view.findViewById(R.id.titleView);
        TextView dateView = (TextView) view.findViewById(R.id.dateView);

        titleView.setText(mKeyword.name);

        String checkInDate = mSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd");

        SaleTime checkOutSaleTime = mSaleTime.getClone(mSaleTime.getOffsetDailyDay() + mNights);
        String checkOutDate = checkOutSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd");

        dateView.setText(String.format("%s - %s", checkInDate, checkOutDate));
    }

    @Override
    protected void requestSearch()
    {
        showEmptyLayout();

        DailyNetworkAPI.getInstance().requestHotelSearchList(getNetworkTag(), mSaleTime, mNights, mKeyword.name, mHotelSearchListJsonResponseListener, this);
    }

    @Override
    protected Keyword getKeyword()
    {
        return mKeyword;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int position = mRecyclerView.getChildAdapterPosition(v);

            if (position < 0)
            {
                return;
            }

            PlaceViewItem placeViewItem = mListAdapter.getItem(position);

            if (placeViewItem.getType() != PlaceViewItem.TYPE_ENTRY)
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
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Network Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mHotelSearchListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    String imageUrl = dataJSONObject.getString("imgUrl");
                    int nights = dataJSONObject.getInt("lengthStay");
                    JSONArray hotelJSONArray = dataJSONObject.getJSONArray("hotelSaleList");

                    int length;

                    if (hotelJSONArray == null)
                    {
                        length = 0;
                    } else
                    {
                        length = hotelJSONArray.length();
                    }

                    mListAdapter.clear();

                    if (length == 0)
                    {
                        showEmptyLayout();
                        mListAdapter.notifyDataSetChanged();
                    } else
                    {
                        showListLayout();

                        ArrayList<PlaceViewItem> placeViewItemList = makeHotelList(hotelJSONArray, imageUrl, nights);
                        mListAdapter.addAll(placeViewItemList);
                        mListAdapter.notifyDataSetChanged();

                        updateResultCount(placeViewItemList.size());
                    }
                } else
                {
                    String message = response.getString("msg");

                    onInternalError(message);
                }
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }

        private ArrayList<PlaceViewItem> makeHotelList(JSONArray jsonArray, String imageUrl, int nights) throws JSONException
        {
            if (jsonArray == null)
            {
                return new ArrayList<>();
            }

            int length = jsonArray.length();
            ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>(length);
            JSONObject jsonObject;

            for (int i = 0; i < length; i++)
            {
                jsonObject = jsonArray.getJSONObject(i);

                Hotel hotel = new Hotel();

                if (hotel.setHotel(jsonObject, imageUrl, nights) == true)
                {
                    PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, hotel);
                    placeViewItemList.add(placeViewItem);
                }
            }

            return placeViewItemList;
        }
    };
}

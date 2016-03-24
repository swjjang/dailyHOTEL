package com.twoheart.dailyhotel.screen.hotel.search;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BasePresenter;
import com.twoheart.dailyhotel.place.base.OnBasePresenterListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HotelSearchResultPresenter extends BasePresenter
{
    protected interface OnPresenterListener extends OnBasePresenterListener
    {
        void onResponseSearchResultList(ArrayList<PlaceViewItem> placeViewItemList);

        void onResponseCustomerSatisfactionTimeMessage(String message);
    }

    public HotelSearchResultPresenter(Context context, String networkTag, OnPresenterListener listener)
    {
        super(context, networkTag, listener);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mOnPresenterListener.onErrorResponse(volleyError);
    }

    @Override
    protected void onErrorMessage(String message)
    {
        mOnPresenterListener.onErrorMessage(message);
    }

    public void requestSearchResultList(SaleTime saleTime, int nights, String keword)
    {
        DailyNetworkAPI.getInstance().requestHotelSearchList(mNetworkTag, saleTime, nights, keword, mHotelSearchListJsonResponseListener, this);
    }

    public void requestCustomerSatisfactionTimeMessage()
    {
        DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, null);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Network Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.KOREA);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                String message = mContext.getString(R.string.dialog_message_cs_operating_time //
                    , Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("openDateTime")))) //
                    , Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("closeDateTime")))));


                ((OnPresenterListener) mOnPresenterListener).onResponseCustomerSatisfactionTimeMessage(message);
            } catch (Exception e)
            {
                mOnPresenterListener.onError(e);
            }
        }
    };

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

                    if (length == 0)
                    {
                        ((OnPresenterListener) mOnPresenterListener).onResponseSearchResultList(null);
                    } else
                    {
                        ArrayList<PlaceViewItem> placeViewItemList = makeHotelList(hotelJSONArray, imageUrl, nights);
                        ((OnPresenterListener) mOnPresenterListener).onResponseSearchResultList(placeViewItemList);
                    }
                } else
                {
                    String message = response.getString("msg");

                    onErrorMessage(message);
                }
            } catch (Exception e)
            {
                mOnPresenterListener.onError(e);
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

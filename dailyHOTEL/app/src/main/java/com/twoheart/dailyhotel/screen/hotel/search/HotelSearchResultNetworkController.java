package com.twoheart.dailyhotel.screen.hotel.search;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.HotelSearch;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HotelSearchResultNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onResponseSearchResultList(int totalCount, ArrayList<PlaceViewItem> placeViewItemList);

        void onResponseLocationSearchResultList(int totalCount, ArrayList<PlaceViewItem> placeViewItemList);

        void onResponseCustomerSatisfactionTimeMessage(String message);

        void onResponseAddress(String address);
    }

    public HotelSearchResultNetworkController(Context context, String networkTag, OnNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError)
    {
        mOnNetworkControllerListener.onErrorResponse(volleyError);
    }

    public void requestSearchResultList(SaleTime saleTime, int nights, String keword, int offset, int count)
    {
        DailyNetworkAPI.getInstance().requestHotelSearchList(mNetworkTag, saleTime, nights, keword, offset, count, mHotelSearchListJsonResponseListener, this);
    }

    public void requestSearchResultList(SaleTime saleTime, int nights, Location location, int offset, int count)
    {
        requestAddress(location, mLocationToAddressListener);

        DailyNetworkAPI.getInstance().requestHotelSearchList(mNetworkTag, saleTime, nights, location, offset, count, mHotelLocationSearchListJsonResponseListener, this);
    }

    public void requestCustomerSatisfactionTimeMessage()
    {
        DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, null);
    }

    private ArrayList<PlaceViewItem> makeHotelList(JSONArray jsonArray, String imageUrl, int nights) throws JSONException
    {
        if (jsonArray == null || jsonArray.length() == 0)
        {
            return new ArrayList<>();
        }

        int length = jsonArray.length();
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>(length);
        JSONObject jsonObject;

        for (int i = 0; i < length; i++)
        {
            jsonObject = jsonArray.getJSONObject(i);

            HotelSearch hotel = new HotelSearch();

            if (hotel.setHotel(jsonObject, imageUrl, nights) == true)
            {
                PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, hotel);
                placeViewItemList.add(placeViewItem);
            }
        }

        return placeViewItemList;
    }

    private void requestAddress(Location location, DailyHotelJsonResponseListener listener)
    {
        final String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s&language=ko"//
            , Double.toString(location.getLatitude())//
            , Double.toString(location.getLongitude())//
            , DailyHotelRequest.getUrlDecoderEx(Constants.GOOGLE_MAP_KEY));

        new SearchAddressAsyncTask(url, listener).execute();
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


                ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseCustomerSatisfactionTimeMessage(message);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
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
                    int totalCount = dataJSONObject.getInt("totalCount");

                    // totalCount == -1 인경우에는 연박으로 호텔의 개수를 알수가 없다.
                    // 이슈 사항은 연박인 경우 더이상 로딩 하지 않는 경우에 발생할수 있다.

                    int length;

                    if (hotelJSONArray == null)
                    {
                        length = 0;
                    } else
                    {
                        length = hotelJSONArray.length();
                    }

                    if (length == 0 && totalCount != -1)
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseSearchResultList(0, null);
                    } else
                    {
                        ArrayList<PlaceViewItem> placeViewItemList = makeHotelList(hotelJSONArray, imageUrl, nights);
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseSearchResultList(totalCount, placeViewItemList);
                    }
                } else
                {
                    String message = response.getString("msg");
                    mOnNetworkControllerListener.onErrorMessage(msgCode, message);
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mHotelLocationSearchListJsonResponseListener = new DailyHotelJsonResponseListener()
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
                    int totalCount = dataJSONObject.getInt("totalCount");

                    // totalCount == -1 인경우에는 연박으로 호텔의 개수를 알수가 없다.
                    // 이슈 사항은 연박인 경우 더이상 로딩 하지 않는 경우에 발생할수 있다.

                    int length;

                    if (hotelJSONArray == null)
                    {
                        length = 0;
                    } else
                    {
                        length = hotelJSONArray.length();
                    }

                    if (length == 0 && totalCount != -1)
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseLocationSearchResultList(0, null);
                    } else
                    {
                        ArrayList<PlaceViewItem> placeViewItemList = makeHotelList(hotelJSONArray, imageUrl, nights);
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseLocationSearchResultList(totalCount, placeViewItemList);
                    }
                } else
                {
                    String message = response.getString("msg");
                    mOnNetworkControllerListener.onErrorMessage(msgCode, message);
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mLocationToAddressListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            if (response == null)
            {
                ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAddress(mContext.getString(R.string.label_search_no_address));
                return;
            }

            try
            {
                JSONArray jsonArray = response.getJSONArray("results");
                JSONObject searchJSONObject = getSearchTypes(jsonArray, "country");

                String shortName = searchJSONObject.getString("short_name");
                String searchKeyword = "KR".equalsIgnoreCase(shortName) ? "sublocality_level_2" : "administrative_area_level_1";

                JSONObject addressJSONObject = getSearchTypes(jsonArray, searchKeyword);

                if (addressJSONObject != null)
                {
                    String address = addressJSONObject.getString("long_name");

                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAddress(address);
                }
            } catch (JSONException e)
            {
                ExLog.e(e.toString());
                ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAddress(mContext.getString(R.string.label_search_no_address));
            }
        }

        private JSONObject getSearchTypes(JSONArray jsonArray, String searchType) throws JSONException
        {
            if (jsonArray == null || Util.isTextEmpty(searchType) == true)
            {
                return null;
            }

            int length = jsonArray.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray addressComponentsJSONArray = jsonObject.getJSONArray("address_components");

                int addressLength = addressComponentsJSONArray.length();

                for (int j = 0; j < addressLength; j++)
                {
                    JSONObject addressJSONObject = addressComponentsJSONArray.getJSONObject(j);
                    JSONArray typesJSONArray = addressJSONObject.getJSONArray("types");

                    boolean hasType = hasType(typesJSONArray, searchType);

                    if (hasType == true)
                    {
                        return addressJSONObject;
                    }
                }
            }

            return null;
        }

        private boolean hasType(JSONArray jsonArray, String searchType) throws JSONException
        {
            if (jsonArray == null || Util.isTextEmpty(searchType) == true)
            {
                return false;
            }

            int length = jsonArray.length();

            for (int i = 0; i < length; i++)
            {
                String type = jsonArray.getString(i);

                if (searchType.equalsIgnoreCase(type) == true)
                {
                    return true;
                }
            }

            return false;
        }
    };

    private class SearchAddressAsyncTask extends AsyncTask<Void, Void, JSONObject>
    {
        private String mUrl;
        private DailyHotelJsonResponseListener mListener;

        public SearchAddressAsyncTask(String url, DailyHotelJsonResponseListener listener)
        {
            mUrl = url;
            mListener = listener;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()//
                .url(mUrl).build();

            JSONObject jsonObject = null;

            try
            {
                Response response = okHttpClient.newCall(request).execute();

                if (response.isSuccessful() == true)
                {
                    String data = response.body().string();

                    if (Util.isTextEmpty(data) == false)
                    {
                        jsonObject = new JSONObject(data);
                    }
                }
            } catch (Exception e)
            {
                jsonObject = null;
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject)
        {
            if (mListener != null)
            {
                mListener.onResponse(mUrl, jsonObject);
            }
        }
    }
}

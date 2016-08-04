package com.twoheart.dailyhotel.screen.search.stay.result;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.SaleTime;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StaySearchResultNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onResponseAddress(String address);
    }

    public StaySearchResultNetworkController(Context context, String networkTag, OnNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestAddress(Location location)
    {
        final String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s&language=ko"//
            , Double.toString(location.getLatitude())//
            , Double.toString(location.getLongitude())//
            , DailyHotelRequest.getUrlDecoderEx(Constants.GOOGLE_MAP_KEY));

        new SearchAddressAsyncTask(url, mLocationToAddressListener).execute();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Network Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mLocationToAddressListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

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

                if (searchJSONObject == null)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAddress(mContext.getString(R.string.label_search_no_address));
                } else
                {
                    String shortName = searchJSONObject.getString("short_name");
                    String searchKeyword = "KR".equalsIgnoreCase(shortName) ? "sublocality_level_2" : "administrative_area_level_1";

                    String address = getSearchTypes(jsonArray, searchKeyword, "long_name");
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAddress(address);
                }
            } catch (Exception e)
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

        private String getSearchTypes(JSONArray jsonArray, String searchType, String key) throws JSONException
        {
            if (jsonArray == null || Util.isTextEmpty(searchType) == true)
            {
                return null;
            }

            int length = jsonArray.length();
            String firstAddress = null;

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
                        String address = addressJSONObject.getString(key);


                        if (Util.isTextEmpty(firstAddress) == true)
                        {
                            firstAddress = address;
                        }

                        if (isKoreanAddress(address) == true)
                        {
                            return address;
                        }
                    }
                }
            }

            return firstAddress;
        }

        private boolean isKoreanAddress(String address)
        {
            if (Util.isTextEmpty(address) == true)
            {
                return false;
            }

            Pattern pattern = Pattern.compile("^[0-9가-힣ㄱ-ㅎㅏ-ㅣ\\s\u318D\u119E\u11A2\u2022\u2025a\u00B7\uFE55]+$");

            int length = address.length();

            for (int i = 0; i < length; i++)
            {
                char c = address.charAt(i);

                if (pattern.matcher(Character.toString(c)).matches() == false)
                {
                    return false;
                }
            }

            return true;
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

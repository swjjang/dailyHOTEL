package com.twoheart.dailyhotel.screen.home.category.nearby;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by android_sam on 2017. 5. 19..
 */

@Deprecated
public class StayCategoryNearByNetworkController extends BaseNetworkController
{
    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onResponseAddress(String address);
    }

    public StayCategoryNearByNetworkController(Context context, String networkTag, OnNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestAddress(Location location)
    {
        if (location == null)
        {
            return;
        }

        requestAddress(location.getLatitude(), location.getLongitude());
    }

    public void requestAddress(double latitude, double longitude)
    {
        final String url = String.format(Locale.KOREA, "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s&language=ko"//
            , Double.toString(latitude)//
            , Double.toString(longitude)//
            , Crypto.getUrlDecoderEx(Constants.GOOGLE_MAP_KEY));

        new SearchAddressAsyncTask(url).execute();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Network Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class SearchAddressAsyncTask extends AsyncTask<Void, Void, JSONObject>
    {
        private String mUrl;
        private retrofit2.Callback mCallback;

        public SearchAddressAsyncTask(String url)
        {
            mUrl = url;
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

                    if (DailyTextUtils.isTextEmpty(data) == false)
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
            try
            {
                JSONArray dataJSONOArray = jsonObject.getJSONArray("results");
                JSONObject searchJSONObject = getSearchTypes(dataJSONOArray, "country");

                if (searchJSONObject == null)
                {
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAddress(mContext.getString(R.string.label_search_nearby_empty_address));
                } else
                {
                    String shortName = searchJSONObject.getString("short_name");
                    String searchKeyword = "KR".equalsIgnoreCase(shortName) ? "sublocality_level_2" : "administrative_area_level_1";

                    String address = getSearchTypes(dataJSONOArray, searchKeyword, "long_name");
                    ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAddress(address);
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
                ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseAddress(mContext.getString(R.string.label_search_nearby_empty_address));
            }
        }

        private JSONObject getSearchTypes(JSONArray jsonArray, String searchType) throws JSONException
        {
            if (jsonArray == null || DailyTextUtils.isTextEmpty(searchType) == true)
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
            if (jsonArray == null || DailyTextUtils.isTextEmpty(searchType) == true)
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


                        if (DailyTextUtils.isTextEmpty(firstAddress) == true)
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
            if (DailyTextUtils.isTextEmpty(address) == true)
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
            if (jsonArray == null || DailyTextUtils.isTextEmpty(searchType) == true)
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
    }
}

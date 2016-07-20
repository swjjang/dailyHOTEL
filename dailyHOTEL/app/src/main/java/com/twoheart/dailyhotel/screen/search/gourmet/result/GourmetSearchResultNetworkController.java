package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetSearch;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GourmetSearchResultNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onResponseSearchResultList(int totalCount, ArrayList<PlaceViewItem> placeViewItemList);

        void onResponseLocationSearchResultList(int totalCount, ArrayList<PlaceViewItem> placeViewItemList);

        void onResponseAddress(String address);
    }

    public GourmetSearchResultNetworkController(Context context, String networkTag, OnNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestSearchResultList(SaleTime saleTime, String keyword, int offset, int count)
    {
        String encodeKeyword;

        try
        {
            encodeKeyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            encodeKeyword = keyword;
        }

        DailyNetworkAPI.getInstance(mContext).requestGourmetSearchList(mNetworkTag, saleTime, encodeKeyword, offset, count, mGourmetSearchListJsonResponseListener, mGourmetSearchListJsonResponseListener);
    }

    public void requestSearchResultList(SaleTime saleTime, Location location, int offset, int count)
    {
        requestAddress(location, mLocationToAddressListener);

        DailyNetworkAPI.getInstance(mContext).requestGourmetSearchList(mNetworkTag, saleTime, location, offset, count, mGourmetLocationSearchListJsonResponseListener, mGourmetLocationSearchListJsonResponseListener);
    }

    private ArrayList<PlaceViewItem> makeGourmetList(JSONArray jsonArray, String imageUrl) throws JSONException
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

            GourmetSearch gourmet = new GourmetSearch();

            if (gourmet.setGourmet(jsonObject, imageUrl) == true)
            {
                PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, gourmet);
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

    private DailyHotelJsonResponseListener mGourmetSearchListJsonResponseListener = new DailyHotelJsonResponseListener()
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
                    JSONArray gourmetJSONArray = dataJSONObject.getJSONArray("gourmetSaleList");
                    int totalCount = dataJSONObject.getInt("totalCount");

                    // totalCount == -1 인경우에는 연박으로 호텔의 개수를 알수가 없다.
                    // 이슈 사항은 연박인 경우 더이상 로딩 하지 않는 경우에 발생할수 있다.

                    int length;

                    if (gourmetJSONArray == null)
                    {
                        length = 0;
                    } else
                    {
                        length = gourmetJSONArray.length();
                    }

                    if (length == 0 && totalCount != -1)
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseSearchResultList(0, null);
                    } else
                    {
                        ArrayList<PlaceViewItem> placeViewItemList = makeGourmetList(gourmetJSONArray, imageUrl);
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseSearchResultList(totalCount, placeViewItemList);
                    }
                } else
                {
                    String message;

                    if (response.has("msg") == false)
                    {
                        if (Constants.DEBUG == false)
                        {
                            String exceptionMessage = url + " : " + response.toString();
                            Crashlytics.logException(new JSONException(exceptionMessage));
                        }

                        message = mContext.getString(R.string.act_base_network_connect);
                    } else
                    {
                        message = response.getString("msg");
                    }

                    mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                }
            } catch (JSONException e)
            {
                if (Constants.DEBUG == false)
                {
                    String message = url + " : " + response.toString();
                    Crashlytics.log(10, "GourmetSearchResultNetworkController", message);
                }

                mOnNetworkControllerListener.onError(e);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }
    };

    private DailyHotelJsonResponseListener mGourmetLocationSearchListJsonResponseListener = new DailyHotelJsonResponseListener()
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
                    JSONArray gourmetJSONArray = dataJSONObject.getJSONArray("gourmetSaleList");
                    int totalCount = dataJSONObject.getInt("totalCount");

                    // totalCount == -1 인경우에는 연박으로 호텔의 개수를 알수가 없다.
                    // 이슈 사항은 연박인 경우 더이상 로딩 하지 않는 경우에 발생할수 있다.

                    int length;

                    if (gourmetJSONArray == null)
                    {
                        length = 0;
                    } else
                    {
                        length = gourmetJSONArray.length();
                    }

                    if (length == 0 && totalCount != -1)
                    {
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseLocationSearchResultList(0, null);
                    } else
                    {
                        ArrayList<PlaceViewItem> placeViewItemList = makeGourmetList(gourmetJSONArray, imageUrl);
                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onResponseLocationSearchResultList(totalCount, placeViewItemList);
                    }
                } else
                {
                    String message;

                    if (response.has("msg") == false)
                    {
                        if (Constants.DEBUG == false)
                        {
                            String exceptionMessage = url + " : " + response.toString();
                            Crashlytics.logException(new JSONException(exceptionMessage));
                        }

                        message = mContext.getString(R.string.act_base_network_connect);
                    } else
                    {
                        message = response.getString("msg");
                    }

                    mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                }
            } catch (JSONException e)
            {
                if (Constants.DEBUG == false)
                {
                    String message = url + " : " + response.toString();
                    Crashlytics.log(10, "GourmetLocationSearchResultNetworkController", message);
                }

                mOnNetworkControllerListener.onError(e);
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
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

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
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
package com.twoheart.dailyhotel.screen.search.collection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class CollectionGourmetActivity extends CollectionBaseActivity
{
    private SaleTime mSaleTime;

    public static Intent newInstance(Context context, SaleTime startSaleTime, SaleTime endSaleTime, String title, String titleImageUrl, String queryType, String query)
    {
        Intent intent = new Intent(context, CollectionGourmetActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_START_SALETIME, startSaleTime);
        intent.putExtra(INTENT_EXTRA_DATA_END_SALETIME, endSaleTime);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE_IMAGE_URL, titleImageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_QUERY_TYPE, queryType);
        intent.putExtra(INTENT_EXTRA_DATA_QUERY, query);

        return intent;
    }

    public static Intent newInstance(Context context, SaleTime saleTime, String title, String titleImageUrl, String queryType, String query)
    {
        Intent intent = new Intent(context, CollectionGourmetActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_SALE_TIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE_IMAGE_URL, titleImageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_QUERY_TYPE, queryType);
        intent.putExtra(INTENT_EXTRA_DATA_QUERY, query);

        return intent;
    }

    @Override
    protected void initIntentTime(Intent intent)
    {
        if (intent.hasExtra(INTENT_EXTRA_DATA_SALE_TIME) == true)
        {
            mSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALE_TIME);

            mStartSaleTime = mSaleTime.getClone(0);
            mEndSaleTime = null;
        } else
        {
            mStartSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_START_SALETIME);
            mEndSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_END_SALETIME);

            // 범위 지정인데 이미 날짜가 지난 경우, 초기화
            if (mStartSaleTime.getOffsetDailyDay() == 0 && mEndSaleTime.getOffsetDailyDay() == 0)
            {
                showSimpleDialog(null, getString(R.string.message_end_event), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent eventIntent = new Intent();
                        eventIntent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=10&v=el"));
                        startActivity(eventIntent);
                    }
                }, null);

                mEndSaleTime = null;
            }

            mSaleTime = mStartSaleTime.getClone();
        }
    }

    @Override
    protected PlaceListAdapter getPlaceListAdapter(View.OnClickListener listener)
    {
        return new CollectionGourmetAdapter(this, new ArrayList<PlaceViewItem>(), mOnItemClickListener, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = GourmetCalendarActivity.newInstance(CollectionGourmetActivity.this, mSaleTime//
                    , mStartSaleTime, mEndSaleTime, AnalyticsManager.ValueType.SEARCH, true, true);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);
            }
        });
    }

    @Override
    protected void requestPlaceList(String params)
    {
        String gourmetParams = String.format("reserveDate=%s&details=true&%s", mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), params);

        Uri uri = Uri.parse("http://www.daily.com/gourmet?" + gourmetParams);

        HashMap<String, Object> hashMap = new HashMap<>();
        List<String> categoryList = null;
        List<String> timeList = null;
        List<String> luxuryList = null;

        for (String parameter : uri.getQueryParameterNames())
        {
            if ("category".equalsIgnoreCase(parameter) == true)
            {
                categoryList = uri.getQueryParameters("category");
            } else if ("timeFrame".equalsIgnoreCase(parameter) == true)
            {
                timeList = uri.getQueryParameters("timeFrame");
            } else if ("luxury".equalsIgnoreCase(parameter) == true)
            {
                luxuryList = uri.getQueryParameters("luxury");
            } else
            {
                hashMap.put(parameter, uri.getQueryParameter(parameter));
            }
        }

        DailyMobileAPI.getInstance(this).requestGourmetList(mNetworkTag, hashMap, categoryList, timeList, luxuryList, mGourmetListCallback);
    }


    public void onPlaceClick(View view, PlaceViewItem placeViewItem, int count)
    {
        if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
        {
            return;
        }

        Gourmet gourmet = placeViewItem.getItem();

        Intent intent = GourmetDetailActivity.newInstance(this, mSaleTime, gourmet, mStartSaleTime, mEndSaleTime, count);

        if (Util.isUsedMutilTransition() == true)
        {
            View simpleDraweeView = view.findViewById(R.id.imageView);
            View nameTextView = view.findViewById(R.id.nameTextView);
            View gradientTopView = view.findViewById(R.id.gradientTopView);
            View gradientBottomView = view.findViewById(R.id.gradientView);

            Object mapTag = gradientBottomView.getTag();

            if (mapTag != null && "map".equals(mapTag) == true)
            {
                intent.putExtra(NAME_INTENT_EXTRA_DATA_FROM_MAP, true);
            }

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,//
                android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL, options.toBundle());
        } else
        {
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);
        }
    }

    @Override
    protected String getCalendarDate()
    {
        if (mSaleTime == null)
        {
            return null;
        }

        return mSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            SaleTime checkInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);

            if (checkInSaleTime == null)
            {
                return;
            }

            mSaleTime = checkInSaleTime;
        }
    }

    @Override
    protected String getSectionTitle(int count)
    {
        return getString(R.string.label_count_gourmet, count);
    }

    private retrofit2.Callback mGourmetListCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                        JSONArray gourmetJSONArray = null;

                        if (dataJSONObject.has("gourmetSales") == true)
                        {
                            gourmetJSONArray = dataJSONObject.getJSONArray("gourmetSales");
                        }

                        String imageUrl;

                        ArrayList<Place> gourmetList;

                        if (gourmetJSONArray != null)
                        {
                            imageUrl = dataJSONObject.getString("imgUrl");
                            gourmetList = makeGourmetList(gourmetJSONArray, imageUrl);
                        } else
                        {
                            gourmetList = new ArrayList<>();
                        }

                        onPlaceList(gourmetList);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");

                        onErrorPopupMessage(msgCode, message);
                    }
                } catch (Exception e)
                {
                    onError(e);
                } finally
                {
                    unLockUI();
                }
            } else
            {
                CollectionGourmetActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            CollectionGourmetActivity.this.onError(t);
        }

        private ArrayList<Place> makeGourmetList(JSONArray jsonArray, String imageUrl) throws JSONException
        {
            if (jsonArray == null)
            {
                return new ArrayList<>();
            }

            int length = jsonArray.length();
            ArrayList<Place> gourmetList = new ArrayList<>(length);
            JSONObject jsonObject;
            Gourmet gourmet;

            for (int i = 0; i < length; i++)
            {
                jsonObject = jsonArray.getJSONObject(i);

                gourmet = new Gourmet();

                if (gourmet.setData(jsonObject, imageUrl) == true)
                {
                    gourmetList.add(gourmet);
                }
            }

            return gourmetList;
        }
    };
}
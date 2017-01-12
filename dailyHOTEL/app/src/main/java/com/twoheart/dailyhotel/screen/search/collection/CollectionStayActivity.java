package com.twoheart.dailyhotel.screen.search.collection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CollectionStayActivity extends CollectionBaseActivity
{
    private SaleTime mCheckInSaleTime;
    private int mNights;

    public static Intent newInstance(Context context, SaleTime startSaleTime, SaleTime endSaleTime, String title, String titleImageUrl, String queryType, String query)
    {
        Intent intent = new Intent(context, CollectionStayActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_START_SALETIME, startSaleTime);
        intent.putExtra(INTENT_EXTRA_DATA_END_SALETIME, endSaleTime);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE_IMAGE_URL, titleImageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_QUERY_TYPE, queryType);
        intent.putExtra(INTENT_EXTRA_DATA_QUERY, query);

        return intent;
    }

    public static Intent newInstance(Context context, SaleTime saleTime, int night, String title, String titleImageUrl, String queryType, String query)
    {
        Intent intent = new Intent(context, CollectionStayActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_SALE_TIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_NIGHT, night);
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
            mCheckInSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALE_TIME);
            mNights = intent.getIntExtra(INTENT_EXTRA_DATA_NIGHT, 1);

            mStartSaleTime = mCheckInSaleTime.getClone(0);
            mEndSaleTime = null;
        } else
        {
            mStartSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_START_SALETIME);
            mEndSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_END_SALETIME);

            // 범위 지정인데 이미 날짜가 지난 경우
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

            mCheckInSaleTime = mStartSaleTime.getClone();
            mNights = 1;
        }
    }

    @Override
    protected PlaceListAdapter getPlaceListAdapter(View.OnClickListener listener)
    {
        return new CollectionStayAdapter(this, new ArrayList<PlaceViewItem>(), mOnItemClickListener, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = StayCalendarActivity.newInstance(CollectionStayActivity.this, mCheckInSaleTime, mNights //
                    , mStartSaleTime, mEndSaleTime, AnalyticsManager.ValueType.SEARCH, true, true);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);
            }
        });
    }

    @Override
    protected void requestPlaceList(String params)
    {
        String stayParams = String.format("dateCheckIn=%s&stays=%d&details=true&%s", mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), mNights, params);

        Uri uri = Uri.parse("http://www.daily.com/stay?" + stayParams);

        HashMap<String, Object> hashMap = new HashMap<>();
        List<String> bedTypeList = null;
        List<String> luxuryList = null;

        for (String parameter : uri.getQueryParameterNames())
        {
            if ("bedType".equalsIgnoreCase(parameter) == true)
            {
                bedTypeList = uri.getQueryParameters("bedType");
            } else if ("luxury".equalsIgnoreCase(parameter) == true)
            {
                luxuryList = uri.getQueryParameters("luxury");
            } else
            {
                hashMap.put(parameter, uri.getQueryParameter(parameter));
            }
        }

        DailyMobileAPI.getInstance(this).requestStayList(mNetworkTag, hashMap, bedTypeList, luxuryList, mStayListJsonResponseListener);
    }

    @Override
    protected void onPlaceClick(View view, PlaceViewItem placeViewItem, int count)
    {
        if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
        {
            return;
        }

        Stay stay = placeViewItem.getItem();

        Intent intent = StayDetailActivity.newInstance(this, mCheckInSaleTime, stay, mStartSaleTime, mEndSaleTime, count);

        if (Util.isUsedMultiTransition() == true)
        {
            View simpleDraweeView = view.findViewById(R.id.imageView);
            View gradeTextView = view.findViewById(R.id.gradeTextView);
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
                android.support.v4.util.Pair.create(gradeTextView, getString(R.string.transition_place_grade)),//
                android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, options.toBundle());
        } else
        {
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);
        }
    }

    @Override
    protected String getCalendarDate()
    {
        if (mCheckInSaleTime == null)
        {
            return null;
        }

        SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + mNights);

        String checkInDate = mCheckInSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
        String checkOutDate = checkOutSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

        return String.format("%s - %s, %d박", checkInDate, checkOutDate, mNights);
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            SaleTime checkInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
            SaleTime checkOutSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);

            if (checkInSaleTime == null || checkOutSaleTime == null)
            {
                return;
            }

            lockUI();

            mCheckInSaleTime = checkInSaleTime;
            mNights = checkOutSaleTime.getOffsetDailyDay() - checkInSaleTime.getOffsetDailyDay();
        }
    }

    @Override
    protected String getSectionTitle(int count)
    {
        return getString(R.string.label_count_stay, count);
    }

    private retrofit2.Callback mStayListJsonResponseListener = new retrofit2.Callback<JSONObject>()
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
                        JSONArray hotelJSONArray = null;

                        if (dataJSONObject.has("hotelSales") == true)
                        {
                            hotelJSONArray = dataJSONObject.getJSONArray("hotelSales");
                        }

                        String imageUrl;

                        ArrayList<Place> stayList;

                        if (hotelJSONArray != null)
                        {
                            imageUrl = dataJSONObject.getString("imgUrl");
                            int nights = dataJSONObject.getInt("stays");
                            stayList = makeStayList(hotelJSONArray, imageUrl, nights);
                        } else
                        {
                            stayList = new ArrayList<>();
                        }

                        onPlaceList(stayList);
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
                CollectionStayActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            CollectionStayActivity.this.onError(t);
        }

        private ArrayList<Place> makeStayList(JSONArray jsonArray, String imageUrl, int nights) throws JSONException
        {
            if (jsonArray == null)
            {
                return new ArrayList<>();
            }

            int length = jsonArray.length();
            ArrayList<Place> stayList = new ArrayList<>(length);
            JSONObject jsonObject;
            Stay stay;

            for (int i = 0; i < length; i++)
            {
                jsonObject = jsonArray.getJSONObject(i);

                stay = new Stay();

                if (stay.setStay(jsonObject, imageUrl, nights) == true)
                {
                    stayList.add(stay); // 추가.
                }
            }

            return stayList;
        }
    };
}
package com.twoheart.dailyhotel.screen.search.collection;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetListAdapter;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class CollectionGourmetActivity extends CollectionBaseActivity
{
    private SaleTime mSaleTime;

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
        mSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALE_TIME);
    }

    @Override
    protected PlaceListAdapter getPlaceListAdapter(View.OnClickListener listener)
    {
        return new GourmetListAdapter(this, new ArrayList<PlaceViewItem>(), mOnItemClickListener, null);
    }

    @Override
    protected void requestPlaceList(String params)
    {
        String stayParms = String.format("reserveDate=%s&details=true&%s", mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), params);

        DailyNetworkAPI.getInstance(this).requestRecentGourmetList(mNetworkTag, stayParms, mGourmetListJsonResponseListener);
    }


    public void onPlaceClick(View view, PlaceViewItem placeViewItem, int count)
    {
        if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
        {
            return;
        }

        Gourmet gourmet = placeViewItem.getItem();

        Intent intent = GourmetDetailActivity.newInstance(this, mSaleTime, gourmet, count);

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

    private DailyHotelJsonResponseListener mGourmetListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");
                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");
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
                    String message = response.getString("msg");

                    onErrorPopupMessage(msgCode, message);
                }
            } catch (Exception e)
            {
                onError(e);
            }
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

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            CollectionGourmetActivity.this.onErrorResponse(volleyError);
        }
    };
}
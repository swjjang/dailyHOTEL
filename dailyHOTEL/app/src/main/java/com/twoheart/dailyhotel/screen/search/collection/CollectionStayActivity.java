package com.twoheart.dailyhotel.screen.search.collection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.list.StayListAdapter;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionStayActivity extends BaseActivity
{
    private static final String QUERY_TYPE_INDEX = "index";
    private static final String QUERY_TYPE_SEARCH = "search";

    private static final String INTENT_EXTRA_DATA_TITLE = "title";
    private static final String INTENT_EXTRA_DATA_TITLE_IMAGE_URL = "titleImageUrl";
    private static final String INTENT_EXTRA_DATA_QUERY_TYPE = "queryType";
    private static final String INTENT_EXTRA_DATA_QUERY = "query";
    private static final String INTENT_EXTRA_DATA_SALE_TIME = "saleTime";
    private static final String INTENT_EXTRA_DATA_NIGHT = "night";

    private DailyToolbarLayout mDailyToolbarLayout;
    private RecyclerView mRecyclerView;
    private StayListAdapter mStayListAdapter;
    private SaleTime mCheckInSaleTime;
    private int mNights;
    private String[] mHotelIndexs;
    private String mQueryType;

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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collection_search);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
        }

        mCheckInSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALE_TIME);

        mNights = intent.getIntExtra(INTENT_EXTRA_DATA_NIGHT, 1);

        String title = intent.getStringExtra(INTENT_EXTRA_DATA_TITLE);
        String titleImageUrl = intent.getStringExtra(INTENT_EXTRA_DATA_TITLE_IMAGE_URL);
        mQueryType = intent.getStringExtra(INTENT_EXTRA_DATA_QUERY_TYPE);
        String query = intent.getStringExtra(INTENT_EXTRA_DATA_QUERY);

        initLayout(title, titleImageUrl);

        if (QUERY_TYPE_INDEX.equalsIgnoreCase(mQueryType) == true)
        {
            String[] targetIndices = query.split("=");
            mHotelIndexs = targetIndices[1].split(",");

            String params = String.format("dateCheckIn=%s&stays=%d&details=true&targetIndices=%s", mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), mNights, URLEncoder.encode(targetIndices[1]));
            DailyNetworkAPI.getInstance(this).requestRecentStayList(mNetworkTag, params, mStayListJsonResponseListener);

        } else if (QUERY_TYPE_SEARCH.equalsIgnoreCase(mQueryType) == true)
        {
            String params = String.format("dateCheckIn=%s&stays=%d&details=true&page=1&limit=100&%s", mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), mNights, query);
            DailyNetworkAPI.getInstance(this).requestStaySearchList(mNetworkTag, params, mStayListJsonResponseListener);
        } else
        {
            finish();
        }
    }

    private void initLayout(String title, String titleImageUrl)
    {
        initToolbar(title);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mStayListAdapter = new StayListAdapter(this, new ArrayList<PlaceViewItem>(), mOnItemClickListener, null);
        mRecyclerView.setAdapter(mStayListAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                int position = linearLayoutManager.findFirstVisibleItemPosition();

                if (position == 0 && linearLayoutManager.findViewByPosition(position).getTop() == 0)
                {
                    mDailyToolbarLayout.setToolbarVisibility(false, true);
                } else
                {
                    mDailyToolbarLayout.setToolbarVisibility(true, true);
                }
            }
        });

        mDailyToolbarLayout.setToolbarVisibility(false, false);
    }

    private void initToolbar(String title)
    {
        View toolbar = findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(title, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private ArrayList<PlaceViewItem> makePlaceList(List<? extends Place> placeList, String[] hotelIndexs)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        if (placeList == null || placeList.size() == 0)
        {
            return placeViewItemList;
        }

        if (hotelIndexs != null && hotelIndexs.length > 0)
        {
            for (String hotelIndex : hotelIndexs)
            {
                try
                {
                    int index = Integer.parseInt(hotelIndex);

                    for (Place place : placeList)
                    {
                        if (index == place.index)
                        {
                            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, place));
                        }
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }
        } else
        {
            for (Place place : placeList)
            {
                placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, place));
            }
        }

        return placeViewItemList;
    }

    private void onStayList(ArrayList<Stay> list)
    {
        if (isFinishing() == true)
        {
            unLockUI();
            return;
        }

        int listSize = list == null ? 0 : list.size();

        ArrayList<PlaceViewItem> placeViewItems = makePlaceList(list, mHotelIndexs);

        mStayListAdapter.setAll(placeViewItems);
        mStayListAdapter.notifyDataSetChanged();

        unLockUI();
    }

    public void onStayClick(View view, PlaceViewItem placeViewItem)
    {
        if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
        {
            return;
        }

        Stay stay = placeViewItem.getItem();

        Intent intent = StayDetailActivity.newInstance(this, mCheckInSaleTime, stay, mStayListAdapter.getItemCount());

        if (Util.isUsedMutilTransition() == true)
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

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL, options.toBundle());
        } else
        {
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);
        }
    }

    protected View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                return;
            }

            PlaceViewItem placeViewItem = mStayListAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                onStayClick(view, placeViewItem);
            }
        }
    };

    private DailyHotelJsonResponseListener mStayListJsonResponseListener = new DailyHotelJsonResponseListener()
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
                    JSONArray hotelJSONArray = null;

                    if (dataJSONObject.has("hotelSales") == true)
                    {
                        hotelJSONArray = dataJSONObject.getJSONArray("hotelSales");
                    }

                    int page;
                    String imageUrl;

                    ArrayList<Stay> stayList;

                    if (hotelJSONArray != null)
                    {
                        imageUrl = dataJSONObject.getString("imgUrl");
                        int nights = dataJSONObject.getInt("stays");
                        stayList = makeStayList(hotelJSONArray, imageUrl, nights);
                    } else
                    {
                        stayList = new ArrayList<>();
                    }

                    onStayList(stayList);

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

        private ArrayList<Stay> makeStayList(JSONArray jsonArray, String imageUrl, int nights) throws JSONException
        {
            if (jsonArray == null)
            {
                return new ArrayList<>();
            }

            int length = jsonArray.length();
            ArrayList<Stay> stayList = new ArrayList<>(length);
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

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            CollectionStayActivity.this.onErrorResponse(volleyError);
        }
    };
}
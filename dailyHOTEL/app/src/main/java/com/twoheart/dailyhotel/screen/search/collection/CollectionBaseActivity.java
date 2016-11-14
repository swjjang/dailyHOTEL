package com.twoheart.dailyhotel.screen.search.collection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public abstract class CollectionBaseActivity extends BaseActivity
{
    private static final String QUERY_TYPE_INDEX = "index";
    private static final String QUERY_TYPE_SEARCH = "search";

    protected static final String INTENT_EXTRA_DATA_TITLE = "title";
    protected static final String INTENT_EXTRA_DATA_TITLE_IMAGE_URL = "titleImageUrl";
    protected static final String INTENT_EXTRA_DATA_QUERY_TYPE = "queryType";
    protected static final String INTENT_EXTRA_DATA_QUERY = "query";
    protected static final String INTENT_EXTRA_DATA_SALE_TIME = "saleTime";
    protected static final String INTENT_EXTRA_DATA_NIGHT = "night";

    private DailyToolbarLayout mDailyToolbarLayout;
    private RecyclerView mRecyclerView;
    private PlaceListAdapter mPlaceListAdapter;
    private String[] mPlaceIndexs;
    private String mQueryType;
    private String mParams;

    protected abstract void initIntentTime(Intent intent);

    protected abstract void requestPlaceList(String params);

    protected abstract PlaceListAdapter getPlaceListAdapter(View.OnClickListener listener);

    protected abstract void onPlaceClick(View view, PlaceViewItem placeViewItem, int count);

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

        initIntentTime(intent);

        String title = intent.getStringExtra(INTENT_EXTRA_DATA_TITLE);
        String titleImageUrl = intent.getStringExtra(INTENT_EXTRA_DATA_TITLE_IMAGE_URL);
        mQueryType = intent.getStringExtra(INTENT_EXTRA_DATA_QUERY_TYPE);
        String query = intent.getStringExtra(INTENT_EXTRA_DATA_QUERY);

        initLayout(title, titleImageUrl);

        if (QUERY_TYPE_INDEX.equalsIgnoreCase(mQueryType) == true)
        {
            String[] targetIndices = query.split("=");
            mPlaceIndexs = targetIndices[1].split(",");

            mParams = String.format("targetIndices=%s", URLEncoder.encode(targetIndices[1]));
        } else if (QUERY_TYPE_SEARCH.equalsIgnoreCase(mQueryType) == true)
        {
            mParams = String.format("page=1&limit=100&%s", query);
        } else
        {
            finish();
            return;
        }

        requestPlaceList(mParams);
    }

    private void initLayout(String title, String titleImageUrl)
    {
        initToolbar(title);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (mPlaceListAdapter == null)
        {
            mPlaceListAdapter = getPlaceListAdapter(mOnItemClickListener);
        }

        mRecyclerView.setAdapter(mPlaceListAdapter);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_HOTEL_DETAIL:
            case CODE_REQUEST_ACTIVITY_PLACE_DETAIL:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                        setResult(resultCode);
                        finish();
                        break;

                    case CODE_RESULT_ACTIVITY_REFRESH:
                        requestPlaceList(mParams);
                        break;
                }
                break;
            }
        }
    }

    private ArrayList<PlaceViewItem> makePlaceList(List<? extends Place> placeList, String[] placeIndexs)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        if (placeList == null || placeList.size() == 0)
        {
            return placeViewItemList;
        }

        if (placeIndexs != null && placeIndexs.length > 0)
        {
            for (String hotelIndex : placeIndexs)
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

    protected void onPlaceList(ArrayList<Place> list)
    {
        if (isFinishing() == true)
        {
            unLockUI();
            return;
        }

        int listSize = list == null ? 0 : list.size();

        ArrayList<PlaceViewItem> placeViewItems = makePlaceList(list, mPlaceIndexs);

        mPlaceListAdapter.setAll(placeViewItems);
        mPlaceListAdapter.notifyDataSetChanged();

        unLockUI();
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

            PlaceViewItem placeViewItem = mPlaceListAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                onPlaceClick(view, placeViewItem, mPlaceListAdapter.getItemCount());
            }
        }
    };
}
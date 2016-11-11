package com.twoheart.dailyhotel.screen.search.collection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;

public class CollectionSearchActivity extends BaseActivity implements View.OnClickListener
{
    private static final String QUERY_TYPE_INDEX = "index";
    private static final String QUERY_TYPE_SEARCH = "search";

    private static final String INTENT_EXTRA_DATA_PLACETYPE = "placeType";
    private static final String INTENT_EXTRA_DATA_TITLE = "title";
    private static final String INTENT_EXTRA_DATA_TITLE_IMAGE_URL = "titleImageUrl";
    private static final String INTENT_EXTRA_DATA_QUERY_TYPE = "queryType";
    private static final String INTENT_EXTRA_DATA_QUERY = "query";

    private RecyclerView mRecyclerView;

    public static Intent newInstance(Context context, PlaceType placeType, String title, String titleImageUrl, String queryType, String query)
    {
        Intent intent = new Intent(context, CollectionSearchActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_PLACETYPE, placeType.name());
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

        PlaceType placeType = PlaceType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_PLACETYPE));
        String title = intent.getStringExtra(INTENT_EXTRA_DATA_TITLE);
        String titleImageUrl = intent.getStringExtra(INTENT_EXTRA_DATA_TITLE_IMAGE_URL);
        String queryType = intent.getStringExtra(INTENT_EXTRA_DATA_QUERY_TYPE);
        String query = intent.getStringExtra(INTENT_EXTRA_DATA_QUERY);

        if (QUERY_TYPE_INDEX.equalsIgnoreCase(queryType) == true)
        {

        } else if (QUERY_TYPE_SEARCH.equalsIgnoreCase(queryType) == true)
        {

        }

    }

    private void initLayout()
    {

    }


    @Override
    public void onClick(View v)
    {

    }
}
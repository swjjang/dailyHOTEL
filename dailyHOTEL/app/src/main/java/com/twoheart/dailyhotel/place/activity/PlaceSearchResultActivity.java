package com.twoheart.dailyhotel.place.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.screen.common.BaseActivity;
import com.twoheart.dailyhotel.util.DailyRecentSearches;
import com.twoheart.dailyhotel.util.StringFilter;

import java.util.List;

public abstract class PlaceSearchResultActivity extends BaseActivity
{
    private View mToolbar;
    protected RecyclerView mRecyclerView;
    protected TextView mResultTextView;

    private View mEmptyLayout;

    protected abstract void initIntent(Intent intent);

    protected abstract void initToolbarLayout(View view);

    protected abstract void requestSearch();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result);

        initIntent(getIntent());

        initLayout();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        requestSearch();
    }

    protected void initLayout()
    {
        initToolbarLayout();

        mEmptyLayout = findViewById(R.id.emptyLayout);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycleView);
        mResultTextView = (TextView)findViewById(R.id.resultCountView);
    }

    private void initToolbarLayout()
    {
        mToolbar = findViewById(R.id.toolbar);

        initToolbarLayout(mToolbar);
    }

    protected void updateResultCount(int count)
    {
        if(mResultTextView == null)
        {
            return;
        }

        mResultTextView.setText(getString(R.string.label_searchresult_resultcount, count));
    }

    protected void showEmptyLayout()
    {
        mEmptyLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    protected void showListLayout()
    {
        mEmptyLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}

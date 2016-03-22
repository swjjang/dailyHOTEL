package com.twoheart.dailyhotel.place.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public abstract class PlaceSearchActivity extends BaseActivity implements View.OnClickListener
{
    private static final int REQUEST_AUTO_COMPLETE_MILLIS = 500;

    private View mToolbar;

    private View mSearchLayout;
    private ViewGroup mAutoCompleteLayout;
    private View mSearchingView;
    private ViewGroup mRcentContentsLayout;

    private EditText mSearchEditText;

    private DailyRecentSearches mDailyRecentSearches;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            requestAutoComplete((String) msg.obj, onAutoCompleteResultListener);
        }
    };

    protected interface OnAutoCompleteResultListener
    {
        void onAutoCompleteResultListener(List<Keyword> keywordList);
    }

    protected abstract void initIntent(Intent intent);

    protected abstract String getAroundPlaceString();

    protected abstract String getRecentSearches();

    protected abstract void writeRecentSearches(String text);

    protected abstract void deleteAllRecentSearches();

    protected abstract void requestAutoComplete(String text, OnAutoCompleteResultListener listener);

    protected abstract void showSearchResult(String text);

    protected abstract void showSearchResult(Keyword keyword);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        initIntent(getIntent());

        initLayout();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        writeRecentSearches(mDailyRecentSearches.toString());
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
    }

    private void initLayout()
    {
        initToolbarLayout();
        initAroundLayout();
        initSearchLayout();
    }

    private void initToolbarLayout()
    {
        mToolbar = findViewById(R.id.toolbar);

        mSearchEditText = (EditText) mToolbar.findViewById(R.id.searchEditText);

        StringFilter stringFilter = new StringFilter(this);
        InputFilter[] allowAlphanumericHangul = new InputFilter[1];
        allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;

        mSearchEditText.setFilters(allowAlphanumericHangul);

        View searchView = mToolbar.findViewById(R.id.searchView);
        searchView.setOnClickListener(this);

        final View deleteView = mToolbar.findViewById(R.id.deleteView);
        deleteView.setOnClickListener(this);
        deleteView.setVisibility(View.INVISIBLE);

        mSearchEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                mHandler.removeMessages(0);

                if (s.length() == 0)
                {
                    deleteView.setVisibility(View.INVISIBLE);

                    showRecentSearchesView();
                } else
                {
                    deleteView.setVisibility(View.VISIBLE);

                    if (s.length() >= 2)
                    {
                        Message message = mHandler.obtainMessage(0, s.toString());
                        mHandler.sendMessageDelayed(message, REQUEST_AUTO_COMPLETE_MILLIS);

                        showSearchingView();
                    } else
                    {
                        hideSearchView();
                    }
                }
            }
        });

        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_SEARCH:
                        showSearchResult(v.getText().toString());
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void initAroundLayout()
    {
        View searchAroundLayout = findViewById(R.id.searchAroundLayout);
        searchAroundLayout.setOnClickListener(this);

        TextView text01View = (TextView) searchAroundLayout.findViewById(R.id.text01View);
        text01View.setText(getAroundPlaceString());
    }

    private void initSearchLayout()
    {
        mSearchLayout = findViewById(R.id.searchLayout);
        mSearchingView = mSearchLayout.findViewById(R.id.searchingView);
        mSearchingView.setVisibility(View.GONE);

        // 내주변 호텔 보기
        View searchAroundLayout = mSearchLayout.findViewById(R.id.searchAroundLayout);
        searchAroundLayout.setOnClickListener(this);

        initRecentSearchesLayout(mSearchLayout);
        initAutoCompleteLayout(mSearchLayout);
    }

    private void initRecentSearchesLayout(View view)
    {
        // 최근 검색어
        // 전체 삭제
        View deleteRecentlyView = view.findViewById(R.id.deleteAllView);
        deleteRecentlyView.setOnClickListener(this);

        // 목록
        mRcentContentsLayout = (ViewGroup) findViewById(R.id.contentsLayout);

        mDailyRecentSearches = new DailyRecentSearches(getRecentSearches());
        updateRecentSearchesLayout(mRcentContentsLayout, mDailyRecentSearches.getList());
    }

    private void updateRecentSearchesLayout(ViewGroup viewGroup, List<String> keywordList)
    {
        if (viewGroup == null)
        {
            return;
        }

        viewGroup.removeAllViews();

        if (keywordList == null || keywordList.size() == 0)
        {
            View view = LayoutInflater.from(this).inflate(R.layout.list_row_search_recently, null, false);

            ImageView imageView = (ImageView) view.findViewById(R.id.iconView);
            TextView textView = (TextView) view.findViewById(R.id.textView);

            imageView.setImageResource(getRecentlyIcon(0));
            textView.setText("최근 검색 내용이 없습니다.");

            mRcentContentsLayout.addView(view);
        } else
        {
            View.OnClickListener onClickListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    showSearchResult(((TextView) v).getText().toString());
                }
            };

            for (String text : keywordList)
            {
                View view = LayoutInflater.from(this).inflate(R.layout.list_row_search_recently, null, false);
                view.setOnClickListener(onClickListener);

                ImageView imageView = (ImageView) view.findViewById(R.id.iconView);
                TextView textView = (TextView) view.findViewById(R.id.textView);

                String[] values = text.split("\\:");

                imageView.setImageResource(getRecentlyIcon(Integer.parseInt(values[0])));
                textView.setText(values[1]);

                viewGroup.addView(view);
            }
        }
    }

    private void initAutoCompleteLayout(View view)
    {
        mAutoCompleteLayout = (ViewGroup) view.findViewById(R.id.autoCompleteLayout);
        mAutoCompleteLayout.setVisibility(View.GONE);


    }

    private void updateAutoCompleteLayout(ViewGroup viewGroup, List<Keyword> keywordList)
    {
        if (viewGroup == null)
        {
            return;
        }

        viewGroup.removeAllViews();

        if (keywordList == null || keywordList.size() == 0)
        {
        } else
        {
            View.OnClickListener onClickListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    showSearchResult((Keyword) v.getTag());
                }
            };

            for (Keyword keyword : keywordList)
            {
                View view = LayoutInflater.from(this).inflate(R.layout.list_row_search_autocomplete, null, false);
                view.setOnClickListener(onClickListener);
                view.setTag(keyword);

                TextView textView01 = (TextView) view.findViewById(R.id.textView01);
                TextView textView02 = (TextView) view.findViewById(R.id.textView02);

                textView01.setText(keyword.name);
                textView02.setText(keyword.price + getString(R.string.currency));

                viewGroup.addView(view);
            }
        }
    }

    private void showRecentSearchesView()
    {
        mSearchingView.setVisibility(View.GONE);
        mAutoCompleteLayout.setVisibility(View.GONE);
        mRcentContentsLayout.setVisibility(View.VISIBLE);
    }

    private void showSearchingView()
    {
        mSearchingView.setVisibility(View.VISIBLE);
        mAutoCompleteLayout.setVisibility(View.GONE);
        mRcentContentsLayout.setVisibility(View.GONE);
    }

    private void showAutoCompleteView()
    {
        mSearchingView.setVisibility(View.GONE);
        mAutoCompleteLayout.setVisibility(View.VISIBLE);
        mRcentContentsLayout.setVisibility(View.GONE);
    }

    private void hideSearchView()
    {
        mSearchingView.setVisibility(View.GONE);
        mAutoCompleteLayout.setVisibility(View.GONE);
        mRcentContentsLayout.setVisibility(View.GONE);
    }

    private int getRecentlyIcon(int type)
    {
        return 0;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.searchAroundLayout:
            {

                break;
            }

            case R.id.searchView:
            {
                showSearchResult(mSearchEditText.getText().toString());

                String text = String.format("0:%s", mSearchEditText.getText().toString());
                mDailyRecentSearches.addString(text);
                break;
            }

            case R.id.deleteAllView:
            {
                mDailyRecentSearches.clear();
                deleteAllRecentSearches();
                break;
            }
        }
    }

    private OnAutoCompleteResultListener onAutoCompleteResultListener = new OnAutoCompleteResultListener()
    {
        @Override
        public void onAutoCompleteResultListener(List<Keyword> keywordList)
        {
            updateAutoCompleteLayout(mAutoCompleteLayout, keywordList);
        }
    };
}

package com.twoheart.dailyhotel.place.layout;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyRecentSearches;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;
import java.util.List;

public abstract class PlaceSearchLayout extends BaseLayout implements View.OnClickListener
{
    private static final int DELAY_AUTO_COMPLETE_MILLIS = 100;

    private static final int DEFAULT_ICON = 0;
    private static final int HOTEL_ICON = 1;
    private static final int GOURMET_ICON = 2;

    private View mToolbar;

    private View mSearchLayout;
    private ViewGroup mAutoCompleteLayout;
    private View mAutoCompleteScrollLayout;
    private View mSearchingView;
    private View mRecentSearchLayout;
    private ViewGroup mRcentContentsLayout;
    private View mDeleteAllRecentSearchesView;

    private EditText mSearchEditText;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            ((OnEventListener) mOnEventListener).onAutoCompleteKeyword((String) msg.obj);
        }
    };

    public interface OnEventListener extends OnBaseEventListener
    {
        void onResetKeyword();

        void onShowTermsOfLocationDialog();

        void onDeleteRecentSearches();

        void onAutoCompleteKeyword(String keyword);

        void onSearchResult(String text);

        void onSearchResult(Keyword keyword);
    }

    protected abstract String getAroundPlaceString();

    protected abstract String getSearchHintText();

    public PlaceSearchLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbarLayout(view);
        initAroundLayout(view);
        initSearchLayout(view);
    }

    private void initToolbarLayout(View view)
    {
        mToolbar = view.findViewById(R.id.toolbar);

        View backView = mToolbar.findViewById(R.id.backImageView);
        backView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).finish();
            }
        });

        mSearchEditText = (EditText) mToolbar.findViewById(R.id.searchEditText);
        mSearchEditText.setHint(getSearchHintText());

        StringFilter stringFilter = new StringFilter(mContext);
        InputFilter[] allowAlphanumericHangul = new InputFilter[2];
        allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;
        allowAlphanumericHangul[1] = new InputFilter.LengthFilter(20);

        mSearchEditText.setFilters(allowAlphanumericHangul);

        final View searchView = mToolbar.findViewById(R.id.searchView);
        searchView.setOnClickListener(this);
        searchView.setEnabled(false);

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
                    searchView.setEnabled(false);

                    updateAutoCompleteLayout(mAutoCompleteLayout, null, null);

                    showRecentSearchesView();
                } else
                {
                    if (s.length() == 1 && s.charAt(0) == ' ')
                    {
                        s.delete(0, 1);
                        return;
                    }

                    deleteView.setVisibility(View.VISIBLE);
                    searchView.setEnabled(true);

                    Message message = mHandler.obtainMessage(0, s.toString());
                    mHandler.sendMessageDelayed(message, DELAY_AUTO_COMPLETE_MILLIS);

                    showAutoCompleteView();
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
                        validateKeyword(v.getText().toString());
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    public void resetSearchKeyword()
    {
        mSearchEditText.setText(null);
    }

    private void initAroundLayout(View view)
    {
        View searchAroundLayout = view.findViewById(R.id.searchAroundLayout);
        searchAroundLayout.setOnClickListener(this);

        TextView text01View = (TextView) searchAroundLayout.findViewById(R.id.text01View);
        text01View.setText(getAroundPlaceString());

        if (DailyPreference.getInstance(mContext).isAgreeTermsOfLocation() == true)
        {
            TextView text02View = (TextView) searchAroundLayout.findViewById(R.id.text02View);
            text02View.setVisibility(View.GONE);
        }
    }

    private void initSearchLayout(View view)
    {
        mSearchLayout = view.findViewById(R.id.searchLayout);
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
        mDeleteAllRecentSearchesView = view.findViewById(R.id.deleteAllView);
        mDeleteAllRecentSearchesView.setOnClickListener(this);

        mRecentSearchLayout = view.findViewById(R.id.recentSearchLayout);
        mRecentSearchLayout.setVisibility(View.VISIBLE);

        // 목록
        mRcentContentsLayout = (ViewGroup) view.findViewById(R.id.contentsLayout);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validateKeyword((String) v.getTag());
            }
        };

        for (int i = 0; i < DailyRecentSearches.MAX_KEYWORD; i++)
        {
            View keywordView = LayoutInflater.from(mContext).inflate(R.layout.list_row_search_recently, mRcentContentsLayout, false);
            keywordView.setOnClickListener(onClickListener);

            mRcentContentsLayout.addView(keywordView);
        }
    }

    public void updateRecentSearchesLayout(List<String> keywordList)
    {
        updateRecentSearchesLayout(mRcentContentsLayout, keywordList);
    }

    private void updateRecentSearchesLayout(ViewGroup viewGroup, List<String> keywordList)
    {
        if (viewGroup == null)
        {
            return;
        }

        if (keywordList == null || keywordList.size() == 0)
        {
            mDeleteAllRecentSearchesView.setEnabled(false);

            View view = viewGroup.getChildAt(0);
            view.setVisibility(View.VISIBLE);

            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setTextColor(mContext.getResources().getColor(R.color.search_hint_text));
            textView.setCompoundDrawablesWithIntrinsicBounds(getRecentSearchesIcon(DEFAULT_ICON), 0, 0, 0);
            textView.setText(R.string.label_search_recentsearches_none);

            View underLineView = view.findViewById(R.id.underLineView);
            underLineView.setVisibility(View.GONE);

            int childCount = viewGroup.getChildCount();

            for (int i = 1; i < childCount; i++)
            {
                viewGroup.getChildAt(i).setVisibility(View.GONE);
            }
        } else
        {
            mDeleteAllRecentSearchesView.setEnabled(true);

            int size = keywordList.size();
            String[] values;
            TextView textView;
            View view;

            int childCount = viewGroup.getChildCount();

            for (int i = 0; i < childCount; i++)
            {
                view = viewGroup.getChildAt(i);

                if (i < size)
                {
                    view.setVisibility(View.VISIBLE);
                } else
                {
                    view.setVisibility(View.GONE);
                    view.setTag(null);
                    continue;
                }

                values = keywordList.get(i).split("\\:");

                view.setTag(values[1]);

                textView = (TextView) view.findViewById(R.id.textView);
                textView.setCompoundDrawablesWithIntrinsicBounds(getRecentSearchesIcon(Integer.parseInt(values[0])), 0, 0, 0);
                textView.setText(values[1]);

                View underLineView = view.findViewById(R.id.underLineView);

                if (i < size - 1)
                {
                    underLineView.setVisibility(View.VISIBLE);
                } else
                {
                    underLineView.setVisibility(View.GONE);
                }
            }
        }
    }

    private void initAutoCompleteLayout(View view)
    {
        mAutoCompleteScrollLayout = view.findViewById(R.id.autoCompleteScrollLayout);
        mAutoCompleteLayout = (ViewGroup) mAutoCompleteScrollLayout.findViewById(R.id.autoCompleteLayout);

        mAutoCompleteScrollLayout.setVisibility(View.GONE);
    }

    public void updateAutoCompleteLayout(String text, List<Keyword> keywordList)
    {
        if (mSearchEditText.length() == 0)
        {
            return;
        }

        showAutoCompleteView();

        updateAutoCompleteLayout(mAutoCompleteLayout, text, keywordList);
    }

    private void updateAutoCompleteLayout(ViewGroup viewGroup, String text, List<Keyword> keywordList)
    {
        if (viewGroup == null)
        {
            return;
        }

        if (keywordList == null || keywordList.size() == 0)
        {
            hideSearchView();
        } else
        {
            View.OnClickListener onClickListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    validateKeyword((Keyword) v.getTag());
                }
            };

            int childCount = viewGroup.getChildCount();
            int size = keywordList.size();

            int length = Math.max(childCount, size);
            View view;
            Keyword keyword;

            for (int i = 0; i < length; i++)
            {
                if (i >= childCount)
                {
                    view = LayoutInflater.from(mContext).inflate(R.layout.list_row_search_autocomplete, viewGroup, false);
                    view.setOnClickListener(onClickListener);
                    viewGroup.addView(view);
                } else
                {
                    view = viewGroup.getChildAt(i);
                }

                if (i >= size)
                {
                    view.setVisibility(View.GONE);
                    view.setTag(null);
                    continue;
                } else
                {
                    view.setVisibility(View.VISIBLE);

                    keyword = keywordList.get(i);

                    view.setTag(keyword);

                    TextView textView01 = (TextView) view.findViewById(R.id.textView01);
                    TextView textView02 = (TextView) view.findViewById(R.id.textView02);

                    if (keyword.price > 0)
                    {
                        int startIndex = keyword.name.lastIndexOf(text);
                        int endIndex = startIndex + text.length();

                        if (startIndex >= 0)
                        {
                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(keyword.name);
                            spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
                                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            textView01.setText(spannableStringBuilder);
                        } else
                        {
                            textView01.setText(keyword.name);
                        }

                        DecimalFormat comma = new DecimalFormat("###,##0");
                        String strPrice = comma.format(keyword.price);

                        textView02.setVisibility(View.VISIBLE);
                        textView02.setText(strPrice + mContext.getString(R.string.currency));
                    } else
                    {
                        textView01.setText(keyword.name);
                        textView02.setVisibility(View.INVISIBLE);
                    }

                    View underLineView = view.findViewById(R.id.underLineView);

                    if (i < size - 1)
                    {
                        underLineView.setVisibility(View.VISIBLE);
                    } else
                    {
                        underLineView.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void showRecentSearchesView()
    {
        mSearchingView.setVisibility(View.GONE);
        mAutoCompleteScrollLayout.setVisibility(View.GONE);
        mRecentSearchLayout.setVisibility(View.VISIBLE);
    }

    private void showSearchingView()
    {
        mSearchingView.setVisibility(View.VISIBLE);
        mAutoCompleteScrollLayout.setVisibility(View.GONE);
        mRecentSearchLayout.setVisibility(View.GONE);
    }

    private void showAutoCompleteView()
    {
        mSearchingView.setVisibility(View.GONE);
        mAutoCompleteScrollLayout.setVisibility(View.VISIBLE);
        mRecentSearchLayout.setVisibility(View.GONE);
    }

    private void hideSearchView()
    {
        mSearchingView.setVisibility(View.GONE);
        mAutoCompleteScrollLayout.setVisibility(View.GONE);
        mRecentSearchLayout.setVisibility(View.GONE);
    }

    private void validateKeyword(String keyword)
    {
        String text = keyword.trim();

        if (Util.isTextEmpty(text) == true)
        {
            return;
        }

        ((OnEventListener) mOnEventListener).onSearchResult(text);
    }

    private void validateKeyword(Keyword keyword)
    {
        ((OnEventListener) mOnEventListener).onSearchResult(keyword);
    }

    private int getRecentSearchesIcon(int type)
    {
        switch (type)
        {
            case HOTEL_ICON:
                return R.drawable.search_ic_02_hotel;

            case GOURMET_ICON:
                return R.drawable.search_ic_02_gourmet;

            default:
                return R.drawable.search_ic_03_recent;
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.searchAroundLayout:
            {
                ((OnEventListener) mOnEventListener).onShowTermsOfLocationDialog();
                break;
            }

            case R.id.searchView:
            {
                validateKeyword(mSearchEditText.getText().toString());
                break;
            }

            case R.id.deleteAllView:
            {
                ((OnEventListener) mOnEventListener).onDeleteRecentSearches();
                break;
            }

            case R.id.deleteView:
            {
                ((OnEventListener) mOnEventListener).onResetKeyword();
                break;
            }
        }
    }
}

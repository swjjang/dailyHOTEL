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
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyRecentSearches;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;

import java.text.DecimalFormat;
import java.util.List;

public abstract class PlaceSearchLayout extends BaseLayout implements View.OnClickListener
{
    private static final int DELAY_AUTO_COMPLETE_MILLIS = 100;
    private static final int DELAY_HIDE_AUTO_COMPLETE_MILLIS = 500;

    protected static final int DEFAULT_ICON = 0;
    public static final int HOTEL_ICON = 1;
    protected static final int GOURMET_ICON = 2;

    private static final int HANDLER_MESSAGE_REQUEST_AUTOCOMPLETE = 0;
    private static final int HANDLER_MESSAGE_HIDE_AUTOCOMPLETE = 1;

    private View mToolbar;

    private View mSearchLayout;
    private View mTermsOfLocationView;
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
            switch (msg.what)
            {
                case HANDLER_MESSAGE_REQUEST_AUTOCOMPLETE:
                    ((OnEventListener) mOnEventListener).onAutoCompleteKeyword((String) msg.obj);
                    break;

                case HANDLER_MESSAGE_HIDE_AUTOCOMPLETE:
                    hideAutoCompleteView();
                    break;
            }

        }
    };

    public interface OnEventListener extends OnBaseEventListener
    {
        void onResetKeyword();

        void onShowTermsOfLocationDialog();

        void onSearchMyLocation();

        void onDeleteRecentSearches();

        void onAutoCompleteKeyword(String keyword);

        void onSearch(String text);

        void onSearch(String text, Keyword keyword);
    }

    protected abstract String getAroundPlaceString();

    protected abstract String getSearchHintText();

    protected abstract int getRecentSearchesIcon(int type);

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

        if (Util.getLCDWidth(mContext) < 720)
        {
            mSearchEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        }

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
                mHandler.removeMessages(HANDLER_MESSAGE_REQUEST_AUTOCOMPLETE);
                mHandler.removeMessages(HANDLER_MESSAGE_HIDE_AUTOCOMPLETE);

                int length = s.length();

                if (length == 0)
                {
                    deleteView.setVisibility(View.INVISIBLE);
                    searchView.setEnabled(false);

                    updateAutoCompleteLayout(mAutoCompleteLayout, null, null);

                    showRecentSearchesView();
                } else
                {
                    if (length == 1 && s.charAt(0) == ' ')
                    {
                        s.delete(0, 1);
                        return;
                    }

                    if (length > 1 && s.charAt(length - 1) == ' ')
                    {
                        if (s.charAt(length - 2) == ' ')
                        {
                            s.delete(length - 1, length);
                        }
                        return;
                    }

                    deleteView.setVisibility(View.VISIBLE);
                    searchView.setEnabled(true);

                    Message message = mHandler.obtainMessage(HANDLER_MESSAGE_REQUEST_AUTOCOMPLETE, s.toString());
                    mHandler.sendMessageDelayed(message, DELAY_AUTO_COMPLETE_MILLIS);
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

    public void showSearchKeyboard()
    {
        mSearchEditText.requestFocus();
        mSearchEditText.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mSearchEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500);
    }

    private void initAroundLayout(View view)
    {
        View searchAroundLayout = view.findViewById(R.id.searchAroundLayout);
        searchAroundLayout.setOnClickListener(this);

        TextView text01View = (TextView) searchAroundLayout.findViewById(R.id.text01View);
        text01View.setText(getAroundPlaceString());

        mTermsOfLocationView = searchAroundLayout.findViewById(R.id.text02View);

        updateTermsOfLocationLayout(mTermsOfLocationView);
    }

    private void updateTermsOfLocationLayout(View view)
    {
        if (DailyPreference.getInstance(mContext).isAgreeTermsOfLocation() == true)
        {
            view.setVisibility(View.GONE);
        } else
        {
            view.setVisibility(View.VISIBLE);
        }
    }

    public void updateTermsOfLocationLayout()
    {
        updateTermsOfLocationLayout(mTermsOfLocationView);
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
        ScrollView recentSearchesScrollLayout = (ScrollView) view.findViewById(R.id.recentSearchesScrollLayout);
        EdgeEffectColor.setEdgeGlowColor(recentSearchesScrollLayout, mContext.getResources().getColor(R.color.over_scroll_edge));

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
                Keyword keyword = (Keyword) v.getTag();

                if (keyword != null)
                {
                    validateKeyword(keyword);
                }
            }
        };

        for (int i = 0; i < DailyRecentSearches.MAX_KEYWORD; i++)
        {
            View keywordView = LayoutInflater.from(mContext).inflate(R.layout.list_row_search_recently, mRcentContentsLayout, false);
            keywordView.setOnClickListener(onClickListener);

            mRcentContentsLayout.addView(keywordView);
        }
    }

    public void updateRecentSearchesLayout(List<Keyword> keywordList)
    {
        updateRecentSearchesLayout(mRcentContentsLayout, keywordList);
    }

    private void updateRecentSearchesLayout(ViewGroup viewGroup, List<Keyword> keywordList)
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
            view.setTag(null);

            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setTextColor(mContext.getResources().getColor(R.color.search_hint_text));
            textView.setCompoundDrawablesWithIntrinsicBounds(getRecentSearchesIcon(DEFAULT_ICON), 0, 0, 0);
            textView.setText(R.string.label_search_recentsearches_none);

            int childCount = viewGroup.getChildCount();

            for (int i = 1; i < childCount; i++)
            {
                viewGroup.getChildAt(i).setVisibility(View.GONE);
            }
        } else
        {
            mDeleteAllRecentSearchesView.setEnabled(true);

            int size = keywordList.size();
            Keyword recentKeyword;
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

                recentKeyword = keywordList.get(i);

                view.setTag(recentKeyword);

                textView = (TextView) view.findViewById(R.id.textView);
                textView.setTextColor(mContext.getResources().getColor(R.color.search_text));
                textView.setCompoundDrawablesWithIntrinsicBounds(getRecentSearchesIcon(recentKeyword.icon), 0, 0, 0);
                textView.setText(recentKeyword.name);
            }
        }
    }

    private void initAutoCompleteLayout(View view)
    {
        mAutoCompleteScrollLayout = view.findViewById(R.id.autoCompleteScrollLayout);
        mAutoCompleteLayout = (ViewGroup) mAutoCompleteScrollLayout.findViewById(R.id.autoCompleteLayout);

        mAutoCompleteScrollLayout.setVisibility(View.GONE);

        EdgeEffectColor.setEdgeGlowColor((ScrollView) mAutoCompleteScrollLayout, mContext.getResources().getColor(R.color.over_scroll_edge));
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

    private void resetAutoCompleteLayout(ViewGroup viewGroup)
    {
        if (viewGroup == null)
        {
            return;
        }

        View view;
        int childCount = viewGroup.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            view = viewGroup.getChildAt(i);
            view.setVisibility(View.GONE);
            view.setTag(null);
        }
    }

    private void updateAutoCompleteLayout(ViewGroup viewGroup, String text, List<Keyword> keywordList)
    {
        if (viewGroup == null)
        {
            return;
        }

        mHandler.removeMessages(HANDLER_MESSAGE_HIDE_AUTOCOMPLETE);

        if (keywordList == null || keywordList.size() == 0)
        {
            mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_HIDE_AUTOCOMPLETE, DELAY_HIDE_AUTO_COMPLETE_MILLIS);
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
                        int separatorIndex = keyword.name.indexOf('>');
                        int startIndex = keyword.name.lastIndexOf(text);
                        int endIndex = startIndex + text.length();

                        if (startIndex > separatorIndex)
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

    private void hideAutoCompleteView()
    {
        mAutoCompleteScrollLayout.setVisibility(View.GONE);

        resetAutoCompleteLayout(mAutoCompleteLayout);
    }

    private void validateKeyword(String keyword)
    {
        String text = keyword.trim();

        if (Util.isTextEmpty(text) == true)
        {
            return;
        }

        ((OnEventListener) mOnEventListener).onSearch(text);
    }

    private void validateKeyword(Keyword keyword)
    {
        ((OnEventListener) mOnEventListener).onSearch(mSearchEditText.getText().toString().trim(), keyword);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.searchAroundLayout:
            {
                if (DailyPreference.getInstance(mContext).isAgreeTermsOfLocation() == true)
                {
                    ((OnEventListener) mOnEventListener).onSearchMyLocation();
                } else
                {
                    ((OnEventListener) mOnEventListener).onShowTermsOfLocationDialog();
                }
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

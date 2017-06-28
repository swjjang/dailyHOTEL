package com.twoheart.dailyhotel.place.layout;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyScrollView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyRecentSearches;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class PlaceSearchLayout extends BaseLayout implements View.OnClickListener
{
    private static final int DELAY_AUTO_COMPLETE_MILLIS = 100;
    private static final int DELAY_HIDE_AUTO_COMPLETE_MILLIS = 500;

    protected static final int DEFAULT_ICON = 0;
    public static final int HOTEL_ICON = 1;
    public static final int GOURMET_ICON = 2;

    private static final int HANDLER_MESSAGE_REQUEST_AUTOCOMPLETE = 0;
    private static final int HANDLER_MESSAGE_HIDE_AUTOCOMPLETE = 1;

    private TextView mTermsOfLocationView;
    ViewGroup mAutoCompleteLayout;
    private DailyScrollView mAutoCompleteScrollLayout;
    private View mRecentSearchLayout;
    private ViewGroup mRecentContentsLayout;
    private View mDeleteAllRecentSearchesView;

    EditText mSearchEditText;
    private TextView mDateTextView;

    Handler mHandler;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onResetKeyword();

        void onSearchMyLocation();

        void onDeleteRecentSearches();

        void onAutoCompleteKeyword(String keyword);

        void onSearch(String text);

        void onSearch(String text, Keyword keyword);

        void onCalendarClick(boolean isAnimation);

        void onSearchEnabled(boolean enabled);
    }

    protected abstract String getAroundPlaceText();

    protected abstract SpannableString getAroundPlaceTermText();

    protected abstract String getSearchHintText();

    protected abstract int getRecentSearchesIcon(int type);

    public PlaceSearchLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);

        mHandler = new SearchHandler(this);
    }

    @Override
    protected void initLayout(View view)
    {
        initSearchLayout(view);
        initCalendarLayout(view);
        initAroundLayout(view);
        initSearchKeywordLayout(view);
    }

    private void initSearchLayout(View view)
    {
        mSearchEditText = (EditText) view.findViewById(R.id.searchEditText);
        mSearchEditText.setHint(getSearchHintText());

        StringFilter stringFilter = new StringFilter(mContext);
        InputFilter[] allowSearchFilter = new InputFilter[2];
        allowSearchFilter[0] = stringFilter.allowSearchFilter;
        allowSearchFilter[1] = new InputFilter.LengthFilter(20);

        mSearchEditText.setFilters(allowSearchFilter);
        mSearchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        final View deleteView = view.findViewById(R.id.deleteView);
        deleteView.setOnClickListener(this);
        deleteView.setVisibility(View.GONE);

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
                if (mAutoCompleteLayout == null)
                {
                    return;
                }

                mHandler.removeMessages(HANDLER_MESSAGE_REQUEST_AUTOCOMPLETE);
                mHandler.removeMessages(HANDLER_MESSAGE_HIDE_AUTOCOMPLETE);

                int length = s.length();

                if (length == 0)
                {
                    deleteView.setVisibility(View.GONE);
                    ((OnEventListener) mOnEventListener).onSearchEnabled(false);

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
                    ((OnEventListener) mOnEventListener).onSearchEnabled(true);

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

    private void initCalendarLayout(View view)
    {
        mDateTextView = (TextView) view.findViewById(R.id.calendarTextView);

        mDateTextView.setOnClickListener(this);
    }

    private void initAroundLayout(View view)
    {
        View searchAroundLayout = view.findViewById(R.id.searchAroundLayout);
        searchAroundLayout.setOnClickListener(this);

        TextView text01View = (TextView) searchAroundLayout.findViewById(R.id.text01View);
        text01View.setText(getAroundPlaceText());

        mTermsOfLocationView = (TextView) searchAroundLayout.findViewById(R.id.text02View);
        mTermsOfLocationView.setText(getAroundPlaceTermText());

        updateTermsOfLocationLayout(mTermsOfLocationView);
    }

    private void initSearchKeywordLayout(View view)
    {
        initRecentSearchesLayout(view);
        initAutoCompleteLayout(view);
    }

    public void resetSearchKeyword()
    {
        if (mSearchEditText == null)
        {
            return;
        }

        mSearchEditText.setText(null);
    }

    public void clearSearchKeywordFocus()
    {
        if (mSearchEditText == null)
        {
            return;
        }

        mSearchEditText.setFocusable(false);
        mSearchEditText.setFocusableInTouchMode(false);
        mSearchEditText.clearFocus();
    }

    public String getSearchKeyWord()
    {
        return mSearchEditText.getText().toString();
    }

    public void setSearchKeyword(String word)
    {
        if (mSearchEditText == null)
        {
            return;
        }

        mSearchEditText.setText(word);
    }

    public void showSearchKeyboard()
    {
        if (mSearchEditText == null)
        {
            return;
        }

        mSearchEditText.setFocusable(true);
        mSearchEditText.setFocusableInTouchMode(true);
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

    public void hideSearchKeyboard()
    {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void setDataText(String date)
    {
        if (mDateTextView == null)
        {
            return;
        }

        mDateTextView.setText(date);
    }

    public void requestUpdateAutoCompleteLayout()
    {
        if (mSearchEditText == null)
        {
            return;
        }

        mSearchEditText.setText(mSearchEditText.getText().toString());
        mSearchEditText.setSelection(mSearchEditText.length());
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

    private void initRecentSearchesLayout(View view)
    {
        DailyScrollView recentSearchesScrollLayout = (DailyScrollView) view.findViewById(R.id.recentSearchesScrollLayout);
        recentSearchesScrollLayout.setOnScrollChangedListener(new DailyScrollView.OnScrollChangedListener()
        {
            private int mDistance;
            boolean mIsHide;

            @Override
            public void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt)
            {
                if (mIsHide == true)
                {

                } else
                {
                    if (scrollView.getHeight() < ScreenUtils.getScreenHeight(mContext) / 2)
                    {
                        mDistance += (t - oldt);

                        if (mDistance > ScreenUtils.dpToPx(mContext, 41) == true)
                        {
                            mDistance = 0;
                            mIsHide = true;

                            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                            mHandler.postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    mIsHide = false;
                                }
                            }, 1000);
                        }
                    }
                }
            }
        });

        EdgeEffectColor.setEdgeGlowColor(recentSearchesScrollLayout, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        // 최근 검색어
        // 전체 삭제
        mDeleteAllRecentSearchesView = view.findViewById(R.id.deleteAllView);
        mDeleteAllRecentSearchesView.setOnClickListener(this);

        mRecentSearchLayout = view.findViewById(R.id.recentSearchLayout);
        mRecentSearchLayout.setVisibility(View.VISIBLE);

        // 목록
        mRecentContentsLayout = (ViewGroup) view.findViewById(R.id.contentsLayout);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Object tag = v.getTag();

                if (tag != null && tag instanceof Keyword == true)
                {
                    validateKeyword((Keyword) tag);
                }
            }
        };

        for (int i = 0; i < DailyRecentSearches.MAX_KEYWORD; i++)
        {
            View keywordView = LayoutInflater.from(mContext).inflate(R.layout.list_row_search_recently, mRecentContentsLayout, false);
            keywordView.setOnClickListener(onClickListener);

            mRecentContentsLayout.addView(keywordView);
        }
    }

    public void updateRecentSearchesLayout(List<Keyword> keywordList)
    {
        updateRecentSearchesLayout(mRecentContentsLayout, keywordList);
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
        mAutoCompleteScrollLayout = (DailyScrollView) view.findViewById(R.id.autoCompleteScrollLayout);
        mAutoCompleteLayout = (ViewGroup) mAutoCompleteScrollLayout.findViewById(R.id.autoCompleteLayout);

        mAutoCompleteScrollLayout.setVisibility(View.GONE);
        mAutoCompleteScrollLayout.setOnScrollChangedListener(new DailyScrollView.OnScrollChangedListener()
        {
            private int mDistance;
            boolean mIsHide;

            @Override
            public void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt)
            {
                if (mIsHide == true)
                {

                } else
                {
                    if (scrollView.getHeight() < ScreenUtils.getScreenHeight(mContext) / 2)
                    {
                        mDistance += (t - oldt);

                        if (mDistance > ScreenUtils.dpToPx(mContext, 41) == true)
                        {
                            mDistance = 0;
                            mIsHide = true;

                            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                            mHandler.postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    mIsHide = false;
                                }
                            }, 1000);
                        }
                    }
                }
            }
        });

        EdgeEffectColor.setEdgeGlowColor(mAutoCompleteScrollLayout, mContext.getResources().getColor(R.color.default_over_scroll_edge));
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

    void updateAutoCompleteLayout(ViewGroup viewGroup, String text, List<Keyword> keywordList)
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
                    Object tag = v.getTag();

                    if (tag != null && tag instanceof Keyword == true)
                    {
                        validateKeyword((Keyword) tag);
                    }
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
                } else
                {
                    view.setVisibility(View.VISIBLE);

                    keyword = keywordList.get(i);

                    view.setTag(keyword);

                    TextView textView01 = (TextView) view.findViewById(R.id.textView01);
                    TextView textView02 = (TextView) view.findViewById(R.id.textView02);

                    if (keyword.price > 0)
                    {
                        String keywordNameUpperCase = keyword.name.toUpperCase();
                        String textUpperCase = text.toUpperCase();

                        int separatorIndex = keywordNameUpperCase.indexOf('>');
                        int startIndex = keywordNameUpperCase.lastIndexOf(textUpperCase);
                        int endIndex = startIndex + textUpperCase.length();

                        if (startIndex > separatorIndex)
                        {
                            try
                            {
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(keyword.name);
                                spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
                                    startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                textView01.setText(spannableStringBuilder);
                            } catch (Exception e)
                            {
                                textView01.setText(keyword.name);
                            }
                        } else
                        {
                            textView01.setText(keyword.name);
                        }

                        textView02.setVisibility(View.VISIBLE);
                        textView02.setText(DailyTextUtils.getPriceFormat(mContext, keyword.price, false));
                    } else
                    {
                        textView01.setText(keyword.name);
                        textView02.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    void showRecentSearchesView()
    {
        mAutoCompleteScrollLayout.setVisibility(View.GONE);
        mRecentSearchLayout.setVisibility(View.VISIBLE);
    }

    private void showAutoCompleteView()
    {
        mAutoCompleteScrollLayout.setVisibility(View.VISIBLE);
        mRecentSearchLayout.setVisibility(View.GONE);
    }

    void hideAutoCompleteView()
    {
        mAutoCompleteScrollLayout.setVisibility(View.GONE);

        resetAutoCompleteLayout(mAutoCompleteLayout);
    }

    void validateKeyword(String keyword)
    {
        String text = keyword.trim();

        if (DailyTextUtils.isTextEmpty(text) == true)
        {
            return;
        }

        ((OnEventListener) mOnEventListener).onSearch(text);
    }

    void validateKeyword(Keyword keyword)
    {
        if (mOnEventListener == null)
        {
            Util.restartApp(mContext);
            return;
        }

        ((OnEventListener) mOnEventListener).onSearch(mSearchEditText.getText().toString().trim(), keyword);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.calendarTextView:
            {
                hideSearchKeyboard();

                mSearchEditText.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ((OnEventListener) mOnEventListener).onCalendarClick(true);
                    }
                }, 100);
                break;
            }

            case R.id.searchAroundLayout:
            {
                ((OnEventListener) mOnEventListener).onSearchMyLocation();
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

    private static class SearchHandler extends Handler
    {
        private final WeakReference<PlaceSearchLayout> mWeakReference;

        public SearchHandler(PlaceSearchLayout placeSearchLayout)
        {
            mWeakReference = new WeakReference<>(placeSearchLayout);
        }

        @Override
        public void handleMessage(Message msg)
        {
            PlaceSearchLayout placeSearchLayout = mWeakReference.get();

            if (placeSearchLayout == null)
            {
                return;
            }

            switch (msg.what)
            {
                case HANDLER_MESSAGE_REQUEST_AUTOCOMPLETE:
                    ((OnEventListener) placeSearchLayout.mOnEventListener).onAutoCompleteKeyword((String) msg.obj);
                    break;

                case HANDLER_MESSAGE_HIDE_AUTOCOMPLETE:
                    placeSearchLayout.hideAutoCompleteView();
                    break;
            }
        }
    }
}
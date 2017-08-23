package com.twoheart.dailyhotel.place.layout;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyEditText;
import com.daily.base.widget.DailyScrollView;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.SearchCalendarReturnData;
import com.daily.dailyhotel.view.DailySearchCircleIndicator;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.screen.search.SearchCardViewAdapter;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class PlaceSearchLayout extends BaseLayout implements View.OnClickListener
{
    private static final int DELAY_AUTO_COMPLETE_MILLIS = 100;

    public static final int DEFAULT_ICON = 0;
    public static final int HOTEL_ICON = 1;
    public static final int GOURMET_ICON = 2;
    public static final int TAG_ICON = 3;

    private static final int HANDLER_MESSAGE_REQUEST_AUTOCOMPLETE = 0;

    private TextView mTermsOfLocationView;
    ViewGroup mAutoCompleteLayout;
    private DailyScrollView mAutoCompleteScrollView;
    private View mAutoCompleteScrollLayout;

    protected DailySearchCircleIndicator mCircleIndicator;
    protected RecyclerView mRecyclerView;
    protected SearchCardViewAdapter mRecyclerAdapter;

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

        void onCalendarClick(boolean isAnimation, SearchCalendarReturnData returnData);

        void onSearchEnabled(boolean enabled);

        void onSearchCampaignTag(CampaignTag campaignTag);

        void onSearchRecentlyPlace(Place place);

        void onChangeAutoCompleteScrollView(boolean isShow);
    }

    protected abstract String getAroundPlaceText();

    protected abstract SpannableString getAroundPlaceTermText();

    protected abstract String getSearchHintText();

    protected abstract void updateSuggestLayout(TextView titleTextView, TextView priceTextView, Keyword keyword, String text);

    public abstract void setRecyclerViewData(List<? extends Place> recentlyList, ArrayList<CampaignTag> campaignTagList, List<Keyword> recentSearchList);

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
        initAutoCompleteLayout(view);
        initRecyclerView(view);
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

        mSearchEditText.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (MotionEvent.ACTION_UP == event.getAction())
                {
                    showSearchKeyboard();
                }

                return false;
            }
        });

        ((DailyEditText) mSearchEditText).setOnKeyImeListener(new DailyEditText.OnKeyImeListener()
        {
            @Override
            public void onKeyPreIme(int keyCode, KeyEvent event)
            {
                // keybord hide 시 이벤트 처리 - keyCode 가 back key 이면서 action up 이면서
                // editText 의 글자가 0개 일때 hide
                if (keyCode != KeyEvent.KEYCODE_BACK)
                {
                    return;
                }

                if (event == null || event.getAction() != KeyEvent.ACTION_UP)
                {
                    return;
                }

                if (mSearchEditText.getText().length() == 0)
                {
                    hideSearchKeyboard();
                    hideAutoCompleteLayout();
                }
            }
        });

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

                int length = s.length();

                if (length == 0)
                {
                    deleteView.setVisibility(View.GONE);
                    ((OnEventListener) mOnEventListener).onSearchEnabled(false);

                    hideAutoCompleteScrollView();

                    updateAutoCompleteLayout(mAutoCompleteLayout, null, null);
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

                    showAutoCompleteScrollView();

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

    private void initRecyclerView(View view)
    {
        mCircleIndicator = (DailySearchCircleIndicator) view.findViewById(R.id.searchCircleIndicator);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.searchRecyclerView);

        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setAutoMeasureEnabled(true);
        layoutManager.setReverseLayout(false);

        mRecyclerView.setLayoutManager(layoutManager);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        mCircleIndicator.setTotalCount(1);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    View view = snapHelper.findSnapView(mRecyclerView.getLayoutManager());
                    int position = mRecyclerView.getChildAdapterPosition(view);
                    mCircleIndicator.setPosition(position);
                }
            }
        });
    }

    public void setKeywordListData(List<Keyword> keywordList)
    {
        if (mRecyclerAdapter == null)
        {
            return;
        }

        mRecyclerAdapter.setKeywordListData(keywordList);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    public void resetSearchKeyword()
    {
        if (mSearchEditText == null)
        {
            return;
        }

        mSearchEditText.setText(null);
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

        showAutoCompleteLayout();

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
        //        hideAutoCompleteLayout();
        mSearchEditText.clearFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
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

    private void initAutoCompleteLayout(View view)
    {
        mAutoCompleteScrollLayout = view.findViewById(R.id.autoCompleteScrollLayout);
        mAutoCompleteScrollView = (DailyScrollView) mAutoCompleteScrollLayout.findViewById(R.id.autoCompleteScrollView);
        mAutoCompleteLayout = (ViewGroup) mAutoCompleteScrollView.findViewById(R.id.autoCompleteLayout);

        hideAutoCompleteLayout();

        mAutoCompleteScrollLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                hideSearchKeyboard();
                hideAutoCompleteLayout();
            }
        });

        mAutoCompleteScrollView.setOnScrollChangedListener(new DailyScrollView.OnScrollChangedListener()
        {
            private int mDistance;
            boolean mIsHide;

            @Override
            public void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt)
            {
                if (scrollView == null || scrollView.getVisibility() != View.VISIBLE)
                {
                    return;
                }

                if (mIsHide == true)
                {
                    return;
                }

                mDistance += (t - oldt);

                if (mDistance > ScreenUtils.dpToPx(mContext, 41) == true)
                {
                    mDistance = 0;
                    mIsHide = true;

                    hideSearchKeyboard();

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
        });

        EdgeEffectColor.setEdgeGlowColor(mAutoCompleteScrollView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
    }

    public void updateAutoCompleteLayout(String text, List<? extends Keyword> keywordList)
    {
        if (mSearchEditText.length() == 0)
        {
            return;
        }

        showAutoCompleteScrollView();

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

    void updateAutoCompleteLayout(ViewGroup viewGroup, String text, List<? extends Keyword> keywordList)
    {
        if (viewGroup == null)
        {
            return;
        }

        if (keywordList == null || keywordList.size() == 0)
        {
            resetAutoCompleteLayout(viewGroup);
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

            viewGroup.removeAllViews();

            int size = keywordList.size();
            View view;
            Keyword keyword;

            for (int i = 0; i < size; i++)
            {
                view = LayoutInflater.from(mContext).inflate(R.layout.list_row_search_autocomplete, viewGroup, false);
                view.setOnClickListener(onClickListener);
                viewGroup.addView(view);

                keyword = keywordList.get(i);
                view.setTag(keyword);

                TextView textView01 = (TextView) view.findViewById(R.id.textView01);
                TextView textView02 = (TextView) view.findViewById(R.id.textView02);

                updateSuggestLayout(textView01, textView02, keyword, text);
            }
        }
    }

    public void showAutoCompleteLayout()
    {
        mAutoCompleteScrollLayout.setVisibility(View.VISIBLE);
    }

    public void hideAutoCompleteLayout()
    {
        mAutoCompleteScrollLayout.setVisibility(View.GONE);
        hideAutoCompleteScrollView();
    }

    private void showAutoCompleteScrollView()
    {
        ((OnEventListener) mOnEventListener).onChangeAutoCompleteScrollView(true);

        mAutoCompleteScrollView.setVisibility(View.VISIBLE);
    }

    void hideAutoCompleteScrollView()
    {
        ((OnEventListener) mOnEventListener).onChangeAutoCompleteScrollView(false);

        mAutoCompleteScrollView.setVisibility(View.GONE);
        resetAutoCompleteLayout(mAutoCompleteLayout);
    }

    public boolean isShowAutoCompleteScrollView()
    {
        if (mAutoCompleteScrollView == null)
        {
            return false;
        }

        return mAutoCompleteScrollView.getVisibility() == View.VISIBLE;
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

    public void setRecyclerViewPosition(int position)
    {
        if (mRecyclerView == null || mCircleIndicator == null || mRecyclerAdapter == null)
        {
            return;
        }

        if (position < 0 || position > mRecyclerAdapter.getItemCount() - 1)
        {
            return;
        }

        mCircleIndicator.setPosition(position);
        mRecyclerView.scrollToPosition(position);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.calendarTextView:
            {
                hideSearchKeyboard();

                if (mSearchEditText.getText().length() == 0)
                {
                    hideAutoCompleteLayout();
                }

                mSearchEditText.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ((OnEventListener) mOnEventListener).onCalendarClick(true, null);
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
            }
        }
    }
}
package com.daily.dailyhotel.screen.home.search.stay.inbound.suggest;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.StaySuggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivitySearchStaySuggestDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class SearchStaySuggestView extends BaseDialogView<SearchStaySuggestView.OnEventListener, ActivitySearchStaySuggestDataBinding> //
    implements SearchStaySuggestInterface, View.OnClickListener
{
    private SuggestListAdapter mSuggestListAdapter;
    private RecentlySuggestListAdapter mRecentlySuggestListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSearchSuggest(String keyword);

        void onSuggestClick(StaySuggest staySuggest);

        void onRecentlySuggestClick(StaySuggest staySuggest);

        void onDeleteAllRecentlySuggest(boolean skipLock);

        void onDeleteRecentlySuggest(int position, StaySuggest staySuggest);

        void onVoiceSearchClick();

        void setCheckVoiceSearchEnabled();

        void onNearbyClick(StaySuggest staySuggest);
    }

    public SearchStaySuggestView(BaseActivity baseActivity, SearchStaySuggestView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivitySearchStaySuggestDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initScrollLayout(viewDataBinding);
        initSearchToolbarLayout(viewDataBinding);
        initSuggestLayout(viewDataBinding);
        initRecentlySuggestLayout(viewDataBinding);

        viewDataBinding.progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.default_probressbar), PorterDuff.Mode.SRC_IN);
        setProgressBarVisible(false);
    }

    private void initScrollLayout(final ActivitySearchStaySuggestDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.nearbyDataBinding.bottomDivider.setVisibility(View.VISIBLE);

        viewDataBinding.nearbyDataBinding.nearbyLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                StaySuggest staySuggest = (StaySuggest) view.getTag();

                getEventListener().onNearbyClick(staySuggest);
            }
        });

        viewDataBinding.descriptionTextView.setText(R.string.label_search_suggest_recently_empty_description_type_stay);
    }

    private void initSearchToolbarLayout(final ActivitySearchStaySuggestDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.keywordEditText.setBackgroundDrawable(null);
        viewDataBinding.keywordEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        viewDataBinding.keywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_DONE:
                        if (DailyTextUtils.isTextEmpty(v.getText().toString()) == false)
                        {
                            getEventListener().onSearchSuggest(v.getText().toString());
                        }
                        return true;

                    default:
                        return false;
                }
            }
        });

        viewDataBinding.backImageView.setOnClickListener(this);

        setVoiceSearchEnabled(false);
        viewDataBinding.voiceSearchView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getEventListener().onVoiceSearchClick();
            }
        });

        viewDataBinding.keywordEditText.addTextChangedListener(mTextWatcher);

        viewDataBinding.deleteTextView.setVisibility(View.INVISIBLE);
        viewDataBinding.deleteTextView.setOnClickListener(this);
    }

    private void initSuggestLayout(final ActivitySearchStaySuggestDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.suggestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.suggestsRecyclerView, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.suggestsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            private int mDistance;
            private boolean mIsHide;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState != RecyclerView.SCROLL_STATE_DRAGGING)
                {
                    mDistance = 0;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (mIsHide == true)
                {
                    mDistance = 0;
                    return;
                }

                int defaultValue = ScreenUtils.dpToPx(getContext(), 41);

                mDistance += dy;

                if (mDistance > defaultValue == true)
                {
                    mDistance = 0;
                    mIsHide = true;

                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);

                    Observable.just(false).delaySubscription(1, TimeUnit.SECONDS).subscribe(isHide -> mIsHide = isHide);
                }
            }
        });

        setSuggestListAdapter();
    }

    private void setSuggestListAdapter()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (getViewDataBinding().suggestsRecyclerView.getAdapter() != null)
        {
            ExLog.d("sam : " + getViewDataBinding().suggestsRecyclerView.getAdapter().toString());
            return;
        }

        if (mSuggestListAdapter == null)
        {
            mSuggestListAdapter = new SuggestListAdapter(getContext(), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    StaySuggest staySuggest = (StaySuggest) v.getTag();

                    if (staySuggest != null)
                    {
                        getEventListener().onSuggestClick(staySuggest);
                    }
                }
            });

            getViewDataBinding().suggestsRecyclerView.setAdapter(mSuggestListAdapter);
        }
    }

    private void initRecentlySuggestLayout(final ActivitySearchStaySuggestDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.recentlySuggestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recentlySuggestRecyclerView, getColor(R.color.default_over_scroll_edge));
        viewDataBinding.recentlySuggestRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            private int mDistance;
            private boolean mIsHide;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState != RecyclerView.SCROLL_STATE_DRAGGING)
                {
                    mDistance = 0;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (mIsHide == true)
                {
                    mDistance = 0;
                    return;
                }

                int defaultValue = ScreenUtils.dpToPx(getContext(), 41);

                mDistance += dy;

                if (mDistance > defaultValue == true)
                {
                    mDistance = 0;
                    mIsHide = true;

                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);

                    Observable.just(false).delaySubscription(1, TimeUnit.SECONDS).subscribe(isHide -> mIsHide = isHide);
                }
            }
        });

        viewDataBinding.deleteRecentlySuggestLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onDeleteAllRecentlySuggest(false);
            }
        });

        setRecentlySuggestListAdapter();
    }

    private void setRecentlySuggestListAdapter()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (getViewDataBinding().recentlySuggestRecyclerView.getAdapter() != null)
        {
            return;
        }

        if (mRecentlySuggestListAdapter == null)
        {
            mRecentlySuggestListAdapter = new RecentlySuggestListAdapter(getContext(), new RecentlySuggestListAdapter.OnRecentlySuggestListener()
            {
                @Override
                public void onItemClick(int position, StaySuggest staySuggest)
                {
                    getEventListener().onRecentlySuggestClick(staySuggest);
                }

                @Override
                public void onDeleteClick(int position, StaySuggest staySuggest)
                {
                    getEventListener().onDeleteRecentlySuggest(position, staySuggest);
                }

                @Override
                public void onNearbyClick(StaySuggest staySuggest)
                {
                    getEventListener().onNearbyClick(staySuggest);
                }
            });
        }

        getViewDataBinding().recentlySuggestRecyclerView.setAdapter(mRecentlySuggestListAdapter);
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void setSuggestsVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (visible == true)
        {
            getViewDataBinding().suggestsRecyclerView.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().suggestsRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setSuggests(List<StaySuggest> staySuggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setSuggestListAdapter();

        List<ObjectItem> objectItemList = new ArrayList<>();

        String keyword = getViewDataBinding().keywordEditText.getText().toString();

        if (DailyTextUtils.isTextEmpty(keyword) == false)
        {
            objectItemList.add(new ObjectItem(ObjectItem.TYPE_HEADER_VIEW, new StaySuggest(StaySuggest.MENU_TYPE_DIRECT, StaySuggest.CATEGORY_DIRECT, keyword)));
        }

        if (staySuggestList != null && staySuggestList.size() > 0)
        {
            for (StaySuggest staySuggest : staySuggestList)
            {
                if (DailyTextUtils.isTextEmpty(staySuggest.categoryKey) == true)
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, staySuggest));
                } else
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, staySuggest));
                }
            }

            // 마지막줄
            objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, new StaySuggest(StaySuggest.MENU_TYPE_SUGGEST, null, null)));
        }

        mSuggestListAdapter.setAll(keyword, objectItemList);
        mSuggestListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSuggest(String suggest)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().keywordEditText.removeTextChangedListener(mTextWatcher);
        getViewDataBinding().keywordEditText.setText(suggest);
        getViewDataBinding().keywordEditText.setSelection(getViewDataBinding().keywordEditText.length());
        getViewDataBinding().keywordEditText.addTextChangedListener(mTextWatcher);
    }

    @Override
    public void showKeyboard()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().keywordEditText.setFocusable(true);
        getViewDataBinding().keywordEditText.setFocusableInTouchMode(true);
        getViewDataBinding().keywordEditText.requestFocus();
        getViewDataBinding().keywordEditText.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(getViewDataBinding().keywordEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500);
    }

    @Override
    public void hideKeyboard()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getViewDataBinding().keywordEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void setProgressBarVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (visible == true)
        {
            getViewDataBinding().progressBarScrollView.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().progressBarScrollView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setRecentlySuggestVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (visible == true)
        {
            getViewDataBinding().recentlySuggestLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().deleteRecentlySuggestLayout.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().recentlySuggestLayout.setVisibility(View.GONE);
            getViewDataBinding().deleteRecentlySuggestLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void setRecentlySuggests(List<StaySuggest> staySuggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setRecentlySuggestListAdapter();

        List<ObjectItem> objectItemList = new ArrayList<>();

        if (staySuggestList != null && staySuggestList.size() > 0)
        {
            // 현재 위치 검색 추가
            objectItemList.add(new ObjectItem(ObjectItem.TYPE_LOCATION_VIEW //
                , new StaySuggest(StaySuggest.MENU_TYPE_LOCATION //
                , StaySuggest.CATEGORY_LOCATION, null)));

            for (StaySuggest staySuggest : staySuggestList)
            {
                if (DailyTextUtils.isTextEmpty(staySuggest.categoryKey))
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, staySuggest));
                } else
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, staySuggest));
                }
            }

            objectItemList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));
        }

        mRecentlySuggestListAdapter.setAll(objectItemList);
        mRecentlySuggestListAdapter.notifyDataSetChanged();
    }

    @Override
    public int getRecentlySuggestEntryCount()
    {
        if (mRecentlySuggestListAdapter == null)
        {
            return 0;
        }

        return mRecentlySuggestListAdapter.getEntryCount();
    }

    @Override
    public void setKeywordEditText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().keywordEditText.setText(text);
        getViewDataBinding().keywordEditText.setSelection(getViewDataBinding().keywordEditText.length());
    }

    @Override
    public void setVoiceSearchEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().voiceSearchView.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    @Override
    public void removeRecentlyItem(int position)
    {
        if (mRecentlySuggestListAdapter == null)
        {
            return;
        }

        mRecentlySuggestListAdapter.removeItem(position);
        mRecentlySuggestListAdapter.notifyDataSetChanged();
    }

    @Override
    public void removeRecentlySection(int menuType)
    {
        if (mRecentlySuggestListAdapter == null)
        {
            return;
        }

        mRecentlySuggestListAdapter.removeSection(menuType);
        mRecentlySuggestListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setNearbyStaySuggest(boolean isAgreePermission, StaySuggest nearbyStaySuggest)
    {
        String descriptionText = null;
        if (isAgreePermission == false)
        {
            descriptionText = getString(R.string.label_search_nearby_description);
        } else
        {
            descriptionText = nearbyStaySuggest != null ? nearbyStaySuggest.displayName : null;
        }

        if (mRecentlySuggestListAdapter != null)
        {
            mRecentlySuggestListAdapter.setNearByStaySuggest(nearbyStaySuggest);
            mRecentlySuggestListAdapter.notifyDataSetChanged();
        }

        if (getViewDataBinding() != null)
        {
            getViewDataBinding().nearbyDataBinding.nearbyLayout.setTag(nearbyStaySuggest);
            getViewDataBinding().nearbyDataBinding.descriptionTextView.setText(descriptionText);
            getViewDataBinding().nearbyDataBinding.descriptionTextView.setVisibility(DailyTextUtils.isTextEmpty(descriptionText) ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.backImageView:
                getEventListener().onBackClick();
                break;

            // 자동 완성 목록에서 특정 텍스트를 클릭하는 경우
            case R.id.textView:
                Object object = view.getTag();

                if (object == null || object instanceof StaySuggest == false)
                {
                    return;
                }

                StaySuggest staySuggest = (StaySuggest) object;

                getEventListener().onSuggestClick(staySuggest);
                break;

            case R.id.deleteTextView:
                setSuggest(null);
                setSuggests(null);
                setSuggestsVisible(false);
                getEventListener().setCheckVoiceSearchEnabled();
                break;
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher()
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
        public void afterTextChanged(Editable editable)
        {
            if (getViewDataBinding() == null)
            {
                return;
            }

            int length = editable.length();

            if (length == 0)
            {
                getViewDataBinding().deleteTextView.setVisibility(View.INVISIBLE);
                getViewDataBinding().voiceSearchView.setVisibility(View.VISIBLE);
            } else
            {
                if (length == 1 && editable.charAt(0) == ' ')
                {
                    editable.delete(0, 1);
                    return;
                }

                if (length > 1 && editable.charAt(length - 1) == ' ')
                {
                    if (editable.charAt(length - 2) == ' ')
                    {
                        editable.delete(length - 1, length);
                    }
                    return;
                }

                getViewDataBinding().deleteTextView.setVisibility(View.VISIBLE);
                getViewDataBinding().voiceSearchView.setVisibility(View.GONE);
            }

            getEventListener().onSearchSuggest(editable.toString());
        }
    };
}

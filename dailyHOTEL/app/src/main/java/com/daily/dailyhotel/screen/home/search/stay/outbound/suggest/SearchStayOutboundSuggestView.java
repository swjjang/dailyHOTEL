package com.daily.dailyhotel.screen.home.search.stay.outbound.suggest;

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
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundSearchSuggestDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class SearchStayOutboundSuggestView //
    extends BaseDialogView<SearchStayOutboundSuggestView.OnEventListener, ActivityStayOutboundSearchSuggestDataBinding> //
    implements SearchStayOutboundSuggestInterface, View.OnClickListener
{
    private StayOutboundSuggestListAdapter mSuggestListAdapter;
    private StayOutboundRecentlySuggestListAdapter mRecentlySuggestListAdapter;
    private StayOutboundPopularSuggestListAdapter mPopularSuggestListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSearchSuggest(String keyword);

        void onSuggestClick(StayOutboundSuggest stayOutboundSuggest);

        void onRecentlySuggestClick(StayOutboundSuggest stayOutboundSuggest);

        void onPopularSuggestClick(StayOutboundSuggest stayOutboundSuggest);

        void onDeleteRecentlySuggest(int position, StayOutboundSuggest stayOutboundSuggest);

        void onVoiceSearchClick();

        void setCheckVoiceSearchEnabled();

        void onNearbyClick(StayOutboundSuggest stayOutboundSuggest);
    }

    public SearchStayOutboundSuggestView(BaseActivity baseActivity, SearchStayOutboundSuggestView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundSearchSuggestDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initSearchToolbarLayout(viewDataBinding);
        initRecyclerLayout(viewDataBinding);

        viewDataBinding.progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.default_probressbar), PorterDuff.Mode.SRC_IN);
        setProgressBarVisible(false);
    }

    private void initSearchToolbarLayout(final ActivityStayOutboundSearchSuggestDataBinding viewDataBinding)
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
                        return false;

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

        viewDataBinding.deleteImageView.setVisibility(View.INVISIBLE);
        viewDataBinding.deleteImageView.setOnClickListener(this);
    }

    private void initRecyclerLayout(final ActivityStayOutboundSearchSuggestDataBinding viewDataBinding)
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
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void setSuggests(List<StayOutboundSuggest> stayOutboundSuggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mSuggestListAdapter == null)
        {
            mSuggestListAdapter = new StayOutboundSuggestListAdapter(getContext(), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    StayOutboundSuggest stayOutboundSuggest = (StayOutboundSuggest) v.getTag();

                    if (stayOutboundSuggest != null)
                    {
                        getEventListener().onSuggestClick(stayOutboundSuggest);
                    }
                }
            });
        }
        getViewDataBinding().suggestsRecyclerView.setAdapter(mSuggestListAdapter);

        List<ObjectItem> objectItemList = new ArrayList<>();

        String keyword = getViewDataBinding().keywordEditText.getText().toString();

        if (stayOutboundSuggestList != null && stayOutboundSuggestList.size() > 0)
        {
            for (StayOutboundSuggest stayOutboundSuggest : stayOutboundSuggestList)
            {
                if (stayOutboundSuggest.id == 0)
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, stayOutboundSuggest));
                } else
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, stayOutboundSuggest));
                }
            }

            // 마지막줄
            objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, new StayOutboundSuggest(0, null)));

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
    public void setEmptySuggestsVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (visible == true)
        {
            getViewDataBinding().emptyLayout.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().emptyLayout.setVisibility(View.GONE);
        }
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
    public void setRecentlySuggests(StayOutboundSuggest locationSuggest, List<StayOutboundSuggest> stayOutboundSuggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mRecentlySuggestListAdapter == null)
        {
            mRecentlySuggestListAdapter = new StayOutboundRecentlySuggestListAdapter(getContext(), new StayOutboundRecentlySuggestListAdapter.OnRecentlySuggestListener()
            {
                @Override
                public void onItemClick(int position, StayOutboundSuggest stayOutboundSuggest)
                {
                    getEventListener().onRecentlySuggestClick(stayOutboundSuggest);
                }

                @Override
                public void onDeleteClick(int position, StayOutboundSuggest stayOutboundSuggest)
                {
                    getEventListener().onDeleteRecentlySuggest(position, stayOutboundSuggest);
                }

                @Override
                public void onNearbyClick(StayOutboundSuggest stayOutboundSuggest)
                {
                    getEventListener().onNearbyClick(stayOutboundSuggest);
                }
            });
        }

        getViewDataBinding().suggestsRecyclerView.setAdapter(mRecentlySuggestListAdapter);

        List<ObjectItem> objectItemList = new ArrayList<>();

        if (locationSuggest != null)
        {
            objectItemList.add(new ObjectItem(ObjectItem.TYPE_LOCATION_VIEW, locationSuggest));
        }

        if (stayOutboundSuggestList != null && stayOutboundSuggestList.size() > 0)
        {
            for (StayOutboundSuggest stayOutboundSuggest : stayOutboundSuggestList)
            {
                if (DailyTextUtils.isTextEmpty(stayOutboundSuggest.categoryKey))
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, stayOutboundSuggest));
                } else
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, stayOutboundSuggest));
                }
            }

            objectItemList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));
        }

        mRecentlySuggestListAdapter.setAll(objectItemList);
        mRecentlySuggestListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPopularAreaSuggests(StayOutboundSuggest locationSuggest, List<StayOutboundSuggest> stayOutboundSuggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mPopularSuggestListAdapter == null)
        {
            mPopularSuggestListAdapter = new StayOutboundPopularSuggestListAdapter(getContext(), new StayOutboundPopularSuggestListAdapter.OnPopularSuggestListener()
            {
                @Override
                public void onItemClick(int position, StayOutboundSuggest stayOutboundSuggest)
                {
                    getEventListener().onPopularSuggestClick(stayOutboundSuggest);
                }

                @Override
                public void onNearbyClick(StayOutboundSuggest stayOutboundSuggest)
                {
                    getEventListener().onNearbyClick(stayOutboundSuggest);
                }
            });
        }

        getViewDataBinding().suggestsRecyclerView.setAdapter(mPopularSuggestListAdapter);

        List<ObjectItem> objectItemList = new ArrayList<>();

        if (locationSuggest != null)
        {
            objectItemList.add(new ObjectItem(ObjectItem.TYPE_LOCATION_VIEW //
                , locationSuggest));
        }

        if (stayOutboundSuggestList != null && stayOutboundSuggestList.size() > 0)
        {
            for (StayOutboundSuggest stayOutboundSuggest : stayOutboundSuggestList)
            {
                if (DailyTextUtils.isTextEmpty(stayOutboundSuggest.categoryKey))
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, stayOutboundSuggest));
                } else
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, stayOutboundSuggest));
                }
            }
        }

        objectItemList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));

        mPopularSuggestListAdapter.setAll(objectItemList);
        mPopularSuggestListAdapter.notifyDataSetChanged();
    }

    @Override
    public int getRecentlySuggestAllEntryCount()
    {
        if (mRecentlySuggestListAdapter == null)
        {
            return 0;
        }

        return mRecentlySuggestListAdapter.getEntryCount();
    }

    @Override
    public void setKeywordEditHint(String hint)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().keywordEditText.setHint(hint);
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
    public void setNearbyStaySuggest(StayOutboundSuggest locationSuggest)
    {
        if (locationSuggest == null)
        {
            return;
        }

        if (mRecentlySuggestListAdapter != null)
        {
            mRecentlySuggestListAdapter.setNearByStayOutboundSuggest(locationSuggest);
            mRecentlySuggestListAdapter.notifyDataSetChanged();
        }

        if (mPopularSuggestListAdapter != null)
        {
            mPopularSuggestListAdapter.setNearByStayOutboundSuggest(locationSuggest);
            mPopularSuggestListAdapter.notifyDataSetChanged();
        }

        if (getViewDataBinding() != null)
        {
            getViewDataBinding().nearbyDataBinding.nearbyLayout.setTag(locationSuggest);
            getViewDataBinding().nearbyDataBinding.descriptionTextView.setText(locationSuggest.display);
            getViewDataBinding().nearbyDataBinding.descriptionTextView.setVisibility( //
                DailyTextUtils.isTextEmpty(locationSuggest.displayText) ? View.GONE : View.VISIBLE);
            getViewDataBinding().nearbyDataBinding.bottomDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.backImageView:
                getEventListener().onBackClick();
                break;

            // 자동 완성 목록에서 특정 텍스트를 클릭하는 경우
            case R.id.textView:
                Object object = v.getTag();

                if (object == null || object instanceof StayOutboundSuggest == false)
                {
                    return;
                }

                StayOutboundSuggest stayOutboundSuggest = (StayOutboundSuggest) object;

                getEventListener().onSuggestClick(stayOutboundSuggest);
                break;

            case R.id.deleteImageView:
                setKeywordEditText(null);
                showKeyboard();
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
                getViewDataBinding().deleteImageView.setVisibility(View.INVISIBLE);
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

                getViewDataBinding().deleteImageView.setVisibility(View.VISIBLE);
                getViewDataBinding().voiceSearchView.setVisibility(View.GONE);
            }

            getEventListener().onSearchSuggest(editable.toString());
        }
    };
}

package com.daily.dailyhotel.screen.home.search.gourmet.suggest;

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
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.entity.ObjectItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivitySearchGourmetSuggestDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class SearchGourmetSuggestView extends BaseDialogView<SearchGourmetSuggestView.OnEventListener, ActivitySearchGourmetSuggestDataBinding> //
    implements SearchGourmetSuggestInterface, View.OnClickListener
{
    private GourmetSuggestListAdapter mSuggestListAdapter;
    private GourmetRecentlySuggestListAdapter mRecentlySuggestListAdapter;
    private GourmetPopularSuggestListAdapter mPopularSuggestListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSearchSuggest(String keyword);

        void onSuggestClick(GourmetSuggestV2 gourmetSuggest);

        void onRecentlySuggestClick(GourmetSuggestV2 gourmetSuggest);

        void onDeleteRecentlySuggest(int position, GourmetSuggestV2 gourmetSuggest);

        void onVoiceSearchClick();

        void setCheckVoiceSearchEnabled();

        void onNearbyClick(GourmetSuggestV2 gourmetSuggest);
    }

    public SearchGourmetSuggestView(BaseActivity baseActivity, SearchGourmetSuggestView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivitySearchGourmetSuggestDataBinding viewDataBinding)
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

    private void initSearchToolbarLayout(final ActivitySearchGourmetSuggestDataBinding viewDataBinding)
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

    private void initRecyclerLayout(final ActivitySearchGourmetSuggestDataBinding viewDataBinding)
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
    public void setSuggests(List<GourmetSuggestV2> gourmetSuggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mSuggestListAdapter == null)
        {
            mSuggestListAdapter = new GourmetSuggestListAdapter(getContext(), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    GourmetSuggestV2 gourmetSuggest = (GourmetSuggestV2) v.getTag();

                    if (gourmetSuggest != null)
                    {
                        getEventListener().onSuggestClick(gourmetSuggest);
                    }
                }
            });
        }

        getViewDataBinding().suggestsRecyclerView.setAdapter(mSuggestListAdapter);

        List<ObjectItem> objectItemList = new ArrayList<>();

        String keyword = getViewDataBinding().keywordEditText.getText().toString();

        if (DailyTextUtils.isTextEmpty(keyword) == false)
        {
            objectItemList.add(new ObjectItem(ObjectItem.TYPE_HEADER_VIEW //
                , new GourmetSuggestV2(GourmetSuggestV2.MenuType.DIRECT, new GourmetSuggestV2.Direct(keyword))));
        }

        if (gourmetSuggestList != null && gourmetSuggestList.size() > 0)
        {
            for (GourmetSuggestV2 gourmetSuggest : gourmetSuggestList)
            {
                if (gourmetSuggest.getSuggestType() == GourmetSuggestV2.SuggestType.UNKNOWN)
                {
                    continue;
                }

                if (gourmetSuggest.getSuggestType() == GourmetSuggestV2.SuggestType.SECTION)
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, gourmetSuggest));
                } else
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, gourmetSuggest));
//
//                    GourmetSuggestData data = gourmetSuggest.getSuggestData();
//
//                    try
//                    {
//                        String json = LoganSquare.serialize(data);
//                        ExLog.d("sam : logan = " + json);
//
//                        GourmetSuggestData newData = LoganSquare.parse(json, GourmetSuggestData.class);
//                        ExLog.d("sam logan 2 = " + newData);
//
//                        GourmetSuggestV2 re2 = newData.getSuggest();
//                        ExLog.d("sam logan 3 = " + re2);
//                    } catch (Exception e)
//                    {
//                        ExLog.d(e.toString());
//                    }
                }
            }

            // 마지막줄
            objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION //
                , new GourmetSuggestV2(GourmetSuggestV2.MenuType.SUGGEST, new GourmetSuggestV2.Section(null))));
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
    public void setRecentlySuggests(GourmetSuggestV2 locationSuggest, List<GourmetSuggestV2> gourmetSuggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mRecentlySuggestListAdapter == null)
        {
            mRecentlySuggestListAdapter = new GourmetRecentlySuggestListAdapter(getContext(), new GourmetRecentlySuggestListAdapter.OnRecentlySuggestListener()
            {
                @Override
                public void onItemClick(int position, GourmetSuggestV2 gourmetSuggest)
                {
                    getEventListener().onRecentlySuggestClick(gourmetSuggest);
                }

                @Override
                public void onDeleteClick(int position, GourmetSuggestV2 gourmetSuggest)
                {
                    getEventListener().onDeleteRecentlySuggest(position, gourmetSuggest);
                }

                @Override
                public void onNearbyClick(GourmetSuggestV2 gourmetSuggest)
                {
                    getEventListener().onNearbyClick(gourmetSuggest);
                }
            });
        }

        getViewDataBinding().suggestsRecyclerView.setAdapter(mRecentlySuggestListAdapter);

        List<ObjectItem> objectItemList = new ArrayList<>();

        if (locationSuggest != null)
        {
            objectItemList.add(new ObjectItem(ObjectItem.TYPE_LOCATION_VIEW, locationSuggest));
        }

        if (gourmetSuggestList != null && gourmetSuggestList.size() > 0)
        {
            for (GourmetSuggestV2 gourmetSuggest : gourmetSuggestList)
            {
                if (gourmetSuggest.getSuggestType() == GourmetSuggestV2.SuggestType.SECTION)
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, gourmetSuggest));
                } else
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, gourmetSuggest));
                }
            }

            objectItemList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));
        }

        mRecentlySuggestListAdapter.setAll(objectItemList);
        mRecentlySuggestListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPopularAreaSuggests(GourmetSuggestV2 locationSuggest, List<GourmetSuggestV2> gourmetSuggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mPopularSuggestListAdapter == null)
        {
            mPopularSuggestListAdapter = new GourmetPopularSuggestListAdapter(getContext(), new GourmetPopularSuggestListAdapter.OnPopularSuggestListener()
            {
                @Override
                public void onNearbyClick(GourmetSuggestV2 gourmetSuggest)
                {
                    getEventListener().onNearbyClick(gourmetSuggest);
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

        if (gourmetSuggestList != null && gourmetSuggestList.size() > 0)
        {
            for (GourmetSuggestV2 gourmetSuggest : gourmetSuggestList)
            {
                objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, gourmetSuggest));
            }
        }

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
    public void setNearbyGourmetSuggest(GourmetSuggestV2 locationSuggest)
    {
        if (locationSuggest == null)
        {
            return;
        }

        if (mRecentlySuggestListAdapter != null)
        {
            mRecentlySuggestListAdapter.setNearByGourmetSuggest(locationSuggest);
            mRecentlySuggestListAdapter.notifyDataSetChanged();
        }

        if (mPopularSuggestListAdapter != null)
        {
            mPopularSuggestListAdapter.setNearByGourmetSuggest(locationSuggest);
            mPopularSuggestListAdapter.notifyDataSetChanged();
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

                if (object == null || object instanceof GourmetSuggestV2 == false)
                {
                    return;
                }

                GourmetSuggestV2 gourmetSuggest = (GourmetSuggestV2) object;
                getEventListener().onSuggestClick(gourmetSuggest);
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

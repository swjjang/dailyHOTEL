package com.daily.dailyhotel.screen.home.stay.outbound.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.ListItem;
import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundSearchSuggestDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowStayOutboundSuggestEntryDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowStayOutboundSuggestTitleDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class StayOutboundSearchSuggestView extends BaseDialogView<StayOutboundSearchSuggestView.OnEventListener, ActivityStayOutboundSearchSuggestDataBinding> implements StayOutboundSearchSuggestViewInterface, View.OnClickListener
{
    private SuggestListAdapter mSuggestListAdapter;
    private RecentlySuggestListAdapter mRecentlySuggestListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSearchSuggest(String keyword);

        void onSuggestClick(Suggest suggest);

        void onRecentlySuggestClick(Suggest suggest);

        void onDeleteAllRecentlySuggest();
    }

    public StayOutboundSearchSuggestView(BaseActivity baseActivity, StayOutboundSearchSuggestView.OnEventListener listener)
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
        viewDataBinding.keywordEditText.addTextChangedListener(mTextWatcher);
        viewDataBinding.deleteImageView.setVisibility(View.INVISIBLE);
        viewDataBinding.deleteImageView.setOnClickListener(this);
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
                if (getEventListener() == null)
                {
                    return;
                }

                getEventListener().onDeleteAllRecentlySuggest();
            }
        });

        viewDataBinding.progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.default_probressbar), PorterDuff.Mode.SRC_IN);
        setProgressBarVisible(false);
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
    public void setSuggests(List<Suggest> suggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mSuggestListAdapter == null)
        {
            mSuggestListAdapter = new SuggestListAdapter(getContext(), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Suggest suggest = (Suggest) v.getTag();

                    if (suggest != null)
                    {
                        getEventListener().onSuggestClick(suggest);
                    }
                }
            });

            getViewDataBinding().suggestsRecyclerView.setAdapter(mSuggestListAdapter);
        }

        if (suggestList == null || suggestList.size() == 0)
        {
            mSuggestListAdapter.setAll(null, null);
            mSuggestListAdapter.notifyDataSetChanged();
        } else
        {
            List<ListItem> listItemList = new ArrayList<>(suggestList.size());

            for (Suggest suggest : suggestList)
            {
                if (suggest.id == 0)
                {
                    listItemList.add(new ListItem(ListItem.TYPE_SECTION, suggest));
                } else
                {
                    listItemList.add(new ListItem(ListItem.TYPE_ENTRY, suggest));
                }
            }

            // 마지막줄
            listItemList.add(new ListItem(ListItem.TYPE_SECTION, new Suggest(0, null)));

            mSuggestListAdapter.setAll(getViewDataBinding().keywordEditText.getText().toString(), listItemList);
            mSuggestListAdapter.notifyDataSetChanged();
        }
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

                if (object == null || object instanceof Suggest == false)
                {
                    return;
                }

                Suggest suggest = (Suggest) object;

                getEventListener().onSuggestClick(suggest);
                break;

            case R.id.deleteImageView:
                setSuggest(null);
                setSuggests(null);
                setSuggestsVisible(false);
                setEmptySuggestsVisible(false);
                break;
        }
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
    public void setRecentlySuggests(List<Suggest> suggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mRecentlySuggestListAdapter == null)
        {
            mRecentlySuggestListAdapter = new RecentlySuggestListAdapter(getContext(), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Suggest suggest = (Suggest) v.getTag();

                    if (suggest != null)
                    {
                        getEventListener().onRecentlySuggestClick(suggest);
                    }
                }
            });

            getViewDataBinding().recentlySuggestRecyclerView.setAdapter(mRecentlySuggestListAdapter);
        }

        if (suggestList == null || suggestList.size() == 0)
        {
            getViewDataBinding().recentlySuggestLayout.setVisibility(View.GONE);
            mRecentlySuggestListAdapter.setAll(null);
            mRecentlySuggestListAdapter.notifyDataSetChanged();
            return;
        }

        getViewDataBinding().recentlySuggestLayout.setVisibility(View.VISIBLE);

        List<ListItem> listItemList = new ArrayList<>();

        for (Suggest suggest : suggestList)
        {
            listItemList.add(new ListItem(ListItem.TYPE_ENTRY, suggest));
        }

        listItemList.add(new ListItem(ListItem.TYPE_FOOTER_VIEW, null));

        mRecentlySuggestListAdapter.setAll(listItemList);
        mRecentlySuggestListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setKeywordEditText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().keywordEditText.setText(text);
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
            }

            getEventListener().onSearchSuggest(editable.toString());
        }
    };

    private class SuggestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private Context mContext;
        private View.OnClickListener mOnClickListener;

        private String mKeyword;
        private List<ListItem> mSuggestList;

        public SuggestListAdapter(Context context, View.OnClickListener listener)
        {
            mContext = context;
            mOnClickListener = listener;

            setAll(null, null);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            switch (viewType)
            {
                case ListItem.TYPE_SECTION:
                {
                    ListRowStayOutboundSuggestTitleDataBinding dataBinding //
                        = DataBindingUtil.inflate(LayoutInflater.from(mContext) //
                        , R.layout.list_row_stay_outbound_suggest_title_data, parent, false);

                    TitleViewHolder titleViewHolder = new TitleViewHolder(dataBinding);

                    return titleViewHolder;
                }

                case ListItem.TYPE_ENTRY:
                {
                    ListRowStayOutboundSuggestEntryDataBinding dataBinding //
                        = DataBindingUtil.inflate(LayoutInflater.from(mContext) //
                        , R.layout.list_row_stay_outbound_suggest_entry_data, parent, false);

                    EntryViewHolder entryViewHolder = new EntryViewHolder(dataBinding);

                    return entryViewHolder;
                }
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            ListItem item = getItem(position);

            if (item == null)
            {
                return;
            }

            switch (item.mType)
            {
                case ListItem.TYPE_SECTION:
                    onBindViewHolder((TitleViewHolder) holder, item, position);
                    break;

                case ListItem.TYPE_ENTRY:
                    onBindViewHolder((EntryViewHolder) holder, item, position);
                    break;
            }
        }

        @Override
        public int getItemCount()
        {
            if (mSuggestList == null)
            {
                return 0;
            } else
            {
                return mSuggestList.size();
            }
        }

        @Override
        public int getItemViewType(int position)
        {
            return mSuggestList.get(position).mType;
        }

        public void setAll(String keyword, List<ListItem> list)
        {
            mKeyword = keyword;

            if (mSuggestList == null)
            {
                mSuggestList = new ArrayList<>();
            }

            mSuggestList.clear();

            if (list != null && list.size() > 0)
            {
                mSuggestList.addAll(list);
            }
        }

        public ListItem getItem(int position)
        {
            if (position < 0 || mSuggestList.size() <= position)
            {
                return null;
            }

            return mSuggestList.get(position);
        }

        private void onBindViewHolder(TitleViewHolder holder, ListItem item, int position)
        {
            if (position == 0)
            {
                holder.dataBinding.dividerView.setVisibility(View.GONE);
            } else
            {
                holder.dataBinding.dividerView.setVisibility(View.VISIBLE);
            }

            Suggest suggest = item.getItem();

            if (DailyTextUtils.isTextEmpty(suggest.name) == true)
            {
                holder.dataBinding.titleTextView.setVisibility(View.GONE);
            } else
            {
                holder.dataBinding.titleTextView.setVisibility(View.VISIBLE);
            }

            holder.dataBinding.titleTextView.setText(suggest.display);
        }

        private void onBindViewHolder(EntryViewHolder holder, ListItem item, int position)
        {
            Suggest suggest = item.getItem();

            holder.itemView.getRootView().setTag(suggest);

            if (DailyTextUtils.isTextEmpty(suggest.display) == true)
            {
                holder.dataBinding.textView.setText(null);
            } else
            {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(suggest.display);

                if (DailyTextUtils.isTextEmpty(mKeyword) == false)
                {
                    int fromIndex = 0;
                    do
                    {
                        int startIndex = suggest.display.indexOf(mKeyword, fromIndex);

                        if (startIndex < 0)
                        {
                            break;
                        }

                        int endIndex = startIndex + mKeyword.length();
                        fromIndex = endIndex;

                        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
                            startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } while (true);
                }

                holder.dataBinding.textView.setText(spannableStringBuilder);
            }

            switch (suggest.categoryKey)
            {
                case Suggest.CATEGORY_AIRPORT:
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_04_airport, 0, 0, 0);
                    break;

                case Suggest.CATEGORY_HOTEL:
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_02_hotel, 0, 0, 0);
                    break;

                case Suggest.CATEGORY_POINT:
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_03_landmark, 0, 0, 0);
                    break;

                case Suggest.CATEGORY_REGION:
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_01_region, 0, 0, 0);
                    break;

                case Suggest.CATEGORY_STATION:
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_05_train, 0, 0, 0);
                    break;

                default:
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_01_region, 0, 0, 0);
                    break;
            }
        }

        class TitleViewHolder extends RecyclerView.ViewHolder
        {
            ListRowStayOutboundSuggestTitleDataBinding dataBinding;

            public TitleViewHolder(ListRowStayOutboundSuggestTitleDataBinding dataBinding)
            {
                super(dataBinding.getRoot());

                this.dataBinding = dataBinding;
            }
        }

        class EntryViewHolder extends RecyclerView.ViewHolder
        {
            ListRowStayOutboundSuggestEntryDataBinding dataBinding;

            public EntryViewHolder(ListRowStayOutboundSuggestEntryDataBinding dataBinding)
            {
                super(dataBinding.getRoot());

                this.dataBinding = dataBinding;

                dataBinding.getRoot().setOnClickListener(mOnClickListener);
            }
        }
    }

    private class RecentlySuggestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private Context mContext;
        private View.OnClickListener mOnClickListener;

        private List<ListItem> mSuggestList;

        public RecentlySuggestListAdapter(Context context, View.OnClickListener listener)
        {
            mContext = context;
            mOnClickListener = listener;

            setAll(null);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            switch (viewType)
            {
                case ListItem.TYPE_FOOTER_VIEW:
                {
                    View view = new View(mContext);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(mContext, 10d));
                    view.setLayoutParams(params);

                    FooterViewHolder titleViewHolder = new FooterViewHolder(view);

                    return titleViewHolder;
                }

                case ListItem.TYPE_ENTRY:
                {
                    ListRowStayOutboundSuggestEntryDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_stay_outbound_suggest_entry_data, parent, false);

                    EntryViewHolder entryViewHolder = new EntryViewHolder(dataBinding);

                    return entryViewHolder;
                }
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            ListItem item = getItem(position);

            if (item == null)
            {
                return;
            }

            switch (item.mType)
            {
                case ListItem.TYPE_FOOTER_VIEW:
                    break;

                case ListItem.TYPE_ENTRY:
                    onBindViewHolder((EntryViewHolder) holder, item, position);
                    break;
            }
        }

        @Override
        public int getItemCount()
        {
            if (mSuggestList == null)
            {
                return 0;
            } else
            {
                return mSuggestList.size();
            }
        }

        @Override
        public int getItemViewType(int position)
        {
            return mSuggestList.get(position).mType;
        }

        public void setAll(List<ListItem> list)
        {
            if (mSuggestList == null)
            {
                mSuggestList = new ArrayList<>();
            }

            mSuggestList.clear();

            if (list != null && list.size() > 0)
            {
                mSuggestList.addAll(list);
            }
        }

        public ListItem getItem(int position)
        {
            if (position < 0 || mSuggestList.size() <= position)
            {
                return null;
            }

            return mSuggestList.get(position);
        }

        private void onBindViewHolder(EntryViewHolder holder, ListItem item, int position)
        {
            Suggest suggest = item.getItem();

            holder.itemView.getRootView().setTag(suggest);

            holder.dataBinding.textView.setText(suggest.display);

            switch (suggest.categoryKey)
            {
                case Suggest.CATEGORY_AIRPORT:
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_04_airport, 0, 0, 0);
                    break;

                case Suggest.CATEGORY_HOTEL:
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_02_hotel, 0, 0, 0);
                    break;

                case Suggest.CATEGORY_POINT:
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_03_landmark, 0, 0, 0);
                    break;

                case Suggest.CATEGORY_REGION:
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_01_region, 0, 0, 0);
                    break;

                case Suggest.CATEGORY_STATION:
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_05_train, 0, 0, 0);
                    break;

                default:
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_01_region, 0, 0, 0);
                    break;
            }
        }

        class FooterViewHolder extends RecyclerView.ViewHolder
        {
            public FooterViewHolder(View itemView)
            {
                super(itemView);
            }
        }

        class EntryViewHolder extends RecyclerView.ViewHolder
        {
            ListRowStayOutboundSuggestEntryDataBinding dataBinding;

            public EntryViewHolder(ListRowStayOutboundSuggestEntryDataBinding dataBinding)
            {
                super(dataBinding.getRoot());

                this.dataBinding = dataBinding;

                dataBinding.getRoot().setOnClickListener(mOnClickListener);
            }
        }
    }
}

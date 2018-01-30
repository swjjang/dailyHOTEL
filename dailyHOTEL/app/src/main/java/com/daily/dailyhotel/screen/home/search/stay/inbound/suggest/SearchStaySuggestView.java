package com.daily.dailyhotel.screen.home.search.stay.inbound.suggest;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.location.Location;
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
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.StaySuggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivitySearchStaySuggestDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowSearchSuggestTypeEntryDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowSearchSuggestTypeNearbyDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowSearchSuggestTypeSectionDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class SearchStaySuggestView extends BaseDialogView<SearchStaySuggestView.OnEventListener, ActivitySearchStaySuggestDataBinding> //
    implements SearchStaySuggestInterface, View.OnClickListener
{
    private SuggestListAdapter mSuggestListAdapter;
    private RecentlySuggestListAdapter mRecentlySuggestListAdapter;
    private RecentlySuggestListAdapter mPopularSuggestListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSearchSuggest(String keyword);

        void onSuggestClick(StaySuggest staySuggest);

        void onRecentlySuggestClick(StaySuggest staySuggest);

        void onPopularSuggestClick(StaySuggest staySuggest);

        void onDeleteAllRecentlySuggest();

        void onDeleteRecentlySuggest(int position, StaySuggest staySuggest);

        void onVoiceSearchClick();

        void onCheckVoiceSearchEnabled();
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
    public void setSuggests(List<StaySuggest> staySuggestList)
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
                    StaySuggest staySuggest = (StaySuggest) v.getTag();

                    if (staySuggest != null)
                    {
                        getEventListener().onSuggestClick(staySuggest);
                    }
                }
            });

            getViewDataBinding().suggestsRecyclerView.setAdapter(mSuggestListAdapter);
        }

        List<ObjectItem> objectItemList = new ArrayList<>();

        String keyword = getViewDataBinding().keywordEditText.getText().toString();

        if (DailyTextUtils.isTextEmpty(keyword) == false)
        {
            objectItemList.add(new ObjectItem(ObjectItem.TYPE_HEADER_VIEW, new StaySuggest(StaySuggest.CATEGORY_DIRECT, keyword)));
        }

        if (staySuggestList == null || staySuggestList.size() == 0)
        {
            //            mSuggestListAdapter.setAll(keyword, objectItemList);
            //            mSuggestListAdapter.notifyDataSetChanged();
        } else
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
            objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, new StaySuggest(null, null)));

            mSuggestListAdapter.setAll(keyword, objectItemList);
            mSuggestListAdapter.notifyDataSetChanged();
        }

        //        mSuggestListAdapter.setAll(keyword, objectItemList);
        //        mSuggestListAdapter.notifyDataSetChanged();
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
    public void setRecentlySuggests(List<StaySuggest> staySuggestList, Location location)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mRecentlySuggestListAdapter == null)
        {
            mRecentlySuggestListAdapter = new RecentlySuggestListAdapter(getContext(), new OnRecentlySuggestListener()
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
            });
        }

        getViewDataBinding().recentlySuggestRecyclerView.setAdapter(mRecentlySuggestListAdapter);

        List<ObjectItem> objectItemList = new ArrayList<>();

        // TODO : 현재 위치 적용
        objectItemList.add(new ObjectItem(ObjectItem.TYPE_LOCATION_VIEW, new StaySuggest()));

        if (staySuggestList == null || staySuggestList.size() == 0)
        {
            getViewDataBinding().recentlySuggestLayout.setVisibility(View.GONE);
            mRecentlySuggestListAdapter.setAll(null);
            mRecentlySuggestListAdapter.notifyDataSetChanged();
            return;
        }

        getViewDataBinding().recentlySuggestLayout.setVisibility(View.VISIBLE);
        getViewDataBinding().deleteRecentlySuggestLayout.setVisibility(View.VISIBLE);


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

        mRecentlySuggestListAdapter.setAll(objectItemList);
        mRecentlySuggestListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPopularAreaSuggests(List<StaySuggest> staySuggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mPopularSuggestListAdapter == null)
        {
            mPopularSuggestListAdapter = new RecentlySuggestListAdapter(getContext(), new OnRecentlySuggestListener()
            {
                @Override
                public void onItemClick(int position, StaySuggest staySuggest)
                {
                    getEventListener().onPopularSuggestClick(staySuggest);
                }

                @Override
                public void onDeleteClick(int position, StaySuggest staySuggest)
                {
                    getEventListener().onDeleteRecentlySuggest(position, staySuggest);
                }
            });
        }

        getViewDataBinding().recentlySuggestRecyclerView.setAdapter(mPopularSuggestListAdapter);

        if (staySuggestList == null || staySuggestList.size() == 0)
        {
            getViewDataBinding().recentlySuggestLayout.setVisibility(View.GONE);
            mPopularSuggestListAdapter.setAll(null);
            mPopularSuggestListAdapter.notifyDataSetChanged();
            return;
        }

        getViewDataBinding().recentlySuggestLayout.setVisibility(View.VISIBLE);
        getViewDataBinding().deleteRecentlySuggestLayout.setVisibility(View.GONE);

        List<ObjectItem> objectItemList = new ArrayList<>();

        for (StaySuggest staySuggest : staySuggestList)
        {
            objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, staySuggest));
        }

        objectItemList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));

        mPopularSuggestListAdapter.setAll(objectItemList);
        mPopularSuggestListAdapter.notifyDataSetChanged();
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
                setEmptySuggestsVisible(false);
                getEventListener().onCheckVoiceSearchEnabled();
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private class SuggestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private Context mContext;
        View.OnClickListener mOnClickListener;

        private String mKeyword;
        private List<ObjectItem> mSuggestList;

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
                case ObjectItem.TYPE_HEADER_VIEW:
                {
                    ListRowSearchSuggestTypeEntryDataBinding dataBinding //
                        = DataBindingUtil.inflate(LayoutInflater.from(mContext) //
                        , R.layout.list_row_search_suggest_type_entry_data, parent, false);

                    DirectViewHolder entryViewHolder = new DirectViewHolder(dataBinding);

                    return entryViewHolder;
                }

                case ObjectItem.TYPE_SECTION:
                {
                    ListRowSearchSuggestTypeSectionDataBinding dataBinding //
                        = DataBindingUtil.inflate(LayoutInflater.from(mContext) //
                        , R.layout.list_row_search_suggest_type_section_data, parent, false);

                    SectionViewHolder sectionViewHolder = new SectionViewHolder(dataBinding);

                    return sectionViewHolder;
                }

                case ObjectItem.TYPE_ENTRY:
                {
                    ListRowSearchSuggestTypeEntryDataBinding dataBinding //
                        = DataBindingUtil.inflate(LayoutInflater.from(mContext) //
                        , R.layout.list_row_search_suggest_type_entry_data, parent, false);

                    SuggestListAdapter.EntryViewHolder entryViewHolder = new EntryViewHolder(dataBinding);

                    return entryViewHolder;
                }
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            ObjectItem item = getItem(position);

            if (item == null)
            {
                return;
            }

            switch (item.mType)
            {
                case ObjectItem.TYPE_HEADER_VIEW:
                    onBindViewHolder((DirectViewHolder) holder, item, position);
                    break;

                case ObjectItem.TYPE_SECTION:
                    onBindViewHolder((SectionViewHolder) holder, item, position);
                    break;

                case ObjectItem.TYPE_ENTRY:
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

        public void setAll(String keyword, List<ObjectItem> objectItemList)
        {
            mKeyword = keyword;

            if (mSuggestList == null)
            {
                mSuggestList = new ArrayList<>();
            }

            mSuggestList.clear();

            if (objectItemList != null && objectItemList.size() > 0)
            {
                mSuggestList.addAll(objectItemList);
            }
        }

        public ObjectItem getItem(int position)
        {
            if (position < 0 || mSuggestList.size() <= position)
            {
                return null;
            }

            return mSuggestList.get(position);
        }

        private void onBindViewHolder(DirectViewHolder holder, ObjectItem item, int position)
        {
            StaySuggest staySuggest = item.getItem();

            holder.itemView.getRootView().setTag(staySuggest);

            holder.dataBinding.descriptionTextView.setVisibility(View.GONE);
            holder.dataBinding.deleteImageView.setVisibility(View.GONE);
            holder.dataBinding.priceTextView.setVisibility(View.GONE);
            holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_07_recent);
            holder.dataBinding.bottomDivider.setVisibility(View.VISIBLE);
            holder.dataBinding.deleteImageView.setVisibility(View.GONE);

            if (DailyTextUtils.isTextEmpty(staySuggest.displayName) == true)
            {
                holder.dataBinding.titleTextView.setText(null);
            } else
            {
                String text = getString(R.string.label_search_suggest_direct_search_format, staySuggest.displayName);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);

                if (DailyTextUtils.isTextEmpty(mKeyword) == false)
                {
                    int fromIndex = 0;
                    do
                    {
                        int startIndex = text.indexOf(mKeyword, fromIndex);

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

                holder.dataBinding.titleTextView.setText(spannableStringBuilder);
            }
        }

        private void onBindViewHolder(SectionViewHolder holder, ObjectItem item, int position)
        {
            StaySuggest staySuggest = item.getItem();

            if (DailyTextUtils.isTextEmpty(staySuggest.displayName) == true)
            {
                holder.dataBinding.titleTextView.setVisibility(View.GONE);
            } else
            {
                holder.dataBinding.titleTextView.setVisibility(View.VISIBLE);
            }

            holder.dataBinding.titleTextView.setText(staySuggest.displayName);
        }

        private void onBindViewHolder(EntryViewHolder holder, ObjectItem item, int position)
        {
            StaySuggest staySuggest = item.getItem();

            holder.itemView.getRootView().setTag(staySuggest);

            holder.dataBinding.descriptionTextView.setVisibility(View.GONE);
            holder.dataBinding.bottomDivider.setVisibility(View.GONE);
            holder.dataBinding.deleteImageView.setVisibility(View.GONE);

            if (DailyTextUtils.isTextEmpty(staySuggest.displayName) == true)
            {
                holder.dataBinding.titleTextView.setText(null);
            } else
            {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(staySuggest.displayName);

                if (DailyTextUtils.isTextEmpty(mKeyword) == false)
                {
                    int fromIndex = 0;
                    do
                    {
                        int startIndex = staySuggest.displayName.indexOf(mKeyword, fromIndex);

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

                holder.dataBinding.titleTextView.setText(spannableStringBuilder);
            }

            switch (staySuggest.categoryKey)
            {
                case StaySuggest.CATEGORY_STAY:
                case StaySuggest.CATEGORY_RECENTLY:
                    holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_02_hotel);

                    holder.dataBinding.priceTextView.setVisibility(View.VISIBLE);
                    if (staySuggest.discountAveragePrice > 0)
                    {

                        DecimalFormat decimalFormat = new DecimalFormat("###,##0");
                        String priceText = decimalFormat.format(staySuggest.discountAveragePrice) + getContext().getString(R.string.currency);

                        holder.dataBinding.priceTextView.setText(priceText);
                    } else
                    {
                        holder.dataBinding.priceTextView.setText(R.string.label_soldout);
                    }

                    break;

                case StaySuggest.CATEGORY_LOCATION:
                    holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_09_nearby);

                    holder.dataBinding.priceTextView.setVisibility(View.GONE);
                    break;

                case StaySuggest.CATEGORY_REGION:
                    holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_01_region);

                    holder.dataBinding.priceTextView.setVisibility(View.GONE);
                    break;

                case StaySuggest.CATEGORY_STATION:
                    holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_06_train);

                    holder.dataBinding.priceTextView.setVisibility(View.GONE);
                    break;

                default:
                    holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_07_recent);

                    holder.dataBinding.priceTextView.setVisibility(View.GONE);
                    break;
            }
        }

        class DirectViewHolder extends RecyclerView.ViewHolder
        {
            ListRowSearchSuggestTypeEntryDataBinding dataBinding;

            public DirectViewHolder(ListRowSearchSuggestTypeEntryDataBinding dataBinding)
            {
                super(dataBinding.getRoot());

                this.dataBinding = dataBinding;

                dataBinding.getRoot().setOnClickListener(mOnClickListener);
            }
        }

        class SectionViewHolder extends RecyclerView.ViewHolder
        {
            ListRowSearchSuggestTypeSectionDataBinding dataBinding;

            public SectionViewHolder(ListRowSearchSuggestTypeSectionDataBinding dataBinding)
            {
                super(dataBinding.getRoot());

                this.dataBinding = dataBinding;
            }
        }

        class EntryViewHolder extends RecyclerView.ViewHolder
        {
            ListRowSearchSuggestTypeEntryDataBinding dataBinding;

            public EntryViewHolder(ListRowSearchSuggestTypeEntryDataBinding dataBinding)
            {
                super(dataBinding.getRoot());

                this.dataBinding = dataBinding;

                dataBinding.getRoot().setOnClickListener(mOnClickListener);
            }
        }
    }

    public interface OnRecentlySuggestListener
    {
        void onItemClick(int position, StaySuggest staySuggest);

        void onDeleteClick(int position, StaySuggest staySuggest);
    }

    private class RecentlySuggestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private Context mContext;
        OnRecentlySuggestListener mListener;

        private List<ObjectItem> mSuggestList;

        public RecentlySuggestListAdapter(Context context, OnRecentlySuggestListener listener)
        {
            mContext = context;
            mListener = listener;

            setAll(null);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            switch (viewType)
            {
                case ObjectItem.TYPE_LOCATION_VIEW:
                {
                    ListRowSearchSuggestTypeNearbyDataBinding dataBinding //
                        = DataBindingUtil.inflate(LayoutInflater.from(mContext) //
                        , R.layout.list_row_search_suggest_type_nearby_data, parent, false);

                    NearByViewHolder nearByViewHolder = new NearByViewHolder(dataBinding);

                    return nearByViewHolder;
                }

                case ObjectItem.TYPE_SECTION:
                {
                    ListRowSearchSuggestTypeSectionDataBinding dataBinding //
                        = DataBindingUtil.inflate(LayoutInflater.from(mContext) //
                        , R.layout.list_row_search_suggest_type_section_data, parent, false);

                    SectionViewHolder sectionViewHolder = new SectionViewHolder(dataBinding);

                    return sectionViewHolder;
                }

                case ObjectItem.TYPE_FOOTER_VIEW:
                {
                    View view = new View(mContext);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(mContext, 10d));
                    view.setLayoutParams(params);

                    FooterViewHolder footerViewHolder = new FooterViewHolder(view);

                    return footerViewHolder;
                }

                case ObjectItem.TYPE_ENTRY:
                {
                    ListRowSearchSuggestTypeEntryDataBinding dataBinding //
                        = DataBindingUtil.inflate(LayoutInflater.from(mContext) //
                        , R.layout.list_row_search_suggest_type_entry_data, parent, false);

                    EntryViewHolder entryViewHolder = new EntryViewHolder(dataBinding);

                    return entryViewHolder;
                }
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            ObjectItem item = getItem(position);

            if (item == null)
            {
                return;
            }

            switch (item.mType)
            {
                case ObjectItem.TYPE_LOCATION_VIEW:
                    onBindViewHolder((NearByViewHolder) holder, item);
                    break;

                case ObjectItem.TYPE_SECTION:
                    onBindViewHolder((SectionViewHolder) holder, item, position);
                    break;

                case ObjectItem.TYPE_FOOTER_VIEW:
                    break;

                case ObjectItem.TYPE_ENTRY:
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

        public void setAll(List<ObjectItem> objectItemList)
        {
            if (mSuggestList == null)
            {
                mSuggestList = new ArrayList<>();
            }

            mSuggestList.clear();

            if (objectItemList != null && objectItemList.size() > 0)
            {
                mSuggestList.addAll(objectItemList);
            }
        }

        public ObjectItem getItem(int position)
        {
            if (position < 0 || mSuggestList.size() <= position)
            {
                return null;
            }

            return mSuggestList.get(position);
        }

        public StaySuggest removeItem(int position)
        {
            if (mSuggestList == null || mSuggestList.size() == 0)
            {
                return null;
            }

            ObjectItem removeItem = mSuggestList.remove(position);

            if (mSuggestList.size() == 1)
            {
                ObjectItem checkItem = mSuggestList.get(0);
                if (checkItem.mType == ObjectItem.TYPE_FOOTER_VIEW)
                {
                    mSuggestList.remove(0);
                }
            }

            StaySuggest staySuggest = removeItem.getItem();
            return staySuggest;
        }

        private void onBindViewHolder(NearByViewHolder holder, ObjectItem item)
        {
            StaySuggest staySuggest = item.getItem();

            holder.itemView.getRootView().setTag(staySuggest);

            holder.dataBinding.bottomDivider.setVisibility(View.VISIBLE);

            if (staySuggest.latitude != 0 && staySuggest.longitude != 0)
            {
                holder.dataBinding.descriptionTextView.setText(staySuggest.displayName);
            } else
            {
                holder.dataBinding.descriptionTextView.setText(R.string.label_search_nearby_description);
            }
        }

        private void onBindViewHolder(SectionViewHolder holder, ObjectItem item, int position)
        {
            StaySuggest staySuggest = item.getItem();

            if (DailyTextUtils.isTextEmpty(staySuggest.displayName) == true)
            {
                holder.dataBinding.titleTextView.setVisibility(View.GONE);
            } else
            {
                holder.dataBinding.titleTextView.setVisibility(View.VISIBLE);
            }

            holder.dataBinding.titleTextView.setText(staySuggest.displayName);
        }

        private void onBindViewHolder(EntryViewHolder holder, ObjectItem item, int position)
        {
            StaySuggest staySuggest = item.getItem();

            holder.itemView.getRootView().setTag(staySuggest);
            holder.itemView.getRootView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (mListener == null)
                    {
                        return;
                    }

                    mListener.onItemClick(position, staySuggest);
                }
            });

            holder.dataBinding.titleTextView.setText(staySuggest.displayName);

            holder.dataBinding.descriptionTextView.setVisibility(View.GONE);
            holder.dataBinding.priceTextView.setVisibility(View.GONE);
            holder.dataBinding.bottomDivider.setVisibility(View.GONE);
            holder.dataBinding.deleteImageView.setVisibility(View.VISIBLE);

            holder.dataBinding.deleteImageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (mListener == null)
                    {
                        return;
                    }

                    mListener.onDeleteClick(position, staySuggest);
                }
            });

            switch (staySuggest.categoryKey)
            {
                case StaySuggest.CATEGORY_STAY:
                case StaySuggest.CATEGORY_RECENTLY:
                    holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_02_hotel);
                    break;

                case StaySuggest.CATEGORY_LOCATION:
                    holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_09_nearby);
                    break;

                case StaySuggest.CATEGORY_REGION:
                    holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_01_region);
                    break;

                case StaySuggest.CATEGORY_STATION:
                    holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_06_train);
                    break;

                default:
                    holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_07_recent);
                    break;
            }
        }

        class NearByViewHolder extends RecyclerView.ViewHolder
        {
            ListRowSearchSuggestTypeNearbyDataBinding dataBinding;

            public NearByViewHolder(ListRowSearchSuggestTypeNearbyDataBinding dataBinding)
            {
                super(dataBinding.getRoot());

                this.dataBinding = dataBinding;
            }
        }

        class FooterViewHolder extends RecyclerView.ViewHolder
        {
            public FooterViewHolder(View itemView)
            {
                super(itemView);
            }
        }

        class SectionViewHolder extends RecyclerView.ViewHolder
        {
            ListRowSearchSuggestTypeSectionDataBinding dataBinding;

            public SectionViewHolder(ListRowSearchSuggestTypeSectionDataBinding dataBinding)
            {
                super(dataBinding.getRoot());

                this.dataBinding = dataBinding;
            }
        }

        class EntryViewHolder extends RecyclerView.ViewHolder
        {
            ListRowSearchSuggestTypeEntryDataBinding dataBinding;

            public EntryViewHolder(ListRowSearchSuggestTypeEntryDataBinding dataBinding)
            {
                super(dataBinding.getRoot());

                this.dataBinding = dataBinding;
            }
        }
    }
}

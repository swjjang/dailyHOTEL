package com.daily.dailyhotel.screen.stay.outbound.search;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.ListItem;
import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundSearchSuggestDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowStayOutboundSuggestEntryDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowStayOutboundSuggestTitleDataBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class StayOutboundSearchSuggestView extends BaseView<StayOutboundSearchSuggestView.OnEventListener, ActivityStayOutboundSearchSuggestDataBinding> implements StayOutboundSearchSuggestViewInterface, View.OnClickListener
{
    private SuggestListAdapter mSuggestListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onRequestSuggests(String keyword);

        void onSuggestClick(Suggest suggest);
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

        viewDataBinding.backImageView.setOnClickListener(this);
        viewDataBinding.keywordEditText.addTextChangedListener(mTextWatcher);
        viewDataBinding.deleteImageView.setVisibility(View.INVISIBLE);
        viewDataBinding.deleteImageView.setOnClickListener(this);
        viewDataBinding.suggestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        viewDataBinding.suggestsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            private int mDistance;
            private boolean mIsHide;
            private int mOldY;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState != RecyclerView.SCROLL_STATE_DRAGGING)
                {
                    mOldY = 0;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (mIsHide == true)
                {
                    mOldY = 0;
                } else
                {
                    if (recyclerView.getHeight() < ScreenUtils.getScreenHeight(getContext()) / 2)
                    {
                        mDistance += (dy - mOldY);
                        mOldY = dy;

                        if (mDistance > ScreenUtils.dpToPx(getContext(), 41) == true)
                        {
                            mDistance = 0;
                            mIsHide = true;

                            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                            Observable.just(false).delaySubscription(1, TimeUnit.SECONDS).subscribe(isHide -> mIsHide = isHide);
                        }
                    }
                }
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

            getEventListener().onRequestSuggests(editable.toString());
        }
    };

    class SuggestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
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
                    ListRowStayOutboundSuggestTitleDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_stay_outbound_suggest_title_data, parent, false);

                    TitleViewHolder titleViewHolder = new TitleViewHolder(dataBinding);

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

            switch (suggest.categoryKey)
            {
                case "airport":
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_04_airport, 0, 0, 0);
                    break;

                case "hotel":
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_02_hotel, 0, 0, 0);
                    break;

                case "point":
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_03_landmark, 0, 0, 0);
                    break;

                case "region":
                    holder.dataBinding.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ob_search_ic_01_region, 0, 0, 0);
                    break;

                case "station":
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
}

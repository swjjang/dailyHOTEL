package com.daily.dailyhotel.screen.home.search.gourmet.suggest;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.entity.ObjectItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowSearchSuggestTypeEntryDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowSearchSuggestTypeSectionDataBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2018. 2. 1..
 */

public class GourmetSuggestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    View.OnClickListener mOnClickListener;

    private String mKeyword;
    private List<ObjectItem> mSuggestList;

    public GourmetSuggestListAdapter(Context context, View.OnClickListener listener)
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
        GourmetSuggestV2 gourmetSuggest = item.getItem();

        holder.itemView.getRootView().setTag(gourmetSuggest);

        GourmetSuggestV2.Direct direct = (GourmetSuggestV2.Direct) gourmetSuggest.suggestItem;

        holder.dataBinding.descriptionTextView.setVisibility(View.GONE);
        holder.dataBinding.deleteImageView.setVisibility(View.GONE);
        holder.dataBinding.priceTextView.setVisibility(View.GONE);
        holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_07_recent);
        holder.dataBinding.bottomDivider.setVisibility(View.VISIBLE);
        holder.dataBinding.deleteImageView.setVisibility(View.GONE);

        if (DailyTextUtils.isTextEmpty(direct.name) == true)
        {
            holder.dataBinding.titleTextView.setText(null);
        } else
        {
            String text = mContext.getString(R.string.label_search_suggest_direct_search_format, direct.name);
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
        GourmetSuggestV2 gourmetSuggest = item.getItem();
        GourmetSuggestV2.Section section = (GourmetSuggestV2.Section) gourmetSuggest.suggestItem;

        if (DailyTextUtils.isTextEmpty(section.name) == true)
        {
            holder.dataBinding.titleTextView.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.titleTextView.setVisibility(View.VISIBLE);
        }

        holder.dataBinding.titleTextView.setText(section.name);
    }

    private void onBindViewHolder(EntryViewHolder holder, ObjectItem item, int position)
    {
        GourmetSuggestV2 gourmetSuggest = item.getItem();

        holder.itemView.getRootView().setTag(gourmetSuggest);

        //        holder.dataBinding.descriptionTextView.setVisibility(View.GONE);
        holder.dataBinding.bottomDivider.setVisibility(View.GONE);
        holder.dataBinding.deleteImageView.setVisibility(View.GONE);

        GourmetSuggestV2.SuggestItem suggestItem = gourmetSuggest.suggestItem;
        if (suggestItem == null)
        {
            holder.itemView.getRootView().setVisibility(View.GONE);
            ExLog.e("suggestItem is null - check GourmetSuggestV2");
            return;
        }

        String title = null;
        String description = null;
        if (suggestItem instanceof GourmetSuggestV2.Gourmet)
        {
            GourmetSuggestV2.Gourmet gourmet = (GourmetSuggestV2.Gourmet) suggestItem;
            title = gourmet.name;
            description = gourmet.province == null ? null : gourmet.province.name;

            holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_03_gourmet);

            holder.dataBinding.priceTextView.setVisibility(View.VISIBLE);

            if (gourmet.availableTickets == 0 || gourmet.availableTickets < gourmet.minimumOrderQuantity || gourmet.isExpired == true)
            {
                holder.dataBinding.priceTextView.setText(R.string.label_soldout);
            } else
            {
                holder.dataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, gourmet.discount, false));
            }
        } else if (suggestItem instanceof GourmetSuggestV2.Province)
        {
            GourmetSuggestV2.Province province = (GourmetSuggestV2.Province) suggestItem;

            if (province.area == null)
            {
                title = province.name + " " + mContext.getString(R.string.label_all);
                description = null;
            } else
            {
                title = province.area.name;
                description = province.name;
            }

            holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_01_region);

            holder.dataBinding.priceTextView.setVisibility(View.GONE);
        } else
        {
            title = suggestItem.name;
            description = null;

            holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_07_recent);

            holder.dataBinding.priceTextView.setVisibility(View.GONE);
        }

        if (DailyTextUtils.isTextEmpty(title))
        {
            holder.dataBinding.titleTextView.setText(null);
        } else
        {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(title);

            if (DailyTextUtils.isTextEmpty(mKeyword) == false)
            {
                String keywordUpperCase = mKeyword.toUpperCase();
                String displayNameUpperCase = DailyTextUtils.isTextEmpty(title) ? "" : title.toUpperCase();

                int fromIndex = 0;
                do
                {
                    int startIndex = displayNameUpperCase.indexOf(keywordUpperCase, fromIndex);

                    if (startIndex < 0)
                    {
                        break;
                    }

                    int endIndex = startIndex + keywordUpperCase.length();
                    fromIndex = endIndex;

                    spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
                        startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } while (true);
            }

            holder.dataBinding.titleTextView.setText(spannableStringBuilder);
        }

        holder.dataBinding.descriptionTextView.setText(description);
        holder.dataBinding.descriptionTextView.setVisibility(DailyTextUtils.isTextEmpty(description) ? View.GONE : View.VISIBLE);
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

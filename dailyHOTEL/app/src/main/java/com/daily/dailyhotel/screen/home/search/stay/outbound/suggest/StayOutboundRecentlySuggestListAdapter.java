package com.daily.dailyhotel.screen.home.search.stay.outbound.suggest;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowSearchSuggestTypeDeleteDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowSearchSuggestTypeEntryDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowSearchSuggestTypeNearbyDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowSearchSuggestTypeSectionDataBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2018. 2. 2..
 */

public class StayOutboundRecentlySuggestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public interface OnRecentlySuggestListener
    {
        void onItemClick(int position, StayOutboundSuggest stayOutboundSuggest);

        void onDeleteClick(int position, StayOutboundSuggest stayOutboundSuggest);

        void onNearbyClick(StayOutboundSuggest stayOutboundSuggest);
    }

    private Context mContext;
    OnRecentlySuggestListener mListener;

    private List<ObjectItem> mSuggestList;

    public StayOutboundRecentlySuggestListAdapter(Context context, OnRecentlySuggestListener listener)
    {
        mContext = context;
        this.mListener = listener;

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

                LocationViewHolder locationViewHolder = new LocationViewHolder(dataBinding);

                return locationViewHolder;
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
                ListRowSearchSuggestTypeDeleteDataBinding dataBinding //
                    = DataBindingUtil.inflate(LayoutInflater.from(mContext) //
                    , R.layout.list_row_search_suggest_type_delete_data, parent, false);

                FooterViewHolder footerViewHolder = new FooterViewHolder(dataBinding);

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
                onBindViewHolder((LocationViewHolder) holder, item);
                break;

            case ObjectItem.TYPE_SECTION:
                onBindViewHolder((SectionViewHolder) holder, item, position);
                break;

            case ObjectItem.TYPE_FOOTER_VIEW:
                onBindViewHolder((FooterViewHolder) holder);
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

    public int getEntryCount()
    {
        if (mSuggestList == null || mSuggestList.size() == 0)
        {
            return 0;
        }

        int count = 0;
        for (ObjectItem item : mSuggestList)
        {
            if (ObjectItem.TYPE_ENTRY == item.mType)
            {
                count++;
            }
        }

        return count;
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

    public StayOutboundSuggest removeItem(int position)
    {
        if (mSuggestList == null || mSuggestList.size() == 0)
        {
            return null;
        }

        if (position < 0 || position > mSuggestList.size() - 1)
        {
            return null;
        }

        ObjectItem removeItem = mSuggestList.remove(position);
        StayOutboundSuggest stayOutboundSuggest = removeItem.getItem();

        if (stayOutboundSuggest != null)
        {
            checkAndRemoveSection(stayOutboundSuggest.menuType);
        }

        if (mSuggestList.size() == 1)
        {
            ObjectItem checkItem = mSuggestList.get(0);
            if (checkItem.mType == ObjectItem.TYPE_FOOTER_VIEW)
            {
                mSuggestList.remove(0);
            }
        }

        return stayOutboundSuggest;
    }

    private void checkAndRemoveSection(int menuType)
    {
        if (mSuggestList == null || mSuggestList.size() == 0)
        {
            return;
        }

        int sectionPosition = -1;
        boolean hasEntry = false;

        for (int i = 0; i < mSuggestList.size(); i++)
        {
            ObjectItem item = mSuggestList.get(i);
            StayOutboundSuggest stayOutboundSuggest = item.getItem();

            if (stayOutboundSuggest == null || stayOutboundSuggest.menuType != menuType)
            {
                continue;
            }

            if (ObjectItem.TYPE_ENTRY == item.mType)
            {
                hasEntry = true;
                break;
            } else if (ObjectItem.TYPE_SECTION == item.mType)
            {
                sectionPosition = i;
            }
        }

        if (hasEntry == false && sectionPosition >= 0 && sectionPosition < mSuggestList.size())
        {
            mSuggestList.remove(sectionPosition);
        }
    }

    public void setNearByStayOutboundSuggest(StayOutboundSuggest nearByStayOutboundSuggest)
    {
        if (mSuggestList == null || mSuggestList.size() == 0 || nearByStayOutboundSuggest == null)
        {
            return;
        }

        String descriptionText = mContext.getString(R.string.label_search_nearby_description);

        for (ObjectItem item : mSuggestList)
        {
            if (ObjectItem.TYPE_LOCATION_VIEW == item.mType)
            {
                StayOutboundSuggest stayOutboundSuggest = item.getItem();

                stayOutboundSuggest.display = nearByStayOutboundSuggest != null ? nearByStayOutboundSuggest.display : descriptionText;
                stayOutboundSuggest.displayText = nearByStayOutboundSuggest != null ? nearByStayOutboundSuggest.displayText : descriptionText;
                stayOutboundSuggest.latitude = nearByStayOutboundSuggest.latitude;
                stayOutboundSuggest.longitude = nearByStayOutboundSuggest.longitude;
                stayOutboundSuggest.categoryKey = nearByStayOutboundSuggest.categoryKey;
                stayOutboundSuggest.menuType = nearByStayOutboundSuggest.menuType;
                break;
            }
        }
    }

    private void onBindViewHolder(LocationViewHolder holder, ObjectItem item)
    {
        StayOutboundSuggest stayOutboundSuggest = item.getItem();

        holder.itemView.getRootView().setTag(stayOutboundSuggest);
        holder.itemView.getRootView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mListener == null)
                {
                    return;
                }

                mListener.onNearbyClick(stayOutboundSuggest);
            }
        });

        holder.dataBinding.bottomDivider.setVisibility(View.VISIBLE);

        holder.dataBinding.descriptionTextView.setText(stayOutboundSuggest.displayText);

        if (DailyTextUtils.isTextEmpty(stayOutboundSuggest.displayText) == true)
        {
            holder.dataBinding.descriptionTextView.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.descriptionTextView.setVisibility(View.VISIBLE);
        }
    }

    private void onBindViewHolder(SectionViewHolder holder, ObjectItem item, int position)
    {
        StayOutboundSuggest stayOutboundSuggest = item.getItem();

        if (DailyTextUtils.isTextEmpty(stayOutboundSuggest.displayText) == true)
        {
            holder.dataBinding.titleTextView.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.titleTextView.setVisibility(View.VISIBLE);
        }

        holder.dataBinding.titleTextView.setText(stayOutboundSuggest.displayText);
    }

    private void onBindViewHolder(FooterViewHolder holder)
    {
        holder.dataBinding.deleteLayout.setVisibility(View.GONE);
        holder.dataBinding.deleteTextView.setOnClickListener(null);
    }

    private void onBindViewHolder(EntryViewHolder holder, ObjectItem item, int position)
    {
        StayOutboundSuggest stayOutboundSuggest = item.getItem();

        holder.itemView.getRootView().setTag(stayOutboundSuggest);
        holder.itemView.getRootView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mListener == null)
                {
                    return;
                }

                mListener.onItemClick(position, stayOutboundSuggest);
            }
        });

        if (DailyTextUtils.isTextEmpty(stayOutboundSuggest.country))
        {
            holder.dataBinding.descriptionTextView.setText(null);
            holder.dataBinding.descriptionTextView.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.descriptionTextView.setText(stayOutboundSuggest.country);
            holder.dataBinding.descriptionTextView.setVisibility(View.VISIBLE);
        }

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

                mListener.onDeleteClick(position, stayOutboundSuggest);
            }
        });

        String title = stayOutboundSuggest.displayText;

        switch (stayOutboundSuggest.categoryKey)
        {
            case StayOutboundSuggest.CATEGORY_AIRPORT:
                holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_ob_search_ic_04_airport);
                break;

            case StayOutboundSuggest.CATEGORY_HOTEL:
                holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_02_hotel);
                break;

            case StayOutboundSuggest.CATEGORY_LOCATION:
                holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_09_nearby);

                title = mContext.getString(R.string.label_search_suggest_type_location_item_format, title);
                break;

            case StayOutboundSuggest.CATEGORY_POINT:
                holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_04_landmark);
                break;

            case StayOutboundSuggest.CATEGORY_REGION:
                holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_01_region);
                break;

            case StayOutboundSuggest.CATEGORY_STATION:
                holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_06_train);
                break;

            default:
                holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_01_region);
                break;
        }

        holder.dataBinding.titleTextView.setText(title);
    }

    class LocationViewHolder extends RecyclerView.ViewHolder
    {
        ListRowSearchSuggestTypeNearbyDataBinding dataBinding;

        public LocationViewHolder(ListRowSearchSuggestTypeNearbyDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder
    {
        ListRowSearchSuggestTypeDeleteDataBinding dataBinding;

        public FooterViewHolder(ListRowSearchSuggestTypeDeleteDataBinding dataBinding)
        {
            super(dataBinding.getRoot());

            this.dataBinding = dataBinding;
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

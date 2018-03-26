package com.daily.dailyhotel.screen.home.search.stay.inbound.suggest;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.StaySuggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowSearchSuggestTypeDeleteDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowSearchSuggestTypeEntryDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowSearchSuggestTypeNearbyDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowSearchSuggestTypeSectionDataBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2018. 2. 1..
 */

public class StayRecentlySuggestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public interface OnRecentlySuggestListener
    {
        void onItemClick(int position, StaySuggest staySuggest);

        void onDeleteClick(int position, StaySuggest staySuggest);

        void onNearbyClick(StaySuggest staySuggest);
    }

    private Context mContext;
    OnRecentlySuggestListener mListener;

    private List<ObjectItem> mSuggestList;

    public StayRecentlySuggestListAdapter(Context context, OnRecentlySuggestListener listener)
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

                return new LocationViewHolder(dataBinding);
            }

            case ObjectItem.TYPE_SECTION:
            {
                ListRowSearchSuggestTypeSectionDataBinding dataBinding //
                    = DataBindingUtil.inflate(LayoutInflater.from(mContext) //
                    , R.layout.list_row_search_suggest_type_section_data, parent, false);

                return new SectionViewHolder(dataBinding);
            }

            case ObjectItem.TYPE_FOOTER_VIEW:
            {
                ListRowSearchSuggestTypeDeleteDataBinding dataBinding //
                    = DataBindingUtil.inflate(LayoutInflater.from(mContext) //
                    , R.layout.list_row_search_suggest_type_delete_data, parent, false);

                return new FooterViewHolder(dataBinding);
            }

            case ObjectItem.TYPE_ENTRY:
            {
                ListRowSearchSuggestTypeEntryDataBinding dataBinding //
                    = DataBindingUtil.inflate(LayoutInflater.from(mContext) //
                    , R.layout.list_row_search_suggest_type_entry_data, parent, false);

                return new EntryViewHolder(dataBinding);
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

    public StaySuggest removeItem(int position)
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
        StaySuggest staySuggest = removeItem.getItem();

        if (staySuggest != null)
        {
            checkAndRemoveSection(staySuggest.menuType);
        }

        if (mSuggestList.size() == 1)
        {
            ObjectItem checkItem = mSuggestList.get(0);
            if (checkItem.mType == ObjectItem.TYPE_FOOTER_VIEW)
            {
                mSuggestList.remove(0);
            }
        }

        return staySuggest;
    }

    private void checkAndRemoveSection(StaySuggest.MenuType menuType)
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
            StaySuggest staySuggest = item.getItem();

            if (staySuggest == null || staySuggest.menuType != menuType)
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

    public void setNearByStaySuggest(StaySuggest nearByStaySuggest)
    {
        if (mSuggestList == null || mSuggestList.size() == 0 || nearByStaySuggest == null)
        {
            return;
        }

        StaySuggest.Location nearByLocation = (StaySuggest.Location) nearByStaySuggest.getSuggestItem();
        if (nearByLocation == null)
        {
            return;
        }

        for (ObjectItem item : mSuggestList)
        {
            if (ObjectItem.TYPE_LOCATION_VIEW == item.mType)
            {
                StaySuggest staySuggest = item.getItem();
                StaySuggest.Location location = (StaySuggest.Location) staySuggest.getSuggestItem();

                location.name = nearByLocation.name;
                location.address = nearByLocation.address;
                location.latitude = nearByLocation.latitude;
                location.longitude = nearByLocation.longitude;
                staySuggest.menuType = nearByStaySuggest.menuType;
                break;
            }
        }
    }

    private void onBindViewHolder(LocationViewHolder holder, ObjectItem item)
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

                mListener.onNearbyClick(staySuggest);
            }
        });

        holder.dataBinding.bottomDivider.setVisibility(View.VISIBLE);

        holder.dataBinding.descriptionTextView.setText(staySuggest.getText2());
        holder.dataBinding.descriptionTextView.setVisibility(//
            DailyTextUtils.isTextEmpty(staySuggest.getText2()) ? View.GONE : View.VISIBLE);
    }

    private void onBindViewHolder(SectionViewHolder holder, ObjectItem item, int position)
    {
        StaySuggest staySuggest = item.getItem();

        if (DailyTextUtils.isTextEmpty(staySuggest.getText1()) == true)
        {
            holder.dataBinding.titleTextView.setVisibility(View.GONE);
            holder.dataBinding.titleTextView.setText(null);
        } else
        {
            holder.dataBinding.titleTextView.setVisibility(View.VISIBLE);
            holder.dataBinding.titleTextView.setText(staySuggest.getText1());
        }
    }

    private void onBindViewHolder(FooterViewHolder holder)
    {
        holder.dataBinding.deleteLayout.setVisibility(View.GONE);
        holder.dataBinding.deleteTextView.setOnClickListener(null);
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

        String title = staySuggest.getText1();
        String description = staySuggest.getText2();

        switch (staySuggest.getSuggestType())
        {
            case STATION:
            {
                holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_06_train);
                break;
            }

            case STAY:
            {
                holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_02_hotel);
                break;
            }

            case LOCATION:
            {
                holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_09_nearby);

                title = mContext.getString(R.string.label_search_suggest_type_location_item_format, title);
                description = null;
                break;
            }

            case AREA_GROUP:
            {
                holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_01_region);

                if (DailyTextUtils.isTextEmpty(description))
                {
                    title += " " + mContext.getString(R.string.label_all);
                }
                break;
            }

            default:
            {
                holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_07_recent);
                break;
            }
        }

        holder.dataBinding.titleTextView.setText(title);

        holder.dataBinding.descriptionTextView.setText(description);
        holder.dataBinding.descriptionTextView.setVisibility(DailyTextUtils.isTextEmpty(description) ? View.GONE : View.VISIBLE);
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

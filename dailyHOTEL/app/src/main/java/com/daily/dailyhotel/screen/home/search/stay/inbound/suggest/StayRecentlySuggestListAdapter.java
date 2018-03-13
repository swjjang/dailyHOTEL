package com.daily.dailyhotel.screen.home.search.stay.inbound.suggest;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.StaySuggestV2;
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
        void onItemClick(int position, StaySuggestV2 staySuggest);

        void onDeleteClick(int position, StaySuggestV2 staySuggest);

        void onNearbyClick(StaySuggestV2 staySuggest);
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

    public StaySuggestV2 removeItem(int position)
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
        StaySuggestV2 staySuggest = removeItem.getItem();

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
            StaySuggestV2 staySuggest = item.getItem();

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

    public void setNearByStaySuggest(StaySuggestV2 nearByStaySuggest)
    {
        if (mSuggestList == null || mSuggestList.size() == 0 || nearByStaySuggest == null)
        {
            return;
        }

        StaySuggestV2.Location nearByLocation = (StaySuggestV2.Location) nearByStaySuggest.suggestItem;
        if (nearByLocation == null)
        {
            return;
        }

        for (ObjectItem item : mSuggestList)
        {
            if (ObjectItem.TYPE_LOCATION_VIEW == item.mType)
            {
                StaySuggestV2 staySuggest = item.getItem();
                StaySuggestV2.Location location = (StaySuggestV2.Location) staySuggest.suggestItem;

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
        StaySuggestV2 staySuggest = item.getItem();
        StaySuggestV2.Location location = (StaySuggestV2.Location) staySuggest.suggestItem;

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

        holder.dataBinding.descriptionTextView.setText(location.address);

        if (DailyTextUtils.isTextEmpty(location.address) == true)
        {
            holder.dataBinding.descriptionTextView.setVisibility(View.GONE);
        } else
        {
            holder.dataBinding.descriptionTextView.setVisibility(View.VISIBLE);
        }
    }

    private void onBindViewHolder(SectionViewHolder holder, ObjectItem item, int position)
    {
        StaySuggestV2 staySuggest = item.getItem();
        StaySuggestV2.Section section = (StaySuggestV2.Section) staySuggest.suggestItem;

        if (section == null || DailyTextUtils.isTextEmpty(section.name) == true)
        {
            holder.dataBinding.titleTextView.setVisibility(View.GONE);
            holder.dataBinding.titleTextView.setText(null);
        } else
        {
            holder.dataBinding.titleTextView.setVisibility(View.VISIBLE);
            holder.dataBinding.titleTextView.setText(section.name);
        }
    }

    private void onBindViewHolder(FooterViewHolder holder)
    {
        holder.dataBinding.deleteLayout.setVisibility(View.GONE);
        holder.dataBinding.deleteTextView.setOnClickListener(null);
    }

    private void onBindViewHolder(EntryViewHolder holder, ObjectItem item, int position)
    {
        StaySuggestV2 staySuggest = item.getItem();
        StaySuggestV2.SuggestItem suggestItem = staySuggest.suggestItem;

        if (suggestItem == null)
        {
            holder.itemView.getRootView().setVisibility(View.GONE);
            ExLog.e("suggestItem is null - check StaySuggestV2");
            return;
        }

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

        String title = null;
        String description = null;

        if (suggestItem instanceof StaySuggestV2.Station)
        {
            holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_06_train);

            StaySuggestV2.Station station = (StaySuggestV2.Station) suggestItem;
            title = station.getDisplayName();
            description = station.region;
        } else if (suggestItem instanceof StaySuggestV2.Stay)
        {
            holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_02_hotel);

            StaySuggestV2.Stay stay = (StaySuggestV2.Stay) suggestItem;

            title = stay.name;
            description = stay.province == null ? null : stay.province.name;
        } else if (suggestItem instanceof StaySuggestV2.Location)
        {
            holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_09_nearby);

            StaySuggestV2.Location location = (StaySuggestV2.Location) suggestItem;

            title = mContext.getString(R.string.label_search_suggest_type_location_item_format, location.name);
            description = null;
        } else if (suggestItem instanceof StaySuggestV2.Province)
        {
            holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_01_region);

            StaySuggestV2.Province province = (StaySuggestV2.Province) suggestItem;

            if (province.area == null)
            {
                title = province.name + " " + mContext.getString(R.string.label_all);
                description = null;
            } else
            {
                title = province.area.name;
                description = province.name;
            }
        } else
        {
            holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_07_recent);

            title = suggestItem.name;
            description = null;
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

package com.daily.dailyhotel.screen.home.search.gourmet.suggest;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.entity.ObjectItem;
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

public class GourmetRecentlySuggestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public interface OnRecentlySuggestListener
    {
        void onItemClick(int position, GourmetSuggestV2 gourmetSuggest);

        void onDeleteClick(int position, GourmetSuggestV2 gourmetSuggest);

        void onNearbyClick(GourmetSuggestV2 gourmetSuggest);
    }

    private Context mContext;
    OnRecentlySuggestListener mListener;

    private List<ObjectItem> mSuggestList;

    public GourmetRecentlySuggestListAdapter(Context context, OnRecentlySuggestListener listener)
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

    public GourmetSuggestV2 removeItem(int position)
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
        GourmetSuggestV2 gourmetSuggest = removeItem.getItem();

        if (gourmetSuggest != null)
        {
            checkAndRemoveSection(gourmetSuggest.menuType);
        }

        if (mSuggestList.size() == 1)
        {
            ObjectItem checkItem = mSuggestList.get(0);
            if (checkItem.mType == ObjectItem.TYPE_FOOTER_VIEW)
            {
                mSuggestList.remove(0);
            }
        }

        return gourmetSuggest;
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
            GourmetSuggestV2 gourmetSuggest = item.getItem();

            if (gourmetSuggest == null || gourmetSuggest.menuType != menuType)
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

    public void setNearByGourmetSuggest(GourmetSuggestV2 nearByGourmetSuggest)
    {
        if (mSuggestList == null || mSuggestList.size() == 0 || nearByGourmetSuggest == null)
        {
            return;
        }

        GourmetSuggestV2.Location nearByLocation = (GourmetSuggestV2.Location) nearByGourmetSuggest.suggestItem;
        if (nearByLocation == null)
        {
            return;
        }

        for (ObjectItem item : mSuggestList)
        {
            if (ObjectItem.TYPE_LOCATION_VIEW == item.mType)
            {
                GourmetSuggestV2 gourmetSuggest = item.getItem();
                GourmetSuggestV2.Location location = (GourmetSuggestV2.Location) gourmetSuggest.suggestItem;

                location.name = nearByLocation.name;
                location.address = nearByLocation.address;
                location.latitude = nearByLocation.latitude;
                location.longitude = nearByLocation.longitude;
                gourmetSuggest.menuType = nearByGourmetSuggest.menuType;
                break;
            }
        }
    }

    private void onBindViewHolder(LocationViewHolder holder, ObjectItem item)
    {
        GourmetSuggestV2 gourmetSuggest = item.getItem();
        GourmetSuggestV2.Location location = (GourmetSuggestV2.Location) gourmetSuggest.suggestItem;

        holder.itemView.getRootView().setTag(gourmetSuggest);
        holder.itemView.getRootView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mListener == null)
                {
                    return;
                }

                mListener.onNearbyClick(gourmetSuggest);
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

    private void onBindViewHolder(FooterViewHolder holder)
    {
        holder.dataBinding.deleteLayout.setVisibility(View.GONE);
        holder.dataBinding.deleteTextView.setOnClickListener(null);
    }

    private void onBindViewHolder(EntryViewHolder holder, ObjectItem item, int position)
    {
        GourmetSuggestV2 gourmetSuggest = item.getItem();
        GourmetSuggestV2.SuggestItem suggestItem = gourmetSuggest.suggestItem;

        if (suggestItem == null)
        {
            holder.itemView.getRootView().setVisibility(View.GONE);
            ExLog.e("suggestItem is null - check GourmetSuggestV2");
            return;
        }

        holder.itemView.getRootView().setTag(gourmetSuggest);
        holder.itemView.getRootView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mListener == null)
                {
                    return;
                }

                mListener.onItemClick(position, gourmetSuggest);
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

                mListener.onDeleteClick(position, gourmetSuggest);
            }
        });

        String title = null;
        String description = null;
        if (suggestItem instanceof GourmetSuggestV2.Gourmet)
        {
            holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_03_gourmet);

            GourmetSuggestV2.Gourmet gourmet = (GourmetSuggestV2.Gourmet) suggestItem;
            title = gourmet.name;
            description = gourmet.province == null ? null : gourmet.province.name;
        } else if (suggestItem instanceof GourmetSuggestV2.Location)
        {
            holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_09_nearby);

            GourmetSuggestV2.Location location = (GourmetSuggestV2.Location) suggestItem;

            title = mContext.getString(R.string.label_search_suggest_type_location_item_format, location.name);
            description = null;
        } else if (suggestItem instanceof GourmetSuggestV2.Province)
        {
            holder.dataBinding.iconImageView.setVectorImageResource(R.drawable.vector_search_ic_01_region);

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

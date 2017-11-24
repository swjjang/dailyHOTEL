package com.daily.dailyhotel.screen.home.stay.inbound.region;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.Province;
import com.daily.dailyhotel.entity.Region;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutRegionListAreaDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutRegionListProvinceDataBinding;
import com.twoheart.dailyhotel.widget.DailyAnimatedExpandableListView.AnimatedExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

public class StayRegionListAdapter extends AnimatedExpandableListAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Region> mRegionList;
    private View.OnClickListener mOnItemClickListener;
    private boolean mTablet;

    public StayRegionListAdapter(Context context)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);

        mRegionList = new ArrayList<>();
    }

    public void setData(List<Region> regionList)
    {
        mRegionList.clear();
        mRegionList.addAll(regionList);
    }

    public void setOnChildClickListener(View.OnClickListener listener)
    {
        mOnItemClickListener = listener;
    }

    public List<Area> getChildren(int groupPosition)
    {
        if (mRegionList == null || mRegionList.size() == 0)
        {
            return null;
        }

        return mRegionList.get(groupPosition).getAreaList();
    }

    @Override
    public Area getChild(int groupPosition, int childPosition)
    {
        if (mRegionList == null || mRegionList.size() == 0)
        {
            return null;
        }

        return mRegionList.get(groupPosition).getAreaList().get(childPosition);
    }

    public Region getRegion(int groupPosition)
    {
        return mRegionList.get(groupPosition);
    }

    public void setTablet(boolean tablet)
    {
        mTablet = tablet;
    }

    private void setRealChildView(TextView textView, int groupPosition, Area area)
    {
        if (textView == null)
        {
            return;
        }

        textView.setOnClickListener(mOnItemClickListener);

        if (area != null)
        {
            textView.setTag(area);
            textView.setTag(textView.getId(), groupPosition);
            textView.setEnabled(true);
            textView.setText(null);

            if (area.index == -1)
            {
                textView.setText(area.name + " " + mContext.getString(R.string.label_all));
            } else
            {
                textView.setText(area.name);
            }
        } else
        {
            textView.setTag(null);
            textView.setTag(textView.getId(), null);
            textView.setEnabled(false);
            textView.setMaxWidth(-1);
            textView.setText(null);
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        final int COLUMN_COUNT = 2;

        Area leftArea = getChild(groupPosition, childPosition * COLUMN_COUNT);
        Area rightArea;

        if (childPosition * COLUMN_COUNT + 1 < mRegionList.get(groupPosition).getAreaList().size())
        {
            rightArea = getChild(groupPosition, childPosition * COLUMN_COUNT + 1);
        } else
        {
            rightArea = null;
        }

        LayoutRegionListAreaDataBinding viewDataBinding;

        if (convertView == null)
        {
            viewDataBinding = DataBindingUtil.inflate(mInflater, R.layout.layout_region_list_area_data, parent, false);
            convertView = viewDataBinding.getRoot();
        } else
        {
            Integer resourceId = (Integer) convertView.getTag(parent.getId());

            if (resourceId == null || resourceId != R.layout.layout_region_list_area_data)
            {
                viewDataBinding = DataBindingUtil.inflate(mInflater, R.layout.layout_region_list_area_data, parent, false);
                convertView = viewDataBinding.getRoot();
            } else
            {
                viewDataBinding = DataBindingUtil.getBinding(convertView);
            }
        }

        convertView.setTag(parent.getId(), R.layout.layout_region_list_area_data);

        setRealChildView(viewDataBinding.areaNameLeftTextView, groupPosition, leftArea);
        setRealChildView(viewDataBinding.areaNameRightTextView, groupPosition, rightArea);

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition)
    {
        int size = mRegionList.get(groupPosition).getAreaList().size();

        return size / 2 + size % 2;
    }

    @Override
    public Province getGroup(int groupPosition)
    {
        return mRegionList.get(groupPosition).getProvince();
    }

    @Override
    public int getGroupCount()
    {
        return mRegionList.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        Province province = getGroup(groupPosition);

        LayoutRegionListProvinceDataBinding viewDataBinding;

        if (convertView == null)
        {
            viewDataBinding = DataBindingUtil.inflate(mInflater, R.layout.layout_region_list_province_data, parent, false);
            convertView = viewDataBinding.getRoot();
        } else
        {
            Integer resourceId = (Integer) convertView.getTag(parent.getId());

            if (resourceId == null || resourceId != R.layout.layout_region_list_province_data)
            {
                viewDataBinding = DataBindingUtil.inflate(mInflater, R.layout.layout_region_list_province_data, parent, false);
                convertView = viewDataBinding.getRoot();
            } else
            {
                viewDataBinding = DataBindingUtil.getBinding(convertView);
            }
        }

        if (mTablet == true)
        {
            ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
            layoutParams.height = ScreenUtils.getScreenWidth(mContext) * 10 / 36;
        }

        convertView.setTag(parent.getId(), R.layout.layout_region_list_province_data);
        convertView.setTag(groupPosition);

        viewDataBinding.provinceTextView.setText(province.name);

        boolean hasChildren = getRealChildrenCount(groupPosition) > 0;

        // 우측 위아래 화살펴 표시 여부.
        viewDataBinding.arrowImageView.setVisibility(hasChildren ? View.VISIBLE : View.GONE);

        if (hasChildren == true)
        {
            if (getRegion(groupPosition).expandGroup == true)
            {
                viewDataBinding.arrowImageView.setImageResource(R.drawable.ic_region_ic_sub_v_top);
            } else
            {
                viewDataBinding.arrowImageView.setImageResource(R.drawable.ic_region_ic_sub_v);
            }
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1)
    {
        return true;
    }
}
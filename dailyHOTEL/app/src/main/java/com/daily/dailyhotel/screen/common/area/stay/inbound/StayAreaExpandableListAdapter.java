package com.daily.dailyhotel.screen.common.area.stay.inbound;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.AreaGroup;
import com.daily.dailyhotel.entity.StayArea;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutRegionListAreaDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutRegionListProvinceDataBinding;
import com.twoheart.dailyhotel.widget.DailyAnimatedExpandableListView.AnimatedExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

public class StayAreaExpandableListAdapter extends AnimatedExpandableListAdapter
{
    private Context mContext;
    private List<AreaGroup> mAreaGroupList;
    private View.OnClickListener mOnItemClickListener;
    private boolean mTablet;
    private int mSelectedGroupPosition;

    public StayAreaExpandableListAdapter(Context context)
    {
        mContext = context;
        mAreaGroupList = new ArrayList<>();

        setSelectedGroupPosition(-1);
    }

    public void setAreaGroup(List<? extends AreaGroup> areaGroupList)
    {
        mAreaGroupList.clear();
        mAreaGroupList.addAll(areaGroupList);
    }

    public void setOnChildClickListener(View.OnClickListener listener)
    {
        mOnItemClickListener = listener;
    }

    public List<? extends Area> getChildren(int groupPosition)
    {
        if (mAreaGroupList == null || mAreaGroupList.size() == 0)
        {
            return null;
        }

        return mAreaGroupList.get(groupPosition).getAreaList();
    }

    @Override
    public Area getChild(int groupPosition, int childPosition)
    {
        if (mAreaGroupList == null || mAreaGroupList.size() == 0)
        {
            return null;
        }

        return mAreaGroupList.get(groupPosition).getArea(childPosition);
    }

    public AreaGroup getAreaGroup(int groupPosition)
    {
        return mAreaGroupList.get(groupPosition);
    }

    public void setTablet(boolean tablet)
    {
        mTablet = tablet;
    }

    public void setSelectedGroupPosition(int position)
    {
        mSelectedGroupPosition = position;
    }

    private void setRealChildView(TextView textView, int groupPosition, Area stayTown)
    {
        if (textView == null)
        {
            return;
        }

        textView.setOnClickListener(mOnItemClickListener);

        if (stayTown != null)
        {
            textView.setTag(stayTown);
            textView.setTag(textView.getId(), groupPosition);
            textView.setEnabled(true);
            textView.setText(null);

            if (stayTown.index == StayArea.ALL)
            {
                textView.setText(stayTown.name + " " + mContext.getString(R.string.label_all));
            } else
            {
                textView.setText(stayTown.name);
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

        Area leftTown = getChild(groupPosition, childPosition * COLUMN_COUNT);
        Area rightTown;

        if (childPosition * COLUMN_COUNT + 1 < mAreaGroupList.get(groupPosition).getAreaCount())
        {
            rightTown = getChild(groupPosition, childPosition * COLUMN_COUNT + 1);
        } else
        {
            rightTown = null;
        }

        LayoutRegionListAreaDataBinding viewDataBinding;

        if (convertView == null)
        {
            viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_region_list_area_data, parent, false);
            convertView = viewDataBinding.getRoot();
        } else
        {
            Integer resourceId = (Integer) convertView.getTag(parent.getId());

            if (resourceId == null || resourceId != R.layout.layout_region_list_area_data)
            {
                viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_region_list_area_data, parent, false);
                convertView = viewDataBinding.getRoot();
            } else
            {
                viewDataBinding = DataBindingUtil.getBinding(convertView);
            }
        }

        convertView.setTag(parent.getId(), R.layout.layout_region_list_area_data);

        setRealChildView(viewDataBinding.areaNameLeftTextView, groupPosition, leftTown);
        setRealChildView(viewDataBinding.areaNameRightTextView, groupPosition, rightTown);

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition)
    {
        int size = mAreaGroupList.get(groupPosition).getAreaCount();

        return size / 2 + size % 2;
    }

    @Override
    public AreaGroup getGroup(int groupPosition)
    {
        return mAreaGroupList.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return mAreaGroupList.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        AreaGroup areaGroup = getGroup(groupPosition);

        LayoutRegionListProvinceDataBinding viewDataBinding;

        if (convertView == null)
        {
            viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_region_list_province_data, parent, false);
            convertView = viewDataBinding.getRoot();
        } else
        {
            Integer resourceId = (Integer) convertView.getTag(parent.getId());

            if (resourceId == null || resourceId != R.layout.layout_region_list_province_data)
            {
                viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_region_list_province_data, parent, false);
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

        viewDataBinding.provinceTextView.setText(areaGroup.name);

        boolean hasChildren = getRealChildrenCount(groupPosition) > 0;

        // 우측 위아래 화살펴 표시 여부.
        viewDataBinding.arrowImageView.setVisibility(hasChildren ? View.VISIBLE : View.GONE);

        if (hasChildren == true)
        {
            if (groupPosition == mSelectedGroupPosition)
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
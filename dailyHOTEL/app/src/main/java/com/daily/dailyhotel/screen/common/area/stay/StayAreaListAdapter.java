package com.daily.dailyhotel.screen.common.area.stay;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutRegionListAreaDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutRegionListProvinceDataBinding;
import com.twoheart.dailyhotel.widget.DailyAnimatedExpandableListView.AnimatedExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

public class StayAreaListAdapter extends AnimatedExpandableListAdapter
{
    private Context mContext;
    private List<StayAreaGroup> mStayDistrictList;
    private View.OnClickListener mOnItemClickListener;
    private boolean mTablet;
    private int mSelectedGroupPosition;

    public StayAreaListAdapter(Context context)
    {
        mContext = context;
        mStayDistrictList = new ArrayList<>();

        setSelectedGroupPosition(-1);
    }

    public void setData(List<StayAreaGroup> districtList)
    {
        mStayDistrictList.clear();
        mStayDistrictList.addAll(districtList);
    }

    public void setOnChildClickListener(View.OnClickListener listener)
    {
        mOnItemClickListener = listener;
    }

    public List<StayArea> getChildren(int groupPosition)
    {
        if (mStayDistrictList == null || mStayDistrictList.size() == 0)
        {
            return null;
        }

        return mStayDistrictList.get(groupPosition).getAreaList();
    }

    @Override
    public StayArea getChild(int groupPosition, int childPosition)
    {
        if (mStayDistrictList == null || mStayDistrictList.size() == 0)
        {
            return null;
        }

        return mStayDistrictList.get(groupPosition).getAreaList().get(childPosition);
    }

    public StayAreaGroup getAreaGroup(int groupPosition)
    {
        return mStayDistrictList.get(groupPosition);
    }

    public void setTablet(boolean tablet)
    {
        mTablet = tablet;
    }

    public void setSelectedGroupPosition(int position)
    {
        mSelectedGroupPosition = position;
    }

    private void setRealChildView(TextView textView, int groupPosition, StayArea stayTown)
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

        StayArea leftTown = getChild(groupPosition, childPosition * COLUMN_COUNT);
        StayArea rightTown;

        if (childPosition * COLUMN_COUNT + 1 < mStayDistrictList.get(groupPosition).getAreaCount())
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
        int size = mStayDistrictList.get(groupPosition).getAreaCount();

        return size / 2 + size % 2;
    }

    @Override
    public StayAreaGroup getGroup(int groupPosition)
    {
        return mStayDistrictList.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return mStayDistrictList.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        StayAreaGroup stayDistrict = getGroup(groupPosition);

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

        viewDataBinding.provinceTextView.setText(stayDistrict.name);

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
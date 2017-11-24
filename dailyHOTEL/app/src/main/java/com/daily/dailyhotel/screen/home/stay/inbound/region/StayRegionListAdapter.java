package com.daily.dailyhotel.screen.home.stay.inbound.region;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daily.base.util.ExLog;
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

    /**
     * AreaName 이 멀티라인으로 나올때 단말자체에서 우측에 여백을 발생시켜
     * areaName 과 areaCount 의 사이가 넓게 띄어져 보이는 이슈로인한 처리
     * 여백이 발생하는 위치(즉 1줄의 끝부분)에 \n 을 삽입해 강제로 2줄로 만드는 방법 사용
     *
     * @param textView
     * @param areaName
     */
    private void setAreaNameText(TextView textView, String areaName)
    {
        textView.setText(areaName+"/"+areaName);
//
//        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        textView.measure(widthMeasureSpec, heightMeasureSpec);
//
//        Layout layout = textView.getLayout();
//        if (layout == null)
//        {
//            ExLog.w("layout is null");
//            return;
//        }
//
//        int lineCount = layout.getLineCount();
//        if (lineCount > 1)
//        {
//            int firstLineEndIndex = layout.getLineEnd(0);
//
//            StringBuilder builder = new StringBuilder(areaName);
//            builder.insert(firstLineEndIndex, "\n");
//
//            textView.setText(builder.toString());
//        }
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
                setAreaNameText(textView, area.name + " " + mContext.getString(R.string.label_all));
            } else
            {
                setAreaNameText(textView, area.name);
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

        viewDataBinding.underLineView.setVisibility(isLastChild ? View.GONE : View.VISIBLE);

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

        boolean isExpandGroup = false;

        if (hasChildren == true)
        {
            isExpandGroup = getRegion(groupPosition).expandGroup;

            if (isExpandGroup == true)
            {
                viewDataBinding.arrowImageView.setImageResource(R.drawable.ic_region_ic_sub_v_top);
                viewDataBinding.underLineView.setVisibility(View.VISIBLE);
            } else
            {
                viewDataBinding.arrowImageView.setImageResource(R.drawable.ic_region_ic_sub_v);
                viewDataBinding.underLineView.setVisibility(View.GONE);
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
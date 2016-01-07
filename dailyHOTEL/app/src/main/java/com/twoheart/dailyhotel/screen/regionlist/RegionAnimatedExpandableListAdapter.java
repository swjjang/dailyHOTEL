package com.twoheart.dailyhotel.screen.regionlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.DailyAnimatedExpandableListView.AnimatedExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

public class RegionAnimatedExpandableListAdapter extends AnimatedExpandableListAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private Province mSelectedProvince;
    private List<RegionViewItem> items;
    private View.OnClickListener mOnItemclickListener;

    public RegionAnimatedExpandableListAdapter(Context context)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<RegionViewItem> items)
    {
        this.items = items;
    }

    public void setSelected(Province province)
    {
        mSelectedProvince = province;
    }

    public void setOnChildClickListener(View.OnClickListener listener)
    {
        mOnItemclickListener = listener;
    }

    public ArrayList<Area[]> getChildren(int groupPosition)
    {
        return items.get(groupPosition).getAreaList();
    }

    @Override
    public Area[] getChild(int groupPosition, int childPosition)
    {
        return items.get(groupPosition).getAreaList().get(childPosition);
    }

    public RegionViewItem getAreaItem(int groupPosition)
    {
        return items.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        Area[] area = getChild(groupPosition, childPosition);

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.list_row_area, parent, false);
        } else
        {
            Integer resourceId = (Integer) convertView.getTag(parent.getId());

            if (resourceId == null || resourceId.intValue() != R.layout.list_row_area)
            {
                convertView = mInflater.inflate(R.layout.list_row_area, parent, false);
            }
        }

        convertView.setTag(parent.getId(), R.layout.list_row_area);

        View areaLayout1 = convertView.findViewById(R.id.areaLayout1);
        View areaLayout2 = convertView.findViewById(R.id.areaLayout2);

        TextView areaTextView1 = (TextView) areaLayout1.findViewById(R.id.areaTextView1);
        TextView areaSubTextView1 = (TextView) areaLayout1.findViewById(R.id.areaSubTextView1);

        TextView areaTextView2 = (TextView) areaLayout2.findViewById(R.id.areaTextView2);
        TextView areaSubTextView2 = (TextView) areaLayout2.findViewById(R.id.areaSubTextView2);

        areaLayout1.setOnClickListener(mOnItemclickListener);
        areaLayout2.setOnClickListener(mOnItemclickListener);

        areaLayout1.setTag(area[0]);
        areaLayout1.setTag(areaLayout1.getId(), groupPosition);

        if (childPosition == 0)
        {
            areaTextView1.setText(area[0].tag);
            areaSubTextView1.setVisibility(View.GONE);
        } else
        {
            areaTextView1.setText(area[0].name);
            areaSubTextView1.setText(area[0].tag);

            if(Util.isTextEmpty(area[0].tag) == false)
            {
                areaSubTextView1.setVisibility(View.VISIBLE);
            } else
            {
                areaSubTextView1.setVisibility(View.GONE);
            }
        }

        if (area[1] != null)
        {
            areaLayout2.setTag(area[1]);
            areaLayout2.setTag(areaLayout2.getId(), groupPosition);
            areaLayout2.setEnabled(true);

            areaTextView2.setText(area[1].name);
            areaSubTextView2.setText(area[1].tag);

            if(Util.isTextEmpty(area[1].tag) == false)
            {
                areaSubTextView2.setVisibility(View.VISIBLE);
            } else
            {
                areaSubTextView2.setVisibility(View.GONE);
            }
        } else
        {
            areaLayout2.setTag(null);
            areaLayout2.setTag(areaLayout2.getId(), null);
            areaLayout2.setEnabled(false);

            areaTextView2.setText(null);
            areaSubTextView2.setText(null);
        }

        View underLineView = convertView.findViewById(R.id.underLineView);

        if (isLastChild == true)
        {
            underLineView.setVisibility(View.INVISIBLE);
        } else
        {
            underLineView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition)
    {
        return items.get(groupPosition).getAreaList().size();
    }

    @Override
    public Province getGroup(int groupPosition)
    {
        return items.get(groupPosition).getProvince();
    }

    @Override
    public int getGroupCount()
    {
        return items.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    private String getInsertSpaceName(String name)
    {
        char[] nameChars = name.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();

        for (char nameChar : nameChars)
        {
            stringBuilder.append(nameChar);
            stringBuilder.append(' ');
        }

        return stringBuilder.toString();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        Province province = getGroup(groupPosition);

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.list_row_province, parent, false);
        } else
        {
            Integer resourceId = (Integer) convertView.getTag(parent.getId());

            if (resourceId == null || resourceId.intValue() != R.layout.list_row_province)
            {
                convertView = mInflater.inflate(R.layout.list_row_province, parent, false);
            }
        }

        convertView.setTag(parent.getId(), R.layout.list_row_province);
        convertView.setTag(groupPosition);

        ImageView provinceImageView = (ImageView) convertView.findViewById(R.id.provinceImageView);
        ImageView arrowImageView = (ImageView) convertView.findViewById(R.id.updownArrowImageView);
        TextView textView = (TextView) convertView.findViewById(R.id.provinceTextView);
        TextView englishTextView = (TextView) convertView.findViewById(R.id.provinceEnglishTextView);

        Glide.with(mContext).load(province.imageUrl).crossFade().into(provinceImageView);

        textView.setText(getInsertSpaceName(province.name));
        englishTextView.setText(getInsertSpaceName(province.englishName));

        boolean hasChildren = getRealChildrenCount(groupPosition) > 0;
        boolean isSelected = false;

        // 우측 위아래 화살펴 표시 여부.
        if (hasChildren == true)
        {
            arrowImageView.setVisibility(View.VISIBLE);
        } else
        {
            arrowImageView.setVisibility(View.GONE);
        }

        if (hasChildren == true)
        {
            if (getAreaItem(groupPosition).isExpandGroup == true)
            {
                arrowImageView.setImageResource(R.drawable.region_ic_sub_v_top);
            } else
            {
                arrowImageView.setImageResource(R.drawable.region_ic_sub_v);
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
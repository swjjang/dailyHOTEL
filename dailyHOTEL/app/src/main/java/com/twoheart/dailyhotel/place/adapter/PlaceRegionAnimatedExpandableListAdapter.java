package com.twoheart.dailyhotel.place.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyAnimatedExpandableListView.AnimatedExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

public class PlaceRegionAnimatedExpandableListAdapter extends AnimatedExpandableListAdapter
{
    private LayoutInflater mInflater;
    private List<RegionViewItem> items;
    private View.OnClickListener mOnItemclickListener;

    public PlaceRegionAnimatedExpandableListAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<RegionViewItem> items)
    {
        this.items = items;
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

            if (resourceId == null || resourceId != R.layout.list_row_area)
            {
                convertView = mInflater.inflate(R.layout.list_row_area, parent, false);
            }
        }

        convertView.setTag(parent.getId(), R.layout.list_row_area);

        TextView areaTextView1 = (TextView) convertView.findViewById(R.id.areaTextView1);
        TextView areaTextView2 = (TextView) convertView.findViewById(R.id.areaTextView2);

        areaTextView1.setOnClickListener(mOnItemclickListener);
        areaTextView2.setOnClickListener(mOnItemclickListener);

        areaTextView1.setTag(area[0]);
        areaTextView1.setTag(areaTextView1.getId(), groupPosition);
        areaTextView1.setText(area[0].tag);

        if (area[1] != null)
        {
            areaTextView2.setTag(area[1]);
            areaTextView2.setTag(areaTextView2.getId(), groupPosition);
            areaTextView2.setEnabled(true);

            areaTextView2.setText(area[1].tag);
        } else
        {
            areaTextView2.setTag(null);
            areaTextView2.setTag(areaTextView2.getId(), null);
            areaTextView2.setEnabled(false);

            areaTextView2.setText(null);
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

            if (resourceId == null || resourceId != R.layout.list_row_province)
            {
                convertView = mInflater.inflate(R.layout.list_row_province, parent, false);
            }
        }

        convertView.setTag(parent.getId(), R.layout.list_row_province);
        convertView.setTag(groupPosition);

        com.facebook.drawee.view.SimpleDraweeView provinceImageView = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.provinceImageView);
        ImageView arrowImageView = (ImageView) convertView.findViewById(R.id.updownArrowImageView);
        TextView textView = (TextView) convertView.findViewById(R.id.provinceTextView);
        TextView englishTextView = (TextView) convertView.findViewById(R.id.provinceEnglishTextView);

        Uri uri = Util.isTextEmpty(province.imageUrl) ? null : Uri.parse(province.imageUrl);
        provinceImageView.setImageURI(uri);

        textView.setText(getInsertSpaceName(province.name));
        englishTextView.setText(getInsertSpaceName(province.englishName));

        boolean hasChildren = getRealChildrenCount(groupPosition) > 0;

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
package com.twoheart.dailyhotel.screen.region;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.AreaItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.view.DailyAnimatedExpandableListView.AnimatedExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

public class RegionAnimatedExpandableListAdapter extends AnimatedExpandableListAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private Province mSelectedProvince;
    private List<AreaItem> items;

    public RegionAnimatedExpandableListAdapter(Context context)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<AreaItem> items)
    {
        this.items = items;
    }

    public void setSelected(Province province)
    {
        mSelectedProvince = province;
    }

    public ArrayList<Area> getChildren(int groupPosition)
    {
        return items.get(groupPosition).getAreaList();
    }

    @Override
    public Area getChild(int groupPosition, int childPosition)
    {
        return items.get(groupPosition).getAreaList().get(childPosition);
    }

    public AreaItem getAreaItem(int groupPosition)
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
        Area area = getChild(groupPosition, childPosition);

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

        TextView textView = (TextView) convertView.findViewById(R.id.areaTextView);

        textView.setText(area.tag);

        boolean isSelected = false;

        if (mSelectedProvince != null)
        {
            if (mSelectedProvince instanceof Area)
            {
                if (mSelectedProvince.index == area.index && mSelectedProvince.name.equalsIgnoreCase(area.name) == true)
                {
                    isSelected = true;
                }
            } else
            {
                if (mSelectedProvince.getProvinceIndex() == ((Province) area).getProvinceIndex() && area.index == -1)
                {
                    isSelected = true;
                }
            }
        }

        if (isSelected == true)
        {
            textView.setTextColor(mContext.getResources().getColor(R.color.white));
            textView.setBackgroundColor(mContext.getResources().getColor(R.color.dh_theme_color));
        } else
        {
            try
            {
                XmlResourceParser parser = mContext.getResources().getXml(R.drawable.selector_textview_selectarea_childcolor);
                ColorStateList colors = ColorStateList.createFromXml(mContext.getResources(), parser);
                textView.setTextColor(colors);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            textView.setBackgroundResource(R.drawable.selector_background_area);
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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        Province province = getGroup(groupPosition);

        if (province == null)
        {
            convertView = mInflater.inflate(R.layout.list_row_region_footer, parent, false);
            convertView.setTag(null);
            convertView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });

            return convertView;
        } else
        {
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
        }

        convertView.setTag(parent.getId(), R.layout.list_row_province);
        convertView.setTag(groupPosition);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.updownArrowImageView);
        TextView textView = (TextView) convertView.findViewById(R.id.provinceTextView);

        textView.setText(province.name);

        boolean hasChildren = getRealChildrenCount(groupPosition) > 0;
        boolean isSelected = false;

        // 우측 위아래 화살펴 표시 여부.
        if (hasChildren == true)
        {
            imageView.setVisibility(View.VISIBLE);
        } else
        {
            imageView.setVisibility(View.GONE);
        }

        if (mSelectedProvince instanceof Area == false && mSelectedProvince.index == province.index)
        {
            if (hasChildren == false)
            {
                isSelected = true;
            }
        }

        if (isSelected == true)
        {
            textView.setBackgroundColor(mContext.getResources().getColor(R.color.dh_theme_color));
            textView.setTextColor(mContext.getResources().getColor(R.color.white));
        } else
        {
            if (hasChildren == true)
            {
                textView.setTextColor(mContext.getResources().getColor(R.color.selectarea_text_group));
                textView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            } else
            {
                try
                {
                    XmlResourceParser parser = mContext.getResources().getXml(R.drawable.selector_textview_selectarea_groupcolor);
                    ColorStateList colors = ColorStateList.createFromXml(mContext.getResources(), parser);
                    textView.setTextColor(colors);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                textView.setBackgroundResource(R.drawable.selector_background_province);
            }
        }

        if (hasChildren == true)
        {
            if (getAreaItem(groupPosition).isExpandGroup == true)
            {
                imageView.setImageResource(R.drawable.ic_details_menu_on);
            } else
            {
                imageView.setImageResource(R.drawable.ic_details_menu_off);
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
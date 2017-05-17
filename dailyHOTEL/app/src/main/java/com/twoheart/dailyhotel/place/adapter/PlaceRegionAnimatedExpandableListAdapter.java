package com.twoheart.dailyhotel.place.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.widget.DailyAnimatedExpandableListView.AnimatedExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

public class PlaceRegionAnimatedExpandableListAdapter extends AnimatedExpandableListAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<RegionViewItem> items;
    private View.OnClickListener mOnItemClickListener;
    private boolean mIsTablet;

    public PlaceRegionAnimatedExpandableListAdapter(Context context)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<RegionViewItem> items)
    {
        this.items = items;
    }

    public void setOnChildClickListener(View.OnClickListener listener)
    {
        mOnItemClickListener = listener;
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

    public void setIsTablet(boolean isTablet)
    {
        mIsTablet = isTablet;
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
        textView.setText(areaName);

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);

        Layout layout = textView.getLayout();
        if (layout == null)
        {
            ExLog.w("layout is null");
            return;
        }

        int lineCount = layout.getLineCount();
        if (lineCount > 1)
        {
            int firstLineEndIndex = layout.getLineEnd(0);

            StringBuilder builder = new StringBuilder(areaName);
            builder.insert(firstLineEndIndex, "\n");

            textView.setText(builder.toString());
        }
    }

    /**
     * 소지역 이름 뷰의 최대 넓이 셋팅하는 메소드
     * @param layout
     * @param nameTextView
     * @param countTextView
     */
    private void setNameTextViewMaxWidth(View layout, TextView nameTextView, TextView countTextView) {
        // 전체 레이아웃 넓이
        int layoutWidth = (ScreenUtils.getScreenWidth(mContext) / 2) - 1;
        int widthMeasureSpec1 = View.MeasureSpec.makeMeasureSpec(layoutWidth, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec1 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        layout.measure(widthMeasureSpec1, heightMeasureSpec1);

        int maxWidth = layout.getMeasuredWidth() - layout.getPaddingLeft() - layout.getPaddingRight();

        // 숫자 레이아웃 넓이
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        countTextView.measure(widthMeasureSpec, heightMeasureSpec);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) countTextView.getLayoutParams();

        int countWidth = countTextView.getMeasuredWidth() + params.leftMargin + params.rightMargin;

        nameTextView.setMaxWidth(maxWidth - countWidth);
    }

    private void setRealChildView(View convertView, int groupPosition, Area[] areas, int index)
    {
        View layout = convertView.findViewById(index == 0 ? R.id.areaLayout1 : R.id.areaLayout2);
        TextView nameTextView = (TextView) convertView.findViewById(index == 0 ? R.id.areaNameTextView1 : R.id.areaNameTextView2);
        TextView countTextView = (TextView) convertView.findViewById(index == 0 ? R.id.areaCountTextView1 : R.id.areaCountTextView2);

        layout.setOnClickListener(mOnItemClickListener);

        Area area = areas[index];

        if (area != null)
        {
            layout.setTag(area);
            layout.setTag(layout.getId(), groupPosition);
            layout.setEnabled(true);

            countTextView.setText(Integer.toString(area.count));

            setNameTextViewMaxWidth(layout, nameTextView, countTextView);
            setAreaNameText(nameTextView, area.name);
        } else
        {
            layout.setTag(null);
            layout.setTag(layout.getId(), null);
            layout.setEnabled(false);

            countTextView.setText(null);
            nameTextView.setMaxWidth(-1);
            nameTextView.setText(null);
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
        Area[] areas = getChild(groupPosition, childPosition);

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

        setRealChildView(convertView, groupPosition, areas, 0);
        setRealChildView(convertView, groupPosition, areas, 1);

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

        if (mIsTablet == true)
        {
            ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
            layoutParams.height = ScreenUtils.getScreenWidth(mContext) * 10 / 36;
        }

        convertView.setTag(parent.getId(), R.layout.list_row_province);
        convertView.setTag(groupPosition);

        View topDivider = convertView.findViewById(R.id.topDivider);
        topDivider.setVisibility(groupPosition == 0 ? View.GONE : View.VISIBLE);

        ImageView arrowImageView = (ImageView) convertView.findViewById(R.id.updownArrowImageView);
        TextView textView = (TextView) convertView.findViewById(R.id.provinceTextView);
        View expandBottomLineView = convertView.findViewById(R.id.expandBottomLine);
        View collapseBottomLineView = convertView.findViewById(R.id.collapseBottomLine);

        textView.setText(getInsertSpaceName(province.name));

        boolean hasChildren = getRealChildrenCount(groupPosition) > 0;

        // 우측 위아래 화살펴 표시 여부.
        if (hasChildren == true)
        {
            arrowImageView.setVisibility(View.VISIBLE);
        } else
        {
            arrowImageView.setVisibility(View.GONE);
        }

        boolean isExpandGroup = false;

        if (hasChildren == true)
        {
            isExpandGroup = getAreaItem(groupPosition).isExpandGroup;

            if (isExpandGroup == true)
            {
                arrowImageView.setImageResource(R.drawable.ic_region_ic_sub_v_top);
            } else
            {
                arrowImageView.setImageResource(R.drawable.ic_region_ic_sub_v);
            }
        }

        expandBottomLineView.setVisibility(hasChildren == true && isExpandGroup == true ? View.VISIBLE : View.GONE);
        collapseBottomLineView.setVisibility(hasChildren == true && isExpandGroup == true ? View.GONE : View.VISIBLE);

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
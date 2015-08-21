package com.twoheart.dailyhotel.adapter;

import java.util.ArrayList;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Board;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class BoardListAdapter extends BaseExpandableListAdapter
{
	private ArrayList<Board> list = null;
	private LayoutInflater inflater = null;
	private Boolean groupClickState[];

	public BoardListAdapter(Context context, ArrayList<Board> list)
	{
		super();
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		groupClickState = new Boolean[list.size()];
		for (int i = 0; i < groupClickState.length; i++)
			groupClickState[i] = false;
	}

	@Override
	public String getChild(int groupPosition, int childPosition)
	{
		return list.get(groupPosition).getContent();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{

		View v = convertView;

		if (v == null)
			v = inflater.inflate(R.layout.list_row_board_child, null);

		TextView tv_content = (TextView) v.findViewById(R.id.tv_board_content);
		tv_content.setText(getChild(groupPosition, 0));

		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return list.get(groupPosition).getSubject();
	}

	@Override
	public int getGroupCount()
	{
		return list.size();
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
	{
		View v = convertView;
		if (v == null)
			v = inflater.inflate(R.layout.list_row_board_group, parent, false);

		TextView tv_subject = (TextView) v.findViewById(R.id.tv_board_subject);
		TextView tv_regdate = (TextView) v.findViewById(R.id.tv_board_regdate);
		tv_subject.setText((String) getGroup(groupPosition));

		if (TextUtils.isEmpty(list.get(groupPosition).getRegdate()) == true)
		{
			tv_regdate.setVisibility(View.GONE);
		} else
		{
			tv_regdate.setVisibility(View.VISIBLE);
			tv_regdate.setText(list.get(groupPosition).getRegdate());
		}

		return v;
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

	@Override
	public void onGroupExpanded(int groupPosition)
	{
		groupClickState[groupPosition] = true;
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public void onGroupCollapsed(int groupPosition)
	{
		groupClickState[groupPosition] = false;
		super.onGroupCollapsed(groupPosition);
	}

}

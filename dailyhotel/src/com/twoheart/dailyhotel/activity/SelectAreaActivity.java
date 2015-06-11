package com.twoheart.dailyhotel.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.ui.DailyAnimatedExpandableListView;
import com.twoheart.dailyhotel.ui.DailyAnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class SelectAreaActivity extends BaseActivity
{
	private DailyAnimatedExpandableListView mListView;
	private ArrayList<AreaItem> mArrayList;

	private class AreaItem
	{
		Province province;
		ArrayList<Area> areaList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectarea);
		setActionBar(R.string.label_selectarea_area);

		Intent intent = getIntent();

		if (intent == null)
		{
			finish();
			return;
		}

		ArrayList<Province> mProvinceList = intent.getParcelableArrayListExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
		ArrayList<Area> mAreaList = intent.getParcelableArrayListExtra(NAME_INTENT_EXTRA_DATA_AREA);

		mArrayList = new ArrayList<AreaItem>(mProvinceList.size());

		for (Province province : mProvinceList)
		{
			AreaItem item = new AreaItem();

			item.province = province;
			item.areaList = new ArrayList<Area>();

			for (Area area : mAreaList)
			{
				if (province.index == area.provinceIndex)
				{
					if (item.areaList.size() == 0)
					{
						Area totalArea = new Area();

						totalArea.index = -1;
						totalArea.name = province.name + " 전체";
						totalArea.provinceIndex = province.index;
						totalArea.sequence = -1;
						totalArea.tag = totalArea.name;

						item.areaList.add(totalArea);
					}

					item.areaList.add(area);
				}
			}

			mArrayList.add(item);
		}

		initLayout();
	}

	private void initLayout()
	{
		AreaAnimatedExpandableListAdapter adapter = new AreaAnimatedExpandableListAdapter(this);
		adapter.setData(mArrayList);

		mListView = (DailyAnimatedExpandableListView) findViewById(R.id.listview);
		mListView.setAdapter(adapter);

		mListView.setOnGroupClickListener(new OnGroupClickListener()
		{
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
			{
				//
				if (mArrayList.get(groupPosition).areaList.size() == 0)
				{
					Intent intent = new Intent();
					intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, mArrayList.get(groupPosition).province);
					setResult(RESULT_OK, intent);
					finish();
					return true;
				}

				if (mListView.isGroupExpanded(groupPosition))
				{
					mListView.collapseGroupWithAnimation(groupPosition);
				} else
				{
					mListView.expandGroupWithAnimation(groupPosition);
				}
				return true;
			}
		});

		mListView.setOnChildClickListener(new OnChildClickListener()
		{
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
			{
				Intent intent = new Intent();

				if (childPosition == 0)
				{
					intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, mArrayList.get(groupPosition).province);

				} else
				{
					intent.putExtra(NAME_INTENT_EXTRA_DATA_AREA, mArrayList.get(groupPosition).areaList.get(childPosition));
				}

				setResult(RESULT_OK, intent);
				finish();

				return false;
			}
		});
	}

	@Override
	public void onBackPressed()
	{
		setResult(RESULT_CANCELED);

		super.onBackPressed();
	}

	@Override
	public void finish()
	{
		super.finish();

		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}

	private class AreaAnimatedExpandableListAdapter extends
			AnimatedExpandableListAdapter
	{
		private LayoutInflater inflater;

		private List<AreaItem> items;

		public AreaAnimatedExpandableListAdapter(Context context)
		{
			inflater = LayoutInflater.from(context);
		}

		public void setData(List<AreaItem> items)
		{
			this.items = items;
		}

		@Override
		public Area getChild(int groupPosition, int childPosition)
		{
			return items.get(groupPosition).areaList.get(childPosition);
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
				convertView = inflater.inflate(R.layout.list_row_area, parent, false);
			}

			TextView textView = (TextView) convertView.findViewById(R.id.areaTextView);

			textView.setText(area.tag);

			return convertView;
		}

		@Override
		public int getRealChildrenCount(int groupPosition)
		{
			return items.get(groupPosition).areaList.size();
		}

		@Override
		public Province getGroup(int groupPosition)
		{
			return items.get(groupPosition).province;
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

			if (convertView == null)
			{
				convertView = inflater.inflate(R.layout.list_row_province, parent, false);
			}

			TextView textView = (TextView) convertView.findViewById(R.id.provinceTextView);

			textView.setText(province.name);

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
}

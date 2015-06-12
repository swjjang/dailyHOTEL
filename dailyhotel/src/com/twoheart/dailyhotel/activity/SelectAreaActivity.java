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
import com.twoheart.dailyhotel.model.AreaItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.ui.DailyAnimatedExpandableListView;
import com.twoheart.dailyhotel.ui.DailyAnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class SelectAreaActivity extends BaseActivity
{
	private DailyAnimatedExpandableListView mListView;
	private AreaAnimatedExpandableListAdapter mAdapter;

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

		Province province = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
		ArrayList<AreaItem> arrayList = intent.getParcelableArrayListExtra(NAME_INTENT_EXTRA_DATA_AREAITEMLIST);

		initLayout(arrayList, province);

		if (province instanceof Area)
		{
			int size = arrayList.size();
			Area selectedArea = (Area) province;

			for (int i = 0; i < size; i++)
			{
				AreaItem areaItem = arrayList.get(i);

				if (selectedArea.provinceIndex == areaItem.getProvince().index)
				{
					if (areaItem.getAreaList().size() == 0)
					{
						// 상세 지역이 없는 경우.
						mListView.setSelection(i);
						mListView.setSelectedGroup(i);
					} else
					{
						ArrayList<Area> areaList = areaItem.getAreaList();
						int areaSize = areaList.size();

						for (int j = 0; j < areaSize; j++)
						{
							Area area = areaList.get(j);

							if (area.index == selectedArea.index)
							{
								mListView.setSelection(i);
								mListView.expandGroup(i);
								break;
							}
						}
					}
					break;
				}
			}
		} else
		{
			int size = arrayList.size();

			for (int i = 0; i < size; i++)
			{
				AreaItem areaItem = arrayList.get(i);

				if (province.index == areaItem.getProvince().index)
				{
					if (areaItem.getAreaList().size() == 0)
					{
						// 상세 지역이 없는 경우.
						mListView.setSelection(i);
						mListView.setSelectedGroup(i);
					} else
					{
						mListView.setSelection(i);
						mListView.expandGroup(i);
					}
					break;
				}
			}
		}
	}

	private void initLayout(ArrayList<AreaItem> arrayList, Province province)
	{
		mAdapter = new AreaAnimatedExpandableListAdapter(this);
		mAdapter.setData(arrayList);
		mAdapter.setSelected(province);

		mListView = (DailyAnimatedExpandableListView) findViewById(R.id.listview);
		mListView.setAdapter(mAdapter);

		mListView.setOnGroupClickListener(new OnGroupClickListener()
		{
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
			{
				//
				if (mAdapter.getChildrenCount(groupPosition) == 0)
				{
					mAdapter.setSelected(mAdapter.getGroup(groupPosition));
					mAdapter.notifyDataSetChanged();

					Intent intent = new Intent();
					intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, mAdapter.getGroup(groupPosition));
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
					mAdapter.setSelected(mAdapter.getChildren(groupPosition).get(childPosition));
					mAdapter.notifyDataSetChanged();

					intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, mAdapter.getGroup(groupPosition));

				} else
				{
					mAdapter.setSelected(mAdapter.getChildren(groupPosition).get(childPosition));
					mAdapter.notifyDataSetChanged();

					intent.putExtra(NAME_INTENT_EXTRA_DATA_AREA, mAdapter.getChildren(groupPosition).get(childPosition));
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
		private Province mSelectedProvince;
		private List<AreaItem> items;

		public AreaAnimatedExpandableListAdapter(Context context)
		{
			inflater = LayoutInflater.from(context);
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

			boolean isSelected = false;

			if (mSelectedProvince instanceof Area)
			{
				if (mSelectedProvince.index == area.index && mSelectedProvince.name.equalsIgnoreCase(area.name) == true)
				{
					isSelected = true;
				}
			} else if (mSelectedProvince instanceof Province)
			{
				if (mSelectedProvince.index == area.provinceIndex && area.index == -1)
				{
					isSelected = true;
				}
			}

			if (isSelected == true)
			{
				convertView.setBackgroundColor(getResources().getColor(R.color.dh_theme_color));
			} else
			{
				convertView.setBackgroundResource(R.drawable.selector_background_area);
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

			if (convertView == null)
			{
				convertView = inflater.inflate(R.layout.list_row_province, parent, false);
			}

			TextView textView = (TextView) convertView.findViewById(R.id.provinceTextView);

			textView.setText(province.name);

			boolean isSelected = false;

			if (mSelectedProvince instanceof Area == false && mSelectedProvince.index == province.index)
			{
				if (getRealChildrenCount(groupPosition) == 0)
				{
					isSelected = true;
				}
			}

			if (isSelected == true)
			{
				convertView.setBackgroundColor(getResources().getColor(R.color.dh_theme_color));
			} else
			{
				convertView.setBackgroundResource(R.drawable.selector_background_province);
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
}

package com.twoheart.dailyhotel.widget;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;

public class RegionPopupListView extends LinearLayout
{
	private ListView mListView;
	private ListAdatper mAdapter;
	private UserActionListener mUserActionListener;

	private ArrayList<String> mRegionArrayList;

	public interface UserActionListener
	{
		public void onItemClick(int position);
	}

	public RegionPopupListView(Context context)
	{
		super(context);
		init();
	}

	public RegionPopupListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public RegionPopupListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public void setData(ArrayList<String> regionList)
	{
		mRegionArrayList = regionList;
		mAdapter.notifyDataSetChanged();
	}

	public void setUserActionListener(UserActionListener l)
	{
		mUserActionListener = l;
	}

	private void init()
	{
		LayoutInflater.from(getContext()).inflate(R.layout.popup_list_region, this, true);

		mListView = (ListView) findViewById(R.id.regionListView);
		mListView.setOnItemClickListener(mOnItemClickListener);

		mAdapter = new ListAdatper();
		mListView.setAdapter(mAdapter);
	}

	private class ListAdatper extends BaseAdapter
	{
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			String item = (String) getItem(position);

			LinearLayout linearLayout = (LinearLayout) View.inflate(getContext(), R.layout.popup_list_row_region, null);
			TextView textView = (TextView) linearLayout.findViewById(R.id.regionItem);
			textView.setText(item);

			return linearLayout;
		}

		@Override
		public int getCount()
		{
			return mRegionArrayList == null ? 0 : mRegionArrayList.size();
		}

		@Override
		public Object getItem(int position)
		{
			return mRegionArrayList.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			if (mUserActionListener != null)
			{
				mUserActionListener.onItemClick(position);
			}
		}
	};
}

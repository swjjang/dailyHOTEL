package com.twoheart.dailyhotel.adapter;

import java.util.ArrayList;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.TicketListMapFragment;
import com.twoheart.dailyhotel.model.TicketDto;
import com.twoheart.dailyhotel.util.ui.TicketViewItem;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class TicketViewPagerAdapter extends PagerAdapter
{
	protected Context mContext;
	private ArrayList<TicketViewItem> mTicketViewItemList;
	protected TicketListMapFragment.OnUserActionListener mOnUserActionListener;

	protected abstract void makeLayout(View view, final TicketDto ticketDto);

	public TicketViewPagerAdapter(Context context)
	{
		mContext = context;

		mTicketViewItemList = new ArrayList<TicketViewItem>();
	}

	public void setOnUserActionListener(TicketListMapFragment.OnUserActionListener listener)
	{
		mOnUserActionListener = listener;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		if (mTicketViewItemList == null || mTicketViewItemList.size() < position)
		{
			return null;
		}

		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = layoutInflater.inflate(R.layout.viewpager_column_hotel, null);

		TicketViewItem item = mTicketViewItemList.get(position);

		makeLayout(view, item.getTicketDto());

		container.addView(view, 0);

		return view;
	}

	@Override
	public int getItemPosition(Object object)
	{
		return POSITION_NONE;
	}

	@Override
	public int getCount()
	{
		if (mTicketViewItemList != null)
		{
			return mTicketViewItemList.size();
		} else
		{
			return 0;
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		container.removeView((View) object);
	}

	public void setData(ArrayList<TicketViewItem> list)
	{
		if (mTicketViewItemList == null)
		{
			mTicketViewItemList = new ArrayList<TicketViewItem>();
		}

		mTicketViewItemList.clear();

		if (list != null)
		{
			mTicketViewItemList.addAll(list);
		}
	}
}

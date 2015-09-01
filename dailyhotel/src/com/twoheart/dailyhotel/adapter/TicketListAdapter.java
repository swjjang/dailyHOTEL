package com.twoheart.dailyhotel.adapter;

import java.util.ArrayList;
import java.util.Collection;

import com.twoheart.dailyhotel.util.ui.HotelListViewItem;
import com.twoheart.dailyhotel.util.ui.TicketViewItem;
import com.twoheart.dailyhotel.widget.PinnedSectionListView.PinnedSectionListAdapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class TicketListAdapter
		extends ArrayAdapter<TicketViewItem>implements PinnedSectionListAdapter
{
	protected Context context;
	protected int resourceId;
	protected LayoutInflater inflater;
	private ArrayList<TicketViewItem> mTicketList;
	protected PaintDrawable mPaintDrawable;

	@Override
	public abstract View getView(final int position, View convertView, ViewGroup parent);

	public TicketListAdapter(Context context, int resourceId, ArrayList<TicketViewItem> arrayList)
	{
		super(context, resourceId, arrayList);

		if (mTicketList == null)
		{
			mTicketList = new ArrayList<TicketViewItem>();
		}

		mTicketList.clear();
		mTicketList.addAll(arrayList);

		this.context = context;
		this.resourceId = resourceId;

		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		makeShaderFactory();
	}

	private void makeShaderFactory()
	{
		// 그라디에이션 만들기.
		final int colors[] = { Color.parseColor("#ED000000"), Color.parseColor("#E8000000"), Color.parseColor("#E2000000"), Color.parseColor("#66000000"), Color.parseColor("#00000000") };
		final float positions[] = { 0.0f, 0.01f, 0.02f, 0.17f, 0.38f };

		mPaintDrawable = new PaintDrawable();
		mPaintDrawable.setShape(new RectShape());

		ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory()
		{
			@Override
			public Shader resize(int width, int height)
			{
				return new LinearGradient(0, height, 0, 0, colors, positions, Shader.TileMode.CLAMP);
			}
		};

		mPaintDrawable.setShaderFactory(sf);
	}

	@Override
	public void clear()
	{
		if (mTicketList == null)
		{
			mTicketList = new ArrayList<TicketViewItem>();
		}

		mTicketList.clear();

		super.clear();
	}

	@Override
	public TicketViewItem getItem(int position)
	{
		if (mTicketList == null)
		{
			return null;
		}

		return mTicketList.get(position);
	}

	@Override
	public int getCount()
	{
		if (mTicketList == null)
		{
			return 0;
		}

		return mTicketList.size();
	}

	@Override
	public void addAll(Collection<? extends TicketViewItem> collection)
	{
		if (collection == null)
		{
			return;
		}

		if (mTicketList == null)
		{
			mTicketList = new ArrayList<TicketViewItem>();
		}

		mTicketList.addAll(collection);
	}

	public ArrayList<TicketViewItem> getData()
	{
		return mTicketList;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType)
	{
		return viewType == HotelListViewItem.TYPE_SECTION;
	}

	@Override
	public int getViewTypeCount()
	{
		return 2;
	}

	@Override
	public int getItemViewType(int position)
	{
		return getItem(position).type;
	}
}

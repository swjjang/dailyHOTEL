package com.twoheart.dailyhotel.view;

import com.twoheart.dailyhotel.model.Hotel;

public class HotelListViewItem
{

	public static final int TYPE_SECTION = 1;
	public static final int TYPE_ENTRY = 0;

	private int type;
	private Hotel item;
	private String category;

	public HotelListViewItem(Hotel hotel)
	{
		this.type = TYPE_ENTRY;
		item = hotel;
	}

	public HotelListViewItem(String region)
	{
		this.type = TYPE_SECTION;
		category = region;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public Hotel getItem()
	{
		return item;
	}

	public void setItem(Hotel item)
	{
		this.item = item;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

}
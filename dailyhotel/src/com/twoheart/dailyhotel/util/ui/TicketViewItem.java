package com.twoheart.dailyhotel.util.ui;

import com.twoheart.dailyhotel.model.TicketDto;

public abstract class TicketViewItem
{
	public static final int TYPE_SECTION = 1;
	public static final int TYPE_ENTRY = 0;

	public int type;
	public String title;

	public TicketViewItem()
	{
		type = TYPE_ENTRY;
	}

	public TicketViewItem(String title)
	{
		type = TYPE_SECTION;
		this.title = title;
	}

	public abstract TicketDto getTicketDto();
}
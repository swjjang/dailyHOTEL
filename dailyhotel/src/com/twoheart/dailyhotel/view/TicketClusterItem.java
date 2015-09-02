package com.twoheart.dailyhotel.view;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.twoheart.dailyhotel.model.TicketDto;

public class TicketClusterItem implements ClusterItem
{
	private final TicketDto mTicketDto;
	private final LatLng mPosition;

	public TicketClusterItem(TicketDto ticketDto)
	{
		mTicketDto = ticketDto;
		mPosition = new LatLng(ticketDto.latitude, ticketDto.longitude);
	}

	@Override
	public LatLng getPosition()
	{
		return mPosition;
	}

	public TicketDto getTicketDto()
	{
		return mTicketDto;
	}
}

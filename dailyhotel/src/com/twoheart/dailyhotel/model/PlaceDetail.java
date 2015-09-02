package com.twoheart.dailyhotel.model;

import java.util.ArrayList;

import org.json.JSONObject;

public abstract class PlaceDetail
{
	public int index;
	public Place.Grade grade;
	public String name;
	public String address;
	public String benefit;
	public double latitude;
	public double longitude;
	protected ArrayList<String> mImageUrlList;
	protected ArrayList<DetailInformation> mInformationList;
	protected ArrayList<TicketInformation> mTicketInformationList;

	public abstract void setData(JSONObject jsonObject) throws Exception;

	public PlaceDetail(int index)
	{
		this.index = index;
	}

	public ArrayList<String> getImageUrlList()
	{
		return mImageUrlList;
	}

	public ArrayList<TicketInformation> getTicketInformation()
	{
		return mTicketInformationList;
	}

	public ArrayList<DetailInformation> getInformation()
	{
		return mInformationList;
	}
}

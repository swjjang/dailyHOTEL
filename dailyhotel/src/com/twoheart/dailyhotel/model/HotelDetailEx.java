package com.twoheart.dailyhotel.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class HotelDetailEx
{
	public int hotelIndex;
	public int nights;
	public String grade;
	public String hotelName;
	public String address;
	public String addressNatural;
	public double latitude;
	public double longitude;
	public boolean isOverseas; // 0 : 국내 , 1 : 해외 
	private ArrayList<String> mImageUrlList;
	public String hotelBenefit;
	private ArrayList<DetailInformation> mInformationList;
	private ArrayList<DetailInformation> mMoreInformationList;
	private ArrayList<SaleRoomInformation> mSaleRoomList;

	public HotelDetailEx(int hotelIndex, int nights)
	{
		this.hotelIndex = hotelIndex;
		this.nights = nights;
	}

	public void setData(JSONObject jsonObject) throws Exception
	{
		grade = jsonObject.getString("grade");
		hotelName = jsonObject.getString("hotel_name");
		address = jsonObject.getString("address");
		addressNatural = jsonObject.getString("address_natural");

		longitude = jsonObject.getDouble("longitude");
		latitude = jsonObject.getDouble("latitude");

		isOverseas = jsonObject.getBoolean("is_overseas");

		// Image Url
		JSONArray imageJsonArray = jsonObject.getJSONArray("img_url");
		int imageLength = imageJsonArray.length();

		mImageUrlList = new ArrayList<String>(imageLength);

		for (int i = 0; i < imageLength; i++)
		{
			mImageUrlList.add(imageJsonArray.getString(i));
		}

		hotelBenefit = jsonObject.getString("hotel_benefit");

		// Detail
		JSONArray detailJSONArray = jsonObject.getJSONArray("detail");
		int detailLength = detailJSONArray.length();

		mInformationList = new ArrayList<DetailInformation>(detailLength);

		for (int i = 0; i < detailLength; i++)
		{
			mInformationList.add(new DetailInformation(detailJSONArray.getJSONObject(i)));
		}

		// Detail Info
		JSONArray detailMoreJSONArray = jsonObject.getJSONArray("detail_more");
		int detailMoreLength = detailMoreJSONArray.length();

		if (detailMoreLength == 0)
		{
			mMoreInformationList = null;
		} else
		{
			mMoreInformationList = new ArrayList<DetailInformation>(detailMoreLength);

			for (int i = 0; i < detailMoreLength; i++)
			{
				mMoreInformationList.add(new DetailInformation(detailMoreJSONArray.getJSONObject(i)));
			}
		}

		// Room Sale Info
		JSONArray saleRoomJSONArray = jsonObject.getJSONArray("room_sale_info");
		int saleRoomLength = saleRoomJSONArray.length();

		mSaleRoomList = new ArrayList<SaleRoomInformation>(saleRoomLength);

		for (int i = 0; i < saleRoomLength; i++)
		{
			mSaleRoomList.add(new SaleRoomInformation(hotelName, saleRoomJSONArray.getJSONObject(i), isOverseas));
		}
	}

	public ArrayList<String> getImageUrlList()
	{
		return mImageUrlList;
	}

	public ArrayList<SaleRoomInformation> getSaleRoomList()
	{
		return mSaleRoomList;
	}

	public ArrayList<DetailInformation> getInformation()
	{
		return mInformationList;
	}

	public ArrayList<DetailInformation> getMoreInformation()
	{
		return mMoreInformationList;
	}
}

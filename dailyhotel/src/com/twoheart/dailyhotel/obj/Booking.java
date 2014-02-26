package com.twoheart.dailyhotel.obj;

public class Booking {
	String sday;
	String hotel_idx;
	String hotel_name;
	
	public Booking(String sday, String hotel_idx, String hotel_name) {
		this.sday = sday;
		this.hotel_idx = hotel_idx;
		this.hotel_name = hotel_name;
	}

	public String getSday() {
		return sday;
	}

	public void setSday(String sday) {
		this.sday = sday;
	}

	public String getHotel_idx() {
		return hotel_idx;
	}

	public void setHotel_idx(String hotel_idx) {
		this.hotel_idx = hotel_idx;
	}

	public String getHotel_name() {
		return hotel_name;
	}

	public void setHotel_name(String hotel_name) {
		this.hotel_name = hotel_name;
	}
	
	
	
}

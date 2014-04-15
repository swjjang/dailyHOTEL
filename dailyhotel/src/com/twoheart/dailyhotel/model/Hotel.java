package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Hotel implements Parcelable {
	
	private String image;
	private String name;
	private String price;
	private String discount;
	private String address;
	private String category;
	private int idx;
	private int availableRoom;
	private int sequence;
	private String bedType;
	private String detailRegion;

	public Hotel() {
		super();
	}
	
	public Hotel(Parcel in) {
		readFromParcel(in);
	}

	public Hotel(String image, String name, String price, String discount,
			String address, String category, int idx, int availableRoom, int sequence, String bedType) {
		this.image = image;
		this.name = name;
		this.price = price;
		this.discount = discount;
		this.address = address;
		this.category = category;
		this.idx = idx;
		this.availableRoom = availableRoom;
		this.sequence = sequence;
		this.bedType = bedType;
		
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(image);
		dest.writeString(name);
		dest.writeString(price);
		dest.writeString(discount);
		dest.writeString(address);
		dest.writeString(category);
		dest.writeInt(idx);
		dest.writeInt(availableRoom);
		dest.writeInt(sequence);
		dest.writeString(bedType);

	}
	
	private void readFromParcel(Parcel in) {
		this.image = in.readString();
		this.name = in.readString();
		this.price = in.readString();
		this.discount = in.readString();
		this.address = in.readString();
		this.category = in.readString();
		this.idx = in.readInt();
		this.availableRoom = in.readInt();
		this.sequence = in.readInt();
		this.bedType = in.readString();
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Hotel createFromParcel(Parcel in) {
			return new Hotel(in);
		}

		@Override
		public Hotel[] newArray(int size) {
			return new Hotel[size];
		}

	};

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public int getAvailableRoom() {
		return availableRoom;
	}

	public void setAvailableRoom(int availableRoom) {
		this.availableRoom = availableRoom;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getBedType() {
		return bedType;
	}

	public void setBedType(String bedType) {
		this.bedType = bedType;
	}

	public String getDetailRegion() {
		return detailRegion;
	}

	public void setDetailRegion(String detailRegion) {
		this.detailRegion = detailRegion;
	}

	

}

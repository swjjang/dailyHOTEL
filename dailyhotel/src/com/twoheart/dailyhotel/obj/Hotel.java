package com.twoheart.dailyhotel.obj;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Hotel implements Parcelable {
	
	private String img;
	private String name;
	private String price;
	private String discount;
	private String address;
	private String cat;
	private int idx;
	private int avali_cnt;
	private int seq;

	public Hotel() {
		super();
	}
	
	public Hotel(Parcel in) {
		readFromParcel(in);
	}

	public Hotel(String img, String name, String price, String discount,
			String address, String cat, int idx, int avali_cnt, int seq) {
		this.img = img;
		this.name = name;
		this.price = price;
		this.discount = discount;
		this.address = address;
		this.cat = cat;
		this.idx = idx;
		this.avali_cnt = avali_cnt;
		this.seq = seq;
		
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(img);
		dest.writeString(name);
		dest.writeString(price);
		dest.writeString(discount);
		dest.writeString(address);
		dest.writeString(cat);
		dest.writeInt(idx);
		dest.writeInt(avali_cnt);
		dest.writeInt(seq);

	}
	
	private void readFromParcel(Parcel in) {
		this.img = in.readString();
		this.name = in.readString();
		this.price = in.readString();
		this.discount = in.readString();
		this.address = in.readString();
		this.cat = in.readString();
		this.idx = in.readInt();
		this.avali_cnt = in.readInt();
		this.seq = in.readInt();
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

	public String getCat() {
		return cat;
	}

	public void setCat(String cat) {
		this.cat = cat;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
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

	public int getAvali_cnt() {
		return avali_cnt;
	}

	public void setAvali_cnt(int avali_cnt) {
		this.avali_cnt = avali_cnt;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	

}

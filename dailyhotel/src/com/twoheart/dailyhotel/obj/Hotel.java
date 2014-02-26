package com.twoheart.dailyhotel.obj;

public class Hotel {
	private String img;
	private String name;
	private String price;
	private String discount;
	private String address;
	private String cat;
	private int idx;
	private int avali_cnt;
	private int seq;
	
	
	public Hotel(String img, String name, String price, String discount, String address, int idx, int avali_cnt, int seq, String cat) {
		this.img = img;
		this.name = name;
		this.price = price;
		this.discount = discount;
		this.address = address;
		this.idx = idx;
		this.avali_cnt = avali_cnt;
		this.seq = seq;
		this.cat = cat;
	}


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
	
	
	
}

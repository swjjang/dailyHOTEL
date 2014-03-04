package com.twoheart.dailyhotel.obj;

public class DrawerMenu {
	
	public static final int DRAWER_MENU_LIST_TYPE_LOGO = 0;
	public static final int DRAWER_MENU_LIST_TYPE_SECTION = 1;
	public static final int DRAWER_MENU_LIST_TYPE_ENTRY = 2;
	
	private String title;
	private int icon;
	private int type;
	
	public DrawerMenu(int type) {
		super();
		this.type = type;
	}

	public DrawerMenu(String title, int type) {
		super();
		this.title = title;
		this.type = type;
	}

	public DrawerMenu(String title, int icon, int type) {
		super();
		this.title = title;
		this.icon = icon;
		this.type = type;
	}
	
	public int gettype() {
		return type;
	}
	public void settype(int type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	
}

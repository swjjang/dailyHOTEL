package com.twoheart.dailyhotel.obj;

public class DrawerMenu {
	
	private String title;
	private int icon;
	private int background;
	
	public DrawerMenu(String title, int icon, int background) {
		super();
		this.title = title;
		this.icon = icon;
		this.background = background;
	}
	
	public int getBackground() {
		return background;
	}
	public void setBackground(int background) {
		this.background = background;
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

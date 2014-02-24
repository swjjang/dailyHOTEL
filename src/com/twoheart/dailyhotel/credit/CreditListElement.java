package com.twoheart.dailyhotel.credit;

public class CreditListElement {
	private String content;
	private String bonus;
	private String expires;
	
	public CreditListElement(String content, String bonus, String expires) {
		this.content = content;
		this.bonus = bonus;
		this.expires = expires;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getBonus() {
		return bonus;
	}
	public void setBonus(String bonus) {
		this.bonus = bonus;
	}
	public String getExpires() {
		return expires;
	}
	public void setExpires(String expires) {
		this.expires = expires;
	}
	
	
}

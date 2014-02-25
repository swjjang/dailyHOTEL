package com.twoheart.dailyhotel.setting;

public class BoardElement {
	public String subject;
	public String content;
	public String regdate;
	
	public BoardElement(String subject, String content, String regdate) {
		this.subject = subject;
		this.content = content;
		this.regdate = regdate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRegdate() {
		return regdate;
	}

	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
	
	
	
}

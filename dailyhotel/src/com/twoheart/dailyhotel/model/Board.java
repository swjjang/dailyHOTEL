package com.twoheart.dailyhotel.model;

public class Board {
	public String subject;
	public String content;
	public String regdate;
	
	public Board(String subject, String content, String regdate) {
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

package com.twoheart.dailyhotel.util.ui;

import android.support.v7.app.ActionBar;

public class NoActionBarException extends Exception {
	
	private ActionBar actionBar;
	
	public NoActionBarException(ActionBar actionBar) {
		this.actionBar = actionBar;
	}
	
	@Override
	public String getMessage() {
		return "Not defined ActionBar in this Activity. " +
				"Did you call setActionBar() in this Activity?";
		
	}

}

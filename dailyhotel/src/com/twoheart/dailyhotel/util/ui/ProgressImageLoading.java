package com.twoheart.dailyhotel.util.ui;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ProgressImageLoading extends SimpleImageLoadingListener {
	
	private ProgressBar mProgressBar;
	
	public ProgressImageLoading(ProgressBar progressBar) {
		mProgressBar = progressBar;
		
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		super.onLoadingComplete(imageUri, view, loadedImage);
		mProgressBar.setVisibility(View.GONE);
	}

	@Override
	public void onLoadingStarted(String imageUri, View view) {
		super.onLoadingStarted(imageUri, view);
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	

}

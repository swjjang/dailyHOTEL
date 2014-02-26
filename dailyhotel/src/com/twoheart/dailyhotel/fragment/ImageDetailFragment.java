package com.twoheart.dailyhotel.fragment;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ImageLoader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageDetailFragment extends Fragment{
	public static ImageDetailFragment newInstance(String imgUrl) {
		ImageDetailFragment fragment = new ImageDetailFragment();
		fragment.imgUrl = imgUrl;
		
		return fragment;
	}
	
	private String imgUrl;
	private View view;
	private ImageLoader imageLoader;
	private ImageView iv;
	private ProgressBar pb;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_image_detail, container, false);
		iv = (ImageView) view.findViewById(R.id.iv_image_detail);
		pb = (ProgressBar) view.findViewById(R.id.pb_image_detail);
		imageLoader = new ImageLoader(view.getContext());
		imageLoader.DisplayImage(imgUrl, iv, pb);
		
		return view;
	}
}

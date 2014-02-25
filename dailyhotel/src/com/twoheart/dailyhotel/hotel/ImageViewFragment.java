package com.twoheart.dailyhotel.hotel;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.common.ImageLoader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageViewFragment extends Fragment implements OnClickListener{
	public static ImageViewFragment newInstance(String imgUrl) {
		ImageViewFragment fragment = new ImageViewFragment();
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
		
		view = inflater.inflate(R.layout.fragment_image_view, container, false);
		iv = (ImageView) view.findViewById(R.id.iv_image_view);
		pb = (ProgressBar) view.findViewById(R.id.pb_image_view);
		imageLoader = new ImageLoader(view.getContext());
		imageLoader.DisplayImage(imgUrl, iv, pb);
		
		iv.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == iv.getId()) {
			Intent i = new Intent(view.getContext(), ImageDetailActivity.class);
			startActivity(i);
		}
	}
}

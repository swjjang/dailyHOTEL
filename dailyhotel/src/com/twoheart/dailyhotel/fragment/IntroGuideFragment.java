package com.twoheart.dailyhotel.fragment;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IntroGuideFragment extends BaseFragment
{

	private static final String KEY_BUNDLE_ARGUMENTS_BACKGROUND = "background";
	private static final String KEY_BUNDLE_ARGUMENTS_TITLE = "title";
	private static final String KEY_BUNDLE_ARGUMENTS_IMAGE = "image";
	private static final String KEY_BUNDLE_ARGUMENTS_DESC = "desc";

	private ImageView ivImage;
	private TextView tvTitle;
	private TextView tvDesc;
	private LinearLayout llIntroGuide;

	private int mBackground;
	private String mTitle;
	private int mImage;
	private String mDesc;

	public static IntroGuideFragment newInstance(int backgroud, String title, int image, String desc)
	{
		IntroGuideFragment newFragment = new IntroGuideFragment();

		Bundle arguments = new Bundle();
		arguments.putInt(KEY_BUNDLE_ARGUMENTS_BACKGROUND, backgroud);
		arguments.putString(KEY_BUNDLE_ARGUMENTS_TITLE, title);
		arguments.putInt(KEY_BUNDLE_ARGUMENTS_IMAGE, image);
		arguments.putString(KEY_BUNDLE_ARGUMENTS_DESC, desc);
		newFragment.setArguments(arguments);

		return newFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mBackground = getArguments().getInt(KEY_BUNDLE_ARGUMENTS_BACKGROUND);
		mTitle = getArguments().getString(KEY_BUNDLE_ARGUMENTS_TITLE);
		mImage = getArguments().getInt(KEY_BUNDLE_ARGUMENTS_IMAGE);
		mDesc = getArguments().getString(KEY_BUNDLE_ARGUMENTS_DESC);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_intro_guide, container, false);

		llIntroGuide = (LinearLayout) view.findViewById(R.id.ll_intro_guide);
		tvTitle = (TextView) view.findViewById(R.id.tv_guide_title);
		tvDesc = (TextView) view.findViewById(R.id.tv_guide_desc);
		ivImage = (ImageView) view.findViewById(R.id.iv_guide_icon);

		llIntroGuide.setBackgroundResource(mBackground);

		tvTitle.setText(mTitle);
		tvDesc.setText(mDesc);
		ivImage.setImageResource(mImage);

		return view;
	}

}
/**
 * 
 */
package com.twoheart.dailyhotel.widget;

import java.util.ArrayList;

import com.twoheart.dailyhotel.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TabIndicator extends LinearLayout implements OnClickListener
{
	private SparseArray<TabIndicatorItem> mTabArrray;
	private boolean mTabEnable;

	private OnTabSelectedListener mOnTabSelectedListener;

	public interface OnTabSelectedListener
	{
		public void onTabSelected(int position);
	}

	/**
	 * @param context
	 */
	public TabIndicator(Context context)
	{
		super(context);
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public TabIndicator(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public TabIndicator(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	private void init()
	{
		mTabArrray = new SparseArray<TabIndicatorItem>();

		setTabEnable(true);
	}

	public void setData(ArrayList<String> dataList, boolean isSelectTab)
	{
		for (int i = 0; i < dataList.size(); i++)
		{
			TabIndicatorItem tab = new TabIndicatorItem(getContext());
			tab.setId(i);
			tab.setMainText(dataList.get(i));
			tab.setOnClickListener(this);
			mTabArrray.put(i, tab);
			LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
			lp.weight = 1;
			this.addView(tab, lp);
		}
		mTabArrray.get(0).setSelected(true);
	}
	
	public void setData(ArrayList<String> dataList, ArrayList<String> subList, boolean isSelectTab)
	{
		for (int i = 0; i < dataList.size(); i++)
		{
			TabIndicatorItem tab = new TabIndicatorItem(getContext());
			tab.setId(i);
			tab.setMainText(dataList.get(i));
			tab.setOnClickListener(this);
			mTabArrray.put(i, tab);
			LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
			lp.weight = 1;
			this.addView(tab, lp);
		}
		mTabArrray.get(0).setSelected(true);
	}

	public void setSelectedItemUnderLineColor(int color)
	{
		for (int i = 0; i < mTabArrray.size(); i++)
		{
			mTabArrray.get(i).setSelectedUnderLine(color);
		}
	}

	public void setTextTypeface(int style)
	{
		for (int i = 0; i < mTabArrray.size(); i++)
		{
			mTabArrray.get(i).setMainTypeface(style);
		}
	}

	public void setTextColor(int color)
	{
		for (int i = 0; i < mTabArrray.size(); i++)
		{
			mTabArrray.get(i).setMainTextColor(color);
		}
	}

	public void setSubTextColor(int color)
	{
		for (int i = 0; i < mTabArrray.size(); i++)
		{
			mTabArrray.get(i).setSubTextColor(color);
		}
	}

	public void setTextColor(ColorStateList color)
	{
		for (int i = 0; i < mTabArrray.size(); i++)
		{
			mTabArrray.get(i).setMainTextColor(color);
		}
	}

	public void setSubTextColor(ColorStateList color)
	{
		for (int i = 0; i < mTabArrray.size(); i++)
		{
			mTabArrray.get(i).setSubTextColor(color);
		}
	}

	public void setSubTextEnable(int index, boolean enable)
	{
		if (index > -1 && index < mTabArrray.size())
		{
			mTabArrray.get(index).setSubTextEnable(enable);
		}
	}

	public void setSubText(int index, String text)
	{
		if (index > -1 && index < mTabArrray.size())
		{
			mTabArrray.get(index).setSubText(text);
		}
	}

	public void setCurrentItem(int position)
	{
		for (int i = 0; i < mTabArrray.size(); i++)
		{
			if (position == i)
			{
				mTabArrray.get(i).setSelected(true);
			} else
			{
				mTabArrray.get(i).setSelected(false);
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		for (int i = 0; i < mTabArrray.size(); i++)
		{
			if (v.getId() == mTabArrray.get(i).getId())
			{
				mTabArrray.get(i).setSelected(true);
			} else
			{
				mTabArrray.get(i).setSelected(false);
			}

		}
		if (null != mOnTabSelectedListener)
		{
			mOnTabSelectedListener.onTabSelected(v.getId());
		}
	}

	public void setOnTabSelectListener(OnTabSelectedListener l)
	{
		mOnTabSelectedListener = l;
	}

	public boolean isTabEnable()
	{
		return mTabEnable;
	}

	public void setTabEnable(boolean enable)
	{
		mTabEnable = enable;

		int size = mTabArrray.size();

		if (true == mTabEnable)
		{
			for (int i = 0; i < size; i++)
			{
				mTabArrray.get(i).setEnabled(true);
			}
		} else
		{
			for (int i = 0; i < size; i++)
			{
				TabIndicatorItem tabIndicatorItem = mTabArrray.get(i);
				if (false == tabIndicatorItem.isSelected())
				{
					tabIndicatorItem.setEnabled(false);
				}
			}
		}
	}

	private class TabIndicatorItem extends RelativeLayout
	{
		private TextView mTitleTextView;
		private TextView mDayTextView;
		private View mSelectedUnderLineView;

		public TabIndicatorItem(Context context)
		{
			super(context);
			init();
		}

		public TabIndicatorItem(Context context, AttributeSet attrs)
		{
			super(context, attrs);
			init();
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		public TabIndicatorItem(Context context, AttributeSet attrs, int defStyle)
		{
			super(context, attrs, defStyle);
			init();
		}

		private void init()
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View viewv = inflater.inflate(R.layout.tab_indicator_item, this, true);

			mTitleTextView = (TextView) viewv.findViewById(R.id.tab_indicator_main_text);
			mDayTextView = (TextView) viewv.findViewById(R.id.tab_indicator_sub_text);
			mSelectedUnderLineView = viewv.findViewById(R.id.tab_indicator_under_line);
		}

		public void setMainTextColor(int color)
		{
			mTitleTextView.setTextColor(color);
		}

		public void setMainTypeface(int style)
		{
			mTitleTextView.setTypeface(mTitleTextView.getTypeface(), style);
		}

		public void setSubTextColor(int color)
		{
			mDayTextView.setTextColor(color);
		}

		public void setMainTextColor(ColorStateList color)
		{
			mTitleTextView.setTextColor(color);
		}

		public void setSubTextColor(ColorStateList color)
		{
			mDayTextView.setTextColor(color);
		}

		public void setSubTextEnable(boolean enable)
		{
			if (enable)
			{
				mDayTextView.setVisibility(View.VISIBLE);
			} else
			{
				mDayTextView.setVisibility(View.GONE);
			}
		}

		public void setMainText(String text)
		{
			mTitleTextView.setText(text);
		}

		public void setSubText(String text)
		{
			mDayTextView.setText(text);
		}

		public void setSelectedUnderLine(int color)
		{
			mSelectedUnderLineView.setBackgroundColor(color);
		}

		@Override
		public void setSelected(boolean selected)
		{
			mTitleTextView.setSelected(selected);
			mDayTextView.setSelected(selected);
			
			if (selected)
			{
				mSelectedUnderLineView.setVisibility(View.VISIBLE);
			} else
			{
				mSelectedUnderLineView.setVisibility(View.GONE);
			}
		}
	}
}

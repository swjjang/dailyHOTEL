/**
 * 
 */
package com.twoheart.dailyhotel.view.widget;

import java.util.ArrayList;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TabIndicator
		extends HorizontalScrollView implements OnClickListener
{
	private SparseArray<TabIndicatorItem> mTabArrray;
	private boolean mTabEnable;
	private boolean mHasSubText;
	private LinearLayout mLinearLayout;
	private ViewPager mViewPager;

	private int mCurrentPosition = 0;
	private float mCurrentPositionOffset = 0f;
	private int mLastScrollX = 0;
	private int mScrollOffset = 52;
	private int mIndicatorHeight = 8;
	private Paint mRectPaint;
	private boolean mIsLock;

	private OnPageChangeListener mRequestOnPageChangeListener;
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
		init(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public TabIndicator(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
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
		init(context);
	}

	private void init(Context context)
	{
		setFillViewport(true);
		setWillNotDraw(false);

		mLinearLayout = new LinearLayout(context);
		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		mLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(mLinearLayout);

		mTabArrray = new SparseArray<TabIndicatorItem>();

		mRectPaint = new Paint();
		mRectPaint.setAntiAlias(true);
		mRectPaint.setStyle(Style.FILL);
		mRectPaint.setColor(getResources().getColor(R.color.dh_theme_color));

		mIndicatorHeight = Util.dpToPx(context, 4);

		setTabEnable(true);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if (mLinearLayout == null)
		{
			return;
		}

		final int height = getHeight();

		View currentView = mLinearLayout.getChildAt(mCurrentPosition);

		if (currentView != null)
		{
			float lineLeft = currentView.getLeft();
			float lineRight = currentView.getRight();

			if (mCurrentPositionOffset > 0f && mCurrentPosition < size() - 1)
			{
				View nextView = mLinearLayout.getChildAt(mCurrentPosition + 1);
				final float nextTabLeft = nextView.getLeft();
				final float nextTabRight = nextView.getRight();

				lineLeft = (mCurrentPositionOffset * nextTabLeft + (1f - mCurrentPositionOffset) * lineLeft);
				lineRight = (mCurrentPositionOffset * nextTabRight + (1f - mCurrentPositionOffset) * lineRight);
			}

			canvas.drawRect(lineLeft, height - mIndicatorHeight, lineRight, height, mRectPaint);
		}
	}

	public void setViewPager(ViewPager viewPager)
	{
		mViewPager = viewPager;

		if (viewPager.getAdapter() == null)
		{
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		}

		viewPager.setOnPageChangeListener(mOnPageChangeListener);
	}

	public void setOnPageChangeListener(OnPageChangeListener l)
	{
		mRequestOnPageChangeListener = l;
	}

	public int size()
	{
		if (mTabArrray == null)
		{
			return 0;
		}

		return mTabArrray.size();
	}

	public void setData(ArrayList<String> dataList, boolean hasSubText)
	{
		if (dataList == null)
		{
			return;
		}

		mHasSubText = hasSubText;

		int size = dataList.size();
		TabIndicatorItem tabIndicatorItem;
		LinearLayout.LayoutParams layoutParams;

		for (int i = 0; i < size; i++)
		{
			tabIndicatorItem = new TabIndicatorItem(getContext());
			tabIndicatorItem.setId(i);

			tabIndicatorItem.setSubTextEnable(false);
			tabIndicatorItem.setMainText(dataList.get(i));
			tabIndicatorItem.setOnClickListener(this);

			mTabArrray.put(i, tabIndicatorItem);

			layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
			layoutParams.weight = 1;

			mLinearLayout.addView(tabIndicatorItem, layoutParams);
		}

		mTabArrray.get(0).setSelected(true);
	}

	public void setData(ArrayList<String> dataList, ArrayList<String> subList)
	{
		if (dataList == null || subList == null || dataList.size() != subList.size())
		{
			return;
		}

		int size = dataList.size();
		TabIndicatorItem tabIndicatorItem;
		LinearLayout.LayoutParams layoutParams;

		for (int i = 0; i < size; i++)
		{
			tabIndicatorItem = new TabIndicatorItem(getContext());
			tabIndicatorItem.setId(i);
			tabIndicatorItem.setMainText(dataList.get(i));

			String subText = subList.get(i);

			if (TextUtils.isEmpty(subText) == true)
			{
				tabIndicatorItem.setSubTextEnable(false);
			} else
			{
				tabIndicatorItem.setSubTextEnable(true);
				tabIndicatorItem.setSubText(subText);
			}

			tabIndicatorItem.setOnClickListener(this);
			mTabArrray.put(i, tabIndicatorItem);

			layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
			layoutParams.weight = 1;

			mLinearLayout.addView(tabIndicatorItem, layoutParams);
		}

		mTabArrray.get(0).setSelected(true);
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

	public String getMainText(int index)
	{
		if (index > -1 && index < mTabArrray.size())
		{
			return mTabArrray.get(index).getMainText();
		}

		return null;
	}

	public String getSubText(int index)
	{
		if (index > -1 && index < mTabArrray.size())
		{
			return mTabArrray.get(index).getSubText();
		}

		return null;
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
		if (mIsLock == true)
		{
			return;
		}

		mIsLock = true;

		for (int i = 0; i < mTabArrray.size(); i++)
		{
			if (v.getId() == mTabArrray.get(i).getId())
			{
				mTabArrray.get(i).setSelected(true);

				scrollToChild(i, 0);
			} else
			{
				mTabArrray.get(i).setSelected(false);
			}
		}
		if (null != mOnTabSelectedListener)
		{
			mOnTabSelectedListener.onTabSelected(v.getId());
		}

		mIsLock = false;
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

	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener()
	{

		@Override
		public void onPageSelected(int arg0)
		{
			if (null != mRequestOnPageChangeListener)
			{
				mRequestOnPageChangeListener.onPageSelected(arg0);
			}

			scrollToChild(arg0, 0);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
		{
			mCurrentPosition = position;
			mCurrentPositionOffset = positionOffset;

			scrollToChild(position, (int) (positionOffset * mLinearLayout.getChildAt(position).getWidth()));

			invalidate();

			if (null != mRequestOnPageChangeListener)
			{
				mRequestOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state)
		{
			if (state == ViewPager.SCROLL_STATE_IDLE)
			{
				scrollToChild(mViewPager.getCurrentItem(), 0);
			}

			if (null != mRequestOnPageChangeListener)
			{
				mRequestOnPageChangeListener.onPageScrollStateChanged(state);
			}
		}
	};

	private void scrollToChild(int position, int offset)
	{
		if (size() == 0)
		{
			return;
		}

		int newScrollX = mLinearLayout.getChildAt(position).getLeft() + offset;

		if (position > 0 || offset > 0)
		{
			newScrollX -= mScrollOffset;
		}

		if (newScrollX != mLastScrollX)
		{
			mLastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}

	}

	private class TabIndicatorItem extends RelativeLayout
	{
		private TextView mTitleTextView;
		private TextView mDayTextView;

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
			setGravity(Gravity.CENTER);

			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.tab_row_hotel_main, this, true);

			mTitleTextView = (TextView) view.findViewById(R.id.tab_indicator_main_text);
			mDayTextView = (TextView) view.findViewById(R.id.tab_indicator_sub_text);
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
				if (mHasSubText == true)
				{
					mDayTextView.setVisibility(View.INVISIBLE);
				} else
				{
					mDayTextView.setVisibility(View.GONE);
				}
			}
		}

		public void setMainText(String text)
		{
			mTitleTextView.setText(text);
		}

		public String getMainText()
		{
			return mTitleTextView.getText().toString();
		}

		public void setSubText(String text)
		{
			mDayTextView.setText(text);
		}

		public String getSubText()
		{
			return mDayTextView.getText().toString();
		}

		@Override
		public void setSelected(boolean selected)
		{
			super.setSelected(selected);

			if (selected == true)
			{
				mTitleTextView.setTypeface(mTitleTextView.getTypeface(), Typeface.BOLD);

				if (mHasSubText == true)
				{
					mDayTextView.setTypeface(mDayTextView.getTypeface(), Typeface.BOLD);
				}
			} else
			{
				mTitleTextView.setTypeface(mTitleTextView.getTypeface(), Typeface.NORMAL);

				if (mHasSubText == true)
				{
					mDayTextView.setTypeface(mDayTextView.getTypeface(), Typeface.NORMAL);
				}
			}

			mTitleTextView.setSelected(selected);
			mDayTextView.setSelected(selected);
		}
	}
}

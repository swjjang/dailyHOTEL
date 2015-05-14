/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * CreditFragment (적립금 화면)
 * 
 * 로그인 여부에 따라 적립금을 안내하는 화면이다. 적립금을 표시하며 카카오톡
 * 친구 초대 버튼이 있다. 세부 내역을 따로 표시해주는 버튼을 가지고 있어 
 * 해당 화면을 띄워주기도 한다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.ui;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.support.v7.internal.widget.TintRadioButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.CreditCardListActivity;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.Util;

/**
 * 신용카드 등록하기.
 * 
 * @author sheldon
 *
 */
public class CreditCardLayout extends FrameLayout
{
	private ViewGroup mLogingLayout, mIntroductionLayout;
	private TextView mAddCreditCardButton;
	private ListView mListView;
	private CreditCardListAdapter mAdapter;

	private CreditCardListActivity.OnUserActionListener mOnUserActionListener;

	public CreditCardLayout(Context context)
	{
		super(context);

		initLayout(context);
	}

	public CreditCardLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		initLayout(context);
	}

	public CreditCardLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);

		initLayout(context);
	}

	public CreditCardLayout(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);

		initLayout(context);
	}

	private void initLayout(Context context)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.layout_creditcard, this, true);

		mLogingLayout = (ViewGroup) view.findViewById(R.id.loginLayout);
		mIntroductionLayout = (ViewGroup) mLogingLayout.findViewById(R.id.introductionLayout);

		mAddCreditCardButton = (TextView) view.findViewById(R.id.addCreditCardButton);

		mListView = (ListView) view.findViewById(R.id.creditcardListView);
		mListView.setDivider(null);
		mListView.setDividerHeight(Util.dpToPx(context, 20));

		mAddCreditCardButton.setOnClickListener(mAddCreditCardClickListener);
	}

	public void setUserActionListener(CreditCardListActivity.OnUserActionListener listener)
	{
		mOnUserActionListener = listener;

		mListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				CreditCard caeditCard = mAdapter.getItem(position);

				if (mOnUserActionListener != null)
				{
					mOnUserActionListener.onItemClick(caeditCard);
				}
			}
		});
	}

	public void setCreditCardList(ArrayList<CreditCard> arrayList)
	{
		setCreditCardList(arrayList, false, null);
	}

	public void setCreditCardList(ArrayList<CreditCard> arrayList, boolean isPickMode, CreditCard selectedCreditCard)
	{
		if (mAdapter == null)
		{
			mAdapter = new CreditCardListAdapter(getContext(), R.layout.list_row_creditcard, null);
			mListView.setAdapter(mAdapter);
		}

		if (arrayList == null || arrayList.size() == 0)
		{
			mIntroductionLayout.setVisibility(View.VISIBLE);
		} else
		{
			mIntroductionLayout.setVisibility(View.GONE);
		}

		mAdapter.clear();
		mAdapter.addAll(arrayList);

		if (selectedCreditCard != null)
		{
			mAdapter.setPickMode(isPickMode, selectedCreditCard);
		} else
		{
			// 만일 선택된 카드가 없을 경우 첫번째 배열로 한다.
			if (arrayList.size() > 0)
			{
				mAdapter.setPickMode(isPickMode, arrayList.get(0));
			}
		}

		mAdapter.notifyDataSetChanged();
	}

	public void setViewLoginLayout(boolean isLogin)
	{
		if (isLogin)
		{
			mLogingLayout.setVisibility(View.VISIBLE);
			RenewalGaManager.getInstance(getContext()).recordScreen("creditWithLogon", "/credit-with-logon/");
		} else
		{
			mLogingLayout.setVisibility(View.GONE);
			RenewalGaManager.getInstance(getContext()).recordScreen("creditWithLogoff", "/credit-with-logoff/");
		}
	}

	private class CreditCardListAdapter extends ArrayAdapter<CreditCard>
	{
		private ArrayList<CreditCard> mArrayList;

		private boolean mIsPickMode;
		private CreditCard mSelectedCreditCard;

		public CreditCardListAdapter(Context context, int textViewResourceId, ArrayList<CreditCard> arrayList)
		{
			super(context, textViewResourceId, arrayList);

			mArrayList = new ArrayList<CreditCard>();

			addAll(arrayList);
		}

		@Override
		public int getCount()
		{
			if (mArrayList == null)
			{
				return 0;
			} else
			{
				return mArrayList.size();
			}
		}

		@Override
		public CreditCard getItem(int position)
		{
			if (mArrayList == null)
			{
				return null;

			} else
			{
				return mArrayList.get(position);
			}
		}

		@Override
		public void addAll(Collection<? extends CreditCard> collection)
		{
			if (mArrayList == null || collection == null)
			{
				return;
			}

			mArrayList.addAll(collection);
		}

		@Override
		public void clear()
		{
			if (mArrayList == null)
			{
				return;
			}

			mArrayList.clear();
		}

		public void setPickMode(boolean isPickMode, CreditCard selectedCreditCard)
		{
			mIsPickMode = isPickMode;
			mSelectedCreditCard = selectedCreditCard;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = convertView;

			if (view == null)
			{
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.list_row_creditcard, null);
			}

			final CreditCard card = getItem(position);

			if (mIsPickMode == true)
			{
				TintRadioButton radioButton = (TintRadioButton) view.findViewById(R.id.selectRadioButton);
				radioButton.setVisibility(View.VISIBLE);

				radioButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if (mOnUserActionListener != null)
						{
							mOnUserActionListener.onItemClick(card);
						}
					}
				});

				if (mSelectedCreditCard != null && mSelectedCreditCard.billingkey.equals(card.billingkey) == true)
				{
					radioButton.setChecked(true);
				} else
				{
					radioButton.setChecked(false);
				}
			}

			TextView nameTextView = (TextView) view.findViewById(R.id.nameTextView);
			TextView numberTextView = (TextView) view.findViewById(R.id.numberTextView);

			nameTextView.setText(card.name);
			numberTextView.setText(card.number);

			View deleteButton = view.findViewById(R.id.deleteImageView);

			deleteButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mOnUserActionListener != null)
					{
						mOnUserActionListener.deleteCreditCard(card);
					}
				}
			});

			return view;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// UI Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private View.OnClickListener mAddCreditCardClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if (mOnUserActionListener != null)
			{
				mOnUserActionListener.addCreditCard();
			}
		}
	};
}
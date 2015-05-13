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
	private ViewGroup mNotLoginLayout, mLogingLayout, mIntroductionLayout;
	private TextView mLoginButton, mSignupButton;
	private TextView mAddCreditCardButton;
	private ListView mListView;
	private CreditCardListAdapter mAdapter;

	private CreditCardListActivity.UserActionListener mUserActionListener;

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

		mNotLoginLayout = (ViewGroup) view.findViewById(R.id.notLoginLayout);
		mLogingLayout = (ViewGroup) view.findViewById(R.id.loginLayout);
		mIntroductionLayout = (ViewGroup) mLogingLayout.findViewById(R.id.introductionLayout);

		mLoginButton = (TextView) view.findViewById(R.id.loginButton);
		mSignupButton = (TextView) view.findViewById(R.id.singupButton);
		mAddCreditCardButton = (TextView) view.findViewById(R.id.addCreditCardButton);

		mListView = (ListView) view.findViewById(R.id.creditcardListView);
		mListView.setDivider(null);
		mListView.setDividerHeight(Util.dpToPx(context, 20));

		mLoginButton.setOnClickListener(mLoginClickListener);
		mSignupButton.setOnClickListener(mSignupClickListener);
		mAddCreditCardButton.setOnClickListener(mAddCreditCardClickListener);
	}

	public void setUserActionListener(CreditCardListActivity.UserActionListener listener)
	{
		mUserActionListener = listener;

		mListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				CreditCard caeditCard = mAdapter.getItem(position);

				if (mUserActionListener != null)
				{
					mUserActionListener.onItemClick(caeditCard);
				}
			}
		});
	}

	public void setCreditCardList(ArrayList<CreditCard> arrayList)
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
		mAdapter.notifyDataSetChanged();
	}

	public void setViewLoginLayout(boolean isLogin)
	{
		if (isLogin)
		{
			mNotLoginLayout.setVisibility(View.GONE);
			mLogingLayout.setVisibility(View.VISIBLE);
			RenewalGaManager.getInstance(getContext()).recordScreen("creditWithLogon", "/credit-with-logon/");
		} else
		{
			mNotLoginLayout.setVisibility(View.VISIBLE);
			mLogingLayout.setVisibility(View.GONE);
			RenewalGaManager.getInstance(getContext()).recordScreen("creditWithLogoff", "/credit-with-logoff/");
		}
	}

	private class CreditCardListAdapter extends ArrayAdapter<CreditCard>
	{
		private ArrayList<CreditCard> mArrayList;

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
					if (mUserActionListener != null)
					{
						mUserActionListener.deleteCreditCard(card);
					}
				}
			});

			return view;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// UI Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private View.OnClickListener mLoginClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if (mUserActionListener != null)
			{
				mUserActionListener.onLogin();
			}
		}
	};

	private View.OnClickListener mSignupClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if (mUserActionListener != null)
			{
				mUserActionListener.onSingup();
			}
		}
	};

	private View.OnClickListener mAddCreditCardClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if (mUserActionListener != null)
			{
				mUserActionListener.addCreditCard();
			}
		}
	};
}
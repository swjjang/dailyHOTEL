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
package com.twoheart.dailyhotel.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.CreditCardListActivity;
import com.twoheart.dailyhotel.model.CreditCard;

import java.util.ArrayList;
import java.util.Collection;

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
		} else
		{
			mLogingLayout.setVisibility(View.GONE);
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
				View.OnClickListener onClickListener = new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						mSelectedCreditCard = card;

						notifyDataSetChanged();

						if (mOnUserActionListener != null)
						{
							mOnUserActionListener.onItemClick(card);
						}
					}
				};

				ImageView checkImageView = (ImageView) view.findViewById(R.id.checkedImageView);

				View cardLayout = view.findViewById(R.id.cardLayout);
				cardLayout.setOnClickListener(onClickListener);

				if (mSelectedCreditCard != null && mSelectedCreditCard.billingkey.equals(card.billingkey) == true)
				{
					checkImageView.setVisibility(View.VISIBLE);

					cardLayout.setBackgroundResource(R.drawable.card_bg_select);
					checkImageView.setImageResource(R.drawable.card_btn_v_select);
				} else
				{
					checkImageView.setVisibility(View.INVISIBLE);
					cardLayout.setBackgroundResource(R.drawable.card_bg);
				}
			}

			ImageView logoImageView = (ImageView) view.findViewById(R.id.logoImageView);

			int resLogo = getLogo(card.cardcd);

			if (resLogo == 0)
			{
				logoImageView.setVisibility(View.INVISIBLE);
			} else
			{
				logoImageView.setVisibility(View.VISIBLE);
				logoImageView.setImageResource(resLogo);
			}

			TextView numberTextView = (TextView) view.findViewById(R.id.numberTextView);
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

		private int getLogo(String cardcd)
		{
			//			'01','외환'
			//			'03','롯데'
			//			'04','현대'
			//			'06','국민'
			//			'11','BC'
			//			'12','삼성'
			//			'14','신한'
			//			'15','한미'
			//			'16','NH'
			//			'17','하나 SK'
			//			'21','해외비자'
			//			'22','해외마스터'
			//			'23','JCB'
			//			'24','해외아멕스'
			//			'25','해외다이너스'

			if ("01".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_01;
			} else if ("03".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_02;
			} else if ("04".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_03;
			} else if ("06".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_04;
			} else if ("11".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_05;
			} else if ("12".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_06;
			} else if ("14".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_07;
			} else if ("15".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_08;
			} else if ("16".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_09;
			} else if ("17".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_10;
			} else if ("21".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_11;
			} else if ("22".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_12;
			} else if ("23".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_13;
			} else if ("24".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_14;
			} else if ("25".equalsIgnoreCase(cardcd) == true)
			{
				return R.drawable.card_logo_15;
			} else
			{
				return 0;
			}
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
package com.twoheart.dailyhotel.screen.mydaily.creditcard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 신용카드 등록하기.
 *
 * @author sheldon
 */
public class CreditCardLayout extends FrameLayout
{
    private ViewGroup mLoginLayout, mEmptyCardLayout;
    private View mBottomLayout;
    private ListView mListView;
    CreditCardListAdapter mAdapter;

    CreditCardListActivity.OnUserActionListener mOnUserActionListener;
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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

        mLoginLayout = view.findViewById(R.id.loginLayout);
        mEmptyCardLayout = mLoginLayout.findViewById(R.id.emptyCardLayout);
        mEmptyCardLayout.setOnClickListener(mAddCreditCardClickListener);

        mBottomLayout = view.findViewById(R.id.bottomLayout);
        View addCreditCardButton = view.findViewById(R.id.addCreditCardButton);

        mListView = view.findViewById(R.id.creditcardListView);
        EdgeEffectColor.setEdgeGlowColor(mListView, context.getResources().getColor(R.color.default_over_scroll_edge));

        addCreditCardButton.setOnClickListener(mAddCreditCardClickListener);

        View emptyCardLayout = view.findViewById(R.id.emptyCardLayout);
        emptyCardLayout.setOnClickListener(mAddCreditCardClickListener);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) emptyCardLayout.getLayoutParams();

        if (layoutParams == null)
        {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(ScreenUtils.dpToPx(context, 30), ScreenUtils.dpToPx(context, 24), ScreenUtils.dpToPx(context, 30), 0);
        }

        layoutParams.height = (ScreenUtils.getScreenWidth(context) - ScreenUtils.dpToPx(context, 60)) * 9 / 16;
        emptyCardLayout.setLayoutParams(layoutParams);
    }

    public void setUserActionListener(CreditCardListActivity.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;

        mListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                CreditCard creditCard = mAdapter.getItem(position);

                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.onItemClick(creditCard);
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
        } else
        {
            mAdapter.clear();
        }

        if (arrayList == null || arrayList.size() == 0)
        {
            mEmptyCardLayout.setVisibility(View.VISIBLE);
            mBottomLayout.setVisibility(View.INVISIBLE);
        } else
        {
            mEmptyCardLayout.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.VISIBLE);
        }

        if (arrayList != null)
        {
            mAdapter.addAll(arrayList);
        }

        if (selectedCreditCard != null)
        {
            mAdapter.setPickMode(isPickMode, selectedCreditCard);
        } else
        {
            // 만일 선택된 카드가 없을 경우 첫번째 배열로 한다.
            if (arrayList != null && arrayList.size() > 0)
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
            mLoginLayout.setVisibility(View.VISIBLE);
        } else
        {
            mLoginLayout.setVisibility(View.GONE);
        }
    }

    public int getCreditCardCount()
    {
        if (mListView == null)
        {
            return 0;
        }

        return mListView.getCount();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UI Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class CreditCardListAdapter extends ArrayAdapter<CreditCard>
    {
        private ArrayList<CreditCard> mArrayList;

        private Context mContext;
        private boolean mIsPickMode;
        CreditCard mSelectedCreditCard;

        public CreditCardListAdapter(Context context, int textViewResourceId, ArrayList<CreditCard> arrayList)
        {
            super(context, textViewResourceId, arrayList);

            mContext = context;
            mArrayList = new ArrayList<>();

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

            View cardLayout = view.findViewById(R.id.cardLayout);

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) cardLayout.getLayoutParams();

            if (layoutParams == null)
            {
                layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(ScreenUtils.dpToPx(mContext, 30), ScreenUtils.dpToPx(mContext, 24), ScreenUtils.dpToPx(mContext, 30), 0);
            }

            layoutParams.height = (ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dpToPx(mContext, 60)) * 9 / 16;
            cardLayout.setLayoutParams(layoutParams);

            final CreditCard card = getItem(position);

            DailyTextView logoTextView = view.findViewById(R.id.logoTextView);

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

                cardLayout.setOnClickListener(onClickListener);

                if (mSelectedCreditCard != null && mSelectedCreditCard.billingkey.equals(card.billingkey) == true)
                {
                    logoTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.card_btn_v_select, 0);
                    cardLayout.setBackgroundResource(R.drawable.shape_fillrect_leb2135_bffffff_r10);
                } else
                {
                    logoTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    cardLayout.setBackgroundResource(R.drawable.shape_fillrect_ld1d1d1_bffffff_r10);
                }
            }

            logoTextView.setText(card.name);

            TextView numberTextView = view.findViewById(R.id.numberTextView);
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
}
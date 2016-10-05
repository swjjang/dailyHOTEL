package com.twoheart.dailyhotel.screen.information.creditcard;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 신용카드 등록하기.
 *
 * @author sheldon
 */
public class CreditCardLayout extends FrameLayout
{
    private ViewGroup mLogingLayout, mIntroductionLayout;
    private View mBottomLayout;
    private ListView mListView;
    private CreditCardListAdapter mAdapter;

    private CreditCardListActivity.OnUserActionListener mOnUserActionListener;
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

        mLogingLayout = (ViewGroup) view.findViewById(R.id.loginLayout);
        mIntroductionLayout = (ViewGroup) mLogingLayout.findViewById(R.id.introductionLayout);
        mIntroductionLayout.setOnClickListener(mAddCreditCardClickListener);

        mBottomLayout = view.findViewById(R.id.bottomLayout);
        View addCreditCardButton = view.findViewById(R.id.addCreditCardButton);

        mListView = (ListView) view.findViewById(R.id.creditcardListView);
        EdgeEffectColor.setEdgeGlowColor(mListView, context.getResources().getColor(R.color.default_over_scroll_edge));

        addCreditCardButton.setOnClickListener(mAddCreditCardClickListener);

        View emptyCardLayout = view.findViewById(R.id.emptyCardLayout);
        emptyCardLayout.setOnClickListener(mAddCreditCardClickListener);
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
            mBottomLayout.setVisibility(View.INVISIBLE);
        } else
        {
            mIntroductionLayout.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.VISIBLE);
        }

        mAdapter.clear();
        mAdapter.addAll(arrayList);

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
            mLogingLayout.setVisibility(View.VISIBLE);
        } else
        {
            mLogingLayout.setVisibility(View.GONE);
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

        private boolean mIsPickMode;
        private CreditCard mSelectedCreditCard;

        public CreditCardListAdapter(Context context, int textViewResourceId, ArrayList<CreditCard> arrayList)
        {
            super(context, textViewResourceId, arrayList);

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

                    cardLayout.setBackgroundResource(R.drawable.shape_fillrect_l900034_bffffff);
                    checkImageView.setImageResource(R.drawable.card_btn_v_select);
                } else
                {
                    checkImageView.setVisibility(View.INVISIBLE);
                    cardLayout.setBackgroundResource(R.drawable.shape_fillrect_le6e6e7_bffffff);
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


            if (Util.isTextEmpty(cardcd) == true)
            {
                return 0;
            }

            switch (cardcd)
            {
                case "01":
                    return R.drawable.card_logo_01;
                case "03":
                    return R.drawable.card_logo_02;
                case "04":
                    return R.drawable.card_logo_03;
                case "06":
                    return R.drawable.card_logo_04;
                case "11":
                    return R.drawable.card_logo_05;
                case "12":
                    return R.drawable.card_logo_06;
                case "14":
                    return R.drawable.card_logo_07;
                case "15":
                    return R.drawable.card_logo_08;
                case "16":
                    return R.drawable.card_logo_09;
                case "17":
                    return R.drawable.card_logo_10;
                case "21":
                    return R.drawable.card_logo_11;
                case "22":
                    return R.drawable.card_logo_12;
                case "23":
                    return R.drawable.card_logo_13;
                case "24":
                    return R.drawable.card_logo_14;
                case "25":
                    return R.drawable.card_logo_15;
                case "26":
                case "32":
                case "33":
                case "34":
                case "35":
                case "41":
                case "43":
                case "44":
                case "48":
                case "51":
                case "52":
                case "54":
                case "71":
                case "95":
                default:
                    return 0;
            }
        }
    }
}
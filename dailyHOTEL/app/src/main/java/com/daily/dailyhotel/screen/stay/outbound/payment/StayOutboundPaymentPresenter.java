package com.daily.dailyhotel.screen.stay.outbound.payment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.repository.remote.PaymentRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyUserPreference;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundPaymentPresenter extends BaseExceptionPresenter<StayOutboundPaymentActivity, StayOutboundPaymentInterface> implements StayOutboundPaymentView.OnEventListener
{
    private StayOutboundPaymentAnalyticsInterface mAnalytics;

    private PaymentRemoteImpl mPaymentRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;

    private StayBookDateTime mStayBookDateTime;
    private int mStayIndex;
    private People mPeople;
    private String mStayName, mRoomType;
    private String mRateCode, mRateKey, mRoomTypeCode;
    private StayOutboundPayment mStayOutboundPayment;
    private Card mSelectedCard;
    private User mUser;


    public interface StayOutboundPaymentAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundPaymentPresenter(@NonNull StayOutboundPaymentActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundPaymentInterface createInstanceViewInterface()
    {
        return new StayOutboundPaymentView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundPaymentActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_payment_data);

        setAnalytics(new StayOutboundPaymentAnalyticsImpl());

        mPaymentRemoteImpl = new PaymentRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundPaymentAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mStayIndex = intent.getIntExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_STAY_INDEX, -1);

        if (mStayIndex == -1)
        {
            return false;
        }

        mStayName = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_STAY_NAME);
        mRoomType = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_ROOM_TYPE);
        mRateCode = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_RATE_CODE);
        mRateKey = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_RATE_KEY);
        mRoomTypeCode = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_ROOM_TYPE_CODE);

        if (DailyTextUtils.isTextEmpty(mStayName, mRoomType, mRateCode, mRateKey, mRoomTypeCode) == true)
        {
            return false;
        }

        String checkInDateTime = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_CHECKIN);
        String checkOutDateTime = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_CHECKOUT);

        setStayBookDateTime(checkInDateTime, checkOutDateTime);

        int numberOfAdults = intent.getIntExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
        ArrayList<Integer> childAgeList = intent.getIntegerArrayListExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_CHILD_LIST);

        setPeople(numberOfAdults, childAgeList);

        return true;
    }

    @Override
    public void onPostCreate()
    {
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();
    }

    @Override
    protected void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        Observable<StayOutboundPayment> observable;

        addCompositeDisposable(Observable.zip(mPaymentRemoteImpl.getStayOutBoundPayment(mStayBookDateTime, mStayIndex, mRateCode, mRateKey, mRoomTypeCode, mPeople)//
            , mPaymentRemoteImpl.getSimpleCardList(), mProfileRemoteImpl.getProfile(), new Function3<StayOutboundPayment, List<Card>, User, Boolean>()
            {
                @Override
                public Boolean apply(@io.reactivex.annotations.NonNull StayOutboundPayment stayOutboundPayment//
                    , @io.reactivex.annotations.NonNull List<Card> cardList, @io.reactivex.annotations.NonNull User user) throws Exception
                {
                    setStayOutboundPayment(stayOutboundPayment);

                    Card selectedCard = getSelectedCard(cardList);
                    setSelectedCard(selectedCard);
                    DailyPreference.getInstance(getActivity()).setSelectedCard(selectedCard);

                    setUser(user);

                    return true;
                }
            }).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
            {
                onPaymentInformation(mStayOutboundPayment);
                onUserInformation(mUser);
                onSimpleCard(mSelectedCard);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                onHandleError(throwable);
                onBackClick();
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }


    private void onPaymentInformation(StayOutboundPayment stayOutboundPayment)
    {


    }

    private void onUserInformation(User user)
    {
        String firstName = DailyUserPreference.getInstance(getActivity()).getOverseasFirstName();
        String lastName = DailyUserPreference.getInstance(getActivity()).getOverseasLastName();
        String email = DailyUserPreference.getInstance(getActivity()).getOverseasEmail();
        String phone = DailyUserPreference.getInstance(getActivity()).getOverseasPhone();

    }

    private void onSimpleCard(Card card)
    {


    }

    private void setStayOutboundPayment(StayOutboundPayment stayOutboundPayment)
    {
        mStayOutboundPayment = stayOutboundPayment;
    }

    private void setUser(User user)
    {
        if (user == null)
        {
            return;
        }

        mUser = user;
    }

    private void setSelectedCard(Card card)
    {
        mSelectedCard = card;
    }

    private void setStayBookDateTime(String checkInDateTime, String checkOutDateTime)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        if (mStayBookDateTime == null)
        {
            mStayBookDateTime = new StayBookDateTime();
        }

        try
        {
            mStayBookDateTime.setCheckInDateTime(checkInDateTime);
            mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void setPeople(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        if (mPeople == null)
        {
            mPeople = new People(People.DEFAULT_ADULTS, null);
        }

        mPeople.numberOfAdults = numberOfAdults;
        mPeople.setChildAgeList(childAgeList);
    }

    private Card getSelectedCard(List<Card> cardList)
    {
        if (cardList == null || cardList.size() == 0)
        {
            return null;
        } else
        {
            // 기존에 저장된 카드 정보를 가져온다.
            String selectedCard = DailyPreference.getInstance(getActivity()).getSelectedSimpleCard();

            if (selectedCard == null)
            {
                return cardList.get(0);
            } else
            {
                for (Card card : cardList)
                {
                    String value = card.number.replaceAll("\\*|-", "").substring(4) + card.billkey.substring(3, 7);

                    // 이전 버전 호환.
                    if (selectedCard.equalsIgnoreCase(card.billkey) == true//
                        || selectedCard.equalsIgnoreCase(value) == true)
                    {
                        return card;
                    }
                }

                return cardList.get(0);
            }
        }
    }
}

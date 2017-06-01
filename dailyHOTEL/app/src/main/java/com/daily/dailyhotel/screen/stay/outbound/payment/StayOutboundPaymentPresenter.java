package com.daily.dailyhotel.screen.stay.outbound.payment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.Guest;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.UserInformation;
import com.daily.dailyhotel.repository.remote.PaymentRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.screen.common.call.CallDialogActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.mydaily.creditcard.CreditCardListActivity;
import com.twoheart.dailyhotel.screen.mydaily.creditcard.RegisterCreditCardActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.InputMobileNumberDialogActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

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
    private int mStayIndex, mRoomPrice, mRoomBedTypeId;
    private People mPeople;
    private String mStayName, mRoomType;
    private String mRateCode, mRateKey, mRoomTypeCode;
    private StayOutboundPayment mStayOutboundPayment;
    private Card mSelectedCard;
    private Guest mGuest;
    private StayOutboundPayment.PaymentType mPaymentType;

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
        mProfileRemoteImpl = new ProfileRemoteImpl(activity);

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
        mRoomPrice = intent.getIntExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_ROOM_PRICE, -1);
        mRoomType = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_ROOM_TYPE);
        mRateCode = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_RATE_CODE);
        mRateKey = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_RATE_KEY);
        mRoomTypeCode = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_ROOM_TYPE_CODE);
        mRoomBedTypeId = intent.getIntExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_ROOM_BED_TYPE_ID, 0);

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
        getViewInterface().setToolbarTitle(getString(R.string.actionbar_title_payment_activity));

        // 리모트 컨피그에 있는 결제 타입
        checkAvailablePaymentType();


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

        switch (requestCode)
        {
            case StayOutboundPaymentActivity.REQUEST_CODE_CARDMANAGER:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    String cardName = data.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_NAME);
                    String cardNumber = data.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_NUMBER);
                    String cardBillingKey = data.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_BILLING_KEY);
                    String cardCd = data.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_CD);

                    if (DailyTextUtils.isTextEmpty(cardName, cardNumber, cardBillingKey, cardCd) == false)
                    {
                        setSelectCard(cardName, cardNumber, cardBillingKey, cardCd);
                        setPaymentType(StayOutboundPayment.PaymentType.EASY_CARD);
                    }
                }

                checkEasyCardList();
                break;
            }

            case StayOutboundPaymentActivity.REQUEST_CODE_REGISTER_CARD:
            case StayOutboundPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT:
            {
                // 간편 결제 실행후 카드가 없어 등록후에 돌아온경우.
                String msg = null;

                switch (resultCode)
                {
                    case Constants.CODE_RESULT_PAYMENT_BILLING_SUCCSESS:
                        if (requestCode == Constants.CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD_AND_PAYMENT)
                        {
                            // 신용카드 등록후에 바로 결제를 할경우.
                            //                            DailyMobileAPI.getInstance(this).requestUserBillingCardList(mNetworkTag, mPaymentAfterRegisterCreditCardCallback);
                        } else
                        {
                            checkEasyCardList();
                        }
                        return;

                    case Constants.CODE_RESULT_PAYMENT_BILLING_DUPLICATE:
                        msg = getString(R.string.message_billing_duplicate);
                        break;

                    case Constants.CODE_RESULT_PAYMENT_BILLING_FAIL:
                        msg = getString(R.string.message_billing_fail);
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION:
                        restartExpiredSession();
                        return;

                    case Constants.CODE_RESULT_ACTIVITY_PAYMENT_FAIL:
                        msg = getString(R.string.act_toast_payment_fail);
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR:
                        msg = getString(R.string.act_toast_payment_network_error);
                        break;
                }

                if (DailyTextUtils.isTextEmpty(msg) == false)
                {
                    String title = getString(R.string.dialog_notice2);
                    String positive = getString(R.string.dialog_btn_text_confirm);

                    getViewInterface().showSimpleDialog(title, msg, positive, null);
                }
                break;
            }

            case StayOutboundPaymentActivity.REQUEST_CODE_REGISTER_PHONE_NUMBER:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    String phoneMobile = data.getStringExtra(InputMobileNumberDialogActivity.INTENT_EXTRA_MOBILE_NUMBER);
                    setUserPhoneNumber(phoneMobile);
                    notifyGuestPhoneInformationChanged();
                }
                break;
            }
        }
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

        addCompositeDisposable(Observable.zip(mPaymentRemoteImpl.getStayOutBoundPayment(mStayBookDateTime, mStayIndex//
            , mRateCode, mRateKey, mRoomTypeCode, mRoomBedTypeId, mPeople)//
            , mPaymentRemoteImpl.getSimpleCardList(), mProfileRemoteImpl.getUserInformation()//
            , new Function3<StayOutboundPayment, List<Card>, UserInformation, Boolean>()
            {
                @Override
                public Boolean apply(@io.reactivex.annotations.NonNull StayOutboundPayment stayOutboundPayment//
                    , @io.reactivex.annotations.NonNull List<Card> cardList, @io.reactivex.annotations.NonNull UserInformation userInformation) throws Exception
                {
                    setStayOutboundPayment(stayOutboundPayment);
                    setSelectCard(getSelectedCard(cardList));
                    setGuestInformation(userInformation);

                    return true;
                }
            }).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
            {
                onBookingInformation(mStayOutboundPayment, mStayBookDateTime);

                notifyGuestInformationChanged();
                notifyPaymentTypeChanged();
                notifyEasyCardChanged();
                notifyStayOutboundPaymentChanged();

                // 가격이 변동된 경우
                if (mRoomPrice != mStayOutboundPayment.totalPrice)
                {

                }

                // 솔드 아웃인 경우
                if (mStayOutboundPayment.availableRooms == 0)
                {

                }

                unLockAll();
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

    @Override
    public void onCallClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(CallDialogActivity.newInstance(getActivity()), StayOutboundPaymentActivity.REQUEST_CODE_CALL);
    }

    @Override
    public void onBonusClick(boolean enabled)
    {

    }

    @Override
    public void onCardManagerClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(CreditCardListActivity.newInstance(getActivity()//
            , mSelectedCard.name, mSelectedCard.number, mSelectedCard.billKey, mSelectedCard.cd)//
            , StayOutboundPaymentActivity.REQUEST_CODE_CARDMANAGER);
    }

    @Override
    public void onRegisterCardClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(RegisterCreditCardActivity.newInstance(getActivity()), StayOutboundPaymentActivity.REQUEST_CODE_REGISTER_CARD);
    }

    @Override
    public void onPaymentTypeClick(StayOutboundPayment.PaymentType paymentType)
    {
        if (paymentType == null)
        {
            return;
        }

        setPaymentType(paymentType);
        notifyPaymentTypeChanged();
    }

    @Override
    public void onPaymentClick()
    {

    }

    @Override
    public void onPhoneNumberClick(String phoneNumber)
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(InputMobileNumberDialogActivity.newInstance(getActivity(), phoneNumber)//
            , StayOutboundPaymentActivity.REQUEST_CODE_REGISTER_PHONE_NUMBER);
    }

    private void onBookingInformation(StayOutboundPayment stayOutboundPayment, StayBookDateTime stayBookDateTime)
    {
        if (stayOutboundPayment == null || stayBookDateTime == null)
        {
            return;
        }

        final String DATE_FORMAT = "yyyy.MM.dd(EEE)";

        try
        {
            String checkInTime = getString(R.string.label_stay_outbound_payment_hour, stayOutboundPayment.checkInTime.split(":")[0]);
            String checkInDate = stayBookDateTime.getCheckInDateTime(DATE_FORMAT);

            SpannableString checkInDateSpannableString = new SpannableString(checkInDate + " " + checkInTime);
            checkInDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getBoldTypeface()),//
                checkInDate.length(), checkInDate.length() + checkInTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String checkOutTime = getString(R.string.label_stay_outbound_payment_hour, stayOutboundPayment.checkOutTime.split(":")[0]);
            String checkOutDate = stayBookDateTime.getCheckOutDateTime(DATE_FORMAT);

            SpannableString checkOutDateSpannableString = new SpannableString(checkOutDate + " " + checkOutTime);
            checkOutDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getBoldTypeface()),//
                checkOutDate.length(), checkOutDate.length() + checkOutTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            getViewInterface().setBooking(checkInDateSpannableString, checkOutDateSpannableString, mStayBookDateTime.getNights(), mStayName, mRoomType);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void notifyEasyCardChanged()
    {
        getViewInterface().setEasyCard(mSelectedCard);
    }

    private void notifyStayOutboundPaymentChanged()
    {
        if (mGuest == null || mStayOutboundPayment == null || mStayBookDateTime == null)
        {
            return;
        }

        try
        {
            getViewInterface().setStayOutboundPayment(mGuest.bonus, mStayBookDateTime.getNights()//
                , mStayOutboundPayment.totalPrice, mStayOutboundPayment.discountPrice//
                , mStayOutboundPayment.paymentPrice, mStayOutboundPayment.feeTotalAmountUsd);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        getViewInterface().setRefundDescriptionList(mStayOutboundPayment.getRefundDescriptionList());
    }

    private void setStayOutboundPayment(StayOutboundPayment stayOutboundPayment)
    {
        mStayOutboundPayment = stayOutboundPayment;
    }

    private void setGuestInformation(UserInformation userInformation)
    {
        if (userInformation == null)
        {
            return;
        }

        if (mGuest == null)
        {
            mGuest = getGustInformation();
        }

        mGuest.bonus = userInformation.bonus;

        if (DailyTextUtils.isTextEmpty(mGuest.phone) == true)
        {
            mGuest.phone = userInformation.phone;
        }

        if (DailyTextUtils.isTextEmpty(mGuest.email) == true)
        {
            mGuest.email = userInformation.email;
        }
    }

    private Guest getGustInformation()
    {
        Guest guest = new Guest();

        guest.firstName = DailyUserPreference.getInstance(getActivity()).getOverseasFirstName();
        guest.lastName = DailyUserPreference.getInstance(getActivity()).getOverseasLastName();
        guest.phone = DailyUserPreference.getInstance(getActivity()).getOverseasPhone();
        guest.email = DailyUserPreference.getInstance(getActivity()).getOverseasEmail();

        return guest;
    }

    private void setUserPhoneNumber(String phoneNumber)
    {
        if (mGuest == null)
        {
            return;
        }

        mGuest.phone = phoneNumber;
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

    private void setPaymentType(StayOutboundPayment.PaymentType paymentType)
    {
        mPaymentType = paymentType;
    }

    private void setSelectCard(Card card)
    {
        mSelectedCard = card;

        if (card != null && DailyTextUtils.isTextEmpty(card.number, card.billKey) == false)
        {
            DailyPreference.getInstance(getActivity()).setSelectedCard(card);
        }
    }

    private void setSelectCard(String cardName, String cardNumber, String cardBillingKey, String cardCd)
    {
        if (DailyTextUtils.isTextEmpty(cardName, cardNumber, cardBillingKey, cardCd) == true)
        {
            return;
        }

        if (mSelectedCard == null)
        {
            mSelectedCard = new Card();
        }

        mSelectedCard.name = cardName;
        mSelectedCard.number = cardNumber;
        mSelectedCard.billKey = cardBillingKey;
        mSelectedCard.cd = cardCd;

        DailyPreference.getInstance(getActivity()).setSelectedCard(mSelectedCard);
    }

    private void notifyGuestInformationChanged()
    {
        if (mGuest == null || mPeople == null)
        {
            return;
        }

        getViewInterface().setGuestInformation(mGuest.firstName, mGuest.lastName, mGuest.phone, mGuest.email);
        getViewInterface().setPeople(mPeople);
    }

    private void notifyGuestPhoneInformationChanged()
    {
        if (mGuest == null)
        {
            return;
        }

        String phone = DailyUserPreference.getInstance(getActivity()).getOverseasPhone();

        getViewInterface().setGuestPhoneInformation(mGuest.phone);
    }

    private void notifyPaymentTypeChanged()
    {
        if (mPaymentType == null)
        {
            return;
        }

        getViewInterface().setPaymentType(mPaymentType);
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
                    String value = card.number.replaceAll("\\*|-", "").substring(4) + card.billKey.substring(3, 7);

                    // 이전 버전 호환.
                    if (selectedCard.equalsIgnoreCase(card.billKey) == true//
                        || selectedCard.equalsIgnoreCase(value) == true)
                    {
                        return card;
                    }
                }

                return cardList.get(0);
            }
        }
    }

    private void checkEasyCardList()
    {
        screenLock(true);

        addCompositeDisposable(mPaymentRemoteImpl.getSimpleCardList().subscribe(new Consumer<List<Card>>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull List<Card> cardList) throws Exception
            {
                if (cardList.size() > 0)
                {
                    setPaymentType(StayOutboundPayment.PaymentType.EASY_CARD);
                    setSelectCard(getSelectedCard(cardList));
                } else
                {
                    setSelectCard(null);
                }

                notifyEasyCardChanged();
                notifyPaymentTypeChanged();

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }

    private void checkAvailablePaymentType()
    {
        boolean isSimpleCardPaymentEnabled = DailyPreference.getInstance(getActivity()).isRemoteConfigStayOutboundSimpleCardPaymentEnabled();
        boolean isCardPaymentEnabled = DailyPreference.getInstance(getActivity()).isRemoteConfigStayOutboundCardPaymentEnabled();
        boolean isPhonePaymentEnabled = DailyPreference.getInstance(getActivity()).isRemoteConfigStayOutboundPhonePaymentEnabled();

        StringBuilder guideMemo = new StringBuilder();

        if (isSimpleCardPaymentEnabled == false)
        {
            guideMemo.append(getString(R.string.label_simple_payment));
            guideMemo.append(", ");
        }

        if (isCardPaymentEnabled == false)
        {
            guideMemo.append(getString(R.string.label_card_payment));
            guideMemo.append(", ");
        }

        if (isPhonePaymentEnabled == false)
        {
            guideMemo.append(getString(R.string.act_booking_pay_mobile));
            guideMemo.append(", ");
        }

        if (guideMemo.length() > 0)
        {
            guideMemo.setLength(guideMemo.length() - 2);

            getViewInterface().setMemoPaymentType(getString(R.string.message_dont_support_payment_type, guideMemo.toString()));
        } else
        {
            getViewInterface().setMemoPaymentType(null);
        }

        getViewInterface().setPaymentTypeEnabled(StayOutboundPayment.PaymentType.EASY_CARD, isSimpleCardPaymentEnabled);
        getViewInterface().setPaymentTypeEnabled(StayOutboundPayment.PaymentType.CARD, isCardPaymentEnabled);
        getViewInterface().setPaymentTypeEnabled(StayOutboundPayment.PaymentType.PHONE_PAY, isPhonePaymentEnabled);

        if (isSimpleCardPaymentEnabled == true)
        {
            setPaymentType(StayOutboundPayment.PaymentType.EASY_CARD);
        } else if (isCardPaymentEnabled == true)
        {
            setPaymentType(StayOutboundPayment.PaymentType.CARD);
        } else if (isPhonePaymentEnabled == true)
        {
            setPaymentType(StayOutboundPayment.PaymentType.PHONE_PAY);
        }
    }
}

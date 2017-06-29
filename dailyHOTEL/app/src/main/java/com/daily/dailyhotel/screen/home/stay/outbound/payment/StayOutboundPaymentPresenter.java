package com.daily.dailyhotel.screen.home.stay.outbound.payment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.Guest;
import com.daily.dailyhotel.entity.PaymentResult;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.UserInformation;
import com.daily.dailyhotel.repository.remote.PaymentRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.screen.common.call.CallDialogActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.thankyou.StayOutboundThankYouActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.StayOutboundPaymentWebActivity;
import com.twoheart.dailyhotel.screen.mydaily.creditcard.CreditCardListActivity;
import com.twoheart.dailyhotel.screen.mydaily.creditcard.RegisterCreditCardActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.InputMobileNumberDialogActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private String mStayName, mRoomType, mImageUrl;
    private String mRateCode, mRateKey, mRoomTypeCode;
    private StayOutboundPayment mStayOutboundPayment;
    private Card mSelectedCard;
    private Guest mGuest;
    private StayOutboundPayment.PaymentType mPaymentType;
    private boolean mBonusSelected;

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
        mImageUrl = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_IMAGE_URL);
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

        String checkInDateTime = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_CHECK_IN);
        String checkOutDateTime = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_CHECK_OUT);

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

        setBonusSelected(false);

        notifyBonusSelectedChanged();
        notifyBonusEnabledChanged();
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
            case StayOutboundPaymentActivity.REQUEST_CODE_CARD_MANAGER:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    String cardName = data.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_NAME);
                    String cardNumber = data.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_NUMBER);
                    String cardBillingKey = data.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_BILLING_KEY);
                    String cardCd = data.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_CD);

                    if (DailyTextUtils.isTextEmpty(cardName, cardNumber, cardBillingKey, cardCd) == false)
                    {
                        setSelectCard(cardName, cardNumber, cardBillingKey, cardCd);
                        setPaymentType(StayOutboundPayment.PaymentType.EASY_CARD);
                    }
                }

                selectEasyCard(cardList ->
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
                });
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
                        if (requestCode == StayOutboundPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT)
                        {
                            selectEasyCard(cardList ->
                            {
                                unLockAll();

                                if (cardList.size() > 0)
                                {
                                    setPaymentType(StayOutboundPayment.PaymentType.EASY_CARD);
                                    setSelectCard(getSelectedCard(cardList));

                                    onPaymentClick(mGuest.firstName, mGuest.lastName, mGuest.phone, mGuest.email);
                                } else
                                {
                                    setSelectCard(null);
                                }

                                notifyEasyCardChanged();
                                notifyPaymentTypeChanged();
                            });
                        } else
                        {
                            selectEasyCard(cardList ->
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
                            });
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

            case StayOutboundPaymentActivity.REQUEST_CODE_PAYMENT_WEB_CARD:
            case StayOutboundPaymentActivity.REQUEST_CODE_PAYMENT_WEB_PHONE:
                onPaymentWebResult(resultCode, data.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_PAYMENT_RESULT));
                break;
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

        addCompositeDisposable(Observable.zip(mPaymentRemoteImpl.getStayOutboundPayment(mStayBookDateTime, mStayIndex//
            , mRateCode, mRateKey, mRoomTypeCode, mRoomBedTypeId, mPeople)//
            , mPaymentRemoteImpl.getEasyCardList(), mProfileRemoteImpl.getUserInformation()//
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
                notifyBonusEnabledChanged();
                notifyPaymentTypeChanged();
                notifyEasyCardChanged();
                notifyStayOutboundPaymentChanged();

                // 가격이 변동된 경우
                if (mRoomPrice != mStayOutboundPayment.totalPrice)
                {
                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_payment_changed_price)//
                        , getString(R.string.dialog_btn_text_confirm), null);
                }

                // 솔드 아웃인 경우
                if (mStayOutboundPayment.availableRooms == 0)
                {
                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_outbound_payment_sold_out)//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                                onBackClick();
                            }
                        });
                }

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                unLockAll();

                getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.act_base_network_connect)//
                    , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            onBackClick();
                        }
                    });
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
        if (mGuest == null || mStayBookDateTime == null || lock() == true)
        {
            return;
        }

        if (enabled == true)
        {
            int discountPrice = mGuest.bonus;

            if (discountPrice <= 0)
            {
                unLockAll();
                return;
            }

            setBonusSelected(true);
            notifyBonusSelectedChanged();

            if (discountPrice >= mStayOutboundPayment.totalPrice)
            {
                discountPrice = mStayOutboundPayment.totalPrice;
            }

            setStayOutboundPayment(discountPrice);

            notifyStayOutboundPaymentChanged();
        } else
        {
            // 적립금 삭제
            getViewInterface().showSimpleDialog(null, getString(R.string.message_booking_cancel_bonus), getString(R.string.dialog_btn_text_yes), //
                getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        setBonusSelected(false);
                        setStayOutboundPayment(0);

                        notifyBonusSelectedChanged();
                        notifyStayOutboundPaymentChanged();
                    }
                }, null);
        }

        unLockAll();
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
            , StayOutboundPaymentActivity.REQUEST_CODE_CARD_MANAGER);
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
    public void onPaymentClick(String firstName, String lastName, String phone, String email)
    {
        if (mGuest == null || lock() == true)
        {
            return;
        }

        mGuest.firstName = firstName;
        mGuest.lastName = lastName;
        mGuest.phone = phone;
        mGuest.email = email;

        if (DailyTextUtils.isTextEmpty(firstName, lastName) == true)
        {
            DailyToast.showToast(getActivity(), getString(R.string.message_stay_outbound_payment_empty_name), DailyToast.LENGTH_SHORT);

            unLockAll();
            return;
        }

        if (DailyTextUtils.isTextEmpty(phone) == true)
        {
            DailyToast.showToast(getActivity(), getString(R.string.toast_msg_please_input_contact), DailyToast.LENGTH_SHORT);

            unLockAll();
            return;
        }

        if (DailyTextUtils.isTextEmpty(email) == true)
        {
            DailyToast.showToast(getActivity(), getString(R.string.toast_msg_please_input_email), DailyToast.LENGTH_SHORT);

            unLockAll();
            return;
        }

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == false)
        {
            DailyToast.showToast(getActivity(), getString(R.string.toast_msg_wrong_email_address), DailyToast.LENGTH_SHORT);

            unLockAll();
            return;
        }

        // 보너스로만 결제하는 경우
        if (mBonusSelected == true && mStayOutboundPayment.totalPrice == mStayOutboundPayment.discountPrice)
        {
            // 보너스로만 결제할 경우에는 팝업이 기존의 카드 타입과 동일한다.
            getViewInterface().showAgreeTermDialog(StayOutboundPayment.PaymentType.CARD, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getViewInterface().hideSimpleDialog();

                    unLockAll();

                    onAgreedPaymentClick();
                }
            }, new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    unLockAll();
                }
            });
        } else
        {
            if (mPaymentType == StayOutboundPayment.PaymentType.EASY_CARD && mSelectedCard == null)
            {
                startActivityForResult(RegisterCreditCardActivity.newInstance(getActivity())//
                    , StayOutboundPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT);
            } else
            {
                getViewInterface().showAgreeTermDialog(mPaymentType, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        getViewInterface().hideSimpleDialog();

                        unLockAll();

                        onAgreedPaymentClick();
                    }
                }, new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        unLockAll();
                    }
                });
            }
        }
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

    private void onAgreedPaymentClick()
    {
        if (lock() == true)
        {
            return;
        }

        screenLock(true);

        // 입력돈 내용을 저장한다.
        DailyUserPreference.getInstance(getActivity()).setOverseasInformation(mGuest.firstName, mGuest.lastName, mGuest.phone, mGuest.email);

        if (mBonusSelected == true && mStayOutboundPayment.totalPrice == mStayOutboundPayment.discountPrice)
        {
            addCompositeDisposable(mPaymentRemoteImpl.getPaymentTypeBonus(mStayBookDateTime, mStayIndex//
                , mRateCode, mRateKey, mRoomTypeCode, mRoomBedTypeId, mPeople//
                , mBonusSelected, mGuest, mStayOutboundPayment.totalPrice).subscribe(new Consumer<PaymentResult>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull PaymentResult paymentResult) throws Exception
                {
                    startActivityForResult(StayOutboundThankYouActivity.newInstance(getActivity(), mStayIndex, mStayName, mImageUrl, mStayOutboundPayment.totalPrice//
                        , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , mStayOutboundPayment.checkInTime, mStayOutboundPayment.checkOutTime, mRoomType, paymentResult.bookingIndex), StayOutboundPaymentActivity.REQUEST_CODE_THANK_YOU);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    unLockAll();

                    if (throwable instanceof BaseException)
                    {
                        onPaymentError((BaseException) throwable);
                    } else
                    {
                        getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.act_base_network_connect)//
                            , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialog)
                                {
                                    onBackClick();
                                }
                            });
                    }
                }
            }));
        } else
        {
            switch (mPaymentType)
            {
                case EASY_CARD:
                {
                    // 진입하기 전에 이미 막혀있지만 최후의 보루
                    if (mSelectedCard == null)
                    {
                        startActivityForResult(RegisterCreditCardActivity.newInstance(getActivity())//
                            , StayOutboundPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT);
                        return;
                    }

                    addCompositeDisposable(mPaymentRemoteImpl.getPaymentTypeEasy(mStayBookDateTime, mStayIndex//
                        , mRateCode, mRateKey, mRoomTypeCode, mRoomBedTypeId, mPeople//
                        , mBonusSelected, mGuest, mStayOutboundPayment.totalPrice, mSelectedCard.billKey).subscribe(new Consumer<PaymentResult>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull PaymentResult paymentResult) throws Exception
                        {
                            startActivityForResult(StayOutboundThankYouActivity.newInstance(getActivity(), mStayIndex, mStayName, mImageUrl, mStayOutboundPayment.totalPrice//
                                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                                , mStayOutboundPayment.checkInTime, mStayOutboundPayment.checkOutTime, mRoomType, paymentResult.bookingIndex), StayOutboundPaymentActivity.REQUEST_CODE_THANK_YOU);
                        }
                    }, throwable ->
                    {
                        unLockAll();

                        if (throwable instanceof BaseException)
                        {
                            onPaymentError((BaseException) throwable);
                        } else
                        {
                            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.act_base_network_connect)//
                                , getString(R.string.frag_error_btn), null, new DialogInterface.OnDismissListener()
                                {
                                    @Override
                                    public void onDismiss(DialogInterface dialog)
                                    {
                                        onBackClick();
                                    }
                                });
                        }
                    }));
                    break;
                }

                case CARD:
                {
                    final String PAYMENT_TYPE = "CREDIT_CARD";

                    JSONObject jsonObject = getPaymentJSONObject(PAYMENT_TYPE, mStayBookDateTime, mStayIndex//
                        , mRateCode, mRateKey, mRoomTypeCode, mRoomBedTypeId, mPeople//
                        , mBonusSelected, mGuest, mStayOutboundPayment.totalPrice);

                    startActivityForResult(StayOutboundPaymentWebActivity.newInstance(getActivity(), mStayIndex, "card", jsonObject.toString())//
                        , StayOutboundPaymentActivity.REQUEST_CODE_PAYMENT_WEB_CARD);
                    break;
                }

                case PHONE_PAY:
                {
                    final String PAYMENT_TYPE = "MOBILE_PHONE";

                    JSONObject jsonObject = getPaymentJSONObject(PAYMENT_TYPE, mStayBookDateTime, mStayIndex//
                        , mRateCode, mRateKey, mRoomTypeCode, mRoomBedTypeId, mPeople//
                        , mBonusSelected, mGuest, mStayOutboundPayment.totalPrice);

                    startActivityForResult(StayOutboundPaymentWebActivity.newInstance(getActivity(), mStayIndex, "mobile", jsonObject.toString())//
                        , StayOutboundPaymentActivity.REQUEST_CODE_PAYMENT_WEB_PHONE);
                    break;
                }
            }
        }
    }

    private JSONObject getPaymentJSONObject(String paymentType, StayBookDateTime stayBookDateTime, int index//
        , String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people//
        , boolean usedBonus, Guest guest, int totalPrice)
    {
        JSONObject jsonObject = new JSONObject();

        final int NUMBER_OF_ROOMS = 1;

        try
        {
            jsonObject.put("arrivalDate", stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            jsonObject.put("departureDate", stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            jsonObject.put("numberOfRooms", NUMBER_OF_ROOMS);
            jsonObject.put("rooms", getRooms(new People[]{people}, new int[]{roomBedTypeId}));
            jsonObject.put("rateCode", rateCode);
            jsonObject.put("rateKey", rateKey);
            jsonObject.put("roomTypeCode", roomTypeCode);

            if (usedBonus == true)
            {
                int bonus = guest.bonus > totalPrice ? totalPrice : guest.bonus;

                jsonObject.put("bonusAmount", bonus);
            }

            jsonObject.put("firstName", guest.firstName);
            jsonObject.put("lastName", guest.lastName);
            jsonObject.put("email", guest.email);
            jsonObject.put("phoneNumber", guest.phone.replace("-", ""));
            jsonObject.put("paymentType", paymentType);
            jsonObject.put("total", totalPrice);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            jsonObject = null;
        }

        return jsonObject;
    }

    private JSONArray getRooms(People[] peoples, int[] roomBedTypeIds)
    {
        JSONArray roomJSONArray = new JSONArray();

        if (peoples == null || peoples.length == 0 || roomBedTypeIds == null || roomBedTypeIds.length == 0//
            || peoples.length != roomBedTypeIds.length)
        {
            return roomJSONArray;
        }

        try
        {
            int length = peoples.length;

            for (int i = 0; i < length; i++)
            {
                JSONObject roomJSONObject = new JSONObject();

                roomJSONObject.put("numberOfAdults", peoples[i].numberOfAdults);
                roomJSONObject.put("roomBedTypeId", roomBedTypeIds[i]);

                List<Integer> childAgeList = peoples[i].getChildAgeList();

                if (childAgeList != null && childAgeList.size() > 0)
                {
                    JSONArray childJSONArray = new JSONArray();

                    for (int age : childAgeList)
                    {
                        childJSONArray.put(Integer.toString(age));
                    }

                    roomJSONObject.put("numberOfChildren", childAgeList.size());
                    roomJSONObject.put("childAges", childJSONArray);
                } else
                {
                    roomJSONObject.put("numberOfChildren", 0);
                    roomJSONObject.put("childAges", null);
                }

                roomJSONArray.put(roomJSONObject);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return roomJSONArray;
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
                , mStayOutboundPayment.feeTotalAmountUsd);

            // 1000원 미만 결제시에 간편/일반 결제 불가 - 쿠폰 또는 적립금 전체 사용이 아닌경우 조건 추가
            int paymentPrice = mStayOutboundPayment.totalPrice - (mBonusSelected == true ? mGuest.bonus : 0);

            final int CARD_MIN_PRICE = 1000;
            final int PHONE_MAX_PRICE = 500000;

            if (paymentPrice > 0 && paymentPrice < CARD_MIN_PRICE)
            {
                getViewInterface().setPaymentTypeEnabled(StayOutboundPayment.PaymentType.EASY_CARD, false);
                getViewInterface().setPaymentTypeEnabled(StayOutboundPayment.PaymentType.CARD, false);

                if (DailyPreference.getInstance(getActivity()).isRemoteConfigStayOutboundPhonePaymentEnabled() == true)
                {
                    getViewInterface().setPaymentTypeEnabled(StayOutboundPayment.PaymentType.PHONE_PAY, true);
                }
            } else if (paymentPrice >= PHONE_MAX_PRICE)
            {
                if (DailyPreference.getInstance(getActivity()).isRemoteConfigStayOutboundSimpleCardPaymentEnabled() == true)
                {
                    getViewInterface().setPaymentTypeEnabled(StayOutboundPayment.PaymentType.EASY_CARD, true);
                }

                if (DailyPreference.getInstance(getActivity()).isRemoteConfigStayOutboundCardPaymentEnabled() == true)
                {
                    getViewInterface().setPaymentTypeEnabled(StayOutboundPayment.PaymentType.CARD, true);
                }

                getViewInterface().setPaymentTypeEnabled(StayOutboundPayment.PaymentType.PHONE_PAY, false);
            } else if (paymentPrice > 0)
            {
                if (DailyPreference.getInstance(getActivity()).isRemoteConfigStayOutboundSimpleCardPaymentEnabled() == true)
                {
                    getViewInterface().setPaymentTypeEnabled(StayOutboundPayment.PaymentType.EASY_CARD, true);
                }

                if (DailyPreference.getInstance(getActivity()).isRemoteConfigStayOutboundCardPaymentEnabled() == true)
                {
                    getViewInterface().setPaymentTypeEnabled(StayOutboundPayment.PaymentType.CARD, true);
                }

                if (DailyPreference.getInstance(getActivity()).isRemoteConfigStayOutboundPhonePaymentEnabled() == true)
                {
                    getViewInterface().setPaymentTypeEnabled(StayOutboundPayment.PaymentType.PHONE_PAY, true);
                }
            }

        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        getViewInterface().setNonRefundableDescription(mStayOutboundPayment.nonRefundableDescription);
    }

    private void setStayOutboundPayment(StayOutboundPayment stayOutboundPayment)
    {
        mStayOutboundPayment = stayOutboundPayment;
    }

    private void setStayOutboundPayment(int discountPrice)
    {
        if (mStayOutboundPayment == null)
        {
            return;
        }

        mStayOutboundPayment.discountPrice = discountPrice;
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
            DailyPreference.getInstance(getActivity()).setFavoriteCard(card.number, card.billKey);
        }
    }

    private void setBonusSelected(boolean selected)
    {
        mBonusSelected = selected;
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

        DailyPreference.getInstance(getActivity()).setFavoriteCard(mSelectedCard.number, mSelectedCard.billKey);
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

    private void notifyBonusEnabledChanged()
    {
        if (mGuest == null)
        {
            getViewInterface().setBonusEnabled(false);
        } else
        {
            getViewInterface().setBonusEnabled(mGuest.bonus > 0);
        }
    }

    private void notifyBonusSelectedChanged()
    {
        getViewInterface().setBonusSelected(mBonusSelected);
    }

    private Card getSelectedCard(List<Card> cardList)
    {
        if (cardList == null || cardList.size() == 0)
        {
            return null;
        } else
        {
            // 기존에 저장된 카드 정보를 가져온다.
            String selectedCard = DailyPreference.getInstance(getActivity()).getFavoriteCard();

            if (selectedCard == null)
            {
                return cardList.get(0);
            } else
            {
                for (Card card : cardList)
                {
                    String value = card.number.replaceAll("\\*|-", "") + card.billKey.substring(3, 7);

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

    private void selectEasyCard(Consumer<List<Card>> consumer)
    {
        screenLock(true);

        addCompositeDisposable(mPaymentRemoteImpl.getEasyCardList().subscribe(consumer, new Consumer<Throwable>()
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
        boolean isSimpleCardPaymentEnabled = DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundSimpleCardPaymentEnabled();
        boolean isCardPaymentEnabled = DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundCardPaymentEnabled();
        boolean isPhonePaymentEnabled = DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundPhonePaymentEnabled();

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

    private void onPaymentWebResult(int resultCode, String result)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            Observable.just(result).map(jsonString ->
            {
                PaymentResult paymentResult = new PaymentResult();

                JSONObject jsonObject = new JSONObject(jsonString);

                int msgCode = jsonObject.getInt("msgCode");
                String msg = jsonObject.getString("msg");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = jsonObject.getJSONObject("data");

                    paymentResult.bookingIndex = dataJSONObject.getInt("reservationIdx");
                    paymentResult.result = dataJSONObject.getString("result");
                } else
                {
                    throw new BaseException(msgCode, msg);
                }

                return paymentResult;
            }).subscribe(new Consumer<PaymentResult>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull PaymentResult paymentResult) throws Exception
                {
                    startActivityForResult(StayOutboundThankYouActivity.newInstance(getActivity(), mStayIndex, mStayName, mImageUrl, mStayOutboundPayment.totalPrice//
                        , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , mStayOutboundPayment.checkInTime, mStayOutboundPayment.checkOutTime, mRoomType, paymentResult.bookingIndex), StayOutboundPaymentActivity.REQUEST_CODE_THANK_YOU);

                }
            }, throwable ->
            {
                unLockAll();

                if (throwable instanceof BaseException)
                {
                    onPaymentError((BaseException) throwable);
                } else
                {
                    getViewInterface().showSimpleDialog(getString(R.string.dialog_title_payment), getString(R.string.act_base_network_connect)//
                        , getString(R.string.frag_error_btn), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                                onBackClick();
                            }
                        });
                }
            });
        } else
        {
            String title = getString(R.string.dialog_title_payment);
            String message = null;

            View.OnClickListener confirmListener = null;

            switch (resultCode)
            {
                case Constants.CODE_RESULT_ACTIVITY_PAYMENT_FAIL:
                    message = getString(R.string.act_toast_payment_fail);
                    break;

                case Constants.CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION:
                    restartExpiredSession();
                    return;

                case Constants.CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE:
                    title = getString(R.string.dialog_notice2);
                    message = getString(R.string.act_toast_payment_not_available);
                    break;


                case Constants.CODE_RESULT_ACTIVITY_PAYMENT_CANCELED:
                    message = getString(R.string.act_toast_payment_canceled);

                    confirmListener = new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                        }
                    };
                    break;

                case Constants.CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR:
                    message = getString(R.string.act_toast_payment_network_error);
                    break;

                default:
                    message = getString(R.string.act_toast_payment_fail);
                    break;
            }

            if (confirmListener == null)
            {
                confirmListener = new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        setResult(Constants.CODE_RESULT_ACTIVITY_REFRESH);
                        onBackClick();
                    }
                };
            }

            getViewInterface().showSimpleDialog(title, message, getString(R.string.dialog_btn_text_confirm), null, confirmListener, null, false);
        }
    }

    private void onPaymentError(BaseException baseException)
    {
        unLockAll();

        if (baseException == null)
        {
            getViewInterface().showSimpleDialog(getString(R.string.dialog_title_payment), getString(R.string.act_base_network_connect)//
                , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        onBackClick();
                    }
                });

            return;
        }

        String message = baseException.getMessage();

        switch (baseException.getCode())
        {

        }

        getViewInterface().showSimpleDialog(getString(R.string.dialog_title_payment), message//
            , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    onBackClick();
                }
            }, false);
    }
}

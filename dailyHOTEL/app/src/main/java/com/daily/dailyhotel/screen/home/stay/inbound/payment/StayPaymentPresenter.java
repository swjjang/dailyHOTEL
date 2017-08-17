package com.daily.dailyhotel.screen.home.stay.inbound.payment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.DomesticGuest;
import com.daily.dailyhotel.entity.PaymentResult;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayPayment;
import com.daily.dailyhotel.entity.StayRefundPolicy;
import com.daily.dailyhotel.entity.UserSimpleInformation;
import com.daily.dailyhotel.parcel.analytics.StayPaymentAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayThankYouAnalyticsParam;
import com.daily.dailyhotel.repository.remote.PaymentRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.screen.common.call.CallDialogActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.thankyou.StayThankYouActivity;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.mydaily.creditcard.CreditCardListActivity;
import com.twoheart.dailyhotel.screen.mydaily.creditcard.RegisterCreditCardActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.InputMobileNumberDialogActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function4;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayPaymentPresenter extends BaseExceptionPresenter<StayPaymentActivity, StayPaymentInterface> implements StayPaymentView.OnEventListener
{
    private static final int MIN_AMOUNT_FOR_BONUS_USAGE = 20000; // 보너스를 사용하기 위한 최소 주문 가격

    private StayPaymentAnalyticsInterface mAnalytics;

    private PaymentRemoteImpl mPaymentRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;

    private StayBookDateTime mStayBookDateTime;
    private int mStayIndex, mRoomPrice, mRoomIndex;
    private String mStayName, mImageUrl, mCategory, mRoomName;
    private StayPayment mStayPayment;
    private StayRefundPolicy mStayRefundPolicy;
    private Card mSelectedCard;
    private DomesticGuest mGuest;
    private String mCouponCode;
    private Transportation mTransportationType;
    private DailyBookingPaymentTypeView.PaymentType mPaymentType;
    private boolean mOverseas, mBonusSelected, mCouponSelected, mAgreedThirdPartyTerms;
    private UserSimpleInformation mUserSimpleInformation;

    // 서버로 해당 문자열 그대로 보냄.(수정 금지)
    enum Transportation
    {
        UNKNOWN,
        WALKING,
        CAR,
    }

    public interface StayPaymentAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(StayPaymentAnalyticsParam analyticsParam);

        StayPaymentAnalyticsParam getAnalyticsParam();

        void onScreen(Activity activity, StayBookDateTime stayBookDateTime);

        void onScreenPaymentCompleted(Activity activity, StayPayment stayPayment, StayBookDateTime stayBookDateTime//
            , int stayIndex, String stayName, DailyBookingPaymentTypeView.PaymentType paymentType, boolean fullBonus//
            , boolean registerEasyCard, UserSimpleInformation userSimpleInformation);

        void onEventTransportationVisible(Activity activity, boolean visible);

        void onEventStartPayment(Activity activity, String label);

        void onEventEndPayment(Activity activity);

        StayThankYouAnalyticsParam getThankYouAnalyticsParam(DailyBookingPaymentTypeView.PaymentType paymentType//
            , boolean fullBonus, boolean usedBonus, boolean registerEasyCard);
    }

    public StayPaymentPresenter(@NonNull StayPaymentActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayPaymentInterface createInstanceViewInterface()
    {
        return new StayPaymentView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayPaymentActivity activity)
    {
        setContentView(R.layout.activity_stay_payment_data);

        setAnalytics(new StayPaymentAnalyticsImpl());

        mPaymentRemoteImpl = new PaymentRemoteImpl(activity);
        mProfileRemoteImpl = new ProfileRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayPaymentAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mStayIndex = intent.getIntExtra(StayPaymentActivity.INTENT_EXTRA_DATA_STAY_INDEX, -1);
        mRoomIndex = intent.getIntExtra(StayPaymentActivity.INTENT_EXTRA_DATA_ROOM_INDEX, -1);

        if (mStayIndex == -1 || mRoomIndex == -1)
        {
            return false;
        }

        mStayName = intent.getStringExtra(StayPaymentActivity.INTENT_EXTRA_DATA_STAY_NAME);
        mImageUrl = intent.getStringExtra(StayPaymentActivity.INTENT_EXTRA_DATA_IMAGE_URL);
        mRoomPrice = intent.getIntExtra(StayPaymentActivity.INTENT_EXTRA_DATA_ROOM_PRICE, -1);

        String checkInDateTime = intent.getStringExtra(StayPaymentActivity.INTENT_EXTRA_DATA_CHECK_IN);
        String checkOutDateTime = intent.getStringExtra(StayPaymentActivity.INTENT_EXTRA_DATA_CHECK_OUT);

        setStayBookDateTime(checkInDateTime, checkOutDateTime);

        mOverseas = intent.getBooleanExtra(StayPaymentActivity.INTENT_EXTRA_DATA_OVERSEAS, false);
        mCategory = intent.getStringExtra(StayPaymentActivity.INTENT_EXTRA_DATA_CATEGORY);
        mRoomName = intent.getStringExtra(StayPaymentActivity.INTENT_EXTRA_DATA_ROOM_NAME);

        mAnalytics.setAnalyticsParam(intent.getParcelableExtra(BaseActivity.INTENT_EXTRA_DATA_ANALYTICS));

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

        mAnalytics.onScreen(getActivity(), mStayBookDateTime);

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
            case StayPaymentActivity.REQUEST_CODE_CARD_MANAGER:
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
                        setPaymentType(DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
                    }
                }

                selectEasyCard(cardList ->
                {
                    if (cardList.size() > 0)
                    {
                        setPaymentType(DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
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

            case StayPaymentActivity.REQUEST_CODE_REGISTER_CARD:
            case StayPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT:
            {
                // 간편 결제 실행후 카드가 없어 등록후에 돌아온경우.
                String msg = null;

                switch (resultCode)
                {
                    case Constants.CODE_RESULT_PAYMENT_BILLING_SUCCSESS:
                        if (requestCode == StayPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT)
                        {
                            selectEasyCard(cardList ->
                            {
                                unLockAll();

                                if (cardList.size() > 0)
                                {
                                    setPaymentType(DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
                                    setSelectCard(getSelectedCard(cardList));

                                    onPaymentClick(mGuest.name, mGuest.phone, mGuest.email);
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
                                    setPaymentType(DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
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

            case StayPaymentActivity.REQUEST_CODE_REGISTER_PHONE_NUMBER:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    String phoneMobile = data.getStringExtra(InputMobileNumberDialogActivity.INTENT_EXTRA_MOBILE_NUMBER);
                    setUserPhoneNumber(phoneMobile);
                    notifyGuestMobileInformationChanged();
                }
                break;

            case StayPaymentActivity.REQUEST_CODE_PAYMENT_WEB_CARD:
            case StayPaymentActivity.REQUEST_CODE_PAYMENT_WEB_PHONE:
                if (data != null)
                {
                    onPaymentWebResult(resultCode, data.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_PAYMENT_RESULT));
                }
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

        addCompositeDisposable(Observable.zip(mPaymentRemoteImpl.getStayPayment(mStayBookDateTime, mRoomIndex)//
            , mPaymentRemoteImpl.getEasyCardList(), mProfileRemoteImpl.getUserSimpleInformation()//
            , mPaymentRemoteImpl.getStayRefundPolicy(mStayBookDateTime, mStayIndex, mRoomIndex) //
            , new Function4<StayPayment, List<Card>, UserSimpleInformation, StayRefundPolicy, Boolean>()
            {
                @Override
                public Boolean apply(@io.reactivex.annotations.NonNull StayPayment stayPayment//
                    , @io.reactivex.annotations.NonNull List<Card> cardList//
                    , @io.reactivex.annotations.NonNull UserSimpleInformation userSimpleInformation//
                    , @io.reactivex.annotations.NonNull StayRefundPolicy stayRefundPolicy) throws Exception
                {
                    setStayPayment(stayPayment);
                    setSelectCard(getSelectedCard(cardList));
                    setUserInformation(userSimpleInformation);
                    setGuestInformation(userSimpleInformation);
                    setStayRefundPolicy(stayRefundPolicy);

                    return true;
                }
            }).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
            {
                onBookingInformation(mStayPayment, mStayBookDateTime);

                notifyUserInformationChanged();
                notifyGuestInformationChanged();
                notifyBonusEnabledChanged();
                notifyPaymentTypeChanged();
                notifyEasyCardChanged();
                notifyStayOutboundPaymentChanged();
                notifyRefundPolicyChanged();

                // 가격이 변동된 경우
                if (mRoomPrice != mStayPayment.totalPrice)
                {
                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_payment_changed_price)//
                        , getString(R.string.dialog_btn_text_confirm), null);
                } else if (mStayPayment.soldOut == true) // 솔드 아웃인 경우
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

                mAnalytics.onEventTransportationVisible(getActivity(), StayPayment.VISIT_TYPE_NONE.equalsIgnoreCase(mStayPayment.transportation) == false);

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

        startActivityForResult(CallDialogActivity.newInstance(getActivity()), StayPaymentActivity.REQUEST_CODE_CALL);
    }

    @Override
    public void onTransportationClick(Transportation transportation)
    {

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
            //호텔 가격이 xx 이하인 이벤트 호텔에서는 적립금 사용을 못하게 막음.
            if (mStayPayment.totalPrice <= MIN_AMOUNT_FOR_BONUS_USAGE)
            {
                setBonusSelected(false);
                setStayPaymentDiscountPrice(0);

                notifyBonusSelectedChanged();
                notifyStayOutboundPaymentChanged();

                String msg = getString(R.string.dialog_btn_payment_no_reserve, DailyTextUtils.getPriceFormat(getActivity(), MIN_AMOUNT_FOR_BONUS_USAGE, false));

                getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), msg, getString(R.string.dialog_btn_text_confirm), null);

                unLockAll();
                return;
            }

            int discountPrice = mGuest.bonus;

            if (discountPrice <= 0)
            {
                unLockAll();
                return;
            }

            setBonusSelected(true);
            notifyBonusSelectedChanged();

            if (discountPrice >= mStayPayment.totalPrice)
            {
                discountPrice = mStayPayment.totalPrice;
            }

            setStayPaymentDiscountPrice(mStayPayment.discountPrice);

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
                        setStayPaymentDiscountPrice(0);

                        notifyBonusSelectedChanged();
                        notifyStayOutboundPaymentChanged();
                    }
                }, null);
        }

        unLockAll();
    }

    @Override
    public void onEasyCardManagerClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(CreditCardListActivity.newInstance(getActivity()//
            , mSelectedCard.name, mSelectedCard.number, mSelectedCard.billKey, mSelectedCard.cd)//
            , StayPaymentActivity.REQUEST_CODE_CARD_MANAGER);
    }

    @Override
    public void onRegisterEasyCardClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(RegisterCreditCardActivity.newInstance(getActivity()), StayPaymentActivity.REQUEST_CODE_REGISTER_CARD);
    }

    @Override
    public void onPaymentTypeClick(DailyBookingPaymentTypeView.PaymentType paymentType)
    {
        if (paymentType == null)
        {
            return;
        }

        setPaymentType(paymentType);
        notifyPaymentTypeChanged();
    }

    @Override
    public synchronized void onPaymentClick(String name, String phone, String email)
    {
        if (mGuest == null || lock() == true)
        {
            return;
        }

        mGuest.name = name;
        mGuest.phone = phone;
        mGuest.email = email;

        if (DailyTextUtils.isTextEmpty(name) == true)
        {
            if (mOverseas == true)
            {
                DailyToast.showToast(getActivity(), R.string.toast_msg_please_input_guest_typeoverseas, DailyToast.LENGTH_SHORT);
            } else
            {
                DailyToast.showToast(getActivity(), R.string.toast_msg_please_input_guest, DailyToast.LENGTH_SHORT);
            }

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

        if (mAgreedThirdPartyTerms == false)
        {
            DailyToast.showToast(getActivity(), R.string.message_payment_please_agree_personal_information, DailyToast.LENGTH_SHORT);

            unLockAll();
            return;
        }

        // 보너스로만 결제하는 경우
        if (mBonusSelected == true && mStayPayment.totalPrice == mStayPayment.discountPrice)
        {
            // 보너스로만 결제할 경우에는 팝업이 기존의 카드 타입과 동일한다.
            getViewInterface().showAgreeTermDialog(DailyBookingPaymentTypeView.PaymentType.CARD, new View.OnClickListener()
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
            if (mPaymentType == DailyBookingPaymentTypeView.PaymentType.EASY_CARD && mSelectedCard == null)
            {
                startActivityForResult(RegisterCreditCardActivity.newInstance(getActivity())//
                    , StayPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT);

                mAnalytics.onEventStartPayment(getActivity(), AnalyticsManager.Label.EASYCARDPAY);
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
            , StayPaymentActivity.REQUEST_CODE_REGISTER_PHONE_NUMBER);
    }

    @Override
    public void onAgreedTermClick(boolean checked)
    {
        mAgreedThirdPartyTerms = checked;
    }

    private synchronized void onAgreedPaymentClick()
    {
        if (lock() == true)
        {
            return;
        }

        screenLock(true);

        // 입력된 내용을 저장한다.
        if (mOverseas == true)
        {
            DailyUserPreference.getInstance(getActivity()).setOverseasInformation(mGuest.name, mGuest.phone, mGuest.email);
        }

        String transportationType = mTransportationType == null ? null : mTransportationType.name();

        if (mBonusSelected == true && mStayPayment.totalPrice == mStayPayment.discountPrice)
        {
            addCompositeDisposable(mPaymentRemoteImpl.getStayPaymentTypeBonus(mStayBookDateTime, mRoomIndex//
                , mStayPayment.discountPrice, mCouponCode, mGuest, transportationType).subscribe(new Consumer<PaymentResult>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull PaymentResult paymentResult) throws Exception
                {
                    startThankYou(paymentResult.bookingIndex, true);
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

            mAnalytics.onEventStartPayment(getActivity(), AnalyticsManager.Label.FULLBONUS);
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
                            , StayPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT);
                        return;
                    }

                    addCompositeDisposable(mPaymentRemoteImpl.getStayPaymentTypeEasy(mStayBookDateTime, mRoomIndex//
                        , mStayPayment.discountPrice, mCouponCode, mGuest, transportationType, mSelectedCard.billKey).subscribe(new Consumer<PaymentResult>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull PaymentResult paymentResult) throws Exception
                        {
                            startThankYou(paymentResult.bookingIndex, false);
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

                    mAnalytics.onEventStartPayment(getActivity(), "EasyCardPay");
                    break;
                }

                case CARD:
                {
                    final String PAYMENT_TYPE = "credit";

                    JSONObject jsonObject = getPaymentJSONObject(mStayBookDateTime, mRoomIndex//
                        , mStayPayment.discountPrice, mCouponCode, mGuest, transportationType);

                    startActivityForResult(StayPaymentWebActivity.newInstance(getActivity(), mStayIndex, PAYMENT_TYPE, jsonObject.toString())//
                        , StayPaymentActivity.REQUEST_CODE_PAYMENT_WEB_CARD);

                    mAnalytics.onEventStartPayment(getActivity(), "CardPay");
                    break;
                }

                case PHONE:
                {
                    final String PAYMENT_TYPE = "mobile";

                    JSONObject jsonObject = getPaymentJSONObject(mStayBookDateTime, mRoomIndex//
                        , mStayPayment.discountPrice, mCouponCode, mGuest, transportationType);

                    startActivityForResult(StayPaymentWebActivity.newInstance(getActivity(), mStayIndex, PAYMENT_TYPE, jsonObject.toString())//
                        , StayPaymentActivity.REQUEST_CODE_PAYMENT_WEB_PHONE);

                    mAnalytics.onEventStartPayment(getActivity(), "PhoneBillPay");
                    break;
                }

                case VBANK:
                {
                    final String PAYMENT_TYPE = "vbank";

                    JSONObject jsonObject = getPaymentJSONObject(mStayBookDateTime, mRoomIndex//
                        , mStayPayment.discountPrice, mCouponCode, mGuest, transportationType);

                    startActivityForResult(StayPaymentWebActivity.newInstance(getActivity(), mStayIndex, PAYMENT_TYPE, jsonObject.toString())//
                        , StayPaymentActivity.REQUEST_CODE_PAYMENT_WEB_VBANK);

                    mAnalytics.onEventStartPayment(getActivity(), "VirtualAccountPay");
                    break;
                }
            }
        }
    }

    private void startThankYou(int bookingIndex, boolean fullBonus)
    {
        startActivityForResult(StayThankYouActivity.newInstance(getActivity(), mOverseas, mCategory, mStayName, mImageUrl//
            , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mRoomName, bookingIndex, mAnalytics.getThankYouAnalyticsParam(mPaymentType, true, mBonusSelected, mSelectedCard != null))//
            , StayPaymentActivity.REQUEST_CODE_THANK_YOU);

        mAnalytics.onScreenPaymentCompleted(getActivity(), mStayPayment, mStayBookDateTime//
            , mStayIndex, mStayName, mPaymentType, true, mSelectedCard != null, mUserSimpleInformation);
        mAnalytics.onEventEndPayment(getActivity());
    }

    private JSONObject getPaymentJSONObject(StayBookDateTime stayBookDateTime, int roomIndex//
        , int bonusAmount, String couponCode, DomesticGuest guest, String transportation)
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("bonusAmount", bonusAmount);
            jsonObject.put("checkInDate", stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            jsonObject.put("days", stayBookDateTime.getNights());
            jsonObject.put("couponCode", couponCode);
            jsonObject.put("roomIdx", roomIndex);

            JSONObject bookingGuestJSONObject = new JSONObject();
            bookingGuestJSONObject.put("arrivalDateTime", stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
            bookingGuestJSONObject.put("arrivalType", transportation);
            bookingGuestJSONObject.put("email", guest.email);
            bookingGuestJSONObject.put("name", guest.name);
            bookingGuestJSONObject.put("phone", guest.phone);

            jsonObject.put("bookingGuest", bookingGuestJSONObject);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            jsonObject = null;
        }

        return jsonObject;
    }

    private void onBookingInformation(StayPayment stayPayment, StayBookDateTime stayBookDateTime)
    {
        if (stayPayment == null || stayBookDateTime == null)
        {
            return;
        }

        final String DATE_FORMAT = "yyyy.MM.dd(EEE)";
        final String TIME_FORMAT = "HH:mm";

        try
        {
            String checkInTime = stayBookDateTime.getCheckInDateTime(TIME_FORMAT);
            String checkInDate = stayBookDateTime.getCheckInDateTime(DATE_FORMAT);

            SpannableString checkInDateSpannableString = new SpannableString(checkInDate + " " + checkInTime);
            checkInDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getMediumTypeface()),//
                checkInDate.length(), checkInDate.length() + checkInTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String checkOutTime = stayBookDateTime.getCheckOutDateTime(TIME_FORMAT);
            String checkOutDate = stayBookDateTime.getCheckOutDateTime(DATE_FORMAT);

            SpannableString checkOutDateSpannableString = new SpannableString(checkOutDate + " " + checkOutTime);
            checkOutDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getMediumTypeface()),//
                checkOutDate.length(), checkOutDate.length() + checkOutTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            getViewInterface().setBooking(checkInDateSpannableString, checkOutDateSpannableString, mStayBookDateTime.getNights(), mStayName, mRoomName);
            getViewInterface().setVendorName(stayPayment.businessName);
            getViewInterface().setTransportation(stayPayment.transportation);
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
        if (mGuest == null || mStayPayment == null || mStayBookDateTime == null)
        {
            return;
        }

        try
        {
            getViewInterface().setStayPayment(mGuest.bonus, mStayBookDateTime.getNights()//
                , mStayPayment.totalPrice, mStayPayment.discountPrice);

            // 1000원 미만 결제시에 간편/일반 결제 불가 - 쿠폰 또는 적립금 전체 사용이 아닌경우 조건 추가
            int paymentPrice = mStayPayment.totalPrice - (mBonusSelected == true ? mGuest.bonus : 0);

            final int CARD_MIN_PRICE = 1000;
            final int PHONE_MAX_PRICE = 500000;

            if (paymentPrice > 0 && paymentPrice < CARD_MIN_PRICE)
            {
                getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.EASY_CARD, false);
                getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.CARD, false);

                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundPhonePaymentEnabled() == true)
                {
                    getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.PHONE, true);
                }
            } else if (paymentPrice > PHONE_MAX_PRICE)
            {
                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundSimpleCardPaymentEnabled() == true)
                {
                    getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.EASY_CARD, true);
                }

                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundCardPaymentEnabled() == true)
                {
                    getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.CARD, true);
                }

                getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.PHONE, false);
            } else if (paymentPrice > 0)
            {
                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundSimpleCardPaymentEnabled() == true)
                {
                    getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.EASY_CARD, true);
                }

                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundCardPaymentEnabled() == true)
                {
                    getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.CARD, true);
                }

                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundPhonePaymentEnabled() == true)
                {
                    getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.PHONE, true);
                }
            } else if (paymentPrice == 0)
            {
                getViewInterface().setPaymentType(DailyBookingPaymentTypeView.PaymentType.FREE);
            }

        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void setStayPayment(StayPayment stayPayment)
    {
        mStayPayment = stayPayment;
    }

    private void setStayPaymentDiscountPrice(int discountPrice)
    {
        if (mStayPayment == null)
        {
            return;
        }

        mStayPayment.discountPrice = discountPrice;
    }

    private void setUserInformation(UserSimpleInformation userSimpleInformation)
    {
        mUserSimpleInformation = userSimpleInformation;
    }

    private void setGuestInformation(UserSimpleInformation userSimpleInformation)
    {
        if (userSimpleInformation == null)
        {
            return;
        }

        if (mOverseas == true)
        {
            if (mGuest == null)
            {
                mGuest = getOverseasGustInformation();

                if (DailyTextUtils.isTextEmpty(mGuest.phone) == true)
                {
                    mGuest.phone = userSimpleInformation.phone;
                }

                if (DailyTextUtils.isTextEmpty(mGuest.email) == true)
                {
                    mGuest.email = userSimpleInformation.email;
                }
            }
        } else
        {
            if (mGuest == null)
            {
                mGuest = new DomesticGuest();

                mGuest.name = userSimpleInformation.name;
                mGuest.phone = userSimpleInformation.phone;
                mGuest.email = userSimpleInformation.email;
            }
        }

        mGuest.bonus = userSimpleInformation.bonus;
    }

    private void setStayRefundPolicy(StayRefundPolicy stayRefundPolicy)
    {
        mStayRefundPolicy = stayRefundPolicy;
    }

    private DomesticGuest getOverseasGustInformation()
    {
        DomesticGuest guest = new DomesticGuest();

        guest.name = DailyUserPreference.getInstance(getActivity()).getOverseasFirstName();
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

    private void setPaymentType(DailyBookingPaymentTypeView.PaymentType paymentType)
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

    private void setCouponSelected(boolean selected)
    {
        mCouponSelected = selected;
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

    private void notifyUserInformationChanged()
    {
        if (mUserSimpleInformation == null)
        {
            return;
        }

        if (mOverseas == true)
        {
            getViewInterface().setUserInformationVisible(false);
        } else
        {
            getViewInterface().setUserInformationVisible(true);
            getViewInterface().setUserInformation(mUserSimpleInformation.name, mUserSimpleInformation.phone, mUserSimpleInformation.email);
        }
    }

    private void notifyGuestInformationChanged()
    {
        if (mGuest == null)
        {
            return;
        }

        getViewInterface().setGuestInformation(mGuest.name, mGuest.phone, mGuest.email, mOverseas);
    }

    private void notifyGuestMobileInformationChanged()
    {
        if (mGuest == null)
        {
            return;
        }

        String phone = DailyUserPreference.getInstance(getActivity()).getOverseasPhone();

        getViewInterface().setGuestMobileInformation(mGuest.phone);
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
            if (mGuest == null)
            {
                getViewInterface().setBonusEnabled(false);
            } else
            {
                if (mStayPayment.totalPrice <= MIN_AMOUNT_FOR_BONUS_USAGE)
                {
                    getViewInterface().setBonusGuideText(getString(R.string.dialog_btn_payment_no_reserve//
                        , DailyTextUtils.getPriceFormat(getActivity(), MIN_AMOUNT_FOR_BONUS_USAGE, false)));
                    getViewInterface().setBonusEnabled(false);
                } else
                {
                    getViewInterface().setBonusEnabled(mGuest.bonus > 0);
                }
            }
        }
    }

    private void notifyBonusSelectedChanged()
    {
        getViewInterface().setBonusSelected(mBonusSelected);
    }

    private void notifyRefundPolicyChanged()
    {
        if (mStayRefundPolicy == null)
        {
            return;
        }

        if (StayRefundPolicy.STATUS_NONE.equalsIgnoreCase(mStayRefundPolicy.refundPolicy) == true)
        {
            getViewInterface().setRefundPolicy(null);
        } else
        {
            getViewInterface().setRefundPolicy(mStayRefundPolicy.comment);
        }
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

            getViewInterface().setGuidePaymentType(getString(R.string.message_dont_support_payment_type, guideMemo.toString()));
        } else
        {
            getViewInterface().setGuidePaymentType(null);
        }

        getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.EASY_CARD, isSimpleCardPaymentEnabled);
        getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.CARD, isCardPaymentEnabled);
        getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.PHONE, isPhonePaymentEnabled);

        if (isSimpleCardPaymentEnabled == true)
        {
            setPaymentType(DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
        } else if (isCardPaymentEnabled == true)
        {
            setPaymentType(DailyBookingPaymentTypeView.PaymentType.CARD);
        } else if (isPhonePaymentEnabled == true)
        {
            setPaymentType(DailyBookingPaymentTypeView.PaymentType.PHONE);
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
                    startThankYou(paymentResult.bookingIndex, false);
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
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
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
            unLockAll();

            String title = getString(R.string.dialog_title_payment);
            String message;

            int msgCode;
            View.OnClickListener confirmListener = null;

            try
            {
                JSONObject jsonObject = new JSONObject(result);
                msgCode = jsonObject.getInt("msgCode");

                // 다날 핸드폰 화면에서 취소 버튼 누르는 경우
                if (msgCode == -104)
                {
                    message = getString(R.string.act_toast_payment_canceled);
                } else
                {
                    message = jsonObject.getString("msg");

                    confirmListener = new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            setResult(BaseActivity.RESULT_CODE_REFRESH);
                            onBackClick();
                        }
                    };
                }
            } catch (Exception e)
            {
                msgCode = -1;
                message = getString(R.string.act_toast_payment_fail);

                confirmListener = new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        setResult(BaseActivity.RESULT_CODE_REFRESH);
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

package com.daily.dailyhotel.screen.home.stay.inbound.payment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Pair;
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
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.Coupons;
import com.daily.dailyhotel.entity.DomesticGuest;
import com.daily.dailyhotel.entity.PaymentResult;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayPayment;
import com.daily.dailyhotel.entity.StayRefundPolicy;
import com.daily.dailyhotel.entity.UserSimpleInformation;
import com.daily.dailyhotel.parcel.CouponParcel;
import com.daily.dailyhotel.parcel.analytics.StayPaymentAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayThankYouAnalyticsParam;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.CouponRemoteImpl;
import com.daily.dailyhotel.repository.remote.PaymentRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.payment.PaymentWebActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.thankyou.StayThankYouActivity;
import com.daily.dailyhotel.screen.mydaily.coupon.dialog.SelectStayCouponDialogActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.screen.mydaily.creditcard.CreditCardListActivity;
import com.twoheart.dailyhotel.screen.mydaily.creditcard.RegisterCreditCardActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.InputMobileNumberDialogActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayPaymentPresenter extends BaseExceptionPresenter<StayPaymentActivity, StayPaymentInterface> implements StayPaymentView.OnEventListener
{
    private static final int MIN_AMOUNT_FOR_BONUS_USAGE = 20000; // 보너스를 사용하기 위한 최소 주문 가격

    // 1000원 미만 결제시에 간편/일반 결제 불가 - 쿠폰 또는 적립금 전체 사용이 아닌경우 조건 추가
    private static final int CARD_MIN_PRICE = 1000;
    private static final int PHONE_MAX_PRICE = 500000;

    // 서버로 해당 문자열 그대로 보냄.(수정 금지)
    static final String UNKNOWN = "UNKNOWN";
    static final String WALKING = "WALKING";
    static final String CAR = "CAR";

    // 서버로 해당 문자열 그대로 보냄.(수정 금지)
    @StringDef({UNKNOWN, WALKING, CAR})
    @interface Transportation
    {
    }

    static final int NONE = 0;
    static final int BONUS = 1;
    static final int COUPON = 2;
    static final int STICKER = 3;

    // 서버로 해당 문자열 그대로 보냄.(수정 금지)
    @IntDef({NONE, BONUS, COUPON, STICKER})
    @interface SaleType
    {
    }

    StayPaymentAnalyticsInterface mAnalytics;

    PaymentRemoteImpl mPaymentRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;
    private CouponRemoteImpl mCouponRemoteImpl;

    StayBookDateTime mStayBookDateTime;
    CommonDateTime mCommonDateTime;
    int mStayIndex, mRoomPrice, mRoomIndex;
    String mStayName, mImageUrl, mCategory, mRoomName;
    private double mLatitude, mLongitude;
    StayPayment mStayPayment;
    StayRefundPolicy mStayRefundPolicy;
    Card mSelectedCard;
    private DomesticGuest mGuest;
    Coupon mSelectedCoupon;
    private String mTransportationType;
    private DailyBookingPaymentTypeView.PaymentType mPaymentType;
    boolean mNRD, mAgreedThirdPartyTerms;
    private boolean mGuestInformationVisible;
    UserSimpleInformation mUserSimpleInformation;
    private int mWaitingForBookingMessageType;
    private int mSaleType;
    private int mMaxCouponAmount;
    boolean mCheckChangedPrice;
    boolean mNeedOverwritePrice;

    // ***************************************************************** //
    // ************** 변수 선언시에 onSaveInstanceState 에 꼭 등록해야하는지 판단한다.
    // ************** 클래스는 해당 내부 멤버 변수들이 onSaveInstance에 잘처리되고 있는지 확인한다.
    // ***************************************************************** //


    public interface StayPaymentAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(StayPaymentAnalyticsParam analyticsParam);

        StayPaymentAnalyticsParam getAnalyticsParam();

        void onScreen(Activity activity, String refundPolicy, StayBookDateTime stayBookDateTime, int stayIndex, String stayName//
            , int roomIndex, String roomName, String category, StayPayment stayPayment, boolean registerEasyCard);

        void onScreenAgreeTermDialog(Activity activity, StayBookDateTime stayBookDateTime//
            , int stayIndex, String stayName, int roomIndex, String roomName, String category//
            , StayPayment stayPayment, boolean registerEasyCard, int saleType, Coupon coupon//
            , DailyBookingPaymentTypeView.PaymentType paymentType, UserSimpleInformation userSimpleInformation);

        void onScreenPaymentCompleted(Activity activity, String aggregationId, boolean isOverseas);

        void onEventTransportationVisible(Activity activity, boolean visible);

        void onEventChangedPrice(Activity activity, String stayName);

        void onEventSoldOut(Activity activity, String stayName);

        void onEventBonusClick(Activity activity, boolean selected, int bonus);

        void onEventCouponClick(Activity activity, boolean selected);

        void onEventCallClick(Activity activity);

        void onEventCall(Activity activity, boolean call);

        void onEventAgreedThirdPartyClick(Activity activity);

        void onEventTransportationType(Activity activity, String transportation, String type);

        void onEventEasyCardManagerClick(Activity activity, boolean hasEasyCard);

        void onEventAgreedTermCancelClick(Activity activity);

        void onEventStartPayment(Activity activity, DailyBookingPaymentTypeView.PaymentType paymentType);

        void onEventAgreedTermClick(Activity activity, String stayName, String roomName);

        StayThankYouAnalyticsParam getThankYouAnalyticsParam();

        void setPaymentParam(HashMap<String, String> param);

        HashMap<String, String> getPaymentParam();
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

        mAnalytics = new StayPaymentAnalyticsImpl();

        mPaymentRemoteImpl = new PaymentRemoteImpl();
        mProfileRemoteImpl = new ProfileRemoteImpl();
        mCommonRemoteImpl = new CommonRemoteImpl();
        mCouponRemoteImpl = new CouponRemoteImpl();

        setRefresh(true);
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

        String checkInDateTime = intent.getStringExtra(StayPaymentActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
        String checkOutDateTime = intent.getStringExtra(StayPaymentActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

        setStayBookDateTime(checkInDateTime, checkOutDateTime);

        mNRD = intent.getBooleanExtra(StayPaymentActivity.INTENT_EXTRA_DATA_NRD, false);
        mCategory = intent.getStringExtra(StayPaymentActivity.INTENT_EXTRA_DATA_CATEGORY);
        mRoomName = intent.getStringExtra(StayPaymentActivity.INTENT_EXTRA_DATA_ROOM_NAME);

        mLatitude = intent.getDoubleExtra(StayPaymentActivity.INTENT_EXTRA_DATA_LATITUDE, 0d);
        mLongitude = intent.getDoubleExtra(StayPaymentActivity.INTENT_EXTRA_DATA_LONGITUDE, 0d);

        mAnalytics.setAnalyticsParam(intent.getParcelableExtra(BaseActivity.INTENT_EXTRA_DATA_ANALYTICS));

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.actionbar_title_payment_activity));

        // 리모트 컨피그에 있는 결제 타입
        checkAvailablePaymentType();

        setSaleType(NONE);
        mSelectedCoupon = null;

        getViewInterface().setDepositStickerVisible(false);
        getViewInterface().setOverseas(false);
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

        outState.putInt("stayIndex", mStayIndex);
        outState.putInt("roomPrice", mRoomPrice);
        outState.putInt("roomIndex", mRoomIndex);
        outState.putInt("waitingForBookingMessageType", mWaitingForBookingMessageType);

        outState.putString("stayName", mStayName);
        outState.putString("imageUrl", mImageUrl);
        outState.putString("category", mCategory);
        outState.putString("roomName", mRoomName);
        outState.putString("transportationType", mTransportationType);

        if (mStayBookDateTime != null)
        {
            outState.putString("checkInDateTime", mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
            outState.putString("checkOutDateTime", mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
        }

        if (mPaymentType != null)
        {
            outState.putString("paymentType", mPaymentType.name());
        }

        outState.putBoolean("nrd", mNRD);
        outState.putInt("saleType", mSaleType);
        outState.putBoolean("agreedThirdPartyTerms", mAgreedThirdPartyTerms);
        outState.putBoolean("guestInformationVisible", mGuestInformationVisible);

        outState.putParcelable("selectedCoupon", mSelectedCoupon);

        if (mAnalytics != null)
        {
            outState.putParcelable("analytics", mAnalytics.getAnalyticsParam());
            outState.putSerializable("analyticsPaymentParam", mAnalytics.getPaymentParam());
        }

        try
        {
            outState.putBundle("stayPayment", Util.getClassPublicFieldsBundle(StayPayment.class, mStayPayment));
            outState.putBundle("stayRefundPolicy", Util.getClassPublicFieldsBundle(StayRefundPolicy.class, mStayRefundPolicy));
            outState.putBundle("selectedCard", Util.getClassPublicFieldsBundle(Card.class, mSelectedCard));
            outState.putBundle("guest", Util.getClassPublicFieldsBundle(DomesticGuest.class, mGuest));
            outState.putBundle("userSimpleInformation", Util.getClassPublicFieldsBundle(UserSimpleInformation.class, mUserSimpleInformation));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        mStayIndex = savedInstanceState.getInt("stayIndex");
        mRoomPrice = savedInstanceState.getInt("roomPrice");
        mRoomIndex = savedInstanceState.getInt("roomIndex");
        mWaitingForBookingMessageType = savedInstanceState.getInt("waitingForBookingMessageType");

        mStayName = savedInstanceState.getString("stayName");
        mImageUrl = savedInstanceState.getString("imageUrl");
        mCategory = savedInstanceState.getString("category");
        mRoomName = savedInstanceState.getString("roomName");
        mTransportationType = savedInstanceState.getString("transportationType");

        setStayBookDateTime(savedInstanceState.getString("checkInDateTime"), savedInstanceState.getString("checkOutDateTime"));

        try
        {
            mPaymentType = DailyBookingPaymentTypeView.PaymentType.valueOf(savedInstanceState.getString("paymentType"));
        } catch (Exception e)
        {
            mPaymentType = DailyBookingPaymentTypeView.PaymentType.EASY_CARD;
        }

        mNRD = savedInstanceState.getBoolean("nrd");
        mSaleType = savedInstanceState.getInt("saleType", NONE);
        mAgreedThirdPartyTerms = savedInstanceState.getBoolean("agreedThirdPartyTerms");
        mGuestInformationVisible = savedInstanceState.getBoolean("guestInformationVisible");

        mSelectedCoupon = savedInstanceState.getParcelable("selectedCoupon");

        if (mAnalytics != null)
        {
            mAnalytics.setAnalyticsParam(savedInstanceState.getParcelable("analytics"));
            mAnalytics.setPaymentParam((HashMap<String, String>) savedInstanceState.getSerializable("analyticsPaymentParam"));
        }

        try
        {
            mStayPayment = (StayPayment) Util.setClassPublicFieldsBundle(StayPayment.class, savedInstanceState.getBundle("stayPayment"));
            mStayRefundPolicy = (StayRefundPolicy) Util.setClassPublicFieldsBundle(StayRefundPolicy.class, savedInstanceState.getBundle("stayRefundPolicy"));
            mSelectedCard = (Card) Util.setClassPublicFieldsBundle(Card.class, savedInstanceState.getBundle("selectedCard"));
            mGuest = (DomesticGuest) Util.setClassPublicFieldsBundle(DomesticGuest.class, savedInstanceState.getBundle("guest"));
            mUserSimpleInformation = (UserSimpleInformation) Util.setClassPublicFieldsBundle(UserSimpleInformation.class, savedInstanceState.getBundle("userSimpleInformation"));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
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

                selectEasyCard(cardList -> {
                    unLockAll();

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
                            selectEasyCard(cardList -> {
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
                            selectEasyCard(cardList -> {
                                unLockAll();

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
                    String mobile = data.getStringExtra(InputMobileNumberDialogActivity.INTENT_EXTRA_MOBILE_NUMBER);
                    notifyGuestMobileInformationChanged(mobile);
                }
                break;

            case StayPaymentActivity.REQUEST_CODE_PAYMENT_WEB_CARD:
            case StayPaymentActivity.REQUEST_CODE_PAYMENT_WEB_PHONE:
            case StayPaymentActivity.REQUEST_CODE_PAYMENT_WEB_VBANK:
                onPaymentWebResult(mPaymentType, resultCode, data);
                break;

            case StayPaymentActivity.REQUEST_CODE_COUPON_LIST:
                if (data != null)
                {
                    int maxCouponAmount = data.getIntExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_MAX_COUPON_AMOUNT, 0);
                    setMaxCouponAmount(maxCouponAmount, false);

                    if (resultCode == Activity.RESULT_OK)
                    {
                        CouponParcel couponParcel = data.getParcelableExtra(SelectStayCouponDialogActivity.INTENT_EXTRA_SELECT_COUPON);
                        Coupon coupon = new Coupon(couponParcel.getCoupon());
                        setCoupon(coupon);
                    } else
                    {
                        setCoupon(null);
                    }
                } else
                {
                    setMaxCouponAmount(mMaxCouponAmount, false);
                    setCoupon(null);
                }
                break;

            case StayPaymentActivity.REQUEST_CODE_CALL:
                mAnalytics.onEventCall(getActivity(), resultCode == Activity.RESULT_OK);
                break;
        }
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        addCompositeDisposable(Observable.zip(mPaymentRemoteImpl.getStayPayment(mStayBookDateTime, mRoomIndex)//
            , mPaymentRemoteImpl.getEasyCardList(), mProfileRemoteImpl.getUserSimpleInformation()//
            , mCommonRemoteImpl.getCommonDateTime()//
            , new Function4<StayPayment, List<Card>, UserSimpleInformation, CommonDateTime, StayBookDateTime>()
            {
                @Override
                public StayBookDateTime apply(@io.reactivex.annotations.NonNull StayPayment stayPayment//
                    , @io.reactivex.annotations.NonNull List<Card> cardList//
                    , @io.reactivex.annotations.NonNull UserSimpleInformation userSimpleInformation//
                    , @io.reactivex.annotations.NonNull CommonDateTime commonDateTime) throws Exception
                {
                    setStayPayment(stayPayment);
                    setCommonDateTime(commonDateTime);
                    setStayBookDateTime(stayPayment.checkInDate, stayPayment.checkOutDate);
                    setSelectCard(getSelectedCard(cardList));
                    setUserInformation(userSimpleInformation);
                    setWaitingPopupMessageType(commonDateTime, mStayBookDateTime);

                    return mStayBookDateTime;
                }
            }).flatMap(new Function<StayBookDateTime, Observable<StayRefundPolicy>>()
        {
            public Observable<StayRefundPolicy> apply(@io.reactivex.annotations.NonNull StayBookDateTime stayBookDateTime) throws Exception
            {
                return mPaymentRemoteImpl.getStayRefundPolicy(stayBookDateTime, mStayIndex, mRoomIndex);
            }
        }).subscribe(new Consumer<StayRefundPolicy>()
        {
            @Override
            public void accept(StayRefundPolicy stayRefundPolicy) throws Exception
            {
                if (mNeedOverwritePrice == true)
                {
                    mNeedOverwritePrice = false;
                    mRoomPrice = mStayPayment.totalPrice;
                    getViewInterface().scrollToCheckPriceTitle();
                    setResult(BaseActivity.RESULT_CODE_REFRESH);
                }

                getMaxCouponAmount(mStayIndex, mRoomIndex, mStayBookDateTime);

                setStayRefundPolicy(stayRefundPolicy);

                onBookingInformation(mStayPayment, mStayBookDateTime);
                onRewardStickerInformation(mStayPayment, mStayBookDateTime);

                notifyUserInformationChanged();

                //                if (mOverseas == true)
                //                {
                //                    notifyGuestInformationChanged(getOverseasGustInformation(mUserSimpleInformation));
                //                }

                notifyCardEventChanged(mCommonDateTime);
                notifyBonusEnabledChanged();
                notifyPaymentTypeChanged();
                notifyEasyCardChanged();
                notifyStayPaymentChanged();

                // 위의 리워드 스티커 여부와 정책 여부에 따라서 순서 및 단어가 바뀐다.
                notifyRefundPolicyChanged();

                if (mStayPayment.soldOut == true) // 솔드 아웃인 경우
                {
                    setResult(BaseActivity.RESULT_CODE_REFRESH);

                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_stay_stop_onsale)//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                                onBackClick();
                            }
                        });

                    mAnalytics.onEventSoldOut(getActivity(), mStayName);
                } else if ((mCheckChangedPrice == false && mRoomPrice != mStayPayment.totalPrice)//
                    && (mNRD == false && StayRefundPolicy.STATUS_NRD.equalsIgnoreCase(stayRefundPolicy.refundPolicy)))
                {
                    mCheckChangedPrice = true;
                    setResult(BaseActivity.RESULT_CODE_REFRESH);

                    // 가격이 변동되고 nrd가 되어버린 경우
                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_payment_changed_price_nrd)//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface)
                            {
                                onBackClick();
                            }
                        });

                    mAnalytics.onEventChangedPrice(getActivity(), mStayName);
                } else if (mCheckChangedPrice == false && mRoomPrice != mStayPayment.totalPrice)
                {
                    mCheckChangedPrice = true;
                    setResult(BaseActivity.RESULT_CODE_REFRESH);

                    // 가격이 변동된 경우
                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_payment_changed_price)//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface)
                            {
                                onBackClick();
                            }
                        });

                    mAnalytics.onEventChangedPrice(getActivity(), mStayName);
                } else if (mNRD == false && StayRefundPolicy.STATUS_NRD.equalsIgnoreCase(stayRefundPolicy.refundPolicy))
                {
                    setResult(BaseActivity.RESULT_CODE_REFRESH);

                    // 무료에서 NRD로 변경된 경우
                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_payment_changed_nrd)//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface)
                            {
                                onBackClick();
                            }
                        });
                } else if (DailyTextUtils.isTextEmpty(mStayPayment.mWarningMessage) == false)
                {
                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), mStayPayment.mWarningMessage//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                            }
                        });
                }

                mAnalytics.onScreen(getActivity(), mStayRefundPolicy.refundPolicy, mStayBookDateTime//
                    , mStayIndex, mStayName, mRoomIndex, mRoomName, mCategory//
                    , mStayPayment, mSelectedCard != null);

                mAnalytics.onEventTransportationVisible(getActivity(), StayPayment.VISIT_TYPE_NONE.equalsIgnoreCase(mStayPayment.transportation) == false);

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                unLockAll();

                onReportError(throwable);

                if (throwable instanceof BaseException)
                {
                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), throwable.getMessage()//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                                setResult(BaseActivity.RESULT_CODE_REFRESH);
                                onBackClick();
                            }
                        });
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

        mAnalytics.onEventCallClick(getActivity());
    }

    @Override
    public void onTransportationClick(@Transportation String transportation)
    {
        if (lock() == true)
        {
            return;
        }

        mTransportationType = transportation;

        getViewInterface().setTransportationType(transportation);

        unLockAll();
    }

    @Override
    public void onBonusClick(boolean selected)
    {
        if (mStayBookDateTime == null || lock() == true)
        {
            return;
        }

        switch (mSaleType)
        {
            case COUPON:
                getViewInterface().showSimpleDialog(null, getString(R.string.message_booking_cancel_coupon), getString(R.string.dialog_btn_text_yes), //
                    getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mSelectedCoupon = null;

                            setSaleType(BONUS);
                            onBonusClick(true);
                        }
                    }, null);
                break;

            case STICKER:
                getViewInterface().showSimpleDialog(null, getString(R.string.message_booking_cancel_deposit_sticker), getString(R.string.dialog_btn_text_yes), //
                    getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            setSaleType(BONUS);
                            onBonusClick(true);
                        }
                    }, null);
                break;

            default:
                if (selected == true)
                {
                    setSaleType(BONUS);

                    notifyStayPaymentChanged();

                    mAnalytics.onEventBonusClick(getActivity(), true, mUserSimpleInformation.bonus);
                } else
                {
                    // 적립금 삭제
                    getViewInterface().showSimpleDialog(null, getString(R.string.message_booking_cancel_bonus), getString(R.string.dialog_btn_text_yes), //
                        getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                setSaleType(hasDepositSticker() ? STICKER : NONE);

                                notifyStayPaymentChanged();

                                mAnalytics.onEventBonusClick(getActivity(), false, mUserSimpleInformation.bonus);
                            }
                        }, null);
                }
                break;
        }

        unLockAll();
    }

    @Override
    public void onCouponClick(boolean selected)
    {
        if (mStayBookDateTime == null || lock() == true)
        {
            return;
        }

        switch (mSaleType)
        {
            case BONUS:
                getViewInterface().showSimpleDialog(null, getString(R.string.message_booking_cancel_bonus), getString(R.string.dialog_btn_text_yes), //
                    getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            setSaleType(COUPON);
                            onCouponClick(true);
                        }
                    }, null);
                break;

            case STICKER:
                getViewInterface().showSimpleDialog(null, getString(R.string.message_booking_cancel_deposit_sticker), getString(R.string.dialog_btn_text_yes), //
                    getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            setSaleType(COUPON);
                            onCouponClick(true);
                        }
                    }, null);
                break;

            default:
                if (selected == true)
                {
                    Intent intent = SelectStayCouponDialogActivity.newInstance(getActivity(), mStayIndex, //
                        mRoomIndex, mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , mCategory, mStayName, mStayPayment.totalPrice);
                    startActivityForResult(intent, StayPaymentActivity.REQUEST_CODE_COUPON_LIST);

                    mAnalytics.onEventCouponClick(getActivity(), true);
                } else
                {
                    getViewInterface().showSimpleDialog(null, getString(R.string.message_booking_cancel_coupon), getString(R.string.dialog_btn_text_yes), //
                        getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                setSaleType(hasDepositSticker() ? STICKER : NONE);

                                notifyStayPaymentChanged();
                            }
                        }, null);
                }
                break;
        }

        unLockAll();
    }

    @Override
    public void onDepositStickerClick(boolean selected)
    {
        if (mStayBookDateTime == null || lock() == true)
        {
            return;
        }

        switch (mSaleType)
        {
            case BONUS:
                getViewInterface().showSimpleDialog(null, getString(R.string.message_booking_cancel_bonus), getString(R.string.dialog_btn_text_yes), //
                    getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            setSaleType(STICKER);
                            onDepositStickerClick(true);
                        }
                    }, null);
                break;

            case COUPON:
                getViewInterface().showSimpleDialog(null, getString(R.string.message_booking_cancel_coupon), getString(R.string.dialog_btn_text_yes), //
                    getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mSelectedCoupon = null;

                            setSaleType(STICKER);
                            onDepositStickerClick(true);
                        }
                    }, null);
                break;

            default:
                if (selected == true)
                {
                    setSaleType(STICKER);
                    notifyStayPaymentChanged();
                } else
                {
                    if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayPhonePaymentEnabled() == false)
                    {
                        getViewInterface().showSimpleDialog(null, getString(R.string.message_payment_none_payment_phone_type), getString(R.string.dialog_btn_text_confirm), null);//
                    } else
                    {
                        getViewInterface().showSimpleDialog(null, getString(R.string.message_booking_cancel_deposit_sticker_by_payment_phone), getString(R.string.dialog_btn_text_yes), //
                            getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    setSaleType(NONE);
                                    getViewInterface().setDepositSticker(false);
                                    notifyStayPaymentChanged();
                                }
                            }, null);
                    }
                }
                break;
        }

        unLockAll();
    }

    @Override
    public void onChangedGuestClick(boolean visible)
    {
        mGuestInformationVisible = visible;

        getViewInterface().setGuestInformationVisible(visible);
    }

    @Override
    public void onEasyCardManagerClick()
    {
        if (mSelectedCard == null || lock() == true)
        {
            return;
        }

        startActivityForResult(CreditCardListActivity.newInstance(getActivity()//
            , mSelectedCard.name, mSelectedCard.number, mSelectedCard.billKey, mSelectedCard.cd)//
            , StayPaymentActivity.REQUEST_CODE_CARD_MANAGER);

        mAnalytics.onEventEasyCardManagerClick(getActivity(), mSelectedCard != null);
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
        if (lock() == true)
        {
            return;
        }

        if (mGuest == null)
        {
            mGuest = new DomesticGuest();
        }

        // 투숙자와 예약자가 정보가 다른 경우
        if (mGuestInformationVisible == true)
        {
            mGuest.name = name;
            mGuest.phone = phone;
            mGuest.email = email;
        } else
        {
            mGuest.name = mUserSimpleInformation.name;
            mGuest.phone = mUserSimpleInformation.phone;
            mGuest.email = mUserSimpleInformation.email;
        }

        if (DailyTextUtils.isTextEmpty(mGuest.name) == true)
        {
            //            if (mOverseas == true)
            //            {
            //                DailyToast.showToast(getActivity(), R.string.toast_msg_please_input_guest_typeoverseas, DailyToast.LENGTH_SHORT);
            //            } else
            //            {
            DailyToast.showToast(getActivity(), R.string.toast_msg_please_input_guest, DailyToast.LENGTH_SHORT);
            //            }

            unLockAll();
            return;
        }

        if (DailyTextUtils.isTextEmpty(mGuest.phone) == true)
        {
            DailyToast.showToast(getActivity(), getString(R.string.toast_msg_please_input_contact), DailyToast.LENGTH_SHORT);

            unLockAll();
            return;
        }

        if (DailyTextUtils.isTextEmpty(mGuest.email) == true)
        {
            DailyToast.showToast(getActivity(), getString(R.string.toast_msg_please_input_email), DailyToast.LENGTH_SHORT);

            unLockAll();
            return;
        }

        if (DailyTextUtils.validEmail(mGuest.email) == false)
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

        checkDuplicatePayment();

        mAnalytics.onEventAgreedTermClick(getActivity(), mStayName, mRoomName);
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
    public void onAgreedThirdPartyTermsClick(boolean checked)
    {
        mAgreedThirdPartyTerms = checked;

        if (checked == true)
        {
            mAnalytics.onEventAgreedThirdPartyClick(getActivity());
        }
    }

    private void checkDuplicatePayment()
    {
        screenLock(true);

        addCompositeDisposable(mPaymentRemoteImpl.getStayHasDuplicatePayment(mStayBookDateTime).subscribe(new Consumer<String>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull String message) throws Exception
            {
                unLockAll();

                if (DailyTextUtils.isTextEmpty(message) == true)
                {
                    showAgreementPopup();
                } else
                {
                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), message//
                        , getString(R.string.label_do_booking), getString(R.string.dialog_btn_text_no)//
                        , new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                showAgreementPopup();
                            }
                        }, null, null, null, false);
                }
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

    void showAgreementPopup()
    {
        // 보너스 / 쿠폰 (으)로만 결제하는 경우
        if ((mSaleType == BONUS && mStayPayment.totalPrice <= mUserSimpleInformation.bonus)//
            || (mSaleType == COUPON && mStayPayment.totalPrice <= mSelectedCoupon.amount))
        {
            // 보너스로만 결제할 경우에는 팝업이 기존의 카드 타입과 동일한다.
            getViewInterface().showAgreeTermDialog(DailyBookingPaymentTypeView.PaymentType.FREE//
                , getAgreedTermMessages(DailyBookingPaymentTypeView.PaymentType.FREE, mStayPayment.waitingForBooking), new View.OnClickListener()
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

                        mAnalytics.onEventAgreedTermCancelClick(getActivity());
                    }
                });

            mAnalytics.onEventStartPayment(getActivity(), DailyBookingPaymentTypeView.PaymentType.FREE);
        } else
        {
            if (mPaymentType == DailyBookingPaymentTypeView.PaymentType.EASY_CARD && mSelectedCard == null)
            {
                startActivityForResult(RegisterCreditCardActivity.newInstance(getActivity())//
                    , StayPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT);

                mAnalytics.onEventStartPayment(getActivity(), DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
            } else
            {
                getViewInterface().showAgreeTermDialog(mPaymentType, getAgreedTermMessages(mPaymentType, mStayPayment.waitingForBooking), new View.OnClickListener()
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

                        mAnalytics.onEventAgreedTermCancelClick(getActivity());
                    }
                });

                mAnalytics.onEventStartPayment(getActivity(), mPaymentType);
            }
        }
    }

    synchronized void onAgreedPaymentClick()
    {
        if (lock() == true)
        {
            return;
        }

        screenLock(true);

        // 입력된 내용을 저장한다.
        //        if (mOverseas == true)
        //        {
        //            DailyUserPreference.getInstance(getActivity()).setOverseasInformation(mGuest.name, mGuest.phone, mGuest.email);
        //        }

        String couponCode = mSelectedCoupon != null ? mSelectedCoupon.couponCode : null;

        // 보너스 / 쿠폰 (으)로만 결제하는 경우

        if ((mSaleType == BONUS && mStayPayment.totalPrice <= mUserSimpleInformation.bonus)//
            || (mSaleType == COUPON && mStayPayment.totalPrice <= mSelectedCoupon.amount))
        {
            JSONObject jsonObject = getPaymentJSONObject(mStayBookDateTime, mRoomIndex//
                , mSaleType, mUserSimpleInformation.bonus, couponCode, mGuest//
                , mStayPayment.totalPrice, mTransportationType, null);

            addCompositeDisposable(mPaymentRemoteImpl.getStayPaymentTypeFree(jsonObject).subscribe(new Consumer<PaymentResult>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull PaymentResult paymentResult) throws Exception
                {
                    startThankYou(paymentResult.aggregationId, true);
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
                            , StayPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT);
                        return;
                    }

                    JSONObject jsonObject = getPaymentJSONObject(mStayBookDateTime, mRoomIndex//
                        , mSaleType, mUserSimpleInformation.bonus, couponCode, mGuest//
                        , mStayPayment.totalPrice, mTransportationType, mSelectedCard.billKey);

                    addCompositeDisposable(mPaymentRemoteImpl.getStayPaymentTypeEasy(jsonObject).subscribe(new Consumer<PaymentResult>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull PaymentResult paymentResult) throws Exception
                        {
                            startThankYou(paymentResult.aggregationId, false);
                        }
                    }, throwable -> {
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
                    final String PAYMENT_TYPE = "credit";

                    JSONObject jsonObject = getPaymentJSONObject(mStayBookDateTime, mRoomIndex//
                        , mSaleType, mUserSimpleInformation.bonus, couponCode, mGuest//
                        , mStayPayment.totalPrice, mTransportationType, null);

                    startActivityForResult(PaymentWebActivity.newInstance(getActivity()//
                        , getWebPaymentUrl(PAYMENT_TYPE), jsonObject.toString(), AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_PROCESS)//
                        , StayPaymentActivity.REQUEST_CODE_PAYMENT_WEB_CARD);
                    break;
                }

                case PHONE:
                {
                    final String PAYMENT_TYPE = "mobile";

                    JSONObject jsonObject = getPaymentJSONObject(mStayBookDateTime, mRoomIndex//
                        , mSaleType, mUserSimpleInformation.bonus, couponCode, mGuest//
                        , mStayPayment.totalPrice, mTransportationType, null);

                    startActivityForResult(PaymentWebActivity.newInstance(getActivity()//
                        , getWebPaymentUrl(PAYMENT_TYPE), jsonObject.toString(), AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_PROCESS)//
                        , StayPaymentActivity.REQUEST_CODE_PAYMENT_WEB_PHONE);
                    break;
                }

                case VBANK:
                {
                    final String PAYMENT_TYPE = "vbank";

                    JSONObject jsonObject = getPaymentJSONObject(mStayBookDateTime, mRoomIndex//
                        , mSaleType, mUserSimpleInformation.bonus, couponCode, mGuest//
                        , mStayPayment.totalPrice, mTransportationType, null);

                    startActivityForResult(PaymentWebActivity.newInstance(getActivity()//
                        , getWebPaymentUrl(PAYMENT_TYPE), jsonObject.toString(), AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_PROCESS)//
                        , StayPaymentActivity.REQUEST_CODE_PAYMENT_WEB_VBANK);
                    break;
                }
            }
        }

        mAnalytics.onScreenAgreeTermDialog(getActivity(), mStayBookDateTime, mStayIndex, mStayName, mRoomIndex, mRoomName//
            , mCategory, mStayPayment, mSelectedCard != null, mSaleType, mSelectedCoupon//
            , mPaymentType, mUserSimpleInformation);
    }

    void startThankYou(String aggregationId, boolean fullBonus)
    {
        try
        {
            mAnalytics.onScreenPaymentCompleted(getActivity(), aggregationId, false);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        // ThankYou 페이지를 홈탭에서 띄우기 위한 코드
        startActivity(DailyInternalDeepLink.getHomeScreenLink(getActivity()));

        String descriptionTitle;
        String descriptionMessage;

        if (mSaleType == STICKER)
        {
            descriptionTitle = getString(R.string.message_payment_reward_sticker_deposit_after_checkout, mStayPayment.providableRewardStickerCount);
            descriptionMessage = null;
        } else
        {
            if (hasDepositSticker() == true)
            {
                descriptionTitle = getString(R.string.message_payment_dont_reward_sticker);
                descriptionMessage = getString(R.string.message_thankyou_dont_reward_sticker_used_bonus_coupon_payment_phone);
            } else
            {
                descriptionTitle = getString(R.string.message_payment_dont_reward_sticker);
                descriptionMessage = null;
            }
        }

        startActivityForResult(StayThankYouActivity.newInstance(getActivity(), false, mStayName, mImageUrl//
            , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mRoomName, aggregationId, mStayPayment.waitingForBooking //
            , mLatitude, mLongitude, descriptionTitle, descriptionMessage, mAnalytics.getThankYouAnalyticsParam())//
            , StayPaymentActivity.REQUEST_CODE_THANK_YOU);

        mAnalytics.onEventTransportationType(getActivity(), mStayPayment.transportation, mTransportationType);
    }

    private JSONObject getPaymentJSONObject(StayBookDateTime stayBookDateTime, int roomIndex//
        , int saleType, int bonus, String couponCode, DomesticGuest guest, int totalPrice//
        , String transportation, String billingKey)
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            switch (saleType)
            {
                case BONUS:
                    jsonObject.put("bonusAmount", bonus > totalPrice ? totalPrice : bonus);
                    jsonObject.put("rewardSticker", false);
                    break;

                case COUPON:
                    jsonObject.put("couponCode", couponCode);
                    jsonObject.put("rewardSticker", false);
                    break;


                case STICKER:
                    jsonObject.put("rewardSticker", true);
                    break;

                default:
                    jsonObject.put("bonusAmount", 0);
                    jsonObject.put("rewardSticker", false);
                    break;
            }

            jsonObject.put("checkInDate", stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            jsonObject.put("days", stayBookDateTime.getNights());
            jsonObject.put("roomIdx", roomIndex);
            jsonObject.put("discountTotal", totalPrice);

            JSONObject bookingGuestJSONObject = new JSONObject();
            bookingGuestJSONObject.put("arrivalDateTime", stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));

            if (DailyTextUtils.isTextEmpty(transportation) == false)
            {
                bookingGuestJSONObject.put("arrivalType", transportation);
            }

            bookingGuestJSONObject.put("email", guest.email);
            bookingGuestJSONObject.put("name", guest.name);
            bookingGuestJSONObject.put("phone", guest.phone);

            jsonObject.put("bookingGuest", bookingGuestJSONObject);

            if (DailyTextUtils.isTextEmpty(billingKey) == false)
            {
                jsonObject.put("billingKey", billingKey);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            jsonObject = null;
        }

        return jsonObject;
    }

    private String getWebPaymentUrl(String paymentType)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/booking/hotel/{type}"//
            : "NTYkNjIkMzIkNTIkMjIkOTEkMTQkNTYkNDYkNzUkNDUkMTckNzEkNzgkMzMkMzQk$NTYyRTI2QTA0MDLFDUMkE2RjQk0OUVGNURWE3PMkUxMUExNDTYD5QTM4NTVKGLQkY5FMzI4RIPDAxRDROSFOTM0RTk3MURBNTUxQwQ==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{type}", paymentType);

        String url;

        if (Constants.DEBUG == true)
        {
            url = DailyPreference.getInstance(getActivity()).getBaseUrl()//
                + Crypto.getUrlDecoderEx(API, urlParams);
        } else
        {
            url = Crypto.getUrlDecoderEx(Setting.getServerUrl())//
                + Crypto.getUrlDecoderEx(API, urlParams);
        }

        return url;
    }

    void onBookingInformation(StayPayment stayPayment, StayBookDateTime stayBookDateTime)
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

            if (DailyTextUtils.isTextEmpty(stayPayment.transportation) == false)
            {
                switch (stayPayment.transportation)
                {
                    case StayPayment.VISIT_TYPE_NONE:
                        onTransportationClick(UNKNOWN);
                        break;

                    case StayPayment.VISIT_TYPE_PARKING:
                        if (DailyTextUtils.isTextEmpty(mTransportationType) == true)
                        {
                            onTransportationClick(WALKING);
                        } else
                        {
                            onTransportationClick(mTransportationType);
                        }
                        break;

                    case StayPayment.VISIT_TYPE_NO_PARKING:
                        onTransportationClick(UNKNOWN);
                        break;
                }
            } else
            {
                onTransportationClick(UNKNOWN);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    void onRewardStickerInformation(StayPayment stayPayment, StayBookDateTime stayBookDateTime)
    {
        if (stayPayment == null || stayBookDateTime == null)
        {
            return;
        }

        // 리워드
        if (stayPayment.activeReward == true)
        {
            getViewInterface().setCheeringMessageVisible(true);

            if (stayPayment.provideRewardSticker == true)
            {
                getViewInterface().setCheeringMessage(true//
                    , getString(R.string.message_booking_reward_cheering_title01, stayPayment.providableRewardStickerCount)//
                    , getString(R.string.message_booking_reward_cheering_warning));

                getViewInterface().setDepositStickerVisible(true);
                getViewInterface().setDepositStickerCardVisible(true);

                // 최초 진입시에는 상관이 없지만, 가격 변경후 화면 변경시 mSaleType 의 초기 타입이 None 이기에 결제 타입이 바뀌게 되는 이슈로 수정
                if (mPaymentType != DailyBookingPaymentTypeView.PaymentType.PHONE && mSaleType == NONE)
                {
                    setSaleType(STICKER);
                }

                getViewInterface().setPaymentTypeDescriptionText(DailyBookingPaymentTypeView.PaymentType.PHONE, getString(R.string.label_booking_reward_phonepay_description));
            } else
            {
                getViewInterface().setCheeringMessage(false//
                    , getString(R.string.message_booking_reward_cheering_title02), null);

                getViewInterface().setDepositStickerVisible(false);
                getViewInterface().setDepositStickerCardVisible(false);

                getViewInterface().setPaymentTypeDescriptionText(DailyBookingPaymentTypeView.PaymentType.PHONE, getString(R.string.label_booking_phonepay_description));
            }
        } else
        {
            getViewInterface().setCheeringMessageVisible(false);
            getViewInterface().setDepositStickerVisible(false);
            getViewInterface().setDepositStickerCardVisible(false);

            getViewInterface().setPaymentTypeDescriptionText(DailyBookingPaymentTypeView.PaymentType.PHONE, getString(R.string.label_booking_phonepay_description));
        }
    }

    void notifyEasyCardChanged()
    {
        getViewInterface().setEasyCard(mSelectedCard);
    }

    void notifyStayPaymentChanged()
    {
        if (mUserSimpleInformation == null || mStayPayment == null || mStayBookDateTime == null)
        {
            return;
        }

        try
        {
            int paymentPrice, discountPrice;

            switch (mSaleType)
            {
                case NONE:
                    // 기본이 스티커 적립 상태이다.
                    paymentPrice = mStayPayment.totalPrice;
                    discountPrice = 0;

                    getViewInterface().setBonus(false, mUserSimpleInformation.bonus, 0);
                    getViewInterface().setCoupon(false, 0, false);
                    getViewInterface().setDepositSticker(false);
                    break;

                case BONUS:
                    paymentPrice = mStayPayment.totalPrice - mUserSimpleInformation.bonus;
                    discountPrice = paymentPrice < 0 ? mStayPayment.totalPrice : mUserSimpleInformation.bonus;

                    getViewInterface().setBonus(true, mUserSimpleInformation.bonus, discountPrice);
                    getViewInterface().setCoupon(false, 0, false);
                    getViewInterface().setDepositSticker(false);
                    break;

                case COUPON:
                    paymentPrice = mStayPayment.totalPrice - mSelectedCoupon.amount;
                    discountPrice = paymentPrice < 0 ? mStayPayment.totalPrice : mSelectedCoupon.amount;

                    getViewInterface().setBonus(false, mUserSimpleInformation.bonus, 0);
                    getViewInterface().setCoupon(true, mSelectedCoupon.amount, mSelectedCoupon.type == Coupon.Type.REWARD);
                    getViewInterface().setDepositSticker(false);
                    break;

                case STICKER:
                default:
                    // 기본이 스티커 적립 상태이다.
                    paymentPrice = mStayPayment.totalPrice;
                    discountPrice = 0;

                    getViewInterface().setBonus(false, mUserSimpleInformation.bonus, 0);
                    getViewInterface().setCoupon(false, 0, false);
                    getViewInterface().setDepositSticker(hasDepositSticker());
                    break;
            }

            setDepositStickerCard(mStayPayment, mStayBookDateTime);

            getViewInterface().setStayPayment(mStayBookDateTime.getNights(), mStayPayment.totalPrice, discountPrice);

            if (paymentPrice <= 0)
            {
                setPaymentType(DailyBookingPaymentTypeView.PaymentType.FREE);
                getViewInterface().setPaymentType(DailyBookingPaymentTypeView.PaymentType.FREE);
            } else
            {
                boolean easyCardEnabled = true;
                boolean cardEnabled = true;
                boolean phoneEnabled = true;
                boolean vBankEnabled = true;

                if (hasDepositSticker() == true)
                {
                    switch (mSaleType)
                    {
                        case NONE:
                            easyCardEnabled = false;
                            cardEnabled = false;
                            vBankEnabled = false;
                            break;

                        case BONUS:
                            break;

                        case COUPON:
                            if (mSelectedCoupon.type == Coupon.Type.REWARD)
                            {
                                phoneEnabled = false;
                            }
                            break;

                        case STICKER:
                        default:
                            phoneEnabled = false;
                            break;
                    }
                } else
                {
                    switch (mSaleType)
                    {
                        case NONE:
                        case BONUS:
                            break;

                        case COUPON:
                            if (mSelectedCoupon.type == Coupon.Type.REWARD)
                            {
                                phoneEnabled = false;
                            }
                            break;

                        case STICKER:
                        default:
                            break;
                    }
                }

                // 리모트에서 조절되는 부분
                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStaySimpleCardPaymentEnabled() == false)
                {
                    easyCardEnabled = false;
                }

                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayCardPaymentEnabled() == false)
                {
                    cardEnabled = false;
                }

                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayPhonePaymentEnabled() == false)
                {
                    phoneEnabled = false;
                }

                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayVirtualPaymentEnabled() == false)
                {
                    vBankEnabled = false;
                }

                if (paymentPrice > 0 && paymentPrice < CARD_MIN_PRICE)
                {
                    easyCardEnabled = false;
                    cardEnabled = false;
                } else if (paymentPrice > PHONE_MAX_PRICE)
                {
                    phoneEnabled = false;
                }

                getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.EASY_CARD, easyCardEnabled);
                getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.CARD, cardEnabled);
                getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.PHONE, phoneEnabled);
                getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.VBANK, vBankEnabled);

                if (mPaymentType == DailyBookingPaymentTypeView.PaymentType.EASY_CARD && easyCardEnabled == true)
                {
                    setPaymentType(DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
                    getViewInterface().setPaymentType(DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
                } else if (mPaymentType == DailyBookingPaymentTypeView.PaymentType.CARD && cardEnabled == true)
                {
                    setPaymentType(DailyBookingPaymentTypeView.PaymentType.CARD);
                    getViewInterface().setPaymentType(DailyBookingPaymentTypeView.PaymentType.CARD);
                } else if (mPaymentType == DailyBookingPaymentTypeView.PaymentType.PHONE && phoneEnabled == true)
                {
                    setPaymentType(DailyBookingPaymentTypeView.PaymentType.PHONE);
                    getViewInterface().setPaymentType(DailyBookingPaymentTypeView.PaymentType.PHONE);
                } else if (mPaymentType == DailyBookingPaymentTypeView.PaymentType.VBANK && vBankEnabled == true)
                {
                    setPaymentType(DailyBookingPaymentTypeView.PaymentType.VBANK);
                    getViewInterface().setPaymentType(DailyBookingPaymentTypeView.PaymentType.VBANK);
                } else
                {
                    if (easyCardEnabled == true)
                    {
                        setPaymentType(DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
                        getViewInterface().setPaymentType(DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
                    } else if (cardEnabled == true)
                    {
                        setPaymentType(DailyBookingPaymentTypeView.PaymentType.CARD);
                        getViewInterface().setPaymentType(DailyBookingPaymentTypeView.PaymentType.CARD);
                    } else if (phoneEnabled == true)
                    {
                        setPaymentType(DailyBookingPaymentTypeView.PaymentType.PHONE);
                        getViewInterface().setPaymentType(DailyBookingPaymentTypeView.PaymentType.PHONE);
                    } else if (vBankEnabled == true)
                    {
                        setPaymentType(DailyBookingPaymentTypeView.PaymentType.VBANK);
                        getViewInterface().setPaymentType(DailyBookingPaymentTypeView.PaymentType.VBANK);
                    } else
                    {
                        getViewInterface().showSimpleDialog(null, getString(R.string.message_payment_none_payment_type)//
                            , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialog)
                                {
                                    finish();
                                }
                            });
                    }
                }
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    void setStayPayment(StayPayment stayPayment)
    {
        mStayPayment = stayPayment;
    }

    void setUserInformation(UserSimpleInformation userSimpleInformation)
    {
        mUserSimpleInformation = userSimpleInformation;
    }

    void setStayRefundPolicy(StayRefundPolicy stayRefundPolicy)
    {
        mStayRefundPolicy = stayRefundPolicy;
    }

    DomesticGuest getOverseasGustInformation(UserSimpleInformation userSimpleInformation)
    {
        DomesticGuest guest = new DomesticGuest();

        guest.name = DailyUserPreference.getInstance(getActivity()).getOverseasFirstName();
        guest.phone = DailyUserPreference.getInstance(getActivity()).getOverseasPhone();
        guest.email = DailyUserPreference.getInstance(getActivity()).getOverseasEmail();

        if (userSimpleInformation != null)
        {
            if (DailyTextUtils.isTextEmpty(guest.phone) == true)
            {
                guest.phone = userSimpleInformation.phone;
            }

            if (DailyTextUtils.isTextEmpty(guest.email) == true)
            {
                guest.phone = userSimpleInformation.email;
            }
        }

        return guest;
    }

    void setCommonDateTime(CommonDateTime commonDateTime)
    {
        mCommonDateTime = commonDateTime;
    }

    void setStayBookDateTime(String checkInDateTime, String checkOutDateTime)
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

    void setSelectCard(Card card)
    {
        mSelectedCard = card;

        if (card != null && DailyTextUtils.isTextEmpty(card.number, card.billKey) == false)
        {
            DailyPreference.getInstance(getActivity()).setFavoriteCard(card.number, card.billKey);
        }
    }

    void setSaleType(int saleType)
    {
        mSaleType = saleType;
    }

    private void setCoupon(Coupon coupon)
    {
        if (coupon == null || mStayPayment == null)
        {
            mSelectedCoupon = null;
            setSaleType(hasDepositSticker() ? STICKER : NONE);

            notifyStayPaymentChanged();
            return;
        }

        if (coupon.amount > mStayPayment.totalPrice)
        {
            String difference = DailyTextUtils.getPriceFormat(getActivity(), (coupon.amount - mStayPayment.totalPrice), false);

            getViewInterface().showSimpleDialog(null, getString(R.string.message_over_coupon_price, difference)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        setSaleType(COUPON);
                        mSelectedCoupon = coupon;

                        notifyStayPaymentChanged();
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mSelectedCoupon = null;
                        setSaleType(hasDepositSticker() ? STICKER : NONE);

                        notifyStayPaymentChanged();
                    }
                }, new DialogInterface.OnCancelListener()
                {

                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        mSelectedCoupon = null;
                        setSaleType(hasDepositSticker() ? STICKER : NONE);

                        notifyStayPaymentChanged();
                    }
                }, null, true);

        } else
        {
            // 호텔 결제 정보에 쿠폰 가격 넣고 텍스트 업데이트 필요
            setSaleType(COUPON);
            mSelectedCoupon = coupon;

            notifyStayPaymentChanged();
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

        DailyPreference.getInstance(getActivity()).setFavoriteCard(mSelectedCard.number, mSelectedCard.billKey);
    }

    void notifyUserInformationChanged()
    {
        if (mUserSimpleInformation == null)
        {
            return;
        }

        getViewInterface().setUserInformation(mUserSimpleInformation.name, mUserSimpleInformation.phone, mUserSimpleInformation.email);
    }

    void notifyGuestInformationChanged(DomesticGuest guest)
    {
        if (guest == null)
        {
            return;
        }

        getViewInterface().setGuestInformation(guest.name, guest.phone, guest.email);
    }

    private void notifyGuestMobileInformationChanged(String mobile)
    {
        getViewInterface().setGuestMobileInformation(mobile);
    }

    void notifyPaymentTypeChanged()
    {
        if (mPaymentType == null)
        {
            return;
        }

        getViewInterface().setPaymentType(mPaymentType);
    }

    void notifyBonusEnabledChanged()
    {
        if (mUserSimpleInformation == null)
        {
            getViewInterface().setBonusEnabled(false);
        } else
        {
            if (mStayPayment != null && mStayPayment.totalPrice <= MIN_AMOUNT_FOR_BONUS_USAGE)
            {
                getViewInterface().setBonusGuideText(getString(R.string.dialog_btn_payment_no_reserve//
                    , DailyTextUtils.getPriceFormat(getActivity(), MIN_AMOUNT_FOR_BONUS_USAGE, false)));
                getViewInterface().setBonusEnabled(false);
            } else
            {
                getViewInterface().setBonusEnabled(mUserSimpleInformation.bonus > 0);
            }
        }
    }

    void notifyRefundPolicyChanged()
    {
        if (mStayRefundPolicy == null)
        {
            return;
        }

        if (StayRefundPolicy.STATUS_NONE.equalsIgnoreCase(mStayRefundPolicy.refundPolicy) == true)
        {
            getViewInterface().setRefundPolicy(null, hasDepositSticker(), false);
        } else
        {
            getViewInterface().setRefundPolicy(mStayRefundPolicy.comment, hasDepositSticker(), StayRefundPolicy.STATUS_NRD.equalsIgnoreCase(mStayRefundPolicy.refundPolicy));
        }
    }

    void notifyCardEventChanged(CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        String cardEvent = DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigPaymentCardEvent();

        if (DailyTextUtils.isTextEmpty(cardEvent) == true)
        {
            getViewInterface().setCardEventVisible(false);
        } else
        {
            try
            {
                long currentTime = DailyCalendar.convertDate(commonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT).getTime();

                JSONArray jsonArray = new JSONArray(cardEvent);

                int length = jsonArray.length();

                List<Pair<String, List<String>>> cardEventList = new ArrayList<>();

                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (jsonObject.getBoolean("enabled") == false)
                    {
                        continue;
                    }

                    long startTime = DailyCalendar.convertDate(jsonObject.getString("startDateTime"), DailyCalendar.ISO_8601_FORMAT).getTime();
                    long endTime = DailyCalendar.convertDate(jsonObject.getString("endDateTime"), DailyCalendar.ISO_8601_FORMAT).getTime();

                    if (currentTime >= startTime && currentTime <= endTime)
                    {
                        List<String> messageList = new ArrayList<>();
                        JSONArray messageJSONArray = jsonObject.getJSONArray("messages");

                        int messageCount = messageJSONArray.length();

                        for (int j = 0; j < messageCount; j++)
                        {
                            messageList.add(messageJSONArray.getString(j));
                        }

                        cardEventList.add(new Pair<>(jsonObject.getString("title"), messageList));
                    }
                }

                if (cardEventList.size() == 0)
                {
                    getViewInterface().setCardEventVisible(false);
                } else
                {
                    getViewInterface().setCardEventVisible(true);
                    getViewInterface().setCardEventData(cardEventList);
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());

                getViewInterface().setCardEventVisible(false);
            }
        }
    }

    Card getSelectedCard(List<Card> cardList)
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
        boolean isSimpleCardPaymentEnabled = DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStaySimpleCardPaymentEnabled();
        boolean isCardPaymentEnabled = DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayCardPaymentEnabled();
        boolean isPhonePaymentEnabled = DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayPhonePaymentEnabled();
        boolean isVirtualPaymentEnabled = DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayVirtualPaymentEnabled();

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

        if (isVirtualPaymentEnabled == false)
        {
            guideMemo.append(getString(R.string.act_booking_pay_account));
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
    }

    void setWaitingPopupMessageType(CommonDateTime commonDateTime, StayBookDateTime stayBookDateTime) throws Exception
    {
        if (commonDateTime == null)
        {
            return;
        }

        int openHour = Integer.parseInt(DailyCalendar.convertDateFormatString(commonDateTime.openDateTime, DailyCalendar.ISO_8601_FORMAT, "HH"));
        int closeHour = Integer.parseInt(DailyCalendar.convertDateFormatString(commonDateTime.closeDateTime, DailyCalendar.ISO_8601_FORMAT, "HH"));
        int currentHour = Integer.parseInt(DailyCalendar.convertDateFormatString(commonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT, "HH"));

        String todayDate = DailyCalendar.convertDateFormatString(commonDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd");
        String bookingDate = stayBookDateTime.getCheckInDateTime("yyyy-MM-dd");

        if (todayDate.equalsIgnoreCase(bookingDate) == true)
        {
            // 서버시간과 같은 날
            if (currentHour < openHour)
            {
                // 당일이고 영업시간 전일때 (서버에서 새벽 3시 부터 당일로 주기 때문에 새벽 3시 체크 안함)
                mWaitingForBookingMessageType = 2;
            } else
            {
                // 당일이고 영엽시간 이후 일때 (서버에서 다음날 새벽 3시까지 당일로 주기 때문에 새벽 3시 체크 안함)
                mWaitingForBookingMessageType = 1;
            }
        } else
        {
            // 사전 예약 일때
            if (openHour <= currentHour && currentHour < 22)
            {
                // 사전예약 이고 9시 부터 22시 전까지
                mWaitingForBookingMessageType = 3;
            } else
            {
                mWaitingForBookingMessageType = 4;
                // 사전예약 이고 9시 이전이거나 22시 이후 일때
            }
        }
    }

    private int[] getAgreedTermMessages(DailyBookingPaymentTypeView.PaymentType paymentType, boolean waitingForBooking)
    {
        if (paymentType == null)
        {
            return null;
        }

        int[] messages;

        if (waitingForBooking == true)
        {
            messages = getWaitingAgreedTermMessages(mWaitingForBookingMessageType, paymentType);
        } else
        {
            switch (paymentType)
            {
                case EASY_CARD:
                    messages = new int[]{R.string.dialog_msg_hotel_payment_message01//
                        , R.string.dialog_msg_hotel_payment_message14//
                        , R.string.dialog_msg_hotel_payment_message02//
                        , R.string.dialog_msg_hotel_payment_message03//
                        , R.string.dialog_msg_hotel_payment_message07};
                    break;

                case CARD:
                    messages = new int[]{R.string.dialog_msg_hotel_payment_message01//
                        , R.string.dialog_msg_hotel_payment_message14//
                        , R.string.dialog_msg_hotel_payment_message02//
                        , R.string.dialog_msg_hotel_payment_message03//
                        , R.string.dialog_msg_hotel_payment_message06};
                    break;

                case PHONE:
                    messages = new int[]{R.string.dialog_msg_hotel_payment_message01//
                        , R.string.dialog_msg_hotel_payment_message14//
                        , R.string.dialog_msg_hotel_payment_message02//
                        , R.string.dialog_msg_hotel_payment_message03//
                        , R.string.dialog_msg_hotel_payment_message06};
                    break;

                case VBANK:
                    messages = new int[]{R.string.dialog_msg_hotel_payment_message01//
                        , R.string.dialog_msg_hotel_payment_message14//
                        , R.string.dialog_msg_hotel_payment_message02//
                        , R.string.dialog_msg_hotel_payment_message03//
                        , R.string.dialog_msg_hotel_payment_message05//
                        , R.string.dialog_msg_hotel_payment_message06};
                    break;

                case FREE:
                    messages = new int[]{R.string.dialog_msg_hotel_payment_message01//
                        , R.string.dialog_msg_hotel_payment_message14//
                        , R.string.dialog_msg_hotel_payment_message02//
                        , R.string.dialog_msg_hotel_payment_message03//
                        , R.string.dialog_msg_hotel_payment_message06};
                    break;

                default:
                    return null;
            }
        }

        return messages;
    }

    private int[] getWaitingAgreedTermMessages(int waitingMessageType, DailyBookingPaymentTypeView.PaymentType paymentType)
    {
        if (paymentType == null)
        {
            return null;
        }

        int[] messageList;

        if (DailyBookingPaymentTypeView.PaymentType.VBANK == paymentType)
        {
            messageList = new int[6];
        } else
        {
            messageList = new int[5];
        }

        messageList[0] = R.string.dialog_msg_hotel_payment_message01;
        messageList[1] = R.string.dialog_msg_hotel_payment_message14;

        switch (waitingMessageType)
        {
            case 1:
                messageList[2] = R.string.dialog_msg_hotel_payment_message_pension_1; // 당일 9시 부터 다음날 새벽 3시
                break;

            case 2:
                messageList[2] = R.string.dialog_msg_hotel_payment_message_pension_2; // 당일 새벽 3시 부터 다음날 9시까지
                break;

            case 3:
                messageList[2] = R.string.dialog_msg_hotel_payment_message_pension_3; // 다음날 9시부터 22시 전까지
                break;

            case 4:
                messageList[2] = R.string.dialog_msg_hotel_payment_message_pension_4; // 다음날 새벽 3시 부터 9시 이전 다음날 22시 부터
                break;

            default:
                break;
        }

        messageList[3] = R.string.dialog_msg_hotel_payment_message03;

        switch (paymentType)
        {
            case EASY_CARD:
                messageList[4] = R.string.dialog_msg_hotel_payment_message07;
                break;

            case VBANK:
                messageList[4] = R.string.dialog_msg_hotel_payment_message05;
                messageList[5] = R.string.dialog_msg_hotel_payment_message06;
                break;

            default:
                messageList[4] = R.string.dialog_msg_hotel_payment_message06;
                break;
        }

        return messageList;
    }

    private void setDepositStickerCard(StayPayment stayPayment, StayBookDateTime stayBookDateTime)
    {
        if (stayPayment == null || stayBookDateTime == null)
        {
            return;
        }

        if (mSaleType == STICKER)
        {
            getViewInterface().setDepositStickerCard(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerCardTitleMessage()//
                , stayPayment.rewardStickerCount, null, getString(R.string.message_payment_reward_sticker_deposit_after_checkout, stayBookDateTime.getNights()));
        } else
        {
            if (hasDepositSticker() == true)
            {
                String text = getString(R.string.message_payment_dont_reward_sticker_used_bonus_coupon_payment_phone);

                SpannableString spannableString = new SpannableString(text);

                int startIndex = text.indexOf('\n');
                spannableString.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getMediumTypeface()),//
                    startIndex, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                getViewInterface().setDepositStickerCard(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerCardTitleMessage()//
                    , stayPayment.rewardStickerCount, null, spannableString);
            }
        }
    }

    boolean hasDepositSticker()
    {
        return mStayPayment != null && mStayPayment.activeReward == true && mStayPayment.provideRewardSticker == true;
    }

    private void onPaymentWebResult(DailyBookingPaymentTypeView.PaymentType paymentType, int resultCode, Intent data)
    {
        // 가격 변동인 경우 결제 화면 전체를 갱신해야 한다. - 전체 갱신이기때문에 onPaymentWebResult를 호출하지 않는다.
        if (resultCode == Constants.CODE_RESULT_ACTIVITY_PAYMENT_CHANGED_PRICE)
        {
            mNeedOverwritePrice = true;
            setRefresh(true);
            return;
        }

        if (resultCode == Constants.CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION)
        {
            restartExpiredSession();
            return;
        }

        // 결제 진행후 취소시에 적립금과 쿠폰을 돌려주어야 한다.
        if (resultCode != Activity.RESULT_OK)
        {
            addCompositeDisposable(mProfileRemoteImpl.getUserSimpleInformation().subscribe(new Consumer<UserSimpleInformation>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull UserSimpleInformation userSimpleInformation) throws Exception
                {
                    setUserInformation(userSimpleInformation);

                    notifyBonusEnabledChanged();
                    notifyStayPaymentChanged();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {

                }
            }));
        }

        String result = data == null ? null : data.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_PAYMENT_RESULT);

        if (resultCode == Activity.RESULT_OK)
        {
            Observable.just(result).map(jsonString -> {
                PaymentResult paymentResult = new PaymentResult();

                JSONObject jsonObject = new JSONObject(jsonString);

                int msgCode = jsonObject.getInt("msgCode");
                String msg = jsonObject.getString("msg");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = jsonObject.getJSONObject("data");

                    if (dataJSONObject != null && dataJSONObject.has("aggregationId") == true)
                    {
                        paymentResult.aggregationId = dataJSONObject.getString("aggregationId");
                    }
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
                    switch (paymentType)
                    {
                        case VBANK:
                        {
                            getViewInterface().showSimpleDialog(getString(R.string.dialog_title_payment), getString(R.string.dialog_msg_issuing_account)//
                                , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                                {
                                    @Override
                                    public void onDismiss(DialogInterface dialog)
                                    {
                                        startActivity(DailyInternalDeepLink.getStayBookingDetailScreenLink(getActivity(), paymentResult.aggregationId));
                                    }
                                });
                            break;
                        }

                        default:
                        {
                            getViewInterface().showSimpleDialog(getString(R.string.dialog_title_payment), getString(R.string.message_completed_payment_default)//
                                , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                                {
                                    @Override
                                    public void onDismiss(DialogInterface dialog)
                                    {
                                        startThankYou(paymentResult.aggregationId, false);
                                    }
                                });
                            break;
                        }
                    }
                }
            }, throwable -> {
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
            DialogInterface.OnDismissListener dismissListener = null;

            try
            {
                JSONObject jsonObject = new JSONObject(result);
                msgCode = jsonObject.getInt("msgCode");

                // 다날 핸드폰 화면에서 취소 버튼 누르는 경우
                // 사용자가 일반 결제 등의 화면에서 back key를 눌러 나타나는 취소 팝업에서 명시적으로 취소 했을 경우
                if (msgCode == -104)
                {
                    message = getString(R.string.act_toast_payment_canceled);
                } else
                {
                    message = jsonObject.getString("msg");
                    dismissListener = new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface)
                        {
                            setResult(BaseActivity.RESULT_CODE_REFRESH);
                            onBackClick();
                        }
                    };
                }
            } catch (Exception e)
            {
                message = getString(R.string.act_toast_payment_fail);
                dismissListener = new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface)
                    {
                        setResult(BaseActivity.RESULT_CODE_REFRESH);
                        onBackClick();
                    }
                };
            }

            getViewInterface().showSimpleDialog(title, message, getString(R.string.dialog_btn_text_confirm), null, dismissListener);
        }
    }

    void onPaymentError(BaseException baseException)
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
            case 1190:
            {
                getViewInterface().showSimpleDialog(getString(R.string.dialog_title_payment), message//
                    , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            mNeedOverwritePrice = true;
                            setRefresh(true);
                            onRefresh(true);
                        }
                    }, false);
                return;
            }
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

    void getMaxCouponAmount(int stayIndex, int roomIndex, StayBookDateTime stayBookDateTime)
    {
        if (stayBookDateTime == null)
        {
            return;
        }

        addCompositeDisposable(mCouponRemoteImpl.getStayCouponListByPayment(stayIndex, roomIndex //
            , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)) //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Coupons>()
            {
                @Override
                public void accept(Coupons coupons) throws Exception
                {
                    setMaxCouponAmount(coupons.maxCouponAmount, false);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    setMaxCouponAmount(0, true);
                }
            }));
    }

    void setMaxCouponAmount(int maxCouponAmount, boolean isError)
    {
        mMaxCouponAmount = maxCouponAmount;

        if (isError == true)
        {
            getViewInterface().setCouponEnabled(true);
            getViewInterface().setMaxCouponAmountVisible(false);
            return;
        }

        if (maxCouponAmount > 0)
        {
            getViewInterface().setCouponEnabled(true);
        } else
        {
            getViewInterface().setCouponEnabled(false);
        }

        getViewInterface().setMaxCouponAmountText(maxCouponAmount);
        getViewInterface().setMaxCouponAmountVisible(true);
    }
}

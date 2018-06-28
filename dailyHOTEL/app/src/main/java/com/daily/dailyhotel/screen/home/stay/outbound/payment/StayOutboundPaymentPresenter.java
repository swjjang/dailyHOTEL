package com.daily.dailyhotel.screen.home.stay.outbound.payment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
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
import com.daily.dailyhotel.entity.Coupon;
import com.daily.dailyhotel.entity.Coupons;
import com.daily.dailyhotel.entity.OverseasGuest;
import com.daily.dailyhotel.entity.PaymentResult;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.StayOutboundRoom;
import com.daily.dailyhotel.entity.UserSimpleInformation;
import com.daily.dailyhotel.parcel.CouponParcel;
import com.daily.dailyhotel.parcel.analytics.StayOutboundPaymentAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundThankYouAnalyticsParam;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.CouponRemoteImpl;
import com.daily.dailyhotel.repository.remote.PaymentRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.payment.PaymentWebActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.payment.coupon.SelectStayOutboundCouponDialogActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.thankyou.StayOutboundThankYouActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.Setting;
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
import io.reactivex.functions.Function4;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundPaymentPresenter extends BaseExceptionPresenter<StayOutboundPaymentActivity, StayOutboundPaymentInterface> implements StayOutboundPaymentView.OnEventListener
{
    private static final int MIN_AMOUNT_FOR_BONUS_USAGE = 20000; // 보너스를 사용하기 위한 최소 주문 가격

    // 1000원 미만 결제시에 간편/일반 결제 불가 - 쿠폰 또는 적립금 전체 사용이 아닌경우 조건 추가
    private static final int CARD_MIN_PRICE = 1000;
    private static final int PHONE_MAX_PRICE = 500000;

    static final int NONE = 0;
    static final int BONUS = 1;
    static final int COUPON = 2;
    static final int STICKER = 3;

    // 서버로 해당 문자열 그대로 보냄.(수정 금지)
    @IntDef({NONE, BONUS, COUPON, STICKER})
    @interface SaleType
    {
    }

    StayOutboundPaymentAnalyticsInterface mAnalytics;

    private PaymentRemoteImpl mPaymentRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;
    private CouponRemoteImpl mCouponRemoteImpl;

    StayBookDateTime mStayBookDateTime;
    int mStayIndex, mRoomPrice, mRoomBedTypeId;
    private People mPeople;
    String mStayName, mRoomType, mVendorType, mImageUrl;
    private String mRateCode, mRateKey, mRoomTypeCode;
    StayOutboundPayment mStayOutboundPayment;
    Card mSelectedCard;
    private OverseasGuest mGuest;
    Coupon mSelectedCoupon;
    private DailyBookingPaymentTypeView.PaymentType mPaymentType;
    private boolean mAgreedThirdPartyTerms;
    UserSimpleInformation mUserSimpleInformation;
    private int mSaleType;
    private int mMaxCouponAmount;
    boolean mCheckChangedPrice;
    boolean mNeedOverwritePrice;

    // ***************************************************************** //
    // ************** 변수 선언시에 onSaveInstanceState 에 꼭 등록해야하는지 판단한다.
    // ************** 클래스는 해당 내부 멤버 변수들이 onSaveInstance에 잘처리되고 있는지 확인한다.
    // ***************************************************************** //


    public interface StayOutboundPaymentAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(StayOutboundPaymentAnalyticsParam analyticsParam);

        StayOutboundPaymentAnalyticsParam getAnalyticsParam();

        void onScreen(Activity activity, StayBookDateTime stayBookDateTime, int stayIndex);

        void onEventEnterVendorType(Activity activity, int stayIndex, String vendorType);

        void onScreenPaymentCompleted(Activity activity, StayOutboundPayment stayOutboundPayment, StayBookDateTime stayBookDateTime//
            , String stayName, DailyBookingPaymentTypeView.PaymentType paymentType, int saleType//
            , boolean registerEasyCard, UserSimpleInformation userSimpleInformation, String aggregationId);

        void onEventStartPayment(Activity activity, DailyBookingPaymentTypeView.PaymentType paymentType);

        void onEventEndPayment(Activity activity, DailyBookingPaymentTypeView.PaymentType paymentType);

        void onEventVendorType(Activity activity, int stayIndex, String vendorType);

        StayOutboundThankYouAnalyticsParam getThankYouAnalyticsParam(DailyBookingPaymentTypeView.PaymentType paymentType //
            , boolean fullBonus, int saleType, boolean registerEasyCard, int stayIndex);

        void setPaymentParam(HashMap<String, String> param);

        void onEventCouponClick(Activity activity, boolean selected);

        void onEventNotAvailableCoupon(Activity activity, String stayName, int roomPrice);

        HashMap<String, String> getPaymentParam();
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

        mAnalytics = new StayOutboundPaymentAnalyticsImpl();

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
        mVendorType = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_VENDOR_TYPE);

        if (DailyTextUtils.isTextEmpty(mStayName, mRoomType, mRateCode, mRateKey, mRoomTypeCode) == true)
        {
            return false;
        }

        String checkInDateTime = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
        String checkOutDateTime = intent.getStringExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

        setStayBookDateTime(checkInDateTime, checkOutDateTime);

        int numberOfAdults = intent.getIntExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
        ArrayList<Integer> childAgeList = intent.getIntegerArrayListExtra(StayOutboundPaymentActivity.INTENT_EXTRA_DATA_CHILD_LIST);

        setPeople(numberOfAdults, childAgeList);

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
        mAnalytics.onEventEnterVendorType(getActivity(), mStayIndex, mVendorType);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        mAnalytics.onScreen(getActivity(), mStayBookDateTime, mStayIndex);

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
        outState.putInt("roomBedTypeId", mRoomBedTypeId);

        outState.putString("stayName", mStayName);
        outState.putString("imageUrl", mImageUrl);
        outState.putString("roomType", mRoomType);
        outState.putString("rateCode", mRateCode);
        outState.putString("rateKey", mRateKey);
        outState.putString("roomTypeCode", mRoomTypeCode);

        if (mStayBookDateTime != null)
        {
            outState.putString("checkInDateTime", mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
            outState.putString("checkOutDateTime", mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
        }

        if (mPaymentType != null)
        {
            outState.putString("paymentType", mPaymentType.name());
        }

        outState.putInt("saleType", mSaleType);
        outState.putBoolean("agreedThirdPartyTerms", mAgreedThirdPartyTerms);

        if (mSelectedCoupon != null)
        {
            outState.putParcelable("selectedCoupon", new CouponParcel(mSelectedCoupon));
        }

        if (mAnalytics != null)
        {
            outState.putParcelable("analytics", mAnalytics.getAnalyticsParam());
            outState.putSerializable("analyticsPaymentParam", mAnalytics.getPaymentParam());
        }

        if (mPeople != null)
        {
            outState.putInt("people_numberOfAdults", mPeople.numberOfAdults);
            outState.putIntegerArrayList("people_childAgeList", mPeople.getChildAgeList());
        }

        try
        {
            outState.putBundle("stayOutboundPayment", Util.getClassPublicFieldsBundle(StayOutboundPayment.class, mStayOutboundPayment));
            outState.putBundle("selectedCard", Util.getClassPublicFieldsBundle(Card.class, mSelectedCard));
            outState.putBundle("guest", Util.getClassPublicFieldsBundle(OverseasGuest.class, mGuest));
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
        mRoomBedTypeId = savedInstanceState.getInt("roomBedTypeId");

        mStayName = savedInstanceState.getString("stayName");
        mImageUrl = savedInstanceState.getString("imageUrl");
        mRoomType = savedInstanceState.getString("roomType");
        mRateCode = savedInstanceState.getString("rateCode");
        mRateKey = savedInstanceState.getString("rateKey");
        mRoomTypeCode = savedInstanceState.getString("roomTypeCode");

        setStayBookDateTime(savedInstanceState.getString("checkInDateTime"), savedInstanceState.getString("checkOutDateTime"));

        try
        {
            mPaymentType = DailyBookingPaymentTypeView.PaymentType.valueOf(savedInstanceState.getString("paymentType"));
        } catch (Exception e)
        {
            mPaymentType = DailyBookingPaymentTypeView.PaymentType.EASY_CARD;
        }

        mSaleType = savedInstanceState.getInt("saleType", NONE);
        mAgreedThirdPartyTerms = savedInstanceState.getBoolean("agreedThirdPartyTerms");

        CouponParcel couponParcel = savedInstanceState.getParcelable("selectedCoupon");

        if (couponParcel != null)
        {
            mSelectedCoupon = couponParcel.getCoupon();
        }

        if (mAnalytics != null)
        {
            mAnalytics.setAnalyticsParam(savedInstanceState.getParcelable("analytics"));
            mAnalytics.setPaymentParam((HashMap<String, String>) savedInstanceState.getSerializable("analyticsPaymentParam"));
        }

        setPeople(savedInstanceState.getInt("people_numberOfAdults"), savedInstanceState.getIntegerArrayList("people_childAgeList"));

        try
        {
            mStayOutboundPayment = (StayOutboundPayment) Util.setClassPublicFieldsBundle(StayOutboundPayment.class, savedInstanceState.getBundle("stayOutboundPayment"));
            mSelectedCard = (Card) Util.setClassPublicFieldsBundle(Card.class, savedInstanceState.getBundle("selectedCard"));
            mGuest = (OverseasGuest) Util.setClassPublicFieldsBundle(OverseasGuest.class, savedInstanceState.getBundle("guest"));
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
                            selectEasyCard(cardList -> {
                                unLockAll();

                                if (cardList.size() > 0)
                                {
                                    setPaymentType(DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
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

            case StayOutboundPaymentActivity.REQUEST_CODE_REGISTER_PHONE_NUMBER:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    String mobile = data.getStringExtra(InputMobileNumberDialogActivity.INTENT_EXTRA_MOBILE_NUMBER);
                    notifyGuestMobileInformationChanged(mobile);
                }
                break;

            case StayOutboundPaymentActivity.REQUEST_CODE_PAYMENT_WEB_CARD:
            case StayOutboundPaymentActivity.REQUEST_CODE_PAYMENT_WEB_PHONE:
                onPaymentWebResult(resultCode, data);
                break;

            case StayOutboundPaymentActivity.REQUEST_CODE_COUPON_LIST:
                if (data != null)
                {
                    int maxCouponAmount = data.getIntExtra(SelectStayOutboundCouponDialogActivity.INTENT_EXTRA_MAX_COUPON_AMOUNT, 0);
                    setMaxCouponAmount(maxCouponAmount, false);

                    if (resultCode == Activity.RESULT_OK)
                    {
                        CouponParcel couponParcel = data.getParcelableExtra(SelectStayOutboundCouponDialogActivity.INTENT_EXTRA_SELECT_COUPON);

                        if (couponParcel != null)
                        {
                            setCoupon(couponParcel.getCoupon());
                        }
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

        addCompositeDisposable(Observable.zip(mPaymentRemoteImpl.getStayOutboundPayment(getActivity(), mStayBookDateTime, mStayIndex//
            , mRateCode, mRateKey, mRoomTypeCode, mRoomBedTypeId, mPeople, mVendorType)//
            , mPaymentRemoteImpl.getEasyCardList(), mProfileRemoteImpl.getUserSimpleInformation()//
            , mCommonRemoteImpl.getCommonDateTime()//
            , new Function4<StayOutboundPayment, List<Card>, UserSimpleInformation, CommonDateTime, CommonDateTime>()
            {
                @Override
                public CommonDateTime apply(@io.reactivex.annotations.NonNull StayOutboundPayment stayOutboundPayment//
                    , @io.reactivex.annotations.NonNull List<Card> cardList, @io.reactivex.annotations.NonNull UserSimpleInformation userSimpleInformation, @io.reactivex.annotations.NonNull CommonDateTime commonDateTime) throws Exception
                {
                    setStayOutboundPayment(stayOutboundPayment);
                    setSelectCard(getSelectedCard(cardList));
                    setUserInformation(userSimpleInformation);

                    return commonDateTime;
                }
            }).subscribe(new Consumer<CommonDateTime>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull CommonDateTime commonDateTime) throws Exception
            {
                if (mNeedOverwritePrice == true)
                {
                    mNeedOverwritePrice = false;
                    mRoomPrice = mStayOutboundPayment.totalPrice;
                    getViewInterface().scrollToCheckPriceTitle();
                    setResult(BaseActivity.RESULT_CODE_REFRESH);
                }

                onBookingInformation(mStayOutboundPayment, mStayBookDateTime);
                onRewardStickerInformation(mStayOutboundPayment, mStayBookDateTime);

                notifyCardEventChanged(commonDateTime);
                notifyGuestInformationChanged(getGuestInformation(mUserSimpleInformation));
                notifyBonusEnabledChanged();
                notifyPaymentTypeChanged();
                notifyEasyCardChanged();
                notifyStayOutboundPaymentChanged();

                // 위의 리워드 스티커 여부와 정책 여부에 따라서 순서 및 단어가 바뀐다.
                notifyRefundPolicyChanged();

                // 가격이 변동된 경우
                if (mCheckChangedPrice == false && mRoomPrice > 0 && mRoomPrice != mStayOutboundPayment.totalPrice)
                {
                    mCheckChangedPrice = true;
                    setResult(BaseActivity.RESULT_CODE_REFRESH);

                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_payment_changed_price)//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface)
                            {
                                mNeedOverwritePrice = true;
                                setRefresh(true);
                                onRefresh(true);
                            }
                        });
                } else if (mStayOutboundPayment.availableRooms == 0) // 솔드 아웃인 경우
                {
                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_outbound_payment_sold_out)//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                                setResult(BaseActivity.RESULT_CODE_REFRESH);
                                onBackClick();
                            }
                        });
                }

                unLockAll();

                mAnalytics.onEventNotAvailableCoupon(getActivity(), mStayName, mRoomPrice);
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

        getMaxCouponAmount(mStayBookDateTime, mStayIndex, mRateCode, mRateKey, mRoomTypeCode, mRoomBedTypeId, mPeople, mVendorType);
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
                                setSaleType(hasDepositSticker() ? STICKER : NONE);

                                notifyStayOutboundPaymentChanged();
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
                    Intent intent = SelectStayOutboundCouponDialogActivity.newInstance(getActivity()//
                        , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , mStayIndex, mStayName, mStayOutboundPayment.rateCode//
                        , mStayOutboundPayment.rateKey, mStayOutboundPayment.roomTypeCode, mStayOutboundPayment.roomBedTypeId//
                        , mPeople.numberOfAdults, mPeople.getChildAgeList(), mVendorType);

                    startActivityForResult(intent, StayOutboundPaymentActivity.REQUEST_CODE_COUPON_LIST);

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

                                notifyStayOutboundPaymentChanged();

                                mAnalytics.onEventCouponClick(getActivity(), false);
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
                    notifyStayOutboundPaymentChanged();
                } else
                {
                    if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundPhonePaymentEnabled() == false)
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
                                    notifyStayOutboundPaymentChanged();
                                }
                            }, null);
                    }
                }
                break;
        }

        unLockAll();
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
            , StayOutboundPaymentActivity.REQUEST_CODE_CARD_MANAGER);
    }

    @Override
    public void onRegisterEasyCardClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(RegisterCreditCardActivity.newInstance(getActivity()), StayOutboundPaymentActivity.REQUEST_CODE_REGISTER_CARD);
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
    public synchronized void onPaymentClick(String firstName, String lastName, String phone, String email)
    {
        if (lock() == true)
        {
            return;
        }

        if (mGuest == null)
        {
            mGuest = new OverseasGuest();
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

        if (DailyTextUtils.validEmail(email) == false)
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

    @Override
    public void onAgreedThirdPartyTermsClick(boolean checked)
    {
        mAgreedThirdPartyTerms = checked;
    }

    private void checkDuplicatePayment()
    {
        screenLock(true);

        JSONObject jsonObject = getPaymentJSONObject(null, mStayBookDateTime//
            , mRateCode, mRateKey, mRoomTypeCode, mRoomBedTypeId, mPeople//
            , mSaleType, mUserSimpleInformation.bonus, mSelectedCoupon, mGuest, mStayOutboundPayment.totalPrice, mVendorType, null);

        addCompositeDisposable(mPaymentRemoteImpl.getStayOutboundHasDuplicatePayment(getActivity(), mStayIndex, jsonObject).subscribe(new Consumer<String>()
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
        // 보너스로만 결제하는 경우
        if (mSaleType == BONUS && mStayOutboundPayment.totalPrice <= mUserSimpleInformation.bonus//
            || (mSaleType == COUPON && mStayOutboundPayment.totalPrice <= mSelectedCoupon.amount))
        {
            // 보너스로만 결제할 경우에는 팝업이 기존의 카드 타입과 동일한다.
            getViewInterface().showAgreeTermDialog(DailyBookingPaymentTypeView.PaymentType.FREE, new View.OnClickListener()
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
                    , StayOutboundPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT);

                mAnalytics.onEventStartPayment(getActivity(), DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
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

    synchronized void onAgreedPaymentClick()
    {
        if (lock() == true)
        {
            return;
        }

        screenLock(true);

        // 입력된 내용을 저장한다.
        DailyUserPreference.getInstance(getActivity()).setOverseasInformation(mGuest.firstName, mGuest.lastName, mGuest.phone, mGuest.email);

        if ((mSaleType == BONUS && mStayOutboundPayment.totalPrice <= mUserSimpleInformation.bonus)//
            || (mSaleType == COUPON && mStayOutboundPayment.totalPrice <= mSelectedCoupon.amount))
        {
            final String PAYMENT_TYPE;

            switch (mSaleType)
            {
                case BONUS:
                    PAYMENT_TYPE = "BONUS";
                    break;

                case COUPON:
                    PAYMENT_TYPE = "COUPON";
                    break;

                default:
                    unLockAll();
                    return;
            }

            JSONObject jsonObject = getPaymentJSONObject(PAYMENT_TYPE, mStayBookDateTime//
                , mRateCode, mRateKey, mRoomTypeCode, mRoomBedTypeId, mPeople//
                , mSaleType, mUserSimpleInformation.bonus, mSelectedCoupon, mGuest, mStayOutboundPayment.totalPrice, mVendorType, null);

            addCompositeDisposable(mPaymentRemoteImpl.getStayOutboundPaymentTypeFree(getActivity(), mStayIndex, PAYMENT_TYPE.toLowerCase(), jsonObject).subscribe(new Consumer<PaymentResult>()
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

            mAnalytics.onEventStartPayment(getActivity(), DailyBookingPaymentTypeView.PaymentType.FREE);
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

                    final String PAYMENT_TYPE = "ONE_CLICK";

                    JSONObject jsonObject = getPaymentJSONObject(PAYMENT_TYPE, mStayBookDateTime//
                        , mRateCode, mRateKey, mRoomTypeCode, mRoomBedTypeId, mPeople//
                        , mSaleType, mUserSimpleInformation.bonus, mSelectedCoupon, mGuest, mStayOutboundPayment.totalPrice//
                        , mVendorType, mSelectedCard.billKey);

                    addCompositeDisposable(mPaymentRemoteImpl.getStayOutboundPaymentTypeEasy(getActivity(), mStayIndex, jsonObject).subscribe(new Consumer<PaymentResult>()
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

                    mAnalytics.onEventStartPayment(getActivity(), DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
                    break;
                }

                case CARD:
                {
                    final String PAYMENT_TYPE = "CREDIT_CARD";

                    JSONObject jsonObject = getPaymentJSONObject(PAYMENT_TYPE, mStayBookDateTime//
                        , mRateCode, mRateKey, mRoomTypeCode, mRoomBedTypeId, mPeople//
                        , mSaleType, mUserSimpleInformation.bonus, mSelectedCoupon, mGuest, mStayOutboundPayment.totalPrice, mVendorType, null);

                    startActivityForResult(PaymentWebActivity.newInstance(getActivity()//
                        , getWebPaymentUrl(mStayIndex, "card"), jsonObject.toString(), AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_PROCESS_OUTBOUND)//
                        , StayOutboundPaymentActivity.REQUEST_CODE_PAYMENT_WEB_CARD);

                    mAnalytics.onEventStartPayment(getActivity(), DailyBookingPaymentTypeView.PaymentType.CARD);
                    break;
                }

                case PHONE:
                {
                    final String PAYMENT_TYPE = "MOBILE_PHONE";

                    JSONObject jsonObject = getPaymentJSONObject(PAYMENT_TYPE, mStayBookDateTime//
                        , mRateCode, mRateKey, mRoomTypeCode, mRoomBedTypeId, mPeople//
                        , mSaleType, mUserSimpleInformation.bonus, mSelectedCoupon, mGuest, mStayOutboundPayment.totalPrice, mVendorType, null);

                    startActivityForResult(PaymentWebActivity.newInstance(getActivity()//
                        , getWebPaymentUrl(mStayIndex, "mobile"), jsonObject.toString(), AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_PROCESS_OUTBOUND)//
                        , StayOutboundPaymentActivity.REQUEST_CODE_PAYMENT_WEB_PHONE);

                    mAnalytics.onEventStartPayment(getActivity(), DailyBookingPaymentTypeView.PaymentType.PHONE);
                    break;
                }
            }
        }
    }

    void startThankYou(String aggregationId, boolean fullBonus)
    {
        // ThankYou 페이지를 홈탭에서 띄우기 위한 코드
        startActivity(DailyInternalDeepLink.getHomeScreenLink(getActivity()));

        String descriptionTitle;
        String descriptionMessage;

        if (mSaleType == STICKER)
        {
            descriptionTitle = getString(R.string.message_payment_reward_sticker_deposit_after_checkout, mStayOutboundPayment.providableRewardStickerCount);
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

        startActivityForResult(StayOutboundThankYouActivity.newInstance(getActivity(), mStayName, mImageUrl//
            , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mStayOutboundPayment.checkInTime, mStayOutboundPayment.checkOutTime, mRoomType, aggregationId//
            , descriptionTitle, descriptionMessage//
            , mSaleType != STICKER, mAnalytics.getThankYouAnalyticsParam(mPaymentType, fullBonus, mSaleType, mSelectedCard != null, mStayIndex)) //
            , StayOutboundPaymentActivity.REQUEST_CODE_THANK_YOU);

        mAnalytics.onScreenPaymentCompleted(getActivity(), mStayOutboundPayment, mStayBookDateTime, mStayName//
            , mPaymentType, mSaleType, mSelectedCard != null, mUserSimpleInformation, aggregationId);
        mAnalytics.onEventEndPayment(getActivity(), mPaymentType);
        mAnalytics.onEventVendorType(getActivity(), mStayIndex, mVendorType);
    }

    private JSONObject getPaymentJSONObject(String paymentType, StayBookDateTime stayBookDateTime//
        , String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people//
        , int saleType, int bonus, Coupon coupon, OverseasGuest guest, int totalPrice, String vendorType, String billingKey)
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

            switch (saleType)
            {
                case BONUS:
                    jsonObject.put("bonusAmount", bonus > totalPrice ? totalPrice : bonus);
                    jsonObject.put("rewardSticker", false);
                    break;

                case COUPON:
                    jsonObject.put("couponCode", coupon.couponCode);
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

            jsonObject.put("firstName", guest.firstName);
            jsonObject.put("lastName", guest.lastName);
            jsonObject.put("email", guest.email);
            jsonObject.put("phoneNumber", guest.phone.replace("-", ""));

            if (DailyTextUtils.isTextEmpty(paymentType) == false)
            {
                jsonObject.put("paymentType", paymentType);
            }

            jsonObject.put("total", totalPrice);
            jsonObject.put("vendorType", vendorType);

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

    private String getWebPaymentUrl(int stayIndex, String paymentType)
    {
        final String API = Constants.UNENCRYPTED_URL ? "outbound/hotels/{hotelId}/room-reservation-payments/{type}/pay"//
            : "MTAwJDUzJDY0JDE1NyQzNSQ0MSQ5NyQxNDUkODEkNTUkMjMkMTIkMTI5JDc2JDkwJDE2NiQ=$Qjc0RkY3QzJEUNkQ2NTkzMENYBMjI5OEUwNUVQGMEI2CMDAzMDFCMzNBOHEUVGMjQzQjAUwNDlGONTMyNjVFMUjg5RGUE4NzU5NDBCRXUI5REFEPMUZGN0QyQUY4NDhDOUIRwRjI0RUFCMDVDRUVCNEVZCRTFBNEUyQkZDIODTZDRkQ0NUY4Q0MzQkQ=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{hotelId}", Integer.toString(stayIndex));
        urlParams.put("{type}", paymentType);

        String url;

        if (Constants.DEBUG == true)
        {
            url = DailyPreference.getInstance(getActivity()).getBaseOutBoundUrl()//
                + Crypto.getUrlDecoderEx(API, urlParams);
        } else
        {
            url = Crypto.getUrlDecoderEx(Setting.getOutboundServerUrl())//
                + Crypto.getUrlDecoderEx(API, urlParams);
        }

        return url;
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

    void onBookingInformation(StayOutboundPayment stayOutboundPayment, StayBookDateTime stayBookDateTime)
    {
        if (stayOutboundPayment == null || stayBookDateTime == null)
        {
            return;
        }

        final String DATE_FORMAT = "yyyy.MM.dd(EEE)";

        try
        {
            String checkInDate = stayBookDateTime.getCheckInDateTime(DATE_FORMAT);

            SpannableString checkInDateSpannableString = new SpannableString(checkInDate + " " + stayOutboundPayment.checkInTime);
            checkInDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getMediumTypeface()),//
                checkInDate.length(), checkInDate.length() + stayOutboundPayment.checkInTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String checkOutDate = stayBookDateTime.getCheckOutDateTime(DATE_FORMAT);

            SpannableString checkOutDateSpannableString = new SpannableString(checkOutDate + " " + stayOutboundPayment.checkOutTime);
            checkOutDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getMediumTypeface()),//
                checkOutDate.length(), checkOutDate.length() + stayOutboundPayment.checkOutTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            getViewInterface().setBooking(checkInDateSpannableString, checkOutDateSpannableString, mStayBookDateTime.getNights(), mStayName, mRoomType);

            if (StayOutboundRoom.VENDOR_TYPE_FIT_RUUMS.equalsIgnoreCase(mVendorType) == true)
            {
                getViewInterface().setVendorName(getString(R.string.label_stay_outbound_payment_third_party_fitruums_vendor));
            } else
            {
                getViewInterface().setVendorName(getString(R.string.label_stay_outbound_payment_third_party_ean_vendor));
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    void onRewardStickerInformation(StayOutboundPayment stayOutboundPayment, StayBookDateTime stayBookDateTime)
    {
        if (stayOutboundPayment == null || stayBookDateTime == null)
        {
            return;
        }

        // 리워드
        if (stayOutboundPayment.activeReward == true)
        {
            getViewInterface().setCheeringMessageVisible(true);

            if (stayOutboundPayment.provideRewardSticker == true)
            {
                getViewInterface().setCheeringMessage(true//
                    , getString(R.string.message_booking_reward_cheering_title01, stayOutboundPayment.providableRewardStickerCount)//
                    , getString(R.string.message_booking_reward_cheering_warning));

                getViewInterface().setDepositStickerVisible(true);
                getViewInterface().setDepositStickerCardVisible(true);

                if (mSaleType == NONE)
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

    void notifyStayOutboundPaymentChanged()
    {
        if (mUserSimpleInformation == null || mStayOutboundPayment == null || mStayBookDateTime == null)
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
                    paymentPrice = mStayOutboundPayment.totalPrice;
                    discountPrice = 0;

                    getViewInterface().setBonus(false, mUserSimpleInformation.bonus, 0);
                    getViewInterface().setCoupon(false, 0, false);
                    getViewInterface().setDepositSticker(false);
                    break;

                case BONUS:
                    paymentPrice = mStayOutboundPayment.totalPrice - mUserSimpleInformation.bonus;
                    discountPrice = paymentPrice < 0 ? mStayOutboundPayment.totalPrice : mUserSimpleInformation.bonus;

                    getViewInterface().setBonus(true, mUserSimpleInformation.bonus, discountPrice);
                    getViewInterface().setCoupon(false, 0, false);
                    getViewInterface().setDepositSticker(false);
                    break;

                case COUPON:
                    paymentPrice = mStayOutboundPayment.totalPrice - mSelectedCoupon.amount;
                    discountPrice = paymentPrice < 0 ? mStayOutboundPayment.totalPrice : mSelectedCoupon.amount;

                    getViewInterface().setBonus(false, mUserSimpleInformation.bonus, 0);
                    getViewInterface().setCoupon(true, mSelectedCoupon.amount, mSelectedCoupon.type == Coupon.Type.REWARD);
                    getViewInterface().setDepositSticker(false);
                    break;

                case STICKER:
                default:
                    // 기본이 스티커 적립 상태이다.
                    paymentPrice = mStayOutboundPayment.totalPrice;
                    discountPrice = 0;

                    getViewInterface().setBonus(false, mUserSimpleInformation.bonus, 0);
                    getViewInterface().setCoupon(false, 0, false);
                    getViewInterface().setDepositSticker(hasDepositSticker());
                    break;
            }

            setDepositStickerCard(mStayOutboundPayment, mStayBookDateTime);

            getViewInterface().setStayOutboundPayment(mStayBookDateTime.getNights(), mStayOutboundPayment.totalPrice//
                , discountPrice, mStayOutboundPayment.feeTotalAmountUsd);

            if (paymentPrice <= 0)
            {
                setPaymentType(DailyBookingPaymentTypeView.PaymentType.FREE);
                getViewInterface().setPaymentType(DailyBookingPaymentTypeView.PaymentType.FREE);
            } else
            {
                boolean easyCardEnabled = true;
                boolean cardEnabled = true;
                boolean phoneEnabled = true;
                //                boolean vBankEnabled = true;

                if (hasDepositSticker() == true)
                {
                    switch (mSaleType)
                    {
                        case NONE:
                            easyCardEnabled = false;
                            cardEnabled = false;
                            //                            vBankEnabled = false;
                            break;

                        case BONUS:
                            break;

                        // 쿠폰 미지원
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
                }

                // 리모트에서 조절되는 부분
                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundSimpleCardPaymentEnabled() == false)
                {
                    easyCardEnabled = false;
                }

                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundCardPaymentEnabled() == false)
                {
                    cardEnabled = false;
                }

                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundPhonePaymentEnabled() == false)
                {
                    phoneEnabled = false;
                }

                //                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStayOutboundVirtualPaymentEnabled() == false)
                //                {
                //                    vBankEnabled = false;
                //                }

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
                //                getViewInterface().setPaymentTypeEnabled(DailyBookingPaymentTypeView.PaymentType.VBANK, vBankEnabled);

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
                    //                } else if (mPaymentType == DailyBookingPaymentTypeView.PaymentType.VBANK && vBankEnabled == true)
                    //                {
                    //                    setPaymentType(DailyBookingPaymentTypeView.PaymentType.VBANK);
                    //                    getViewInterface().setPaymentType(DailyBookingPaymentTypeView.PaymentType.VBANK);
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
                        //                }
                        //                else if (vBankEnabled == true)
                        //                {
                        //                    setPaymentType(DailyBookingPaymentTypeView.PaymentType.VBANK);
                        //                    getViewInterface().setPaymentType(DailyBookingPaymentTypeView.PaymentType.VBANK);
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

    void notifyRefundPolicyChanged()
    {
        if (mStayOutboundPayment == null)
        {
            return;
        }

        getViewInterface().setRefundPolicyList(mStayOutboundPayment.getRefundPolicyList(), hasDepositSticker(), mStayOutboundPayment.nonRefundable);
    }

    void setStayOutboundPayment(StayOutboundPayment stayOutboundPayment)
    {
        mStayOutboundPayment = stayOutboundPayment;
    }

    void setUserInformation(UserSimpleInformation userSimpleInformation)
    {
        mUserSimpleInformation = userSimpleInformation;
    }

    OverseasGuest getGuestInformation(UserSimpleInformation userSimpleInformation)
    {
        OverseasGuest guest = new OverseasGuest();

        guest.firstName = DailyUserPreference.getInstance(getActivity()).getOverseasFirstName();
        guest.lastName = DailyUserPreference.getInstance(getActivity()).getOverseasLastName();
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
                guest.email = userSimpleInformation.email;
            }
        }

        return guest;
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
        if (coupon == null || mStayOutboundPayment == null)
        {
            mSelectedCoupon = null;
            setSaleType(hasDepositSticker() ? STICKER : NONE);

            notifyStayOutboundPaymentChanged();
            return;
        }

        if (coupon.amount > mStayOutboundPayment.totalPrice)
        {
            String difference = DailyTextUtils.getPriceFormat(getActivity(), (coupon.amount - mStayOutboundPayment.totalPrice), false);

            getViewInterface().showSimpleDialog(null, getString(R.string.message_over_coupon_price, difference)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        setSaleType(COUPON);
                        mSelectedCoupon = coupon;

                        notifyStayOutboundPaymentChanged();
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mSelectedCoupon = null;
                        setSaleType(hasDepositSticker() ? STICKER : NONE);

                        notifyStayOutboundPaymentChanged();
                    }
                }, new DialogInterface.OnCancelListener()
                {

                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        mSelectedCoupon = null;
                        setSaleType(hasDepositSticker() ? STICKER : NONE);

                        notifyStayOutboundPaymentChanged();
                    }
                }, null, true);

        } else
        {
            // 호텔 결제 정보에 쿠폰 가격 넣고 텍스트 업데이트 필요
            setSaleType(COUPON);
            mSelectedCoupon = coupon;

            notifyStayOutboundPaymentChanged();
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

    void notifyGuestInformationChanged(OverseasGuest guest)
    {
        if (guest == null || mPeople == null)
        {
            return;
        }

        getViewInterface().setGuestInformation(guest.lastName, guest.firstName, guest.phone, guest.email);
        getViewInterface().setPeople(mPeople);
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
            if (mStayOutboundPayment != null && mStayOutboundPayment.totalPrice <= MIN_AMOUNT_FOR_BONUS_USAGE)
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
    }

    private void setDepositStickerCard(StayOutboundPayment stayOutboundPayment, StayBookDateTime stayBookDateTime)
    {
        if (stayOutboundPayment == null || stayBookDateTime == null)
        {
            return;
        }

        if (mSaleType == STICKER)
        {
            getViewInterface().setDepositStickerCard(DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigRewardStickerCardTitleMessage()//
                , stayOutboundPayment.rewardStickerCount, null, getString(R.string.message_payment_reward_sticker_deposit_after_checkout, stayOutboundPayment.providableRewardStickerCount), false);
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
                    , stayOutboundPayment.rewardStickerCount, null, spannableString, true);
            }
        }
    }

    boolean hasDepositSticker()
    {
        return mStayOutboundPayment != null && mStayOutboundPayment.activeReward == true && mStayOutboundPayment.provideRewardSticker == true;
    }

    private void onPaymentWebResult(int resultCode, Intent data)
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
                    notifyStayOutboundPaymentChanged();
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

                    paymentResult.bookingIndex = dataJSONObject.getInt("reservationIdx");
                    paymentResult.aggregationId = dataJSONObject.getString("aggregationId");
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
                    startThankYou(paymentResult.aggregationId, false);
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

    void getMaxCouponAmount(StayBookDateTime stayBookDateTime, int stayIndex, String rateCode, String rateKey, String roomTypeCode, int roomBedTypeId, People people, String vendorType)
    {
        if (stayBookDateTime == null)
        {
            return;
        }

        final String DATE_FORMAT = "yyyy-MM-dd";

        addCompositeDisposable(mCouponRemoteImpl.getStayOutboundCouponListByPayment(getActivity(), stayBookDateTime.getCheckInDateTime(DATE_FORMAT)//
            , stayBookDateTime.getCheckOutDateTime(DATE_FORMAT), stayIndex, rateCode, rateKey, roomTypeCode, roomBedTypeId, people, vendorType).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Coupons>()
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

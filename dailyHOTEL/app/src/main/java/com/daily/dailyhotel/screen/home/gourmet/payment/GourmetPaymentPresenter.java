package com.daily.dailyhotel.screen.home.gourmet.payment;


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
import com.daily.dailyhotel.entity.Coupons;
import com.daily.dailyhotel.entity.DomesticGuest;
import com.daily.dailyhotel.entity.GourmetCart;
import com.daily.dailyhotel.entity.GourmetCartMenu;
import com.daily.dailyhotel.entity.GourmetPayment;
import com.daily.dailyhotel.entity.GourmetPaymentMenu;
import com.daily.dailyhotel.entity.PaymentResult;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserSimpleInformation;
import com.daily.dailyhotel.parcel.CouponParcel;
import com.daily.dailyhotel.parcel.GourmetCartParcel;
import com.daily.dailyhotel.parcel.analytics.GourmetPaymentAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.GourmetThankYouAnalyticsParam;
import com.daily.dailyhotel.repository.local.CartLocalImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.CouponRemoteImpl;
import com.daily.dailyhotel.repository.remote.PaymentRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.payment.PaymentWebActivity;
import com.daily.dailyhotel.screen.home.gourmet.thankyou.GourmetThankYouActivity;
import com.daily.dailyhotel.screen.mydaily.coupon.dialog.SelectGourmetCouponDialogActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.screen.mydaily.creditcard.CreditCardListActivity;
import com.twoheart.dailyhotel.screen.mydaily.creditcard.RegisterCreditCardActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.AddProfileSocialActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.InputMobileNumberDialogActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
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
import io.reactivex.functions.Function5;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetPaymentPresenter extends BaseExceptionPresenter<GourmetPaymentActivity, GourmetPaymentInterface> implements GourmetPaymentView.OnEventListener
{
    private static final int MIN_AMOUNT_FOR_BONUS_USAGE = 20000; // 보너스를 사용하기 위한 최소 주문 가격

    // 1000원 미만 결제시에 간편/일반 결제 불가 - 쿠폰 또는 적립금 전체 사용이 아닌경우 조건 추가
    private static final int CARD_MIN_PRICE = 1000;
    private static final int PHONE_MAX_PRICE = 500000;

    private static final int MAX_PERSONS = 99;

    static final int NONE = 0;
    static final int BONUS = 1;
    static final int COUPON = 2;
    static final int STICKER = 3;

    // 서버로 해당 문자열 그대로 보냄.(수정 금지)
    @IntDef({NONE, BONUS, COUPON, STICKER})
    @interface SaleType
    {
    }

    GourmetPaymentAnalyticsInterface mAnalytics;

    private PaymentRemoteImpl mPaymentRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;
    private CouponRemoteImpl mCouponRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;
    CartLocalImpl mCartLocalImpl;

    GourmetPayment mGourmetPayment;
    Card mSelectedCard;
    DomesticGuest mGuest;
    Coupon mSelectedCoupon;
    DailyBookingPaymentTypeView.PaymentType mPaymentType;
    boolean mAgreedThirdPartyTerms;
    boolean mGuestInformationVisible;
    UserSimpleInformation mUserSimpleInformation;
    int mSaleType;
    GourmetCart mGourmetCart;
    int mPersons;
    private int mMaxCouponAmount;
    boolean mCheckChangedPrice;
    boolean mNeedOverwritePrice;

    // ***************************************************************** //
    // ************** 변수 선언시에 onSaveInstanceState 에 꼭 등록해야하는지 판단한다.
    // ************** 클래스는 해당 내부 멤버 변수들이 onSaveInstance에 잘처리되고 있는지 확인한다.
    // ***************************************************************** //


    public interface GourmetPaymentAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(GourmetPaymentAnalyticsParam analyticsParam);

        GourmetPaymentAnalyticsParam getAnalyticsParam();

        void onScreen(Activity activity, GourmetCart gourmetCart, GourmetPayment gourmetPayment, boolean registerEasyCard);

        void onScreenAgreeTermDialog(Activity activity, GourmetCart gourmetCart//
            , GourmetPayment gourmetPayment, boolean registerEasyCard, int saleType//
            , Coupon coupon, DailyBookingPaymentTypeView.PaymentType paymentType, UserSimpleInformation userSimpleInformation);

        void onScreenPaymentCompleted(Activity activity, String aggregationId);

        void onEventChangedPrice(Activity activity, String gourmetName);

        void onEventSoldOut(Activity activity, String gourmetName);

        void onEventBonusClick(Activity activity, boolean selected, int bonus);

        void onEventCouponClick(Activity activity, boolean selected);

        void onEventCallClick(Activity activity);

        void onEventCall(Activity activity, boolean call);

        void onEventAgreedThirdPartyClick(Activity activity);

        void onEventEasyCardManagerClick(Activity activity, boolean hasEasyCard);

        void onEventAgreedTermCancelClick(Activity activity);

        void onEventStartPayment(Activity activity, DailyBookingPaymentTypeView.PaymentType paymentType);

        void onEventAgreedTermClick(Activity activity, GourmetCart gourmetCart);

        GourmetThankYouAnalyticsParam getThankYouAnalyticsParam();

        void setPaymentParam(HashMap<String, String> param);

        HashMap<String, String> getPaymentParam();
    }

    public GourmetPaymentPresenter(@NonNull GourmetPaymentActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected GourmetPaymentInterface createInstanceViewInterface()
    {
        return new GourmetPaymentView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(GourmetPaymentActivity activity)
    {
        setContentView(R.layout.activity_gourmet_payment_data);

        mAnalytics = new GourmetPaymentAnalyticsImpl();

        mPaymentRemoteImpl = new PaymentRemoteImpl();
        mProfileRemoteImpl = new ProfileRemoteImpl();
        mCouponRemoteImpl = new CouponRemoteImpl();
        mCommonRemoteImpl = new CommonRemoteImpl();
        mCartLocalImpl = new CartLocalImpl();

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        GourmetCartParcel gourmetCartParcel = intent.getParcelableExtra(GourmetPaymentActivity.INTENT_EXTRA_DATA_GOURMET_CART);

        if (gourmetCartParcel == null)
        {
            return false;
        }

        mGourmetCart = gourmetCartParcel.getGourmetCart();

        if (mGourmetCart == null || mGourmetCart.getMenuCount() == 0)
        {
            return false;
        }

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

        getViewInterface().setOverseas(false);
        getViewInterface().setPersons(0);

        if (DailyHotel.isLogin() == false)
        {
            setRefresh(false);
        }
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

        if (DailyHotel.isLogin() == false)
        {
            setRefresh(false);

            startLogin();
        }
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

        outState.putParcelable("gourmetCart", new GourmetCartParcel(mGourmetCart));

        if (mPaymentType != null)
        {
            outState.putString("paymentType", mPaymentType.name());
        }

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
            outState.putBundle("gourmetPayment", Util.getClassPublicFieldsBundle(GourmetPayment.class, mGourmetPayment));
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

        GourmetCartParcel gourmetCartParcel = savedInstanceState.getParcelable("gourmetCart");
        mGourmetCart = gourmetCartParcel.getGourmetCart();

        try
        {
            mPaymentType = DailyBookingPaymentTypeView.PaymentType.valueOf(savedInstanceState.getString("paymentType"));
        } catch (Exception e)
        {
            mPaymentType = DailyBookingPaymentTypeView.PaymentType.EASY_CARD;
        }

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
            mGourmetPayment = (GourmetPayment) Util.setClassPublicFieldsBundle(GourmetPayment.class, savedInstanceState.getBundle("gourmetPayment"));
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
            case GourmetPaymentActivity.REQUEST_CODE_CARD_MANAGER:
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

            case GourmetPaymentActivity.REQUEST_CODE_REGISTER_CARD:
            case GourmetPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT:
            {
                // 간편 결제 실행후 카드가 없어 등록후에 돌아온경우.
                String msg = null;

                switch (resultCode)
                {
                    case Constants.CODE_RESULT_PAYMENT_BILLING_SUCCSESS:
                        if (requestCode == GourmetPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT)
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

            case GourmetPaymentActivity.REQUEST_CODE_REGISTER_PHONE_NUMBER:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    String mobile = data.getStringExtra(InputMobileNumberDialogActivity.INTENT_EXTRA_MOBILE_NUMBER);
                    notifyGuestMobileInformationChanged(mobile);
                }
                break;

            case GourmetPaymentActivity.REQUEST_CODE_PAYMENT_WEB_CARD:
            case GourmetPaymentActivity.REQUEST_CODE_PAYMENT_WEB_PHONE:
            case GourmetPaymentActivity.REQUEST_CODE_PAYMENT_WEB_VBANK:
                onPaymentWebResult(mPaymentType, resultCode, data);
                break;

            case GourmetPaymentActivity.REQUEST_CODE_COUPON_LIST:
                if (data != null)
                {
                    int maxCouponAmount = data.getIntExtra(SelectGourmetCouponDialogActivity.INTENT_EXTRA_MAX_COUPON_AMOUNT, 0);
                    setMaxCouponAmount(maxCouponAmount, false);

                    if (resultCode == Activity.RESULT_OK)
                    {
                        CouponParcel couponParcel = data.getParcelableExtra(SelectGourmetCouponDialogActivity.INTENT_EXTRA_SELECT_COUPON);
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

            case GourmetPaymentActivity.REQUEST_CODE_CALL:
                mAnalytics.onEventCall(getActivity(), resultCode == Activity.RESULT_OK);
                break;

            case GourmetPaymentActivity.REQUEST_CODE_LOGIN_IN:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    setRefresh(true);
                    onRefresh(true);
                } else
                {
                    onBackClick();
                }
                break;
            }

            case GourmetPaymentActivity.REQUEST_CODE_PROFILE_UPDATE:
                if (resultCode == Activity.RESULT_OK)
                {
                    setRefresh(true);
                } else
                {
                    onBackClick();
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

        addCompositeDisposable(Observable.zip(mPaymentRemoteImpl.getGourmetPayment(mGourmetCart)//
            , mPaymentRemoteImpl.getEasyCardList(), mProfileRemoteImpl.getProfile(), mProfileRemoteImpl.getUserSimpleInformation()//
            , mCommonRemoteImpl.getCommonDateTime()//
            , new Function5<GourmetPayment, List<Card>, User, UserSimpleInformation, CommonDateTime, Pair<CommonDateTime, User>>()
            {
                @Override
                public Pair<CommonDateTime, User> apply(@io.reactivex.annotations.NonNull GourmetPayment gourmetPayment//
                    , @io.reactivex.annotations.NonNull List<Card> cardList//
                    , @io.reactivex.annotations.NonNull User user//
                    , @io.reactivex.annotations.NonNull UserSimpleInformation userSimpleInformation//
                    , @io.reactivex.annotations.NonNull CommonDateTime commonDateTime) throws Exception
                {
                    setGourmetPayment(gourmetPayment);
                    setSelectCard(getSelectedCard(cardList));
                    setUserInformation(userSimpleInformation);

                    return new Pair<CommonDateTime, User>(commonDateTime, user);
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Pair<CommonDateTime, User>>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Pair<CommonDateTime, User> pair) throws Exception
            {
                if (mNeedOverwritePrice == true)
                {
                    mNeedOverwritePrice = false;
                    overwriteGourmetCartPrice(mGourmetPayment, mGourmetCart);
                    getViewInterface().scrollToCheckPriceTitle();

                    setResult(BaseActivity.RESULT_CODE_REFRESH);
                    addCompositeDisposable(mCartLocalImpl.clearGourmetCart(getActivity()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                    {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception
                        {
                            // do nothing!
                        }
                    }));
                }

                onBookingInformation(mGourmetPayment, mGourmetCart);

                int[] menuIndexes = mGourmetCart.getMenuSaleIndexes();
                int[] menuCounts = mGourmetCart.getCountPerMenu();
                getMaxCouponAmount(menuIndexes, menuCounts);

                notifyUserInformationChanged();

                notifyCardEventChanged(pair.first);
                notifyBonusEnabledChanged();
                notifyPaymentTypeChanged();
                notifyEasyCardChanged();
                notifyGourmetPaymentChanged();

                // 가격이 변동된 경우
                if (mCheckChangedPrice == false && checkChangedPrice(mGourmetPayment, mGourmetCart) == true)
                {
                    mCheckChangedPrice = true;
                    setResult(BaseActivity.RESULT_CODE_REFRESH);

                    addCompositeDisposable(mCartLocalImpl.clearGourmetCart(getActivity()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                    {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception
                        {
                            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_gourmet_payment_changed_price)//
                                , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                                {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface)
                                    {
                                        onBackClick();
                                    }

                                });
                        }
                    }));

                    mAnalytics.onEventChangedPrice(getActivity(), mGourmetCart.gourmetName);
                }

                mAnalytics.onScreen(getActivity(), mGourmetCart, mGourmetPayment, mSelectedCard != null);

                User user = pair.second;

                switch (Util.verifyUserInformation(user))
                {
                    case Util.VERIFY_USER:
                        break;

                    case Util.VERIFY_DAILY_USER_NOT_VERIFY_PHONE:
                        startActivityForResult(EditProfilePhoneActivity.newInstance(getActivity()//
                            , EditProfilePhoneActivity.Type.NEED_VERIFICATION_PHONENUMBER, user.phone)//
                            , GourmetPaymentActivity.REQUEST_CODE_PROFILE_UPDATE);
                        break;

                    case Util.VERIFY_SOCIAL_USER_NOT_VERIFY:
                    case Util.VERIFY_SOCIAL_USER_NOT_VERIFY_EMAIL:
                        startActivityForResult(AddProfileSocialActivity.newInstance(getActivity()//
                            , new Customer(user), user.birthday), GourmetPaymentActivity.REQUEST_CODE_PROFILE_UPDATE);
                        break;

                    case Util.VERIFY_SOCIAL_USER_NOT_VERIFY_PHONE:
                        startActivityForResult(EditProfilePhoneActivity.newInstance(getActivity()//
                            , EditProfilePhoneActivity.Type.WRONG_PHONENUMBER, user.phone)//
                            , GourmetPaymentActivity.REQUEST_CODE_PROFILE_UPDATE);
                        break;

                    default:
                        onBackClick();
                        break;
                }

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
                    // 특정 해외 드면 홈으로 이동한다.
                    if (((BaseException) throwable).getCode() == 1180)
                    {
                        setResult(BaseActivity.RESULT_CODE_BACK);
                    } else
                    {
                        setResult(BaseActivity.RESULT_CODE_REFRESH);
                    }

                    addCompositeDisposable(mCartLocalImpl.clearGourmetCart(getActivity()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                    {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception
                        {
                            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), throwable.getMessage()//
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

        startActivityForResult(CallDialogActivity.newInstance(getActivity()), GourmetPaymentActivity.REQUEST_CODE_CALL);

        mAnalytics.onEventCallClick(getActivity());
    }

    @Override
    public void onBonusClick(boolean selected)
    {
        if (lock() == true)
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
                            setSaleType(NONE);
                            mSelectedCoupon = null;

                            notifyGourmetPaymentChanged();

                            onBonusClick(true);
                        }
                    }, null);
                break;

            case STICKER:
                break;

            default:
                if (selected == true)
                {
                    setSaleType(BONUS);

                    notifyGourmetPaymentChanged();

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
                                setSaleType(NONE);

                                notifyGourmetPaymentChanged();

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
        if (lock() == true)
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
                            setSaleType(NONE);

                            notifyGourmetPaymentChanged();

                            onCouponClick(true);
                        }
                    }, null);
                break;

            case STICKER:
                break;

            default:
                if (selected == true)
                {
                    int[] menuIndexes = mGourmetCart.getMenuSaleIndexes();
                    int[] menuCounts = mGourmetCart.getCountPerMenu();

                    try
                    {
                        Intent intent = SelectGourmetCouponDialogActivity.newInstance(getActivity(), DailyCalendar.convertDateFormatString(mGourmetCart.visitTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"), mGourmetCart.gourmetIndex, mGourmetCart.gourmetName, menuIndexes, menuCounts);
                        startActivityForResult(intent, GourmetPaymentActivity.REQUEST_CODE_COUPON_LIST);
                    } catch (Exception e)
                    {
                        ExLog.e(e.toString());
                    }

                    mAnalytics.onEventCouponClick(getActivity(), true);
                } else
                {
                    getViewInterface().showSimpleDialog(null, getString(R.string.message_booking_cancel_coupon), getString(R.string.dialog_btn_text_yes), //
                        getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                setSaleType(NONE);

                                notifyGourmetPaymentChanged();
                            }
                        }, null);
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
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(CreditCardListActivity.newInstance(getActivity()//
            , mSelectedCard.name, mSelectedCard.number, mSelectedCard.billKey, mSelectedCard.cd)//
            , GourmetPaymentActivity.REQUEST_CODE_CARD_MANAGER);

        mAnalytics.onEventEasyCardManagerClick(getActivity(), mSelectedCard != null);
    }

    @Override
    public void onRegisterEasyCardClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(RegisterCreditCardActivity.newInstance(getActivity()), GourmetPaymentActivity.REQUEST_CODE_REGISTER_CARD);
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
        if (mGourmetCart == null || lock() == true)
        {
            return;
        }

        if (mGuest == null)
        {
            mGuest = new DomesticGuest();
        }

        // 방분자와 예약자가 정보가 다른 경우
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

        if (mPersons == 0)
        {
            DailyToast.showToast(getActivity(), R.string.message_gourmet_booking_please_input_visit_persons, DailyToast.LENGTH_SHORT);

            unLockAll();

            getViewInterface().scrollTop();
            return;
        }

        if (DailyTextUtils.isTextEmpty(mGuest.name) == true)
        {
            DailyToast.showToast(getActivity(), R.string.message_gourmet_please_input_guest, DailyToast.LENGTH_SHORT);

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

        confirmLastPayment();

        mAnalytics.onEventAgreedTermClick(getActivity(), mGourmetCart);
    }

    @Override
    public void onPhoneNumberClick(String phoneNumber)
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(InputMobileNumberDialogActivity.newInstance(getActivity(), phoneNumber)//
            , GourmetPaymentActivity.REQUEST_CODE_REGISTER_PHONE_NUMBER);
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

    @Override
    public void onPersonsPlusClick()
    {
        if (lock() == true)
        {
            return;
        }

        if (++mPersons >= MAX_PERSONS)
        {
            mPersons = MAX_PERSONS;

            getViewInterface().setPersonsPlusEnabled(false);
        } else
        {
            getViewInterface().setPersonsPlusEnabled(true);
        }

        getViewInterface().setPersonsMinusEnabled(true);

        getViewInterface().setPersons(mPersons);

        unLockAll();
    }

    @Override
    public void onPersonsMinusClick()
    {
        if (lock() == true)
        {
            return;
        }

        if (--mPersons <= 0)
        {
            mPersons = 0;

            getViewInterface().setPersonsMinusEnabled(false);
        } else
        {
            getViewInterface().setPersonsMinusEnabled(true);
        }

        getViewInterface().setPersonsPlusEnabled(true);

        getViewInterface().setPersons(mPersons);

        unLockAll();
    }

    private void confirmLastPayment()
    {
        screenLock(true);

        addCompositeDisposable(mPaymentRemoteImpl.getGourmetPayment(mGourmetCart).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetPayment>()
        {
            @Override
            public void accept(GourmetPayment gourmetPayment) throws Exception
            {
                unLockAll();

                showAgreementPopup();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                unLockAll();

                onReportError(throwable);

                if (throwable instanceof BaseException)
                {
                    // 특정 해외 드면 홈으로 이동한다.
                    if (((BaseException) throwable).getCode() == 1180)
                    {
                        setResult(BaseActivity.RESULT_CODE_BACK);
                    } else
                    {
                        setResult(BaseActivity.RESULT_CODE_REFRESH);
                    }

                    addCompositeDisposable(mCartLocalImpl.clearGourmetCart(getActivity()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                    {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception
                        {
                            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), throwable.getMessage()//
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

    void showAgreementPopup()
    {
        final int totalPrice = mGourmetPayment.totalPrice;

        // 보너스 / 쿠폰 (으)로만 결제하는 경우
        if ((mSaleType == BONUS && totalPrice <= mUserSimpleInformation.bonus)//
            || (mSaleType == COUPON && totalPrice <= mSelectedCoupon.amount))
        {
            // 보너스로만 결제할 경우에는 팝업이 기존의 카드 타입과 동일한다.
            getViewInterface().showAgreeTermDialog(DailyBookingPaymentTypeView.PaymentType.FREE//
                , getAgreedTermMessages(DailyBookingPaymentTypeView.PaymentType.FREE), new View.OnClickListener()
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
                    , GourmetPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT);

                mAnalytics.onEventStartPayment(getActivity(), DailyBookingPaymentTypeView.PaymentType.EASY_CARD);
            } else
            {
                getViewInterface().showAgreeTermDialog(mPaymentType, getAgreedTermMessages(mPaymentType), new View.OnClickListener()
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
        if (mGourmetCart == null || lock() == true)
        {
            return;
        }

        screenLock(true);

        String couponCode = mSelectedCoupon != null ? mSelectedCoupon.couponCode : null;

        int totalPrice = mGourmetPayment.totalPrice;

        // 보너스 / 쿠폰 (으)로만 결제하는 경우
        if ((mSaleType == BONUS && totalPrice <= mUserSimpleInformation.bonus)//
            || (mSaleType == COUPON && totalPrice <= mSelectedCoupon.amount))
        {
            JSONObject jsonObject = getPaymentJSONObject(mGourmetCart, mSaleType, mUserSimpleInformation.bonus, couponCode, mGuest, mPersons, totalPrice, null);

            addCompositeDisposable(mPaymentRemoteImpl.getGourmetPaymentTypeFree(jsonObject).subscribe(new Consumer<PaymentResult>()
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
                            , GourmetPaymentActivity.REQUEST_CODE_REGISTER_CARD_PAYMENT);
                        return;
                    }

                    JSONObject jsonObject = getPaymentJSONObject(mGourmetCart, mSaleType, mUserSimpleInformation.bonus, couponCode, mGuest, mPersons, totalPrice, mSelectedCard.billKey);

                    addCompositeDisposable(mPaymentRemoteImpl.getGourmetPaymentTypeEasy(jsonObject).subscribe(new Consumer<PaymentResult>()
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

                    JSONObject jsonObject = getPaymentJSONObject(mGourmetCart, mSaleType, mUserSimpleInformation.bonus, couponCode, mGuest, mPersons, totalPrice, null);

                    startActivityForResult(PaymentWebActivity.newInstance(getActivity()//
                        , getWebPaymentUrl(PAYMENT_TYPE), jsonObject.toString(), AnalyticsManager.Screen.DAILYGOURMET_PAYMENT_PROCESS)//
                        , GourmetPaymentActivity.REQUEST_CODE_PAYMENT_WEB_CARD);
                    break;
                }

                case PHONE:
                {
                    final String PAYMENT_TYPE = "mobile";

                    JSONObject jsonObject = getPaymentJSONObject(mGourmetCart, mSaleType, mUserSimpleInformation.bonus, couponCode, mGuest, mPersons, totalPrice, null);

                    startActivityForResult(PaymentWebActivity.newInstance(getActivity()//
                        , getWebPaymentUrl(PAYMENT_TYPE), jsonObject.toString(), AnalyticsManager.Screen.DAILYGOURMET_PAYMENT_PROCESS)//
                        , GourmetPaymentActivity.REQUEST_CODE_PAYMENT_WEB_PHONE);
                    break;
                }

                case VBANK:
                {
                    final String PAYMENT_TYPE = "vbank";

                    JSONObject jsonObject = getPaymentJSONObject(mGourmetCart, mSaleType, mUserSimpleInformation.bonus, couponCode, mGuest, mPersons, totalPrice, null);

                    startActivityForResult(PaymentWebActivity.newInstance(getActivity()//
                        , getWebPaymentUrl(PAYMENT_TYPE), jsonObject.toString(), AnalyticsManager.Screen.DAILYGOURMET_PAYMENT_PROCESS)//
                        , GourmetPaymentActivity.REQUEST_CODE_PAYMENT_WEB_VBANK);
                    break;
                }
            }
        }

        mAnalytics.onScreenAgreeTermDialog(getActivity(), mGourmetCart//
            , mGourmetPayment, mSelectedCard != null, mSaleType, mSelectedCoupon//
            , mPaymentType, mUserSimpleInformation);
    }

    void startThankYou(String aggregationId, boolean fullBonus)
    {
        try
        {
            mAnalytics.onScreenPaymentCompleted(getActivity(), aggregationId);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        addCompositeDisposable(mCartLocalImpl.clearGourmetCart(getActivity()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                // ThankYou 페이지를 홈탭에서 띄우기 위한 코드
                startActivity(DailyInternalDeepLink.getHomeScreenLink(getActivity()));

                startActivityForResult(GourmetThankYouActivity.newInstance(getActivity(), mGourmetCart, aggregationId//
                    , mPersons, mAnalytics.getThankYouAnalyticsParam())//
                    , GourmetPaymentActivity.REQUEST_CODE_THANK_YOU);
            }
        }));
    }

    private JSONObject getPaymentJSONObject(GourmetCart gourmetCart, int saleType, int bonus, String couponCode//
        , DomesticGuest guest, int visitPersons, int totalPrice, String billingKey)
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            switch (saleType)
            {
                case BONUS:
                    jsonObject.put("bonusAmount", bonus > totalPrice ? totalPrice : bonus);
                    break;

                case COUPON:
                    jsonObject.put("couponCode", couponCode);
                    break;

                default:
                    jsonObject.put("bonusAmount", 0);
                    break;
            }

            JSONArray bookingItemsJSONArray = new JSONArray();

            int menuCount = gourmetCart.getMenuCount();

            int[] menuSaleIndexes = gourmetCart.getMenuSaleIndexes();
            int[] countPerMenu = gourmetCart.getCountPerMenu();
            int[] discountPrices = gourmetCart.getDiscountPrices();

            for (int i = 0; i < menuCount; i++)
            {
                JSONObject bookingItem = new JSONObject();
                bookingItem.put("saleRecoIdx", menuSaleIndexes[i]);
                bookingItem.put("count", countPerMenu[i]);
                bookingItem.put("discount", discountPrices[i]);

                bookingItemsJSONArray.put(bookingItem);
            }

            jsonObject.put("bookingItems", bookingItemsJSONArray);

            JSONObject bookingGuestJSONObject = new JSONObject();
            bookingGuestJSONObject.put("arrivalDateTime", gourmetCart.visitTime);
            bookingGuestJSONObject.put("email", guest.email);
            bookingGuestJSONObject.put("name", guest.name);
            bookingGuestJSONObject.put("phone", guest.phone);
            bookingGuestJSONObject.put("numberOfGuest", visitPersons);

            jsonObject.put("bookingGuest", bookingGuestJSONObject);
            jsonObject.put("discountTotal", totalPrice);

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
        final String API = Constants.UNENCRYPTED_URL ? "api/v5/booking/gourmet/{type}"//
            : "ODQkMTYkODEkMjUkNzQkNjckMjEkNjgkNjUkOTIkOCQ1MiQzOCQzMyQxMDAkNzck$Njk2NTVCDMUM5QjMzZRUE5TMUY0HRkEyMKzMxQzJczMDRDNTEyMjcwPRTVBODQ5QkNGQzXk0MVGDkKzRTM0ZQjdCNEY0ORTZFMJOAS==$";

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


    void onBookingInformation(GourmetPayment gourmetPayment, GourmetCart gourmetCart)
    {
        if (gourmetPayment == null || gourmetCart == null)
        {
            return;
        }

        final String DATE_FORMAT = "yyyy.MM.dd(EEE)";

        try
        {
            String visitDay = DailyCalendar.convertDateFormatString(gourmetCart.visitTime, DailyCalendar.ISO_8601_FORMAT, DATE_FORMAT);
            String visitDateTime = visitDay + DailyCalendar.convertDateFormatString(gourmetCart.visitTime, DailyCalendar.ISO_8601_FORMAT, " HH:mm");

            SpannableString spannableString = new SpannableString(visitDateTime);
            spannableString.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getMediumTypeface()),//
                visitDay.length(), visitDateTime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            getViewInterface().setBooking(spannableString, mGourmetCart);
            getViewInterface().setVendorName(gourmetPayment.businessName);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    void notifyEasyCardChanged()
    {
        getViewInterface().setEasyCard(mSelectedCard);
    }

    void notifyGourmetPaymentChanged()
    {
        if (mUserSimpleInformation == null || mGourmetPayment == null)
        {
            return;
        }

        try
        {
            int paymentPrice, discountPrice;
            final int totalPrice = mGourmetPayment.totalPrice;

            switch (mSaleType)
            {
                case BONUS:
                    paymentPrice = totalPrice - mUserSimpleInformation.bonus;
                    discountPrice = paymentPrice < 0 ? totalPrice : mUserSimpleInformation.bonus;

                    getViewInterface().setBonus(true, mUserSimpleInformation.bonus, discountPrice);
                    getViewInterface().setCoupon(false, 0);
                    break;

                case COUPON:
                    paymentPrice = totalPrice - mSelectedCoupon.amount;
                    discountPrice = paymentPrice < 0 ? totalPrice : mSelectedCoupon.amount;

                    getViewInterface().setBonus(false, mUserSimpleInformation.bonus, 0);
                    getViewInterface().setCoupon(true, mSelectedCoupon.amount);
                    break;

                case STICKER:
                default:
                    paymentPrice = totalPrice;
                    discountPrice = 0;

                    getViewInterface().setBonus(false, mUserSimpleInformation.bonus, 0);
                    getViewInterface().setCoupon(false, 0);
                    break;
            }

            getViewInterface().setGourmetPayment(totalPrice, discountPrice);

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

                // 리모트에서 조절되는 부분
                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigGourmetSimpleCardPaymentEnabled() == false)
                {
                    easyCardEnabled = false;
                }

                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigGourmetCardPaymentEnabled() == false)
                {
                    cardEnabled = false;
                }

                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigGourmetPhonePaymentEnabled() == false)
                {
                    phoneEnabled = false;
                }

                if (DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigGourmetVirtualPaymentEnabled() == false)
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

    void setGourmetPayment(GourmetPayment gourmetPayment)
    {
        mGourmetPayment = gourmetPayment;
    }

    void setUserInformation(UserSimpleInformation userSimpleInformation)
    {
        mUserSimpleInformation = userSimpleInformation;
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
        if (coupon == null || mGourmetPayment == null)
        {
            setSaleType(NONE);
            mSelectedCoupon = null;

            notifyGourmetPaymentChanged();
            return;
        }

        final int totalPrice = mGourmetPayment.totalPrice;

        if (coupon.amount > totalPrice)
        {
            String difference = DailyTextUtils.getPriceFormat(getActivity(), (coupon.amount - totalPrice), false);

            getViewInterface().showSimpleDialog(null, getString(R.string.message_over_coupon_price, difference)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        setSaleType(COUPON);
                        mSelectedCoupon = coupon;

                        notifyGourmetPaymentChanged();
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        setSaleType(NONE);
                        mSelectedCoupon = null;

                        notifyGourmetPaymentChanged();
                    }
                }, new DialogInterface.OnCancelListener()
                {

                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        setSaleType(NONE);
                        mSelectedCoupon = null;

                        notifyGourmetPaymentChanged();
                    }
                }, null, true);

        } else
        {
            // 호텔 결제 정보에 쿠폰 가격 넣고 텍스트 업데이트 필요
            setSaleType(COUPON);
            mSelectedCoupon = coupon;

            notifyGourmetPaymentChanged();
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
            final int totalPrice = mGourmetPayment.totalPrice;

            if (mGourmetPayment != null && totalPrice <= MIN_AMOUNT_FOR_BONUS_USAGE)
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
        boolean isSimpleCardPaymentEnabled = DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigGourmetSimpleCardPaymentEnabled();
        boolean isCardPaymentEnabled = DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigGourmetCardPaymentEnabled();
        boolean isPhonePaymentEnabled = DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigGourmetPhonePaymentEnabled();
        boolean isVirtualPaymentEnabled = DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigGourmetVirtualPaymentEnabled();

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

    private int[] getAgreedTermMessages(DailyBookingPaymentTypeView.PaymentType paymentType)
    {
        if (paymentType == null)
        {
            return null;
        }

        int[] messages;

        switch (paymentType)
        {
            case EASY_CARD:
                messages = new int[]{R.string.dialog_msg_gourmet_payment_message01//
                    , R.string.dialog_msg_gourmet_payment_message02//
                    , R.string.dialog_msg_gourmet_payment_message03//
                    , R.string.dialog_msg_gourmet_payment_message07};
                break;

            case CARD:
                messages = new int[]{R.string.dialog_msg_gourmet_payment_message01//
                    , R.string.dialog_msg_gourmet_payment_message02//
                    , R.string.dialog_msg_gourmet_payment_message03//
                    , R.string.dialog_msg_gourmet_payment_message06};
                break;

            case PHONE:
                messages = new int[]{R.string.dialog_msg_gourmet_payment_message01//
                    , R.string.dialog_msg_gourmet_payment_message02//
                    , R.string.dialog_msg_gourmet_payment_message03//
                    , R.string.dialog_msg_gourmet_payment_message06};
                break;

            case VBANK:
                messages = new int[]{R.string.dialog_msg_gourmet_payment_message01//
                    , R.string.dialog_msg_gourmet_payment_message02//
                    , R.string.dialog_msg_gourmet_payment_message03//
                    , R.string.dialog_msg_gourmet_payment_message05//
                    , R.string.dialog_msg_gourmet_payment_message06};
                break;

            case FREE:
                messages = new int[]{R.string.dialog_msg_gourmet_payment_message01//
                    , R.string.dialog_msg_gourmet_payment_message02//
                    , R.string.dialog_msg_gourmet_payment_message03//
                    , R.string.dialog_msg_gourmet_payment_message06};
                break;

            default:
                return null;
        }

        return messages;
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
                    notifyGourmetPaymentChanged();
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
                                        addCompositeDisposable(mCartLocalImpl.clearGourmetCart(getActivity()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                                        {
                                            @Override
                                            public void accept(Boolean aBoolean) throws Exception
                                            {
                                                startActivity(DailyInternalDeepLink.getGourmetBookingDetailScreenLink(getActivity(), paymentResult.aggregationId));
                                            }
                                        }));
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

            try
            {
                JSONObject jsonObject = new JSONObject(result);
                int msgCode = jsonObject.getInt("msgCode");

                // 다날 핸드폰 화면에서 취소 버튼 누르는 경우
                // 사용자가 일반 결제 등의 화면에서 back key를 눌러 나타나는 취소 팝업에서 명시적으로 취소 했을 경우
                if (msgCode == -104)
                {
                    getViewInterface().showSimpleDialog(title, getString(R.string.act_toast_payment_canceled) //
                        , getString(R.string.dialog_btn_text_confirm), null, null, null, false);
                } else
                {
                    addCompositeDisposable(mCartLocalImpl.clearGourmetCart(getActivity()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                    {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception
                        {
                            getViewInterface().showSimpleDialog(title, jsonObject.getString("msg"), getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface)
                                {
                                    setResult(BaseActivity.RESULT_CODE_REFRESH);
                                    onBackClick();
                                }
                            });
                        }
                    }));
                }
            } catch (Exception e)
            {
                getViewInterface().showSimpleDialog(title, getString(R.string.act_toast_payment_fail), getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface)
                    {
                        setResult(BaseActivity.RESULT_CODE_REFRESH);
                        onBackClick();
                    }
                });
            }
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
        } else
        {
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

                case 1180:
                    setResult(BaseActivity.RESULT_CODE_BACK);
                    break;

                default:
                    setResult(BaseActivity.RESULT_CODE_REFRESH);
                    break;
            }

            addCompositeDisposable(mCartLocalImpl.clearGourmetCart(getActivity()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    getViewInterface().showSimpleDialog(getString(R.string.dialog_title_payment), message//
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
    }

    private void startLogin()
    {
        DailyToast.showToast(getActivity(), R.string.toast_msg_please_login, DailyToast.LENGTH_LONG);

        Intent intent = LoginActivity.newInstance(getActivity(), AnalyticsManager.Screen.DAILYGOURMET_DETAIL);
        startActivityForResult(intent, GourmetPaymentActivity.REQUEST_CODE_LOGIN_IN);
    }

    boolean checkChangedPrice(GourmetPayment gourmetPayment, GourmetCart gourmetCart)
    {
        if (gourmetPayment == null || gourmetCart == null)
        {
            return true;
        }

        for (GourmetCartMenu gourmetCartMenu : gourmetCart.getMenuList())
        {
            for (GourmetPaymentMenu gourmetPaymentMenu : gourmetPayment.getGourmetPaymentMenuList())
            {
                if (gourmetCartMenu.saleIndex == gourmetPaymentMenu.saleIndex && gourmetCartMenu.getTotalPrice() != gourmetPaymentMenu.subTotalPrice)
                {
                    return true;
                }
            }
        }

        return false;
    }

    void overwriteGourmetCartPrice(GourmetPayment gourmetPayment, GourmetCart gourmetCart)
    {
        if (gourmetPayment == null || gourmetCart == null)
        {
            return;
        }

        for (GourmetCartMenu gourmetCartMenu : gourmetCart.getMenuList())
        {
            for (GourmetPaymentMenu gourmetPaymentMenu : gourmetPayment.getGourmetPaymentMenuList())
            {
                if (gourmetCartMenu.saleIndex == gourmetPaymentMenu.saleIndex)
                {
                    gourmetCartMenu.discountPrice = gourmetPaymentMenu.price;
                    break;
                }
            }
        }
    }

    void getMaxCouponAmount(int[] ticketSaleIndexes, int[] ticketCount)
    {
        if (ticketSaleIndexes == null || ticketCount == null)
        {
            return;
        }

        addCompositeDisposable(mCouponRemoteImpl.getGourmetCouponListByPayment(ticketSaleIndexes, ticketCount) //
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

package com.twoheart.dailyhotel.screen.mydaily.coupon;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.screen.common.web.DailyWebActivity;
import com.daily.dailyhotel.screen.mydaily.coupon.history.CouponHistoryActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Sam Lee on 2016. 5. 19..
 */
@Deprecated
public class CouponListActivity extends BaseActivity
{
    CouponListLayout mCouponListLayout;
    CouponListNetworkController mCouponListNetworkController;
    ArrayList<Coupon> mCouponList;
    SortType mSortType;
    private DailyDeepLink mDailyDeepLink;

    public enum SortType
    {
        ALL,
        STAY,
        GOURMET
    }

    public static Intent newInstance(Context context, SortType sortType, String deepLink)
    {
        Intent intent = new Intent(context, CouponListActivity.class);

        if (sortType != null)
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, sortType.name());
        }

        if (DailyTextUtils.isTextEmpty(deepLink) == false)
        {
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK, deepLink);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mCouponListLayout = new CouponListLayout(this, mOnEventListener);
        mCouponListNetworkController = new CouponListNetworkController(this, mNetworkTag, mNetworkControllerListener);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE) == true)
        {
            try
            {
                mSortType = SortType.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE));
            } catch (Exception e)
            {
                mSortType = SortType.ALL;
            }
        } else
        {
            mSortType = SortType.ALL;
        }

        initDeepLink(intent);

        DailyPreference.getInstance(this).setNewCoupon(false);
        DailyPreference.getInstance(this).setViewedCouponTime(DailyPreference.getInstance(this).getLatestCouponTime());

        setContentView(mCouponListLayout.onCreateView(R.layout.activity_coupon_list));

        mCouponListLayout.setSelectionSpinner(mSortType);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        initDeepLink(intent);
    }

    private void initDeepLink(Intent intent)
    {
        if (intent == null || intent.hasExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK) == false)
        {
            return;
        }

        try
        {
            mDailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse(intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK)));
        } catch (Exception e)
        {
            mDailyDeepLink = null;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(CouponListActivity.this).recordScreen(this, AnalyticsManager.Screen.MENU_COUPON_BOX, null);

        if (mDailyDeepLink != null)
        {
            if (mDailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;

                if (externalDeepLink.isRegisterCouponView() == true)
                {
                    startRegisterCoupon();
                }
            } else
            {

            }

            mDailyDeepLink.clear();
            mDailyDeepLink = null;
        } else
        {
            if (DailyHotel.isLogin() == false)
            {
                showLoginDialog();
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (DailyHotel.isLogin() == true)
        {
            lockUI();
            mCouponListNetworkController.requestCouponList();
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    private void showLoginDialog()
    {
        // 로그인 필요
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lockUI();
                startLogin();
            }
        };

        View.OnClickListener negativeListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CouponListActivity.this.finish();
            }
        };

        String title = this.getResources().getString(R.string.dialog_notice2);
        String message = this.getResources().getString(R.string.dialog_message_coupon_list_login);
        String positive = this.getResources().getString(R.string.dialog_btn_text_yes);
        String negative = this.getResources().getString(R.string.dialog_btn_text_no);

        showSimpleDialog(title, message, positive, negative, positiveListener, negativeListener, new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                CouponListActivity.this.finish();
            }
        }, null, true);
    }

    void startLogin()
    {
        Intent intent = LoginActivity.newInstance(this);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
    }

    void startRegisterCoupon()
    {
        Intent intent = RegisterCouponActivity.newInstance(CouponListActivity.this, AnalyticsManager.Screen.MENU_COUPON_BOX);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGISTER_COUPON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_LOGIN:
            {
                if (resultCode != Activity.RESULT_OK)
                {
                    finish();
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_REGISTER_COUPON:
            {
                if (DailyHotel.isLogin() == false)
                {
                    finish();
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_COUPON_TERMS:
            case CODE_REQUEST_ACTIVITY_COUPON_HISTORY:
                if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;
        }
    }

    ArrayList<Coupon> makeSortCouponList(ArrayList<Coupon> originList, SortType sortType)
    {
        if (sortType == null || originList == null || originList.size() == 0)
        {
            return new ArrayList<>();
        }

        ArrayList<Coupon> sortList = new ArrayList<>();

        switch (sortType)
        {
            case ALL:
                sortList.addAll(originList);
                break;

            case STAY:
                for (Coupon coupon : originList)
                {
                    if (coupon.availableInStay == true || coupon.availableInOutboundHotel == true)
                    {
                        sortList.add(coupon);
                    }
                }
                break;

            case GOURMET:
                for (Coupon coupon : originList)
                {
                    if (coupon.availableInGourmet)
                    {
                        break;
                    }
                }
                break;
        }

        return sortList;
    }

    // ////////////////////////////////////////////////////////
    // EventListener
    // ////////////////////////////////////////////////////////
    private CouponListLayout.OnEventListener mOnEventListener = new CouponListLayout.OnEventListener()
    {
        @Override
        public void startCouponHistory()
        {
            // 쿠폰 사용내역 이동
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = CouponHistoryActivity.newInstance(CouponListActivity.this);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_COUPON_HISTORY);
        }

        @Override
        public void startNotice()
        {
            // 쿠폰 사용시 유의사항 안내
            Intent intent = CouponTermActivity.newInstance(CouponListActivity.this);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_COUPON_TERMS);
        }

        @Override
        public void startRegisterCoupon()
        {
            CouponListActivity.this.startRegisterCoupon();
        }

        @Override
        public void showListItemNotice(Coupon coupon)
        {
            if (coupon == null)
            {
                return;
            }

            if (coupon.type == Coupon.Type.REWARD)
            {
                startActivityForResult(DailyWebActivity.newInstance(CouponListActivity.this, getString(R.string.coupon_notice_text)//
                    , DailyRemoteConfigPreference.getInstance(CouponListActivity.this).getKeyRemoteConfigStaticUrlDailyRewardCouponTerms())//
                    , CODE_REQUEST_ACTIVITY_COUPON_TERMS);
            } else
            {
                // 리스트 아이템 쿠폰 유의사항 팝업
                // 쿠폰 사용시 유의사항 안내
                Intent intent = CouponTermActivity.newInstance(CouponListActivity.this, coupon.couponCode);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_COUPON_TERMS);
            }
        }

        @Override
        public void onListItemDownLoadClick(Coupon coupon)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            // 리스트 아이템 쿠폰 다운로드
            mCouponListNetworkController.requestDownloadCoupon(coupon);
        }

        @Override
        public void onItemSelectedSpinner(int position)
        {
            CouponListActivity.SortType sortType;

            switch (position)
            {
                case 2:
                    sortType = CouponListActivity.SortType.GOURMET;
                    break;
                case 1:
                    sortType = CouponListActivity.SortType.STAY;
                    break;
                case 0:
                default:
                    sortType = CouponListActivity.SortType.ALL;
                    break;
            }

            mSortType = sortType;
            mCouponListLayout.setData(makeSortCouponList(mCouponList, sortType), sortType, true);
        }

        @Override
        public void finish()
        {
            CouponListActivity.this.finish();
        }
    };

    // ///////////////////////////////////////////////////
    // NetworkController
    // ///////////////////////////////////////////////////
    private CouponListNetworkController.OnNetworkControllerListener mNetworkControllerListener = new CouponListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onDownloadCoupon(String couponCode)
        {
            lockUI();

            Coupon coupon = mCouponListLayout.getCoupon(couponCode);
            recordAnalytics(coupon);

            mCouponListNetworkController.requestCouponList();
        }

        @Override
        public void onCouponList(List<Coupon> list)
        {
            mCouponList = new ArrayList<>();
            mCouponList.addAll(list);

            mCouponListLayout.setData(makeSortCouponList(mCouponList, mSortType), mSortType, false);

            unLockUI();
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            CouponListActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            CouponListActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            CouponListActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            CouponListActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            CouponListActivity.this.onErrorResponse(call, response);
        }

        private void recordAnalytics(Coupon coupon)
        {
            try
            {
                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put(AnalyticsManager.KeyType.COUPON_NAME, coupon.title);
                paramsMap.put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, coupon.availableItem);
                paramsMap.put(AnalyticsManager.KeyType.PRICE_OFF, Integer.toString(coupon.amount));
                //                paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, Util.simpleDateFormat(new Date(), "yyyyMMddHHmm"));
                paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, DailyCalendar.format(new Date(), "yyyyMMddHHmm"));
                //                paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, Util.simpleDateFormatISO8601toFormat(coupon.validTo, "yyyyMMddHHmm"));
                paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyyMMddHHmm"));
                paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_FROM, "couponbox");
                paramsMap.put(AnalyticsManager.KeyType.COUPON_CODE, coupon.couponCode);

                if (coupon.availableInGourmet == true && coupon.availableInStay == true)
                {
                    paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.ALL);
                } else if (coupon.availableInStay == true)
                {
                    paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.STAY);
                } else if (coupon.availableInGourmet == true)
                {
                    paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.GOURMET);
                }

                AnalyticsManager.getInstance(CouponListActivity.this).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                    , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "couponbox-" + coupon.title, paramsMap);
            } catch (ParseException e)
            {
                Crashlytics.log("Coupon List::coupon.validTo: " + (coupon != null ? coupon.validTo : ""));
                ExLog.d(e.toString());
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };

}

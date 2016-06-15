package com.twoheart.dailyhotel.screen.information.coupon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sam Lee on 2016. 5. 19..
 */
public class CouponListActivity extends BaseActivity
{
    private CouponListLayout mCouponListLayout;
    private CouponListNetworkController mCouponListNetworkController;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, CouponListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mCouponListLayout = new CouponListLayout(this, mOnEventListener);
        mCouponListNetworkController = new CouponListNetworkController(this, mNetworkTag, mNetworkControllerListener);

        DailyPreference.getInstance(this).setNewCoupon(false);
        DailyPreference.getInstance(this).setViewedCouponTime(DailyPreference.getInstance(this).getLastestCouponTime());

        setContentView(mCouponListLayout.onCreateView(R.layout.activity_coupon_list));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(CouponListActivity.this).recordScreen(AnalyticsManager.Screen.MENU_COUPON_BOX);

        if (Util.isTextEmpty(DailyPreference.getInstance(this).getAuthorization()) == true)
        {
            lockUI();
            showLoginDialog();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (Util.isTextEmpty(DailyPreference.getInstance(this).getAuthorization()) == false)
        {
            lockUI();
            mCouponListNetworkController.requestCouponList();
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
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

        showSimpleDialog(title, message, positive, negative, positiveListener, negativeListener);
    }

    private void startLogin()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
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
        }
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
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        @Override
        public void startNotice()
        {
            // 쿠폰 사용시 유의사항 안내
            Intent intent = CouponTermActivity.newInstance(CouponListActivity.this);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        @Override
        public void showListItemNotice(View view, int position, Coupon coupon)
        {
            // 리스트 아이템 쿠폰 유의사항 팝업
            // 쿠폰 사용시 유의사항 안내
            Intent intent = CouponTermActivity.newInstance(CouponListActivity.this, coupon.couponCode);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        @Override
        public void onListItemDownLoadClick(View view, int position, Coupon coupon)
        {
            // 리스트 아이템 쿠폰 다운로드
            mCouponListNetworkController.requestDownloadCoupon(coupon);
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
        public void onDownloadCoupon(boolean isSuccess, String userCouponCode)
        {
            if (isSuccess == true)
            {
                lockUI();

                Coupon coupon = mCouponListLayout.getCoupon(userCouponCode);
                recordAnalytics(coupon);

                mCouponListNetworkController.requestCouponList();
            } else
            {
                unLockUI();
            }
        }

        @Override
        public void onCouponList(List<Coupon> list)
        {
            mCouponListLayout.setData(list);

            unLockUI();
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            CouponListActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
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

        private void recordAnalytics(Coupon coupon)
        {
            try
            {
                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put(AnalyticsManager.KeyType.COUPON_NAME, coupon.title);
                paramsMap.put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, coupon.availableItem);
                paramsMap.put(AnalyticsManager.KeyType.PRICE_OFF, Integer.toString(coupon.amount));
                paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, Util.simpleDateFormat(new Date(), "yyyyMMddHHmm"));
                paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, Util.simpleDateFormatISO8601toFormat(coupon.validTo, "yyyyMMddHHmm"));

                AnalyticsManager.getInstance(CouponListActivity.this).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                    , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "CouponBox-" + coupon.title, paramsMap);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };

}

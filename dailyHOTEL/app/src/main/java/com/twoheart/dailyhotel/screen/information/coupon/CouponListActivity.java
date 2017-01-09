package com.twoheart.dailyhotel.screen.information.coupon;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

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
public class CouponListActivity extends BaseActivity
{
    private CouponListLayout mCouponListLayout;
    private CouponListNetworkController mCouponListNetworkController;
    private ArrayList<Coupon> mCouponList;
    private SortType mSortType;

    public enum SortType
    {
        ALL,
        STAY,
        GOURMET
    }

    public static Intent newInstance(Context context, SortType sortType)
    {
        Intent intent = new Intent(context, CouponListActivity.class);

        if (sortType != null)
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, sortType.name());
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

        DailyPreference.getInstance(this).setNewCoupon(false);
        DailyPreference.getInstance(this).setViewedCouponTime(DailyPreference.getInstance(this).getLastestCouponTime());

        setContentView(mCouponListLayout.onCreateView(R.layout.activity_coupon_list));

        mCouponListLayout.setSelectionSpinner(mSortType);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(CouponListActivity.this).recordScreen(AnalyticsManager.Screen.MENU_COUPON_BOX);

        if (DailyDeepLink.getInstance().isValidateLink() == true)
        {
            if (DailyDeepLink.getInstance().isRegisterCouponView() == true)
            {
                startRegisterCoupon();
            }

            DailyDeepLink.getInstance().clear();
        } else
        {
            if (DailyHotel.isLogin() == false)
            {
                lockUI();
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

    private void startLogin()
    {
        Intent intent = LoginActivity.newInstance(this);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
    }

    private void startRegisterCoupon()
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
        }
    }

    private ArrayList<Coupon> makeSortCouponList(ArrayList<Coupon> originList, SortType sortType)
    {
        if (originList == null || originList.size() == 0)
        {
            return new ArrayList<>();
        }

        ArrayList<Coupon> sortList = new ArrayList<>();
        if (sortType == null)
        {
            // do nothing!
        } else if (SortType.ALL.equals(sortType) == true)
        {
            sortList.addAll(originList);
        } else
        {
            for (Coupon coupon : originList)
            {
                if (SortType.STAY.equals(sortType) == true && coupon.availableInStay == true)
                {
                    sortList.add(coupon);
                } else if (SortType.GOURMET.equals(sortType) == true && coupon.availableInGourmet == true)
                {
                    sortList.add(coupon);
                }
            }
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
            startActivity(intent);
        }

        @Override
        public void startNotice()
        {
            // 쿠폰 사용시 유의사항 안내
            Intent intent = CouponTermActivity.newInstance(CouponListActivity.this);
            startActivity(intent);
        }

        @Override
        public void startRegisterCoupon()
        {
            CouponListActivity.this.startRegisterCoupon();
        }

        @Override
        public void showListItemNotice(Coupon coupon)
        {
            // 리스트 아이템 쿠폰 유의사항 팝업
            // 쿠폰 사용시 유의사항 안내
            Intent intent = CouponTermActivity.newInstance(CouponListActivity.this, coupon.couponCode);
            startActivity(intent);
        }

        @Override
        public void onListItemDownLoadClick(Coupon coupon)
        {
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
            mCouponListLayout.setData(makeSortCouponList(mCouponList, sortType), sortType);
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
        public void onDownloadCoupon(String userCouponCode)
        {
            lockUI();

            Coupon coupon = mCouponListLayout.getCoupon(userCouponCode);
            recordAnalytics(coupon);

            mCouponListNetworkController.requestCouponList();
        }

        @Override
        public void onCouponList(List<Coupon> list)
        {
            mCouponList = new ArrayList<>();
            mCouponList.addAll(list);

            mCouponListLayout.setData(makeSortCouponList(mCouponList, mSortType), mSortType);

            unLockUI();
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
                if (Constants.DEBUG == false)
                {
                    Crashlytics.log("Coupon List::coupon.validTo: " + (coupon != null ? coupon.validTo : ""));
                }
                ExLog.d(e.toString());
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };

}

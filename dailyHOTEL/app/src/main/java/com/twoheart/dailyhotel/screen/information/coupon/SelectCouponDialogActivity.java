package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseActivity;

/**
 * Created by android_sam on 2016. 5. 26..
 */
public class SelectCouponDialogActivity extends BaseActivity
{

    public static final String INTENT_EXTRA_SELECT_COUPON = "selectCoupon";


    private SelectCouponDialogLayout mLayout;
    private SelectCouponNetworkController mNetworkController;


    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, SelectCouponDialogActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        mLayout = new SelectCouponDialogLayout(this, getWindow(), mOnEventListener);
        mNetworkController = new SelectCouponNetworkController(this, mNetworkTag, mNetworkControllerListener);

        setContentView(mLayout.onCreateView(R.layout.activity_select_coupon_dialog));

    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    private SelectCouponDialogLayout.OnEventListener mOnEventListener = new SelectCouponDialogLayout.OnEventListener()
    {
        @Override
        public void setResult(Coupon coupon)
        {
            Intent intent = new Intent();
            intent.putExtra(INTENT_EXTRA_SELECT_COUPON, coupon);

            SelectCouponDialogActivity.this.setResult(RESULT_OK, intent);
            SelectCouponDialogActivity.this.finish();
        }

        @Override
        public void onCouponDownloadClick(int position)
        {
            // 쿠폰 다운로드 시도!
        }

        @Override
        public void finish()
        {
            SelectCouponDialogActivity.this.finish();
        }
    };

    // ///////////////////////////////////////////////////
    // NetworkController
    // ///////////////////////////////////////////////////
    private SelectCouponNetworkController.OnNetworkControllerListener mNetworkControllerListener = new SelectCouponNetworkController.OnNetworkControllerListener()
    {

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            SelectCouponDialogActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            SelectCouponDialogActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            SelectCouponDialogActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            SelectCouponDialogActivity.this.onErrorToastMessage(message);
        }
    };
}

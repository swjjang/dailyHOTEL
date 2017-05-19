package com.twoheart.dailyhotel.place.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.networkcontroller.PlacePaymentThankyouNetworkController;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public abstract class PlacePaymentThankyouActivity extends BaseActivity implements OnClickListener
{
    protected static final String INTENT_EXTRA_DATA_IMAGEURL = "imageUrl";
    protected static final String INTENT_EXTRA_DATA_PLACE_NAME = "placeName";
    protected static final String INTENT_EXTRA_DATA_PLACE_TYPE = "placeType";
    protected static final String INTENT_EXTRA_DATA_USER_NAME = "userName";
    protected static final String INTENT_EXTRA_DATA_VISIT_DAY = "visitDay";
    protected static final String INTENT_EXTRA_DATA_VISIT_TIME = "visitTime";
    protected static final String INTENT_EXTRA_DATA_PRODUCT_COUNT = "productCount";
    protected static final String INTENT_EXTRA_DATA_PAYMENT_TYPE = "paymentType";
    protected static final String INTENT_EXTRA_DATA_DISCOUNT_TYPE = "discountType";
    protected static final String INTENT_EXTRA_DATA_MAP_PAYMENT_INFORM = "mapPaymentInform";

    String mPaymentType;
    Map<String, String> mParams;
    View mStampLayout;

    protected abstract void recordEvent(String action, String label);

    protected abstract void onFirstPurchaseSuccess(boolean isFirstStayPurchase, boolean isFirstGourmetPurchase, String paymentType, Map<String, String> params);

    protected abstract void onCouponUsedPurchase(boolean isFirstStayPurchase, boolean isFirstGourmetPurchase, String paymentType, Map<String, String> params);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.abc_fade_in, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_result);

        Intent intent = getIntent();

        if (intent == null)
        {
            Util.restartApp(this);
            return;
        }

        mPaymentType = intent.getStringExtra(INTENT_EXTRA_DATA_PAYMENT_TYPE);

        String imageUrl = intent.getStringExtra(INTENT_EXTRA_DATA_IMAGEURL);
        String placeName = intent.getStringExtra(INTENT_EXTRA_DATA_PLACE_NAME);
        String placeType = intent.getStringExtra(INTENT_EXTRA_DATA_PLACE_TYPE);
        String userName = intent.getStringExtra(INTENT_EXTRA_DATA_USER_NAME);

        String discountType = intent.getStringExtra(INTENT_EXTRA_DATA_DISCOUNT_TYPE);

        mParams = (Map<String, String>) intent.getSerializableExtra(INTENT_EXTRA_DATA_MAP_PAYMENT_INFORM);

        String productIndex = mParams.get(AnalyticsManager.KeyType.TICKET_INDEX);

        initToolbar();
        initLayout(imageUrl, placeName, placeType, userName);
        initStampLayout();

        startReceiptAnimation();

        final ScrollView informationLayout = (ScrollView) findViewById(R.id.informationLayout);
        EdgeEffectColor.setEdgeGlowColor(informationLayout, getResources().getColor(R.color.default_over_scroll_edge));

        recordEvent(AnalyticsManager.Action.END_PAYMENT, mPaymentType);
        recordEvent(AnalyticsManager.Action.PAYMENT_USED, discountType);
        recordEvent(AnalyticsManager.Action.PRODUCT_ID, productIndex);

        PlacePaymentThankyouNetworkController networkController = new PlacePaymentThankyouNetworkController(this, mNetworkTag, mNetworkControllerListener);
        networkController.requestUserTracking();
    }

    private void initToolbar()
    {
        View closeView = findViewById(R.id.closeView);
        closeView.setOnClickListener(this);
    }

    private void initLayout(String imageUrl, String place, String placeType, String userName)
    {
        if (DailyTextUtils.isTextEmpty(place, placeType) == true)
        {
            Util.restartApp(this);
            return;
        }

        int imageHeight = ScreenUtils.getRatioHeightType4x3(ScreenUtils.getScreenWidth(this));
        com.facebook.drawee.view.SimpleDraweeView simpleDraweeView = (com.facebook.drawee.view.SimpleDraweeView) findViewById(R.id.placeImageView);
        ViewGroup.LayoutParams layoutParams = simpleDraweeView.getLayoutParams();
        layoutParams.height = imageHeight;
        simpleDraweeView.setLayoutParams(layoutParams);

        TextView placeTextView = (TextView) findViewById(R.id.bookingPlaceTextView);
        TextView placeTypeTextView = (TextView) findViewById(R.id.productTypeTextView);
        TextView messageTextView = (TextView) findViewById(R.id.messageTextView);
        View confirmView = findViewById(R.id.confirmView);

        Util.requestImageResize(this, simpleDraweeView, imageUrl);
        placeTextView.setText(place);
        placeTypeTextView.setText(placeType);

        String message;
        if (DailyTextUtils.isTextEmpty(userName) == false)
        {
            message = getString(R.string.message_completed_payment_format, userName);
            SpannableStringBuilder userNameBuilder = new SpannableStringBuilder(message);
            userNameBuilder.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(this).getMediumTypeface()),//
                0, userName.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            messageTextView.setText(userNameBuilder);
        } else
        {
            message = getString(R.string.message_completed_payment_default);
            messageTextView.setText(message);
        }

        confirmView.setOnClickListener(this);

        View waitingMessageLayout = findViewById(R.id.waitingMessageLayout);
        View messageBottomView = findViewById(R.id.messageBottomView);

        String categoryCode = mParams.get(AnalyticsManager.KeyType.CATEGORY);
        if (AnalyticsManager.ValueType.PENSION.equalsIgnoreCase(categoryCode) == true)
        {
            waitingMessageLayout.setVisibility(View.VISIBLE);
            messageBottomView.setVisibility(View.GONE);
        } else
        {
            waitingMessageLayout.setVisibility(View.GONE);
            messageBottomView.setVisibility(View.VISIBLE);
        }
    }

    private void initStampLayout()
    {
        mStampLayout = findViewById(R.id.stampLayout);
        mStampLayout.setVisibility(View.GONE);
        mStampLayout.setOnClickListener(this);
    }

    public void setStampLayout(String message1, String message2, String message3)
    {
        if (mStampLayout == null)
        {
            return;
        }

        if (isStampEnabled() == false)
        {
            mStampLayout.setVisibility(View.GONE);
            return;
        }

        TextView message1TextView = (TextView) mStampLayout.findViewById(R.id.message1TextView);
        TextView message2TextView = (TextView) mStampLayout.findViewById(R.id.message2TextView);
        TextView message3TextView = (TextView) mStampLayout.findViewById(R.id.message3TextView);

        message1TextView.setText(message1);
        message2TextView.setText(message2);

        // SpannableString 자체가 null을 허용하지 않
        if (DailyTextUtils.isTextEmpty(message3) == false)
        {
            SpannableString spannableString3 = new SpannableString(message3);
            spannableString3.setSpan(new UnderlineSpan(), 0, spannableString3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            message3TextView.setText(spannableString3);
        } else
        {
            message3TextView.setText("");
        }
    }

    private void startReceiptAnimation()
    {
        final View confirmImageView = findViewById(R.id.confirmImageView);
        confirmImageView.setVisibility(View.INVISIBLE);
        final View receiptLayout = findViewById(R.id.receiptLayout);

        float startY = 0f - ScreenUtils.getScreenHeight(PlacePaymentThankyouActivity.this);
        final float endY = 0.0f;

        final float startScaleY = 2.3f;
        final float endScaleY = 1.0f;

        int animatorSetStartDelay;
        int receiptLayoutAnimatorDuration;
        int confirmImageAnimatorStartDelay;
        int confirmImageAnimatorDuration;
        int stampLayoutAnimatorStartDelay;
        int stampLayoutAnimatorDuration;

        if (VersionUtils.isOverAPI21() == true)
        {
            animatorSetStartDelay = 400;
            receiptLayoutAnimatorDuration = 300;
            confirmImageAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            confirmImageAnimatorDuration = 200;
            stampLayoutAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            stampLayoutAnimatorDuration = 200;
        } else
        {
            animatorSetStartDelay = 600;
            receiptLayoutAnimatorDuration = 400;
            confirmImageAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            confirmImageAnimatorDuration = 200;
            stampLayoutAnimatorStartDelay = receiptLayoutAnimatorDuration - 50;
            stampLayoutAnimatorDuration = 200;
        }

        receiptLayout.setTranslationY(startY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setStartDelay(animatorSetStartDelay);

        final ObjectAnimator confirmImageAnimator = ObjectAnimator.ofPropertyValuesHolder(confirmImageView //
            , PropertyValuesHolder.ofFloat("scaleX", startScaleY, endScaleY) //
            , PropertyValuesHolder.ofFloat("scaleY", startScaleY, endScaleY) //
            , PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f) //
        );

        confirmImageAnimator.setDuration(confirmImageAnimatorDuration);
        confirmImageAnimator.setStartDelay(confirmImageAnimatorStartDelay);
        confirmImageAnimator.setInterpolator(new OvershootInterpolator(1.6f));
        confirmImageAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                confirmImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                confirmImageView.setScaleX(endScaleY);
                confirmImageView.setScaleY(endScaleY);

                confirmImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        ObjectAnimator receiptLayoutAnimator = ObjectAnimator.ofPropertyValuesHolder(receiptLayout //
            , PropertyValuesHolder.ofFloat("translationY", startY, endY) //
        );

        receiptLayoutAnimator.setDuration(receiptLayoutAnimatorDuration);
        receiptLayoutAnimator.setInterpolator(new OvershootInterpolator(0.82f));
        receiptLayoutAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                receiptLayout.setTranslationY(endY);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                receiptLayout.setTranslationY(endY);
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        final ObjectAnimator stampLayoutAnimator = ObjectAnimator.ofPropertyValuesHolder(mStampLayout //
            , PropertyValuesHolder.ofFloat("scaleX", 0.9f, 1.0f) //
            , PropertyValuesHolder.ofFloat("scaleY", 0.9f, 1.0f) //
            , PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f) //
        );

        stampLayoutAnimator.setDuration(stampLayoutAnimatorDuration);
        stampLayoutAnimator.setStartDelay(stampLayoutAnimatorStartDelay);
        stampLayoutAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        stampLayoutAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mStampLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {

            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        if (isStampEnabled() == true)
        {
            animatorSet.playTogether(receiptLayoutAnimator, confirmImageAnimator, stampLayoutAnimator);
        } else
        {
            animatorSet.playTogether(receiptLayoutAnimator, confirmImageAnimator);
        }
        animatorSet.start();
    }

    protected boolean isStampEnabled()
    {
        return false;
    }

    @Override
    public void onBackPressed()
    {
        recordEvent(AnalyticsManager.Action.THANKYOU_SCREEN_BUTTON_CLICKED, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED);
        setResult(RESULT_OK);

        super.onBackPressed();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.abc_fade_out);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.closeView:
                recordEvent(AnalyticsManager.Action.THANKYOU_SCREEN_BUTTON_CLICKED, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED);
                setResult(RESULT_OK);
                finish();
                break;

            case R.id.confirmView:
                recordEvent(AnalyticsManager.Action.THANKYOU_SCREEN_BUTTON_CLICKED, AnalyticsManager.Label.VIEW_BOOKING_STATUS_CLICKED);
                setResult(RESULT_OK);
                finish();
                break;

            case R.id.stampLayout:
                startActivity(DailyInternalDeepLink.getStampScreenLink(this));

                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.NAVIGATION,//
                    AnalyticsManager.Action.STAMP_DETAIL_CLICK, AnalyticsManager.Label.STAY_THANKYOU, null);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_STAMP:
            {
                setResult(CODE_RESULT_ACTIVITY_GO_HOME);
                finish();
                return;
            }

            default:
                break;
        }
    }

    private final PlacePaymentThankyouNetworkController.OnNetworkControllerListener mNetworkControllerListener = new PlacePaymentThankyouNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onUserTracking(int hotelPaymentCompletedCount, int gourmetPaymentCompletedCount)
        {
            boolean isFirstStayPurchase = hotelPaymentCompletedCount == 1;
            boolean isFirstGourmetPurchase = gourmetPaymentCompletedCount == 1;
            boolean isCouponUsed = false;

            if (mParams != null && mParams.containsKey(AnalyticsManager.KeyType.COUPON_REDEEM) == true)
            {
                try
                {
                    isCouponUsed = Boolean.parseBoolean(mParams.get(AnalyticsManager.KeyType.COUPON_REDEEM));
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }

            if (isFirstStayPurchase == true || isFirstGourmetPurchase == true)
            {
                PlacePaymentThankyouActivity.this.onFirstPurchaseSuccess(isFirstStayPurchase, isFirstGourmetPurchase, mPaymentType, mParams);

            }

            if (isCouponUsed == true)
            {
                PlacePaymentThankyouActivity.this.onCouponUsedPurchase(isFirstStayPurchase, isFirstGourmetPurchase, mPaymentType, mParams);
            }
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            PlacePaymentThankyouActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            // do nothing
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            // do nothing
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            // do nothing
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            // do nothing
        }
    };
}

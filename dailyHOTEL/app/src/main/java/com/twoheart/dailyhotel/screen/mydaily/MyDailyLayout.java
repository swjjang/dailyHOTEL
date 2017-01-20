package com.twoheart.dailyhotel.screen.mydaily;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.screen.mydaily.coupon.CouponListActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.text.DecimalFormat;

/**
 * Created by sam on 2016. 5. 18..
 */
public class MyDailyLayout extends BaseLayout implements View.OnClickListener
{
    private static final float DEFAULT_PROFILE_TEXT_SIZE_DP = 18.0f;
    private static final float SMALL_PROFILE_TEXT_SIZE_DP = 12.0f;

    private static final float DEFAULT_MESSAGE_TEXT_SIZE_DP = 12.0f;
    private static final float EMPTY_MIDDLE_MESSAGE_TEXT_SIZE_DP = 13.0f;
    private static final float EMPTY_SMALL_MESSAGE_TEXT_SIZE_DP = 11.0f;

    private View mProfileLayout;
    private View mAccountInfoLayout;
    private View mNewCouponIconView;
    private View mLinkAlarmLayoutView;

    private TextView mPushTextView;
    private TextView mPushBenefitTextView;

    public interface OnEventListener extends OnBaseEventListener
    {
        void startLogin();

        void startSignUp();

        void startEditProfile();

        void startCouponList(CouponListActivity.SortType sortType);

        void startBonusList();

        void startCreditCardList();

        void startInviteFriend();

        void startWishList(Constants.PlaceType placeType);

        void startRecentPlaces(Constants.PlaceType placeType);

        void onPushClick();

        void startSettingAlarm();
    }

    public MyDailyLayout(Context context, OnEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(mContext, view);

        ScrollView scrollView = (ScrollView) view.findViewById(R.id.myDailyScrollView);
        EdgeEffectColor.setEdgeGlowColor(scrollView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        mProfileLayout = view.findViewById(R.id.profileLayout);
        mAccountInfoLayout = view.findViewById(R.id.accountInfoLayout);

        initProfileLayout(mContext, view);
        initAccountInfoLayout(mContext, view);

        View recommendLayout = view.findViewById(R.id.recommendLayout);
        View wishListLayout = view.findViewById(R.id.wishListLayout);
        View recentPlacesLayout = view.findViewById(R.id.recentPlacesLayout);

        recommendLayout.setOnClickListener(this);
        recentPlacesLayout.setOnClickListener(this);
        wishListLayout.setOnClickListener(this);

        View pushBenefitLayout = view.findViewById(R.id.pushBenefitLayout);
        pushBenefitLayout.setOnClickListener(this);

        mPushTextView = (TextView) pushBenefitLayout.findViewById(R.id.pushTextView);
        mPushBenefitTextView = (TextView) pushBenefitLayout.findViewById(R.id.pushBenefitTextView);

        boolean isLogin = DailyHotel.isLogin();
        updateLoginLayout(isLogin, true);
        updateAccountLayout(isLogin, -1, -1);

        boolean isAllowBenefitAlarm = DailyPreference.getInstance(mContext).isUserBenefitAlarm();
        updatePushIcon(isAllowBenefitAlarm);

        mLinkAlarmLayoutView = view.findViewById(R.id.linkAlarmLayout);

        TextView linkAlarmTextView = (TextView) mLinkAlarmLayoutView.findViewById(R.id.linkAlarmTextView);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(linkAlarmTextView.getText());

        spannableStringBuilder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.default_text_c323232)), //
            52, 58, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.setSpan(new UnderlineSpan(), 52, 58, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.default_text_cb70038)), //
            59, 69, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        linkAlarmTextView.setText(spannableStringBuilder);
        linkAlarmTextView.setOnClickListener(this);

        setLinkAlarmVisible(false);
    }

    private void initToolbar(Context context, View view)
    {
        View toolbar = view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(context, toolbar);
        dailyToolbarLayout.initToolbar(mContext.getResources().getString(R.string.menu_item_title_mydaily), null, false);
    }

    private void initProfileLayout(Context context, View view)
    {
        View loginView = view.findViewById(R.id.loginTextView);
        View signUpView = view.findViewById(R.id.signupTextView);
        View editProfileView = view.findViewById(R.id.editProfileTextView);
        TextView profileTextView = (TextView) view.findViewById(R.id.profileTextView);

        if (Util.getLCDWidth(context) < 720)
        {
            profileTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SMALL_PROFILE_TEXT_SIZE_DP);
        } else
        {
            profileTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PROFILE_TEXT_SIZE_DP);
        }

        loginView.setOnClickListener(this);
        signUpView.setOnClickListener(this);
        editProfileView.setOnClickListener(this);
    }

    private void initAccountInfoLayout(Context context, View view)
    {
        View couponLayout = view.findViewById(R.id.couponLayout);
        View bonusLayout = view.findViewById(R.id.bonusLayout);
        View creditCardLayout = view.findViewById(R.id.creditcardLayout);

        couponLayout.setOnClickListener(this);
        bonusLayout.setOnClickListener(this);
        creditCardLayout.setOnClickListener(this);

        mNewCouponIconView = couponLayout.findViewById(R.id.newCounponIconView);
    }

    public void updatePushIcon(boolean onOff)
    {
        if (mPushTextView == null)
        {
            return;
        }

        mPushTextView.setText(onOff == false ? R.string.label_off : R.string.label_on);
    }

    public void updateNewIconView(boolean hasNewCoupon)
    {
        if (mNewCouponIconView == null)
        {
            return;
        }

        if (hasNewCoupon == true)
        {
            mNewCouponIconView.setVisibility(View.VISIBLE);
        } else
        {
            mNewCouponIconView.setVisibility(View.GONE);
        }
    }

    /**
     * @param isLogin
     * @param isInitialize 초기화 여부 초기화시 Analytics 보내지 않기 위해
     */
    public void updateLoginLayout(boolean isLogin, boolean isInitialize)
    {
        TextView profileTextView = (TextView) mProfileLayout.findViewById(R.id.profileTextView);
        TextView loginMessageTextView = (TextView) mProfileLayout.findViewById(R.id.loginMessageTextView);

        View loginView = mProfileLayout.findViewById(R.id.loginTextView);
        View signUpView = mProfileLayout.findViewById(R.id.signupTextView);
        View editProfileView = mProfileLayout.findViewById(R.id.editProfileTextView);
        View profileLayoutBottomLine = mProfileLayout.findViewById(R.id.profileLayoutBottomLine);

        ImageView profileImageView = (ImageView) mProfileLayout.findViewById(R.id.iconImageView);

        if (isLogin == true)
        {
            String userName = DailyPreference.getInstance(mContext).getUserName();
            String userEmail = DailyPreference.getInstance(mContext).getUserEmail();

            boolean isEmptyName = Util.isTextEmpty(userName) == true;
            boolean isEmptyEmail = Util.isTextEmpty(userEmail) == true;

            if (isEmptyName == true && isEmptyEmail == true)
            {
                profileTextView.setVisibility(View.GONE);
                loginMessageTextView.setText(R.string.information_empty_name_n_email_after_login);

                if (Util.getLCDWidth(mContext) < 720)
                {
                    loginMessageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, EMPTY_SMALL_MESSAGE_TEXT_SIZE_DP);
                    loginMessageTextView.setLineSpacing(6.0f, 1.0f);
                } else
                {
                    loginMessageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, EMPTY_MIDDLE_MESSAGE_TEXT_SIZE_DP);
                    loginMessageTextView.setLineSpacing(7.0f, 1.0f);
                }
            } else
            {
                profileTextView.setVisibility(View.VISIBLE);
                loginMessageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MESSAGE_TEXT_SIZE_DP);
                loginMessageTextView.setLineSpacing(0.0f, 1.0f);

                if (isEmptyName == true)
                {
                    profileTextView.setText(R.string.act_profile_input_name);
                } else
                {
                    profileTextView.setText(userName);
                }

                if (isEmptyEmail == true)
                {
                    loginMessageTextView.setText(R.string.act_profile_input_email);
                } else
                {
                    loginMessageTextView.setText(userEmail);
                }
            }

            loginView.setVisibility(View.GONE);
            signUpView.setVisibility(View.GONE);
            editProfileView.setVisibility(View.VISIBLE);

            profileLayoutBottomLine.setBackgroundColor(mContext.getResources().getColor(R.color.default_line_cdcdcdd));
            ViewGroup.LayoutParams layoutParams = profileLayoutBottomLine.getLayoutParams();
            layoutParams.height = 1;
            profileLayoutBottomLine.setLayoutParams(layoutParams);

            profileImageView.setImageResource(R.drawable.more_ic_mydaily_02_login);
        } else
        {
            profileTextView.setText(R.string.frag_need_login);
            loginMessageTextView.setText(R.string.frag_login_message);
            loginMessageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MESSAGE_TEXT_SIZE_DP);
            loginMessageTextView.setLineSpacing(0.0f, 1.0f);

            profileTextView.setVisibility(View.VISIBLE);
            loginView.setVisibility(View.VISIBLE);
            signUpView.setVisibility(View.VISIBLE);
            editProfileView.setVisibility(View.GONE);

            profileLayoutBottomLine.setBackgroundColor(mContext.getResources().getColor(R.color.default_line_cf0f0f0));
            ViewGroup.LayoutParams layoutParams = profileLayoutBottomLine.getLayoutParams();
            layoutParams.height = Util.dpToPx(mContext, 1);
            profileLayoutBottomLine.setLayoutParams(layoutParams);

            profileImageView.setImageResource(R.drawable.more_ic_mydaily_01_logout);
        }
    }

    /**
     * 적립금, 쿠폰, 결제 카드관리 레이아웃
     *
     * @param isLogin     로그인 여부
     * @param bonus       최초 진입시 -1
     * @param couponCount 최초 진입시 -1
     */
    public void updateAccountLayout(boolean isLogin, int bonus, int couponCount)
    {
        TextView couponCountTextView = (TextView) mAccountInfoLayout.findViewById(R.id.couponCountTextView);
        TextView bonusCountTextView = (TextView) mAccountInfoLayout.findViewById(R.id.bonusCountTextView);

        bonusCountTextView.setVisibility(bonus == -1 ? View.GONE : View.VISIBLE);
        couponCountTextView.setVisibility(couponCount == -1 ? View.GONE : View.VISIBLE);

        if (bonus == -1)
        {
            bonusCountTextView.setVisibility(View.GONE);
            bonus = 0;
        } else
        {
            bonusCountTextView.setVisibility(View.VISIBLE);
        }

        if (couponCount == -1)
        {
            couponCountTextView.setVisibility(View.GONE);
            couponCount = 0;
        } else
        {
            couponCountTextView.setVisibility(View.VISIBLE);
        }

        DecimalFormat bonusFormat = new DecimalFormat(mContext.getResources().getString(R.string.frag_currency_decimal_format));
        String bonusString = bonusFormat.format(bonus);

        DecimalFormat countFormat = new DecimalFormat(mContext.getResources().getString(R.string.frag_count_decimal_format));
        String couponCountString = countFormat.format(couponCount);

        if (isLogin == true)
        {
            mAccountInfoLayout.setVisibility(View.VISIBLE);

            couponCountTextView.setText(couponCountString);
            bonusCountTextView.setText(bonusString);

        } else
        {
            mAccountInfoLayout.setVisibility(View.GONE);
        }
    }

    public void updatePushText(String message)
    {
        if (Util.isTextEmpty(message))
        {
            message = mContext.getResources().getString(R.string.frag_push_alert_subtext);
        }

        mPushBenefitTextView.setText(message);
    }

    public void setLinkAlarmVisible(boolean visible)
    {
        if (visible == true)
        {
            if (mLinkAlarmLayoutView.getVisibility() != View.VISIBLE)
            {
                mLinkAlarmLayoutView.setVisibility(View.VISIBLE);
            }
        } else
        {
            if (mLinkAlarmLayoutView.getVisibility() != View.GONE)
            {
                mLinkAlarmLayoutView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.loginTextView:
                ((OnEventListener) mOnEventListener).startLogin();
                break;

            case R.id.signupTextView:
                ((OnEventListener) mOnEventListener).startSignUp();
                break;

            case R.id.editProfileTextView:
                ((OnEventListener) mOnEventListener).startEditProfile();
                break;

            case R.id.creditcardLayout:
                ((OnEventListener) mOnEventListener).startCreditCardList();
                break;

            case R.id.couponLayout:
                ((OnEventListener) mOnEventListener).startCouponList(CouponListActivity.SortType.ALL);
                break;

            case R.id.bonusLayout:
                ((OnEventListener) mOnEventListener).startBonusList();
                break;

            case R.id.recommendLayout:
                ((OnEventListener) mOnEventListener).startInviteFriend();
                break;

            case R.id.wishListLayout:
                ((OnEventListener) mOnEventListener).startWishList(null);
                break;

            case R.id.recentPlacesLayout:
                ((OnEventListener) mOnEventListener).startRecentPlaces(null);
                break;

            case R.id.pushBenefitLayout:
                ((OnEventListener) mOnEventListener).onPushClick();
                break;

            case R.id.linkAlarmTextView:
                ((OnEventListener) mOnEventListener).startSettingAlarm();
                break;

            default:
                break;
        }
    }

}

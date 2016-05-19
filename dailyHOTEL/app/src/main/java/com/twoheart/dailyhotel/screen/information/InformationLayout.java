package com.twoheart.dailyhotel.screen.information;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

/**
 * Created by sam on 2016. 5. 18..
 */
public class InformationLayout
		extends BaseLayout implements View.OnClickListener
{

	private View mProfileLayout;
	private View mAccountInfoLayout;
	private View mNewEventIconView;

	public interface OnEventListener extends OnBaseEventListener
	{
		void onLoginClick();

		void onSignUpClick();

		void showEditProfile();

		void showCouponList();

		void showBonusList();

		void showCreditCardList();

		void onRecommendClick();

		void showEvent();

		void sendCall();

		void sendEmail();

		void showAbout();

		void onPushClick(View view);

		void onFacebookClick();

		void onInstagramClick();

		void onNaverBlogClick();

		void showTerms();

		void showPersonal();

		void showLocationTerms();

		void showProtectChildTerms();

	}

	public InformationLayout(Context context, OnEventListener listener)
	{
		super(context, listener);
	}

	@Override
	protected void initLayout(View view)
	{
		BaseActivity baseActivity = (BaseActivity) mContext;

		initToolbar(baseActivity, view);

		mProfileLayout = view.findViewById(R.id.profileLayout);
		mAccountInfoLayout = view.findViewById(R.id.accountInfoLayout);

		initProfileLayout(baseActivity, view);
		initAccountInfoLayout(baseActivity, view);

		View eventLayout = view.findViewById(R.id.eventLayout);
		View callLayout = view.findViewById(R.id.callLayout);
		View mailLayout = view.findViewById(R.id.mailLayout);
		View aboutLayout = view.findViewById(R.id.aboutLayout);

		eventLayout.setOnClickListener(this);
		callLayout.setOnClickListener(this);
		mailLayout.setOnClickListener(this);
		aboutLayout.setOnClickListener(this);

		mNewEventIconView = eventLayout.findViewById(R.id.newIconView);

		TextView pushTextView = (TextView) view.findViewById(R.id.pushTextView);

		if (DailyPreference.getInstance(baseActivity).isAllowPush() == true)
		{
			pushTextView.setText(R.string.label_on);
		} else
		{
			pushTextView.setText(R.string.label_off);
		}

		pushTextView.setOnClickListener(this);

		initSnsLayout(view);
		initBusinessLayout(baseActivity, view);
		initTermsLayout(baseActivity, view);

		TextView versionTextView = (TextView) view.findViewById(R.id.versionTextView);
		versionTextView.setText(mContext.getResources().getString(R.string.label_version, DailyHotel.VERSION));

		updateLoginLayout(false);
		updateAccountLayout(false);
		updateNewIconView(baseActivity);
	}

	private void initToolbar(Context context, View view)
	{
		View toolbar = view.findViewById(R.id.toolbar);
		DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(context, toolbar);
		dailyToolbarLayout.initToolbar(mContext.getResources().getString(R.string.actionbar_title_setting_frag), null, false);
	}

	private void initProfileLayout(Context context, View view)
	{
		View loginView = view.findViewById(R.id.loginTextView);
		View signUpView = view.findViewById(R.id.signupTextView);
		View editProfileView = view.findViewById(R.id.editProfileTextView);

		loginView.setOnClickListener(this);
		signUpView.setOnClickListener(this);
		editProfileView.setOnClickListener(this);
	}

	private void initAccountInfoLayout(Context context, View view)
	{
		View couponLayout = view.findViewById(R.id.couponLayout);
		View bonusLayout = view.findViewById(R.id.bonusLayout);
		View creditcardLayout = view.findViewById(R.id.creditcardLayout);

		couponLayout.setOnClickListener(this);
		bonusLayout.setOnClickListener(this);
		creditcardLayout.setOnClickListener(this);
	}

	private void initSnsLayout(View view)
	{
		View facebookView = view.findViewById(R.id.facebookLinkView);
		View instagramView = view.findViewById(R.id.instagramLinkView);
		View naverView = view.findViewById(R.id.naverLinkView);

		facebookView.setOnClickListener(this);
		instagramView.setOnClickListener(this);
		naverView.setOnClickListener(this);
	}

	private void initBusinessLayout(BaseActivity baseActivity, View view)
	{
		TextView business1TextView = (TextView) view.findViewById(R.id.business1TextView);
		TextView business2TextView = (TextView) view.findViewById(R.id.business2TextView);
		TextView business3TextView = (TextView) view.findViewById(R.id.business3TextView);

		business1TextView.setText(mContext.getResources().getString(R.string.frag_about_business_license01//
				, DailyPreference.getInstance(baseActivity).getCompanyCEO()//
				, DailyPreference.getInstance(baseActivity).getCompanyBizRegNumber()//
				, DailyPreference.getInstance(baseActivity).getCompanyPhoneNumber()));

		if (Util.getLCDWidth(baseActivity) < 720)
		{
			String text = DailyPreference.getInstance(baseActivity).getCompanyAddress() + '\n'//
					+ mContext.getResources().getString(R.string.frag_about_business_license02//
							, DailyPreference.getInstance(baseActivity).getCompanyItcRegNumber());

			business2TextView.setText(text);
		} else
		{
			String text = DailyPreference.getInstance(baseActivity).getCompanyAddress() + " | "//
					+ mContext.getResources().getString(R.string.frag_about_business_license02//
							, DailyPreference.getInstance(baseActivity).getCompanyItcRegNumber());

			business2TextView.setText(text);
		}

		business3TextView.setText(mContext.getResources().getString(R.string.frag_about_business_license03//
				, DailyPreference.getInstance(baseActivity).getCompanyPrivacyEmail()));
	}

	private void initTermsLayout(BaseActivity baseActivity, View view)
	{
		LinearLayout termsLayout = (LinearLayout) view.findViewById(R.id.termsLayout);

		if (Util.getLCDWidth(baseActivity) < 720)
		{
			termsLayout.setOrientation(LinearLayout.VERTICAL);
		}

		View termsView = view.findViewById(R.id.termsView);
		View personalView = view.findViewById(R.id.personalView);
		View locationTermsView = view.findViewById(R.id.locationTermsView);
		View protectChildTermsView = view.findViewById(R.id.protectChildTermsView);

		termsView.setOnClickListener(this);
		personalView.setOnClickListener(this);
		locationTermsView.setOnClickListener(this);
		protectChildTermsView.setOnClickListener(this);
	}

//	public boolean isLogin()
//	{
//		if (Util.isTextEmpty(DailyPreference.getInstance(mContext).getAuthorization()) == true)
//		{
//			return false;
//		} else
//		{
//			return true;
//		}
//	}

	public void updateNewIconView(Context context)
	{
		if (mNewEventIconView == null)
		{
			return;
		}

		if (DailyPreference.getInstance(context).hasNewEvent() == true)
		{
			mNewEventIconView.setVisibility(View.VISIBLE);
		} else
		{
			mNewEventIconView.setVisibility(View.INVISIBLE);
		}
	}

	public void updateLoginLayout(boolean isLogin)
	{
		TextView profileTextView = (TextView) mProfileLayout.findViewById(R.id.profileTextView);
		TextView loginMessageTextView = (TextView) mProfileLayout.findViewById(R.id.loginMessageTextView);

		View loginView = mProfileLayout.findViewById(R.id.loginTextView);
		View signUpView = mProfileLayout.findViewById(R.id.signupTextView);
		View editProfileView = mProfileLayout.findViewById(R.id.editProfileTextView);

		ImageView profileImageView = (ImageView) mProfileLayout.findViewById(R.id.iconImageView);

		if (isLogin == true)
		{
			AnalyticsManager.getInstance(mContext).recordScreen(AnalyticsManager.Screen.INFORMATION_SIGNIN);

			String userName = DailyPreference.getInstance(mContext).getUserName();
			String userEmail = DailyPreference.getInstance(mContext).getUserEmail();
			profileTextView.setText(userName);
			loginMessageTextView.setText(userEmail);

			loginView.setVisibility(View.GONE);
			signUpView.setVisibility(View.GONE);
			editProfileView.setVisibility(View.VISIBLE);

			profileImageView.setImageResource(R.drawable.more_ic_mydaily_02_login);
		} else
		{
			AnalyticsManager.getInstance(mContext).recordScreen(AnalyticsManager.Screen.INFORMATION_SIGNOUT);

			profileTextView.setText(R.string.frag_need_login);
			loginMessageTextView.setText(R.string.frag_login_message);

			loginView.setVisibility(View.VISIBLE);
			signUpView.setVisibility(View.VISIBLE);
			editProfileView.setVisibility(View.GONE);

			profileImageView.setImageResource(R.drawable.more_ic_mydaily_01_logout);
		}
	}

	public void updateAccountLayout(boolean isLogin)
	{
		View newCouponIconView = mAccountInfoLayout.findViewById(R.id.newCounponIconView);
		View newBonusIconView = mAccountInfoLayout.findViewById(R.id.newBonusIconView);
		TextView couponCountTextView = (TextView) mAccountInfoLayout.findViewById(R.id.couponCountTextView);
		TextView bonusCountTextView = (TextView) mAccountInfoLayout.findViewById(R.id.bonusCountTextView);

		boolean hasNewCoupon = false;
		boolean hasNewBonus = false;

		String couponCountString = "0";
		String bonusCountString = "0";

		if (isLogin == true)
		{
			mAccountInfoLayout.setVisibility(View.VISIBLE);

			newCouponIconView.setVisibility(hasNewCoupon ? View.VISIBLE : View.GONE);
			newBonusIconView.setVisibility(hasNewBonus ? View.VISIBLE : View.GONE);

			couponCountTextView.setText(mContext.getResources().getString(R.string.frag_count_text, couponCountString));
			bonusCountTextView.setText(mContext.getResources().getString(R.string.frag_count_text, bonusCountString));

		} else
		{
			mAccountInfoLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.loginTextView:
			{
				((OnEventListener) mOnEventListener).onLoginClick();
				break;
			}

			case R.id.signupTextView:
			{
				((OnEventListener) mOnEventListener).onSignUpClick();
				break;
			}

			case R.id.editProfileTextView:
			{
				((OnEventListener) mOnEventListener).showEditProfile();
				break;
			}

			case R.id.creditcardLayout:
			{
				((OnEventListener) mOnEventListener).showCreditCardList();
				break;
			}

			case R.id.couponLayout:
			{
				((OnEventListener) mOnEventListener).showCouponList();
				break;
			}

			case R.id.bonusLayout:
			{
				((OnEventListener) mOnEventListener).showBonusList();
				break;
			}

			case R.id.eventLayout:
			{
				((OnEventListener) mOnEventListener).showEvent();
				break;
			}

			case R.id.recommendLayout:
			{
				((OnEventListener) mOnEventListener).onRecommendClick();
				break;
			}

			case R.id.callLayout:
			{
				((OnEventListener) mOnEventListener).sendCall();
				break;
			}

			case R.id.mailLayout:
			{
				((OnEventListener) mOnEventListener).sendEmail();
				break;
			}

			case R.id.aboutLayout:
			{
				((OnEventListener) mOnEventListener).showAbout();
				break;
			}

			case R.id.pushTextView:
			{
				((OnEventListener) mOnEventListener).onPushClick(v);
				break;
			}

			case R.id.facebookLinkView:
			{
				((OnEventListener) mOnEventListener).onFacebookClick();
				break;
			}

			case R.id.instagramLinkView:
			{
				((OnEventListener) mOnEventListener).onInstagramClick();
				break;
			}

			case R.id.naverLinkView:
			{
				((OnEventListener) mOnEventListener).onNaverBlogClick();
				break;
			}

			case R.id.termsView:
			{
				((OnEventListener) mOnEventListener).showTerms();
				break;
			}

			case R.id.personalView:
			{
				((OnEventListener) mOnEventListener).showPersonal();
				break;
			}

			case R.id.locationTermsView:
			{
				((OnEventListener) mOnEventListener).showLocationTerms();
				break;
			}

			case R.id.protectChildTermsView:
			{
				((OnEventListener) mOnEventListener).showProtectChildTerms();
				break;
			}

			default:
				break;
		}
	}
}

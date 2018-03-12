package com.daily.dailyhotel.screen.home.gourmet.thankyou;


import android.animation.Animator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetCart;
import com.daily.dailyhotel.entity.NoticeAgreementMessage;
import com.daily.dailyhotel.entity.NoticeAgreementResultMessage;
import com.daily.dailyhotel.entity.UserTracking;
import com.daily.dailyhotel.parcel.GourmetCartParcel;
import com.daily.dailyhotel.parcel.analytics.GourmetThankYouAnalyticsParam;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetThankYouPresenter extends BaseExceptionPresenter<GourmetThankYouActivity, GourmetThankYouInterface> implements GourmetThankYouView.OnEventListener
{
    GourmetThankYouAnalyticsInterface mAnalytics;

    private ProfileRemoteImpl mProfileRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;

    private String mAggregationId;
    private GourmetBookDateTime mGourmetBookDateTime;
    private GourmetCart mGourmetCart;
    private int mPersons;

    public interface GourmetThankYouAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(GourmetThankYouAnalyticsParam analyticsParam);

        GourmetThankYouAnalyticsParam getAnalyticsParam();

        void onScreen(Activity activity);

        void onEventPayment(Activity activity);

        void onEventTracking(Activity activity, UserTracking userTracking);

        void onEventConfirmClick(Activity activity);

        void onEventBackClick(Activity activity);

        void onEventOrderComplete(Activity activity);
    }

    public GourmetThankYouPresenter(@NonNull GourmetThankYouActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected GourmetThankYouInterface createInstanceViewInterface()
    {
        return new GourmetThankYouView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(GourmetThankYouActivity activity)
    {
        setContentView(R.layout.activity_gourmet_payment_thank_you_data);

        setAnalytics(new GourmetThankYouAnalyticsImpl());

        mProfileRemoteImpl = new ProfileRemoteImpl(activity);
        mCommonRemoteImpl = new CommonRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (GourmetThankYouAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        GourmetCartParcel parcel = intent.getParcelableExtra(GourmetThankYouActivity.INTENT_EXTRA_DATA_GOURMET);
        if (parcel == null)
        {
            mGourmetCart = null;
        } else
        {
            mGourmetCart = parcel.getGourmetCart();
        }

        if (mGourmetCart == null)
        {
            return true;
        }

        setGourmetBookDateTime(mGourmetCart.visitTime);

        mAggregationId = intent.getStringExtra(GourmetThankYouActivity.INTENT_EXTRA_DATA_AGGREGATION_ID);

        mPersons = intent.getIntExtra(GourmetThankYouActivity.INTENT_EXTRA_DATA_PERSONS, 0);

        mAnalytics.setAnalyticsParam(intent.getParcelableExtra(BaseActivity.INTENT_EXTRA_DATA_ANALYTICS));

        mAnalytics.onEventPayment(getActivity());

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.label_completed_payment));
        getViewInterface().setImageUrl(mGourmetCart.imageUrl);

        String name = DailyUserPreference.getInstance(getActivity()).getName();
        getViewInterface().setUserName(name);

        final String DATE_FORMAT = "yyyy.MM.dd(EEE) HH:mm";

        try
        {
            String visitDate = mGourmetBookDateTime.getVisitDateTime(DATE_FORMAT);

            SpannableString visitDateSpannableString = new SpannableString(visitDate);
            visitDateSpannableString.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getMediumTypeface()),//
                visitDate.length() - 5, visitDate.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            getViewInterface().setBooking(visitDateSpannableString, mPersons, mGourmetCart);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        getViewInterface().startAnimation(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                unLockAll();
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
    }

    @Override
    public void onStart()
    {
        super.onStart();

        mAnalytics.onScreen(getActivity());
        mAnalytics.onEventOrderComplete(getActivity());

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
        if (isLock() == true)
        {
            return true;
        }

        mAnalytics.onEventBackClick(getActivity());

        startActivity(DailyInternalDeepLink.getGourmetBookingDetailScreenLink(getActivity(), mAggregationId));

        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);

        addCompositeDisposable(mProfileRemoteImpl.getTracking().subscribe(new Consumer<UserTracking>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull UserTracking userTracking) throws Exception
            {
                mAnalytics.onEventTracking(getActivity(), userTracking);

                unLockAll();

                if (isNotificationEnabled() == false)
                {
                    getViewInterface().showSimpleDialog(null, getString(R.string.message_gourmet_thankyou_disabled_notification)//
                        , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                        , v -> startAppSettingActivity(), null);
                } else if (isThankYouBenefitAlarmEnabled() == true && isBenefitAlarmEnabled() == false)
                {
                    getNotificationMessage();
                }
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                unLockAll();
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onConfirmClick()
    {
        if (isLock() == true)
        {
            return;
        }

        mAnalytics.onEventConfirmClick(getActivity());

        startActivity(DailyInternalDeepLink.getGourmetBookingDetailScreenLink(getActivity(), mAggregationId));

        finish();
    }

    private void setGourmetBookDateTime(String visitDateTime)
    {
        if (DailyTextUtils.isTextEmpty(visitDateTime) == true)
        {
            return;
        }

        if (mGourmetBookDateTime == null)
        {
            mGourmetBookDateTime = new GourmetBookDateTime();
        }

        try
        {
            mGourmetBookDateTime.setVisitDateTime(visitDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    boolean isNotificationEnabled()
    {
        return VersionUtils.isOverAPI19() ? NotificationManagerCompat.from(getActivity()).areNotificationsEnabled() : true;
    }

    boolean isThankYouBenefitAlarmEnabled()
    {
        return DailyUserPreference.getInstance(getActivity()).isThankYouBenefitAlarmEnabled();
    }

    boolean isBenefitAlarmEnabled()
    {
        return DailyUserPreference.getInstance(getActivity()).isBenefitAlarm();
    }

    void startAppSettingActivity()
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:com.twoheart.dailyhotel"));
        startActivity(intent);
    }

    void getNotificationMessage()
    {
        addCompositeDisposable(mCommonRemoteImpl.getNoticeAgreementMessage().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<NoticeAgreementMessage>()
        {
            @Override
            public void accept(NoticeAgreementMessage benefitMessage) throws Exception
            {
                getViewInterface().showSimpleDialog(getString(R.string.label_setting_alarm), benefitMessage.description1 + "\n\n" + benefitMessage.description2//
                    , getString(R.string.label_now_setting_alarm), 2.0f, getString(R.string.label_after_setting_alarm), 1.0f//
                    , new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            updateNotificationResultMessage(true);
                        }
                    }, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            updateNotificationResultMessage(false);
                        }
                    }, new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            updateNotificationResultMessage(false);
                        }
                    }, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            DailyUserPreference.getInstance(getActivity()).setThankYouBenefitAlarmEnabled(false);
                        }
                    }, true);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                ExLog.e(throwable.toString());
            }
        }));
    }

    void updateNotificationResultMessage(boolean agreed)
    {
        addCompositeDisposable(mCommonRemoteImpl.updateNoticeAgreement(agreed).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<NoticeAgreementResultMessage>()
        {
            @Override
            public void accept(NoticeAgreementResultMessage noticeAgreementResultMessage) throws Exception
            {
                DailyUserPreference.getInstance(getActivity()).setBenefitAlarm(agreed);

                String dateFormatString = DailyCalendar.convertDateFormatString(noticeAgreementResultMessage.agreedAt, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일");

                if (agreed == true)
                {
                    String message = noticeAgreementResultMessage.description1InAgree.replace("{{DATE}}", "\n" + dateFormatString) + "\n\n" + noticeAgreementResultMessage.description2InAgree;

                    getViewInterface().showSimpleDialog(getString(R.string.label_setting_alarm), message, getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                        }
                    });
                } else
                {
                    String message = noticeAgreementResultMessage.description1InReject.replace("{{DATE}}", "\n" + dateFormatString) + "\n\n" + noticeAgreementResultMessage.description2InReject;

                    getViewInterface().showSimpleDialog(getString(R.string.label_setting_alarm), message, getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                        }
                    });
                }
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {

            }
        }));
    }
}

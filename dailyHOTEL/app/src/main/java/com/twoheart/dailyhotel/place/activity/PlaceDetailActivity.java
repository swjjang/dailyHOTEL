package com.twoheart.dailyhotel.place.activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl;
import com.daily.dailyhotel.repository.local.model.AnalyticsParam;
import com.daily.dailyhotel.repository.remote.CalendarImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.network.model.TrueVRParams;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceDetailNetworkController;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.AddProfileSocialActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.util.AppResearch;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class PlaceDetailActivity extends BaseActivity
{
    //    protected static final String INTENT_EXTRA_DATA_START_SALETIME = "startSaleTime";
    //    protected static final String INTENT_EXTRA_DATA_END_SALETIME = "endSaleTime";

    protected static final int STATUS_INITIALIZE_NONE = 0; // 아무것도 데이터 관련 받은게 없는 상태
    protected static final int STATUS_INITIALIZE_DATA = 1; // 서버로 부터 데이터만 받은 상태
    protected static final int STATUS_INITIALIZE_LAYOUT = 2; // 데이터를 받아서 레이아웃을 만든 상태
    protected static final int STATUS_INITIALIZE_COMPLETE = -1; // 완료

    protected static final int SKIP_CHECK_DISCOUNT_PRICE_VALUE = Integer.MIN_VALUE;

    protected PlaceDetailLayout mPlaceDetailLayout;
    protected PlaceDetail mPlaceDetail;
    protected PlaceDetailNetworkController mPlaceDetailNetworkController;

    protected CommonRemoteImpl mCommonRemoteImpl;
    protected CalendarImpl mPlaceDetailCalendarImpl;
    protected RecentlyLocalImpl mRecentlyLocalImpl;

    protected PlaceBookingDay mPlaceBookingDay;
    protected TodayDateTime mTodayDateTime;

    protected int mCurrentImage;
    protected boolean mIsDeepLink;
    protected boolean mIsShowCalendar;
    protected boolean mIsShowVR;
    protected String mDefaultImageUrl;
    protected DailyToolbarView mToolbarView, mFakeToolbarView;
    protected boolean mDontReloadAtOnResume;
    protected boolean mIsTransitionEnd;
    protected int mInitializeStatus;

    //    protected Province mProvince;
    //    protected String mArea; // Analytics용 소지역
    protected int mViewPrice; // Analytics용 리스트 가격
    protected int mProductDetailIndex; // 딥링크로 시작시에 객실/티켓 정보 오픈후에 선택되어있는 인덱스
    protected PlaceReviewScores mPlaceReviewScores;
    protected ArrayList<TrueVRParams> mTrueVRParamsList;
    protected List<Integer> mSoldOutList;

    protected Handler mHandler = new Handler();

    private int mResultCode;
    protected Intent mResultIntent;
    protected boolean mIsUsedMultiTransition;
    protected Runnable mTransitionEndRunnable; // 트렌지션 중에 에러가 난경우 팝업을 띄워야 하는데 트렌지션으로 이슈가 발생하여 트레진션 끝나고 동작.

    protected AnalyticsParam mAnalyticsParam;

    private AppResearch mAppResearch;

    protected abstract PlaceDetailLayout getDetailLayout(Context context);

    protected abstract PlaceDetailNetworkController getNetworkController(Context context);

    protected abstract PlaceDetail createPlaceDetail(Intent intent);

    protected abstract void requestCommonDateTimeNSoldOutList(int placeIndex);

    protected abstract void setCommonDateTime(TodayDateTime todayDateTime);

    protected abstract void shareKakao(String imageUrl, PlaceBookingDay placeBookingDay, PlaceDetail placeDetail);

    protected abstract void onCalendarActivityResult(int resultCode, Intent data);

    protected abstract void doBooking();

    protected abstract void downloadCoupon(PlaceBookingDay placeBookingDay, PlaceDetail placeDetail);

    protected abstract void startKakao();

    protected abstract void shareSMS(PlaceBookingDay placeBookingDay, PlaceDetail placeDetail);

    protected abstract void onTrueViewClick();

    protected abstract void onWishClick();

    protected abstract void recordAnalyticsShareClicked();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mAppResearch = new AppResearch(this);

        mPlaceDetailLayout = getDetailLayout(this);
        mPlaceDetailNetworkController = getNetworkController(this);

        mCommonRemoteImpl = new CommonRemoteImpl(this);
        mPlaceDetailCalendarImpl = new CalendarImpl(this);
        mRecentlyLocalImpl = new RecentlyLocalImpl(this);
    }

    protected void initToolbar(String title)
    {
        mToolbarView = (DailyToolbarView) findViewById(R.id.toolbarView);
        mFakeToolbarView = (DailyToolbarView) findViewById(R.id.fakeToolbarView);

        mToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        mToolbarView.clearMenuItem();
        mToolbarView.addMenuItem(DailyToolbarView.MenuItem.WISH_OFF, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onWishClick();
            }
        });

        mToolbarView.addMenuItem(DailyToolbarView.MenuItem.SHARE, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onShareClick();
            }
        });

        mFakeToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        mFakeToolbarView.clearMenuItem();
        mFakeToolbarView.addMenuItem(DailyToolbarView.MenuItem.WISH_OFF, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onWishClick();
            }
        });

        mFakeToolbarView.addMenuItem(DailyToolbarView.MenuItem.SHARE, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onShareClick();
            }
        });
    }

    @Override
    protected void onStart()
    {
        try
        {
            super.onStart();
        } catch (NullPointerException e)
        {
            ExLog.e(e.toString());

            Util.restartApp(this);
        }
    }

    @Override
    protected void onResume()
    {
        if (mPlaceDetailLayout != null)
        {
            if (mPlaceDetailLayout.getBookingStatus() != PlaceDetailLayout.STATUS_SOLD_OUT)
            {
                mPlaceDetailLayout.setBookingStatus(PlaceDetailLayout.STATUS_SELECT_PRODUCT);
            }
        }

        if (mDontReloadAtOnResume == true)
        {
            mDontReloadAtOnResume = false;
        } else
        {
            if (mIsUsedMultiTransition == true && mInitializeStatus != STATUS_INITIALIZE_COMPLETE && mIsDeepLink == false)
            {
                lockUI(false);
            } else
            {
                lockUI();
            }

            requestCommonDateTimeNSoldOutList(mPlaceDetail.index);
        }

        super.onResume();

        // 앱 조사 관련해서 지원
        if (mPlaceDetail != null)
        {
            if (this instanceof StayDetailActivity)
            {
                mAppResearch.onResume("스테이", mPlaceDetail.index);
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (mPlaceDetailLayout != null && mPlaceDetailLayout.isWishTooltipVisibility() == true)
        {
            mPlaceDetailLayout.setWishTooltipVisibility(false, 0);
            DailyPreference.getInstance(this).setWishTooltip(false);
        }

        // 앱 조사 관련해서 지원
        if (mPlaceDetail != null)
        {
            if (this instanceof StayDetailActivity)
            {
                mAppResearch.onPause("스테이", mPlaceDetail.index);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    /**
     * 이전화면이 갱신되어야 하면 Transition 효과를 주지 않도록 한다.
     *
     * @param resultCode
     */
    public void setResultCode(int resultCode)
    {
        mResultCode = resultCode;

        if (mResultIntent == null)
        {
            mResultIntent = new Intent();
        }

        setResult(resultCode, mResultIntent);
    }

    public boolean isSameCallingActivity(String checkClassName)
    {
        ComponentName callingActivity = getCallingActivity();
        if (callingActivity == null || DailyTextUtils.isTextEmpty(checkClassName) == true)
        {
            return false;
        }

        String callingClassName = callingActivity.getClassName();
        return checkClassName.equalsIgnoreCase(callingClassName) == true;
    }

    @Override
    public void finish()
    {
        super.finish();

        if (mIsUsedMultiTransition == false)
        {
            overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mPlaceDetailLayout != null)
        {
            if (mIsUsedMultiTransition == true)
            {
                if (mResultCode == CODE_RESULT_ACTIVITY_REFRESH && isSameCallingActivity(MainActivity.class.getName()) == false)
                {
                    finish();
                    return;
                }

                if (mPlaceDetailLayout.isScrollViewTop() == true)
                {
                    mPlaceDetailLayout.setTransVisibility(View.VISIBLE);
                } else
                {
                    lockUiComponent();

                    mPlaceDetailLayout.setScrollViewTop();
                    mPlaceDetailLayout.setTransVisibility(View.VISIBLE);

                    mHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            PlaceDetailActivity.super.onBackPressed();
                        }
                    }, 300);

                    return;
                }
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockUI();

        try
        {
            switch (requestCode)
            {
                case CODE_REQUEST_ACTIVITY_BOOKING:
                {
                    setResultCode(resultCode);

                    switch (resultCode)
                    {
                        case RESULT_OK:
                        case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                        case CODE_RESULT_ACTIVITY_GO_HOME:
                            finish();
                            break;

                        case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                        case CODE_RESULT_ACTIVITY_REFRESH:
                        case CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER:
                            mDontReloadAtOnResume = false;
                            break;

                        default:
                            mDontReloadAtOnResume = true;
                            break;
                    }
                    break;
                }

                case CODE_REQUEST_ACTIVITY_LOGIN:
                case CODE_REQUEST_ACTIVITY_USERINFO_UPDATE:
                {
                    mDontReloadAtOnResume = true;

                    if (resultCode == RESULT_OK)
                    {
                        doBooking();
                    }
                    break;
                }

                case CODE_REQUEST_ACTIVITY_LOGIN_BY_COUPON:
                {
                    mDontReloadAtOnResume = false;

                    if (resultCode == RESULT_OK)
                    {
                        downloadCoupon(mPlaceBookingDay, mPlaceDetail);
                    }
                    break;
                }

                case CODE_REQUEST_ACTIVITY_LOGIN_BY_DETAIL_WISHLIST:
                {
                    mDontReloadAtOnResume = resultCode != RESULT_OK;
                    break;
                }

                case CODE_REQUEST_ACTIVITY_IMAGELIST:
                case CODE_REQUEST_ACTIVITY_ZOOMMAP:
                case CODE_REQUEST_ACTIVITY_SHAREKAKAO:
                case CODE_REQUEST_ACTIVITY_EXTERNAL_MAP:
                    if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                    {
                        setResult(CODE_RESULT_ACTIVITY_GO_HOME);
                        finish();
                    } else
                    {
                        mDontReloadAtOnResume = true;
                    }
                    break;

                case CODE_REQUEST_ACTIVITY_CALENDAR:
                    if (mIsShowCalendar == true)
                    {
                        mIsShowCalendar = false;
                        mDontReloadAtOnResume = false;
                    } else
                    {
                        mDontReloadAtOnResume = true;
                    }

                    onCalendarActivityResult(resultCode, data);
                    break;

                case CODE_REQUEST_ACTIVITY_DOWNLOAD_COUPON:
                    mDontReloadAtOnResume = true;
                    break;

                case CODE_REQUEST_ACTIVITY_FAQ:
                    if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                    {
                        setResult(CODE_RESULT_ACTIVITY_GO_HOME);
                        finish();
                    }
                    break;

                case CODE_REQUEST_ACTIVITY_TRUEVIEW:
                    mDontReloadAtOnResume = true;
                    break;
            }

            super.onActivityResult(requestCode, resultCode, data);
        } catch (NullPointerException e)
        {
            ExLog.e(e.toString());

            Util.restartApp(this);
        }
    }

    @Override
    public void onError()
    {
        super.onError();

        finish();
    }

    protected void moveToAddSocialUserInformation(Customer user, String birthday)
    {
        Intent intent = AddProfileSocialActivity.newInstance(this, user, birthday);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
    }

    protected void moveToUpdateUserPhoneNumber(Customer user, EditProfilePhoneActivity.Type type, String phoneNumber)
    {
        Intent intent = EditProfilePhoneActivity.newInstance(this, type, phoneNumber);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
    }

    public void showCallDialog(final PlaceType placeType)
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_contact_us_layout, null, false);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        // 버튼
        View contactUs01Layout = dialogView.findViewById(R.id.contactUs01Layout);
        View contactUs02Layout = dialogView.findViewById(R.id.contactUs02Layout);
        contactUs02Layout.setVisibility(View.GONE);

        DailyTextView contactUs01TextView = (DailyTextView) contactUs01Layout.findViewById(R.id.contactUs01TextView);
        contactUs01TextView.setText(R.string.frag_faqs);
        contactUs01TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_05_faq, 0, 0, 0);

        contactUs01Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                startFAQ();

                switch (placeType)
                {
                    case HOTEL:
                        AnalyticsManager.getInstance(PlaceDetailActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                            , AnalyticsManager.Action.FNQ_CLICK, AnalyticsManager.Label.STAY_DETAIL, null);
                        break;

                    case FNB:
                        AnalyticsManager.getInstance(PlaceDetailActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                            , AnalyticsManager.Action.FNQ_CLICK, AnalyticsManager.Label.GOURMET_DETAIL, null);
                        break;
                }
            }
        });

        View kakaoDailyView = dialogView.findViewById(R.id.kakaoDailyView);
        View callDailyView = dialogView.findViewById(R.id.callDailyView);

        kakaoDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                startKakao();

                switch (placeType)
                {
                    case HOTEL:
                        AnalyticsManager.getInstance(PlaceDetailActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                            , AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.STAY_DETAIL, null);
                        break;

                    case FNB:
                        AnalyticsManager.getInstance(PlaceDetailActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                            , AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.GOURMET_DETAIL, null);
                        break;
                }
            }
        });

        callDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                showDailyCallDialog(null);

                switch (placeType)
                {
                    case HOTEL:
                        AnalyticsManager.getInstance(PlaceDetailActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                            , AnalyticsManager.Action.CALL_CLICK, AnalyticsManager.Label.STAY_DETAIL, null);
                        break;

                    case FNB:
                        AnalyticsManager.getInstance(PlaceDetailActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                            , AnalyticsManager.Action.CALL_CLICK, AnalyticsManager.Label.GOURMET_DETAIL, null);
                        break;
                }
            }
        });

        View closeView = dialogView.findViewById(R.id.closeView);
        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockUI();
            }
        });

        try
        {
            dialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    void startFAQ()
    {
        startActivityForResult(new Intent(this, FAQActivity.class), CODE_REQUEST_ACTIVITY_FAQ);
    }

    protected void showTrueViewMenu()
    {
        if (mToolbarView == null || mFakeToolbarView == null)
        {
            return;
        }

        mPlaceDetailLayout.setVRIconVisible(true);

        if (DailyPreference.getInstance(this).isWishTooltip() == true)
        {
            mPlaceDetailLayout.setWishTooltipVisibility(true, 2);

            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    DailyPreference.getInstance(PlaceDetailActivity.this).setWishTooltip(false);
                    mPlaceDetailLayout.hideAnimationTooltip();
                }
            }, 3000);
        } else
        {
            mPlaceDetailLayout.setWishTooltipVisibility(false, 0);
        }
    }

    protected void hideTrueViewMenu()
    {
        if (mToolbarView == null || mFakeToolbarView == null)
        {
            return;
        }

        mPlaceDetailLayout.setVRIconVisible(false);

        if (DailyPreference.getInstance(this).isWishTooltip() == true)
        {
            mPlaceDetailLayout.setWishTooltipVisibility(true, 1);

            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    DailyPreference.getInstance(PlaceDetailActivity.this).setWishTooltip(false);
                    mPlaceDetailLayout.hideAnimationTooltip();
                }
            }, 3000);
        } else
        {
            mPlaceDetailLayout.setWishTooltipVisibility(false, 0);
        }
    }

    protected void setWishTextView(boolean selected, int count)
    {
        if (mToolbarView == null || mFakeToolbarView == null)
        {
            return;
        }

        String wishCountText;

        if (count <= 0)
        {
            wishCountText = null;
        } else if (count > 9999)
        {
            int wishCount = count / 1000;

            if (wishCount % 10 == 0)
            {
                wishCountText = getString(R.string.wishlist_count_over_10_thousand, Integer.toString(wishCount / 10));
            } else
            {
                wishCountText = getString(R.string.wishlist_count_over_10_thousand, Float.toString((float) wishCount / 10.0f));
            }
        } else
        {
            DecimalFormat decimalFormat = new DecimalFormat("###,##0");
            wishCountText = decimalFormat.format(count);
        }

        if (mToolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_OFF) == true)
        {
            if (selected == true)
            {
                mToolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_OFF, DailyToolbarView.MenuItem.WISH_FILL_ON, wishCountText, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onWishClick();
                    }
                });
            } else
            {
                mToolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_OFF, wishCountText, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onWishClick();
                    }
                });
            }
        } else if (mToolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_FILL_ON) == true)
        {
            if (selected == true)
            {
                mToolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_FILL_ON, wishCountText, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onWishClick();
                    }
                });
            } else
            {
                mToolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_FILL_ON, DailyToolbarView.MenuItem.WISH_OFF, wishCountText, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onWishClick();
                    }
                });
            }
        }

        if (mFakeToolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_OFF) == true)
        {
            if (selected == true)
            {
                mFakeToolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_OFF, DailyToolbarView.MenuItem.WISH_LINE_ON, wishCountText, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onWishClick();
                    }
                });
            } else
            {
                mFakeToolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_OFF, wishCountText, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onWishClick();
                    }
                });
            }
        } else if (mFakeToolbarView.hasMenuItem(DailyToolbarView.MenuItem.WISH_LINE_ON) == true)
        {
            if (selected == true)
            {
                mFakeToolbarView.updateMenuItem(DailyToolbarView.MenuItem.WISH_LINE_ON, wishCountText, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onWishClick();
                    }
                });
            } else
            {
                mFakeToolbarView.replaceMenuItem(DailyToolbarView.MenuItem.WISH_LINE_ON, DailyToolbarView.MenuItem.WISH_OFF, wishCountText, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onWishClick();
                    }
                });
            }
        }
    }

    void onShareClick()
    {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_sharedialog_layout, null, false);

        final Dialog shareDialog = new Dialog(PlaceDetailActivity.this);
        shareDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        shareDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        shareDialog.setCanceledOnTouchOutside(true);

        if (Util.isTelephonyEnabled(PlaceDetailActivity.this) == false)
        {
            View smsShareLayout = dialogView.findViewById(R.id.smsShareLayout);
            smsShareLayout.setVisibility(View.GONE);
        }

        // 버튼
        View kakaoShareView = dialogView.findViewById(R.id.kakaoShareView);

        kakaoShareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (shareDialog.isShowing() == true)
                {
                    shareDialog.dismiss();
                }

                if (mDefaultImageUrl == null)
                {
                    if (mPlaceDetail.getImageList() != null && mPlaceDetail.getImageList().size() > 0)
                    {
                        ImageInformation imageInformation = (ImageInformation) mPlaceDetail.getImageList().get(0);
                        mDefaultImageUrl = imageInformation.getImageUrl();
                    }
                }

                shareKakao(mDefaultImageUrl, mPlaceBookingDay, mPlaceDetail);
            }
        });

        View smsShareView = dialogView.findViewById(R.id.smsShareView);

        smsShareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (shareDialog.isShowing() == true)
                {
                    shareDialog.dismiss();
                }

                shareSMS(mPlaceBookingDay, mPlaceDetail);
            }
        });

        View closeTextView = dialogView.findViewById(R.id.closeTextView);
        closeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (shareDialog.isShowing() == true)
                {
                    shareDialog.dismiss();
                }
            }
        });

        try
        {
            shareDialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(PlaceDetailActivity.this, shareDialog);

            shareDialog.show();

            shareDialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        recordAnalyticsShareClicked();
    }
}

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

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceDetailNetworkController;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailLayout;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.screen.information.member.AddProfileSocialActivity;
import com.twoheart.dailyhotel.screen.information.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public abstract class PlaceDetailActivity extends BaseActivity
{
    protected static final String INTENT_EXTRA_DATA_START_SALETIME = "startSaleTime";
    protected static final String INTENT_EXTRA_DATA_END_SALETIME = "endSaleTime";

    protected static final int STATUS_INITIALIZE_NONE = 0; // 아무것도 데이터 관련 받은게 없는 상태
    protected static final int STATUS_INITIALIZE_DATA = 1; // 서버로 부터 데이터만 받은 상태
    protected static final int STATUS_INITIALIZE_LAYOUT = 2; // 데이터를 받아서 레이아웃을 만든 상태
    protected static final int STATUS_INITIALIZE_COMPLETE = -1; // 완료

    protected PlaceDetailLayout mPlaceDetailLayout;
    protected PlaceDetail mPlaceDetail;
    protected PlaceDetailNetworkController mPlaceDetailNetworkController;

    protected SaleTime mSaleTime;
    protected SaleTime mStartSaleTime, mEndSaleTime;
    protected int mCurrentImage;
    protected boolean mIsDeepLink;
    protected String mDefaultImageUrl;
    protected DailyToolbarLayout mDailyToolbarLayout;
    protected boolean mDontReloadAtOnResume;
    protected boolean mIsTransitionEnd;
    protected int mInitializeStatus;

    protected Province mProvince;
    protected String mArea; // Analytics용 소지역
    protected int mViewPrice; // Analytics용 리스트 가격
    protected int mOpenTicketIndex; // 딥링크로 시작시에 객실/티켓 정보 오픈후에 선택되어있는 인덱스

    protected Handler mHandler = new Handler();

    private int mResultCode;
    protected Intent mResultIntent;

    protected abstract PlaceDetailLayout getDetailLayout(Context context);

    protected abstract PlaceDetailNetworkController getNetworkController(Context context);

    protected abstract PlaceDetail createPlaceDetail(Intent intent);

    protected abstract void shareKakao(PlaceDetail placeDetail, String imageUrl);

    protected abstract void onCalendarActivityResult(int resultCode, Intent data);

    protected abstract void hideProductInformationLayout();

    protected abstract void doBooking();

    protected abstract void downloadCoupon();

    protected abstract void startKakao();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mPlaceDetailLayout = getDetailLayout(this);
        mPlaceDetailNetworkController = getNetworkController(this);
    }

    protected void initToolbar(String title)
    {
        View toolbar = findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(title, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        }, false);

        mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_share_01_black, -1);
        mDailyToolbarLayout.setToolbarMenuClickListener(mToolbarOptionsItemSelectedListener);

        View backImage = findViewById(R.id.backView);
        View shareView = findViewById(R.id.shareView);

        backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        shareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mToolbarOptionsItemSelectedListener.onClick(null);
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
            mPlaceDetailLayout.hideProductInformationLayout();
            mPlaceDetailLayout.showWishButton();

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
            if (Util.isUsedMultiTransition() == true && mInitializeStatus != STATUS_INITIALIZE_COMPLETE && mIsDeepLink == false)
            {
                lockUI(false);
            } else
            {
                lockUI();
            }

            mPlaceDetailNetworkController.requestCommonDatetime();
        }

        super.onResume();
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
        if (callingActivity == null || Util.isTextEmpty(checkClassName) == true)
        {
            return false;
        }

        String callingClassName = callingActivity.getClassName();
        if (checkClassName.equalsIgnoreCase(callingClassName) == true)
        {
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mPlaceDetailLayout != null)
        {
            switch (mPlaceDetailLayout.getBookingStatus())
            {
                case StayDetailLayout.STATUS_BOOKING:
                case StayDetailLayout.STATUS_NONE:
                    hideProductInformationLayout();
                    return;
            }

            if (Util.isUsedMultiTransition() == true && mResultCode == CODE_RESULT_ACTIVITY_REFRESH)
            {
                finish();
                return;
            }

            if (Util.isOverAPI21() == true)
            {
                if (mPlaceDetailLayout.isListScrollTop() == true)
                {
                    mPlaceDetailLayout.setTransImageVisibility(true);
                } else
                {
                    mPlaceDetailLayout.setListScrollTop();

                    mHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            onBackPressed();
                        }
                    }, 100);

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
                            finish();
                            break;

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
                        downloadCoupon();
                    }
                    break;
                }

                case CODE_REQUEST_ACTIVITY_LOGIN_BY_DETAIL_WISHLIST:
                {
                    if (resultCode == RESULT_OK)
                    {
                        mDontReloadAtOnResume = false;
                    } else
                    {
                        mDontReloadAtOnResume = true;
                    }
                    break;
                }

                case CODE_REQUEST_ACTIVITY_IMAGELIST:
                case CODE_REQUEST_ACTIVITY_ZOOMMAP:
                case CODE_REQUEST_ACTIVITY_SHAREKAKAO:
                case CODE_REQUEST_ACTIVITY_EXTERNAL_MAP:
                    mDontReloadAtOnResume = true;
                    break;

                case CODE_REQUEST_ACTIVITY_CALENDAR:
                    mDontReloadAtOnResume = true;
                    onCalendarActivityResult(resultCode, data);
                    break;

                case CODE_REQUEST_ACTIVITY_DOWNLOAD_COUPON:
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

    protected void onWishButtonClick(PlaceType placeType, PlaceDetail placeDetail)
    {
        if (isLockUiComponent() == true || isFinishing() == true)
        {
            return;
        }

        if (placeDetail == null)
        {
            return;
        }

        lockUiComponent();

        boolean isExpectSelected = !placeDetail.myWish;
        int wishCount = isExpectSelected == true ? placeDetail.wishCount + 1 : placeDetail.wishCount - 1;
        mPlaceDetailLayout.setWishButtonCount(wishCount);
        mPlaceDetailLayout.setWishButtonSelected(isExpectSelected);

        if (isExpectSelected == true)
        {
            mPlaceDetailNetworkController.requestAddWishList(placeType, placeDetail.index);
        } else
        {
            mPlaceDetailNetworkController.requestRemoveWishList(placeType, placeDetail.index);
        }
    }

    protected void moveToAddSocialUserInformation(Customer user, String birthday)
    {
        Intent intent = AddProfileSocialActivity.newInstance(this, user, birthday);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
    }

    protected void moveToUpdateUserPhoneNumber(Customer user, EditProfilePhoneActivity.Type type)
    {
        Intent intent = EditProfilePhoneActivity.newInstance(this, user.getUserIdx(), type);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
    }

    public void showCallDialog()
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

            WindowManager.LayoutParams layoutParams = Util.getDialogWidthLayoutParams(this, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void startFAQ()
    {
        startActivityForResult(new Intent(this, FAQActivity.class), CODE_REQUEST_ACTIVITY_FAQ);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UserActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener mToolbarOptionsItemSelectedListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = layoutInflater.inflate(R.layout.view_sharedialog_layout, null, false);

            final Dialog shareDialog = new Dialog(PlaceDetailActivity.this);
            shareDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            shareDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            shareDialog.setCanceledOnTouchOutside(true);

            // 버튼
            View kakaoShareLayout = dialogView.findViewById(R.id.kakaoShareLayout);

            kakaoShareLayout.setOnClickListener(new View.OnClickListener()
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
                        if (mPlaceDetail.getImageInformationList() != null && mPlaceDetail.getImageInformationList().size() > 0)
                        {
                            mDefaultImageUrl = mPlaceDetail.getImageInformationList().get(0).url;
                        }
                    }

                    shareKakao(mPlaceDetail, mDefaultImageUrl);
                }
            });

            try
            {
                shareDialog.setContentView(dialogView);

                WindowManager.LayoutParams layoutParams = Util.getDialogWidthLayoutParams(PlaceDetailActivity.this, shareDialog);

                shareDialog.show();

                shareDialog.getWindow().setAttributes(layoutParams);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };
}

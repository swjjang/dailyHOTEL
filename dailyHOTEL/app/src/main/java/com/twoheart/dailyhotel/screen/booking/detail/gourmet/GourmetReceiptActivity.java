package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyEditText;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.GourmetReceipt;
import com.daily.dailyhotel.repository.remote.ReceiptRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class GourmetReceiptActivity extends BaseActivity
{
    private boolean mIsFullscreen;
    private String mAggregationId;
    private DailyToolbarView mDailyToolbarView;
    private View mBottomLayout;
    private int mReservationIndex;

    private ReceiptRemoteImpl mReceiptRemoteImpl;

    public static Intent newInstance(Context context, int bookingIndex, String aggregationId)
    {
        Intent intent = new Intent(context, GourmetReceiptActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, bookingIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_AGGREGATION_ID, aggregationId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        initToolbar();

        mReservationIndex = -1;

        Intent intent = getIntent();

        if (intent != null)
        {
            mReservationIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, -1);
            mAggregationId = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_AGGREGATION_ID);
        }

        if (mReservationIndex < 0 && DailyTextUtils.isTextEmpty(mAggregationId) == true)
        {
            finish();
            return;
        }

        mIsFullscreen = false;

        mReceiptRemoteImpl = new ReceiptRemoteImpl(this);
    }

    private void initToolbar()
    {
        mDailyToolbarView = (DailyToolbarView) findViewById(R.id.toolbarView);
        mDailyToolbarView.setTitleText(R.string.frag_issuing_receipt);
        mDailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    protected void onResume()
    {
        lockUI();

        requestReceiptDetail(mReservationIndex, mAggregationId);

        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        if (mIsFullscreen == true)
        {
            mIsFullscreen = false;
            updateFullscreenStatus(false);
        } else
        {
            super.onBackPressed();
        }
    }

    private void updateFullscreenStatus(boolean bUseFullscreen)
    {
        if (bUseFullscreen)
        {
            mDailyToolbarView.setVisibility(View.GONE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            mBottomLayout.setVisibility(View.GONE);
        } else
        {
            mDailyToolbarView.setVisibility(View.VISIBLE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            mBottomLayout.setVisibility(View.VISIBLE);
        }
    }

    private void makeLayout(GourmetReceipt gourmetReceipt) throws Exception
    {
        // 영수증
        mReservationIndex = gourmetReceipt.gourmetReservationIdx;

        if (gourmetReceipt.gourmetReservationIdx < 0)
        {
            Crashlytics.logException(new NullPointerException("GourmetReceiptActivity : mReservationIndex == null"));
        }

        // **예약 세부 정보**
        View bookingInfoLayout = findViewById(R.id.bookingInfoLayout);

        // 예약 번호
        TextView reservationNumberTextView = bookingInfoLayout.findViewById(R.id.textView13);
        reservationNumberTextView.setText(Integer.toString(gourmetReceipt.gourmetReservationIdx));

        // 이름
        TextView hotelNameTextView = bookingInfoLayout.findViewById(R.id.textView3);
        hotelNameTextView.setText(gourmetReceipt.restaurantName);

        // 주소
        TextView hotelAddressTextView = bookingInfoLayout.findViewById(R.id.textView5);
        hotelAddressTextView.setText(gourmetReceipt.restaurantAddress);

        // 고객성명/번호
        TextView customerInfoTextView = bookingInfoLayout.findViewById(R.id.textView7);
        customerInfoTextView.setText(gourmetReceipt.userName + " / " + gourmetReceipt.userPhone);

        // 날짜
        TextView checkInOutTextView = bookingInfoLayout.findViewById(R.id.textView9);
        checkInOutTextView.setText(gourmetReceipt.sday.replaceAll("-", "/"));

        // 수량
        TextView nightsRoomsTextView = bookingInfoLayout.findViewById(R.id.textView11);
        nightsRoomsTextView.setText(getString(R.string.label_booking_count, gourmetReceipt.ticketCount));

        // **결제 정보**
        View paymentInfoLayout = findViewById(R.id.paymentInfoLayout);

        // 결제일
        TextView paymentDayTextView = paymentInfoLayout.findViewById(R.id.textView23);
        paymentDayTextView.setText(DailyCalendar.convertDateFormatString(gourmetReceipt.paidAt, DailyCalendar.ISO_8601_FORMAT, "yyyy/MM/dd"));

        // 결제수단
        View paymentTypeLayout = paymentInfoLayout.findViewById(R.id.paymentTypeLayout);

        if (DailyTextUtils.isTextEmpty(gourmetReceipt.paymentType) == true)
        {
            paymentTypeLayout.setVisibility(View.GONE);
        } else
        {
            paymentTypeLayout.setVisibility(View.VISIBLE);

            TextView paymentTypeTextView = paymentInfoLayout.findViewById(R.id.textView33);
            paymentTypeTextView.setText(gourmetReceipt.paymentType);
        }

        View saleLayout = paymentInfoLayout.findViewById(R.id.saleLayout);
        saleLayout.setVisibility(View.VISIBLE);

        // 총금액
        TextView totalPriceTextView = paymentInfoLayout.findViewById(R.id.textView29);
        totalPriceTextView.setText(DailyTextUtils.getPriceFormat(this, gourmetReceipt.price, false));

        // 적립금 혹은 쿠폰 사용
        View discountLayout = paymentInfoLayout.findViewById(R.id.discountLayout);

        if (gourmetReceipt.couponAmount > 0)
        {
            discountLayout.setVisibility(View.VISIBLE);
            TextView discountedTextView = paymentInfoLayout.findViewById(R.id.discountedTextView);
            discountedTextView.setText("- " + DailyTextUtils.getPriceFormat(this, gourmetReceipt.couponAmount, false));

            saleLayout.setVisibility(View.VISIBLE);
        } else
        {
            discountLayout.setVisibility(View.GONE);

            saleLayout.setVisibility(View.GONE);
        }

        // 총 입금 금액
        TextView totalPaymentTextView = paymentInfoLayout.findViewById(R.id.totalPaymentTextView);
        totalPaymentTextView.setText(DailyTextUtils.getPriceFormat(this, gourmetReceipt.paymentAmount, false));

        // **공급자**

        String phone = DailyRemoteConfigPreference.getInstance(GourmetReceiptActivity.this).getRemoteConfigCompanyPhoneNumber();
        String fax = DailyRemoteConfigPreference.getInstance(GourmetReceiptActivity.this).getRemoteConfigCompanyFax();
        String address = DailyRemoteConfigPreference.getInstance(GourmetReceiptActivity.this).getRemoteConfigCompanyAddress();
        String ceoName = DailyRemoteConfigPreference.getInstance(GourmetReceiptActivity.this).getRemoteConfigCompanyCEO();
        String registrationNo = DailyRemoteConfigPreference.getInstance(GourmetReceiptActivity.this).getRemoteConfigCompanyBizRegNumber();
        String companyName = DailyRemoteConfigPreference.getInstance(GourmetReceiptActivity.this).getRemoteConfigCompanyName();

        View providerInfoLayout = findViewById(R.id.providerInfoLayout);

        // 상호
        TextView companyNameTextView = providerInfoLayout.findViewById(R.id.companyNameTextView);
        companyNameTextView.setText(getString(R.string.label_receipt_business_license, companyName, ceoName, phone, fax));

        // 주소
        TextView addressTextView = providerInfoLayout.findViewById(R.id.addressTextView);
        addressTextView.setText(getString(R.string.label_receipt_address, address));

        // 등록번호
        TextView registrationNoTextView = providerInfoLayout.findViewById(R.id.registrationNoTextView);
        registrationNoTextView.setText(getString(R.string.label_receipt_registeration_number, registrationNo));

        // 코멘트
        TextView commentTextView = findViewById(R.id.commentTextView);
        commentTextView.setText(gourmetReceipt.notice);

        View view = findViewById(R.id.receiptLayout);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mIsFullscreen = !mIsFullscreen;
                updateFullscreenStatus(mIsFullscreen);
            }
        });

        mBottomLayout = findViewById(R.id.bottomLayout);
        View sendEmailView = mBottomLayout.findViewById(R.id.sendEmailView);
        sendEmailView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (gourmetReceipt.gourmetReservationIdx < 0)
                {
                    restartExpiredSession();
                } else
                {
                    showSendEmailDialog();
                }
            }
        });
    }

    private View getLayout()
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewGroup = inflater.inflate(R.layout.activity_place_receipt, null, false);

        LinearLayout receiptLayout = viewGroup.findViewById(R.id.receiptLayout);

        View reservationInfoLayout = inflater.inflate(R.layout.layout_gourmet_reservationinfo_receipt, null, false);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(this, 165));
        receiptLayout.addView(reservationInfoLayout, 0, layoutParams);

        return viewGroup;
    }

    private void requestReceiptDetail(int reservationIndex, String aggregationId)
    {
        Observable<GourmetReceipt> receiptObservable = Observable.defer(new Callable<ObservableSource<GourmetReceipt>>()
        {
            @Override
            public ObservableSource<GourmetReceipt> call() throws Exception
            {
                if (DailyTextUtils.isTextEmpty(aggregationId) == true)
                {
                    return mReceiptRemoteImpl.getGourmetReceipt(reservationIndex);
                }

                return mReceiptRemoteImpl.getGourmetReceipt(aggregationId);
            }
        });

        addCompositeDisposable(receiptObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetReceipt>()
        {
            @Override
            public void accept(GourmetReceipt gourmetReceipt) throws Exception
            {
                makeLayout(gourmetReceipt);

                unLockUI();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }

    private void showSendEmailDialog()
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_email_layout, null, false);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        final DailyEditText emailEditTExt = dialogView.findViewById(R.id.emailEditTExt);
        emailEditTExt.setDeleteButtonVisible(new DailyEditText.OnDeleteTextClickListener()
        {
            @Override
            public void onDelete(DailyEditText dailyEditText)
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(dailyEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        emailEditTExt.setText(DailyUserPreference.getInstance(this).getEmail());
        emailEditTExt.setSelection(emailEditTExt.length());

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);

        twoButtonLayout.setVisibility(View.VISIBLE);

        TextView negativeTextView = twoButtonLayout.findViewById(R.id.negativeTextView);
        final TextView positiveTextView = twoButtonLayout.findViewById(R.id.positiveTextView);

        negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
            }
        });

        positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = emailEditTExt.getText().toString();

                if (DailyTextUtils.validEmail(email) == false)
                {
                    DailyToast.showToast(GourmetReceiptActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                    return;
                }

                if (dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }

                Observable<String> emailObservable = Observable.defer(new Callable<ObservableSource<? extends String>>()
                {
                    @Override
                    public ObservableSource<? extends String> call() throws Exception
                    {
                        if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
                        {
                            return mReceiptRemoteImpl.getGourmetReceiptByEmail(mReservationIndex, email);
                        }

                        return mReceiptRemoteImpl.getGourmetReceiptByEmail(mAggregationId, email);
                    }
                });

                addCompositeDisposable(emailObservable.observeOn(AndroidSchedulers.mainThread()) //
                    .subscribe(new Consumer<String>()
                    {
                        @Override
                        public void accept(String message) throws Exception
                        {
                            showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
                            unLockUI();
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(Throwable throwable) throws Exception
                        {
                            onHandleError(throwable);
                        }
                    }));
            }
        });

        emailEditTExt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable == null || editable.length() == 0)
                {
                    positiveTextView.setEnabled(false);
                } else
                {
                    positiveTextView.setEnabled(true);
                }
            }
        });

        dialog.setCancelable(true);

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
}

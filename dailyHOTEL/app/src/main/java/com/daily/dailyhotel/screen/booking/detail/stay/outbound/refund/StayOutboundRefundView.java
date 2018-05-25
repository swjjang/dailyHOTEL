package com.daily.dailyhotel.screen.booking.detail.stay.outbound.refund;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundRefundDataBinding;
import com.twoheart.dailyhotel.databinding.DialogStayOutboundRefundCancelDialogDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundRefund01DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundRefund02DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundRefund03DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundRefundCancelReasonEtcDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.List;
import java.util.Locale;

public class StayOutboundRefundView extends BaseDialogView<StayOutboundRefundView.OnEventListener, ActivityStayOutboundRefundDataBinding> //
    implements StayOutboundRefundInterface, View.OnClickListener
{
    private LayoutStayOutboundRefund01DataBinding mRefund01DataBinding;
    private LayoutStayOutboundRefund02DataBinding mRefund02DataBinding;
    private LayoutStayOutboundRefund03DataBinding mRefund03DataBinding;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onRefundClick();

        void onCancelReasonClick();

        void onCancelReasonClick(String key, String reasonText, String message);
    }

    public StayOutboundRefundView(BaseActivity baseActivity, StayOutboundRefundView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundRefundDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        mRefund01DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.layout_stay_outbound_refund_01_data, viewDataBinding.scrollLayout, true);

        mRefund02DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.layout_stay_outbound_refund_02_data, viewDataBinding.scrollLayout, true);

        mRefund03DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.layout_stay_outbound_refund_03_data, viewDataBinding.scrollLayout, true);

        mRefund03DataBinding.selectReasonCancelView.setOnClickListener(this);
        mRefund03DataBinding.requestRefundView.setOnClickListener(this);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.selectReasonCancelView:
                getEventListener().onCancelReasonClick();
                break;

            case R.id.requestRefundView:
                getEventListener().onRefundClick();
                break;
        }
    }

    @Override
    public void setBookingDate(SpannableString checkInDate, SpannableString checkOutDate, int nights)
    {
        if (getViewDataBinding() == null || mRefund01DataBinding == null)
        {
            return;
        }

        mRefund01DataBinding.checkInDayTextView.setText(checkInDate);
        mRefund01DataBinding.checkOutDayTextView.setText(checkOutDate);
        mRefund01DataBinding.nightsTextView.setText(getString(R.string.label_nights, nights));
    }

    @Override
    public void setRefundDetail(StayOutboundRefundDetail stayOutboundRefundDetail)
    {
        setBookingInformation(getContext(), mRefund01DataBinding, stayOutboundRefundDetail);

        setPaymentInformation(getContext(), mRefund02DataBinding, stayOutboundRefundDetail);

        if (stayOutboundRefundDetail.refundStatus != null)
        {
            switch (stayOutboundRefundDetail.refundStatus)
            {
                case PARTIAL:
                    mRefund03DataBinding.requestRefundView.setText(R.string.label_contact_request_refund);
                    break;

                case FULL:
                    mRefund03DataBinding.requestRefundView.setText(R.string.label_request_free_refund);
                    break;

                default:
                    mRefund03DataBinding.requestRefundView.setEnabled(false);
                    break;
            }
        } else
        {
            mRefund03DataBinding.requestRefundView.setEnabled(false);
        }
    }

    @Override
    public void showCancelReasonListDialog(List<Pair<String, String>> cancelList, String key, String message)
    {
        if (cancelList == null || cancelList.size() == 0)
        {
            return;
        }

        DialogStayOutboundRefundCancelDialogDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_stay_outbound_refund_cancel_dialog_data, null, false);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText messageEditText = dataBinding.scrollLayout.findViewById(R.id.messageEditText);
                View messageClickView = dataBinding.scrollLayout.findViewById(R.id.messageClickView);
                View cancelReasonEtcView = dataBinding.scrollLayout.findViewById(R.id.cancelReasonEtcView);

                setSelected(v);

                if (messageEditText != null && messageClickView != null)
                {
                    messageEditText.setText(null);
                    messageClickView.setVisibility(View.VISIBLE);
                    messageClickView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            cancelReasonEtcView.performClick();
                        }
                    });

                    messageEditText.setCursorVisible(false);
                    messageEditText.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(messageEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                        }
                    });
                }
            }

            private void setSelected(View view)
            {
                Object tag = ((View) view.getParent()).getTag();

                if (tag != null && tag instanceof DailyTextView == true)
                {
                    ((DailyTextView) tag).setSelected(false);
                    ((DailyTextView) tag).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

                ((View) view.getParent()).setTag(view);
                view.setSelected(true);
                ((DailyTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.card_btn_v_select, 0);

                dataBinding.positiveTextView.setEnabled(true);
            }
        };

        // 기타 클릭
        View.OnClickListener onOTHClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText messageEditText = dataBinding.scrollLayout.findViewById(R.id.messageEditText);
                View messageClickView = dataBinding.scrollLayout.findViewById(R.id.messageClickView);

                setSelected(v);
                dataBinding.scrollView.fullScroll(View.FOCUS_DOWN);

                if (messageEditText != null && messageClickView != null)
                {
                    messageClickView.setVisibility(View.GONE);
                    messageClickView.setOnClickListener(null);

                    messageEditText.setCursorVisible(true);
                    messageEditText.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(messageEditText, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
            }

            private void setSelected(View view)
            {
                Object tag = dataBinding.scrollLayout.getTag();

                if (tag != null && tag instanceof DailyTextView == true)
                {
                    ((DailyTextView) tag).setSelected(false);
                    ((DailyTextView) tag).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

                dataBinding.scrollLayout.setTag(view);
                view.setSelected(true);
                ((DailyTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.card_btn_v_select, 0);

                dataBinding.positiveTextView.setEnabled(true);
            }
        };

        int index = 0;
        final String OTH = "OTH";
        DailyTextView dailyTextView;
        DailyTextView selectedDailyTextView = null;
        LayoutStayOutboundRefundCancelReasonEtcDataBinding othDataBinding;

        for (Pair<String, String> reason : cancelList)
        {
            // 기타가 존재하는 경우
            if (OTH.equalsIgnoreCase(reason.first) == true)
            {
                othDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_stay_outbound_refund_cancel_reason_etc_data, dataBinding.scrollLayout, true);
                dailyTextView = othDataBinding.cancelReasonEtcView;

                dailyTextView.setText(reason.second);
                dailyTextView.setTag(reason.first);
                dailyTextView.setOnClickListener(onOTHClickListener);

                othDataBinding.messageEditText.setText(message);
                othDataBinding.messageEditText.setSelection(othDataBinding.messageEditText.length());
                othDataBinding.messageEditText.setCursorVisible(false);

                TextView cancelReasonEtcView = othDataBinding.cancelReasonEtcView;

                othDataBinding.messageClickView.setVisibility(View.VISIBLE);
                othDataBinding.messageClickView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        cancelReasonEtcView.performClick();
                    }
                });

                TextView messageCountTextView = othDataBinding.messageCountTextView;

                othDataBinding.messageEditText.addTextChangedListener(new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {

                    }

                    @Override
                    public void afterTextChanged(Editable s)
                    {
                        messageCountTextView.setText(String.format(Locale.KOREA, "(%d/300자)", s.length()));
                    }
                });

                othDataBinding.messageEditText.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        switch (event.getAction() & MotionEvent.ACTION_MASK)
                        {
                            case MotionEvent.ACTION_DOWN:
                                v.getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
                                break;

                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                v.getParent().getParent().getParent().requestDisallowInterceptTouchEvent(false);
                                break;
                        }
                        return false;
                    }
                });

                index += 2;
            } else
            {
                dailyTextView = new DailyTextView(getContext());
                dailyTextView.setTextColor(getColorStateList(R.drawable.selector_text_color_c323232_ceb2135));
                dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                dailyTextView.setText(reason.second);
                dailyTextView.setTag(reason.first);
                dailyTextView.setGravity(Gravity.CENTER_VERTICAL);
                dataBinding.scrollLayout.addView(dailyTextView, index++, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(getContext(), 45)));

                dailyTextView.setOnClickListener(onClickListener);

                View lineView = new View(getContext());
                lineView.setBackgroundResource(R.color.default_line_cf0f0f0);
                dataBinding.scrollLayout.addView(lineView, index++, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(getContext(), 1)));
            }

            if (DailyTextUtils.isTextEmpty(key) == false && key.equalsIgnoreCase(reason.first) == true)
            {
                selectedDailyTextView = dailyTextView;
            }
        }

        if (selectedDailyTextView == null)
        {
            dataBinding.positiveTextView.setEnabled(false);
        } else
        {
            selectedDailyTextView.performClick();
        }

        dataBinding.negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
            }
        });

        dataBinding.positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                TextView selectedView = (TextView) dataBinding.scrollLayout.getTag();

                if (selectedView != null)
                {
                    EditText messageEditText = dataBinding.scrollLayout.findViewById(R.id.messageEditText);
                    String message = null;

                    if (messageEditText != null)
                    {
                        message = messageEditText.getText().toString().trim();
                    }

                    getEventListener().onCancelReasonClick((String) selectedView.getTag(), selectedView.getText().toString(), message);
                }
            }
        });

        showSimpleDialog(dataBinding.getRoot(), null, null, true);

        dataBinding.scrollView.post(new Runnable()
        {
            @Override
            public void run()
            {
                dataBinding.scrollView.scrollTo(0, 0);
            }
        });
    }

    @Override
    public void setCancelReasonText(String reason)
    {
        if (mRefund03DataBinding == null)
        {
            return;
        }

        mRefund03DataBinding.selectReasonCancelView.setText(reason);
    }

    private void initToolbar(ActivityStayOutboundRefundDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });
    }

    private void setBookingInformation(Context context, LayoutStayOutboundRefund01DataBinding dataBinding, StayOutboundRefundDetail stayOutboundRefundDetail)
    {
        if (context == null || dataBinding == null || stayOutboundRefundDetail == null)
        {
            return;
        }

        dataBinding.hotelNameTextView.setText(stayOutboundRefundDetail.name);
        dataBinding.roomTypeTextView.setText(stayOutboundRefundDetail.roomName);
        dataBinding.addressTextView.setText(stayOutboundRefundDetail.address);
    }

    private void setPaymentInformation(Context context, LayoutStayOutboundRefund02DataBinding dataBinding, StayOutboundRefundDetail stayOutboundRefundDetail)
    {
        if (context == null || dataBinding == null || stayOutboundRefundDetail == null)
        {
            return;
        }

        try
        {
            dataBinding.paymentDateTextView.setText(DailyCalendar.convertDateFormatString(stayOutboundRefundDetail.paymentDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        dataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(context, stayOutboundRefundDetail.totalPrice, false));


        if (stayOutboundRefundDetail.bonus > 0)
        {
            dataBinding.bonusLayout.setVisibility(View.VISIBLE);
            dataBinding.bonusTextView.setText("- " + DailyTextUtils.getPriceFormat(context, stayOutboundRefundDetail.bonus, false));
        } else
        {
            dataBinding.bonusLayout.setVisibility(View.GONE);
        }

        if (stayOutboundRefundDetail.couponAmount > 0)
        {
            dataBinding.couponLayout.setVisibility(View.VISIBLE);
            dataBinding.couponTextView.setText("- " + DailyTextUtils.getPriceFormat(context, stayOutboundRefundDetail.couponAmount, false));
        } else
        {
            dataBinding.couponLayout.setVisibility(View.GONE);
        }

        dataBinding.totalPriceTextView.setText(DailyTextUtils.getPriceFormat(context, stayOutboundRefundDetail.paymentPrice, false));
    }
}

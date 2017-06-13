package com.daily.dailyhotel.screen.booking.detail.stay.outbound.refund;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

        void onCancelReasonClick(int position, String message);
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
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
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
    }

    public void showCancelDialog(List<Pair<String, String>> cancelList, String key, String message)
    {
        if(cancelList == null || cancelList.size() == 0)
        {
            return;
        }

        DialogStayOutboundRefundCancelDialogDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_stay_outbound_refund_cancel_dialog_data, null, false);

        int index = 0;

        for(Pair<String, String> reason : cancelList)
        {
            DailyTextView dailyTextView = new DailyTextView(getContext());
            dailyTextView.setTextColor(getColorStateList(R.drawable.selector_text_color_c323232_cb70038));
            dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            dailyTextView.setText(reason.second);
            dailyTextView.setTag(reason.first);
            dataBinding.scrollLayout.addView(dailyTextView, index++, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(getContext(), 45)));

            View lineView = new View(getContext());
            lineView.setBackgroundResource(R.color.default_line_cf0f0f0);
            dataBinding.scrollLayout.addView(lineView, index++, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(getContext(), 1)));
        }


        dataBinding.cancelRefundView01.setTag(1);
        dataBinding.cancelRefundView02.setTag(2);
        dataBinding.cancelRefundView03.setTag(3);
        dataBinding.cancelRefundView04.setTag(4);
        dataBinding.cancelRefundView05.setTag(5);
        dataBinding.cancelRefundView06.setTag(6);
        dataBinding.cancelRefundView07.setTag(7);

        dataBinding.messageEditText.addTextChangedListener(new TextWatcher()
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
                dataBinding.messageCountTextView.setText(String.format(Locale.KOREA, "(%d/300자)", s.length()));
            }
        });

        dataBinding.messageEditText.setText(message);
        dataBinding.messageEditText.setSelection(dataBinding.messageEditText.length());

        dataBinding.messageEditText.setOnTouchListener(new View.OnTouchListener()
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

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.cancelRefundView01:
                    case R.id.cancelRefundView02:
                    case R.id.cancelRefundView03:
                    case R.id.cancelRefundView04:
                    case R.id.cancelRefundView05:
                    case R.id.cancelRefundView06:
                    {
                        dataBinding.messageEditText.setText(null);
                        dataBinding.messageClickView.setVisibility(View.VISIBLE);
                        dataBinding.messageClickView.setOnClickListener(this);

                        dataBinding.messageEditText.setCursorVisible(false);

                        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(dataBinding.messageEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                        setSelected(v);
                        break;
                    }

                    case R.id.cancelRefundView07:
                    {
                        dataBinding.messageClickView.setVisibility(View.GONE);
                        dataBinding.messageClickView.setOnClickListener(null);

                        dataBinding.messageEditText.setCursorVisible(true);

                        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(dataBinding.messageEditText, InputMethodManager.SHOW_IMPLICIT);

                        setSelected(v);

                        dataBinding.scrollView.fullScroll(View.FOCUS_DOWN);
                        break;
                    }

                    case R.id.messageClickView:
                        dataBinding.cancelRefundView07.performClick();
                        break;
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

        dataBinding.cancelRefundView01.setOnClickListener(onClickListener);
        dataBinding.cancelRefundView02.setOnClickListener(onClickListener);
        dataBinding.cancelRefundView03.setOnClickListener(onClickListener);
        dataBinding.cancelRefundView04.setOnClickListener(onClickListener);
        dataBinding.cancelRefundView05.setOnClickListener(onClickListener);
        dataBinding.cancelRefundView06.setOnClickListener(onClickListener);
        dataBinding.cancelRefundView07.setOnClickListener(onClickListener);

        switch (position)
        {
            case 1:
                dataBinding.cancelRefundView01.performClick();
                break;
            case 2:
                dataBinding.cancelRefundView02.performClick();
                break;
            case 3:
                dataBinding.cancelRefundView03.performClick();
                break;
            case 4:
                dataBinding.cancelRefundView04.performClick();
                break;
            case 5:
                dataBinding.cancelRefundView05.performClick();
                break;
            case 6:
                dataBinding.cancelRefundView06.performClick();
                break;
            case 7:
                dataBinding.cancelRefundView07.performClick();
                break;

            default:
                dataBinding.messageClickView.setVisibility(View.VISIBLE);
                dataBinding.messageClickView.setOnClickListener(onClickListener);

                dataBinding.messageEditText.setCursorVisible(false);
                dataBinding.positiveTextView.setEnabled(false);
                break;
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

                TextView selectedView = (TextView) ((View) dataBinding.cancelRefundView01.getParent()).getTag();

                if (selectedView != null)
                {
                    String cancelReason = selectedView.getText().toString();
                    String message = dataBinding.messageEditText.getText().toString().trim();

//                    getEventListener().onCancelReasonClick((Integer) selectedView.getTag(), cancelReason, message);
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

        dataBinding.totalPriceTextView.setText(DailyTextUtils.getPriceFormat(context, stayOutboundRefundDetail.paymentPrice, false));
    }
}

package com.daily.dailyhotel.screen.common.dialog.refund;

import android.content.Context;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.base.BaseMultiWindowView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DialogTypeRefundLayoutDataBinding;

import java.util.Locale;

/**
 * Created by android_sam on 2018. 1. 10..
 */

public class AutoRefundDialogView extends BaseMultiWindowView<AutoRefundDialogView.OnEventListener, DialogTypeRefundLayoutDataBinding> implements AutoRefundDialogInterface, View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onNativeButtonClick();

        void onPositiveButtonClick(int position, String cancelReason, String message);

        void checkConfigChange();
    }

    public AutoRefundDialogView(BaseActivity baseActivity, AutoRefundDialogView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final DialogTypeRefundLayoutDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.cancelRefundView01.setTag(1);
        viewDataBinding.cancelRefundView02.setTag(2);
        viewDataBinding.cancelRefundView03.setTag(3);
        viewDataBinding.cancelRefundView04.setTag(4);
        viewDataBinding.cancelRefundView05.setTag(5);
        viewDataBinding.cancelRefundView06.setTag(6);
        viewDataBinding.cancelRefundView07.setTag(7);

        viewDataBinding.cancelRefundView01.setOnClickListener(this);
        viewDataBinding.cancelRefundView02.setOnClickListener(this);
        viewDataBinding.cancelRefundView03.setOnClickListener(this);
        viewDataBinding.cancelRefundView04.setOnClickListener(this);
        viewDataBinding.cancelRefundView05.setOnClickListener(this);
        viewDataBinding.cancelRefundView06.setOnClickListener(this);
        viewDataBinding.cancelRefundView07.setOnClickListener(this);

        viewDataBinding.messageEditText.addTextChangedListener(new TextWatcher()
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
                viewDataBinding.messageCountTextView.setText(String.format(Locale.KOREA, "(%d/300자)", s.length()));
            }
        });

        viewDataBinding.messageEditText.setOnTouchListener(new View.OnTouchListener()
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

        viewDataBinding.negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onNativeButtonClick();
            }
        });

        viewDataBinding.positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (getViewDataBinding() == null)
                {
                    return;
                }

                TextView selectedView = (TextView) ((View) getViewDataBinding().cancelRefundView01.getParent()).getTag();
                if (selectedView != null)
                {
                    String cancelReason = selectedView.getText().toString();
                    String message = getViewDataBinding().messageEditText.getText().toString().trim();

                    getEventListener().onPositiveButtonClick((Integer) selectedView.getTag(), cancelReason, message);
                }
            }
        });

        getEventListener().checkConfigChange();
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    private void setSelected(View view)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        Object tag = ((View) view.getParent()).getTag();

        if (tag != null && tag instanceof DailyTextView == true)
        {
            ((DailyTextView) tag).setSelected(false);
            ((DailyTextView) tag).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        ((View) view.getParent()).setTag(view);
        view.setSelected(true);
        ((DailyTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.card_btn_v_select, 0);

        getViewDataBinding().positiveTextView.setEnabled(true);
    }

    @Override
    public void onClick(View view)
    {
        if (getViewDataBinding() == null || getContext() == null)
        {
            return;
        }

        switch (view.getId())
        {
            case R.id.cancelRefundView01:
            case R.id.cancelRefundView02:
            case R.id.cancelRefundView03:
            case R.id.cancelRefundView04:
            case R.id.cancelRefundView05:
            case R.id.cancelRefundView06:
            {
                getViewDataBinding().messageClickView.setVisibility(View.VISIBLE);
                getViewDataBinding().messageClickView.setOnClickListener(this);

                getViewDataBinding().messageEditText.setCursorVisible(false);
                getViewDataBinding().messageEditText.setTextColor(getColor(R.color.default_text_c929292));
//                getViewDataBinding().scrollView.post(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        int toY = (int) view.getY();
//                        getViewDataBinding().scrollView.scrollTo(0, toY);
//                    }
//                });

                setSelected(view);

                hideInputKeyboard();
                break;
            }

            case R.id.cancelRefundView07:
            {
                getViewDataBinding().messageClickView.setVisibility(View.GONE);
                getViewDataBinding().messageClickView.setOnClickListener(null);

                getViewDataBinding().messageEditText.setCursorVisible(true);
                getViewDataBinding().messageEditText.setTextColor(getColor(R.color.default_text_c323232));

                setSelected(view);

                //                getViewDataBinding().scrollView.fullScroll(View.FOCUS_DOWN);

                getViewDataBinding().scrollView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getViewDataBinding().scrollView.smoothScrollTo(0, 10000);
                    }
                });

                showInputKeyboard();
                break;
            }

            case R.id.messageClickView:
                getViewDataBinding().cancelRefundView07.performClick();
                break;
        }
    }

    @Override
    public void showInputKeyboard()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().messageEditText.post(new Runnable()
        {
            @Override
            public void run()
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(getViewDataBinding().messageEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    @Override
    public void hideInputKeyboard()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().messageEditText.post(new Runnable()
        {
            @Override
            public void run()
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getViewDataBinding().messageEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });
    }

    @Override
    public void onConfigurationChange(int orientation, boolean isInMultiWindowMode)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        //        ExLog.d("orientation : " + orientation);

        if (isInMultiWindowMode == true || Configuration.ORIENTATION_LANDSCAPE == orientation)
        {
            //            getViewDataBinding().topWeightView.setVisibility(View.GONE);
            //            getViewDataBinding().bottomWeightView.setVisibility(View.GONE);
            getViewDataBinding().scrollView.setVerticalScrollBarEnabled(true);
        } else
        {
            //            getViewDataBinding().topWeightView.setVisibility(View.VISIBLE);
            //            getViewDataBinding().bottomWeightView.setVisibility(View.VISIBLE);
            getViewDataBinding().scrollView.setVerticalScrollBarEnabled(false);
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (isTabletDevice() == true)
        {
            layoutParams.width = ScreenUtils.getScreenWidth(getContext()) * 10 / 15;
        } else
        {
            layoutParams.width = ScreenUtils.getScreenWidth(getContext()) * 13 / 15;
        }

        if (isInMultiWindowMode == true)
        {
            layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
        } else
        {
            layoutParams.height = ScreenUtils.getScreenHeight(getContext()) * 56 / 100;
        }

        getViewDataBinding().getRoot().setLayoutParams(layoutParams);
    }

    @Override
    public void setCancelType(int cancelType)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        switch (cancelType)
        {
            case 1:
                getViewDataBinding().cancelRefundView01.performClick();
                break;
            case 2:
                getViewDataBinding().cancelRefundView02.performClick();
                break;
            case 3:
                getViewDataBinding().cancelRefundView03.performClick();
                break;
            case 4:
                getViewDataBinding().cancelRefundView04.performClick();
                break;
            case 5:
                getViewDataBinding().cancelRefundView05.performClick();
                break;
            case 6:
                getViewDataBinding().cancelRefundView06.performClick();
                break;
            case 7:
                getViewDataBinding().cancelRefundView07.performClick();
                break;

            default:
                getViewDataBinding().messageClickView.setVisibility(View.VISIBLE);
                getViewDataBinding().messageClickView.setOnClickListener(this);

                getViewDataBinding().messageEditText.setCursorVisible(false);
                getViewDataBinding().positiveTextView.setEnabled(false);

                getViewDataBinding().scrollView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getViewDataBinding().scrollView.scrollTo(0, 0);
                    }
                });
                break;
        }
    }

    @Override
    public void setEtcMessage(String message)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().messageEditText.setText(message);
        getViewDataBinding().messageEditText.setSelection(getViewDataBinding().messageEditText.length());
    }
}

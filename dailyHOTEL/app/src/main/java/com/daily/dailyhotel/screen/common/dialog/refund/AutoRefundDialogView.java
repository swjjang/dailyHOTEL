package com.daily.dailyhotel.screen.common.dialog.refund;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
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
                    String message;

                    if (selectedView.getId() == R.id.cancelRefundView07)
                    {
                        message = getViewDataBinding().messageEditText.getText().toString().trim();
                    } else
                    {
                        message = null;
                    }

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

                setSelected(view);

                scrollViewChangedLayoutDisabledByKeyboard();

                hideInputKeyboard();
                break;
            }

            case R.id.cancelRefundView07:
            {
                getViewDataBinding().messageClickView.setVisibility(View.GONE);
                getViewDataBinding().messageClickView.setOnClickListener(null);

                getViewDataBinding().messageEditText.setCursorVisible(true);
                getViewDataBinding().messageEditText.setTextColor(getColor(R.color.default_text_c4d4d4d));

                setSelected(view);

                getViewDataBinding().scrollView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getViewDataBinding().scrollView.smoothScrollTo(0, 10000);
                    }
                });

                showInputKeyboard();

                getViewDataBinding().scrollView.setChangeLayoutEnabled(false);
                break;
            }

            case R.id.messageClickView:
                getViewDataBinding().cancelRefundView07.performClick();
                break;
        }
    }

    private void scrollViewChangedLayoutDisabledByKeyboard()
    {
        getViewDataBinding().scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            Rect rect = new Rect();
            int screenHeight = ScreenUtils.getScreenHeight(getContext());

            @Override
            public void onGlobalLayout()
            {
                getViewDataBinding().getRoot().getRootView().getWindowVisibleDisplayFrame(rect);

                int keypadHeight = screenHeight - rect.bottom;

                if (keypadHeight > screenHeight * 0.15)
                {
                    getViewDataBinding().scrollView.setChangeLayoutEnabled(false);
                } else
                {
                    if (VersionUtils.isOverAPI16() == true)
                    {
                        getViewDataBinding().scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else
                    {
                        getViewDataBinding().scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                    getViewDataBinding().scrollView.setChangeLayoutEnabled(true);
                }
            }
        });
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

        View view = getCurrentFocus();

        if (view != null)
        {
            view.post(new Runnable()
            {
                @Override
                public void run()
                {
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });
        } else
        {
            getViewDataBinding().messageEditText.post(new Runnable()
            {
                @Override
                public void run()
                {
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getViewDataBinding().messageEditText.getWindowToken(), 0);
                }
            });
        }
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

        View selectedView = null;

        switch (cancelType)
        {
            case 1:
                selectedView = getViewDataBinding().cancelRefundView01;
                break;
            case 2:
                selectedView = getViewDataBinding().cancelRefundView02;
                break;
            case 3:
                selectedView = getViewDataBinding().cancelRefundView03;
                break;
            case 4:
                selectedView = getViewDataBinding().cancelRefundView04;
                break;
            case 5:
                selectedView = getViewDataBinding().cancelRefundView05;
                break;
            case 6:
                selectedView = getViewDataBinding().cancelRefundView06;
                break;
            case 7:
                selectedView = getViewDataBinding().cancelRefundView07;
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

        if (selectedView != null)
        {
            scrollSelectView(selectedView);
        }
    }

    private void scrollSelectView(final View view)
    {
        view.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                view.performClick();

                final int scrollY = view.getTop();

                getViewDataBinding().scrollView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getViewDataBinding().scrollView.scrollTo(0, scrollY);
                    }
                });
            }
        }, 200);
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

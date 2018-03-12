package com.daily.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.daily.base.databinding.DialogLayoutDataBinding;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyToast;

public abstract class BaseDialogView<T1 extends OnBaseEventListener, T2 extends ViewDataBinding> extends BaseView<T1, T2> implements BaseDialogViewInterface
{
    private Dialog mDialog;

    public BaseDialogView(BaseActivity activity, T1 listener)
    {
        super(activity, listener);
    }

    @Override
    public boolean isTabletDevice()
    {
        return ScreenUtils.isTabletDevice(getActivity());
    }

    @Override
    public void hideSimpleDialog()
    {
        if (mDialog != null)
        {
            if (mDialog.isShowing() == true)
            {
                mDialog.dismiss();
            }

            mDialog = null;
        }
    }

    @Override
    public void showSimpleDialog(String title, String msg, String positive, View.OnClickListener positiveListener)
    {
        showSimpleDialog(title, msg, positive, null, positiveListener, null);
    }

    @Override
    public void showSimpleDialog(String title, String msg, String positive, View.OnClickListener positiveListener//
        , DialogInterface.OnCancelListener cancelListener)
    {
        showSimpleDialog(title, msg, positive, null, positiveListener, null, cancelListener, null, true);
    }

    @Override
    public void showSimpleDialog(String title, String msg, String positive, View.OnClickListener positiveListener//
        , DialogInterface.OnDismissListener dismissListener)
    {
        showSimpleDialog(title, msg, positive, null, positiveListener, null, null, dismissListener, true);
    }

    @Override
    public void showSimpleDialog(String title, String msg, String positive, View.OnClickListener positiveListener//
        , DialogInterface.OnDismissListener dismissListener, boolean cancelable)
    {
        showSimpleDialog(title, msg, positive, null, positiveListener, null, null, dismissListener, cancelable);
    }

    @Override
    public void showSimpleDialog(String title, String msg, String positive, String negative//
        , View.OnClickListener positiveListener, View.OnClickListener negativeListener)
    {
        showSimpleDialog(title, msg, positive, negative, positiveListener, negativeListener, null, null, true);
    }

    @Override
    public void showSimpleDialog(String title, String msg, String positive, String negative//
        , View.OnClickListener positiveListener, View.OnClickListener negativeListener, boolean cancelable)
    {
        showSimpleDialog(title, msg, positive, negative, positiveListener, negativeListener, null, null, cancelable);
    }

    @Override
    public void showSimpleDialog(String titleText, String msg, String positive, String negative//
        , final View.OnClickListener positiveListener, final View.OnClickListener negativeListener, DialogInterface.OnCancelListener cancelListener, //
                                 DialogInterface.OnDismissListener dismissListener, boolean cancelable)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        hideSimpleDialog();

        DialogLayoutDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_layout_data, null, false);

        mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCanceledOnTouchOutside(false);

        // 상단
        dataBinding.titleTextView.setVisibility(View.VISIBLE);

        if (DailyTextUtils.isTextEmpty(titleText) == true)
        {
            dataBinding.titleTextView.setText(getString(R.string.label_notice));
        } else
        {
            dataBinding.titleTextView.setText(titleText);
        }

        // 메시지
        dataBinding.messageTextView.setText(msg);

        // 버튼
        if (DailyTextUtils.isTextEmpty(positive, negative) == false)
        {
            dataBinding.twoButtonLayout.setVisibility(View.VISIBLE);
            dataBinding.oneButtonLayout.setVisibility(View.GONE);

            dataBinding.negativeTextView.setText(negative);
            dataBinding.negativeTextView.setOnClickListener(v ->
            {
                if (mDialog != null && mDialog.isShowing() == true)
                {
                    mDialog.dismiss();
                }

                if (negativeListener != null)
                {
                    negativeListener.onClick(v);
                }
            });


            dataBinding.positiveTextView.setText(positive);
            dataBinding.positiveTextView.setOnClickListener(v ->
            {
                if (mDialog != null && mDialog.isShowing())
                {
                    mDialog.dismiss();
                }

                if (positiveListener != null)
                {
                    positiveListener.onClick(v);
                }
            });
        } else
        {
            dataBinding.twoButtonLayout.setVisibility(View.GONE);
            dataBinding.oneButtonLayout.setVisibility(View.VISIBLE);

            dataBinding.confirmTextView.setText(positive);
            dataBinding.oneButtonLayout.setOnClickListener(v ->
            {
                if (mDialog != null && mDialog.isShowing())
                {
                    mDialog.dismiss();
                }

                if (positiveListener != null)
                {
                    positiveListener.onClick(v);
                }
            });
        }

        showSimpleDialog(dataBinding.getRoot(), cancelListener, dismissListener, cancelable);
    }

    @Override
    public void showSimpleDialog(View view, DialogInterface.OnCancelListener cancelListener//
        , DialogInterface.OnDismissListener dismissListener, boolean cancelable)
    {
        if (getActivity().isFinishing() == true | view == null)
        {
            return;
        }

        hideSimpleDialog();

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCanceledOnTouchOutside(cancelable);

        if (cancelListener != null)
        {
            mDialog.setOnCancelListener(cancelListener);
        }

        if (dismissListener != null)
        {
            mDialog.setOnDismissListener(dismissListener);
        }

        try
        {
            mDialog.setContentView(view);

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(getActivity(), mDialog);

            mDialog.show();

            mDialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void showSimpleDialog(String titleText, String msg, String positive, float positiveWeight, String negative, float negativeWeight//
        , final View.OnClickListener positiveListener, final View.OnClickListener negativeListener, DialogInterface.OnCancelListener cancelListener, //
                                 DialogInterface.OnDismissListener dismissListener, boolean cancelable)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        hideSimpleDialog();

        DialogLayoutDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_layout_data, null, false);

        mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCanceledOnTouchOutside(false);

        // 상단
        dataBinding.titleTextView.setVisibility(View.VISIBLE);

        if (DailyTextUtils.isTextEmpty(titleText) == true)
        {
            dataBinding.titleTextView.setText(getString(R.string.label_notice));
        } else
        {
            dataBinding.titleTextView.setText(titleText);
        }

        // 메시지
        dataBinding.messageTextView.setText(msg);

        // 버튼
        if (positiveWeight > 0 && negativeWeight > 0)
        {
            dataBinding.twoButtonLayout.setWeightSum(positiveWeight + negativeWeight);

            ((LinearLayout.LayoutParams) dataBinding.positiveTextView.getLayoutParams()).weight = positiveWeight;
            ((LinearLayout.LayoutParams) dataBinding.negativeTextView.getLayoutParams()).weight = negativeWeight;

            dataBinding.positiveTextView.requestLayout();
            dataBinding.negativeTextView.requestLayout();
        }


        if (DailyTextUtils.isTextEmpty(positive, negative) == false)
        {
            dataBinding.twoButtonLayout.setVisibility(View.VISIBLE);
            dataBinding.oneButtonLayout.setVisibility(View.GONE);

            dataBinding.negativeTextView.setText(negative);
            dataBinding.negativeTextView.setOnClickListener(v ->
            {
                if (mDialog != null && mDialog.isShowing() == true)
                {
                    mDialog.dismiss();
                }

                if (negativeListener != null)
                {
                    negativeListener.onClick(v);
                }
            });


            dataBinding.positiveTextView.setText(positive);
            dataBinding.positiveTextView.setOnClickListener(v ->
            {
                if (mDialog != null && mDialog.isShowing())
                {
                    mDialog.dismiss();
                }

                if (positiveListener != null)
                {
                    positiveListener.onClick(v);
                }
            });
        } else
        {
            dataBinding.twoButtonLayout.setVisibility(View.GONE);
            dataBinding.oneButtonLayout.setVisibility(View.VISIBLE);

            dataBinding.confirmTextView.setText(positive);
            dataBinding.oneButtonLayout.setOnClickListener(v ->
            {
                if (mDialog != null && mDialog.isShowing())
                {
                    mDialog.dismiss();
                }

                if (positiveListener != null)
                {
                    positiveListener.onClick(v);
                }
            });
        }

        showSimpleDialog(dataBinding.getRoot(), cancelListener, dismissListener, cancelable);
    }

    @Override
    public void showToast(String message, int duration)
    {
        DailyToast.showToast(getContext(), message, duration);
    }

    @Override
    public void showToast(int messageResId, int duration)
    {
        DailyToast.showToast(getContext(), messageResId, duration);
    }

    protected void showSimpleDialog(String titleText, String msg, String checkBoxText, String positive, String negative//
        , CheckBox.OnCheckedChangeListener checkedChangeListener, View.OnClickListener positiveListener//
        , View.OnClickListener negativeListener, DialogInterface.OnCancelListener cancelListener //
        , DialogInterface.OnDismissListener dismissListener, boolean cancelable)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        hideSimpleDialog();

        DialogLayoutDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_layout_data, null, false);

        mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCanceledOnTouchOutside(false);

        // 상단
        dataBinding.titleTextView.setVisibility(View.VISIBLE);

        if (DailyTextUtils.isTextEmpty(titleText) == true)
        {
            dataBinding.titleTextView.setText(getString(R.string.label_notice));
        } else
        {
            dataBinding.titleTextView.setText(titleText);
        }

        // 메시지
        dataBinding.messageTextView.setText(msg);

        // 체크 박스
        dataBinding.checkBoxView.setVisibility(View.VISIBLE);
        dataBinding.checkBoxView.setText(checkBoxText);
        dataBinding.checkBoxView.setOnCheckedChangeListener(checkedChangeListener);

        // 버튼
        if (DailyTextUtils.isTextEmpty(positive, negative) == false)
        {
            dataBinding.twoButtonLayout.setVisibility(View.VISIBLE);
            dataBinding.oneButtonLayout.setVisibility(View.GONE);

            dataBinding.negativeTextView.setText(negative);
            dataBinding.negativeTextView.setOnClickListener(v ->
            {
                if (mDialog != null && mDialog.isShowing() == true)
                {
                    mDialog.dismiss();
                }

                if (negativeListener != null)
                {
                    negativeListener.onClick(v);
                }
            });


            dataBinding.positiveTextView.setText(positive);
            dataBinding.positiveTextView.setOnClickListener(v ->
            {
                if (mDialog != null && mDialog.isShowing())
                {
                    mDialog.dismiss();
                }

                if (positiveListener != null)
                {
                    positiveListener.onClick(v);
                }
            });
        } else
        {
            dataBinding.twoButtonLayout.setVisibility(View.GONE);
            dataBinding.oneButtonLayout.setVisibility(View.VISIBLE);

            dataBinding.confirmTextView.setText(positive);
            dataBinding.oneButtonLayout.setOnClickListener(v ->
            {
                if (mDialog != null && mDialog.isShowing())
                {
                    mDialog.dismiss();
                }

                if (positiveListener != null)
                {
                    positiveListener.onClick(v);
                }
            });
        }

        showSimpleDialog(dataBinding.getRoot(), cancelListener, dismissListener, cancelable);
    }
}

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

import com.daily.base.databinding.DialogLayoutDataBinding;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;

public abstract class BaseDialogView<T1 extends OnBaseEventListener, T2 extends ViewDataBinding> extends BaseView<T1, T2> implements BaseDialogViewInterface
{
    private Dialog mDialog;

    public BaseDialogView(BaseActivity activity, T1 listener)
    {
        super(activity, listener);
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
    public void showSimpleDialog(String title, String msg, String positive, String negative//
        , View.OnClickListener positiveListener, View.OnClickListener negativeListener)
    {
        showSimpleDialog(title, msg, positive, negative, positiveListener, negativeListener, null, null, true);
    }

    @Override
    public void showSimpleDialog(String title, String msg, String positive, String negative//
        , View.OnClickListener positiveListener, View.OnClickListener negativeListener, boolean isCancelable)
    {
        showSimpleDialog(title, msg, positive, negative, positiveListener, negativeListener, null, null, isCancelable);
    }

    @Override
    public void showSimpleDialog(String titleText, String msg, String positive, String negative//
        , final View.OnClickListener positiveListener, final View.OnClickListener negativeListener, DialogInterface.OnCancelListener cancelListener, //
                                 DialogInterface.OnDismissListener dismissListener, boolean isCancelable)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        hideSimpleDialog();

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DialogLayoutDataBinding dataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_layout_data, null, false);

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

        if (cancelListener != null)
        {
            mDialog.setOnCancelListener(cancelListener);
        }

        if (dismissListener != null)
        {
            mDialog.setOnDismissListener(dismissListener);
        }

        mDialog.setCancelable(isCancelable);

        try
        {
            mDialog.setContentView(dataBinding.getRoot());

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(getActivity(), mDialog);

            mDialog.show();

            mDialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    protected void showSimpleDialog(View view, DialogInterface.OnCancelListener cancelListener//
        , DialogInterface.OnDismissListener dismissListener, boolean isCancelable)
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
        mDialog.setCanceledOnTouchOutside(isCancelable);

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
}

package com.daily.base;

import android.content.DialogInterface;
import android.databinding.ViewDataBinding;
import android.view.View;

import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyToast;

public abstract class BaseFragmentDialogView<T1 extends OnBaseEventListener, T2 extends ViewDataBinding> extends BaseFragmentView<T1, T2> implements BaseFragmentDialogViewInterface
{
    public BaseFragmentDialogView(T1 listener)
    {
        super(listener);
    }

    @Override
    public boolean isTabletDevice()
    {
        return ScreenUtils.isTabletDevice(getActivity());
    }

    @Override
    public void hideSimpleDialog()
    {
        getActivity().getPresenter().getViewInterface().hideSimpleDialog();
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
        getActivity().getPresenter().getViewInterface().showSimpleDialog(titleText, msg, positive, negative, positiveListener, negativeListener, cancelListener, dismissListener, cancelable);
    }

    @Override
    public void showSimpleDialog(View view, DialogInterface.OnCancelListener cancelListener//
        , DialogInterface.OnDismissListener dismissListener, boolean cancelable)
    {
        getActivity().getPresenter().getViewInterface().showSimpleDialog(view, cancelListener, dismissListener, cancelable);
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
}

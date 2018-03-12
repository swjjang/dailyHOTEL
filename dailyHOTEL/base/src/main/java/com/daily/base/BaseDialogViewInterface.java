package com.daily.base;

import android.content.DialogInterface;
import android.view.View;

public interface BaseDialogViewInterface extends BaseViewInterface
{
    boolean isTabletDevice();

    void hideSimpleDialog();

    void showSimpleDialog(String title, String msg, String positive//
        , View.OnClickListener positiveListener);

    void showSimpleDialog(String title, String msg, String positive//
        , View.OnClickListener positiveListener//
        , DialogInterface.OnCancelListener cancelListener);

    void showSimpleDialog(String title, String msg, String positive//
        , View.OnClickListener positiveListener//
        , DialogInterface.OnDismissListener dismissListener);

    void showSimpleDialog(String title, String msg, String positive//
        , View.OnClickListener positiveListener//
        , DialogInterface.OnDismissListener dismissListener, boolean isCancelable);

    void showSimpleDialog(String title, String msg, String positive, String negative//
        , View.OnClickListener positiveListener//
        , View.OnClickListener negativeListener);

    void showSimpleDialog(String title, String msg, String positive, String negative//
        , View.OnClickListener positiveListener//
        , View.OnClickListener negativeListener//
        , boolean isCancelable);

    void showSimpleDialog(String titleText, String msg, String positive, String negative//
        , final View.OnClickListener positiveListener//
        , final View.OnClickListener negativeListener//
        , DialogInterface.OnCancelListener cancelListener//
        , DialogInterface.OnDismissListener dismissListener//
        , boolean isCancelable);

    void showSimpleDialog(View view, DialogInterface.OnCancelListener cancelListener//
        , DialogInterface.OnDismissListener dismissListener, boolean cancelable);

    void showSimpleDialog(String titleText, String msg, String positive, float positiveWeight, String negative, float negativeWeight//
        , final View.OnClickListener positiveListener, final View.OnClickListener negativeListener, DialogInterface.OnCancelListener cancelListener, //
                     DialogInterface.OnDismissListener dismissListener, boolean cancelable);

    void showToast(String message, int duration);

    void showToast(int messageResId, int duration);
}

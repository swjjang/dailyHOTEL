package com.twoheart.dailyhotel.place.base;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.activity.PlaceDetailActivity;
import com.twoheart.dailyhotel.screen.common.LoadingDialog;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity implements Constants
{
    private static final int MESSAGE_SHOW_LOADING_PROGRESS = 1;

    protected interface OnCallDialogListener
    {
        void onShowDialog();

        void onPositiveButtonClick(View v);

        void onNativeButtonClick(View v);
    }

    private Dialog mDialog;
    private LoadingDialog mLockUI;
    protected String mNetworkTag;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MESSAGE_SHOW_LOADING_PROGRESS:
                    showLockUIProgress();
                    break;
            }
        }
    };

    /**
     * UI Component의 잠금 상태인지 확인하는 변수..
     */
    private boolean mIsLockUiComponent = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mLockUI = new LoadingDialog(this);
        mNetworkTag = getClass().getName();

        if (Util.isOverAPI21() == true && Util.isOverAPI23() == false)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.statusbar_background));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        try
        {
            super.onSaveInstanceState(outState);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        try
        {
            super.onBackPressed();
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            finish();
        } finally
        {
            DailyMobileAPI.getInstance(this).cancelAll(this);
        }
    }

    public void onProgressBackPressed()
    {
        onBackPressed();

        if (this instanceof PlaceDetailActivity == true)
        {

        } else if (this instanceof MainActivity == false && isFinishing() == false)
        {
            finish();
        }
    }

    public String getNetworkTag()
    {
        return mNetworkTag;
    }

    public void restartExpiredSession()
    {
        if (isFinishing() == true)
        {
            return;
        }

        //        // 세션이 만료되어 재시작 요청.
        //        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_session_expired), getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
        //        {
        //            @Override
        //            public void onClick(View v)
        //            {
        //                Util.restartApp(BaseActivity.this);
        //            }
        //        }, null, false);

        // 토스트로 수정
        DailyToast.showToast(this, R.string.dialog_msg_session_expired, Toast.LENGTH_SHORT);

        Util.restartApp(this);
    }

    // 메뉴 버튼을 막아버림.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (Util.isMemoryClear() == true)
        {
            if (Constants.DEBUG == true)
            {
                ExLog.d("isMemoryClear is Restart");
            }

            Util.restartApp(this);
            return;
        }
    }

    @Override
    protected void onResume()
    {
        if (mLockUI == null)
        {
            mLockUI = new LoadingDialog(this);
        }

        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setLockUICancelable(boolean flag)
    {
        mLockUI.setCancelable(flag);
    }

    public void lockUI()
    {
        lockUI(true);
    }

    public void lockUI(boolean isShowProgress)
    {
        lockUiComponent();

        if (isFinishing() == false)
        {
            if (mLockUI == null)
            {
                mLockUI = new LoadingDialog(this);
            }

            mHandler.removeMessages(MESSAGE_SHOW_LOADING_PROGRESS);
            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_LOADING_PROGRESS, 1000);
        }
    }

    public void showLockUIProgress()
    {
        if (mLockUI != null && isFinishing() == false)
        {
            mLockUI.showProgress();
        }
    }


    /**
     * 로딩이 완료되어 LoadingDialog를 제거하고 전역 폰트를 설정한다.
     */
    public void unLockUI()
    {
        releaseUiComponent();

        mHandler.removeMessages(MESSAGE_SHOW_LOADING_PROGRESS);

        if (isFinishing() == false && mLockUI != null)
        {
            mLockUI.hide();
        }
    }

    /**
     * UI Component의 잠금 상태를 확인하는 변수..
     *
     * @return
     */
    public boolean isLockUiComponent()
    {
        synchronized (this)
        {
            return mIsLockUiComponent;
        }
    }

    public boolean lockUiComponentAndIsLockUiComponent()
    {
        synchronized (this)
        {
            if (mIsLockUiComponent == true)
            {
                return true;
            } else
            {
                mIsLockUiComponent = true;
                return false;
            }
        }
    }

    /**
     * UI Component를 잠금상태로 변경..
     */
    public void lockUiComponent()
    {
        synchronized (this)
        {
            mIsLockUiComponent = true;
        }
    }

    /**
     * UI Component를 잠금해제로 변경..
     */
    public void releaseUiComponent()
    {
        synchronized (this)
        {
            mIsLockUiComponent = false;
        }
    }

    @Override
    protected void onDestroy()
    {
        releaseUiComponent();

        if (mLockUI != null)
        {
            mLockUI.close();
        }

        // 현재 Activity에 등록된 Request를 취소한다.
        DailyMobileAPI.getInstance(this).cancelAll(this, mNetworkTag);

        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
            mDialog = null;
        }

        // MainActivity에서 종료시 이슈가 있어 MainActivity에서는 작동하지 않도록 수정
        // 이제는 사용하지 않아도 될까해서 삭제함
        //        if (this instanceof MainActivity == false)
        //        {
        //            try
        //            {
        //                recursiveRecycle(getWindow().getDecorView());
        //            } catch (Exception e)
        //            {
        //                ExLog.e(e.toString());
        //            }
        //        }

        super.onDestroy();
    }

    public void onErrorResponse(Call<JSONObject> call, Response<JSONObject> response)
    {
        unLockUI();

        if (response != null && response.code() == 401)
        {
            DailyPreference.getInstance(this).clear();

            try
            {
                LoginManager.getInstance().logOut();
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            try
            {
                UserManagement.requestLogout(null);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            restartExpiredSession();
            return;
        }

        onError();
    }

    public void onError(Throwable e)
    {
        unLockUI();

        if (DEBUG == false && e != null)
        {
            Crashlytics.logException(e);
        } else
        {
            if (e != null)
            {
                ExLog.e(e.toString());
            }
        }

        onError();

        finish();
    }

    protected void onError()
    {
        unLockUI();

        // 혹시나 스레드 상태에서 호출이 될경우를 대비해서
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                DailyToast.showToast(BaseActivity.this, getResources().getString(R.string.act_base_network_connect), Toast.LENGTH_LONG);
            }
        });
    }

    public void onErrorPopupMessage(int msgCode, String message)
    {
        onErrorPopupMessage(msgCode, message, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    public void onErrorPopupMessage(int msgCode, String message, View.OnClickListener listener)
    {
        unLockUI();

        //        showSimpleDialog(null, String.format("%s(%d)", message, msgCode), getString(R.string.dialog_btn_text_confirm), null, listener, null, false);
        showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null, listener, null, false);
    }

    public void onErrorToastMessage(final String message)
    {
        unLockUI();

        if (Util.isTextEmpty(message) == true)
        {
            onError();
        } else
        {
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    DailyToast.showToast(BaseActivity.this, message, Toast.LENGTH_LONG);
                }
            });
        }
    }

    private void recursiveRecycle(View root)
    {
        if (root == null)
        {
            return;
        }

        if (Util.isOverAPI16())
        {
            root.setBackground(null);
        } else
        {
            root.setBackgroundDrawable(null);
        }

        if (root instanceof ViewGroup)
        {
            ViewGroup group = (ViewGroup) root;
            int count = group.getChildCount();

            for (int i = 0; i < count; i++)
            {
                recursiveRecycle(group.getChildAt(i));
            }

            if (!(root instanceof AdapterView))
            {
                group.removeAllViews();
            }
        }

        if (root instanceof ImageView)
        {
            ((ImageView) root).setImageDrawable(null);
        }
    }

    public Dialog createSimpleDialog(String titleText, String message, String positive, String negative, final View.OnClickListener positiveListener, final View.OnClickListener negativeListener)
    {
        if (mDialog != null)
        {
            if (mDialog.isShowing())
            {
                mDialog.dismiss();
            }

            mDialog = null;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_layout, null, false);

        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCanceledOnTouchOutside(false);

        // 상단
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);

        if (Util.isTextEmpty(titleText) == true)
        {
            titleTextView.setText(getString(R.string.dialog_notice2));
        } else
        {
            titleTextView.setText(titleText);
        }

        // 메시지
        TextView messageTextView = (TextView) dialogView.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);
        View oneButtonLayout = buttonLayout.findViewById(R.id.oneButtonLayout);

        if (Util.isTextEmpty(positive, negative) == false)
        {
            twoButtonLayout.setVisibility(View.VISIBLE);
            oneButtonLayout.setVisibility(View.GONE);

            TextView negativeTextView = (TextView) twoButtonLayout.findViewById(R.id.negativeTextView);
            TextView positiveTextView = (TextView) twoButtonLayout.findViewById(R.id.positiveTextView);

            negativeTextView.setText(negative);
            negativeTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mDialog != null && mDialog.isShowing())
                    {
                        mDialog.dismiss();
                    }

                    if (negativeListener != null)
                    {
                        negativeListener.onClick(v);
                    }
                }
            });

            positiveTextView.setText(positive);
            positiveTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mDialog != null && mDialog.isShowing())
                    {
                        mDialog.dismiss();
                    }

                    if (positiveListener != null)
                    {
                        positiveListener.onClick(v);
                    }
                }
            });
        } else
        {
            twoButtonLayout.setVisibility(View.GONE);
            oneButtonLayout.setVisibility(View.VISIBLE);

            TextView confirmTextView = (TextView) oneButtonLayout.findViewById(R.id.confirmTextView);

            confirmTextView.setText(positive);
            oneButtonLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mDialog != null && mDialog.isShowing())
                    {
                        mDialog.dismiss();
                    }

                    if (positiveListener != null)
                    {
                        positiveListener.onClick(v);
                    }
                }
            });
        }

        mDialog.setContentView(dialogView);

        return mDialog;
    }

    public void showSimpleDialog(String title, String msg, String positive, View.OnClickListener positiveListener)
    {
        showSimpleDialog(title, msg, positive, null, positiveListener, null);
    }

    public void showSimpleDialog(String title, String msg, String positive, View.OnClickListener positiveListener, DialogInterface.OnCancelListener cancelListener)
    {
        showSimpleDialog(title, msg, positive, null, positiveListener, null, cancelListener, null, true);
    }

    public void showSimpleDialog(String title, String msg, String positive, View.OnClickListener positiveListener, DialogInterface.OnDismissListener dismissListener)
    {
        showSimpleDialog(title, msg, positive, null, positiveListener, null, null, dismissListener, true);
    }

    public void showSimpleDialog(String title, String msg, String positive, String negative, View.OnClickListener positiveListener, View.OnClickListener negativeListener)
    {
        showSimpleDialog(title, msg, positive, negative, positiveListener, negativeListener, null, null, true);
    }

    public void showSimpleDialog(String title, String msg, String positive, String negative, View.OnClickListener positiveListener, View.OnClickListener negativeListener, boolean isCancelable)
    {
        showSimpleDialog(title, msg, positive, negative, positiveListener, negativeListener, null, null, isCancelable);
    }

    public void showSimpleDialog(String titleText, String msg, String positive, String negative, final View.OnClickListener positiveListener, final View.OnClickListener negativeListener, DialogInterface.OnCancelListener cancelListener, //
                                 DialogInterface.OnDismissListener dismissListener, //
                                 boolean isCancelable)
    {
        if (isFinishing())
        {
            return;
        }

        if (mDialog != null)
        {
            if (mDialog.isShowing())
            {
                mDialog.dismiss();
            }

            mDialog = null;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_layout, null, false);

        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCanceledOnTouchOutside(false);

        // 상단
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);

        if (Util.isTextEmpty(titleText) == true)
        {
            titleTextView.setText(getString(R.string.dialog_notice2));
        } else
        {
            titleTextView.setText(titleText);
        }

        // 메시지
        TextView messageTextView = (TextView) dialogView.findViewById(R.id.messageTextView);
        messageTextView.setText(msg);

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);
        View oneButtonLayout = buttonLayout.findViewById(R.id.oneButtonLayout);

        if (Util.isTextEmpty(positive, negative) == false)
        {
            twoButtonLayout.setVisibility(View.VISIBLE);
            oneButtonLayout.setVisibility(View.GONE);

            TextView negativeTextView = (TextView) twoButtonLayout.findViewById(R.id.negativeTextView);
            TextView positiveTextView = (TextView) twoButtonLayout.findViewById(R.id.positiveTextView);

            negativeTextView.setText(negative);
            negativeTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mDialog != null && mDialog.isShowing())
                    {
                        mDialog.dismiss();
                    }

                    if (negativeListener != null)
                    {
                        negativeListener.onClick(v);
                    }
                }
            });

            positiveTextView.setText(positive);
            positiveTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mDialog != null && mDialog.isShowing())
                    {
                        mDialog.dismiss();
                    }

                    if (positiveListener != null)
                    {
                        positiveListener.onClick(v);
                    }
                }
            });
        } else
        {
            twoButtonLayout.setVisibility(View.GONE);
            oneButtonLayout.setVisibility(View.VISIBLE);

            TextView confirmTextView = (TextView) oneButtonLayout.findViewById(R.id.confirmTextView);

            confirmTextView.setText(positive);
            oneButtonLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mDialog != null && mDialog.isShowing())
                    {
                        mDialog.dismiss();
                    }

                    if (positiveListener != null)
                    {
                        positiveListener.onClick(v);
                    }
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
            mDialog.setContentView(dialogView);
            mDialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    /**
     * 버튼이 좌우가 3:7로 되어있는 팝업
     *
     * @param titleText
     * @param msg
     * @param positive
     * @param negative
     * @param positiveListener
     * @param negativeListener
     * @param cancelListener
     * @param dismissListener
     * @param isCancelable
     */
    public void showSimpleDialogType01(String titleText, String msg, String positive, String negative, final View.OnClickListener positiveListener, final View.OnClickListener negativeListener, DialogInterface.OnCancelListener cancelListener, //
                                       DialogInterface.OnDismissListener dismissListener, //
                                       boolean isCancelable)
    {
        if (isFinishing())
        {
            return;
        }

        if (mDialog != null)
        {
            if (mDialog.isShowing())
            {
                mDialog.dismiss();
            }

            mDialog = null;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_button_weight_layout, null, false);

        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCanceledOnTouchOutside(false);

        // 상단
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);

        if (Util.isTextEmpty(titleText) == true)
        {
            titleTextView.setText(getString(R.string.dialog_notice2));
        } else
        {
            titleTextView.setText(titleText);
        }

        // 메시지
        TextView messageTextView = (TextView) dialogView.findViewById(R.id.messageTextView);
        messageTextView.setText(msg);

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);

        if (Util.isTextEmpty(positive, negative) == false)
        {
            twoButtonLayout.setVisibility(View.VISIBLE);

            TextView negativeTextView = (TextView) twoButtonLayout.findViewById(R.id.negativeTextView);
            TextView positiveTextView = (TextView) twoButtonLayout.findViewById(R.id.positiveTextView);

            negativeTextView.setText(negative);
            negativeTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mDialog != null && mDialog.isShowing())
                    {
                        mDialog.dismiss();
                    }

                    if (negativeListener != null)
                    {
                        negativeListener.onClick(v);
                    }
                }
            });

            positiveTextView.setText(positive);
            positiveTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mDialog != null && mDialog.isShowing())
                    {
                        mDialog.dismiss();
                    }

                    if (positiveListener != null)
                    {
                        positiveListener.onClick(v);
                    }
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
            mDialog.setContentView(dialogView);
            mDialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public void showDailyCallDialog(final OnCallDialogListener listener)
    {
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                if (listener != null)
                {
                    listener.onPositiveButtonClick(v);
                }

                String remoteConfigPhoneNumber = DailyPreference.getInstance(BaseActivity.this).getRemoteConfigCompanyPhoneNumber();
                String phoneNumber = Util.isTextEmpty(remoteConfigPhoneNumber) == false //
                    ? remoteConfigPhoneNumber : Constants.PHONE_NUMBER_DAILYHOTEL;

                String noCallMessage = getResources().getString(R.string.toast_msg_no_call_format, phoneNumber);

                if (Util.isTelephonyEnabled(BaseActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(BaseActivity.this, noCallMessage, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(BaseActivity.this, noCallMessage, Toast.LENGTH_LONG);
                }
            }
        };

        View.OnClickListener nativeListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onNativeButtonClick(v);
                }
            }
        };

        DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        };

        String[] hour = DailyPreference.getInstance(BaseActivity.this).getOperationTime().split("\\,");
        String startHour = hour[0];
        String endHour = hour[1];

        String operatingTimeMessage = getString(R.string.dialog_msg_call) //
            + "\n" + getResources().getString(R.string.message_consult02, startHour, endHour);

        showSimpleDialog(getString(R.string.dialog_notice2), operatingTimeMessage, //
            getString(R.string.dialog_btn_call), getString(R.string.dialog_btn_text_cancel) //
            , positiveListener, nativeListener, null, dismissListener, true);

        if (listener != null)
        {
            listener.onShowDialog();
        }
    }

    /**
     * open login page
     *
     * @param callByScreen AnalyticsManager.Screen
     */
    protected void startLoginActivity(String callByScreen)
    {
        DailyToast.showToast(this, R.string.toast_msg_please_login, Toast.LENGTH_LONG);

        Intent intent = LoginActivity.newInstance(this, callByScreen);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
    }
}

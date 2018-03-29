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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.repository.local.ConfigLocalImpl;
import com.daily.dailyhotel.repository.remote.FacebookRemoteImpl;
import com.daily.dailyhotel.repository.remote.KakaoRemoteImpl;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.screen.common.LoadingDialog;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;

import java.util.Calendar;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.HttpException;

public abstract class BaseActivity extends AppCompatActivity implements Constants
{
    private static final int MESSAGE_SHOW_LOADING_PROGRESS = 1;

    protected interface OnCallDialogListener
    {
        void onShowDialog();

        void onPositiveButtonClick(View v);

        void onNativeButtonClick(View v);

        void onDismissDialog();
    }

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    // showCallDialog 용 interface
    private interface OnOperatingTimeListener
    {
        void onInValidOperatingTime(boolean isInValidOperatingTime);
    }

    Dialog mDialog;
    LoadingDialog mLockUI;
    protected String mNetworkTag;
    private boolean mScreenTouchEnabled = true;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MESSAGE_SHOW_LOADING_PROGRESS:
                    mLockUI.show(msg.arg1 == 1);
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

        if (VersionUtils.isOverAPI21() == true && VersionUtils.isOverAPI23() == false)
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
        }
    }

    public void onProgressBackPressed()
    {
        onBackPressed();

        if (this instanceof MainActivity == false && isFinishing() == false)
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
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        return mScreenTouchEnabled == false || super.dispatchTouchEvent(ev);
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

    public void setScreenTouchEnabled(boolean touchEnabled)
    {
        mScreenTouchEnabled = touchEnabled;
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

            Message message = new Message();
            message.what = MESSAGE_SHOW_LOADING_PROGRESS;
            message.arg1 = isShowProgress ? 1 : 0;

            mHandler.sendMessageDelayed(message, 1000);
        }
    }

    public void lockUIImmediately()
    {
        lockUiComponent();

        if (mLockUI != null && isFinishing() == false)
        {
            if (mLockUI == null)
            {
                mLockUI = new LoadingDialog(this);
            }

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

        clearCompositeDisposable();

        super.onDestroy();
    }

    public void onExpiredSessionError()
    {
        unLockUI();

        if (DEBUG == true)
        {
            ExLog.e("pinkred - onExpiredSessionError : " + DailyHotel.AUTHORIZATION);
        }

        DailyPreference.getInstance(this).clear();
        DailyUserPreference.getInstance(this).clear();

        // 임시 저장된 리뷰 전체 삭제
        DailyDb dailyDb = DailyDbHelper.getInstance().open(this);
        dailyDb.deleteAllTempReview();
        DailyDbHelper.getInstance().close();

        try
        {
            LoginManager.getInstance().logOut();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        try
        {
            UserManagement.getInstance().requestLogout(null);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        restartExpiredSession();
    }

    public void onErrorResponse(Call call, Response response)
    {
        unLockUI();

        if (response != null && response.code() == 401)
        {
            onExpiredSessionError();
            return;
        }

        onError();
    }

    public void onErrorResponse(Response response, String message)
    {
        unLockUI();

        if (response != null && response.code() == 401)
        {
            onExpiredSessionError();
            return;
        }

        onErrorToastMessage(message);
    }

    public void onError(Call call, Throwable e, boolean onlyReport)
    {
        if (call != null && e != null)
        {
            Crashlytics.log(call.request().url().toString());
            Crashlytics.logException(e);

            ExLog.e(e.toString());
        }

        if (onlyReport == false)
        {
            onError();
        }
    }

    public void onError(Throwable e)
    {
        if (e != null)
        {
            Crashlytics.logException(e);
            ExLog.e(e.toString());
        }

        onError();
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

    public void onErrorPopupMessage(int msgCode, String message, final View.OnClickListener listener)
    {
        unLockUI();

        //        showSimpleDialog(null, String.format("%s(%d)", message, msgCode), getString(R.string.dialog_btn_text_confirm), null, listener, null, false);
        showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null, listener, null, null, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                if (listener != null)
                {
                    listener.onClick(null);
                }
            }
        }, true);
    }

    public void onErrorToastMessage(final String message)
    {
        unLockUI();

        if (DailyTextUtils.isTextEmpty(message) == true)
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

    protected void addCompositeDisposable(Disposable disposable)
    {
        if (disposable == null)
        {
            return;
        }

        mCompositeDisposable.add(disposable);
    }

    protected void clearCompositeDisposable()
    {
        mCompositeDisposable.clear();
    }

    protected void onHandleError(Throwable throwable)
    {
        unLockUI();

        onReportError(throwable);

        if (isFinishing() == true)
        {
            return;
        }

        if (throwable instanceof BaseException)
        {
            // 팝업 에러 보여주기
            BaseException baseException = (BaseException) throwable;

            showSimpleDialog(null, baseException.getMessage()//
                , getString(R.string.dialog_btn_text_confirm), null, null, null, null, dialogInterface -> this.onBackPressed(), true);
        } else if (throwable instanceof HttpException)
        {
            retrofit2.HttpException httpException = (HttpException) throwable;

            if (httpException.code() == BaseException.CODE_UNAUTHORIZED)
            {
                addCompositeDisposable(new ConfigLocalImpl(this).clear().subscribe(object -> {
                    new FacebookRemoteImpl().logOut();
                    new KakaoRemoteImpl().logOut();

                    restartExpiredSession();
                }));
            } else
            {
                DailyToast.showToast(this, getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);
            }
        } else
        {
            DailyToast.showToast(this, getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);
        }
    }

    protected void onReportError(Throwable throwable)
    {
        if (throwable == null)
        {
            return;
        }

        try
        {
            if (throwable instanceof BaseException)
            {
                BaseException baseException = (BaseException) throwable;
                Crashlytics.log("msgCode : " + baseException.getCode() + ", message : " + baseException.getMessage());
                Crashlytics.logException(throwable);

                ExLog.e("msgCode : " + baseException.getCode() + ", message : " + baseException.getMessage());
                ExLog.e(throwable.toString());
            } else if (throwable instanceof HttpException)
            {
                retrofit2.HttpException httpException = (HttpException) throwable;
                Crashlytics.log(httpException.response().raw().request().url().toString());
                Crashlytics.logException(throwable);

                ExLog.e(httpException.response().raw().request().url().toString());
                ExLog.e(throwable.toString());
            } else
            {
                Crashlytics.logException(throwable);

                ExLog.e(throwable.toString());
            }
        } catch (Exception e)
        {
            // 리포팅 하다가 죽지는 않겠죠?
        }
    }

    private void recursiveRecycle(View root)
    {
        if (root == null)
        {
            return;
        }

        if (VersionUtils.isOverAPI16())
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
        TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);

        if (DailyTextUtils.isTextEmpty(titleText) == true)
        {
            titleTextView.setText(getString(R.string.dialog_notice2));
        } else
        {
            titleTextView.setText(titleText);
        }

        // 메시지
        TextView messageTextView = dialogView.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);
        View oneButtonLayout = buttonLayout.findViewById(R.id.oneButtonLayout);

        if (DailyTextUtils.isTextEmpty(positive, negative) == false)
        {
            twoButtonLayout.setVisibility(View.VISIBLE);
            oneButtonLayout.setVisibility(View.GONE);

            TextView negativeTextView = twoButtonLayout.findViewById(R.id.negativeTextView);
            TextView positiveTextView = twoButtonLayout.findViewById(R.id.positiveTextView);

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

            TextView confirmTextView = oneButtonLayout.findViewById(R.id.confirmTextView);

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

    public void hideSimpleDialog()
    {
        if (mDialog != null)
        {
            if (mDialog.isShowing())
            {
                mDialog.cancel();
            }

            mDialog = null;
        }
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

        hideSimpleDialog();

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_layout, null, false);

        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCanceledOnTouchOutside(false);

        // 상단
        TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);

        if (DailyTextUtils.isTextEmpty(titleText) == true)
        {
            titleTextView.setText(getString(R.string.dialog_notice2));
        } else
        {
            titleTextView.setText(titleText);
        }

        // 메시지
        TextView messageTextView = dialogView.findViewById(R.id.messageTextView);
        messageTextView.setText(msg);

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);
        View oneButtonLayout = buttonLayout.findViewById(R.id.oneButtonLayout);

        if (DailyTextUtils.isTextEmpty(positive, negative) == false)
        {
            twoButtonLayout.setVisibility(View.VISIBLE);
            oneButtonLayout.setVisibility(View.GONE);

            TextView negativeTextView = twoButtonLayout.findViewById(R.id.negativeTextView);
            TextView positiveTextView = twoButtonLayout.findViewById(R.id.positiveTextView);

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

            TextView confirmTextView = oneButtonLayout.findViewById(R.id.confirmTextView);

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

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, mDialog);

            mDialog.show();

            mDialog.getWindow().setAttributes(layoutParams);
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
    public void showSimpleDialogType01(String titleText, String msg, String positive, String negative//
        , final View.OnClickListener positiveListener, final View.OnClickListener negativeListener//
        , DialogInterface.OnCancelListener cancelListener, DialogInterface.OnDismissListener dismissListener, boolean isCancelable)
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
        TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);

        if (DailyTextUtils.isTextEmpty(titleText) == true)
        {
            titleTextView.setText(getString(R.string.dialog_notice2));
        } else
        {
            titleTextView.setText(titleText);
        }

        // 메시지
        TextView messageTextView = dialogView.findViewById(R.id.messageTextView);
        messageTextView.setText(msg);

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);

        if (DailyTextUtils.isTextEmpty(positive, negative) == false)
        {
            twoButtonLayout.setVisibility(View.VISIBLE);

            TextView negativeTextView = twoButtonLayout.findViewById(R.id.negativeTextView);
            TextView positiveTextView = twoButtonLayout.findViewById(R.id.positiveTextView);

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

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, mDialog);

            mDialog.show();

            mDialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    // EventWebActivity 공유 용
    public void showSimpleDialog(View view, DialogInterface.OnCancelListener cancelListener//
        , DialogInterface.OnDismissListener dismissListener, boolean cancelable)
    {
        if (isFinishing() == true | view == null)
        {
            return;
        }

        hideSimpleDialog();

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mDialog = new Dialog(this);
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

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, mDialog);

            mDialog.show();

            mDialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }


    public interface OnCheckDialogStateListener
    {
        void onState(View view, boolean checked);
    }

    /**
     * 버튼이 좌우가 3:7로 되어있는 팝업
     * 다시 보지 않기 체크 버튼이 들어가 있음.
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
    public void showSimpleDialogType02(String titleText, String msg, String positive, String negative//
        , final OnCheckDialogStateListener positiveListener, final OnCheckDialogStateListener negativeListener//
        , final OnCheckDialogStateListener cancelListener, final OnCheckDialogStateListener dismissListener, boolean isCancelable)
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
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_button_checkbox_layout, null, false);

        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCanceledOnTouchOutside(false);

        // 상단
        TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);

        if (DailyTextUtils.isTextEmpty(titleText) == true)
        {
            titleTextView.setText(getString(R.string.dialog_notice2));
        } else
        {
            titleTextView.setText(titleText);
        }

        // 메시지
        TextView messageTextView = dialogView.findViewById(R.id.messageTextView);
        messageTextView.setText(msg);

        // 체크박스
        final CheckBox checkBox = dialogView.findViewById(R.id.checkBox);

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);

        if (DailyTextUtils.isTextEmpty(positive, negative) == false)
        {
            twoButtonLayout.setVisibility(View.VISIBLE);

            TextView negativeTextView = twoButtonLayout.findViewById(R.id.negativeTextView);
            TextView positiveTextView = twoButtonLayout.findViewById(R.id.positiveTextView);

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
                        negativeListener.onState(v, checkBox.isChecked());
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
                        positiveListener.onState(v, checkBox.isChecked());
                    }
                }
            });
        }

        if (cancelListener != null)
        {
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    if (cancelListener != null)
                    {
                        cancelListener.onState(null, checkBox.isChecked());
                    }
                }
            });
        }

        if (dismissListener != null)
        {
            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    if (dismissListener != null)
                    {
                        dismissListener.onState(null, checkBox.isChecked());
                    }
                }
            });
        }

        mDialog.setCancelable(isCancelable);

        try
        {
            mDialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, mDialog);

            mDialog.show();

            mDialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void checkInValidOperatingTime(OnOperatingTimeListener operatingTimeListener)
    {
        if (operatingTimeListener == null)
        {
            return;
        }

        retrofit2.Callback dateTimeCallback = new retrofit2.Callback<BaseDto<TodayDateTime>>()
        {
            @Override
            public void onResponse(Call<BaseDto<TodayDateTime>> call, Response<BaseDto<TodayDateTime>> response)
            {
                boolean isInValidOperatingTime = false;

                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        BaseDto<TodayDateTime> baseDto = response.body();

                        if (baseDto.msgCode == 100)
                        {
                            TodayDateTime todayDateTime = baseDto.data;

                            Calendar todayCalendar = DailyCalendar.getInstance(todayDateTime.currentDateTime, false);
                            int hour = todayCalendar.get(Calendar.HOUR_OF_DAY);
                            int minute = todayCalendar.get(Calendar.MINUTE);

                            String startHourString = DailyCalendar.convertDateFormatString(todayDateTime.openDateTime, DailyCalendar.ISO_8601_FORMAT, "H");
                            String endHourString = DailyCalendar.convertDateFormatString(todayDateTime.closeDateTime, DailyCalendar.ISO_8601_FORMAT, "H");

                            int startHour = Integer.parseInt(startHourString);
                            int endHour = Integer.parseInt(endHourString);

                            String[] lunchTimes = DailyRemoteConfigPreference.getInstance(BaseActivity.this).getRemoteConfigOperationLunchTime().split("\\,");
                            String[] startLunchTime = lunchTimes[0].split(":");
                            String[] endLunchTime = lunchTimes[1].split(":");

                            int startLunchHour = Integer.parseInt(startLunchTime[0]);
                            int startLunchMinute = Integer.parseInt(startLunchTime[1]);
                            int endLunchHour = Integer.parseInt(endLunchTime[0]);

                            boolean isOverStartTime = hour > startLunchHour || (hour == startLunchHour && minute >= startLunchMinute);
                            boolean isOverEndTime = hour >= endLunchHour;

                            if (hour < startHour && hour > endHour)
                            {
                                // 운영 안하는 시간 03:00:01 ~ 08:59:59 - 팝업 발생
                                isInValidOperatingTime = true;
                            } else if (isOverStartTime == true && isOverEndTime == false)
                            {
                                // 점심시간 11:50:01~12:59:59 - 해피톡의 경우 팝업 발생 안함
                                isInValidOperatingTime = true;
                            }
                        } else
                        {
                            isInValidOperatingTime = false;
                        }
                    } catch (Exception e)
                    {
                        isInValidOperatingTime = false;
                    }
                } else
                {
                    isInValidOperatingTime = false;
                }

                operatingTimeListener.onInValidOperatingTime(isInValidOperatingTime);
            }

            @Override
            public void onFailure(Call<BaseDto<TodayDateTime>> call, Throwable t)
            {
                operatingTimeListener.onInValidOperatingTime(false);
            }
        };

        DailyMobileAPI.getInstance(BaseActivity.this).requestCommonDateTime(mNetworkTag, dateTimeCallback);
    }

    public void showDailyCallDialog(final OnCallDialogListener listener)
    {
        OnOperatingTimeListener operatingTimeListener = new OnOperatingTimeListener()
        {
            @Override
            public void onInValidOperatingTime(boolean isInValidOperatingTime)
            {
                if (isInValidOperatingTime == true)
                {
                    showNonOperatingTimeDialog(listener);
                } else
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

                            String remoteConfigPhoneNumber = DailyRemoteConfigPreference.getInstance(BaseActivity.this).getRemoteConfigCompanyPhoneNumber();
                            String phoneNumber = DailyTextUtils.isTextEmpty(remoteConfigPhoneNumber) == false //
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

                            if (listener != null)
                            {
                                listener.onDismissDialog();
                            }
                        }
                    };

                    String[] hour = DailyPreference.getInstance(BaseActivity.this).getOperationTime().split("\\,");
                    String startHour = hour[0];
                    String endHour = hour[1];

                    String[] lunchTimes = DailyRemoteConfigPreference.getInstance(BaseActivity.this).getRemoteConfigOperationLunchTime().split("\\,");
                    String startLunchTime = lunchTimes[0];
                    String endLunchTime = lunchTimes[1];

                    String operatingTimeMessage = getString(R.string.dialog_msg_call) //
                        + "\n" + getResources().getString(R.string.message_consult02, startHour, endHour, startLunchTime, endLunchTime);

                    showSimpleDialog(getString(R.string.dialog_notice2), operatingTimeMessage, //
                        getString(R.string.dialog_btn_call), getString(R.string.dialog_btn_text_cancel) //
                        , positiveListener, nativeListener, null, dismissListener, true);

                    if (listener != null)
                    {
                        listener.onShowDialog();
                    }
                }
            }
        };

        checkInValidOperatingTime(operatingTimeListener);
    }

    public void showNonOperatingTimeDialog(final OnCallDialogListener listener)
    {
        View.OnClickListener positiveListener = v -> {
            releaseUiComponent();

            if (listener != null)
            {
                listener.onPositiveButtonClick(v);
            }
        };

        DialogInterface.OnDismissListener dismissListener = dialog -> {
            releaseUiComponent();
            if (listener != null)
            {
                listener.onDismissDialog();
            }
        };

        String[] hour = DailyPreference.getInstance(this).getOperationTime().split("\\,");
        String startHour = hour[0];
        String endHour = hour[1];

        String[] lunchTimes = DailyRemoteConfigPreference.getInstance(this).getRemoteConfigOperationLunchTime().split("\\,");
        String startLunchTime = lunchTimes[0];
        String endLunchTime = lunchTimes[1];

        // 우선 점심시간의 경우 로컬에서 시간 픽스
        String noneOperatingTimeMessage = getResources().getString( //
            R.string.dialog_message_none_operating_time, startHour, endHour, startLunchTime, endLunchTime);

        showSimpleDialog(getString(R.string.dialog_information), noneOperatingTimeMessage, //
            getString(R.string.dialog_btn_text_confirm), positiveListener, dismissListener);

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

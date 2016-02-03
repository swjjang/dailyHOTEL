package com.twoheart.dailyhotel.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.LoadingDialog;
import com.twoheart.dailyhotel.view.OnLoadListener;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.FontManager;

public class BaseActivity extends AppCompatActivity implements Constants, OnLoadListener, ErrorListener
{
    private Dialog mDialog;
    private LoadingDialog mLockUI;
    private Handler handler;
    protected String mNetworkTag;

    /**
     * UI Component의 잠금 상태인지 확인하는 변수..
     */
    private boolean mIsLockUiComponent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mLockUI = new LoadingDialog(this);
        handler = new Handler();
        mNetworkTag = getClass().getName();

        if (Util.isOverAPI21() == true)
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
            DailyNetworkAPI.getInstance().cancelAll();
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

    @Override
    public void setContentView(int layoutResID)
    {
        try
        {
            super.setContentView(layoutResID);
        } catch (OutOfMemoryError errror)
        {
            Util.finishOutOfMemory(BaseActivity.this);
        }
    }

    public String getNetworkTag()
    {
        return mNetworkTag;
    }

    public void restartApp()
    {
        if (isFinishing() == true)
        {
            return;
        }

        // 세션이 만료되어 재시작 요청.
        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_session_expired), getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Util.restartApp(BaseActivity.this);
            }
        }, null, false);
    }

    // 메뉴 버튼을 막아버림.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause()
    {
        try
        {
            VolleyHttpClient.cookieManagerStopSync();

            unLockUI();

            super.onPause();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
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

        VolleyHttpClient.cookieManagerStartSync();
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

    /**
     * LoadingDialog를 띄워 로딩 중임을 나타내어 사용자가 UI를 사용할 수 없도록 한다.
     */

    public boolean isVisibleLockUI()
    {
        return mLockUI.isVisible();
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

            mLockUI.show(isShowProgress);
        }
    }

    public void showLockUIProgress()
    {
        if (mLockUI != null)
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
        DailyNetworkAPI.getInstance().cancelAll(mNetworkTag);
        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
            mDialog = null;
        }

        // MainActivity에서 종료시 이슈가 있어 MainActivity에서는 작동하지 않도록 수정
        if (this instanceof MainActivity == false)
        {
            try
            {
                recursiveRecycle(getWindow().getDecorView());
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        super.onDestroy();
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        unLockUI();

        ExLog.e(error.toString());

        onError();
    }

    public void onError(Exception error)
    {
        releaseUiComponent();

        ExLog.e(error.toString());

        onError();
    }

    /**
     * Error 발생 시 분기되는 메서드
     */
    public void onError()
    {
        releaseUiComponent();

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                // 잘못된 멘트, 모든 에러가 이쪽으로 빠지게됨. 변경 필요.
                DailyToast.showToast(BaseActivity.this, getResources().getString(R.string.act_base_network_connect), Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * 기본적으로 내부오류가 발생하였을 경우 사용
     */
    public void onInternalError()
    {
        unLockUI();

        showSimpleDialog(null, getString(R.string.dialog_msg_internal_error), getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        }, null, false);
    }

    public void onInternalError(String message)
    {
        unLockUI();

        showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        }, null, false);
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

        root = null;
        return;
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
        messageTextView.setTypeface(FontManager.getInstance(this).getMediumTypeface());

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);
        View oneButtonLayout = buttonLayout.findViewById(R.id.oneButtonLayout);

        if (Util.isTextEmpty(positive) == false && Util.isTextEmpty(negative) == false)
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
            confirmTextView.setOnClickListener(new View.OnClickListener()
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
        messageTextView.setTypeface(FontManager.getInstance(this).getMediumTypeface());

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);
        View oneButtonLayout = buttonLayout.findViewById(R.id.oneButtonLayout);

        if (Util.isTextEmpty(positive) == false && Util.isTextEmpty(negative) == false)
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
            confirmTextView.setOnClickListener(new View.OnClickListener()
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

    protected void startLoginActivity()
    {
        DailyToast.showToast(this, R.string.toast_msg_please_login, Toast.LENGTH_LONG);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
    }
}

/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * SplashActivity (로딩화면)
 * <p>
 * 어플리케이션 처음 시작 시 나타나는 화면이며, 이는 MainActivity에 의해서
 * 호출된다. SplashActivity는 어플리케이션 처음 실행 시 가장 먼저 나타나는
 * 화면이나 어플리케이션의 주 화면은 아니므로 MainActivity가 처음 실행됐을 시
 * 호출된다. SplashActivity는 어플리케이션이 최신 버전인지 확인하며, 자동
 * 로그인이 필요한 경우 수행하는 일을 한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.HashMap;

public class SplashActivity extends BaseActivity implements Constants, ErrorListener
{
    private static final int PROGRESS_CIRCLE_COUNT = 3;

    private Dialog mAlertDlg;
    private View mProgressView;
    private View[] mCircleViewList;
    private boolean mIsRequestLogin;
    private boolean mDoService;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:
                    if (mDoService == true)
                    {
                        processLogin();
                    }
                    break;

                case 1:
                    if (mProgressView.getVisibility() != View.VISIBLE)
                    {
                        mProgressView.setVisibility(View.VISIBLE);

                        showSplashView();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        mIsRequestLogin = false;
        mDoService = true;

        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putBoolean(KEY_PREFERENCE_REGION_SETTING, false);
        editor.putBoolean(KEY_PREFERENCE_FNB_REGION_SETTING, false);
        editor.remove(KEY_PREFERENCE_GCM_ID);
        editor.apply();

        mProgressView = findViewById(R.id.progressLayout);
        mProgressView.setVisibility(View.INVISIBLE);

        mCircleViewList = new View[PROGRESS_CIRCLE_COUNT];

        for (int i = 0; i < PROGRESS_CIRCLE_COUNT; i++)
        {
            mCircleViewList[i] = findViewById(R.id.iv_splash_circle1 + i);
        }
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(Screen.SPLASH);
        super.onStart();

        // 서버 상태 체크
        DailyNetworkAPI.getInstance().requestCheckServer(mNetworkTag, mStatusHealthCheckJsonResponseListener, new ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (VolleyHttpClient.isAvailableNetwork() == false)
        {
            showDisabledNetworkPopup();
        } else
        {
            if (mIsRequestLogin == false)
            {
                mIsRequestLogin = true;

                mHandler.sendEmptyMessageDelayed(0, 1500);
            }

            showProgress();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        mHandler.removeMessages(1);
    }

    @Override
    protected void onDestroy()
    {
        if (mAlertDlg != null)
        {
            if (mAlertDlg.isShowing() == true)
            {
                mAlertDlg.dismiss();
            }

            mAlertDlg = null;
        }

        super.onDestroy();
    }

    private void showProgress()
    {
        if (mProgressView.getVisibility() != View.VISIBLE)
        {
            mHandler.removeMessages(1);
            mHandler.sendEmptyMessageDelayed(1, 1000);
        }
    }

    private void showDisabledNetworkPopup()
    {
        if (mAlertDlg != null)
        {
            if (mAlertDlg.isShowing() == true)
            {
                mAlertDlg.dismiss();
            }

            mAlertDlg = null;
        }

        View.OnClickListener posListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mAlertDlg.dismiss();

                if (VolleyHttpClient.isAvailableNetwork())
                {
                    if (mProgressView.getVisibility() != View.VISIBLE)
                    {
                        mHandler.removeMessages(1);
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    }

                    processLogin();
                } else
                {
                    mHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            showDisabledNetworkPopup();
                        }
                    }, 100);
                }
            }
        };

        View.OnClickListener negaListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                mAlertDlg.dismiss();
            }
        };

        OnKeyListener keyListener = new OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    mAlertDlg.dismiss();
                    finish();
                    return true;
                }
                return false;
            }
        };

        mAlertDlg = createSimpleDialog(getString(R.string.dialog_btn_text_waiting)//
            , getString(R.string.dialog_msg_network_unstable_retry_or_set_wifi)//
            , getString(R.string.dialog_btn_text_retry)//
            , getString(R.string.dialog_btn_text_setting), posListener, negaListener);
        mAlertDlg.setOnKeyListener(keyListener);

        try
        {
            mAlertDlg.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void showSplashView()
    {
        for (int i = 0; i < PROGRESS_CIRCLE_COUNT; i++)
        {
            final int idx = i;
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mCircleViewList[idx].setVisibility(View.VISIBLE);
                    mCircleViewList[idx].startAnimation(AnimationUtils.loadAnimation(SplashActivity.this, R.anim.splash_load));
                }
            }, 250 * (i + 1));
        }
    }

    private void processLogin()
    {
        if (mStatusBarHeight == 0)
        {
            Rect rectgle = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rectgle);
            mStatusBarHeight = rectgle.top;
        }

        if (sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
        {
            HashMap<String, String> params = Util.getLoginParams(SplashActivity.this, sharedPreference);
            DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, this);
        }

        DailyNetworkAPI.getInstance().requestCommonVer(mNetworkTag, mAppVersionJsonResponseListener, this);
    }

    private void doFinish()
    {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        super.onErrorResponse(error);
        finish();
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.fade_out);
    }

    private void requestConfiguration()
    {
        // ABTest
        //        ABTestPreference.getInstance(getApplicationContext()).requestConfiguration(getApplicationContext(), mQueue, new OnABTestListener()
        //        {
        //            @Override
        //            public void onPostExecute()
        //            {
        //                doFinish();
        //            }
        //        });

        DailyNetworkAPI.getInstance().requestCompanyInformation(mNetworkTag, mCompanyInformationJsonResponseListener, this);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignin = jsonObject.getBoolean("is_signin");

                    if (isSignin == true)
                    {
                        VolleyHttpClient.createCookie();
                        return;
                    }
                }

                // 로그인 실패
                // data 초기화
                SharedPreferences.Editor ed = sharedPreference.edit();
                ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
                ed.putString(KEY_PREFERENCE_USER_ID, null);
                ed.putString(KEY_PREFERENCE_USER_PWD, null);
                ed.putString(KEY_PREFERENCE_USER_TYPE, null);
                ed.commit();
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }
    };

    private DailyHotelJsonResponseListener mAppVersionJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                SharedPreferences.Editor editor = sharedPreference.edit();

                if (RELEASE_STORE == Stores.PLAY_STORE)
                {
                    editor.putString(KEY_PREFERENCE_MAX_VERSION_NAME, response.getString("play_max"));
                    editor.putString(KEY_PREFERENCE_MIN_VERSION_NAME, response.getString("play_min"));
                } else if (RELEASE_STORE == Stores.T_STORE)
                {
                    editor.putString(KEY_PREFERENCE_MAX_VERSION_NAME, response.getString("tstore_max"));
                    editor.putString(KEY_PREFERENCE_MIN_VERSION_NAME, response.getString("tstore_min"));
                } else if (RELEASE_STORE == Stores.N_STORE)
                {
                    editor.putString(KEY_PREFERENCE_MAX_VERSION_NAME, response.getString("nstore_max"));
                    editor.putString(KEY_PREFERENCE_MIN_VERSION_NAME, response.getString("nstore_min"));
                }

                editor.commit();

                int maxVersion = Integer.parseInt(sharedPreference.getString(KEY_PREFERENCE_MAX_VERSION_NAME, "1.0.0").replace(".", ""));
                int minVersion = Integer.parseInt(sharedPreference.getString(KEY_PREFERENCE_MIN_VERSION_NAME, "1.0.0").replace(".", ""));
                int currentVersion = Integer.parseInt(getPackageManager().getPackageInfo(getPackageName(), 0).versionName.replace(".", ""));
                int skipMaxVersion = Integer.parseInt(sharedPreference.getString(KEY_PREFERENCE_SKIP_MAX_VERSION, "1.0.0").replace(".", ""));

                ExLog.d("MIN / MAX / CUR / SKIP : " + minVersion + " / " + maxVersion + " / " + currentVersion + " / " + skipMaxVersion);

                if (minVersion > currentVersion)
                {
                    View.OnClickListener posListener = new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                            marketLaunch.setData(Uri.parse(Util.storeReleaseAddress()));

                            if (marketLaunch.resolveActivity(getPackageManager()) == null)
                            {
                                marketLaunch.setData(Uri.parse(Constants.URL_STORE_GOOGLE_DAILYHOTEL_WEB));
                            }

                            startActivity(marketLaunch);
                            finish();
                        }
                    };

                    OnCancelListener cancelListener = new OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    };

                    showSimpleDialog(getString(R.string.dialog_title_notice), getString(R.string.dialog_msg_please_update_new_version), getString(R.string.dialog_btn_text_update), posListener, cancelListener);

                } else if ((maxVersion > currentVersion) && (skipMaxVersion != maxVersion))
                {
                    View.OnClickListener posListener = new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                            marketLaunch.setData(Uri.parse(Util.storeReleaseAddress()));

                            if (marketLaunch.resolveActivity(getPackageManager()) == null)
                            {
                                marketLaunch.setData(Uri.parse(Constants.URL_STORE_GOOGLE_DAILYHOTEL_WEB));
                            }

                            startActivity(marketLaunch);
                            finish();
                        }
                    };

                    final OnCancelListener cancelListener = new OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            SharedPreferences.Editor editor = sharedPreference.edit();
                            editor.putString(KEY_PREFERENCE_SKIP_MAX_VERSION, sharedPreference.getString(KEY_PREFERENCE_MAX_VERSION_NAME, "1.0.0"));
                            editor.commit();

                            requestConfiguration();
                        }
                    };

                    View.OnClickListener negListener = new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            cancelListener.onCancel(null);
                        }
                    };

                    showSimpleDialog(getString(R.string.dialog_title_notice), getString(R.string.dialog_msg_update_now), getString(R.string.dialog_btn_text_update), getString(R.string.dialog_btn_text_cancel), posListener, negListener, cancelListener, null, false);
                } else
                {
                    requestConfiguration();
                }
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mStatusHealthCheckJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 200)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSuspend = jsonObject.getBoolean("isSuspend");

                    if (isSuspend == true)
                    {
                        mDoService = false;

                        String title = jsonObject.getString("messageTitle");
                        String message = jsonObject.getString("messageBody");

                        showSimpleDialog(title, message, getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                setResult(RESULT_CANCELED);
                                finish();
                            }
                        }, null, false);
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };

    private DailyHotelJsonResponseListener mCompanyInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");
                    JSONObject companyJSONObject = jsonObject.getJSONObject("companyInfo");

                    String companyName = companyJSONObject.getString("name");
                    String companyCEO = companyJSONObject.getString("ceo");
                    String companyBizRegNumber = companyJSONObject.getString("bizRegNumber");
                    String companyItcRegNumber = companyJSONObject.getString("itcRegNumber");
                    String address1 = companyJSONObject.getString("address1");
                    String phoneNumber1 = companyJSONObject.getString("phoneNumber1");

                    DailyPreference.getInstance(SplashActivity.this).setCompanyName(companyName);
                    DailyPreference.getInstance(SplashActivity.this).setCompanyCEO(companyCEO);
                    DailyPreference.getInstance(SplashActivity.this).setCompanyBizRegNumber(companyBizRegNumber);
                    DailyPreference.getInstance(SplashActivity.this).setCompanyItcRegNumber(companyItcRegNumber);
                    DailyPreference.getInstance(SplashActivity.this).setCompanyAddress(address1);
                    DailyPreference.getInstance(SplashActivity.this).setCompanyPhoneNumber(phoneNumber1);
                }

                doFinish();
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };
}

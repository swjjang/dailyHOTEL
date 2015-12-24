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
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.DailyHotel;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        DailyPreference.getInstance(this).setSettingRegion(TYPE.HOTEL, false);
        DailyPreference.getInstance(this).setSettingRegion(TYPE.FNB, false);
        DailyPreference.getInstance(this).setGCMRegistrationId(null);
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
                processLogin();
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
        }
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

                if (VolleyHttpClient.isAvailableNetwork() == true)
                {
                    processLogin();
                } else
                {
                    view.postDelayed(new Runnable()
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

    private void processLogin()
    {
        if (DailyPreference.getInstance(this).isAutoLogin() == true)
        {
            HashMap<String, String> params = Util.getLoginParams(SplashActivity.this);
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
                DailyPreference.getInstance(SplashActivity.this).removeUserInformation();
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
                String maxVersionName;
                String minVersionName;

                switch (RELEASE_STORE)
                {
                    case N_STORE:
                        maxVersionName = response.getString("nstore_max");
                        minVersionName = response.getString("nstore_min");
                        break;

                    case T_STORE:
                        maxVersionName = response.getString("tstore_max");
                        minVersionName = response.getString("tstore_min");
                        break;

                    case PLAY_STORE:
                    default:
                        maxVersionName = response.getString("play_max");
                        minVersionName = response.getString("play_min");
                        break;
                }

                DailyPreference.getInstance(SplashActivity.this).setMaxVersion(maxVersionName);
                DailyPreference.getInstance(SplashActivity.this).setMinVersion(minVersionName);

                int maxVersion = Integer.parseInt(maxVersionName.replace(".", ""));
                int minVersion = Integer.parseInt(minVersionName.replace(".", ""));
                int currentVersion = Integer.parseInt(DailyHotel.VERSION.replace(".", ""));
                int skipMaxVersion = Integer.parseInt(DailyPreference.getInstance(SplashActivity.this).getSkipVersion().replace(".", ""));

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
                            String maxVersion = DailyPreference.getInstance(SplashActivity.this).getMaxVersion();
                            DailyPreference.getInstance(SplashActivity.this).setSkipVersion(maxVersion);

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
                    } else
                    {
                        processLogin();
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
                    String address = companyJSONObject.getString("address1");
                    String phoneNumber = companyJSONObject.getString("phoneNumber1");
                    String fax = companyJSONObject.getString("fax1");

                    DailyPreference.getInstance(SplashActivity.this).setCompanyInformation(companyName//
                        , companyCEO, companyBizRegNumber, companyItcRegNumber, address, phoneNumber, fax);
                }

                doFinish();
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };
}

/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * CreditFragment (적립금 화면)
 * <p/>
 * 로그인 여부에 따라 적립금을 안내하는 화면이다. 적립금을 표시하며 카카오톡
 * 친구 초대 버튼이 있다. 세부 내역을 따로 표시해주는 버튼을 가지고 있어
 * 해당 화면을 띄워주기도 한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.CreditCardLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 신용카드 등록하기.
 *
 * @author sheldon
 */
public class CreditCardListActivity extends BaseActivity
{
    private CreditCardLayout mCreditCardLayout;
    private boolean mIsPickMode;
    private CreditCard mSelectedCreditCard;

    public interface OnUserActionListener
    {
        public void addCreditCard();

        public void deleteCreditCard(CreditCard card);

        public void onItemClick(CreditCard card);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_creditcardlist);

        Intent intent = getIntent();

        if (intent != null && Intent.ACTION_PICK.equalsIgnoreCase(intent.getAction()) == true)
        {
            mIsPickMode = true;
            mSelectedCreditCard = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CREDITCARD);
        } else
        {
            mIsPickMode = false;
        }

        initLayout(mIsPickMode);
    }

    private void initLayout(boolean isPickMode)
    {
        setActionBar(R.string.actionbar_title_creditcard_activity);

        mCreditCardLayout = (CreditCardLayout) findViewById(R.id.creditCardLayout);
        mCreditCardLayout.setUserActionListener(mOnUserActionListener);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        lockUI();
        mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, this));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD)
        {
            String msg = null;

            switch (resultCode)
            {
                case CODE_RESULT_PAYMENT_BILLING_SUCCSESS:
                    msg = getString(R.string.message_billing_success);
                    break;

                case CODE_RESULT_PAYMENT_BILLING_DUPLICATE:
                    msg = getString(R.string.message_billing_duplicate);
                    break;

                case CODE_RESULT_PAYMENT_BILLING_FAIL:
                    if (data != null && data.hasExtra(NAME_INTENT_EXTRA_DATA_MESSAGE) == true)
                    {
                        msg = data.getStringExtra(NAME_INTENT_EXTRA_DATA_MESSAGE);
                    } else
                    {
                        msg = getString(R.string.message_billing_fail);
                    }
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_FAIL:
                    msg = getString(R.string.act_toast_payment_fail);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR:
                    msg = getString(R.string.act_toast_payment_network_error);
                    break;
            }

            if (msg != null)
            {
                if (isFinishing() == true)
                {
                    return;
                }

                String title = getString(R.string.dialog_notice2);
                String positive = getString(R.string.dialog_btn_text_confirm);

                showSimpleDialog(title, msg, positive, null);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UI Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
    {

        @Override
        public void addCreditCard()
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            // 세션 여부를 판단한다.
            mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, new DailyHotelStringResponseListener()
            {
                @Override
                public void onResponse(String url, String response)
                {
                    String result = null;

                    unLockUI();

                    if (false == Util.isTextEmpty(response))
                    {
                        result = response.trim();
                    }

                    if (true == "alive".equalsIgnoreCase(result))
                    {
                        Intent intent = new Intent(CreditCardListActivity.this, RegisterCreditCardActivity.class);
                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                    } else
                    {
                        restartApp();
                    }
                }
            }, CreditCardListActivity.this));
        }

        @Override
        public void deleteCreditCard(final CreditCard card)
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            // 세션 여부를 판단한다.
            mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, new DailyHotelStringResponseListener()
            {
                @Override
                public void onResponse(String url, String response)
                {
                    String result = null;

                    unLockUI();

                    if (false == Util.isTextEmpty(response))
                    {
                        result = response.trim();
                    }

                    if (true == "alive".equalsIgnoreCase(result))
                    {
                        // 신용카드를 삭제하시겠습니까?
                        View.OnClickListener posListener = new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                lockUI();

                                // 등록된 카드 삭제.
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("billkey", card.billingkey);

                                mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SESSION_BILLING_CARD_DEL).toString(), params, mUserSessionBillingCardDelJsonResponseListener, CreditCardListActivity.this));
                            }
                        };

                        if (isFinishing() == true)
                        {
                            return;
                        }

                        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_delete_register_creditcard), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), posListener, null);
                    } else
                    {
                        restartApp();
                    }
                }
            }, CreditCardListActivity.this));
        }

        @Override
        public void onItemClick(CreditCard card)
        {
            if (mIsPickMode == true)
            {
                if (isLockUiComponent() == true)
                {
                    return;
                }

                lockUI();

                Intent intent = new Intent();
                intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CREDITCARD, card);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Network Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserSessionBillingCardInfoJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            int msg_code = -1;

            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                // 해당 화면은 메시지를 넣지 않는다.
                msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    ExLog.d("msg_code : " + msg_code);
                }

                JSONArray jsonArray = response.getJSONArray("data");
                int length = jsonArray.length();

                ArrayList<CreditCard> arrayList = null;

                if (length == 0)
                {
                    arrayList = new ArrayList<CreditCard>();

                    if (mIsPickMode == true)
                    {
                        mSelectedCreditCard = null;
                    }
                } else
                {
                    arrayList = new ArrayList<CreditCard>(length);

                    boolean hasCreditCard = false;

                    for (int i = 0; i < length; i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // 목록에서는 빌링키가 필요없다.
                        CreditCard creditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"), jsonObject.getString("cardcd"));

                        arrayList.add(creditCard);

                        if (mIsPickMode == true)
                        {
                            if (mSelectedCreditCard != null && mSelectedCreditCard.billingkey.equals(creditCard.billingkey) == true)
                            {
                                hasCreditCard = true;
                                mSelectedCreditCard = creditCard;
                            }
                        }
                    }

                    if (mIsPickMode == true && hasCreditCard == false)
                    {
                        mSelectedCreditCard = null;
                    }
                }

                if (mIsPickMode == true)
                {
                    mCreditCardLayout.setCreditCardList(arrayList, true, mSelectedCreditCard);
                } else
                {
                    mCreditCardLayout.setCreditCardList(arrayList);
                }
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }
    };

    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null.");
                }

                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignin = jsonObject.getBoolean("is_signin");

                    if (isSignin == true)
                    {
                        // credit card 요청
                        mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SESSION_BILLING_CARD_INFO).toString(), null, mUserSessionBillingCardInfoJsonResponseListener, CreditCardListActivity.this));
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

                unLockUI();
                mCreditCardLayout.setViewLoginLayout(false);
            } catch (JSONException e)
            {
                unLockUI();
                onError(e);
            }
        }
    };
    private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
    {

        @Override
        public void onResponse(String url, String response)
        {
            String result = null;

            if (false == Util.isTextEmpty(response))
            {
                result = response.trim();
            }

            if (true == "alive".equalsIgnoreCase(result))
            {
                VolleyHttpClient.createCookie();

                // session alive
                // credit card 요청
                // 목록 요청.
                mCreditCardLayout.setViewLoginLayout(true);

                mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SESSION_BILLING_CARD_INFO).toString(), null, mUserSessionBillingCardInfoJsonResponseListener, CreditCardListActivity.this));

            } else if (true == "dead".equalsIgnoreCase(result))
            {
                // session dead
                // 재로그인

                if (true == sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
                {
                    HashMap<String, String> params = Util.getLoginParams(sharedPreference);

                    mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SIGNIN).toString(), params, mUserLoginJsonResponseListener, CreditCardListActivity.this));
                } else
                {
                    unLockUI();
                    mCreditCardLayout.setViewLoginLayout(false);
                }

            } else
            {
                mCreditCardLayout.setViewLoginLayout(false);
                onError();
                unLockUI();
            }
        }
    };
    private DailyHotelJsonResponseListener mUserSessionBillingCardDelJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            int msg_code = -1;

            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                // 해당 화면은 메시지를 넣지 않는다.
                msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    ExLog.d("msg_code : " + msg_code);
                }

                JSONObject jsonObject = response.getJSONObject("data");

                boolean result = false;

                if (jsonObject != null)
                {
                    result = jsonObject.getInt("isSuccess") == 1;
                }

                if (result == true)
                {
                    // 성공
                    // credit card 요청
                    mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SESSION_BILLING_CARD_INFO).toString(), null, mUserSessionBillingCardInfoJsonResponseListener, CreditCardListActivity.this));
                } else
                {
                    unLockUI();

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    // 실패
                    showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_delete_failed_creditcard), getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            lockUI();

                            // credit card 요청
                            mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SESSION_BILLING_CARD_INFO).toString(), null, mUserSessionBillingCardInfoJsonResponseListener, CreditCardListActivity.this));
                        }

                    });
                }
            } catch (Exception e)
            {
                onError(e);

                lockUI();

                // credit card 요청
                mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SESSION_BILLING_CARD_INFO).toString(), null, mUserSessionBillingCardInfoJsonResponseListener, CreditCardListActivity.this));
            }
        }
    };
}
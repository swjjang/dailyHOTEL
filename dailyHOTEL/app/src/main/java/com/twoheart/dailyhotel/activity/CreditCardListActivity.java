package com.twoheart.dailyhotel.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.view.CreditCardLayout;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

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
    private boolean mIsRegisterCreditCard;

    public interface OnUserActionListener
    {
        void addCreditCard();

        void deleteCreditCard(CreditCard card);

        void onItemClick(CreditCard card);
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
        initToolbar();

        mCreditCardLayout = (CreditCardLayout) findViewById(R.id.creditCardLayout);
        mCreditCardLayout.setUserActionListener(mOnUserActionListener);
    }

    private void initToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_creditcard_activity));
    }

    @Override
    public void onResume()
    {
        super.onResume();

        lockUI();
        DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, this);
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
                    mIsRegisterCreditCard = true;
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
            DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, new DailyHotelStringResponseListener()
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

                        if (mCreditCardLayout != null)
                        {
                            if (mCreditCardLayout.getCreditCardCount() == 0)
                            {
                                AnalyticsManager.getInstance(CreditCardListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                                    , AnalyticsManager.Action.CARD_MANAGEMENT_CLICKED, AnalyticsManager.Label.ADDING_CARD_ICON_CLICKED, null);
                            } else
                            {
                                AnalyticsManager.getInstance(CreditCardListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                                    , AnalyticsManager.Action.CARD_MANAGEMENT_CLICKED, AnalyticsManager.Label.ADDING_CARD_BUTTON_CLICKED, null);
                            }
                        }
                    } else
                    {
                        restartApp();
                    }
                }
            }, CreditCardListActivity.this);
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
            DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, new DailyHotelStringResponseListener()
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
                                DailyNetworkAPI.getInstance().requestUserDeleteBillingCard(mNetworkTag, card.billingkey, mUserSessionBillingCardDelJsonResponseListener, CreditCardListActivity.this);

                                AnalyticsManager.getInstance(CreditCardListActivity.this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
                                    , AnalyticsManager.Action.REGISTERED_CARD_DELETE_POPPEDUP, AnalyticsManager.Label.OK, null);
                            }
                        };

                        View.OnClickListener negativeListener = new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                unLockUI();

                                AnalyticsManager.getInstance(CreditCardListActivity.this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
                                    , AnalyticsManager.Action.REGISTERED_CARD_DELETE_POPPEDUP, AnalyticsManager.Label.CANCEL, null);
                            }
                        };

                        DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                unLockUI();

                                AnalyticsManager.getInstance(CreditCardListActivity.this).recordEvent(AnalyticsManager.Category.POPUPBOXES//
                                    , AnalyticsManager.Action.REGISTERED_CARD_DELETE_POPPEDUP, AnalyticsManager.Label.CANCEL, null);
                            }
                        };

                        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_delete_register_creditcard), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), posListener, negativeListener, cancelListener, null, true);
                    } else
                    {
                        restartApp();
                    }
                }
            }, CreditCardListActivity.this);
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
                    AnalyticsManager.getInstance(CreditCardListActivity.this).recordScreen(AnalyticsManager.Screen.CREDITCARD_LIST_EMPTY, null);

                    arrayList = new ArrayList<CreditCard>();

                    if (mIsPickMode == true)
                    {
                        mSelectedCreditCard = null;
                    }
                } else
                {
                    AnalyticsManager.getInstance(CreditCardListActivity.this).recordScreen(AnalyticsManager.Screen.CREDITCARD_LIST, null);

                    arrayList = new ArrayList<CreditCard>(length);

                    boolean hasCreditCard = false;
                    JSONObject jsonObject;

                    for (int i = 0; i < length; i++)
                    {
                        jsonObject = jsonArray.getJSONObject(i);

                        // 목록에서는 빌링키가 필요없다.
                        CreditCard creditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"), jsonObject.getString("cardcd"));

                        arrayList.add(creditCard);

                        if (mIsPickMode == true && mSelectedCreditCard != null && mSelectedCreditCard.billingkey.equals(creditCard.billingkey) == true)
                        {
                            hasCreditCard = true;
                            mSelectedCreditCard = creditCard;
                        }
                    }

                    if (mIsRegisterCreditCard == true)
                    {
                        mIsRegisterCreditCard = false;

                        AnalyticsManager.getInstance(CreditCardListActivity.this).addCreditCard(arrayList.get(arrayList.size() - 1).cardcd);
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
                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignin = jsonObject.getBoolean("is_signin");

                    if (isSignin == true)
                    {
                        // credit card 요청
                        DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mUserSessionBillingCardInfoJsonResponseListener, CreditCardListActivity.this);
                        return;
                    }
                }

                // 로그인 실패
                // data 초기화
                DailyPreference.getInstance(CreditCardListActivity.this).removeUserInformation();

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

                DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mUserSessionBillingCardInfoJsonResponseListener, CreditCardListActivity.this);

            } else if (true == "dead".equalsIgnoreCase(result))
            {
                // session dead
                // 재로그인

                if (true == DailyPreference.getInstance(CreditCardListActivity.this).isAutoLogin())
                {
                    HashMap<String, String> params = Util.getLoginParams(CreditCardListActivity.this);

                    DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, CreditCardListActivity.this);
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
                    DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mUserSessionBillingCardInfoJsonResponseListener, CreditCardListActivity.this);
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
                            DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mUserSessionBillingCardInfoJsonResponseListener, CreditCardListActivity.this);
                        }

                    });
                }
            } catch (Exception e)
            {
                onError(e);

                lockUI();

                // credit card 요청
                DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mUserSessionBillingCardInfoJsonResponseListener, CreditCardListActivity.this);
            }
        }
    };
}
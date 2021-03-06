package com.twoheart.dailyhotel.screen.mydaily.creditcard;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 신용카드 등록하기.
 *
 * @author sheldon
 */
public class CreditCardListActivity extends BaseActivity
{
    CreditCardLayout mCreditCardLayout;
    boolean mIsPickMode;
    CreditCard mSelectedCreditCard;
    boolean mIsRegisterCreditCard;

    public interface OnUserActionListener
    {
        void addCreditCard();

        void deleteCreditCard(CreditCard card);

        void onItemClick(CreditCard card);
    }

    public static Intent newInstance(Context context, String cardName, String cardNumber, String cardBillingKey, String cardCd)
    {
        Intent intent = new Intent(context, CreditCardListActivity.class);

        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_NAME, cardName);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_NUMBER, cardNumber);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_BILLING_KEY, cardBillingKey);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_CD, cardCd);
        return intent;
    }

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, CreditCardListActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_creditcardlist);

        Intent intent = getIntent();

        if (intent != null && Intent.ACTION_PICK.equalsIgnoreCase(intent.getAction()) == true)
        {
            mIsPickMode = true;

            String cardName = intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_NAME);
            String cardNumber = intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_NUMBER);
            String cardBillingKey = intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_BILLING_KEY);
            String cardCd = intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_CD);

            mSelectedCreditCard = new CreditCard(cardName, cardNumber, cardBillingKey, cardCd);
        } else
        {
            mIsPickMode = false;
        }

        initLayout(mIsPickMode);
    }

    private void initLayout(boolean isPickMode)
    {
        initToolbar();

        mCreditCardLayout = findViewById(R.id.creditCardLayout);
        mCreditCardLayout.setUserActionListener(mOnUserActionListener);
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_creditcard_activity);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (DailyHotel.isLogin() == false)
        {
            mCreditCardLayout.setViewLoginLayout(false);
        } else
        {
            lockUI();

            mCreditCardLayout.setViewLoginLayout(true);
            DailyMobileAPI.getInstance(this).requestUserBillingCardList(mNetworkTag, mCreditCardListCallback);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
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

                case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION:
                    restartExpiredSession();
                    return;

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

            startActivityForResult(RegisterCreditCardActivity.newInstance(CreditCardListActivity.this), CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD);

            if (mCreditCardLayout != null)
            {
                if (mCreditCardLayout.getCreditCardCount() == 0)
                {
                } else
                {
                    AnalyticsManager.getInstance(CreditCardListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                        , AnalyticsManager.Action.CARD_MANAGEMENT_CLICKED, AnalyticsManager.Label.ADDING_CARD_BUTTON_CLICKED, null);
                }
            }
        }

        @Override
        public void deleteCreditCard(final CreditCard card)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            // 신용카드를 삭제하시겠습니까?
            View.OnClickListener posListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    lockUI();

                    // 등록된 카드 삭제.
                    DailyMobileAPI.getInstance(CreditCardListActivity.this).requestUserDeleteBillingCard(mNetworkTag, card.billingkey, mDeleteCreditCardCallback);

                    AnalyticsManager.getInstance(CreditCardListActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                        , AnalyticsManager.Action.REGISTERED_CARD_DELETE_POPPEDUP, AnalyticsManager.Label.OK, null);
                }
            };

            View.OnClickListener negativeListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    unLockUI();

                    AnalyticsManager.getInstance(CreditCardListActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                        , AnalyticsManager.Action.REGISTERED_CARD_DELETE_POPPEDUP, AnalyticsManager.Label.CANCEL_, null);
                }
            };

            DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    unLockUI();

                    AnalyticsManager.getInstance(CreditCardListActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                        , AnalyticsManager.Action.REGISTERED_CARD_DELETE_POPPEDUP, AnalyticsManager.Label.CANCEL_, null);
                }
            };

            showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_delete_register_creditcard), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), posListener, negativeListener, cancelListener, null, true);
        }

        @Override
        public void onItemClick(CreditCard card)
        {
            if (card == null || mIsPickMode == false)
            {
                return;
            }

            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            Intent intent = new Intent();
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_NAME, card.name);
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_NUMBER, card.number);
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_BILLING_KEY, card.billingkey);
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CARD_CD, card.cardcd);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Network Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    retrofit2.Callback mCreditCardListCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    // TODO :  추후에 msgCode결과를 가지고 구분하는 코드가 필요할듯.
                    int msgCode = responseJSONObject.getInt("msg_code");

                    JSONArray dataJSONArray = responseJSONObject.getJSONArray("data");
                    int length = dataJSONArray.length();

                    ArrayList<CreditCard> arrayList;

                    if (length == 0)
                    {
                        AnalyticsManager.getInstance(CreditCardListActivity.this).recordScreen(CreditCardListActivity.this,//
                            AnalyticsManager.Screen.CREDITCARD_LIST_EMPTY, null);

                        arrayList = new ArrayList<>();

                        if (mIsPickMode == true)
                        {
                            mSelectedCreditCard = null;
                        }

                        AnalyticsManager.getInstance(CreditCardListActivity.this).updateCreditCard(null);
                    } else
                    {
                        AnalyticsManager.getInstance(CreditCardListActivity.this).recordScreen(CreditCardListActivity.this,//
                            AnalyticsManager.Screen.CREDITCARD_LIST, null);

                        arrayList = new ArrayList<>(length);

                        boolean hasCreditCard = false;
                        JSONObject jsonObject;
                        String cardcds = null;

                        for (int i = 0; i < length; i++)
                        {
                            jsonObject = dataJSONArray.getJSONObject(i);

                            // 목록에서는 빌링키가 필요없다.
                            CreditCard creditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"), jsonObject.getString("cardcd"));

                            arrayList.add(creditCard);

                            if (cardcds == null)
                            {
                                cardcds = creditCard.name;
                            } else
                            {
                                cardcds += "," + creditCard.name;
                            }

                            if (mIsPickMode == true && mSelectedCreditCard != null && mSelectedCreditCard.billingkey.equals(creditCard.billingkey) == true)
                            {
                                hasCreditCard = true;
                                mSelectedCreditCard = creditCard;
                            }
                        }

                        AnalyticsManager.getInstance(CreditCardListActivity.this).updateCreditCard(cardcds);

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
            } else
            {
                CreditCardListActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            CreditCardListActivity.this.onError(t);
        }
    };

    retrofit2.Callback mDeleteCreditCardCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    // TODO :  추후에 msgCode결과를 가지고 구분하는 코드가 필요할듯.
                    int msgCode = responseJSONObject.getInt("msg_code");
                    String msg = responseJSONObject.getString("msg");

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                    boolean result = false;

                    if (dataJSONObject != null)
                    {
                        result = dataJSONObject.getInt("isSuccess") == 1;
                    }

                    if (result == true)
                    {
                        // 성공
                        // credit card 요청
                        DailyMobileAPI.getInstance(CreditCardListActivity.this).requestUserBillingCardList(mNetworkTag, mCreditCardListCallback);

                        DailyToast.showToast(CreditCardListActivity.this, msg, DailyToast.LENGTH_SHORT);
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
                                DailyMobileAPI.getInstance(CreditCardListActivity.this).requestUserBillingCardList(mNetworkTag, mCreditCardListCallback);
                            }

                        });
                    }
                } catch (Exception e)
                {
                    onError(e);

                    lockUI();

                    // credit card 요청
                    DailyMobileAPI.getInstance(CreditCardListActivity.this).requestUserBillingCardList(mNetworkTag, mCreditCardListCallback);
                }
            } else
            {
                CreditCardListActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            CreditCardListActivity.this.onError(t);
        }
    };
}
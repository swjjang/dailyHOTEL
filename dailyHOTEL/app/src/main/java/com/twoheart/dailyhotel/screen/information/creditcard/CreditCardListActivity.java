package com.twoheart.dailyhotel.screen.information.creditcard;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_creditcard_activity));
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (Util.isTextEmpty(DailyPreference.getInstance(this).getAuthorization()) == true)
        {
            mCreditCardLayout.setViewLoginLayout(false);
        } else
        {
            lockUI();

            mCreditCardLayout.setViewLoginLayout(true);
            DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mCreditCardListJsonResponseListener, CreditCardListActivity.this);
        }
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
        }

        @Override
        public void deleteCreditCard(final CreditCard card)
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            // 신용카드를 삭제하시겠습니까?
            View.OnClickListener posListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    lockUI();

                    // 등록된 카드 삭제.
                    DailyNetworkAPI.getInstance().requestUserDeleteBillingCard(mNetworkTag, card.billingkey, mDeleteCreditCardJsonResponseListener, CreditCardListActivity.this);

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
                        , AnalyticsManager.Action.REGISTERED_CARD_DELETE_POPPEDUP, AnalyticsManager.Label.CANCEL, null);
                }
            };

            DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    unLockUI();

                    AnalyticsManager.getInstance(CreditCardListActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                        , AnalyticsManager.Action.REGISTERED_CARD_DELETE_POPPEDUP, AnalyticsManager.Label.CANCEL, null);
                }
            };

            showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_delete_register_creditcard), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), posListener, negativeListener, cancelListener, null, true);
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

    private DailyHotelJsonResponseListener mCreditCardListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            int msg_code = -1;

            try
            {
                // 해당 화면은 메시지를 넣지 않는다.
                msg_code = response.getInt("msg_code");

                JSONArray jsonArray = response.getJSONArray("data");
                int length = jsonArray.length();

                ArrayList<CreditCard> arrayList;

                if (length == 0)
                {
                    AnalyticsManager.getInstance(CreditCardListActivity.this).recordScreen(AnalyticsManager.Screen.CREDITCARD_LIST_EMPTY, null);

                    arrayList = new ArrayList<>();

                    if (mIsPickMode == true)
                    {
                        mSelectedCreditCard = null;
                    }
                } else
                {
                    AnalyticsManager.getInstance(CreditCardListActivity.this).recordScreen(AnalyticsManager.Screen.CREDITCARD_LIST, null);

                    arrayList = new ArrayList<>(length);

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

    private DailyHotelJsonResponseListener mDeleteCreditCardJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            int msg_code = -1;

            try
            {
                // 해당 화면은 메시지를 넣지 않는다.
                msg_code = response.getInt("msg_code");

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
                    DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mCreditCardListJsonResponseListener, CreditCardListActivity.this);
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
                            DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mCreditCardListJsonResponseListener, CreditCardListActivity.this);
                        }

                    });
                }
            } catch (Exception e)
            {
                onError(e);

                lockUI();

                // credit card 요청
                DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mCreditCardListJsonResponseListener, CreditCardListActivity.this);
            }
        }
    };
}
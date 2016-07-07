package com.twoheart.dailyhotel.screen.common;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.terms.LocationTermsActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class PermissionManagerActivity extends BaseActivity implements Constants
{
    private static final String INTENT_EXTRA_DATA_PERMISSION = "permission";
    private static final String CHANGE_STYLE_TEXT = "\'허용\'";

    private PermissionType mPermissionType;
    private Dialog mDialog;

    public enum PermissionType
    {
        READ_PHONE_STATE,
        ACCESS_FINE_LOCATION
    }

    public static Intent newInstance(Context context, PermissionType permission)
    {
        Intent intent = new Intent(context, PermissionManagerActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_PERMISSION, permission.name());

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish(RESULT_CANCELED);
            return;
        }

        mPermissionType = PermissionType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_PERMISSION));

        switch (mPermissionType)
        {
            case READ_PHONE_STATE:
                if (Util.isOverAPI23() == false)
                {
                    finish(RESULT_OK);
                    return;
                }

                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                {
                    finish(RESULT_OK);
                    return;
                }
                break;

            case ACCESS_FINE_LOCATION:
                if (DailyPreference.getInstance(this).isAgreeTermsOfLocation() == false)
                {
                    showTermsOfLocationDialog();
                } else
                {
                    if (Util.isOverAPI23() == false)
                    {
                        finish(RESULT_OK);
                        return;
                    } else
                    {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        {
                            finish(RESULT_OK);
                            return;
                        } else
                        {
                            processCheckPermission(mPermissionType);
                        }
                    }
                }
                break;
        }

        initLayout(mPermissionType);
    }

    private void initLayout(PermissionType permissionType)
    {
        setContentView(R.layout.activity_permission_manager);

        View snackDialogLayout = findViewById(R.id.snackDialogLayout);

        switch (permissionType)
        {
            case READ_PHONE_STATE:
            {
                snackDialogLayout.setVisibility(View.VISIBLE);
                showPermissionSnackPopup(snackDialogLayout, permissionType);
                break;
            }

            case ACCESS_FINE_LOCATION:
            {
                snackDialogLayout.setVisibility(View.GONE);
                break;
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
        }

        mDialog = null;

        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        finish(RESULT_CANCELED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case Constants.REQUEST_CODE_PERMISSIONS_READ_PHONE_STATE:
            case Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    finish(RESULT_OK);
                } else
                {
                    showPermissionGuidePopupBySetting(mPermissionType);
                }
                break;
            }

            default:
                finish(RESULT_CANCELED);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case Constants.REQUEST_CODE_PERMISSIONS_READ_PHONE_STATE:
            {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                {
                    finish(RESULT_OK);
                } else
                {
                    finish(RESULT_CANCELED);
                }
                break;
            }
            case Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION:
            {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    finish(RESULT_OK);
                } else
                {
                    finish(RESULT_CANCELED);
                }
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            default:
                finish(resultCode);
                break;
        }
    }

    private void finish(int resultCode)
    {
        setResult(resultCode);
        finish();
    }

    private void processCheckPermission(PermissionType permissionType)
    {
        String permission = null;

        switch (permissionType)
        {
            case READ_PHONE_STATE:
                permission = Manifest.permission.READ_PHONE_STATE;
                break;

            case ACCESS_FINE_LOCATION:
                permission = Manifest.permission.ACCESS_FINE_LOCATION;
                break;
        }

        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
        {
            if (shouldShowRequestPermissionRationale(permission) == false)
            {
                showPermissionGuidePopup(permissionType);
            } else
            {
                showPermissionGuidePopupBySetting(permissionType);
            }
        }
    }

    private void requestPermissions(PermissionType permissionType)
    {
        switch (permissionType)
        {
            case READ_PHONE_STATE:
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.REQUEST_CODE_PERMISSIONS_READ_PHONE_STATE);
                break;

            case ACCESS_FINE_LOCATION:
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION);
                break;
        }
    }

    private void startSettingDetailsActivity(PermissionType permissionType)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:com.twoheart.dailyhotel"));

        switch (permissionType)
        {
            case READ_PHONE_STATE:
                startActivityForResult(intent, Constants.REQUEST_CODE_PERMISSIONS_READ_PHONE_STATE);
                break;

            case ACCESS_FINE_LOCATION:
                startActivityForResult(intent, Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION);
                break;
        }
    }

    public void showPermissionSnackPopup(final View view, final PermissionType permissionType)
    {
        View confirmTextView = view.findViewById(R.id.confirmTextView);
        confirmTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                view.setVisibility(View.GONE);
                processCheckPermission(permissionType);
            }
        });
    }

    public void showPermissionGuidePopup(final PermissionType permissionType)
    {
        mDialog = new Dialog(this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(this).inflate(R.layout.view_permission_dialog_layout01, null, false);

        TextView messageTextView = (TextView) view.findViewById(R.id.messageTextView);
        TextView permissionTextView = (TextView) view.findViewById(R.id.permissionTextView);

        switch (permissionType)
        {
            case READ_PHONE_STATE:
                messageTextView.setText(changedSpannableColorBoldString(getString(R.string.message_guide_dialog_permission_read_phone_state), CHANGE_STYLE_TEXT));
                permissionTextView.setText(R.string.label_phone);
                permissionTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_call, 0, 0, 0);
                permissionTextView.setCompoundDrawablePadding(Util.dpToPx(this, 5));
                break;

            case ACCESS_FINE_LOCATION:
                messageTextView.setText(changedSpannableColorBoldString(getString(R.string.message_guide_dialog_permission_access_fine_location), CHANGE_STYLE_TEXT));
                permissionTextView.setText(R.string.label_location);
                permissionTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_map, 0, 0, 0);
                permissionTextView.setCompoundDrawablePadding(Util.dpToPx(this, 5));
                break;
        }

        View confirmTextView = view.findViewById(R.id.confirmTextView);

        confirmTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mDialog != null && mDialog.isShowing() == true)
                {
                    mDialog.dismiss();
                }

                requestPermissions(permissionType);
            }
        });

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                finish(RESULT_CANCELED);
            }
        });

        try
        {
            mDialog.setContentView(view);
            mDialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public void showPermissionGuidePopupBySetting(final PermissionType permissionType)
    {
        mDialog = new Dialog(this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(this).inflate(R.layout.view_permission_dialog_layout02, null, false);

        TextView messageTextView01 = (TextView) view.findViewById(R.id.messageTextView01);
        TextView messageTextView02 = (TextView) view.findViewById(R.id.messageTextView02);
        TextView messageTextView03 = (TextView) view.findViewById(R.id.messageTextView03);
        TextView permissionTextView = (TextView) view.findViewById(R.id.permissionTextView);

        switch (permissionType)
        {
            case READ_PHONE_STATE:
            {
                messageTextView01.setText(R.string.message_guide_dialog_denied_permission01);
                messageTextView02.setText(changedSpannableColorBoldString(getString(R.string.message_guide_dialog_denied_permission_read_phone_state), CHANGE_STYLE_TEXT));
                permissionTextView.setText(R.string.label_phone);
                permissionTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_call, 0, 0, 0);
                permissionTextView.setCompoundDrawablePadding(Util.dpToPx(this, 5));
                break;
            }

            case ACCESS_FINE_LOCATION:
            {
                messageTextView01.setText(R.string.message_guide_dialog_denied_permission01);
                messageTextView02.setText(changedSpannableColorBoldString(getString(R.string.message_guide_dialog_denied_permission_access_fine_location), CHANGE_STYLE_TEXT));
                permissionTextView.setText(R.string.label_location);
                permissionTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_map, 0, 0, 0);
                permissionTextView.setCompoundDrawablePadding(Util.dpToPx(this, 5));
                break;
            }
        }

        messageTextView03.setText(Html.fromHtml("<font color = '#323232'>" + getString(R.string.message_guide_dialog_permission02) + "</font>"//
            + ' ' + "<font color = '#929292'>" + getString(R.string.message_guide_dialog_permission03) + "</font>"));

        View positiveTextView = view.findViewById(R.id.positiveTextView);
        View negativeTextView = view.findViewById(R.id.negativeTextView);

        positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mDialog != null && mDialog.isShowing() == true)
                {
                    mDialog.dismiss();
                }

                startSettingDetailsActivity(permissionType);
            }
        });

        negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mDialog != null && mDialog.isShowing() == true)
                {
                    mDialog.dismiss();
                }

                finish(RESULT_CANCELED);
            }
        });

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                finish(RESULT_CANCELED);
            }
        });

        try
        {
            mDialog.setContentView(view);
            mDialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private SpannableStringBuilder changedSpannableColorBoldString(String text, String targetText)
    {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);

        if (Util.isTextEmpty(text) == true)
        {
            return spannableStringBuilder;
        }

        int startIndex = text.indexOf(targetText);
        int endIndex = startIndex + targetText.length();

        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.default_text_c900034)), //
            startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
            startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableStringBuilder;
    }

    protected void showTermsOfLocationDialog()
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_layout, null, false);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        // 상단
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.setText(getString(R.string.label_search_agree_termsoflocation));

        // 메시지
        TextView messageTextView = (TextView) dialogView.findViewById(R.id.messageTextView);

        String message = getString(R.string.message_search_agree_termsoflocation);

        int startIndex = message.lastIndexOf('\n') + 1;
        int endIndex = message.length();

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(message);
        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
            startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.setSpan(new UnderlineSpan(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        messageTextView.setText(spannableStringBuilder);
        messageTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(PermissionManagerActivity.this, LocationTermsActivity.class);
                startActivity(intent);

                //                AnalyticsManager.getInstance(PlaceSearchActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                //                    , AnalyticsManager.Action.LOCATION_AGREEMENT_POPPEDUP, AnalyticsManager.Label.TERMSOF_LOCATION, null);
            }
        });

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);
        View oneButtonLayout = buttonLayout.findViewById(R.id.oneButtonLayout);

        twoButtonLayout.setVisibility(View.GONE);
        oneButtonLayout.setVisibility(View.VISIBLE);

        TextView confirmTextView = (TextView) oneButtonLayout.findViewById(R.id.confirmTextView);

        confirmTextView.setText(R.string.label_search_agree_search_location);
        oneButtonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                DailyPreference.getInstance(PermissionManagerActivity.this).setTermsOfLocation(true);

                Intent intent = PermissionManagerActivity.newInstance(PermissionManagerActivity.this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);

                //                AnalyticsManager.getInstance(PlaceSearchActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                //                    , AnalyticsManager.Action.LOCATION_AGREEMENT_POPPEDUP, AnalyticsManager.Label.AGREE_AND_SEARCH, null);
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                finish(RESULT_CANCELED);

                AnalyticsManager.getInstance(PermissionManagerActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , AnalyticsManager.Action.LOCATION_AGREEMENT_POPPEDUP, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, null);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockUI();
            }
        });

        try
        {
            dialog.setContentView(dialogView);
            dialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

}

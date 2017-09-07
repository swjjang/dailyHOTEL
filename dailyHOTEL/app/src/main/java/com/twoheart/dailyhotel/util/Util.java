package com.twoheart.dailyhotel.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyToast;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.skp.Tmap.TMapTapi;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.databinding.DialogStayOutboundMapDataBinding;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Notice;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import net.simonvt.numberpicker.NumberPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;

public class Util implements Constants
{
    public static final String DEFAULT_COUNTRY_CODE = "대한민국\n+82";
    private static final String REMOVE_CHARACTER = "[\\-\\:\\+]";

    private static SoftReference<String> MEMORY_CLEAR;

    public static void initializeMemory()
    {
        MEMORY_CLEAR = new SoftReference("MEMORY");
    }

    public static boolean isMemoryClear()
    {
        return MEMORY_CLEAR == null;
    }

    public static void initializeFresco(Context context)
    {
        ImagePipelineConfig imagePipelineConfig;

        if (VersionUtils.isOverAPI11() == true && ScreenUtils.getScreenWidth(context) >= 720)
        {
            imagePipelineConfig = OkHttpImagePipelineConfigFactory//
                .newBuilder(context, new OkHttpClient()).build();
        } else
        {
            imagePipelineConfig = OkHttpImagePipelineConfigFactory//
                .newBuilder(context, new OkHttpClient())//
                .setBitmapsConfig(Config.RGB_565).build();
        }

        Fresco.initialize(context, imagePipelineConfig);
    }

    public static void requestImageResize(Context context, com.facebook.drawee.view.SimpleDraweeView simpleDraweeView, String imageUrl)
    {
        simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

        if (DailyTextUtils.isTextEmpty(imageUrl) == true)
        {
            simpleDraweeView.setImageURI((String) null);
            return;
        }

        if (ScreenUtils.getScreenWidth(context) >= 720)
        {
            simpleDraweeView.setImageURI(Uri.parse(imageUrl));
        } else
        {
            final int resizeWidth = 360, resizeHeight = 240;

            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl))//
                .setResizeOptions(new ResizeOptions(resizeWidth, resizeHeight))//
                .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()//
                .setOldController(simpleDraweeView.getController())//
                .setImageRequest(imageRequest)//
                .build();

            simpleDraweeView.setController(controller);
        }
    }

    public static String storeReleaseAddress()
    {
        if (Setting.getStore() == Setting.Stores.PLAY_STORE)
        {
            return URL_STORE_GOOGLE_DAILYHOTEL;
        } else
        {
            return URL_STORE_T_DAILYHOTEL;
        }
    }

    public static void setLocale(Context context, Locale locale)
    {
        try
        {
            Resources res = context.getResources();
            Configuration conf = res.getConfiguration();
            conf.locale = locale;
            res.updateConfiguration(conf, null);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public static void restartApp(Context context)
    {
        if (context == null)
        {
            return;
        }

        Intent intent = new Intent(context, LauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try
        {
            context.startActivity(intent);
        } catch (Exception e)
        {
            intent.addFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void restartExitApp(Context context)
    {
        if (context == null)
        {
            return;
        }

        Intent intent = new Intent(context, LauncherActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 300, pendingIntent);
        System.exit(0);
    }

    public static void finishOutOfMemory(BaseActivity activity)
    {
        if (activity == null || activity.isFinishing() == true)
        {
            return;
        }

        activity.showSimpleDialog(activity.getString(R.string.dialog_notice2), activity.getString(R.string.dialog_msg_outofmemory), activity.getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                System.exit(0);
            }
        }, null, false);
    }

    public static void finishOutOfMemory(Context context)
    {
        if (context == null)
        {
            return;
        }

        DailyToast.showToast(context, R.string.dialog_msg_outofmemory, Toast.LENGTH_LONG);
        System.exit(0);
    }

    public static boolean isUsedMultiTransition()
    {
        return VersionUtils.isOverAPI21() == true;
    }

    public static boolean isTelephonyEnabled(Context context)
    {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    public static boolean isGooglePlayServicesAvailable(Context context)
    {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(context);

        return result == ConnectionResult.SUCCESS;
    }

    public static boolean isInstallGooglePlayService(Context context)
    {
        if (Util.isGooglePlayServicesAvailable(context) == false)
        {
            return false;
        }

        boolean isInstalled;

        try
        {
            PackageManager packageManager = context.getPackageManager();

            ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.google.android.gms", 0);
            PackageInfo packageInfo = packageManager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_SIGNATURES);

            int version = context.getResources().getInteger(com.google.android.gms.R.integer.google_play_services_version);

            isInstalled = packageInfo.versionCode >= version;
        } catch (PackageManager.NameNotFoundException e)
        {
            isInstalled = false;
        }

        return isInstalled;
    }

    public static boolean installGooglePlayService(final BaseActivity activity)
    {
        if (isInstallGooglePlayService(activity) == true)
        {
            return true;
        } else
        {
            if (activity == null || activity.isFinishing() == true)
            {
                return false;
            }

            // set dialog message
            activity.showSimpleDialog(activity.getString(R.string.dialog_title_googleplayservice), activity.getString(R.string.dialog_msg_install_update_googleplayservice), //
                activity.getString(R.string.dialog_btn_text_install), activity.getString(R.string.dialog_btn_text_cancel), //
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        try
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            intent.setPackage("com.android.vending");
                            activity.startActivity(intent);
                        } catch (ActivityNotFoundException e)
                        {
                            try
                            {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                intent.setPackage("com.android.vending");
                                activity.startActivity(intent);
                            } catch (ActivityNotFoundException f)
                            {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                activity.startActivity(intent);
                            }
                        }
                    }
                }, null, true);


            return false;
        }
    }

    public interface OnGoogleCloudMessagingListener
    {
        void onResult(String registrationId);
    }

    public static void requestGoogleCloudMessaging(final Context context, final OnGoogleCloudMessagingListener listener)
    {
        if (Util.isGooglePlayServicesAvailable(context) == false)
        {
            if (listener != null)
            {
                listener.onResult(null);
            }
            return;
        }

        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                String registrationId = null;

                try
                {
                    GoogleCloudMessaging instance = GoogleCloudMessaging.getInstance(context);

                    registrationId = instance.register(GCM_PROJECT_NUMBER);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }

                return registrationId;
            }

            @Override
            protected void onPostExecute(String registrationId)
            {
                if (listener != null)
                {
                    listener.onResult(registrationId);
                }
            }
        }.execute();
    }

    public static String getCountryNameNCode(Context context)
    {
        try
        {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String countryIsoCode = telephonyManager.getSimCountryIso();

            if (DailyTextUtils.isTextEmpty(countryIsoCode) == true)
            {
                Locale currentLocale = context.getResources().getConfiguration().locale;

                countryIsoCode = currentLocale.getCountry();
            }

            if (DailyTextUtils.isTextEmpty(countryIsoCode) == false)
            {
                CountryCodeNumber countryCodeNumber = new CountryCodeNumber();

                return countryCodeNumber.getCountryNameNCode(countryIsoCode);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return DEFAULT_COUNTRY_CODE;
    }

    private static String getValidateCountry(String code)
    {
        CountryCodeNumber countryCodeNumber = new CountryCodeNumber();

        return countryCodeNumber.getCountry(code);
    }

    private static boolean isValidateCountryCode(String code)
    {
        CountryCodeNumber countryCodeNumber = new CountryCodeNumber();

        return countryCodeNumber.hasCountryCode(code);
    }

    public static boolean isValidatePhoneNumber(String phoneNumber)
    {
        if (DailyTextUtils.isTextEmpty(phoneNumber) == true)
        {
            return false;
        }

        if (phoneNumber.charAt(0) == '+')
        {
            String globalPhoneNumber = phoneNumber.replaceFirst("\\s", "^");
            String[] text = globalPhoneNumber.split("\\^");

            // 국제 전화번호 존재 여부 확인
            if (isValidateCountryCode(text[0]) == false || text.length < 2)
            {
                return false;
            }

            text[1] = text[1].replace("-", "");

            if ("+82".equalsIgnoreCase(text[0]) == true)
            {
                if (text[1].startsWith("(0)10") || text[1].startsWith("(0)11") || text[1].startsWith("(0)16") //
                    || text[1].startsWith("(0)17") || text[1].startsWith("(0)18") || text[1].startsWith("(0)19"))
                {
                    if (android.text.TextUtils.isDigitsOnly(text[1].substring(5)) == true)
                    {
                        int length = text[1].length();
                        if (length == 12 || length == 13)
                        {
                            return (Util.isExistMobileNumber(phoneNumber) == false);
                        }
                    }
                }
            } else
            {
                text[1] = text[1].replaceAll("\\(|\\)|\\s|\\-", "");

                // 국내가 아니면 숫자만 있는지 검증한다 7자리 ~ 15자리
                int length = text[1].length();
                if (length >= 7 && length <= 15)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * +82 (0)1012345678 (한국 핸드폰 전화번호만 가능)
     *
     * @param mobileNumber
     * @return
     */
    public static boolean isExistMobileNumber(String mobileNumber)
    {
        if (DailyTextUtils.isTextEmpty(mobileNumber) == true || mobileNumber.startsWith("+82") == false)
        {
            return false;
        }

        mobileNumber = mobileNumber.replaceFirst("\\s", "^");
        String[] number = mobileNumber.split("\\^");

        if (number.length < 2)
        {
            return false;
        }

        number[1] = number[1].replaceAll("\\(|\\)|-", "");

        String mobile01 = number[1].substring(0, 3);

        int middle = number[1].length() == 10 ? 6 : 7;
        String mobile02 = number[1].substring(3, middle);
        String mobile03 = number[1].substring(middle);

        final String PATTERN = "010|011|016|017|018|019{1}";
        final String PATTERN_3 = "111|222|333|444|555|666|777|888|999|000|012|123|234|345|456|567|678|789|987|876|765|654|543|432|321|210{1}";
        final String PATTERN_4 = "1111|2222|3333|4444|5555|6666|7777|8888|9999|0000|0123|1234|2345|3456|4567|5678|6789|9876|8765|7654|6543|5432|4321|3210{1}";

        Pattern pattern01 = Pattern.compile(PATTERN);
        Pattern pattern02 = mobile02.length() == 3 ? Pattern.compile(PATTERN_3) : Pattern.compile(PATTERN_4);
        Pattern pattern03 = Pattern.compile(PATTERN_4);

        return pattern01.matcher(mobile01).matches() == false && pattern02.matcher(mobile02).matches() && pattern03.matcher(mobile03).matches();
    }

    public static String[] getValidatePhoneNumber(String phoneNumber)
    {
        if (DailyTextUtils.isTextEmpty(phoneNumber) == true)
        {
            return null;
        }

        if (phoneNumber.charAt(0) == '+')
        {
            String globalPhoneNumber = phoneNumber.replaceFirst("\\s", "^");
            String[] text = globalPhoneNumber.split("\\^");
            String countryCode = getValidateCountry(text[0]);

            // 국제 전화번호 존재 여부 확인
            if (DailyTextUtils.isTextEmpty(countryCode) == true)
            {
                return null;
            }

            if ("+82".equalsIgnoreCase(text[0]) == true)
            {
                if (text[1].startsWith("(0)10") || text[1].startsWith("(0)11") || text[1].startsWith("(0)16") //
                    || text[1].startsWith("(0)17") || text[1].startsWith("(0)18") || text[1].startsWith("(0)19"))
                {
                    if (android.text.TextUtils.isDigitsOnly(text[1].substring(5)) == true)
                    {
                        int length = text[1].length();
                        if (length == 12 || length == 13)
                        {
                            text[0] = DEFAULT_COUNTRY_CODE;
                            return text;
                        }
                    }
                }
            } else
            {
                text[0] = countryCode + "\n" + text[0];
                return text;
            }
        } else
        {
            String text = phoneNumber.replace("-", "").replace(" ", "");

            if (text.startsWith("010") || text.startsWith("011") || text.startsWith("016") //
                || text.startsWith("017") || text.startsWith("018") || text.startsWith("019"))
            {
                if (android.text.TextUtils.isDigitsOnly(text) == true)
                {
                    int length = text.length();
                    if (length == 10 || length == 11)
                    {
                        return new String[]{DEFAULT_COUNTRY_CODE, text};
                    }
                }
            }
        }

        return null;
    }

    public static Dialog showDatePickerDialog(BaseActivity baseActivity, String titleText, final String[] values, String selectValue, String positive //
        , final View.OnClickListener positiveListener)
    {
        if (baseActivity == null || baseActivity.isFinishing() == true)
        {
            return null;
        }

        final Dialog dialog = new Dialog(baseActivity);

        LayoutInflater layoutInflater = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_pickerdialog_layout, null, false);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        // 상단
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);

        if (DailyTextUtils.isTextEmpty(titleText) == true)
        {
            titleTextView.setText(baseActivity.getString(R.string.dialog_notice2));
        } else
        {
            titleTextView.setText(titleText);
        }

        // 메시지
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        numberPicker.setFocusable(true);
        numberPicker.setFocusableInTouchMode(true);
        numberPicker.setDisplayedValues(values);
        numberPicker.setTextTypeface(FontManager.getInstance(baseActivity).getRegularTypeface());

        for (int i = 0; i < values.length; i++)
        {
            if (values[i].equalsIgnoreCase(selectValue) == true)
            {
                numberPicker.setValue(i);
                break;
            }
        }

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View oneButtonLayout = buttonLayout.findViewById(R.id.oneButtonLayout);
        oneButtonLayout.setVisibility(View.VISIBLE);

        TextView confirmTextView = (TextView) oneButtonLayout.findViewById(R.id.confirmTextView);

        confirmTextView.setText(positive);
        confirmTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                if (positiveListener != null)
                {
                    v.setTag(numberPicker.getValue());
                    positiveListener.onClick(v);
                }
            }
        });

        dialog.setContentView(dialogView);

        if (baseActivity.isFinishing() == true)
        {
            return null;
        }

        try
        {
            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(baseActivity, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);

            return dialog;
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return null;
    }

    public static String addHyphenMobileNumber(Context context, String mobileNumber)
    {
        if (DailyTextUtils.isTextEmpty(mobileNumber) == true)
        {
            return "";
        }

        mobileNumber = mobileNumber.replace("-", "");

        if (Util.isValidatePhoneNumber(mobileNumber) == true)
        {
            String[] countryCode = Util.getValidatePhoneNumber(mobileNumber);

            TextView textView = new TextView(context);

            if (countryCode != null && Util.DEFAULT_COUNTRY_CODE.equals(countryCode[0]) == true)
            {
                textView.addTextChangedListener(new PhoneNumberKoreaFormattingTextWatcher(context));
            } else
            {
                textView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
            }

            textView.setText(countryCode[1].replaceAll("\\(|\\)|-|\\s", ""));
            return countryCode[0].substring(countryCode[0].indexOf('\n') + 1) + " " + textView.getText().toString();
        } else
        {
            return mobileNumber;
        }
    }

    public static boolean isInstalledPackage(Context context, String packageName, Intent intent)
    {
        if (context == null || DailyTextUtils.isTextEmpty(packageName) == true || intent == null)
        {
            return false;
        }

        try
        {
            PackageManager packageManager = context.getPackageManager();
            ResolveInfo resolveInfoMarket = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

            return (packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES) != null && resolveInfoMarket != null);
        } catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    public static void shareDaumMap(Activity activity, String latitude, String longitude)
    {
        if (activity == null || DailyTextUtils.isTextEmpty(latitude, longitude) == true)
        {
            return;
        }

        final String packageName = "net.daum.android.map";
        String url = String.format(Locale.KOREA, "daummaps://look?p=%s,%s", latitude, longitude);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        if (isInstalledPackage(activity, packageName, intent) == true)
        {
            activity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EXTERNAL_MAP);
        } else
        {
            installPackage(activity, packageName);
        }
    }

    public static void shareNaverMap(Activity activity, String name, String latitude, String longitude)
    {
        if (activity == null || DailyTextUtils.isTextEmpty(latitude, longitude) == true)
        {
            return;
        }

        try
        {
            final String packageName = "com.nhn.android.nmap";
            String url = String.format(Locale.KOREA, "navermaps://?menu=location&lat=%s&lng=%s&title=%s", latitude, longitude, URLEncoder.encode(name, "UTF-8"));

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

            if (isInstalledPackage(activity, packageName, intent) == true)
            {
                activity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EXTERNAL_MAP);
            } else
            {
                installPackage(activity, packageName);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public static void shareKakaoNavi(Activity activity, String name, String latitude, String longitude)
    {
        if (activity == null || DailyTextUtils.isTextEmpty(latitude) == true || DailyTextUtils.isTextEmpty(longitude) == true)
        {
            return;
        }

        try
        {
            final String packageName = "com.locnall.KimGiSa";
            String url = String.format(Locale.KOREA, "kakaonavi://navigate?name=%s&coord_type=wgs84&x=%s&y=%s&rpoption=1&key=%s", URLEncoder.encode(name, "UTF-8"), longitude, latitude, Crypto.getUrlDecoderEx(Constants.KAKAO_NAVI_KEY));

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

            if (isInstalledPackage(activity, packageName, intent) == true)
            {
                activity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EXTERNAL_MAP);
            } else
            {
                installPackage(activity, packageName);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public static void shareGoogleMap(Activity activity, String placeName, String latitude, String longitude)
    {
        if (activity == null || DailyTextUtils.isTextEmpty(latitude, longitude) == true)
        {
            return;
        }

        try
        {
            final String packageName = "com.google.android.apps.maps";
            //            String url = String.format("http://maps.google.com/maps?q=%s&ll=%s,%s&z=14", placeName, latitude, longitude);
            String url = String.format(Locale.KOREA, "https://maps.google.com/maps?q=loc:%s,%s(%s)&z=14", latitude, longitude, URLEncoder.encode(placeName, "UTF-8"));

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setPackage(packageName);

            if (isInstalledPackage(activity, packageName, intent) == true)
            {
                activity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EXTERNAL_MAP);
            } else
            {
                installPackage(activity, packageName);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public static void shareTMapNavi(final Activity activity, final String placeName, final float latitude, final float longitude)
    {
        if (activity == null)
        {
            return;
        }

        if (latitude == 0 || longitude == 0)
        {
            showFailedTMapNaviDialog(activity);
            return;
        }

        try
        {
            final TMapTapi tmapTapi = new TMapTapi(activity);

            if (tmapTapi == null)
            {
                showFailedTMapNaviDialog(activity);
                return;
            }

            if (DailyHotel.isSuccessTMapAuth() == false)
            {
                tmapTapi.setSKPMapAuthentication(Crypto.getUrlDecoderEx(Constants.TMAP_NAVI_KEY));
                tmapTapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback()
                {
                    @Override
                    public void SKPMapApikeySucceed()
                    {
                        //                    ExLog.d("TMap : SKPMapApikeySucceed");
                        if (activity != null)
                        {
                            activity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    DailyHotel.setIsSuccessTMapAuth(true);
                                    openTMapNavi(activity, tmapTapi, placeName, latitude, longitude);
                                }
                            });
                        }
                    }

                    @Override
                    public void SKPMapApikeyFailed(String s)
                    {
                        if (activity != null)
                        {
                            activity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    DailyHotel.setIsSuccessTMapAuth(false);
                                    showFailedTMapNaviDialog(activity);
                                }
                            });
                        }
                    }

                    @Override
                    public void SKPMapBizAppIdSucceed()
                    {
                        // do nothing 삭제된 API - Callback Listener 오류로 추가
                    }

                    @Override
                    public void SKPMapBizAppIdFailed(String s)
                    {
                        // do nothing 삭제된 API - Callback Listener 오류로 추가
                    }
                });
            } else
            {
                openTMapNavi(activity, tmapTapi, placeName, latitude, longitude);
            }
        } catch (Exception e)
        {
            String logMessage;
            logMessage = activity != null ? activity.getLocalClassName() : "Unknown activity";
            logMessage = logMessage + " : " + placeName + " : " + latitude + " : " + longitude;

            Crashlytics.log(logMessage);

            if (activity != null)
            {
                showFailedTMapNaviDialog(activity);
            }
        }
    }

    static void openTMapNavi(final Activity activity, TMapTapi tmapTapi, String placeName, float latitude, float longitude)
    {
        if (tmapTapi.isTmapApplicationInstalled() == true)
        {
            //            ExLog.d("TMap placeName : " + placeName + " , latitude : " + latitude + " , longitude : " + longitude);
            tmapTapi.invokeRoute(placeName, longitude, latitude);
        } else
        {
            ArrayList<String> downUrlList = tmapTapi.getTMapDownUrl();
            if (downUrlList == null || downUrlList.size() == 0)
            {
                showFailedTMapNaviDialog(activity);
            } else
            {
                Uri marketUri = null;

                boolean isCheck = isSktNetwork(activity) == false;

                if (downUrlList.size() > 1)
                {
                    for (String url : downUrlList)
                    {
                        if (url.contains("play.google.com") == isCheck)
                        {
                            marketUri = Uri.parse(url);
                            break;
                        }
                    }
                }

                if (Uri.EMPTY.equals(marketUri))
                {
                    marketUri = Uri.parse(downUrlList.get(0));
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, marketUri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                ResolveInfo resolveInfo = activity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (resolveInfo != null)
                {
                    activity.startActivity(intent);
                } else
                {
                    showFailedTMapNaviDialog(activity);
                }
            }
        }
    }

    static void showFailedTMapNaviDialog(final Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_layout, null, false);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        // 상단
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.setText(activity.getString(R.string.dialog_notice2));

        // 메시지
        TextView messageTextView = (TextView) dialogView.findViewById(R.id.messageTextView);
        messageTextView.setText(activity.getString(R.string.message_tmap_navi_failed));

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);
        View oneButtonLayout = buttonLayout.findViewById(R.id.oneButtonLayout);

        twoButtonLayout.setVisibility(View.GONE);
        oneButtonLayout.setVisibility(View.VISIBLE);

        TextView confirmTextView = (TextView) oneButtonLayout.findViewById(R.id.confirmTextView);

        confirmTextView.setText(activity.getString(R.string.dialog_btn_text_confirm));
        oneButtonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
            }
        });

        try
        {
            dialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(activity, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public static void showShareMapDialog(final Activity baseActivity, final String placeName//
        , final double latitude, final double longitude, boolean isOverseas//
        , final String gaCategory, final String gaAction, final String gaLabel//
        , DialogInterface.OnDismissListener onDismissListener)
    {
        if (baseActivity == null || baseActivity.isFinishing() == true)
        {
            return;
        }

        View dialogView;
        final Dialog dialog = new Dialog(baseActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        LayoutInflater layoutInflater = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (isOverseas == false)
        {
            dialogView = layoutInflater.inflate(R.layout.view_searchmap_dialog_layout01, null, false);

            // 버튼
            View kakaoMapLayoutLayout = dialogView.findViewById(R.id.kakaoMapLayout);
            View naverMapLayout = dialogView.findViewById(R.id.naverMapLayout);
            View googleMapLayout = dialogView.findViewById(R.id.googleMapLayout);
            TextView tmapNaviLayout = (TextView) dialogView.findViewById(R.id.tmapNaviLayout);
            View kakaoNaviLayout = dialogView.findViewById(R.id.kakaoNaviLayout);

            int tmapIconResId;
            if (isSktNetwork(baseActivity) == true)
            {
                tmapIconResId = R.drawable.ic_tmap_red;
            } else
            {
                tmapIconResId = R.drawable.ic_tmap_green;
            }

            tmapNaviLayout.setCompoundDrawablesWithIntrinsicBounds(0, tmapIconResId, 0, 0);

            kakaoMapLayoutLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (dialog.isShowing() == true)
                    {
                        dialog.dismiss();
                    }

                    Util.shareDaumMap(baseActivity, Double.toString(latitude), Double.toString(longitude));

                    if (DailyTextUtils.isTextEmpty(gaCategory) == false)
                    {
                        if (DailyTextUtils.isTextEmpty(gaLabel) == true)
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "Daum", null);
                        } else
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "Daum-" + gaLabel, null);
                        }
                    }
                }
            });

            naverMapLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (dialog.isShowing() == true)
                    {
                        dialog.dismiss();
                    }

                    Util.shareNaverMap(baseActivity, placeName, Double.toString(latitude), Double.toString(longitude));

                    if (DailyTextUtils.isTextEmpty(gaCategory) == false)
                    {
                        if (DailyTextUtils.isTextEmpty(gaLabel) == true)
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "Naver", null);
                        } else
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "Naver-" + gaLabel, null);
                        }
                    }
                }
            });

            googleMapLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (dialog.isShowing() == true)
                    {
                        dialog.dismiss();
                    }

                    Util.shareGoogleMap(baseActivity, placeName, Double.toString(latitude), Double.toString(longitude));

                    if (DailyTextUtils.isTextEmpty(gaCategory) == false)
                    {
                        if (DailyTextUtils.isTextEmpty(gaLabel) == true)
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "Google", null);
                        } else
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "Google-" + gaLabel, null);
                        }
                    }
                }
            });

            tmapNaviLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (dialog.isShowing() == true)
                    {
                        dialog.dismiss();
                    }

                    Util.shareTMapNavi(baseActivity, placeName, (float) latitude, (float) longitude);

                    if (DailyTextUtils.isTextEmpty(gaCategory) == false)
                    {
                        if (DailyTextUtils.isTextEmpty(gaLabel) == true)
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "TmapNavi", null);
                        } else
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "TmapNavi-" + gaLabel, null);
                        }
                    }
                }
            });

            kakaoNaviLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (dialog.isShowing() == true)
                    {
                        dialog.dismiss();
                    }

                    Util.shareKakaoNavi(baseActivity, placeName, Double.toString(latitude), Double.toString(longitude));

                    if (DailyTextUtils.isTextEmpty(gaCategory) == false)
                    {
                        if (DailyTextUtils.isTextEmpty(gaLabel) == true)
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "kakaoNavi", null);
                        } else
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "kakaoNavi-" + gaLabel, null);
                        }
                    }
                }
            });
        } else
        {
            DialogStayOutboundMapDataBinding dataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_navigator_outbound_data, null, false);

            dialogView = dataBinding.getRoot();

            dataBinding.googleMapTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (dialog.isShowing() == true)
                    {
                        dialog.dismiss();
                    }

                    Util.shareGoogleMap(baseActivity, placeName, Double.toString(latitude), Double.toString(longitude));

                    if (DailyTextUtils.isTextEmpty(gaCategory) == false)
                    {
                        if (DailyTextUtils.isTextEmpty(gaLabel) == true)
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "Google", null);
                        } else
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "Google-" + gaLabel, null);
                        }
                    }
                }
            });
        }

        dialog.setOnDismissListener(onDismissListener);

        try
        {
            dialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(baseActivity, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public static void installPackage(Context context, String packageName)
    {
        if (context == null || DailyTextUtils.isTextEmpty(packageName) == true)
        {
            return;
        }

        Intent intentMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
        intentMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ResolveInfo resolveInfoMarket = context.getPackageManager().resolveActivity(intentMarket, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfoMarket != null)
        {
            context.startActivity(intentMarket);
        } else
        {
            Intent intentWeb = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName));
            intentWeb.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            ResolveInfo resolveInfoWeb = context.getPackageManager().resolveActivity(intentWeb, PackageManager.MATCH_DEFAULT_ONLY);

            if (resolveInfoWeb != null)
            {
                context.startActivity(intentWeb);
            } else
            {
                DailyToast.showToast(context, R.string.toast_message_failed_install, Toast.LENGTH_SHORT);
            }
        }
    }

    public static String makeIntroImageFileName(String version)
    {
        if (DailyTextUtils.isTextEmpty(version) == true)
        {
            return "daily_intro";
        }

        String[] versions = version.split("\\+");
        return versions[0].replaceAll(REMOVE_CHARACTER, "");
    }

    public static String makeImageFileName(String version)
    {
        if (DailyTextUtils.isTextEmpty(version) == true)
        {
            return "daily_image";
        }

        String[] versions = version.split("\\+");
        return "image" + "_" + versions[0].replaceAll(REMOVE_CHARACTER, "");
    }

    public static String makeStampStayThankYpuImageFileName(String version)
    {
        if (DailyTextUtils.isTextEmpty(version) == true)
        {
            return "daily_stamp_stay";
        }

        String[] versions = version.split("\\+");
        return "stamp_stay_image" + "_" + versions[0].replaceAll(REMOVE_CHARACTER, "");
    }

    public static boolean isSktNetwork(Context context)
    {
        if (context == null)
        {
            return false;
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return "SKTelecom".equalsIgnoreCase(telephonyManager.getNetworkOperatorName()) != false;

    }

    public static boolean hasNoticeNewList(Context context)
    {
        String value = DailyPreference.getInstance(context).getNoticeNewList();

        return (DailyTextUtils.isTextEmpty(value) == false);
    }

    public static ArrayList<Notice> checkNoticeNewList(Context context, ArrayList<Notice> noticeList)
    {
        if (context == null || noticeList == null || noticeList.size() == 0)
        {
            return noticeList;
        }

        final char SEPARATE = '|';

        String removeValue = DailyPreference.getInstance(context).getNoticeNewRemoveList();
        String indexString;

        if (DailyTextUtils.isTextEmpty(removeValue) == true)
        {
            removeValue = "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (Notice notice : noticeList)
        {
            indexString = Integer.toString(notice.index) + SEPARATE;

            if (removeValue.indexOf(indexString) >= 0)
            {
                notice.isNew = false;
            } else
            {
                notice.isNew = true;
                stringBuilder.append(indexString);
            }
        }

        DailyPreference.getInstance(context).setNoticeNewList(stringBuilder.toString());

        return noticeList;
    }

    public static void removeNoticeNewList(Context context, int index)
    {
        final char SEPARATE = '|';

        String newValue = DailyPreference.getInstance(context).getNoticeNewList();
        String removeValue = DailyPreference.getInstance(context).getNoticeNewRemoveList();

        String indexString = Integer.toString(index) + SEPARATE;

        if (DailyTextUtils.isTextEmpty(newValue) == false)
        {
            DailyPreference.getInstance(context).setNoticeNewList(newValue.replace(indexString, ""));
        }

        if (DailyTextUtils.isTextEmpty(removeValue) == true)
        {
            DailyPreference.getInstance(context).setNoticeNewRemoveList(indexString);
        } else
        {
            DailyPreference.getInstance(context).setNoticeNewRemoveList(removeValue + indexString);
        }
    }
    //
    //    public static Bitmap loadBitmapFromView(View v, int width, int height)
    //    {
    //        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    //        Canvas c = new Canvas(b);
    //        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
    //        v.draw(c);
    //        return b;
    //    }

    /**
     * 대지역 이름 반환
     *
     * @param province
     * @return
     */
    public static String getRealProvinceName(Province province)
    {
        String realProvinceName;
        try
        {
            if (province instanceof Area)
            {
                Area area = (Area) province;
                realProvinceName = area.getProvince().name;
            } else
            {
                realProvinceName = province.name;
            }
        } catch (Exception e)
        {
            realProvinceName = null;
        }

        return realProvinceName;
    }

    /**
     * 신규 저장용 지역 JSONObject
     *
     * @param province
     * @return
     */
    public static JSONObject getDailyRegionJSONObject(Province province)
    {
        JSONObject jsonObject;
        try
        {
            String areaName;
            String provinceName;
            boolean isOverSeas;

            if (province instanceof Area)
            {
                Area area = (Area) province;

                areaName = area.name;
                provinceName = area.getProvince().name;
                isOverSeas = area.getProvince().isOverseas;
            } else
            {
                provinceName = province.name;
                areaName = "";
                isOverSeas = province.isOverseas;
            }

            jsonObject = getDailyRegionJSONObject(provinceName, areaName, isOverSeas);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            jsonObject = null;
        }

        return jsonObject;
    }

    /**
     * 신규 저장용 지역 JSONObject
     *
     * @param provinceName
     * @param areaName
     * @param isOverSeas
     * @return
     */
    public static JSONObject getDailyRegionJSONObject(String provinceName, String areaName, boolean isOverSeas)
    {
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject();
            jsonObject.put(Constants.JSON_KEY_PROVINCE_NAME, DailyTextUtils.isTextEmpty(provinceName) ? "" : provinceName);
            jsonObject.put(Constants.JSON_KEY_AREA_NAME, DailyTextUtils.isTextEmpty(areaName) ? "" : areaName);
            jsonObject.put(Constants.JSON_KEY_IS_OVER_SEAS, isOverSeas);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            jsonObject = null;
        }

        return jsonObject;
    }

    public static String getDailyAreaString(JSONObject jsonObject)
    {
        if (jsonObject == null)
        {
            return "";
        }

        String areaName;
        try
        {
            areaName = jsonObject.getString(Constants.JSON_KEY_AREA_NAME);
        } catch (JSONException e)
        {
            ExLog.d(e.toString());
            areaName = "";
        }

        return areaName;
    }

    public static String getDailyProvinceString(JSONObject jsonObject)
    {
        if (jsonObject == null)
        {
            return "";
        }

        String provinceName;
        try
        {
            provinceName = jsonObject.getString(Constants.JSON_KEY_PROVINCE_NAME);
        } catch (JSONException e)
        {
            ExLog.d(e.toString());
            provinceName = "";
        }

        return provinceName;
    }

    public static boolean isDailyOverSeas(JSONObject jsonObject)
    {
        if (jsonObject == null)
        {
            return false;
        }

        boolean isOverSeas;
        try
        {
            isOverSeas = jsonObject.getBoolean(Constants.JSON_KEY_IS_OVER_SEAS);
        } catch (JSONException e)
        {
            ExLog.d(e.toString());
            isOverSeas = false;
        }

        return isOverSeas;
    }

    public static boolean isSameProvinceName(Province province, JSONObject saveProvinceJsonObject)
    {
        if (province == null && saveProvinceJsonObject == null)
        {
            return true;
        } else if (province == null || saveProvinceJsonObject == null)
        {
            return false;
        }

        String saveProvinceName = getDailyProvinceString(saveProvinceJsonObject);
        String saveAreaName = getDailyAreaString(saveProvinceJsonObject);

        boolean isSameProvince = false;
        try
        {
            String regionName = province.name;

            if (province instanceof Area)
            {
                isSameProvince = regionName.equalsIgnoreCase(saveAreaName);
            } else
            {
                isSameProvince = regionName.equalsIgnoreCase(saveProvinceName);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return isSameProvince;
    }

    public static boolean isAvailableNetwork(Context context)
    {
        boolean result = false;

        AvailableNetwork availableNetwork = AvailableNetwork.getInstance();

        switch (availableNetwork.getNetType(context))
        {
            case AvailableNetwork.NET_TYPE_WIFI:
                // WIFI 연결상태
                result = true;
                break;
            case AvailableNetwork.NET_TYPE_3G:
                // 3G 혹은 LTE연결 상태
                result = true;
                break;
            case AvailableNetwork.NET_TYPE_NONE:
                result = false;
                break;
        }
        return result;
    }

    public static void sendSms(Activity activity, String message)
    {
        if (VersionUtils.isOverAPI19() == true)
        {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(activity);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.putExtra("subject", activity.getString(R.string.label_daily_hotel));

            if (defaultSmsPackageName != null)
            {
                intent.setPackage(defaultSmsPackageName);
            }

            activity.startActivity(intent);
        } else
        {
            try
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.putExtra("subject", activity.getString(R.string.label_daily_hotel));
                intent.putExtra("sms_body", message);
                intent.addFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType("vnd.android-dir/mms-sms");
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e)
            {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.putExtra("subject", activity.getString(R.string.label_daily_hotel));
                intent.putExtra("sms_body", message);
                intent.addFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType("vnd.android-dir/mms-sms");
                activity.startActivity(intent);
            }
        }
    }

    public static boolean verifyTrueView(Context context)
    {
        final int SUPPORT_MIN_VERSION = 37;

        WebView webView = new WebView(context);
        String webViewVersion = webView.getSettings().getUserAgentString();

        try
        {
            int startIndex = webViewVersion.indexOf("Chrome/") + "Chrome/".length();
            int endIndex = webViewVersion.indexOf(" ", startIndex);

            String version = webViewVersion.substring(startIndex, endIndex).trim();
            String[] versions = version.split("\\.");

            if (Integer.parseInt(versions[0]) >= SUPPORT_MIN_VERSION)
            {
                return true;
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return false;
    }

    public static boolean supportPreview(Context context)
    {
        // 화면 사이즈가 작은 단말에서 지원하지 않는다. 실제 새로 길이는 안테나 영역과 하단에 소프트바 버튼 영역이
        // 있기 때문에 1024x720에서도 높이는 그보다 작다.
        if (ScreenUtils.getScreenHeight(context) > 800)
        {
            return true;
        }

        return false;
    }

    public static Bundle getClassPublicFieldsBundle(Class bundleClass, Object objectClass) throws Exception
    {
        if (bundleClass == null || objectClass == null)
        {
            return null;
        }

        Bundle bundle = new Bundle();
        String name;
        Object filedObject;
        int modifier;

        for (Field field : bundleClass.getFields())
        {
            modifier = field.getModifiers();

            if (Modifier.isStatic(modifier) == true || Modifier.isFinal(modifier) == true || Modifier.isPublic(modifier) == false)
            {
                continue;
            }

            name = field.getName();
            filedObject = field.get(objectClass);

            if (filedObject instanceof String)
            {
                bundle.putString(name, (String) filedObject);
            } else if (filedObject instanceof Integer)
            {
                bundle.putInt(name, (Integer) filedObject);
            } else if (filedObject instanceof Boolean)
            {
                bundle.putBoolean(name, (Boolean) filedObject);
            }
        }

        return bundle;
    }

    public static Object setClassPublicFieldsBundle(Class bundleClass, Bundle bundle) throws Exception
    {
        if (bundle == null || bundleClass == null)
        {
            return null;
        }

        Object objectClass = bundleClass.newInstance();
        Object filedObject;
        int modifier;

        for (Field field : bundleClass.getFields())
        {
            modifier = field.getModifiers();

            if (Modifier.isStatic(modifier) == true || Modifier.isFinal(modifier) == true || Modifier.isPublic(modifier) == false)
            {
                continue;
            }

            field.setAccessible(true);
            field.set(objectClass, bundle.get(field.getName()));
        }

        return objectClass;
    }
}
package com.twoheart.dailyhotel.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.soloader.SoLoaderShim;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.FontManager;

import net.simonvt.numberpicker.NumberPicker;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;

public class Util implements Constants
{
    public static final String DEFAULT_COUNTRY_CODE = "대한민국\n+82";
    private static final String REMOVE_CHARACTER = "[\\-\\:\\+]";

    private static String MEMORY_CLEAR;

    static
    {
        try
        {
            SoLoaderShim.loadLibrary("webp");
        } catch (UnsatisfiedLinkError e)
        {
            ExLog.e(e.toString());
        }
    }

    public static void initializeMemory()
    {
        MEMORY_CLEAR = "MEMORY";
    }

    public static boolean isMemoryClear()
    {
        return Util.isTextEmpty(MEMORY_CLEAR);
    }

    public static void initializeFresco(Context context)
    {
        ImagePipelineConfig imagePipelineConfig;

        if (Util.isOverAPI11() == true && Util.getLCDWidth(context) >= 720)
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

        if (Util.isTextEmpty(imageUrl) == true)
        {
            simpleDraweeView.setImageURI((String) null);
            return;
        }

        if (Util.getLCDWidth(context) >= 720)
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

    public static int dpToPx(Context context, double dp)
    {
        if (context == null)
        {
            context = DailyHotel.getGlobalApplicationContext();
        }

        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static String storeReleaseAddress()
    {
        if (RELEASE_STORE == Stores.PLAY_STORE || RELEASE_STORE == Stores.N_STORE)
        {
            return URL_STORE_GOOGLE_DAILYHOTEL;
        } else
        {
            return URL_STORE_T_DAILYHOTEL;
        }
    }

    public static void setLocale(Context context, Locale locale)
    {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
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

    public static String getDeviceId(Context context)
    {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();

        // // 참고로 태블릿, 웨어러블 기기에서는 값이 null이 나온다.
        if (Util.isTelephonyEnabled(context) == false && deviceId == null)
        {
            return getDeviceUUID(context);
        }

        return deviceId;
    }

    public static String getDeviceUUID(Context context)
    {
        UUID uuid = null;

        final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        try
        {
            if ("9774d56d682e549c".equals(androidId) == false)
            {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
            } else
            {
                final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
            }
        } catch (UnsupportedEncodingException e)
        {
            ExLog.d(e.toString());
        }

        if (uuid != null)
        {
            return uuid.toString();
        } else
        {
            return null;
        }
    }

    public static int getLCDWidth(Context context)
    {
        if (context == null)
        {
            return 0;
        }

        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getLCDHeight(Context context)
    {
        if (context == null)
        {
            return 0;
        }

        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static boolean isNameCharacter(String text)
    {
        boolean result = false;

        if (Util.isTextEmpty(text) == false)
        {
            result = Pattern.matches("^[a-zA-Z\\s.'-]+$", text);
        }

        return result;
    }

    public static boolean isOverAPI11()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean isOverAPI21()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isOverAPI12()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean isOverAPI16()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean isOverAPI15()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    public static boolean isOverAPI19()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean isOverAPI23()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isTelephonyEnabled(Context context)
    {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    public static boolean isTextEmpty(String... texts)
    {
        if (texts == null)
        {
            return true;
        }

        for (String text : texts)
        {
            if ((TextUtils.isEmpty(text) == true || "null".equalsIgnoreCase(text) == true || text.trim().length() == 0) == true)
            {
                return true;
            }
        }

        return false;
    }

    public static String getAppVersion(Context context)
    {
        String version = null;
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (NameNotFoundException e)
        {
            ExLog.d(e.toString());
        }

        return version;
    }

    public static boolean isGooglePlayServicesAvailable(Context context)
    {
        try
        {
            return (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS);
        } catch (Exception e)
        {
            return false;
        }
    }

    public static boolean isInstallGooglePlayService(Activity activity)
    {
        if (Util.isGooglePlayServicesAvailable(activity) == false)
        {
            return false;
        }

        boolean isInstalled;

        try
        {
            PackageManager packageManager = activity.getPackageManager();

            ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.google.android.gms", 0);
            PackageInfo packageInfo = packageManager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_SIGNATURES);

            int version = activity.getResources().getInteger(com.google.android.gms.R.integer.google_play_services_version);

            if (packageInfo.versionCode < version)
            {
                isInstalled = false;
            } else
            {
                isInstalled = true;
            }
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

            if (Util.isTextEmpty(countryIsoCode) == true)
            {
                Locale currentLocale = context.getResources().getConfiguration().locale;

                countryIsoCode = currentLocale.getCountry();
            }

            if (Util.isTextEmpty(countryIsoCode) == false)
            {
                CountryCodeNumber countryCodeNumber = new CountryCodeNumber();

                return countryCodeNumber.getContryNameNCode(countryIsoCode);
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

    public static boolean isValidatePhoneNumber(String phonenumber)
    {
        if (Util.isTextEmpty(phonenumber) == true)
        {
            return false;
        }

        if (phonenumber.charAt(0) == '+')
        {
            String globalPhoneNumber = phonenumber.replaceFirst("\\s", "^");
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
                    if (TextUtils.isDigitsOnly(text[1].substring(5)) == true)
                    {
                        int length = text[1].length();
                        if (length == 12 || length == 13)
                        {
                            return (Util.isExistMobileNumber(phonenumber) == false);
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
        if (Util.isTextEmpty(mobileNumber) == true || mobileNumber.startsWith("+82") == false)
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
        final String PATTENR_4 = "1111|2222|3333|4444|5555|6666|7777|8888|9999|0000|0123|1234|2345|3456|4567|5678|6789|9876|8765|7654|6543|5432|4321|3210{1}";

        Pattern pattern01 = Pattern.compile(PATTERN);
        Pattern pattern02 = mobile02.length() == 3 ? Pattern.compile(PATTERN_3) : Pattern.compile(PATTENR_4);
        Pattern pattern03 = Pattern.compile(PATTENR_4);

        return pattern01.matcher(mobile01).matches() == false && pattern02.matcher(mobile02).matches() && pattern03.matcher(mobile03).matches();
    }

    public static String[] getValidatePhoneNumber(String phonenumber)
    {
        if (Util.isTextEmpty(phonenumber) == true)
        {
            return null;
        }

        if (phonenumber.charAt(0) == '+')
        {
            String globalPhoneNumber = phonenumber.replaceFirst("\\s", "^");
            String[] text = globalPhoneNumber.split("\\^");
            String countryCode = getValidateCountry(text[0]);

            // 국제 전화번호 존재 여부 확인
            if (isTextEmpty(countryCode) == true)
            {
                return null;
            }

            if ("+82".equalsIgnoreCase(text[0]) == true)
            {
                if (text[1].startsWith("(0)10") || text[1].startsWith("(0)11") || text[1].startsWith("(0)16") //
                    || text[1].startsWith("(0)17") || text[1].startsWith("(0)18") || text[1].startsWith("(0)19"))
                {
                    if (TextUtils.isDigitsOnly(text[1].substring(5)) == true)
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
            String text = phonenumber.replace("-", "").replace(" ", "");

            if (text.startsWith("010") || text.startsWith("011") || text.startsWith("016") //
                || text.startsWith("017") || text.startsWith("018") || text.startsWith("019"))
            {
                if (TextUtils.isDigitsOnly(text) == true)
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

        if (Util.isTextEmpty(titleText) == true)
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
            dialog.show();

            return dialog;
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return null;
    }

    public static String addHippenMobileNumber(Context context, String mobileNumber)
    {
        if (Util.isTextEmpty(mobileNumber) == true)
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
        if (context == null || Util.isTextEmpty(packageName) == true || intent == null)
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
        if (activity == null || Util.isTextEmpty(latitude, longitude) == true)
        {
            return;
        }

        final String packageName = "net.daum.android.map";
        String url = String.format("daummaps://look?p=%s,%s", latitude, longitude);

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
        if (activity == null || Util.isTextEmpty(latitude, longitude) == true)
        {
            return;
        }

        final String packageName = "com.nhn.android.nmap";
        String url = String.format("navermaps://?menu=location&lat=%s&lng=%s&title=%s", latitude, longitude, name);

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

    //    public static void shareKakaoNavi(Context context, String name, String latitude, String longitude)
    //    {
    //        if (context == null || Util.isTextEmpty(latitude) == true || Util.isTextEmpty(longitude) == true)
    //        {
    //            return;
    //        }
    //
    //        final String packageName = "com.nhn.android.nmap";
    //
    //        if (isInstalledPackage(context, packageName) == true)
    //        {
    //            String url = String.format("kimgisa://navigate?name=%s&coord_type=wgs84&pos_x=%s&pos_y=%s", name, longitude, latitude);
    //
    //            Intent intent = new Intent(Intent.ACTION_VIEW);
    //            intent.setData(Uri.parse(url));
    //            context.startActivity(intent);
    //        } else
    //        {
    //            final String downloadUrl = String.format("https://play.google.com/store/apps/details?id=%s", packageName);
    //
    //            Intent intent = new Intent(Intent.ACTION_VIEW);
    //            intent.setData(Uri.parse(downloadUrl));
    //            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //            context.startActivity(intent);
    //        }
    //    }

    public static void shareGoogleMap(Activity activity, String placeName, String latitude, String longitude)
    {
        if (activity == null || Util.isTextEmpty(latitude, longitude) == true)
        {
            return;
        }

        final String packageName = "com.google.android.apps.maps";
        //            String url = String.format("http://maps.google.com/maps?q=%s&ll=%s,%s&z=14", placeName, latitude, longitude);
        String url = String.format("http://maps.google.com/maps?q=loc:%s,%s(%s)&z=14", latitude, longitude, placeName);

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
    }

    public static void showShareMapDialog(final BaseActivity baseActivity, final String placeName//
        , final double latitude, final double longitude, boolean isOverseas//
        , final String gaCategory, final String gaAction, final String gaLabel)
    {
        if (baseActivity == null || baseActivity.isFinishing() == true)
        {
            return;
        }

        View dialogView;
        final Dialog dialog = new Dialog(baseActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        LayoutInflater layoutInflater = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (isOverseas == false)
        {
            dialogView = layoutInflater.inflate(R.layout.view_searchmap_dialog_layout01, null, false);

            // 버튼
            View kakaoMapLayoutLayout = dialogView.findViewById(R.id.kakaoMapLayout);
            View naverMapLayout = dialogView.findViewById(R.id.naverMapLayout);
            View googleMapLayout = dialogView.findViewById(R.id.googleMapLayout);

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

                    if (Util.isTextEmpty(gaCategory) == false)
                    {
                        if (Util.isTextEmpty(gaLabel) == true)
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

                    if (Util.isTextEmpty(gaCategory) == false)
                    {
                        if (Util.isTextEmpty(gaLabel) == true)
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

                    if (Util.isTextEmpty(gaCategory) == false)
                    {
                        if (Util.isTextEmpty(gaLabel) == true)
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "Google", null);
                        } else
                        {
                            AnalyticsManager.getInstance(baseActivity).recordEvent(gaCategory, gaAction, "Google-" + gaLabel, null);
                        }
                    }
                }
            });
        } else
        {
            dialogView = layoutInflater.inflate(R.layout.view_searchmap_dialog_layout02, null, false);

            // 버튼
            View googleMapLayout = dialogView.findViewById(R.id.googleMapLayout);

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

                    if (Util.isTextEmpty(gaCategory) == false)
                    {
                        if (Util.isTextEmpty(gaLabel) == true)
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

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                baseActivity.unLockUI();
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

    public static void clipText(Context context, String text)
    {
        if (Util.isOverAPI11() == true)
        {
            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
        } else
        {
            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(text);
        }
    }

    public static void installPackage(Context context, String packageName)
    {
        if (context == null || Util.isTextEmpty(packageName) == true)
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

    public static String getPriceFormat(Context context, int price, boolean isPrefixType)
    {
        if (isPrefixType == true)
        {
            DecimalFormat decimalFormat = new DecimalFormat(context.getString(R.string.currency_format_prefix));
            return decimalFormat.format(price);
        } else
        {
            DecimalFormat decimalFormat = new DecimalFormat(context.getString(R.string.currency_format));
            return decimalFormat.format(price);
        }
    }

    //    public static Date getISO8601Date(String time) throws ParseException, NullPointerException
    //    {
    //        if (isTextEmpty(time) == true)
    //        {
    //            throw new NullPointerException("time is empty");
    //        }
    //
    //        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_8601_FORMAT_STRING, Locale.KOREA);
    //        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));
    //        return simpleDateFormat.parse(time);
    //    }

    //    public static String getISO8601String(long time)
    //    {
    //        Date date = new Date(time);
    //
    //        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_8601_FORMAT_STRING, Locale.KOREA);
    //        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));
    //
    //        String formatString = simpleDateFormat.format(date);
    //
    //        return formatString;
    //    }

    //    public static String simpleDateFormatISO8601toFormat(String iso8601, String format) throws ParseException, NullPointerException
    //    {
    //        if (Util.isTextEmpty(iso8601, format) == true)
    //        {
    //            throw new NullPointerException("iso8601, format is empty");
    //        }
    //
    ////        Date date = getISO8601Date(iso8601);
    //        Date date = DailyCalendar.convertDate(iso8601, DailyCalendar.ISO_8601_FORMAT);
    //
    //        return simpleDateFormat(date, format);
    //    }

    //    public static String simpleDateFormat(Date date, String format)
    //    {
    //        if (date == null || Util.isTextEmpty(format) == true)
    //        {
    //            return null;
    //        }
    //
    //        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.KOREA);
    //        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));
    //
    //        return simpleDateFormat.format(date);
    //    }

    //    public static String getISO8601String(Date date)
    //    {
    //        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_8601_FORMAT_STRING, Locale.KOREA);
    //        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));
    //
    //        String formatString = simpleDateFormat.format(date);
    //
    //        return formatString;
    //    }

    /**
     * String value 값 중 "true", "1", "Y", "y" 값을 true로 바꿔 주는 메소드
     *
     * @param value
     * @return boolean value
     */
    public static boolean parseBoolean(String value)
    {
        if (isTextEmpty(value) == true)
        {
            return false;
        }

        value = value.toLowerCase();

        if ("true".equalsIgnoreCase(value))
        {
            return true;
        } else if ("1".equalsIgnoreCase(value))
        {
            return true;
        } else if ("Y".equalsIgnoreCase(value))
        {
            return true;
        }

        return false;
    }

    /**
     * int value 값을 true로 바꿔 주는 메소드
     *
     * @param value int value
     * @return boolean value
     */
    public static boolean parseBoolean(int value)
    {
        if (1 == value)
        {
            return true;
        }

        return false;
    }

    public static float getTextWidth(Context context, String text, double dp, Typeface typeface)
    {
        return getScaleTextWidth(context, text, dp, 1.0f, typeface);
    }

    public static float getScaleTextWidth(Context context, String text, double dp, float scaleX, Typeface typeface)
    {
        if (context == null || isTextEmpty(text))
        {
            return 0;
        }

        Paint p = new Paint();

        float size = dpToPx(context, dp);
        p.setTextSize(size);
        p.setTypeface(typeface);
        p.setTextScaleX(scaleX);

        float width = p.measureText(text);

        p.reset();
        return width;
    }

    public static String makeIntroImageFileName(String version)
    {
        if (Util.isTextEmpty(version) == true)
        {
            return "daily_intro";
        }

        String[] versions = version.split("\\+");
        return versions[0].replaceAll(REMOVE_CHARACTER, "");
    }
}
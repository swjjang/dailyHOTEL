# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/sheldon/Development/Android/_Android-SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class com.facebook.** {
   *;
}

-keep class com.daily.base.**  { *; }
-keep class com.twoheart.dailyhotel.widget.**  { *; }

-keep public class com.twoheart.dailyhotel.JavaScriptInterface

-keepclassmembers class com.twoheart.dailyhotel.JavaScriptInterface {
    <fields>;
    <methods>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

# Google Play Services

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keep class com.kakao.** { *; }
-keepattributes Signature
-keepclassmembers class * {
  public static <fields>;
  public *;
}

-keep class com.crashlytics.** { *; }

-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }

-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

# Allow obfuscation of android.support.v7.internal.view.menu.**
# to avoid problem on Samsung 4.2.2 devices with appcompat v21
# see https://code.google.com/p/android/issues/detail?id=78377
#-keep class !android.support.v7.internal.view.menu.**, android.support.v7.** { *; }
#-keep class !android.support.v7.view.menu.**, android.support.v7.** { *; }

-keep class com.google.** { *; }
-keep interface com.google.** { *; }

-keep class com.google.android.** { *; }
-keep interface com.google.android.** { *; }


#OkHttp3
-keepattributes Signature
-keepattributes Annotation
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

-keepnames class android.widget.ScrollView { *; }
-keepnames class android.widget.AbsListView { *; }
-keepnames class android.support.v4.widget.NestedScrollView { *; }
-keepnames class android.support.v7.widget.RecyclerView { *; }
-keepnames class android.support.v4.view.ViewPager { *; }
-keepnames class android.widget.EdgeEffect { *; }
-keepnames class android.support.v4.widget.EdgeEffectCompat { *; }

#Appboy
-keep class bo.app.** { *; }
-keep class com.appboy.** { *; }

#kakao
-keep public class com.kakao.** { *; }

#Adjust
-keep public class com.adjust.sdk.** { *; }
-keep class com.google.android.gms.common.ConnectionResult {
    int SUCCESS;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
    com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
    java.lang.String getId();
    boolean isLimitAdTrackingEnabled();
}
-keep class dalvik.system.VMRuntime {
    java.lang.String getRuntime();
}
-keep class android.os.Build {
    java.lang.String[] SUPPORTED_ABIS;
    java.lang.String CPU_ABI;
}
-keep class android.content.res.Configuration {
    android.os.LocaleList getLocales();
    java.util.Locale locale;
}
-keep class android.os.LocaledList {
    java.util.Locale get(int);
}
-keep public class com.android.installreferrer.** { *; }

#Retrofit
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

#LoganSquare
-keep class com.bluelinelabs.logansquare.** { *; }
-keep @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep class **$$JsonObjectMapper { *; }

#KeyFrames
-keep class com.facebook.keyframes.** { *; }

#RetroLambda
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

-dontwarn android.support.v4.**, android.support.v7.**, com.ning.http.client.**, org.jboss.netty.**
-dontwarn org.slf4j.**, com.fasterxml.jackson.databind.**, com.google.android.gms.**, com.crashlytics.**
-dontwarn com.google.**, android.net.http.AndroidHttpClient, com.android.volley.**

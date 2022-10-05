package com.albumi;

import android.content.Intent;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.onesignal.OneSignal;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;


public class LoaderActivity extends AppCompatActivity {
    // КОД ДЛЯ БЛЯДСКИХ ТЕСТОВ
    // Диплинк
    // adb shell am start -W -a android.intent.action.VIEW "myapp://test1/test2/test3/test4/test5"
    // adb shell am start -W -a android.intent.action.VIEW "myapp://test1/test2/test3/test4/test5" com.albumi
    // adb shell am start -W -a android.intent.action.VIEW -d "myapp://test1/test2/test3/test4/test5"
    // adb shell "am start com.albumi -a com.albumi.action.VIEW"
    // Аппс флаер
    // adb shell am start -W -a android.intent.action.VIEW "https://app.appsflyer.com/com.albumi?pid=devtest&advertising_id=43eba73f-2fd7-41c4-8948-c2fa730cc6f1"




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        //runWebView();
        initFacebook();
    }


    /*
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
    */
    //==============================================================================================
    private void runWebView() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                webView(linkBuilder());
            }
        });
        thread.start();
    }

    //==============================================================================================

    private void test() {
        //OneSignal.setExternalUserId(AdvertisingIdClient.getAdvertisingIdInfo(this).toString());
    }

    //==============================================================================================



    //==============================================================================================
    //==============================================================================================
    //==============================================================================================
    private String ONESIGNAL_APP_ID = "31233279-e064-47e3-b413-5e75394a774f";

    private void initOneSignal(final String link, final String tag) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Configuration myConfig = new Configuration.Builder()
                        .setMinimumLoggingLevel(android.util.Log.INFO)
                        .build();

                WorkManager.initialize(getApplicationContext(), myConfig);

                OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

                //OneSignal.promptForPushNotifications();

                OneSignal.initWithContext(getApplicationContext());
                OneSignal.setAppId(ONESIGNAL_APP_ID);
                OneSignal.setExternalUserId(googleAdId());

                OneSignal.sendTag("key2", tag);

                webView(link);
            }
        });
        thread.start();
    }

    private String googleAdId() {
        String advertisingId = "null";

        com.google.android.gms.ads.identifier.AdvertisingIdClient.Info idInfo;

        try {
            idInfo = com.google.android.gms.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo(this);
            advertisingId = idInfo.getId();
            //System.out.println("### 1 Гугл: есть реакция на запуск: " + advertisingId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }

        return advertisingId;
    }

    //==============================================================================================
    private void initFacebook() {
        //==========================================================================================
        FacebookSdk.sdkInitialize(this);
        FacebookSdk.fullyInitialize();

        AppLinkData.fetchDeferredAppLinkData(this,
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        if (appLinkData != null) {
                            String deeplinkTag = appLinkData.toString().replace("myapp://", "");
                            deeplinkTag = deeplinkTag.substring(deeplinkTag.indexOf("/"));
                            initOneSignal(linkBuilder(Objects.requireNonNull(appLinkData.getTargetUri()).toString()), deeplinkTag);
                        } else{
                            initAppsFlyer();
                        }

                    }
                }
        );


        /*
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
        AppLinkData.fetchDeferredAppLinkData(this,
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        // Process app link data
                        System.out.println("############################################");
                    }
                }
        );
         */

    }
    //==============================================================================================
    private void initAppsFlyer(){
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionDataMap) {
                //System.out.println("############################################");
                //for (String attrName : conversionDataMap.keySet())
                    //System.out.println("### Conversion attribute: " + attrName + " = " + conversionDataMap.get(attrName));
                String status = Objects.requireNonNull(conversionDataMap.get("af_status")).toString();
                if (status.equals("Organic")) {
                    initOneSignal(linkBuilder(), "organic");
                } else {
                    initOneSignal(linkBuilder(conversionDataMap), Objects.requireNonNull(conversionDataMap.get("campain")).toString().substring(Objects.requireNonNull(conversionDataMap.get("campain")).toString().indexOf("_")));
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                //System.out.println("### error getting conversion data: " + errorMessage);
                initOneSignal(linkBuilder(), "organic");
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                // Must be overriden to satisfy the AppsFlyerConversionListener interface.
                // Business logic goes here when UDL is not implemented.
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                // Must be overriden to satisfy the AppsFlyerConversionListener interface.
                // Business logic goes here when UDL is not implemented.
            }

        };
    }
    //==============================================================================================
    private String linkBuilder(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("icelucky.xyz")
                .appendPath("icelucky.php")
                .appendQueryParameter(getResources().getString(R.string.secure_get_parametr), getResources().getString(R.string.secure_key))
                .appendQueryParameter(getResources().getString(R.string.dev_tmz_key), TimeZone.getDefault().getID())
                .appendQueryParameter(getResources().getString(R.string.gadid_key), googleAdId())
                .appendQueryParameter(getResources().getString(R.string.deeplink_key), null)
                .appendQueryParameter(getResources().getString(R.string.source_key), null)
                .appendQueryParameter(getResources().getString(R.string.af_id_key), null)
                .appendQueryParameter(getResources().getString(R.string.adset_id_key), null)
                .appendQueryParameter(getResources().getString(R.string.campaign_id_key), null)
                .appendQueryParameter(getResources().getString(R.string.app_campaign_key), null)
                .appendQueryParameter(getResources().getString(R.string.adset_key), null)
                .appendQueryParameter(getResources().getString(R.string.adgroup_key), null)
                .appendQueryParameter(getResources().getString(R.string.orig_cost_key), null)
                .appendQueryParameter(getResources().getString(R.string.af_siteid_key), null);
        return builder.build().toString();
    }

    private String linkBuilder(final String deep){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("icelucky.xyz")
                .appendPath("icelucky.php")
                .appendQueryParameter(getResources().getString(R.string.secure_get_parametr), getResources().getString(R.string.secure_key))
                .appendQueryParameter(getResources().getString(R.string.dev_tmz_key), TimeZone.getDefault().getID())
                .appendQueryParameter(getResources().getString(R.string.gadid_key), googleAdId())
                .appendQueryParameter(getResources().getString(R.string.deeplink_key), deep)
                .appendQueryParameter(getResources().getString(R.string.source_key), "deeplink")
                .appendQueryParameter(getResources().getString(R.string.af_id_key), null)
                .appendQueryParameter(getResources().getString(R.string.adset_id_key), null)
                .appendQueryParameter(getResources().getString(R.string.campaign_id_key), null)
                .appendQueryParameter(getResources().getString(R.string.app_campaign_key), null)
                .appendQueryParameter(getResources().getString(R.string.adset_key), null)
                .appendQueryParameter(getResources().getString(R.string.adgroup_key), null)
                .appendQueryParameter(getResources().getString(R.string.orig_cost_key), null)
                .appendQueryParameter(getResources().getString(R.string.af_siteid_key), null);
        return builder.build().toString();
    }

    private String linkBuilder(Map<String, Object> conversionDataMap){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("icelucky.xyz")
                .appendPath("icelucky.php")
                .appendQueryParameter(getResources().getString(R.string.secure_get_parametr), getResources().getString(R.string.secure_key))
                .appendQueryParameter(getResources().getString(R.string.dev_tmz_key), TimeZone.getDefault().getID())
                .appendQueryParameter(getResources().getString(R.string.gadid_key), googleAdId())
                .appendQueryParameter(getResources().getString(R.string.deeplink_key), null)
                .appendQueryParameter(getResources().getString(R.string.source_key), Objects.requireNonNull(conversionDataMap.get("media_source")).toString())
                .appendQueryParameter(getResources().getString(R.string.af_id_key), AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext()))
                .appendQueryParameter(getResources().getString(R.string.adset_id_key), Objects.requireNonNull(conversionDataMap.get("adset_id")).toString())
                .appendQueryParameter(getResources().getString(R.string.campaign_id_key), Objects.requireNonNull(conversionDataMap.get("campaign_id")).toString())
                .appendQueryParameter(getResources().getString(R.string.app_campaign_key), Objects.requireNonNull(conversionDataMap.get("campaign")).toString())
                .appendQueryParameter(getResources().getString(R.string.adset_key), Objects.requireNonNull(conversionDataMap.get("adset")).toString())
                .appendQueryParameter(getResources().getString(R.string.adgroup_key), Objects.requireNonNull(conversionDataMap.get("adgroup")).toString())
                .appendQueryParameter(getResources().getString(R.string.orig_cost_key), Objects.requireNonNull(conversionDataMap.get("orig_cost")).toString())
                .appendQueryParameter(getResources().getString(R.string.af_siteid_key), Objects.requireNonNull(conversionDataMap.get("af_siteid")).toString());
        return builder.build().toString();
    }
    //==============================================================================================
    private void webView(String link){
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("builtLink", link);
        startActivity(intent);
        overridePendingTransition(R.anim.up, R.anim.down);
        finish();
    }
    //==============================================================================================
    //==============================================================================================
    //==============================================================================================
}
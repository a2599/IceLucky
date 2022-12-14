package com.albumi;

import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class LoaderActivity extends AppCompatActivity {
    // КОД ДЛЯ ТЕСТОВ
    // Диплинк
    // adb shell am start -W -a android.intent.action.VIEW "myapp://test1/test2/test3/test4/test5"
    // adb shell am start -W -a android.intent.action.VIEW "myapp://test1/test2/test3/test4/test5" com.albumi
    // adb shell am start -W -a android.intent.action.VIEW -d "myapp://test1/test2/test3/test4/test5"
    // adb shell "am start com.albumi -a com.albumi.action.VIEW"
    // Аппс флаер
    // adb shell am start -W -a android.intent.action.VIEW "https://app.appsflyer.com/com.albumi?pid=devtest&advertising_id=43eba73f-2fd7-41c4-8948-c2fa730cc6f1"

    //private boolean isHasDeeplink = false;
    //private boolean isHasCampaign = false;
    //private boolean isAppsFlyerStarting = false;


    private static final String AF_DEV_KEY = "dVk5VvxWsn6KZVtGCXMFu8";
    private static final String ONESIGNAL_APP_ID = "31233279-e064-47e3-b413-5e75394a774f";
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);


        sp = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        if (sp.getString("link", null) !=null)
            webView(sp.getString("link", null));
        else
            initFacebook();


        //System.out.println("###################################################################################### "+AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext()));



        //runWebView();
        //initFacebook();
        //initAppsFlyer();

        //Handler handler = new Handler();
        //handler.postDelayed(new Runnable() {
        //    public void run() {
        //        initAppsFlyer();
        //    }
        //}, 5000);

        //runWeb(20000);
        //==========================================================================================

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    //==============================================================================================
    private void runWeb(int delay) {

        /*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!isHasCampaign && !isHasDeeplink) {
                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            initOneSignal(linkBuilder(), "organic");
                        }
                    });
                    thread.start();
                } else
                    runWeb(5000);
            }
        }, delay);
         */

    }

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
        Handler handler = new Handler();
        FacebookSdk.InitializeCallback initializeCallback = new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AppLinkData.fetchDeferredAppLinkData(getApplicationContext(),
                                new AppLinkData.CompletionHandler() {
                                    @Override
                                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {


                                        if (appLinkData != null) {
                                            //isHasDeeplink = true;
                                            String deeplinkTag = Objects.requireNonNull(appLinkData.getTargetUri()).toString().replace("myapp://", "").replaceAll("\\/.*", "");
                                            initOneSignal(linkBuilder(Objects.requireNonNull(appLinkData.getTargetUri()).toString()), deeplinkTag);
                                        } else {
                                            //isHasDeeplink = false;
                                            initAppsFlyer();
                                        }

                                        /*
                                        //========================================================== ТЕСТ
                                        String deeplinkTag = "myapp://test1/test2/test3/test4/".replace("myapp://", "").replaceAll("\\/.*", "");
                                        //deeplinkTag = deeplinkTag.substring(deeplinkTag.indexOf("/"));

                                        //deeplinkTag = deeplinkTag.replaceAll("\\/.*", "");
                                        //System.out.println("###################################################################################### "+deeplinkTag);
                                        initOneSignal(linkBuilder("myapp://test1/test2/test3/test4/"), deeplinkTag);
                                        //==========================================================
                                        */
                                    }
                                }
                        );
                    }
                });
            }
        };

        FacebookSdk.sdkInitialize(this, initializeCallback);
        FacebookSdk.fullyInitialize();
        //FacebookSdk.setAutoInitEnabled(true);
        //FacebookSdk.fullyInitialize();
    }

    //==============================================================================================
    private void initAppsFlyer() {
        //if (!isAppsFlyerStarting) {


        //AFApplication application = new AFApplication();
        //application.onCreate();
        //AppsFlyerLib.getInstance().sendDeepLink(this);


        //AppsFlyerLib.getInstance().registerConversionListener(this, new AppsFlyerConversionListener() {
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {

            /* Returns the attribution data. Note - the same conversion data is returned every time per install */
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                //for (String attrName : conversionData.keySet()) {
                //Log.d(AppsFlyerLibCore.LOG_TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
                //}
                //isHasCampaign = true;

                //for (String attrName : conversionDataMap.keySet())
                //System.out.println("### Conversion attribute: " + attrName + " = " + conversionDataMap.get(attrName));
                //==================================================================================

                if (conversionData != null) {
                    String status = Objects.requireNonNull(conversionData.get("af_status")).toString();

                    if (status.equals("Organic"))
                        initOneSignal(linkBuilder(), "organic");
                    else
                        initOneSignal(linkBuilder(conversionData), Objects.requireNonNull(conversionData.get("campaign")).toString().substring(Objects.requireNonNull(conversionData.get("campaign")).toString().indexOf("_")));
                } else
                    initOneSignal(linkBuilder(), "organic");

                //================================================================================== ТЕСТ
                /*
                Map<String, Object> mockData = new HashMap<>();

                mockData.put("media_source", "utest1");
                //data.put("","");
                mockData.put("adset_id", "utest2");
                mockData.put("campaign_id", "utest3");
                mockData.put("campaign", "test1_test2_test3_test4");
                mockData.put("adset", "utest4");
                mockData.put("adgroup", "utest5");
                mockData.put("orig_cost", "utest6");
                mockData.put("af_siteid", "utest7");

                initOneSignal(linkBuilder(mockData), Objects.requireNonNull(mockData.get("campaign")).toString().substring(Objects.requireNonNull(mockData.get("campaign")).toString().indexOf("_")));
                 */
                //==================================================================================
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                //Log.d(AppsFlyerLibCore.LOG_TAG, "error onInstallConversionFailure : " + errorMessage);
                //if (!isHasDeeplink)
                initOneSignal(linkBuilder(), "organic");
            }


            /* Called only when a Deep Link is opened */
            @Override
            public void onAppOpenAttribution(Map<String, String> conversionData) {
                StringBuilder attributionDataText = new StringBuilder("Attribution Data: \n");
                for (String attrName : conversionData.keySet()) {
                    //Log.d(AppsFlyerLibCore.LOG_TAG, "attribute: " + attrName + " = " +conversionData.get(attrName));
                    attributionDataText.append(conversionData.get(attrName)).append("\n");
                }
                //setAttributionText(attributionDataText);
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                //Log.d(AppsFlyerLibCore.LOG_TAG, "error onAttributionFailure : " + errorMessage);
            }
        };

        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, getApplicationContext());
        AppsFlyerLib.getInstance().start(this);
        //AppsFlyerLib.getInstance().start(this, AF_DEV_KEY, conversionListener);

        /* Set to true to see the debug logs. Comment out or set to false to stop the function */

        AppsFlyerLib.getInstance().setDebugLog(true);
        //isAppsFlyerStarting = true;
        //}
    }

    //==============================================================================================
    private String linkBuilder() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("icelucky.xyz")
                .appendPath("icelucky.php")
                .appendQueryParameter(getResources().getString(R.string.secure_get_parametr), getResources().getString(R.string.secure_key))
                .appendQueryParameter(getResources().getString(R.string.dev_tmz_key), TimeZone.getDefault().getID())
                .appendQueryParameter(getResources().getString(R.string.gadid_key), googleAdId())
                .appendQueryParameter(getResources().getString(R.string.deeplink_key), null)
                .appendQueryParameter(getResources().getString(R.string.source_key), null)
                .appendQueryParameter(getResources().getString(R.string.af_id_key), AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext()))
                .appendQueryParameter(getResources().getString(R.string.adset_id_key), null)
                .appendQueryParameter(getResources().getString(R.string.campaign_id_key), null)
                .appendQueryParameter(getResources().getString(R.string.app_campaign_key), null)
                .appendQueryParameter(getResources().getString(R.string.adset_key), null)
                .appendQueryParameter(getResources().getString(R.string.adgroup_key), null)
                .appendQueryParameter(getResources().getString(R.string.orig_cost_key), null)
                .appendQueryParameter(getResources().getString(R.string.af_siteid_key), null);
        return builder.build().toString();
    }

    private String linkBuilder(final String deep) {
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

    private String linkBuilder(Map<String, Object> conversionDataMap) {
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
    private void webView(String link) {
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
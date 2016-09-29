package com.moybl.topnumber;

import com.google.android.gms.ads.MobileAds;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class TopNumberApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    FacebookSdk.sdkInitialize(getApplicationContext());
    AppEventsLogger.activateApp(this);
    MobileAds.initialize(getApplicationContext(), "ca-app-pub-1215267734081435~3495564103");
  }

}

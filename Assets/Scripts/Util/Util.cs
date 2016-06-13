using System;
using UnityEngine;

public static class Util
{
    public static bool IsAppInstalled(string bundleID)
    {
#if UNITY_ANDROID
        var up = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        var ca = up.GetStatic<AndroidJavaObject>("currentActivity");
        var packageManager = ca.Call<AndroidJavaObject>("getPackageManager");
        AndroidJavaObject launchIntent = null;

        try {
            launchIntent = packageManager.Call<AndroidJavaObject>("getLaunchIntentForPackage", bundleID);
        } catch (Exception ex) {
            Debug.Log("exception" + ex.Message);
        }

        if (launchIntent == null) {
            return false;
        }

        return true;
#else
         return false;
#endif
    }
}
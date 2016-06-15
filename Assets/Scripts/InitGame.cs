using Facebook.Unity;
using GooglePlayGames;
using GooglePlayGames.BasicApi;
using Heyzap;
using UnityEngine;
using UnityEngine.SceneManagement;

public class InitGame : MonoBehaviour
{
    private void Start()
    {
        HeyzapAds.Start("71ceaa268195811d7c9ccb4a791154c8", HeyzapAds.FLAG_NO_OPTIONS);
        HZIncentivizedAd.Fetch();

        if (!FB.IsInitialized) {
            // Initialize the Facebook SDK
            FB.Init(() =>
            {
                FB.ActivateApp();

                SceneManager.LoadScene("Main");
            }, isGameShown => {});
        } else {
            // Already initialized, signal an app activation App Event
            FB.ActivateApp();
        }

#if UNITY_ANDROID
        var config = new PlayGamesClientConfiguration.Builder()
            .EnableSavedGames()
            .Build();
        PlayGamesPlatform.InitializeInstance(config);
        PlayGamesPlatform.Activate();
#endif
    }
}
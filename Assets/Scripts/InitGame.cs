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

#if UNITY_ANDROID
        var config = new PlayGamesClientConfiguration.Builder().Build();
        PlayGamesPlatform.InitializeInstance(config);
        PlayGamesPlatform.Activate();
#endif
        Social.localUser.Authenticate(success =>
        {
            if (success) {
                LoadScore();
            } else {
                SceneManager.LoadScene("Main");
            }
        });
    }

    private void LoadScore()
    {
        var unreportedScore = 0;

        if (PlayerPrefs.HasKey("UnreportedScore")) {
            unreportedScore = PlayerPrefs.GetInt("UnreportedScore");
            PlayerPrefs.DeleteKey("UnreportedScore");
            PlayerPrefs.Save();
        }

#if UNITY_ANDROID
        PlayGamesPlatform.Instance.LoadScores(GooglePlayIds.leaderboardHighScores, LeaderboardStart.PlayerCentered, 1,
            LeaderboardCollection.Public, LeaderboardTimeSpan.AllTime, data =>
            {
                /*
                var onlineScore = 0;
                var prefsScore = GameData.instance.bestScore;

                if (data.Valid) {
                    onlineScore = (int) data.PlayerScore.value;
                }

                var bestScore = Mathf.Max(unreportedScore, Mathf.Max(onlineScore, prefsScore));
                GameData.instance.bestScore = bestScore;

                if (unreportedScore > onlineScore) {
                    Social.ReportScore(bestScore, GooglePlayIds.leaderboardHighScores, success =>
                    {
                        if (!success) {
                            PlayerPrefs.SetInt("UnreportedScore", bestScore);
                            PlayerPrefs.Save();
                        }

                        SceneManager.LoadScene("Main");
                    });
                } else {
                    SceneManager.LoadScene("Main");
                }
                */
            });
#endif
    }
}
using System;
using System.Collections;
using GooglePlayGames;
using UnityEngine;
using UnityEngine.SocialPlatforms;

public class LeaderboardsWindow : MonoBehaviour
{
    [SerializeField]
    private DataBindContext m_DataBindContext;
    [SerializeField]
    private GameObject m_Offline;
    [SerializeField]
    private GameObject m_Online;

    private void OnEnable()
    {
        if (Social.localUser.authenticated) {
            m_Online.SetActive(true);
            m_Offline.SetActive(false);
            LoadNumbers();
        } else {
            m_Online.SetActive(false);
            m_Online.SetActive(true);
        }
    }

    public void OnConnectClick()
    {
        Social.localUser.Authenticate(success =>
        {
            if (success) {
                m_Online.SetActive(true);
                m_Offline.SetActive(false);
                LoadNumbers();
            } else {
                m_Online.SetActive(false);
                m_Online.SetActive(true);
            }
        });
    }

    private void LoadNumbers()
    {
#if UNITY_EDITOR
        LoadTestData();
#elif UNITY_ANDROID
        var lb = PlayGamesPlatform.Instance.CreateLeaderboard();
        lb.id = GooglePlayIds.leaderboardBiggestNumber;
        lb.LoadScores(success =>
        {
            if (success) {
                var userIds = new List<string>();

                for (var i = 0; i < lb.scores.Length; i++) {
                    userIds.Add(lb.scores[i].userID);
                }

                Social.LoadUsers(userIds.ToArray(), users =>
                {
                    PopulateList(users, lb.scores);
                });
            }
        });
#endif
    }

#if UNITY_EDITOR
    private void LoadTestData()
    {
        var users = new[]
        {
            new PlayGamesUserProfile("Bobby", "", ""),
            new PlayGamesUserProfile("Timmy", "", ""),
            new PlayGamesUserProfile("Jerry", "", "")
        };
        var scores = new[]
        {
            new PlayGamesScore(DateTime.Now, null, 0, null, (ulong) BitConverter.DoubleToInt64Bits(42), null),
            new PlayGamesScore(DateTime.Now, null, 0, null, (ulong) BitConverter.DoubleToInt64Bits(33), null),
            new PlayGamesScore(DateTime.Now, null, 0, null, (ulong) BitConverter.DoubleToInt64Bits(90325), null)
        };

        PopulateList(users, scores);
    }
#endif

    private void PopulateList(IUserProfile[] users, IScore[] scores)
    {
        var list = new ObservableList("users");

        for (var i = 0; i < users.Length; i++) {
            var user = users[i];
            var score = scores[i];

            var userNumber = (Huge) BitConverter.Int64BitsToDouble(score.value);
            var number = "";

            if (userNumber.power >= 3) {
                number = userNumber.firstDigits.ToString("###.###") + " <size=10>" +
                         Localization.GetText("Power" + userNumber.power) + "</size>";
            } else {
                number = ((int) userNumber.firstDigits).ToString();
            }

            var image = default(Sprite);

            if (user.image) {
                image = Sprite.Create(user.image, new Rect(0, 0, user.image.width, user.image.height),
                    new Vector2(user.image.width, user.image.height) / 2.0f);
            }

            list.Add(new UserNumberViewModel(user.userName, number, image, userNumber));
        }

        list.Sort(new NumberDescending());

        m_DataBindContext["users"] = list;
    }

    private class NumberDescending : IComparer
    {
        public int Compare(object x, object y)
        {
            var a = (UserNumberViewModel) x;
            var b = (UserNumberViewModel) y;

            return a.hugeNumber.CompareTo(b.hugeNumber);
        }
    }

    public class UserNumberViewModel
    {
        public UserNumberViewModel(string userName, string number, Sprite image, double hugeNumber)
        {
            this.userName = userName;
            this.number = number;
            this.image = image;
            this.hugeNumber = hugeNumber;
        }

        public string userName { get; set; }
        public string number { get; set; }
        public Sprite image { get; set; }
        public double hugeNumber { get; set; }
    }
}
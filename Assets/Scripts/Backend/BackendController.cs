using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Facebook.MiniJSON;
using Facebook.Unity;
using UnityEngine;

public class BackendController : Singleton<BackendController>
{
    private static readonly UTF8Encoding s_Utf8 = new UTF8Encoding();
    private static readonly string s_BackendUrl = "https://number-getting-bigger-82376485.appspot.com/";

    public event Action<bool> logInStateChanged = delegate {};

    private string m_SessionToken;
    public BackendUser localUser { get; private set; }
    public bool isLoggedIn
    {
        get { return !string.IsNullOrEmpty(m_SessionToken); }
    }

    private void Start()
    {
        if (!FB.IsInitialized) {
            FB.Init(() =>
            {
#if UNITY_ANDROID
                FB.ActivateApp();
#endif
                if (FB.IsLoggedIn) {
                    StartCoroutine(AppLogIn(success =>
                    {
                        ActiveBasedOnSignedIn.NotifyStateChanged(isLoggedIn);
                    }));
                } else {
                    ActiveBasedOnSignedIn.NotifyStateChanged(false);
                }
            });
        }
    }

    public void LogIn(Action<bool> callback = null)
    {
        var perms = new List<string>
        {
            "public_profile",
            "user_friends"
        };
        FB.LogInWithReadPermissions(perms, result =>
        {
            if (FB.IsLoggedIn) {
                StartCoroutine(AppLogIn(success =>
                {
                    if (callback != null) {
                        callback.Invoke(success);
                    }
                }));
            } else {
                if (callback != null) {
                    callback.Invoke(false);
                }
            }
        });
    }

    public void PostNumber(double number, Action<bool> callback)
    {
        StartCoroutine(AppPostNumber(number, callback));
    }

    public void GetFriends(Action<BackendUser[]> callback)
    {
        if (!isLoggedIn) {
            return;
        }

        FB.API("/me/friends?fields=picture,name,id", HttpMethod.GET, result =>
        {
            if (string.IsNullOrEmpty(result.Error)) {
                var friendsData = (IList) result.ResultDictionary["data"];
                var userIds = new string[friendsData.Count];
                var friends = new BackendUser[friendsData.Count];

                for (var i = 0; i < friendsData.Count; i++) {
                    var friendData = (IDictionary) friendsData[i];
                    userIds[i] = (string) friendData["id"];

                    var picture = (IDictionary) ((IDictionary) friendData["picture"])["data"];

                    var user = new BackendUser(userIds[i], 0, (string) friendData["name"]);
                    user.hasPicture = !(bool) picture["is_silhouette"];
                    friends[i] = user;
                }

                StartCoroutine(AppGetNumbers(userIds, numbers =>
                {
                    if (numbers == null) {
                        callback.Invoke(null);
                    } else {
                        for (var i = 0; i < friends.Length; i++) {
                            friends[i].number = numbers[i];
                        }

                        callback.Invoke(friends.OrderByDescending(user => user.number)
                                               .ToArray());
                    }
                }));
            } else {
                Debug.LogError(result.Error);
                callback.Invoke(null);
            }
        });
    }

    public void GetInvitableFriends(Action<BackendUser[]> callback)
    {
        if (!isLoggedIn) {
            return;
        }

        FB.API("/me/invitable_friends", HttpMethod.GET, result =>
        {
            if (string.IsNullOrEmpty(result.Error)) {
                var friendsData = (IList) result.ResultDictionary["data"];
                var friends = new BackendUser[friendsData.Count];

                for (var i = 0; i < friendsData.Count; i++) {
                    var friend = (IDictionary) friendsData[i];
                    friends[i] = new BackendUser((string) friend["id"], 0, (string) friend["name"]);
                }

                callback.Invoke(friends);
            } else {
                Debug.LogError(result.Error);
                callback.Invoke(null);
            }
        });
    }

    private IEnumerator AppLogIn(Action<bool> callback)
    {
        var url = s_BackendUrl + "log_in?access_token=" + AccessToken.CurrentAccessToken.TokenString;
        var www = new WWW(url, new byte[]
        {
            0
        });

        yield return www;

        if (string.IsNullOrEmpty(www.error)) {
            var data = (IDictionary) Json.Deserialize(www.text);
            m_SessionToken = (string) data["session_token"];

            FB.API("/me?fields=picture,name", HttpMethod.GET, result =>
            {
                if (string.IsNullOrEmpty(result.Error)) {
                    var picture = (IDictionary)((IDictionary)result.ResultDictionary["picture"])["data"];

                    localUser = new BackendUser(AccessToken.CurrentAccessToken.UserId, (double) data["number"],
                        (string) result.ResultDictionary["name"]);
                    localUser.hasPicture = !(bool)picture["is_silhouette"];

                    ActiveBasedOnSignedIn.NotifyStateChanged(true);
                    logInStateChanged.Invoke(true);
                    callback.Invoke(true);
                } else {
                    Debug.LogError(result.Error);
                    callback.Invoke(false);
                }
            });
        } else {
            Debug.LogError(www.error);
            callback.Invoke(false);
        }
    }

    private IEnumerator AppPostNumber(double number, Action<bool> callback)
    {
        if (!isLoggedIn) {
            callback.Invoke(false);
            yield break;
        }

        var url = s_BackendUrl + "numbers?number=" + number;
        var headers = new Dictionary<string, string>
        {
            {
                "X-USER-ID", AccessToken.CurrentAccessToken.UserId
            },
            {
                "X-SESSION-TOKEN", m_SessionToken
            }
        };
        var www = new WWW(url, new byte[]
        {
            0
        }, headers);

        yield return www;

        if (string.IsNullOrEmpty(www.error)) {
            callback.Invoke(true);
        } else {
            Debug.LogError(www.error);
            callback.Invoke(false);
        }
    }

    private IEnumerator AppGetNumbers(string[] userIds, Action<double[]> callback)
    {
        if (!isLoggedIn) {
            callback.Invoke(null);
            yield break;
        }

        var url = s_BackendUrl + "leaderboard";
        var headers = new Dictionary<string, string>
        {
            {
                "X-USER-ID", AccessToken.CurrentAccessToken.UserId
            },
            {
                "X-SESSION-TOKEN", m_SessionToken
            }
        };
        var postData = s_Utf8.GetBytes(string.Join(",", userIds));
        var www = new WWW(url, postData, headers);

        yield return www;

        if (string.IsNullOrEmpty(www.error)) {
            var data = (IList) Json.Deserialize(www.text);
            var numbers = new double[data.Count];

            for (var i = 0; i < data.Count; i++) {
                numbers[i] = (double) data[i];
            }

            callback.Invoke(numbers);
        } else {
            Debug.LogError(www.error);
            callback.Invoke(null);
        }
    }

    public void LogOut()
    {
        FB.LogOut();
        m_SessionToken = null;
        localUser = null;
        ActiveBasedOnSignedIn.NotifyStateChanged(false);
        logInStateChanged.Invoke(false);
    }
}
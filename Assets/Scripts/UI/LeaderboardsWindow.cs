using System;
using Facebook.Unity;
using UnityEngine;

public class LeaderboardsWindow : MonoBehaviour
{
    [SerializeField]
    private DataBindContext m_DataBindContext;

    private void OnEnable()
    {
        ActiveBasedOnSignedIn.NotifyStateChanged(BackendController.instance.isLoggedIn);

        if (BackendController.instance.isLoggedIn) {
            LoadNumbers();
        }
    }

    public void OnLogInClick()
    {
        BackendController.instance.LogIn(success =>
        {
            if (success) {
                LoadNumbers();
            }
        });
    }

    private void LoadNumbers()
    {
        BackendController.instance.GetFriends(friends =>
        {
            if (friends != null) {
                PopulateList(friends);
            }
        });
    }

    public void OnInviteFriendsClick()
    {
        FB.Mobile.AppInvite(new Uri("https://fb.me/627321040769639"), new Uri("http://i.imgur.com/zkYlB.jpg"),
            result => {});
    }

    private void PopulateList(BackendUser[] users)
    {
        var list = new ObservableList("users");

        for (var i = 0; i < users.Length; i++) {
            var user = users[i];

            var userNumber = (Huge) user.number;
            var number = "";

            if (userNumber.power >= 3) {
                number = userNumber.firstDigits.ToString("###.###") + " <size=10>" +
                         Localization.GetText("Power" + userNumber.power) + "</size>";
            } else {
                number = ((int) userNumber.firstDigits).ToString();
            }

            list.Add(new UserNumberViewModel(user.id, user.name, number, userNumber, user.hasPicture));
        }

        m_DataBindContext["users"] = list;
    }
}
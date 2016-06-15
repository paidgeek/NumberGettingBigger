using GooglePlayGames;
using UnityEngine;
using UnityEngine.UI;

public class SettingsWindow : MonoBehaviour
{
    [SerializeField]
    private Image m_SoundIcon;
    private GameSettings m_GameSettings;

    [Header("Icons")]
    [SerializeField]
    private Sprite m_SoundOnIcon;
    [SerializeField]
    private Sprite m_SoundOffIcon;

    private void OnEnable()
    {
        m_GameSettings = GameSettings.instance;
        OnSoundChanged();
    }

    public void OnSoundClick()
    {
        m_GameSettings.isSoundOn = !m_GameSettings.isSoundOn;
        OnSoundChanged();
    }

    public void OnLogInClick()
    {
        if (BackendController.instance.isLoggedIn) {
            BackendController.instance.LogOut();
        } else {
            BackendController.instance.LogIn();
        }
    }

    private void OnSoundChanged()
    {
        if (m_GameSettings.isSoundOn) {
            m_SoundIcon.sprite = m_SoundOnIcon;
        } else {
            m_SoundIcon.sprite = m_SoundOffIcon;
        }
    }
}
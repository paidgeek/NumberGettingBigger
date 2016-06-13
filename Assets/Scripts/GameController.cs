using System.Collections;
using System.Globalization;
using UnityEngine;

public class GameController : Singleton<GameController>
{
    private EventDispatcher m_EventDispatcher;
    private int m_Coins;
    private Huge m_Number;

    public Huge number
    {
        get { return m_Number; }
        set
        {
            m_Number = value;
            onNumberChanged.Invoke(m_Number);
        }
    }
    public Huge income
    {
        get { return 1.0; }
    }

    public BasicEvent<Huge> onNumberChanged { get; set; }

    private void Awake()
    {
        m_EventDispatcher = FindObjectOfType<EventDispatcher>();
        onNumberChanged = new BasicEvent<Huge>();
    }

    private void Start()
    {
        number = new Huge(double.Parse(PlayerPrefs.GetString("Number", "0")));
    }

    private new void OnDestroy()
    {
        base.OnDestroy();
        Save();
    }

    private void Update()
    {
        number += income * Time.deltaTime;
    }

    private void OnApplicationPause(bool paused)
    {
        if (paused) {
            Pause();
        }

        Save();
    }

    private void Save()
    {
        PlayerPrefs.SetString("Number", number.value.ToString(new CultureInfo("en-GB")));
        PlayerPrefs.Save();
    }

    public void Pause()
    {
        m_EventDispatcher.Invoke(EventId.PauseGame);
    }

    public void Continue()
    {
        m_EventDispatcher.Invoke(EventId.ContinueGame);
    }
}
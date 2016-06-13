using System.Collections;
using System.Globalization;
using UnityEngine;

public class GameController : Singleton<GameController>
{
    private EventDispatcher m_EventDispatcher;
    private int m_Coins;
    private Huge m_Number;
    private Source[] m_Sources;

    public Huge number
    {
        get { return m_Number; }
        set
        {
            m_Number = value;
            onNumberChanged.Invoke(m_Number);
        }
    }
    public Huge rate
    {
        get { return 1.0; }
    }
    public Source[] sources
    {
        get { return m_Sources; }
    }

    public BasicEvent<Huge> onNumberChanged { get; set; }

    private void Awake()
    {
        m_EventDispatcher = FindObjectOfType<EventDispatcher>();
        onNumberChanged = new BasicEvent<Huge>();

        LoadData();
    }

    private void LoadData()
    {
        number = new Huge(double.Parse(PlayerPrefs.GetString("Number", "0")));

        m_Sources = new Source[Source.Count];
        for (int i = 0; i < Source.Count; i++) {
            m_Sources[i] = new Source(i, PlayerPrefs.GetInt("SourceLevel" + i, 0));
        }
    }

    private new void OnDestroy()
    {
        base.OnDestroy();
        SaveData();
    }

    private void Update()
    {
        number += rate * Time.deltaTime;
    }

    private void OnApplicationPause(bool paused)
    {
        if (paused) {
            Pause();
        }

        SaveData();
    }

    private void SaveData()
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
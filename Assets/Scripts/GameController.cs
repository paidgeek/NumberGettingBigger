using System;
using System.Collections;
using System.Globalization;
using System.Net.Sockets;
using GooglePlayGames;
using UnityEngine;

public class GameController : Singleton<GameController>
{
    private EventDispatcher m_EventDispatcher;
    private int m_Coins;
    private Huge m_Number;
    private Source[] m_Sources;
    private Huge m_Rate;

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
        get { return m_Rate; }
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

        m_Rate = 1.0;
        LoadData();
    }

    private void LoadData()
    {
        number = new Huge(double.Parse(PlayerPrefs.GetString("Number", "0")));

        m_Sources = new Source[Source.Count];
        for (int i = 0; i < Source.Count; i++) {
            m_Sources[i] = new Source(i, PlayerPrefs.GetInt("SourceLevel" + i, 0));
        }

        UpdateRate();
    }

    private void SaveData()
    {
        /*
        PlayerPrefs.SetString("Number", number.value.ToString(new CultureInfo("en-GB")));

        for (var i = 0; i < m_Sources.Length; i++) {
            var s = m_Sources[i];
            PlayerPrefs.SetInt("SourceLevel" + i, s.level);
        }

        PlayerPrefs.Save();
        */
    }

    private void UpdateRate()
    {
        m_Rate = 1.0;

        for (int i = 0; i < m_Sources.Length; i++) {
            m_Rate += m_Sources[i].rate;
        }
    }

    public bool CanExchange(Source source)
    {
        return number >= source.cost;
    }

    public void Exchange(Source source)
    {
        number -= source.cost;
        source.level++;
        UpdateRate();
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

    public void Pause()
    {
        m_EventDispatcher.Invoke(EventId.PauseGame);
    }

    public void Continue()
    {
        m_EventDispatcher.Invoke(EventId.ContinueGame);
    }

    public void SaveToCloud()
    {
        if (!Social.localUser.authenticated) {
            return;
        }

        Social.ReportScore(BitConverter.DoubleToInt64Bits(number), GooglePlayIds.leaderboardBiggestNumber, success => {});
    }
}
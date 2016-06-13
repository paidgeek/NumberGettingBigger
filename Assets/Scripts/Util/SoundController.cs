using System.Collections.Generic;
using UnityEngine;

public class SoundController : Singleton<SoundController>
{
    private IDictionary<string, AudioClip> m_AudioClips;
    private AudioSource m_AudioSource;

    private void Start()
    {
        var clips = Resources.LoadAll<AudioClip>("Audio");
        m_AudioClips = new Dictionary<string, AudioClip>();

        for (var i = 0; i < clips.Length; i++) {
            var clip = clips[i];
            m_AudioClips[clip.name] = clip;
        }

        m_AudioSource = gameObject.AddComponent<AudioSource>();
    }

    public void Play(string clipName)
    {
        if (m_AudioClips == null || !GameSettings.instance.isSoundOn) {
            return;
        }

        m_AudioSource.clip = m_AudioClips[clipName];
        m_AudioSource.Play();
    }
}

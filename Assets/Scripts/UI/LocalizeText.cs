using UnityEngine;
using UnityEngine.UI;

public class LocalizeText : MonoBehaviour
{
    [SerializeField]
    private Text m_Text;
    [SerializeField]
    private string m_Key;
    [SerializeField]
    private bool m_UpperCase;
    [SerializeField]
    private string m_Before;
    [SerializeField]
    private string m_After;

    private void Start()
    {
        Localize();
    }

    [ContextMenu("Localize")]
    public void Localize()
    {
        m_Text.text = m_Before + Localization.GetText(m_Key) + m_After;

        if (m_UpperCase) {
            m_Text.text = m_Text.text.ToUpperInvariant();
        }
    }
}
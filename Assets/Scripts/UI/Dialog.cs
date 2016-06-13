using UnityEngine;
using UnityEngine.UI;

public class Dialog : Singleton<Dialog>
{
    [SerializeField]
    private Text m_Text;
    [SerializeField]
    private Text[] m_ButtonTexts;
    private System.Action[] m_Actions;

    public void Show(string text, string[] buttons, System.Action[] actions)
    {
        if (buttons.Length == 0 || buttons.Length != actions.Length) {
            Debug.LogError("Invalid button count");
            return;
        }

        for (int i = 0; i < m_ButtonTexts.Length; i++) {
            m_ButtonTexts[i].transform.parent.gameObject.SetActive(i < buttons.Length);
        }
        gameObject.SetActive(true);

        m_Text.text = text;
        m_Actions = actions;

        for (int i = 0; i < buttons.Length; i++) {
            m_ButtonTexts[i].text = buttons[i];
        }
    }

    public void OnClick(int button)
    {
        gameObject.SetActive(false);

        if (m_Actions[button] != null) {
            m_Actions[button].Invoke();
        }
    }
}
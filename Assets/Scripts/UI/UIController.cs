using UnityEngine;

public class UIController : MonoBehaviour
{
    [SerializeField]
    private DataBindContext m_DataBindContext;

    private void Start()
    {
        GameController.instance.onNumberChanged.AddListener(OnNumberChanged);
    }

    private void OnNumberChanged(Huge number)
    {
        if (number.power >= 3) {
            m_DataBindContext["number"] = number.firstDigits.ToString("###.###");
            m_DataBindContext["numberName"] = Localization.GetText("Power" + number.power);
        } else {
            m_DataBindContext["number"] = (int) number.firstDigits;
        }
    }
}
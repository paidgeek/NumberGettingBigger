using UnityEngine;

public class UIController : MonoBehaviour
{
    [SerializeField]
    private DataBindContext m_DataBindContext;

    private void Start()
    {
        var gc = GameController.instance;

        gc.onNumberChanged.AddListener(OnNumberChanged);
        var sources = new ObservableList("sources");

        for (var i = 0; i < gc.sources.Length; i++) {
            var s = gc.sources[i];
            sources.Add(s);
        }

        m_DataBindContext["sources"] = sources;
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
using UnityEngine;
using UnityEngine.UI;

public class SourceView : MonoBehaviour, IModel
{
    [SerializeField]
    private Button m_ExchangeButton;
    public SourceViewModel viewModel
    {
        get { return (SourceViewModel) model; }
    }

    public object model { get; set; }

    private void Start()
    {
        m_ExchangeButton.interactable = GameController.instance.CanExchange(viewModel.source);
    }

    private void Update()
    {
        m_ExchangeButton.interactable = GameController.instance.CanExchange(viewModel.source);
    }

    public void OnExchangeClick()
    {
        UIController.instance.ExchangeSource(viewModel.source);
        m_ExchangeButton.interactable = GameController.instance.CanExchange(viewModel.source);

        var context = new DataContext();
        var bindables = GetComponentsInChildren<IBindable>();
        var properties = viewModel.GetType()
                                  .GetProperties();

        for (var j = 0; j < properties.Length; j++) {
            var p = properties[j];

            context["s." + p.Name] = p.GetValue(viewModel, null);
        }

        for (var i = 0; i < bindables.Length; i++) {
            bindables[i].Bind(context);
        }
    }
}
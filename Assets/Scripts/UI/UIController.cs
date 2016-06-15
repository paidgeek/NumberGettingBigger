using UnityEngine;

public class UIController : Singleton<UIController>
{
    [SerializeField]
    private DataBindContext m_DataBindContext;
    private ObservableList m_Sources;

    private void Awake()
    {
        BackendController.instance.logInStateChanged += OnLogInStateChanged;
    }

    private void Start()
    {
        var gc = GameController.instance;

        gc.onNumberChanged.AddListener(OnNumberChanged);
        m_Sources = new ObservableList("sources");

        for (var i = 0; i < gc.sources.Length; i++) {
            var s = gc.sources[i];
            m_Sources.Add(new SourceViewModel(s));
        }

        m_DataBindContext["sources"] = m_Sources;
    }

    private void OnLogInStateChanged(bool loggedIn)
    {
        if (loggedIn) {
            var user = BackendController.instance.localUser;

            m_DataBindContext["userName"] = user.name;
            if (user.hasPicture) {
                StartCoroutine(Util.FetchSprite("http://graph.facebook.com/" + user.id + "/picture?type=large", sprite =>
                {
                    if (sprite) {
                        m_DataBindContext["userPicture"] = sprite;
                    }
                }));
            }
        }
    }

    public void ExchangeSource(Source source)
    {
        if (GameController.instance.CanExchange(source)) {
            GameController.instance.Exchange(source);
        }
    }

    private void OnNumberChanged(Huge number)
    {
        if (number.power >= 3) {
            m_DataBindContext["number"] = number.firstDigits.ToString("###.###") + "\n<size=50>" +
                                          Localization.GetText("Power" + number.power) + "</size>";
        } else {
            m_DataBindContext["number"] = (int) number.firstDigits;
        }
    }

    public void OnShareClick()
    {
        var text = string.Format(Localization.GetText("ShareNumber"),
            "https://play.google.com/store/apps/details?id=" + Application.bundleIdentifier);

        NativeShare.ShareScreenshotWithText(text);
    }

    public void OnLogInClick()
    {
        BackendController.instance.LogIn();
    }
}
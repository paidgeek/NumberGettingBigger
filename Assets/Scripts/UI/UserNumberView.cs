using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class UserNumberView : MonoBehaviour, IModel
{
    [SerializeField]
    private Image m_Picture;
    public UserNumberViewModel viewModel
    {
        get { return (UserNumberViewModel) model; }
    }
    public object model { get; set; }

    private void Start()
    {
        if (viewModel.hasPicture) {
            var url = "http://graph.facebook.com/" + viewModel.id + "/picture?type=large";

            StartCoroutine(Util.FetchSprite(url, sprite =>
            {
                if (sprite) {
                    m_Picture.sprite = sprite;
                }
            }));
        }
    }
}
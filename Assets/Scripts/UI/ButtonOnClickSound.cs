using UnityEngine;

public class ButtonOnClickSound : MonoBehaviour
{
    public void OnClick()
    {
        SoundController.instance.Play("Click");
    }
}
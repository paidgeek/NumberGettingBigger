using UnityEngine;
using UnityEngine.EventSystems;

public class ButtonOnClickSound : MonoBehaviour, IPointerClickHandler
{
    public void OnPointerClick(PointerEventData eventData)
    {
        SoundController.instance.Play("Click");
    }
}
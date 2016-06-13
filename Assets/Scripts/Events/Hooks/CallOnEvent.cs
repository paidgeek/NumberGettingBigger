using UnityEngine;
using UnityEngine.Events;

public class CallOnEvent : BehaviourHook
{
    [SerializeField]
    private UnityEvent m_Call;

    protected override void OnInvoke()
    {
        if (m_Call != null) {
            m_Call.Invoke();
        }
    }
}
using UnityEngine;
using System.Collections;

public abstract class BehaviourHook : MonoBehaviour, IEventHook
{
	[SerializeField]
	protected EventId m_EventId;

	public void OnInvoke(EventId eventId)
	{
		if (m_EventId == eventId) {
			OnInvoke();
		}
	}

	protected virtual void OnInvoke() {}
}

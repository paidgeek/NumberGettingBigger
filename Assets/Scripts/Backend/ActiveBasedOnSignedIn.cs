using UnityEngine;

public class ActiveBasedOnSignedIn : MonoBehaviour
{
    [SerializeField]
    private bool m_ActiveWhenSignedIn;

    public static void NotifyStateChanged(bool signedIn)
    {
        var objects = Resources.FindObjectsOfTypeAll<ActiveBasedOnSignedIn>();

        for (var i = 0; i < objects.Length; i++) {
            var obj = objects[i];
            obj.gameObject.SetActive(obj.m_ActiveWhenSignedIn == signedIn);
        }
    }
}
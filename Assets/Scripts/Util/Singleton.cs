using UnityEngine;

/// <summary>
///    http://wiki.unity3d.com/index.php/Singleton
/// </summary>
public class Singleton<T> : MonoBehaviour where T : MonoBehaviour
{
	private static T s_Instance;

	private static readonly object s_Lock = new object();

	private static bool applicationIsQuitting;

	public static T instance
	{
		get
		{
			if (applicationIsQuitting) {
				return null;
			}

			lock (s_Lock) {
				if (s_Instance == null) {
					s_Instance = (T) FindObjectOfType(typeof (T));

					if (FindObjectsOfType(typeof (T)).Length > 1) {
						return s_Instance;
					}

					if (s_Instance == null) {
						var singleton = new GameObject();
						s_Instance = singleton.AddComponent<T>();
						singleton.name = "(Singleton) " + typeof (T);

						DontDestroyOnLoad(singleton);
					}
				}

				return s_Instance;
			}
		}
	}

	public static bool isDestroyed
	{
		get { return instance == null; }
	}

	/// <summary>
	///    When Unity quits, it destroys objects in a random order.
	///    In principle, a Singleton is only destroyed when application quits.
	///    If any script calls Instance after it have been destroyed,
	///    it will create a buggy ghost object that will stay on the Editor scene
	///    even after stopping playing the Application. Really bad!
	///    So, this was made to be sure we're not creating that buggy ghost object.
	/// </summary>
	protected void OnDestroy()
	{
		applicationIsQuitting = true;
	}

	protected void OnEnable()
	{
		applicationIsQuitting = false;
	}
}
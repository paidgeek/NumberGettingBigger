using UnityEditor;
using UnityEngine;

public class DeleteAllPrefs : MonoBehaviour
{
    [MenuItem("Edit/Delete All Prefs")]
    public static void Perform()
    {
        PlayerPrefs.DeleteAll();
        PlayerPrefs.Save();
    }
}
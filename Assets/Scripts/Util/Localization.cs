using System.Collections;
using System.Collections.Generic;
using Newtonsoft.Json;
using UnityEngine;

public class Localization
{
    private static readonly IDictionary<string, string> s_Content = new Dictionary<string, string>();
    private static string s_Language = "en";

    private static string Language
    {
        get { return s_Language; }
        set
        {
            if (s_Language != value) {
                s_Language = value;
                CreateContent();
            }
        }
    }

    private static IDictionary<string, string> Content
    {
        get
        {
            if (s_Content == null || s_Content.Count == 0) {
                CreateContent();
            }

            return s_Content;
        }
    }

    public static string GetText(string key)
    {
#if UNITY_EDITOR
        CreateContent();
#endif

        var result = "";
        Content.TryGetValue(key, out result);

        if (string.IsNullOrEmpty(result)) {
            return key + "[" + Language + "]" + " No Text defined";
        }

        return result;
    }

    public static string GetLanguage()
    {
        return Language;
    }

    public static void SetLanguage(string language)
    {
        Language = language;
    }

    private static IDictionary<string, string> GetContent()
    {
        if (s_Content == null || s_Content.Count == 0) {
            CreateContent();
        }

        return s_Content;
    }

    private static void CreateContent()
    {
        foreach (var asset in Resources.LoadAll("Localization")) {
            var data = (IDictionary) JsonConvert.DeserializeObject<Dictionary<string, IDictionary>>(asset.ToString());

            foreach (var key in data.Keys) {
                if (s_Language == (string) key) {
                    var vals = (IDictionary) data[key];

                    foreach (var valKey in vals.Keys) {
                        s_Content[(string) valKey] = (string) vals[valKey];
                    }
                }
            }
        }
    }
}
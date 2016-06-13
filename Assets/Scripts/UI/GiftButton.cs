using System.Collections;
using Heyzap;
using UnityEngine;
using UnityEngine.Advertisements;

public class GiftButton : MonoBehaviour
{
    [SerializeField]
    private DataBindContext m_DataBindContext;
    [SerializeField]
    private GameObject m_Button;

    private void OnEnable()
    {
        m_Button.SetActive(false);
        HZIncentivizedAd.Fetch();
        StartCoroutine(LoadAdCoroutine());

        HZIncentivizedAd.AdDisplayListener listener = (adState, adTag) => {
            if (adState == "incentivized_result_complete") {
            }
        };
        HZIncentivizedAd.SetDisplayListener(listener);
    }

    public void OnClick()
    {
        if (!HZIncentivizedAd.IsAvailable()) {
            return;
        }

        m_Button.SetActive(false);
        
        HZIncentivizedAd.Show();
        HZIncentivizedAd.Fetch();
    }

    private IEnumerator LoadAdCoroutine()
    {
        while (!HZIncentivizedAd.IsAvailable()) {
            yield return new WaitForSeconds(0.5f);
        }

        m_Button.SetActive(true);
    }
}
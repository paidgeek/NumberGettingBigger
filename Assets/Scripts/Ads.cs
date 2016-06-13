using Heyzap;

public class Ads : Singleton<Ads>
{
    public void ShowBanner()
    {
        var showOptions = new HZBannerShowOptions();
        showOptions.Position = HZBannerShowOptions.POSITION_BOTTOM;

        HZBannerAd.ShowWithOptions(showOptions);
    }

    public void HideBanner()
    {
        HZBannerAd.Hide();
        HZBannerAd.Destroy();
    }

    public void ShowInterstitial()
    {
        HZInterstitialAd.Show();
    }
}
public class SourceViewModel
{
    public SourceViewModel(Source source)
    {
        this.source = source;
    }

    public Source source { get; private set; }
    public int level
    {
        get { return source.level; }
    }
    public double rate
    {
        get { return source.rate.firstDigits; }
    }
    public string rateName
    {
        get
        {
            if (source.rate.power >= 3) {
                return Localization.GetText("Power" + source.rate.power);
            }

            return "";
        }
    }
    public double cost
    {
        get { return source.cost.firstDigits; }
    }
    public string costName
    {
        get
        {
            if (source.cost.power >= 3) {
                return Localization.GetText("Power" + source.cost.power);
            }

            return "";
        }
    }
}
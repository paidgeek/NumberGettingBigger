using System;

public class Source
{
    public const int Count = 50;
    private const double Multiplier = 1.15;

    public Source(int index, int level)
    {
        this.index = index;
        this.level = level;
    }

    public int index { get; set; }
    public int level { get; set; }

    public Huge baseCost {
        get
        {
            var n = Math.Pow(5, index);

            return Util.PrettyNumber(n);
        }
    }

    public Huge baseRate
    {
        get
        {
            var n = Math.Pow(3, index);

            return Util.PrettyNumber(n);
        }
    }

    public Huge rate
    {
        get { return baseRate * level; }
    }

    public Huge cost
    {
        get
        {
            var cost = baseCost * Math.Pow(Multiplier, level);
            return Util.PrettyNumber(cost);
        }
    }
}
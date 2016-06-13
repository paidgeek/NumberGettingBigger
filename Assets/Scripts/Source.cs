public class Source
{
    public const int Count = 20;

    public Source(int index, int level)
    {
        this.index = index;
        this.level = level;
    }

    public int index { get; set; }
    public int level { get; set; }

    public Huge rate
    {
        get { return index * 3 + level; }
    }

    public Huge cost
    {
        get { return level * 2; }
    }
}
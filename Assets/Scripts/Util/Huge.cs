using System;

public struct Huge
{
    public double value;

    public Huge(double value)
    {
        this.value = value;
    }

    public int power
    {
        get { return (int) Math.Log10(value) / 3 * 3; }
    }

    public double firstDigits
    {
        get { return value / Math.Pow(10.0, power); }
    }

    public override string ToString()
    {
        return value.ToString("N0");
    }

    public static implicit operator Huge(double value)
    {
        return new Huge(value);
    }

    public static implicit operator double(Huge huge)
    {
        return huge.value;
    }

    public static Huge operator ++(Huge huge)
    {
        huge.value++;

        return huge;
    }

    public static Huge operator --(Huge huge)
    {
        huge.value--;

        return huge;
    }

    public static Huge Parse(string s)
    {
        return new Huge(double.Parse(s));
    }
}
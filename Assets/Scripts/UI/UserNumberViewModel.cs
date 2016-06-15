using UnityEngine;

public class UserNumberViewModel
{
    public UserNumberViewModel(string id,string name, string number, double hugeNumber, bool hasPicture)
    {
        this.id = id;
        this.name = name;
        this.number = number;
        this.hugeNumber = hugeNumber;
        this.hasPicture = hasPicture;
    }

    public string id { get; set; }
    public string name { get; set; }
    public string number { get; set; }
    public double hugeNumber { get; set; }
    public bool hasPicture { get; set; }
}

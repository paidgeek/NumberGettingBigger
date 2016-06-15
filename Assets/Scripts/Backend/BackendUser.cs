using UnityEngine;

public class BackendUser
{
    public string id { get; set; }
    public double number { get; set; }
    public string name { get; set; }
    public bool hasPicture { get; set; }

    public BackendUser(string id, double number, string name)
    {
        this.id = id;
        this.number = number;
        this.name = name;
    }
}
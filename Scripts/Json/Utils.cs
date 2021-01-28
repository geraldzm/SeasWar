using System;
using UnityEngine;
using UnityEngine.UI;

public class Utils
{
    public static string[] Fighters = new string[] {
        "Manta negra", "Poseidon", "Aquaman", "Jack Sparrow", "Davy Jones"
    };

    public static int getFighterIndex(string fighter)
    {
        for (int i = 0; i < Fighters.Length; i++)
            if (Fighters[i] == fighter) return i;

        return -1;
    }

    public static IDMessage getMessage(string message)
    {
        switch (message)
        {
            case "DONE":
                return IDMessage.DONE;
            case "ADMIN":
                return IDMessage.ADMIN;
            case "MESSAGE":
                return IDMessage.MESSAGE;
            case "LOGBOOK":
                return IDMessage.LOGBOOK;
            case "ACCEPTED":
                return IDMessage.ACCEPTED;
            case "RESPONSE":
                return IDMessage.RESPONSE;
            case "REJECTED":
                return IDMessage.REJECTED;
            case "REQUESTNAME":
                return IDMessage.REQUESTNAME;
            case "WRONGNAME":
                return IDMessage.WRONGNAME;
            case "STARTED":
                return IDMessage.STARTED;
            case "ID":
                return IDMessage.ID;
            case "REQUESTCHARACTERS":
                return IDMessage.REQUESTCHARACTERS;
            case "INITMATRIX1":
                return IDMessage.INITMATRIX1;
            case "INITMATRIX2":
                return IDMessage.INITMATRIX2;
            default:
                return IDMessage.NONE;
        }
    }
    public static Text AddTextToCanvas(string textString, GameObject canvasGameObject)
    {
        GameObject holder = new GameObject("ChatText");
        holder.transform.SetParent(canvasGameObject.transform);

        Text text = holder.AddComponent<Text>();
        text.text = textString;

        Font ArialFont = (Font) Resources.GetBuiltinResource(typeof(Font), "Arial.ttf");
        text.font = ArialFont;
        text.material = ArialFont.material;

        return text;
    }
}
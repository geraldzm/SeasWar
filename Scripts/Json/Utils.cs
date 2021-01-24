using System;
using UnityEngine;
using UnityEngine.UI;

public class Utils
{
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
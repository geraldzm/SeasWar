using System;

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
}
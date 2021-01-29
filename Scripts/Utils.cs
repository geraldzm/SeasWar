using System;
using System.Text.RegularExpressions;
using UnityEngine;
using UnityEngine.UI;

public class Utils
{
    public static string[] Fighters = new string[] {
        "MantaNegra", "Poseidon", "Aquaman", "JackSparrow", "DavyJones"
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
            case "MATRIX":
                return IDMessage.MATRIX;
            case "GETFIGHTER":
                return IDMessage.GETFIGHTER;
            case "FINISHTURN":
                return IDMessage.FINISHTURN;
            case "VOLCANO":
                return IDMessage.VOLCANO;
            case "GARBAGE":
                return IDMessage.GARBAGE;
            case "WAVES":
                return IDMessage.WAVES;
            case "NUMBERS":
                return IDMessage.NUMBERS;
            case "ATTACKLOG":
                return IDMessage.ATTACKLOG;
            case "LOOSER":
                return IDMessage.LOOSER;
            case "TURN":
                return IDMessage.TURN;
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

    public static string[] ParseCommand(string message)
    {
        var val = message.ToLower();

        string[] single = ParseSingle(val);

        if (single != null) 
        {
            return single;
        } 
        else if (val.StartsWith("chat") || val.StartsWith("to"))
        {
            return ParseChatMessage(message);
        }
        else if (val.StartsWith("show"))
        {
            return ParseShow(message);
        }
        else if (val.StartsWith("enemy"))
        {
            return ParseEnemy(message);
        }
        else if (val.StartsWith("attack"))
        {
            return ParseAttack(message);
        }
        
        return null;
    }

    // Dado un comando de ataque parsea los datos de acuerdo a ello
    private static string[] ParseAttack(string message)
    {
        var match = Regex.Matches(message, @"(\w)+");

        if (match.Count < 4) return null;

        string val = match[3].Value.ToLower();

        // Caso kraken
        if (val == "tentacle" || val == "breath" || val == "releasekraken")
        {
            return ParseKraken(message);
        }
        // Caso Tridente poseidon
        else if (val == "threelines" || val == "threenumbers")
        {
            return ParsePoseidon(message);
        }
        // Caso Fish Telepathy
        else if (val == "cardumen" || val == "sharkattack" || val == "pulp")
        {
            return ParseFish(message);
        }
        // Undersea firse
        else if (val == "volcanoraising" || val == "volcanoexplosion" || val == "termalrush") 
        {
            return ParseVolcano(message);
        }
        // Thunders under the sea
        else if (val == "thunderrain" || val == "poseidonthunders" || val == "eelattack")
        {
            return ParseSea(message);
        }
        // Waves control
        else if (val == "swirlraising" || val == "sendhuman" || val == "radioactive")
        {
            return ParseWaves(message);
        }

        return null;
    }

    // Parsea los comandos de Waves Under Control
    private static string[] ParseWaves(string message)
    {
        var match = Regex.Matches(message, @"(\w)+");

        string val = match[3].Value.ToLower();

        if (val == "swirlraising" || val == "sendhuman")
        {
            if (match.Count != 6) return null;

            if (!ValidatePoint(new string[] { match[4].Value, match[5].Value })) return null;
        }
        else if (val == "radioactive" && match.Count != 3)
        {
            return null;
        }

        return MatchToArray(match);
    }

    // Parse los comandos de Thunders under the sea
    private static string[] ParseSea(string message)
    {
        var match = Regex.Matches(message, @"(\w)+");

        if (match.Count != 4)
        {
            return null;
        }

        return MatchToArray(match);
    }

    // Parse los comandos de Volcano
    private static string[] ParseVolcano(string message)
    {
        var match = Regex.Matches(message, @"(\w)+");

        string val = match[3].Value.ToLower();

        if (val == "volcanoraising" && match.Count != 4)
        {
            return null;
        }
        else if (val == "volcanoexplosion" || val == "termalrush")
        {
            if (match.Count != 6) return null;

            if (!ValidatePoint(new string[] { match[4].Value, match[5].Value})) return null;
        }

        return MatchToArray(match);
    }

    // Parse los comandos de Fish
    private static string[] ParseFish(string message)
    {
        var match = Regex.Matches(message, @"(\w)+");

        string val = match[3].Value.ToLower();

        if (val == "cardumen" && match.Count != 4)
        {
            return null;
        }
        else if ((val == "sharkattack" || val == "pulp") && match.Count != 4)
        {
            return null;
        }

        return MatchToArray(match);
    }

    // Parse los comandos de Poseidon
    private static string[] ParsePoseidon(string message)
    {
        var match = Regex.Matches(message, @"(\w)+");

        string val = match[3].Value.ToLower();

        if (val == "threelines")
        {
            if (match.Count != 10) return null;

            // Validamos los tres puntos
            if (!ValidatePoint(new string[] { match[4].Value, match[5].Value })) return null;
            if (!ValidatePoint(new string[] { match[6].Value, match[7].Value })) return null;
            if (!ValidatePoint(new string[] { match[8].Value, match[9].Value })) return null;
        }
        else if (val == "threenumbers")
        {
            if (match.Count != 7) return null;

            if (!ValidateNumber(match[4].Value, 0, 9)) return null;
            if (!ValidateNumber(match[5].Value, 0, 9)) return null;
            if (!ValidateNumber(match[6].Value, 0, 9)) return null;
        }

        return MatchToArray(match);
    }

    // Parse los comandos del kraken
    private static string[] ParseKraken(string message)
    {
        var match = Regex.Matches(message, @"(\w)+");

        string val = match[2].Value.ToLower();

        if (val == "tentacle")
        {
            if (match.Count != 10) return null;

            // Validamos los tres puntos
            if (!ValidatePoint(new string[] { match[4].Value, match[5].Value })) return null;
            if (!ValidatePoint(new string[] { match[6].Value, match[7].Value })) return null;
            if (!ValidatePoint(new string[] { match[8].Value, match[9].Value })) return null;
        }
        else if (val == "breath")
        {
            if (match.Count != 7) return null;

            // Validamos las direcciones
            string dir = match[4].Value.ToLower();

            if (dir != "up" && dir != "down" && dir != "left" && dir != "right" && dir != "all") return null;

            // Validamos que el punto del respiro sea correcto
            if (!ValidatePoint(new string[] { match[5].Value, match[6].Value })) return null;
        }
        else if (val == "releasekraken")
        {
            if (match.Count != 5 || !ValidateNumber(match[4].Value, 1, 9)) return null;
        }

        return MatchToArray(match);
    }

    private static string[] ParseSingle(string message)
    {
        string[] single = new string[] { "skip", "giveup", "log", "logsum", "showbussy", "showpercentage" };

        for (int i = 0; i < single.Length; i++)
        {
            if (message.StartsWith(single[i]))
                return new string[] { single[i] };
        }

        return null;
    }

    // Parsea el comando para un enemigo (enemy / nombre)
    private static string[] ParseEnemy(string message)
    {
        string[] rst = new string[2] { "", ""};

        var match = Regex.Matches(message, @"(\w)+");

        if (match.Count != 2) return null;

        rst[0] = match[0].Value;
        rst[1] = match[1].Value;

        return rst;
    }

    private static string[] ParseShow(string message)
    {
        string[] rst = new string[3] { "", "", "" };

        // Vamos a extraer todas las palabras
        // la primera sera el keyword, el segundo y tercero numeros (col/fila)
        var match = Regex.Matches(message, @"(\w)+");

        if (match.Count != 3) return null;

        if (match.Count > 0)
        {
            for (int i = 0; i < 3; i++)
            {
                rst[i] = match[i].Value;
            }
        }

        if (!ValidatePoint(new string[] { rst[1], rst[2] })) return null;

        return rst;
    }

    private static string[] ParseChatMessage(string message)
    {
        string[] rst;

        // Vamos a extraer todas las palabras
        // la primera sera el keyword y el resto el mensaje
        var match = Regex.Matches(message, @"(\w)+");

        if (match.Count <= 0) return null;

        if (message.StartsWith("chat"))
        {
            rst = new string[2] { "", "" };

            for (int i = 0; i < match.Count; i++)
            {
                int index = (i == 0) ? 0 : 1;
                string extra = (i == 0) ? "" : " ";

                rst[index] += match[i].Value + extra;
            }

            return rst;
        }
        else if (message.StartsWith("to"))
        {
            rst = new string[3] { "", "", "" };

            for (int i = 0; i < match.Count; i++)
            {
                if (i == 0 || i == 1)
                {
                    rst[i] = match[i].Value;
                }
                else
                {
                    rst[i] += match[i].Value;
                }
            }

            return rst;
        }

        return null;
    }

    private static string[] MatchToArray(MatchCollection match)
    {
        string[] builded = new string[match.Count];

        for (int i = 0; i < match.Count; i++)
        {
            builded[i] = match[i].Value;
        }

        return builded;
    }

    private static bool ValidateNumber(string number, int min, int max)
    {
        try
        {
            int test = int.Parse(number);

            if (min <= test && test <= max) return true;
        }
        catch (FormatException)
        {
            return false;
        }

        return false;
    }

    private static bool ValidatePoint(string[] point)
    {
        for (int i = 0; i < 2; i++)
        {
            try
            {
                int test = int.Parse(point[i]);

                if (test < 0) throw new FormatException("Numero negativo");

                if (i == 1 && test >= Matrix.rows) throw new FormatException("Out of bounds");

                if (i == 2 && test >= Matrix.cols) throw new FormatException("Out of bounds");
            }
            catch (FormatException)
            {
                return false;
            }
        }

        return true;
    }
}
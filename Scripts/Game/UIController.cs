using System.Text.RegularExpressions;
using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIController : MonoBehaviour
{
    private Button sendButton;

    private GameObject chatContainer;
    private GameObject logbookContainer;
    private GameObject heroesContainer;
    private GameObject attackContainer;

    private Text playerName;

    public GameObject chatPrefab;

    public Sprite[] warriors;

    public static Sprite[] staticWarriors;
    public Sprite[] tiles;

    private bool responseNumber = false;

    private Dictionary<string, GameObject> warriorData = new Dictionary<string, GameObject>();

    // Start is called before the first frame update
    void Start()
    {
        staticWarriors = warriors;

        chatContainer = GameObject.Find("ChatContainer");
        logbookContainer = GameObject.Find("LogbookContainer");
        attackContainer = GameObject.Find("AttackContent");
        heroesContainer = GameObject.Find("ContentHeroes");
        playerName = GameObject.Find("PlayerName").GetComponent<Text>();

        sendButton = GameObject.Find("ButtonSend").GetComponent<Button>();
        sendButton.onClick.AddListener(OnBtnSendClick);

        playerName.text = "Jugador: " + Network.name;

        AppendWarriors();
    }

    // Update is called once per frame
    void Update()
    {
        if (Network.LogbookMessage != "")
        {
            AddLogbookMessage(Network.LogbookMessage);

            Network.LogbookMessage = "";
        }

        if (Network.GlobalMessage != "")
        {
            AddChatMessage(Network.GlobalMessage);

            Network.GlobalMessage = "";
        }

        if (Network.AttackMessage != "")
        {
            AddAttackMessage(Network.AttackMessage);

            Network.AttackMessage = "";
        }

        if (Network.askingNumbers != -1)
        {
            AddChatMessage("Estas bajo ataque!, responde con tres numeros del 0 al 9! ");

            responseNumber = true;
            Network.askingNumbers = -1;
        }

        if (Network.updateWarrior != "")
        {
            UpdateWarriorData(Network.updateWarrior);

            Network.updateWarrior = "";
        }
    }

    // Permite agregar mensajes al chat
    public void AddChatMessage(string message)
    {
        GameObject newMessage = Instantiate(chatPrefab, chatContainer.transform);
        Text content = newMessage.GetComponent<Text>();

        content.text = message;
    }

    // Permite agregar mensajes a la bitacora
    public void AddLogbookMessage(string message)
    {
        GameObject newMessage = Instantiate(chatPrefab, logbookContainer.transform);
        Text content = newMessage.GetComponent<Text>();

        content.text = message;
    }

    // Permite agregar mensajes de ataque
    public void AddAttackMessage(string message)
    {
        GameObject newMessage = Instantiate(chatPrefab, attackContainer.transform);
        Text content = newMessage.GetComponent<Text>();

        content.text = message;
    }

    // Permite enviar mensajes al chat
    public void OnBtnSendClick()
    {
        if (responseNumber && ResponseNumbers())
        {
            string text = GameObject.Find("TextChat").GetComponent<Text>().text;

            var match = Regex.Matches(text, @"(\w)+");

            Message message = new Message {
                numbers = new int[] { int.Parse(match[0].Value), int.Parse(match[1].Value), int.Parse(match[2].Value) },
                id = Network.PlayerID,
                idMessage = "RESPONSE"
            };

            Network.getInstance().SendMessage(message);

            responseNumber = false;
        }
        else
        {
            NormalBtnClick();
        }
    }

    // Funcion para obtener los numeros del ataque de trident
    private bool ResponseNumbers()
    {
        try
        {
            string text = GameObject.Find("TextChat").GetComponent<Text>().text;

            var match = Regex.Matches(text, @"(\w)+");

            if (match.Count != 3) return false;

            string[] data = new string[] { match[0].Value, match[1].Value, match[2].Value };

            for (int i = 0; i < data.Length; i++) {
                int val = int.Parse(data[i]);

                if (0 > val || val > 9) return false;
            }

            return true;
        }
        catch (FormatException)
        {
            AddChatMessage("Ingrese tres numeros validos...");

            return false;
        }
    }

    private void NormalBtnClick()
    {
        string text = GameObject.Find("TextChat").GetComponent<Text>().text;

        string[] parsed = Utils.ParseCommand(text);

        if (parsed == null)
        {
            AddChatMessage("Game: Formato de mensaje incorrecto...");

            return;
        }

        if (parsed.Length >= 3 && parsed[2].ToLower() == Network.name.ToLower())
        {
            AddChatMessage("Game: No te puedes herir a ti mismo :c quierete we"); 

            return;
        }

        Message message;

        switch (parsed[0].ToLower())
        {
            case "attack":
                if (!Network.isUIEnabled) return;

                message = new Message
                {
                    idMessage = "ATTACK",
                    id = Network.PlayerID,
                    texts = parsed
                };

                Network.getInstance().SendMessage(message);

                break;
            case "chat":
            case "to:":
                message = new Message
                {
                    id = Network.PlayerID,
                    idMessage = "MESSAGE",
                    texts = parsed
                };

                Network.getInstance().SendMessage(message);
                break;
            case "skip":
                message = new Message
                {
                    id = Network.PlayerID,
                    idMessage = "SKIP"
                };

                Network.getInstance().SendMessage(message);
                break;
            case "show":
                message = new Message
                {
                    id = Network.PlayerID,
                    idMessage = "GETSTATEBOX",
                    texts = parsed
                };

                Network.getInstance().SendMessage(message);

                break;
            case "enemy":
                message = new Message
                {
                    id = Network.PlayerID,
                    idMessage = "GETSTATEENEMY",
                    texts = parsed
                };

                Network.getInstance().SendMessage(message);

                break;
            default:
                break;
        }

        AddChatMessage(text);
        GameObject.Find("TextChat").GetComponent<Text>().text = "";
    }

    // Funcion para generar los personajes recibidos en el warriors
    public void AppendWarriors()
    {
        for (int i = 0; i < Utils.Fighters.Length; i++)
        {
            if (!Network.warriors.ContainsKey(Utils.Fighters[i])) continue;

            AppendWarrior(Utils.Fighters[i]);
        }
    }

    public void UpdateWarriorData(string warriorname)
    {
        if (!warriorData.ContainsKey(warriorname) || !Network.warriors.ContainsKey(warriorname)) return;

        GameObject warrior = warriorData[warriorname];

        Fighter fighter = Network.warriors[warriorname];

        Text attack = warrior.transform.Find("FighterAttack").GetComponent<Text>();

        attack.text = "Ataques: ";

        for (int i = 0; i < fighter.attacks.Length; i++)
        {
            if (i == fighter.attacks.Length - 1)
            {
                attack.text += fighter.attacks[i];
            }
            else
            {
                attack.text += fighter.attacks[i] + ", ";
            }
        }
    }

    // Esta funcion agrega un solo luchador a la lista
    public void AppendWarrior(string warriorName)
    {
        GameObject reference = (GameObject) Instantiate(Resources.Load("GameFighter"), heroesContainer.transform);

        Fighter fighter = Network.warriors[warriorName];

        Image image = reference.transform.Find("FighterImage").GetComponent<Image>();
        Image tile = reference.transform.Find("FighterTile").GetComponent<Image>();

        Text name = reference.transform.Find("FighterName").GetComponent<Text>();
        Text porc = reference.transform.Find("FighterPorc").GetComponent<Text>();
        Text health = reference.transform.Find("FighterPower").GetComponent<Text>();
        Text power = reference.transform.Find("FighterRes").GetComponent<Text>();
        Text res = reference.transform.Find("FighterHealth").GetComponent<Text>();
        Text attack = reference.transform.Find("FighterAttack").GetComponent<Text>();

        image.sprite = staticWarriors[Utils.getFighterIndex(warriorName)];
        tile.sprite = tiles[Utils.getFighterIndex(warriorName)];

        name.text = warriorName;
        porc.text = "Porcentaje: " + fighter.per.ToString();
        health.text = "Sanidad: " + fighter.health.ToString();
        power.text = "Poder: " + fighter.power.ToString();
        res.text = "Resistencia: " + fighter.res.ToString();
        attack.text = "Ataques: ";

        for (int i = 0; i < fighter.attacks.Length; i++)
        {
            if (i == fighter.attacks.Length - 1)
            {
                attack.text += fighter.attacks[i];
            }
            else
            {
                attack.text += fighter.attacks[i] + ", ";
            }
        }

        warriorData[warriorName] = reference;
    }
}

using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIController : MonoBehaviour
{
    private Button sendButton;

    private GameObject chatContainer;
    private GameObject logbookContainer;
    private GameObject heroesContainer;

    public GameObject chatPrefab;

    // Start is called before the first frame update
    void Start()
    {
        Network.controller = this;

        chatContainer = GameObject.Find("ChatContainer");
        logbookContainer = GameObject.Find("LogbookContainer");
        heroesContainer = GameObject.Find("ContentHeroes");

        sendButton = GameObject.Find("ButtonSend").GetComponent<Button>();
        sendButton.onClick.AddListener(OnBtnSendClick);

        AppendWarriors();
    }

    // Update is called once per frame
    void Update()
    {
        
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


    // Permite enviar mensajes al chat
    public void OnBtnSendClick()
    {
        string text = GameObject.Find("TextChat").GetComponent<Text>().text;

        string[] parsed = Utils.ParseCommand(text);
            
        if (parsed == null)
        {
            AddChatMessage("Game: Formato de mensaje incorrecto...");

            return;
        }

        Message message = new Message
        {
            idMessage = "MESSAGE",
            texts = parsed
        };

        Network.getInstance().SendMessage(message);
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

    // Esta funcion agrega un solo luchador a la lista
    public void AppendWarrior(string warriorName)
    {

        GameObject reference = (GameObject)Instantiate(Resources.Load("GameFighter"), heroesContainer.transform);

        Fighter fighter = Network.warriors[warriorName];

        Text name = reference.transform.Find("FighterName").GetComponent<Text>();
        Text porc = reference.transform.Find("FighterPorc").GetComponent<Text>();
        Text health = reference.transform.Find("FighterPower").GetComponent<Text>();
        Text power = reference.transform.Find("FighterRes").GetComponent<Text>();
        Text res = reference.transform.Find("FighterHealth").GetComponent<Text>();
        Text attack = reference.transform.Find("FighterAttack").GetComponent<Text>();

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
    }
}

using UnityEngine;
using System;
using System.Threading;
using System.Text;
using SimpleTcp;
using Newtonsoft.Json;

public class Network
{
    public static Network network;

    public static bool isConnected = false;

    private static Message messageAvailable = null;

    private SimpleTcpClient client;
    private static UIController controller;

    public static int PlayerID = -1;
    public static string name = "";

    public static IDMessage lastMessage = IDMessage.NONE;

    private void Start()
    {
        // Inicializamos la conexion
        client = new SimpleTcpClient("127.0.0.1:42069");

        // set events
        client.Events.Connected += Connected;
        client.Events.Disconnected += Disconnected;
        client.Events.DataReceived += DataReceived;

        Thread listener = new Thread(new ThreadStart(Listener));

        network.Connect();
        listener.Start();
    }

    // Listener de mensajes
    private void Listener()
    {
        while (isConnected)
        {
            if (messageAvailable != null)
            {
                OnMessageReceived();
            }
        }
    }

    private void OnMessageReceived()
    {
        IDMessage id = Utils.getMessage(messageAvailable.idMessage);

        lastMessage = id;

        Debug.Log(messageAvailable.idMessage + " : " + messageAvailable.text);

        switch (id)
        {
            case IDMessage.MESSAGE:
                controller.AddChatMessage(messageAvailable.text);
                break;
            case IDMessage.STARTED:
                // TODO: El mero desvergue de cambiar de scene
                Debug.Log("El juego puede iniciar!");
                break;
            case IDMessage.REQUESTNAME:
                Debug.Log("Respondiendo el nombre...");

                Message msgName = new Message
                {
                    text = name,
                    idMessage = "RESPONSE" // TODO: Cambiar esto en utils
                };

                SendMessage(msgName);

                break;
            case IDMessage.REQUESCHARACTERS:
                Debug.Log("Enviando luchadores");

                // TODO: Mover a utils
                string[] warriorText = new string[3];

                for (int i = 0; i < 3; i++)
                    warriorText[i] = JsonConvert.SerializeObject(FighterGenerator.GetFighters()[i]);

                Message msgChar = new Message
                {
                    id = PlayerID,
                    texts = warriorText,
                    idMessage = "RESPONSE" // TODO: Cambiar esto en utils :D
                };

                SendMessage(msgChar);

                break;
            case IDMessage.ID:
                PlayerID = messageAvailable.number;
                Debug.Log("ID del jugador: " + PlayerID.ToString());

                break;
            case IDMessage.ADMIN:
                Debug.Log("Admin");
                Message admin = new Message
                {
                    number = 2, // TODO: Cambiar esto a un combo box
                    idMessage = "RESPONSE"
                };

                SendMessage(admin);

                break;
            case IDMessage.REJECTED:
                Debug.Log("F");
                // TODO: Trigger mensaje global
                Disconnect();
                break;
            default:
                Debug.Log("Mensaje no soportado: " + messageAvailable.idMessage);
                break;
        }

        messageAvailable = null;
    }

    public void SendMessage(Message message)
    {
        string json = JsonConvert.SerializeObject(message);

        client.Send(json);
    }

    public void Disconnect()
    {
        client.Disconnect();

        isConnected = false;
    }

    public void Connect()
    {
        client.Connect();

        isConnected = true;
    }

    static void Connected(object sender, EventArgs e)
    {
        Debug.Log("*** Server connected");
    }

    static void Disconnected(object sender, EventArgs e)
    {
        Debug.Log("*** Server disconnected");
    }

    static void DataReceived(object sender, DataReceivedEventArgs e)
    {
        string received = Encoding.UTF8.GetString(e.Data);

        Message message = JsonConvert.DeserializeObject<Message>(received);

        Debug.Log("Mensaje recibido");

        messageAvailable = message;
    }

    public static Network getInstance()
    {
        if (network == null)
        {
            network = new Network();

            network.Start();
        }

        return network;
    }
}
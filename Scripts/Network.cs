using UnityEngine;
using System;
using System.Text;
using SimpleTcp;
using Newtonsoft.Json;

public class Network : MonoBehaviour
{
    public bool isConnected = false;

    private static Message messageAvailable = null;

    private SimpleTcpClient client;
    private UIController controller;

    public static int PlayerID = -1;
    public static string name = "";

    public static IDMessage lastMessage = IDMessage.NONE;

    // Inicializamos el socket
    void Start()
    {
        // Inicializamos la conexion
        client = new SimpleTcpClient("127.0.0.1:42069");

        // set events
        client.Events.Connected += Connected;
        client.Events.Disconnected += Disconnected;
        client.Events.DataReceived += DataReceived;

        // Inicializamos algunos componentes
        if (GameObject.Find("UIEvents"))
            controller = GameObject.Find("UIEvents").GetComponent<UIController>();
    }

    void Update()
    {
        if (isConnected && Network.messageAvailable != null)
        {
            OnMessageReceived();
        }
    }

    private void OnMessageReceived()
    {
        IDMessage id = Utils.getMessage(messageAvailable.idMessage);

        lastMessage = id;

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
                Debug.Log("");

                Message msgChar = new Message { 
                    texts = { } // Serializar los personajes
                };

                break;
            case IDMessage.ID:
                Debug.Log("ID del jugador");
                PlayerID = messageAvailable.number;

                break;
            case IDMessage.ADMIN:
                Debug.Log("Admin");
                Message admin = new Message
                {
                    number = 2, // Cambiar esto a un combo box
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
                Debug.Log("Mensaje no soportado: " + Network.messageAvailable.idMessage);
                break;
        }

        Network.messageAvailable = null;
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

        Network.messageAvailable = message;
    }

    public void SendMessage(Message message)
    {
        string json = JsonConvert.SerializeObject(message);

        client.Send(json);
    }
}

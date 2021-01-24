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

    // Inicializamos el socket
    void Start()
    {
        // Inicializamos la conexion
        client = new SimpleTcpClient("127.0.0.1:42069");

        // set events
        client.Events.Connected += Connected;
        client.Events.Disconnected += Disconnected;
        client.Events.DataReceived += DataReceived;

        client.Connect();

        Message message = new Message();

        message.text = "Hola!";
        message.idMessage = "ADMIN";
        message.number = 5;

        string testMsg = JsonConvert.SerializeObject(message);

        client.Send(testMsg);

        // Inicializamos algunos componentes
        controller = GameObject.Find("UIEvents").GetComponent<UIController>();
    }

    // Si lo voy a usar
    void Update()
    {
        if (Network.messageAvailable != null)
        {
            IDMessage id = Utils.getMessage(messageAvailable.idMessage);

            switch (id)
            {
                case IDMessage.MESSAGE:
                    Debug.Log(Network.messageAvailable.text);
                    break;
                default:
                    Debug.Log("Mensaje no soportado: " + Network.messageAvailable.text);
                    break;
            }

            Network.messageAvailable = null;
        }
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

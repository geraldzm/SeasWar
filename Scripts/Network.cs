using UnityEngine;
using System.Net.Sockets;
using System.Net;
using System;

public class Network : MonoBehaviour
{
    public bool isConnected = false;
    private Socket clientSocket;
    private static int MAX_BUFFER = 1024;

    // Inicializamos el socket
    void Start()
    {
        IPEndPoint serverAddress = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 42069);

        clientSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
        clientSocket.Connect(serverAddress);

        // Receiving
        byte[] rcvLenBytes = new byte[MAX_BUFFER];
        clientSocket.Receive(rcvLenBytes);
        String rcv = System.Text.Encoding.ASCII.GetString(rcvLenBytes);

        Debug.Log("Client received: " + rcv);
    }

    // Si lo voy a usar
    void Update()
    {
        bool keyDown = Input.GetKeyDown(KeyCode.W);

        if (keyDown)
        {
            string toSend = "Hello!";

            // Sending
            byte[] toSendBytes = System.Text.Encoding.ASCII.GetBytes(toSend);
            clientSocket.Send(toSendBytes);
        }
    }
}

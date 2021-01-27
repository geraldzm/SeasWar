﻿using UnityEngine;
using System;
using System.Threading;
using System.Text;
using SimpleTcp;
using Newtonsoft.Json;

using System.Net;
using System.Net.Sockets;

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

    public IPHostEntry host = Dns.GetHostEntry("localhost");
    public IPAddress ipAddress;
    public IPEndPoint remoteEP;

    private Socket sender;

    private byte[] buffer = new byte[4096];

    private void Start()
    {
        try
        {
            // Connect to a Remote server  
            // Get Host IP Address that is used to establish a connection  
            // In this case, we get one IP address of localhost that is IP : 127.0.0.1  
            // If a host has multiple addresses, you will get a list of addresses  
            ipAddress = host.AddressList[0];
            remoteEP = new IPEndPoint(ipAddress, 42069);

            // Create a TCP/IP  socket.    
            sender = new Socket(ipAddress.AddressFamily,
                SocketType.Stream, ProtocolType.Tcp);

            // Connect the socket to the remote endpoint. Catch any errors.    
            try
            {
                // Connect to Remote EndPoint  
                sender.Connect(remoteEP);

                Debug.Log("Socket connected to " + sender.RemoteEndPoint.ToString());

                isConnected = true;

                Thread listener = new Thread(new ThreadStart(Listener));

                listener.Start();

            }
            catch (ArgumentNullException ane)
            {
                Debug.Log("ArgumentNullException :" +  ane.ToString());
            }
            catch (SocketException se)
            {
                Debug.Log("SocketException :" + se.ToString());
            }
            catch (Exception e)
            {
                Debug.Log("Unexpected exception :" + e.ToString());
            }

        }
        catch (Exception e)
        {
            Debug.Log(e.ToString());
        }

        /*// Inicializamos la conexion
        client = new SimpleTcpClient("127.0.0.1:42069");

        // set events
        client.Events.Connected += Connected;
        client.Events.Disconnected += Disconnected;
        client.Events.DataReceived += DataReceived;

        Thread listener = new Thread(new ThreadStart(Listener));

        network.Connect();
        listener.Start();

        Message admin = new Message
        {
            number = 2, // TODO: Cambiar esto a un combo box
            idMessage = "RESPONSE"
        };*/
    }

    // Listener de mensajes
    private void Listener()
    {
        while (isConnected)
        {
            Array.Clear(buffer, 0, buffer.Length);

            Debug.Log("Antes de: " + Encoding.UTF8.GetString(buffer));

            int bytes = sender.Receive(buffer);

            if (bytes <= 0) continue;

            Debug.Log("Cantidad de bytes: " + bytes.ToString());

            string received = Encoding.UTF8.GetString(buffer);

            Debug.Log("JSON: " + received);

            Message message = JsonConvert.DeserializeObject<Message>(received);

            messageAvailable = message;

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

        sender.Send(Encoding.UTF8.GetBytes(json));

        Array.Clear(buffer, 0, buffer.Length);
    }

    public void Disconnect()
    {
        sender.Close();

        isConnected = false;
    }

    public void Connect()
    {
        //client.Connect();

        isConnected = true;
    }

    static void Connected(object sender, EventArgs e)
    {
        Debug.Log("*** Server connected");
    }

    static void Disconnected(object sender, EventArgs e)
    {
        Debug.Log("*** Server disconnected" + e.ToString());
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
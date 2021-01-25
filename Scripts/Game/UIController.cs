using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIController : MonoBehaviour
{
    private Button sendButton;
    private Network network;

    private GameObject chatContainer;

    public GameObject chatPrefab;

    // Start is called before the first frame update
    void Start()
    {
        chatContainer = GameObject.Find("ChatContainer");
        sendButton = GameObject.Find("ButtonSend").GetComponent<Button>();
        sendButton.onClick.AddListener(OnBtnSendClick);

        network = GameObject.Find("Network").GetComponent<Network>();
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

    // Permite enviar mensajes al chat
    public void OnBtnSendClick()
    {
        string text = GameObject.Find("TextChat").GetComponent<Text>().text;

        Message message = new Message();

        message.idMessage = "MESSAGE";
        message.text = text;

        network.SendMessage(message);
    }
}

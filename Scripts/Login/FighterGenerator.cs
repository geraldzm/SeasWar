using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

using Newtonsoft.Json;

public class FighterGenerator : MonoBehaviour
{
    public Sprite[] FightersImage;

    public string[] FightersAttacks;
    public string[] FightersAttacksEnums;
    public static string[] FightersName = new string[] { 
        "Manta negra", "Poseidon", "Aquaman", "Jack Sparrow", "Davy Jones"
    };

    public int[] FightersPorc;

    private int amountFighters;
    private int[] values = new int[] { 100, 75, 50};

    private Transform availableContainer;
    private Transform selectedContainer;

    private Button playButton;
    private Text playerName;

    public static Dictionary<string, Fighter> warriors = new Dictionary<string, Fighter>();

    void Start()
    {
        FightersAttacksEnums = new string[] {
            "THUNDERS", "TELEPATHY", "KRAKEN", "WAVES", "TRIDENT", "VOLCANO"
        };

        availableContainer = GameObject.Find("AvailableContent").GetComponent<Transform>();
        selectedContainer = GameObject.Find("SelectedContainer").GetComponent<Transform>();
        playButton = GameObject.Find("PlayButton").GetComponent<Button>();
        playerName = GameObject.Find("TextName").GetComponent<Text>();


        playButton.onClick.AddListener(delegate {
            OnPlayButton();
        });

        amountFighters = FightersImage.Length;

        generatFighters();
    }
    void Update()
    {
        
    }

    private void OnPlayButton()
    {
        if (Network.lastMessage != IDMessage.WRONGNAME)
        {
            Network.name = playerName.text;

            Network.getInstance().Connect();
        } else
        {
            Message msgName = new Message
            {
                text = name,
                idMessage = "RESPONSE" // TODO: Cambiar esto en utils
            };

            Network.getInstance().SendMessage(msgName);
        }
    }

    // Aqui generamos los luchadores disponibles
    private void generatFighters()
    {
        GameObject reference = (GameObject)Instantiate(Resources.Load("Fighters"));

        for (int i = 0; i < amountFighters; i++)
        {
            GameObject fighter = Instantiate(reference, availableContainer);
            
            Text name = fighter.transform.Find("FighterName").GetComponent<Text>();
            Text porc = fighter.transform.Find("FighterPorc").GetComponent<Text>();
            Image image = fighter.transform.Find("FighterImage").GetComponent<Image>();
            Button button = fighter.transform.Find("FighterButton").GetComponent<Button>();

            image.sprite = FightersImage[i];
            name.text = FightersName[i];
            porc.text = "Porcentaje: " + FightersPorc[i].ToString() + "%";

            int localIndex = i;

            button.onClick.AddListener(delegate () {
                int index = localIndex;

                generateSelected(index);
            });
        }

        Destroy(reference);
    }

    // Aqui agregamos los jugadores que vamos a usar
    private void generateSelected(int index)
    {
        if (warriors.ContainsKey(FightersName[index]))
        {
            // Trigger global message
            return;
        }

        if (warriors.Count >= 3)
        {
            // Trigger global message

            return;
        }

        GameObject fighter = (GameObject)Instantiate(Resources.Load("SelectedFighter"), selectedContainer);

        Text name = fighter.transform.Find("FighterName").GetComponent<Text>();
        Text porc = fighter.transform.Find("FighterPorc").GetComponent<Text>();

        Image image = fighter.transform.Find("FighterImage").GetComponent<Image>();
        Dropdown dpPower = fighter.transform.Find("DPPower").GetComponent<Dropdown>();
        Dropdown dpHealth = fighter.transform.Find("DPHealth").GetComponent<Dropdown>();
        Dropdown dpRes = fighter.transform.Find("DPRes").GetComponent<Dropdown>();
        Dropdown dpAttack = fighter.transform.Find("DPAttack").GetComponent<Dropdown>();

        name.text = FightersName[index];
        image.sprite = FightersImage[index];
        porc.text = "Porcentaje: " + FightersPorc[index].ToString() + "%";

        Fighter fighterRef = new Fighter();

        fighterRef.name = name.text;
        fighterRef.per = FightersPorc[index];

        // TODO: Cambiar estos por uno de obtener libres
        fighterRef.power = values[0];
        fighterRef.res = values[0];
        fighterRef.health = values[0];
        fighterRef.attacks = new string[] { FightersAttacksEnums[0] };

        dpPower.onValueChanged.AddListener(delegate {
            fighterRef.power = values[dpPower.value];
        });

        dpHealth.onValueChanged.AddListener(delegate {
            fighterRef.health = values[dpPower.value];
        });

        dpRes.onValueChanged.AddListener(delegate {
            fighterRef.res = values[dpPower.value];
        });

        dpAttack.onValueChanged.AddListener(delegate {
            fighterRef.attacks[0] = FightersAttacksEnums[dpAttack.value];
        });

        warriors.Add(name.text, fighterRef);
    }

    public static Fighter[] GetFighters()
    {
        Fighter[] playerFighters = new Fighter[3];

        int count = 0;

        for (int i = 0; i < FightersName.Length; i++) 
            if (warriors.ContainsKey(FightersName[i]))
            {
                playerFighters[count] = warriors[FightersName[i]];
                count++;
            }

        return playerFighters;
    }
}

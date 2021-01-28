using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class FighterGenerator : MonoBehaviour
{
    public Sprite[] FightersImage;

    public string[] FightersAttacks;
    public string[] FightersAttacksEnums;

    private int amountFighters;
    private int[] values = new int[] { 100, 75, 50};

    private Transform availableContainer;
    private Transform selectedContainer;

    private Button playButton;
    private Text playerName;
    private Text alertText;

    public static Dictionary<string, Fighter> warriors = new Dictionary<string, Fighter>();

    private bool playButtonClicked = false;

    void Start()
    {
        FightersAttacksEnums = new string[] {
            "THUNDERS", "TELEPATHY", "KRAKEN", "WAVES", "TRIDENT", "VOLCANO"
        };

        availableContainer = GameObject.Find("AvailableContent").GetComponent<Transform>();
        selectedContainer = GameObject.Find("SelectedContainer").GetComponent<Transform>();
        playButton = GameObject.Find("PlayButton").GetComponent<Button>();
        playerName = GameObject.Find("TextName").GetComponent<Text>();
        alertText = GameObject.Find("TextAlert").GetComponent<Text>();


        playButton.onClick.AddListener(delegate {
            OnPlayButton();
        });

        amountFighters = FightersImage.Length;

        generateFighters();
    }
    void Update()
    {
        
    }

    private void OnPlayButton()
    {
        if (!ValidateCharacters()) return;

        if (Network.lastMessage == IDMessage.WRONGNAME)
        {
            Message msgName = new Message
            {
                text = name,
                idMessage = "RESPONSE" // TODO: Cambiar esto en utils
            };

            Network.getInstance().SendMessage(msgName);
        } else if (!playButtonClicked)
        {
            Network.name = playerName.text;
            Network.warriors = warriors;
            Network.getInstance();

            playButtonClicked = true;
        }
    }

    private bool ValidateCharacters()
    {
        Fighter[] currentFighters = GetFighters();
        Dictionary<int, int> values = new Dictionary<int, int>
        {
            { 50, 0 },
            { 75, 0 },
            { 100, 0 }
        };

        int totalPerc = 0;

        if (warriors.Count != 3)
        {
            alertText.text = "Debes tener tres jugadores...";
            return false;
        }

        for (int i = 0; i < currentFighters.Length; i++)
        {
            totalPerc += currentFighters[i].per;

            Debug.Log(currentFighters[i]);
            Debug.Log("Iteracion #" + i.ToString());
            Debug.Log("Vida: " + currentFighters[i].health.ToString());
            Debug.Log("Poder: " + currentFighters[i].power.ToString());
            Debug.Log("Res: " + currentFighters[i].res.ToString());
            values[currentFighters[i].health]++;
            values[currentFighters[i].power]++;
            values[currentFighters[i].res]++;
        }

        // Validaciones
        if (totalPerc != 100)
        {
            alertText.text = "Los porcentajes deben sumar 100...";
            return false;
        }

        if (values[50] > 3)
        {
            alertText.text = "Hay mas de 3 50%...";
            return false;
        }

        if (values[75] > 3)
        {
            alertText.text = "Hay mas de 3 75%...";
            return false;
        }

        if (values[100] > 3)
        {
            alertText.text = "Hay mas de 3 100%...";
            return false;
        }

        if (playerName.text == "")
        {
            alertText.text = "Tu pueblo necesita un nombre...";
            return false;
        }

        alertText.text = "";

        return true;
    }

    // Aqui generamos los luchadores disponibles
    private void generateFighters()
    {
        GameObject reference = (GameObject)Instantiate(Resources.Load("Fighters"));

        for (int i = 0; i < amountFighters; i++)
        {
            GameObject fighter = Instantiate(reference, availableContainer);
            
            Text name = fighter.transform.Find("FighterName").GetComponent<Text>();
            Image image = fighter.transform.Find("FighterImage").GetComponent<Image>();
            Button button = fighter.transform.Find("FighterButton").GetComponent<Button>();

            image.sprite = FightersImage[i];
            name.text = Utils.Fighters[i];

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
        if (warriors.ContainsKey(Utils.Fighters[index]))
        {
            alertText.text = "Ya tienes a ese luchador...";
            return;
        }

        if (warriors.Count >= 3)
        {
            alertText.text = "Ya tienes 3 luchadores...";
            return;
        }

        GameObject fighter = (GameObject) Instantiate(Resources.Load("SelectedFighter"), selectedContainer);

        Text name = fighter.transform.Find("FighterName").GetComponent<Text>();

        Image image = fighter.transform.Find("FighterImage").GetComponent<Image>();
        Dropdown dpPower = fighter.transform.Find("DPPower").GetComponent<Dropdown>();
        Dropdown dpHealth = fighter.transform.Find("DPHealth").GetComponent<Dropdown>();
        Dropdown dpRes = fighter.transform.Find("DPRes").GetComponent<Dropdown>();
        Dropdown dpAttack = fighter.transform.Find("DPAttack").GetComponent<Dropdown>();
        InputField ifPorc = fighter.transform.Find("FighterPorcIF").GetComponent<InputField>();
        Text porc = ifPorc.transform.Find("FighterPorc").GetComponent<Text>();

        name.text = Utils.Fighters[index];
        image.sprite = FightersImage[index];

        Fighter fighterRef = new Fighter
        {
            name = name.text,
            per = 0,

            // TODO: Cambiar estos por uno de obtener libres
            power = values[0],
            res = values[0],
            health = values[0],
            attacks = new string[] { FightersAttacksEnums[0] }
        };

        ifPorc.onEndEdit.AddListener(delegate {
            try
            {
                int val = int.Parse(porc.text);

                fighterRef.per = val;
            }
            catch (FormatException)
            {
                // TODO: Habilitar mensaje global
            }
        });

        dpPower.onValueChanged.AddListener(delegate {
            fighterRef.power = values[dpPower.value];

            warriors[fighterRef.name] = fighterRef;
        });

        dpHealth.onValueChanged.AddListener(delegate {
            fighterRef.health = values[dpHealth.value];

            warriors[fighterRef.name] = fighterRef;
        });

        dpRes.onValueChanged.AddListener(delegate {
            fighterRef.res = values[dpRes.value];

            warriors[fighterRef.name] = fighterRef;
        });

        dpAttack.onValueChanged.AddListener(delegate {
            fighterRef.attacks[0] = FightersAttacksEnums[dpAttack.value];

            warriors[fighterRef.name] = fighterRef;
        });

        warriors.Add(name.text, fighterRef);
    }

    public static Fighter[] GetFighters()
    {
        Fighter[] playerFighters = new Fighter[3];

        int count = 0;

        for (int i = 0; i < Utils.Fighters.Length; i++) 
            if (warriors.ContainsKey(Utils.Fighters[i]))
            {
                if (!warriors.ContainsKey(Utils.Fighters[i])) continue;

                playerFighters[count] = warriors[Utils.Fighters[i]];
                count++;
            }

        return playerFighters;
    }
}

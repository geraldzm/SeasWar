using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class LoginController : MonoBehaviour
{
    private Dropdown dpAdmin;
    public static bool ChangeScene = false;

    // Start is called before the first frame update
    void Start()
    {
        dpAdmin = GameObject.Find("dpAdmin").GetComponent<Dropdown>();

        dpAdmin.onValueChanged.AddListener(delegate {
            Network.AmountPlayers = dpAdmin.value + 2;
        });
    }

    // Update is called once per frame
    void Update()
    {
        if (ChangeScene)
        {
            // Abrimos la escena de juego
            SceneManager.LoadScene(1);

            ChangeScene = false;
        }
    }
}

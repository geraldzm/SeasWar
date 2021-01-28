using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class LoginController : MonoBehaviour
{
    public static bool ChangeScene = false;

    // Start is called before the first frame update
    void Start()
    {
        
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

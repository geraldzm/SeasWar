using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Matrix : MonoBehaviour
{
    public static int cols = 30;
    public static int rows = 20;

    public float tileSize = 1;

    public static GameObject[,] matrix;
    public static byte[,] byteMatrix;

    public Sprite[] spriteArray;

    public static Sprite[] spriteStaticArray;

    void Start()
    {
        Message message = new Message
        {
            idMessage = "DONE"
        };

        Network.getInstance().SendMessage(message);

        spriteStaticArray = spriteArray;

        matrix = new GameObject[cols, rows];
        byteMatrix = new byte[cols, rows];

        generateGrid();

        // Centramos la camara
        float gridW = cols * tileSize;
        float gridH = rows * tileSize;

        transform.position = new Vector2(-gridW / 2 + tileSize / 2, gridH / 2 - tileSize / 2);

        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
            {
                SpriteRenderer sprite = matrix[col, row].GetComponent<SpriteRenderer>();
                byteMatrix[col, row] = 100;

                sprite.sortingLayerName = "Objects";
                sprite.sprite = spriteArray[0];
            }
    }

    void Update()
    {
    }

    public static void SetMatrix(string[,] receivedMatrix)
    {
        for (int col = 0; col < cols; col++)
        {
            for (int row = 0; row < rows; row++) {
                SpriteRenderer render = matrix[col, row].GetComponent<SpriteRenderer>();

                int index = Utils.getFighterIndex(receivedMatrix[row, col]);

                render.sprite = spriteStaticArray[index];
            }
        }
    }

    // Generamos la matriz de juego
    private void generateGrid()
    {
        GameObject reference = (GameObject)Instantiate(Resources.Load("tiles"));

        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
            {
                GameObject tile = Instantiate(reference, transform);

                float posX = col * tileSize;
                float posY = row * -tileSize;

                tile.transform.position = new Vector2(posX, posY);

                matrix[col, row] = tile;
            }

        Destroy(reference);
    }
}

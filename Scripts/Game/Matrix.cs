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
    public static Sprite deadTile;
    public static Sprite lowLifeTile;

    void Start()
    {
        spriteStaticArray = spriteArray;
        deadTile = spriteStaticArray[6];

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

        Network.getInstance().SendDone();
    }

    void Update()
    {
        if (Network.namesMatrix != null)
        {
            SetMatrix(Network.namesMatrix);

            Message message = new Message
            {
                idMessage = "DONE"
            };

            Network.getInstance().SendMessage(message);

            Network.namesMatrix = null;
        }

        if (Network.intMatrix != null)
        {
            SetMatrixByInts(Network.intMatrix);

            Message message = new Message
            {
                idMessage = "DONE"
            };

            Network.getInstance().SendMessage(message);

            Network.intMatrix = null;
        }
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

    public static void SetMatrixByInts(int[,] receivedMatrix)
    {
        for (int col = 0; col < cols; col++)
        {
            for (int row = 0; row < rows; row++)
            {
                SpriteRenderer render = matrix[col, row].GetComponent<SpriteRenderer>();

                float life = receivedMatrix[row, col];

                if (life != 0)
                    render.color = new Color(1, life / 100f, life / 100f, 1);
                else
                {
                    render.color = new Color(1, 1, 1, 1);
                    render.sprite = deadTile;
                }

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

using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Matrix : MonoBehaviour
{
    public int cols = 30;
    public int rows = 20;

    public float tileSize = 1;

    public static GameObject[,] matrix;
    public static byte[,] byteMatrix;

    public Sprite[] spriteArray;

    void Start()
    {
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
                sprite.sprite = spriteArray[Random.Range(0, spriteArray.Length - 1)];
            }
    }

    void Update()
    {
    }

    // Generamos la matriz de juego
    private void generateGrid()
    {
        GameObject reference = (GameObject)Instantiate(Resources.Load("tiles"));

        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
            {
                GameObject tile = (GameObject)Instantiate(reference, transform);

                float posX = col * tileSize;
                float posY = row * -tileSize;

                tile.transform.position = new Vector2(posX, posY);

                matrix[col, row] = tile;
            }

        Destroy(reference);
    }
}

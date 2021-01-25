using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CameraMovement : MonoBehaviour
{
    public float dragSpeed = 1;
    private Vector3 dragOrigin;
    private Vector3 move;
    private bool shouldMove = true;

    void Start()
    {
        
    }

    void Update()
    {
        if (Input.GetMouseButtonDown(0))
        {
            dragOrigin = Input.mousePosition;
            return;
        }

        if (!Input.GetMouseButton(0)) return;

        Vector3 pos = Camera.main.ScreenToViewportPoint(Input.mousePosition - dragOrigin);
        move = new Vector3(pos.x * dragSpeed, 0, pos.y * dragSpeed);

        if (shouldMove)
            transform.Translate(move, Space.World);
    }

    private void OnCollisionEnter2D(Collision2D collision)
    {
        shouldMove = false;
    }
    private void OnCollisionExit2D(Collision2D collision)
    {
        shouldMove = true;
    }

    private void OnCollisionStay2D(Collision2D collision)
    {
        if (collision.gameObject.CompareTag("left"))
        {
            transform.Translate(new Vector2(1, 0));
        } 
        else if (collision.gameObject.CompareTag("right"))
        {
            transform.Translate(new Vector2(-1, 0));
        }
    }
}

package com.gameproj.entities.collectables;

public class ItemDef {
    private Class type;
    private float posX;
    private float posY;

    public ItemDef(Class t, float x, float y) {
        type = t;
        posX = x;
        posY = y;
    }

    public Class getType() {
        return type;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }
}

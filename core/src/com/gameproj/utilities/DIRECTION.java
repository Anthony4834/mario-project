package com.gameproj.utilities;

public enum DIRECTION {

    LEFT(-1, 0),
    RIGHT(1, 0),
    UP(0, 1),
    DOWN(0, -1);

    private final int x, y;

    private DIRECTION(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}

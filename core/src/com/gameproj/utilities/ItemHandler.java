package com.gameproj.utilities;

import com.gameproj.entities.collectables.Coin;
import com.gameproj.entities.collectables.ItemDef;
import com.gameproj.entities.collectables.Mushroom;
import com.gameproj.entities.environment.BrickPiece;
import com.gameproj.screens.GameScreen;

public class ItemHandler {

    private GameScreen screen;

    public ItemHandler(GameScreen s) {
        screen = s;
    }

    public void spawnCoin(float posX, float posY) {
        Coin c = new Coin(Coin.blank.getWorld(), posX, posY, true);
        c.getFixture().setSensor(true);
        screen.getCollectables().add(c);
    }
    public void spawnMushroom(float posX, float posY) {
        Mushroom m = new Mushroom(Coin.blank.getWorld(), posX, posY);
        screen.getCollectables().add(m);
    }
    public void spawnBrickPiece(float posX, float posY) {
        BrickPiece b = new BrickPiece(Coin.blank.getWorld(), posX, posY);
        screen.getNonCollectables().add(b);
    }
    public void update() {
        if(!GameScreen.itemsToAdd.isEmpty()) {
            ItemDef def = GameScreen.itemsToAdd.pop();
            if(def.getType() == Coin.class)
                spawnCoin(def.getPosX(), def.getPosY());
            if(def.getType() == Mushroom.class) {
                spawnMushroom(def.getPosX(), def.getPosY());
            }
            if(def.getType() == BrickPiece.class) {
                spawnBrickPiece(def.getPosX(), def.getPosY());
            }
        }
    }
}

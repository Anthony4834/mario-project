package com.gameproj.entities.environment;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.World;
import com.gameproj.GameOne;
import com.gameproj.entities.collectables.ItemDef;
import com.gameproj.entities.collectables.Mushroom;
import com.gameproj.screens.GameScreen;

public class Brick extends InteractiveTileMapObject{

    private float animTerminateTime = 0;
    private boolean destroyed = false;
    protected boolean isActive = true;
    protected boolean beingHit = false;
    protected TiledMapTileLayer.Cell cell;

    public Brick(World w, TiledMap tM, MapObject object) {
        super(w, tM, object, ((RectangleMapObject) object).getRectangle());
        fixture.setUserData(this);
        cell = getCell();
    }
    @Override
    public void onHeadHit() {
        if (!destroyed && isActive) {
            animTerminateTime = (float) (GameOne.timer + 0.2);

            if (object.getProperties().containsKey("containsMushroom") && ((boolean) object.getProperties().get("containsMushroom")))
                GameScreen.itemsToAdd.add(new ItemDef(Mushroom.class, body.getPosition().x, body.getPosition().y));
            else {
                GameOne.jukeBox.play("bump");
            }

            beingHit = true;
        }
    }
    public void update() {
            if(GameOne.timer >= animTerminateTime) {
                cell.getTile().setOffsetY(0);
                cell.setTile(tileSet.getTile(62));
            } else if(this.beingHit && GameOne.timer < animTerminateTime - 0.1 ) {
                cell.setTile(tileSet.getTile(63));
                cell.getTile().setOffsetY((float) (cell.getTile().getOffsetY() + 0.2));
            } else {
                cell.setTile(tileSet.getTile(63));
                cell.getTile().setOffsetY((float) (cell.getTile().getOffsetY() - 0.2));
            }
    }
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

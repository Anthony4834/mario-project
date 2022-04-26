package com.gameproj.entities.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.physics.box2d.World;
import com.gameproj.GameOne;
import com.gameproj.entities.collectables.Coin;
import com.gameproj.entities.collectables.ItemDef;
import com.gameproj.entities.collectables.Mushroom;
import com.gameproj.screens.GameScreen;
import com.gameproj.utilities.TextureGrabber;

import static com.gameproj.GameOne.PPM;

public class GoldenBrick extends Brick {

    private static float animTerminateTime = 0;
    private final Animation<TiledMapTile> activeAnimation;

    public GoldenBrick(World w, TiledMap tM, MapObject object) {
        super(w, tM, object);
        activeAnimation = new Animation<>(0.15f, TextureGrabber.grabAnimationFrames("activeGoldenBrick", tileSet));
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Golden brick", "hit");

        if(this.isActive) {
            GoldenBrick.animTerminateTime = (float) (GameOne.timer + 0.05);
            cell.setTile(tileSet.getTile(145));
            cell.getTile().setOffsetY(1);

            if(object.getProperties().containsKey("containsMushroom") && ( (boolean) object.getProperties().get("containsMushroom") ))
                GameScreen.itemsToAdd.add(new ItemDef(Mushroom.class, body.getPosition().x, body.getPosition().y));
            else {
                GameOne.jukeBox.play("coin");
                GameScreen.itemsToAdd.add(new ItemDef(Coin.class, body.getPosition().x, body.getPosition().y + 16 / PPM));
            }

            beingHit = true;
            isActive = false;
        }


    }
    @Override
    public void update() {
        if(!isActive) {
            if(GameOne.timer >= GoldenBrick.animTerminateTime) {
                cell.getTile().setOffsetY(0);
                cell.setTile(tileSet.getTile(72));
                if(beingHit) {
                    cell.getTile().setOffsetY(1);
                    beingHit = false;
                }
            }
        } else {
            cell.setTile(activeAnimation.getKeyFrame(GameOne.timer, true));
        }
    }
}

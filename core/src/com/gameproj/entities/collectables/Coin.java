package com.gameproj.entities.collectables;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.gameproj.GameOne;
import com.gameproj.utilities.TextureGrabber;

import static com.gameproj.GameOne.PPM;

public class Coin extends Collectable {

    private Animation<TextureRegion> animation;
    public static Coin blank;
    private boolean spawnedFromBlock;
    private boolean givenForce;

    public Coin(World w, float x, float y, boolean block) {
        super(w, x, y, 12, 12, new TextureRegion(TextureGrabber.atlasMap.get("items-npcs").findRegion("28")));
        animation = new Animation((float) 0.15, TextureGrabber.grabAnimationFrames("coin_spinning", "default"));
        fixture.setUserData(this);
        fixture.setSensor(true);
        spawnedFromBlock = block;
        givenForce = false;
        if(!spawnedFromBlock)
            b2dBody.setType(BodyDef.BodyType.StaticBody);
    }
    @Override
    public void update(float delta) {
        if(active) {
            setRegion(animation.getKeyFrame(GameOne.timer, true));
            setBounds((float) (b2dBody.getPosition().x - getRegionWidth() / PPM / 2 * 0.8), (float) (b2dBody.getPosition().y - getRegionHeight() / PPM / 2 * 0.8), (float) (getRegionWidth() / PPM * 0.7), (float) (getRegionHeight() / PPM * 0.7));
            handleCoinFromBlock();
        } else {
            if(!destroyed) {
                if(!spawnedFromBlock)
                    GameOne.jukeBox.play("coin");

                destroy();
            }
        }

    }
    private void handleCoinFromBlock() {
        if(spawnedFromBlock && !givenForce) {
            b2dBody.applyLinearImpulse(new Vector2(0, 3f), b2dBody.getWorldCenter(), true);
            givenForce = true;
        } else if(spawnedFromBlock && b2dBody.getPosition().y <= posY) {
            active = false;
        }
    }
}

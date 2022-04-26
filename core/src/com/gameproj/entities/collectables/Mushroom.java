package com.gameproj.entities.collectables;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.gameproj.GameOne;
import com.gameproj.entities.Player;
import com.gameproj.screens.GameScreen;
import com.gameproj.utilities.TextureGrabber;

import static com.gameproj.GameOne.PPM;

public class Mushroom extends Collectable {
    public static Mushroom blank;
    private boolean movingRight;
    private boolean emerging;
    private boolean givenForce;
    private boolean sfxPlaying;
    private float prevX = 0;

    public Mushroom(World w, float x, float y) {
        super(w, x, y, 16, 16, TextureGrabber.grab("mushroom"));
        emerging = true;
        movingRight = true;
        fixture.setUserData(this);
        fixture.getShape().setRadius(7 / PPM);
    }
    @Override
    public void update(float delta) {
        setPosition(b2dBody.getPosition().x - getWidth() / 2, b2dBody.getPosition().y - getHeight() / 2);

        if(active) {
            if(emerging) {
                handleEmerging();
            } else {
                moveMushroom();
            }
        } else {
            if(!destroyed) {
                destroy();
            }
        }

    }

    public boolean isEmerging() {
        return emerging;
    }
    private void handleEmerging() {
        fixture.setSensor(true);
        b2dBody.setLinearVelocity(new Vector2(0, 0.4f));
        if(!sfxPlaying) {
            GameOne.jukeBox.play("item_from_block");
            sfxPlaying = true;
        }

        if(b2dBody.getPosition().y >= posY + 16 / PPM) {
            emerging = false;
            fixture.setSensor(false);
        }
    }
    private void moveMushroom() {
        b2dBody.setLinearVelocity(new Vector2(movingRight ? 0.5f : -0.5f, getB2dBody().getLinearVelocity().y));
        if(b2dBody.getPosition().x == prevX && GameOne.frame % 2 == 0)
            movingRight = !movingRight;
        prevX = b2dBody.getPosition().x;
    }
    public void use(Player target) {
        target.fixture.getShape().setRadius(14 / PPM);
        target.setFrozen(true);
        target.setGrowing(true);
        GameOne.jukeBox.play("powerup");
        Player.animTermTime = (float) (GameOne.timer + 1);

        active = false;
    }
    public static void use(Player target, boolean test) {
        target.fixture.getShape().setRadius(14 / PPM);
        target.setFrozen(true);
        target.setGrowing(true);
        GameOne.jukeBox.play("powerup");
        Player.animTermTime = (float) (GameOne.timer + 1);
    }
}

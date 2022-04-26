package com.gameproj.actors.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.gameproj.GameOne;
import com.gameproj.entities.Player;
import com.gameproj.screens.GameScreen;
import com.gameproj.utilities.TextureGrabber;

import static com.gameproj.GameOne.PPM;

public class Enemy extends Sprite {
    protected float posX;
    protected float posY;
    protected Body b2dBody;
    protected World world;
    protected Fixture fixture;
    protected float width;
    protected float height;
    protected boolean active;
    protected boolean destroyed;

    public Enemy(World w, float x, float y, float wi, float h, TextureRegion tR) {
        super(tR);
        world = w;
        posX = x;
        posY = y;
        width = wi;
        height = h;
        active = true;

        init();

    }
    public void init() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(posX, posY);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2dBody = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / PPM);
        fDef.shape = shape;

        fixture = b2dBody.createFixture(fDef);
        setBounds(0, 0, width / PPM, height / PPM);
    }
    public void update(float delta) {
        setBounds(b2dBody.getPosition().x - getRegionWidth() / PPM / 2, b2dBody.getPosition().y - getRegionHeight() / PPM / 2, (float) (getRegionWidth() / PPM * 1.2), (float) (getRegionHeight() / PPM * 1.2) );
    }
    public void damagePlayer(Player target) {
        if(target.isInvulnerable()) {
            return;
        }
        target.fixture.getShape().setRadius(7 / PPM);
        target.setFrozen(true);
        target.setShrinking(true);
        GameOne.jukeBox.play("pipe");
        Player.animTermTime = (float) (GameOne.timer + 0.8);
    }
    public static void damagePlayer(Player target, boolean test) {
        target.fixture.getShape().setRadius(7 / PPM);
        target.setFrozen(true);
        target.setShrinking(true);
        GameOne.jukeBox.play("pipe");
        Player.animTermTime = (float) (GameOne.timer + 0.8);
    }
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    public void destroy() {
        b2dBody.destroyFixture(fixture);
        b2dBody.getWorld().destroyBody(b2dBody);
        destroyed = true;
    }

    public Body getB2dBody() {
        return b2dBody;
    }

    public World getWorld() {
        return world;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public void use() {
        active = false;
    }
}

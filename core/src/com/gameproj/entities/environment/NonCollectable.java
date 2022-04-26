package com.gameproj.entities.environment;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.gameproj.GameOne;
import com.gameproj.screens.GameScreen;
import com.gameproj.utilities.TextureGrabber;

import static com.gameproj.GameOne.PPM;

public class NonCollectable extends Sprite {
    protected float posX;
    protected float posY;
    protected Body b2dBody;
    protected World world;
    protected Fixture fixture;
    protected float width;
    protected float height;
    protected boolean active;
    protected boolean destroyed;

    public NonCollectable(World w, float x, float y, float wi, float h, TextureRegion tR) {
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
        shape.setRadius(width / PPM);
        fDef.shape = shape;

        fixture = b2dBody.createFixture(fDef);
        fixture.setSensor(true);
        setBounds(0, 0, width / PPM, height / PPM);
    }
    public void update(float delta) {
        setPosition(b2dBody.getPosition().x - getWidth() / 2, b2dBody.getPosition().y - getHeight() / 2);
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

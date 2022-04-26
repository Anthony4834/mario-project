package com.gameproj.actors.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.gameproj.GameOne;
import com.gameproj.entities.Player;
import com.gameproj.entities.collectables.Mushroom;
import com.gameproj.utilities.TextureGrabber;

import static com.gameproj.GameOne.PPM;
import static com.gameproj.GameOne.p;

public class Goomba extends Enemy {
    public static Mushroom blank;

    private float prevX = 0;
    private float animTimer;
    private float animTermTime = -1;

    private boolean movingRight;
    private boolean dying = false;

    private Animation<TextureRegion> walking;
    private Animation<TextureRegion> death;

    public Fixture head;

    public Goomba(World w, float x, float y) {
        super(w, x, y, 16, 16, TextureGrabber.grab("goomba1"));
        movingRight = true;
        fixture.setUserData(new Object[] {"goomba", this});
        fixture.getShape().setRadius(7 / PPM);



        walking = new Animation(0.2f, TextureGrabber.grabAnimationFrames("goomba_walking", ""));
        death = walking;
    }

    private void moveMushroom() {
        b2dBody.setLinearVelocity(new Vector2(movingRight ? 0.5f : -0.5f, getB2dBody().getLinearVelocity().y));
        if(b2dBody.getPosition().x == prevX && GameOne.frame % 3 == 0)
            movingRight = !movingRight;
        prevX = b2dBody.getPosition().x;
    }

    public void update(float delta) {
        if(active) {
            if(dying) {
                handleDeath(delta);
            } else {
                moveMushroom();
                setRegion(walking.getKeyFrame(GameOne.timer, true));
                setBounds(b2dBody.getPosition().x - getRegionWidth() / PPM / 2, b2dBody.getPosition().y - getRegionHeight() / PPM / 2, (float) (getRegionWidth() / PPM * 1.2), (float) (getRegionHeight() / PPM * 1.2) );

            }
        } else {
            if(!destroyed)
                destroy();
        }
    }

    public void collision(Fixture f, Fixture player) {
        if(player.getBody().getWorldCenter().y - (player.getShape().getRadius()) >= f.getBody().getWorldCenter().y) {
            dying = true;
            boolean jumping = Gdx.input.isKeyPressed(Input.Keys.SPACE);
            player.getBody().setLinearVelocity(new Vector2(0, jumping ? 5 : 3));
        } else if(!dying) {
            damagePlayer((Player) player.getUserData());
        }
    }
    public void createHead() {
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-3 / PPM, 6 / PPM), new Vector2(3 / PPM, 6 / PPM));
        FixtureDef fDef = new FixtureDef();
        fDef.shape = head;

        Fixture f = b2dBody.createFixture(fDef);
        f.setSensor(true);
        f.setUserData(new Object[] {"goomba_head", this});

        this.head = f;
    }

    public void handleDeath(float delta) {
        setBounds(b2dBody.getPosition().x - getRegionWidth() / PPM / 2, (float) (b2dBody.getPosition().y - getRegionHeight() / PPM * 0.75), (float) (getRegionWidth() / PPM * 1.2), (float) (getRegionHeight() / PPM * 1.2) );
        if(animTermTime == -1) {
            GameOne.jukeBox.play("squish");
            animTermTime = (float) (GameOne.timer + 0.4);
        }

        if(GameOne.timer <= animTermTime) {
            setRegion(TextureGrabber.grab("goomba3"));
            animTimer += delta;
        } else {
            active = false;
            dying = false;
            animTermTime = -1;
        }
    }

}


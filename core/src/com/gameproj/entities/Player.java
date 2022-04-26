package com.gameproj.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.gameproj.Controller;
import com.gameproj.GameOne;
import com.gameproj.utilities.TextureGrabber;

import javax.swing.plaf.synth.Region;

import static com.gameproj.GameOne.PPM;
import static com.gameproj.GameOne.p;

public class Player extends Sprite {

    public World world;
    public Body b2dBody;
    public Fixture fixture;
    public Fixture feet;
    public Fixture head;

    public enum STATE {FALLING, JUMPING, RUNNING, STANDING, SLIDING}

    public enum YAXIS {DESCENDING, ASCENDING, NONE}

    public float peakYSinceTouchedGround = 0;
    public YAXIS ascOrDesc = YAXIS.NONE;
    public STATE currentState;
    public STATE previousState;
    private float stateTimer;
    public float prevY = 0;
    private float invulnerabilityTermTime;

    private Animation<TextureRegion> playerRun;
    //    private Animation<TextureRegion> playerShrink;
    private Animation<TextureRegion> playerGrow1;
    private Animation<TextureRegion> playerGrow2;
    private Animation<TextureRegion> playerShrink1;
    private Animation<TextureRegion> playerShrink2;
    public static float animTermTime = 0;

    private double maxVelocity;
    public static float prevJumpY = 0;

    private boolean frozen = false;
    private boolean runningRight = true;
    private boolean touchingGround = false;
    private boolean growing = false;
    private boolean shrinking = false;
    private boolean invulnerable = false;
    private String size = "small";

    public Player(World w) {
        super(TextureGrabber.atlasMap.get("tiles").findRegion("25"));
        world = w;
        init();

        currentState = STATE.STANDING;
        previousState = STATE.STANDING;
        stateTimer = 0;

        this.maxVelocity = 1;

        grabAnimations();
        setRegion(TextureGrabber.grab("mario_standing_" + size));

    }

    public void init() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(GameOne.tile * 2 / PPM, GameOne.tile * 10 / PPM);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2dBody = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(7 / PPM);
        fDef.shape = shape;

        fixture = b2dBody.createFixture(fDef);
        fixture.setUserData(this);

        this.head = createHead(fDef);
        this.feet = createFeet(fDef);


    }

    private Fixture createHead(FixtureDef fDef) {
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-4 / PPM, 6 / PPM), new Vector2(4 / PPM, 6 / PPM));
        fDef.shape = head;
        fDef.isSensor = true;

        Fixture f = b2dBody.createFixture(fDef);
        f.setUserData("head");

        return f;
    }

    private Fixture createFeet(FixtureDef fDef) {
        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-4 / PPM, -6 / PPM), new Vector2(4 / PPM, -6 / PPM));
        fDef.shape = feet;
        fDef.isSensor = true;

        Fixture f = b2dBody.createFixture(fDef);
        f.setUserData("feet");

        return f;
    }

    public void update(float delta) {
        setRegion(getFrame(delta));
        setBounds(b2dBody.getPosition().x - getRegionWidth() / PPM / 2, b2dBody.getPosition().y - getRegionHeight() / PPM / 2, (float) (getRegionWidth() / PPM * 1.2), (float) (getRegionHeight() / PPM * 1.2));
        monitorVelocity();

        freeze(frozen);

        prevY = getBoundingRectangle().y;

        if (invulnerable && GameOne.timer >= invulnerabilityTermTime)
            invulnerable = false;

    }

    public TextureRegion getFrame(float delta) {
        currentState = getState();
        TextureRegion region;

        if (growing || shrinking) {
            return handleGrowingOrShrinking();
        }

        switch (currentState) {
            case JUMPING:
                region = TextureGrabber.grab("mario_jumping_" + size);
                break;
            case RUNNING:
                region = playerRun.getKeyFrame(stateTimer, true);
                break;
            case SLIDING:
                if (!runningRight)
                    region = TextureGrabber.grab("mario_sliding_right_" + size);
                else
                    region = TextureGrabber.grab("mario_sliding_left_" + size);
                break;
            default:
                region = TextureGrabber.grab("mario_standing_" + size);
                break;
        }

        if (region != null) {
            if ((b2dBody.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
                if (currentState != STATE.SLIDING)
                    region.flip(true, false);
                runningRight = false;
            } else if ((b2dBody.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
                if (currentState != STATE.SLIDING)
                    region.flip(true, false);
                runningRight = true;
            }
        }

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;

        return region;
    }

    public STATE getState() {
        if (b2dBody.getLinearVelocity().y > 0 || (b2dBody.getLinearVelocity().y < 0 && previousState == STATE.JUMPING))
            return STATE.JUMPING;
        else if (b2dBody.getLinearVelocity().y < 0)
            return STATE.FALLING;
        else if (b2dBody.getLinearVelocity().x != 0)
            return getRunningOrSliding();
        else
            return STATE.STANDING;
    }

    public STATE getRunningOrSliding() {
        if ((b2dBody.getLinearVelocity().x < -0.5 && Gdx.input.isKeyPressed(Input.Keys.D))
                || (b2dBody.getLinearVelocity().x > 0.5 && Gdx.input.isKeyPressed(Input.Keys.A))) {
            if (GameOne.frame % 5 == 0) {
                GameOne.jukeBox.play("skid");
            }
            return STATE.SLIDING;
        }

        return STATE.RUNNING;
    }

    public void jump() {
        if (!Controller.canExtendJump)
            GameOne.jukeBox.play("mario_jump");


        if (b2dBody.getPosition().y <= prevJumpY + (GameOne.tile * 1.2 / PPM))
            if (!Controller.initialJump) {
                b2dBody.applyLinearImpulse(new Vector2(0, 1.3f), b2dBody.getWorldCenter(), true);
                Controller.initialJump = true;
            } else {
                b2dBody.applyLinearImpulse(new Vector2(0, 0.5f), b2dBody.getWorldCenter(), true);
            }
    }

    public void growPlayer() {
        growing = false;
        frozen = false;
        size = "big";
        playerRun = new Animation<>(0.08f, TextureGrabber.grabAnimationFrames("mario_running", size));
        fixture.getShape().setRadius((float) 9.5 / PPM);

        ((EdgeShape) head.getShape()).set(new Vector2(-4 / PPM, 16 / PPM), new Vector2(4 / PPM, 16 / PPM));
        ((EdgeShape) feet.getShape()).set(new Vector2(-4 / PPM, -12 / PPM), new Vector2(4 / PPM, -12 / PPM));
        feet.setSensor(false);
        head.setSensor(false);
    }

    public void shrinkPlayer() {
        shrinking = false;
        frozen = false;
        size = "small";
        playerRun = new Animation<>(0.08f, TextureGrabber.grabAnimationFrames("mario_running", size));
        fixture.getShape().setRadius((float) 7 / PPM);

        ((EdgeShape) head.getShape()).set(new Vector2(-4 / PPM, 6 / PPM), new Vector2(4 / PPM, 6 / PPM));
        ((EdgeShape) feet.getShape()).set(new Vector2(-4 / PPM, 6 / PPM), new Vector2(4 / PPM, -6 / PPM));
        feet.setSensor(true);
        head.setSensor(true);
    }

    private TextureRegion handleGrowingOrShrinking() {
        TextureRegion region;
        if (growing) {
            region = Math.abs(GameOne.timer - animTermTime) >= 0.45
                    ? playerGrow1.getKeyFrame(Math.abs(GameOne.timer - animTermTime), true)
                    : playerGrow2.getKeyFrame(Math.abs(GameOne.timer - animTermTime), true);
        } else {
            region = Math.abs(GameOne.timer - animTermTime) >= 0.45
                    ? playerShrink1.getKeyFrame(Math.abs(GameOne.timer - animTermTime), true)
                    : playerShrink2.getKeyFrame(Math.abs(GameOne.timer - animTermTime), true);
            if (!invulnerable) {
                invulnerabilityTermTime = GameOne.timer + 2;
                invulnerable = true;
            }

        }

        if((!runningRight && !region.isFlipX()) || (runningRight && region.isFlipX()))
                region.flip(true, false);

        if (GameOne.timer >= animTermTime) {
            if(growing)
                growPlayer();
            else
                shrinkPlayer();
        }

        return region;
    }
    private void monitorVelocity() {
        if(b2dBody.getLinearVelocity().x >= maxVelocity)
            b2dBody.getLinearVelocity().x = (float) maxVelocity;
        if(b2dBody.getLinearVelocity().x < -maxVelocity)
            b2dBody.getLinearVelocity().x = (float) (-maxVelocity);


        if(getBoundingRectangle().y > prevY) {
            peakYSinceTouchedGround = feet.getBody().getPosition().y;
            ascOrDesc = YAXIS.ASCENDING;
        } else if(getBoundingRectangle().y < prevY) {
            ascOrDesc = YAXIS.DESCENDING;
        } else {
            ascOrDesc = YAXIS.NONE;
            touchingGround = true;
        }
    }
    private void grabAnimations() {
        playerRun = new Animation<>(0.1f, TextureGrabber.grabAnimationFrames("mario_running", size));
        playerGrow1 = new Animation<>(0.1f, TextureGrabber.grabAnimationFrames("mario_growing_1", size));
        playerShrink1 = new Animation<>(0.1f, TextureGrabber.grabAnimationFrames("mario_shrinking_1", size));
        playerGrow2 = new Animation<>(0.1f, TextureGrabber.grabAnimationFrames("mario_growing_2", size));
        playerShrink2 = new Animation<>(0.1f, TextureGrabber.grabAnimationFrames("mario_shrinking_2", size));
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public boolean isTouchingGround() {
        return touchingGround;
    }

    public void setTouchingGround(boolean touchingGround) {
        prevJumpY = b2dBody.getPosition().y;
        this.touchingGround = touchingGround;
        Controller.initialJump = false;
        peakYSinceTouchedGround = feet.getBody().getPosition().y;
        System.out.println("TOUCHED GROUND: " + peakYSinceTouchedGround);
    }
    public void freeze(boolean freeze) {
        fixture.getBody().setType(freeze ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody);
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public void setGrowing(boolean growing) {
        this.growing = growing;
    }

    public boolean isShrinking() {
        return shrinking;
    }

    public void setShrinking(boolean shrinking) {
        this.shrinking = shrinking;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }
}

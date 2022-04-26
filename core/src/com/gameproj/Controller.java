package com.gameproj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.gameproj.actors.enemies.Enemy;
import com.gameproj.entities.Player;
import com.gameproj.entities.collectables.ItemDef;
import com.gameproj.entities.collectables.Mushroom;
import com.gameproj.entities.environment.BrickPiece;
import com.gameproj.screens.GameScreen;

public class Controller extends InputAdapter {

    private final GameScreen screen;
    private boolean jumpDisabled;
    public static boolean canExtendJump = true;
    public static boolean initialJump;
    private boolean isFrozen;

    public Controller(GameScreen screen) {
        this.screen = screen;
        this.isFrozen = false;
    }

    public boolean update(float delta) {
        if(this.isFrozen)
            return false;

        if(keyPressed(Keys.SHIFT_LEFT))
            screen.getPlayer().setMaxVelocity(2.5);
        else
            screen.getPlayer().setMaxVelocity(1);

        if(Gdx.input.isKeyPressed(Keys.SPACE)) {
            if(     screen.getPlayer().getState() == Player.STATE.FALLING ||
                    (screen.getPlayer().getState() == Player.STATE.JUMPING && !canExtendJump) ||
                    (!screen.getPlayer().isTouchingGround() && !canExtendJump) ||
                    screen.getPlayer().isTouchingGround() && jumpDisabled     )
                return false;

            screen.getPlayer().jump();
            jumpDisabled = true;
            canExtendJump = true;
        } else {
            jumpDisabled = false;
            canExtendJump = false;
        }
        if(keyPressed(Keys.A) && !atMaxVelocity() && screen.getPlayer().currentState != Player.STATE.SLIDING)
            screen.getPlayer().b2dBody.applyLinearImpulse(new Vector2(-0.08f, 0), screen.getPlayer().b2dBody.getWorldCenter(), true);
        if(keyPressed(Keys.D) && !atMaxVelocity() && screen.getPlayer().currentState != Player.STATE.SLIDING)
            screen.getPlayer().b2dBody.applyLinearImpulse(new Vector2(0.08f, 0), screen.getPlayer().b2dBody.getWorldCenter(), true);
        if(Gdx.input.isKeyJustPressed(Keys.SLASH))
            Mushroom.use(screen.getPlayer(), true);
        if(Gdx.input.isKeyJustPressed(Keys.PERIOD))
            Enemy.damagePlayer(screen.getPlayer(), true);
        if(Gdx.input.isKeyJustPressed(Keys.ENTER))
            GameScreen.itemsToAdd.add(new ItemDef(BrickPiece.class, screen.getPlayer().b2dBody.getPosition().x, screen.getPlayer().b2dBody.getPosition().y));
        return false;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }
    private boolean keyPressed(int k) {
        return Gdx.input.isKeyPressed(k);
    }
    private boolean atMaxVelocity() {
        return Math.abs(screen.getPlayer().b2dBody.getLinearVelocity().x) >= screen.getPlayer().getMaxVelocity();
    }
}

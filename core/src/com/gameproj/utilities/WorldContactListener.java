package com.gameproj.utilities;

import com.badlogic.gdx.physics.box2d.*;
import com.gameproj.Controller;
import com.gameproj.GameOne;
import com.gameproj.actors.enemies.Goomba;
import com.gameproj.entities.Player;
import com.gameproj.entities.collectables.Coin;
import com.gameproj.entities.collectables.Collectable;
import com.gameproj.entities.collectables.Mushroom;
import com.gameproj.entities.environment.InteractiveTileMapObject;
import com.gameproj.entities.environment.WalkableTerrain;
import com.gameproj.entities.environment.Wall;
import com.gameproj.screens.GameScreen;

public class WorldContactListener implements ContactListener {

    private GameScreen screen;

    public WorldContactListener init(GameScreen screen) {
        this.screen = screen;

        return this;
    }

    @Override
    public void beginContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        //MARIO HEAD COLLISION
        if(fixA.getUserData() == "head" || fixB.getUserData() == "head") {
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            Controller.canExtendJump = false;


            if(object.getUserData() != null && object.getUserData() instanceof InteractiveTileMapObject) {
                ((InteractiveTileMapObject) object.getUserData()).onHeadHit();
            }
        }

        //MARIO FEET COLLISION
        if(fixA.getUserData() == "feet" || fixB.getUserData() == "feet") {
            Fixture other = fixA.getUserData() == "feet" ? fixB : fixA;

            screen.getPlayer().setTouchingGround(true);

            handleEnemyCollision(screen.getPlayer().fixture, other == fixA ? fixA : fixB);

        }

        //MARIO WORLD ITEM COLLISION
        if(fixA == screen.getPlayer().fixture || fixB == screen.getPlayer().fixture) {

            Fixture other;

            if(fixA == screen.getPlayer().fixture)
                other = fixB;
            else
                other = fixA;

            if(other.getUserData() instanceof Collectable) {
                if(other.getUserData() instanceof Mushroom)
                    ((Mushroom) other.getUserData()).use(screen.getPlayer());
                else
                    ( (Collectable) other.getUserData()).use();
            }

        //MARIO ENEMY COLLISION
            handleEnemyCollision(other == fixA ? fixB : fixA, other == fixA ? fixA : fixB);
        }


    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if(fixA.getUserData() == "feet" || fixB.getUserData() == "feet") {
            if(fixA.getUserData() == "feet")
                screen.getPlayer().setTouchingGround(false);
            else
                screen.getPlayer().setTouchingGround(false);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
    private void handleEnemyCollision(Fixture player, Fixture enemy) {
        if(enemy.getUserData() instanceof Object[]) {
            Object[] enemyData = (Object[]) enemy.getUserData();
            if(enemyData[0].equals("goomba_head") || enemyData[0].equals("goomba")) {
                Goomba goomba = (Goomba) enemyData[1];

                goomba.collision(enemyData[0].equals("goomba_head") ? goomba.head : goomba.getFixture(), player);
            }
        }
    }
}

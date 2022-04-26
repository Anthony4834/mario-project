package com.gameproj.utilities;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.gameproj.GameOne;
import com.gameproj.actors.enemies.Goomba;
import com.gameproj.entities.Player;
import com.gameproj.entities.collectables.Coin;
import com.gameproj.entities.environment.*;
import com.gameproj.screens.GameScreen;

import java.util.HashSet;

import static com.gameproj.GameOne.PPM;

public class WorldGenerator {
    public static HashSet<Fixture> oneWayPlatforms;
    private static GameScreen screen;

    public static void init(final GameScreen s) {

        oneWayPlatforms = new HashSet();
        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap map = mapLoader.load("mario3_1.tmx");
        WorldGenerator.screen = s;
        screen.setRenderer(new OrthogonalTiledMapRenderer(map, 1 / PPM));
        screen.getCamera().position.set(screen.viewport.getWorldWidth() / (float) 2, screen.viewport.getWorldHeight() / 2, 0);

        World world = new World(new Vector2(0, -10), true);

        screen.setWorld(world);
        screen.setB2dRenderer(new Box2DDebugRenderer());

        for(RectangleMapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {

            GoldenBrick b = new GoldenBrick(screen.getWorld(), map, object);
            screen.getBricks().add(b);

        }
        for(RectangleMapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            oneWayPlatforms.add(new WalkableTerrain(screen.getWorld(), map, object).fixture);
        }
        for(RectangleMapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {

            new Pipe(screen.getWorld(), map, object);
        }
        for(RectangleMapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {

            new Wall(screen.getWorld(), map, object);
        }
        for(RectangleMapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle r = object.getRectangle();

            Coin coin = new Coin(screen.getWorld(), ((r.getX() - r.getWidth() / 2) + GameOne.tile) / PPM, ((r.getY() - r.getHeight() / 2) + GameOne.tile) / PPM, false);
            if(Coin.blank == null)
                Coin.blank = coin;

            screen.getCollectables().add(coin);
        }
        for(RectangleMapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle r = object.getRectangle();

            Goomba goomba = new Goomba(screen.getWorld(), ((r.getX() - r.getWidth() / 2) + GameOne.tile) / PPM, ((r.getY() - r.getHeight() / 2) + GameOne.tile) / PPM);

            screen.getEnemies().add(goomba);
        }
        for(RectangleMapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)) {

            Brick b = new Brick(screen.getWorld(), map, object);
            screen.getBricks().add(b);

        }

        world.setContactFilter(new ContactFilter() {
            @Override
            public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
                Fixture platform = null;
                Fixture player = null;

                if(checkIfPlayer(fixtureA))
                    player = fixtureA;
                else if(checkIfPlayer(fixtureB))
                    player = fixtureB;

                if(checkIfPlatform(fixtureA))
                    platform = fixtureA;
                else if(checkIfPlatform(fixtureB))
                    platform = fixtureB;

                if(platform == null || player == null)
                    return true;


                if( ( (WalkableTerrain) (platform.getUserData()) ).collidableWithPlayer ) {
                    if(player.getUserData() == "head")
                        return false;
                    return true;
                }

                return false;
            }
        });

        screen.setPlayer(new Player(screen.getWorld()));

    }

    private static boolean checkIfPlayer(Fixture f) {
        Object data = f.getUserData();

        if(f == screen.getPlayer().fixture || data == "head" || data == "feet") {

            return true;
        }

        return false;
    }
    private static boolean checkIfPlatform(Fixture f) {
        return f.getUserData() instanceof WalkableTerrain;
    }
}

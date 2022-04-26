package com.gameproj.entities.environment;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.gameproj.GameOne;
import com.gameproj.entities.collectables.Mushroom;

import static com.gameproj.GameOne.PPM;

public abstract class InteractiveTileMapObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected TiledMapTileSet tileSet;
    protected MapObject object;
    public Fixture fixture;

    public InteractiveTileMapObject(World w, TiledMap tM, MapObject obj, Rectangle bounds) {
        world = w;
        map = tM;
        object = obj;
        tileSet = map.getTileSets().getTileSet("NES - Super Mario Bros 3 - Stage Tiles");

        BodyDef bDef = new BodyDef();
        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.position.set((bounds.getX() + bounds.getWidth() / 2) / PPM, (bounds.getY() + bounds.getHeight() / 2) / PPM);

        body = w.createBody((bDef));

        shape.setAsBox(bounds.getWidth() / 2 / PPM, bounds.getHeight() / 2 / PPM);
        fDef.shape = shape;
        fixture = body.createFixture(fDef);

    }

    public abstract void onHeadHit();

    public TiledMapTileLayer.Cell getCell() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        if(this instanceof GoldenBrick) {
            layer = (TiledMapTileLayer) map.getLayers().get(7);
        }

        TiledMapTileLayer.Cell cell = layer.getCell((int) (body.getPosition().x * GameOne.PPM / GameOne.tile),
                (int) (body.getPosition().y * PPM / GameOne.tile));
        System.out.println("CELL: " + cell.toString());

        return cell;
    }
}

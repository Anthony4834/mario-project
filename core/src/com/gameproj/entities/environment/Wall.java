package com.gameproj.entities.environment;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Wall extends InteractiveTileMapObject {


    public Wall(World w, TiledMap tM, MapObject obj) {
        super(w, tM, obj, ((RectangleMapObject) obj).getRectangle());
        fixture.setUserData(this);
    }

    @Override
    public void onHeadHit() {

    }
}

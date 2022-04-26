package com.gameproj.utilities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class TextureGrabber {

    public static HashMap<String, TextureRegion> textures = new HashMap<>();
    public static HashMap<String, TextureAtlas> atlasMap;

    public static void init(HashMap<String, TextureAtlas> atlasMap) {

        TextureGrabber.atlasMap = atlasMap;
        textures.put("mario_standing_small", new TextureRegion(atlasMap.get("tiles").findRegion("25")));
        textures.put("mario_standing_big", new TextureRegion(atlasMap.get("tiles").findRegion("59")));
        textures.put("mario_jumping_small", new TextureRegion(atlasMap.get("tiles").findRegion("27")));
        textures.put("mario_jumping_big", new TextureRegion(atlasMap.get("tiles").findRegion("65")));
        textures.put("mario_sliding_right_small", new TextureRegion(atlasMap.get("tiles").findRegion("33")));
        textures.put("mario_sliding_right_big", new TextureRegion(atlasMap.get("tiles").findRegion("70")));
        textures.put("mario_sliding_left_small", new TextureRegion(atlasMap.get("tiles").findRegion("34")));
        textures.put("mario_sliding_left_big", new TextureRegion(atlasMap.get("tiles").findRegion("71")));
        textures.put("mushroom", new TextureRegion(atlasMap.get("items-npcs").findRegion("38")));
        textures.put("dud", new TextureRegion(atlasMap.get("items-npcs").findRegion("82")));
        textures.put("goomba1", new TextureRegion(atlasMap.get("enemies").findRegion("goomba1")));
        textures.put("goomba2", new TextureRegion(atlasMap.get("enemies").findRegion("goomba2")));
        textures.put("goomba3", new TextureRegion(atlasMap.get("enemies").findRegion("goomba3")));

    }

    public static TextureRegion grab(String key) {
        return textures.containsKey(key) ? textures.get(key) : null;
    }
    public static Array<TextureRegion> grabAnimationFrames(String key, String size) {

        Array<TextureRegion> output = new Array<>();

        switch(key) {
            case "mario_running":
                if(size.equals("small")) {
                    output.add(new TextureRegion(atlasMap.get("tiles").findRegion("25")));
                    output.add(new TextureRegion(atlasMap.get("tiles").findRegion("26")));
                } else {
                    output.add(new TextureRegion(atlasMap.get("tiles").findRegion("59")));
                    output.add(new TextureRegion(atlasMap.get("tiles").findRegion("60")));
                    output.add(new TextureRegion(atlasMap.get("tiles").findRegion("63")));
                    output.add(new TextureRegion(atlasMap.get("tiles").findRegion("60")));
                }
                break;
            case "mario_growing_1":
                output.add(new TextureRegion(atlasMap.get("tiles").findRegion("25")));
                output.add(new TextureRegion(atlasMap.get("tiles").findRegion("midMario")));
                break;
            case "mario_growing_2":
                output.add(new TextureRegion(atlasMap.get("tiles").findRegion("midMario")));
                output.add(new TextureRegion(atlasMap.get("tiles").findRegion("59")));
                break;
            case "mario_shrinking_1":
                output.add(new TextureRegion(atlasMap.get("tiles").findRegion("59")));
                output.add(new TextureRegion(atlasMap.get("tiles").findRegion("midMario")));
                break;
            case "mario_shrinking_2":
                output.add(new TextureRegion(atlasMap.get("tiles").findRegion("midMario")));
                output.add(new TextureRegion(atlasMap.get("tiles").findRegion("25")));
                break;
            case "coin_spinning":
                output.add(new TextureRegion(atlasMap.get("items-npcs").findRegion("33")));
                output.add(new TextureRegion(atlasMap.get("items-npcs").findRegion("29")));
                output.add(new TextureRegion(atlasMap.get("items-npcs").findRegion("30")));
                output.add(new TextureRegion(atlasMap.get("items-npcs").findRegion("28")));
                break;
            case "goomba_walking":
                output.add(textures.get("goomba1"));
                output.add(textures.get("goomba2"));
                break;
            default:
                break;
        }

        return output;
    }
    public static Array<TiledMapTile> grabAnimationFrames(String key, TiledMapTileSet tS) {

        Array<TiledMapTile> output = new Array<>();

        switch(key) {
            case "activeGoldenBrick":
                output.add(tS.getTile(214));
                output.add(tS.getTile(215));
                output.add(tS.getTile(216));
                output.add(tS.getTile(217));
                break;
            default:
                break;
        }

        return output;
    }
    public static void dispose() {
        for(String key : atlasMap.keySet()) {
            atlasMap.get(key).dispose();
        }
    }

}

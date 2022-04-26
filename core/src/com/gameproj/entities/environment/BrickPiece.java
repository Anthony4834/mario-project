package com.gameproj.entities.environment;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.gameproj.utilities.TextureGrabber;

import java.util.Objects;

import static com.gameproj.GameOne.PPM;

public class BrickPiece extends NonCollectable {
    protected World world;
    protected Fixture fixture;
    protected float width;
    protected float height;

    public BrickPiece (World w, float x, float y) {
        super(w, x, y, 5, 5, Objects.requireNonNull(TextureGrabber.grab("dud")));
    }
}